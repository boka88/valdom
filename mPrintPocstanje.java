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

public class mPrintPocstanje {
	private static Integer pageWidth;
	private static Integer pageHeight;
	private static Integer headerHeight;
	private static Integer footerHeight;
	private static java.io.FileWriter output;
	private mNadji nad;
	private String tblpromet="mprom";
	public Connection connection = null;
	private String nazivp,pPre,nazdok,datumn,ddatum="";
	private int jur,odnal,donal;
	private String nazivk,mesto,adresa,nazivjur,nazivmag,nazivkonta,nazivmt;
	private	int odrj,dorj,odmag,domag,konto,sifra,odsif,dosif,brdokm,koji,mestr;

  public mPrintPocstanje(Connection _connection,int _odrj,int _odmag,int _koji,String _datum) {

		connection = _connection;
		odrj = _odrj;
		odmag = _odmag;
		brdokm = 0;
		koji = _koji;
		ddatum = _datum;

	JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
	tft.setValue(new java.util.Date());
	datumn = tft.getText();

	pPre = new String("1");
	tblpromet = tblpromet + pPre.trim();

	//nazivp = new String(IzborPre.kojiNaz);
	int pred = Integer.parseInt(pPre);

	nad = new mNadji();
	nazivjur = nad.nadjiRJ(connection,odrj,pred);
	nazivmag = nad.nadjiMag(connection,odmag,pred);
	nazivp = nad.nadjiPreduzece(connection,pred);

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
	zaglavlje();
	idiNaKraj();

	//--------------
	run();
	izlaz();
 }
 //-----------------------------------------------------------------------------------------------------
public void izlaz(){
  	zatvoriKonekciju();
 }
  //-------------------------------------------------------------------------------------
    private void idiNaKraj(){
		try {
			output.write("    </fo:flow>\n");
			output.write("  </fo:page-sequence>\n");
			output.write("</fo:root>");
			output.close();
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
			//System.exit( -1 );
		}
	}
//--------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
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
	protected String konvUnicode(String _str){
		String str,str1,strall;
		int duz,i;
		strall = "";
		str = _str;
		if (str == null)
		{
			str=" ";
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
 private void zaglavlje() {

        try {
			output.write("  <fo:page-sequence \n"); 
			output.write("          master-reference=\"simple\">\n"); 
			output.write("		<fo:flow flow-name=\"xsl-region-body\">\n");
			//===========ZAGLAVLJE DOKUMENTA =============
			//prva linija-------------------------------------------------------------------
			// okvir oko dokumenta --------------------

			// tabela bez bordera sa 2 kolone a jedan red --------------------
			output.write("      <fo:table table-layout=\"fixed\" >\n");
			output.write("	<fo:table-column column-width=\"7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"1cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("	<fo:table-column column-width=\"1.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\"  font-size=\"10pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(nazivp) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\"  font-size=\"12pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode("PO\u010cETNO STANJE") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\"  font-size=\"14pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\"  font-size=\"14pt\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\"  font-size=\"10pt\">\n"); 
			output.write("              <fo:block>" + "Datum:" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\"  font-size=\"10pt\">\n"); 
			output.write("              <fo:block>" + ddatum + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			//drugi red zaglavlja tabela --------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"11pt\" >\n");
			output.write("	<fo:table-column column-width=\"7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"12cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naslov ----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//naziv vrste knj. -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			//drugi red zaglavlja tabela --------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"11pt\" >\n");
			output.write("	<fo:table-column column-width=\"7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naslov ----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + " "  + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//naziv vrste knj. -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5pt\" space-after.optimum=\"5pt\">");
			output.write("              </fo:block>"); 

			//drugi red zaglavlja tabela --------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"11pt\" >\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"0.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naslov ----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			if (koji == 1)
			{
				output.write("              <fo:block>" + "Radna jedinica:" + "\n"); 
			}else{
				output.write("              <fo:block>" + "Prodavnica:" + "\n"); 
			}
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"right\">\n"); 
			output.write("              <fo:block>" + String.valueOf(odrj) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// crtica  -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// br. naloga -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//naziv vrste knj. -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naslov ----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "Magacin:" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"right\">\n"); 
			output.write("              <fo:block>" + String.valueOf(odmag) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// crtica  -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// br. naloga -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//naziv vrste knj. -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			output.write("<fo:block text-align=\"center\" >");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"2pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 

			//treci red zaglavlja tabela --------------------------------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			//--------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + konvUnicode("\u0160ifra") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Konto" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("              <fo:block>" + "Naziv robe" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\" >\n"); 
			output.write("              <fo:block>" + "Jed.mere" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\" >\n"); 
			output.write("              <fo:block>" + konvUnicode("Koli\u010dina") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//--------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\" >\n"); 
			output.write("              <fo:block>" + "Cena" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// valuta -------------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\" >\n"); 
			output.write("              <fo:block>" + "Vrednost" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"2pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
			//output.write("     </fo:static-content>\n"); 
			// kraj zaglavlja koje se ponavlja na svakoj stranici--------------------		

			//pocetak stampanja podataka

			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("    <fo:table-body>\n"); 

		popuniPodatkeIzBaze();
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
		popuniPodnozje();

			//popuniUkupno();
		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//---------------------------------------------------------------------------------
 private void popuniPodatkeIzBaze() {

	//uzimanje podataka---------------------------------------------------
    String sql = "SELECT konto,sifm,nazivm,ROUND(kolic,2)," +
		"ROUND(cena,2),ROUND(vredn,2) FROM "+tblpromet+" WHERE brdok=0 AND vrdok=0 AND mag=" + 
		//odmag + " AND jur=" + odrj + " AND pre=" + pPre + " AND kolic!=0 ";
		odmag + " AND jur=" + odrj + " AND pre=" + pPre;
	  Statement stat = null;

      try {
         stat = connection.createStatement();
               
            ResultSet rs = stat.executeQuery( sql );
			int i = 1;
        while ( rs.next() ) {

               String[] record = new String[7];
				record[0] = rs.getString("konto");
				record[1] = rs.getString("sifm");
				record[2] = nad.nadjiRobu(connection,rs.getInt("sifm"));
				record[4] = rs.getString("ROUND(kolic,2)");
				record[5] = rs.getString("ROUND(cena,2)");
				record[6] = rs.getString("ROUND(vredn,2)");

				record[3] = nad.nadjiJMere(connection,Integer.parseInt(record[1]));			

				//prikaz podataka----------------------
		  try {
			output.write("          <fo:table-row text-align=\"end\">\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + record[1] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + record[0] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[2]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + record[3] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell  text-align=\"end\">\n"); 
			output.write("              <fo:block>" + record[4] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell  text-align=\"end\">\n"); 
			output.write("              <fo:block>" + record[5] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell  text-align=\"end\">\n"); 
			output.write("              <fo:block>" + record[6] + "\n"); 
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
					System.out.println("Error sql." + sqlex);
         }
		//.....................................................................................
		finally{
			if (stat != null){
				try{
					stat.close();
					stat = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori stat");}}
		}
		//.....................................................................................
	 }
//---------------------------------------------------------------------------------
 private void popuniPodnozje() {

	//uzimanje podataka---------------------------------------------------
    String sql = "SELECT ROUND(SUM(vredn),2) FROM "+tblpromet+" WHERE brdok=0 AND vrdok=0 AND mag=" + 
		odmag + " AND jur=" + odrj + " AND pre=" + pPre + " AND kolic!=0 " +
		" GROUP BY brdok";
	  Statement stat = null;

      try {
         stat = connection.createStatement();
               
            ResultSet rs = stat.executeQuery( sql );
			int i = 1;
        while ( rs.next() ) {

               String[] record = new String[1];
				record[0] = rs.getString("ROUND(SUM(vredn),2)");

				//prikaz podataka----------------------
		  try {
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"2pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" >\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row text-align=\"end\">\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "UKUPNO: " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + record[0] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"1pt\" space-after.optimum=\"1pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"2pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
				}
				catch ( java.io.IOException ioe ) {
					System.out.println("Error writing to output file.");
				}
				//while petlja   ----------------------
				i++;
           }
         }
         catch ( SQLException sqlex ) {
					System.out.println("Error sql." + sqlex);
         }
		//.....................................................................................
		finally{
			if (stat != null){
				try{
					stat.close();
					stat = null;
				}catch (Exception e){
					JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori stat");}}
		}
		//.....................................................................................
   }
 }//end of class fPrintKarKon
