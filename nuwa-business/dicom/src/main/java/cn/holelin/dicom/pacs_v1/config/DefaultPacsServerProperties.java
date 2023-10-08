package cn.holelin.dicom.pacs_v1.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Pacs配置
 *
 * @author HoleLin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DefaultPacsServerProperties extends PacsServerProperties {

    /**
     * 本地STORESCP存储DICOM数据的文件目录
     */
    private String localStoragePath;

    /**
     * 建立连接时是否需要设置扩展协商
     * 默认为true,需要设置扩展协商
     */
    private Boolean needExtendedNegotiation = true;
}
