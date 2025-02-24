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


public class aOstalaOprema extends JInternalFrame implements InternalFrameListener
			{
	public static mQTMUplate1 qtbl;
   	public static JTable jtbl;
	private JButton novi;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector redbr = new Vector();
	private boolean izmena = false,novislog=false;
	private int koji,koji2,prvisledeci=1;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,koji1,koji3,koji4,koji5;
	private JButton unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj;
   	private JLabel  l[];
	public static JLabel mkdisplej;
   	private int n_fields,vrdok=1,zadnjibroj=0;
	private mPanelVozila vozila;
	public static String parcela="0",brsasije="";
	public static int sifragroblja=0,rbr=0;
	private String rednibroj="1";
//------------------------------------------------------------------------------------------------------------------
    public aOstalaOprema() {
		super("", true, true, true, true);
		setTitle("Unos dodatne opreme     F9 - \u0160ifarnici");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( null );

		JPanel glavni = new JPanel();
		glavni.setLayout( null );

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,280,350);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		buttonPanel.setBounds(5,285,400,30);

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});
		buttonPanel.add( unesi );


		novi = new JButton("Novi");
		novi.setMnemonic('N');
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});
		buttonPanel.add( novi );

		izmeni = new JButton("Izmeni");
		izmeni.setMnemonic('U');
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});
		buttonPanel.add( izmeni );

		JButton brisi = new JButton("Briši");
		brisi.setMnemonic('B');
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		buttonPanel.add( brisi );

		JButton trazi = new JButton("Traži");
		trazi.setMnemonic('T');
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		buttonPanel.add( trazi );


		JButton izlaz = new JButton("\u0160tampa");
		izlaz.setMnemonic('P');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		buttonPanel.add( izlaz );

		uzmiKonekciju();
		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		vozila = new mPanelVozila();
		vozila.setBounds(410,5,350,580);

		//container.add(buildFilterPanel());
		main.add(buildFilterPanel());
		main.add(buildTable());
		main.add(buttonPanel);
		main.add(vozila);

		getContentPane().add(main);
		pack();
		setSize(780,630);
		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));
		//t[0].requestFocus();
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
				JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
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
		p.setLayout( new MCLuplate1() );
		p.setBorder( new TitledBorder("Unos") );
		int i;
        n_fields = 9; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 

		String fmm;

		fmm = "********************";
        l[0] = new JLabel("Broj \u0161asije :");
        t[0] = new JFormattedTextField(createFormatter(fmm,3));
		t[0].setColumns(10);
		t[0].setEditable(false);
		t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[0].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziSifarnik();}});

		fmm = "*********************";
        l[1] = new JLabel("Registarski br.:");
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
		t[1].setColumns(10);
		t[1].setEditable(false);
		t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});


		l[2] = new JLabel("Vrsta vozila:");
        t[2] = new JFormattedTextField(createFormatter(fmm,3));
		t[2].setColumns(10);
		t[2].setEditable(false);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		l[3] = new JLabel("Marka :");
        t[3] = new JFormattedTextField(createFormatter(fmm,3));
		t[3].setColumns(15);
		t[3].setEditable(false);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "******************************";
		l[4] = new JLabel("Naziv :");
        t[4] = new JFormattedTextField(createFormatter(fmm,3));
		t[4].setColumns(15);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "**********";
		l[5] = new JLabel("Komada :");
        t[5] = new JFormattedTextField(createFormatter(fmm,3));
		t[5].setColumns(15);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		
		fmm = "******************************";
		l[6] = new JLabel("Broj:");
        t[6] = new JFormattedTextField(createFormatter(fmm,3));
		t[6].setColumns(15);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		l[7] = new JLabel("Opis :");
        t[7] = new JFormattedTextField(createFormatter(fmm,3));
		t[7].setColumns(18);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

		fmm = "*************";
		l[8] = new JLabel("Voza\u010d :");
        t[8] = new JFormattedTextField(createFormatter(fmm,3));
		t[8].setColumns(18);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[8].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziVozila();}});

		for(i=0;i<9;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
		p.setBounds(5,5,350,250);
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
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(0,0);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 9; 
        for(i=4;i<n_fields;i++)
            t[i].setText("");		
		izmena = false;
		t[4].requestFocus();
	}
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    }
//-------------------------------------------------------------------------
   public void prikaziSifarnik(){
		int t = 1;
		//gSifPrik sk = new gSifPrik(connection,t);
		//sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void prikaziVozila(){
		int t = 11;
		mVozilaPrik sk = new mVozilaPrik(t);
		sk.setVisible(true);
   }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0){
			jPrintOprema jpo = new jPrintOprema(connection,brsasije);
		}else{
			JOptionPane.showMessageDialog(this, "Unesi prvo podatke");		
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
//---------------------------------------------------------------------------
   public int novaStavka(){ 
	   int sledeci=1;
		Statement statement = null;
      try {
         statement = connection.createStatement();
               		String query = "SELECT MAX(rbr) FROM oprema WHERE brsasije='" +
						t[0].getText().trim() + "'";
	         ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					sledeci = rs.getInt(1);
					sledeci = sledeci + 1;
				}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju prve sledece stavke:"+sqlex);
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
		return sledeci;
}
//------------------------------------------------------------------------------------------------------------------
	public void AddRecord() {
	  Statement statement = null;
		prvisledeci = novaStavka();
      try {
         statement = connection.createStatement();
		if (t[0].getText().trim().length() != 0 && t[4].getText().trim().length() != 0 && 
			t[5].getText().trim().length() != 0 ){
			
				String query = "INSERT INTO oprema (brsasije,rbr,naziv,komada,broj,opis,jmbg)" +
					" VALUES('" +
				t[0].getText().trim() + "'," + 
				prvisledeci + ",'" +
				t[4].getText().trim() + "'," +
				Double.parseDouble(t[5].getText().trim()) + ",'" +
				t[6].getText().trim() + "','" + 
				t[7].getText().trim() + "','" +
				t[8].getText().trim() + "')";
			
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
			//JOptionPane.showMessageDialog(this, "Slog je unet");

				String upit = "SELECT * FROM oprema WHERE brsasije='" + brsasije +
					"' ORDER BY rbr";
				popuniTabelu(upit);
				NoviPressed();
			}     
			else {
				JOptionPane.showMessageDialog(this, "Slog nije unet");
				NoviPressed();
            }
         }
         else {JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");
			t[0].requestFocus();}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u unosu");
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
	public void UpdateRecord() {
		String query = "";
		Statement statement = null;
      try {
         statement = connection.createStatement();

		if (t[0].getText().trim().length() != 0 && t[4].getText().trim().length() != 0 && 
			t[5].getText().trim().length() != 0 ){
               query = "UPDATE oprema SET " +
				"naziv='" + t[4].getText().trim()+ "'," +
				"broj='" + t[6].getText().trim() + "'," + 
				"opis='" + t[7].getText().trim() + "'," + 
				"jmbg='" + t[8].getText().trim() + "'," + 
				"komada=" + Double.parseDouble(t[5].getText().trim()) + 
				" WHERE brsasije='" + t[0].getText().trim() + "'" +
				" AND rbr=" + rednibroj;

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je izmenjen");
					String upit = "SELECT * FROM oprema WHERE brsasije='" + brsasije +
						"' ORDER BY rbr";
				popuniTabelu(upit);
				novislog = false;
				NoviPressed();
		}     
            else {
				JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
				NoviPressed();
            }
         }
         else {JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje Broj Saradnika");}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u izmeni"+sqlex);
			JOptionPane.showMessageDialog(this, query);
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
	public void DeleteRecord() {
 
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         if (t[0].getText().trim().length() != 0 ) {
               	String query = "DELETE FROM oprema " + 
				" WHERE brsasije='" + brsasije + "'" +
				" AND rbr=" + rednibroj;

               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					//JOptionPane.showMessageDialog(this, "Slog je izbrisan");
					String upit = "SELECT * FROM oprema WHERE brsasije='" + brsasije +
						"' ORDER BY rbr";
					popuniTabelu(upit);
					NoviPressed();
				}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne mo\u017ee izbrisati");
               		NoviPressed();
            	}
         }
         else{JOptionPane.showMessageDialog(this, "Unesi prvo podatke");
		 }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Gre\u0161ka u brisanju");
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
	public void FindRecord() {
 
	  Statement statement = null;
      try {
         statement = connection.createStatement();

			String query = "SELECT * FROM oprema WHERE brsasije='" + brsasije +
				"' AND rbr=" + rednibroj;

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					t[4].setText(rs.getString("naziv"));
					t[5].setText(rs.getString("komada"));
					t[6].setText(rs.getString("broj"));
					t[7].setText(rs.getString("opis"));
					t[8].setText(rs.getString("jmbg"));
					izmena = true;
					rs.close();
				}
					t[4].setSelectionStart(0);
					t[4].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Gre\u0161ka u tra\u017eenju");
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
//--------------------------------------------------------------------------------------
    public void proveriSifru(){ 
		String queryy;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[0].getText().equals( "" )) {
				queryy = "SELECT * FROM sifarnik WHERE sifra=" +
		            Integer.parseInt(t[0].getText().trim());
				try {
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		mkdisplej.setText(String.valueOf(rs.getString("naziv")));
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji groblje sa tom \u0161ifrom");
					}
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji \u0161ifra groblja morate je otvoriti");
				} 
			}
            else {
				JOptionPane.showMessageDialog(this, "Podatak nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u tra\u017eenju \u0161ifre groblja");
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
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi deo naziva");
		String upit = "SELECT * FROM oprema WHERE naziv LIKE '%" + String.valueOf(result) + "%'";
		popuniTabelu(upit);
		//jtbl.requestFocus();
		NoviPressed();
  }
//------------------------------------------------------------------------------------------------------------------
    public static void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("uplate") );

	   	qtbl = new mQTMUplate1(connection);
		String sql;
		sql = "SELECT * FROM oprema WHERE brsasije='9999999999' ORDER BY rbr";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		ptbl.setBounds(5,320,350,280);
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		/*
		int kojirec = jtbl.getSelectedRow();
		koji = Integer.parseInt(String.valueOf(uplate.get(kojirec)));
		koji1 = String.valueOf(suplate.get(kojirec));
		koji2 = Integer.parseInt(String.valueOf(buplate.get(kojirec)));
		koji3 = String.valueOf(uplate1.get(kojirec));
		koji4 = String.valueOf(uplate2.get(kojirec));
		koji5 = String.valueOf(uplate3.get(kojirec));
		*/
		izmena = true;
		FindRecord();
	}
//------------------------------------------------------------------------------------------------------------------
	public void idiNaDugme(){
			//if (izmena)
			//{
				izmeni.requestFocus();
			//}
			//else{
			//		unesi.requestFocus();
			//}
  }
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[4]){
					t[5].setSelectionStart(0);
					t[5].requestFocus();
				}
				else if (source == t[5]){
					if (t[5].getText().trim().length() == 0)
					{
						t[5].setText("0");
					}
					t[6].setSelectionStart(0);
					t[6].requestFocus();
				}
				else if (source == t[6]){
					if (t[6].getText().trim().length() == 0)
					{
						t[6].setText(" ");
					}
					t[7].setSelectionStart(0);
					t[7].setSelectionStart(0);
					t[7].requestFocus();
				}
				else if (source == t[7]){
					if (t[7].getText().trim().length() == 0)
					{
						t[7].setText(" ");
					}
					t[8].setSelectionStart(0);
					t[8].requestFocus();
				}
				else if (source == t[8]){
					if (t[8].getText().trim().length() == 0)
					{
						t[8].setText(" ");
					}
					if (izmena){
						izmeni.requestFocus();
					}
					else{
						unesi.requestFocus();
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
			int kojirec = jtbl.getSelectedRow();
			rednibroj = jtbl.getValueAt(kojirec,0).toString();
			prikaziIzTabele();
		}
	}
}//end of class ML

//===========================================================================
 class mQTMUplate1 extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Rbr","Naziv","Komada","Broj","Opis"};

//------------------------------------------------------------------------------------------------------------------
   public mQTMUplate1(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 5; }
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

               Object[] record = new Object[5];
				record[0] = rs.getString("rbr");
				record[1] = rs.getString("naziv");
				record[2] = rs.getString("komada");
				record[3] = rs.getString("broj");
				record[4] = rs.getString("opis");
               
			   redbr.addElement(record[0]);
   
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
class MCLuplate1 implements LayoutManager {

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

}// end of class MCLuplate1

