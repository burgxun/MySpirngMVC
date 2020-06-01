package burgxun.spring.ioc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Container
 * @Auther burgxun
 * @Description: 容器帮助类
 * @Date 2020/05/29 16:17
 **/
public class Container {
    /* 项目中配置的 注解能扫描到的类*/
    public static List<Class<?>> classs = new ArrayList<Class<?>>();

    /* 存储实例化后的类*/
    public static Map<String, Object> beans = new HashMap<String, Object>();

    /* 存储 url 和执行方法的 映射关系*/
    public static Map<String, Object> handlerMapping = new HashMap<String, Object>();
}
