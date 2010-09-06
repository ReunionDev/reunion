# SQL Manager 2005 Lite for MySQL 3.6.0.3
# ---------------------------------------
# Host     : localhost
# Port     : 3306
# Database : reunion


SET FOREIGN_KEY_CHECKS=0;

DROP DATABASE IF EXISTS `reunion`;

CREATE DATABASE `reunion`
    CHARACTER SET 'latin1'
    COLLATE 'latin1_swedish_ci';

USE `reunion`;

#
# Structure for the `accounts` table : 
#

DROP TABLE IF EXISTS `accounts`;

CREATE TABLE `accounts` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(28) NOT NULL,
  `password` varchar(28) NOT NULL,
  `email` varchar(256) NOT NULL,
  `realname` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `characters` table : 
#

DROP TABLE IF EXISTS `characters`;

CREATE TABLE `characters` (
  `id` int(11) unsigned NOT NULL,
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
  `userlevel` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `equipment` table : 
#

DROP TABLE IF EXISTS `equipment`;

CREATE TABLE `equipment` (
  `charid` int(11) NOT NULL,
  `head` int(11) default '-1',
  `body` int(11) default '-1',
  `legs` int(11) default '-1',
  `feet` int(11) default '-1',
  `weapon` int(11) default '-1',
  `shield` int(11) default '-1',
  `shouldermount` int(11) default '-1',
  `bracelet` int(11) default '-1',
  `ring` int(11) default '-1',
  `necklace` int(11) default '-1',
  PRIMARY KEY  (`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `exchange` table : 
#

DROP TABLE IF EXISTS `exchange`;

CREATE TABLE `exchange` (
  `characterid` int(11) NOT NULL default '0',
  `uniqueitemid` int(11) NOT NULL default '0',
  `x` int(11) NOT NULL default '0',
  `y` int(11) NOT NULL default '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `guilds` table : 
#

DROP TABLE IF EXISTS `guilds`;

CREATE TABLE `guilds` (
  `id` int(11) NOT NULL default '0',
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `inventory` table : 
#

DROP TABLE IF EXISTS `inventory`;

CREATE TABLE `inventory` (
  `characterid` int(11) NOT NULL,
  `uniqueitemid` int(11) NOT NULL,
  `tab` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  PRIMARY KEY  (`characterid`,`uniqueitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `items` table : 
#

DROP TABLE IF EXISTS `items`;

CREATE TABLE `items` (
  `id` int(11) NOT NULL default '0',
  `type` int(11) NOT NULL default '0',
  `gemnumber` int(11) NOT NULL default '0',
  `extrastats` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `quickslot` table : 
#

DROP TABLE IF EXISTS `quickslot`;

CREATE TABLE `quickslot` (
  `characterid` int(11) NOT NULL default '0',
  `uniqueitemid` int(11) NOT NULL default '0',
  `slot` int(11) NOT NULL default '0',
  PRIMARY KEY  (`characterid`,`uniqueitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `settings` table : 
#

DROP TABLE IF EXISTS `settings`;

CREATE TABLE `settings` (
  `id` varchar(64) NOT NULL,
  `data` varchar(1024) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `skills` table : 
#

DROP TABLE IF EXISTS `skills`;

CREATE TABLE `skills` (
  `charid` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY  (`charid`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `slots`;

CREATE TABLE `slots` (
  `accountid` int(11) NOT NULL,
  `characterid` int(11) NOT NULL,
  `slotnumber` int(11) NOT NULL,
  PRIMARY KEY  (`accountid`,`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `stash` table : 
#

DROP TABLE IF EXISTS `stash`;

CREATE TABLE `stash` (
  `accountid` int(11) NOT NULL default '0',
  `pos` int(11) NOT NULL default '0',
  `uniqueitemid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`uniqueitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Data for the `accounts` table  (LIMIT 0,500)
#

INSERT INTO `accounts` (`id`, `username`, `password`, `email`, `realname`) VALUES 
  (1,'Test','test','teej@mail.com','teej'),
  (2,'Test2','test','sam@mail.com','sam');

COMMIT;

#
# Data for the `characters` table  (LIMIT 0,500)
#

INSERT INTO `characters` (`id`, `accountid`, `name`, `level`, `str`, `wis`, `dex`, `con`, `lea`, `race`, `sex`, `hair`, `currHp`, `MaxHp`, `currMana`, `maxMana`, `currElect`, `maxElect`, `currStm`, `maxStm`, `totalExp`, `lvlUpExp`, `lime`, `statusPoints`, `penaltyPoints`, `guildid`, `guildlvl`, `userlevel`) VALUES 
  (1,1,'TJ',300,200,200,200,200,200,0,0,1,9000,10000,500,1000,20,70,500,1000,1234,200,100,100,10000,-1,0,0),
  (2,2,'SAM',45,75,5,5,20,5,0,0,0,10003,10003,1000,1000,70,70,1003,1003,1448953,129187,215411,134,10000,-1,0,0);

COMMIT;

#
# Data for the `equipment` table  (LIMIT 0,500)
#

INSERT INTO `equipment` (`charid`, `head`, `body`, `legs`, `feet`, `weapon`, `shield`, `shouldermount`, `bracelet`, `ring`, `necklace`) VALUES 
  (1,-1,-1,-1,-1,8,7,-1,-1,-1,-1),
  (2,-1,-1,-1,-1,6,5,-1,-1,-1,-1);

COMMIT;

#
# Data for the `inventory` table  (LIMIT 0,500)
#

INSERT INTO `inventory` (`characterid`, `uniqueitemid`, `tab`, `x`, `y`) VALUES 
  (2,46,0,0,1);

COMMIT;

#
# Data for the `items` table  (LIMIT 0,500)
#

INSERT INTO `items` (`id`, `type`, `gemnumber`, `extrastats`) VALUES 
  (3,0,9999,0),
  (4,0,99979,0),
  (5,190,0,0),
  (6,212,0,0),
  (7,190,0,0),
  (8,212,0,0),
  (46,642,1,0),
  (47,642,0,255),
  (48,642,0,0),
  (49,642,0,0),
  (50,642,0,0);

COMMIT;

#
# Data for the `settings` table  (LIMIT 0,500)
#

INSERT INTO `settings` (`id`, `data`) VALUES 
  ('clientversion','100'),
  ('sessionradius','500');

COMMIT;

#
# Data for the `skills` table  (LIMIT 0,500)
#

#
# Data for the `slots` table  (LIMIT 0,500)
#

INSERT INTO `slots` (`accountid`, `characterid`, `slotnumber`) VALUES 
  (1,1,2),
  (2,2,2);

COMMIT;

#
# Data for the `stash` table  (LIMIT 0,500)
#

INSERT INTO `stash` (`accountid`, `pos`, `uniqueitemid`) VALUES 
  (1,12,3),
  (2,12,4),
  (2,7,47),
  (2,0,48),
  (2,0,49),
  (2,0,50);

COMMIT;

