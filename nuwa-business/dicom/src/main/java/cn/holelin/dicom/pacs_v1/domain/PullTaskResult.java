package cn.holelin.dicom.pacs_v1.domain;

import cn.holelin.dicom.pacs_v1.request.PacsStoreCondition;
import lombok.Data;

import java.util.List;

/**
 * @author HoleLin
 */
@Data
public class PullTaskResult {

    /**
     * 成功的数据
     */
    private List<PacsStoreCondition> succeed;
    /**
     * 失败的数据
     */
    private List<PacsStoreCondition> failed;

    /**
     * 当前批次的总数据
     */
    private Integer batchSize;
}
