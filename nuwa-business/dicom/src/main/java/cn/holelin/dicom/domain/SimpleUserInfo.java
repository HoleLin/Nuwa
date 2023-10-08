package cn.holelin.dicom.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @author HoleLin
 */
@Data
@Builder
public class SimpleUserInfo {

    /**
     * 用户名 实际上用的user表中的telephone字段
     */
    private String userId;

    private String username;

    /**
     * 单位
     */
    private String organization;

    /**
     * 用户的角色ID
     */
    private String roleId;
    /**
     * 角色中文名称
     */
    private String roleName;

    /**
     * 当前用户使用的IP地址
     */
    private String currentIpAddress;
}
