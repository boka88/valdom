//Lager liste - robno
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


public class mPregledNalogaR extends JInternalFrame implements InternalFrameListener
			{
	private QTMPreglNalRad qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaciList = new Vector();
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private float dug,pot,saldo;
	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,nazivk;
	private JButton novi,unesi,minus,vrednost;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj,naziv,Ukupno,UkupnoNap;
   	public static JLabel  l[],lnaziv,mkdisplej,lblUkupno,lblUkupnoNap;
	private JList list;
   	private int n_fields,kkk,kontomal,datval,sifraradnika=0;
	private double koeficient=0;
	private String pattern = "##########.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);

//------------------------------------------------------------------------------------------------------------------
    public mPregledNalogaR() {
		super("", true, true, true, true);
		setTitle("Spisak naloga po vozilu");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( null);

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,250,320);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Prikaz ");
		unesi.setMnemonic('R');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});

		minus = new JButton("\u0160tampa");
		minus.setMnemonic('P');
        minus.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        minus.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Stampa();}});
		minus.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
        t = new JFormattedTextField[1]; 
        l = new JLabel[1]; 
		
        lblUkupno = new JLabel("Ukupno rad:");
        Ukupno = new JFormattedTextField();
		Ukupno.setColumns(8);
		Ukupno.setEditable(false);
		Ukupno.setHorizontalAlignment(JTextField.TRAILING);
		Ukupno.setForeground(Color.blue);
		Ukupno.setFont(new Font("Arial",Font.BOLD,12));

        lblUkupnoNap = new JLabel("Zarada radnika:");
        UkupnoNap = new JFormattedTextField();
		UkupnoNap.setColumns(8);
		UkupnoNap.setEditable(false);
		UkupnoNap.setHorizontalAlignment(JTextField.TRAILING);
		UkupnoNap.setForeground(Color.blue);
		UkupnoNap.setFont(new Font("Arial",Font.BOLD,12));


		buttonPanel.add( unesi );
		buttonPanel.add( minus );
		buttonPanel.add( izlaz );
		//buttonPanel.add( lblUkupno );
		//buttonPanel.add( Ukupno );
		//buttonPanel.add( lblUkupnoNap );
		//buttonPanel.add( UkupnoNap );
		nazivk = " ";
		pPre = new String("1");
		kontomal = 1340;

		uzmiKonekciju();
		//sifraradnika = aLogin.UserSifra;

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		//kreirajTabelu();

		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
		main.add(buildNazivPanel(), BorderLayout.NORTH);
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
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
			try {connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
			}
		} 
    }
	protected void Izlaz(){
		try{
				this.setClosed(true);}
		catch (Exception e){
				JOptionPane.showMessageDialog(this, e);}
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( new CLPreglNalRad() );
		p.setBorder( new TitledBorder("Uslovi") );

		int i;
        n_fields = 3; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 
		
		String fmm;

		fmm = "******************************";
        l[0] = new JLabel("Vozilo :");
        t[0] = new JFormattedTextField(createFormatter(fmm,3));
		t[0].setColumns(15);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[0].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziVozila();}});

		fmm = "##/##/####";
        l[1] = new JLabel("Od Datuma :");
        t[1] = new JFormattedTextField(createFormatter(fmm,4));
		t[1].setColumns(8);
		t[1].setText("01/01/" + date.substring(6,10));
		t[1].setSelectionStart(0);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

        l[2] = new JLabel("Do Datuma :");
        t[2] = new JFormattedTextField(createFormatter(fmm,4));
		t[2].setColumns(8);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});


		JLabel lista = new JLabel("Izaberi :");
		String a1 = new String("Samo napla\u0107eni");
		String a2 = new String("Svi nalozi");
		podaciList.addElement( a1 );
		podaciList.addElement( a2 );
		list = new JList(podaciList); 
 		list.setVisibleRowCount(2);
		list.setSelectedIndex(0);
        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        list.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {idiNaDugme();}});


	    for(i=0;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
		//p.add(lista);
		//p.add(list);
		return p;
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNazivPanel() {
		JPanel p1 = new JPanel();
		p1.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		p1.setBorder( new TitledBorder("") );

		JLabel vrd = new JLabel("Reg.broj: ");
		mkdisplej = new JLabel("                       "); 
		mkdisplej.setFont(new Font("Arial",Font.BOLD,14));
		mkdisplej.setForeground(Color.red);

		p1.add(vrd);
		p1.add(mkdisplej);
		return p1;
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
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
			System.exit(-1);
		}
		return formatter;
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
//------------------------------------------------------------------------------------------------------------------
	public void idiNaDugme(){

		unesi.requestFocus();
}
//-------------------------------------------------------------------------
   public void kreirajTabelu(){
		String strsql;
		strsql = "drop table if exists tmprnal";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE  tmprnal (" +                           
           "rbr  int(11) NOT NULL," +                        
           "brsasije  varchar(50) NOT NULL default ' '," +   
		   "marka varchar(50) default NULL," +              
		   "tip varchar(50) default NULL," + 
           "datum  date NOT NULL default '0000-00-00'," +    
           "cenarada  double NOT NULL default '0'," +        
           "cenadelova  double NOT NULL default '0'" +      
           ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		izvrsi(strsql);
   }
//-------------------------------------------------------------------------
   public void prikaziSarad(){
		int t = 12;
   }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
    }
//------------------------------------------------------------------------------------------------------------------
    public void prikaziVozila() {
		int t=10;
		mVozilaPrik sk = new mVozilaPrik(t);
		sk.setModal(true);
		sk.setVisible(true);
    }
//--------------------------------------------------------------------------------------
    public boolean proveriVozilo(){ 
		String queryy;
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( t[0].getText().trim().length() != 0) {
				queryy = "SELECT * FROM vozila WHERE brsasije='" +
		            t[0].getText().trim() + "'";
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						mkdisplej.setText(rs.getString("regbroj"));
						provera = true;
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji vozilo morate ga uneti");
					}
			}     
            else {
				JOptionPane.showMessageDialog(this, "Vozilo nije uneto");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju vozila:" + sqlex);
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
 public void UnesiPressed() {
		int kupac;
		String oddat,dodat,strsql,sql,vozilo="";
	if (t[1].getText().trim().length() != 0 && t[2].getText().trim().length() != 0
		&& t[0].getText().trim().length() != 0)
	{
		oddat = konvertujDatum(t[1].getText().trim());
		dodat = konvertujDatum(t[2].getText().trim());
		vozilo = t[0].getText().trim();
		
		sql = "SELECT * FROM rnal WHERE brsasije='" + vozilo + "' ORDER BY rbr";
		popuniTabelu(sql);
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite datume");
		t[0].requestFocus();
	}
 }
//------------------------------------------------------------------------------------------------------------------
  public void Stampa() {
		int kupac;
		String oddat,dodat,naziv,regbroj,vozilo;

	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length()!= 0  )
	{
		vozilo = t[0].getText().trim();
		oddat = t[1].getText().trim();
		dodat = t[2].getText().trim();
		regbroj = mkdisplej.getText();

		jPrintListaR mm = new jPrintListaR(connection,oddat,dodat,vozilo,regbroj);
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
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
    public void BrisiPressed() {
        this.setVisible(false);
    	}
//------------------------------------------------------------------------------------------------------------------
    public void quitForm() {
		try {	connection.close(); } 
		catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
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
    public void popuniSaldo(String _oddat,String _dodat) {
		Statement statement = null;
		double uku=0;
	  try {
         statement = connection.createStatement();
                String query = "SELECT ROUND(SUM(cenarada),2)" +
					" FROM tmprnal";
		        ResultSet rs = statement.executeQuery( query );
		        rs.next();
				uku = rs.getDouble("ROUND(SUM(cenarada),2)");
				String output = myFormatter.format(uku);
				Ukupno.setText(output);
				uku = uku * koeficient/100;
				UkupnoNap.setText(myFormatter.format(uku));
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u Ukupno" + sqlex);
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
    public double nadjiKoeficient(int _sifraradnika){ 
		String queryy;
		double koef = 0;
		Statement statement = null;
      try {
         statement = connection.createStatement();
				queryy = "SELECT * FROM radnici WHERE sifra=" + _sifraradnika;
				try {
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		koef = rs.getDouble("koeficient");
					}
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji radnik morate ga otvoriti");
					t[0].setText("");
					t[0].requestFocus();}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju koeficienta radnika ");
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
		return koef;
   }
//------------------------------------------------------------------------------------------------------------------
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekora\u010dena du\u017eina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		JOptionPane.showMessageDialog(this, String.valueOf(koji));
   }
//------------------------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(70);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(120);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(120);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );
		ptbl.setBounds(260,5,400,320);
	   	qtbl = new QTMPreglNalRad(connection);
		String sql;
		sql = "SELECT * FROM rnal" +
			" WHERE brsasije='999999' ORDER BY datum";

	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(70);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(120);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(120);

		jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;

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
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		t[5].setText(String.valueOf(podaci.get(kojirec)));
		t[5].requestFocus();
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					if (t[0].getText().trim().length() == 0){
						t[0].requestFocus();
					}
					if (proveriVozilo())
					{
						t[1].setText("01/01/" + date.substring(6,10));
						t[1].setSelectionStart(0);
						t[1].setSelectionEnd(20);
						t[1].requestFocus();
					}else{
						t[0].setSelectionStart(0);
						t[0].requestFocus();
					}
				}
				else if (source == t[1]){
					t[2].setText("31/12/" + date.substring(6,10));
					t[2].setSelectionStart(0);
					t[2].requestFocus();
				}
				else if (source == t[2]){
					unesi.requestFocus();
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
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
	}
//------------------------------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
	}
}
//===========================================================================
 class QTMPreglNalRad extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Br.naloga","Datum","Opis kvara","Opis radova"};

//------------------------------------------------------------------------------------------------------------------
   public QTMPreglNalRad(Connection dbc){
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
		podaci.clear();
		spodaci.clear();
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
 }//end of class QTMPreglNalRad

}// end of class Konto ====================================================================
class CLPreglNalRad implements LayoutManager {

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

}// end of class CLPreglNalRad
/*
		strsql = "CREATE TEMPORARY TABLE  tmprnal (" +                           
           "rbr  int(11) NOT NULL," +                        
           "brsasije  varchar(50) NOT NULL default ' '," +   
           "datum  date NOT NULL default '0000-00-00'," +    
           "cenarada  double NOT NULL default '0'," +        
           "cenadelova  double NOT NULL default '0'," +      
           "garancija  varchar(20) NOT NULL default ' '," +  
           "opiskvara  mediumtext NOT NULL," +               
           "opisradova  mediumtext NOT NULL," +              
           "delovi  mediumtext NOT NULL," +                  
           "sifra  int(5) NOT NULL default '0'," +           
          "UNIQUE KEY  rbr  ( rbr , sifra )" +               
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8"

*/
