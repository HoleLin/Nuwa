package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 远程应用配置
 * @author HoleLin
 */
@Data
public class RemoteAEConfig implements Serializable {
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
