package cn.holelin.dicom.utils.validator;

import cn.holelin.dicom.domain.DicomImagePretreatment;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.IOD;
import org.dcm4che3.data.ValidationResult;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description: Dicom文件校验
 * @Author: HoleLin
 * @CreateDate: 2022/3/23 3:31 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/23 3:31 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class XmlValidator implements Validator {

    private final IOD validator;

    public XmlValidator() throws IOException {
        this.validator = IOD.load("resource:validators/dicom-validation.xml");
    }

    @Override
    public Boolean validated(DicomImagePretreatment dicomFrame) {
        final Attributes attributes = dicomFrame.getAttributes();
        if (Objects.nonNull(attributes)) {
            final ValidationResult validationResult = attributes.validate(validator);
            return validationResult.isValid();
        }
        return false;
    }

}
