-- MySQL dump 10.13  Distrib 9.6.0, for Win64 (x86_64)
--
-- Host: localhost    Database: sitrac_auth
-- ------------------------------------------------------
-- Server version	9.6.0

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

/*!40000 DROP DATABASE IF EXISTS `sitrac_auth`*/;

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
  `rol` enum('ADMIN','MANTENIMIENTO','OPERADOR','SOMMA','SUPERVISOR') NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKm2dvbwfge291euvmk6vkkocao` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,_binary '\0','Administrador SITRAC','$2a$10$Co5Y.y6uzPx2ByH6twEUGuYxlVjEa6FurlFEH5RNlG0AntNfQoyvy','ADMIN','admin'),(2,_binary '','Administrador SITRAC','$2a$10$gEEvtgmQX7OLA9BTScFTQeTj4MhShKHQepDP0aPqRHXkJuAqvV6ju','ADMIN','admin.sitrac'),(3,_binary '','Operador de Transporte','$2a$10$l6WZ/TR/teCVgT0U4nIiYe1XIz63ZtA.HXKC8a/goUiT93cWfq.PS','OPERADOR','operador.sitrac'),(4,_binary '','Responsable SOMMA','$2a$10$GLpckELCc7sG5uZVvz7UYu6CD1jaNZI4It4EA/4Lhp/bSpwKJIo.6','SOMMA','somma.sitrac'),(5,_binary '','Responsable de Mantenimiento','$2a$10$ghGgAme1UiqFG0t.dJr5aeSDGm4/SidZLWt26ZtyyhMnJxt5yhhpa','MANTENIMIENTO','mantenimiento.sitrac'),(6,_binary '','Supervisor de Operaciones','$2a$10$yvPR7pfzJAwTtqCO0ujIm.8O9ShuHpQoztOvDur81EYqO/Ra7oNbK','SUPERVISOR','supervisor.sitrac');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_auth'
--

--
-- Dumping routines for database 'sitrac_auth'
--

--
-- Current Database: `sitrac_clientes`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_clientes`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (3,_binary '','Carretera Panamericana Norte Km 666, Pacasmayo, La Libertad','contacto@cpsaa.com.pe','CEMENTOS PACASMAYO S.A.A.','20419387658','(01) 317-6000','RUC'),(4,_binary '','Jr. Cajamarquilla 1241, Zarate, San Juan de Lurigancho, Lima','','ARCA CONTINENTAL LINDLEY S.A.','20101024645','(01) 319-4000','RUC'),(5,_binary '','Av. Santiago Antunez de Mayolo s/n, Zona Industrial, Chimbote, Ancash','gestionterceros@sider.com','EMPRESA SIDERURGICA DEL PERU S.A.A.','20402885549','(043) 483000','RUC'),(6,_binary '','Av. Argentina 4793, Carmen de la Legua Reynoso, Callao','atencionconsumidor@alicorp.com.pe','ALICORP S.A.A.','20100055237','(01) 315-0800','RUC'),(7,_binary '','Calle Carpaccio 250, San Borja, Lima','atencionalcliente@solgas.com.pe','SOLGAS S.A.','20100176450','(01) 613-3330','RUC'),(8,_binary '','Calle Bernini 149, San Borja, Lima',NULL,'LIMA GAS S.A.','20100007348','(01) 634-0000','RUC'),(9,_binary '','Carretera Desvio Otuzco-Huamachuco Km 141, Quiruvilca, La Libertad','informes@lagunasnorte.com','MINERA BOROO MISQUICHILCA S.A.','20209133394','(044) 604300','RUC'),(10,_binary '','Av. Gonzalo Ugas 29, Pacasmayo, La Libertad',NULL,'TECNICA AVICOLA S.A.','20505120702',NULL,'RUC'),(11,_binary '','Car. Panamericana Sur Nro. 241 Panamericana Sur','logistica@acerosarequipa.pe','CORPORACION ACEROS AREQUIPA S.A.','20370146994','951741123','RUC');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_clientes'
--

--
-- Dumping routines for database 'sitrac_clientes'
--

--
-- Current Database: `sitrac_pedidos`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_pedidos`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (2,1,'Pedido de prueba para integración','Trujillo','PROGRAMADO','2026-10-03','Pacasmayo','CEMENTO_BOLSA',30.00),(4,3,'Cal a granel para distribución industrial','Trujillo, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(5,3,'Cemento embolsado para distribución','Chiclayo, Lambayeque','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CEMENTO_BOLSA',35.00),(6,10,'Carga a granel para operación avícola','Trujillo, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(7,10,'Segundo despacho de carga a granel','Virú, La Libertad','PROGRAMADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(8,5,'Estructuras metálicas de gran dimensión','Trujillo, La Libertad','PROGRAMADO','2026-10-05','Chimbote, Ancash','CARGA_ANCHA',35.00),(9,6,'Productos de consumo masivo paletizados','Trujillo, La Libertad','REGISTRADO','2026-10-05','Callao, Lima','OTRO',30.00),(10,4,'Productos terminados para centro de distribución','Trujillo, La Libertad','REGISTRADO','2026-10-05','Lima','OTRO',28.00),(11,7,'Equipamiento para operación de GLP','Trujillo, La Libertad','REGISTRADO','2026-10-05','Lima','ESPECIAL',32.00),(12,8,'Equipamiento industrial para planta','Chiclayo, Lambayeque','REGISTRADO','2026-10-05','Lima','ESPECIAL',30.00),(13,9,'Maquinaria pesada para operación minera','Quiruvilca, La Libertad','REGISTRADO','2026-10-05','Trujillo, La Libertad','MAQUINARIA',35.00),(14,3,'Cal a granel para distribución industrial','Trujillo, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(15,3,'Cemento embolsado para distribución','Chiclayo, Lambayeque','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CEMENTO_BOLSA',35.00),(16,10,'Carga a granel para operación avícola','Trujillo, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(17,10,'Segundo despacho de carga a granel','Virú, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(18,5,'Estructuras metálicas de gran dimensión','Trujillo, La Libertad','REGISTRADO','2026-10-05','Chimbote, Ancash','CARGA_ANCHA',35.00),(19,6,'Productos de consumo masivo paletizados','Trujillo, La Libertad','REGISTRADO','2026-10-05','Callao, Lima','OTRO',30.00),(20,4,'Productos terminados para centro de distribución','Trujillo, La Libertad','REGISTRADO','2026-10-05','Lima','OTRO',28.00),(21,7,'Equipamiento para operación de GLP','Trujillo, La Libertad','REGISTRADO','2026-10-05','Lima','ESPECIAL',32.00),(22,8,'Equipamiento industrial para planta','Chiclayo, Lambayeque','REGISTRADO','2026-10-05','Lima','ESPECIAL',30.00),(23,9,'Maquinaria pesada para operación minera','Quiruvilca, La Libertad','REGISTRADO','2026-10-05','Trujillo, La Libertad','MAQUINARIA',35.00),(24,3,'Cal a granel para distribución industrial','Trujillo, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(25,3,'Cemento embolsado para distribución','Chiclayo, Lambayeque','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CEMENTO_BOLSA',35.00),(26,10,'Carga a granel para operación avícola','Trujillo, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(27,10,'Segundo despacho de carga a granel','Virú, La Libertad','REGISTRADO','2026-10-05','Pacasmayo, La Libertad','CAL_GRANEL',35.00),(28,5,'Estructuras metálicas de gran dimensión','Trujillo, La Libertad','REGISTRADO','2026-10-05','Chimbote, Ancash','CARGA_ANCHA',35.00),(29,6,'Productos de consumo masivo paletizados','Trujillo, La Libertad','REGISTRADO','2026-10-05','Callao, Lima','OTRO',30.00),(30,4,'Productos terminados para centro de distribución','Trujillo, La Libertad','REGISTRADO','2026-10-05','Lima','OTRO',28.00),(31,7,'Equipamiento para operación de GLP','Trujillo, La Libertad','REGISTRADO','2026-10-05','Lima','ESPECIAL',32.00),(32,8,'Equipamiento industrial para planta','Chiclayo, Lambayeque','REGISTRADO','2026-10-05','Lima','ESPECIAL',30.00),(33,9,'Maquinaria pesada para operación minera','Quiruvilca, La Libertad','REGISTRADO','2026-10-05','Trujillo, La Libertad','MAQUINARIA',35.00),(34,6,'MERCADERIAS VARIAS','LIMA','REGISTRADO','2026-10-06','TRUJILLO','CEMENTO_BOLSA',36.00),(35,7,'TRANSPORTE EN CISTERNAS','Lima, Lima','REGISTRADO','2026-10-06','Trujillo, La Libertad','OTRO',22.00),(36,11,'CARGA ANCHA','Lima, Lima','REGISTRADO','2026-10-07','Trujillo, La Libertad','MAQUINARIA',20.00);
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_pedidos'
--

--
-- Dumping routines for database 'sitrac_pedidos'
--

--
-- Current Database: `sitrac_conductores`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_conductores`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conductores`
--

LOCK TABLES `conductores` WRITE;
/*!40000 ALTER TABLE `conductores` DISABLE KEYS */;
INSERT INTO `conductores` VALUES (1,_binary '','Ramirez Torres','A-IIIB',_binary '\0','45879632','2027-12-31','Juan Carlos','Q45879632','987654321'),(2,_binary '','Mendoza Ruiz','A-IIIB',_binary '\0','47896521','2028-12-31','Luis Alberto','Q47896521','986543210'),(3,_binary '','CALDERON ESPINOLA','AIIIC',_binary '\0','46258317','2028-05-23','YANCARLOS','Q46258317','900000001'),(4,_binary '','ROJAS MENDOZA','AIIIC',_binary '','47192684','2029-03-15','MIGUEL ALEJANDRO','Q47192684','900000002'),(5,_binary '','VARGAS PEREZ','AIIIC',_binary '','43971825','2028-11-20','CESAR AUGUSTO','Q43971825','900000003'),(6,_binary '','TORRES RAMIREZ','AIIIC',_binary '\0','00000004','2029-07-08','LUIS ALBERTO','LIC-DEMO-004','900000004'),(7,_binary '','MENDOZA FLORES','AIIIC',_binary '\0','00000005','2030-01-12','JORGE LUIS','LIC-DEMO-005','900000005'),(8,_binary '','GUTIERREZ SILVA','AIIIC',_binary '','00000006','2028-09-30','JOSE CARLOS','LIC-DEMO-006','900000006'),(9,_binary '','SALAZAR VEGA','AIIIC',_binary '','00000007','2029-06-18','MARCO ANTONIO','LIC-DEMO-007','900000007'),(10,_binary '','CASTILLO CRUZ','AIIIC',_binary '','00000008','2028-12-05','RAUL EDUARDO','LIC-DEMO-008','900000008'),(11,_binary '','DIAZ HERRERA','AIIIC',_binary '','00000009','2030-04-22','PEDRO MIGUEL','LIC-DEMO-009','900000009'),(12,_binary '','PAREDES LEON','AIIIC',_binary '','00000010','2029-10-14','RICARDO JAVIER','LIC-DEMO-010','900000010');
/*!40000 ALTER TABLE `conductores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_conductores'
--

--
-- Dumping routines for database 'sitrac_conductores'
--

--
-- Current Database: `sitrac_flota`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_flota`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semirremolques`
--

LOCK TABLES `semirremolques` WRITE;
/*!40000 ALTER TABLE `semirremolques` DISABLE KEYS */;
INSERT INTO `semirremolques` VALUES (1,_binary '',35.00,'DISPONIBLE','R8B-614','BOMBONA'),(2,_binary '',35.00,'ASIGNADO','SRP-782','PLATAFORMA'),(3,_binary '',35.00,'DISPONIBLE','S1A-301','BOMBONA'),(4,_binary '',35.00,'ASIGNADO','S1A-302','BOMBONA'),(5,_binary '',35.00,'DISPONIBLE','S1A-303','BOMBONA'),(6,_binary '',35.00,'ASIGNADO','S1A-304','PLATAFORMA'),(7,_binary '',35.00,'DISPONIBLE','S1A-305','PLATAFORMA'),(8,_binary '',35.00,'DISPONIBLE','S1A-306','PLATAFORMA'),(9,_binary '',35.00,'DISPONIBLE','S1A-307','PLATAFORMA'),(10,_binary '',35.00,'DISPONIBLE','S1A-308','CAMA_BAJA'),(11,_binary '',35.00,'ASIGNADO','S1A-309','CAMA_BAJA'),(12,_binary '',35.00,'DISPONIBLE','S1A-310','CAMA_BAJA');
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tractos`
--

LOCK TABLES `tractos` WRITE;
/*!40000 ALTER TABLE `tractos` DISABLE KEYS */;
INSERT INTO `tractos` VALUES (1,_binary '',2022,35.00,'DISPONIBLE','Volvo','FH','T3A-921'),(2,_binary '',2023,35.00,'DISPONIBLE','Volvo','FH','T4B-782'),(3,_binary '',2022,35.00,'ASIGNADO','VOLVO','FH 540','T1A-201'),(4,_binary '',2021,35.00,'ASIGNADO','VOLVO','FH 500','T1A-202'),(5,_binary '',2022,35.00,'DISPONIBLE','SCANIA','R450','T1A-203'),(6,_binary '',2023,35.00,'ASIGNADO','SCANIA','R500','T1A-204'),(7,_binary '',2021,35.00,'DISPONIBLE','FREIGHTLINER','CASCADIA','T1A-205'),(8,_binary '',2022,35.00,'DISPONIBLE','FREIGHTLINER','CASCADIA','T1A-206'),(9,_binary '',2020,35.00,'DISPONIBLE','INTERNATIONAL','LT625','T1A-207'),(10,_binary '',2021,35.00,'DISPONIBLE','MACK','ANTHEM','T1A-208'),(11,_binary '',2020,35.00,'DISPONIBLE','VOLVO','FH 460','T1A-209'),(12,_binary '',2020,35.00,'MANTENIMIENTO','SCANIA','G440','T1A-210');
/*!40000 ALTER TABLE `tractos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_flota'
--

--
-- Dumping routines for database 'sitrac_flota'
--

--
-- Current Database: `sitrac_programaciones`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_programaciones`*/;

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
  `estado` enum('CANCELADA','FINALIZADA','PROGRAMADA') NOT NULL,
  `fecha_programada` datetime(6) NOT NULL,
  `observacion` varchar(300) DEFAULT NULL,
  `pedido_id` bigint NOT NULL,
  `semirremolque_id` bigint NOT NULL,
  `tracto_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programaciones`
--

LOCK TABLES `programaciones` WRITE;
/*!40000 ALTER TABLE `programaciones` DISABLE KEYS */;
INSERT INTO `programaciones` VALUES (1,1,'PROGRAMADA','2026-10-04 08:00:00.000000','Programación inicial para transporte de cal a granel',1,1,1),(4,2,'PROGRAMADA','2026-10-04 12:00:00.000000','Prueba de asignación automática de flota',3,2,2),(5,3,'PROGRAMADA','2026-10-06 02:52:00.000000','750 BOLSA DE CEMENTO PCY',2,6,4),(6,7,'PROGRAMADA','2026-10-06 03:14:00.000000','TRANSPORTE DE CHATARRA',8,11,3),(7,6,'PROGRAMADA','2026-10-07 03:24:00.000000','TRANSPORTE DE MAIZ A GRANEL',7,4,6);
/*!40000 ALTER TABLE `programaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_programaciones'
--

--
-- Dumping routines for database 'sitrac_programaciones'
--

--
-- Current Database: `sitrac_viajes`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_viajes`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `viajes`
--

LOCK TABLES `viajes` WRITE;
/*!40000 ALTER TABLE `viajes` DISABLE KEYS */;
INSERT INTO `viajes` VALUES (1,'PROGRAMADO',NULL,'2026-10-04 08:30:00.000000',NULL,125430.50,'Viaje correspondiente al transporte de cal a granel',1),(2,'FINALIZADO','2026-10-04 04:30:00.000000','2026-10-04 00:05:00.000000',150245.00,150000.00,'Viaje finalizado correctamente',2),(3,'EN_VIAJE',NULL,'2026-10-06 03:02:00.000000',NULL,2000.00,'',5),(4,'PROGRAMADO',NULL,NULL,NULL,NULL,'',4);
/*!40000 ALTER TABLE `viajes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_viajes'
--

--
-- Dumping routines for database 'sitrac_viajes'
--

--
-- Current Database: `sitrac_mantenimiento`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_mantenimiento`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimientos`
--

LOCK TABLES `mantenimientos` WRITE;
/*!40000 ALTER TABLE `mantenimientos` DISABLE KEYS */;
INSERT INTO `mantenimientos` VALUES (1,850.00,'Mantenimiento preventivo general del tracto','PROGRAMADO','2026-10-05','2026-10-05','PREVENTIVO','TRACTO',1),(2,500.00,'Mantenimiento preventivo programado del tracto','PROGRAMADO',NULL,'2026-10-04','PREVENTIVO','TRACTO',2),(3,500.00,'EMBREGUE Y FRENOS','EN_PROCESO','2026-10-08','2026-10-06','CORRECTIVO','TRACTO',12);
/*!40000 ALTER TABLE `mantenimientos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_mantenimiento'
--

--
-- Dumping routines for database 'sitrac_mantenimiento'
--

--
-- Current Database: `sitrac_somma`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_somma`*/;

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
  `programacion_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registros_somma`
--

LOCK TABLES `registros_somma` WRITE;
/*!40000 ALTER TABLE `registros_somma` DISABLE KEYS */;
INSERT INTO `registros_somma` VALUES (1,1,'Charla preventiva sobre conducción segura, revisión de unidad y riesgos en carretera.','REGISTRADO','2026-10-04 07:30:00.000000','Base Trujillo','CHARLA','Charla de seguridad antes de ruta',NULL),(2,2,'Charla SOMMA realizada antes de iniciar la operación programada.','REGISTRADO','2026-10-03 22:55:00.000000','Base Trujillo','CHARLA','Charla de seguridad previa al viaje',2),(3,3,'CHARLA ORIENTADO AL MANEJO SEGURO Y DEFENSIVO','CERRADO','2026-10-06 02:48:00.000000','TRUJILLO','CHARLA','MANEJO DEFENSIVO',5),(4,2,'CHARLA HECHO POR EL ING RUIZ MILLONES','REGISTRADO','2026-10-06 03:20:00.000000','Base Cajamarca','CAPACITACION','MANEJO DEFENSIVO',4);
/*!40000 ALTER TABLE `registros_somma` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_somma'
--

--
-- Dumping routines for database 'sitrac_somma'
--

--
-- Current Database: `sitrac_combustible`
--

/*!40000 DROP DATABASE IF EXISTS `sitrac_combustible`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `abastecimientos_combustible`
--

LOCK TABLES `abastecimientos_combustible` WRITE;
/*!40000 ALTER TABLE `abastecimientos_combustible` DISABLE KEYS */;
INSERT INTO `abastecimientos_combustible` VALUES (1,35.000,2,542.50,'2026-10-03 23:55:00.000000',150000.00,NULL,'Abastecimiento interno para programación 2',15.5000,2,NULL,'TANQUE-01','INTERNO',2,NULL),(2,100.000,3,NULL,'2026-10-07 03:02:00.000000',2000.00,NULL,NULL,NULL,5,NULL,'BASE TRUJILLO','INTERNO',4,3);
/*!40000 ALTER TABLE `abastecimientos_combustible` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sitrac_combustible'
--

--
-- Dumping routines for database 'sitrac_combustible'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-06  6:50:57
