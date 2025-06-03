CREATE DATABASE  IF NOT EXISTS `chocofonso` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `chocofonso`;
-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: chocofonso
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `registration_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `email_verified` tinyint(1) DEFAULT '0',
  `verification_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verification_token_expiry` timestamp NULL DEFAULT NULL,
  `password_reset_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_reset_token_expiry` timestamp NULL DEFAULT NULL,
  `first_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_city` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_postal_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billing_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billing_city` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billing_postal_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_password_reset` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `failed_login_attempts` int DEFAULT '0',
  `lockout_until` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'alfonsorodriguez235@gmail.com','$argon2id$v=19$m=65536,t=3,p=4096$Ff5W10cZRRzzN5+ROvO8Ng$B0ZWyx2m5nr0eh2Ai+mjDFgDxEJ7DViJRptsIILaWj4','2025-05-11 16:02:10','2025-06-03 22:17:16',1,0,NULL,NULL,NULL,NULL,'Alfonso','Rodriguez','696350289','Valor','Avenida Portugal 62 3b','Pantoja','45290','Avenida Portugal 62 3b','Pantoja','45290','2025-05-11 16:02:10','2025-06-03 22:17:16',NULL,0,NULL),(3,'prueba@u.com','$argon2id$v=19$m=65536,t=3,p=4096$j3WK3pmrpfsj2flVKFpaxQ$KtFM9Pw5UFaT3piu/vljesmuFjUgNrlxGZkZOv2YB40','2025-05-15 11:02:32','2025-06-03 16:31:23',1,0,NULL,NULL,NULL,NULL,'Prueba','Chocofonso','684361999','Valor','Avenida Portugal 62 3b','Pantoja','45290','Avenida Portugal 62 3b','Pantoja','45290','2025-05-15 11:02:32','2025-06-03 16:31:38',NULL,0,NULL),(5,'pruebas@alfonso.com','$argon2id$v=19$m=65536,t=3,p=4096$bCVALFm4YAAZhdDURfDJ5g$iAj/w8M6Xbq+Z8L6KouxGZxF4EKG5FrtvA7PL7QJuDg','2025-05-15 11:26:30','2025-05-15 11:28:12',1,0,NULL,NULL,NULL,NULL,'Pruebas','Alfonsito','666555444',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-15 11:26:30','2025-05-15 11:28:22',NULL,0,NULL),(7,'prueba@usas.com','$argon2id$v=19$m=65536,t=3,p=4096$0kzQx6X+BHAs084dZ/hdqg$eEWFp51oRET8zp5ddViNRIXf6SV3U2w4vOBKlZqJ0PA','2025-05-15 11:40:02',NULL,1,0,NULL,NULL,NULL,NULL,'Pruebitas','Alfonso','666555333','Picafonso',NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-15 11:40:02','2025-06-02 20:02:23',NULL,NULL,NULL),(8,'prueba@us.com','$argon2id$v=19$m=65536,t=3,p=4096$gH4AEYokCa81d0hDJrkYOQ$ZSE6sUmO1GlGM0GoceKP51QAE2TMyCUNLqrr90yAW4E','2025-05-15 12:33:43','2025-05-15 20:56:10',1,0,NULL,NULL,NULL,NULL,'Pepe','Garcia','777555333','','Calle Contubernio 49','Madrid','28223','Calle Contubernio 43','Madrid','28223','2025-05-15 12:33:43','2025-06-01 23:00:00',NULL,0,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-04  0:28:35
