package com.hyl.api.system.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.business.MSupplierInfoEntity;
import com.hyl.api.system.mapper.business.MSupplierInfoMapper;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.service.business.IMSupplierInfoService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import com.hyl.core.common.Query;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 供应商信息表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
@Service
public class MSupplierInfoServiceImpl extends ServiceImpl<MSupplierInfoMapper, MSupplierInfoEntity> implements IMSupplierInfoService {

    @Resource
    private SysLoginService loginService;
    @Resource
    private MSupplierInfoMapper supplierInfoMapper;

    /**
     * 分页查询
     * @param params
     * @return
     * @throws HylException
     */
    @Override
    public PageUtils querySupplierPage(Map<String, Object> params) throws HylException {
        String name = String.valueOf(params.get("name"));
        IPage<MSupplierInfoEntity> page = this.page(
                new Query<MSupplierInfoEntity>().getPage(params),
                new QueryWrapper<MSupplierInfoEntity>()
                        .like(StringUtils.isNotEmpty(name),"name", name)
                        .like(ObjectUtils.isNotEmpty(params.get("contactPer")),"contact_per",params.get("contactPer"))
                        .orderByDesc("create_time"));
        return new PageUtils(page);
    }


    /**
     * 更新信息
     */
    @Override
    @Transactional
    public ResultBean<Object> dealSupplier(Map<String, Object> map) throws HylException {
        SysUserEntity accuser = loginService.getLoginUser();

        MSupplierInfoEntity user = JSON.parseObject(JSON.toJSONString(map), MSupplierInfoEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            // insert
            if (StringUtils.isEmpty(user.getName())) {
                log.error("供应商名为空");
                //return ResultBean.error("供应商名为空", "USERNAME IS EXISTS", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
            }

            user.setCreateBy(accuser.getUsername());
            user.setStatus(true);
            user.setDelFlag(false);
            try {
                this.baseMapper.insert(user);
            } catch (Exception e) {
                log.error("新增供应商异常," + e.getMessage());
                throw  e;
            }
        } else {
            // update
            try {
                user.setUpdateBy(accuser.getUsername());
                this.baseMapper.update(user, new UpdateWrapper<MSupplierInfoEntity>()
                        .set("name", user.getName())
                        .set("email", user.getEmail())
                        .set("tel", user.getTel())
                        .set("contact_per", user.getContactPer())
                        .set("address", user.getAddress())
                        .set("fax", user.getFax())
                        .set("status", user.getStatus())
                        .set("update_time", LocalDateTime.now())
                        .eq("id", user.getId())
                );
            } catch (Exception e) {
                log.error("更新供应商异常," + e.getMessage());
                throw e;
            }
        }

        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 删除信息
     * @param map
     * @return
     * @throws HylException
     */
    @Override
    @Transactional
    public ResultBean deleteSupplier(Map<String, Object> map) throws HylException {
        if (Objects.isNull(map.get("id"))) {
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        String id = map.get("id").toString();
        MSupplierInfoEntity user = supplierInfoMapper.selectById(id);
        if (null == user) {
            return ResultBean.error("当前供应商已被删除!请刷新后重试", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            supplierInfoMapper.deleteById(id);
        } catch (Exception e) {
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    @Override
    public MSupplierInfoEntity queryIdBySupplierId(String id) {
        return this.baseMapper.selectOne(new QueryWrapper<MSupplierInfoEntity>().eq("id", id));
    }
}
