package cn.holelin.dicom.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/22 4:36 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/22 4:36 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DicomSeriesBaseInfo extends DicomStudyBaseInfo {

    /**
     * -------------------------------------
     *
     *      Series Information
     *
     * -------------------------------------
     */
    private String seriesInstanceUid;
    private String seriesDescription;
}
