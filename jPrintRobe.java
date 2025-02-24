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

public class jPrintRobe {

	public Connection connection = null;
	private String fileName = null,nazivpre="",mestopre="",pPre="",sql;
	private int odmes=0,domes=0,pred,koji;

	public jPrintRobe(Connection _connection,int _koji,String _sql){
		connection = _connection;
		koji = _koji;
		sql = _sql;
		pPre = "1";
		pred = Integer.parseInt(pPre);

		nadjiPreduzece();

		Map<String, String> parameters = new HashMap<String, String>();
		try
		{
			JasperDesign jasperDesign = null;
			if (koji == 1)
			{
				jasperDesign = JasperManager.loadXmlDesign("print/artiklisvi.jrxml");
			}else{
				jasperDesign = JasperManager.loadXmlDesign("print/artiklicen.jrxml");
			}
			parameters.put("preduzece", nazivpre);
			parameters.put("mesto", mestopre);
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

		Statement statement = null;
		//String sql = "SELECT * FROM sifarnikrobe WHERE pre=" + Integer.parseInt(pPre);;
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
}
