//Lager liste - robno
//=======================================
import java.io.*;
import java.sql.*;
import java.sql.ResultSet.*;
import java.awt.*;
import java.awt.event.*;
//import java.awt.swing.event.*;
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
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.StringTokenizer;


public class mStampaDokumenata extends JInternalFrame implements InternalFrameListener
			{
	public int rbr=0, rednibroj = 0,kontomal;
	public String brr,sifm,datum,kolic,cena;
	private float dug,pot,saldo;
	private Vector<String> podaciList = new Vector<String>();
	private MaskFormatter fmt = null;
	public String pPre,nazivPre,date,nazivk,tblpromet="mprom";
	private JButton novi,unesi;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],mmoj,naziv;
   	private JLabel  l[],lnaziv;
	private JList list;
   	private int n_fields,koji,pred,konto,jur,mag,brunm,kojidok;
//------------------------------------------------------------------------------------------------------------------
    public mStampaDokumenata(int _jur,int _mag,int _konto, int _brun,int _kojidok) {
		super("", true, true, true, true);
		setTitle("\u0160tampa prijemnice, izdatnice i otpremnice    ");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		konto = _konto;
		jur = _jur;
		mag = _mag;
		brunm = _brun;
		kojidok = _kojidok;

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( null);

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,350,270);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("\u0160tampa");
		unesi.setMnemonic('P');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Obrada();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Obrada(); }});

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});


		buttonPanel.add( unesi );
		buttonPanel.add( izlaz );
		nazivk = " ";
		pPre = new String("1");
		kontomal = 1340;

		uzmiKonekciju();
		tblpromet = tblpromet + pPre.trim();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		container.add(buildFilterPanel());
		glavni.add(container);
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		

		setSize(370,420);

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
			String strsql = "drop table if exists tmpkarticarobe";
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
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( new CLPrijemPazara1() );
		p.setBorder( new TitledBorder("Uslovi") );

		int i;
        n_fields = 9; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 

		
		String fmm;
		fmm = "**";
        l[0] = new JLabel("Radna jedinica:");
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setColumns(2);
	if (kojidok != 0)
	{
		t[0].setText(String.valueOf(jur));
	}
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "**";
        l[1] = new JLabel("Magacin:");
        t[1] = new JFormattedTextField(createFormatter(fmm,1));
		t[1].setColumns(2);
	if (kojidok != 0)
	{
		t[1].setText(String.valueOf(mag));
	}
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});
	
		fmm = "****";
        l[2] = new JLabel("Konto :");
        t[2] = new JFormattedTextField(createFormatter(fmm,1));
		t[2].setColumns(4);
	if (kojidok != 0)
	{
		t[2].setText(String.valueOf(konto));
	}
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "*****";
		l[3] = new JLabel("Od br. dokumenta :");
        t[3] = new JFormattedTextField(createFormatter(fmm,1));
		t[3].setColumns(5);
	if (kojidok != 0)
	{
		t[3].setText(String.valueOf(brunm));
	}
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "*****";
		l[4] = new JLabel("Do br. dokumenta :");
        t[4] = new JFormattedTextField(createFormatter(fmm,1));
		t[4].setColumns(5);
	if (kojidok != 0)
	{
		t[4].setText(String.valueOf(brunm));
	}
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		JLabel lista = new JLabel("Izaberi  :");
		String a1 = new String("1.Prijemnice");
		String a2 = new String("2.Izdatnice");
		String a3 = new String("3.Otpremnice");
		String a4 = new String("4.Prijem proizvodnja");
		String a5 = new String("5.Priznanice");
		podaciList.addElement( a1 );
		podaciList.addElement( a2 );
		podaciList.addElement( a3 );
		podaciList.addElement( a4 );
		podaciList.addElement( a5 );
		list = new JList(podaciList); 
 		list.setVisibleRowCount(3);

	switch (kojidok)
	{
	case 0:
		list.setSelectedIndex(0);
		break;	
	case 1:
		list.setSelectedIndex(0);
		break;	
	case 2:
		list.setSelectedIndex(3);
		break;	
	case 3:
		list.setSelectedIndex(1);
		break;	
	case 4:
		list.setSelectedIndex(2);
		break;	
	}

		//list.setSelectedIndex(0);
        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        list.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {idiNaDugme();}});
	

	    for(i=0;i<5;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
		p.add(lista);
		p.add(list);
		return p;
    }
//------------------------------------------------------------------------------------------------------------------
	protected MaskFormatter createFormatter(String s, int _koji) {
		MaskFormatter formatter = null;

		try {
			formatter = new MaskFormatter(s);
				switch (_koji)	{
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
		this.setLocation(x - 10,0);
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

  }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa(int _vrdokm) {
		int brdokm = Integer.parseInt(t[3].getText().trim());
		int jurm = Integer.parseInt(t[0].getText().trim());
		int magm = Integer.parseInt(t[1].getText().trim());

		if (_vrdokm == 2)
		{
			_vrdokm = 1;
		}

		if (koji == 4)
		{
			/* STAMPA PRIZNANICA */
			mPrintPriznanica pp = new mPrintPriznanica(connection,jurm,magm,brdokm,_vrdokm);
		}
		else{
			/* STAMPA OSTALIH DOKUMENATA */
			//mPrintPrijemnica pp = new mPrintPrijemnica(connection,jurm,magm,brdokm,_vrdokm,kojidok);
			jPrintPrijemnica pp = new jPrintPrijemnica(connection,jurm,magm,brdokm,_vrdokm,kojidok);
		}
		
	}
//------------------------------------------------------------------------------------------------------------------
   public void Obrada() {
	   String strsql;
		int jurm,magm,kontom,brdokm,brdokm1,koji,vrdokm=0,brunm1=0; //brunm ????????????????????????
	if (!t[0].getText().trim().equals( "" ) && !t[1].getText().trim().equals( "" ) && 
		!t[2].getText().trim().equals( "" ) &&
		!t[3].getText().trim().equals( "" ) && !t[4].getText().trim().equals( "" ))
		{
		pred = Integer.parseInt(pPre);
		jurm = Integer.parseInt(t[0].getText().trim());
		magm = Integer.parseInt(t[1].getText().trim());
		kontom = Integer.parseInt(t[2].getText().trim());
		brdokm = Integer.parseInt(t[3].getText().trim());
		brdokm1 = Integer.parseInt(t[4].getText().trim());
		koji = list.getSelectedIndex();

		if (koji == 0)
		{
			vrdokm = 1;
		}else if (koji == 1)
		{
			vrdokm = 3;
		}else if (koji == 2)
		{
			vrdokm = 4;
		}else if (koji == 3)
		{
			vrdokm = 2;
		}else if (koji == 4)
		{
			vrdokm = 1;
		}

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
		" and "+tblpromet+".brun <= 99999 and "+tblpromet+".jur = " + jurm + " and "+tblpromet+".mag = " + magm + 
		" and "+tblpromet+".vrdok = " + vrdokm + " and "+tblpromet+".brdok >= " + brdokm + 
		" and "+tblpromet+".brdok <= " + brdokm1;
		izvrsi(strsql);

		strsql = "update tmpkarticarobe,sifarnikrobe set tmpkarticarobe.nazivm=sifarnikrobe.nazivm, " +
			"tmpkarticarobe.jmere=sifarnikrobe.jmere " +
			"where tmpkarticarobe.sifm = sifarnikrobe.sifm";
		izvrsi(strsql);
		
		strsql = "update tmpkarticarobe set pre = " + pred;
		izvrsi(strsql);

		if (koji == 4)
		{
			//PRIZNANICE
			//preracun pdv_a koje upisujem u polje rabat
			strsql = "update tmpkarticarobe set " +
				"tmpkarticarobe.rabat = tmpkarticarobe.vredn  * (tmpkarticarobe.por1 / 100.0000) ";
			izvrsi(strsql);
			//preracun vrednosti sa pdv
			strsql = "update tmpkarticarobe set tmpkarticarobe.pvredn = tmpkarticarobe.vredn + tmpkarticarobe.rabat";
			izvrsi(strsql);    
		 }
		
		strsql = "update tmpkarticarobe,saradnici " + 
				 " set tmpkarticarobe.nazivkupca = saradnici.nazivkupca " + 
				 " where tmpkarticarobe.kupacbr = saradnici.kupacbr";
		izvrsi(strsql);

		strsql = "update tmpkarticarobe,mestatroska " + 
				 " set tmpkarticarobe.naztros = mestatroska.naztros " + 
				 " where tmpkarticarobe.mtros = mestatroska.mtros " + 
			     " and tmpkarticarobe.pre = mestatroska.pre";
		izvrsi(strsql);
		//strsql = "DELETE FROM tmpkarticarobe where vredn=0";
		//izvrsi(strsql);

	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
	}

		Stampa(vrdokm);
}
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska: " + sql);
      }
	  finally{
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(this, "Nije uspeo da zatvori statement");}}
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
		// zatvaranje programa-----------------------------
		/*
		try {	connection.close(); } 
		catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
		}
		*/
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
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					t[1].setText(t[0].getText());
					t[1].setSelectionStart(0);
					t[1].requestFocus();}
				else if (source == t[1]){
					t[2].setSelectionStart(0);
					t[2].setSelectionEnd(2);
					t[2].requestFocus();}
				else if (source == t[2]){
					t[3].setSelectionStart(0);
					t[3].setSelectionEnd(2);
					t[3].requestFocus();}
				else if (source == t[3]){
					t[4].setText(t[3].getText());
					t[4].setSelectionStart(0);
					t[4].setSelectionEnd(4);
					t[4].requestFocus();}
				else if (source == t[4]){
					list.requestFocus();}

}
//===========================================================================
class ML extends MouseAdapter{
	public void mousePressed(MouseEvent e) {
		//Object source = e.getSource();
		//if (source == jtbl){
		//prikaziIzTabele();
		//}
	}
}//end of class ML
class CLPrijemPazara1 implements LayoutManager {

  int xInset = 5;
  int yInset = 6;
  int yGap = 8;

  public void addLayoutComponent(String s, Component c) {}

  public void layoutContainer(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      Dimension compSize1 = null;
      for (int i = 0; i < children.length; i = i + 2) {
	  compSize = children[i].getPreferredSize();
	  compSize1 = children[i + 1].getPreferredSize();
	  children[i].setSize(compSize.width, compSize.height);
	  children[i].setLocation( xInset + insets.left, height + 1);
	  children[i + 1].setSize(compSize1.width, compSize1.height);
	  children[i + 1].setLocation( xInset + insets.left + 130, height);
	  height += compSize.height + yGap;
      }
  }

  public Dimension minimumLayoutSize(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      int width = 0 + insets.left + insets.right;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      for (int i = 0; i < children.length; i++) {
	  compSize = children[i].getPreferredSize();
	  height += compSize.height + yGap;
	  width = Math.max(width, compSize.width + insets.left + insets.right + xInset*2 + 120);
      }
      height += insets.bottom - 240;
      return new Dimension( width, height);
  }
  
  public Dimension preferredLayoutSize(Container c) {
      return minimumLayoutSize(c);
  	}
  public void removeLayoutComponent(Component c) {} 

}//end of class CL

}// end of class CLPrijemPazara1

