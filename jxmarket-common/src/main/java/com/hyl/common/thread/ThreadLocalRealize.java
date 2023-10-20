package com.hyl.common.thread;

import com.alibaba.fastjson.JSONObject;
import com.hyl.common.domain.JwtData;

/**
 * 线程本地实现
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public class ThreadLocalRealize {

    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置线程本地变量
     * 设置对象
     *
     * @param value 价值
     */
    public static void setThreadLocalVar(String value) {
        THREAD_LOCAL.set(value);

    }

    /**
     * 获取线程本地变量
     * 获取对象
     *
     * @return {@link Object}
     */
    public static Object getThreadLocalVar() {
        return THREAD_LOCAL.get();
    }

    /**
     * 删除线程本地变量
     */
    public static void removeThreadLocalVar() {
        THREAD_LOCAL.remove();
    }

    /**
     * jwt数据
     *
     * @return {@link JwtData}
     */
    public static JwtData getJwtData() {
        Object obj = ThreadLocalRealize.getThreadLocalVar();
        if (null == obj) {
            return null;
        }
        return JSONObject.parseObject(obj.toString(), JwtData.class);
    }

}
