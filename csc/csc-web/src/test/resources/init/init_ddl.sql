/*
 * 初始化本地数据库
 * - 本地安装mysql，推荐参考wiki：http://wiki.test2pay.com/pages/viewpage.action?pageId=1770003
 * - 以下sql需使用mysql的root用户执行
 */
DROP DATABASE IF EXISTS csc;
CREATE DATABASE `csc` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE USER IF NOT EXISTS 'cscuser'@'localhost' IDENTIFIED WITH mysql_native_password BY 'csc_B0eWUg3H5vhw4I';
GRANT select,update,delete,insert on csc.* to cscuser@localhost;

CREATE USER IF NOT EXISTS 'reader'@'localhost' IDENTIFIED WITH mysql_native_password BY 'reader_j0kqgaHxsI5O6j';
GRANT select on csc.* to reader@localhost;


USE csc;


create table leaf_alloc (
  biz_tag varchar(128) not null default '' comment 'sequence名称',
  max_id bigint(20) not null default '1' comment '当前序列最大值',
  step int(11) not null comment '缓存的个数，一般配置表为10，订单表为200以上，看订单量',
  description varchar(256) default null comment '描述',
  update_time timestamp not null default current_timestamp comment '更新时间',
  PRIMARY KEY (`biz_tag`)
) engine=InnoDB default charset=utf8;


-- csc.t_compare_define definition

CREATE TABLE `t_compare_define` (
  `define_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '定义ID',
  `define_name` varchar(128) NOT NULL COMMENT '定义名称',
  `src_datasource_code` varchar(32) NOT NULL COMMENT '源数据源代码',
  `src_sql_template` text NOT NULL COMMENT '源数据查询模版',
  `src_split_minutes` int(11) NOT NULL COMMENT '源数据切分时长',
  `src_relate_field` varchar(32) NOT NULL COMMENT '源数据关联字段',
  `target_datasource_code` varchar(32) NOT NULL COMMENT '目标数据源代码',
  `target_sql_template` text NOT NULL COMMENT '目标数据查询模版',
  `target_relate_field` varchar(32) NOT NULL COMMENT '目标数据关联字段',
  `target_sharding_expression` varchar(128) DEFAULT NULL COMMENT '目标数据分片表达式',
  `check_expression` varchar(1000) NOT NULL COMMENT '校验表达式',
  `compensate_expression` varchar(1000) DEFAULT NULL COMMENT '补单表达式',
  `compensate_config` varchar(128) DEFAULT NULL COMMENT '补单配置',
  `enable_flag` char(1) NOT NULL COMMENT '启用标志：Y=启用，N=停用',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`define_id`)
) ENGINE=InnoDB AUTO_INCREMENT=319 DEFAULT CHARSET=utf8 COMMENT='对账定义';


-- csc.t_compare_detail definition

CREATE TABLE `t_compare_detail` (
  `detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `relate_identity` varchar(128) NOT NULL COMMENT '关联标志',
  `src_data` varchar(1000) DEFAULT NULL COMMENT '源数据',
  `target_data` varchar(1000) DEFAULT NULL COMMENT '目标数据',
  `compare_status` char(1) NOT NULL COMMENT '比对状态：L=少数据，M=不一致',
  `compensate_flag` char(1) DEFAULT NULL COMMENT '补单标志：W=等待补单，S=补单成功，F=补单失败，E=补单异常',
  `error_message` varchar(128) DEFAULT NULL COMMENT '错误信息',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`detail_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_relate_identity` (`relate_identity`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8 COMMENT='对账明细';


-- csc.t_compare_schedule definition

CREATE TABLE `t_compare_schedule` (
  `schedule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `define_id` bigint(20) NOT NULL COMMENT '定义ID',
  `check_minutes` int(11) NOT NULL COMMENT '对账时长',
  `delay_minutes` int(11) NOT NULL COMMENT '延迟时长',
  `checked_time` timestamp NOT NULL COMMENT '当前进度',
  `next_trigger_time` timestamp NOT NULL COMMENT '下次允许触发时间',
  `current_task_id` bigint(20) DEFAULT NULL COMMENT '当前对账任务',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `error_count` int(11) NOT NULL COMMENT '异常次数',
  `schedule_status` char(1) NOT NULL COMMENT '计划状态：Y=启用，N=停用，E=异常停用',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`schedule_id`),
  KEY `idx_define_id` (`define_id`),
  KEY `idx_next_trigger_time` (`next_trigger_time`)
) ENGINE=InnoDB AUTO_INCREMENT=248 DEFAULT CHARSET=utf8 COMMENT='对账计划';


-- csc.t_compare_task definition

CREATE TABLE `t_compare_task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `define_id` bigint(20) NOT NULL COMMENT '定义ID',
  `task_type` char(1) NOT NULL COMMENT '任务类型：M=人工，S=计划，R=重试',
  `operator` varchar(64) NOT NULL COMMENT '操作员',
  `orig_task_id` bigint(20) DEFAULT NULL COMMENT '原任务ID',
  `data_begin_time` timestamp NOT NULL COMMENT '数据开始时间',
  `data_end_time` timestamp NOT NULL COMMENT '数据结束时间',
  `task_status` varchar(10) NOT NULL COMMENT '任务状态：P=处理中，S=一致，F=不一致，CP=补单中，RW=等待重试，RS=重试成功',
  `compare_statistic` varchar(128) DEFAULT NULL COMMENT '对账统计',
  `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
  `error_message` varchar(255) DEFAULT NULL COMMENT '错误信息',
  `last_retry_task_id` bigint(20) DEFAULT NULL COMMENT '最后重试任务ID',
  `compare_consume` int(11) DEFAULT NULL COMMENT '对账耗时：单位：秒',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_update_time` (`update_time`),
  KEY `idx_orig_task_id` (`orig_task_id`),
  KEY `idx_createtime_taskstatus` (`create_time`,`task_status`)
) ENGINE=InnoDB AUTO_INCREMENT=268 DEFAULT CHARSET=utf8 COMMENT='对账任务';


-- csc.t_compensation_event definition

CREATE TABLE `t_compensation_event` (
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '事件id',
  `event_key` varchar(64) NOT NULL COMMENT '事件主体标志',
  `main_type` varchar(32) NOT NULL COMMENT '事件主类型',
  `sub_type` varchar(32) NOT NULL COMMENT '事件子类型',
  `execute_status` char(1) NOT NULL COMMENT '执行状态：I=初始，F=失败，S=成功，E=异常',
  `execute_count` int(11) NOT NULL COMMENT '执行次数',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展信息',
  `error_message` varchar(128) DEFAULT NULL COMMENT '错误信息',
  `allow_time` timestamp NULL DEFAULT NULL COMMENT '允许执行时间',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`event_id`),
  UNIQUE KEY `uk_event_key_type_subtype` (`event_key`,`main_type`,`sub_type`),
  KEY `idx_allow_time` (`allow_time`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8 COMMENT='补偿事件';


-- csc.t_monitor_define definition

CREATE TABLE `t_monitor_define` (
  `define_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '定义ID',
  `application_name` varchar(128) NOT NULL COMMENT '应用名称',
  `define_name` varchar(128) NOT NULL COMMENT '定义名称',
  `datasource_type` varchar(10) NOT NULL COMMENT '数据源类型：MYSQL，ES',
  `datasource_code` varchar(128) NOT NULL COMMENT '数据源代码',
  `query_template` text NOT NULL COMMENT '查询模版',
  `monitor_type` char(1) NOT NULL COMMENT '监控类型：R=报表，A=报警',
  `priority` char(1) NOT NULL COMMENT '优先级：H=高，M=中，L=低',
  `split_strategy` varchar(10) NOT NULL COMMENT '切分策略：NO=不切分，UNION=拼接，SUM=汇总',
  `split_minutes` int(11) DEFAULT NULL COMMENT '数据切分时长',
  `key_field` varchar(128) DEFAULT NULL COMMENT '关键字段',
  `notify_expression` varchar(1000) DEFAULT NULL COMMENT '通知表达式',
  `enable_flag` char(1) NOT NULL COMMENT '启用标识：Y=启用，N=停用',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`define_id`)
) ENGINE=InnoDB AUTO_INCREMENT=882 DEFAULT CHARSET=utf8 COMMENT='监控定义';


-- csc.t_monitor_schedule definition

CREATE TABLE `t_monitor_schedule` (
  `schedule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `define_id` bigint(20) NOT NULL COMMENT '定义ID',
  `check_minutes` int(11) NOT NULL COMMENT '对账时长',
  `delay_minutes` int(11) NOT NULL COMMENT '延迟时长',
  `checked_time` timestamp NOT NULL COMMENT '当前进度',
  `next_trigger_time` timestamp NOT NULL COMMENT '下次允许触发时间',
  `current_task_id` bigint(20) DEFAULT NULL COMMENT '当前任务',
  `update_version` bigint(20) NOT NULL COMMENT '版本号',
  `error_count` int(11) NOT NULL COMMENT '异常次数',
  `schedule_status` char(1) NOT NULL COMMENT '计划状态：Y=启用，N=停用，E=异常停用',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`schedule_id`),
  KEY `idx_define_id` (`define_id`),
  KEY `idx_next_trigger_time` (`next_trigger_time`)
) ENGINE=InnoDB AUTO_INCREMENT=664 DEFAULT CHARSET=utf8 COMMENT='监控计划';


-- csc.t_monitor_task definition

CREATE TABLE `t_monitor_task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `define_id` bigint(20) NOT NULL COMMENT '定义ID',
  `monitor_type` char(1) NOT NULL COMMENT '监控类型：R=报表，A=报警',
  `task_type` char(1) NOT NULL COMMENT '任务类型：M=人工，S=计划，R=重试',
  `operator` varchar(64) NOT NULL COMMENT '操作员',
  `orig_task_id` bigint(20) DEFAULT NULL COMMENT '原任务ID',
  `data_begin_time` timestamp NOT NULL COMMENT '数据开始时间',
  `data_end_time` timestamp NOT NULL COMMENT '数据结束时间',
  `task_status` varchar(10) NOT NULL COMMENT '任务状态：P=处理中，S=无报警或报表，F=报警，RS=重试无报警，E=异常',
  `detail_count` int(11) DEFAULT NULL COMMENT '明细数量',
  `is_all_detail` char(1) DEFAULT NULL COMMENT '是否全部明细：Y=是，N=否',
  `alarm_level` char(1) DEFAULT NULL COMMENT '报警登记：I=忽略，N=普通，U=紧急',
  `error_code` varchar(32) DEFAULT NULL COMMENT '错误码',
  `error_message` varchar(255) DEFAULT NULL COMMENT '错误信息',
  `last_retry_task_id` bigint(20) DEFAULT NULL COMMENT '最后重试任务ID',
  `execute_consume` int(11) DEFAULT NULL COMMENT '执行耗时：单位：秒',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`),
  KEY `idx_orig_task_id` (`orig_task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=805 DEFAULT CHARSET=utf8 COMMENT='监控任务';


-- csc.t_monitor_task_detail definition

CREATE TABLE `t_monitor_task_detail` (
  `detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `key_value` varchar(128) DEFAULT NULL COMMENT '关键字段',
  `detail_content` varchar(1000) NOT NULL COMMENT '明细内容',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`detail_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_key_value` (`key_value`)
) ENGINE=InnoDB AUTO_INCREMENT=339 DEFAULT CHARSET=utf8 COMMENT='监控任务明细';


-- csc.t_notify_contact definition

CREATE TABLE `t_notify_contact` (
  `contact_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
  `contact_name` varchar(128) NOT NULL COMMENT '联系人名称',
  `email` varchar(255) NOT NULL COMMENT '邮箱地址',
  `mobile` varchar(128) DEFAULT NULL COMMENT '手机号',
  `totok_id` varchar(128) DEFAULT NULL COMMENT 'TotokID',
  `normal_notify_type` varchar(10) NOT NULL COMMENT '普通通知类型：EMAIL,SMS,TOTOK',
  `urgent_notify_type` varchar(10) NOT NULL COMMENT '紧急通知类型：EMAIL,SMS,TOTOK',
  `enable_flag` char(1) NOT NULL COMMENT '启用标识：Y=启用，N=停用',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`contact_id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COMMENT='通知联系人';


-- csc.t_notify_group definition

CREATE TABLE `t_notify_group` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '组ID',
  `group_name` varchar(128) NOT NULL COMMENT '组名称',
  `is_main` char(1) DEFAULT 'N' COMMENT '是否主通知 Y:是 N:否',
  `teams_url` varchar(512) DEFAULT NULL COMMENT 'teams频道url',
  `enable_flag` char(1) NOT NULL COMMENT '启用标识：Y=启用，N=停用',
  `extension` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COMMENT='通知组';


-- csc.t_test_data definition

CREATE TABLE `t_test_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `data_set` varchar(32) NOT NULL COMMENT '数据组',
  `data_type` char(1) NOT NULL COMMENT '数据类型：S=源数据，T=目标数据',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `status` varchar(32) NOT NULL COMMENT '状态',
  `amount` decimal(19,4) DEFAULT NULL COMMENT '金额',
  `currency` varchar(10) DEFAULT NULL COMMENT '币种',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6840 DEFAULT CHARSET=utf8 COMMENT='测试数据';


-- csc.tr_notify_define_group definition

CREATE TABLE `tr_notify_define_group` (
  `relate_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `define_type` varchar(10) NOT NULL COMMENT '定义类型：C=对账，M=监控',
  `define_id` bigint(20) NOT NULL COMMENT '定义ID',
  `group_id` bigint(20) NOT NULL COMMENT '组ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`relate_id`),
  UNIQUE KEY `uk_define_group` (`define_id`,`define_type`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=454 DEFAULT CHARSET=utf8 COMMENT='通知定义和组关联';


-- csc.tr_notify_group_contact definition

CREATE TABLE `tr_notify_group_contact` (
  `relate_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `group_id` bigint(20) NOT NULL COMMENT '组ID',
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`relate_id`),
  UNIQUE KEY `uk_group_id_contact_id` (`group_id`,`contact_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 COMMENT='通知组联系人关联';


create table t_log_rule
(
    id int not null auto_increment comment 'ID',
    function_code varchar(32) comment '功能: porter_cgs,porter_sgs',
    rule_type varchar(32) comment '规则类型: 黑名单=BLACK,白名单=WHITE',
    app_code varchar(64)  comment '系统编码',
    api_code varchar(128) comment 'api',
    return_code varchar(32) comment '返回码',
    expression_type varchar(32) not null comment '表达式类型: 正则=REGEX，全文匹配=TEXT,空白=NONE',
    match_expression varchar(255)  comment '匹配表达式: 返回消息匹配表达式',
    enable_flag char(1) not null comment '启用标识: Y=启用，N=停用',
    update_by varchar(32) comment '最后修改人',
    memo varchar(100) comment '备注',
    create_time timestamp not null comment '创建时间',
    update_time timestamp not null comment '修改时间',
    primary key (id)
) comment = '日志匹配规则';

create table t_log_stat
(
    id bigint not null auto_increment comment 'ID',
    begin_time timestamp not null comment '开始时间',
    end_time timestamp not null comment '结束时间',
    app_code varchar(64) not null comment 'appCode',
    api_code varchar(128) not null comment 'apiCode',
    return_code varchar(32) comment '返回码',
    return_msg varchar(255) comment '错误信息',
    value_count int not null comment '出现次数',
    latest_tid varchar(64) comment '最新tid',
    status varchar(10) not null comment '确认状态: I=初始状态，M=人工确认，R=重试成功',
    update_by varchar(32) comment '修改人',
    create_time timestamp not null comment '创建时间',
    update_time timestamp not null comment '修改时间',
    primary key (id)
) comment = '日志统计';

alter table t_log_stat add index idx_begin_time (begin_time);

create table t_job_progress
(
    job_code varchar(32) not null comment '任务代码',
    job_desc varchar(32) comment '任务描述',
    check_minutes int not null comment '检查时长',
    delay_minutes int not null comment '延迟时长',
    checked_time timestamp not null comment '当前进度',
    next_trigger_time timestamp not null comment '下次触发时间',
    enable_flag char(1) not null comment '启用标识: Y=启用，N=停用',
    extension varchar(255) comment '扩展参数',
    update_version bigint not null comment '修改版本',
    memo varchar(100) comment '备注',
    create_time timestamp not null comment '创建时间',
    update_time timestamp not null comment '修改时间',
    primary key (job_code)
) comment = '任务进度表';



