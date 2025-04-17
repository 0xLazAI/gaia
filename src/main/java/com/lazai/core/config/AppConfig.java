package com.lazai.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lazai.core.interceptor.AuthenticationInterceptor;
import com.lazai.core.interceptor.SystemTraceIdInterceptor;
import com.lazai.utils.SnowFlake;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${machineId:0}")
    private String machineId;

    @Bean
    public SnowFlake snowFlake(){
        return new SnowFlake(0,Long.parseLong(machineId));
    }


    @Resource
    private SystemTraceIdInterceptor systemTraceIdInterceptor;

    @Resource
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //配置链路追踪traceId拦截器
        registry.addInterceptor(systemTraceIdInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**");
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper;
    }

    @Bean
    public TransactionTemplate transactionTemplateCommon(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public OkHttpClient okHttpClient(){
        return  new OkHttpClient().newBuilder()
                .build();
    }

}
