DROP DATABASE IF EXISTS `moni_one_x`;
CREATE DATABASE `moni_one_x`;
USE `moni_one_x`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE user (
                      id bigint UNSIGNED COMMENT '用户ID',
                      user_name varchar(64) NOT NULL COMMENT '用户身份标识：学号、用户名等',
                      hash_password varchar(64) NOT NULL COMMENT '用户登录凭证',
                      name varchar(64) DEFAULT '' NOT NULL COMMENT '用户姓名/昵称',
                      status tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户状态：0不可用；1正常；2封禁',
                      user_type tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户权限类型：0超级管理员、1管理员、2普通用户',
                      PRIMARY KEY (id)
) ENGINE=innodb CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '用户注册信息表';

INSERT INTO user(id, user_name, hash_password, name, status, user_type)
VALUES
    (7331143291401928704, 'SUPER_ADMIN', 'fb10f0238035c2b31076223dc45beea0', '超级管理员', 1, 0),
    (7331143520448675840, 'ADMIN', '73acd9a5972130b75066c82595a1fae3', '管理员', 1, 1);

DROP TABLE IF EXISTS `module`;
CREATE TABLE module (
                        id bigint UNSIGNED COMMENT '题目模块ID',
                        display_order int UNSIGNED COMMENT '顺序',
                        label varchar(64) NOT NULL COMMENT '模块Label',
                        name varchar(64) NOT NULL COMMENT '模块名称',
                        bg_color varchar(64) COMMENT '背景/主题色',
                        is_open bool NOT NULL DEFAULT true COMMENT '是否公开',
                        is_gen_choose bool NOT NULL DEFAULT true COMMENT '是否被选中为生成每日任务',
                        remark varchar(255) COMMENT '备注',
                        PRIMARY KEY (id)
) ENGINE=innodb CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '题目模块信息表';
INSERT INTO module (id, display_order, label, name, bg_color, is_open, is_gen_choose, remark)
VALUES
    (1926254740090523648, 0, 'MYSQL', 'MYSQL关系数据库', '#7eb0c8', true, true, ''),
    (1926272538225287168, 0, '东方Project', '东方Project', '#7ec897', true, true, ''),
    (1926273266499067904, 0, 'Hololive', '虚拟主播', '#9b7ec8', true, true, '');


DROP TABLE IF EXISTS `question`;
CREATE TABLE question (
                          id bigint UNSIGNED COMMENT '题目ID',
                          display_order int UNSIGNED COMMENT '顺序',
                          title varchar(64) COMMENT '题目标题',
                          content text NOT NULL COMMENT '题目内容',
                          create_time datetime NOT NULL DEFAULT NOW() COMMENT '创建时间',
                          PRIMARY KEY (id),
                          module_id bigint UNSIGNED COMMENT '模块ID'
) ENGINE=innodb CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '题目信息表';

INSERT INTO question (id, display_order, title, content, create_time, module_id)
VALUES
    (1926258647453917184, 1, 'MYSQL初始默认值', 'MYSQL安装时，默认的启动端口号为<span class="option-order" data-order="1">1</span>，默认管理员用户名为<span class="option-order" data-order="2">2</span>。', '2025-05-24 20:47:04', 1926254740090523648),
    (1926284660514738176, 1, '病娇的MYSQL', '在开发后端服务时，使用URL连接Mysql数据库， 通常需要添加参数<span class="option-order" data-order="1">1</span>=true，使得后端在长时间运行时，不会因不活跃导致终止连接。', '2025-05-24 22:30:26', 1926254740090523648);

DROP TABLE IF EXISTS `question_option`;
CREATE TABLE question_option (
                                 id bigint UNSIGNED COMMENT '题目选项ID',
                                 order_no int3 UNSIGNED NOT NULL COMMENT '选项顺序',
                                 answer VARCHAR(255) NOT NULL DEFAULT '' COMMENT '选项答案',
                                 PRIMARY KEY (id),
                                 question_id bigint UNSIGNED COMMENT '题目ID'
) ENGINE=innodb CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '题目选项信息表';

DROP TABLE IF EXISTS `question_submit_option`;
CREATE TABLE question_submit_option (
                                        id bigint UNSIGNED COMMENT '题目选项ID',
                                        submit_date datetime NOT NULL DEFAULT NOW() COMMENT '提交日期',
                                        user_answer varchar(255) NOT NULL COMMENT '用户提交的答案',
                                        is_right bool NOT NULL DEFAULT false COMMENT '是否正确：便于数据处理',
                                        PRIMARY KEY (id),
                                        option_id bigint UNSIGNED NOT NULL COMMENT '选项ID',
                                        question_id bigint UNSIGNED COMMENT '题目ID',
                                        user_id bigint UNSIGNED COMMENT '用户ID'
) ENGINE=innodb CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '用户提交信息表';

INSERT INTO question_option(id, order_no, answer, question_id)
VALUES
    (1926258647504248832, 1, '3306', 1926258647453917184),
    (1926258647525220352, 2, 'root', 1926258647453917184),
    (1926284660556681216, 1, 'autoReconnect', 1926284660514738176);

DROP TABLE IF EXISTS `map_schema`;
CREATE TABLE map_schema(
                           id bigint UNSIGNED COMMENT 'ID',
                           map_key varchar(255) NOT NULL COMMENT 'map key',
                           map_value text COMMENT 'map value',
                           remark varchar(255) COMMENT '备注',
                           PRIMARY KEY (id),
                           UNIQUE (map_key)
) ENGINE=innodb CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '键值对map表';

DROP TABLE IF EXISTS `daily_submit`;
CREATE TABLE daily_submit(
                             id bigint UNSIGNED COMMENT 'ID',
                             user_id bigint UNSIGNED COMMENT '用户ID',
                             question_id bigint UNSIGNED COMMENT '题目ID',
                             result int UNSIGNED COMMENT '是否正确, 0错误，1正确  ',
                             submit_time datetime COMMENT '提交日期'
) ENGINE=innodb CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '每日用户提交记录';