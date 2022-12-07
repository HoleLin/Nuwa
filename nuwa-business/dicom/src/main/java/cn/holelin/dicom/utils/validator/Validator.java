package cn.holelin.dicom.utils.validator;

import cn.holelin.dicom.domain.DicomImagePretreatment;

/**
 * @Description: 验证器
 * @Author: HoleLin
 * @CreateDate: 2022/5/7 7:58 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/7 7:58 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public interface Validator {
    /**
     * dicom Tag值验证
     *
     * @param dicomFrame dicom对象
     * @return true--验证通过,false--验证不通过
     */
    Boolean validated(DicomImagePretreatment dicomFrame);

}
