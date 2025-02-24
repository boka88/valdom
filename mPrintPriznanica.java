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

public class mPrintPriznanica {
	private static Integer pageWidth;
	private static Integer pageHeight;
	private static Integer headerHeight;
	private static Integer footerHeight;
	private static java.io.FileWriter output;
	private mNadji nad;
	public Connection connection = null;
	private String nazivp,pPre,nazdok,datumn="";
	private int jur,odnal,donal;
	private String nazivk,mesto,adresa,nazivjur,nazivmag,nazivkonta,nazivmt;
	private String rek9,rek10,rek11,rek12,rek13,rek14,rek15,rek16,rek17;
	private String mestok,adresak,faxk,napomenak,uiznos="";

	
	private	int odrj,dorj,odmag,domag,konto,sifra,odsif,dosif,brdokm,koji,mestr;
  public mPrintPriznanica(Connection _connection,
						int _odrj,
						int _odmag,
						int _brdokm,
						int _koji) {

   // Exit when user clicks the frame\u75f4 close button.
		connection = _connection;
		odrj = _odrj;
		odmag = _odmag;
		brdokm = _brdokm;
		koji = _koji;

	//JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
	//tft.setValue(new java.util.Date());
	//datumn = tft.getText();

	pPre = new String("1");
	nazivp = new String("Valdom");
	int pred = Integer.parseInt(pPre);

	nad = new mNadji();
	nazivjur = nad.nadjiRJ(connection,odrj,pred);
	nazivmag = nad.nadjiMag(connection,odmag,pred);

	if (koji == 3)
	{
		nazivmt = nad.nadjiMT(connection,mestr,pred);
	}
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
	nadjiIOS();	
	
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
//------------------------------------------------------------------------
    public void nadjiIOS() {
		String query="",nazivkupca;
		int brdok;
		double saldo1;
		Statement statement=null;
			query = "SELECT DISTINCT brdok,kupacbr,nazivkupca,datum,mestok,adresak,faxk,napomenak " +
					" FROM tmpkarticarobe ORDER BY brdok";
	  try {
			statement = connection.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        while(rs.next()){
					brdok = rs.getInt("brdok");
					nazivkupca = rs.getString("nazivkupca");
					datumn = konvertujDatumIzPodataka(rs.getString("datum"));
					mestok = rs.getString("mestok");
					adresak = rs.getString("adresak");
					faxk = rs.getString("faxk");
					napomenak = rs.getString("napomenak");
					zaglavlje(brdok,nazivkupca);

				}//end while
				rs.close();
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
//------------------------------------------------------------------------
    public void nadjiPRED() {
		String query="";
		Statement statement=null;
			query = "SELECT *  FROM preduzeca where pre = " + Integer.parseInt(pPre);
	  try {
			statement = connection.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        while(rs.next()){
					rek9 = rs.getString("mesto");
					rek10 = rs.getString("adresa");
					rek11 = rs.getString("telefon");
					rek12 = rs.getString("pib");
					rek13 = rs.getString("pepdv");
					rek14 = rs.getString("ziror");
					rek15 = rs.getString("sido");
					rek16 = rs.getString("matbr");


				}//end while
				rs.close();
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska u nadjiPRED" + sqlex);
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
//------------------------------------------------------------------------
    public void nadjiMT(int _brdok) {
		String query="";
		Statement statement=null;
			query = "SELECT mtros,naztros " +
					" FROM tmpkarticarobe WHERE brdok=" + _brdok;
	  try {
			statement = connection.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					mestr = rs.getInt("mtros");
					nazivmt = rs.getString("naztros");
				}else{
					mestr = 0;
					nazivmt = " ";
				}
					rs.close();
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
		output.write("                           margin-left=\"5mm\"\n");
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
 private void zaglavlje(int _brdok, String _nazivkupca) {

	if (koji == 3)
	{
		nadjiMT(_brdok);
	}

	nadjiPRED();	


        try {
			output.write("  <fo:page-sequence \n"); 
			output.write("          master-reference=\"simple\">\n"); 
			output.write("		<fo:flow flow-name=\"xsl-region-body\">\n");
			//===========ZAGLAVLJE DOKUMENTA =============
			//prva linija-------------------------------------------------------------------
			// okvir oko dokumenta --------------------

			//output.write("      <fo:table table-layout=\"fixed\" >\n");
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" border-style=\"solid\" border-width=\"0.5pt\">\n");


			output.write("	<fo:table-column column-width=\"9.75cm\"/>\n");
			output.write("	<fo:table-column column-width=\"9.75cm\"/>\n");

			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			
			output.write("            <fo:table-cell text-align=\"left\"  font-size=\"10pt\">\n"); 
			output.write("              <fo:block>" + "OBVEZNIK PDV-ISPLATILAC PRIZNANICE - KUPAC" + "\n"); 
			output.write("              </fo:block>\n"); 	
			output.write("              <fo:block>" + konvUnicode("NAZIV PO\u0160ILJAOCA : ") + konvUnicode(nazivp) + "\n"); 
			output.write("              </fo:block>\n"); 	
			output.write("              <fo:block>" + konvUnicode("Adresa : ") + konvUnicode(rek9) + konvUnicode(rek10) + konvUnicode("Tel : ") + konvUnicode(rek11) + "\n"); 
			output.write("              </fo:block>\n"); 	
			output.write("              <fo:block>" + konvUnicode("PIB : ") + konvUnicode(rek12) + konvUnicode("EPPDV : ")+ konvUnicode(rek13) + "\n"); 
			output.write("              </fo:block>\n"); 	
			output.write("              <fo:block>" + konvUnicode("Broj ra\u010duna : ") + konvUnicode(rek14) + "\n"); 
			output.write("              </fo:block>\n"); 	
			output.write("              <fo:block>" + konvUnicode("Delatnost : ") + konvUnicode(rek15) + konvUnicode("Mati\u010dni broj : ") + konvUnicode(rek16) + "\n"); 
			output.write("              </fo:block>\n"); 	
			output.write("              <fo:block>" + konvUnicode("Priznanica izdata dana : ") + datumn + konvUnicode(" u : ") + konvUnicode(rek9) + "\n"); 
			output.write("              </fo:block>\n"); 	

			output.write("              <fo:block>" + "." + "\n"); 
			output.write("              </fo:block>\n"); 	

			output.write("            </fo:table-cell>\n"); 
		
			output.write("            <fo:table-cell text-align=\"left\"  font-size=\"10pt\">\n"); 
			output.write("              <fo:block>" + "POLJOPRIVREDNIK obveznik poreza na dohodak" + "\n"); 
			output.write("              </fo:block>\n"); 			
			output.write("              <fo:block>" + konvUnicode("gra\u0111ana na prihode od poljoprivrede i \u0161umarstva") + "\n"); 
			output.write("              </fo:block>\n"); 			
			output.write("              <fo:block>" + konvUnicode("na osnovu katastarskog prihoda - PRODAVAC") + "\n"); 
			output.write("              </fo:block>\n");
			output.write("              <fo:block>" + konvUnicode("Ime i prezime : ") + konvUnicode(_nazivkupca) + "\n"); 
			output.write("              </fo:block>\n"); 			
			output.write("              <fo:block>" + konvUnicode("Mesto Stanovanja : ") + konvUnicode(mestok) + "\n"); 
			output.write("              </fo:block>\n"); 			
			output.write("              <fo:block>" + konvUnicode("Ulica i broj : ") + konvUnicode(adresak) + "\n"); 
			output.write("              </fo:block>\n"); 			
			output.write("              <fo:block>" + konvUnicode("JMBG PIB : ") + konvUnicode(faxk) + "\n"); 
			output.write("              </fo:block>\n"); 			
			output.write("              <fo:block>" + konvUnicode("Broj poreskog re\u0161enja : ") + konvUnicode(napomenak) + "\n"); 
			output.write("              </fo:block>\n"); 			
	

			output.write("            </fo:table-cell>\n"); 		
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			// tabela bez bordera sa 2 kolone a jedan red --------------------
			output.write("      <fo:table table-layout=\"fixed\" >\n");
			output.write("	<fo:table-column column-width=\"9.75cm\"/>\n");
			output.write("	<fo:table-column column-width=\"0.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"9.75cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
						
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\"  font-size=\"14pt\">\n"); 
			output.write("              <fo:block>" + "PRIZNANICA Br." + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\"  font-size=\"14pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\"  font-size=\"14pt\">\n"); 
			output.write("              <fo:block>" + String.valueOf(_brdok) + "\n"); 
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
			//output.write("              <fo:block>" + konvUnicode(mesto) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// vrsta knjizenja -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 

			
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
			//output.write("              <fo:block>" + konvUnicode(adresa) + "\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//naziv vrste knj. -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			
			// tabela bez bordera sa 2 kolone a jedan red --------------------
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"12pt\" >\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("	<fo:table-column column-width=\"6cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 




			//treci red zaglavlja tabela --------------------------------------------
			//output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" border-style=\"solid\" border-width=\"0.2pt\">\n");
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" border-style=\"solid\" border-width=\"0.5pt\">\n");

			output.write("	<fo:table-column column-width=\"1cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"1.8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"0.7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");


			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// prazno  ----------------------------------------------------------------------
			//output.write("            <fo:table-cell text-align=\"left\">\n"); 
			//output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 

			output.write("              <fo:block>" + "Red br." + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// od datuma -----------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			//output.write("            <fo:table-cell text-align=\"center\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + konvUnicode("Naziv kupljenih dobara odnosno izvr\u0161enih usluga") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// datum   -----------------------------------------------------------------------
			//output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + "Jed.Mere" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// do datuma -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\" >\n"); 
			//output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Koli\u010dina") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//datum --------------------------------------------------------------------------
			//output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			//output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 

			output.write("            <fo:table-cell text-align=\"center\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + konvUnicode("Cena po jed.mere") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// prazno ---------------------------------------------------------------------------------------
			//output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("            <fo:table-cell text-align=\"center\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + "Vrednost bez PDV osnovica (4x5)" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// valuta -------------------------------------------------------------------------------
			//output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			//output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("            <fo:table-cell text-align=\"center\" border-style=\"solid\">  \n"); 
			
			
			output.write("              <fo:block>" + "%" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// valuta -------------------------------------------------------------------------------
			//output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + "PDV Iznos" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// valuta -------------------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\" >\n"); 
			output.write("              <fo:block>" + "UKUPNO ZA ISPLATU (6+8)" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 


			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			
			// kraj zaglavlja koje se ponavlja na svakoj stranici--------------------		

			
			//pocetak stampanja podataka

		popuniPodatkeIzBaze(_brdok);
		popuniUkupno(_brdok);
		popuniPodnozje();

		}
		catch ( java.io.IOException ioe ) {
            System.out.println("Error writing to output file.");
            System.exit( -1 );
        }
	}
//---------------------------------------------------------------------------------
 private void popuniPodatkeIzBaze(int _brdok) {

	//uzimanje podataka---------------------------------------------------
    String sql = "SELECT sifm,nazivm,ROUND(kolic,2)," +
		"ROUND(cena,2),ROUND(vredn,2),por1,ROUND(rabat,2),ROUND(pvredn,2) " +
		"FROM tmpkarticarobe WHERE brdok=" + _brdok;
	  Statement statement = null;

		 try {
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" border-style=\"solid\" border-width=\"0.5pt\">\n");
			output.write("	<fo:table-column column-width=\"1cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"1.8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"0.7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
		}
		catch ( java.io.IOException ioe ) {
			System.out.println("Error writing to output file.");
		}

	  
	  try {
         statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			int i = 1;
			int j = 0;

        while ( rs.next() ) {

               String[] record = new String[9];
				record[0] = rs.getString("sifm");
				record[1] = rs.getString("nazivm");
				record[3] = rs.getString("ROUND(kolic,2)");
				record[4] = rs.getString("ROUND(cena,2)");
				record[5] = rs.getString("ROUND(vredn,2)");
				record[6] = rs.getString("por1");
				record[7] = rs.getString("ROUND(rabat,2)");
				record[8] = rs.getString("ROUND(pvredn,2)");

				record[2] = nad.nadjiJMere(connection,Integer.parseInt(record[0]));			

				//prikaz podataka----------------------
		  try {
			output.write("          <fo:table-row text-align=\"end\">\n"); 
			//output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + String.valueOf(i) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 

			output.write("            <fo:table-cell text-align=\"left\" border-style=\"solid\">  \n"); 
			//output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + record[1] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + konvUnicode(record[2]) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[3] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[4] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + record[5] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[6] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[7] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[8] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n");
			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
			}
			
			i++;
         }//kraj while petlja   ----------------------

		//odavde dopunjava---------------------------
		if (i < 6)
		{

		for (j=6-i;j<7 ;j++ )
		{
		  try {
			output.write("          <fo:table-row text-align=\"end\">\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" >\n"); 
			output.write("              <fo:block>" + String.valueOf(j) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"left\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + " " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n");
			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
			}
		}
		}//end if

		  try {
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
			}
		//--------------------------------------------
         }
         catch ( SQLException sqlex ) {
					System.out.println("Error sql." + sqlex);
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
 private void popuniPodnozje() {
	String iznos="",zirorn="",datumm="",mesto="";
	//uzimanje podataka---------------------------------------------------
				//prikaz podataka----------------------
		  try {
			output.write("<fo:block text-align=\"center\" space-before.optimum=\"5pt\" space-after.optimum=\"5pt\">");
			output.write("              </fo:block>"); 

			output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" >\n");
			//output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" border-style=\"solid\" border-width=\"0.5pt\">\n");

			output.write("	<fo:table-column column-width=\"7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"1cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("              <fo:block>" + konvUnicode("Promet navedenih roba i usluga izvr\u0161en je dana :") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\"  >\n"); 
			output.write("              <fo:block>" + datumn + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "u" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode(rek9) + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 


			output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" >\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + konvUnicode("Dobra ili usluge isporu\u010dio") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "Dobra ili usluge primio" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "____________________________________________________" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"center\">\n"); 
			output.write("              <fo:block>" + "M.P." + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + "____________________________________________________" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			
			
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" >\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"9cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + konvUnicode("\u017diro ra\u010dun") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\">\n"); 
			output.write("              <fo:block>" + zirorn + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" >\n");
			output.write("	<fo:table-column column-width=\"10cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "L.K. ___________________ izd. _______________________" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" >\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"3cm\"/>\n");
			output.write("	<fo:table-column column-width=\"8cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "Ukupna vrednost robe - usluge iz kolone 9 u iznosu od" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"end\" >\n"); 
			output.write("              <fo:block>" + uiznos + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "&#160;&#160;" + konvUnicode("dinara, ispla\u0107ena dana ___________________ ") + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			
			
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"8pt\" >\n");
			output.write("	<fo:table-column column-width=\"4.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"4.5cm\"/>\n");


			output.write("    <fo:table-body>\n"); 
			output.write("          <fo:table-row >\n"); 
			// naziv preduzeca --------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "Potpis primaoca novca : " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("              <fo:block>" + "&#160;&#160;" + "______________________" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\"  >\n"); 
			output.write("              <fo:block>" + "Potpis isplatioca novca : " + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// naslov -----------------------------------------------------------------------
			output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("              <fo:block>" + "&#160;&#160;" + "_______________________" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			// strana -----------------------------------------------------------------------
			output.write("          </fo:table-row>\n"); 
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 

			output.write("<fo:block text-align=\"center\" space-before.optimum=\"10pt\" space-after.optimum=\"10pt\">");
			output.write("<fo:leader leader-pattern=\"rule\" rule-thickness=\"0.5pt\" leader-length=\"100%\"/>");
			output.write("              </fo:block>"); 
			
			
			output.write("    </fo:flow>\n");
			output.write("  </fo:page-sequence>\n");

			
			}
			catch ( java.io.IOException ioe ) {
					System.out.println("Error writing to output file.");
					System.exit( -1 );
			}
	 }
//---------------------------------------------------------------------------------
 private void popuniUkupno(int _brdok) {

	//uzimanje podataka---------------------------------------------------
    String sql = "SELECT ROUND(SUM(pvredn),2),ROUND(SUM(rabat),2),ROUND(SUM(vredn),2),ROUND(SUM(kolic),2) " +
		"FROM tmpkarticarobe WHERE brdok=" + _brdok;
	  Statement statement = null;

		 try {
			output.write("      <fo:table table-layout=\"fixed\" font-size=\"10pt\" border-style=\"solid\" border-width=\"0.5pt\">\n");
			output.write("	<fo:table-column column-width=\"1cm\"/>\n");
			output.write("	<fo:table-column column-width=\"5.5cm\"/>\n");
			output.write("	<fo:table-column column-width=\"1.8cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"0.7cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("	<fo:table-column column-width=\"2.2cm\"/>\n");
			output.write("    <fo:table-body>\n"); 
		}
		catch ( java.io.IOException ioe ) {
			System.out.println("Error writing to output file.");
		}

	  
	  try {
         statement = connection.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			int i = 1;
			int j = 0;

        if ( rs.next() ) {

               String[] record = new String[4];
				record[0] = rs.getString("ROUND(SUM(pvredn),2)");
				record[1] = rs.getString("ROUND(SUM(rabat),2)");
				record[2] = rs.getString("ROUND(SUM(vredn),2)");
				record[3] = rs.getString("ROUND(SUM(kolic),2)");
				uiznos = record[0] ;

				//prikaz podataka----------------------
		  try {
			output.write("          <fo:table-row text-align=\"end\">\n"); 
			//output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 

			output.write("            <fo:table-cell text-align=\"left\" border-style=\"solid\">  \n"); 
			//output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + "Ukupno:" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell text-align=\"left\">\n"); 
			output.write("            <fo:table-cell border-style=\"solid\" border-width=\"0.5pt\">\n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell text-align=\"left\" >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[3] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("              <fo:block>" + record[2] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + "" + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[1] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			//output.write("            <fo:table-cell >\n"); 
			output.write("            <fo:table-cell text-align=\"right\" border-style=\"solid\">  \n"); 
			output.write("              <fo:block>" + record[0] + "\n"); 
			output.write("              </fo:block>\n"); 
			output.write("            </fo:table-cell>\n"); 
			output.write("          </fo:table-row>\n");
			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
			}
			
		}//end if

		  try {
			output.write("        </fo:table-body>\n"); 
			output.write("      </fo:table>\n"); 
			}
			catch ( java.io.IOException ioe ) {
				System.out.println("Error writing to output file.");
			}
		//--------------------------------------------
         }
         catch ( SQLException sqlex ) {
					System.out.println("Error sql." + sqlex);
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
