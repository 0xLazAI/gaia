package com.lazai.exception;

import com.lazai.core.common.JsonApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");

    @ExceptionHandler({Exception.class})
    public JsonApiResponse handlerException(Exception e, HttpServletRequest request, HttpServletResponse response){

        ERROR_LOGGER.error("handle exception:",e);

        Integer code = 500;
        String message = "System error!";
        if(e instanceof DomainException){
            code = ((DomainException) e).getCode();
            message = e.getMessage();
        }
        return JsonApiResponse.failed(null,code, message);
    }

}
