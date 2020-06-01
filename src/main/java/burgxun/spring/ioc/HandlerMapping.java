package burgxun.spring.ioc;

import burgxun.spring.annotation.Controller;
import burgxun.spring.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @ClassName HandlerMapping
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/30 23:44
 **/
public class HandlerMapping {
    public static void execute() {
        if (Container.beans.isEmpty()) {
            System.err.println("没有实例化的类");
        }
        for (Map.Entry<String, Object> entry : Container.beans.entrySet()) {
            Object instance = entry.getValue();
            String beanName = entry.getKey();
            Class<?> clazz = instance.getClass();
            if (instance.getClass().isAnnotationPresent(Controller.class)) {
                Method[] methods = instance.getClass().getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        String mappingValue =
                                ((RequestMapping) method.getAnnotation(RequestMapping.class)).value();
                        String mappingUrl = beanName + mappingValue;
                        Container.handlerMapping.put(mappingUrl, method);
                    }
                }
            }
        }
    }
}
