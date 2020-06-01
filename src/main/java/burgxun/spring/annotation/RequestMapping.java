package burgxun.spring.annotation;

import java.lang.annotation.*;

/**
 * @ClassName RequestMapping
 * @Auther burgxun
 * @Description:
 * @Date 2020/5/28 21:57
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {
    String value() default "";
}
