package com.hyl.api.system.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.business.MCustomerInfoEntity;
import com.hyl.api.system.mapper.business.MCustomerInfoMapper;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.service.business.IMCustomerInfoService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import com.hyl.core.common.Query;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 客户信息表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-18
 */
@Service
public class MCustomerInfoServiceImpl extends ServiceImpl<MCustomerInfoMapper, MCustomerInfoEntity> implements IMCustomerInfoService {
    private static final Logger logger = LoggerFactory.getLogger(MCustomerInfoServiceImpl.class);

    @Resource
    private MCustomerInfoMapper customerInfoMapper;
    @Resource
    private SysLoginService loginService;

    /**
     * 分页查询用户信息
     *
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) throws HylException {
        String username = String.valueOf(params.get("username"));
        IPage<MCustomerInfoEntity> page = this.page(
                new Query<MCustomerInfoEntity>().getPage(params),
                new QueryWrapper<MCustomerInfoEntity>()
                        .like(StringUtils.isNotEmpty(username),"username", username)
                        .like(ObjectUtils.isNotEmpty(params.get("tel")),"tel",params.get("tel"))
                        .like(ObjectUtils.isNotEmpty(params.get("membership")),"membership",params.get("membership"))
                        .orderByDesc("create_time"));
        return new PageUtils(page);
    }

    /**
     * 保存用户信息
     */
    @Override
    @Transactional
    public ResultBean saveUser(Map<String, Object> map) throws HylException {
        MCustomerInfoEntity user = JSON.parseObject(JSON.toJSONString(map), MCustomerInfoEntity.class);
        if (StringUtils.isEmpty(user.getUsername())) {
            return ResultBean.error("客户名为空", "USERNAME IS EXISTS", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        int count = customerInfoMapper.selectByName(user.getUsername());
        if (count > 0) {
            return ResultBean.error("客户名已存在", "USERNAME IS EXISTS", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }

        StringBuffer buffer1 = checkInfo(user);
        if (!StringUtils.isEmpty(buffer1)) {
            return ResultBean.error(buffer1.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }

        SysUserEntity accuser = loginService.getLoginUser();
        user.setCreateBy(accuser.getUsername());
        user.setStatus(true);
        user.setDelFlag(false);
        try {
            this.baseMapper.insert(user);
        } catch (Exception e) {
            log.error("新增客户异常," + e.getMessage());
            throw  e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional
    public ResultBean updateUser(Map<String, Object> map) throws HylException {
        MCustomerInfoEntity user = JSON.parseObject(JSON.toJSONString(map), MCustomerInfoEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            return ResultBean.error(ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }

        try {
            //更新用户信息
            SysUserEntity accuser = loginService.getLoginUser();
            user.setUpdateBy(accuser.getUsername());

            StringBuffer buffer1 = checkInfo(user);
            if (!StringUtils.isEmpty(buffer1)) {
                return ResultBean.error(buffer1.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
            }
            this.baseMapper.update(user, new UpdateWrapper<MCustomerInfoEntity>()
                    .set("birthday", user.getBirthday())
                    .set("email", user.getEmail())
                    .set("tel", user.getTel())
                    .set("sex", user.getSex())
                    .set("address", user.getAddress())
                    .set("status", user.getStatus())
                    .set("update_time",LocalDateTime.now())
                    .eq("id", user.getId())
            );
        } catch (Exception e) {
            log.error("更新客户异常," + e.getMessage());
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 更新信息
     */
    @Override
    public ResultBean<Object> dealUser(Map<String, Object> map) throws HylException {
        MCustomerInfoEntity user = JSON.parseObject(JSON.toJSONString(map), MCustomerInfoEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            // insert
            return saveUser(map);
        } else {
            // update
            return updateUser(map);
        }
    }


    /**
     * 删除用户信息
     *
     * @param map
     * @return
     */
    @Override
    @Transactional
    public ResultBean delete(Map<String, Object> map) throws HylException {
        if (Objects.isNull(map.get("id"))) {
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        String id = map.get("id").toString();
        MCustomerInfoEntity user = customerInfoMapper.selectById(id);
        if (null == user) {
            return ResultBean.error("当前客户已被删除!请刷新后重试", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            customerInfoMapper.deleteById(id);
        } catch (Exception e) {
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 更改用户状态
     * @param map
     * @return
     */
    @Override
    public ResultBean updateUserStatus(Map<String, Object> map) {
        MCustomerInfoEntity user = JSON.parseObject(JSON.toJSONString(map), MCustomerInfoEntity.class);
        if(CollectionUtils.isEmpty(map) || ObjectUtils.isEmpty(user.getStatus()) || ObjectUtils.isEmpty(user.getId())){
            return ResultBean.error("缺少必填参数","VALID_IS_EMPTY",ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            this.baseMapper.update(user, new UpdateWrapper<MCustomerInfoEntity>()
                    .set("status", user.getStatus())
                    .eq("id", user.getId())
            );
        }catch (Exception e){
            log.error("更新客户状态失败");
            return ResultBean.error("更新客户状态失败", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 校验用户的基本信息
     * @param user
     * @return
     */
    StringBuffer checkInfo( MCustomerInfoEntity user) {
        StringBuffer buffer = new StringBuffer();
        if(null!=user.getBirthday()){
            if(!LocalDateTime.now().isAfter(user.getBirthday())){
                buffer.append("生日时间选择错误,时间应为小于今天日期;");
            }
        }
        if(StringUtils.isNotEmpty(user.getEmail())){
            String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            Pattern p = Pattern.compile(REGEX_EMAIL);
            //正则表达式的匹配器
            Matcher m = p.matcher(user.getEmail());
            //进行正则匹配\
            if(!m.matches()){
                buffer.append("邮箱, 有效字符(不支持中文), 且中间必须有@,后半部分必须有.;");
            }
        }
        if(StringUtils.isNotEmpty(user.getTel())){
            if(user.getTel().length()>20){
                buffer.append("手机号码过长,应输入不大于20长度的数字;");
            }
            if(!StringUtils.isNumeric(user.getTel())){
                buffer.append("手机号码应为数字;");
            }
        }

        return buffer;
    }
}
