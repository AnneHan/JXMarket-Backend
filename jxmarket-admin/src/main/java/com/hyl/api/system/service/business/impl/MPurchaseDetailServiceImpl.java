package com.hyl.api.system.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.business.MGoodInfoEntity;
import com.hyl.api.system.entity.business.MGoodQuantityEntity;
import com.hyl.api.system.entity.business.MPurchaseDetailEntity;
import com.hyl.api.system.mapper.business.MPurchaseDetailMapper;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.service.business.IMGoodInfoService;
import com.hyl.api.system.service.business.IMGoodQuantityService;
import com.hyl.api.system.service.business.IMPurchaseDetailService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import com.hyl.core.common.Query;
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
 * 采购明细表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-20
 */
@Service
public class MPurchaseDetailServiceImpl extends ServiceImpl<MPurchaseDetailMapper, MPurchaseDetailEntity> implements IMPurchaseDetailService {
    private static final Logger logger = LoggerFactory.getLogger(MPurchaseDetailServiceImpl.class);

    @Resource
    private SysLoginService loginService;
    @Resource
    private IMGoodInfoService goodInfoService;
    @Resource
    private IMGoodQuantityService goodQuantityService;
    @Resource
    private MPurchaseDetailMapper purchaseDetailMapper;

    /**
     * 分页查询
     * @param params
     * @return
     * @throws HylException
     */
    @Override
    public PageUtils queryPurchaseDetPage(Map<String, Object> params) throws HylException {
        String id = String.valueOf(params.get("id"));
        String purchaseId = String.valueOf(params.get("purchaseId"));
        String goodId = String.valueOf(params.get("goodId"));
        IPage<MPurchaseDetailEntity> page = this.page(
                new Query<MPurchaseDetailEntity>().getPage(params),
                new QueryWrapper<MPurchaseDetailEntity>()
                        .like(StringUtils.isNotEmpty(id),"id", id)
                        .like(StringUtils.isNotEmpty(purchaseId),"purchase_id", purchaseId)
                        .like(StringUtils.isNotEmpty(goodId),"good_id", goodId)
                        .orderByDesc("create_time"));
        //查询出对应的商品信息
        page.getRecords().forEach(purchase -> {
            MGoodInfoEntity goodInfoEntity = goodInfoService.queryIdByGoodId(purchase.getGoodId());
            if (null != goodInfoEntity) {
                purchase.setGoodName(goodInfoEntity.getName());
            }
        });
        return new PageUtils(page);
    }


    /**
     * 更新信息
     */
    @Override
    @Transactional
    public ResultBean<Object> dealPurchaseDet(Map<String, Object> map) throws HylException {
        SysUserEntity accuser = loginService.getLoginUser();

        MPurchaseDetailEntity user = JSON.parseObject(JSON.toJSONString(map), MPurchaseDetailEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            // insert
            user.setCreateBy(accuser.getUsername());
            try {
                this.baseMapper.insert(user);

                // 新增采购明细时，更新商品库存
                MGoodQuantityEntity goodQuantityEntity = goodQuantityService.queryIdByGoodId(user.getGoodId());
                if (null == goodQuantityEntity) {
                    goodQuantityEntity = new MGoodQuantityEntity();
                    goodQuantityEntity.setId(user.getGoodId());
                    goodQuantityEntity.setQuantity(user.getQuantity());
                    goodQuantityEntity.setCreateBy(accuser.getUsername());
                    goodQuantityService.save(goodQuantityEntity);
                } else {
                    goodQuantityEntity.setQuantity(goodQuantityEntity.getQuantity() + user.getQuantity());
                    goodQuantityEntity.setUpdateBy(accuser.getUsername());
                    goodQuantityService.updateById(goodQuantityEntity);
                }
            } catch (Exception e) {
                logger.error("新增采购单明细异常," + e.getMessage());
                throw  e;
            }
        } else {
            // update
            try {
                user.setUpdateBy(accuser.getUsername());
                this.baseMapper.update(user, new UpdateWrapper<MPurchaseDetailEntity>()
                        .set("quantity", user.getQuantity())
                        .set("unit_price", user.getUnitPrice())
                        .set("update_time", LocalDateTime.now())
                        .eq("id", user.getId())
                );
            } catch (Exception e) {
                log.error("更新采购单明细异常," + e.getMessage());
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
    public ResultBean deletePurchaseDet(Map<String, Object> map) throws HylException {
        if (Objects.isNull(map.get("id"))) {
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }

        SysUserEntity accuser = loginService.getLoginUser();

        String id = map.get("id").toString();
        MPurchaseDetailEntity user = purchaseDetailMapper.selectById(id);
        if (null == user) {
            return ResultBean.error("当前采购单明细已被删除!请刷新后重试", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            purchaseDetailMapper.deleteById(id);

            //删除采购单明细，要把库存量减掉
            MGoodQuantityEntity goodQuantityEntity = goodQuantityService.queryIdByGoodId(user.getGoodId());
            if (null != goodQuantityEntity) {
                goodQuantityEntity.setQuantity(goodQuantityEntity.getQuantity() - user.getQuantity());
                goodQuantityEntity.setUpdateBy(accuser.getUsername());
                goodQuantityService.updateById(goodQuantityEntity);
            }
        } catch (Exception e) {
            log.error("删除采购单明细异常," + e.getMessage());
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    @Override
    public MPurchaseDetailEntity queryIdByPurchaseId(String id) {
        return this.baseMapper.selectOne(new QueryWrapper<MPurchaseDetailEntity>().eq("purchase_id", id));
    }
    @Override
    public MPurchaseDetailEntity queryIdByPurchaseDetId(String id) {
        return this.baseMapper.selectOne(new QueryWrapper<MPurchaseDetailEntity>().eq("id", id));
    }
}
