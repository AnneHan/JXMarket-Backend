package com.hyl.api.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysLogEntity;
import com.hyl.api.system.mapper.SystemLogMapper;
import com.hyl.api.system.service.ISystemLogService;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import com.hyl.core.common.Query;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 系统日志信息
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class ISystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SysLogEntity> implements ISystemLogService {

    @Resource
    SystemLogMapper systemLogMapper;


    @Override
    public void saveData(SysLogEntity sysLog) {
        systemLogMapper.insert(sysLog);
    }


    /**
     * 分页查询登录日志
     *
     * @param params
     * @return
     * @throws HylException
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) throws HylException {
        IPage<SysLogEntity> page = this.page(
                new Query<SysLogEntity>().getPage(params),
                new QueryWrapper<SysLogEntity>()
                        .like(ObjectUtils.isNotEmpty(params.get("logContent")), "log_content", params.get("logContent"))
                        .gt(ObjectUtils.isNotEmpty(params.get("createTime")), "create_time", params.get("createTime"))
                        .like(ObjectUtils.isNotEmpty(params.get("username")), "username", params.get("username"))
                        .like(ObjectUtils.isNotEmpty(params.get("userid")), "userid", params.get("userid"))
                        .eq(ObjectUtils.isNotEmpty(params.get("ip")), "ip", params.get("ip"))
                        .eq(ObjectUtils.isNotEmpty(params.get("logType")), "log_type", params.get("logType"))
                        .eq(ObjectUtils.isNotEmpty(params.get("requestType")), "request_type", params.get("requestType"))
                        .eq(ObjectUtils.isNotEmpty(params.get("operateType")), "operate_type", params.get("operateType"))
                        .orderByDesc("create_time"));

        return new PageUtils(page);
    }
}
