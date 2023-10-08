package cn.holelin.dicom.pacs_v1.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 操作结果
 * @author HoleLin
 */
@Data
public class OperationResponse implements Serializable {

    private Integer code;

    private String message;

}
