package cn.holelin.dicom.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/19 12:42 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/19 12:42 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DicomStudy extends DicomStudyBaseInfo {


    /**
     * 多序列
     */
    Map<String, List<DicomImage>> series;
}
