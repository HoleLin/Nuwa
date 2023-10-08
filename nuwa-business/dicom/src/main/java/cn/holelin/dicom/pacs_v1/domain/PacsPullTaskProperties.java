package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;

/**
 * Pacs拉图任务配置信息类
 *
 * @author HoleLin
 */
@Data
public class PacsPullTaskProperties {

    /**
     * 单个用户拉图队列阀值
     * 默认为10
     */
    private Integer singleUserPullTaskQueueThreshold = 10;
}
