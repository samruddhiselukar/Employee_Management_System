#employee_db schema
#---!Ups

CREATE TABLE IF NOT EXISTS `employee_db`.`employee_table` (
 `companyID` INT(11) NOT NULL,
 `employeeID` INT(11) NOT NULL AUTO_INCREMENT,
 `firstName` VARCHAR(45) NULL DEFAULT NULL,
 `lastName` VARCHAR(45) NULL DEFAULT NULL,
 `email` VARCHAR(45) NULL DEFAULT NULL,
 `designation` VARCHAR(45) NULL DEFAULT NULL,
 PRIMARY KEY (`employeeID`),
 UNIQUE KEY UK_email (`email`))
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;

#---!Downs

DROP TABLE `employee_table`;