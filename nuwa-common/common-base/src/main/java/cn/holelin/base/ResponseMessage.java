package cn.holelin.base;

import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 响应类
 * @Author: HoleLin
 * @CreateDate: 2020/1/3 11:10
 * @UpdateUser: HoleLin
 * @UpdateDate: 2020/1/3 11:10
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@NoArgsConstructor
@Data
public class ResponseMessage<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 错误消息
     */
    private String message;
    /**
     * 响应成功时响应内容
     */
    private T result;
    /**
     * 状态码
     */
    private int status;
    /**
     * 自定状态码
     */
    private String code;
    /**
     * 时间戳
     */
    private Long timestamp;


    public static <T> ResponseMessage<T> error(String message) {
        return error(500, "-1", message);
    }

    public static <T> ResponseMessage<T> error(int status, String code, String message) {
        ResponseMessage<T> msg = new ResponseMessage<>();
        msg.message = message;
        msg.status(status);
        msg.code(code);
        return msg.putTimeStamp();
    }

    public static <T> ResponseMessage<T> ok() {
        return ok(null);
    }

    public static <T> ResponseMessage<T> ok(T result) {
        return new ResponseMessage<T>()
                .result(result)
                .putTimeStamp()
                .code("0")
                .status(200);
    }

    public ResponseMessage<T> status(int status) {
        this.status = status;
        return this;
    }

    public ResponseMessage<T> code(String code) {
        this.code = code;
        return this;
    }

    private ResponseMessage<T> putTimeStamp() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public ResponseMessage<T> result(T result) {
        this.result = result;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss");
    }

}
