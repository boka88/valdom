import java.io.*;
import java.sql.*;
import java.sql.ResultSet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Action.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.lang.*;
import javax.swing.table.*;
import java.util.*;
import java.util.Set.*;
import java.text.*;
import javax.swing.KeyStroke.*;


public class mServis3
	{

	private ConnMySQL dbconn;
	private Connection connection = null;
	private String pPre,nazivPre,upit,upitlis,oznaka;
	private boolean semNemaPodataka = true,zbir,pog;
	private boolean imaNemaPreduzeca = true,imapravila = true,radi = true;
	private Integer jur,mag,ui,sifra,indikator,brun,red,redd,jurr,dojurr,magr,domagr,kontor,sifmr,dosifmr,brojslog;
	private Integer jurr1,magr1,kontor1,sifmr1,vrstaaz,najur,namag,brsl,sifm,nakonto,konto,red2,vrdok,prodavnica;
	private double u1,u2,cc,vv,cena,pcena,pcena1,vredn,kolic,vr,aduz,red1,privcen;  
	private int kontomal,kontorob,kontotr,kontorep,kontorez;
	private boolean nivmal,nivtr,nivrep,nivrob,nivrez;
	private String tblpromet="mprom";
 
    public mServis3(int _jurr,
						int _magr,
						int _kontor,
						int _sifmr) {
		jurr = _jurr;
		magr = _magr;
		kontor = _kontor;
		sifmr = _sifmr;
		pPre = new String("1");	
		tblpromet = tblpromet + pPre.trim();

		uzmiKonekciju();
		Start();
		zatvoriKonekciju();
		//hide();
	}
//------------------------------------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		try{
			connection = aLogin.konekcija;
		}catch (Exception f) {
			JOptionPane.showMessageDialog(null, "Ne mo\u017ee preuzeti konekciju:"+f);
		}
    }
//------------------------------------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
		/*
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(null, "Ne moze se zatvoriti konekcija");
			}
		}
		*/
   	}
//-----------------------------------------------------------------------------------
   public void Start() {
        //preuzimanje globalnih promenljivi
		kontomal = 1340;
		kontotr = 1320;
		kontorep = 1010;
		kontorob = 1320;
		kontorez = 1020;
		nivmal = false;
		nivtr = false;
		nivrep = false;
		nivrob = false;
		nivrez = false;

	   radi = true;
	   if (kontor == kontomal && nivmal == true)
	   {radi = false;}
	   if (kontor == kontotr && nivtr == true)
	   {radi = false;}
	   if (kontor == kontorep && nivrep == true)
	   {radi = false;}
	   if (kontor == kontorob && nivrob == true)
	   {radi = false;}
	   if (kontor == kontorez && nivrez == true)
	   {radi = false;}

			if (radi)
			{Pokreni();}
			else
	        {
				//JOptionPane.showMessageDialog(null, "Prezuriranje Izlaznih cena je ZAVRSENO !!!");
			}
      
	  }
//-----------------------------------------------------------------------------------
   public void Pokreni() {


		upit = "drop table if exists tmpazneaz";
		izvrsi(upit);
		upit = "drop table if exists tmpazneaz1";
		izvrsi(upit);
		upit = "drop table if exists tmpazneaz2";
		izvrsi(upit);

		  upit = "CREATE temporary TABLE  tmpazneaz  ( "+
          " pre  int(11) NOT NULL default '0', "+                            
          " jur  int(11) NOT NULL,"+                                         
          " mag  int(11) NOT NULL,"+                                         
          " ui  tinyint(4) NOT NULL,"+                                       
          " indikator  tinyint(4) NOT NULL,"+                                
          " brun  double NOT NULL,"+                                         
          " rbr  int(11) NOT NULL,"+                                         
          " vrdok int(11) NOT NULL default '0',"+
          " prodavnica int(11) NOT NULL default '0',"+
          " cena  double default '0',"+                                      
          " vredn  double default '0',"+                                     
          " konto  int(11) default '0',"+                                    
          " sifm  int(11) default '0',"+                                     
          " pcena  double default '0',"+                                     
          " pcena1  double default '0',"+                                    
          "PRIMARY KEY  ( pre , jur , mag , ui , indikator , brun , rbr ,vrdok ,prodavnica)"+  
		  ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
  		  izvrsi(upit);

		upit = "CREATE temporary TABLE  tmpazneaz1  ("+              
           " pre  int(11) default '0',"+          
           " jur  int(11) default NULL,"+         
           " mag  int(11) default NULL,"+         
           " konto  int(11) default '0',"+        
           " sifm  int(11) default '0'"+          
         ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
  		  izvrsi(upit);

		upit = "CREATE temporary TABLE  tmpazneaz2  ("+                                           
           " pre  int(11) NOT NULL default '0',"+                              
           " jur  int(11) NOT NULL,"+                                          
           " mag  int(11) NOT NULL,"+                                          
           " ui  tinyint(4) NOT NULL,"+                                        
           " indikator  tinyint(4) NOT NULL,"+                                 
           " brun  double NOT NULL,"+        
           " vrdok int(11) NOT NULL default '0',"+
           " prodavnica int(11) NOT NULL default '0',"+
           " rbr  int(11) NOT NULL,"+                                          
           "PRIMARY KEY  ( pre , jur , mag , ui , indikator , brun , vrdok, prodavnica, rbr )"+ 
           ") ENGINE=MyISAM DEFAULT CHARSET=utf8"; 
  		  izvrsi(upit);


		//upit = "delete from tmpazneaz2 where pre = " + Integer.parseInt(pPre);					
		//izvrsi(upit);

		//formiranje azneaz1 inicijalno
		//upit = "delete from tmpazneaz1 where pre = " + Integer.parseInt(pPre);	
		//izvrsi(upit);

		upit = "INSERT INTO tmpazneaz1(pre,jur,mag,konto,sifm) " +
		" SELECT DISTINCT "+tblpromet+".pre,"+tblpromet+".jur,"+tblpromet+".mag,"+tblpromet+".konto,"+tblpromet+".sifm FROM "+tblpromet+" " +
		" where "+tblpromet+".pre = " + Integer.parseInt(pPre) + 
		" and "+tblpromet+".jur = " + jurr +
		" and "+tblpromet+".mag = " + magr +
		" and "+tblpromet+".konto = " + kontor +
		" and "+tblpromet+".sifm = " + sifmr;
		izvrsi(upit);

			 brojslog = 1;

			 while (brojslog != 0)
			 //petlju prekida brojslog = 0
			 {

				Petlja();

				//procedura kojom se proverava dali treba zavrsiti ili nastaviti posao
				ProveriKraj();
				
				//prcedura za prepis azneaz u azneaz1 (pocetak)
				//OVO JE U BASICU PROCEDURA prepis
				upit = "delete from tmpazneaz1 where pre = " + Integer.parseInt(pPre);
				izvrsi(upit);

				upit = "INSERT INTO tmpazneaz1(pre,jur,mag,konto,sifm) SELECT distinct " +
				"tmpazneaz.pre,tmpazneaz.jur,tmpazneaz.mag,tmpazneaz.konto,tmpazneaz.sifm FROM tmpazneaz " + 
				" where tmpazneaz.pre = " + Integer.parseInt(pPre);
				izvrsi(upit);
				//procedura za prepis azneaz u azneaz1 (kraj)
			 } //od while brojslog

				//Dodatak za interni racun - Pocetak
				upit = "update "+tblpromet+" set mar = " +
				" ((cena - pcena *(zav/100) - pcena - pcena * (por1/100) - pcena * (zav/100) * (por1/100) ) " +
				" / (pcena + pcena * (zav/100) + pcena * (por1/100) + pcena * (zav/100) *(por1/100) )) * 100" +
				" where "+tblpromet+".pre = " + Integer.parseInt(pPre) +
				" and ui=1 and (vrdok=10 or vrdok=15) and pcena <> 0" +
				" and "+tblpromet+".jur = " + jurr +
				" and "+tblpromet+".mag = " + magr +
				" and "+tblpromet+".konto = " + kontomal +
				" and "+tblpromet+".sifm = " + sifmr ;
				izvrsi(upit);
				upit = "update "+tblpromet+" set mar = " +
				" ((pcena - cena *(zav/100) - cena - cena * (por1/100) - cena * (zav/100) * (por1/100) ) " +
				" / (cena + cena * (zav/100) + cena * (por1/100) + cena * (zav/100) *(por1/100) )) * 100" +
				" where "+tblpromet+".pre = " + Integer.parseInt(pPre) +
				" and ui=3 and (vrdok=10 or vrdok=15) and cena <> 0 " + 
				" and "+tblpromet+".jur = " + jurr +
				" and "+tblpromet+".mag = " + magr +
				" and konto = " + kontor + 
				" and "+tblpromet+".sifm = " + sifmr ;
				izvrsi(upit);
				//Dodatak za interni racun - Kraj


		Azcen(); //Azuriram prosecne cene

   }
//-----------------------------------------------------------------------------------
   public void Petlja() {

	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM tmpazneaz1 where pre = " + Integer.parseInt(pPre);
			try {
		         ResultSet rs = statement.executeQuery( query );
				int i=0;

					// petlja listanje slogova---------------
					while ( rs.next() ) {

			         	jurr1 = rs.getInt("jur");
			         	magr1 = rs.getInt("mag");
			         	kontor1 = rs.getInt("konto");
			         	sifmr1 = rs.getInt("sifm");

						Azneaz();
				
						//azuriranje ulaza medjuprometa izlazima medjuprometa
						upit = "update "+tblpromet+",tmpazneaz set "+tblpromet+".vredn = tmpazneaz.vredn," +
						""+tblpromet+".cena = tmpazneaz.cena,"+tblpromet+".pcena = tmpazneaz.pcena," +
						""+tblpromet+".pcena1 = tmpazneaz.pcena1,"+tblpromet+".oznaka = '-',"+tblpromet+".preneto1 = 0  " + 
						"where "+tblpromet+".pre = tmpazneaz.pre and "+tblpromet+".jur = tmpazneaz.jur and " + 
						""+tblpromet+".mag = tmpazneaz.mag and "+tblpromet+".ui = tmpazneaz.ui and " + 
						""+tblpromet+".indikator = tmpazneaz.indikator and "+tblpromet+".brun = tmpazneaz.brun " +
						"and "+tblpromet+".rbr = tmpazneaz.rbr and "+tblpromet+".pre = " + Integer.parseInt(pPre);
						izvrsi(upit);
		
				}

			         	//JOptionPane.showMessageDialog(null, "Prezuriranje IZLAZNIH cena je ZAVRSENO !!!");
					//kraj listanja--------------------------
					//brojslog = 0;
				if (i == 0){
	
				}		

			}
		      catch ( SQLException sqlex ) {
		      }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(null, "Greska u podacima");
		//brojslog = 0;
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
		}
		//.....................................................................................
  }
//-----------------------------------------------------------------------------------

    public void ProveriKraj() {
	  //Proveravam da li da nastavim posao ili je kraj
	  Statement statement = null;
      try {
         statement = connection.createStatement();

                String query = "SELECT * FROM tmpazneaz" +
			  	" WHERE pre=" + Integer.parseInt(pPre);
			try {
		        ResultSet rs = statement.executeQuery( query );
				rs.next();
				sifra = rs.getInt("sifm");
				brojslog = 1;
			}
		      catch ( SQLException sqlex ) {
					brojslog = 0;
		     }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(null, "Greska u podacima");
		brojslog = 0;
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
		}
		//.....................................................................................
  }
  //------------------------------------------------------------------------
     public void Slogova() {
	  //Proveravam broj slogova
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "select * from tmpazneaz2 where pre = " + Integer.parseInt(pPre) + 
					  " and jur = " + najur + " and mag = " + namag +
					  " and ui = 1 " + " and indikator = " + indikator +
					  " and brun = " + brun  + " and rbr = " + redd +
				      " and vrdok = " + vrdok + 
				      " and prodavnica = " + prodavnica;
			try {
		        ResultSet rs = statement.executeQuery( query );
				rs.next();
				sifra = rs.getInt("jur");
				brsl = 1;
				statement.close();
         		statement = null;
			}
		      catch ( SQLException sqlex ) {
					brsl = 0;
					statement.close();
         			statement = null;
		     }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(null, "Greska u podacima");
		brsl = 0;
      }
 		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
		}
		//.....................................................................................
 }
  //------------------------------------------------------------------------
 
   public void Azneaz() {

	u1 = 0.00;
	u2 = 0.00;

				upit = "delete from tmpazneaz where pre = " + Integer.parseInt(pPre);
				izvrsi(upit);

	  Statement statement = null;
      try {
         statement = connection.createStatement();
															
				 String query;

					query = "select * from "+tblpromet+" where pre = " + Integer.parseInt(pPre) + 
					" and jur = " + jurr1 + " and mag = " + magr1 + " and konto = " + kontor1 +
					" and sifm = " + sifmr1 + " order by datum, ui, kolic " ;

			try {
		         ResultSet rs = statement.executeQuery( query );
				int i=0;
					// petlja listanje slogova---------------
					while ( rs.next() ) {

			         	jur = rs.getInt("jur");
			         	mag = rs.getInt("mag");
			         	ui = rs.getInt("ui");
			         	sifm = rs.getInt("sifm");
			         	indikator = rs.getInt("indikator");
			         	brun = rs.getInt("brun");
			         	red = rs.getInt("rbr");
			         	vrdok = rs.getInt("vrdok");
			         	prodavnica = rs.getInt("prodavnica");
			         	najur = rs.getInt("najur");
			         	namag = rs.getInt("namag");
			         	nakonto = rs.getInt("nakonto");
			         	konto = rs.getInt("konto");
						cena = rs.getDouble("cena");
						pcena = rs.getDouble("pcena");
						pcena1 = rs.getDouble("pcena1");
						vredn = rs.getDouble("vredn");
						kolic = rs.getDouble("kolic");
						oznaka = rs.getString("oznaka");

						redd = red + 1;

						if ( oznaka.equals("-") && (ui == 0 ||  ui == 1) )
						{
				            u1 = u1 + rs.getDouble("kolic");
				            u2 = u2 + rs.getDouble("vredn");

						}
				        if ( ui == 2 || ui == 3 )
						{
											//ovde obavezno prikazi i u1 i kolic

								            if (u1 >=  rs.getDouble("kolic"))
											{

													                if (u1 != 0)
																	{ cc = (u2 / u1);}
													                else { cc = 0.00 ; }

													     u1 = u1 - rs.getDouble("kolic");

														upit = "UPDATE "+tblpromet+" set oznaka = " + "'-'" + 
														" WHERE pre = " + Integer.parseInt(pPre) +
														" AND jur = " + jur +
														" AND mag = " + mag +
														" AND ui = " + ui +
														" AND indikator = " + indikator +
														" AND brun = " + brun +
														" AND vrdok = " + vrdok +
													    " AND prodavnica = " + prodavnica +
														" AND rbr = " + red;
														izvrsi(upit);

											} // end if od u1 >0 rs.getDouble("kolic")
											else
											{

												upit = "UPDATE "+tblpromet+" set oznaka = " + "'+'" + 
												" WHERE pre = " + Integer.parseInt(pPre) +
												" AND jur = " + jur +
												" AND mag = " + mag +
												" AND ui = " + ui +
												" AND indikator = " + indikator +
												" AND brun = " + brun +
												" AND vrdok = " + vrdok +
												" AND prodavnica = " + prodavnica +
												" AND rbr = " + red;
												izvrsi(upit);
												cc = 0.00;
											}

							vv = cc * rs.getDouble("kolic");
	
							upit = "UPDATE "+tblpromet+" set cena = " + cc + " , vredn = " + vv + 
							" WHERE pre = " + Integer.parseInt(pPre) +
							" AND jur = " + jur +
							" AND mag = " + mag +
							" AND ui = " + ui +
							" AND indikator = " + indikator +
							" AND brun = " + brun +
							" AND vrdok = " + vrdok +
							" AND prodavnica = " + prodavnica +
							" AND rbr = " + red;
							izvrsi(upit);
		//JOptionPane.showMessageDialog(null, "cena:"+upit);
							cena = cc;
							vredn = vv;

							if (rs.getInt("vrdok") != 4 && rs.getInt("vrdok") != 10 && rs.getInt("vrdok") != 15)
							{
								upit = "UPDATE "+tblpromet+" set pcena = " + cc + " , pcena1 = " + cc + 
								" WHERE pre = " + Integer.parseInt(pPre) +
								" AND jur = " + jur +
								" AND mag = " + mag +
								" AND ui = " + ui +
								" AND indikator = " + indikator +
								" AND brun = " + brun +
								" AND vrdok = " + vrdok +
								" AND prodavnica = " + prodavnica +
								" AND rbr = " + red;
								izvrsi(upit);
								pcena = cc;
								pcena1 = cc;
							}


							u2 = u2 - vv;

							upit = "UPDATE "+tblpromet+" set preneto1 = 0" + 
							" WHERE pre = " + Integer.parseInt(pPre) +
							" AND jur = " + jur +
							" AND mag = " + mag +
							" AND ui = " + ui +
							" AND indikator = " + indikator +
							" AND brun = " + brun +
							" AND vrdok = " + vrdok +
							" AND prodavnica = " + prodavnica +
							" AND rbr = " + red;
							izvrsi(upit);

						} // end if od ui = 2 || ui = 3

		                if (rs.getInt("ui") == 3)
						{
							Slogova();
							if (brsl == 0)
							{
									
							upit = "INSERT INTO tmpazneaz2(pre,jur,mag,ui,indikator,brun,vrdok,prodavnica,rbr) values ( " +
							Integer.parseInt(pPre) + "," + najur + "," + namag + ",1" + "," +
							indikator + "," + brun + "," + vrdok + "," + prodavnica + "," + redd + ")" ;
							izvrsi(upit);

									//pamcenje ulaza medjuprometa
									if (rs.getInt("vrdok") == 8)
										{
										//medjupromet
										upit = "INSERT INTO tmpazneaz(pre,jur,mag,ui,indikator,brun,rbr,cena," +
										"pcena,pcena1,vredn,sifm,konto) values " +
										"( " + Integer.parseInt(pPre) + "," + najur + "," +
										namag + ",1" + "," + indikator + "," + brun + "," +
										redd + "," + cena + "," + pcena + "," + pcena1 + "," + vredn + "," +
										sifm + "," + nakonto + ")";
										izvrsi(upit);
										}
									else
										{
										//interni racun
										vr = kolic * pcena;
										upit = "INSERT INTO tmpazneaz(pre,jur,mag,ui,indikator,brun,rbr,vrdok,prodavnica,cena," +
										"pcena,pcena1,vredn,sifm,konto) values " +
										"( " + Integer.parseInt(pPre) + "," + najur + "," +
										namag + ",1" + "," + indikator + "," + brun + "," +
										redd +  "," + vrdok + "," + prodavnica + "," + pcena + "," + cena + "," + cena + "," + vr + "," +
										sifm + "," + konto + ")" ;
										izvrsi(upit);
										}
							}  // end if od brsl		

						} // end if od ui = 3
					
				}
				if (i == 0){
				}		

		}
		      catch ( SQLException sqlex ) {
		      }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(null, "Greska u podacima");
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
		}
		//.....................................................................................
  }
//-----------------------------------------------------------------------------------
   public void izvrsi(String _upit) {
//JOptionPane.showMessageDialog(null, "Upit:"+_upit);
	  Statement statement = null;
      try {
        statement = connection.createStatement();
		statement.executeUpdate( _upit );
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(null, "Greska:"+sqlex);
		JOptionPane.showMessageDialog(null, "Upit:"+_upit);
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
		}
		//.....................................................................................
 } 
//-----------------------------------------------------------------------------------
   public void Azcen() {
   // Azuriranje Prosecnih cena i cena Medjuskladisnica
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM "+tblpromet+" where pre = " + Integer.parseInt(pPre) + 
			   " and  (vrdok = 8 or vrdok = 9) and sifm = " + sifmr  +
			   "  order by brun , rbr";
   
			try {
		         ResultSet rs = statement.executeQuery( query );
				int i=0;


					// petlja listanje slogova---------------
					while ( rs.next() ) {

			         	jur = rs.getInt("jur");
			         	mag = rs.getInt("mag");
			         	ui = rs.getInt("ui");
			         	sifm = rs.getInt("sifm");
			         	indikator = rs.getInt("indikator");
			         	brun = rs.getInt("brun");
			         	red = rs.getInt("rbr");
			         	kolic = rs.getDouble("kolic");

						 red1 = ((double) red);
						 aduz = red1/2 - java.lang.Math.round(red1/2);
					

						 if (aduz == 0)
							{
							//Broj je paran
								
								vredn = kolic * privcen;
								upit = "UPDATE "+tblpromet+" set pcena = " + privcen + " , pcena1 = " + privcen +
								" , pcena1 = " + privcen + " , vredn = " + vredn +
								" WHERE pre = " + Integer.parseInt(pPre) +
								" AND jur = " + jur +
								" AND mag = " + mag +
								" AND ui = " + ui +
								" AND indikator = " + indikator +
								" AND brun = " + brun +
								" AND rbr = " + red;
								izvrsi(upit);

							}

						 else {
 							    //Broj je paran
                                //Zaokruzujem cenu na 7 decimala
								privcen = rs.getDouble("pcena");
							    privcen = privcen * 10000000 + 0.50;
								long prcni = (long) privcen ;
			                    privcen = ((double) prcni) / 10000000 ; 

					          }
								upit = "UPDATE "+tblpromet+" set preneto1 = 0" +
								" WHERE pre = " + Integer.parseInt(pPre) +
								" AND jur = " + jur +
								" AND mag = " + mag +
								" AND ui = " + ui +
								" AND indikator = " + indikator +
								" AND brun = " + brun +
								" AND rbr = " + red;
								izvrsi(upit);


				}

				if (i == 0){
				}		

			}
		      catch ( SQLException sqlex ) {
		      }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(null, "Greska u podacima");
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
		}
		//.....................................................................................
  }
//-----------------------------------------------------------------------------------
   public void Azcen1() {
	   /*
		upit = "Update msta Set kolicul = 0,koliciz = 0,vredul = 0,vrediz = 0," +
		"kolicme = 0,prc = 0,kolic = 0,vredn = 0 where pre =  " + Integer.parseInt(pPre);
		izvrsi(upit);


		//ULAZ
		upit = " create temporary table "+tblpromet+"p ( pre smallint(6) NOT NULL default '0', " +
		" jur smallint(6) NOT NULL default '0', " + 
		" mag smallint(6) NOT NULL default '0', " +  
		" konto int(11) NOT NULL default '0', " +
		" sifm int(11) NOT NULL default '0', " +
		" PRIMARY KEY  (pre,jur,mag,konto,sifm)) "  + 
	   "select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" and ("+tblpromet+".ui = 0 or "+tblpromet+".ui = 1 ) group by pre,jur,mag,konto,sifm";
		izvrsi(upit);

		upit = "Update msta,"+tblpromet+"p Set msta.kolicul = "+tblpromet+"p.kolic" +
		" , msta.vredul = "+tblpromet+"p.vredn where msta.pre = "+tblpromet+"p.pre and " +
		"msta.jur = "+tblpromet+"p.jur and " +
		"msta.mag = "+tblpromet+"p.mag and " +
		"msta.konto = "+tblpromet+"p.konto and " +
		"msta.sifm = "+tblpromet+"p.sifm";
		izvrsi(upit);

		upit = "drop table if exists "+tblpromet+"p";
		izvrsi(upit);


		//IZLAZ
		upit = " create temporary table "+tblpromet+"p ( pre smallint(6) NOT NULL default '0', " +
		" jur smallint(6) NOT NULL default '0', " + 
		" mag smallint(6) NOT NULL default '0', " +  
		" konto int(11) NOT NULL default '0', " +
		" sifm int(11) NOT NULL default '0', " +
		" PRIMARY KEY  (pre,jur,mag,konto,sifm)) "  + 
	   "select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" and ("+tblpromet+".ui = 2 or "+tblpromet+".ui = 3 ) group by pre,jur,mag,konto,sifm";
		izvrsi(upit);

		upit = "Update msta,"+tblpromet+"p Set msta.koliciz = "+tblpromet+"p.kolic" +
		" , msta.vrediz = "+tblpromet+"p.vredn where msta.pre = "+tblpromet+"p.pre and " +
		"msta.jur = "+tblpromet+"p.jur and " +
		"msta.mag = "+tblpromet+"p.mag and " +
		"msta.konto = "+tblpromet+"p.konto and " +
		"msta.sifm = "+tblpromet+"p.sifm";
		izvrsi(upit);

		upit = "drop table if exists "+tblpromet+"p";
		izvrsi(upit);

		//MEDJUPROMET
		upit = " create temporary table "+tblpromet+"p ( pre smallint(6) NOT NULL default '0', " +
		" jur smallint(6) NOT NULL default '0', " + 
		" mag smallint(6) NOT NULL default '0', " +  
		" konto int(11) NOT NULL default '0', " +
		" sifm int(11) NOT NULL default '0', " +
		" PRIMARY KEY  (pre,jur,mag,konto,sifm)) "  + 
	   "select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" and "+tblpromet+".ui = 1 and "+tblpromet+".oznaka = '+' group by pre,jur,mag,konto,sifm";
		izvrsi(upit);


		upit = "Update msta,"+tblpromet+"p Set msta.kolicme = "+tblpromet+"p.kolic" +
		" where msta.pre = "+tblpromet+"p.pre and " +
		"msta.jur = "+tblpromet+"p.jur and " +
		"msta.mag = "+tblpromet+"p.mag and " +
		"msta.konto = "+tblpromet+"p.konto and " +
		"msta.sifm = "+tblpromet+"p.sifm";
		izvrsi(upit);

		upit = "drop table if exists "+tblpromet+"p";
		izvrsi(upit);

		upit = "update msta set kolicul = 0 where kolicul is null";
		izvrsi(upit);
		upit = "update msta set koliciz = 0 where koliciz is null";
		izvrsi(upit);
		upit = "update msta set kolicme = 0 where kolicme is null";
		izvrsi(upit);
		upit = "update msta set vredul = 0 where vredul is null";
		izvrsi(upit);
		upit = "update msta set vrediz = 0 where vrediz is null";
		izvrsi(upit);

		upit = "update msta set vredul = round(vredul,5) , vrediz = round(vrediz,5) " +
		" , koliciz = round(koliciz,5) , kolicul = round(kolicul,5) , " +
		"kolicme = round(kolicme,5) " ;
		izvrsi(upit);

		upit = "Update msta Set kolic = kolicul - koliciz - kolicme " +
		" , vredn = vredul - vrediz " +
		"where pre =  " + Integer.parseInt(pPre);
		izvrsi(upit);

		upit = "Update msta Set prc = (vredul - vrediz) / (kolicul - koliciz - kolicme) " +
		"where kolicul - koliciz - kolicme > 0 and pre =  " + Integer.parseInt(pPre);
		izvrsi(upit);

		upit = "Update msta Set prc = 0 where kolicul - koliciz - kolicme <= 0 " +
		"and pre =  " + Integer.parseInt(pPre);
		izvrsi(upit);
								
		upit = "update msta set oznaka = '-'" +
		" where msta.pre = " + Integer.parseInt(pPre);
		izvrsi(upit);


		upit = " create temporary table "+tblpromet+"p ( pre smallint(6) NOT NULL default '0', " +
		" jur smallint(6) NOT NULL default '0', " +
		" mag smallint(6) NOT NULL default '0', " +  
		" konto int(11) NOT NULL default '0', " +
		" sifm int(11) NOT NULL default '0', " +
		" PRIMARY KEY  (pre,jur,mag,konto,sifm)) "  + 
	   "select pre,jur,mag,konto,sifm,oznaka from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" and "+tblpromet+".oznaka = '+' group by pre,jur,mag,konto,sifm";
		izvrsi(upit);

		upit = "Update msta,"+tblpromet+"p Set msta.oznaka = "+tblpromet+"p.oznaka" +
		" where msta.pre = "+tblpromet+"p.pre and " +
		"msta.jur = "+tblpromet+"p.jur and " +
		"msta.mag = "+tblpromet+"p.mag and " +
		"msta.konto = "+tblpromet+"p.konto and " +
		"msta.sifm = "+tblpromet+"p.sifm";
		izvrsi(upit);

		upit = "drop table if exists "+tblpromet+"p";
		izvrsi(upit);
		*/
   }
//------------------------------------------------------------------------------------------------------------------
} //end of class IborPre


