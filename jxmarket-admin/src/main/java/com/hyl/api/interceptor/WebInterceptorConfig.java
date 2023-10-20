package com.hyl.api.interceptor;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * 网络拦截器配置
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Configuration
public class WebInterceptorConfig extends WebMvcConfigurationSupport {


    @Bean
    public WebMvcInterceptor webMbcInterceptor() {
        return new WebMvcInterceptor();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //图片资源
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webMbcInterceptor())
                .excludePathPatterns("/access")
                .excludePathPatterns("/entry")
                .excludePathPatterns("/login/monitor")
                .addPathPatterns("/**");
    }
}
