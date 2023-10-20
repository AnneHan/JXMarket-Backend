package com.hyl.api.system.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.business.MGoodInfoEntity;
import com.hyl.api.system.entity.business.MGoodQuantityEntity;
import com.hyl.api.system.mapper.business.MGoodQuantityMapper;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.service.business.IMGoodInfoService;
import com.hyl.api.system.service.business.IMGoodQuantityService;
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

/**
 * <p>
 * 商品库存表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
@Service
public class MGoodQuantityServiceImpl extends ServiceImpl<MGoodQuantityMapper, MGoodQuantityEntity> implements IMGoodQuantityService {
    private static final Logger logger = LoggerFactory.getLogger(MGoodQuantityServiceImpl.class);

    @Resource
    private SysLoginService loginService;
    @Resource
    private IMGoodInfoService goodInfoService;
    @Resource
    private MGoodQuantityMapper goodQuantityMapper;

    /**
     * 分页查询
     * @param params
     * @return
     * @throws HylException
     */
    @Override
    public PageUtils queryGoodNumPage(Map<String, Object> params) throws HylException {
        String id = String.valueOf(params.get("id"));
        IPage<MGoodQuantityEntity> page = this.page(
                new Query<MGoodQuantityEntity>().getPage(params),
                new QueryWrapper<MGoodQuantityEntity>()
                        .like(StringUtils.isNotEmpty(id),"id", id)
                        .orderByDesc("create_time"));
        //查询出对应的商品信息
        page.getRecords().forEach(good -> {
            MGoodInfoEntity goodInfoEntity = goodInfoService.queryIdByGoodId(good.getId());
            if (null != goodInfoEntity) {
                good.setGoodId(goodInfoEntity.getId());
                good.setGoodName(goodInfoEntity.getName());
            }
        });
        return new PageUtils(page);
    }


    /**
     * 更新信息
     */
    @Override
    @Transactional
    public ResultBean<Object> dealGoodNum(Map<String, Object> map) throws HylException {
        SysUserEntity accuser = loginService.getLoginUser();

        MGoodQuantityEntity user = JSON.parseObject(JSON.toJSONString(map), MGoodQuantityEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            // insert
            user.setCreateBy(accuser.getUsername());
            // 和商品编号保持一致
            user.setId(user.getGoodId());
            try {
                this.baseMapper.insert(user);
            } catch (Exception e) {
                log.error("新增商品存量异常," + e.getMessage());
                throw  e;
            }
        } else {
            // update
            try {
                user.setUpdateBy(accuser.getUsername());
                this.baseMapper.update(user, new UpdateWrapper<MGoodQuantityEntity>()
                        .set("quantity", user.getQuantity())
                        .set("innerQuantity", user.getInnerQuantity())
                        .set("update_time", LocalDateTime.now())
                        .eq("id", user.getId())
                );
            } catch (Exception e) {
                log.error("更新商品存量异常," + e.getMessage());
                throw e;
            }
        }

        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

}
