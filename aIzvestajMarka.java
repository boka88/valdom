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


public class aIzvestajMarka extends JInternalFrame implements InternalFrameListener
			{
	private QTMKartUplata qtbl;
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
    public static JFormattedTextField t[],mmoj,naziv,Ukupno;
   	public static JLabel  l[],lnaziv,mkdisplej,lblUkupno;
	private JList list;
   	private int n_fields,kkk,kontomal;
//------------------------------------------------------------------------------------------------------------------
    public aIzvestajMarka() {
		super("", true, true, true, true);
		setTitle("Izve\u0161taj po marki vozila ");
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
		container.setBounds(5,5,300,300);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Prikaz ");
		unesi.setMnemonic('P');
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
		
        lblUkupno = new JLabel("Ukupno:");
        Ukupno = new JFormattedTextField();
		Ukupno.setColumns(10);
		Ukupno.setEditable(false);
		Ukupno.setHorizontalAlignment(JTextField.TRAILING);
		Ukupno.setForeground(Color.blue);
		Ukupno.setFont(new Font("Arial",Font.BOLD,12));


		buttonPanel.add( unesi );
		buttonPanel.add( minus );
		buttonPanel.add( izlaz );
		buttonPanel.add( lblUkupno );
		buttonPanel.add( Ukupno );
		nazivk = " ";
		pPre = "1";
		//kontomal = IzborPre.kontomal;

		uzmiKonekciju();
		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		kreirajTabelu();

		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
//		main.add(buildNazivPanel(), BorderLayout.NORTH);
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		
		setSize(710,420);

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
			String strsql = "drop table if exists tmpmarka";
			izvrsi(strsql); 
			
			
			
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
		p.setLayout( new CLIzvestajMarka() );
		p.setBorder( new TitledBorder("Uslovi") );

		int i;
        n_fields = 3; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 
		
		String fmm;
		fmm = "******************************";
		l[0] = new JLabel("");
		l[0].setText("Marka vozila:");
        t[0] = new JFormattedTextField(createFormatter(fmm,3));
		t[0].setColumns(13);
		t[0].setSelectionStart(0);
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
		t[1].setSelectionStart(0);
		t[1].setSelectionEnd(1);
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
		String a1 = new String("Sve uplate");
		String a2 = new String("Gotovina");
		String a3 = new String("\u017diralne");
		podaciList.addElement( a1 );
		podaciList.addElement( a2 );
		podaciList.addElement( a3 );
		list = new JList(podaciList); 
 		list.setVisibleRowCount(2);
		list.setSelectedIndex(0);
        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        list.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {idiNaDugme();}});


	    for(i=0;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
		//p.add(lista);
		//p.add(list);
		return p;
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNazivPanel() {
		JPanel p1 = new JPanel();
		p1.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		p1.setBorder( new TitledBorder("") );

		JLabel vrd = new JLabel("Korisnik: ");
		mkdisplej = new JLabel("                       "); 
		mkdisplej.setFont(new Font("Arial",Font.BOLD,14));
		mkdisplej.setForeground(Color.red);

		p1.add(vrd);
		p1.add(mkdisplej);
		return p1;
    }
//-------------------------------------------------------------------------
   public void kreirajTabelu(){
		String strsql;
		strsql = "drop table if exists tmpmarka";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE tmpmarka (" +
		"datum date NOT NULL default '0000-00-00'," + 
		"brsasije varchar(50) NOT NULL default ' '," + 
		"marka varchar(50) NOT NULL default ' '," +   
		"tip varchar(50) NOT NULL default ' '" +
		") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		izvrsi(strsql);
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
    public void prikaziVozila() {
		int t=2;
		mVozilaPrik mvp = new mVozilaPrik(t);
		mvp.setModal(true);
		mvp.setVisible(true);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 9; 
        for(i=0;i<n_fields;i++)
            t[i].setText("");
		t[2].setText("00000");
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
		t[0].setText("00000");
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
		t[0].requestFocus();
    }
//------------------------------------------------------------------------------------------------------------------
 public void UnesiPressed() {
		String oddat,dodat,marka;

	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 )
	{

		oddat = t[1].getText().trim();
		dodat = t[2].getText().trim();
		marka = t[0].getText().trim();
		String sql;
		sql = "INSERT INTO tmpmarka(brsasije,datum) SELECT brsasije,datum FROM rnal WHERE " +
			" datum>='" + konvertujDatum(oddat) + 
			"' AND datum<='" + konvertujDatum(dodat) + "'";
		izvrsi(sql);
		sql = "UPDATE tmpmarka,vozila SET tmpmarka.marka=vozila.marka,tmpmarka.tip=vozila.tip" + 
			  " WHERE tmpmarka.brsasije=vozila.brsasije";
		izvrsi(sql);
		sql= "DELETE FROM tmpmarka WHERE marka NOT LIKE '%" + marka + 
		"%'";
		izvrsi(sql);

		sql = "SELECT * FROM tmpmarka";
		popuniTabelu(sql);
		popuniSaldo();
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
	}
 }
//------------------------------------------------------------------------------------------------------------------
  public void Stampa() {
		int vrsta;
		String oddat,dodat,markavozila;
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length()!= 0  )
	{
		oddat = t[1].getText().trim();
		dodat = t[2].getText().trim();
		markavozila = t[0].getText().trim();

		jPrintMarka mm = new jPrintMarka(connection,oddat,dodat,markavozila);
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite podatke");
		t[0].requestFocus();
	}
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
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(100);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(150);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );
		ptbl.setBounds(310,5,350,300);
	   	qtbl = new QTMKartUplata(connection);
		String sql;
		sql = "SELECT * FROM tmpmarka Where tip='999999999999999'";

	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(100);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(150);

		jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;

	}
 //------------------------------------------------------------------------------------------------------------------
    public void popuniSaldo() {
		Statement statement = null;
		int uku=0;
	  try {
         statement = connection.createStatement();
                String query = "SELECT COUNT(*) FROM tmpmarka";
		        ResultSet rs = statement.executeQuery( query );
		        if(rs.next()){
					uku = rs.getInt(1);
				}
				String output = String.valueOf(uku);
				Ukupno.setText(output);
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
					if (t[0].getText().trim().length() == 0)
					{
						t[0].requestFocus();
					}else{
						t[1].setText("01/01/" + date.substring(6,10));
						t[1].setSelectionStart(0);
						t[1].requestFocus();
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
 class QTMKartUplata extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Datum","Tip vozila"};

//------------------------------------------------------------------------------------------------------------------
   public QTMKartUplata(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 2; }
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

               Object[] record = new Object[2];
               record[0] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[1] = rs.getString("tip");

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
 }//end of class QTMKartUplata

}// end of class Konto ====================================================================
class CLIzvestajMarka implements LayoutManager {

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

}// end of class CLIzvestajMarka

