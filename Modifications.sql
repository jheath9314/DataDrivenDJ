ALTER TABLE `datadrivendj`.`song`
CHANGE COLUMN `lyrics` `lyrics` MEDIUMTEXT NOT NULL DEFAULT NULL;
ALTER TABLE `datadrivendj`.`song`
ADD FULLTEXT INDEX `new` (`lyrics` ASC);
