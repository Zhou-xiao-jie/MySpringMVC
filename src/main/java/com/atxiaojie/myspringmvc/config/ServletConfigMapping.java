package com.atxiaojie.myspringmvc.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ServletConfigMapping
 * @Description: ServletConfigMapping
 * @author: zhouxiaojie
 * @date: 2021/11/6 0:20
 * @Version: V1.0.0
 */
public class ServletConfigMapping {

    private static List<ServletConfig> config = new ArrayList<ServletConfig>();

    static {
        config.add(new ServletConfig("MyServlet", "/myTestController/query", "com.atxiaojie.myspringmvc.servlet.DispatcherServlet"));
    }

    public static List<ServletConfig> getConfigs(){
        return config;
    }
}
