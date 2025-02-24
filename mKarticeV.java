//kartica - materijalno
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


public class mKarticeV extends JInternalFrame implements InternalFrameListener
			{
	private mQTMKart qtbl;
   	private JTable jtbl;
	private Vector<Object[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaciList = new Vector<String>();
	private Vector podaci = new Vector();
	private Vector<Object> spodaci = new Vector<Object>();
	private Vector<Object> kpodaci = new Vector<Object>();
	private Vector<Object> jpodaci = new Vector<Object>();
	private Vector<Object> mpodaci = new Vector<Object>();
	private float dug,pot,saldo;
	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,nazivk,godin="",tblpromet="mprom";
	private JButton novi,unesi;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static FormField t[],naziv;
	public static JLabel mkdisplej,mkdisplej1;
   	private JLabel  l[],lnaziv;
	private JList list;
   	int n_fields,kontomal=1340;
//------------------------------------------------------------------------------------------------------------------
    public mKarticeV() {
		super("", true, true, true, true);
		setTitle("Kartice delova - Vozila ");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( null );

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,280,360);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Prikaz    ");
		unesi.setMnemonic('K');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});

		buttonPanel.add( unesi );
		buttonPanel.add( izlaz );
		nazivk = " ";
		pPre = "1";
		tblpromet = tblpromet + pPre.trim();
		//kontomal = IzborPre.kontomal;
		uzmiKonekciju();

		napraviTMPTabelu();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		godin = date.substring(6,10);
		//date = date.substring(0,6)+godin;
		
		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
		main.add(buildNazivPanel(), BorderLayout.NORTH);
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		setSize(790,480);

		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));
		postaviUslove();
		t[2].requestFocus();

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
			String strsql = "drop table if exists tmpmsta";
			izvrsi(strsql);
			if (connection != null)
			{
				try {connection.close(); } 
				catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
				}
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
		p.setLayout( new mCLKartV() );
		p.setBorder( new TitledBorder("Uslovi") );

		int i;
        n_fields = 8; 
        t = new FormField[n_fields]; 
        l = new JLabel[n_fields]; 

		
		String fmm;
		fmm = "**";
        l[0] = new JLabel("Od RJ :");
        t[0] = new FormField(createFormatter(fmm,1));
		t[0].setColumns(2);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "**";
		l[1] = new JLabel("Do RJ :");
        t[1] = new FormField(createFormatter(fmm,1));
		t[1].setColumns(2);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "**";
        l[2] = new JLabel("Od Mag :");
        t[2] = new FormField(createFormatter(fmm,1));
		t[2].setColumns(2);
		t[2].setText("1");
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

        l[3] = new JLabel("Do Mag :");
        t[3] = new FormField(createFormatter(fmm,1));
		t[3].setColumns(2);
		t[3].setSelectionStart(0);
		t[3].setSelectionEnd(1);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "****";
		l[4] = new JLabel("Konto :");
        t[4] = new FormField(createFormatter(fmm,1));
		t[4].setColumns(4);
		t[4].setText("1010");
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[4].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziKonto();}});

		fmm = "*****************************************";
        l[5] = new JLabel("Vozilo :");
        t[5] = new FormField(createFormatter(fmm,3));
		t[5].setColumns(15);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[5].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziSifru();}});

		fmm = "##/##/####";
        l[6] = new JLabel("Od Datuma :");
        t[6] = new FormField(createFormatter(fmm,4));
		t[6].setColumns(8);
		t[6].setSelectionStart(0);
		t[6].setSelectionEnd(1);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

        l[7] = new JLabel("Do Datuma :");
        t[7] = new FormField(createFormatter(fmm,4));
		t[7].setColumns(8);
		t[7].setSelectionStart(0);
		t[7].setSelectionEnd(1);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});


		JLabel lista = new JLabel("Izaberi na\u010din :");
		String a1 = new String("Sortirano po datumu");
		String a2 = new String("Sortirano po dokumentu");
		podaciList.addElement( a1 );
		podaciList.addElement( a2 );
		list = new JList(podaciList); 
 		list.setVisibleRowCount(2);
		list.setSelectedIndex(0);
        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        list.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {idiNaDugme();}});

	    for(i=2;i<4;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
	    for(i=5;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
		p.add(lista);
		p.add(list);
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
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
			System.exit(-1);
		}
		return formatter;
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNazivPanel() {
		JPanel p1 = new JPanel();
		p1.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		p1.setBorder( new TitledBorder("") );

		Color c = new Color(143,154,185);
		p1.setBackground(c);
		JLabel kon = new JLabel("Vozilo :");
		mkdisplej = new JLabel("                       "); 
		mkdisplej.setFont(new Font("Arial",Font.BOLD,14));
		mkdisplej.setBackground(Color.red);
		mkdisplej.setForeground(Color.white);
		
		p1.add(kon);
		p1.add(mkdisplej);

		return p1;
    }
//------------------------------------------------------------------------------------------------------------------
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height - 100;
		int x = screenSize.width - size.width - 100;
		this.setLocation(10,0);
    }
//------------------------------------------------------------------------------------------------------------------
    public void postaviUslove() {
		t[6].setText("01/01/" + godin);
		t[7].setText("31/12/" + godin);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 12; 
        for(i=2;i<n_fields;i++)
            t[i].setText("");
		t[2].setText("00000");
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
		t[0].setText("00000");
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
		t[0].requestFocus();
    }
 //-----------------------------------------------------------------------------------------------------
    public void nadjiRobu() {
	  Statement statement = null;
      try {
			statement = connection.createStatement();
               String query = "SELECT * FROM vozila WHERE brsasije='" + 
				   t[5].getText().trim() + "' ";

			try {
		        ResultSet rs = statement.executeQuery( query );
		        rs.next();
				mkdisplej.setText(rs.getString("regbroj"));
		      }
		      catch ( SQLException sqlex ) {
		         	System.out.println("Nema tog vozila u sifarniku:"+sqlex);
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju vozila:"+sqlex);
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
    public void UnesiPressed() {
		int odrj,dorj,odmag,domag,konto;
		String nazkonta,nazrobe,sifra="";
		String oddat,dodat;

		nazkonta = mkdisplej.getText();
		//nazrobe = mkdisplej1.getText();
		odrj = 1;
		dorj = 1;
		odmag = Integer.parseInt(t[2].getText().trim());
		domag = Integer.parseInt(t[3].getText().trim());
		//konto = Integer.parseInt(t[4].getText().trim());
		sifra = t[5].getText().trim();
		oddat = t[6].getText().trim();
		dodat = t[7].getText().trim();


		int koji = list.getSelectedIndex();

			JInternalFrame pred = new mKartRobeV(oddat,dodat,odrj,dorj,
												odmag,domag,
												sifra,
												koji,nazkonta);
				AutoFrame.desktop.add(pred, "");
			try { 
				pred.setVisible(true);
				pred.setSelected(true); 
			} catch (java.beans.PropertyVetoException e2) {}

    }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {

		}
//------------------------------------------------------------------------------------------------------------------
	public void napraviTMPTabelu() {

		String strsql = "drop table if exists tmpmsta";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE tmpmsta (" +
           "vozilo  varchar(50) NOT NULL default ' '," +                         
           "regbroj varchar(20) NOT NULL default ' '," + 
		   "vrstavozila varchar(50) default ' '," +
		   "jur  int(11) NOT NULL," +                         
           "mag  int(11) NOT NULL," +                         
           "konto  int(11) NOT NULL," +                        
           "nazivm  varchar(32) NOT NULL default '-'," +
           "kolic  double NOT NULL default '0'," +      
           "kolicul  double NOT NULL default '0'," +      
           "koliciz  double NOT NULL default '0'," +      
           "prc  double NOT NULL default '0'," +               
           "vredn  double NOT NULL default '0'" +              
			") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		izvrsi(strsql);

		strsql = "INSERT INTO tmpmsta(jur,mag,vozilo,konto) SELECT DISTINCT " +
           "jur,mag,vozilo,konto FROM "+tblpromet+" WHERE "+tblpromet+".pre=1" + 
			" AND "+tblpromet+".rbr>0"; 
		izvrsi(strsql);
		strsql = "Update tmpmsta,vozila " +
				"SET tmpmsta.regbroj = vozila.regbroj,tmpmsta.vrstavozila = vozila.vrstavozila"+
				" where tmpmsta.vozilo = vozila.brsasije ";
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
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(50);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(150);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new mQTMKart(connection);
		String sql;

		sql = "SELECT jur,mag,vozilo,regbroj,vrstavozila FROM tmpmsta ORDER BY vozilo";

	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); //ADDED THIS
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); //ADDED THIS
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
 	   	//jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(50);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(150);

		jspane = new JScrollPane(jtbl);
	   	ptbl.add( jspane );

	   	ptbl.setBounds(295,5,460,360);
		return ptbl;
	}
//--------------------------------------------------------------------------------------
    public boolean proveriKonto(){ 
		String queryy;
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[4].getText().equals( "" )) {
				queryy = "SELECT * FROM materijalnakonta WHERE konto=" +
		            Integer.parseInt(t[4].getText().trim());
				try {
					ResultSet rs = statement.executeQuery( queryy );
					rs.next();
		         		mkdisplej.setText(String.valueOf(rs.getString("nazivk")));
						provera = true;
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji konto morate ga otvoriti");
				}
			}     
            else {
				JOptionPane.showMessageDialog(this, "Sifra konta nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju konta");
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
//--------------------------------------------------------------------------------------
    public boolean proveriRobu(){ 
		String queryy;
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[4].getText().equals( "" )) {
				queryy = "SELECT * FROM vozila WHERE brsasije='" +
		            t[5].getText().trim() + "'";
				try {
					ResultSet rs = statement.executeQuery( queryy );
					rs.next();
		         		mkdisplej.setText(String.valueOf(rs.getString("regbroj")));
						provera = true;
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji vozilo morate ga otvoriti");
				}
			}     
            else {
            JOptionPane.showMessageDialog(this, "Sifra vozila nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju vozila:"+sqlex);
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
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		//t[0].setText(jtbl.getValueAt(kojirec,0).toString());
		//t[1].setText(jtbl.getValueAt(kojirec,0).toString());
		t[2].setText(jtbl.getValueAt(kojirec,1).toString());
		t[3].setText(jtbl.getValueAt(kojirec,1).toString());
		t[4].setText(jtbl.getValueAt(kojirec,3).toString());
		t[5].setText(jtbl.getValueAt(kojirec,2).toString());
		t[6].setText("01/01/" + godin);
		t[7].setText("31/12/" + godin);
		
		//proveriKonto();
		proveriRobu();
		unesi.requestFocus();
	}
//-------------------------------------------------------------------------
   public void prikaziKonto(){
		int t = 2;
		//mKontPlan sk = new mKontPlan(connection,t);
		//sk.setModal(true);
		//sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void prikaziSifru(){
		int t = 7;
		mVozilaPrik sk = new mVozilaPrik(t);
		sk.setModal(true);
		sk.setVisible(true);
   }
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(FormField e) {
		FormField source;
		source = e;

				if (source == t[0]){
					if (t[0].getText().trim().length() == 0){
						t[0].setText("1");}
					t[1].setText(t[0].getText());
					t[1].setSelectionStart(0);
					t[1].requestFocus();}
				else if (source == t[1]){
					if (t[1].getText().trim().length() == 0){
						t[1].setText("1");}
					t[2].setSelectionStart(0);
					t[2].requestFocus();}
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0){
						t[2].setText("1");
					}
					t[3].setText(t[2].getText());
					t[3].setSelectionStart(0);
					t[3].requestFocus();}
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0){
						t[3].setText("1");}
					t[5].setSelectionStart(0);
					t[5].requestFocus();
				}
				else if (source == t[4]){
					if (proveriKonto()){	
					t[5].setSelectionStart(0);
					t[5].setSelectionEnd(10);
					t[5].requestFocus();}
				    }
				else if (source == t[5]){
					if (proveriRobu()){
					t[6].setSelectionStart(0);
					t[6].setText("01/01/" + godin);
					t[6].requestFocus();}
				    }
 				else if (source == t[6]){
					t[7].setSelectionStart(0);
					t[7].setText("31/12/" + godin);
					t[7].requestFocus();}
  				else if (source == t[7]){
					list.requestFocus();}

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
 class mQTMKart extends AbstractTableModel {
	Connection dbconn;
	//String[] colheads = {"RJ","Mag","\u0160ifra","Konto","Naziv","Koli\u010dina","Cena","Kolicul","Koliciz"};
	String[] colheads = {"RJ","Mag","Vozilo","Reg.broj","Vrsta"};

//------------------------------------------------------------------------------------------------------------------
   public mQTMKart(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector<Object[]>();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 5; }
   public int getRowCount() { return totalrows.size(); }
   public Object getValueAt(int row, int col) {
      return totalrows.elementAt(row)[col];
   }
   public boolean isCellEditable(int row, int col) {
      return false;
   }
   public Class<? extends Object> getColumnClass(int c) {
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
			totalrows = new Vector<Object[]>();
            while ( rs.next() ) {

               Object[] record = new Object[5];
				record[0] = rs.getString("jur");
				record[1] = rs.getString("mag");
				record[2] = rs.getString("vozilo");
				record[3] = rs.getString("regbroj");
				record[4] = rs.getString("vrstavozila");
				//record[5] = rs.getBigDecimal("ROUND(kolic,2)");
				//record[6] = rs.getBigDecimal("ROUND(prc,2)");
				//record[7] = rs.getBigDecimal("ROUND(kolicul,2)");
				//record[8] = rs.getBigDecimal("ROUND(koliciz,2)");
				spodaci.addElement(record[0]);
				kpodaci.addElement(record[3]);
				jpodaci.addElement(record[1]);
				mpodaci.addElement(record[2]);
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
 }//end of class mQTMKart

}// end of class Konto ====================================================================
class mCLKartV implements LayoutManager {

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

}// end of class mCLKartV

