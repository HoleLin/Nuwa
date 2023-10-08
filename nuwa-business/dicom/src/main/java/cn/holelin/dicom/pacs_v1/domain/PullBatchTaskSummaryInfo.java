package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;

/**
 * 拉图任务的批次汇总信息
 *
 * @author HoleLin
 */
@Data
public class PullBatchTaskSummaryInfo {

    /**
     * 拉图任务的批次Id
     */
    private String batchId;

    /**
     * 规划类型
     */
    private String projectType;
}
