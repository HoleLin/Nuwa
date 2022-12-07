package cn.holelin.dicom.pacs.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 16:13
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 16:13
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class PacsRemoteParam implements Serializable {
    /**
     * 远端PACS的主机名称
     */
    private String remoteHostName;
    /**
     * 远端PACS的端口
     */
    private Integer remotePort;
    /**
     * 远端PACS的AET
     */
    private String remoteAeTitle;
}
