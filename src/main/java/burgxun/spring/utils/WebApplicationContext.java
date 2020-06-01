package burgxun.spring.utils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName WebApplicationContext
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/29 15:16
 **/

/* WebApplicationContext去包装下 请求下的HttpServletRequest  HttpServletResponse 后面的程序就能直接使用  可以自行脑补下*/
public class WebApplicationContext {

    /*使用ThreadLocal 目的使得 线程数据隔离*/
    public static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();
    public static ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<HttpServletResponse>();

    public HttpServletRequest getRequest() {
        return requestThreadLocal.get();
    }

    public HttpServletResponse getResponse() {
        return responseThreadLocal.get();
    }

    public HttpSession getSession() {
        return requestThreadLocal.get().getSession();
    }

    public ServletContext getServletRequest(){
        return  requestThreadLocal.get().getServletContext();
    }

}
