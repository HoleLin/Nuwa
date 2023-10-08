package cn.holelin.dicom.pacs_v1.support;


import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.utils.TransferSyntaxHelper;
import org.dcm4che3.net.*;
import org.dcm4che3.net.pdu.AAssociateRQ;
import org.dcm4che3.net.pdu.PresentationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * C-ECHO 实现类
 *
 * @author HoleLin
 */
public class PacsCEchoSupport {
    private final Logger log = LoggerFactory.getLogger(PacsCEchoSupport.class);

    private Association association = null;
    ExecutorService executorService = new ThreadPoolExecutor(1, 1,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Executors.defaultThreadFactory());

    public PacsCEchoSupport(PacsBaseConfig config) {
        final String aeTitle = config.getAeTitle();
        final String deviceName = config.getDeviceName();
        final String remoteAeTitle = config.getRemoteAeTitle();
        final Integer remotePort = config.getRemotePort();
        final String remoteHostName = config.getRemoteHostName();

        ApplicationEntity ae = new ApplicationEntity();
        // 本地连接
        Connection local = new Connection();
        ae.setAETitle(aeTitle);
        ae.addConnection(local);
        // 远程连接
        Connection remote = new Connection();
        remote.setHostname(remoteHostName);
        remote.setPort(remotePort);

        // 请求
        AAssociateRQ request = new AAssociateRQ();
        request.setCalledAET(remoteAeTitle);

        // 本地设备
        Device device = new Device();
        device.setDeviceName(deviceName);
        device.addConnection(local);
        device.addApplicationEntity(ae);
        device.setExecutor(executorService);

        // 初始化上下文
        initPresentationContext(request);
        try {
            association = ae.connect(remote, request);
        } catch (IOException | InterruptedException |
                 IncompatibleConnectionException | GeneralSecurityException e) {
            e.printStackTrace();
            log.error("C-ECHO 连接失败,连接配置为AET:{},IP:{},PORT:{}", remoteAeTitle, remoteHostName, remotePort);
        }
    }

    /**
     * 初始化请求的上下文
     *
     * @param request PACS 请求
     */
    public void initPresentationContext(AAssociateRQ request) {
        // C-ECHO
        request.addPresentationContext(new PresentationContext(2 * request.getNumberOfPresentationContexts() + 1,
                InformationModelEnum.ECHO.getCuid(), TransferSyntaxHelper.VERIFICATION));
    }

    public boolean echo() {
        boolean echo;
        if (Objects.isNull(association) || !association.isReadyForDataTransfer()) {
            return false;
        }
        try {
            echo = association.cecho().next();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            disconnect();
        }
        return echo;
    }

    public void disconnect() {
        if (Objects.nonNull(association) && association.isReadyForDataTransfer()) {
            try {
                association.waitForOutstandingRSP();
                association.release();
                executorService.shutdown();
            } catch (InterruptedException | IOException e) {
                log.error("销毁PACS 连接失败");
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        PacsBaseConfig config = new PacsBaseConfig();
        config.setRemotePort(11112);
        config.setRemoteHostName("192.168.11.216");
        config.setRemoteAeTitle("DCM4CHEE");
        String testFindScu = "test_echo";
        config.setAeTitle(testFindScu);
        config.setDeviceName(testFindScu);
        PacsCEchoSupport support = new PacsCEchoSupport(config);
        if (support.echo()) {
            System.out.println("连通性测试成功");
        } else {
            System.out.println("连通性测试失败");
        }

    }
}
