//Sifarnik materijalnih konta--------------------
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


public class aStampaPrijemnice extends JInternalFrame implements InternalFrameListener{
	public static int UserSifra=0,UserStatus=0,Administrator=0;
	public static String UserName=" ",poslovnagodina="";
	private String koji,date,godina;
	public Connection connection=null;

	private QTMPopis qtbl;
   	private JTable jtbl;
	private JScrollPane jspane;
	private Vector totalrows;

	private MaskFormatter fmt = null;
	private String pPre,nazivPre,datumracuna;
	private JButton novi, unesi,izlaz;
	private ConnMySQL dbconn;
    public static JFormattedTextField t[],mmoj;
	private JPasswordField pass;
   	private JLabel  l[],lblRadim;
   	int n_fields,kojakasa=1;
	private String brracuna="0";

//------------------------------------------------------------------------------------------------------------------
    public aStampaPrijemnice()	{
		super("", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		Container container = getContentPane();
		
		container.setLayout(new BorderLayout());
		setTitle("Pregled prijemnica");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		uzmiKonekciju();

		//proveriTabelu();

		Obrada();

		unesi = new JButton("\u0160tampa");
		unesi.setMnemonic('P');
		unesi.setBounds(10,380,100,30);
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Kreni();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Kreni(); }});

		izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.setBounds(140,380,100,30);
        izlaz.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izlaz.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Izlaz();}});
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});

		lblRadim = new JLabel("");
		buttonPanel.add( lblRadim );

		
		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		container.add(buildFilterPanel(), BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setSize(400,450);

		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(buttonPanel));

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
					String sql = "";
					sql = "drop table if exists tmpprijemnice";
					izvrsi(sql);
		} 
    }
	protected void Izlaz(){
		try{
				this.setClosed(true);}
		catch (Exception e){
				JOptionPane.showMessageDialog(this, e);}
    }
//------------------------------------------------------------------------------------------------------------------
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height/2 - 260;
		int x = screenSize.width/2 - 150;
		this.setLocation(x,y);
    }
//-------------------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		try{
			connection = aLogin.konekcija;
		}catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee preuzeti konekciju:"+f);
		}
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout(null);
		p.setBorder( new TitledBorder("") );

		int i;
        n_fields = 3; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[3]; 
		
		String fmm;
		fmm = "*******";
        l[0] = new JLabel("Magacin:");
		l[0].setBounds(10,250,70,25);
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setBounds(90,250,90,25);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { t[1].requestFocus();}});

        l[1] = new JLabel("Prijem. br.:");
		l[1].setBounds(10,280,70,25);
        t[1] = new JFormattedTextField(createFormatter(fmm,1));
		t[1].setBounds(90,280,90,25);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { t[2].requestFocus();}});

		fmm = "##/##/####";
        l[2] = new JLabel("Datum :");
		l[2].setBounds(10,310,70,25);
		t[2] = new JFormattedTextField(createFormatter(fmm,3));
		t[2].setBounds(90,310,90,25);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e){unesi.requestFocus();}});


		buildTable();

            p.add(l[0]); 
            p.add(t[0]); 
            p.add(l[1]); 
            p.add(t[1]); 
            p.add(l[2]); 
            p.add(t[2]); 
            p.add(unesi); 
            p.add(izlaz); 
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
					formatter.setValidCharacters("0123456789/ ");
					break;
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
}
//------------------------------------------------------------------------------------------------------------------
	public void Obrada() {
		String sql = "";
		sql = "drop table if exists tmpprijemnice";
		izvrsi(sql);
		sql = "CREATE TEMPORARY TABLE tmpprijemnice SELECT DISTINCT mag,brun,datum FROM mprom1 " +
			" WHERE vrdok=1 AND mag<99";
		izvrsi(sql);
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
//-------------------------------------------------------------------------
   public void prikaziSarad(){
		int t = 21;
		fKupciPrik sk = new fKupciPrik(connection,t);
		sk.setModal(true);
		sk.setVisible(true);
   }
//-------------------------------------------------------------------------------------
   public void Kreni() {
	   String strsql,tblpromet="mprom1";
		int pred=1,jurm,magm,kontom,brdokm,brdokm1,koji,vrdokm=0,brunm1=0;
	if (t[0].getText().trim().length()>0 && t[1].getText().trim().length()>0 && 
			t[2].getText().trim().length()>0 )
		{
		jurm = 1;
		magm = Integer.parseInt(t[0].getText().trim());
		brdokm = Integer.parseInt(t[1].getText().trim());
		vrdokm = 1;

		strsql = "drop table if exists tmpkarticarobe";
		izvrsi(strsql);


		strsql = "CREATE temporary TABLE  tmpkarticarobe  ("+          
              " oznaka  varchar(1) default NULL,"+   
               " opis  varchar(30) default NULL,"+    
               " jur  int(11) default NULL,"+         
               " mag  int(11) default NULL,"+         
               " konto  int(11) default NULL,"+       
               " sifm  int(11) default NULL,"+        
               " nazivm  varchar(100) default NULL,"+  
               " jmere  varchar(6) default NULL,"+  
               " brrac  double default NULL,"+        
               " datum  datetime default NULL,"+      
               " ui  tinyint(4) default NULL,"+       
               " vrdok  int(11) default NULL,"+       
               " brdok  int(11) default NULL,"+       
               " kolic  double default NULL,"+        
               " cena  double default '0',"+          
               " cenad  double default '0',"+         
               " vredn  double default '0',"+         
               " rbr  int(11) default NULL,"+         
               " brun  double default NULL,"+         
               " kupacbr  int(11) default NULL,"+     
	           " nazivkupca varchar(45) default '',"+  
               " fak  tinyint(4) default '0',"+       
               " sar  tinyint(4) default '0',"+       
               " tarbr  int(11) default '0',"+        
               " por1  double default '0',"+          
               " por2  double default '0',"+          
               " por3  double default '0',"+          
               " zav  double default '0',"+           
               " mar  double default '0',"+           
               " taksa  double default '0',"+         
               " preneto  tinyint(4) default '0',"+   
               " pcena  double default NULL,"+        
               " pcena1  double default '0',"+        
               " pvredn  double default NULL,"+       
               " rabat  double default '0',"+         
               " ulazi  tinyint(4) default '1',"+     
               " mtros  int(11) default '0',"+        
               " naztros varchar(40) default '',"+  
               " prenosbr  int(11) default NULL,"+    
               " k1  double default '0',"+            
               " k2  double default '0',"+            
               " v1  double default '0',"+            
               " v2  double default '0',"+            
               " prc  double default '0',"+           
               " odjur  int(11) default '0',"+        
               " dojur  int(11) default '0',"+        
               " odmag  int(11) default '0',"+        
               " domag  int(11) default '0',"+        
               " pre  int(11) default '0',"+          
               " odbrun  int(11) default '0',"+       
               " dobrun  int(11) default '0',"+       
               " prod  int(11) default '0',"+         
               " preneto1  tinyint(4) default '0',"+  
               " kolicme  double default '0'"+        
             ") ENGINE=MyISAM DEFAULT CHARSET=utf8";  
		izvrsi(strsql);

		strsql = "INSERT INTO tmpkarticarobe(oznaka,jur,mag,ui,vrdok,brdok,datum,kolic,cena,vredn,rbr,brun," +
		"kupacbr,brrac,fak,sar,tarbr,por1,por2,por3,zav,mar,taksa,preneto,pcena,pcena1,pvredn,rabat,ulazi," +
		"mtros,konto,sifm,prenosbr) SELECT "+tblpromet+".oznaka,"+tblpromet+".jur,"+tblpromet+".mag,"+tblpromet+".ui,"+tblpromet+".vrdok,"+tblpromet+".brdok," +
		""+tblpromet+".datum,"+tblpromet+".kolic,"+tblpromet+".cena,"+tblpromet+".vredn,"+tblpromet+".rbr,"+tblpromet+".brun,"+tblpromet+".kupacbr,"+tblpromet+".brrac," +
		""+tblpromet+".fak,"+tblpromet+".sar,"+tblpromet+".tarbr,"+tblpromet+".por1,"+tblpromet+".por2,"+tblpromet+".por3,"+tblpromet+".zav,"+tblpromet+".mar,"+tblpromet+".taksa," +
		""+tblpromet+".preneto,"+tblpromet+".pcena,"+tblpromet+".pcena1,"+tblpromet+".pvredn,"+tblpromet+".rabat,"+tblpromet+".ulazi,"+tblpromet+".mtros,"+tblpromet+".konto," +
		""+tblpromet+".sifm,"+tblpromet+".prenosbr  FROM "+tblpromet+" where "+tblpromet+".pre = " + pred + " and "+tblpromet+".brun >=0 " + 
		" and "+tblpromet+".brun = " + brdokm + " and "+tblpromet+".jur = " + jurm + " and "+tblpromet+".mag = " + magm + 
		" and "+tblpromet+".vrdok = " + vrdokm;
		izvrsi(strsql);

		strsql = "update tmpkarticarobe,sifarnikrobe set tmpkarticarobe.nazivm=sifarnikrobe.nazivm, " +
			"tmpkarticarobe.jmere=sifarnikrobe.jmere " +
			"where tmpkarticarobe.sifm = sifarnikrobe.sifm";
		izvrsi(strsql);
		
		strsql = "update tmpkarticarobe set pre = " + pred;
		izvrsi(strsql);

		strsql = "update tmpkarticarobe,saradnici " + 
				 " set tmpkarticarobe.nazivkupca = saradnici.nazivkupca " + 
				 " where tmpkarticarobe.kupacbr = saradnici.kupacbr";
		izvrsi(strsql);

		strsql = "update tmpkarticarobe,mestatroska " + 
				 " set tmpkarticarobe.naztros = mestatroska.naztros " + 
				 " where tmpkarticarobe.mtros = mestatroska.mtros " + 
			     " and tmpkarticarobe.pre = mestatroska.pre";
		izvrsi(strsql);
		strsql = "DELETE FROM tmpkarticarobe WHERE sifm=0";
		izvrsi(strsql);

		jPrintPrijemnica pp = new jPrintPrijemnica(connection,jurm,magm,brdokm,vrdokm,1);

	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
	}

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
//--------------------------------------------------------------------------------------
    public boolean proveriSaradnika(){ 
		String queryy;
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[0].getText().equals( "" )) {
				queryy = "SELECT * FROM saradnici WHERE kupacbr=" +
		            Integer.parseInt(t[0].getText().trim());
				try {
					ResultSet rs = statement.executeQuery( queryy );
					rs.next();
		         		t[1].setText(String.valueOf(rs.getString("nazivkupca")));
						provera = true;
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji saradnik morate ga otvoriti");
					provera = false;
				}
			}     
            else {
		        JOptionPane.showMessageDialog(this, "Saradnik nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju saradnika broja");
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
//-------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
   	}
//------------------------------------------------------------------------------------------------------------------
	protected MaskFormatter createFormatter(String s) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(s);
		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		t[0].setText(jtbl.getValueAt(kojirec,0).toString().trim());
		t[1].setText(jtbl.getValueAt(kojirec,1).toString().trim());
		t[2].setText(jtbl.getValueAt(kojirec,2).toString().trim());
	}
//------------------------------------------------------------------------------------------------------------------
    public void buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new QTMPopis(connection);
		String sql;

		sql = "SELECT * FROM tmpprijemnice ORDER BY brun";
		
	   	qtbl.query(sql);
 	   	
		jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());

		TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);

		jspane = new JScrollPane( jtbl );
		jspane.setBounds(5,10,280,220);
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;
			if (source == t[1]){
				if (t[0].getText().trim().length() > 0)
				{
					if (proveriSaradnika())
					{
						t[1].requestFocus();
					}
				}else{
					t[0].requestFocus();
				}
			}else if (source == t[1])
			{
				unesi.requestFocus();
				unesi.setBackground(Color.red);
			}
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		//Object source = e.getSource();
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
 class QTMPopis extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Magacin","Broj prijemnice","Datum"};

//------------------------------------------------------------------------------------------------------------------
   public QTMPopis(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 3; }
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

               Object[] record = new Object[3];
               record[0] = rs.getString("mag");
               record[1] = rs.getString("brun");
               record[2] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
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
 }//end of class mQTMUplate


}// end of class Konto ====================================================================



class mCLLPrijemniceStampa implements LayoutManager {

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

}// end of class mCLLPrijemniceStampa

