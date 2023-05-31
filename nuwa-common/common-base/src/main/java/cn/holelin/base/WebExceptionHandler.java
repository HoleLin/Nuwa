package cn.holelin.base;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * ClassName: WebExceptionHandler
 * web异常处理类
 *
 * @author HoleLin
 * @version 1.0
 * @date 2019/10/5
 */
@RestControllerAdvice
@ResponseBody
@Slf4j
public class WebExceptionHandler implements ResponseBodyAdvice {
    private ThreadLocal<Object> modelHolder = new ThreadLocal<>();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseMessage handleIllegalParamException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String tips = "参数不合法";
        if (errors.size() > 0) {
            tips = errors.get(0).getDefaultMessage();
        }
        return ResponseMessage.error(tips);
    }

    @ExceptionHandler(ResultException.class)
    public ResponseMessage handleResultException(ResultException e, HttpServletRequest request) {
        log.debug("uri={} | requestBody={}", request.getRequestURI(),
                JSON.toJSONString(modelHolder.get()));
        return ResponseMessage.error("ResultException");
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseMessage handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("uri={} | requestBody={}", request.getRequestURI(),
                JSON.toJSONString(modelHolder.get()), e);
        return ResponseMessage.error("IllegalArgumentException");
    }

    // NoSuchElementException
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseMessage handleNoSuchElementException(NoSuchElementException e, HttpServletRequest request) {
        log.error("uri={} | requestBody={}", request.getRequestURI(),
                JSON.toJSONString(modelHolder.get()), e);
        return ResponseMessage.error("NoSuchElementException");
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        // ModelHolder 初始化
        modelHolder.set(webDataBinder.getTarget());
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter,
                                  MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        // ModelHolder 清理
        modelHolder.remove();
        return body;
    }
}
