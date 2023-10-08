package cn.holelin.dicom.pacs_v1.config;

import cn.holelin.dicom.pacs_v1.domain.PacsPullTaskProperties;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author HoleLin
 */
@Getter
@Component
@ConfigurationProperties("pacs")
public class PacsProperties {

    /**
     * 本地默认配置
     */
    @NestedConfigurationProperty
    private final DefaultPacsServerProperties defaultConfig = new DefaultPacsServerProperties();

    /**
     * 远程配置
     */
    @NestedConfigurationProperty
    private final PacsServerProperties remoteConfig = new PacsServerProperties();

    /**
     * 拉图配置
     */
    @NestedConfigurationProperty
    private final PacsPullTaskProperties pullConfig= new PacsPullTaskProperties();
}
