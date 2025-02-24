//Materijalna kartica
//=======================================
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


public class mKartRobe extends JInternalFrame implements InternalFrameListener
			{
	private Object[][] data;
	private QTMKartProm qtbl;
   	private JTable jtbl;
	private Vector<Object[]> totalrows;
	private JScrollPane jspane;
	private Vector podaciList = new Vector();
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private double dug,pot,saldo;
	private int odjur,dojur,odmag,domag,konto,sifra,koji;
	private String pPre,nazivPre,nazkonta,nazrobe,oddat,dodat;
	private JButton novi,unesi;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],mmoj,naziv;
   	private JLabel  l[],lnaziv;
	private JList list;
	private String tblpromet="mprom";
	String pattern = "#########.00";
	DecimalFormat myFormatter = new DecimalFormat(pattern);

   	int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public mKartRobe(	String _oddat,
						String _dodat,
						int _odjur,
						int _dojur,
						int _odmag,
						int _domag,
						int _konto,
						int _sifra,
						int _koji,
						String _nazkonta,
						String _nazrobe) {
		super("", true, true, true, true);
		setTitle("Kartica materijala ");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);
		oddat = _oddat;
		dodat = _dodat;
		odjur = _odjur;
		dojur = _dojur;
		odmag = _odmag;
		domag = _domag;
		konto = _konto;
		sifra = _sifra;
		koji = _koji;
		nazkonta = _nazkonta;
		nazrobe = _nazrobe;
		definisiZaglavlje();

		JPanel main = new JPanel();
		main.setLayout( null);
		JPanel zag = new JPanel();
		zag.setLayout( null);
		zag.setBounds(5,5,750,160);

		JPanel zag1 = new JPanel();
		zag1.setLayout( new GridLayout(1,3));
		zag1.setBounds(5,50,750,100);
		zag1.add(panel1());
		zag1.add(panel2());
		zag1.add(panel3());
		zag.add(buildNazivPanel());
		zag.add(zag1);

		pPre = new String("1");
		tblpromet = tblpromet + pPre.trim();
		uzmiKonekciju();
		Obrada();

		main.add(zag);
		main.add(buildTable());
		getContentPane().add(main);
		pack();
		setSize(780,480);
		centerDialog();
		popuniSaldo();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));
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
			String strsql = "drop table if exists tmpkarticarobe;";
			izvrsi(strsql);
		} 
    }
	protected void Izlaz(){
		try{
				this.setClosed(true);}
		catch (Exception e){
				JOptionPane.showMessageDialog(this, e);}
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNazivPanel() {
		JPanel p1 = new JPanel();
		p1.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		p1.setBorder( new TitledBorder("") );
		p1.setBounds(5,5,750,40);
		JLabel kk = new JLabel("\u0160ifra: ");
		JLabel kk1 = new JLabel("Naziv robe: ");

        JFormattedTextField kkk = new JFormattedTextField();
		kkk.setColumns(8);
		kkk.setText(String.valueOf(sifra));
		kkk.setEditable(false);
		kkk.setForeground(Color.blue);
		kkk.setFont(new Font("Arial",Font.BOLD,12));
        JFormattedTextField nazkkk = new JFormattedTextField();
		nazkkk.setForeground(Color.red);
		nazkkk.setFont(new Font("Arial",Font.BOLD,12));
		nazkkk.setColumns(30);
		nazkkk.setText(nazrobe);
		nazkkk.setEditable(false);

		unesi = new JButton("\u0160tampa    ");
		unesi.setMnemonic('P');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});


		p1.add(kk);
		p1.add(kkk);
		p1.add(kk1);
		p1.add(nazkkk);
		p1.add(unesi);
		p1.add(izlaz);
		return p1;
    }
//------------------------------------------------------------------------------------------------------------------
    public void definisiZaglavlje() {
		int i;
        n_fields = 7; 
        t = new JFormattedTextField[n_fields]; 

        l = new JLabel[n_fields]; 
		
        l[0] = new JLabel("Koli\u010d. Ulaz :");
        t[0] = new JFormattedTextField();
		t[0].setColumns(8);
		t[0].setEditable(false);
		t[0].setHorizontalAlignment(JTextField.TRAILING); // poravnanje==========
		t[0].setForeground(Color.blue);
		t[0].setFont(new Font("Arial",Font.BOLD,12));

		l[1] = new JLabel("Vredn. Ulaz :");
        t[1] = new JFormattedTextField();
		t[1].setColumns(8);
		t[1].setEditable(false);
		t[1].setHorizontalAlignment(JTextField.TRAILING);
		t[1].setForeground(Color.blue);
		t[1].setFont(new Font("Arial",Font.BOLD,12));

        l[2] = new JLabel("Koli\u010d. Izlaz :");
        t[2] = new JFormattedTextField();
		t[2].setColumns(8);
		t[2].setEditable(false);
		t[2].setHorizontalAlignment(JTextField.TRAILING);
		t[2].setForeground(Color.red);
		t[2].setFont(new Font("Arial",Font.BOLD,12));

        l[3] = new JLabel("Vredn. Izlaz :");
        t[3] = new JFormattedTextField();
		t[3].setColumns(8);
		t[3].setEditable(false);
		t[3].setHorizontalAlignment(JTextField.TRAILING);
		t[3].setForeground(Color.red);
		t[3].setFont(new Font("Arial",Font.BOLD,12));

		l[4] = new JLabel("Prose\u010dna cena   :");
        t[4] = new JFormattedTextField();
		t[4].setColumns(8);
		t[4].setEditable(false);
		t[4].setHorizontalAlignment(JTextField.TRAILING);
		t[4].setForeground(Color.red);
		t[4].setFont(new Font("Arial",Font.BOLD,12));

		l[5] = new JLabel("Stanje koli\u010dine :");
        t[5] = new JFormattedTextField();
		t[5].setColumns(8);
		t[5].setEditable(false);
		t[5].setHorizontalAlignment(JTextField.TRAILING);
		t[5].setForeground(Color.red);
		t[5].setFont(new Font("Arial",Font.BOLD,12));

		l[6] = new JLabel("Ukupna vrednost :");
        t[6] = new JFormattedTextField();
		t[6].setColumns(8);
		t[6].setEditable(false);
		t[6].setHorizontalAlignment(JTextField.TRAILING);
		t[6].setForeground(Color.red);
		t[6].setFont(new Font("Arial",Font.BOLD,12));

    }
//------------------------------------------------------------------------------------------------------------------
	public JPanel panel1(){
		int i;
		JPanel p = new JPanel();
		p.setLayout( new CLKartRobe());
		p.setBorder( new TitledBorder("") );
	    for(i=0;i<2;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
		return p;
	}
//------------------------------------------------------------------------------------------------------------------
	public JPanel panel2(){
		int i;
		JPanel p = new JPanel();
		p.setLayout( new CLKartRobe());
		p.setBorder( new TitledBorder("") );
	    for(i=2;i<4;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
		return p;
	}
//------------------------------------------------------------------------------------------------------------------
	public JPanel panel3(){
		int i;
		JPanel p = new JPanel();
		p.setLayout( new CLKartRobe());
		p.setBorder( new TitledBorder("") );
	    for(i=4;i<7;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
		return p;
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
		this.setLocation(x,0);
    }
//==================================================================================================================
 public void Obrada() {
	String strsql;

	strsql = "drop table if exists tmpkarticarobe;";
	izvrsi(strsql);

	//strsql = "CREATE TABLE tmpkarticarobe (" +
	strsql = "CREATE TEMPORARY TABLE tmpkarticarobe (" +
	  " oznaka  char(1) default ' '," +
	  " opis  varchar(30) default ' '," +
	  " jur  smallint(6) default 0," +
	  " mag  smallint(6) default 0," +
	  " konto  int(11) default 0," +
	  " sifm  int(11) default 0," +
	  " nazivm  varchar(100) NOT NULL default ' '," +
	  " brrac  double NOT NULL default 0," +
	  " datum  datetime default '0000-00-00'," +
	  " ui  tinyint(3) NOT NULL default 0," +
	  " vrdok  smallint(6) NOT NULL default 0," +
	  " brdok  int(11) default 0," +
	  " kolicina  double NOT NULL default 0," +
	  " cena  double NOT NULL default '0'," +
	  " cenad  double default '0'," +
	  " vredn  double default '0'," +
	  " rbr  int(11) default 0," +
	  " brun  double NOT NULL default 0," +
	  " kupacbr  int(11) NOT NULL default 0," +
	  " tarbr  smallint(6) default '0'," +
	  " por1  double default '0'," +
	  " mar  double default '0'," +
	  " taksa  double default '0'," +
	  " preneto  tinyint(3) unsigned default '0'," +
	  " pcena  double default 0," +
	  " pcena1  double default '0'," +
	  " pvredn  double default 0," +
	  " rabat  double default '0'," +
	  " ulazi  tinyint(3) unsigned default '1'," +
	  " mtros  int(11) default '0'," +
	  " prenosbr  int(11) default 0," +
	  " k1  double default '0'," +
	  " k2  double default '0'," +
	  " v1  double default '0'," +
	  " v2  double default '0'," +
	  " prc  double default '0'," +
	  " odjur  smallint(6) default '0'," +
	  " dojur  smallint(6) default '0'," +
	  " odmag  int(11) default '0'," +
	  " domag  smallint(6) default '0'," +
	  " pre  smallint(6) default '0'," +
	  " odbrun  int(11) default '0'," +
	  " dobrun  int(11) default '0'," +
	  " prod  smallint(6) default '0'," +
      " oddat  datetime default '0000-00-00',"+   
      " dodat  datetime default '0000-00-00',"+   
	  " preneto1  tinyint(3) unsigned default '0'," +
	  " kumul  double default '0'," +
	  " kolicul  double default '0'," +
	  " koliciz  double default '0'," +
	  " vredul  double default '0'," +
	  " vrediz  double default '0'," +
	  " kolicme  double default '0'" +
		") ENGINE=MyISAM DEFAULT CHARSET=utf8;"; 
		izvrsi(strsql);


		strsql = "INSERT INTO tmpkarticarobe(konto,sifm,oznaka,jur,mag,ui,vrdok,brdok,datum,kolicina," +
		"cena,vredn,rbr,brun,kupacbr,brrac,tarbr,por1," +
		"mar,taksa,preneto,pcena,pcena1,pvredn,rabat,ulazi,mtros) " +
		"SELECT konto,sifm,oznaka,jur,mag,ui,vrdok,brdok,datum,kolic,cena,vredn," +
		"rbr,brun,kupacbr,brrac,tarbr,por1" +
		",mar,taksa,preneto,pcena,pcena1,pvredn,rabat,ulazi,mtros  " +
		"FROM "+tblpromet+" WHERE "+tblpromet+".pre = " + Integer.parseInt(pPre) + 
		" and "+tblpromet+".jur >= " + odjur + 
		" and "+tblpromet+".jur <= " + dojur + " and "+tblpromet+".mag >= " + odmag + 
		" and "+tblpromet+".mag <= " + domag + " and  "+tblpromet+".konto = " + konto + 
		" AND "+tblpromet+".datum >= '" + konvertujDatum(oddat) + "'" +
		" AND "+tblpromet+".datum <= '" + konvertujDatum(dodat) + "'"+
		" and "+tblpromet+".sifm = " + sifra;
		izvrsi(strsql);

		strsql = "UPDATE tmpkarticarobe,dokumentimaterijalno SET " +
		"tmpkarticarobe.opis = dokumentimaterijalno.nazdok " +
		"WHERE tmpkarticarobe.ui = dokumentimaterijalno.uliz " +
		"AND tmpkarticarobe.vrdok = dokumentimaterijalno.vrdok AND tmpkarticarobe.vrdok!=99";
		izvrsi(strsql);

		//uzimanje stavki iz tabele 'kvarovidelovi' =============================================
		strsql = "drop table if exists tmpkvarovidelovi;";
		izvrsi(strsql);

		//strsql = "CREATE TABLE tmpkvarovidelovi LIKE kvarovidelovi";
		strsql = "CREATE TEMPORARY TABLE tmpkvarovidelovi LIKE kvarovidelovi";
		izvrsi(strsql);
		strsql = "INSERT INTO tmpkvarovidelovi SELECT * FROM kvarovidelovi WHERE sifm=" + sifra;
		izvrsi(strsql);
		//dodavanje polja koja nedostaju za karticu
		strsql = "ALTER TABLE tmpkvarovidelovi ADD oznaka varchar(1) NOT NULL DEFAULT '-'";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD opis varchar(30) NOT NULL DEFAULT ''";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD datum datetime NOT NULL DEFAULT '2000-01-01'";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD jur smallint(6) NOT NULL DEFAULT 1";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD mag smallint(6) NOT NULL DEFAULT 1";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD vrdok smallint(6) NOT NULL DEFAULT 99";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD konto smallint(6) NOT NULL DEFAULT 1020";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD cena double DEFAULT 0";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD vredn double DEFAULT 0";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD ui int(2) NOT NULL DEFAULT 2";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD kupacbr int(11) NOT NULL DEFAULT 0";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD mtros int(11) NOT NULL DEFAULT 0";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD brun double NOT NULL DEFAULT 0";
		izvrsi(strsql);
		strsql = "UPDATE tmpkvarovidelovi,kvarovi SET tmpkvarovidelovi.datum=kvarovi.datum WHERE " +
			" tmpkvarovidelovi.brojkvara=kvarovi.rbr";
		izvrsi(strsql);
		//uzimanje sifre masine kao mesta troska 'mtros'
		strsql = "UPDATE tmpkvarovidelovi,kvarovi SET tmpkvarovidelovi.mtros=kvarovi.sifra WHERE " +
			" tmpkvarovidelovi.brojkvara=kvarovi.rbr";
		izvrsi(strsql);

		//brise slogove koji nisu u rasponu datuma oddat-dodat
		strsql = "DELETE FROM tmpkvarovidelovi WHERE (datum<'" + konvertujDatum(oddat) + 
			"' OR datum>'" + konvertujDatum(dodat) + "')";
		izvrsi(strsql);
		strsql = "UPDATE tmpkvarovidelovi SET opis='Ugradjen deo'";
		izvrsi(strsql);

		strsql = "INSERT INTO tmpkarticarobe(konto,sifm,oznaka,jur,mag,ui,vrdok,brdok,datum,kolicina," +
		"cena,vredn,rbr,brun,mtros) " +
		"SELECT konto,sifm,oznaka,jur,mag,ui,vrdok,brojkvara,datum,kolicina," +
		"cena,vredn,rbr,brojkvara,mtros  " +
		"FROM tmpkvarovidelovi";
		izvrsi(strsql);
		//========================================================================================


 		strsql = "UPDATE tmpkarticarobe SET tmpkarticarobe.kolicme = tmpkarticarobe.kolicina " +
		"WHERE tmpkarticarobe.ui = 1 and cena=0";
		izvrsi(strsql);
 		strsql = "UPDATE tmpkarticarobe SET tmpkarticarobe.kolicul = tmpkarticarobe.kolicina, " +
	    "tmpkarticarobe.vredul = tmpkarticarobe.vredn " +
		"WHERE (tmpkarticarobe.ui = 0 or tmpkarticarobe.ui = 1)  ";
		izvrsi(strsql);
 		strsql = "UPDATE tmpkarticarobe SET tmpkarticarobe.koliciz = tmpkarticarobe.kolicina, " +
	    "tmpkarticarobe.vrediz = tmpkarticarobe.vredn " +
		"WHERE (tmpkarticarobe.ui = 2 or tmpkarticarobe.ui = 3)  ";
		izvrsi(strsql);


		strsql = "UPDATE tmpkarticarobe SET tmpkarticarobe.kolicina = tmpkarticarobe.kolicina * -1," +
		"tmpkarticarobe.vredn = tmpkarticarobe.vredn * -1 WHERE tmpkarticarobe.ui = 2 " +
		"OR tmpkarticarobe.ui = 3";
			izvrsi(strsql);



		strsql = "UPDATE tmpkarticarobe SET opis = 'Interni racun' WHERE vrdok = 10 OR vrdok = 15";
			izvrsi(strsql);
		strsql = "UPDATE tmpkarticarobe SET opis = 'Povrat iz malop.' WHERE vrdok = 16";
			izvrsi(strsql);
		strsql = "UPDATE tmpkarticarobe SET opis = 'Ugradjen deo' WHERE vrdok = 99";
			izvrsi(strsql);

		strsql = "update tmpkarticarobe set oddat = '" + konvertujDatum(oddat) + "'" + ", dodat = ' " + konvertujDatum(dodat) + "'";
		izvrsi(strsql);
		

		if (koji == 0)
		{
			strsql = "SELECT * FROM tmpkarticarobe order by datum , ui";
		}
		else{
			strsql = "SELECT * FROM tmpkarticarobe order by brdok , ui, datum";}
			//popuniTabelu(strsql);
	    Kumul();
  }
//==================================================================================================================
//------------------------------------------------------------------------
    public void Kumul() {

  		//PROCEDURA ZA RACUNANJE UKUPNOG ZBIRA 
	  Statement statement = null;
      try {
         statement = connection.createStatement();
            String query = "SELECT * from tmpkarticarobe order by datum,vrdok,brdok";
			double saldox = 0.00;
			int jur,mag,ui,brun,rbr,vrdok;
			String strsql;

			try {
		         ResultSet rs = statement.executeQuery( query );
				int i=0;
					// petlja listanje slogova---------------
					while ( rs.next() ) {
					 
					  jur  =  rs.getInt("jur");
					  mag  =  rs.getInt("mag");
					  ui  =  rs.getInt("ui");
					  brun  =  rs.getInt("brun");
					  rbr =  rs.getInt("rbr");
					  vrdok  =  rs.getInt("vrdok");
					  saldox  =  saldox + rs.getDouble("kolicina");
					  strsql = "UPDATE tmpkarticarobe SET kumul = " + saldox +
					  " where jur = " + jur +
					  " and mag = " + mag + " and ui = " + ui + " and brun = " + brun + " and rbr = " + rbr + " and vrdok = " + vrdok;
			          izvrsi(strsql);
					}
					//kraj listanja--------------------------
				if (i == 0){
				}		
			}
		      catch ( SQLException sqlex ) {
		      }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u podacima");
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
//-------------------------------------------------------------------------------------

    public void UnesiPressed() {

}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {

		jPrintKart jjj = new jPrintKart(connection,odjur,dojur,
												odmag,domag,
												konto,sifra,
												koji,nazkonta,
												nazrobe,
												t[0].getText().trim(),
												t[1].getText().trim(),
												t[2].getText().trim(),
												t[3].getText().trim(),
												t[4].getText().trim(),
												t[5].getText().trim(),
												t[6].getText().trim());
	}
//------------------------------------------------------------------------------------------------------------------
	public void idiNaDugme(){
		unesi.requestFocus();
	}
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
        this.setVisible(false);
    }
//------------------------------------------------------------------------------------------------------------------
    public void quitForm() {
        this.setVisible(false);
    } 
//------------------------------------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		try{
			connection = aLogin.konekcija;
		}catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee preuzeti konekciju:"+f);
		}
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
	public void obrisiPomocnuTabelu() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
            String query = "DELETE FROM tmpkarticarobe";
		
			int result = statement.executeUpdate( query );
            if ( result != 0 ){
			}     
			else {
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u brisanju pomocne tabele");
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
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska: " + sqlex);
			JOptionPane.showMessageDialog(null, "UPIT: " + sql);
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
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekoracena duzina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		JOptionPane.showMessageDialog(this, String.valueOf(koji));
   }
//------------------------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= _sql;
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(30);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(40);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(40);
}
//------------------------------------------------------------------------------------------------------------------
    public void popuniSaldo() {
		String strsql;
		double u1,u2,u3,u4,u5,u6,u7,u8,proscena;
			strsql = "SELECT @u1:=ROUND(SUM(kolicul),2), @u2:=ROUND(SUM(koliciz),2)," +
				" @u3:=ROUND(SUM(vredul),2),@u4:=ROUND(SUM(vrediz),2), " +
				"@u5:=ROUND(SUM(kolicme),2),@u6:=ROUND(SUM(kolicul)-SUM(koliciz),2)," +
				"@u7:=ROUND(SUM(vredul)-SUM(vrediz),2)," +
				"@u8:=ROUND((SUM(vredul)-SUM(vrediz))/(SUM(kolicul)-SUM(koliciz)-SUM(kolicme)),2) FROM tmpkarticarobe ";

	  try {
         Statement statement = connection.createStatement();
		        ResultSet rs = statement.executeQuery( strsql );
		        rs.next();
					u1 = rs.getDouble(1);
					u2 = rs.getDouble(2);
					u3 = rs.getDouble(3);
					u4 = rs.getDouble(4);
					u5 = rs.getDouble(5);
					u6 = rs.getDouble(6);
					u7 = rs.getDouble(7);
					u8 = rs.getDouble(8);
					if (u8<0.00)
					{u8=0;}
					t[0].setText(rs.getString(1));
					t[2].setText(rs.getString(2));
					t[1].setText(rs.getString(3));
					t[3].setText(rs.getString(4));
					t[5].setText(rs.getString(6));
					t[6].setText(rs.getString(7));
					t[4].setText(String.valueOf(u8));
					statement.close();
       				statement = null;
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u popuniSaldo"+sqlex);
      }
  }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("") );

	   	qtbl = new QTMKartProm(connection);
		String sql;
	   
		if (koji == 0)
		{

		sql = "SELECT oznaka,opis,jur,mag,vrdok,mtros,datum,ROUND(kolicina,2)," +
				"ROUND(cena,2),ROUND(vredn,2),brun,rbr,kupacbr FROM tmpkarticarobe ORDER BY datum , ui";
		
		}else{

			sql = "SELECT oznaka,opis,jur,mag,vrdok,mtros,datum,ROUND(kolicina,2)," +
			"ROUND(cena,2),ROUND(vredn,2),brun,rbr,kupacbr FROM tmpkarticarobe order by brdok , ui, datum";
		}
	   try{
	   		qtbl.query(sql);
			TableSorter sorter = new TableSorter(qtbl); //ADDED THIS
			jtbl = new JTable( sorter );
			sorter.addMouseListenerToHeaderInTable(jtbl); //ADDED THIS
			jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
 	   		//jtbl = new JTable( qtbl );
	   }catch(NullPointerException e){
			JOptionPane.showMessageDialog(this, "Tabela tmpkarticarobe poseduje <<null>> vrednosti");
			Izlaz();
	   }

		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(30);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(40);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(40);
		
		jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		ptbl.setBounds(5,165,750,290);
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
	}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				koji = Integer.parseInt(t[0].getText());
			}
	}
//------------------------------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
	}
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
 class QTMKartProm extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Ozn","Opis","RJ","Mag.","Vrs.dok.",
						"Ma\u0161ina","Datum","Kolicina","Cena",
						"Vrednost","Br.unosa","Br.stavke","Saradnik","Saldo kol."};

//------------------------------------------------------------------------------------------------------------------
   public QTMKartProm(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector<Object[]>();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 14; }
   public int getRowCount() { return totalrows.size(); }
   public Object getValueAt(int row, int col) {
      return totalrows.elementAt(row)[col];
   }
   public boolean isCellEditable(int row, int col) {
      return false;
   }
   public Class<? extends Object> getColumnClass(int c) throws NullPointerException {
	   try{
	       getValueAt(0, c).getClass();
	   }catch(NullPointerException e){
		   throw e;
	   }
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
   //-------------------------------------------------------------------------
   protected String Formatiraj(String _forma) {
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
//------------------------------------------------------------------------------------------------------------------
   public void query(String _sql) {
		String sql;
		sql = _sql;
		podaci.clear();
		spodaci.clear();
		double saldokol=0;
		Statement statement = null;
		try {
        statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector<Object[]>();
            while ( rs.next() ) {

               Object[] record = new Object[14];
				record[0] = rs.getString("oznaka");
				record[1] = rs.getString("opis");
				record[2] = rs.getString("jur");
				record[3] = rs.getString("mag");
				record[4] = rs.getString("vrdok");
				record[5] = rs.getString("mtros");
				record[6] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
				record[7] = rs.getBigDecimal("ROUND(kolicina,2)");
				record[8] = rs.getBigDecimal("ROUND(cena,2)");
				record[9] = rs.getBigDecimal("ROUND(vredn,2)");
				record[10] =rs.getString("brun");
				record[11] = rs.getString("rbr");
				record[12] = rs.getString("kupacbr");
				saldokol = saldokol + Double.parseDouble(record[7].toString());
				record[13] = myFormatter.format(saldokol);
				
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
 }//end of class QTMKartProm

}// end of class Konto ====================================================================
class CLKartRobe implements LayoutManager {

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

}// end of class CLKartRobe
/*
SELECT @u1:=SUM(kolicul), @u2:=SUM(koliciz), @u3:=SUM(vredul),
@u4:=SUM(vrediz), @u12:=SUM(kolicul-koliciz), @u34:=(vredul-vrediz) FROM msta 
WHERE msta.pre = 1 and msta.jur >= 1 
and msta.jur <= 1 and msta.mag >= 1 
and msta.mag <= 1 and  msta.konto = 1200 and msta.sifm = 1
*/