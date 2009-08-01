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
  `0` int(11) NOT NULL default '0',
  `1` int(11) NOT NULL default '0',
  `2` int(11) NOT NULL default '0',
  `3` int(11) NOT NULL default '0',
  `4` int(11) NOT NULL default '0',
  `5` int(11) NOT NULL default '0',
  `6` int(11) NOT NULL default '0',
  `7` int(11) NOT NULL default '0',
  `8` int(11) NOT NULL default '0',
  `9` int(11) NOT NULL default '0',
  `10` int(11) NOT NULL default '0',
  `11` int(11) NOT NULL default '0',
  `12` int(11) NOT NULL default '0',
  `13` int(11) NOT NULL default '0',
  `14` int(11) NOT NULL default '0',
  `15` int(11) NOT NULL default '0',
  `16` int(11) NOT NULL default '0',
  `17` int(11) NOT NULL default '0',
  `18` int(11) NOT NULL default '0',
  `19` int(11) NOT NULL default '0',
  `20` int(11) NOT NULL default '0',
  `21` int(11) NOT NULL default '0',
  `22` int(11) NOT NULL default '0',
  `23` int(11) NOT NULL default '0',
  `24` int(11) NOT NULL default '0',
  `25` int(11) NOT NULL default '0',
  `26` int(11) NOT NULL default '0',
  `27` int(11) NOT NULL default '0',
  `28` int(11) NOT NULL default '0',
  `29` int(11) NOT NULL default '0',
  `30` int(11) NOT NULL default '0',
  `31` int(11) NOT NULL default '0',
  `32` int(11) NOT NULL default '0',
  `33` int(11) NOT NULL default '0',
  `34` int(11) NOT NULL default '0',
  `35` int(11) NOT NULL default '0',
  `36` int(11) NOT NULL default '0',
  `37` int(11) NOT NULL default '0',
  `38` int(11) NOT NULL default '0',
  `39` int(11) NOT NULL default '0',
  `40` int(11) NOT NULL default '0',
  `41` int(11) NOT NULL default '0',
  `42` int(11) NOT NULL default '0',
  `43` int(11) NOT NULL default '0',
  `44` int(11) NOT NULL default '0',
  `45` int(11) NOT NULL default '0',
  `46` int(11) NOT NULL default '0',
  `47` int(11) NOT NULL default '0',
  `48` int(11) NOT NULL default '0',
  `49` int(11) NOT NULL default '0',
  `50` int(11) NOT NULL default '0',
  `51` int(11) NOT NULL default '0',
  `52` int(11) NOT NULL default '0',
  `53` int(11) NOT NULL default '0',
  `54` int(11) NOT NULL default '0',
  `55` int(11) NOT NULL default '0',
  `56` int(11) NOT NULL default '0',
  `57` int(11) NOT NULL default '0',
  `58` int(11) NOT NULL default '0',
  `59` int(11) NOT NULL default '0',
  `60` int(11) NOT NULL default '0',
  `61` int(11) NOT NULL default '0',
  `62` int(11) NOT NULL default '0',
  `63` int(11) NOT NULL default '0',
  `64` int(11) NOT NULL default '0',
  `65` int(11) NOT NULL default '0',
  `66` int(11) NOT NULL default '0',
  `67` int(11) NOT NULL default '0',
  `68` int(11) NOT NULL default '0',
  `69` int(11) NOT NULL default '0',
  `70` int(11) NOT NULL default '0',
  `71` int(11) NOT NULL default '0',
  `72` int(11) NOT NULL default '0',
  `73` int(11) NOT NULL default '0',
  `74` int(11) NOT NULL default '0',
  `75` int(11) NOT NULL default '0',
  `76` int(11) NOT NULL default '0',
  `77` int(11) NOT NULL default '0',
  `78` int(11) NOT NULL default '0',
  `79` int(11) NOT NULL default '0',
  `80` int(11) NOT NULL default '0',
  `81` int(11) NOT NULL default '0',
  `82` int(11) NOT NULL default '0',
  `83` int(11) NOT NULL default '0',
  `84` int(11) NOT NULL default '0',
  `85` int(11) NOT NULL default '0',
  `86` int(11) NOT NULL default '0',
  `87` int(11) NOT NULL default '0',
  `88` int(11) NOT NULL default '0',
  `89` int(11) NOT NULL default '0',
  `90` int(11) NOT NULL default '0',
  `91` int(11) NOT NULL default '0',
  `92` int(11) NOT NULL default '0',
  `93` int(11) NOT NULL default '0',
  `94` int(11) NOT NULL default '0',
  `95` int(11) NOT NULL default '0',
  `96` int(11) NOT NULL default '0',
  `97` int(11) NOT NULL default '0',
  `98` int(11) NOT NULL default '0',
  PRIMARY KEY  (`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Structure for the `slots` table : 
#

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

INSERT INTO `skills` (`charid`, `0`, `1`, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `10`, `11`, `12`, `13`, `14`, `15`, `16`, `17`, `18`, `19`, `20`, `21`, `22`, `23`, `24`, `25`, `26`, `27`, `28`, `29`, `30`, `31`, `32`, `33`, `34`, `35`, `36`, `37`, `38`, `39`, `40`, `41`, `42`, `43`, `44`, `45`, `46`, `47`, `48`, `49`, `50`, `51`, `52`, `53`, `54`, `55`, `56`, `57`, `58`, `59`, `60`, `61`, `62`, `63`, `64`, `65`, `66`, `67`, `68`, `69`, `70`, `71`, `72`, `73`, `74`, `75`, `76`, `77`, `78`, `79`, `80`, `81`, `82`, `83`, `84`, `85`, `86`, `87`, `88`, `89`, `90`, `91`, `92`, `93`, `94`, `95`, `96`, `97`, `98`) VALUES 
  (1,1,0,0,5,5,1,1,1,1,1,1,1,5,1,1,0,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
  (2,1,0,0,5,5,1,1,1,1,1,1,1,5,1,1,0,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);

COMMIT;

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

