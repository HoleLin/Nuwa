package cn.holelin.dicom.pacs.param;

import cn.holelin.dicom.pacs.enums.InformationModelEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcm4che3.data.Attributes;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 16:55
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 16:55
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PacsQueryParam extends PacsBaseParam {

    /**
     * Abstract Syntax
     */
    private InformationModelEnum model;

    /**
     * 查询条件
     */
    private Attributes conditions;

}
