//program za stampu kartice konta za JUR i Preduzece
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
import java.util.Set.*;
import java.beans.PropertyVetoException.*;
import java.text.*;

public class mPrintFinanKart {
	private static Integer pageWidth;
	private static Integer pageHeight;
	private static Integer headerHeight;
	private static Integer footerHeight;
	private static java.io.FileWriter output;

	public Connection connection = null;
	private String nazivp,pPre,nazdok,datumn;
	private int jur,odnal,donal;
	private String oddat,dodat,naziv,kupac;
	private	int magacin;
	
  public mPrintFinanKart(Connection _connection,
						String _oddat,
						String _dodat,
						String _kupac,
						String _naziv) {

		connection = _connection;
		oddat = _oddat;
		dodat = _dodat;
		kupac = _kupac;
		naziv = _naziv;

	JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
	tft.setValue(new java.util.Date());
	datumn = tft.getText();

	pPre = new String("1");
	nazivp = " ";

	//uzmiKonekciju();

	//--------------------------------------------------	
	String reportFileName = "stampa.fo";
	try {
		output = new FileWriter(reportFileName);
	} catch ( IOException ioe ) {
		ioe.printStackTrace();
		System.exit( -1 );
	}

	writeHeader();
	
	writeDetails();
	try {
		output.write("    </fo:flow>\n");
		output.write("  </fo:page-sequence>\n");
		output.write("</fo:root>");
		output.close();
	} catch ( java.io.IOException ioe ) {
		System.out.println("Error closing output FO file");
	}
	//--------------
	run();
	//izlaz();
 }
 //-----------------------------------------------------------------------------------------------------
	public void izlaz(){
  	//zatvoriKonekciju();
	//hide();
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
				System.out.println("Ne moze se zatvoriti konekcija");
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
 //--------------------------------------------------------------------------
   private String konvertujDatum(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(6,10);
		datum = datum + "-" + pom.substring(3,5);
		datum = datum + "-" + pom.substring(0,2);
	return datum;
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
	protected String konvUnicode(String _str){
		String str,str1,strall;
		int duz,i;
		strall = "";
		str = _str;
		if (str == null)
		{
			str = "";
		}
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
//--------------------------------------------------------------------------------------
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
		output.write("              <fo:simple-page-master master-name=\"only\" \n");

		output.write("               page-height=\"" +  pageHeight + "mm\"\n");
		output.write("                           page-width=\"" +  pageWidth + "mm\"\n");
		output.write("                           margin-left=\"5mm\"\n");
		output.write("                           margin-right=\"5mm\"\n");
		output.write("                           margin-top=\"" + headerHeight + "mm\"\n");
		output.write("                           margin-bottom=\"" + footerHeight + "mm\">\n");
	//---------------------bitno je definisati velicinu gornje margine od region-body jer odatle
	// pocinju stavke  -------------------------------------------------------------------------
		output.write("      <fo:region-body margin-top=\"45mm\"\n"); 
		output.write("                      margin-bottom=\"0mm\" />\n"); 
	//-------------------- definisanje regiona za header-before i footer-after -------------
	//-------------------- prva stavka je visina u koju mora stati zaglavlje   -------------
		output.write("      <fo:region-before extent=\"45mm\"/>\n"); 
		output.write("      <fo:region-after extent=\"10mm\"/>\n"); 
	//--------------------------------------------------------------------------------------
		output.write("    </fo:simple-page-master>\n");
		output.write("  </fo:layout-master-set>\n");
		output.write("  <fo:page-sequence \n"); 
		output.write("          master-reference=\"only\" initial-page-number=\"1\">\n"); 
	//-------- definisanje zaglavlja koje stoji na svakoj strani (static-content)---------------
		output.write("     <fo:static-content flow-name=\"xsl-region-before\">\n"); 
			output.write("      <fo:block text-align=\"end\" font-size=\"8pt\" >" + "str: " + "<fo:page-number/>\n"); 
			output.write("      </fo:block>\n"); 

	} catch ( java.io.IOException ioe ) {
		System.out.println("Error writing to output file.");
		System.exit( -1 );
	}
}
//==========================================================================
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
//------------------------------------------------------------------------------------------
 private void writeDetails() {

        try {
			//===========ZAGLAVLJE DOKUMENTA =============
			//prva linija-------------------------------------------------------------------
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5pt\" space-after.optimum=\"5pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"1.0pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
			// tabela bez bordera sa 2 kolone a jedan red --------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Kartica kupca") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			//drugi red zaglavlja tabela --------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naslov ----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Broj \u0161asije :") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + kupac + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// crtica  -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "Naziv: " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// br. naloga -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\" font-size=\"12pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(naziv) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// crtica  -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			
			//drugi red zaglavlja tabela --------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"11pt\" >\n");
			output.write("	<fo:table-column column-width=\"1cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"1cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"0.5cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naslov ----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "Od :" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + oddat + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// crtica  -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Do: " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// br. naloga -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + dodat + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// br. naloga -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// crtica  -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"2pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 

			//treci red zaglavlja tabela --------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// od datuma -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Dokument" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// od datuma -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Datum" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// do datuma -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\" >\n"); 
			output.write("              <fo:block>" + "Br.dok." + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// datum   -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\" >\n"); 
			output.write("              <fo:block>" + "Duguje" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// datum   -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\" >\n"); 
			output.write("              <fo:block>" + "Uplate" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"2pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
			output.write("     </fo:static-content>\n"); 
			// kraj zaglavlja koje se ponavlja na svakoj stranici--------------------		

			//pocetak stampanja podataka
			output.write("		<fo:flow flow-name=\"xsl-region-body\">\n");

			output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\"  border-collapse=\"separate\">\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("    <fo:table-body>\n"); 

			popuniPodatkeIzBaze();

			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			popuniUkupno();
		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//---------------------------------------------------------------------------------
 private void popuniPodatkeIzBaze() {

	String sql = "SELECT naziv,datum,valuta,brrac,ROUND(dug,2),ROUND(pot,2) " +
		" FROM tmpduznici ORDER BY datum";
 	  Statement statement = null;
     try {
         statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			int i = 1;
        while ( rs.next() ) {

               String[] record = new String[6];
               record[0] = rs.getString("naziv");
               record[1] = konvertujDatumIzPodataka(rs.getString("datum"));
               record[2] = rs.getString("brrac");
               record[3] = rs.getString("ROUND(dug,2)");
               record[4] = rs.getString("ROUND(pot,2)");

				//prikaz podataka----------------------
		  try {
			output.write("          <fo:table-row text-align=\"end\">\n"); 
			output.write("            <fo:table-cell text-align=\"center\" border-bottom-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[0]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\" border-bottom-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + record[1] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\" border-bottom-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + record[2] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\" border-bottom-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + record[3] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\" border-bottom-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + record[4] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
				}
				catch ( java.io.IOException ioe ) {
					System.out.println("Error writing to output file.");
				}
				//while petlja   ----------------------
				i++;
           }
         }
         catch ( SQLException sqlex ) {
					System.out.println("stavke");
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
//---------------------------------------------------------------------------------
 private void popuniUkupno() {
		String sql = "SELECT ROUND(SUM(dug),2),ROUND(SUM(pot),2),ROUND(SUM(dug-pot),2)" +
			" FROM tmpduznici";

	  Statement statement = null;
      try {
         statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			int i = 1;
			double ukdug,ukpot,saldo;
			saldo = 0;
			ukdug = 0;
			ukpot = 0;
        if ( rs.next() ) {

               String[] record = new String[3];
				record[0] = rs.getString("ROUND(SUM(dug),2)");
				record[1] = rs.getString("ROUND(SUM(pot),2)");
				record[2] = rs.getString("ROUND(SUM(dug-pot),2)");

				//prikaz podataka----------------------
		  try {
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"1.0pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"end\" font-size=\"8pt\" font-weight=\"bold\">\n"); 
			output.write("            <fo:table-cell text-align=\"left\" font-size=\"12pt\">\n"); 
			output.write("              <fo:block>" + "UKUPNO:" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + record[0] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + record[1] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"1.0pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 

			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"end\" font-size=\"8pt\" font-weight=\"bold\">\n"); 
			output.write("            <fo:table-cell text-align=\"left\" font-size=\"12pt\">\n"); 
			output.write("              <fo:block>" + "UKUPNO saldo:" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + record[2] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"1.0pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 

				}
				catch ( java.io.IOException ioe ) {
					System.out.println("Error writing to output file.");
				}
           }
         }
         catch ( SQLException sqlex ) {
					System.out.println("ukupno");
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
 }//end of class fPrintKarKon
