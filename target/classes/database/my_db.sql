-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema webproject
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `webproject` ;

-- -----------------------------------------------------
-- Schema webproject
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `webproject` DEFAULT CHARACTER SET utf8 ;
USE `webproject` ;

-- -----------------------------------------------------
-- Table `webproject`.`periodicals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `webproject`.`periodicals` ;

CREATE TABLE IF NOT EXISTS `webproject`.`periodicals` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `category` VARCHAR(45) NULL DEFAULT NULL,
  `publisher` VARCHAR(45) NULL DEFAULT NULL,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `one_month_cost` BIGINT(20) NULL DEFAULT NULL,
  `status` ENUM('active', 'inactive', 'discarded') NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `name_UNIQUE` ON `webproject`.`periodicals` (`name` ASC);


-- -----------------------------------------------------
-- Table `webproject`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `webproject`.`users` ;

CREATE TABLE IF NOT EXISTS `webproject`.`users` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `first_name` VARCHAR(64) NULL DEFAULT NULL,
  `last_name` VARCHAR(64) NULL DEFAULT NULL,
  `email` VARCHAR(128) NOT NULL,
  `address` VARCHAR(100) NULL DEFAULT NULL,
  `password` VARCHAR(64) NOT NULL,
  `status` ENUM('active', 'blocked') NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `email` ON `webproject`.`users` (`email` ASC);

CREATE UNIQUE INDEX `username_UNIQUE` ON `webproject`.`users` (`username` ASC);


-- -----------------------------------------------------
-- Table `webproject`.`invoices`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `webproject`.`invoices` ;

CREATE TABLE IF NOT EXISTS `webproject`.`invoices` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `periodical_id` BIGINT(20) NOT NULL,
  `period` INT(11) NULL DEFAULT NULL,
  `total_sum` BIGINT(20) NULL DEFAULT NULL,
  `creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `payment_date` TIMESTAMP NULL DEFAULT NULL,
  `status` ENUM('new', 'paid') NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `p_invoice_fk`
  FOREIGN KEY (`periodical_id`)
  REFERENCES `webproject`.`periodicals` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `u_invoice_fk`
  FOREIGN KEY (`user_id`)
  REFERENCES `webproject`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARACTER SET = utf8;

CREATE INDEX `u_invoice_fk_idx` ON `webproject`.`invoices` (`user_id` ASC);

CREATE INDEX `p_invoice_fk_idx` ON `webproject`.`invoices` (`periodical_id` ASC);


-- -----------------------------------------------------
-- Table `webproject`.`subscriptions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `webproject`.`subscriptions` ;

CREATE TABLE IF NOT EXISTS `webproject`.`subscriptions` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `periodical_id` BIGINT(20) NOT NULL,
  `delivery_address` VARCHAR(100) NULL DEFAULT NULL,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` ENUM('active', 'inactive') NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `periodicalId_fk`
  FOREIGN KEY (`periodical_id`)
  REFERENCES `webproject`.`periodicals` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `userId_fk`
  FOREIGN KEY (`user_id`)
  REFERENCES `webproject`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARACTER SET = utf8;

CREATE INDEX `periodicalId_fk_idx` ON `webproject`.`subscriptions` (`periodical_id` ASC);

CREATE INDEX `userId_fk_idx` ON `webproject`.`subscriptions` (`user_id` ASC);


-- -----------------------------------------------------
-- Table `webproject`.`user_roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `webproject`.`user_roles` ;

CREATE TABLE IF NOT EXISTS `webproject`.`user_roles` (
  `user_id` BIGINT(11) NOT NULL,
  `name` ENUM('admin', 'subscriber') NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `webproject`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

CREATE INDEX `user_id_idx` ON `webproject`.`user_roles` (`user_id` ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
