package com.atxiaojie.myspringmvc.service.impl;

import com.atxiaojie.myspringmvc.annotation.MyService;
import com.atxiaojie.myspringmvc.service.MyTestService;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MyTestServiceImpl
 * @Description: 测试serviceImpl
 * @author: zhouxiaojie
 * @date: 2021/10/31 15:41
 * @Version: V1.0.0
 */
@MyService("myTestServiceImpl")
public class MyTestServiceImpl implements MyTestService{

    public String query(String name, String age) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("age", age);
        return map.toString();
    }
}
