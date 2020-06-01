package burgxun.spring.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ViewData
 * @Auther burgxun
 * @Description:
 * @Date 2020/6/1 14:20
 **/
public class ViewData {
    private HttpServletRequest request;

    public ViewData() {
        request = WebApplicationContext.requestThreadLocal.get();//获取当前的请求
    }

    public void setItem(String name, Object object) {
        this.request.setAttribute(name, object);
    }
}
