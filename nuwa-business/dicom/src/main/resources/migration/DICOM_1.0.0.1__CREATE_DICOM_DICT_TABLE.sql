-- ----------------------------
-- Table structure for dicom_tag_dict
-- ----------------------------
DROP TABLE IF EXISTS `dicom_tag_dict`;
CREATE TABLE `dicom_tag_dict`
(
    `id`           int         NOT NULL AUTO_INCREMENT,
    `tag_id`       int         NOT NULL COMMENT 'Tag的ID,值为dicom_tag表id',
    `dict_value`   varchar(50) NOT NULL COMMENT '字典值',
    `created_time` timestamp   NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of dicom_tag_dict
-- ----------------------------
BEGIN;
INSERT INTO `dicom_tag_dict` VALUES (1, 125, 'CT', '2022-05-07 13:40:14');
INSERT INTO `dicom_tag_dict` VALUES (2, 710, 'LUNG', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (3, 710, 'CHEST', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (4, 710, 'THORAX', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (5, 710, 'ABDOMEN', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (6, 91, 'ORIGINAL', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (7, 91, 'DERIVED', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (8, 91, 'PRIMARY', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (9, 91, 'SECONDARY', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (10, 1990, 'JPEG2000Lossless', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (11, 1990, 'JPEGLossless', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (12, 1990, 'RAW', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (13, 131, 'GE', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (14, 131, 'PHILIPS', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (15, 131, 'SIEMENS', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (16, 131, 'TOSHIBA', '2022-05-07 13:41:05');
INSERT INTO `dicom_tag_dict` VALUES (17, 131, 'UNKNOWN', '2022-05-07 13:41:05');
COMMIT;