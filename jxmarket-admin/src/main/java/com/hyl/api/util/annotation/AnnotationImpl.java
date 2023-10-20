package com.hyl.api.util.annotation;

import com.hyl.common.exception.HylException;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.hyl.common.enums.ResponseCodeEnum.VALID_IS_EMPTY;

/**
 * 实现校验对象属性的值工具类
 * @author AnneHan
 * @date 2023-09-15
 */
public class AnnotationImpl {


    /**
     * 校验注解上对象属性值是否为空值;
     * @param t
     * @param <T>
     * @throws Exception
     */
    public static <T>  void checkBeanIsEmpty(T t) throws HylException, IllegalAccessException {
        if(null == t){
            throw  new HylException(VALID_IS_EMPTY, "传入对象值空值");
        }
        //获取class对象
        Class<?> aClass = t.getClass();
        //获取当前对象所有属性  使用带Declared的方法可访问private属性
        Field[] declaredFields = aClass.getDeclaredFields();
        //遍历对象属性
        for (Field field : declaredFields) {
            //开启访问权限
            field.setAccessible(true);
            //使用此方法 field.get(Object obj) 可以获取  当前对象这个列的值
            Object o = field.get(t);
            Annotation annotation = field.getDeclaredAnnotation(CheckNotNull.class);
            //如果没有设置当前注解 不用校验
            if(annotation == null){
                continue;
            }
            //获取注解接口对象
            CheckNotNull notNull = (CheckNotNull)annotation;

            if(notNull.required()){
                //如果设置了当前注解，但是没有值，抛出异常
                if(null==o || "".equalsIgnoreCase(o.toString().trim())){
                    if(StringUtils.isNotBlank(notNull.message())){
                        //设置了注解message值 直接返回
                        throw  new HylException(VALID_IS_EMPTY, notNull.message());
                    }else{
                        //没有设置可以拼接
                        throw  new HylException(VALID_IS_EMPTY, field.getName()+" is null");
                    }
                }
            }

        }
    }
}
