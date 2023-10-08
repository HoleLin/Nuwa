package cn.holelin.dicom.pacs_v1.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
