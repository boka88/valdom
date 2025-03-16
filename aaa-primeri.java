//Pravljenje exe-jar fajla za start programa
jar -cvfm fin.jar MANIFEST.MF .
//raspakivanje nekog .jar fajla
jar -xvf ime.jar

LAF za robno tamni ekrani
--------------------------------
Color c = new Color(65,166,199);

svetli ekrani
---------------------------------
Color c = new Color(110,218,253);

//ubacivanje u panel
		Color c;
		if (laf>8 && laf!=14)
		{
			c = new Color(65,166,199);
		}else{
			c = new Color(183,218,252);
		}
		p.setBackground(c);
=============================================================================
SETOVANJE FONTA U TABELI IZ valdom.properties parametara tablefont
		jtbl.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.tablefont));
		JTableHeader header = jtbl.getTableHeader();
        header.setFont(new Font("Arial", Font.PLAIN,ConnMySQL.tablefont));















znak & i " u nazivima su nepozeljni ali ako mora onda se u XML
fajlu i ostalim tekstualnim fajlovima mora zameniti sa:
&   npr.   B&B     B&amp;B
<	&alt;
>	&gt;
"	&quot;
'	&apos;

if (myString.contains("&")) { ako ispituje da li string ima &
if (myString.contains("\"")) {   ako ispituje da li string ima "  dvostruki navodnik (double quote)
if (myString.contains("'")) { ako ispituje da li string ima ' jednostruki navodnik (single quote)

String originalString = "This & that";
String replacedString = originalString.replace("&", "&amp;");
============================================================================



//formatiranje velikih brojeva u String---------------------------------
String pattern = "##########.00";
DecimalFormat myFormatter = new DecimalFormat(pattern);
String output = myFormatter.format(value);
System.out.println(value + " " + pattern + " " + output);

//druga metoda za formatiranje brojeva (korisnicki definisana 
//funkcija u okviru neke klase)
//-----------------------------------------------------------
public class Formatiraj(){
	NumberFormat nf = null;
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setGroupingUsed(false);

	public String format(double dbl)
	{
		return nf.format(dbl);
	}
}
	String strbroj = Formatiraj.format(doublebroj);

//----------------------------------------------------------------------
JOptionPane.showMessageDialog(null, message);		//poruka bez panela
//----------------------------------------------------------------------
//Poruka za potvrdu Yes,No,Cancel(0,1,2) vraca Integer
	   int aaa=0;
	   aaa = JOptionPane.showConfirmDialog(this,"Da li stvarno bri\u0161ete sve podatke");
	   if (aaa == 0){
		   Start();
	   }
//----------------------------------------------------------------------

import java.math.BigDecimal.*;
java.math.BigDecimal broj;
double aaa;
aaa = broj.doubleValue(); -konvertovanje u double


slova kod stampe FOP-----------------------
-------- velika slova ----------JAVA-----------
&#x0160;	- Saljivo			\u0160
&#x0110;	- Djordje			\u0110
&#x010c;	- Covek				\u010c
&#x0106;	- Corak				\u0106
&#x017d;	- Zabokrecina		\u017d
--------- mala slova ----------------------
&#x0161;	- saljivo			\u0161
&#x0111;	- djordje			\u0111
&#x010d;	- covek				\u010d
&#x0107;	- corak				\u0107
&#x017e;	- zabokrecina		\u017e
-------------------------------------------
"&#160;&#160;"    = 2 znaka za odvajanje teksta od pocetka

border-style=\"solid\" border-width=\"0.5pt\"	
font-weight=\"bold\"
font-weight=\"plain\"

border-top-width=\"0.5pt\"
border-bottom-width=\"0.5pt\" border-style=\"solid\"
border-right-width=\"0.5pt\"
border-left-width=\"0.5pt\"
//--------------------------------umetanje slike u izvestaj -----------------------------------
		output.write("              <fo:block space-before.optimum=\"10pt\">\n"); 
		output.write("					<fo:external-graphic src=\"url(proba.gif)\"/>\n");
		output.write("              </fo:block>\n"); 

		duzina slike = 20cm,  sirina = 2 - 2.5 cm
//-------------------------------stampanje vise naloga odjednom mora imati : --------------------------
  <fo:page-sequence master-reference="first">
    <fo:static-content flow-name="xsl-region-before">
	</fo:static-content>
    <fo:static-content flow-name="xsl-region-after">
	</fo:static-content>
    <fo:flow flow-name="xsl-region-body">

	  </fo:flow>
  </fo:page-sequence>
						// izmedju dve strane==========
  <fo:page-sequence master-reference="first">
    <fo:static-content flow-name="xsl-region-after">
	</fo:static-content>
    <fo:flow flow-name="xsl-region-body">
	  </fo:flow>
  </fo:page-sequence>
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
//--------------------------------------------------------------------------------

odredjivanje visine celije---------"               "---------------------------------------------------------
<fo:table-cell text-align=\"left\" height=\"2.8cm\" border-right-width=\"0.5pt\" border-right-style=\"solid\">

definisanje tabele kad ocemo da imamo delimicne bordere
-------------------------------------------------------
<fo:table border-collapse=\"separate\" table-layout=\"fixed\">

				** poravnanje udesno kod txt polja **
 //--------------------------------------------------------------------------
		t[0].setHorizontalAlignment(JTextField.TRAILING); na kraj polja
		t[0].setHorizontalAlignment(JTextField.LEFT);
		LEADING
		TOP
		SOUTH
		CENTER
		EAST
		RIGHT
 //--------------------------------------------------------------------------
		lblRadim = new JLabel("");
		lblRadim.setFont(new Font("Arial",Font.BOLD,14));
		lblRadim.setFont(new Font("Arial",Font.PLAIN,10));
		lblRadim.setFont(new Font("YU-Time",Font.TRUETYPE_FONT,10))

		lblRadim.setForeground(Color.red);

	lblRadim.setText("Te\u010de obrada molim sa\u010dekajte ......");
	paintAll(getGraphics());
	lblRadim.setForeground(Color.blue);
	lblRadim.setText("Obrada zavr\u0161ena");

 //--------------------------------------------------------------------------
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 && t[3].getText().trim().length() != 0 &&
		t[4].getText().trim().length() != 0 && t[5].getText().trim().length() != 0 &&
		t[6].getText().trim().length() != 0 && t[7].getText().trim().length() != 0 &&
		t[8].getText().trim().length() != 0 && t[9].getText().trim().length() != 0 &&
		t[10].getText().trim().length() != 0 && t[11].getText().trim().length() != 0)
	{



	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
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
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska: " + sqlex);
			JOptionPane.showMessageDialog(null, "Upit:" + sql);
      }
	  finally{
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
	  }
  }
//----------------------------------------------------------------------------------------------
	  Statement statement = null;
//----------------------------------------------------------------------------------------------
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


//splash labela==============================================================================
    private JWindow splashScreen = null;
    private JLabel splashLabel = null;

	
	
	public void createSplashScreen() {
	splashLabel = new JLabel(createImageIcon("Splash.jpg", "Splash.accessible_description"));
	
	    splashScreen = new JWindow(getFrame());
	    splashScreen.getContentPane().add(splashLabel);
	    splashScreen.pack();
	    Rectangle screenRect = getFrame().getGraphicsConfiguration().getBounds();
	    splashScreen.setLocation(
         screenRect.x + screenRect.width/2 - splashScreen.getSize().width/2,
		 screenRect.y + screenRect.height/2 - splashScreen.getSize().height/2);
    }

    public void showSplashScreen() {
	    splashScreen.show();
    }

    /**
     * pop down the splash screen
     */
    public void hideSplash() {
	if(!isApplet()) {
	    splashScreen.setVisible(false);
	    splashScreen = null;
	    splashLabel = null;
	}
    }
	//----AKCIJA--------------------------------------------------------------------------
		createSplashScreen();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			showSplashScreen();}});

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			showSwingSet2();
			hideSplash();}});

//==========================================================================================
    private ResourceBundle bundle = null;

    public String getString(String key) {
		String value = null;
		try {
			value = getResourceBundle().getString(key);
		} catch (MissingResourceException e) {
			System.out.println("java.util.MissingResourceException: Couldn't find value for: " + key);
		}
		if(value == null) {
			value = "Could not find resource: " + key + "  ";
		}
		return value;
    }
//====================properties files=========================================================
   	  FileOutputStream fos = new FileOutputStream("birds.out");
      
      java.util.Properties xmlProps = OutputPropertiesFactory.getDefaultMethodProperties("xml");
      xmlProps.setProperty("indent", "yes");
      xmlProps.setProperty("standalone", "no");      
      Serializer serializer = SerializerFactory.getSerializer(xmlProps);
      serializer.setOutputStream(fos);

//==========================================================================================
		  //polje se moze menjati u tabeli samo ako je definisan primarni kljuc
		statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                                java.sql.ResultSet.CONCUR_UPDATABLE);
		statement.setFetchSize(25);

			String query = "SELECT * FROM tmpfinkar ORDER BY datum, vrprom, brrac";
		         ResultSet rs = statement.executeQuery( query );
		         while(rs.next()){
					dugu = rs.getDouble("dugu");
					potr = rs.getDouble("potr");
					saldo = saldo + dugu - potr;
					//rs.moveToUpdateRow();
					rs.updateDouble("saldo",saldo);
					rs.updateString("naziv","akslajd");
					rs.updateInt("sifra",11);


			String strsql = "DROP TABLE IF EXISTS artikli";
			izvrsi(strsql);

			  strsql = "CREATE TABLE artikli ("+
			  "vrrobe char(2) NOT NULL default '',"+
			  "firma char(2) NOT NULL default '',"+
			  "sifra char(5) NOT NULL default '',"+
			  "barkod char(13) NOT NULL default ' ',"+
			  "naziv char(50) NOT NULL default '',"+
			  "jm char(3) NOT NULL default '',"+
			  "tb char(2) NOT NULL default ' ',
			  "taksa decimal(10,2) NOT NULL default '0',"+
			  "sjm char(1) NOT NULL default '',"+
			  "nabcena double NOT NULL default '0',"+
			  "velcena double NOT NULL default '0',"+
			  "cena double NOT NULL default '0',"+
			  "gramaza double NOT NULL default '0',"+
			  "kolicina int NOT NULL default '0',"+  
			  "tezina double NOT NULL default '0',"+
			  "minkolic int(3) NOT NULL default '0',"+
			  "opis char(50) NOT NULL default ' ',"+
			  "PRIMARY KEY  (vrrobe,sifra)"+
			  ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
			izvrsi(strsql);



//============================setovanje mysql servera da moze sa druge masine==========
sa mySQLLyog konektujes se u bazu 'mysql' i ukucas sql komandu:

	GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'
	IDENTIFIED BY 'bin123' WITH GRANT OPTION;

 //====================================================================================
	Export u XML fajl

import javax.xml.parsers.*;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.xml.sax.*;

	try {
	//Ovako se kreira DOM Document objekat
	DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document doc = docBuilder.newDocument();
	//Dalje je sve standardni DOM. npr.
	Element rootElem = doc.createElement("Data");
	doc.appendChild(rootElem);

		Element firstElem = doc.createElement("ROW");
		//firstElem.setAttribute("id","xxxxx");
		//firstElem.setTextContent("aaaaaa");
		Element sifm = doc.createElement("sifm");
		Element nazivm = doc.createElement("nazivm");

		sifm.setTextContent(record[0]);
		firstElem.appendChild(sifm);
		rootElem.appendChild(firstElem);

	FileWriter fw = new FileWriter("./slanje/sifrobe.xml");
	OutputFormat outFormat = new OutputFormat();
	outFormat.setEncoding("UTF-8");
	outFormat.setIndenting(true);

	XMLSerializer ser = new XMLSerializer(fw, outFormat);
	ser.serialize(doc);
	}
	catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex);		
	}
//===================================================================================================
//	pokretanje eksternog programa
	String acrobat="/../../Program Files/Adobe/Acrobat 5.0/Reader/AcroRd32.exe"
		try{
			Process proc = Runtime.getRuntime().exec(new String(akrobat + " ./pdf/stampa.pdf"),null);
		}catch (Exception e){System.out.println(e);}
//===================================================================================================
Ako kod prikaza tabele sa rs.getBigDecimal("ROUND(nekiiznos,2)") n.p.r. pocne da brljavi prikaz 
	proveriti da li tabela ima "NULL" vrednosti i to srediti i staviti u "Alter table" da ne
	moze da ima.

//===================================================================================================
//-------------------------------------------------------------------------------
RAD Sa vector nizom vracanje podataka iz niza:
	
	Vector podaci;
	int duzina = podaci.size();

	if (podaci.isEmpty())
	{
	}else{

        while ( i < duzina  ) {
               String[] record = new String[2];
				record[0] = ((String[])podaci.elementAt(i))[0];	
				record[1] = ((String[])podaci.elementAt(i))[1];	
		}
	}
//-------------------------------------------------------------------------------
Sortiranje u tabeli po kolonama (treba kopirati TableSorter.java i TableMap.java u folder)
dodati sledece kod funkcije buildTable() posle reda
	   	qtbl.query(sql);
	dodaje se:
		TableSorter sorter = new TableSorter(qtbl); 
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); 
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
		//ovu liniju komentarisati
 	   	//jtbl = new JTable( qtbl );

Klik misem (levi ili desni taster) na kolonu sortira tabelu po rastucem redosledu a 
Shift+click sortira po opadajucem redosledu.


//======================================================================================
		//n-ti koren iz nekog broja gde je funkcija Math.pow(double broj,double stepen)
		//gde je stepen  = 1/n
		koren = Math.pow(broj,stepen));
//======================================================================================
	java.util.Date d1,d2;

	int result = d1.compareTo(d2);(DAJE BROJ DANA IZMEDJU)

	Calendar now = Calendar.getInstance();  // trenutni datum
	 //dodaje broj dana u kalendar npr:
	 now.add(Calendar.DATE,4);


//======================================================================================
	//Dodavanje broja dana u neki datum i trazenje novog datuma
 public void dodajDane(){
		String vraca="";
		java.util.Date datee=null;
		String oddatuma = t[3].getText().trim();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar c = Calendar.getInstance();
		
		//setovanje Date promenljive iz stringa (polja)
		try{
			datee = sdf.parse(oddatuma);
		}catch (ParseException ee){
			JOptionPane.showMessageDialog(null, "Greska u konvertovanju datuma:"+ee);		
		}
		
		//setovanje kalendara na osnovu datuma
		c.setTime(datee);

		//dodavanje broja dana u kalendar
		c.add(Calendar.DATE,10);

		//uzimanje dobijenog datuma iz kalendara
		vraca = sdf.format(c.getTime());
		t[4].setText(vraca);
 }
//======================================================================================
        tt[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        tt[8].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziMestaTroska();}});


================ JASPER REPORT =================================
	iReport	- ubacivanje rednog broja u izvestaj po redovima
	ide u "Variables"	REPORT_COUNT
 
//======================================================================================
	PREUZIMANJE POLJA IZ TABELE PO REDOSLEDU PRIKAZA
		int kojirec = jtbl.getSelectedRow();
		double	koji = Double.parseDouble(jtbl.getValueAt(kojirec,0).toString().trim());
		int		koji1 = Integer.parseInt(jtbl.getValueAt(kojirec,1).toString().trim());
		String	koji2 = jtbl.getValueAt(kojirec,2).toString().trim();
		ili  t[0].setText(jtbl.getValueAt(kojirec,0).toString());
//======================================================================================
=====================================================================================
	//dodavanje rednog broja u tabeli
	sql = "ALTER TABLE tmpevidnal ADD rbr bigint NOT NULL AUTO_INCREMENT key";
=====================================================================================
BRISANJE SLOGOVA KAD SE KORISTI VISE TABELA:
DELETE t1, t2 FROM t1, t2, t3 WHERE t1.id=t2.id AND t2.id=t3.id;

primer kad brisemo slogove iz tabele "sifarnikrobe" a imamo uslov
vezan i za tabelu "koop"  :
DELETE sifarnikrobe FROM sifarnikrobe,koop WHERE sifarnikrobe.sifm=koop.sifm
=====================================================================================
	//upit sa jednim uslovom  m.brrac=a.brrac
	strsql = "UPDATE tmpkepu a JOIN (SELECT brrac,sum(vredn) as uku FROM mprom" +
	" WHERE (vrdok =10 OR vrdok=15) GROUP BY brrac) m ON " +
	" m.brrac=a.brrac SET a.dugu = m.uku";	
	izvrsi(strsql);
	//upit sa dva yadnja uslova
	strsql = "UPDATE tmpkepu a JOIN (SELECT brrac,sum(vredn) as uku FROM mprom" +
	" WHERE (vrdok =10 OR vrdok=15) GROUP BY brrac) m ON " +
	" m.brrac=a.brrac AND m.vrsta=a.vrsta  SET a.dugu = m.uku";	

=====================================================================================
IMPORT PODATAKA IZ CSV (excel fajl) u tabelu. Treba napraviti isti broj polja kao u CSV

LOAD DATA INFILE 'c:/11/opstine.csv' INTO TABLE opstine
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"' 
LINES TERMINATED BY '\r\n';

Ako javi gresku ide se na dole:

LOAD DATA LOCAL INFILE  'c:/aaa/mzag1.csv'
IGNORE INTO TABLE mzag 
FIELDS TERMINATED BY ';'
ENCLOSED BY '\"' 
LINES TERMINATED BY '\n' 
(pre,jur,mag,ui,brun,datum,opis,brst,z);
=====================================================================================
CITANJE IZ .properties FAJLA
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 
public class App {
  public static void main(String[] args) {
 
	Properties prop = new Properties();
	InputStream input = null;
 
	try {
 
		input = new FileInputStream("config.properties");
 
		// load a properties file
		prop.load(input);
 
		// get the property value and print it out
		System.out.println(prop.getProperty("database"));
		System.out.println(prop.getProperty("dbuser"));
		System.out.println(prop.getProperty("dbpassword"));
 
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 
  }
}
=====================================================================================
UPIS U .properties FAJL

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
 
public class App {
  public static void main(String[] args) {
 
	Properties prop = new Properties();
	OutputStream output = null;
 
	try {
 
		output = new FileOutputStream("config.properties");
 
		// set the properties value
		prop.setProperty("database", "localhost");
		prop.setProperty("dbuser", "mkyong");
		prop.setProperty("dbpassword", "password");
 
		// save properties to project root folder
		prop.store(output, null);
 
	} catch (IOException io) {
		io.printStackTrace();
	} finally {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
	}
  }
}
//=====================================================================================
		Dialog za pitanje a default je dugme No

		int selection = JOptionPane.showOptionDialog(null,
			"Da li radite bekap podataka","Confirm",JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,null,
			new String[]{"Yes","No"},"No");
		if (selection == JOptionPane.YES_OPTION)
		{
			obrada();
		}
//=====================================================================================
Uzimanje vrednosti kolona iz tabele
		int koji = jtbl.getSelectedRow();
		t[0].setText(jtbl.getValueAt(koji,0).toString());
		t[1].setText(jtbl.getValueAt(koji,1).toString());
//=====================================================================================
selektovanje polja iz dve tabele:

SELECT sifarnikrobe.nazivm,msta.prc FROM sifarnikrobe 
	INNER JOIN msta ON sifarnikrobe.sifm=msta.sifm;
----------------------------------------------------
The LEFT JOIN daje sve kolone u levoj tabeli u zavisnosti od naredbe ON 
	ON sifarnikrobe.sifm = msta.sifm;.

Syntax:

SELECT sifarnikrobe.sifm,sifarnikrobe.nazivm,jmere,tarb  
FROM sifarnikrobe  
LEFT JOIN msta  
ON sifarnikrobe.sifm = msta.sifm;

ovde moramo navesti "sifarnikrobe.sifm,sifarnikrobe.nazivm" zato sto
sifm i nazivm postoji i u tabeli msta a ostala polja kojih nema u msta mogu biti bez
sifarnikrobe.
---------------------------------------------------
The MySQL Right Outer Join returns all rows from the RIGHT-hand table specified 
in the ON condition and only those rows from the other table where he join condition is fulfilled.

SELECT columns  
FROM table1  
RIGHT JOIN table2  
ON table1.column = table2.column;  

SELECT officers.officer_name, officers.address, students.course_name, students.student_name  
FROM officers  
RIGHT JOIN students  
ON officers.officer_id = students.student_id;  

==================================================================================
	PANEL KAD NESTO RADI
		
import java.awt.Graphics;


		public JPanel lblPanel;
		public JLabel lblRadim;

		lblPanel = new JPanel();
		lblPanel.setLayout( null );
		lblPanel.setBounds(5,535,200,25);

		lblRadim = new JLabel("");
		lblRadim.setFont(new Font("Arial",Font.BOLD,14));
		lblRadim.setForeground(Color.red);
		lblRadim.setBounds(0,0,200,30);
		lblPanel.add(lblRadim);


		container.add( lblPanel );

			lblRadim.setText("Radim sacekajte....");
			lblPanel.paintImmediately(0, 0, lblPanel.getWidth(), lblPanel.getHeight());

			ili:
			lblRadim.setText("Radim sacekajte....");
			lblPanel.paintImmediately(0, 0, 200, 25);

KAD NECE DA PRIKAZE LABELU ODMAH:
		lblRadim.setText("Radim sacekajte....");
		lblPanel.paintImmediately(0, 0, 200, 25);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					//----------------------------------------- deo koda koji ide iza lblRadim
					JInternalFrame pred = new mLagerMinus();
					FinFrame.desktop.add(pred, "");
					pred.setVisible(true);
					pred.setSelected(true);

					lblRadim.setText("");
					lblPanel.paintImmediately(0, 0, 200, 25);
					//------------------------------------------------------------------------
					return null;
				}

				@Override
				protected void done() {
				}
			};

			// Pokreni SwingWorker
			worker.execute();	
			
====================================================================================================
	bojanje redova u tabeli

	Color boja = new Color(81,206,128);

		qtbl.query(sql);
		jtbl = new JTable( qtbl )
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);

				//  Color row based on a cell value
				if (!isRowSelected(row))
				{
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);
					//uzima vrednost zadnje kolone "poslato"
					int type = (Integer)getModel().getValueAt(modelRow, 6); //individualVatId
					
					if (type>0 ) c.setBackground(boja);
				}
				return c;
			}
		};

====================================================================================================
INSERT INTO mzag(pre,jur,mag,ui,brun,datum,opis,brst,z)
	VALUES(1,1,99,0,38,'2024-10-30','Nivelacija',1,0);

INSERT INTO mprom(nazivm,pre,jur,mag,ui,indikator,brun,rbr,prodavnica,sifm,konto,datum,valuta,vrdok,brdok,
	kupacbr,brrac,kolic,cena,vredn,tarbr,por1,pcena,pcena1,pvredn)
VALUES('Naziv proizvoda',1,1,99,0,0,38,1,0,5000,1340,'2024-10-30','2024-10-30',6,38,0,38,0,-100,-300,1,20,900,-100,-300)
	

cena = razlika cena(staracena-novacena)
vredn = cena*kolic
pcena = novacena
pcena1 = cena
pvredn=vredn
