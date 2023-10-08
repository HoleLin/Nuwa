package cn.holelin.dicom.pacs_v1.pool;


import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.utils.PacsConfigRegistry;
import cn.holelin.dicom.pacs_v1.utils.TransferSyntaxHelper;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.dcm4che3.net.*;
import org.dcm4che3.net.pdu.AAssociateRQ;
import org.dcm4che3.net.pdu.ExtendedNegotiation;
import org.dcm4che3.net.pdu.PresentationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Pacs连接池工厂类
 *
 * @author HoleLin
 */
public class PacsConnectionPoolFactory extends BaseKeyedPooledObjectFactory<String, Association> {
    private final Logger log = LoggerFactory.getLogger(PacsConnectionPoolFactory.class);
    /**
     * 建立连接是否要设置扩展协商
     */
    private final Boolean needExtendedNegotiation;

    public PacsConnectionPoolFactory(Boolean needExtendedNegotiation) {
        this.needExtendedNegotiation = needExtendedNegotiation;
    }

    @Override
    public Association create(String key) throws Exception {
        PacsBaseConfig config = PacsConfigRegistry.getConfig(key);

        final String aeTitle = config.getAeTitle();
        final String deviceName = config.getDeviceName();
        final String remoteAeTitle = config.getRemoteAeTitle();
        final Integer remotePort = config.getRemotePort();
        final String remoteHostName = config.getRemoteHostName();

        ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), Executors.defaultThreadFactory());
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

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
        device.setScheduledExecutor(scheduledExecutorService);

        // 初始化上下文
        initPresentationContext(request, needExtendedNegotiation);
        Association association = null;
        try {
            association = ae.connect(remote, request);
        } catch (IOException | InterruptedException |
                 IncompatibleConnectionException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return association;
    }

    /**
     * 初始化请求的上下文
     *
     * @param request                 PACS 请求
     * @param needExtendedNegotiation 建立连接是否要设置扩展协商
     */
    public void initPresentationContext(AAssociateRQ request, Boolean needExtendedNegotiation) {
        // C-ECHO
        request.addPresentationContext(new PresentationContext(2 * request.getNumberOfPresentationContexts() + 1,
                InformationModelEnum.ECHO.getCuid(), TransferSyntaxHelper.VERIFICATION));
        // C-FIND
        request.addPresentationContext(new PresentationContext(2 * request.getNumberOfPresentationContexts() + 1,
                InformationModelEnum.FIND.getCuid(), TransferSyntaxHelper.IMAGE_STORAGE_ALL_TS));
        final EnumSet<QueryOption> queryOptions;
        if (needExtendedNegotiation) {
            queryOptions = EnumSet.allOf(QueryOption.class);
        } else {
            queryOptions = EnumSet.of(QueryOption.FUZZY, QueryOption.RELATIONAL);
        }
        request.addExtendedNegotiation(new ExtendedNegotiation(InformationModelEnum.FIND.getCuid(),
                QueryOption.toExtendedNegotiationInformation(queryOptions)));
        // C-MOVE
        request.addPresentationContext(new PresentationContext(2 * request.getNumberOfPresentationContexts() + 1,
                InformationModelEnum.MOVE.getCuid(), TransferSyntaxHelper.IMAGE_STORAGE_ALL_TS));
    }

    @Override
    public PooledObject<Association> wrap(Association association) {
        return new DefaultPooledObject<>(association);
    }

    @Override
    public void destroyObject(String key, PooledObject<Association> p, DestroyMode destroyMode) throws Exception {
        Association association = p.getObject();
        if (Objects.nonNull(association) && association.isReadyForDataTransfer()) {
            try {
                association.waitForOutstandingRSP();
                association.release();
            } catch (InterruptedException | IOException e) {
                log.error("销毁PACS 连接失败");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean validateObject(String key, PooledObject<Association> p) {
        Association association = p.getObject();
        if (Objects.isNull(association)) {
            return false;
        }
        try {
            return association.cecho().next();
        } catch (IOException | InterruptedException e) {
            log.error("验证PACS连接连通性失败");
            throw new RuntimeException(e);
        }
    }
}
