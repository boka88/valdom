//Materijalna kartica - Lager
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


public class mLager1 extends JInternalFrame implements InternalFrameListener
			{
	private QTMKartPromxL qtbl;
   	private JTable jtbl;
	private Vector<Object[]> totalrows;
	private JScrollPane jspane;
	private Vector podaciList = new Vector();
	private Vector<Object> podaci = new Vector<Object>();
	private Vector spodaci = new Vector();
	private double dug,pot,saldo;
	private String koji,oddat,dodat,nazivk;
	private	int odrj,dorj,odmag,domag,konto,odsif,dosif;
	private String pPre,nazivPre,tblpromet="mprom",rjprod="R.J.";
	private JButton novi,unesi;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],naziv;
   	private JLabel  l[],lnaziv;
	private JList list;
   	private int n_fields,kkk;
//------------------------------------------------------------------------------------------------------------------
	public mLager1(		int _odrj,
						int _dorj,
						int _odmag,
						int _domag,
						int _konto,
						int _odsif,
						int _dosif,
						String _oddat,
						String _dodat,
						String _nazivk,int _kkk) {
		super("", true, true, true, true);
		setTitle("Lager lista delova i opreme");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);
		odrj = _odrj;
		dorj = _dorj;
		odmag = _odmag;
		domag = _domag;
		konto = _konto;
		odsif = _odsif;
		dosif = _dosif;
		oddat = _oddat;
		dodat = _dodat;
		nazivk = _nazivk;
		kkk = _kkk;

		if (kkk == 2)
		{
			rjprod="Prod";
		}

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		pPre = new String("1");
		tblpromet=tblpromet + pPre.trim();
		uzmiKonekciju();
		Obrada();

		main.add(buildFilterPanel(), BorderLayout.SOUTH);
		main.add(buildTable(), BorderLayout.CENTER);
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
			String strsql = "drop table if exists tmpstanjerobe";
			izvrsi(strsql);
		} 
    }
//------------------------------------------------------------------------------------------------------------------
	protected void Izlaz(){
		try{
			String strsql = "drop table if exists tmpstanjerobe";
			izvrsi(strsql);
			this.setClosed(true);}
		catch (Exception e){
				JOptionPane.showMessageDialog(this, e);}
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		p.setBorder( new TitledBorder("") );

		unesi = new JButton("\u0160tampa    ");
		unesi.setMnemonic('P');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Stampa();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});

		int i;
        n_fields = 2; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 

		
        l[0] = new JLabel("Ukupna Koli\u010dina :");
        t[0] = new JFormattedTextField();
		t[0].setColumns(10);
		t[0].setEditable(false);
		t[0].setHorizontalAlignment(JTextField.TRAILING);
		t[0].setForeground(Color.blue);
		t[0].setFont(new Font("Arial",Font.BOLD,12));

		l[1] = new JLabel("Ukupna Vrednost :");
        t[1] = new JFormattedTextField();
		t[1].setColumns(10);
		t[1].setEditable(false);
		t[1].setHorizontalAlignment(JTextField.TRAILING);
		t[1].setForeground(Color.blue);
		t[1].setFont(new Font("Arial",Font.BOLD,12));

		p.add(unesi);
		p.add(izlaz);
	    for(i=0;i<n_fields;i++){ 
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

		strsql = "drop table if exists tmpstanjerobe";
		izvrsi(strsql);

			strsql = "CREATE TEMPORARY TABLE tmpstanjerobe (" +
			//strsql = "CREATE TABLE tmpstanjerobe (" +
			  " sifm  int(11) NOT NULL default '0'," +
			  " nazivm  varchar(100) default ' '," +
			  " jmere  varchar(6) default ' '," +
			  " kolicina  double default '0'," +
			  " cena  double default '0'," +
			  " vrednost  double default '0'," +
			  " kolicul  double default '0'," +
			  " koliciz  double default '0'," +
			  " vredul  double default '0'," +
			  " vrediz  double default '0'," +
			  " mtros  int(11) default 0," +
			  " jur  smallint(6) default 0," +
			  " mag  smallint(6) default '0'," +
			  " pre  smallint(6) default '0'," +
			  " kolpop  double default '0'," +
			  " kolicme  double default '0'," +
			  " odjur  smallint(6) default '0'," +
			  " dojur  smallint(6) default '0'," +
			  " konto  int(11) default 0," +
			  " odmag  int(11) default '0'," +
			  " domag  smallint(6) default '0'," +
			  " nazivmag  varchar(50) default ' '," +
			  " nazjur  varchar(50) default ' '," +
			  " nazivk  varchar(50) default ' '," +
			  " oddat  datetime default '0000-00-00'," +
			  " dodat  datetime default '0000-00-00'," +
			  " prod  smallint(6) default '0'," +
			  " rezervacija  double default '0'," +
			  " PRIMARY KEY (`pre`,`jur`,`mag`,`konto`,`sifm`)" +
				") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
			izvrsi(strsql);

			
			uzmiPodatke();
			

			strsql = "Update tmpstanjerobe,sifarnikrobe " +
				"SET tmpstanjerobe.nazivm = sifarnikrobe.nazivm "+
				" where tmpstanjerobe.sifm = sifarnikrobe.sifm";
			izvrsi(strsql);
			strsql = "Update tmpstanjerobe Set cena = vrednost / kolicina " +
				"where ROUND(kolicina,2)>0.00 and pre =  " + Integer.parseInt(pPre);
			izvrsi(strsql);

			strsql = "Update tmpstanjerobe Set cena = 0 where ROUND(kolicina,2) <= 0.00 " +
				"and pre =  " + Integer.parseInt(pPre);
			izvrsi(strsql);
	}
//==================================================================================================================
    public void uzmiPodatke() {
		//Procedura kad se lager list trazi iz prometa a ne iz stanja
		String strsql;

		strsql = "INSERT INTO tmpstanjerobe(pre,jur,mag,konto,sifm)" +
					" SELECT distinct pre,jur,mag,konto,sifm " + 
					"FROM "+tblpromet+" WHERE "+tblpromet+".pre = " +
					Integer.parseInt(pPre) + 
					" AND "+tblpromet+".jur >= " + odrj + 
					" AND "+tblpromet+".jur <= " + dorj + 
					" AND "+tblpromet+".mag >= " + odmag +
					" AND "+tblpromet+".mag <= " + domag +
					" AND "+tblpromet+".konto = " + konto + 
				    " AND "+tblpromet+".sifm <= " + dosif + 
					" AND "+tblpromet+".sifm >= " + odsif +
					" AND "+tblpromet+".datum >= '" + konvertujDatum(oddat) + "'" +
					" AND "+tblpromet+".datum <= '" + konvertujDatum(dodat) + "'";
		izvrsi(strsql);

		//Ulaz -----------------------------------------------------------------------------
		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);
		strsql = " create temporary table "+tblpromet+"p " + 
  		"select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" AND "+tblpromet+".jur >= " + odrj + 
		" AND "+tblpromet+".jur <= " + dorj + 
		" AND "+tblpromet+".mag >= " + odmag +
		" AND "+tblpromet+".mag <= " + domag +
		" AND "+tblpromet+".konto = " + konto +
		" AND "+tblpromet+".datum >= '" + konvertujDatum(oddat) + "'" +
		" AND "+tblpromet+".datum <= '" + konvertujDatum(dodat) + "'"+
		" AND "+tblpromet+".sifm <= " + dosif + " AND "+tblpromet+".sifm >= " + odsif +
		" and ("+tblpromet+".ui = 0 or "+tblpromet+".ui = 1 ) group by pre,jur,mag,konto,sifm";
		izvrsi(strsql);

		//Ulaz
		strsql = "Update tmpstanjerobe,"+tblpromet+"p Set tmpstanjerobe.kolicul = "+tblpromet+"p.kolic" +
		" , tmpstanjerobe.vredul = "+tblpromet+"p.vredn where tmpstanjerobe.pre = "+tblpromet+"p.pre and " +
		"tmpstanjerobe.konto = "+tblpromet+"p.konto and " +
		"tmpstanjerobe.jur = "+tblpromet+"p.jur and " +
		"tmpstanjerobe.mag = "+tblpromet+"p.mag and " +
		"tmpstanjerobe.sifm = "+tblpromet+"p.sifm";
		izvrsi(strsql);

		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);
		//--------------------------------------------------------------------------------------

		//Izlaz
		strsql = " create temporary table "+tblpromet+"p "  + 
	    "select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" AND "+tblpromet+".jur >= " + odrj + 
		" AND "+tblpromet+".jur <= " + dorj + 
		" AND "+tblpromet+".mag >= " + odmag +
		" AND "+tblpromet+".mag <= " + domag +
		" AND "+tblpromet+".konto = " + konto + 
		" AND "+tblpromet+".sifm <= " + dosif + " AND "+tblpromet+".sifm >= " + odsif +
		" AND "+tblpromet+".datum >= '" + konvertujDatum(oddat) + "'" +
		" AND "+tblpromet+".datum <= '" + konvertujDatum(dodat) + "'"+
		" and ("+tblpromet+".ui = 2 or "+tblpromet+".ui = 3 ) group by pre,jur,mag,konto,sifm";
		izvrsi(strsql);

		strsql = "Update tmpstanjerobe,"+tblpromet+"p Set tmpstanjerobe.koliciz = "+tblpromet+"p.kolic" +
		" , tmpstanjerobe.vrediz = "+tblpromet+"p.vredn where tmpstanjerobe.pre = "+tblpromet+"p.pre and " +
		"tmpstanjerobe.konto = "+tblpromet+"p.konto and " +
		"tmpstanjerobe.jur = "+tblpromet+"p.jur and " +
		"tmpstanjerobe.mag = "+tblpromet+"p.mag and " +
		"tmpstanjerobe.sifm = "+tblpromet+"p.sifm";
		izvrsi(strsql);

		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);

		//Medjupromet
		/*
		strsql = " create temporary table "+tblpromet+"p "  + 
 	    "select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" AND "+tblpromet+".jur >= " + odrj + 
		" AND "+tblpromet+".jur <= " + dorj + 
		" AND "+tblpromet+".mag >= " + odmag +
		" AND "+tblpromet+".mag <= " + domag +
		" AND "+tblpromet+".konto = " + konto + 
		" AND "+tblpromet+".sifm <= " + dosif + " AND "+tblpromet+".sifm >= " + odsif +
		" AND "+tblpromet+".datum >= '" + konvertujDatum(oddat) + "'" +
		" AND "+tblpromet+".datum <= '" + konvertujDatum(dodat) + "'"+
		" and "+tblpromet+".ui = 1 and "+tblpromet+".oznaka='+' group by pre,konto,sifm";
		izvrsi(strsql);

		strsql = "Update tmpstanjerobe,"+tblpromet+"p Set tmpstanjerobe.kolicme = "+tblpromet+"p.kolic" +
		" where tmpstanjerobe.pre = "+tblpromet+"p.pre and " +
		"tmpstanjerobe.konto = "+tblpromet+"p.konto and " +
		"tmpstanjerobe.jur = "+tblpromet+"p.jur and " +
		"tmpstanjerobe.mag = "+tblpromet+"p.mag and " +
		"tmpstanjerobe.sifm = "+tblpromet+"p.sifm";
		izvrsi(strsql);

		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);
		*/



		DodajUgradnju();



		strsql = "Update tmpstanjerobe SET kolicina = kolicul - koliciz - kolicme " +
		" , vrednost = vredul - vrediz " +
		"where pre =  " + Integer.parseInt(pPre);
		izvrsi(strsql);
	}
//------------------------------------------------------------------------------------------------------------------
    public void DodajUgradnju() {
		//uzimanje stavki iz tabele 'kvarovidelovi' =============================================
		String strsql = "drop table if exists tmpkvarovidelovi;";
		izvrsi(strsql);

		//strsql = "CREATE TABLE tmpkvarovidelovi LIKE kvarovidelovi";
		strsql = "CREATE TEMPORARY TABLE tmpkvarovidelovi LIKE kvarovidelovi";
		izvrsi(strsql);
		strsql = "INSERT INTO tmpkvarovidelovi SELECT * FROM kvarovidelovi";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD datum datetime NOT NULL DEFAULT '2000-01-01'";
		izvrsi(strsql);
		strsql = "UPDATE tmpkvarovidelovi,kvarovi SET tmpkvarovidelovi.datum=kvarovi.datum WHERE " +
			" tmpkvarovidelovi.brojkvara=kvarovi.rbr";
		izvrsi(strsql);
		//brise slogove koji nisu u rasponu datuma oddat-dodat
		strsql = "DELETE FROM tmpkvarovidelovi WHERE (datum<'" + konvertujDatum(oddat) + 
			"' OR datum>'" + konvertujDatum(dodat) + "')";
		izvrsi(strsql);
		
		//dodavanje polja koja nedostaju za karticu
		strsql = "ALTER TABLE tmpkvarovidelovi ADD pre smallint(6) NOT NULL DEFAULT 1";
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
		strsql = "ALTER TABLE tmpkvarovidelovi ADD koliciz double NOT NULL DEFAULT 0";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD vrediz double NOT NULL DEFAULT 0";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD kolicul double NOT NULL DEFAULT 0";
		izvrsi(strsql);
		strsql = "ALTER TABLE tmpkvarovidelovi ADD vredul double NOT NULL DEFAULT 0";
		izvrsi(strsql);
		//uzimanje sifre masine kao mesta troska 'mtros'
		strsql = "UPDATE tmpkvarovidelovi,kvarovi SET tmpkvarovidelovi.mtros=kvarovi.sifra WHERE " +
			" tmpkvarovidelovi.brojkvara=kvarovi.rbr";
		izvrsi(strsql);


		//dodavanje sifara u 'tmpstanjerobe' iz tabele 'kvarovidelovi' ako nema tih sifara u 'mprom1'
		strsql = "INSERT IGNORE INTO tmpstanjerobe(pre,jur,mag,konto,sifm)" +
					" SELECT distinct pre,jur,mag,konto,sifm " + 
					"FROM tmpkvarovidelovi WHERE " +
					" tmpkvarovidelovi.jur >= " + odrj + 
					" AND tmpkvarovidelovi.jur <= " + dorj + 
					" AND tmpkvarovidelovi.mag >= " + odmag +
					" AND tmpkvarovidelovi.mag <= " + domag +
					" AND tmpkvarovidelovi.konto = " + konto + 
				    " AND tmpkvarovidelovi.sifm <= " + dosif + 
					" AND tmpkvarovidelovi.sifm >= " + odsif;
		izvrsi(strsql);
		
		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);

		//dodavanje kolicina iz tabele 'tmpkvarovidelovi'
		strsql = " create temporary table "+tblpromet+"p "  + 
	    "select jur,mag,konto,sifm,sum(kolicina) as kolic,sum(vredn) as vredn from tmpkvarovidelovi " + 
		" WHERE tmpkvarovidelovi.jur >= " + odrj + 
		" AND tmpkvarovidelovi.jur <= " + dorj + 
		" AND tmpkvarovidelovi.mag >= " + odmag +
		" AND tmpkvarovidelovi.mag <= " + domag +
		" AND tmpkvarovidelovi.konto = " + konto + 
		" AND tmpkvarovidelovi.sifm <= " + dosif + " AND tmpkvarovidelovi.sifm >= " + odsif +
		"  group by jur,mag,konto,sifm";
		izvrsi(strsql);

		strsql = "Update tmpstanjerobe,"+tblpromet+"p SET tmpstanjerobe.koliciz = "+tblpromet+"p.kolic" +
		" , tmpstanjerobe.vrediz = "+tblpromet+"p.vredn WHERE " +
		"tmpstanjerobe.konto = "+tblpromet+"p.konto and " +
		"tmpstanjerobe.jur = "+tblpromet+"p.jur and " +
		"tmpstanjerobe.mag = "+tblpromet+"p.mag and " +
		"tmpstanjerobe.sifm = "+tblpromet+"p.sifm";
		izvrsi(strsql);

		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);
		
		
		//========================================================================================
	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		jPrintLager jjj = new jPrintLager(connection,odrj,dorj,odmag,domag,konto,
											odsif,dosif,oddat,dodat,nazivk,kkk);
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
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekoracena duzina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		JOptionPane.showMessageDialog(this, String.valueOf(koji));
   }
//------------------------------------------------------------------------------------------------------------------
    public void popuniTabelu() {
		String sqll = "SELECT jur,mag,konto,sifm,nazivm,ROUND(kolicina)," +
			"ROUND(cena),ROUND(vrednost),ROUND(kolicul),ROUND(koliciz)," +
			"ROUND(vredul),ROUND(vrediz) FROM tmpstanjerobe";
	   	qtbl.query(sqll);
		qtbl.fire();
		TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(60);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(60);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(120);
	}
//------------------------------------------------------------------------------------------------------------------
    public void popuniSaldo() {
		Statement statement = null;
	  try {
         statement = connection.createStatement();
                String query = "SELECT ROUND(SUM(kolicina),2),ROUND(SUM(vrednost),2)" +
					" FROM tmpstanjerobe GROUP BY pre";
		        ResultSet rs = statement.executeQuery( query );
		        rs.next();
				t[0].setText(rs.getString("ROUND(SUM(kolicina),2)"));
				t[1].setText(rs.getString("ROUND(SUM(vrednost),2)"));
      }
      catch ( SQLException sqlex ) {
		//JOptionPane.showMessageDialog(this, "Greska u SUM" + sqlex);
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
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new QTMKartPromxL(connection);
		String sql;

		sql = "SELECT jur,mag,konto,sifm,nazivm,ROUND(kolicina,2)," +
			"ROUND(cena,2),ROUND(vrednost,2),ROUND(kolicul,2),ROUND(koliciz,2)," +
			"ROUND(vredul,2),ROUND(vrediz,2) FROM tmpstanjerobe";
	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); //ADDED THIS
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); //ADDED THIS
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));

 	   	//jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
 	   	
		
		//jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.setFont(new Font("Tahoma",Font.PLAIN,10));

		TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(60);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(60);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(120);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = String.valueOf(Integer.parseInt(String.valueOf(podaci.get(kojirec))));
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
}

//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				koji = String.valueOf(Integer.parseInt(t[0].getText()));
			}
	}
//------------------------------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		/*	if (source == t[9]){
				t[10].setText(pPre);
			}
			else if (source == t[11]){
					t[12].setText("1");
			}
		*/
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
 class QTMKartPromxL extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {rjprod,"Mag.","Konto","\u0160ifra","Naziv",
						"Koli\u010dina","Cena","Vrednost","Kol-Ulaz","Kol-Izlaz","Vr.-Ulaz","Vr-Izlaz"};

//------------------------------------------------------------------------------------------------------------------
   public QTMKartPromxL(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector<Object[]>();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 12; }
   public int getRowCount() { return totalrows.size(); }
   public Object getValueAt(int row, int col) {
      return totalrows.elementAt(row)[col];
   }
   public Class<? extends Object> getColumnClass(int c) {
	return getValueAt(0 , c).getClass();
   }
   public boolean isCellEditable(int row, int col) {
      return false;
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
		Statement statement = null;
		try {
        statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector<Object[]>();
            while ( rs.next() ) {

			   Object[] record = new Object[12];
				record[0] = rs.getString("jur");
				record[1] = rs.getString("mag");
				record[2] = rs.getString("konto");
				record[3] = rs.getString("sifm");
				record[4] = rs.getString("nazivm");
				record[5] = rs.getBigDecimal("ROUND(kolicina,2)");
				record[6] = rs.getBigDecimal("ROUND(cena,2)");
				record[7] = rs.getBigDecimal("ROUND(vrednost,2)");
				record[8] = rs.getBigDecimal("ROUND(kolicul,2)");
				record[9] = rs.getBigDecimal("ROUND(koliciz,2)");
				record[10] = rs.getBigDecimal("ROUND(vredul,2)");
				record[11] = rs.getBigDecimal("ROUND(vrediz,2)");

				podaci.addElement(record[0]);
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
 }//end of class QTMKartPromxL

}// end of class Konto ====================================================================
class CLKartProxLL implements LayoutManager {

  int xInset = 5;
  int yInset = 10;
  int yGap = 6;

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

}// end of class CLKartProxLL
