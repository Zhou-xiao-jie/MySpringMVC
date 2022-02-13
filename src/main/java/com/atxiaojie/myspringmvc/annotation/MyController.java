package com.atxiaojie.myspringmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {

    /**
     * @MethodsName: value
     * @Description 给controller注册别名
     * @Author zhouxiaojie
     * @Date 15:06 2021/10/31
     * @Param []
     * @return java.lang.String
     **/
    String value() default "";
}
