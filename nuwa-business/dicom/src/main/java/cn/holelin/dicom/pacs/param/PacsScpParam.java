package cn.holelin.dicom.pacs.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 初始化SCP参数类
 *
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 17:08
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 17:08
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PacsScpParam extends PacsBaseParam {

    /**
     * 接收DICOM目录
     */
    private String storageDir;

}
