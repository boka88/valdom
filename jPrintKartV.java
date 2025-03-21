import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import javax.swing.*;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperManager;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.JRResultSetDataSource;

public class jPrintKartV {

	public Connection connection = null;
	private String fileName = null,nazivpre="",mestopre="",pPre="",sql,nazrobe;
	private int odmes=0,domes=0,pred,koji;
	private int odrj,dorj,odmag,domag,konto;
	private String radjed="",magacin="",nazivjur="",nazivmag="",nazkonta,sifra;
	private String kolul="",koliz="",stanjekol="",vrul="",vriz="",stanje="",prc="";

	public jPrintKartV(Connection _connection,
						int _odrj,
						int _dorj,
						int _odmag,
						int _domag,
						String _sifra,
						int _koji,
						String _nazkonta){
		connection = _connection;
		odrj = _odrj;
		dorj = _dorj;
		odmag = _odmag;
		domag = _domag;
		//konto = _konto;
		sifra = _sifra;
		koji = _koji;
		nazkonta = _nazkonta;
		//nazrobe = _nazrobe;
		//kolul = _kolul;
		//vrul = _vrul;
		//koliz=_koliz;
		//vriz = _vriz;
		//prc=_prc;
		//stanjekol = _stanjekol;
		//stanje = _stanje;

		pPre = "1";
		pred = 1;

		nadjiPreduzece();
		//popuniUkupno();

		if (odrj == dorj)
		{
			if (odmag == 99)
			{
				nadjiProdavnicu();
				radjed = "Prodavnica: " + nazivjur;
			}else{
				//nadjiRJ();
				radjed = "Radna jedinica: 1 ";
			}
		}else{
			if (odmag == 99)
			{
				radjed = "Prodavnica: od " + String.valueOf(odrj) + 
					" do " + String.valueOf(dorj);
			}else{
				radjed = "Radna jedinica: od " + String.valueOf(odrj) + 
					" do " + String.valueOf(dorj);
			}
		}

		if (odmag == domag)
		{
			nadjiMag();
			magacin = "Magacin: " + nazivmag; 
		}else{
			magacin = "Magacin: od " + String.valueOf(odmag) + 
				" do " + String.valueOf(domag); 
		}

		Map<String, String> parameters = new HashMap<String, String>();
		try
		{
			JasperDesign jasperDesign = null;
			jasperDesign = JasperManager.loadXmlDesign("print/kartvoz.jrxml");
			parameters.put("nazivpre", nazivpre);
			//parameters.put("radjed", radjed);
			parameters.put("magacin", magacin);
			//parameters.put("nazkonta", nazkonta);
			//parameters.put("nazrobe", nazrobe);
			//parameters.put("kolul", kolul);
			//parameters.put("koliz", koliz);
			//parameters.put("stanjekol", stanjekol);
			//parameters.put("vrul", vrul);
			//parameters.put("vriz", vriz);
			//parameters.put("stanje", stanje);
			//parameters.put("prc", prc);

			JasperReport jasperReport = JasperManager.compileReport(jasperDesign);
		
			//dodavanje eksternih varijabli u report*********************
			if (koji == 3)
			{
				parameters.put("naziv", "SELEKCIJA");
			}else if (koji == 2)
			{
				parameters.put("naziv", " ");
			}
			//***********************************************************
			JasperPrint jasperPrint=null;
			jasperPrint = uzmiPodatke(jasperReport,parameters);
			//*********************************************************************

			JasperViewer.viewReport(jasperPrint,false);
		}
		catch (JRException ee)
		{
			JOptionPane.showMessageDialog(null, "Gre\u0161ka:" + ee);
			return;
		}
	}
//--------------------------------------------------------------------------------------
	private JasperPrint uzmiPodatke(JasperReport _jasperReport,Map<String, String> _parameters){
		JasperPrint jrprint = null;
		JRResultSetDataSource obj = null;

		try {
		Statement statement = null;
		String sql = "SELECT * FROM tmpkarticarobe ORDER BY datum,vrdok,brdok";
			statement = connection.createStatement();

			ResultSet rss = statement.executeQuery(sql);

			obj = new JRResultSetDataSource(rss);
			jrprint = JasperFillManager.fillReport(
					_jasperReport,_parameters, obj);
		}catch(Exception e) {
		}
		return jrprint;
	}
//--------------------------------------------------------------------------------------
    public void nadjiPreduzece() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM preduzeca WHERE Pre=" + pred;

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					nazivpre = rs.getString("Naziv1"); 
					mestopre = rs.getString("Mesto"); 
					rs.close();
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje za preduzece ");
		      }
	  }     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju preduzeca ");
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
   //-----------------------------------------------------------------------------------------------------
    public void nadjiRJ() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM radnejedinice WHERE jur=" + odrj +
				   " AND pre=" + Integer.parseInt(pPre);

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					nazivjur = rs.getString("nazjur");
				}else{
					JOptionPane.showMessageDialog(null, "Podaci ne postoje u Radn.jed.");
				}
		      }
		      catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(null, "Greska:"+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju preduze\u0107a");
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
   //-----------------------------------------------------------------------------------------------------
    public void nadjiProdavnicu() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM prodavnice WHERE prod=" + odrj +
				   " AND pre=" + Integer.parseInt(pPre);

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					nazivjur = rs.getString("nazprod");
				}else{
					JOptionPane.showMessageDialog(null, "Podaci ne postoje u Prodavnicama");
				}
		      }
		      catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(null, "Greska:"+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Gre\u0161ka u tra\u017eenju preduze\u0107a");
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
 //-----------------------------------------------------------------------------------------------------
    public void nadjiMag() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM magacini WHERE mag=" + odmag +
				   " AND pre=" + Integer.parseInt(pPre);

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					nazivmag = rs.getString("nazivm");
				}else{
					JOptionPane.showMessageDialog(null, "Podaci ne postoje u Magacinima");
				}
		      }
		      catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(null, "Greska:"+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska:"+sqlex);
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
 //-----------------------------------------------------------------------------------------------------
 private void popuniUkupno() {

	//uzimanje podataka---------------------------------------------------
		String sql = "SELECT ROUND(SUM(kolicul),2),ROUND(SUM(koliciz),2)," +
				" ROUND(SUM(kolicul)-SUM(koliciz),2),ROUND(SUM(vredul),2), " +
				"ROUND(SUM(vrediz),2),ROUND(SUM(vredul)-SUM(vrediz),2)," +
				"ROUND((SUM(vredul)-SUM(vrediz))/(SUM(kolicul)-SUM(koliciz)),2) FROM msta " +
					"WHERE msta.pre = " + Integer.parseInt(pPre) + " and msta.jur >= " + odrj + 
					" and msta.jur <= " + dorj + " and msta.mag >= " + odmag + 
					" and msta.mag <= " + domag + " and  msta.konto = " + konto + 
					" and msta.sifm = " + sifra;

	  Statement statement = null;
      try {
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery( sql );
        if ( rs.next() ) {

				kolul = rs.getString("ROUND(SUM(kolicul),2)");
				koliz = rs.getString("ROUND(SUM(koliciz),2)");
				stanjekol = rs.getString("ROUND(SUM(kolicul)-SUM(koliciz),2)");
				vrul = rs.getString("ROUND(SUM(vredul),2)");
				vriz = rs.getString("ROUND(SUM(vrediz),2)");
				stanje = rs.getString("ROUND(SUM(vredul)-SUM(vrediz),2)");
				prc = rs.getString("ROUND((SUM(vredul)-SUM(vrediz))/(SUM(kolicul)-SUM(koliciz)),2)");
           }
       }catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(null, "Greska u ukupno: "+sqlex);
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
 //-----------------------------------------------------------------------------------------------------
}
