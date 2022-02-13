package com.atxiaojie.myspringmvc.tomcat;

import com.atxiaojie.myspringmvc.config.ServletConfig;
import com.atxiaojie.myspringmvc.config.ServletConfigMapping;
import com.atxiaojie.myspringmvc.servlet.MyRequest;
import com.atxiaojie.myspringmvc.servlet.MyResponse;
import com.atxiaojie.myspringmvc.servlet.Servlet;
import com.atxiaojie.myspringmvc.utils.HttpUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MyTomcat
 * @Description: TODO
 * @author: zhouxiaojie
 * @date: 2021/11/6 9:42
 * @Version: V1.0.0
 */
public class MyTomcat {

    private int port = 8080;

    public MyTomcat(){

    }

    public MyTomcat(int port){
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private Map<String, Class<Servlet>> stringClassMap = new HashMap<String, Class<Servlet>>();
    public void initServlet() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for(ServletConfig servletConfig : ServletConfigMapping.getConfigs()){
            Class<Servlet> servletClass = (Class<Servlet>) Class.forName(servletConfig.getClazz());
            Servlet servlet = servletClass.newInstance();
            servlet.init();
            stringClassMap.put(servletConfig.getUrlMapping(), servletClass);
        }
    }

    public void disPatch(MyRequest request, MyResponse response) throws IllegalAccessException, InstantiationException {
        Class<Servlet> servletClass = stringClassMap.get(request.getUrl());
        if(servletClass != null){
            Servlet servlet = servletClass.newInstance();
            servlet.service(request, response);
        }else{
            response.write(HttpUtil.getHttpResponseContext404());
        }
    }

    public void start() throws Exception {
        initServlet();
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("my tomcat started on port:" + port);
        while (true){
            Socket socket = serverSocket.accept();
            MyRequest request = new MyRequest(socket.getInputStream());
            MyResponse response = new MyResponse(socket.getOutputStream());
            if(request.getUrl().equals("/")){
                response.write(HttpUtil.getHttpResponseContext404());
            }else if(stringClassMap.get(request.getUrl()) == null){
                response.writeHtml(request.getUrl());
            }else {
                disPatch(request, response);
            }
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        MyTomcat myTomcat = new MyTomcat();
        myTomcat.start();
    }
}
