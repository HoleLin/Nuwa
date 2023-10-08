package cn.holelin.dicom.pacs_v1.base;


import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.pool.PacsConnectionPool;
import cn.holelin.dicom.pacs_v1.response.OperationResponse;
import cn.holelin.dicom.pacs_v1.response.PacsSearchResponse;
import cn.holelin.dicom.pacs_v1.utils.PacsConfigRegistry;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.net.Association;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Pacs行为抽象类  抽取C-FIND/C-MOVE/C-STORE的公共部分
 *
 * @author HoleLin
 */
public abstract class AbstractPacsBehaviorSupport implements PacsBehavior {

    /**
     * DICOM连接
     */
    private Association association;

    private final PacsConnectionPool pool;
    private final String key;

    public AbstractPacsBehaviorSupport(PacsBaseConfig config) {
        key = PacsConfigRegistry.register(config);
        pool = PacsConnectionPool.getInstance();
    }
    public AbstractPacsBehaviorSupport(PacsBaseConfig config,Boolean needExtendedNegotiation) {
        key = PacsConfigRegistry.register(config);
        pool = PacsConnectionPool.getInstance(needExtendedNegotiation);
    }

    @Override
    public void connect() throws Exception {
        association = pool.borrowObject(key);
    }

    @Override
    public void disconnect() {
        pool.returnObject(key, association);
        association = null;
    }

    @Override
    public boolean echo() {
        if (Objects.isNull(association) || !association.isReadyForDataTransfer()) {
            try {
                connect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return association.cecho().next();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 模板方法
     * C-MOVE/C-STORE 三个操作都类型的操作顺序(设置连接参数-连接-执行具体操作-关闭连接)
     *
     * @param model      Abstract Syntax
     * @param conditions 查询条件
     */
    public OperationResponse execute(InformationModelEnum model, Attributes conditions) throws
            Exception {
        if (Objects.isNull(association) || !association.isReadyForDataTransfer()) {
            connect();
        }
        return doExecute(model, conditions);
    }

    /**
     * 模板方法
     * C-FIND的操作顺序(设置连接参数-连接-执行具体操作-关闭连接)
     *
     * @param model      Abstract Syntax
     * @param conditions 查询条件
     */
    public List<PacsSearchResponse> executeWithResult(InformationModelEnum model, Attributes conditions) throws
            Exception {
        if (Objects.isNull(association) || !association.isReadyForDataTransfer()) {
            connect();
        }
        return doExecuteWithResult(model, conditions);
    }


    public Association getAssociation() {
        return association;
    }

    /**
     * 执行具体的操作
     *
     * @param model      Abstract Syntax
     * @param conditions 查询条件
     */
    public abstract OperationResponse doExecute(InformationModelEnum model, Attributes conditions);

    /**
     * 执行具体的操作并带有返回值
     * 针对C-FIND操作
     *
     * @param model      Abstract Syntax
     * @param conditions 查询条件
     * @return 执行后的返回值
     */
    public abstract List<PacsSearchResponse> doExecuteWithResult(InformationModelEnum model, Attributes conditions);
}
