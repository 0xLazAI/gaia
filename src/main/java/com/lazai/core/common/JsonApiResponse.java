package com.lazai.core.common;

import com.lazai.utils.TraceIdUtil;
import org.slf4j.MDC;

public class JsonApiResponse<T> {

    private Integer code;
    private String message;
    private T data;
    private String traceId;

    public static <T> JsonApiResponse<T> success(T data){
        JsonApiResponse jsonApiResponse = new JsonApiResponse();
        jsonApiResponse.setCode(200);
        jsonApiResponse.setMessage("success");
        jsonApiResponse.setData(data);
        jsonApiResponse.setTraceId(MDC.get(TraceIdUtil.TRACE_ID));
        return jsonApiResponse;
    }

    public static <T> JsonApiResponse<T> failed(T data, Integer code, String message){
        JsonApiResponse jsonApiResponse = new JsonApiResponse();
        jsonApiResponse.setCode(code);
        jsonApiResponse.setMessage(message);
        jsonApiResponse.setData(data);
        jsonApiResponse.setTraceId(MDC.get(TraceIdUtil.TRACE_ID));
        return jsonApiResponse;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
