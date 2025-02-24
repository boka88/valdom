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
public class PrintVozilaPoj {
	private static Integer pageWidth;
	private static Integer pageHeight;
	private static Integer headerHeight;
	private static Integer footerHeight;
	private static java.io.OutputStreamWriter output;

	public Connection connection = null;
	public static String konto,nazivk,brsasije;
 	private double dugg,pott,sall;

public PrintVozilaPoj(String _brsasije) {
	brsasije = _brsasije;
	uzmiKonekciju();
	//--------------------------------------------------	
	String reportFileName = "stampa.fo";
	try {
		output = new FileWriter(reportFileName);
	} catch ( IOException ioe ) {
		ioe.printStackTrace();
		System.exit( -1 );
	}

	writeHeader();
	upisiPodatke();
	
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
//--------------------------------------------------------------------------------------
 public void izlaz(){
  	zatvoriKonekciju();
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
//--------------------------------------------------------------------------------
private void writeHeader() 
{
	pageHeight = 297;
	pageWidth = 210;
	headerHeight = 10;
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
	}
}
//---------------------------------------------------------------------------------------
 private void upisiPodatke() {
	    String sql = "SELECT * FROM vozila WHERE brsasije='" + brsasije + "'";
		String[] record = new String[17];

      try {
         Statement statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			int i = 1;
			double ddug,ppot,saldo;
			saldo = 0;
			ddug = 0;
			ppot = 0;
            if (rs.next())
            {

					record[0] = rs.getString("brsasije");
					record[1] = rs.getString("zemljaproizvodnje");
					record[2] = rs.getString("regbroj");
					record[3] = rs.getString("snagamotora");
					record[4] = rs.getString("ime");
					record[5] = rs.getString("radnazapremina");
					record[6] = rs.getString("brmotora");
					record[7] = rs.getString("telefon");
					record[8] = rs.getString("vrstavozila");
					record[9] = rs.getString("mobilni");
					record[10] = rs.getString("marka");
					record[11] = rs.getString("adresa");
					record[12] = rs.getString("tip");
					record[13] = rs.getString("mesto");
					record[14] = rs.getString("godproizvodnje");
					record[15] = rs.getString("pib");
					record[16] = rs.getString("pepdv");
            }

        try {

			// Zaglavlje preduzeca - Pocetak
            output.write("       <fo:block space-before.optimum=\"20pt\">		</fo:block>\n");

			output.write("      <fo:table table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"18cm\"/>\n");
			
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + konvUnicode("PODACI ZA VOZILO:") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			output.write("       <fo:block space-before.optimum=\"20pt\">		</fo:block>\n");
		
		output.write("      <fo:table border-style=\"solid\" border-width=\"1.0pt\" border-color=\"black\" table-layout=\"fixed\" font-size=\"10pt\" >\n");
		output.write("	<fo:table-column column-width=\"3cm\"/>\n");
		output.write("	<fo:table-column column-width=\"4cm\"/>\n");
		output.write("	<fo:table-column column-width=\"2cm\"/>\n");
		output.write("	<fo:table-column column-width=\"4cm\"/>\n");
		output.write("	<fo:table-column column-width=\"4cm\"/>\n");
		output.write("									\n");
		output.write("    <fo:table-body>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Br.\u0161asije:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[0]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Kilometra\u017ea:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[1]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Registarski broj") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[2]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Snaga motora u KW:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[3]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Vlasnik:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[4]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Radna zapremina:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[5]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Broj motora:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[6]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Telefon:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[7]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Vrsta vozila:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[8]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Mobilni:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[9]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Marka:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[10]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Adresa:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[11]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Tip:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[12]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("Mesto:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[13]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("God. proizv.:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[14]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("PIB:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[15]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("          <fo:table-row text-align=\"center\">\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode("PEPDV:") + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + konvUnicode(record[16]) + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
		output.write("              <fo:block>" + " " + "\n"); 
		output.write("              </fo:block>\n"); 
		output.write("            </fo:table-cell>\n"); 
		output.write("          </fo:table-row>\n"); 
		//--------------------------------------------------------------------
		output.write("        </fo:table-body>\n"); 
		output.write("      </fo:table>\n"); 
//------------------------------kraj zaglavlja ---------------------------------------		
		} catch ( java.io.IOException ioe ) {
			System.out.println("Error writing to output file.");
		}
      }
      catch ( SQLException sqlex ) {
				//greska();
      }
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
 }//end of class fPrintKonto
