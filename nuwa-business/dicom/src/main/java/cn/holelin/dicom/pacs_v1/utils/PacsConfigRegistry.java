package cn.holelin.dicom.pacs_v1.utils;

import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.collect.Maps;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author HoleLin
 */
public class PacsConfigRegistry {

    private final static Map<String, PacsBaseConfig> CONTEXT = Maps.newConcurrentMap();

    public static String register(PacsBaseConfig config) {
        Integer remotePort = config.getRemotePort();
        String remoteAeTitle = config.getRemoteAeTitle();
        String remoteHostName = config.getRemoteHostName();
        String key = DigestUtil.md5Hex(remoteAeTitle + remoteHostName + remotePort, StandardCharsets.UTF_8);
        CONTEXT.put(key, config);
        return key;
    }

    public static PacsBaseConfig getConfig(String key) {
        return CONTEXT.get(key);
    }

}
