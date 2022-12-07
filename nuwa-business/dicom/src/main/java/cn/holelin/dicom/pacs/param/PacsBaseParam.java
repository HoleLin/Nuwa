package cn.holelin.dicom.pacs.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 16:51
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 16:51
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PacsBaseParam extends PacsRemoteParam {
    /**
     * 本地设备名称
     */
    private String deviceName;
    /**
     * 本地设备AET
     */
    private String aeTitle;



}
