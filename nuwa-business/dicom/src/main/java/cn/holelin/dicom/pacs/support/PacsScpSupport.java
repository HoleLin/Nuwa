package cn.holelin.dicom.pacs.support;

import cn.holelin.dicom.pacs.StoreScpDimseHandler;
import cn.holelin.dicom.pacs.param.PacsScpParam;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.TransferCapability;
import org.dcm4che3.net.service.BasicCEchoSCP;
import org.dcm4che3.net.service.BasicCStoreSCP;
import org.dcm4che3.net.service.DicomServiceRegistry;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Pacs SCP 实现类
 *
 * @Description: 提供启动SCP, 停止SCP
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 17:06
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 17:06
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
public class PacsScpSupport {

    private final Device device = new Device();
    private final ApplicationEntity ae = new ApplicationEntity("*");
    private final Connection conn = new Connection();
    private final ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Executors.defaultThreadFactory());
    /**
     * 接收DICOM目录
     */
    private String storageDir;




    public PacsScpSupport(PacsScpParam param) {
        // 设置工作目录
        this.storageDir = param.getStorageDir();
        final String remoteAeTitle = param.getRemoteAeTitle();
        final Integer remotePort = param.getRemotePort();
        final String remoteHostName = param.getRemoteHostName();
        final String deviceName = param.getDeviceName();

        // 设置DIMSE请求处理器
        final StoreScpDimseHandler storeScpDimseHandler = new StoreScpDimseHandler(storageDir);
        device.setDimseRQHandler(createServiceRegistry(storeScpDimseHandler));

        device.setDeviceName(deviceName);
        conn.setPort(remotePort);
        conn.setHostname(remoteHostName);
        ae.setAETitle(remoteAeTitle);

        device.addConnection(conn);
        device.addApplicationEntity(ae);
        ae.setAssociationAcceptor(true);
        // 设置SCP接收所有类型的SOP Class并且接受所有传输协议
        ae.addTransferCapability(new TransferCapability(null, "*",
                TransferCapability.Role.SCP, "*"));
        ae.addConnection(conn);
    }

    /**
     * 启动一个SCP
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void start() throws GeneralSecurityException, IOException {
        device.setExecutor(executorService);
        device.bindConnections();
        log.info("DICOM PACS SCP start SUCCESS,aet:{},host:{},port:{}", ae.getAETitle(), conn.getHostname(),
                conn.getPort());
    }

    /**
     * 关闭当前SCP
     */
    public void shutdown() {
        device.unbindConnections();
        executorService.shutdown();
        log.info("DICOM PACS SCP stop SUCCESS");
    }


    /**
     * 配置DICOM服务
     *
     * @return DicomServiceRegistry
     */
    private DicomServiceRegistry createServiceRegistry(BasicCStoreSCP handler) {
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        // 支持C-ECHO操作
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        if (Objects.nonNull(handler)) {
            serviceRegistry.addDicomService(handler);
        }
        return serviceRegistry;
    }

}
