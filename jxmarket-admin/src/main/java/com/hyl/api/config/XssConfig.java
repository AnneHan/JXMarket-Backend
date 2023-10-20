package com.hyl.api.config;

import com.hyl.api.interceptor.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Configuration
public class XssConfig {
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new XssFilter());
        //filter order ,set it first
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        //set filter all url mapping
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
