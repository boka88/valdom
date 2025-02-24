//program za stampu korisnika
//=================================================
import java.net.URL;
import java.io.IOException;
import java.io.FileWriter;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import javax.swing.plaf.metal.*;
import java.util.*;
import java.beans.PropertyVetoException.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.regex.*;

/*
FileWriter fw = new FileWriter("xmlfilename.xml");      
OutputFormat outFormat = new OutputFormat();      
outFormat.setEncoding("UTF-8");      
outFormat.setIndenting(true);      
XMLSerializer ser = new XMLSerializer(fw, outFormat);      
ser.serialize(doc);
*/
public class PrintFiz {
	private static Integer pageWidth;
	private static Integer pageHeight;
	private static Integer headerHeight;
	private static Integer footerHeight;
	private static java.io.OutputStreamWriter output;

	public Connection connection = null;
	public static String konto,nazivk;
 	private double dugg,pott,sall;

  public PrintFiz() {
   // Exit when user clicks the frame’s close button.

	uzmiKonekciju();
	//--------------------------------------------------	
	String reportFileName = "stampa.fo";
	try {
		//outFormat.setEncoding("UTF-8");      
		//outFormat.setIndenting(true);   
		output = new FileWriter(reportFileName);
	} catch ( IOException ioe ) {
		ioe.printStackTrace();
		System.exit( -1 );
	}

	// generate a FO file header
	writeHeader();
	
	// iterate through database records
	writeDetails();
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
private void writeHeader() 
{
	pageHeight = 297;
	pageWidth = 210;
	headerHeight = 15;
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
//---------------------bitno je definisati velicinu gornje margine od region-body-------
		output.write("      <fo:region-body margin-top=\"10mm\"\n"); 
		output.write("                      margin-bottom=\"0mm\" />\n"); 
//-------------------- definisanje regiona za header-before i footer-after -------------
		output.write("      <fo:region-before extent=\"10mm\"/>\n"); 
		output.write("      <fo:region-after extent=\"10mm\"/>\n"); 
//--------------------------------------------------------------------------------------		
		output.write("    </fo:simple-page-master>\n");
		output.write("  </fo:layout-master-set>\n");
		output.write("  <fo:page-sequence \n"); 
		output.write("          master-reference=\"simple\">\n"); 
//-------- definisanje zaglavlja koje stoji na svakoj strani (static-content)---------------
		output.write("     <fo:static-content flow-name=\"xsl-region-before\">\n"); 
		output.write("        <fo:block font-size=\"10pt\"\n"); 
		output.write("                  font-family=\"sans-serif\"\n"); 
		output.write("                  line-height=\"normal\"\n"); 
		output.write("                  text-align=\"center\"\n"); 
		output.write("                  >" + "ISTEK REGISTRACIJE FIZI&#x010c;KA LICA" + "</fo:block>\n"); 
		output.write("      <fo:table border-style=\"solid\" border-width=\"1.0pt\" border-color=\"black\" table-layout=\"fixed\" font-size=\"10pt\" >\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"4cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("									\n");
		output.write("    <fo:table-body>\n"); 
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + "JMBG" + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + "Reg.br" + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + "Datumreg." + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + "Naziv" + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + "Mesto" + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + "Telefon" + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		output.write("        </fo:table-body>\n"); 
		output.write("      </fo:table>\n"); 
		output.write("     </fo:static-content>\n"); 
//------------------------------kraj zaglavlja ---------------------------------------		
		output.write("		<fo:flow flow-name=\"xsl-region-body\">\n");
	} catch ( java.io.IOException ioe ) {
		System.out.println("Error writing to output file.");
		System.exit( -1 );
	}
}
//================================================================================
/* ------definisanje FOOTER-a na kraju svake strane (region-after)----------------
		output.write("     <fo:static-content flow-name=\"xsl-region-after\">\n"); 
		output.write("        <fo:block font-size=\"10pt\"\n"); 
		output.write("                  font-family=\"sans-serif\"\n"); 
		output.write("                  line-height=\"normal\"\n"); 
		output.write("                  text-align=\"center\"\n"); 
		output.write("                  >" + "podnozje strane" + "</fo:block>\n"); 
		output.write("     </fo:static-content>\n"); 
*/
//================================================================================

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
//--------------------------------------------------------------------------------
 private void writeDetails() {


	String str = "SPISAK POLAZNIKA ";

        try {
			output.write("      <fo:table border-style=\"solid\" border-width=\"1.0pt\" border-color=\"black\" table-layout=\"fixed\" font-size=\"10pt\" >\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"4cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("									\n");
			output.write("    <fo:table-body>\n"); 
		popuniPodatkeIzBaze();

			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			// linija
			//output.write("<fo:block text-align=\"center\" space-before.optimum=\"12pt\" space-after.optimum=\"12pt\">");
			//output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"1.0pt\" leader-length=\"100%\"/>");
			//output.write("              </fo:block>"); 

		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//--------------------------------------------------------------------------------
 private void popuniPodatkeIzBaze() {
	//uzimanje podataka---------------------------------------------------
    String sql = "SELECT * FROM pomfizicka";

      try {
         Statement statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			int i = 1;
			double ddug,ppot,saldo;
			saldo = 0;
			ddug = 0;
			ppot = 0;
            while ( rs.next() ) {

               String[] record = new String[6];
				record[0] = rs.getString("JMBG");
				record[1] = rs.getString("regbroj");
				record[2] = konvertujDatumIzPodataka(rs.getString("datumreg"));
				record[3] = rs.getString("prezime").trim() + " " + rs.getString("ime").trim();
				record[4] = rs.getString("mesto");
				record[5] = rs.getString("telefon");
			//prikaz podataka----------------------
		  try {
			output.write("          <fo:table-row>\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block text-align=\"left\">" + record[0] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block text-align=\"left\">" + konvUnicode(record[1]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block text-align=\"left\">" + record[2] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block text-align=\"left\">" + konvUnicode(record[3]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block text-align=\"left\">" + konvUnicode(record[4]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block text-align=\"left\">" + record[5] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
				System.exit( -1 );
		    }
				
				i++;
           }
				statement.close();
         		statement = null;
         }
         catch ( SQLException sqlex ) {
				//greska();
         }
	}
 }//end of class fPrintKonto
/*
-------- velika slova ---------------------
&#x0160;	- Saljivo
&#x0110;	- Djordje
&#x010c;	- Covek
&#x0106;	- Corak
&#x017d;	- Zabokrecina
--------- mala slova ----------------------
&#x0161;	- saljivo
&#x0111;	- djordje
&#x010d;	- covek
&#x0107;	- corak
&#x017e;	- zabokrecina
-------------------------------------------
*/