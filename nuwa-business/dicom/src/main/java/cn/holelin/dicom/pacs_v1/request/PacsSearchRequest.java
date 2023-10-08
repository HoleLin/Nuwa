package cn.holelin.dicom.pacs_v1.request;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author HoleLin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PacsSearchRequest extends PacsDimseConditionBase {
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
}
