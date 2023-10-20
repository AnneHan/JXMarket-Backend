package com.hyl.api.system.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.business.MPurchaseDetailEntity;
import com.hyl.api.system.entity.business.MPurchaseInfoEntity;
import com.hyl.api.system.entity.business.MSupplierInfoEntity;
import com.hyl.api.system.mapper.business.MPurchaseInfoMapper;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.service.business.IMPurchaseDetailService;
import com.hyl.api.system.service.business.IMPurchaseInfoService;
import com.hyl.api.system.service.business.IMSupplierInfoService;
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
 * 采购信息表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-20
 */
@Service
public class MPurchaseInfoServiceImpl extends ServiceImpl<MPurchaseInfoMapper, MPurchaseInfoEntity> implements IMPurchaseInfoService {
    private static final Logger logger = LoggerFactory.getLogger(MPurchaseInfoServiceImpl.class);

    @Resource
    private SysLoginService loginService;
    @Resource
    private IMSupplierInfoService supplierInfoService;
    @Resource
    private IMPurchaseDetailService purchaseDetailService;
    @Resource
    private MPurchaseInfoMapper purchaseInfoMapper;

    /**
     * 分页查询
     * @param params
     * @return
     * @throws HylException
     */
    @Override
    public PageUtils queryPurchasePage(Map<String, Object> params) throws HylException {
        String name = String.valueOf(params.get("id"));
        IPage<MPurchaseInfoEntity> page = this.page(
                new Query<MPurchaseInfoEntity>().getPage(params),
                new QueryWrapper<MPurchaseInfoEntity>()
                        .like(StringUtils.isNotEmpty(name),"id", name)
                        .orderByDesc("create_time"));
        //查询出对应的供应商信息
        page.getRecords().forEach(purchase -> {
            MSupplierInfoEntity supplierInfoEntity = supplierInfoService.queryIdBySupplierId(purchase.getSupplierId());
            if (null != supplierInfoEntity) {
                purchase.setSupplierName(supplierInfoEntity.getName());
            }
        });
        return new PageUtils(page);
    }


    /**
     * 更新信息
     */
    @Override
    @Transactional
    public ResultBean<Object> dealPurchase(Map<String, Object> map) throws HylException {
        SysUserEntity accuser = loginService.getLoginUser();

        MPurchaseInfoEntity user = JSON.parseObject(JSON.toJSONString(map), MPurchaseInfoEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            // insert
            user.setCreateBy(accuser.getUsername());
            user.setDeportId("仓库1");
            user.setStatus(true);
            user.setDelFlag(false);
            try {
                this.baseMapper.insert(user);
            } catch (Exception e) {
                logger.error("新增采购单异常," + e.getMessage());
                throw  e;
            }
        } else {
            // update
            try {
                user.setUpdateBy(accuser.getUsername());
                this.baseMapper.update(user, new UpdateWrapper<MPurchaseInfoEntity>()
                        .set("purchase_date", user.getPurchaseDate())
                        .set("amount", user.getAmount())
                        .set("pay_method", user.getPayMethod())
                        .set("pay_status", user.getPayStatus())
                        .set("notes", user.getNotes())
                        .set("update_time", LocalDateTime.now())
                        .eq("id", user.getId())
                );
            } catch (Exception e) {
                log.error("更新采购单异常," + e.getMessage());
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
    public ResultBean deletePurchase(Map<String, Object> map) throws HylException {
        if (Objects.isNull(map.get("id"))) {
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        String id = map.get("id").toString();
        MPurchaseInfoEntity user = purchaseInfoMapper.selectById(id);
        if (null == user) {
            return ResultBean.error("当前采购单已被删除!请刷新后重试", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        // 判断该采购单下面有没有明细
        MPurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.queryIdByPurchaseId(id);
        if (null != purchaseDetailEntity) {
            return ResultBean.error("当前采购单已有明细数据，请先到【采购明细】菜单中进行处理！", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            purchaseInfoMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除采购单异常," + e.getMessage());
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    @Override
    public MPurchaseInfoEntity queryIdByPurchaseId(String id) {
        return this.baseMapper.selectOne(new QueryWrapper<MPurchaseInfoEntity>().eq("id", id));
    }
}
