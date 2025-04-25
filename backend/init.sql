CREATE DATABASE IF NOT EXISTS meeting_room DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meeting_room;

-- 用户表
CREATE TABLE `account` (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           username VARCHAR(50) UNIQUE NOT NULL,
                           password VARCHAR(100) NOT NULL,
                           email VARCHAR(100) UNIQUE NOT NULL,
                           nick_name VARCHAR(50) NOT NULL,
                           role_name VARCHAR(20) NOT NULL CHECK (role_name IN ('admin', 'worker', 'user')),
                           company VARCHAR(100),
                           phone VARCHAR(20),
                           balance DECIMAL(10,2) DEFAULT 0.00,
                           status_type VARCHAR(20) NOT NULL DEFAULT '正常' CHECK (status_type IN ('正常', '冻结', '待审核', '审核不通过')),
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会议室表
CREATE TABLE meeting_room (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              room_name VARCHAR(100) NOT NULL,
                              room_type VARCHAR(20) NOT NULL CHECK (room_type IN ('教室型', '圆桌型')),
                              capacity INT NOT NULL,
                              has_projector BOOLEAN DEFAULT FALSE,
                              price_per_hour DECIMAL(10,2) NOT NULL,
                              room_status VARCHAR(20) NOT NULL DEFAULT '空闲' CHECK (room_status IN ('空闲', '锁定', '预定', '使用', '维护')),
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              del_flag TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE `room_order` (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              room_id BIGINT NOT NULL,
                              start_time DATETIME NOT NULL,
                              end_time DATETIME NOT NULL,
                              total_price DECIMAL(10,2) NOT NULL,
                              order_status VARCHAR(20) NOT NULL DEFAULT '待支付' CHECK (order_status IN ('待支付', '退款中', '已支付', '已取消')),
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              payment_time DATETIME,
                              cancel_time DATETIME,
                              refund_amount DECIMAL(10,2) DEFAULT 0.00,
                              del_flag TINYINT DEFAULT 0,

                              FOREIGN KEY (user_id) REFERENCES account(id) ON DELETE RESTRICT,
                              FOREIGN KEY (room_id) REFERENCES meeting_room(id) ON DELETE RESTRICT,
                              INDEX idx_user_time (user_id, start_time),
                              INDEX idx_room_time (room_id, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 取消申请表
CREATE TABLE cancellation_application (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          order_id BIGINT NOT NULL,
                                          apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                          refund_percent INT NOT NULL CHECK (refund_percent IN (25, 75, 100)),
                                          staff_id BIGINT,
                                          audit_status VARCHAR(20) DEFAULT '待审核' CHECK (audit_status IN ('待审核', '通过', '拒绝')),
                                          note VARCHAR(300),
                                          audit_time DATETIME,
                                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          del_flag TINYINT DEFAULT 0,

                                          FOREIGN KEY (order_id) REFERENCES room_order(id) ON DELETE CASCADE,
                                          FOREIGN KEY (staff_id) REFERENCES account(id) ON DELETE SET NULL,
                                          INDEX idx_order_status (order_id, audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 时间粒度表
CREATE TABLE room_time_slot (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                room_id BIGINT NOT NULL,
                                time_slot DATETIME NOT NULL,
                                status_type VARCHAR(20) NOT NULL DEFAULT '空闲' CHECK (status_type IN ('空闲', '锁定', '预定')),
                                del_flag TINYINT DEFAULT 0,

                                FOREIGN KEY (room_id) REFERENCES meeting_room(id) ON DELETE CASCADE,
                                UNIQUE KEY uk_room_time (room_id, time_slot),
                                INDEX idx_time_status (time_slot, status_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;