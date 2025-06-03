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
-- Table structure for table `producto_imagenes`
--

DROP TABLE IF EXISTS `producto_imagenes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_imagenes` (
  `id_imagen` int NOT NULL AUTO_INCREMENT,
  `id_producto` int NOT NULL,
  `url_imagen` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `es_principal` tinyint(1) DEFAULT '0',
  `orden` int DEFAULT '0',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_imagen`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `producto_imagenes_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto_imagenes`
--

LOCK TABLES `producto_imagenes` WRITE;
/*!40000 ALTER TABLE `producto_imagenes` DISABLE KEYS */;
INSERT INTO `producto_imagenes` VALUES (42,26,'https://static.carrefour.es/hd_1500x_/img_pim_food/218896_00_1.jpg',1,0,'2025-05-31 23:45:48'),(43,27,'https://static.carrefour.es/hd_1500x_/img_pim_food/972437_00_1.jpg',1,0,'2025-05-31 23:50:22'),(44,28,'https://static.carrefour.es/hd_510x_/img_pim_food/886759_00_1.jpg',1,0,'2025-05-31 23:51:37'),(46,30,'https://static.carrefour.es/hd_510x_/img_pim_food/831830_00_1.jpg',1,0,'2025-05-31 23:54:36'),(47,29,'https://www.valor.es/wp-content/uploads/2020/08/99.png',1,0,'2025-05-31 23:54:41'),(55,34,'https://www.golosinasysnacks.com/13486-medium_default/kit-kat-36-unidades.jpg',0,1,'2025-06-01 16:36:15'),(56,34,'https://www.golosinasysnacks.com/14818-medium_default/kit-kat-36-unidades.jpg',1,0,'2025-06-01 16:36:15'),(57,34,'https://www.golosinasysnacks.com/13487-medium_default/kit-kat-36-unidades.jpg',0,2,'2025-06-01 16:36:15'),(59,35,'https://sgfm.elcorteingles.es/SGFM/dctm/MEDIA03/202309/27/00120642901555____11__1200x1200.jpg',1,0,'2025-06-01 16:39:32'),(60,32,'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcRO1UbFuIpJpj3zR4PEYdSrfEUVz9LMZNuPuZc5azgx-dF67EQbnQHN13Bvuf7U1i93lL2qCLrWEm5axaRam3wTcEbmM-W169NKlvmr7dZ_h-m-EhMjZL9k',1,1,'2025-06-01 16:40:08'),(61,32,'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcT46VPfmK0XwCFeYlElftafCsPDit-II6T8YX_7KVxWZoYssSI7PELcgHfCohl-vBMe9nx-AhGNvyLNbubwBvVAC-mZrmzwXkjG9VaMKLhJVsbp9UjwcGGN',0,0,'2025-06-01 16:40:08'),(62,33,'https://sgfm.elcorteingles.es/SGFM/dctm/MEDIA03/202110/13/00120643001991____1__1200x1200.jpg',1,0,'2025-06-01 16:40:18'),(63,36,'https://mesadelsur.com/cdn/shop/files/jungly2_1024x1024@2x.jpg?v=1705590666',1,0,'2025-06-01 16:44:20'),(64,37,'https://m.media-amazon.com/images/I/51Gbfgu8-jL._AC_SX425_.jpg',1,0,'2025-06-02 21:55:19'),(65,38,'https://sgfm.elcorteingles.es/SGFM/dctm/MEDIA03/202504/16/00120646701951____1__1200x1200.jpg',1,0,'2025-06-02 21:57:42'),(66,39,'https://sgfm.elcorteingles.es/SGFM/dctm/MEDIA03/202208/17/00120642900565____12__1200x1200.jpg',1,0,'2025-06-02 21:59:46'),(67,40,'https://m.media-amazon.com/images/I/61HYnBxaLHL._AC_SX425_.jpg',0,2,'2025-06-02 22:02:39'),(68,40,'https://m.media-amazon.com/images/I/61DCLfRN1vL._AC_SX425_.jpg',0,3,'2025-06-02 22:02:39'),(69,40,'https://m.media-amazon.com/images/I/61DIOrdXENL._AC_SX425_.jpg',0,4,'2025-06-02 22:02:39'),(70,40,'https://m.media-amazon.com/images/I/61fHoln7duL._AC_SX425_.jpg',1,0,'2025-06-02 22:02:39'),(71,40,'https://m.media-amazon.com/images/I/81KKnoH9DML._AC_SX569_.jpg',0,1,'2025-06-02 22:02:39'),(72,41,'https://static.carrefour.es/hd_1500x_/img_pim_food/800240_00_3.jpg',0,2,'2025-06-02 22:05:11'),(73,41,'https://static.carrefour.es/hd_1500x_/img_pim_food/800240_00_4.jpg',0,3,'2025-06-02 22:05:11'),(74,41,'https://static.carrefour.es/hd_1500x_/img_pim_food/800240_00_1.jpg',1,0,'2025-06-02 22:05:11'),(75,41,'https://static.carrefour.es/hd_1500x_/img_pim_food/800240_00_2.jpg',0,1,'2025-06-02 22:05:11');
/*!40000 ALTER TABLE `producto_imagenes` ENABLE KEYS */;
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
