package cn.holelin.dicom.pacs_v1.request;

import lombok.Data;

/**
 * @author HoleLin
 */
@Data
public class PacsDimseConditionBase {
    /**
     * 患者ID-->(当前用作放射号)
     */
    private String patientId;
    /**
     * 患者姓名
     */
    private String patientName;
    private String patientAge;
    private String patientSex;
    /**
     * 检查号
     */
    private String accessionNumber;
    /**
     * Study唯一标识
     */
    private String studyInstanceUid;
    /**
     * Series唯一标识
     */
    private String seriesInstanceUid;
    /**
     * 身体部位
     */
    private String bodyPartExamined;
    /**
     * 模态
     */
    private String modality;

}
