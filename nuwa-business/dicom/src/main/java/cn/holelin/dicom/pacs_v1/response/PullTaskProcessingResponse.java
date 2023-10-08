package cn.holelin.dicom.pacs_v1.response;

import cn.holelin.dicom.pacs_v1.domain.PullTaskResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author HoleLin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PullTaskProcessingResponse extends PullTaskResult {
}
