package com.liubj.ipinterceptor.config;

import com.liubj.ipinterceptor.interceptors.AccessLimitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created with IntelliJ IDEA.
 * User: liubaojun
 * Date: 2022/9/17
 * Time: 20:11
 * Description: No Description
 */
@Configuration
public class WebFilterConfig implements WebMvcConfigurer {

    /**
     * 这里需要先将限流拦截器入住，不然无法获取到拦截器中的redistemplate
     * @return
     */
    @Bean
    public AccessLimitInterceptor getAccessLimitInterceptor(){
        return  new AccessLimitInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAccessLimitInterceptor()).addPathPatterns("/**");
    }
}
