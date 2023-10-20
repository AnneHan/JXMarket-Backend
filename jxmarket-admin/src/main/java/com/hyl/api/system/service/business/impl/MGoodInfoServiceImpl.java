package com.hyl.api.system.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.business.MGoodInfoEntity;
import com.hyl.api.system.entity.business.MSupplierInfoEntity;
import com.hyl.api.system.mapper.business.MGoodInfoMapper;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.service.business.IMGoodInfoService;
import com.hyl.api.system.service.business.IMGoodQuantityService;
import com.hyl.api.system.service.business.IMSupplierInfoService;
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

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 商品信息表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
@Service
public class MGoodInfoServiceImpl extends ServiceImpl<MGoodInfoMapper, MGoodInfoEntity> implements IMGoodInfoService {
    private static final Logger logger = LoggerFactory.getLogger(MGoodInfoServiceImpl.class);

    @Resource
    private SysLoginService loginService;
    @Resource
    private IMSupplierInfoService supplierInfoService;
    @Resource
    private IMGoodQuantityService goodQuantityService;
    @Resource
    private MGoodInfoMapper goodInfoMapper;

    /**
     * 分页查询
     * @param params
     * @return
     * @throws HylException
     */
    @Override
    public PageUtils queryGoodPage(Map<String, Object> params) throws HylException {
        String name = String.valueOf(params.get("name"));
        IPage<MGoodInfoEntity> page = this.page(
                new Query<MGoodInfoEntity>().getPage(params),
                new QueryWrapper<MGoodInfoEntity>()
                        .like(StringUtils.isNotEmpty(name),"name", name)
                        .like(ObjectUtils.isNotEmpty(params.get("category")),"category",params.get("category"))
                        .orderByDesc("create_time"));
        //查询出对应的供应商信息
        page.getRecords().forEach(good -> {
            MSupplierInfoEntity supplierInfoEntity = supplierInfoService.queryIdBySupplierId(good.getSupplierId());
            if (null != supplierInfoEntity) {
                good.setSupplierName(supplierInfoEntity.getName());
            }
        });
        return new PageUtils(page);
    }


    /**
     * 更新信息
     */
    @Override
    @Transactional
    public ResultBean<Object> dealGood(Map<String, Object> map) throws HylException {
        SysUserEntity accuser = loginService.getLoginUser();

        MGoodInfoEntity user = JSON.parseObject(JSON.toJSONString(map), MGoodInfoEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            // insert
            if (StringUtils.isEmpty(user.getName())) {
                log.error("商品名为空");
            }

            // 商品信息
            user.setCreateBy(accuser.getUsername());
            user.setStatus(true);
            user.setDelFlag(false);
            try {
                this.baseMapper.insert(user);

                // 商品库存
                /*MGoodQuantityEntity goodQuantityEntity = new MGoodQuantityEntity();
                goodQuantityEntity.setId(user.getId());
                goodQuantityEntity.setQuantity(Integer.valueOf(map.get("quantity").toString()));
                goodQuantityEntity.setCreateBy(accuser.getUsername());
                goodQuantityService.save(goodQuantityEntity);*/
            } catch (Exception e) {
                logger.error("新增商品异常," + e.getMessage());
                throw  e;
            }
        } else {
            // update
            try {
                user.setUpdateBy(accuser.getUsername());
                this.baseMapper.update(user, new UpdateWrapper<MGoodInfoEntity>()
                        .set("name", user.getName())
                        .set("category", user.getCategory())
                        .set("model", user.getModel())
                        .set("unit", user.getUnit())
                        .set("costPrice", user.getCostPrice())
                        .set("retailPrice", user.getRetailPrice())
                        .set("expiryDate", user.getExpiryDate())
                        .set("barCode", user.getBarCode())
                        .set("supplierId", user.getSupplierId())
                        .set("warnValue", user.getWarnValue())
                        .set("shelfNumber", user.getShelfNumber())
                        .set("innerWarnValue", user.getInnerWarnValue())
                        .set("notes", user.getNotes())
                        .set("status", user.getStatus())
                        .set("update_time", LocalDateTime.now())
                        .eq("id", user.getId())
                );
            } catch (Exception e) {
                log.error("更新商品异常," + e.getMessage());
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
    public ResultBean deleteGood(Map<String, Object> map) throws HylException {
        if (Objects.isNull(map.get("id"))) {
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        String id = map.get("id").toString();
        MGoodInfoEntity user = goodInfoMapper.selectById(id);
        if (null == user) {
            return ResultBean.error("当前商品已被删除!请刷新后重试", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            goodInfoMapper.deleteById(id);
        } catch (Exception e) {
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    @Override
    public MGoodInfoEntity queryIdByGoodId(String id) {
        return this.baseMapper.selectOne(new QueryWrapper<MGoodInfoEntity>().eq("id", id));
    }
}
