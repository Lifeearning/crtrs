/*
 Navicat Premium Data Transfer

 Source Server         : locallhost
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : crtrs

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 23/03/2026 13:41:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for b_card
-- ----------------------------
DROP TABLE IF EXISTS `b_card`;
CREATE TABLE `b_card`  (
  `b_id` int NOT NULL AUTO_INCREMENT COMMENT '商品卡片id（书籍id）',
  `uid` bigint NOT NULL COMMENT '卡片所属用户uid',
  `b_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品卡片图片地址',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '书籍名称',
  `isbn` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ISBN码',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `major` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联专业',
  `course` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联课程',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '书籍作者',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出版社',
  `is_sold` tinyint NOT NULL DEFAULT 0 COMMENT '标注此商品是否已经被售出（默认为未售出“0”，已售出为“1”）',
  PRIMARY KEY (`b_id`, `uid`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `b_id`(`b_id` ASC) USING BTREE,
  CONSTRAINT `uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `order_id` bigint(8) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT COMMENT '订单号',
  `pub_uid` bigint NOT NULL COMMENT '发布者uid',
  `buy_uid` bigint NOT NULL COMMENT '买家uid',
  `bid` int NOT NULL COMMENT '书籍卡片bid',
  `order_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  `opinion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '买家评价',
  `opinion_star` int NULL DEFAULT NULL COMMENT '评价星级（1-5）',
  `opinion_time` datetime NULL DEFAULT NULL COMMENT '评价时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `bid`(`bid` ASC) USING BTREE,
  INDEX `buy_uid`(`buy_uid` ASC) USING BTREE,
  INDEX `pub_uid`(`pub_uid` ASC) USING BTREE,
  CONSTRAINT `bid` FOREIGN KEY (`bid`) REFERENCES `b_card` (`b_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `buy_uid` FOREIGN KEY (`buy_uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `pub_uid` FOREIGN KEY (`pub_uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `uid` bigint NOT NULL AUTO_INCREMENT COMMENT '系统内部使用，不提供给用户，用户唯一识别码',
  `u_count` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号，用户通过该方式登录，唯一',
  `u_password` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `u_pho` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '账号余额',
  PRIMARY KEY (`uid` DESC) USING BTREE,
  UNIQUE INDEX `ucount`(`u_count` ASC) USING BTREE COMMENT '保持用户账号唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 1000016 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
