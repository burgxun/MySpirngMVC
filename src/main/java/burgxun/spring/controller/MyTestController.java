package burgxun.spring.controller;

import burgxun.spring.annotation.Autowired;
import burgxun.spring.annotation.Controller;
import burgxun.spring.annotation.RequestMapping;
import burgxun.spring.service.TestService;
import burgxun.spring.utils.View;
import burgxun.spring.utils.ViewData;
import burgxun.spring.utils.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName MyTestController
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/28 22:16
 **/
@Controller
@RequestMapping("/test")
public class MyTestController {

    @Autowired("testService")
    TestService testService;

    //使用RequestMapping注解指明forward1方法的访问路径
    @RequestMapping("/login")
    public View forward1() {
        System.out.println("login...");
        testService.test();
        return new View("/jsps/login.jsp");
    }


    @RequestMapping(value = "/index")
    public View index() {
        System.out.println("index...");
        testService.test();
        String name = WebApplicationContext.requestThreadLocal.get().getParameter("username");
        ViewData viewData = new ViewData();
        viewData.setItem("showMsg", "欢迎登录：" + name);
        return new View("/jsps/index.jsp");
    }
}
