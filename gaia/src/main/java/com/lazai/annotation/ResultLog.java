package com.lazai.annotation;



import com.lazai.enums.MethodTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResultLog {

    String name() default  "";

    MethodTypeEnum methodType() default MethodTypeEnum.UNKNOWN;

    boolean printParam() default true;

    boolean printResult() default true;



}
