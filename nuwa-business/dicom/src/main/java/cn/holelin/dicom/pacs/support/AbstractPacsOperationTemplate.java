package cn.holelin.dicom.pacs.support;

import cn.holelin.dicom.pacs.enums.InformationModelEnum;
import cn.holelin.dicom.pacs.param.PacsBaseParam;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.UID;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.IncompatibleConnectionException;
import org.dcm4che3.net.pdu.AAssociateRQ;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * PACS操作模板抽象类
 *
 * @Description: 提供PACS的连接/断开等操作
 * @Author: HoleLin
 * @CreateDate: 2022/8/15 10:20
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/8/15 10:20
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public abstract class AbstractPacsOperationTemplate {

    /**
     * 本地设备
     */
    private Device device = new Device();

    /**
     * 本地pacs名称
     */
    private ApplicationEntity ae = new ApplicationEntity();

    /**
     * 本地连接
     */
    private Connection local = new Connection();

    /**
     * 远程连接 即Find操作的目标服务
     */
    private Connection remote = new Connection();

    /**
     * 连接请求
     */
    public AAssociateRQ aarq = new AAssociateRQ();

    /**
     * DICOM连接
     */
    public Association association;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Transfer syntax array
     */
    /**
     * CT 需要的传输语法
     */
    public static String[] CT_IMAGE_STORAGE = {
            UID.ImplicitVRLittleEndian,
            UID.ExplicitVRLittleEndian,
            UID.ExplicitVRBigEndian
    };
    /**
     * MR 需要的传输语法
     */
    public static String[] MR_IMAGE_STORAGE = {
            UID.ImplicitVRLittleEndian,
            UID.ExplicitVRLittleEndian,
    };
    /**
     * CR 需要的传输语法
     */
    public static String[] CR_IMAGE_STORAGE = MR_IMAGE_STORAGE;

    public AbstractPacsOperationTemplate(PacsBaseParam param) {
        final String aeTitle = param.getAeTitle();
        final String deviceName = param.getDeviceName();
        final String remoteAeTitle = param.getRemoteAeTitle();
        final Integer remotePort = param.getRemotePort();
        final String remoteHostName = param.getRemoteHostName();

        device.setDeviceName(deviceName);
        ae.setAETitle(aeTitle);
        remote.setHostname(remoteHostName);
        remote.setPort(remotePort);
        aarq.setCalledAET(remoteAeTitle);

        device.addConnection(local);
        device.addApplicationEntity(ae);
        ae.addConnection(local);
        device.setExecutor(executorService);
        device.setScheduledExecutor(scheduledExecutorService);
    }

    /**
     * 与远端PACS建立连接
     *
     * @throws IncompatibleConnectionException
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws InterruptedException
     */
    private void connect() throws IncompatibleConnectionException, GeneralSecurityException, IOException, InterruptedException {
        association = ae.connect(local, remote, aarq);
    }

    /**
     * 与远端PACS断开连接
     */
    private void disconnect() {
        if (Objects.nonNull(association) && association.isReadyForDataTransfer()) {
            try {
                association.waitForOutstandingRSP();
                association.release();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 模板方法
     * C-FIND/C-MOVE/C-STORE 三个操作都类型的操作顺序(设置连接参数-连接-执行具体操作-关闭连接)
     *
     * @param model      Abstract Syntax
     * @param conditions 查询条件
     * @throws IncompatibleConnectionException
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws InterruptedException
     */
    public void template(InformationModelEnum model, Attributes conditions) throws IncompatibleConnectionException,
            GeneralSecurityException, IOException, InterruptedException {
        preConnect(model);
        connect();
        try {
            execute(model, conditions);
        } finally {
            disconnect();
        }
    }

    /**
     * 连接前的准备操作
     * 如设置Abstract Syntax
     *
     * @param model Abstract Syntax
     */
    public abstract void preConnect(InformationModelEnum model);

    /**
     * 执行具体的操作
     *
     * @param model      Abstract Syntax
     * @param conditions 查询条件
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract void execute(InformationModelEnum model, Attributes conditions) throws IOException, InterruptedException;

}
