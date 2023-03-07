package xyz.thewind.music.interceptor;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AccessLimit {

    /**
     * 指定时间 单位：秒
     *
     * @return
     */
    int seconds() default 60 * 60 * 24;


    int hours() default 24;

    /**
     * 指定时间内API请求次数
     *
     * @return
     */
    int maxCount() default 100;

}