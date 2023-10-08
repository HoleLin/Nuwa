package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author HoleLin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PacsBaseConfig extends RemoteAEConfig {
    /**
     * 本地设备名称
     */
    private String deviceName;
    /**
     * 本地设备AET
     */
    private String aeTitle;
}
