package com.hyl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author AnneHan
 * @date 2023-09-15
 */
@Component
public class ApplicationUtils implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUtils.class);
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("进入application上下文处理器");
        ApplicationUtils.setApplicationContextStatic(applicationContext);
    }

    public static <T> T getBean(String name, Class<T> tClass) {
        if (null == context) {
            return null;
        }
        return context.getBean(name, tClass);
    }

    public static <T> T getBean(Class<T> tClass) {
        if (null == context) {
            return null;
        }
        return context.getBean(tClass);
    }

    public static void setApplicationContextStatic(final ApplicationContext applicationContext) {
        ApplicationUtils.context = applicationContext;

    }
}
