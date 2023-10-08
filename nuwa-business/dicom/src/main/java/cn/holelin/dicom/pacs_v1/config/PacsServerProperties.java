package cn.holelin.dicom.pacs_v1.config;

import lombok.Data;

import java.io.Serializable;

/**
 * Pacs配置
 *
 * @author HoleLin
 */
@Data
public class PacsServerProperties implements Serializable {

    private String aet;
    private String hostname;
    private Integer port;
}
