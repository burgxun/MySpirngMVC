package burgxun.spring.service;

import burgxun.spring.annotation.Service;

/**
 * @ClassName TestService
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/28 22:17
 **/
@Service("testService")
public class TestService {

    public String test() {
        return " Hello My MVC";
    }
}
