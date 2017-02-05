/*
Navicat MySQL Data Transfer

Source Server         : zhuzheng
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : zhuzheng

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-01-27 16:26:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for chinese_question_copy
-- ----------------------------
DROP TABLE IF EXISTS `chinese_question_copy`;
CREATE TABLE `chinese_question_copy` (
  `q_id` int(11) NOT NULL AUTO_INCREMENT,
  `question` text,
  PRIMARY KEY (`q_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;
