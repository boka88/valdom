/*

ubaciti u bazu:
-------------------
CREATE TABLE masine  (
   sifra  INT(5) NOT NULL,
   naziv  VARCHAR(50) NOT NULL DEFAULT '',
   tip  VARCHAR(50) NOT NULL DEFAULT '',
   fabbroj  VARCHAR(50) NOT NULL DEFAULT '',
   invbroj  VARCHAR(50) NOT NULL DEFAULT '',
   proizvodjac  VARCHAR(50) NOT NULL DEFAULT '',
   godizrade  VARCHAR(50) NOT NULL DEFAULT '',
   napomena  VARCHAR(50) NOT NULL DEFAULT '',
  PRIMARY KEY ( sifra )
) ENGINE=MYISAM DEFAULT CHARSET=utf8;


CREATE TABLE  kvarovi  (
   rbr  int(11) NOT NULL,
   sifra  int(5) NOT NULL DEFAULT '0',
   naziv  varchar(50) NOT NULL DEFAULT ' ',
   datum  date NOT NULL DEFAULT '0000-00-00',
   sifraradnika int(5) NOT NULL DEFAULT '0',
   imeprezime  varchar(50) NOT NULL DEFAULT ' ',
   opiskvara  mediumtext NOT NULL,
   opisradova  mediumtext NOT NULL,
   delovi  mediumtext NOT NULL,
   napomena  mediumtext NOT NULL,
  UNIQUE KEY  rbr  ( rbr )
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE  operateri  (
   sifra  int(11) NOT NULL,
   imeprezime  varchar(50) DEFAULT NULL,
   radnomesto  varchar(50) DEFAULT NULL,
   napomena  varchar(50) DEFAULT NULL,
  PRIMARY KEY ( sifra )
) ENGINE=MyISAM DEFAULT CHARSET=utf8;










*/