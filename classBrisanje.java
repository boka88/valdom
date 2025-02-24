//klasa za razdvajanje PDV - a
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Set.*;
import javax.swing.*;
import java.beans.PropertyVetoException.*;

public class classBrisanje {

 public classBrisanje() {
 }
//-----------------------------------------------------------------------------------------------------
    public boolean proveriStavku(Connection connect,int _pre,int _jur,int _vrprom,int _nalog,int _rbr) {
		double dug=0,pot=0;
		String query = "SELECT dugu,potr FROM fprom WHERE " +
			" pre=" + _pre + " AND jur=" + _jur + " AND vrprom=" + _vrprom +
			" AND nalog=" + _nalog + " AND rbr=" + _rbr;

		Statement statement=null;
		try {
			statement = connect.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					dug = rs.getDouble("dugu");
					pot = rs.getDouble("potr");
					if (dug == 0 && pot == 0)
					{
						return true;
					}
				}
				rs.close();
		}catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "greska classBrisanje.proveriStavku:"+sqlex);
		}finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
		}
		return false;
  }
//-----------------------------------------------------------------------------------------------------
    public void izbrisiStavku(Connection connect,int _pre,int _jur,int _vrprom,int _nalog,int _rbr) {
		//brise stavku iz prometa fprom
		String sql = "DELETE FROM fprom " + 
						"  WHERE pre=" + _pre + " AND jur=" + _jur + " AND vrprom=" + _vrprom +
						" AND nalog=" + _nalog + " AND rbr=" + _rbr;
		izvrsi(connect,sql);
		//smanjuje broj stavki za 1 u fznal
		sql = "UPDATE fznal SET stav=stav-1 " + 
						"  WHERE pre=" + _pre + " AND vrprom=" + _vrprom +
						" AND nalog=" + _nalog;
		izvrsi(connect,sql);
  }
//-----------------------------------------------------------------------------------------------------
    public void preazurirajRbr(Connection connect,int _pre,int _jur,int _vrprom,int _nalog,int _rbr) {
		int i=1,rbr=0;
		String sql="";
		String query = "SELECT * FROM fprom WHERE " +
			" pre=" + _pre + " AND jur=" + _jur + " AND vrprom=" + _vrprom +
			" AND nalog=" + _nalog + " ORDER BY rbr";

		Statement statement=null;
        try {
			statement = connect.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        while(rs.next()){
					rbr = rs.getInt("rbr");
					sql = "UPDATE fprom SET rbr=" + i + 
						"  WHERE pre=" + _pre + " AND jur=" + _jur + " AND vrprom=" + _vrprom +
						" AND nalog=" + _nalog + " AND rbr=" + rbr;
					
					izvrsi(connect,sql);

					i = i +1;
				}
				rs.close();
	    }catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "greska classBrisanje.preazurirajRbr:"+sqlex);
        }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
	    }
  }
 //====================================================================================================
	public void izvrsi(Connection connection,String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska classBrisanje: " + sqlex);
			JOptionPane.showMessageDialog(null, "Upit:" + sql);
      }
	  finally{
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
	  }
  }
 //====================================================================================================
    public boolean proveriStavkuRobnoKolic(Connection connect,int _pre,int _jur,int _mag,
										int _vrdok,int _brun,int _rbr) {
		String query = "";
		double kolic=0;
		//ako je faktura iz veleprodaje ili proizvodnje
		query = "SELECT kolic FROM mprom1 WHERE " +
				" pre=" + _pre + " AND jur=" + _jur + " AND vrdok=" + _vrdok +
				" AND mag=" + _mag +
				" AND brun=" + _brun + " AND rbr=" + _rbr;
		Statement statement=null;
		try {
			statement = connect.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					kolic = rs.getDouble("kolic");
					if ( kolic == 0 )
					{
						return true;
					}
				}
				rs.close();
		}catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "greska classBrisanje.proveriStavkuRobno:"+sqlex);
		}finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
		}
		return false;
  }
 //====================================================================================================
    public boolean proveriStavkuRobnoPreneto(Connection connect,int _pre,int _jur,int _mag,
										int _vrdok,int _brun,int _rbr) {
		String query = "";
		int preneto = 0;
		//ako je faktura iz veleprodaje ili proizvodnje
		if (_vrdok == 4 && _mag == 0)
		{
			query = "SELECT preneto FROM mprom1 WHERE " +
				" pre=" + _pre + " AND jur=" + _jur + " AND vrdok=" + _vrdok +
				" AND mag=<99" + 
				" AND brun=" + _brun + " AND rbr=" + _rbr;

		}else{
			query = "SELECT preneto FROM mprom WHERE " +
				" pre=" + _pre + " AND jur=" + _jur + " AND vrdok=" + _vrdok +
				" AND mag=" + _mag +
				" AND brun=" + _brun + " AND rbr=" + _rbr;
		}
		Statement statement=null;
		try {
			statement = connect.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					preneto = rs.getInt("preneto");
					if ( preneto == 0)
					{
						return true;
					}
				}
				rs.close();
		}catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "greska classBrisanje.proveriStavkuRobno:"+sqlex);
		}finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
		}
		return false;
  }
//-----------------------------------------------------------------------------------------------------
    public void izbrisiStavkuRobno(Connection connect,int _pre,int _jur,int _mag,
									int _vrdok,int _brun,int _rbr) {
		String opis="";
		switch (_vrdok)
		{
			case 0 :
				opis = "Pocetno stanje";
				break;
			case 1 :
				opis = "Ulaz";
				break;
			case 2 :
				opis = "Ulaz";
				break;
			case 3 :
				opis = "Izdatnice";
				break;
			case 6 :
				opis = "Nivelacija";
				break;
			case 7 :
				opis = "Ulaz";
				break;
			case 10 :				//interni racun sa razduzenjem
				opis = "Interni racun";
				break;
			case 11 :				//kasa maloprodaje
				opis = "Kasa";
				break;
			case 12 :
				opis = "Rashod";
				break;
			case 13 :
				opis = "Rashod";
				break;
			case 15 :				//interni racun bez razduzenja
				opis = "Interni racun-1";
				break;
			case 16 :
				opis = "Povrat-robe";
				break;
			case 17 :
				opis = "IzlazSP";
				break;
			case 33 :
				opis = "Ulaz";
				break;
			case 66 :
				opis = "Izdatnice";
				break;
		}
		String sql = "";

		sql = "DELETE FROM mprom1 WHERE " + 
					" pre=" + _pre + " AND jur=" + _jur + " AND vrdok=" + _vrdok +
					" AND brun=" + _brun + " AND mag=" + _mag + " AND rbr=" + _rbr;
		izvrsi(connect,sql);
		
  }
//-----------------------------------------------------------------------------------------------------
    public void preazurirajRbrRobno(Connection connect,int _pre,int _jur,int _mag,
									int _vrdok,int _brun,int _rbr) {
		int i=1,rbr=0;
		String sql="";
		String query = "SELECT * FROM mprom1 WHERE " +
			" pre=" + _pre + " AND jur=" + _jur + " AND vrdok=" + _vrdok +
			" AND brun=" + _brun + " AND mag=" + _mag + " ORDER BY rbr";

		Statement statement=null;
        try {
			statement = connect.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        while(rs.next()){
					rbr = rs.getInt("rbr");
					sql = "UPDATE mprom1 SET rbr=" + i + 
						"  WHERE pre=" + _pre + " AND jur=" + _jur + " AND vrdok=" + _vrdok +
						" AND brun=" + _brun + " AND mag=" + _mag + " AND rbr=" + rbr;
					
					izvrsi(connect,sql);

					i = i +1;
				}
				rs.close();
	    }catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "greska classBrisanje.preazurirajRbr:"+sqlex);
        }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
	    }
  }
//--------------------------------------------------------------------------------------
 }//end of class classPDV
