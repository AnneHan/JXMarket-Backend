package com.hyl.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.util.HashSet;
import java.util.Set;

/**
 * bean和bean 互转工具栏
 * @author AnneHan
 * @date 2023-09-15
 */
public class Bean2BeanUtil {

    public static String[] editBean(Object source) throws BeansException {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * bean to bean 空值null不复制
     *
     * @param source 来源
     * @param target 目标
     */
    public static void beanToBeanIgnoreNullValue(Object source, Object target) {
        BeanUtils.copyProperties(source, target, editBean(source));
    }
}
