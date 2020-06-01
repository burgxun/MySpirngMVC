package burgxun.spring.servlet;

import burgxun.spring.ioc.*;
import burgxun.spring.utils.View;
import burgxun.spring.utils.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @ClassName BurgxunHandlerServlet
 * @Auther burgxun
 * @Description:
 * @Date 2020/05/29 15:46
 **/
public class BurgxunHandlerServlet extends HttpServlet {


    @Override
    public void init(ServletConfig config) throws ServletException {
        /*
         * 这边就是服务启动的时候 要执行的地方  我们要做一起处理
         * Step 1: 扫描 配置的包
         * Step 2: 实例化对象
         * Step 3: 完成对象中的依赖注入
         * Step 4: 建立mapping关系  路径和方法的映射关系
         * */

        super.init(config);

        System.out.println("=======>初始化开始了");
        StartScanPackage(config);

        ClassInstance.execute();

        DependencyInjection.execute();

        HandlerMapping.execute();

        System.out.println("========>初始化结束");
    }


    private void StartScanPackage(ServletConfig config) {
        String basePackage = config.getInitParameter("basePackage");
        if (basePackage.indexOf(",") > 0) {
            String[] packageNameList = basePackage.split(",");
            for (String packageName : packageNameList) {
                ScanPackage.execute(packageName);/*扫码配置的包*/
            }
        } else {
            ScanPackage.execute(basePackage);/*扫码配置的包*/
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.printf("BurgxunHandlerServlet---->doPost \n");
        this.Run(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.printf("BurgxunHandlerServlet---->doGet \n");
        this.Run(req, resp);
    }

    private void Run(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebApplicationContext.requestThreadLocal.set(request);
        WebApplicationContext.responseThreadLocal.set(response);

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();

        String path = requestURI.replaceAll(contextPath, "");
        path=path.substring(0,path.lastIndexOf("."));
        System.out.println("path:" + path);

        Method method = (Method) Container.handlerMapping.get(path);
        if (method != null) {
            String beanName = "/" + path.split("/")[1];
            Object instance = Container.beans.get(beanName);

            Object object = null;
            try {
                object = method.invoke(instance);
                /*   response.getWriter().append("===>Result is:").append((String) object);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (object != null) {
                View view = (View) object;
                if (view.getDispatchAction().equals("forward")) {
                    //使用服务器端跳转方式
                    request.getRequestDispatcher(view.getUrl()).forward(request, response);
                } else if (view.getDispatchAction().equals("redirect")) {
                    //使用客户端跳转方式
                    response.sendRedirect(request.getContextPath() + view.getUrl());
                } else {
                    request.getRequestDispatcher(view.getUrl()).forward(request, response);
                }
            }
        }

    }
}
