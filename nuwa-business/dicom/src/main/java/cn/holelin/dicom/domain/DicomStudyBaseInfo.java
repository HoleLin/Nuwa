package cn.holelin.dicom.domain;

import lombok.Data;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/22 4:36 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/22 4:36 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class DicomStudyBaseInfo {
    /**
     * 检查ID.
     */
    private String studyID;
    /**
     * 检查实例号：唯一标记不同检查的号码
     */
    private String studyInstanceUID;

}
