package cn.holelin.dicom.pacs_v1.response;

import cn.holelin.dicom.pacs_v1.request.PacsDimseConditionBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author HoleLin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PacsSearchResponse extends PacsDimseConditionBase {
    /**
     * Study描述
     */
    private String studyDescription;
    /**
     * 层厚
     */
    private Double sliceThickness;
    /**
     * Series描述
     */
    private String seriesDescription;
    /**
     * Study创建日期
     */
    private String studyDate;
    /**
     * Study创建时间
     */
    private String studyTime;
    /**
     * 序列的数量
     */
    private Integer numberOfSeriesRelatedInstances;

    /**
     * 业务字段
     * 是否已经拉取过
     */
    private Boolean exist;
}
