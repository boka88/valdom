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

public class jPrintLager {

	public Connection connection = null;
	private String fileName = null,nazivpre="",mestopre="",pPre="",sql,nazrobe;
	private int odmes=0,domes=0,pred,koji,kkk;
	private int odrj,dorj,odmag,domag,konto,odsifre,dosifre;
	private String radjed="",magacin="",nazivjur="",nazivmag="",nazivkonta,oddat="",dodat="";
	private String kolul="",koliz="",stanjekol="",vrul="",vriz="",stanje="",prc="";

	public jPrintLager(Connection _connection,
						int _odrj,
						int _dorj,
						int _odmag,
						int _domag,
						int _konto,
						int _odsif,
						int _dosif,
						String _oddat,
						String _dodat,
						String _nazivk,
						int _kkk){
		connection = _connection;
		odrj = _odrj;
		dorj = _dorj;
		odmag = _odmag;
		domag = _domag;
		konto = _konto;
		oddat = _oddat;
		dodat = _dodat;
		kkk = _kkk;

		pPre = new String("1");
		pred = Integer.parseInt(pPre);

		mNadji mn = new mNadji();
		if (kkk != 1)
		{
			nazivjur = mn.nadjiProdavnicu(connection,odrj,pred);
		}else{
			nazivjur = mn.nadjiRJ(connection,odrj,pred);
		}
		nadjiPreduzece();
		//popuniUkupno();
		nazivkonta = mn.nadjiKonto(connection,konto);

		if (odrj == dorj)
		{
			if (odmag == 99)
			{
				radjed = "Prodavnica: " + nazivjur;
			}else{
				radjed = "Radna jedinica: " + nazivjur;
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
			jasperDesign = JasperManager.loadXmlDesign("print/lager.jrxml");
			parameters.put("nazivpre", nazivpre);
			parameters.put("radjed", radjed);
			parameters.put("magacin", magacin);
			parameters.put("nazivkonta", nazivkonta);

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
		String sql = "SELECT * FROM tmpstanjerobe ORDER BY sifm";
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
}
