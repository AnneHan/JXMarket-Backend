/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.hyl.api.system.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

	String value() default "";

	/**
	 * 0- 操作日志 1-登录日志
	 * @return
	 */
	int logType() default 2;
	//操作类型 0-新增或者修改 1-新增 2-删除 3-修改 4-查询 5-登录
	//int operateType() default ;
}
