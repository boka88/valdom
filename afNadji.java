//program za trazenje u finansijskom
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Set.*;
import java.beans.PropertyVetoException.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class afNadji {
 public afNadji() {
 }
 //-----------------------------------------------------------------------------------------------------
 public void izlaz(){
  		 
 }
 //-----------------------------------------------------------------------------------------------------
    public int fNadjiBrojUnosa(Connection connection,int _pre,int _jur,int _mag,
								String _vrdok,int _ui,int _indikator) {
		int brunosa=0;
		Statement statement=null;
      try {
			statement = connection.createStatement();
               String query = "SELECT MAX(brun) FROM mprom WHERE" +
				   " pre=" + _pre + " AND " +
				   " jur=" + _jur + " AND " +
				   " mag=" + _mag + " AND " +
				   " ui=" + _ui + " AND " +
				   " indikator=" + _indikator + _vrdok;
			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					brunosa = rs.getInt(1);
					brunosa = brunosa + 1;
				}
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri:aNadji.mNadjiBrojUnosa: "+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri :aNadji.NadjiBrojUnosa: "+sqlex);
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
	  }
	  return brunosa;
  }
 //-----------------------------------------------------------------------------------------------------
    public int fNadjiNovuStavku(Connection connection,int _pre,int _jur,int _mag,
								int _brun,int _ui,int _indikator,String _vrdok) {
		int stavka=1;
		Statement statement=null;
      try {
			statement = connection.createStatement();
               String query = "SELECT MAX(rbr) FROM mprom WHERE" +
				   " pre=" + _pre + " AND " +
				   " jur=" + _jur + " AND " +
				   " mag=" + _mag + " AND " +
				   " ui=" + _ui + " AND " +
				   " brun=" + _brun + " AND " +
				   " indikator=" + _indikator + _vrdok;
			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					stavka = rs.getInt(1);
					stavka = stavka + 1;
				}
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri:aNadji.mNadjiNovuStavku: "+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri :aNadji.mNadjiNovuStavku: "+sqlex);
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
	  }
	  return stavka;
  }
 //-----------------------------------------------------------------------------------------------------
    public double mNadjiPRC(Connection connection,int _pre,int _jur,int _mag,
								int _sifm,int _konto) {
		double prc=0.0;
		Statement statement=null;
      try {
			statement = connection.createStatement();
               String query = "SELECT ROUND((SUM(kolicul*cena)-SUM(koliciz*cena))/(SUM(kolicul)-SUM(koliciz)),2)" +
				   " FROM mprom WHERE pre=" + _pre + 
				   " AND jur=" + _jur + " AND " +
				   " mag=" + _mag + " AND " +
				   " sifm=" + _sifm + " AND " +
				   " konto=" + _konto;
			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					prc = rs.getDouble(1);
					if (prc < 0)
					{
						prc = 0.0;
					}
				}
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri:aNadji.mNadjiNovuStavku: "+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri :aNadji.mNadjiNovuStavku: "+sqlex);
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
	  }
	  return prc;
  }
 //-----------------------------------------------------------------------------------------------------
    public double mNadjiKolicinu(Connection connection,int _pre,int _jur,int _mag,
								int _sifm,int _konto) {
		double kolicina=0.0;
		Statement statement=null;
      try {
			statement = connection.createStatement();
               String query = "SELECT ROUND(SUM(kolicul)-SUM(koliciz),2) FROM mprom WHERE pre=" + _pre + 
				   " AND jur=" + _jur + " AND " +
				   " mag=" + _mag + " AND " +
				   " sifm=" + _sifm + " AND " +
				   " konto=" + _konto;
			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					kolicina = rs.getDouble(1);
				}
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri:aNadji.mNadjiNovuStavku: "+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Gre\u0161ka u proceduri :aNadji.mNadjiNovuStavku: "+sqlex);
      }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
	  }
	  return kolicina;
  }
//------------------------------------------------------------------------------------------------------------------
	public void obrisiPomocniNultiSlog(Connection connection,int _pre,int _jur,int _mag,
										int _ui,int _indikator,int _brun,String _vrdok) {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               	String query = "DELETE FROM mprom WHERE pre=" + 
					_pre + " AND " +
					" jur=" + _jur+ " AND " +	
					" mag=" + _mag + " AND " +	
					" ui=" + _ui + " AND " +	
					" indikator=" + _indikator + " AND " +	
					" brun=" + _brun+ " AND " +	
					" ui=0 AND indikator=0 AND rbr=0 AND sifm=0 AND konto=0" +
					_vrdok;

               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
				}     
            	else {
            		//JOptionPane.showMessageDialog(null, "Pomo\u0107ni slog se ne mo\u017ee izbrisati");
            	}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska u brisanju pomocnog sloga:"+sqlex);
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
//------------------------------------------------------------------------------------------------------------------
	public void unesiPomocniNultiSlog(Connection connection,int _pre,int _jur,int _mag,
										int _ui,int _indikator,int _brun,String _datum,
										int _vrdok) {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "INSERT INTO mprom (pre,jur,mag,ui,indikator,brun,sifm,konto,rbr,datum,z,vrdok)" +
				" VALUES (" + _pre + "," +		
				_jur + "," +		
				_mag + "," + _ui + "," + _indikator + "," + _brun + "," +		
				"0,0,0,'" + _datum + "',1," + _vrdok + ")";										

               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
				}     
            	else {
            		//JOptionPane.showMessageDialog(null, "Pomo\u0107ni slog se ne mo\u017ee izbrisati");
            	}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska u brisanju pomocnog sloga:"+sqlex);
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
//--------------------------------------------------------------------------------------
 }//end of class aNadji
