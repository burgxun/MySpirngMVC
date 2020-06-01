package burgxun.spring.ioc;

import burgxun.spring.annotation.Autowired;
import burgxun.spring.annotation.Controller;
import burgxun.spring.annotation.Service;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @ClassName DependencyInjection
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/30 23:18
 **/
public class DependencyInjection {

    public static void execute() {
        if (Container.beans.isEmpty()) {
            System.err.println("没有实例化的类\n");
        }
        for (Map.Entry<String, Object> entry : Container.beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)) {
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        String injectObjectName =
                                ((Autowired) field.getAnnotation(Autowired.class)).value();
                        //没有值的话就默认使用字段名称
                        injectObjectName = injectObjectName == "" ? field.getName() : injectObjectName;
                        Object injectObject = Container.beans.get(injectObjectName);
                        field.setAccessible(true);
                        try {
                            /*最关键的地方  完成依赖注入*/
                            field.set(instance, injectObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
