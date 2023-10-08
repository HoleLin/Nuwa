package cn.holelin.dicom.pacs_v1.support;

import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.holelin.dicom.pacs_v1.domain.PacsServerConfig;
import cn.holelin.dicom.pacs_v1.events.PullTaskFailedEvent;
import cn.holelin.dicom.pacs_v1.utils.PacsContext;
import cn.holelin.dicom.pacs_v1.utils.SpringUtil;
import cn.holelin.dicom.pacs_v1.utils.TransferSyntaxHelper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.net.*;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.BasicCEchoSCP;
import org.dcm4che3.net.service.BasicCStoreSCP;
import org.dcm4che3.net.service.DicomServiceException;
import org.dcm4che3.net.service.DicomServiceRegistry;
import org.dcm4che3.util.SafeClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HoleLin
 */

public class PacsServerSupport {

    private final Logger log = LoggerFactory.getLogger(PacsServerSupport.class);

    /**
     * 本地设备
     */
    private final Device device = new Device("test");

    /**
     * 本地pacs名称
     */
    private final ApplicationEntity ae = new ApplicationEntity("*");

    /**
     * 本地连接
     */
    private final Connection connection = new Connection();


    private static int[] responseDelays;


    private final ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new ThreadFactoryBuilder().setNameFormat("pacs-scp-handlers-%d").build());

    public PacsServerSupport(PacsServerConfig config) {
        // 存放DICOM目录的路径
        String localStoragePath = config.getLocalStoragePath();
        final CStoreScpImpl defaultBasicStoreScp = new CStoreScpImpl(localStoragePath);
        device.setDimseRQHandler(createServiceRegistry(defaultBasicStoreScp));
        initScp(config);
    }

    private void initScp(PacsServerConfig config) {
        device.addConnection(connection);
        device.addApplicationEntity(ae);
        device.setDeviceName(config.getDeviceName());

        connection.setPort(config.getLocalPort());
        connection.setHostname(config.getLocalHostName());
        ae.setAETitle(config.getAeTitle());
        ae.addTransferCapability(new TransferCapability(null, "*",
                TransferCapability.Role.SCP, TransferSyntaxHelper.IMAGE_STORAGE_ALL_TS));
        ae.setAssociationAcceptor(true);
        ae.addConnection(connection);
    }

    public void start() throws GeneralSecurityException, IOException {
        device.setExecutor(executorService);
        device.bindConnections();
        log.info("DICOM PACS SCP start SUCCESS,aet:{},host:{},port:{}", ae.getAETitle(),
                connection.getHostname(), connection.getPort());

    }

    public void shutdown() {
        device.unbindConnections();
        executorService.shutdown();
        log.info("DICOM PACS SCP stop SUCCESS");
    }


    public boolean echo() {
        PacsBaseConfig config = new PacsBaseConfig();
        config.setRemotePort(connection.getPort());
        config.setRemoteHostName(connection.getHostname());
        config.setRemoteAeTitle(ae.getAETitle());
        String testFindScu = "test_echo";
        config.setAeTitle(testFindScu);
        config.setDeviceName(testFindScu);
        PacsCEchoSupport support = new PacsCEchoSupport(config);
        return support.echo();
    }

    /**
     * 配置DICOM服务
     *
     * @return DicomServiceRegistry
     */
    private DicomServiceRegistry createServiceRegistry(BasicCStoreSCP handler) {
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        if (Objects.nonNull(handler)) {
            serviceRegistry.addDicomService(handler);
        }
        return serviceRegistry;
    }

    /**
     * SCP 接收文件存储处理类
     */
    class CStoreScpImpl extends BasicCStoreSCP {

        private final String localStoragePath;

        private ReentrantLock lock = new ReentrantLock();

        public CStoreScpImpl(String localStoragePath) {
            super("*");
            this.localStoragePath = localStoragePath;
        }

        @Override
        protected void store(Association as, PresentationContext pc, Attributes rq,
                             PDVInputStream data, Attributes rsp)
                throws IOException {
            try {
                log.debug("Association: {}", as);
                log.debug("PresentationContext: {}", pc);
                log.debug("Request: {}", rq);
                log.debug("Response: {}", rsp);
                rsp.setInt(Tag.Status, VR.US, 0);
                if (Objects.isNull(localStoragePath)) {
                    log.warn("localStoragePath is null");
                    return;
                }
                final File dir = new File(localStoragePath);
                if (!Files.exists(dir.toPath()) && !dir.mkdirs()) {
                    return;
                }
                String cuid = rq.getString(Tag.AffectedSOPClassUID);
                String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
                String tsuid = pc.getTransferSyntax();
                log.debug("cuid: {},iuid: {}", cuid, iuid);
                String taskId = PacsContext.peek();
                String path = taskId + File.separator;
                File file = new File(dir, path + UUID.randomUUID());
                try {
                    log.info("{}: M-WRITE {}.", as, file);
                    final Attributes information = as.createFileMetaInformation(iuid, cuid, tsuid);
                    storeTo(information, data, file, tsuid);
                } catch (Exception e) {
                    if (file.delete()) {
                        log.info("{}: M-DELETE {}", as, file);
                    } else {
                        log.warn("{}: M-DELETE {} failed!", as, file);
                    }
                    SpringUtil.getContext().publishEvent(new PullTaskFailedEvent(taskId,"STORESCP错误"));
                    throw new DicomServiceException(Status.ProcessingFailure, e);
                }
            } finally {
                sleep(as, responseDelays);
            }
        }

        /**
         * 存储DICOM文件
         *
         * @param fmi  dicom属性信息
         * @param data 数据
         * @param file 最终保存的文件对象
         */
        private void storeTo(Attributes fmi,
                             PDVInputStream data, File file, String tsuid) throws IOException {
            file.getParentFile().mkdirs();
            DicomOutputStream out = new DicomOutputStream(Files.newOutputStream(file.toPath()), tsuid);
            try {
                out.writeFileMetaInformation(fmi);
                data.copyTo(out);
            } finally {
                SafeClose.close(out);
            }
        }

        /**
         * 修改名称
         *
         * @param from 原始文件对象
         * @param dest 最终文件对象
         */
        private void renameTo(File from, File dest) {
            try {
                dest.getParentFile().mkdirs();
                Files.move(from.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sleep(Association as, int[] delays) {
            int responseDelay = delays != null
                    ? delays[(as.getNumberOfReceived(Dimse.C_STORE_RQ) - 1) % delays.length]
                    : 0;
            if (responseDelay > 0) {
                try {
                    Thread.sleep(responseDelay);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }


    public static void main(String[] args) {
        PacsServerConfig pacsServerConfig = new PacsServerConfig();
        pacsServerConfig.setDeviceName("scp_test");
        pacsServerConfig.setLocalHostName("0.0.0.0");
        pacsServerConfig.setAeTitle("scp_test");
        pacsServerConfig.setLocalPort(10087);
        pacsServerConfig.setLocalStoragePath("C:\\Data\\pacs\\scp");

        PacsServerSupport support = new PacsServerSupport(pacsServerConfig);
        try {
            support.start();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(support.echo());

    }
}
