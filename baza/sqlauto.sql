-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.18-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema `autoexpres`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `autoexpres`;
USE `autoexpres`;

--
-- Table structure for table `autoexpres`.`delovi`
--

DROP TABLE IF EXISTS `delovi`;
CREATE TABLE `delovi` (
  `rbr` int(10) NOT NULL default '0',
  `datum` date NOT NULL default '0000-00-00',
  `ime` varchar(50) NOT NULL default ' ',
  `telefon` varchar(15) NOT NULL default ' ',
  `mobilni` varchar(15) NOT NULL default ' ',
  `adresa` varchar(30) NOT NULL default ' ',
  `mesto` varchar(20) NOT NULL default ' ',
  `pib` varchar(9) NOT NULL default '0',
  `opis` varchar(200) NOT NULL default '0',
  `cena` double NOT NULL default '0',
  `vrstenekretnina` varchar(50) NOT NULL default ' ',
  `tip` varchar(50) NOT NULL default ' ',
  `kvadratura` varchar(50) NOT NULL default ' ',
  `godproizvodnje` int(11) NOT NULL default '0',
  `vrsta` int(1) NOT NULL default '2',
  `aktivan` int(1) NOT NULL default '1',
  UNIQUE KEY `rbr` (`rbr`),
  KEY `ime` (`ime`),
  KEY `vrstenekretnina` (`vrstenekretnina`),
  KEY `tip` (`tip`),
  KEY `godproizvodnje` (`godproizvodnje`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`delovi`
--

/*!40000 ALTER TABLE `delovi` DISABLE KEYS */;
INSERT INTO `delovi` (`rbr`,`datum`,`ime`,`telefon`,`mobilni`,`adresa`,`mesto`,`pib`,`opis`,`cena`,`vrstenekretnina`,`tip`,`kvadratura`,`godproizvodnje`,`vrsta`,`aktivan`) VALUES 
 (1,'2009-05-05','Kupac kupac                  ','022/789789     ','546546546      ','adresa                        ','sid                 ','0','',0,'Kuce                          ','prizemna                ','                    ',0,1,1),
 (2,'2009-05-05','Miric Mitar                  ','4564564        ','213213213      ','asdasdasd                     ','mesto               ','0','sadf fsadfasdf',2000,'Vikendice                     ','vikend naselje          ','50                  ',1990,2,1),
 (3,'2009-05-05','Lalic Lala                   ','564565646      ','6546           ','asrg                          ','sdfgsdfg            ','0','as sdafasdf',50000,'Stanovi                       ','dva sprata              ','50                  ',2000,2,1),
 (4,'2009-11-17','ime i prezime                ','913249         ','123            ','adresa asdklfjlsd             ','mesto               ','0','opis',1000,'Kuce                          ','prizemna                ','100                 ',2000,2,1);
/*!40000 ALTER TABLE `delovi` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`dokumentimaterijalno`
--

DROP TABLE IF EXISTS `dokumentimaterijalno`;
CREATE TABLE `dokumentimaterijalno` (
  `nista` tinyint(4) default '0',
  `uliz` int(11) default '0',
  `vrdok` int(11) NOT NULL default '0',
  `nazdok` varchar(30) default NULL,
  PRIMARY KEY  (`vrdok`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`dokumentimaterijalno`
--

/*!40000 ALTER TABLE `dokumentimaterijalno` DISABLE KEYS */;
INSERT INTO `dokumentimaterijalno` (`nista`,`uliz`,`vrdok`,`nazdok`) VALUES 
 (0,0,0,'Pocetno stanje'),
 (0,0,1,'Prijemnice'),
 (0,0,2,'Zapisnik o oprihodovanju'),
 (0,2,3,'Izdatnice'),
 (0,2,4,'Otpremnice'),
 (0,0,6,'Nivelacija'),
 (0,0,7,'Eksterni ulazi maloprodaje'),
 (0,3,8,'Medjuskladisnice izlaz'),
 (0,1,9,'Medjuskladisnica ulaz'),
 (0,3,10,'Interni racun'),
 (0,2,11,'Kasa'),
 (0,2,12,'Rashod Maloprodaje'),
 (0,3,15,'Interni racun - 1'),
 (0,0,55,'Ostalo-Ulaz'),
 (0,2,66,'Ostalo-Izlaz'),
 (0,2,88,'Fakture usluge'),
 (0,0,16,'Povrat iz prodavnice          ');
/*!40000 ALTER TABLE `dokumentimaterijalno` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`gorivo`
--

DROP TABLE IF EXISTS `gorivo`;
CREATE TABLE `gorivo` (
  `rbr` int(11) NOT NULL,
  `brsasije` varchar(50) default ' ',
  `regbroj` varchar(20) default ' ',
  `datum` date NOT NULL default '0000-00-00',
  `kolicina` double NOT NULL default '0',
  `stanjekm` double NOT NULL default '0',
  `predjenokm` double NOT NULL default '0',
  `potrosnja` double NOT NULL default '0',
  UNIQUE KEY `rbr` (`rbr`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`gorivo`
--

/*!40000 ALTER TABLE `gorivo` DISABLE KEYS */;
INSERT INTO `gorivo` (`rbr`,`brsasije`,`regbroj`,`datum`,`kolicina`,`stanjekm`,`predjenokm`,`potrosnja`) VALUES 
 (1,'123456w123','SM 789-879','2011-06-03',110,120000,230,23);
/*!40000 ALTER TABLE `gorivo` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`korisnici`
--

DROP TABLE IF EXISTS `korisnici`;
CREATE TABLE `korisnici` (
  `JMBG` varchar(13) NOT NULL,
  `ime` varchar(50) default NULL,
  `prezime` varchar(50) default NULL,
  `imeroditelja` varchar(50) default NULL,
  `ulica` varchar(50) default NULL,
  `broj` varchar(10) default NULL,
  `mesto` varchar(50) default NULL,
  `opstina` varchar(50) default NULL,
  `telefon` varchar(50) default NULL,
  `mobilni` varchar(50) default NULL,
  `zanimanje` varchar(50) default NULL,
  `datumrodjenja` datetime default NULL,
  `mestorodjenja` varchar(50) default NULL,
  `drzava` varchar(50) default NULL,
  `drz` varchar(50) default NULL,
  `brlk` varchar(50) default NULL,
  PRIMARY KEY  (`JMBG`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`korisnici`
--

/*!40000 ALTER TABLE `korisnici` DISABLE KEYS */;
/*!40000 ALTER TABLE `korisnici` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`magacini`
--

DROP TABLE IF EXISTS `magacini`;
CREATE TABLE `magacini` (
  `pre` int(11) NOT NULL,
  `mag` int(11) NOT NULL,
  `nazivm` varchar(50) default NULL,
  `adesa` varchar(20) default NULL,
  `mesto` varchar(20) default NULL,
  PRIMARY KEY  (`pre`,`mag`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`magacini`
--

/*!40000 ALTER TABLE `magacini` DISABLE KEYS */;
INSERT INTO `magacini` (`pre`,`mag`,`nazivm`,`adesa`,`mesto`) VALUES 
 (1,99,'Maloprodaja                             ','                    ','                    '),
 (1,2,'Drugi magacin                           ','sadfsadf            ','                    '),
 (1,1,'Roba                                    ','                    ','                    '),
 (1,3,'Magacin gotove robe                     ','                    ','                    '),
 (2,99,'Maloprodaja                             ','                    ','                    '),
 (2,3,'Magacin gotove robe                     ','                    ','                    '),
 (2,1,'drugo preduzece               ',' ',' '),
 (2,2,'Drugi magacin                           ','sadfsadf            ','                    '),
 (3,1,'Trece prerduzece              ',' ',' '),
 (3,2,'Drugi magacin                           ','sadfsadf            ','                    '),
 (3,3,'Magacin gotove robe                     ','                    ','                    '),
 (3,99,'Maloprodaja                             ','                    ','                    ');
/*!40000 ALTER TABLE `magacini` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`materijalnakonta`
--

DROP TABLE IF EXISTS `materijalnakonta`;
CREATE TABLE `materijalnakonta` (
  `konto` int(11) NOT NULL,
  `nazivk` varchar(30) default NULL,
  `nista` tinyint(4) default '0',
  PRIMARY KEY  (`konto`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`materijalnakonta`
--

/*!40000 ALTER TABLE `materijalnakonta` DISABLE KEYS */;
INSERT INTO `materijalnakonta` (`konto`,`nazivk`,`nista`) VALUES 
 (1010,'Repro materijal',NULL),
 (1020,'Rezervni delovi',0),
 (1030,'Ambalaza',0),
 (1200,'Gotovi proizvodi',0),
 (1201,'Poluproizvodi',0),
 (1320,'Trgovacka roba                ',0),
 (1340,'Roba u prodavnici',0);
/*!40000 ALTER TABLE `materijalnakonta` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`mestatroska`
--

DROP TABLE IF EXISTS `mestatroska`;
CREATE TABLE `mestatroska` (
  `pre` int(11) NOT NULL,
  `mtros` int(11) NOT NULL,
  `naztros` varchar(40) default NULL,
  `nazjm` varchar(6) default NULL,
  PRIMARY KEY  (`pre`,`mtros`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`mestatroska`
--

/*!40000 ALTER TABLE `mestatroska` DISABLE KEYS */;
INSERT INTO `mestatroska` (`pre`,`mtros`,`naztros`,`nazjm`) VALUES 
 (1,30000,'Proizvodi                               ','Kg    '),
 (1,50000,'Mašine                                  ','kom   '),
 (1,90000,'Opšti troškovi preduyeca                ','      '),
 (1,90100,'Opšti troškovi R.jedinica               ','      '),
 (2,90100,'Opšti troškovi R.jedinica               ','      '),
 (2,90000,'Opšti troškovi preduyeca                ','      '),
 (2,50000,'Mašine                                  ','kom   '),
 (2,30000,'Proizvodi                               ','Kg    '),
 (3,30000,'Proizvodi                               ','Kg    '),
 (3,50000,'Mašine                                  ','kom   '),
 (3,90000,'Opšti troškovi preduyeca                ','      '),
 (3,90100,'Opšti troškovi R.jedinica               ','      ');
/*!40000 ALTER TABLE `mestatroska` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`mprom1`
--

DROP TABLE IF EXISTS `mprom1`;
CREATE TABLE `mprom1` (
  `pre` int(11) NOT NULL default '0',
  `jur` int(11) NOT NULL default '0',
  `mag` int(11) NOT NULL default '0',
  `ui` tinyint(4) NOT NULL default '0',
  `indikator` tinyint(4) NOT NULL default '0',
  `brun` double NOT NULL default '0',
  `rbr` int(11) NOT NULL default '0',
  `sifm` int(11) NOT NULL default '0',
  `konto` int(11) NOT NULL default '0',
  `nazivm` varchar(32) NOT NULL default '-',
  `datum` datetime NOT NULL default '2000-01-01 00:00:00',
  `vrdok` int(11) NOT NULL default '0',
  `brdok` int(11) NOT NULL default '0',
  `kupacbr` int(11) NOT NULL default '0',
  `brrac` double NOT NULL default '0',
  `prodavnica` int(11) NOT NULL default '0',
  `fak` tinyint(4) NOT NULL default '0',
  `sar` tinyint(4) NOT NULL default '0',
  `kolic` double NOT NULL default '0',
  `cena` double NOT NULL default '0',
  `vredn` double NOT NULL default '0',
  `vrednnivel` double NOT NULL default '0',
  `tarbr` int(11) NOT NULL default '0',
  `vrtar` tinyint(4) NOT NULL default '0',
  `por1` double NOT NULL default '0',
  `por2` double NOT NULL default '0',
  `por3` double NOT NULL default '0',
  `zav` double NOT NULL default '0',
  `mar` double NOT NULL default '0',
  `taksa` double NOT NULL default '0',
  `preneto` tinyint(4) NOT NULL default '0',
  `preneto1` tinyint(4) NOT NULL default '0',
  `ispravno` tinyint(4) NOT NULL default '0',
  `pcena` double NOT NULL default '0',
  `pcena1` double NOT NULL default '0',
  `pvredn` double NOT NULL default '0',
  `rabat` double NOT NULL default '0',
  `ulazi` tinyint(4) NOT NULL default '1',
  `mtros` int(11) NOT NULL default '0',
  `oznaka` varchar(1) NOT NULL default '-',
  `prenosbr` int(11) NOT NULL default '0',
  `najur` int(11) NOT NULL default '0',
  `namag` int(11) NOT NULL default '0',
  `nakonto` int(11) NOT NULL default '0',
  `razmar` double NOT NULL default '0',
  `valuta` datetime NOT NULL default '2000-01-01 00:00:00',
  `datumnaloga` datetime NOT NULL default '2000-01-01 00:00:00',
  `gorivo` tinyint(4) NOT NULL default '0',
  `kon1` varchar(6) NOT NULL default '0',
  `kon2` varchar(6) NOT NULL default '0',
  `kon3` varchar(6) NOT NULL default '0',
  `imaporez` tinyint(4) NOT NULL default '0',
  `uvoz` tinyint(4) NOT NULL default '0',
  `moze` tinyint(4) NOT NULL default '0',
  `mesec` int(11) NOT NULL default '0',
  `smena` int(2) NOT NULL default '0',
  `prodavac` int(2) NOT NULL default '0',
  `kojakasa` int(2) NOT NULL default '0',
  `kolicul` double NOT NULL default '0',
  `koliciz` double NOT NULL default '0',
  `kolicme` double NOT NULL default '0',
  `z` tinyint(4) NOT NULL default '0',
  `vozilo` varchar(50) NOT NULL default ' ',
  `rnal` int(7) NOT NULL default '0',
  PRIMARY KEY  (`pre`,`jur`,`mag`,`ui`,`indikator`,`brun`,`rbr`,`najur`,`vrdok`,`prodavnica`,`kojakasa`),
  KEY `brrac` (`brrac`),
  KEY `brun` (`brun`),
  KEY `indikator` (`indikator`),
  KEY `jur` (`jur`),
  KEY `konto` (`konto`),
  KEY `kupacbr` (`kupacbr`),
  KEY `mag` (`mag`),
  KEY `pre` (`pre`),
  KEY `rbr` (`rbr`),
  KEY `ui` (`ui`),
  KEY `vrdok` (`vrdok`),
  KEY `vozilo` (`vozilo`),
  KEY `rnal` (`rnal`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`mprom1`
--

/*!40000 ALTER TABLE `mprom1` DISABLE KEYS */;
INSERT INTO `mprom1` (`pre`,`jur`,`mag`,`ui`,`indikator`,`brun`,`rbr`,`sifm`,`konto`,`nazivm`,`datum`,`vrdok`,`brdok`,`kupacbr`,`brrac`,`prodavnica`,`fak`,`sar`,`kolic`,`cena`,`vredn`,`vrednnivel`,`tarbr`,`vrtar`,`por1`,`por2`,`por3`,`zav`,`mar`,`taksa`,`preneto`,`preneto1`,`ispravno`,`pcena`,`pcena1`,`pvredn`,`rabat`,`ulazi`,`mtros`,`oznaka`,`prenosbr`,`najur`,`namag`,`nakonto`,`razmar`,`valuta`,`datumnaloga`,`gorivo`,`kon1`,`kon2`,`kon3`,`imaporez`,`uvoz`,`moze`,`mesec`,`smena`,`prodavac`,`kojakasa`,`kolicul`,`koliciz`,`kolicme`,`z`,`vozilo`,`rnal`) VALUES 
 (1,1,1,0,0,1,1,1,1010,'guma za tocak       ','2011-05-02 00:00:00',1,1,1,1,0,0,0,100,10,1000,0,1,0,18,0,0,0,0,0,0,0,0,10,10,1000,0,1,0,'-',0,0,0,0,0,'2011-05-02 00:00:00','2011-05-02 00:00:00',0,'0','0','0',0,0,0,1,0,0,0,100,0,0,0,' ',0),
 (1,1,1,0,0,1,2,2,1010,'Motorno ulje K2-1   ','2011-05-02 00:00:00',1,1,1,1,0,0,0,200,20,4000,0,1,0,18,0,0,0,0,0,0,0,0,20,20,4000,0,1,0,'-',0,0,0,0,0,'2011-05-02 00:00:00','2011-05-02 00:00:00',0,'0','0','0',0,0,0,1,0,0,0,200,0,0,0,' ',0),
 (1,1,1,2,0,1,1,1,1010,'guma za tocak                 ','2011-05-02 00:00:00',3,1,0,0,0,0,0,1,10,10,0,0,0,0,0,0,0,0,0,0,0,0,10,10,10,0,1,0,'-',0,0,0,0,0,'2011-05-02 00:00:00','2011-05-02 00:00:00',0,'0','0','0',0,0,0,0,0,0,0,0,1,0,0,'123456w123',0);
INSERT INTO `mprom1` (`pre`,`jur`,`mag`,`ui`,`indikator`,`brun`,`rbr`,`sifm`,`konto`,`nazivm`,`datum`,`vrdok`,`brdok`,`kupacbr`,`brrac`,`prodavnica`,`fak`,`sar`,`kolic`,`cena`,`vredn`,`vrednnivel`,`tarbr`,`vrtar`,`por1`,`por2`,`por3`,`zav`,`mar`,`taksa`,`preneto`,`preneto1`,`ispravno`,`pcena`,`pcena1`,`pvredn`,`rabat`,`ulazi`,`mtros`,`oznaka`,`prenosbr`,`najur`,`namag`,`nakonto`,`razmar`,`valuta`,`datumnaloga`,`gorivo`,`kon1`,`kon2`,`kon3`,`imaporez`,`uvoz`,`moze`,`mesec`,`smena`,`prodavac`,`kojakasa`,`kolicul`,`koliciz`,`kolicme`,`z`,`vozilo`,`rnal`) VALUES 
 (1,1,1,2,0,1,2,2,1010,'Motorno ulje K2-1             ','2011-05-02 00:00:00',3,1,0,0,0,0,0,2,20,40,0,0,0,0,0,0,0,0,0,0,0,0,20,20,40,0,1,0,'-',0,0,0,0,0,'2011-05-02 00:00:00','2011-05-02 00:00:00',0,'0','0','0',0,0,0,0,0,0,0,0,2,0,0,'123456w123',0),
 (1,1,1,0,0,2,1,2,1010,'Motorno ulje K2-1   ','2011-05-03 00:00:00',1,2,1,1,0,0,0,5,200,1000,0,1,0,18,0,0,0,0,0,0,0,0,200,200,1000,0,1,0,'-',0,0,0,0,0,'2011-05-03 00:00:00','2011-05-03 00:00:00',0,'0','0','0',0,0,0,1,0,0,0,5,0,0,0,' ',0),
 (1,1,1,2,0,2,1,1,1010,'guma za tocak                 ','2011-05-03 00:00:00',3,2,0,0,0,0,0,5,10,50,0,0,0,0,0,0,0,0,0,0,0,0,10,10,50,0,1,0,'-',0,0,0,0,0,'2011-05-03 00:00:00','2011-05-03 00:00:00',0,'0','0','0',0,0,0,0,0,0,0,0,5,0,0,'456123AA123',0);
INSERT INTO `mprom1` (`pre`,`jur`,`mag`,`ui`,`indikator`,`brun`,`rbr`,`sifm`,`konto`,`nazivm`,`datum`,`vrdok`,`brdok`,`kupacbr`,`brrac`,`prodavnica`,`fak`,`sar`,`kolic`,`cena`,`vredn`,`vrednnivel`,`tarbr`,`vrtar`,`por1`,`por2`,`por3`,`zav`,`mar`,`taksa`,`preneto`,`preneto1`,`ispravno`,`pcena`,`pcena1`,`pvredn`,`rabat`,`ulazi`,`mtros`,`oznaka`,`prenosbr`,`najur`,`namag`,`nakonto`,`razmar`,`valuta`,`datumnaloga`,`gorivo`,`kon1`,`kon2`,`kon3`,`imaporez`,`uvoz`,`moze`,`mesec`,`smena`,`prodavac`,`kojakasa`,`kolicul`,`koliciz`,`kolicme`,`z`,`vozilo`,`rnal`) VALUES 
 (1,1,1,0,0,3,1,2,1010,'Motorno ulje K2-1   ','2011-05-08 00:00:00',1,3,2,0,0,0,0,10,100,1000,0,1,0,18,0,0,0,0,0,0,0,0,100,100,1000,0,1,0,'-',0,0,0,0,0,'2011-05-08 00:00:00','2011-05-08 00:00:00',0,'0','0','0',0,0,0,1,0,0,0,10,0,0,0,' ',0),
 (1,1,1,2,0,3,1,2,1010,'Motorno ulje K2-1             ','2011-05-08 00:00:00',3,3,0,0,0,0,0,10,27.98,279.8,0,0,0,0,0,0,0,0,0,0,0,0,27.98,27.98,279.8,0,1,0,'-',0,0,0,0,0,'2011-05-08 00:00:00','2011-05-08 00:00:00',0,'0','0','0',0,0,0,0,0,0,0,0,10,0,0,'456123AA123',0),
 (1,1,1,2,0,4,1,1,1010,'guma za tocak                 ','2011-05-24 00:00:00',3,4,0,0,0,0,0,1,10,10,0,0,0,0,0,0,0,0,0,0,0,0,10,10,10,0,1,0,'-',0,0,0,0,0,'2011-05-24 00:00:00','2011-05-24 00:00:00',0,'0','0','0',0,0,0,0,0,0,0,0,1,0,0,'123456w123',1);
/*!40000 ALTER TABLE `mprom1` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`oprema`
--

DROP TABLE IF EXISTS `oprema`;
CREATE TABLE `oprema` (
  `rbr` int(10) NOT NULL,
  `brsasije` varchar(50) NOT NULL,
  `naziv` varchar(30) NOT NULL default ' ',
  `komada` double default '0',
  `broj` varchar(30) NOT NULL default ' ',
  `opis` varchar(50) NOT NULL default ' ',
  `jmbg` varchar(13) NOT NULL default ' ',
  PRIMARY KEY  (`rbr`,`brsasije`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`oprema`
--

/*!40000 ALTER TABLE `oprema` DISABLE KEYS */;
INSERT INTO `oprema` (`rbr`,`brsasije`,`naziv`,`komada`,`broj`,`opis`,`jmbg`) VALUES 
 (1,'123456w123','akumulator',1,'12345','asd',' '),
 (2,'123456w123','aparat za gasenje',1,'1234','atest',' '),
 (1,'456123AA123','komad',1,'456456','opis','456123AA123');
/*!40000 ALTER TABLE `oprema` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`pomfizicka`
--

DROP TABLE IF EXISTS `pomfizicka`;
CREATE TABLE `pomfizicka` (
  `JMBG` varchar(13) default NULL,
  `regbroj` varchar(20) default NULL,
  `datumreg` datetime default NULL,
  `ime` varchar(50) default NULL,
  `prezime` varchar(50) default NULL,
  `ulica` varchar(50) default NULL,
  `broj` varchar(10) default NULL,
  `mesto` varchar(50) default NULL,
  `telefon` varchar(50) default NULL,
  `mobilni` varchar(50) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`pomfizicka`
--

/*!40000 ALTER TABLE `pomfizicka` DISABLE KEYS */;
/*!40000 ALTER TABLE `pomfizicka` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`pompravna`
--

DROP TABLE IF EXISTS `pompravna`;
CREATE TABLE `pompravna` (
  `matbr` varchar(13) default NULL,
  `regbroj` varchar(20) default NULL,
  `datumreg` datetime default NULL,
  `naziv` varchar(50) default NULL,
  `ulica` varchar(50) default NULL,
  `broj` varchar(50) default NULL,
  `mesto` varchar(50) default NULL,
  `telefon` varchar(50) default NULL,
  `mobilni` varchar(50) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`pompravna`
--

/*!40000 ALTER TABLE `pompravna` DISABLE KEYS */;
/*!40000 ALTER TABLE `pompravna` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`pravna`
--

DROP TABLE IF EXISTS `pravna`;
CREATE TABLE `pravna` (
  `matbr` varchar(13) NOT NULL,
  `naziv` varchar(50) default NULL,
  `delatnost` varchar(50) default NULL,
  `ulica` varchar(50) default NULL,
  `broj` varchar(50) default NULL,
  `mesto` varchar(50) default NULL,
  `opstina` varchar(50) default NULL,
  `telefon` varchar(50) default NULL,
  `mobilni` varchar(50) default NULL,
  `zanimanje` varchar(50) default NULL,
  `lice` varchar(50) default NULL,
  PRIMARY KEY  (`matbr`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`pravna`
--

/*!40000 ALTER TABLE `pravna` DISABLE KEYS */;
INSERT INTO `pravna` (`matbr`,`naziv`,`delatnost`,`ulica`,`broj`,`mesto`,`opstina`,`telefon`,`mobilni`,`zanimanje`,`lice`) VALUES 
 ('11111111','Prvo                     ','987987','Ulica                 ','56','Sid                 ','Sid                 ','89798789       ','09809809       ',NULL,'                         ');
/*!40000 ALTER TABLE `pravna` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`preduzeca`
--

DROP TABLE IF EXISTS `preduzeca`;
CREATE TABLE `preduzeca` (
  `Pre` int(11) NOT NULL,
  `Naziv1` varchar(35) default NULL,
  `Mesto` varchar(20) default NULL,
  `Adresa` varchar(25) default NULL,
  `Telefon` varchar(15) default NULL,
  `Fax` varchar(15) default NULL,
  `Ziror` varchar(30) default NULL,
  `pbroj` int(11) default '0',
  `nadimak` varchar(10) default NULL,
  `nadimak1` varchar(10) default NULL,
  `sido` varchar(15) default NULL,
  `matbr` varchar(15) default NULL,
  `pib` varchar(15) default NULL,
  `Ziror1` varchar(30) default NULL,
  `Ziror2` varchar(30) default NULL,
  `PEPDV` varchar(20) default NULL,
  PRIMARY KEY  (`Pre`),
  UNIQUE KEY `Naziv1` (`Naziv1`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`preduzeca`
--

/*!40000 ALTER TABLE `preduzeca` DISABLE KEYS */;
INSERT INTO `preduzeca` (`Pre`,`Naziv1`,`Mesto`,`Adresa`,`Telefon`,`Fax`,`Ziror`,`pbroj`,`nadimak`,`nadimak1`,`sido`,`matbr`,`pib`,`Ziror1`,`Ziror2`,`PEPDV`) VALUES 
 (1,'S.A.T.R.AUTOBELI              ','Šid                 ','NIKOLE TESLE 6           ','022/712-767    ','022/711-931    ','355-1020895-24                ',2224,NULL,NULL,'102323         ','2345656      ','101232617','                              ','                              ','                    ');
/*!40000 ALTER TABLE `preduzeca` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`radnejedinice`
--

DROP TABLE IF EXISTS `radnejedinice`;
CREATE TABLE `radnejedinice` (
  `pre` int(11) NOT NULL,
  `jur` int(11) NOT NULL,
  `nazjur` varchar(20) default NULL,
  PRIMARY KEY  (`pre`,`jur`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`radnejedinice`
--

/*!40000 ALTER TABLE `radnejedinice` DISABLE KEYS */;
INSERT INTO `radnejedinice` (`pre`,`jur`,`nazjur`) VALUES 
 (1,1,'Prva RJ             '),
 (2,1,'drugo preduzece    '),
 (1,2,'druga rj           '),
 (3,1,'Trece prerduzece   ');
/*!40000 ALTER TABLE `radnejedinice` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`radnici`
--

DROP TABLE IF EXISTS `radnici`;
CREATE TABLE `radnici` (
  `passwd` varchar(200) NOT NULL default ' ',
  `stat` int(1) NOT NULL default '0',
  `imeprezime` varchar(50) NOT NULL default ' ',
  `koeficient` double NOT NULL default '0',
  `jmbg` varchar(13) NOT NULL default '0',
  `radnomesto` varchar(30) NOT NULL default ' ',
  `brvozdozvole` varchar(25) NOT NULL default ' ',
  `datrodjenja` date NOT NULL default '0000-00-00',
  `brlk` varchar(30) NOT NULL default ' ',
  `zaposlenod` date NOT NULL default '0000-00-00',
  `datumzadlek` date NOT NULL default '0000-00-00',
  `lekarskimes` int(3) NOT NULL default '0',
  PRIMARY KEY  (`jmbg`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`radnici`
--

/*!40000 ALTER TABLE `radnici` DISABLE KEYS */;
INSERT INTO `radnici` (`passwd`,`stat`,`imeprezime`,`koeficient`,`jmbg`,`radnomesto`,`brvozdozvole`,`datrodjenja`,`brlk`,`zaposlenod`,`datumzadlek`,`lekarskimes`) VALUES 
 (' ',0,'Petar Petrovic                     ',0,'1234567891231','vozac                      ','123123                   ','1980-02-02','546465                     ','2000-02-02','2011-03-13',5),
 (' ',0,'Mitic Mitar                        ',0,'2222222222222','majstor                    ','                         ','1970-05-05','5464656                    ','2000-06-06','2000-01-01',0),
 (' ',0,'Tarabic Tiosav                     ',0,'3333333333333','bravar                     ','5456                     ','1960-08-08','9898                       ','1995-06-06','2011-03-01',6);
/*!40000 ALTER TABLE `radnici` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`rnal`
--

DROP TABLE IF EXISTS `rnal`;
CREATE TABLE `rnal` (
  `rbr` int(11) NOT NULL,
  `brsasije` varchar(50) NOT NULL default ' ',
  `datum` date NOT NULL default '0000-00-00',
  `cenarada` double NOT NULL default '0',
  `cenadelova` double NOT NULL default '0',
  `garancija` varchar(20) NOT NULL default ' ',
  `opiskvara` mediumtext NOT NULL,
  `opisradova` mediumtext NOT NULL,
  `delovi` mediumtext NOT NULL,
  `jmbg` varchar(13) NOT NULL default '0',
  `placeno` int(1) NOT NULL default '0',
  `napomena` mediumtext NOT NULL,
  `reklamacija` int(1) NOT NULL default '0',
  `rabat` double NOT NULL default '0',
  `regbroj` varchar(20) NOT NULL default ' ',
  UNIQUE KEY `rbr` (`rbr`,`jmbg`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`rnal`
--

/*!40000 ALTER TABLE `rnal` DISABLE KEYS */;
INSERT INTO `rnal` (`rbr`,`brsasije`,`datum`,`cenarada`,`cenadelova`,`garancija`,`opiskvara`,`opisradova`,`delovi`,`jmbg`,`placeno`,`napomena`,`reklamacija`,`rabat`,`regbroj`) VALUES 
 (1,'123456w123               ','2011-05-24',0,0,' ','tyerty ksdflkasjdf lkdfj alsfjkd lksdfj lskd flaskd laskdfj lk eorptiuoertu oeiur toeiur toiwerut oeri tuoweir toweir toeiur towieur towieur toiwe rtowier towieurt oweiru toweir toweirt oweirut weoritu weoritu weorit uweoirtu woerit','ghdfh','cxhvsd','1234567891123',0,'shgdfgh',0,0,'SM 789-879     '),
 (2,'456123AA123              ','2011-05-24',0,0,' ','oooooooooo','aaaaaaaaa','bbbbbbbbbb','1234567891123',0,'cccccccccccccc',0,0,'SM 897-122     '),
 (3,'g11233g                  ','2011-05-24',0,0,' ','ppppppppppppp','mmmmmmmmmmmm','nnnnnnnnn','4561234561231',0,']33333333333333',0,0,'sm62112        '),
 (4,'123456w123               ','2011-05-02',0,0,' ','Kvar ot prije','ksdffksfd','sdfasdf','4561234561231',0,'',0,0,'SM 789-879     ');
/*!40000 ALTER TABLE `rnal` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`saradnici`
--

DROP TABLE IF EXISTS `saradnici`;
CREATE TABLE `saradnici` (
  `kupacbr` int(11) NOT NULL,
  `nazivkupca` varchar(45) NOT NULL,
  `adresa` varchar(40) NOT NULL,
  `mesto` varchar(20) NOT NULL,
  `pbroj` int(11) NOT NULL default '0',
  `ziror` varchar(30) NOT NULL,
  `telefon` varchar(15) NOT NULL default '0',
  `fax` varchar(15) NOT NULL,
  `napomena` varchar(50) NOT NULL,
  `passwd` varchar(200) NOT NULL default '0',
  `mobilni` varchar(15) NOT NULL default '0',
  `tekst` blob NOT NULL,
  `kupdob` varchar(1) NOT NULL default ' ',
  `valuta` int(11) NOT NULL default '0',
  PRIMARY KEY  (`kupacbr`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`saradnici`
--

/*!40000 ALTER TABLE `saradnici` DISABLE KEYS */;
INSERT INTO `saradnici` (`kupacbr`,`nazivkupca`,`adresa`,`mesto`,`pbroj`,`ziror`,`telefon`,`fax`,`napomena`,`passwd`,`mobilni`,`tekst`,`kupdob`,`valuta`) VALUES 
 (1,'sopstvena prodavnica','adresa','sid',0,'','0','','','0','0','',' ',0),
 (2,'Autobeli','Nikole Tesle 22','sid',0,'','0','','','0','0','',' ',0);
/*!40000 ALTER TABLE `saradnici` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`sifarnikrobe`
--

DROP TABLE IF EXISTS `sifarnikrobe`;
CREATE TABLE `sifarnikrobe` (
  `sifm` int(11) NOT NULL,
  `nazivm` varchar(30) NOT NULL default '-',
  `jmere` varchar(6) NOT NULL default '-',
  `katbr` varchar(20) NOT NULL default '-',
  `tarb` int(11) NOT NULL default '0',
  `gorivo` tinyint(4) NOT NULL default '0',
  `plcena` double NOT NULL default '0',
  `barkod` varchar(20) NOT NULL default '0',
  `minkolic` double NOT NULL default '0',
  `plu` varchar(10) NOT NULL default '0',
  `kupacbr` int(11) NOT NULL default '0',
  `vcena` double NOT NULL default '0',
  `cenovnik` tinyint(1) NOT NULL default '1',
  `tezina` double NOT NULL default '0',
  `pre` int(11) NOT NULL default '1',
  UNIQUE KEY `sifm` (`sifm`,`pre`),
  KEY `nazivm` (`nazivm`),
  KEY `barkod` (`barkod`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`sifarnikrobe`
--

/*!40000 ALTER TABLE `sifarnikrobe` DISABLE KEYS */;
INSERT INTO `sifarnikrobe` (`sifm`,`nazivm`,`jmere`,`katbr`,`tarb`,`gorivo`,`plcena`,`barkod`,`minkolic`,`plu`,`kupacbr`,`vcena`,`cenovnik`,`tezina`,`pre`) VALUES 
 (1,'guma za tocak                 ','kom   ','adsfaskdfljasf      ',1,0,0,'1',0,'0',0,0,0,0,1),
 (2,'Motorno ulje K2-1             ','l     ','kasflskad           ',1,0,0,'2',0,'0',0,0,0,0,1),
 (3,'Brezoni 25mm                  ','kom   ','ksafjl              ',1,0,0,'3',0,'0',0,0,0,0,1);
/*!40000 ALTER TABLE `sifarnikrobe` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`tarifnibrojevi`
--

DROP TABLE IF EXISTS `tarifnibrojevi`;
CREATE TABLE `tarifnibrojevi` (
  `nista` tinyint(4) default '0',
  `tarb` int(11) NOT NULL,
  `naztar` varchar(30) default NULL,
  `vrtar` tinyint(4) default '0',
  `kon1` varchar(6) default NULL,
  `kon2` varchar(6) default NULL,
  `kon3` varchar(6) default NULL,
  `por1` int(11) default '0',
  `por2` int(11) default '0',
  `por3` int(11) default '0',
  PRIMARY KEY  (`tarb`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`tarifnibrojevi`
--

/*!40000 ALTER TABLE `tarifnibrojevi` DISABLE KEYS */;
INSERT INTO `tarifnibrojevi` (`nista`,`tarb`,`naztar`,`vrtar`,`kon1`,`kon2`,`kon3`,`por1`,`por2`,`por3`) VALUES 
 (0,0,'Bez poreza                    ',0,'0     ','0     ','0     ',0,0,0),
 (0,1,'Porez 18 % Sa pravom odbitka  ',0,'00    ','0     ','0     ',18,0,0),
 (0,2,'Porez 8 % Sa pravom odbitka   ',0,'10    ','0     ','0     ',8,0,0),
 (0,3,'Porez 5 % Poljoprivrednici    ',0,'005   ','      ','      ',5,0,0),
 (0,4,'Porez 18 % Bez prava odbitka  ',1,'00    ','      ','      ',18,0,0),
 (0,5,'Porez 8 % Bez prava odbitka   ',1,'10    ','      ','      ',8,0,0),
 (0,6,'Porez 8 % Nije u PDV sistemu  ',1,'10    ','      ','      ',8,0,0),
 (0,7,'Porez 18 % Nije u PDV sistemu ',1,'00    ','      ','      ',18,0,0);
/*!40000 ALTER TABLE `tarifnibrojevi` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`tmpteh`
--

DROP TABLE IF EXISTS `tmpteh`;
CREATE TABLE `tmpteh` (
  `brsasije` varchar(50) default ' ',
  `regbroj` varchar(20) default ' ',
  `tehpregled` date NOT NULL default '0000-00-00',
  `novidatum` date NOT NULL default '0000-00-00',
  `marka` varchar(50) default ' ',
  `tip` varchar(50) default ' ',
  `brojmesteh` int(11) default '0',
  `brojdana` int(7) default '0',
  UNIQUE KEY `brsasije` (`brsasije`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`tmpteh`
--

/*!40000 ALTER TABLE `tmpteh` DISABLE KEYS */;
INSERT INTO `tmpteh` (`brsasije`,`regbroj`,`tehpregled`,`novidatum`,`marka`,`tip`,`brojmesteh`,`brojdana`) VALUES 
 ('123456789','SM 456-789     ','2011-01-01','2011-07-01','SCANIA              ','TT                  ',6,24),
 ('2222222222222','SM 222-22      ','2011-03-03','2011-09-03','MERCEDES            ','D3500               ',6,88);
/*!40000 ALTER TABLE `tmpteh` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`uplate`
--

DROP TABLE IF EXISTS `uplate`;
CREATE TABLE `uplate` (
  `rbr` int(11) NOT NULL default '0',
  `vrdok` int(2) NOT NULL default '0',
  `pre` int(2) NOT NULL default '0',
  `mag` int(2) NOT NULL default '0',
  `kupacbr` varchar(50) NOT NULL default '0',
  `datum` datetime NOT NULL default '0000-00-00 00:00:00',
  `brun` int(11) NOT NULL default '0',
  `iznos` double NOT NULL default '0',
  `zirogot` tinyint(1) NOT NULL default '0',
  UNIQUE KEY `rbr` (`rbr`,`vrdok`),
  KEY `kupacbr` (`kupacbr`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`uplate`
--

/*!40000 ALTER TABLE `uplate` DISABLE KEYS */;
/*!40000 ALTER TABLE `uplate` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`vozila`
--

DROP TABLE IF EXISTS `vozila`;
CREATE TABLE `vozila` (
  `brputnmesta` varchar(50) default ' ',
  `regbroj` varchar(20) default ' ',
  `brojregistra` varchar(10) default ' ',
  `vrsta` int(11) default '0',
  `vrstavozila` varchar(50) default ' ',
  `oznakaJUS` varchar(50) default ' ',
  `marka` varchar(50) default ' ',
  `tip` varchar(50) default ' ',
  `brsasije` varchar(50) default ' ',
  `brmotora` varchar(50) default ' ',
  `godproizvodnje` int(11) default '0',
  `zemljaproizvodnje` varchar(50) default ' ',
  `snagamotora` double default '0',
  `radnazapremina` double default '0',
  `masapraznog` double default '0',
  `nosivost` double default '0',
  `brojmestased` int(11) default '0',
  `brojmestastaj` int(11) default '0',
  `obliknamena` varchar(50) default ' ',
  `osnovnanamena` varchar(30) default ' ',
  `bojakaroserije` varchar(30) default ' ',
  `bojadodata` varchar(30) default ' ',
  `brojosovina` int(11) default '0',
  `brojpogosovina` int(11) default '0',
  `imakuku` int(11) default '0',
  `kilometraza` double default '0',
  `registracija` date NOT NULL default '0000-00-00',
  `gorivo` int(11) default '0',
  `vrstaprevoza` int(11) default '0',
  `brojvrata` int(11) default '0',
  `brojtockova` int(11) default '0',
  `radio` int(11) default '0',
  `telefon` varchar(15) default ' ',
  `brojpolise` varchar(25) default ' ',
  `adresa` varchar(20) default ' ',
  `brzelkartona` varchar(25) default ' ',
  `tahograf` date NOT NULL default '0000-00-00',
  `adr` date NOT NULL default '0000-00-00',
  `tehpregled` date NOT NULL default '0000-00-00',
  `brojmesteh` int(11) NOT NULL default '0',
  UNIQUE KEY `brsasije` (`brsasije`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`vozila`
--

/*!40000 ALTER TABLE `vozila` DISABLE KEYS */;
INSERT INTO `vozila` (`brputnmesta`,`regbroj`,`brojregistra`,`vrsta`,`vrstavozila`,`oznakaJUS`,`marka`,`tip`,`brsasije`,`brmotora`,`godproizvodnje`,`zemljaproizvodnje`,`snagamotora`,`radnazapremina`,`masapraznog`,`nosivost`,`brojmestased`,`brojmestastaj`,`obliknamena`,`osnovnanamena`,`bojakaroserije`,`bojadodata`,`brojosovina`,`brojpogosovina`,`imakuku`,`kilometraza`,`registracija`,`gorivo`,`vrstaprevoza`,`brojvrata`,`brojtockova`,`radio`,`telefon`,`brojpolise`,`adresa`,`brzelkartona`,`tahograf`,`adr`,`tehpregled`,`brojmesteh`) VALUES 
 ('40                           ','SM 456-789     ',' ',0,'AUTOBUS             ',' ','SCANIA              ','TT                  ','123456789','54565DD5645         ',2000,' ',5600,0,0,0,0,0,'OBLIK                            ',' ','9879879DG98',' ',0,0,0,250000,'2011-01-01',0,0,0,0,0,' ','87887878            ',' ','545655              ','2011-06-05','2011-06-02','2011-01-01',6),
 ('20                           ','SM 222-22      ',' ',0,'MINIBUS             ',' ','MERCEDES            ','D3500               ','2222222222222','83838WJ7T           ',1990,' ',5400,0,0,0,0,0,'NAMENA                           ',' ','PLAVA',' ',0,0,0,300000,'2011-03-03',0,0,0,0,0,' ','666655              ',' ','23232323            ','2011-03-03','2011-03-03','2011-03-03',6),
 ('3                            ','SM 33-33       ',' ',0,'KAMION              ',' ','MERCEDES            ','DF4500              ','333333333333333333','8908098D09-         ',1998,' ',6500,0,0,0,0,0,'OBLIK                            ',' ','SIVA',' ',0,0,0,150000,'2011-05-05',0,0,0,0,0,' ','987987              ',' ','565465465           ','2011-06-06','2011-06-06','2011-05-05',0);
/*!40000 ALTER TABLE `vozila` ENABLE KEYS */;


--
-- Table structure for table `autoexpres`.`zamenaulja`
--

DROP TABLE IF EXISTS `zamenaulja`;
CREATE TABLE `zamenaulja` (
  `rbr` int(11) NOT NULL,
  `brsasije` varchar(50) default ' ',
  `regbroj` varchar(20) default ' ',
  `datum` date NOT NULL default '0000-00-00',
  `kolicina` double NOT NULL default '0',
  `stanjekm` double NOT NULL default '0',
  `predjenokm` double NOT NULL default '0',
  `potrosnja` double NOT NULL default '0',
  UNIQUE KEY `rbr` (`rbr`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `autoexpres`.`zamenaulja`
--

/*!40000 ALTER TABLE `zamenaulja` DISABLE KEYS */;
INSERT INTO `zamenaulja` (`rbr`,`brsasije`,`regbroj`,`datum`,`kolicina`,`stanjekm`,`predjenokm`,`potrosnja`) VALUES 
 (1,'123456789','SM 456-789','2011-06-05',9,112000,0,0),
 (2,'2222222222222','SM 222-22      ','2011-01-01',6,56000,0,0);
/*!40000 ALTER TABLE `zamenaulja` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
