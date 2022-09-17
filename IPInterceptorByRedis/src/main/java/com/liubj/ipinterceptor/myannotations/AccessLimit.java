package com.liubj.ipinterceptor.myannotations;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: liubaojun
 * Date: 2022/9/17
 * Time: 19:05
 * Description: No Description
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@Documented
@Inherited
public @interface AccessLimit {
    /**
     * second 时间内，最多请求次数
     * @return
     */
     int times() default 5;

    /**
     * 指定Second ，Redis过期时间
     * @return
     */
    int second() default 10;
}
