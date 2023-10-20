package com.hyl.api.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysPermissionEntity;
import com.hyl.api.system.entity.SysRoleEntity;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.SysUserRoleEntity;
import com.hyl.api.system.mapper.SysRoleMapper;
import com.hyl.api.system.mapper.SysUserMapper;
import com.hyl.api.system.mapper.SysUserRoleMapper;
import com.hyl.api.system.service.ISysUserRoleService;
import com.hyl.api.system.service.ISysUserService;
import com.hyl.api.system.service.SysAdminCacheService;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.util.file.FileUtil;
import com.hyl.api.util.file.MimeTypeUtil;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.GlobalConstant;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.common.utils.AesEncodeUtil;
import com.hyl.core.common.PageUtils;
import com.hyl.core.common.Query;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserEntity> implements ISysUserService {

    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysUserRoleMapper roleMapper;
    @Resource
    private SysAdminCacheService sysAdminCacheService;
    @Resource
    private SysLoginService loginService;
    @Resource
    private ISysUserRoleService userRoleService;
    @Resource
    private SysRoleMapper sysRoleMapper;

    private final static String word = "Anne1234567";

    @Value("${storage.location.avatar}")
    private String avatarPath;
    @Value("${image.upfile.uploadfile}")
    private String uploadPath;

    @Override
    public List<Long> getUserIdListByResourceId(Long resourceId) {
        return userMapper.getUserIdListByResourceId(resourceId);
    }

    @Override
    public SysUserEntity getAdminByUsername(String username) {
        SysUserEntity userEntity = sysAdminCacheService.getUser(username);
        if (userEntity != null) {
            return userEntity;
        }
        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUserEntity::getUsername, username);
        List<SysUserEntity> adminList = list(wrapper);
        if (adminList != null && adminList.size() > 0) {
            userEntity = adminList.get(0);
            sysAdminCacheService.setUser(userEntity);
            return userEntity;
        }
        return null;
    }

    @Override
    public List<SysPermissionEntity> getResourceList(Long userId) {
        List<SysPermissionEntity> resourceList = sysAdminCacheService.getResourceList(userId);
        if (CollUtil.isNotEmpty(resourceList)) {
            return resourceList;
        }
        // resourceList = resourceMapper.getResourceList(adminId);
        if (CollUtil.isNotEmpty(resourceList)) {
            sysAdminCacheService.setResourceList(userId, resourceList);
        }
        return resourceList;
    }

    @Override
    public List<Long> queryAllMenuId(String userId) {
        return this.baseMapper.queryAllMenuId(Long.valueOf(userId));
    }


    /**
     * 分页查询用户信息
     *
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) throws HylException {

        String username = String.valueOf(params.get("username"));
        IPage<SysUserEntity> page = this.page(
                new Query<SysUserEntity>().getPage(params),
                new QueryWrapper<SysUserEntity>()
                        .like(StringUtils.isNotEmpty(username),"username", username)
                        .like(ObjectUtils.isNotEmpty(params.get("phone")),"phone",params.get("phone"))
                        .like(ObjectUtils.isNotEmpty(params.get("email")),"email",params.get("email"))
                        .eq(ObjectUtils.isNotEmpty(params.get("sex")),"sex",params.get("sex"))
                        .eq(ObjectUtils.isNotEmpty(params.get("status")),"status",params.get("status"))
                        .orderByDesc("create_time"));
        //查询出对应的角色信息
        page.getRecords().forEach(user -> {
            SysUserRoleEntity userRoleEntity = userRoleService.queryIdByUserId(user.getId());
            if (null != userRoleEntity) {
                SysRoleEntity sysRoleEntity = sysRoleMapper.selectById(userRoleEntity.getRoleId());
                user.setRole(sysRoleEntity);
            }
        });
        return new PageUtils(page);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds
     */
    @Override
    public void deleteBatch(Long[] userIds) {
        try {
            this.removeByIds(Arrays.asList(userIds));
        } catch (Exception e) {
            log.error("批量删除用户失败" + e.getMessage());
        }
    }

    /**
     * 保存用户信息
     */
    @Override
    @Transactional
    public ResultBean saveUser(Map<String, Object> map) throws HylException {
        SysUserEntity user = JSON.parseObject(JSON.toJSONString(map), SysUserEntity.class);
         if (StringUtils.isEmpty(user.getUsername())) {
            return ResultBean.error("用户名为空", "USERNAME IS EXISTS", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        int count = userMapper.selectByName(user.getUsername());

        if (count>0) {
            return ResultBean.error("用户名已存在", "USERNAME IS EXISTS", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        //增加密码校验；
        StringBuffer buffer = checkUserNameAndPassword(map, user);
        if (!StringUtils.isEmpty(buffer)) {
            return ResultBean.error(buffer.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        StringBuffer buffer1 = checkInfo(user);
        if (!StringUtils.isEmpty(buffer1)) {
            return ResultBean.error(buffer1.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        SysUserEntity accuser = loginService.getLoginUser();
        user.setCreateBy(accuser.getUsername());
        user.setPassword(user.getPassword());
        user.setStatus(true);
        user.setDelFlag(false);
        try {
            this.baseMapper.insert(user);
            SysUserRoleEntity userRoleEntity = new SysUserRoleEntity();
            userRoleEntity.setUserId(user.getId());
            userRoleEntity.setRoleId(map.get("roleId").toString());
            userRoleEntity.setCreateBy(accuser.getUsername());
            userRoleService.save(userRoleEntity);
        } catch (Exception e) {
            log.error("新增用户异常," + e.getMessage());
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
        SysUserEntity user = JSON.parseObject(JSON.toJSONString(map), SysUserEntity.class);
        if (StringUtils.isEmpty(user.getId())) {
            return ResultBean.error(ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }

        try {
            //更新用户信息
            SysUserEntity accuser = loginService.getLoginUser();
            user.setUpdateBy(accuser.getUsername());

            //针对上传头像的接口
            if (map.containsKey("personCenter") && "avatar".equalsIgnoreCase(map.get("personCenter").toString())) {
                this.baseMapper.update(user, new UpdateWrapper<SysUserEntity>()
                        .set("avatar", user.getAvatar())
                        .set("update_time",LocalDateTime.now())
                        .eq("id", user.getId())
                );
            } else if (map.containsKey("personCenter") && "personCenter".equalsIgnoreCase(map.get("personCenter").toString())) {
                StringBuffer buffer1 = checkInfo(user);
                if (!StringUtils.isEmpty(buffer1)) {
                    return ResultBean.error(buffer1.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
                }
                this.baseMapper.update(user, new UpdateWrapper<SysUserEntity>()
                        .set("avatar", user.getAvatar())
                        .set("birthday", user.getBirthday())
                        .set("email", user.getEmail())
                        .set("phone", user.getPhone())
                        .set("sex", user.getSex())
                        .set("status", user.getStatus())
                        .set("update_time",LocalDateTime.now())
                        .eq("id", user.getId())
                );
            }
            //不包含 personCenter 表示需要更新角色ID信息
            if (!map.containsKey("personCenter")) {
                StringBuffer buffer1 = checkInfo(user);
                if (!StringUtils.isEmpty(buffer1)) {
                    return ResultBean.error(buffer1.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
                }
                this.baseMapper.update(user, new UpdateWrapper<SysUserEntity>()
                        .set("avatar", user.getAvatar())
                        .set("birthday", user.getBirthday())
                        .set("email", user.getEmail())
                        .set("phone", user.getPhone())
                        .set("sex", user.getSex())
                        .set("status", user.getStatus())
                        .set("update_time",LocalDateTime.now())
                        .eq("id", user.getId())
                );
                SysUserRoleEntity userRoleEntity = userRoleService.queryIdByUserId(user.getId());
                //更新角色信息表
                userRoleEntity.setRoleId(map.get("roleId").toString());
                userRoleService.updateById(userRoleEntity);
            }

        } catch (Exception e) {
            log.error("更新用户异常," + e.getMessage());
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
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
        //查出该用户是否是管理员角色
        if (GlobalConstant.SUPER_ADMIN.equalsIgnoreCase(id)) {
            return ResultBean.error("系统管理员不能删除,请联系后台管理人员手动删除", "NOT DELETE", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        //当前自己的用户不能杀出
        SysUserEntity accuser = loginService.getLoginUser();
        //即将需要删除的角色
        SysUserEntity user = userMapper.selectById(id);
        if (null == user) {
            return ResultBean.error("当前用户已被删除!请刷新后重试", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        if (user.getUsername().equalsIgnoreCase(accuser.getUsername())) {
            return ResultBean.error("当前自己用户不能删除,请联系后台管理人员手动删除", "DELETE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            //删除自己的信息
            userMapper.deleteById(id);
            //删除所对应的角色信息
            QueryWrapper<SysUserRoleEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", user.getId()); //通过wrapper设置条件
            roleMapper.delete(wrapper);
        } catch (Exception e) {
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 重置密码
     */
    @Override
    public ResultBean resetPassword(Map<String, Object> map) throws HylException {
        if (Objects.isNull(map.get("id")) || StringUtils.isEmpty(map.get("id").toString())) {
            return ResultBean.error(ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }

        SysUserEntity user = this.baseMapper.selectById(map.get("id").toString());
        SysUserEntity accuser = loginService.getLoginUser();
        user.setUpdateBy(accuser.getUsername());
        String s = Base64.getEncoder().encodeToString(AesEncodeUtil.encrypt(word).getBytes(Charset.forName("UTF-8")));
        user.setPassword(s);
        try {
            user.setId(map.get("id").toString());
            //更新用户信息
            this.baseMapper.updateById(user);
        } catch (Exception e) {
            log.error("重置密码异常," + e.getMessage());
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);

    }

    /**
     * 上传头像
     *
     * @param files
     * @return
     */
    @Override
    public ResultBean uploadAvatar(MultipartFile files) {
        String path = this.avatarPath;
        if (null == files || files.isEmpty()) {
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            Map<String, Object> upload = FileUtil.upload(files, path, MimeTypeUtil.IMAGE_EXTENSION_AVATAR);
            String riderPath = (String) upload.get("riderPath");
            upload.put("riderPath", uploadPath + "header" + riderPath);
            return ResultBean.ok(upload, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
        } catch (Exception e) {
            log.error("上传头像出现异常" + e.getMessage());
        }
        return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 用户修改密码
     *
     * @param map
     * @return
     */
    @Override
    public ResultBean updatePassword(Map<String, Object> map) {
        try {
            String userId = (String) map.get("id");
            String oldPassword = (String) map.get("oldPassword");
            String password = (String) map.get("password");
            String passwordRepeat = (String) map.get("passwordRepeat");
            if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(oldPassword) ||
                    StringUtils.isEmpty(password) || StringUtils.isEmpty(passwordRepeat)) {
                return ResultBean.error("数据异常!", "DATA ERROR", ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
            }
            if (!passwordRepeat.equals(password)) {
                return ResultBean.error("两次密码不一致!", "DATA ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
            }
            //开始更新数据
            SysUserEntity userEntity = this.baseMapper.selectById(userId);
            if (!oldPassword.equals(userEntity.getPassword())) {
                return ResultBean.error("旧密码输入错误!", "OLD PASSWORD ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
            }
            byte[] decode = Base64.getDecoder().decode(passwordRepeat);
            passwordRepeat = AesEncodeUtil.decrypt(new String(decode, StandardCharsets.UTF_8));
            String userPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$";
            boolean isMatch = Pattern.matches(userPattern, passwordRepeat);
            if (!isMatch) {
                return ResultBean.error("必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间!", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
            }

            //更新角色信息表
            userEntity.setUpdateBy(loginService.getLoginUser().getUsername());
            userEntity.setPassword(password);
            this.baseMapper.updateById(userEntity);
        } catch (Exception e) {
            log.error("用户更新密码异常" + e.getMessage());
            return ResultBean.error("更新密码异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
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
        SysUserEntity user = JSON.parseObject(JSON.toJSONString(map), SysUserEntity.class);
        if(CollectionUtils.isEmpty(map)||ObjectUtils.isEmpty(user.getStatus())||ObjectUtils.isEmpty(user.getId())){
            return ResultBean.error("缺少必填参数","VALID_IS_EMPTY",ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            this.baseMapper.update(user, new UpdateWrapper<SysUserEntity>()
                    .set("status", user.getStatus())
                    .eq("id", user.getId())
            );
        }catch (Exception e){
            log.error("更新用户状态失败");
            return ResultBean.error("更新用户状态失败", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);

        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 检查其合法性
     *
     * @param map
     * @return
     */
    StringBuffer checkUserNameAndPassword(Map<String, Object> map, SysUserEntity user) {
        StringBuffer buffer = new StringBuffer();
        // SysUserEntity user = JSON.parseObject(JSON.toJSONString(map), SysUserEntity.class);
        //增加密码校验；
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            buffer.append("用户名和密码不能为空;");
            return buffer;
        }
        if (Objects.isNull(map.get("roleId"))) {
            buffer.append("请为该用户添加角色信息;");
            return buffer;
        }
        byte[] decode = Base64.getDecoder().decode(user.getPassword());
        String password = AesEncodeUtil.decrypt(new String(decode, StandardCharsets.UTF_8));
        //强密码(必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间)：^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$
        String userPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$";
        boolean isMatch = Pattern.matches(userPattern, password);
        if (!isMatch) {
            buffer.append("必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间;");
        }
        //^[a-zA-Z][a-zA-Z0-9_]{4,15}$
        boolean isUserNameMatch = Pattern.matches("^[a-zA-Z][a-zA-Z0-9_]{4,15}$", user.getUsername());
        if (!isUserNameMatch) {
            buffer.append("请输入合法账号,字母开头，允许5-16字节，允许字母数字下划线;");
            return buffer;
        }
        return buffer;
    }

    /**
     * 校验用户的基本信息
     * @param user
     * @return
     */
    StringBuffer checkInfo( SysUserEntity user) {
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
        if(StringUtils.isNotEmpty(user.getPhone())){
                if(user.getPhone().length()>20){
                    buffer.append("手机号码过长,应输入不大于20长度的数字;");
                }
                if(!StringUtils.isNumeric(user.getPhone())){
                    buffer.append("手机号码应为数字;");
                }
        }

        return buffer;
    }

}
