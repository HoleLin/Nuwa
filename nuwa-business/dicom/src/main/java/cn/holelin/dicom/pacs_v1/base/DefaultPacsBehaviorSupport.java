package cn.holelin.dicom.pacs_v1.base;

import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.exception.NotSupportBehaviorException;
import cn.holelin.dicom.pacs_v1.response.OperationResponse;
import cn.holelin.dicom.pacs_v1.response.PacsSearchResponse;
import org.dcm4che3.data.Attributes;

import java.util.List;

/**
 * 默认实现类
 *
 * @author HoleLin
 */
public class DefaultPacsBehaviorSupport extends AbstractPacsBehaviorSupport {


    public DefaultPacsBehaviorSupport(PacsBaseConfig config) {
        super(config);
    }

    public DefaultPacsBehaviorSupport(PacsBaseConfig config, Boolean needExtendedNegotiation) {
        super(config, needExtendedNegotiation);
    }

    @Override
    public OperationResponse doExecute(InformationModelEnum model, Attributes conditions) {
        throw new NotSupportBehaviorException("not implements doExecute method");
    }

    @Override
    public List<PacsSearchResponse> doExecuteWithResult(InformationModelEnum model, Attributes conditions) {
        throw new NotSupportBehaviorException("not implements doExecute method");
    }
}
