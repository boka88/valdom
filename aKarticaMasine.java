import java.io.*;
import java.sql.*;
import java.sql.ResultSet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Action.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.lang.*;
import javax.swing.table.*;
import java.util.*;
import java.util.Set.*;
import java.text.*;
import javax.swing.KeyStroke.*;

import javax.swing.KeyStroke.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.PdfMerger;



public class aKarticaMasine extends JInternalFrame implements InternalFrameListener
	{
	public static mQTMUplate1 qtbl;
   	public static JTable jtbl;
	private JButton novi,trazi;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector pregledi = new Vector();
	private Vector suplate = new Vector();
	private Vector buplate = new Vector();
	private Vector pregledi1 = new Vector();
	private Vector pregledi2 = new Vector();
	private Vector pregledi3 = new Vector();
	private boolean izmena = false,novislog=false;
	private int koji,koji2,prvisledeci=0;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,koji1,brkvara;
	private JButton unesi,izmeni;
	private JButton eho,akuser;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static FormField t[],mmoj;
   	private JLabel  l[];
	public static JLabel mkdisplej,lblPacijent;
   	private int n_fields,vrdok=1,zadnjibroj=0,kojipacijent=0;
	private aPanelPregled pregled ;
	private int brojpdffajla=0;
//------------------------------------------------------------------------------------------------------------------
    public aKarticaMasine() {
		super("", true, true, true, true);
		setTitle("Kartica ma\u0161ine     ");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		kojipacijent = 0;

		JPanel main = new JPanel();
		main.setLayout( null );

		JPanel glavni = new JPanel();
		glavni.setLayout( null );

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,280,350);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		buttonPanel.setBounds(5,285,350,30);

		unesi = new JButton("\u0160tampa");
		unesi.setMnemonic('P');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Stampa();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		buttonPanel.add( unesi );


		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.setBounds(835,620,100,30);
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});

		JButton html = new JButton("Svi kvarovi");
		html.setMnemonic('S');
		html.setBounds(635,620,100,30);
		html.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   SviKvarovi(); }});

		JButton karton = new JButton("Karton ma\u0161ine");
		karton.setMnemonic('M');
		karton.setBounds(370,620,120,30);
		karton.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   KartonMasine(); }});

		uzmiKonekciju();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		pregled  = new aPanelPregled(connection);
		pregled.setBounds(370,5,600,600);

		main.add(buildFilterPanel());
		main.add(buildNazivPanel());
		main.add(buildTable());
		main.add(pregled);
		main.add(izlaz);
		main.add(html);
		main.add(karton);

		getContentPane().add(main);
		pack();
		setSize(980,720);
		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));
		t[0].requestFocus();
		/*
		if (kojipacijent>0)
		{
			uzmiPreglede();
		}
		*/
	}
//------------------------------------------------------------------------------------------------------------------
    public void internalFrameClosing(InternalFrameEvent e) {}
    public void internalFrameClosed(InternalFrameEvent e) {}
    public void internalFrameOpened(InternalFrameEvent e) {}
    public void internalFrameIconified(InternalFrameEvent e) {}
    public void internalFrameDeiconified(InternalFrameEvent e) {}
    public void internalFrameActivated(InternalFrameEvent e) {}
    public void internalFrameDeactivated(InternalFrameEvent e) {}
	protected void fireInternalFrameEvent(int id){
		if (id == InternalFrameEvent.INTERNAL_FRAME_CLOSING ) {
				if (connection != null){
					try {connection.close(); } 
					catch (Exception f) {
						JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
					}
				}

		} 
    }
	public void Izlaz(){
		try{
				this.setClosed(true);}
		catch (Exception e){
				JOptionPane.showMessageDialog(null, e);}
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNazivPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder("") );

        l = new JLabel[1]; 


        mkdisplej = new JLabel("");
		mkdisplej.setBounds(10,5,250,30);
		mkdisplej.setFont(new Font("Arial",Font.BOLD,14));

        p.add(mkdisplej); 

		p.setBounds(5,5,350,40);
		return p;
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder("") );

		t = new FormField[2]; 
		JLabel lblPac = new JLabel("Ma\u0161ina:");
		lblPac.setBounds(5,5,70,25);

		String fmm="******";
        t[0] = new FormField(createFormatter(fmm,1));
		t[0].setBounds(80,5,50,30);
		if (kojipacijent>0)
		{
			t[0].setText(String.valueOf(kojipacijent));
		}
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {uzmiPreglede();}});
        
		t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[0].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziMasine();}});
		
		trazi = new JButton("Tra�i kvarove");
		trazi.setMnemonic('T');
		trazi.setBounds(140,5,130,25);
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   uzmiPreglede(); }});
        trazi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        trazi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {uzmiPreglede();}});


        p.add(lblPac); 
        p.add(t[0]); 
        p.add(trazi); 

		p.setBounds(5,50,350,40);
		return p;
    }
//------------------------------------------------------------------------------------------------------------------
protected MaskFormatter createFormatter(String s, int koji) {
		MaskFormatter formatter = null;

		try {
			formatter = new MaskFormatter(s);
				switch (koji)	{
				case 1:
					//formater za cele brojeve
					formatter.setValidCharacters("0123456789 ");
					break;
				case 2:
					//formater za iznose sa decimalama
					formatter.setValidCharacters("0123456789. ");
					break;
				case 3:
					//za tekstualna polja bez ogranicenja
					break;
				case 4:
					//formater za datume
					formatter.setValidCharacters("0123456789/ ");
					break;
				case 5:
					//formater za datume
					formatter.setValidCharacters("0123456789.- ");
					break;
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
	}
//------------------------------------------------------------------------------------------------------------------
    public void uzmiPreglede() {
		if(proveriMasinu()){
			kojipacijent = Integer.parseInt(t[0].getText().trim());
			String sql = "SELECT * FROM kvarovi WHERE sifra=" + 
				t[0].getText().trim() + " ORDER BY datum DESC";
			popuniTabelu(sql);

		}
	}
//------------------------------------------------------------------------------------------------------------------
  public void prikaziMasine() {
		int t=2;
		aMasinePrik mvp = new aMasinePrik(t);
		mvp.setModal(true);
		mvp.setVisible(true);
  }
//------------------------------------------------------------------------------------------------------------------
  public void SviKvarovi() {
		 if (t[0].getText().trim().length()>0 )
		 {
			listajKvarove();
		 }else{
			 JOptionPane.showMessageDialog(this, "Nema podataka masine");
			 t[0].requestFocus();
		 }
  }
//------------------------------------------------------------------------------------------------------------------
  public void Html() {
	  int ppac=0;
	 if (t[0].getText().trim().length()>0 )
	 {
		 ppac = Integer.parseInt(t[0].getText().trim());
			JInternalFrame pred = new pProba(ppac);
			AutoFrame.desktop.add(pred, "");
		    try { 
				pred.setVisible(true);
				pred.setSelected(true); 
			} catch (java.beans.PropertyVetoException e2) {}
	}else{
		JOptionPane.showMessageDialog(this, "Nema podataka masine");
		t[0].requestFocus();
	}
  }
//------------------------------------------------------------------------------------------------------------------
  public void KartonMasine() {
		String queryy,sql="";
		int rbr=0;
		 if (t[0].getText().trim().length()>0 )
		 {
			sql = "DROP TABLE IF EXISTS tmpkvar";
			izvrsi(sql);
			//sql = "CREATE TEMPORARY TABLE tmpkvar like kvarovi";
			//sql = "CREATE TABLE tmpkvar like kvarovi";
			izvrsi(sql);
			sql = "CREATE TABLE  tmpkvar (" +
			"   rbr  int(11) NOT NULL," +
			"   datum  date NOT NULL DEFAULT '0000-00-00'," +
			"   opiskvara  mediumtext NOT NULL," +
			"   drugidokument  varchar(30) NOT NULL DEFAULT ''," +
			"  UNIQUE KEY  rbr  ( rbr )" +
			") ENGINE=MyISAM DEFAULT CHARSET=utf8";
			izvrsi(sql);
			sql = "INSERT INTO tmpkvar(rbr,datum,opiskvara,drugidokument) SELECT rbr,datum,opiskvara,drugidokument" +
				" FROM kvarovi WHERE kvarovi.sifra=" + t[0].getText().trim();
			izvrsi(sql);

			sql = "DROP TABLE IF EXISTS tmpkvardelovi";
			izvrsi(sql);
			sql = "CREATE TABLE `tmpkvardelovi` (" +
			  "`nazivm` VARCHAR(100) NOT NULL," +
			  "`jmere` VARCHAR(6) NOT NULL DEFAULT '-'," +
			  "`kolicina` INT(11) NOT NULL DEFAULT '0'" +
			  ") ENGINE=MYISAM DEFAULT CHARSET=utf8;";

			//sql = "CREATE TEMPORARY TABLE tmpkvardelovi like kvarovidelovi";
			//sql = "CREATE TABLE tmpkvardelovi like kvarovidelovi";
			izvrsi(sql);

			sql = "INSERT INTO tmpkvardelovi(nazivm,jmere,kolicina)" +
				" SELECT t3.nazivm, t3.jmere,t3.kolicina " +
				" FROM kvarovidelovi t3 " +
				" INNER JOIN tmpkvar t1 ON t3.brojkvara = t1.rbr";
			izvrsi(sql);



			jPrintKartonMasine pn = new jPrintKartonMasine(connection,t[0].getText().trim());
		 
		 }else{
			 JOptionPane.showMessageDialog(this, "Nema podataka masine");
			 t[0].requestFocus();
		 }
  }
//------------------------------------------------------------------------------------------------------------------
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(80,0);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 8; 
        for(i=3;i<n_fields;i++)
            t[i].setText("");		
		izmena = false;
	}
//--------------------------------------------------------------------------------------
    public void listajKvarove(){ 
		String queryy,sql="";
		int rbr=0;
		brojpdffajla = 0;
			sql = "DROP TABLE IF EXISTS tmpkvar";
			izvrsi(sql);
			sql = "CREATE TEMPORARY TABLE tmpkvar like kvarovi";
			izvrsi(sql);
			sql = "DROP TABLE IF EXISTS tmpkvardelovi";
			izvrsi(sql);
			sql = "CREATE TEMPORARY TABLE tmpkvardelovi like kvarovidelovi";
			izvrsi(sql);
		
			//brise postojece pdf fajlove iz podfoldera /pdf
			izbrisiFajlove();

		Statement statement = null;
		  try {
			 statement = connection.createStatement();
					queryy = "SELECT * FROM kvarovi WHERE sifra=" + t[0].getText().trim() +
						" ORDER BY datum";
						ResultSet rs = statement.executeQuery( queryy );
						while(rs.next()){
							brojpdffajla = brojpdffajla + 1;
							
							rbr = rs.getInt("rbr");
							sql = "DELETE FROM tmpkvar";
							izvrsi(sql);
							sql = "DELETE FROM tmpkvardelovi";
							izvrsi(sql);
							sql = "INSERT INTO tmpkvar SELECT * FROM kvarovi WHERE kvarovi.rbr=" + rbr;
							izvrsi(sql);
							sql = "INSERT INTO tmpkvardelovi SELECT * FROM kvarovidelovi WHERE kvarovidelovi.brojkvara=" + rbr;
							izvrsi(sql);
							
							jPrintKvarPDF pn = new jPrintKvarPDF(connection,t[0].getText().trim(),brojpdffajla);
						}
					SpojiPDF();
			
			}catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(this, "Greska u listajKvarove:"+sqlex);
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
//------------------------------------------------------------------------------------------------
   public void izbrisiFajlove(){
		String putanja="";
		putanja =".\\pdf";

		String dir = putanja;
		File folder = new File(dir);
        File fList[] = folder.listFiles();
		for (File f : fList) {
                f.delete(); 
        }
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
			JOptionPane.showMessageDialog(null, "Upit: " + sql);
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
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
	if (t[0].getText().trim().length()>0 )
	{
		//Stampa je u aPanelPregled
	}else{
		JOptionPane.showMessageDialog(this, "Nema podataka");
		t[0].requestFocus();
	}
  }
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
        this.setVisible(false);
    	}
//------------------------------------------------------------------------------------------------------------------
	public void uzmiKonekciju(){
		ConnMySQL _dbconn = new ConnMySQL();
		if (_dbconn.getConnection() != null)
			{connection = _dbconn.getConnection();}
		else
			{JOptionPane.showMessageDialog(this, "Konekcija nije otvorena");}
		return;
    }
//--------------------------------------------------------------------------------------
    public boolean proveriMasinu(){ 
		String queryy,ime="",prezime="";
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[0].getText().equals( "" )) {
				queryy = "SELECT * FROM masine WHERE sifra=" + t[0].getText().trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						ime = rs.getString("naziv");
						//prezime = rs.getString("prezime");
						ime = ime.trim();
						prezime = "";

						mkdisplej.setText(ime+" "+prezime);
						provera = true;
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji masina u podacima");
					}
			}else {
				JOptionPane.showMessageDialog(this, "Podatak za masinu nije unet");
            }
		}catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju pacijenta:"+sqlex);
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
		return provera;
}
//------------------------------------------------------------------------------------------------------------------
	public void SpojiPDF() {
			String folderPath = "pdf"; // Relativna putanja do foldera
            String outputFile = folderPath + "/kvarovi.pdf";
		try {
			int totalFiles = brojpdffajla; // Postavi ovde ukupan broj fajlova
            String[] pdfFiles = new String[totalFiles];
            for (int i = 1; i <= totalFiles; i++) {
                pdfFiles[i - 1] = folderPath + "/" + i + ".pdf";
            }

            // Spajanje PDF fajlova
            mergePdfFiles(pdfFiles, outputFile);

			openPdfFile(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
//------------------------------------------------------------------------------------------------------------------
public static void mergePdfFiles(String[] pdfFiles, String outputFile) throws IOException {
        // Kreiranje PdfWriter za izlazni fajl
        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfMerger merger = new PdfMerger(pdfDoc);

        try {
            for (String file : pdfFiles) {
                // Otvaranje svakog PDF fajla za citanje
                PdfDocument tempPdf = new PdfDocument(new PdfReader(file));
                merger.merge(tempPdf, 1, tempPdf.getNumberOfPages());
                tempPdf.close();
            }
        } finally {
            // Zatvaranje izlaznog dokumenta
            pdfDoc.close();
        }
    }//------------------------------------------------------------------------------------------------------------------
 public void openPdfFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                // Koristi default aplikaciju za otvaranje PDF fajlova u Windowsu
                Desktop.getDesktop().open(file);
            } else {
				JOptionPane.showMessageDialog(null, "Fajl nije pronaden: " + filePath);
                //System.out.println("Fajl nije pronaden: " + filePath);
            }
        } catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Gre�ka prilikom otvaranja fajla: " + e.getMessage());
            //System.out.println("Gre�ka prilikom otvaranja fajla: " + e.getMessage());
        }
    }
//------------------------------------------------------------------------------------------------------------------
	private void TraziRecord(){
		if (t[0].getText().trim().length()!= 0)
		{
			proveriMasinu();
			String upit = "SELECT * FROM kvarovi WHERE rbr=" + t[0].getText().trim() +
				" ORDER BY datum DESC";
			popuniTabelu(upit);
		
		}
  }
//------------------------------------------------------------------------------------------------------------------
    public static void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(130);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(130);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("kvarovi") );

	   	qtbl = new mQTMUplate1(connection);
		String sql;
		sql = "SELECT * FROM kvarovi WHERE sifra=99999";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(130);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(130);

	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		ptbl.setBounds(5,100,350,500);
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		String dokt="0";
		int col = 0;
		int row = jtbl.getSelectedRow();
		brkvara = jtbl.getModel().getValueAt(row,0).toString();

		pregled.FindRecord(t[0].getText().trim(),brkvara);
		pregled.t[3].setText(t[0].getText().trim());
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					if (t[0].getText().trim().length()==0)
					{
						t[0].requestFocus();
					}else{
						if (proveriMasinu())
						{
							trazi.requestFocus();
						}
					}
				}
}
//------------------------------------------------------------------------------------------------------------------
	public void idiNaNoviSlog() {
		//JOptionPane.showMessageDialog(this, "Stisni prvo dugme Novi slog");
		novi.requestFocus();
	}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
}
//------------------------------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
	}
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
//--------------------------------------------------------------------------
   public String konvertujDatum(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(6,10);
		datum = datum + "-" + pom.substring(3,5);
		datum = datum + "-" + pom.substring(0,2);
	return datum;
   }

//===========================================================================
class ML extends MouseAdapter{
	public void mousePressed(MouseEvent e) {
		Object source = e.getSource();
		if (source == jtbl){
			prikaziIzTabele();
		}
	}
}//end of class ML

//===========================================================================
 class mQTMUplate1 extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Br.kvara","Datum","Opis kvara","Opis radova"};

//------------------------------------------------------------------------------------------------------------------
   public mQTMUplate1(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 4; }
   public int getRowCount() { return totalrows.size(); }
   public Object getValueAt(int row, int col) {
      return ((Object[])totalrows.elementAt(row))[col];
   }
   public boolean isCellEditable(int row, int col) {
      return false;
   }
   public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
   }
   public void fire() {
      fireTableChanged(null);
   }
//------------------------------------------------------------------------------------------------------------------
   public String konvertujDatumIzPodatakaQTB(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
 //------------------------------------------------------------------------------------------------------------------
   public void query(String _sql) {
		String sql;
		sql = _sql;
		Statement statement = null;
		try {
        statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector();
            while ( rs.next() ) {

               Object[] record = new Object[4];
               record[0] = rs.getString("rbr");
               record[1] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[2] = rs.getString("opiskvara");
               record[3] = rs.getString("opisradova");
               totalrows.addElement( record );

            }
         }
         catch ( SQLException sqlex ) {
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
 }//end of class mQTMUplate1

}// end of class Konto ====================================================================
class MCLuplateKart implements LayoutManager {

  int xInset = 5;
  int yInset = 10;
  int yGap = 8;

//------------------------------------------------------------------------------------------------------------------
  public void addLayoutComponent(String s, Component c) {}

//------------------------------------------------------------------------------------------------------------------
  public void layoutContainer(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      Dimension compSize1 = null;
      for (int i = 0; i < children.length; i = i + 2) {
	  compSize = children[i].getPreferredSize();
	  compSize1 = children[i + 1].getPreferredSize();
	//pozicija za labele
	  children[i].setSize(compSize.width, compSize.height);
	  children[i].setLocation( xInset + insets.left, height + 5);
	//pozicija za txt
	  children[i + 1].setSize(compSize1.width, compSize1.height);
	  children[i + 1].setLocation( xInset + insets.left + 120, height);
	  height += compSize.height + yGap;
      }
  }

//------------------------------------------------------------------------------------------------------------------
  public Dimension minimumLayoutSize(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      int width = 0 + insets.left + insets.right;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      for (int i = 0; i < children.length; i++) {
	  compSize = children[i].getPreferredSize();
	  height += compSize.height + yGap;
	  width = Math.max(width, compSize.width + insets.left + insets.right + xInset*2 + 10);
      }
      height += insets.bottom - 740;
      return new Dimension( width, height);
  }
  
//------------------------------------------------------------------------------------------------------------------
  public Dimension preferredLayoutSize(Container c) {
      return minimumLayoutSize(c);
  	}
//------------------------------------------------------------------------------------------------------------------
  public void removeLayoutComponent(Component c) {} 

}// end of class MCLuplateKart

