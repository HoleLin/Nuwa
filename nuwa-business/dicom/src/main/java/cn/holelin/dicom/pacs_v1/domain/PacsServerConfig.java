package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;

/**
 * @author HoleLin
 */
@Data
public class PacsServerConfig {

    /**
     * 本地设备名称
     */
    private String deviceName = "yw-backend-device";

    /**
     * 本地设备AET
     */
    private String aeTitle = "yw-backend-aet";

    /**
     * 本地SCP端口
     */
    private int localPort;

    /**
     * 本地SCP hostname
     */
    private String localHostName;

    /**
     * 本地存储目录
     */
    private String localStoragePath;
}
