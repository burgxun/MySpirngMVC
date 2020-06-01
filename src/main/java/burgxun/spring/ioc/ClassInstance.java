package burgxun.spring.ioc;

import burgxun.spring.annotation.Controller;
import burgxun.spring.annotation.RequestMapping;
import burgxun.spring.annotation.Service;

/**
 * @ClassName ClassInstance
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/30 22:18
 **/
public class ClassInstance {

    public static void execute() {
        if (Container.classs.isEmpty()) {
            System.err.println("没有扫描到任何类 \n");
        }
        for (Class<?> clazz : Container.classs) {
            if (!clazz.isInterface()) {
                if (clazz.isAnnotationPresent(Controller.class)) {
                    controllerInstance(clazz);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    serviceInstance(clazz);
                }
            }
        }
    }

    private static void controllerInstance(Class<?> clazz) {
        String mappingValue = ((RequestMapping) clazz.getAnnotation(RequestMapping.class)).value();
        String controllerName = clazz.getSimpleName().replace("Controller", "");
        String beanName = mappingValue == "" ? controllerName : mappingValue;
        try {
            Object object = clazz.newInstance();
            Container.beans.put(beanName, object);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void serviceInstance(Class<?> clazz) {
        Service service = (Service) clazz.getAnnotation(Service.class);
        String beanName = service.value() == "" ? clazz.getName() : service.value();
        try {
            Object object = clazz.newInstance();
            Container.beans.put(beanName, object);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
