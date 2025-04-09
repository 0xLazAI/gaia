package com.lazai.annotation.aspect;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.lazai.annotation.ResultLog;
import com.lazai.core.common.LogData;
import com.lazai.exception.DomainException;
import com.lazai.utils.DateUtils;
import com.lazai.utils.TraceIdUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
@Slf4j

public class ResultLogAspect {

    private Logger logger = LoggerFactory.getLogger(ResultLogAspect.class);

    private static final Logger RESULT_LOGGER = LoggerFactory.getLogger("RESULT_LOG");

    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("@annotation(com.lazai.annotation.ResultLog)")
    public void myPointCut(){}
//    @Before("myPointCut()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取url,请求方法，ip地址，类名以及方法名，参数
//        logger.info("url={},method={},ip={},class_method={},args={}", request.getRequestURI(),request.getMethod(),request.getRemoteAddr(),joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),joinPoint.getArgs());

    }
//    @AfterReturning(pointcut = "myPointCut()")
    public void printLog(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ResultLog resultLog = method.getAnnotation(ResultLog.class);
    }

    @Around("myPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        Object result = null;
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        ResultLog resultLog = method.getAnnotation(ResultLog.class);
        LogData logData = new LogData();
        boolean success = true;
        long start = System.currentTimeMillis();
        try{
            result = point.proceed();
        }catch (Throwable e){
            success = false;
            throw e;
        }finally {
            try{
                logData.setTime(DateUtils.getTime());
                logData.setName(resultLog.name());
                if(StringUtils.isBlank(resultLog.name())){
                    logData.setName(methodSignature.getDeclaringTypeName() + "." + method.getName());
                }
                logData.setMethodTypeEnum(resultLog.methodType());

                logData.setParam(generateParam(point));
                logData.setSuccess(success);
                logData.setRt(System.currentTimeMillis() - start);
                logData.setTraceId(TraceIdUtil.getTraceId());
                logData.setResult(result);
                String json = objectMapper.writeValueAsString(logData);
                RESULT_LOGGER.info(json);
            }catch (Throwable ee){
                log.error("result log exception,bean={}", objectMapper.writeValueAsString(resultLog));
            }
        }
        return result;
    }

    private Object generateParam(ProceedingJoinPoint point){
        Object[] args = point.getArgs();
        String[] parameterName = ((MethodSignature) point.getSignature()).getParameterNames();
        Map<String, Object> paramsMap = new HashMap<>();
        for(int i = 0; i < parameterName.length; i++){
            if(args[i] instanceof RequestFacade){
                Map<String, Object> requestMap = new HashMap<>();
                RequestFacade requestFacade = ((RequestFacade) args[i]);
                requestMap.put("uri", requestFacade.getRequestURI());
                requestMap.put("ip", requestFacade.getRemoteAddr());
                requestMap.put("auth", requestFacade.getHeader(HttpHeaders.AUTHORIZATION));
                paramsMap.put(parameterName[i], requestMap);
            }else {
                paramsMap.put(parameterName[i], args[i]);
            }
        }
        return paramsMap;
    }
}
