package com.hyl.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * api服务器应用程序启动器
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@SpringBootApplication(scanBasePackages = {"com.hyl"},exclude = {MultipartAutoConfiguration.class})
@PropertySource(value = "classpath:config-${spring.profiles.active}.properties", encoding = "UTF-8")
@EnableAsync
@EnableConfigurationProperties

public class ApiServerApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(ApiServerApplicationStarter.class,args);
    }
}
