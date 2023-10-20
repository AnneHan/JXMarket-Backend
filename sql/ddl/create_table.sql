create schema jx_market;

create table sys_config
(
    id           bigint auto_increment comment '主键id'
        primary key,
    config_key   varchar(32)  null comment '配置key',
    config_value varchar(255) null comment '配置value',
    remark_en    varchar(255) null comment '中文备注',
    remark_zh    varchar(255) null comment '英文备注',
    create_time  datetime     null comment '创建时间',
    update_time  datetime     null comment '更新时间',
    create_by    varchar(32)  null comment '创建人',
    update_by    varchar(32)  null comment '更新人'
);

create table sys_log
(
    id            bigint auto_increment
        primary key,
    log_type      int           null comment '日志类型（1登录日志，0操作日志）',
    log_content   varchar(1000) null comment '日志内容',
    operate_type  int           null comment '操作类型',
    userid        varchar(32)   null comment '操作用户账号',
    username      varchar(100)  null comment '操作用户名称',
    ip            varchar(100)  null comment 'IP',
    method        varchar(500)  null comment '请求java方法',
    request_url   varchar(255)  null comment '请求路径',
    request_param longtext      null comment '请求参数',
    request_type  varchar(10)   null comment '请求类型',
    cost_time     bigint        null comment '耗时',
    create_by     varchar(32)   null comment '创建人',
    create_time   datetime      null comment '创建时间',
    update_by     varchar(32)   null comment '更新人',
    update_time   datetime      null comment '更新时间'
)
    comment '系统日志表';

create index idx_sl_create_time
    on sys_log (create_time);

create index idx_sl_log_type
    on sys_log (log_type);

create index idx_sl_operate_type
    on sys_log (operate_type);

create index idx_sl_userid
    on sys_log (userid);

create table sys_user
(
    id                   bigint auto_increment comment '主键id'
        primary key,
    username             varchar(100) not null comment '登录账号',
    password             varchar(255) null comment '密码',
    avatar               varchar(255) null comment '头像',
    birthday             date         null comment '生日',
    sex                  tinyint(1)   null comment '性别(1-男,0-女)',
    email                varchar(45)  null comment '电子邮件',
    phone                varchar(45)  null comment '电话',
    docking_code         varchar(50)  null comment 'HF_04XPKVTXCSDV41YWKXF2R7UQYUNX0IQN',
    status               tinyint(1)   null comment '状态(1-正常,0-冻结)',
    del_flag             tinyint(1)   null comment '删除状态(0-正常,1-已删除)',
    business_type        varchar(10)  null comment '',
    personal_description varchar(255) null comment '个人描述',
    create_by            varchar(32)  null comment '创建人',
    create_time          datetime     null comment '创建时间',
    update_by            varchar(32)  null comment '更新人',
    update_time          datetime     null comment '更新时间',
    constraint uniq_sys_user_username
        unique (username)
)
    comment '系统用户表';

create index idx_su_del_flag
    on sys_user (del_flag);

create index idx_su_status
    on sys_user (status);

create index idx_su_username
    on sys_user (username);

create index index_user_del_flag
    on sys_user (del_flag);

create index index_user_status
    on sys_user (status);

create table sys_role
(
    id          bigint auto_increment comment '主键id'
        primary key,
    role_name   varchar(200) null comment '角色名称',
    role_code   varchar(100) not null comment '角色编码',
    description varchar(255) null comment '描述',
    create_by   varchar(32)  null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   varchar(32)  null comment '更新人',
    update_time datetime     null comment '更新时间',
    constraint uniq_sys_role_role_code
        unique (role_code)
)
    comment '角色表';

create index idx_sr_role_code
    on sys_role (role_code);

create table sys_permission
(
    id                   bigint auto_increment comment '主键id'
        primary key,
    parent_id            varchar(32)               null comment '父id',
    name                 varchar(100)              null comment '菜单标题',
    url                  varchar(255)              null comment '路径',
    component            varchar(255)              null comment '组件',
    component_name       varchar(100)              null comment '组件名字',
    redirect             varchar(255)              null comment '一级菜单跳转地址',
    menu_type            int                       null comment '菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)',
    perms                varchar(255)              null comment '菜单权限编码',
    perms_type           varchar(10)  default '0'  null comment '权限策略1显示2禁用',
    sort_no              double(8, 2)              null comment '菜单排序',
    always_show          tinyint(1)                null comment '聚合子路由: 1是0否',
    icon                 varchar(100)              null comment '菜单图标',
    is_route             tinyint(1)   default 1    null comment '是否路由菜单: 0:不是  1:是（默认值1）',
    is_leaf              tinyint(1)                null comment '是否叶子节点:    1:是   0:不是',
    keep_alive           tinyint(1)                null comment '是否缓存该页面:    1:是   0:不是',
    hidden               int          default 0    null comment '是否隐藏路由: 0否,1是',
    hide_tab             int                       null comment '是否隐藏tab: 0否,1是',
    description          varchar(255)              null comment '描述',
    create_by            varchar(32)               null comment '创建人',
    create_time          datetime                  null comment '创建时间',
    update_by            varchar(32)               null comment '更新人',
    update_time          datetime                  null comment '更新时间',
    logic_flag           int          default 0    null comment '删除状态 0正常 1已删除',
    status               varchar(2)   default '1'  null comment '按钮权限状态(0无效1有效)',
    internal_or_external tinyint(1)                null comment '外链菜单打开方式 0/内部打开 1/外部打开',
    height               varchar(255) default '40' null,
    active               tinyint(1)   default 0    null
)
    comment '菜单权限表';

create table sys_user_role
(
    id          bigint auto_increment comment '主键id'
        primary key,
    user_id     varchar(32) null comment '用户id',
    role_id     varchar(32) null comment '角色id',
    create_by   varchar(32) null comment '创建人',
    create_time datetime    null comment '创建时间',
    update_by   varchar(32) null comment '更新人',
    update_time datetime    null comment '更新时间'
)
    comment '用户角色表';

create index idx_sur_role_id
    on sys_user_role (role_id);

create index idx_sur_user_id
    on sys_user_role (user_id);

create index idx_sur_user_role_id
    on sys_user_role (user_id, role_id);

create table sys_role_permission
(
    id            bigint auto_increment
        primary key,
    role_id       varchar(32) null comment '角色id',
    permission_id varchar(32) null comment '权限id',
    create_by     varchar(32) null comment '创建人',
    create_time   datetime    null comment '创建时间',
    update_by     varchar(32) null comment '更新人',
    update_time   datetime    null comment '更新时间'
)
    comment '角色权限表';

create index idx_srp_permission_id
    on sys_role_permission (permission_id);

create index idx_srp_role_id
    on sys_role_permission (role_id);

create index idx_srp_role_per_id
    on sys_role_permission (role_id, permission_id);

create table m_customer_info
(
    id          bigint auto_increment comment '客户编号'
        primary key,
    username    varchar(100) not null comment '客户名称',
    sex         tinyint(1)   null comment '性别(1-男,0-女)',
    birthday    date         null comment '生日',
    email       varchar(45)  null comment '电子邮件',
    tel         varchar(45)  null comment '电话',
    address     varchar(500) null comment '地址',
    membership  varchar(45)  null comment '会员卡号',
    status      tinyint(1)   null comment '状态(1-正常,0-冻结)',
    del_flag    tinyint(1)   null comment '删除状态(0-正常,1-已删除)',
    create_by   varchar(32)  null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   varchar(32)  null comment '更新人',
    update_time datetime     null comment '更新时间'
)
    comment '客户信息表';
