package com.hyl.api.system.aspect;

import com.alibaba.fastjson.JSON;
import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.dto.login.LoginParamDto;
import com.hyl.api.system.entity.SysLogEntity;
import com.hyl.api.system.service.ISystemLogService;
import com.hyl.common.constants.CacheConstant;
import com.hyl.common.enums.HttpMethod;
import com.hyl.common.exception.HylException;
import com.hyl.common.redis.service.RedisService;
import com.hyl.common.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 系统日志，切面处理类
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Aspect
@Component
public class SysLogAspect {

    @Resource
    private ISystemLogService sysLogService;

    @Resource
    RedisService redisService;

    @Pointcut("@annotation(com.hyl.api.system.annotation.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        saveSysLog(point, time);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) throws HylException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        SysLogEntity sysLogEntity = new SysLogEntity();
        SysLog syslog = method.getAnnotation(SysLog.class);
        // 操作类型 0-新增或者修改 1-新增 2-删除 3-修改 4-查询 5-登录 6-其他
        if (syslog != null) {
            //注解上的描述
            sysLogEntity.setLogContent(syslog.value());
            sysLogEntity.setLogType(String.valueOf(syslog.logType()));
            String name = method.getName();
            if (name.startsWith("login")) {
                sysLogEntity.setOperateType("5");
            } else if (name.startsWith("save")) {
                sysLogEntity.setOperateType("1");
            } else if (name.startsWith("list") || name.startsWith("select") || name.startsWith("query")) {
                sysLogEntity.setOperateType("4");
            } else if (name.startsWith("upd")) {
                sysLogEntity.setOperateType("3");
            } else if (name.startsWith("del")) {
                sysLogEntity.setOperateType("2");
            } else if (name.startsWith("deal")) {
                sysLogEntity.setOperateType("0");
            } else {
                sysLogEntity.setOperateType("6");
            }
            //请求的方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            sysLogEntity.setMethod(className + "." + methodName + "()");
            //请求的参数
            Object[] args = joinPoint.getArgs();
            try {
                System.out.println(getRequestAttributes().getRequest().getMethod());
                if (HttpMethod.PUT.name().equals(getRequestAttributes().getRequest().getMethod()) || HttpMethod.POST.name().equals(getRequestAttributes().getRequest().getMethod())) {
                    String params = argsArrayToString(joinPoint.getArgs());
                    sysLogEntity.setRequestParam(StringUtils.substring(params, 0, 2000));
                } else {
                    Map<?, ?> paramsMap = (Map<?, ?>) getRequestAttributes().getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                    if (ObjectUtils.isEmpty(paramsMap)) {
                        paramsMap = getRequestAttributes().getRequest().getParameterMap();
                        sysLogEntity.setRequestParam(StringUtils.substring(JSON.toJSONString(paramsMap), 0, 2000));
                    } else {
                        sysLogEntity.setRequestParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
                    }
                }
                //String params = JSON.toJSONString(args);
                //sysLogEntity.setRequestParam(params);
                String userName = "";
                if (syslog.logType() == 1) {
                    //System.out.println(args[0]);
                    // LoginParamDto loginParamDto = JSONObject.parseObject(args[0].toString(), LoginParamDto.class);
                    userName = ((LoginParamDto) args[0]).getUsername();
                } else {
                    userName = null == redisService.get(CacheConstant.OAUTH_USER_INFO_KEY_SYSTEM_LOGIN_NAME) ? "" : redisService.get(CacheConstant.OAUTH_USER_INFO_KEY_SYSTEM_LOGIN_NAME).toString();
                }
                sysLogEntity.setUsername(userName);
                sysLogEntity.setCreateBy(userName);
                sysLogEntity.setUserid(userName);
            } catch (Exception e) {

            }
            //设置IP地址
            sysLogEntity.setIp((WebUtils.getRemoteAddress(request)));
            sysLogEntity.setRequestUrl(request.getRequestURL().toString());
            //用户名
            sysLogEntity.setRequestType(request.getMethod());
            //登录日志
            sysLogEntity.setCostTime(time);
            //保存系统日志
            sysLogService.saveData(sysLogEntity);
        }
    }

    public ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (!ObjectUtils.isEmpty(o) && !isFilterObject(o)) {
                    try {
                        Object jsonObj = JSON.toJSON(o);
                        params += jsonObj.toString() + " ";
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

    public static void main(String[] args) {

    }
}
