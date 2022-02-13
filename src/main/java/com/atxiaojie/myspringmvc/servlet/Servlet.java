package com.atxiaojie.myspringmvc.servlet;

/**
 * @ClassName: Servlet
 * @Description: 自定义Servlet
 * @author: zhouxiaojie
 * @date: 2021/11/6 0:07
 * @Version: V1.0.0
 */
public abstract class Servlet {

    public abstract void init();

    public abstract void doGet(MyRequest request, MyResponse response);

    public abstract void doPost(MyRequest request, MyResponse response);

    public Servlet(){

    }

    public void service(MyRequest request, MyResponse response){
        if("GET".equals(request.getMethod())){
            doGet(request, response);
        }else{
            doPost(request, response);
        }
    }

}
