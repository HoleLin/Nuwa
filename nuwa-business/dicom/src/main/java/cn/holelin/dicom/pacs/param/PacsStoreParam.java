package cn.holelin.dicom.pacs.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 17:59
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 17:59
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PacsStoreParam extends PacsBaseParam {

    /**
     * 需要传输的DICOM目录
     */
    private String dicomDir;
}
