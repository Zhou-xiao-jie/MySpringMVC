package com.atxiaojie.myspringmvc.servlet;

import com.atxiaojie.myspringmvc.annotation.*;
import com.atxiaojie.myspringmvc.controller.MyTestController;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DispatcherServlet
 * @Description:
 * @author: zhouxiaojie
 * @date: 2021/10/31 14:48
 * @Version: V1.0.0
 */
public class DispatcherServlet extends Servlet {

    static List<String> classUrls = new ArrayList<String>();//保存待实例化所有类的路径
    static Map<String, Object> iocMap = new HashMap<String, Object>();//ioc容器
    static Map<String, Object> urlHandlers = new HashMap<String, Object>();//地址映射

    /**
     * @MethodsName: init
     * @Description tomcat启动初始化的内容：1、扫描包，2、对找到需要实例化的类，进行实例化，3、处理依赖关系，4、路径方法绑定映射
     * @Author zhouxiaojie
     * @Date 15:56 2021/10/31
     * @Param [config]
     * @return void
     **/
    @Override
    public void init(){
        //1、扫描包
        doScanPackage("com.atxiaojie");
        //2、对找到需要实例化的类
        doInstance();
        //3、处理依赖关系
        doAutowired();
        //4、路径方法绑定映射
        doUrlMapping();
    }

    /**
     * @MethodsName: doScanPackage
     * @Description 扫描包
     * @Author zhouxiaojie
     * @Date 16:00 2021/10/31
     * @Param [packageName]
     * @return void
     **/
    private void doScanPackage(String packageName) {
        //packageName就是com.atxiaojie
        //url是一个绝对路径，例：D：/xxxx/xxx/xxx/com/atxiaojie
        String s = packageName.replaceAll("\\.", "/");
        URL url = this.getClass().getClassLoader().getResource(s);
        String fileStr = url.getFile();
        File file = new File(fileStr);
        ///com/atxiaojie目录下有多少个文件夹或者文件，然后遍历找到.class文件
        String[] fileStrs = file.list();
        for(String path : fileStrs){
            File filePath = new File(fileStr + "/" + path);
            //判断是否是文件夹，如果是递归调用找到.class文件
            if(filePath.isDirectory()){
                doScanPackage(packageName + "." + path);
            }else{
                //找到.class文件，然后去掉.class，转换成类似com.atxiaojie.myspringmvc.controller.MyTestController,保存在classUrls中
                //用来全类路径名用来创建对象
                classUrls.add(packageName + "." + filePath.getName().replace(".class", ""));
            }
        }


    }

    /**
     * @MethodsName: doInstance
     * @Description 对找到需要实例化的类，进行实例化
     * @Author zhouxiaojie
     * @Date 16:00 2021/10/31
     * @Param []
     * @return void
     **/
    private void doInstance(){
        for(String classUrl : classUrls){
            try{
                Class<?> clazz = Class.forName(classUrl);
                if(clazz.isAnnotationPresent(MyController.class)){
                    Object controllerInstance = clazz.newInstance();
                    //MyRequestMapping clazzRequestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    MyController clazzMyController = clazz.getAnnotation(MyController.class);
                    //或者去类的clazz.getSimpleName()加上转把字符串的首字母小写
                    //String s = toLowerFirstWord(clazz.getSimpleName());
                    String clazzRequestMappingValue = clazzMyController.value();
                    iocMap.put(clazzRequestMappingValue, controllerInstance);
                }else if(clazz.isAnnotationPresent(MyService.class)){
                    Object serviceInstance = clazz.newInstance();
                    MyService clazzService = clazz.getAnnotation(MyService.class);
                    String clazzServiceValue = clazzService.value();
                    iocMap.put(clazzServiceValue, serviceInstance);
                }else{
                    continue;
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
                continue;
            } catch (InstantiationException e) {
                e.printStackTrace();
                continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * @MethodsName: doAutowired
     * @Description 处理依赖关系
     * @Author zhouxiaojie
     * @Date 16:00 2021/10/31
     * @Param []
     * @return void
     **/
    private void doAutowired() {
        for(Map.Entry<String, Object> entry : iocMap.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(MyController.class)){
                Field[] fields = clazz.getDeclaredFields();
                for(Field field : fields){
                    if(field.isAnnotationPresent(MyAutowired.class)){
                        MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                        String myAutowiredValue = myAutowired.value();
                        Object ins = iocMap.get(myAutowiredValue);
                        field.setAccessible(true);
                        try {
                            field.set(instance, ins);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * @MethodsName: doUrlMapping
     * @Description 路径方法绑定映射
     * @Author zhouxiaojie
     * @Date 16:00 2021/10/31
     * @Param []
     * @return void
     **/
    private void doUrlMapping() {
        for(Map.Entry<String, Object> entry : iocMap.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(MyController.class)){
                MyRequestMapping clazzAnnotation = clazz.getAnnotation(MyRequestMapping.class);
                String classPath = clazzAnnotation.value();
                Method[] methods = clazz.getMethods();
                for(Method method : methods){
                    if(method.isAnnotationPresent(MyRequestMapping.class)){
                        MyRequestMapping methodAnnotation = method.getAnnotation(MyRequestMapping.class);
                        String methodPath = methodAnnotation.value();
                        urlHandlers.put(classPath + methodPath, method);
                    }
                }
            }
        }

    }

    @Override
    public void doPost(MyRequest request, MyResponse response){
        String uri = request.getUrl();// /myspringmvc/myTestController/query
        //String contextPath = request.getContextPath();// /myspringmvc
        //String path = uri.replace(contextPath, "");// /myTestController/query
        Method method = (Method) urlHandlers.get(uri);
        MyTestController instance = (MyTestController) iocMap.get(uri.split("/")[1]);// myTestController
        Object[] args = hand(request, response, method);
        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doGet(MyRequest request, MyResponse response){
        this.doPost(request,response);
    }

    private static Object[] hand(MyRequest request, MyResponse response, Method method){
        //拿到当前待执行的方法有哪些参数
        Class<?>[] paramClazzs = method.getParameterTypes();
        //根据参数的个数，new一个参数的数组，将方法里的所有参数赋值到args来
        Object[] args = new Object[paramClazzs.length];

        int args_i = 0;
        int index = 0;
        for(Class<?> paramClazz : paramClazzs){
            if(MyRequest.class.isAssignableFrom(paramClazz)){
                args[args_i++] = request;
            }
            if(MyResponse.class.isAssignableFrom(paramClazz)){
                args[args_i++] = response;
            }
            //从0-3判断有没有RequestParam注解，很明显paramClazz为0和1时，不是，
            //当为2和3时为@RequestParam,需要解析
            //[@com.atxiaojie.myspringmvc.annotation.MyRequestParam(value=name)]
            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if(paramAns.length > 0){
                for(Annotation paramAn : paramAns){
                    if(MyRequestParam.class.isAssignableFrom(paramAn.getClass())){
                        MyRequestParam rp = (MyRequestParam) paramAn;
                        //找到注解里的name和age
                        Map<String, String> param = request.getParam();
                        args[args_i++] = param.get(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }

    /*private static Object[] hand(MyRequest request, MyResponse response, Method method){
        //拿到当前待执行的方法有哪些参数
        Class<?>[] paramClazzs = method.getParameterTypes();
        //根据参数的个数，new一个参数的数组，将方法里的所有参数赋值到args来
        Object[] args = new Object[paramClazzs.length];

        int args_i = 0;
        int index = 0;
        for(Class<?> paramClazz : paramClazzs){
            if(ServletRequest.class.isAssignableFrom(paramClazz)){
                args[args_i++] = request;
            }
            if(ServletResponse.class.isAssignableFrom(paramClazz)){
                args[args_i++] = response;
            }
            //从0-3判断有没有RequestParam注解，很明显paramClazz为0和1时，不是，
            //当为2和3时为@RequestParam,需要解析
            //[@com.atxiaojie.myspringmvc.annotation.MyRequestParam(value=name)]
            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if(paramAns.length > 0){
                for(Annotation paramAn : paramAns){
                    if(MyRequestParam.class.isAssignableFrom(paramAn.getClass())){
                        MyRequestParam rp = (MyRequestParam) paramAn;
                        //找到注解里的name和age
                        args[args_i++] = request.getParameter(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }*/

    /**
     * 把字符串的首字母小写
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name){
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

}
