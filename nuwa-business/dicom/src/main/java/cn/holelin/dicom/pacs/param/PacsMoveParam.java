package cn.holelin.dicom.pacs.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 16:56
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 16:56
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PacsMoveParam extends PacsQueryParam {

    /**
     * 接收DICOM文件的SCP的AET
     */
    private String desAeTitle;
}
