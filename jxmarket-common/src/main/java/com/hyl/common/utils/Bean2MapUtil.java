package com.hyl.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * bean和map互转工具栏
 * @author AnneHan
 * @date 2023-09-15
 */
public class Bean2MapUtil {
    private static final Logger logger = LoggerFactory.getLogger(Bean2MapUtil.class);

    public static Map<String, Object> beanToMap(Object obj, String... ignoreProperties) {
        Map<String, Object> params = new HashMap<>();
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
            Set<String> set = null;
            if (CollectionUtils.isNotEmpty(ignoreList)) {
                set = new HashSet<>(ignoreList);
            }
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                if (!"class".equals(name)) {
                    if (CollectionUtils.isNotEmpty(set) && set.contains(name)) {
                        continue;
                    }
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                }
            }
        } catch (Exception e) {
            logger.error("BeanUtil beanToMap 转换失败，失败原因:{}", e.getMessage());
        }
        return params;
    }

    public static Map<String, Object> beanToMapObject(Object obj, String... ignoreProperties) {
        Map<String, Object> params = new HashMap<>();
        try {
            HandleBeanToMap handleBeanToMap = new HandleBeanToMap(obj, ignoreProperties).invoke();
            PropertyUtilsBean propertyUtilsBean = handleBeanToMap.getPropertyUtilsBean();
            PropertyDescriptor[] descriptors = handleBeanToMap.getDescriptors();
            Set<String> set = handleBeanToMap.getSet();
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                if (!"class".equals(name)) {
                    if (CollectionUtils.isNotEmpty(set) && set.contains(name)) {
                        continue;
                    }
                    Object value = propertyUtilsBean.getNestedProperty(obj, name);
                    params.put(name, value != null ? value.toString() : "");
                }
            }
        } catch (Exception e) {
            logger.error("BeanUtil beanToMap 转换失败，失败原因:{}", e.getMessage());
        }
        return params;
    }


    public static <T> T map2Bean(Map<String, Object> map, Class<T> class1) {
        T bean = null;
        try {
            bean = class1.newInstance();
            BeanUtils.populate(bean, map);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("BeanUtil-> map2Bean 转换失败:{}", e.getMessage());
        }
        return bean;
    }

    private static class HandleBeanToMap {
        private final Object obj;
        private final String[] ignoreProperties;
        private PropertyUtilsBean propertyUtilsBean;
        private PropertyDescriptor[] descriptors;
        private Set<String> set;

        public HandleBeanToMap(Object obj, String... ignoreProperties) {
            this.obj = obj;
            this.ignoreProperties = ignoreProperties;
        }

        public PropertyUtilsBean getPropertyUtilsBean() {
            return propertyUtilsBean;
        }

        public PropertyDescriptor[] getDescriptors() {
            return descriptors;
        }

        public Set<String> getSet() {
            return set;
        }

        public HandleBeanToMap invoke() {
            propertyUtilsBean = new PropertyUtilsBean();
            descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
            set = null;
            if (CollectionUtils.isNotEmpty(ignoreList)) {
                set = new HashSet<>(ignoreList);
            }
            return this;
        }
    }


}
