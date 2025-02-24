//program za stampu fakture
//=================================================
import java.net.URL;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import javax.swing.plaf.metal.*;
import java.util.*;
import java.beans.PropertyVetoException.*;
import java.lang.*;
import java.text.*;



public class aPrintRacun {
	private static Integer pageWidth;
	private static Integer pageHeight;
	private static Integer headerHeight;
	private static Integer footerHeight;
	private static java.io.FileWriter output;

	public Connection connection = null;
	private String nazivp,pPre,nazdok,datumn;
	private String nazivkupca,mestokupca,adresakupca,pibkupca,pepdvkupca;
	public static String konto,nazivk;
 	private double dugg,pott,sall;
	private int vrk,donal,nalog;
	private double odnal;
	private int sifra,sifraradnika;
	private String mesto="",telefon="",imeradnika="",adresa="",brsasije="";
	private String imeprezime="",regbroj="",PIB,zirorn,zirorn1;
	private String pattern = "#########.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);

  public aPrintRacun(int _sifra,int _sifraradnika,String _brsasije) {
	sifra = _sifra;
	sifraradnika = _sifraradnika;
	brsasije = _brsasije;

	pPre = "1";
	nazivp = "";
	uzmiKonekciju();
	nadjiPreduzece();
	nadjiSifruRadnika();
	nadjiRadnika();
	nadjiPodatkeOVozilu();

	//--------------------------------------------------	
	String reportFileName = "stampa.fo";
	try {
		output = new FileWriter(reportFileName);
	} catch ( IOException ioe ) {
		ioe.printStackTrace();
		System.exit( -1 );
	}

	// generate a FO file header
	writeHeader();
	//nadjiFznal();
	writeDetails();
	
	//kraj dokumenta--------------------------------------
	try {
		output.write("    </fo:flow>\n");
		output.write("  </fo:page-sequence>\n");
		output.write("</fo:root>");
		output.close();
	} catch ( java.io.IOException ioe ) {
		System.out.println("Error closing output FO file");
	}

	//---------------------------------------------------
	run();
	izlaz();
 }
 public void izlaz(){
  	zatvoriKonekciju();
 }
 //-----------------------------------------------------------------------------------------------------
     public void nadjiPreduzece() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM preduzeca WHERE Pre=1";

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					nazivp = rs.getString("Naziv1");
					mesto = rs.getString("Mesto");
					adresa = rs.getString("Adresa");
					telefon = rs.getString("Telefon");
					PIB = rs.getString("pib");
					zirorn = rs.getString("Ziror");
					zirorn1 = rs.getString("Ziror1");
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u preduzeca ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju");
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
     public void nadjiRadnika() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM radnici WHERE sifra=" + sifraradnika;

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					imeradnika = rs.getString("imeprezime");
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u radnici ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju");
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
     public void nadjiPodatkeOVozilu() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM vozila WHERE brsasije='" + brsasije + "'";

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					nazivkupca = rs.getString("ime");
					regbroj = rs.getString("regbroj");
					mestokupca = rs.getString("mesto");
					adresakupca = rs.getString("adresa");
					pibkupca = rs.getString("pib");
					pepdvkupca = rs.getString("pepdv");

				 }
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u vozila ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju");
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
     public void nadjiSifruRadnika() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM rnal WHERE rbr=" + sifra;

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					sifraradnika = rs.getInt("sifra");
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Podaci ne postoje u rnal ");
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju sifre radnika");
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
   public void uzmiKonekciju(){
	ConnMySQL _dbconn = new ConnMySQL();
	if (_dbconn.getConnection() != null){
		connection = _dbconn.getConnection();
	}
	else
	   {System.out.println("Konekcija sa podacima nije moguca");
		}
	return;
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				System.out.println("Ne mo\u017ee se zatvoriti konekcija");
			}
		}
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
//--------------------------------------------------------------------------------------

 public void run() {
	File fofile = new File("stampa.fo");
	AWTViewer awtv = new AWTViewer();
	try
	{
		awtv.viewFO(fofile);
	}
	catch (Exception e)
	{
	}
 }

	public void greska(){
		System.out.println("Nema podataka");
    }
//--------------------------------------------------------------------------------
private void writeHeader() 
{
	pageHeight = 297;
	pageWidth = 210;
	headerHeight = 5;
	footerHeight = 10;
	try {
		output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		output.write("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");
		output.write("  <fo:layout-master-set>\n");
		output.write("              <fo:simple-page-master master-name=\"simple\" \n");

		output.write("               page-height=\"" +  pageHeight + "mm\"\n");
		output.write("                           page-width=\"" +  pageWidth + "mm\"\n");
		output.write("                           margin-left=\"10mm\"\n");
		output.write("                           margin-right=\"10mm\"\n");
		output.write("                           margin-top=\"" + headerHeight + "mm\"\n");
		output.write("                           margin-bottom=\"" + footerHeight + "mm\">\n");
		output.write("      <fo:region-body margin-top=\"0mm\"\n"); 
		output.write("                      margin-bottom=\"0mm\" />\n"); 
		output.write("    </fo:simple-page-master>\n");
		output.write("  </fo:layout-master-set>\n");
		output.write("  <fo:page-sequence \n"); 
		output.write("          master-reference=\"simple\">\n"); 
		output.write("		<fo:flow flow-name=\"xsl-region-body\">\n");
	} catch ( java.io.IOException ioe ) {
		System.out.println("Error writing to output file.");
		System.exit( -1 );
	}
}
//============================================================

   public String Formatiraj(String _forma) {
	String forma,format;
	int i,j;
	forma = _forma.trim();
	j = forma.length();
	for (i=j;i<11 ;i++ )
	{
		forma = "  " + forma;
	}

	return forma;
 }
//--------------------------------------------------------------------------------
	protected String konvUnicode(String _str){
		String str,str1,strall;
		int duz,i;
		strall = "";
		str = _str;
		duz = str.length();
		for (i=0;i<duz ;i++ )
		{
			str1 = str.substring(i,i+1);
			if (str1.equals( "\u0160" ))
			{
				strall = strall + "&#x0160;";	//
			}
			else if (str1.equals( "\u0110" ))
			{
				strall = strall + "&#x0110;";	//
			}
			else if (str1.equals( "\u017d" ))
			{
				strall = strall + "&#x017d;";	//
			}
			else if (str1.equals( "\u0161" ))
			{
				strall = strall + "&#x0161;";	//
			}
			else if (str1.equals( "\u0111" ))
			{
				strall = strall + "&#x0111;";	//
			}
			else if (str1.equals( "\u010d" ))
			{
				strall = strall + "&#x010d;";	//
			}
			else if (str1.equals( "\u0107" ))
			{
				strall = strall + "&#x0107;";	//
			}
			else if (str1.equals( "\u017e" ))
			{
				strall = strall + "&#x017e;";	//
			}
			else if (str1.equals( "\u010c" ))
			{
				strall = strall + "&#x010c;";	//
			}
			else if (str1.equals( "\u0106" ))
			{
				strall = strall + "&#x0106;";	//
			}
			else{strall = strall + str1;}
		}
		return strall;
	}
//------------------------------------------------------------------------------------------
 private void writeDetails() {
	 double cenarada=0,cenadelova=0,rabat=0,osnovpdv=0,pdv=0;
    String sql = "SELECT brsasije,ROUND(cenarada,2)," +
		"ROUND(cenadelova,2),garancija,opiskvara,opisradova,delovi,datum," +
		"ROUND(cenarada + cenadelova,2),rabat FROM rnal WHERE rbr=" + sifra +
		" AND sifra=" + sifraradnika;

	  Statement statement = null;
      try {
         statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			int i = 1;
            rs.next(); 

               String[] record = new String[10];
				record[0] = rs.getString("brsasije");
				record[1] = rs.getString("ROUND(cenarada,2)");
				record[2] = rs.getString("ROUND(cenadelova,2)");
				record[3] = rs.getString("garancija");
				record[4] = rs.getString("opiskvara");
				record[5] = rs.getString("opisradova");
				record[6] = rs.getString("delovi");
                record[7] = konvertujDatumIzPodataka(rs.getString("datum"));
                record[8] = rs.getString("ROUND(cenarada + cenadelova,2)");
				record[9] = rs.getString("rabat");

				//prikaz podataka----------------------

        try {
			//===========ZAGLAVLJE DOKUMENTA =============
			//prva linija-------------------------------------------------------------------
			// naziv preduzeca --------------------


			output.write("      <fo:table table-layout=\"fixed\" font-size=\"14pt\" >\n");
			output.write("	<fo:table-column column-width=\"19cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row font-weight=\"bold\">\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\" border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(nazivp) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ------------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			//druga linija==================================================================
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" font-weight=\"italic\" >\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// pbroj --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode(mesto) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode(adresa) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode(telefon) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ---------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" font-weight=\"italic\" >\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// pbroj --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "PIB:" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + PIB + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ---------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			// pbroj --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode("\u017diro rn.:") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + zirorn + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ---------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			// pbroj --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ---------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			// pbroj --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + zirorn1 + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ---------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			//razmak izmedju tabela
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5pt\" space-after.optimum=\"5pt\">");
			output.write("              </fo:block>"); 

			//treca linija==================================================================
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ---------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			// ---------------------------------------------------------------
			output.write("          <fo:table-row >\n"); 
			// mesto --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// adresa --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ---------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			//cetvrta linija==================================================================
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"6.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// -------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			// -------------------------------------------------------------------------
				output.write("      <fo:table table-layout=\"fixed\" font-size=\"12pt\" >\n");
				output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
				output.write("	<fo:table-column column-width=\"4cm\"/>\n");
				output.write("    <fo:table-body>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + " " + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				// -------------------------------------------------------------------------
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "" + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "" + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				// -------------------------------------------------------------------------
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "" + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "" + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				// -------------------------------------------------------------------------
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "" + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "" + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				// -------------------------------------------------------------------------
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "" + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("        </fo:table-body>\n"); 
				output.write("      </fo:table>\n"); 
			// ----------------------------------------------------------------------------
			output.write("            </fo:table-cell>\n"); 
			// -------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\" border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			// =================================okvir za kupca==============================================
				output.write("      <fo:table table-layout=\"fixed\" font-size=\"12pt\">\n");
				output.write("	<fo:table-column column-width=\"9cm\"/>\n");
				output.write("    <fo:table-body>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block >" + "&#160;&#160;" + konvUnicode(nazivkupca) + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "&#160;&#160;" + konvUnicode(adresakupca) +"\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\" >\n"); 
				output.write("              <fo:block>" + "&#160;&#160;" + konvUnicode(mestokupca) + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "&#160;&#160;" + "PIB:" + konvUnicode(pibkupca) + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// -------------------------------------------------------------------------
				output.write("          <fo:table-row >\n"); 
				output.write("            <fo:table-cell text-align=\"left\">\n"); 
				output.write("              <fo:block>" + "&#160;&#160;" + "PEPDV:" + konvUnicode(pepdvkupca) + "\n"); 
				output.write("              </fo:block>\n"); 
				output.write("            </fo:table-cell>\n"); 
				output.write("          </fo:table-row>\n"); 
				// ------------------------------------------------------------------------
				output.write("        </fo:table-body>\n"); 
				output.write("      </fo:table>\n"); 
			// ----------------------------------------------------------------------------
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			//razmak izmedju tabela
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5pt\" space-after.optimum=\"5pt\">");
			output.write("              </fo:block>"); 

			// RACUN  BROJ : ... --------------------------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"14pt\" font-family=\"Helvetica\">\n");
			output.write("	<fo:table-column column-width=\"19cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row border-style=\"solid\" border-width=\"0.5pt\" font-weight=\"bold\">\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + konvUnicode("RA\u010cUN  broj:  ") + String.valueOf(sifra) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ------------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			//razmak izmedju tabela
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5pt\" space-after.optimum=\"5pt\">");
			output.write("              </fo:block>"); 

			// Na osnovu  --------------------------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"14cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			// -------------------------------------------------------------------------
			output.write("          <fo:table-row >\n"); 
			output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("              <fo:block>" + konvUnicode("Datum ra\u010duna:") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// -------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("              <fo:block>" +  record[7] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			// ------------------------------------------------------------------------
			output.write("          <fo:table-row >\n"); 
			output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("              <fo:block>" + konvUnicode("Datum prometa dob.i usluga:") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// -------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" +  record[7] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			// ------------------------------------------------------------------------
			output.write("          <fo:table-row >\n"); 
			output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("              <fo:block>" + konvUnicode("Valuta pla\u0107anja:") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// -------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" +  record[7] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			// ------------------------------------------------------------------------
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"0.5pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 

			//		ZAGLAVLJE tabele podataka prometa -------------------------------
			output.write("      <fo:table  table-layout=\"fixed\" font-size=\"9pt\" font-weight=\"bold\">\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("									\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			//--------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Naziv usluge" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------			
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Cena" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------			
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Rabat(%)" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------			
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Vrednost" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------			
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Osnov PDV" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------			
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "PDV (%)" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------			
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"0.5pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
			
			cenarada = Double.parseDouble(record[1]);
			cenadelova = Double.parseDouble(record[2]);
			rabat = Double.parseDouble(record[9]);
			//osnovpdv = cenarada+cenadelova-(cenarada+cenadelova)*(rabat/100);
			osnovpdv = cenarada - (cenarada)*(rabat/100);
			pdv = osnovpdv*0.18;
			
			output.write("      <fo:table  table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("									\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + konvUnicode(record[5]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + myFormatter.format(cenarada)+ "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + myFormatter.format(rabat)+ "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + myFormatter.format(osnovpdv)+ "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + myFormatter.format(osnovpdv)+ "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "18"+ "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"10pt\" space-after.optimum=\"15pt\">");
			output.write("              </fo:block>"); 
			output.write("      <fo:table  table-layout=\"fixed\" border-collapse=\"separate\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"10cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("									\n");
			output.write("    <fo:table-body>\n"); 
			//--------------------------------------------
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "Vrednost usluga :" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + myFormatter.format(cenarada) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			//---------------------------------------------
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "Ukupan popust :" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + myFormatter.format((cenarada)*(rabat/100)) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			//---------------------------------------------
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "Osnovica za PDV :" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + myFormatter.format(osnovpdv) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			//---------------------------------------------
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-bottom-width=\"0.5pt\" border-bottom-style=\"solid\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Obra\u010dunat PDV 18% :") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\" border-bottom-width=\"0.5pt\" border-bottom-style=\"solid\">\n"); 
			output.write("              <fo:block>" + myFormatter.format(pdv) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			//---------------------------------------------
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + "Ukupna vrednost usluga:" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\" >\n"); 
			output.write("              <fo:block>" + myFormatter.format(osnovpdv+pdv) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			//---------------------------------------------
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"0.5pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 

			output.write("<fo:block text-align=\"center\" space-before.optimum=\"10pt\" space-after.optimum=\"15pt\">");
			output.write("              </fo:block>"); 
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"19cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row border-style=\"solid\" border-width=\"0.5pt\" >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Napomena za poresko oslobo\u0111enje: Nema.  ") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ------------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row border-style=\"solid\" border-width=\"0.5pt\" >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Za neblagovremeno pla\u0107anje zara\u010dunavamo zakonsku kamatu.") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// ------------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			output.write("<fo:block text-align=\"center\" space-before.optimum=\"10pt\" space-after.optimum=\"15pt\">");
			output.write("              </fo:block>"); 
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" space-before.optimum=\"20pt\">\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"7cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>\n"); 
			output.write("Fakturisao  ");
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "M.P." + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Primio") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>\n"); 
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"1.0pt\" leader-length=\"70%\"/>");
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>\n"); 
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"1.0pt\" leader-length=\"70%\"/>");
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>\n"); 
			output.write(" ");
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>\n"); 
			output.write(" ");
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 

			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
				System.exit( -1 );
			}
		}
        catch ( SQLException sqlex ) {
				System.out.println(sqlex);
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
//-----------------------------------------------------------------------------------------
 }//end of class fPrintKonto
