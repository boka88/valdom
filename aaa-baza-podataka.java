/*
kreirana tabela - sifarnik radnika --------------------------------
CREATE TABLE  radnici  (
   sifra  int(5) NOT NULL default '0',
   ime  varchar(20) NOT NULL default ' ',
   passwd varchar(50) NOT NULL default ' ',
   stat int(1) NOT NULL default '0',
   UNIQUE KEY  sifra  ( sifra )
) ENGINE=MyISAM DEFAULT CHARSET=utf8
-------------------------------------------------------------------
09-04-2010
----------
ubaciti u tabelu "rnal" polje z int(1) not null default '0'
i iskljuciti u indeksu polje sifra da ostane samo rbr
u primarnom indeksu





*/