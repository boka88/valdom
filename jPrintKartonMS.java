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


public class jPrintKartonMS {

	public Connection connection = null;
	private String fileName = null,nazivpre="",mestopre="",pPre="";
	private int odmes=0,domes=0,pred;
	private String masina="",strvaluta="",strdatump="",oddat,dodat;
	private String naziv="",tip="",oznaka="",merniopseg="",klasatacnosti="",datumnabavke="",prvipregled="";
	private String periodpregleda="",proizvodjac="",mestoupotrebe="",serijskibroj="";
	private String mernosredstvo="";

	public jPrintKartonMS(Connection _connection, String _mernosredstvo){
		connection = _connection;
		mernosredstvo = _mernosredstvo;

		//nadjiPreduzece();
		nadjiMernosredstvo(_mernosredstvo);
		try
		{
			JasperDesign jasperDesign = JasperManager.loadXmlDesign("print/kartonms.jrxml");
			JasperReport jasperReport = JasperManager.compileReport(jasperDesign);
			
		
			Map parameters = new HashMap();
			//dodavanje eksternih varijabli u report*********************
			parameters.put("naziv", naziv);
			parameters.put("tip", tip);
			parameters.put("oznaka", oznaka);
			parameters.put("merniopseg", merniopseg);
			parameters.put("klasatacnosti", klasatacnosti);
			parameters.put("datumnabavke", datumnabavke);
			parameters.put("prvipregled", prvipregled);
			parameters.put("proizvodjac", proizvodjac);
			parameters.put("mestoupotrebe", mestoupotrebe);
			parameters.put("serijskibroj", serijskibroj);
			parameters.put("periodpregleda", periodpregleda);

			//***********************************************************
			//JasperPrint jasperPrint=null;
			//jasperPrint = uzmiPodatke(jasperReport,parameters);
			//KADA IMAMO SUBREPORT U REPORTU MORAMO PRESLEDITI KONEKCIJU vidi dole
			JasperPrint jasperPrint=null;
			jasperPrint = uzmiPodatke(jasperReport,parameters);
			//*********************************************************************

			JasperViewer.viewReport(jasperPrint,false);
		}
		catch (JRException ee)
		{
			JOptionPane.showMessageDialog(null, "Greska:" + ee);
			return;
		}
	}
//--------------------------------------------------------------------------------------
	private JasperPrint uzmiPodatke(JasperReport _jasperReport,Map _parameters){
		JasperPrint jrprint = null;
		JRResultSetDataSource obj = null;

		Statement statement = null;
		String sql = "SELECT * FROM atesti WHERE sifmas=" + mernosredstvo;
		try {
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
               String query = "SELECT * FROM preduzeca WHERE Pre=1";

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
//--------------------------------------------------------------------------------------
    public void nadjiMernosredstvo(String _mernosr) {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM mernasredstva WHERE rbr=" + _mernosr.trim();

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					naziv = rs.getString("nazmerila"); 
					tip = rs.getString("tip"); 
					oznaka = rs.getString("oznaka"); 
					merniopseg = rs.getString("merops"); 
					klasatacnosti = rs.getString("klastac"); 
					datumnabavke = rs.getString("datumnabavke"); 
					prvipregled = konvertujDatumIzPodataka(rs.getString("prvipregled")); 
					periodpregleda = rs.getString("peratest"); 
					proizvodjac = rs.getString("proiz"); 
					mestoupotrebe="Kontrola kvaliteta";
					serijskibroj=rs.getString("serija"); 
					periodpregleda = rs.getString("peratest"); 
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
//--------------------------------------------------------------------------------------
	public String konvertujDatumIzPodataka(String _datum){
		String datumm,pom;
		pom = _datum;
		datumm = pom.substring(8,10);
		datumm = datumm + "." + pom.substring(5,7);
		datumm = datumm + "." + pom.substring(0,4);
		return datumm;
	}
}
