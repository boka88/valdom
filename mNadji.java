//program za trazenje naziva u sifarnicima
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Set.*;
import java.beans.PropertyVetoException.*;

public class mNadji {
	//private Connection connection = null;
 public mNadji() {
 }
 //-----------------------------------------------------------------------------------------------------
 public void izlaz(){
  		 
 }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiPreduzece(Connection connection,int pre) {
		String preduzece="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM preduzeca WHERE pre=" + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					preduzece = rs.getString("Naziv1");
				}else{
					preduzece = "nema og preduzeca";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u preduze\u0107ima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju preduze\u0107a");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return preduzece;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiPIB(Connection connection,int pre) {
		String pib="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM preduzeca WHERE pre=" + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					pib = rs.getString("pib").trim();
				}else{
					pib = "0";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u preduze\u0107ima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju preduze\u0107a");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return pib;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiNaziv(Connection connection,int pre) {
		String naziv="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM preduzeca WHERE pre=" + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					naziv = rs.getString("Naziv1");
				}else{
					naziv = " ";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u preduze\u0107ima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju preduze\u0107a");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return naziv;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiAdresu(Connection connection,int pre) {
		String adresa="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM preduzeca WHERE pre=" + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					adresa = rs.getString("Adresa")+",  "+rs.getString("Mesto");
				}else{
					adresa = " ";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u preduze\u0107ima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju preduze\u0107a");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return adresa;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiRJ(Connection connection,int odrj,int pre) {
		String rj="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM radnejedinice WHERE jur=" + odrj +
				   " AND pre=" + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					rj = rs.getString("nazjur");
				}else{
					rj = "nema te radne jedinice";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u preduze\u0107a ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju preduze\u0107a");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return rj;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiMag(Connection connection,int magacin, int pre) {
		String mag="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM magacini WHERE mag=" + magacin +
				   " AND pre=" + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					mag = rs.getString("nazivm");
				}else{
					mag = "nema tog magacina";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u magacinima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju magacina");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return mag;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiKonto(Connection connection,int konto) {
		String nazivkonta="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM materijalnakonta WHERE konto=" + konto;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					nazivkonta = rs.getString("nazivk");
				}else{
					nazivkonta = "nema tog konta";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u kontima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju konta");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return nazivkonta;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjifKonto(Connection connection,int konto) {
		String nazivkonta="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM kontniplan WHERE konto='" + konto + "'";

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					nazivkonta = rs.getString("nazivk");
				}else{
					nazivkonta = "nema tog konta";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u fin. kontima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju fin. konta");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return nazivkonta;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiRobu(Connection connection,int roba) {
		String nazivrobe="";
		Statement statement=null;
		 
		String pPre = new String("1");

      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM sifarnikrobe WHERE sifm=" + roba + 
				   " AND pre=" + Integer.parseInt(pPre);

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					nazivrobe = rs.getString("nazivm");
				}else{
					nazivrobe = "nema te robe";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u sifarniku robe ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju robe");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return nazivrobe;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiMT(Connection connection,int mtros,int pre) {
		String mt="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM mestatroska WHERE mtros=" + mtros +
				   " and pre="  + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					mt = rs.getString("naztros");
				}else{
					mt = "nema tog mesta troska";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u mestima troska ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju mesta troska");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return mt;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiKupca(Connection connection,int brkupca) {
		String kupac="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM saradnici WHERE kupacbr=" + brkupca;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					kupac = rs.getString("nazivkupca");
				}else{
					kupac = "nema tog kupca";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u kupcima ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju kupca");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return kupac;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiProdavnicu(Connection connection,int brprod,int pre) {
		String prod="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM prodavnice WHERE prod=" + brprod +
				   " AND pre=" + pre;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					prod = rs.getString("nazprod");
				}else{
					prod = "nema te prodavnice";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u prodavnicama ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju prodavnice");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return prod;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiJMere(Connection connection,int _sifm) {
		String jmere="";
		Statement statement=null;
		 
		String pPre = new String("1");

      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM sifarnikrobe WHERE sifm=" + _sifm +
				   " AND pre=" + Integer.parseInt(pPre);

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					jmere = rs.getString("jmere");
				}else{
					jmere = "nema JM";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u sifarniku robe ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju jed.mere");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return jmere;
  }
   //-----------------------------------------------------------------------------------------------------
    public boolean proveriKonto(Connection connection,int konto) {
		boolean ima=false;
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM materijalnakonta WHERE konto=" + konto;

		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					ima = true;
				}
		}     
      catch ( SQLException sqlex ) {
			System.out.println("Gre\u0161ka u tra\u017eenju konta");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return ima;
  }
 //-----------------------------------------------------------------------------------------------------
    public String nadjiVK(Connection connection,int vrdok) {
		String kupac="";
		Statement statement=null;
		 
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM dokumentifinansijsko WHERE sifdok=" + vrdok;

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					kupac = rs.getString("nazdok");
				}else{
					kupac = "nema te vrste knjizenja";
				}
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u dokumentifinansijsko ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju vrste knjizenja");
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
			 
	  }
	  return kupac;
  }
//--------------------------------------------------------------------------------------
 }//end of class fPrintKarKon
