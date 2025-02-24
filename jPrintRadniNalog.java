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
import net.sf.jasperreports.engine.JasperCompileManager;

public class jPrintRadniNalog {

	public Connection connection = null;
	private String fileName = null,nazivpre="",mestopre="",pPre="";
	private int odmes=0,domes=0,pred;
	private String brradnika="",vrboda=" ",brisplate=" ",iznosslovima="";
	private String strdatum="",strvaluta="",strdatump="",oddat,dodat,markavozila="";
	private double vrednostbezpdv=0.0,rabat=0.0,osnovicazapdv=0.0,strpdv=0.0,
		vrednostracuna=0.0,avans=0.0,zanaplatu=0.0;
	private int radninalog=0;
	private String jmbg="",ime="",brsasije="",datumnaloga="",opiskvara="",opisradova="",regbroj="";

	public jPrintRadniNalog(Connection _connection,int _radninalog){
		connection = _connection;
		radninalog = _radninalog;

		nadjiPreduzece();
		nadjiRadniNalog();
		nadjiRadnika();
		try
		{
			//JasperDesign jd1 = JasperManager.loadXmlDesign("print/radnal_subreport1.jrxml");
			//JasperCompileManager.compileReportToFile("print/radnal_subreport1.jrxml");	
			
			//JasperDesign jasperDesign = JasperManager.loadXmlDesign("print/radnals.jrxml");
			JasperDesign jasperDesign = JasperManager.loadXmlDesign("print/radnal.jrxml");
			JasperReport jasperReport = JasperManager.compileReport(jasperDesign);
		
			Map parameters = new HashMap();
			//dodavanje eksternih varijabli u report*********************
			parameters.put("rbr", radninalog);
			parameters.put("ime", ime);
			//parameters.put("SUBREPORT_DIR", "print/");
			//parameters.put("Subreport_1", jr1);

			//***********************************************************
			JasperPrint jasperPrint=null;
			//jasperPrint = uzmiPodatke(jasperReport,parameters);
			jasperPrint = JasperManager.fillReport(jasperReport, parameters, connection); 
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
		String sql = "SELECT * FROM rnal WHERE rbr=" + radninalog;
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
		System.out.println("Greska u trazenju preduzeca: "+sqlex);
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
    public void nadjiRadniNalog() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM rnal WHERE rbr=" + radninalog;

			try {
		         ResultSet rs = statement.executeQuery( query );
				 
				 if(rs.next()){
					jmbg = rs.getString("jmbg"); 
					brsasije = rs.getString("brsasije"); 
					regbroj = rs.getString("regbroj"); 
					datumnaloga = konvertujDatumIzPodataka(rs.getString("datum")); 
					opiskvara = rs.getString("opiskvara"); 
					opisradova = rs.getString("opisradova"); 
					rs.close();
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje za radni nalog ");
		      }
	  }     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju rad.naloga: "+sqlex);
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
    public void nadjiRadnika() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM radnici WHERE jmbg='" + jmbg + "'";

			try {
		         ResultSet rs = statement.executeQuery( query );

				 if(rs.next()){
					jmbg = rs.getString("jmbg"); 
					ime = rs.getString("imeprezime"); 
					rs.close();
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje za radnika ");
		      }
	  }     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju radnika: "+sqlex);
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
   public String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
}
