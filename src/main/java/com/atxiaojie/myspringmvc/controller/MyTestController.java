package com.atxiaojie.myspringmvc.controller;

import com.atxiaojie.myspringmvc.annotation.MyAutowired;
import com.atxiaojie.myspringmvc.annotation.MyController;
import com.atxiaojie.myspringmvc.annotation.MyRequestMapping;
import com.atxiaojie.myspringmvc.annotation.MyRequestParam;
import com.atxiaojie.myspringmvc.service.MyTestService;
import com.atxiaojie.myspringmvc.servlet.MyRequest;
import com.atxiaojie.myspringmvc.servlet.MyResponse;

/**
 * @ClassName: MyController
 * @Description: 测试控制类
 * @author: zhouxiaojie
 * @date: 2021/10/31 15:22
 * @Version: V1.0.0
 */
@MyController("myTestController")
@MyRequestMapping("/myTestController")
public class MyTestController {

    @MyAutowired("myTestServiceImpl")
    private MyTestService myTestService;

    private int i = 0;

    @MyRequestMapping("/query")
    public void query(MyRequest request, MyResponse response,
                      @MyRequestParam("name") String name,
                      @MyRequestParam("age") String age){
        /*PrintWriter pw = response.getWriter();
        String query = myTestService.query(name, age);
        pw.write(query);*/
        String query = myTestService.query(name, age);
        response.write(query);
    }

    public int test(){
        return 0;
    }

}
