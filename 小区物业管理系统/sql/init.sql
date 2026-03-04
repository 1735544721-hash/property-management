-- ==========================================
-- 小区物业管理系统 - 数据库初始化脚本
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE property_management;

-- ==========================================
-- 1. 用户表
-- ==========================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'resident' COMMENT '角色（admin/staff/resident）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0禁用/1启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0未删除/1已删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==========================================
-- 2. 楼栋表
-- ==========================================
DROP TABLE IF EXISTS building;
CREATE TABLE building (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    floors INT NOT NULL COMMENT '楼层数',
    units INT NOT NULL COMMENT '单元数',
    description VARCHAR(255) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼栋表';

-- ==========================================
-- 3. 房屋表
-- ==========================================
DROP TABLE IF EXISTS house;
CREATE TABLE house (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    unit_number INT NOT NULL COMMENT '单元号',
    floor_number INT NOT NULL COMMENT '楼层号',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    area DECIMAL(10,2) COMMENT '面积(㎡)',
    owner_id BIGINT COMMENT '业主ID',
    status TINYINT DEFAULT 0 COMMENT '状态（0空置/1已入住）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_building_id (building_id),
    KEY idx_owner_id (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房屋表';

-- ==========================================
-- 4. 报修表
-- ==========================================
DROP TABLE IF EXISTS repair;
CREATE TABLE repair (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '报修人ID',
    house_id BIGINT COMMENT '房屋ID',
    title VARCHAR(100) NOT NULL COMMENT '报修标题',
    content TEXT COMMENT '报修内容',
    images VARCHAR(500) COMMENT '图片URL（多张用逗号分隔）',
    status TINYINT DEFAULT 0 COMMENT '状态（0待处理/1处理中/2已完成）',
    handler_id BIGINT COMMENT '处理人ID',
    handle_result TEXT COMMENT '处理结果',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    handle_time DATETIME COMMENT '处理时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修表';

-- ==========================================
-- 5. 费用表
-- ==========================================
DROP TABLE IF EXISTS fee;
CREATE TABLE fee (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    house_id BIGINT NOT NULL COMMENT '房屋ID',
    fee_type VARCHAR(20) NOT NULL COMMENT '费用类型（物业费/水费/电费/停车费）',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    fee_month VARCHAR(10) NOT NULL COMMENT '费用月份（如2024-01）',
    status TINYINT DEFAULT 0 COMMENT '状态（0未缴/1已缴）',
    pay_time DATETIME COMMENT '缴费时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_house_id (house_id),
    KEY idx_fee_month (fee_month),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='费用表';

-- ==========================================
-- 6. 公告表
-- ==========================================
DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    publisher_id BIGINT COMMENT '发布人ID',
    status TINYINT DEFAULT 0 COMMENT '状态（0草稿/1已发布）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    publish_time DATETIME COMMENT '发布时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ==========================================
-- 7. 投诉建议表
-- ==========================================
DROP TABLE IF EXISTS complaint;
CREATE TABLE complaint (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '投诉人ID',
    type VARCHAR(20) NOT NULL COMMENT '类型（投诉/建议）',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    status TINYINT DEFAULT 0 COMMENT '状态（0待处理/1已处理）',
    reply TEXT COMMENT '回复内容',
    handler_id BIGINT COMMENT '处理人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    handle_time DATETIME COMMENT '处理时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投诉建议表';

-- ==========================================
-- 初始化数据
-- ==========================================

-- 所有账号密码统一为: 123456
-- BCrypt加密后: $2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW

-- 初始化管理员账号
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES 
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '系统管理员', '13800000000', 'admin', 1);

-- 初始化物业员工账号
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES 
('staff01', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '张物业', '13800000001', 'staff', 1);

-- 初始化业主账号
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES 
('user01', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '李业主', '13800000002', 'resident', 1),
('user02', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '王业主', '13800000003', 'resident', 1);

-- 初始化楼栋数据
INSERT INTO building (building_name, floors, units, description) VALUES 
('1号楼', 18, 2, '小区东区高层住宅'),
('2号楼', 18, 2, '小区东区高层住宅'),
('3号楼', 6, 3, '小区西区多层住宅'),
('4号楼', 6, 3, '小区西区多层住宅');

-- 初始化房屋数据
INSERT INTO house (building_id, unit_number, floor_number, room_number, area, owner_id, status) VALUES 
(1, 1, 1, '101', 89.50, 3, 1),
(1, 1, 1, '102', 120.00, 4, 1),
(1, 1, 2, '201', 89.50, NULL, 0),
(1, 1, 2, '202', 120.00, NULL, 0),
(2, 1, 1, '101', 95.00, NULL, 0),
(2, 1, 1, '102', 110.00, NULL, 0);

-- 初始化公告数据
INSERT INTO notice (title, content, publisher_id, status, publish_time) VALUES 
('关于小区道路维修的通知', '尊敬的业主朋友们：\n\n因小区主干道路面破损严重，物业将于本周六（12月28日）进行道路维修施工。施工期间，请各位业主绕行，给您带来不便，敬请谅解！\n\n物业服务中心\n2025年12月27日', 1, 1, NOW()),
('2025年元旦放假通知', '尊敬的业主朋友们：\n\n2025年元旦假期将至，物业服务中心放假安排如下：\n1月1日-1月3日放假调休，共3天。\n\n假期期间，如遇紧急情况请拨打物业24小时服务热线：400-123-4567。\n\n提前祝大家元旦快乐！\n\n物业服务中心\n2025年12月27日', 1, 1, NOW());

-- 初始化报修数据
INSERT INTO repair (user_id, house_id, title, content, status, create_time) VALUES 
(3, 1, '厨房水龙头漏水', '厨房水龙头关不紧，一直在漏水，请尽快安排维修。', 0, NOW()),
(4, 2, '客厅灯不亮', '客厅主灯突然不亮了，可能是线路问题，请派人检查。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 初始化费用数据
INSERT INTO fee (house_id, fee_type, amount, fee_month, status) VALUES 
(1, '物业费', 268.50, '2025-01', 0),
(1, '水费', 45.80, '2025-01', 0),
(1, '电费', 156.20, '2025-01', 0),
(2, '物业费', 360.00, '2025-01', 0),
(2, '水费', 62.50, '2025-01', 0),
(2, '电费', 203.80, '2025-01', 0),
(1, '物业费', 268.50, '2024-12', 1),
(1, '水费', 52.30, '2024-12', 1);

-- 初始化投诉建议数据
INSERT INTO complaint (user_id, type, title, content, status) VALUES 
(3, '建议', '希望增加小区健身设施', '建议在小区中心花园增加一些健身器材，方便居民锻炼身体。', 0),
(4, '投诉', '楼道灯损坏多日未修', '2号楼1单元2楼楼道灯已损坏一周多，夜间出行很不方便，希望尽快维修。', 0);

