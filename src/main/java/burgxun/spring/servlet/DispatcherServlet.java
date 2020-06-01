package burgxun.spring.servlet;

import burgxun.spring.annotation.Autowired;
import burgxun.spring.annotation.Controller;
import burgxun.spring.annotation.RequestMapping;
import burgxun.spring.annotation.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DispatcherServlet
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/28 22:31
 **/
public class DispatcherServlet extends HttpServlet {
    List<String> classNames = new ArrayList<String>();
    Map<String, Object> beans = new HashMap<String, Object>();
    Map<String, Object> handlerMethod = new HashMap<String, Object>();

    @Override
    public void init() throws ServletException {
        System.out.println("======init DispatcherServlet");
        //Step1 包扫描
        scanPackage("burgxun.spring");
        for (String name : classNames) {
            System.out.println(name);
        }
        //Step2 类实例化
        classInstance();
        for (Map.Entry<String,Object> entry : beans.entrySet()) {
            System.out.println(entry.getKey());
        }

        //Step3 完成依赖注入
        inject();

        //Step4 完成映射 url和Controller 中的关系映射
        handlerMapping();
        for (Map.Entry<String,Object> entry : handlerMethod.entrySet()) {
            System.out.println(entry.getKey());
        }
    }

    /*关系映射*/
    private void handlerMapping() {
        if (beans.isEmpty()) {
            System.out.println("没有实例化的类");
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            /*从集合中获取实例*/
            Object instance = entry.getValue();
            if (instance.getClass().isAnnotationPresent(Controller.class)) {
                RequestMapping requestMapping =
                        (RequestMapping) instance.getClass().getAnnotation(RequestMapping.class);

                String controllerPath = requestMapping.value();
                Method[] methods = instance.getClass().getMethods();
                for (Method method : methods) {
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping mapping =
                                (RequestMapping) method.getAnnotation(RequestMapping.class);
                        String methodPath = mapping.value();
                        handlerMethod.put(controllerPath + methodPath, method);
                    }
                }
            }

        }
    }

    /*依赖注入*/
    private void inject() {
        if (beans.isEmpty()) {
            System.out.println("没有实例化的类");
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            /*从集合中获取实例*/
            Object instance = entry.getValue();

            /*获取类中的所有成员属性*/
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                /*判断是否有自动装配的属性*/
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = (Autowired) field.getAnnotation(Autowired.class);
                    String value = autowired.value();
                    field.setAccessible(true);
                    try {
                        /*最关键的地方  完成依赖注入*/
                        field.set(instance, beans.get(value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*类实例化*/
    private void classInstance() {
        if (classNames.isEmpty()) {
            System.out.println("没有扫描到任何类");
        }
        for (String name : classNames) {
            //burgxun.spring.annotation.RequestMapping.class->burgxun.spring.annotation.RequestMapping
            String realName = name.replace(".class", "");

            try {
                Class cls = Class.forName(realName);
                if (cls.isAnnotationPresent(Controller.class)) {
                    Controller controller = (Controller) cls.getAnnotation(Controller.class);
                    /*完成Controller的实例化*/
                    Object instance = cls.newInstance();

                    RequestMapping requestMapping =
                            (RequestMapping) cls.getAnnotation(RequestMapping.class);
                    String mappingValue = requestMapping.value();

                    /*吧类的路径 和实例化的对象绑定*/
                    beans.put(mappingValue, instance);
                }

                if (cls.isAnnotationPresent(Service.class)) {
                    Service service = (Service) cls.getAnnotation(Service.class);

                    Object instance = cls.newInstance();
                    beans.put(service.value(), instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /*包扫描的具体函数*/
    private void scanPackage(String basePackage) {
        /*把路径中的. 替换成/*/
        String path = basePackage.replaceAll("\\.", "/");
        URL url = getClass().getClassLoader().getResource("/" + path);

        //目录的递归扫描
        String filePath = url.getFile();
        System.out.println("fileStr:" + filePath);
        File[] files = new File(filePath).listFiles();//得到目录下的说有文件

        for (File file : files) {
            /*判断是否是一个目录*/
            if (file.isDirectory()) {
//                System.out.println(file.getPath());
                scanPackage(basePackage + "." + file.getName());
            } else {
                classNames.add(basePackage + "." + file.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append("Served at:").append(req.getContextPath());
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        System.out.println("uri:" + uri);//   /MySpirngMVC/text/index

        String contextPath = req.getContextPath();
        System.out.println("ContextPath:" + contextPath);// /MySpirngMVC

        String path = uri.replaceAll(contextPath, "");
        System.out.println("path:" + path);//  /text/index

        Method method = (Method) handlerMethod.get(path);
        /*  ===>/test/index  */
        Object instance = beans.get("/" + path.split("/")[1]);
        try {
            Object object = method.invoke(instance);
            resp.getWriter().append("===>Result is:").append((String)object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
