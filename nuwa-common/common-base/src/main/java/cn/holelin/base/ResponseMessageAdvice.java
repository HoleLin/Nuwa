package cn.holelin.base;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseMessageAdvice implements ResponseBodyAdvice {

    protected boolean isStringConverter(Class converterType) {
        return converterType.equals(StringHttpMessageConverter.class);
    }

    // 可以配合注解来使用
//    protected boolean isApiResult(MethodParameter returnType) {
//        return returnType.hasMethodAnnotation(ResponseMessage.class);
//    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
//        return !isStringConverter(converterType) && isApiResult(returnType);
        return !isStringConverter(converterType) ;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) { //关键
        return ResponseMessage.ok(body);
    }


}