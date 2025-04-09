package com.lazai.core.interceptor;


import com.lazai.utils.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SystemTraceIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //如果有上层调用就用上层的ID
        String traceId = request.getHeader(TraceIdUtil.TRACE_ID);
        if (traceId == null) {
            traceId = TraceIdUtil.generateTraceId();
        }
        MDC.put(TraceIdUtil.TRACE_ID, traceId);

        //配置traceId到响应header中
        response.setHeader(TraceIdUtil.TRACE_ID, MDC.get(TraceIdUtil.TRACE_ID));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //调用结束后删除
        MDC.remove(TraceIdUtil.TRACE_ID);
    }
}
