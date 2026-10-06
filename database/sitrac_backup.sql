-- MySQL dump 10.13  Distrib 9.4.0, for Win64 (x86_64)
--
-- Host: localhost    Database: sitrac_auth
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `sitrac_auth`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_auth` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_auth`;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `nombre_completo` varchar(120) NOT NULL,
  `password` varchar(100) NOT NULL,
  `rol` enum('ADMIN','OPERADOR','SOMMA') NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKm2dvbwfge291euvmk6vkkocao` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_clientes`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_clientes` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_clientes`;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `nombre_razon_social` varchar(150) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `tipo_documento` enum('CE','DNI','OTRO','RUC') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clientes_numero_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,_binary '','Trujillo, La Libertad','logistica@tecnicaavicola.com','Técnica Avícola S.A.','20123456789','044123456','RUC');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_pedidos`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_pedidos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_pedidos`;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente_id` bigint NOT NULL,
  `descripcion_carga` varchar(200) DEFAULT NULL,
  `destino` varchar(150) NOT NULL,
  `estado` enum('CANCELADO','EN_VIAJE','FINALIZADO','PROGRAMADO','REGISTRADO') NOT NULL,
  `fecha_solicitud` date NOT NULL,
  `origen` varchar(150) NOT NULL,
  `tipo_carga` enum('CAL_GRANEL','CARGA_ANCHA','CEMENTO_BOLSA','ESPECIAL','MAQUINARIA','OTRO') NOT NULL,
  `toneladas` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (1,1,'Transporte de cal a granel','Trujillo','REGISTRADO','2026-10-03','Pacasmayo','CAL_GRANEL',35.00),(2,1,'Pedido de prueba para integración','Trujillo','REGISTRADO','2026-10-03','Pacasmayo','CEMENTO_BOLSA',30.00);
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_conductores`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_conductores` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_conductores`;

--
-- Table structure for table `conductores`
--

DROP TABLE IF EXISTS `conductores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conductores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `categoria_licencia` varchar(20) NOT NULL,
  `disponible` bit(1) NOT NULL,
  `dni` varchar(8) NOT NULL,
  `fecha_vencimiento_licencia` date NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `numero_licencia` varchar(20) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conductores_dni` (`dni`),
  UNIQUE KEY `uk_conductores_licencia` (`numero_licencia`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conductores`
--

LOCK TABLES `conductores` WRITE;
/*!40000 ALTER TABLE `conductores` DISABLE KEYS */;
INSERT INTO `conductores` VALUES (1,_binary '','Ramirez Torres','A-IIIB',_binary '','45879632','2027-12-31','Juan Carlos','Q45879632','987654321'),(2,_binary '','Mendoza Ruiz','A-IIIB',_binary '','47896521','2028-12-31','Luis Alberto','Q47896521','986543210');
/*!40000 ALTER TABLE `conductores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_flota`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_flota` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_flota`;

--
-- Table structure for table `semirremolques`
--

DROP TABLE IF EXISTS `semirremolques`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semirremolques` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `capacidad_toneladas` decimal(10,2) NOT NULL,
  `estado` enum('ASIGNADO','DISPONIBLE','INACTIVO','MANTENIMIENTO') NOT NULL,
  `placa` varchar(10) NOT NULL,
  `tipo` enum('BOMBONA','CAMA_BAJA','OTRO','PLATAFORMA') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_semirremolques_placa` (`placa`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semirremolques`
--

LOCK TABLES `semirremolques` WRITE;
/*!40000 ALTER TABLE `semirremolques` DISABLE KEYS */;
INSERT INTO `semirremolques` VALUES (1,_binary '',35.00,'DISPONIBLE','R8B-614','BOMBONA');
/*!40000 ALTER TABLE `semirremolques` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tractos`
--

DROP TABLE IF EXISTS `tractos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tractos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `anio` int NOT NULL,
  `capacidad_toneladas` decimal(10,2) NOT NULL,
  `estado` enum('ASIGNADO','DISPONIBLE','INACTIVO','MANTENIMIENTO') NOT NULL,
  `marca` varchar(60) NOT NULL,
  `modelo` varchar(60) NOT NULL,
  `placa` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tractos_placa` (`placa`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tractos`
--

LOCK TABLES `tractos` WRITE;
/*!40000 ALTER TABLE `tractos` DISABLE KEYS */;
INSERT INTO `tractos` VALUES (1,_binary '',2022,35.00,'DISPONIBLE','Volvo','FH','T3A-921');
/*!40000 ALTER TABLE `tractos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_programaciones`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_programaciones` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_programaciones`;

--
-- Table structure for table `programaciones`
--

DROP TABLE IF EXISTS `programaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `programaciones` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conductor_id` bigint NOT NULL,
  `estado` enum('CANCELADA','PROGRAMADA') NOT NULL,
  `fecha_programada` datetime(6) NOT NULL,
  `observacion` varchar(300) DEFAULT NULL,
  `pedido_id` bigint NOT NULL,
  `semirremolque_id` bigint NOT NULL,
  `tracto_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programaciones`
--

LOCK TABLES `programaciones` WRITE;
/*!40000 ALTER TABLE `programaciones` DISABLE KEYS */;
INSERT INTO `programaciones` VALUES (1,1,'PROGRAMADA','2026-10-04 08:00:00.000000','Programación inicial para transporte de cal a granel',1,1,1);
/*!40000 ALTER TABLE `programaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_viajes`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_viajes` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_viajes`;

--
-- Table structure for table `viajes`
--

DROP TABLE IF EXISTS `viajes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `viajes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `estado` enum('CANCELADO','EN_VIAJE','FINALIZADO','PROGRAMADO') NOT NULL,
  `fecha_fin` datetime(6) DEFAULT NULL,
  `fecha_inicio` datetime(6) DEFAULT NULL,
  `kilometraje_final` decimal(12,2) DEFAULT NULL,
  `kilometraje_inicial` decimal(12,2) DEFAULT NULL,
  `observacion` varchar(500) DEFAULT NULL,
  `programacion_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_viajes_programacion` (`programacion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `viajes`
--

LOCK TABLES `viajes` WRITE;
/*!40000 ALTER TABLE `viajes` DISABLE KEYS */;
INSERT INTO `viajes` VALUES (1,'PROGRAMADO',NULL,'2026-10-04 08:30:00.000000',NULL,125430.50,'Viaje correspondiente al transporte de cal a granel',1);
/*!40000 ALTER TABLE `viajes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_mantenimiento`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_mantenimiento` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_mantenimiento`;

--
-- Table structure for table `mantenimientos`
--

DROP TABLE IF EXISTS `mantenimientos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimientos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `costo` decimal(12,2) DEFAULT NULL,
  `descripcion` varchar(500) NOT NULL,
  `estado` enum('CANCELADO','EN_PROCESO','FINALIZADO','PROGRAMADO') NOT NULL,
  `fecha_fin` date DEFAULT NULL,
  `fecha_inicio` date NOT NULL,
  `tipo_mantenimiento` enum('CORRECTIVO','PREVENTIVO') NOT NULL,
  `tipo_unidad` enum('SEMIRREMOLQUE','TRACTO') NOT NULL,
  `unidad_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimientos`
--

LOCK TABLES `mantenimientos` WRITE;
/*!40000 ALTER TABLE `mantenimientos` DISABLE KEYS */;
INSERT INTO `mantenimientos` VALUES (1,850.00,'Mantenimiento preventivo general del tracto','PROGRAMADO','2026-10-05','2026-10-05','PREVENTIVO','TRACTO',1);
/*!40000 ALTER TABLE `mantenimientos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_somma`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_somma` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_somma`;

--
-- Table structure for table `registros_somma`
--

DROP TABLE IF EXISTS `registros_somma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registros_somma` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conductor_id` bigint DEFAULT NULL,
  `descripcion` varchar(1000) NOT NULL,
  `estado` enum('CANCELADO','CERRADO','REGISTRADO') NOT NULL,
  `fecha` datetime(6) NOT NULL,
  `lugar` varchar(150) DEFAULT NULL,
  `tipo` enum('ACCIDENTE','CAPACITACION','CHARLA','INCIDENTE','INSPECCION') NOT NULL,
  `titulo` varchar(150) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registros_somma`
--

LOCK TABLES `registros_somma` WRITE;
/*!40000 ALTER TABLE `registros_somma` DISABLE KEYS */;
INSERT INTO `registros_somma` VALUES (1,1,'Charla preventiva sobre conducción segura, revisión de unidad y riesgos en carretera.','REGISTRADO','2026-10-04 07:30:00.000000','Base Trujillo','CHARLA','Charla de seguridad antes de ruta');
/*!40000 ALTER TABLE `registros_somma` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `sitrac_combustible`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sitrac_combustible` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sitrac_combustible`;

--
-- Table structure for table `abastecimientos_combustible`
--

DROP TABLE IF EXISTS `abastecimientos_combustible`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `abastecimientos_combustible` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cantidad_galones` decimal(12,3) NOT NULL,
  `conductor_id` bigint NOT NULL,
  `costo_total` decimal(14,2) DEFAULT NULL,
  `fecha_hora` datetime(6) NOT NULL,
  `kilometraje` decimal(12,2) NOT NULL,
  `numero_comprobante` varchar(80) DEFAULT NULL,
  `observacion` varchar(500) DEFAULT NULL,
  `precio_unitario` decimal(12,4) DEFAULT NULL,
  `programacion_id` bigint NOT NULL,
  `proveedor` varchar(150) DEFAULT NULL,
  `tanque_origen` varchar(100) DEFAULT NULL,
  `tipo_abastecimiento` enum('INTERNO','TERCERO') NOT NULL,
  `tracto_id` bigint NOT NULL,
  `viaje_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `abastecimientos_combustible`
--

LOCK TABLES `abastecimientos_combustible` WRITE;
/*!40000 ALTER TABLE `abastecimientos_combustible` DISABLE KEYS */;
/*!40000 ALTER TABLE `abastecimientos_combustible` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-03 18:08:25
