package cn.holelin.dicom.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/3/23 4:43 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/23 4:43 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class DicomTag {
    private Integer id;
    /**
     * Tag标识
     */
    private String tagName;

    /**
     * 标准坐标
     */
    private String coordinates;

    /**
     * org.dcm4che3.data.Tag类中对应的Tag值
     */
    private Long tagValue;

    /**
     * 是否必须存在
     */
    private Boolean isMust;

    /**
     * 是否需要脱敏
     */
    private Boolean isNeedDesensitized;

    /**
     * 是否需要校验Tag值的合法性
     */
    private Boolean isNeedCheck;
    /**
     * 当前字段是否需要存储到数据库
     */
    private Boolean isNeedStore;

    /**
     * 值的类型
     *
     * @see org.dcm4che3.data.VR
     */
    private String vr;

    private String vm;

    /**
     * 描述
     */
    private String description;

    private LocalDateTime createdTime;

}
