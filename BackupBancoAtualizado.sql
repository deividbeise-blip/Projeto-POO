-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: mysql-4eaccc0-projeto-poo.k.aivencloud.com    Database: projeto_poo
-- ------------------------------------------------------
-- Server version	8.4.11

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '7946664e-d3af-11f1-82a8-52943b7d2bbb:1-116';

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `name_cliente` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `cpf` varchar(11) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(15) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `name_cliente` (`name_cliente`),
  UNIQUE KEY `cpf` (`cpf`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concessionaria`
--

DROP TABLE IF EXISTS `concessionaria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `concessionaria` (
  `id_concessionaria` int NOT NULL AUTO_INCREMENT,
  `name_concessionaria` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `address_concessionaria` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id_concessionaria`),
  UNIQUE KEY `name_concessionaria` (`name_concessionaria`),
  UNIQUE KEY `address_concessionaria` (`address_concessionaria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pagamento`
--

DROP TABLE IF EXISTS `pagamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamento` (
  `id_pagamento` int NOT NULL AUTO_INCREMENT,
  `forma_pagamento` enum('Dinheiro','Cartao','Pix') COLLATE utf8mb4_general_ci NOT NULL,
  `valor_pagamento` decimal(10,2) NOT NULL,
  `data_pagamento` date NOT NULL,
  `cliente_pagamento` int NOT NULL,
  `veiculo_pagamento` int NOT NULL,
  PRIMARY KEY (`id_pagamento`),
  KEY `cliente_pagamento` (`cliente_pagamento`),
  KEY `veiculo_pagamento` (`veiculo_pagamento`),
  CONSTRAINT `pagamento_ibfk_1` FOREIGN KEY (`cliente_pagamento`) REFERENCES `cliente` (`id_cliente`) ON UPDATE CASCADE,
  CONSTRAINT `pagamento_ibfk_2` FOREIGN KEY (`veiculo_pagamento`) REFERENCES `veiculo` (`id_veiculo`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pagamentocartao`
--

DROP TABLE IF EXISTS `pagamentocartao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamentocartao` (
  `id_pagamento_cartao` int NOT NULL AUTO_INCREMENT,
  `numero_cartao` varchar(16) COLLATE utf8mb4_general_ci NOT NULL,
  `nomeTitular` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `validade_cartao` date NOT NULL,
  `codigo_seguranca` varchar(4) COLLATE utf8mb4_general_ci NOT NULL,
  `numero_parcelas` int NOT NULL,
  `valor_parcela` decimal(10,2) NOT NULL,
  `pagamento_cartao` int NOT NULL,
  PRIMARY KEY (`id_pagamento_cartao`),
  UNIQUE KEY `pagamento_cartao` (`pagamento_cartao`),
  CONSTRAINT `pagamentocartao_ibfk_1` FOREIGN KEY (`pagamento_cartao`) REFERENCES `pagamento` (`id_pagamento`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pagamentopix`
--

DROP TABLE IF EXISTS `pagamentopix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamentopix` (
  `id_pagamento_pix` int NOT NULL AUTO_INCREMENT,
  `chave_pix` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `tipo_chave_pix` enum('CPF','CNPJ','Email','Telefone') COLLATE utf8mb4_general_ci NOT NULL,
  `pagamento_pix` int NOT NULL,
  PRIMARY KEY (`id_pagamento_pix`),
  UNIQUE KEY `pagamento_pix` (`pagamento_pix`),
  CONSTRAINT `pagamentopix_ibfk_1` FOREIGN KEY (`pagamento_pix`) REFERENCES `pagamento` (`id_pagamento`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `statusveiculo`
--

DROP TABLE IF EXISTS `statusveiculo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statusveiculo` (
  `id_veiculo` int NOT NULL,
  `StatusVeiculo` enum('Disponivel','Vendido','Em_Manutencao') COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id_veiculo`),
  CONSTRAINT `statusveiculo_ibfk_1` FOREIGN KEY (`id_veiculo`) REFERENCES `veiculo` (`id_veiculo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `veiculo`
--

DROP TABLE IF EXISTS `veiculo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veiculo` (
  `id_veiculo` int NOT NULL AUTO_INCREMENT,
  `marca_veiculo` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `modelo_veiculo` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `ano_veiculo` int NOT NULL,
  `placa_veiculo` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `preco_veiculo` decimal(10,2) NOT NULL,
  `status_veiculo` enum('Disponivel','Vendido','Em_Manutencao') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Disponivel',
  `moto` tinyint(1) NOT NULL DEFAULT '0',
  `concessionario_veiculo` int NOT NULL,
  PRIMARY KEY (`id_veiculo`),
  UNIQUE KEY `placa_veiculo` (`placa_veiculo`),
  KEY `concessionario_veiculo` (`concessionario_veiculo`),
  CONSTRAINT `veiculo_ibfk_1` FOREIGN KEY (`concessionario_veiculo`) REFERENCES `concessionaria` (`id_concessionaria`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `venda`
--

DROP TABLE IF EXISTS `venda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venda` (
  `id_venda` int NOT NULL AUTO_INCREMENT,
  `data_venda` date NOT NULL,
  `cliente_venda` int NOT NULL,
  `veiculo_venda` int NOT NULL,
  `vendedor_venda` int NOT NULL,
  `pagamento_venda` int DEFAULT NULL,
  PRIMARY KEY (`id_venda`),
  UNIQUE KEY `pagamento_venda` (`pagamento_venda`),
  KEY `cliente_venda` (`cliente_venda`),
  KEY `veiculo_venda` (`veiculo_venda`),
  KEY `vendedor_venda` (`vendedor_venda`),
  CONSTRAINT `venda_ibfk_1` FOREIGN KEY (`cliente_venda`) REFERENCES `cliente` (`id_cliente`) ON UPDATE CASCADE,
  CONSTRAINT `venda_ibfk_2` FOREIGN KEY (`veiculo_venda`) REFERENCES `veiculo` (`id_veiculo`) ON UPDATE CASCADE,
  CONSTRAINT `venda_ibfk_3` FOREIGN KEY (`vendedor_venda`) REFERENCES `vendedor` (`id_vendedor`) ON UPDATE CASCADE,
  CONSTRAINT `venda_ibfk_4` FOREIGN KEY (`pagamento_venda`) REFERENCES `pagamento` (`id_pagamento`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendedor`
--

DROP TABLE IF EXISTS `vendedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendedor` (
  `id_vendedor` int NOT NULL AUTO_INCREMENT,
  `name_vendedor` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `cpf_vendedor` varchar(11) COLLATE utf8mb4_general_ci NOT NULL,
  `comissao_percentual` decimal(5,2) NOT NULL,
  `concessionario_vendedor` int NOT NULL,
  PRIMARY KEY (`id_vendedor`),
  UNIQUE KEY `name_vendedor` (`name_vendedor`),
  UNIQUE KEY `cpf_vendedor` (`cpf_vendedor`),
  KEY `concessionario_vendedor` (`concessionario_vendedor`),
  CONSTRAINT `vendedor_ibfk_1` FOREIGN KEY (`concessionario_vendedor`) REFERENCES `concessionaria` (`id_concessionaria`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-07 21:27:32
