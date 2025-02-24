import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class jPrintPrijemnica {

	public Connection connection = null;
	private String fileName = null,nazivpre="",mestopre="",pPre="",sql,nazrobe,saradnik;
	private int odmes=0,brdok,domes=0,pred,koji,kkk;
	private int jur,mag,vrdok,odsifre,dosifre;
	private String radjed="",magacin="",nazivjur="",nazivmag="",nazivkonta,oddat="",dodat="";
	private String kolul="",koliz="",stanjekol="",vrul="",vriz="",stanje="",prc="";
	private String nazivdokumenta="",nazsarad="",nazivsarad="",datumn="",
			nazivkupca="";
	
	public jPrintPrijemnica(Connection _connection,
						int _jur,
						int _mag,
						int _brdok,
						int _vrdok,
						int _kkk){
		connection = _connection;
		jur = _jur;
		mag = _mag;
		brdok = _brdok;
		vrdok = _vrdok;
		kkk = _kkk;

		pPre = "1";
		pred = Integer.parseInt(pPre);

		mNadji mn = new mNadji();
		if (kkk != 1)
		{
			nazivjur = mn.nadjiProdavnicu(connection,jur,pred);
		}else{
			nazivjur = mn.nadjiRJ(connection,jur,pred);
		}
		nadjiPreduzece();
		nadjiKupca();

		if (jur > 0)
		{
			if (mag == 99)
			{
				radjed = "Prodavnica: " + nazivjur;
			}else{
				radjed = "Radna jedinica: " + nazivjur;
			}
		}

		if (mag > 0)
		{
			nadjiMag();
			magacin = nazivmag; 
		}
		
		if (kkk == 3)
		{
			nazivdokumenta = "IZDATNICA Br."; 
			nazsarad = " ";
			nazivsarad = " ";
		}else if (kkk == 4)
		{
			nazivdokumenta = "OTPREMNICA Br."; 
			nazsarad = "Kupac:";
		}else if (kkk == 1)
		{
			nazivdokumenta = "PRIJEMNICA Br."; 
			nazsarad = "Dobavlja\u010d:";
		}else if (kkk == 2)
		{
			nazivdokumenta = "PRIJEMNICA Br."; 
			nazsarad = " ";
			nazivkupca = "sopstvena proizvodnja";
		}
	
		Map<String, String> parameters = new HashMap<String, String>();
		try
		{
			JasperDesign jasperDesign = null;
			jasperDesign = 
			jasperDesign = JasperManager.loadXmlDesign("print/mdokumenti.jrxml");
			parameters.put("nazivpre", nazivpre);
			parameters.put("radjed", radjed);
			parameters.put("magacin", magacin);
			parameters.put("saradnik", saradnik);
			parameters.put("nazivdokumenta", nazivdokumenta);
			parameters.put("nazsarad", nazsarad);
			parameters.put("nazivkupca", nazivkupca);
			parameters.put("datum", datumn);
			parameters.put("brdokumenta", String.valueOf(brdok));
			parameters.put("nazivjur", nazivjur);

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
		String sql = "SELECT * FROM tmpkarticarobe ORDER BY rbr";
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
               String query = "SELECT * FROM radnejedinice WHERE jur=" + jur +
				   " AND pre=" + Integer.parseInt(pPre);

			try {
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					nazivjur = rs.getString("nazjur");
				}else{
					//JOptionPane.showMessageDialog(null, "Podaci ne postoje u Radn.jed.");
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
               String query = "SELECT * FROM magacini WHERE mag=" + mag +
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
    public void nadjiKupca() {
		String query="";
		Statement statement=null;
			query = "SELECT DISTINCT brdok,kupacbr,nazivkupca,datum " +
					" FROM tmpkarticarobe ORDER BY rbr";
	  try {
			statement = connection.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					//brdok = rs.getString("brdok");
					nazivkupca = rs.getString("nazivkupca");
					datumn = konvertujDatumIzPodataka(rs.getString("datum"));
					rs.close();
				}
				
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska u nadjiIOS" + sqlex);
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
		datumm = datumm + "/" + pom.substring(5,7);
		datumm = datumm + "/" + pom.substring(0,4);
		return datumm;
	}
//------------------------------------------------------------------------
}
