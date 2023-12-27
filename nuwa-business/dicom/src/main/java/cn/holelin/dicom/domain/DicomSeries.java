package cn.holelin.dicom.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/19 12:42 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/19 12:42 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DicomSeries extends DicomSeriesBaseInfo {

    /**
     * ---------------------
     *
     *       业务字段
     *
     * ---------------------
     */
    /**
     * 序列影像文件总帧数
     */
    private Integer numberOfInstances = 0;

    /**
     * InstanceNumber起始值
     */
    private Integer minInstanceNumber = Integer.MAX_VALUE;

    /**
     * InstanceNumber结束值
     */
    private Integer maxInstanceNumber = Integer.MIN_VALUE;

    /**
     * 序列是否完整,值为false时即为序列缺帧
     * 根据序列中InstanceNumber最大值和最小值的差值是否等于总帧数
     */
    private Boolean isWhole = true;

    /**
     * 序列是否连续,值为false时即为序列不连续
     * 根据InstanceNumber之间的差值是否为1
     */
    private Boolean isSerial = true;

    /**
     * 序列最后一帧解析的时间
     */
    private LocalDateTime lastReceivedTime;

    /**
     * 序列权重,权重最大的序列则为最优序列
     */
    private Integer weight = 0;
}
