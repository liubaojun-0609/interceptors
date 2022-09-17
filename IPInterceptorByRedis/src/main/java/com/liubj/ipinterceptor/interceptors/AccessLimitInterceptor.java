package com.liubj.ipinterceptor.interceptors;

import com.liubj.ipinterceptor.cache.IGlobalCache;
import com.liubj.ipinterceptor.myannotations.AccessLimit;
import com.liubj.ipinterceptor.utils.IpUtil;
import com.liubj.ipinterceptor.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: liubaojun
 * Date: 2022/9/17
 * Time: 19:32
 * Description: No Description
 */
@Component
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Resource
    private IGlobalCache iGlobalCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断handler是否为HandlerMethod实例
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            //判断方法上是否有限流注解
            if(!method.isAnnotationPresent(AccessLimit.class)){
                return true;
            }

            //获取注解上的内容
            AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
            int second = accessLimit.second();
            int times = accessLimit.times();

            //拼接Redis key  IP + URI
            String redisKey  = IpUtil.getIpAddr(request) + request.getRequestURI();

            //从Redis中获取key
            Integer maxTimes = null;
            String value = String.valueOf(iGlobalCache.get(redisKey));
            if(!StringUtils.isEmpty(value) && !"null".equals(value)){
                maxTimes = Integer.valueOf(value);
            }

            if(maxTimes == null){
                //Redis 中没有ip对应的次数，说明是第一次调用 放入Redis
                iGlobalCache.set(redisKey,1,second);
            } else if (maxTimes < times) {
                //访问次数小于限制次数 允许继续访问   访问次数加一  重新设置过期时间
                iGlobalCache.set(redisKey,maxTimes + 1,second);
            }else {
                log.info(redisKey + " 请求过于频繁");
                return setResponse("当前IP请求过于频繁，请稍后再试!",response);
            }

        }
        return true;
    }

    private boolean setResponse(String results, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = null;
        try {
            response.setHeader("Content-type", "application/json; charset=utf-8");
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtil.object2Json(results).getBytes("UTF-8"));
        } catch (Exception e) {
            log.error("setResponse方法报错", e);
            return false;
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
        return true;
    }
}
