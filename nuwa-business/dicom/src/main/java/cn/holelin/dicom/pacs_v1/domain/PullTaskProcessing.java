package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;

/**
 * @author HoleLin
 */
@Data
public class PullTaskProcessing {
    private String state;
    private Integer count;
}
