package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author HoleLin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PacsCMoveConfig extends PacsBaseConfig {
    /**
     * 接收DICOM文件的SCP的AET
     */
    private String destinationAeTitle;
}
