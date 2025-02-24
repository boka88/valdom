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


public class mLager extends JInternalFrame implements InternalFrameListener
			{
	private QTMKartl qtbl;
   	private JTable jtbl;
	private Vector<Object[]> totalrows;
	private JScrollPane jspane;
	private Vector podaciList = new Vector();
	private Vector<Object> podaci = new Vector<Object>();
	private Vector spodaci = new Vector();
	private float dug,pot,saldo;
	private String koji,tblpromet="mprom";
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,nazivk,rjprod="RJ",godin="";
	private JButton novi,unesi,minus,vrednost;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public FormField t[],naziv;
   	private JLabel  l[],lnaziv;
	private JList list;
   	private int n_fields,kkk,kontomal;
//------------------------------------------------------------------------------------------------------------------
    public mLager(int _kkk) {
		super("", true, true, true, true);
		setTitle("Lager liste roba i materijala  ");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		kkk = _kkk;
		if (kkk == 2)
		{
			rjprod="Prod";
		}

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( null);

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,250,400);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Prikaz/\u0160tampa ");
		unesi.setMnemonic('P');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});

		minus = new JButton("Minusne stavke ");
		minus.setMnemonic('M');
        minus.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        minus.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {uMinus();}});
		minus.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   uMinus(); }});

		vrednost = new JButton("Nepravilne stavke ");
		vrednost.setMnemonic('N');
        vrednost.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        vrednost.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {uVrednost();}});
		vrednost.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   uVrednost(); }});

		
		
		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});


		buttonPanel.add( unesi );
		//buttonPanel.add( minus );
		//buttonPanel.add( vrednost );
		buttonPanel.add( izlaz );
		nazivk = " ";
		pPre = new String("1");
		tblpromet=tblpromet + pPre.trim();
		kontomal = 1340;

		uzmiKonekciju();

		napraviTMPTabelu();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		godin = date.substring(6,10);

		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		
		setSize(780,480);
		//setSize(890,480);

		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));
		postaviUslove();
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
			String strsql = "drop table if exists tmpmsta2";
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
		p.setLayout( new CLKartL() );
		p.setBorder( new TitledBorder("Uslovi") );

		int i;
        n_fields = 9; 
        t = new FormField[n_fields]; 
        l = new JLabel[n_fields]; 

		
		String fmm;
		fmm = "**";
		l[0] = new JLabel("");
		if (kkk == 2)
		{
			l[0].setText("Od Prodavnice:");
		}else{
			l[0].setText("Od Radne Jedinice:");
		}
        t[0] = new FormField(createFormatter(fmm,1));
		t[0].setColumns(2);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		l[1] = new JLabel("");
		if (kkk == 2)
		{
			l[1].setText("Do Prodavnice:");
		}else{
			l[1].setText("Do Radne Jedinice:");
		}
        t[1] = new FormField(createFormatter(fmm,1));
		t[1].setColumns(2);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "**";
		l[2] = new JLabel("Od magacina :");
        t[2] = new FormField(createFormatter(fmm,1));
		t[2].setColumns(2);
		if (kkk != 1)
		{
			t[2].setText("99");
			t[2].setEditable(false);
		}
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

        l[3] = new JLabel("Do magacina :");
        t[3] = new FormField(createFormatter(fmm,1));
		t[3].setColumns(2);
		if (kkk != 1)
		{
			t[3].setText("99");
			t[3].setEditable(false);
		}
		t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "****";
        l[4] = new JLabel("Konto :");
        t[4] = new FormField(createFormatter(fmm,1));
		t[4].setColumns(4);
		if (kkk != 1)
		{
			t[4].setText(String.valueOf(kontomal));
			t[4].setEditable(false);
		}
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "*****";
		l[5] = new JLabel("Od \u0160ifre :");
        t[5] = new FormField(createFormatter(fmm,1));
		t[5].setColumns(5);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

        l[6] = new JLabel("Do \u0160ifre :");
        t[6] = new FormField(createFormatter(fmm,1));
		t[6].setColumns(5);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		fmm = "##/##/####";
        l[7] = new JLabel("Od Datuma :");
        t[7] = new FormField(createFormatter(fmm,4));
		t[7].setColumns(8);
		t[7].setSelectionStart(0);
		t[7].setSelectionEnd(1);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

        l[8] = new JLabel("Do Datuma :");
        t[8] = new FormField(createFormatter(fmm,4));
		t[8].setColumns(8);
		t[8].setSelectionStart(0);
		t[8].setSelectionEnd(1);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});
           

	    for(i=0;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
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
    public void postaviUslove() {
		t[0].setText("1");
		t[1].setText("1");
		if (kkk == 1)
		{
			t[2].setText("1");
		}else{
			t[2].setText("99");
		}
		if (kkk == 1)
		{
			t[3].setText(t[2].getText());
		}else{
			t[3].setText("99");
		}
				
		if (kkk == 1)
		{
			t[4].setText("");
		}else{
			t[4].setText(String.valueOf(kontomal));
		}
		t[5].setText("00000");
		t[6].setText("99999");
		t[7].setText("01/01/" + godin);
		t[8].setText("31/12/" + godin);
		t[0].setSelectionStart(0);
		t[0].requestFocus();
   }

//------------------------------------------------------------------------------------------------------------------
	public void napraviTMPTabelu() {

		String strsql = "drop table if exists tmpmsta2";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE tmpmsta2 (" +
           "pre  int(11) NOT NULL," +                         
           "sifm  int(11) NOT NULL," +                         
           "jur  int(11) NOT NULL," +                         
           "mag  int(11) NOT NULL," +                         
           "konto  int(11) NOT NULL," +                        
           "nazivm  varchar(100) NOT NULL default '-'," +
           "kolicina  double NOT NULL default '0'," +      
           "kolicul  double NOT NULL default '0'," +      
           "koliciz  double NOT NULL default '0'," +      
           "prc  double NOT NULL default '0'," +               
           "vredul  double NOT NULL default '0'," +               
           "vrediz  double NOT NULL default '0'," +               
           "vrednost  double NOT NULL default '0'" +              
			") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		izvrsi(strsql);

		strsql = "INSERT INTO tmpmsta2(pre,jur,mag,konto,sifm)" +
					" SELECT distinct pre,jur,mag,konto,sifm " + 
					"FROM "+tblpromet+" WHERE "+tblpromet+".pre = " +
					Integer.parseInt(pPre) + " AND "+tblpromet+".rbr>0"; 
		izvrsi(strsql);
		strsql = "Update tmpmsta2,sifarnikrobe " +
				"SET tmpmsta2.nazivm = sifarnikrobe.nazivm "+
				" where tmpmsta2.sifm = sifarnikrobe.sifm ";
		izvrsi(strsql);

		
		/*
		//Ulaz
		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);
		strsql = " create temporary table "+tblpromet+"p " + 
  		"select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" and ("+tblpromet+".ui = 0 or "+tblpromet+".ui = 1 ) group by pre,jur,mag,konto,sifm";
		izvrsi(strsql);

		//Ulaz
		strsql = "Update tmpmsta2,"+tblpromet+"p Set tmpmsta2.kolicul = "+tblpromet+"p.kolic" +
		" , tmpmsta2.vredul = "+tblpromet+"p.vredn where tmpmsta2.pre = "+tblpromet+"p.pre and " +
		"tmpmsta2.konto = "+tblpromet+"p.konto and " +
		"tmpmsta2.jur = "+tblpromet+"p.jur and " +
		"tmpmsta2.mag = "+tblpromet+"p.mag and " +
		"tmpmsta2.sifm = "+tblpromet+"p.sifm";
		izvrsi(strsql);

		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);

		//Izlaz
		strsql = " create temporary table "+tblpromet+"p "  + 
	    "select pre,jur,mag,konto,sifm,sum(kolic) as kolic,sum(vredn) as vredn from "+tblpromet+" " + 
		" where "+tblpromet+".pre = " +  Integer.parseInt(pPre) +
		" and ("+tblpromet+".ui = 2 or "+tblpromet+".ui = 3 ) group by pre,jur,mag,konto,sifm";
		izvrsi(strsql);

		strsql = "Update tmpmsta2,"+tblpromet+"p Set tmpmsta2.koliciz = "+tblpromet+"p.kolic" +
		" , tmpmsta2.vrediz = "+tblpromet+"p.vredn where tmpmsta2.pre = "+tblpromet+"p.pre and " +
		"tmpmsta2.konto = "+tblpromet+"p.konto and " +
		"tmpmsta2.jur = "+tblpromet+"p.jur and " +
		"tmpmsta2.mag = "+tblpromet+"p.mag and " +
		"tmpmsta2.sifm = "+tblpromet+"p.sifm";
		izvrsi(strsql);

		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);
		*/
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
		strsql = "Update tmpmsta2,"+tblpromet+"p Set tmpmsta2.kolicme = "+tblpromet+"p.kolic" +
		" where tmpmsta2.pre = "+tblpromet+"p.pre and " +
		"tmpmsta2.konto = "+tblpromet+"p.konto and " +
		"tmpmsta2.jur = "+tblpromet+"p.jur and " +
		"tmpmsta2.mag = "+tblpromet+"p.mag and " +
		"tmpmsta2.sifm = "+tblpromet+"p.sifm";
		izvrsi(strsql);

		strsql = "drop table if exists "+tblpromet+"p";
		izvrsi(strsql);
		*/
		/*
		strsql = "Update tmpmsta2 Set kolicina = kolicul - koliciz " +
		" , vrednost = vredul - vrediz " +
		"where pre =  " + Integer.parseInt(pPre);
		izvrsi(strsql);
		strsql = "Update tmpmsta2,sifarnikrobe " +
				"SET tmpmsta2.nazivm = sifarnikrobe.nazivm "+
				" where tmpmsta2.sifm = sifarnikrobe.sifm AND sifarnikrobe.pre=" + Integer.parseInt(pPre);
		izvrsi(strsql);
		strsql = "Update tmpmsta2 Set prc = vrednost / kolicina " +
				"where ROUND(kolicina,2)>0.00 and pre=" + Integer.parseInt(pPre);
		izvrsi(strsql);

		strsql = "Update tmpmsta2 Set prc = 0 where ROUND(kolicina,2) <= 0.00 " +
				"and pre=" + Integer.parseInt(pPre);
		izvrsi(strsql);
		*/
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
	private void uMinus(){
			/*
	
				JInternalFrame pred = new mLagerMinus();
				AutoFrame.desktop.add(pred, "");
			try { 
				pred.setVisible(true);
				pred.setSelected(true); 
			} catch (java.beans.PropertyVetoException e2) {}
			*/
	}
//------------------------------------------------------------------------------------------------------------------
	private void uVrednost(){
			/*
				JInternalFrame pred = new mLagerNepravilne();
				AutoFrame.desktop.add(pred, "");
			try { 
				pred.setVisible(true);
				pred.setSelected(true); 
			} catch (java.beans.PropertyVetoException e2) {}
			*/

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

		int odrj,dorj,odmag,domag,konto,odsif,dosif;
		String oddat,dodat;

	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 && t[3].getText().trim().length() != 0 &&
		t[4].getText().trim().length() != 0 && t[5].getText().trim().length() != 0 &&
		t[6].getText().trim().length() != 0 && t[7].getText().trim().length() != 0 &&
		t[8].getText().trim().length() != 0 )
	{

		odrj = Integer.parseInt(t[0].getText().trim());
		dorj = Integer.parseInt(t[1].getText().trim());
		odmag = Integer.parseInt(t[2].getText().trim());
		domag = Integer.parseInt(t[3].getText().trim());
		konto = Integer.parseInt(t[4].getText().trim());
		odsif = Integer.parseInt(t[5].getText().trim());
		dosif = Integer.parseInt(t[6].getText().trim());
		oddat = t[7].getText().trim();
		dodat = t[8].getText().trim();
		int koji = 0;	
		
		if (koji == 0)
		{
			mLager1 pred = new mLager1(odrj,dorj,
												odmag,domag,
												konto,odsif,
												dosif,oddat,
												dodat,nazivk,kkk);

				AutoFrame.desktop.add(pred, "");
			try { 
				pred.setVisible(true);
				pred.setSelected(true); 
			} catch (java.beans.PropertyVetoException e2) {}
		}
		else{

			
			mLager1 pred = new mLager1(odrj,dorj,
												odmag,domag,
												konto,odsif,
												dosif,oddat,
												dodat,nazivk,kkk);
				AutoFrame.desktop.add(pred, "");
			try { 
				pred.setVisible(true);
				pred.setSelected(true); 
			} catch (java.beans.PropertyVetoException e2) {}
		}

 	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
	}

	
	
	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {

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
//------------------------------------------------------------------------------------------------------------------
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekora\u010dena du\u017eina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		JOptionPane.showMessageDialog(this, String.valueOf(koji));
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
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						provera = true;
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji konto morate ga otvoriti");
					}
			}else {
				JOptionPane.showMessageDialog(this, "Sifra konta nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriKonto:" + sqlex);
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
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(55);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(50);
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
		ptbl.setBounds(260,5,500,400);
	   	qtbl = new QTMKartl(connection);
		String sql = " ";
		if (kkk == 1)
		{
		sql = "SELECT jur,mag,sifm,konto,nazivm FROM tmpmsta2 Where " + 
			" mag<99 order by konto , jur , mag , sifm" ;
		}else{
		sql = "SELECT jur,mag,sifm,konto,nazivm FROM tmpmsta2 Where " + 
			" konto=" + kontomal + " AND mag=99 order by konto , jur , mag , sifm" ;
		}

	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); //ADDED THIS
		jtbl = new JTable( sorter );
		jtbl.addMouseListener(new ML());

        sorter.addMouseListenerToHeaderInTable(jtbl); //ADDED THIS
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));

 	   	//jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(55);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(50);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(50);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(150);

		jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;

	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		t[0].setText(jtbl.getValueAt(kojirec,0).toString());
		t[1].setText(jtbl.getValueAt(kojirec,0).toString());
		t[2].setText(jtbl.getValueAt(kojirec,1).toString());
		t[3].setText(jtbl.getValueAt(kojirec,1).toString());
		t[4].setText(jtbl.getValueAt(kojirec,3).toString());
		t[5].setText(jtbl.getValueAt(kojirec,2).toString());
		t[5].requestFocus();
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(FormField e) {
		FormField source;
		source = e;

				if (source == naziv){
					t[0].setText("1");
					t[0].setSelectionStart(0);
					t[0].requestFocus();}
				else if (source == t[0]){
					t[1].setText(t[0].getText());
					t[1].setSelectionStart(0);
					t[1].requestFocus();}
				else if (source == t[1]){
					if (kkk == 1)
					{
						t[2].setText("1");
					}else{
						t[2].setText("99");
					}
					t[2].setSelectionStart(0);
					t[2].setSelectionEnd(2);
					t[2].requestFocus();}
				else if (source == t[2]){
					if (kkk == 1)
					{
						t[3].setText(t[2].getText());
					}else{
						t[3].setText("99");
					}
					t[3].setSelectionStart(0);
					t[3].setSelectionEnd(2);
					t[3].requestFocus();
				}
				else if (source == t[3]){
					if (kkk == 1)
					{
						t[4].setText("");
					}else{
						t[4].setText(String.valueOf(kontomal));
					}
					t[4].setSelectionStart(0);
					t[4].setSelectionEnd(4);
					t[4].requestFocus();}
				else if (source == t[4]){
					if (t[4].getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(this, "Unesi konto");
						t[4].requestFocus();
					}else{
						if (proveriKonto()){
							t[5].setText("00000");
							t[5].setSelectionStart(0);
							t[5].setSelectionEnd(5);
							t[5].requestFocus();
						}
					}
				}
				else if (source == t[5]){
					t[6].setText("99999");
					t[6].setSelectionStart(0);
					t[6].setSelectionEnd(5);
					t[6].requestFocus();}
				else if (source == t[6]){
					t[7].setSelectionStart(0);
					t[7].setText("01/01/" + godin);
					t[7].requestFocus();}
				else if (source == t[7]){
					t[8].setSelectionStart(0);
					t[8].setText("31/12/" + godin);
					t[8].requestFocus();}
				else if (source == t[8]){
					unesi.requestFocus();}

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
 class QTMKartl extends AbstractTableModel {
	Connection dbconn;
	//String[] colheads = {rjprod,"Mag","\u0160ifra","Konto","NazivRobe","Koli\u010dina","Cena","Vredn"};
	String[] colheads = {rjprod,"Mag","\u0160ifra","Konto","NazivRobe"};

//------------------------------------------------------------------------------------------------------------------
   public QTMKartl(Connection dbc){
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
				record[2] = rs.getString("sifm");
				record[3] = rs.getString("konto");
				record[4] = rs.getString("nazivm");

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
 }//end of class QTMKartl

}// end of class Konto ====================================================================
class CLKartL implements LayoutManager {

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

}// end of class CLKartL
/*
		String strsql = "drop table if exists tmpmsta2";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE tmpmsta2 (" +
           "pre  int(11) NOT NULL," +                         
           "sifm  int(11) NOT NULL," +                         
           "jur  int(11) NOT NULL," +                         
           "mag  int(11) NOT NULL," +                         
           "konto  int(11) NOT NULL," +                        
           "nazivm  varchar(30) NOT NULL default '-'," +
           "kolic  double NOT NULL default '0'," +      
           "prc  double NOT NULL default '0'," +               
           "vredn  double NOT NULL default '0'" +              
			") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		izvrsi(strsql);

		strsql = "INSERT INTO tmpmsta2(pre,jur,mag,sifm,konto,nazivm) SELECT DISTINCT " +
           "pre,jur,mag,sifm,konto,nazivm FROM "+tblpromet+" WHERE "+tblpromet+".pre=" + 
			Integer.parseInt(pPre) + " AND "+tblpromet+".rbr>0";
		izvrsi(strsql);
		
		//ukupan promet  *******************************************************
		strsql = "drop table if exists anbb";
		izvrsi(strsql);
		strsql = "create temporary table anbb select pre,jur,mag,sifm,konto," +
			"sum(kolicul-koliciz) as kolic,(sum(kolicul*cena)-sum(koliciz*cena))/(sum(kolicul)-sum(koliciz)) " +
			" as prc FROM "+tblpromet+" GROUP BY pre,jur,mag,sifm,konto";
		izvrsi(strsql);
		//korekcija cene i kolicine ako je neka vrednost null
		strsql = "UPDATE anbb SET prc = 0 where prc is null";
		izvrsi(strsql);
		strsql = "UPDATE anbb SET kolic = 0 where kolic is null";
		izvrsi(strsql);

		strsql = "UPDATE tmpmsta2,anbb SET tmpmsta2.kolic = anbb.kolic , tmpmsta2.prc = anbb.prc, "+
			" tmpmsta2.vredn=(anbb.kolic*anbb.prc) WHERE " +
			"tmpmsta2.pre = anbb.pre AND " + 
			"tmpmsta2.jur = anbb.jur AND " + 
			"tmpmsta2.mag = anbb.mag AND " + 
			"tmpmsta2.sifm = anbb.sifm AND " + 
			"tmpmsta2.konto = anbb.konto";
		izvrsi(strsql);
		strsql = "drop table if exists anbb";
		izvrsi(strsql);
		//----------------------------------------------------------------------
		strsql = "UPDATE tmpmsta2 SET prc = 0,vredn=0 WHERE prc<0";
		izvrsi(strsql);


*/
