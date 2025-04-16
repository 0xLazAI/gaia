package com.lazai.core.common;

import com.lazai.utils.TraceIdUtil;
import org.slf4j.MDC;

public class GraphQLResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String traceId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public static<T> GraphQLResponse<T> success(T data){
        GraphQLResponse<T> response = new GraphQLResponse<T>();
        response.setData(data);
        response.setSuccess(true);
        response.setTraceId(MDC.get(TraceIdUtil.TRACE_ID));
        return response;
    }

}
