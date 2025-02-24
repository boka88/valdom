//program za stampu finansijskog naloga
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

public class PrintRegFizicka {
	private static Integer pageWidth;
	private static Integer pageHeight;
	private static Integer headerHeight;
	private static Integer footerHeight;
	private static java.io.FileWriter output;

	public Connection connection = null;
	private String nazdok,datumn,JMBG,regbroj,razlog;
	public static String konto,nazivk;
 	private double dugg,pott,sall;
	private int vrk,odnal,donal,nalog;

  public PrintRegFizicka(String _jmbg, String _regbroj, String _razl) {
   // Exit when user clicks the frame’s close button.

	
	JMBG = _jmbg;
	regbroj = _regbroj;
	razlog = _razl;
	
	uzmiKonekciju();


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

		//nadjiVozilo();
		pocetakStrane();
		popuniPodatkeIzBaze();
		popuniImaoca();
		krajStrane();
	
	//kraj dokumenta--------------------------------------
	try {
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
//--------------------------------------------------------------------------------
private void pocetakStrane() 
{
	try {
		output.write("  <fo:page-sequence \n"); 
		output.write("          master-reference=\"simple\">\n"); 
		output.write("		<fo:flow flow-name=\"xsl-region-body\">\n");
	} catch ( java.io.IOException ioe ) {
		System.out.println("Error writing to pocetak.");
		System.exit( -1 );
	}
}
//--------------------------------------------------------------------------------
private void krajStrane() 
{
	try {
		output.write("    </fo:flow>\n");
		output.write("  </fo:page-sequence>\n");
	} catch ( java.io.IOException ioe ) {
		System.out.println("Error closing output FO file");
	}
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
 private void prazanRed() {

        try {

				//prazan red ===============================
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5.5pt\" space-after.optimum=\"5.5pt\">"
							+ " " + "\n");
			output.write("              </fo:block>"); 
				//==========================================
		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//------------------------------------------------------------------------------------------
 private void prazanRed3() {

        try {

				//prazan red ===============================
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"4pt\" space-after.optimum=\"8.8pt\">"
							+ " " + "\n");
			output.write("              </fo:block>"); 
				//==========================================
		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//------------------------------------------------------------------------------------------
 private void prazanRedManji() {

        try {

				//prazan red ===============================
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5pt\" space-after.optimum=\"5pt\">"
							+ " " + "\n");
			output.write("              </fo:block>"); 
				//==========================================
		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//------------------------------------------------------------------------------------------
 private void prazanRedM() {

        try {

				//prazan red ===============================
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"4pt\" space-after.optimum=\"3.6pt\">"
							+ " " + "\n");
			output.write("              </fo:block>"); 
				//==========================================
		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//------------------------------------------------------------------------------------------
 private void prazanRedM1() {

        try {

				//prazan red ===============================
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"3pt\" space-after.optimum=\"3pt\">"
							+ " " + "\n");
			output.write("              </fo:block>"); 
				//==========================================
		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//------------------------------------------------------------------------------------------------------------------
	public void popuniImaoca() {
		String[] rec = new String[10];
		int i;
 
      try {
         Statement statement = connection.createStatement();

			String query = "SELECT * FROM korisnici WHERE JMBG='" + JMBG + "'";

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		         	rec[0]= rs.getString("JMBG");
		         	rec[1]= rs.getString("prezime");
		         	rec[2]= rs.getString("ime");
		         	rec[3]= rs.getString("imeroditelja");
		         	rec[4]= rs.getString("zanimanje");
		         	rec[5]= rs.getString("drz");
		         	rec[6]= rs.getString("opstina");
		         	rec[7]= rs.getString("mesto");
		         	rec[8]= rs.getString("ulica");
		         	rec[9]= rs.getString("broj");
				for (i=0;i<10 ;i++ )
				{
					if (rec[i].trim().length() == 0){
						rec[i] = " - ";
					}		
				}

		  try {
			//treca strana======================================================================
			pocetakStrane();
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"10pt\" space-after.optimum=\"10pt\" >"
							+ " " + "\n");
			output.write("              </fo:block>"); 
			//JMBG----------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"13cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"center\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell  border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + rec[0] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			//prezime----------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell  border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[1]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// ime -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[2]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// ime roditelja -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[3]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// zanimanje -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[4]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// drzavljanstvo -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[5]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// opstina -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[6]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// mesto -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[7]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// ulica -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[8]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed3();
			// broj -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rec[9]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			//prazanRed3();
			//krajStrane();
			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
				System.exit( -1 );
		    }
		}
	    statement.close();
        statement = null;
      }
      catch ( SQLException sqlex ) {
      }
  }
//------------------------------------------------------------------------------------------
 private void popuniPodatkeIzBaze() {
	int aa = 20;
	int i;

	String[] record = new String[32];

	//uzimanje podataka---------------------------------------------------
    String sql = "SELECT * FROM vozila WHERE JMBG='" + JMBG +
					"' And regbroj='" + regbroj + "'";

      try {
         Statement statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
		       if(rs.next()){
				
		         	record[0]= rs.getString("JMBG");
		         	record[1]= rs.getString("regbroj");
		         	record[2] = rs.getString("brojregistra");
		         	record[3] = rs.getString("vrsta");
		         	record[4] = rs.getString("vrstavozila").trim();
		         	record[5] = rs.getString("oznakaJUS");
		         	record[6] = rs.getString("marka");
		         	record[7] = rs.getString("tip");
		         	record[8] = rs.getString("brsasije");
		         	record[9] = rs.getString("brmotora");
		         	record[10] = rs.getString("godproizvodnje");
		         	record[11] = rs.getString("zemljaproizvodnje");
		         	record[12] = rs.getString("snagamotora");
		         	record[13] = rs.getString("radnazapremina");
		         	record[14] = rs.getString("masapraznog");
		         	record[15] = rs.getString("nosivost");
		         	record[16] = rs.getString("brojmestased");
		         	record[17] = rs.getString("brojmestastaj");
		         	record[18] = rs.getString("obliknamena").trim();
		         	record[19] = rs.getString("osnovnanamena");
		         	record[20] = rs.getString("bojaosnovna");
		         	record[21] = rs.getString("bojadodata");
		         	record[22] = rs.getString("brojosovina");
		         	record[23] = rs.getString("brojpogosovina");
		         	record[24] = rs.getString("imakuku");
		         	record[25] = rs.getString("kilometara");
		         	record[26] = konvertujDatumIzPodataka(rs.getString("datumreg"));
		         	record[27] = rs.getString("gorivo");
		         	record[28] = rs.getString("vrstaprevoza");
		         	record[29] = rs.getString("brojvrata");
		         	record[30] = rs.getString("brojtockova");
		         	record[31] = rs.getString("radio");
				
				for (i=0;i<32 ;i++ )
				{
					if (record[i].trim().length() == 0){
						record[i] = " - ";
					}		
				}
				//System.out.println("proso rsnext");
				//prikaz podataka----------------------
		  try {
			  //prva strana---------------------------------------------------
			//registarska oznaka----------------
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"100pt\" space-after.optimum=\"75pt\">"
							+ " " + "\n");
			output.write("              </fo:block>"); 
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"center\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell  border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[1]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			prazanRed();
			//broj registra --------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"13.7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[2]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			//razlog popunjavanja ----------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"13.7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + razlog + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			krajStrane();

			//druga strana----------------------------------------------------
			//output.write("<fo:block text-align=\"center\" space-before.optimum=\"22cm\" space-after.optimum=\"140pt\">"
			pocetakStrane();
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"10pt\" space-after.optimum=\"20pt\" >"
							+ " " + "\n");
			output.write("              </fo:block>"); 
			
			//vrsta vozila I I OBLIKNAMENA----------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[4]) + "  " + konvUnicode(record[18])  + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// marka------------------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[6]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// tip -----------------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[7]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// zemlja proizvodnje -------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[11]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// godina proizvodnje ---------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"11.7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[10]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// broj sasije ------------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[8]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// broj motora ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[9]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// namena ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[19]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// boja osnovna ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[20]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRed();
			// boja dodata ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[21]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			prazanRedM();
			prazanRedM();
			prazanRedM();
			//prazanRedM();
			// TEHNICKE KARAKTERISTIKE VOZILA  =================================================
			// snaga motora---------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"11.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[12]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM1();
			// radna zapremina ---------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"10.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[13]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM1();
			// masa praznog vozila  ---------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"10.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[14]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedManji();
			// nosivost ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"10.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[15]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			prazanRedM();
			prazanRedM();
			// broj putn.mesta ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"8.2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[16]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedManji();
			// br.pogonskih osovina ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"12.4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[23]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			// uk. br.osovina ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"12.4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[22]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			// vrsta goriva ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + "." + "\n"); 
			//output.write("              <fo:block>" + konvUnicode(record[27]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			// uredjaj kuka  ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + "." + "\n"); 
			//output.write("              <fo:block>" + konvUnicode(record[24]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			// vrsta prevoza  ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + "." + "\n"); 
			//output.write("              <fo:block>" + konvUnicode(record[28]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			// broj vrata  ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"12.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[29]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			// broj tockova  ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"11.9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[30]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			prazanRedM();
			// radio  ---------------------------
			output.write("      <fo:table border-width=\"0pt\" table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-width=\"0pt\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			//output.write("              <fo:block>" + konvUnicode(record[31]) + "\n"); 
			output.write("              </fo:block>\n");
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			//prazanRedManji();
			krajStrane();

			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
				System.exit( -1 );
		    }

		  }// end if
				System.out.println(record[1]);
				statement.close();
         		statement = null;
         }
         catch ( SQLException sqlex ) {
				//greska();
         }
		 
	}

 }//end of class fPrintKonto
