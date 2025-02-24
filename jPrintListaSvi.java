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


public class jPrintListaSvi {

	public Connection connection = null;
	private String fileName = null,nazivpre="",mestopre="",pPre="";
	private int odmes=0,domes=0,pred;
	private String brradnika="",vrboda=" ",brisplate=" ",iznosslovima="";
	private String strdatum="",strvaluta="",strdatump="",oddat,dodat;
	private double vrednostbezpdv=0.0,rabat=0.0,osnovicazapdv=0.0,strpdv=0.0,
		vrednostracuna=0.0,avans=0.0,zanaplatu=0.0;
	private int radnik=0;

	public jPrintListaSvi(Connection _connection,String _oddat,String _dodat){
		connection = _connection;
		oddat = _oddat;
		dodat = _dodat;

		nadjiPreduzece();
		try
		{
			JasperDesign jasperDesign = JasperManager.loadXmlDesign("print/listasvi.jrxml");
			JasperReport jasperReport = JasperManager.compileReport(jasperDesign);
			
		
			Map parameters = new HashMap();
			//dodavanje eksternih varijabli u report*********************
			parameters.put("oddat", oddat);
			parameters.put("dodat", dodat);
			parameters.put("nazivpre", nazivpre);
			parameters.put("mestopre", mestopre);

			//***********************************************************
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
		String sql = "SELECT * FROM tmprnal ORDER BY rbr";
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
private void izracunajUkupno(){
		String ukdug,ukpot,uksaldo,slovima;
		ukdug = "";
		ukpot = "";
		uksaldo = "";

		String sql = "SELECT ROUND(SUM(vredn),2), ROUND(SUM(irabat),2)," +
			"ROUND((SUM(vredn)-SUM(irabat)),2),ROUND(SUM(ipor1),2)," +
			"ROUND((SUM(vredn)-SUM(irabat)+SUM(ipor1)),2),ROUND(avans,2)," +
			"ROUND(iznosr,2),slovima,para,opis5,ROUND(avans,2),por1,ROUND((SUM(vredn)-SUM(irabat)),2)," +
			 "ROUND(SUM(ipor1),2) FROM tmpfakture GROUP BY kupacbr";
	  Statement statement = null;
		try {
			statement = connection.createStatement();
            ResultSet rs = statement.executeQuery( sql );
            String[] record = new String[10];
			if (rs.next())
			{
               vrednostbezpdv = rs.getDouble("ROUND(SUM(vredn),2)");
               rabat = rs.getDouble("ROUND(SUM(irabat),2)");
               osnovicazapdv = rs.getDouble("ROUND((SUM(vredn)-SUM(irabat)),2)");
               strpdv = rs.getDouble("ROUND(SUM(ipor1),2)");
               vrednostracuna = rs.getDouble("ROUND((SUM(vredn)-SUM(irabat)+SUM(ipor1)),2)");
               avans = rs.getDouble("ROUND(avans,2)");
               zanaplatu = rs.getDouble("ROUND(iznosr,2)");
               iznosslovima = rs.getString("slovima") + " I " + rs.getString("para") + " PARA.";
               //record[9] = rs.getDouble("opis5");

			   //por1 = rs.getDouble("por1");
			   //osnovica = rs.getDouble("ROUND((SUM(vredn)-SUM(irabat)),2)");
			   //pdvosnovica = rs.getDouble("ROUND(SUM(ipor1),2)");
				rs.close();
			}
         }
         catch ( SQLException sqlex ) {
					System.out.println("greska u ukupno: " + sqlex);
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
private void uzmiDatume(){

		String sql = "SELECT datum,valuta,datump" +
			" FROM tmpfakture ";
	  Statement statement = null;
		try {
			statement = connection.createStatement();
            ResultSet rs = statement.executeQuery( sql );
            String[] record = new String[10];
			if (rs.next())
			{
               strdatum = konvertujDatumIzPodataka(rs.getString("datum"));
               strvaluta = konvertujDatumIzPodataka(rs.getString("valuta"));
               strdatump = konvertujDatumIzPodataka(rs.getString("datump"));
				rs.close();
			}
         }
         catch ( SQLException sqlex ) {
					System.out.println("greska u uzmiDatume: " + sqlex);
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
}
