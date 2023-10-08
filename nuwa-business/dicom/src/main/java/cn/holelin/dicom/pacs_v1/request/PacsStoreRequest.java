package cn.holelin.dicom.pacs_v1.request;


import lombok.Data;

import java.util.List;

/**
 * @author HoleLin
 */
@Data
public class PacsStoreRequest {

    /**
     * 拉图的数据查询条件
     */
    private List<PacsStoreCondition> list;
}
