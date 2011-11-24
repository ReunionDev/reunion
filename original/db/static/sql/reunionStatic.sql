# SQL Manager 2005 Lite for MySQL 3.6.0.3
# ---------------------------------------
# Host     : localhost
# Port     : 3306
# Database : reunion

SET FOREIGN_KEY_CHECKS=0;

DROP DATABASE IF EXISTS `reunionStatic`;

CREATE DATABASE `reunionStatic`
    CHARACTER SET 'utf8'
    COLLATE 'utf8_unicode_ci';

USE `reunionStatic`;

DROP TABLE IF EXISTS `quests`;
CREATE TABLE IF NOT EXISTS `quests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeid` int(11) NOT NULL DEFAULT '0',
  `minlevel` int(11) NOT NULL DEFAULT '0',
  `maxlevel` int(11) NOT NULL DEFAULT '0',
  `name` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=31 ;


INSERT INTO `quests` (`id`, `typeid`, `minlevel`, `maxlevel`, `name`) VALUES
(1, 2, 20, 25, 'S.King Chmero'),
(25, 1, 35, 40, 'S. King Pro Giant'),
(26, 1, 50, 55, 'S. Battle Giant'),
(27, 2, 50, 100, 'S. Dekadun Bracelet'),
(29, 2, 26, 30, 'S.Life Amulet'),
(30, 2, 40, 45, 'S.Seeking Strength Ring');



DROP TABLE IF EXISTS `quests_objective`;
CREATE TABLE IF NOT EXISTS `quests_objective` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `questid` int(11) NOT NULL,
  `objectivetype` int(11) NOT NULL DEFAULT '0',
  `objectiveid` int(11) NOT NULL DEFAULT '0',
  `ammount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11 ;


INSERT INTO `quests_objective` (`id`, `questid`, `objectivetype`, `objectiveid`, `ammount`) VALUES
(1, 1, 1, 228, 1),
(2, 25, 1, 229, 10),
(3, 26, 1, 230, 10),
(4, 27, 1, 253, 1),
(5, 29, 1, 26, 50),
(6, 29, 1, 37, 5),
(7, 29, 1, 41, 2),
(8, 30, 1, 54, 20),
(9, 30, 1, 106, 10),
(10, 30, 1, 48, 5);



DROP TABLE IF EXISTS `quests_objective_type`;
CREATE TABLE IF NOT EXISTS `quests_objective_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;


INSERT INTO `quests_objective_type` (`id`, `class`) VALUES
(1, 'MobObjective'),
(2, 'PointsObjective');



DROP TABLE IF EXISTS `quests_reward`;
CREATE TABLE IF NOT EXISTS `quests_reward` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `questid` int(11) NOT NULL,
  `rewardtype` int(11) NOT NULL DEFAULT '0',
  `rewardid` int(11) NOT NULL DEFAULT '0',
  `ammount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;


INSERT INTO `quests_reward` (`id`, `questid`, `rewardtype`, `rewardid`, `ammount`) VALUES
(1, 1, 3, 222, 1),
(2, 25, 3, 223, 1),
(3, 26, 3, 224, 1),
(4, 27, 3, 901, 1),
(5, 29, 3, 939, 1),
(6, 29, 1, 0, 300),
(7, 30, 3, 937, 1),
(8, 30, 1, 0, 1000);



DROP TABLE IF EXISTS `quests_reward_type`;
CREATE TABLE IF NOT EXISTS `quests_reward_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;


INSERT INTO `quests_reward_type` (`id`, `class`) VALUES
(1, 'ExperienceReward'),
(2, 'LimeReward'),
(3, 'ItemReward');



DROP TABLE IF EXISTS `quests_type`;
CREATE TABLE IF NOT EXISTS `quests_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;


INSERT INTO `quests_type` (`id`, `class`) VALUES
(1, 'ExperienceQuest'),
(2, 'LimeQuest'),
(3, 'SpecialQuest'),
(4, 'PointsQuest');

COMMIT;
