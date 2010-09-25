# SQL Manager 2005 Lite for MySQL 3.6.0.3
# ---------------------------------------
# Host     : localhost
# Port     : 3306
# Database : reunion


SET FOREIGN_KEY_CHECKS=0;

DROP DATABASE IF EXISTS `reunion`;

CREATE DATABASE `reunion`
    CHARACTER SET 'utf8'
    COLLATE 'utf8_unicode_ci';

USE `reunion`;

DROP TABLE IF EXISTS `accounts`;

CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(28) NOT NULL,
  `password` varchar(28) NOT NULL,
  `email` varchar(256) NOT NULL,
  `level` int(11) NOT NULL default '0',
  `realname` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `characters`;

CREATE TABLE `characters` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `level` int(11) NOT NULL default '1',
  `str` int(11) NOT NULL default '1',
  `wis` int(11) NOT NULL default '1',
  `dex` int(11) NOT NULL default '1',
  `con` int(11) NOT NULL default '1',
  `lea` int(11) NOT NULL default '1',
  `race` int(11) NOT NULL default '1',
  `sex` int(11) NOT NULL default '1',
  `hair` int(11) NOT NULL default '1',
  `currHp` int(11) NOT NULL,
  `MaxHp` int(11) NOT NULL,
  `currMana` int(11) NOT NULL,
  `maxMana` int(11) NOT NULL,
  `currElect` int(11) NOT NULL,
  `maxElect` int(11) NOT NULL,
  `currStm` int(11) NOT NULL,
  `maxStm` int(11) NOT NULL,
  `totalExp` int(11) NOT NULL,
  `lvlUpExp` int(11) NOT NULL,
  `lime` int(11) NOT NULL,
  `statusPoints` int(11) NOT NULL,
  `penaltyPoints` int(11) NOT NULL,
  `guildid` int(11) NOT NULL default '-1',
  `guildlvl` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `equipment`;

CREATE TABLE `equipment` (
  `charid` int(11) NOT NULL,
  `slot` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  PRIMARY KEY  (`charid`,`slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exchange`;

CREATE TABLE `exchange` (
  `charid` int(11) NOT NULL default '0',
  `itemid` int(11) NOT NULL default '0',
  `x` int(11) NOT NULL default '0',
  `y` int(11) NOT NULL default '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `guilds`;

CREATE TABLE `guilds` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `inventory`;

CREATE TABLE `inventory` (
  `charid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  `tab` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  PRIMARY KEY  (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `items`;

CREATE TABLE `items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `gemnumber` int(11) NOT NULL default '0',
  `extrastats` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `quickslot`;

CREATE TABLE `quickslot` (
  `charid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  `slot` int(11) NOT NULL,
  PRIMARY KEY  (`charid`,`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `roaming`;

CREATE TABLE `roaming` (
  `itemid` int(11) NOT NULL,
  `mapid` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `charid` int(11) DEFAULT NULL,
  `dropped` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`itemid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `skills`;

CREATE TABLE `skills` (
  `charid` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY  (`charid`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `slots`;

CREATE TABLE `slots` (
  `accountid` int(11) NOT NULL,
  `charid` int(11) NOT NULL,
  `slot` int(11) NOT NULL,
  PRIMARY KEY  (`accountid`,`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `warehouse`;

CREATE TABLE `warehouse` (
  `accountid` int(11) NOT NULL default '0',
  `pos` int(11) NOT NULL default '0',
  `itemid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `accounts` (`id`, `username`, `password`, `email`, `realname`,`level`) VALUES 
  (1,'test','test','test@example.com','Test User',0),
  (2,'admin','admin','admin@example.com','Test Admin',255);

COMMIT;
