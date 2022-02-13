package com.atxiaojie.myspringmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyService {

    /**
     * @MethodsName: value
     * @Description 给MyService注册别名
     * @Author zhouxiaojie
     * @Date 15:21 2021/10/31
     * @Param []
     * @return java.lang.String
     **/
    String value() default "";
}
