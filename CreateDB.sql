CREATE SCHEMA `Stocks` ;
use Stocks;
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `Transaction_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Transaction_History` (
  `tickerName` varchar(5) DEFAULT NULL,
  `price` decimal(8,4) DEFAULT NULL,
  `shares` int(11) DEFAULT NULL,
  `BUY_SELL` varchar(10) DEFAULT NULL,
  `gross` decimal(12,4) DEFAULT NULL,
  `net` decimal(12,4) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `entryID` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`entryID`),
  KEY `fk_Transaction_history_1_idx` (`tickerName`),
  CONSTRAINT `fk_Transaction_history_1` FOREIGN KEY (`tickerName`) REFERENCES `Followed_Stocks` (`tickerName`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `Owned_Stocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Owned_Stocks` (
  `tickerName` varchar(5) NOT NULL,
  `buyPrice` decimal(8,4) NOT NULL,
  `shares` int(11) NOT NULL,
  `maxLoss` decimal(8,4) NOT NULL,
  `buyDate` datetime DEFAULT NULL,
  PRIMARY KEY (`tickerName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `Ticker_Prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Ticker_Prices` (
  `tickerName` varchar(5) NOT NULL,
  `price` decimal(8,4) NOT NULL,
  `time` datetime NOT NULL,
  `entryID` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`entryID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
DROP TABLE IF EXISTS `Followed_Stocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Followed_Stocks` (
  `tickerName` varchar(5) NOT NULL,
  `mostRecentSellPrice` decimal(8,4) DEFAULT NULL,
  PRIMARY KEY (`tickerName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `Current_Cash`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Current_Cash` (
  `currentCash` decimal(12,4) NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=latin1;
DROP TABLE IF EXISTS `Day_Trades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Day_Trades` (
  `tickerName` varchar(5) NOT NULL,
  `buyTime` datetime NOT NULL,
  `sellTime` datetime NOT NULL,
  `entryID` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`entryID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

