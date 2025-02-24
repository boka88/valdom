//program za unos sifarnika polaznika
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


public class izvZamenaUlja extends JInternalFrame implements InternalFrameListener
			{
	private izvQTM3 qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private boolean izmena = false;
	private String koji,date;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],mmoj;
   	private JLabel  l[];
   	int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public izvZamenaUlja() {
		super("", true, true, true, true);
        setTitle("Dospe\u0107e zamene ulja vozila");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		uzmiKonekciju();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		napraviPomocnuTabelu();

		buildTable();
		main.add(buildPanel(), BorderLayout.CENTER);
		getContentPane().add(main);
		pack();
		setSize(680,480);
		centerDialog();
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
			String strsql = "drop table if exists tmpzam";
			izvrsi(strsql);
			
			try {connection.close(); 
			} 
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
			}
		} 
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder("Unos") );

		// setBounds(X,Y,sirina,visina)
		int prviLX,prviTX,drugiLX,drugiTX,treciLX,treciTX,visina,razmakY;
		int sirinaL,txt,aa,btnY,btnX,btnsirina,btnrazmak;
		aa = 25;					//rastojanje od gornje ivice panela
		sirinaL = 80;			//sirina labele
		visina = 20;			//visina komponente
		prviLX = 10;			//X-pozicija za prvi red labela
		prviTX = 10 + sirinaL;	//X-pozicija za prvi red text-box
		drugiLX = 210;
		drugiTX = drugiLX + sirinaL + 20;
		treciLX = 450;
		treciTX = treciLX + sirinaL + 20;
		razmakY = 3;			// razmak izmedju komponenti po visini
		txt = 10;				// vrednost jedinicnog razmaka u txt - polju
		btnY = 6*(visina + razmakY) + 50;
		btnX = 50;
		btnsirina = 80;
		btnrazmak = 5;

		int i;
        n_fields = 10; 
        t = new JFormattedTextField[3]; 
        l = new JLabel[10]; 

		
		String fmm;
		fmm = "##/##/####";
        l[0] = new JLabel("Do datuma. :");
			l[0].setBounds(prviLX,aa,sirinaL,visina);
        t[0] = new JFormattedTextField(createFormatter(fmm,4));
		t[0].setColumns(10);
			t[0].setBounds(prviTX,aa,8*txt,visina);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		l[1] = new JLabel("Do datuma :");
			l[1].setBounds(prviLX,aa + visina + razmakY,sirinaL,visina);
		l[1].setFont(new Font("Arial",Font.PLAIN,9));
        t[1] = new JFormattedTextField(createFormatter(fmm,4));
		t[1].setFont(new Font("Arial",Font.PLAIN,9));
		t[1].setColumns(20);
			t[1].setBounds(prviTX,aa + visina + razmakY,10*txt,visina);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "********************";
        l[2] = new JLabel("Mesto :");
 			l[2].setBounds(prviLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[2].setFont(new Font("Arial",Font.PLAIN,9));
		t[2] = new JFormattedTextField(createFormatter(fmm,3));
		t[2].setFont(new Font("Arial",Font.PLAIN,9));
		t[2].setColumns(20);
			t[2].setBounds(prviTX,aa + 2*(visina + razmakY),15*txt,visina);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
		unesi.setBounds(prviTX,aa + 3*(visina + razmakY),btnsirina,20);
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Obrada();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Obrada(); }});

		JButton stampa = new JButton("\u0160tampa");
		stampa.setMnemonic('P');
		stampa.setBounds(prviTX + btnsirina + 10,aa + 3*(visina + razmakY),btnsirina,20);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

	    /*
		for(i=0;i<3;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
		*/
        p.add(l[0]); 
        p.add(t[0]); 
		p.add(unesi);
		p.add(stampa);
		p.add(jspane);
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
					formatter.setValidCharacters("0123456789/- ");
					break;
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
	}
//------------------------------------------------------------------------------------------------------------------
	public void napraviPomocnuTabelu() {
		String strsql = "drop table if exists tmpzam";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE  tmpzam  (" +               
           "brsasije varchar(50) default ' '," +        
           "regbroj varchar(20) default ' '," +        
           "kilometraza double NOT NULL default '0'," +  
           "novostanje double NOT NULL default '0'," +  
           "marka varchar(50) default ' '," +                    
           "tip varchar(50) default ' '," +   
           "kmzamulja int(11) default '0'," +   
           "brojkm int(7) default '0'," +   
           "UNIQUE KEY  brsasije  ( brsasije )" +   
           ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		izvrsi(strsql);
		strsql = "INSERT INTO tmpzam (brsasije,regbroj,kilometraza,marka,tip,kmzamulja)" +    
			" SELECT brsasije,regbroj,kilometraza,marka,tip,kmzamulja FROM vozila";
		izvrsi(strsql);
		
		strsql = "UPDATE tmpzam,gorivo SET novostanje=(SELECT MAX(stanjekm) FROM gorivo" +
			 " WHERE tmpzam.brsasije=gorivo.brsasije)";
		izvrsi(strsql);
		//listajPodatke();
		strsql = "UPDATE tmpzam SET brojkm=kmzamulja-(novostanje-kilometraza)";
		izvrsi(strsql);
		strsql = "UPDATE tmpzam SET brojkm=0 WHERE novostanje=0";
		izvrsi(strsql);

  }
//------------------------------------------------------------------------------------------------------------------
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = 5;
		int x = 5;
		this.setLocation(x,y);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 10; 
        for(i=0;i<n_fields;i++)
            t[i].setText("");
		izmena = false;
		t[0].requestFocus();
    }
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		//AddRecord();
    	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		jPrintZam kkl = new jPrintZam(connection);
	}
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
        this.setVisible(false);
    }
//------------------------------------------------------------------------------------------------------------------
    public void Obrada() {
       // this.setVisible(false);
    }
//------------------------------------------------------------------------------------------------------------------
    public void quitForm() {
		// zatvaranje programa-----------------------------
		try {	connection.close(); } 
		catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
		}
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
//-------------------------------------------------------------------------------
	private String NoviDatum(String _stariDatum,int _brojdana){
		String noviDatum="";
		Calendar kalendar = Calendar.getInstance();
		ParsePosition pp = new ParsePosition(0);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date ddatum = df.parse(_stariDatum.trim(),pp);
		kalendar.setTime(ddatum);
		
		kalendar.roll(Calendar.DAY_OF_YEAR,_brojdana);

		java.util.Date datumvalute = new java.util.Date();
		datumvalute = kalendar.getTime();
		noviDatum = df.format(datumvalute);

		return noviDatum;
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
//--------------------------------------------------------------------------
   private String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
   public boolean proveriDatum(String _datum){
		boolean ispravan = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date testDate = null;
		try
		{
			testDate = sdf.parse(_datum);
		}
		//provera da li je odgovarajuci format datuma
		catch (ParseException e)
		{
			JOptionPane.showMessageDialog(this, "Neispravan format datuma");
			return false;
		}
		//provera da li je datum ispravan
		if (!sdf.format(testDate).equals(_datum))
		{
			JOptionPane.showMessageDialog(this, "Neispravan datum");
			return false;
		}

		return true;
   }
//------------------------------------------------------------------------------------------------------------------
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva");
		String upit = "SELECT * FROM pravna WHERE naziv LIKE '" + String.valueOf(result) + "%'";
		popuniTabelu(upit);
		jtbl.requestFocus();
  }
//------------------------------------------------------------------------------------------------------------------
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekora\u010dena du\u017eina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		//JOptionPane.showMessageDialog(this, "Mogu se unositi samo brojevi");
		JOptionPane.showMessageDialog(this, String.valueOf(koji));
   }
//------------------------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(120);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(120);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(120);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(150);

	}
//------------------------------------------------------------------------------------------------------------------
    public void buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new izvQTM3(connection);
		String sql;
		sql = "SELECT * FROM tmpzam ORDER BY brojkm";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl ){
			public Component prepareRenderer(TableCellRenderer renderer, int index_row, int index_col){
				Component comp = super.prepareRenderer(renderer, index_row, index_col);
				//odd col index, selected or not selected
				if(index_col == 4 ){
					comp.setBackground(Color.yellow);
				}
				else{
					comp.setBackground(Color.white);
				}
				return comp;
			}
		};
		jtbl.setRowSelectionAllowed(false);
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(120);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(120);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(120);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(150);
		//tcol4.setBackground(Color.yellow);

	   	jspane = new JScrollPane( jtbl );
		jspane.setBounds(10,130,600,300);
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = String.valueOf(podaci.get(kojirec));
		//FindRecord();
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					if (proveriDatum(String.valueOf(t[0].getText()).trim()) == true){
						unesi.requestFocus();
					}
					else{
						t[0].setSelectionStart(0);
						t[0].setSelectionEnd(15);
						t[0].requestFocus();}
				}
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				koji = String.valueOf(t[0].getText());
				//FindRecord();
			}
	}
//----------------------------------------------------------------------------
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
 class izvQTM3 extends AbstractTableModel {
	Connection dbconn;
	//public Vector totalrows;
	String[] colheads = {"Reg.broj","Broj \u0161asije","Stanje km.","Novo stanje km","Preostalo km"};
//------------------------------------------------------------------------------------------------------------------
   public izvQTM3(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 5; }
   public int getRowCount() { return totalrows.size(); }
	/*
   public Object getValueAt(int row, int col) {
      return ((Object[])totalrows.elementAt(row))[col];
   }
   public Class getColumnClass(int c) {
	return getValueAt(0 , c).getClass();
   }
   public boolean isCellEditable(int row, int col) {
      return false;
   }
   public void fire() {
      fireTableChanged(null);
   }
   */
   public Object getValueAt(int row, int col) {
      return ((String[])totalrows.elementAt(row))[col];
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
		//JOptionPane.showMessageDialog(this, datum);
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
   public void query(String _sql) {
		String sql;
		sql = _sql;
		podaci.clear();
		spodaci.clear();
		try {
        Statement statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector();
            while ( rs.next() ) {

               String[] record = new String[5];
               record[0] = rs.getString("regbroj");
               record[1] = rs.getString("brsasije");
               record[2] = rs.getString("kilometraza");
               record[3] = rs.getString("novostanje");
               record[4] = rs.getString("brojkm");

			   podaci.addElement(record[0]);
               totalrows.addElement( record );
            }
            statement.close();
         	statement = null;
         }
         catch ( SQLException sqlex ) {
         }
    }
 }//end of class izvQTM3
}// end of class pravna ====================================================================
/*
UPDATE pompravna,pravna SET 
pompravna.naziv = pravna.naziv,
pompravna.ulica = pravna.ulica,
pompravna.broj = pravna.broj,
pompravna.mesto = pravna.mesto,
pompravna.telefon = pravna.telefon,
pompravna.mobilni = pravna.mobilni 
FROM pravna WHERE pompravna.JMBG = pravna.matbr
*/