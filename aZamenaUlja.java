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


public class aZamenaUlja extends JInternalFrame implements InternalFrameListener
			{
	private mQTMUplate qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private Vector podaciList = new Vector();
	private boolean izmena = false;
	private int koji,prvisledeci=0;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,brsasije="";
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj;
   	private JLabel  l[];
	public static JLabel mkdisplej;
   	private int n_fields,vrdok=1;
	private JList list;
//------------------------------------------------------------------------------------------------------------------
    public aZamenaUlja() {
		super("", true, true, true, true);
        setTitle("Zamena ulja na vozilima");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( new GridLayout(1,2) );

		JPanel container = new JPanel();
		container.setLayout( new BorderLayout() );

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		Color c = new Color(117,175,163);
		//buttonPanel.setBackground(c);

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});
		buttonPanel.add( unesi );

		JButton novi = new JButton("Novi slog");
		novi.setMnemonic('N');
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});
		buttonPanel.add( novi );

		izmeni = new JButton("Izmeni");
		izmeni.setMnemonic('I');
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});
		buttonPanel.add( izmeni );

		JButton brisi = new JButton("Bri�i slog");
		brisi.setMnemonic('B');
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		buttonPanel.add( brisi );

		JButton trazi = new JButton("Tra�i");
		trazi.setMnemonic('T');
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		//buttonPanel.add( trazi );

		JButton stampa = new JButton("�tampa");
		stampa.setMnemonic('P');
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		//buttonPanel.add( stampa );

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
		buttonPanel.add( izlaz );
		
		pPre = new String("1");
		uzmiKonekciju();

		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
		main.add(buildNazivPanel(), BorderLayout.NORTH);
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		setSize(650,400);
		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(container));
		novaStavka();
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
		p.setLayout( new MCLzamenaulja() );
		p.setBorder( new TitledBorder("Unos") );
		Color c = new Color(117,175,163);
		//p.setBackground(c);
		int i;
        n_fields = 7; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 

		String fmm;

		fmm = "*******";
        l[0] = new JLabel("Redni broj :");
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setColumns(4);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
		t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "******************************";
        l[1] = new JLabel("Br. \u0161asije :");
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
		t[1].setColumns(15);
		t[1].setSelectionStart(0);
		t[1].setSelectionEnd(1);
		t[1].addFocusListener(new FL());
		t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[1].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziVozila();}});


		fmm = "##/##/####";
		l[2] = new JLabel("Datum :");
        t[2] = new JFormattedTextField(createFormatter(fmm,4));
		t[2].setColumns(10);
		t[2].setText(date);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "**********";
		l[3] = new JLabel("Koli\u010dina ulja :");
        t[3] = new JFormattedTextField(createFormatter(fmm,2));
		t[3].setColumns(10);
		t[3].addFocusListener(new FL());
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "************";
		l[4] = new JLabel("Stanje km :");
        t[4] = new JFormattedTextField(createFormatter(fmm,2));
		t[4].setColumns(10);
		t[4].addFocusListener(new FL());
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		
		
		//-----------------------------------------------------------------
		fmm = "************";
		l[5] = new JLabel("Pre\u0111eno km :");
        t[5] = new JFormattedTextField(createFormatter(fmm,2));
		t[5].setColumns(10);
		t[5].addFocusListener(new FL());
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		fmm = "************";
		l[6] = new JLabel("Potro\u0161nja na 100km :");
        t[6] = new JFormattedTextField(createFormatter(fmm,2));
		t[6].setColumns(10);
		t[6].addFocusListener(new FL());
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		JLabel lista = new JLabel("Izaberi :");
		String a2 = new String("\u017diralno");
		String a1 = new String("Gotovina");
		podaciList.addElement( a1 );
		podaciList.addElement( a2 );
		list = new JList(podaciList); 
 		list.setVisibleRowCount(2);
		list.setSelectedIndex(0);
        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        list.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {idiNaDugme();}});


	    for(i=0;i<5;i++){ 
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
		Color c = new Color(117,175,163);
		//p1.setBackground(c);

		JLabel vrd = new JLabel("Reg.broj:   ");
		mkdisplej = new JLabel("                         "); 
		mkdisplej.setFont(new Font("Arial",Font.BOLD,12));
		mkdisplej.setForeground(Color.blue);
		
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
		this.setLocation(20,20);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 5; 
        for(i=0;i<n_fields;i++)
            t[i].setText("");		
		izmena = false;
		t[2].setText(date);
		mkdisplej.setText("");
		list.setSelectedIndex(0);
		novaStavka();
	}
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    }
//-------------------------------------------------------------------------
   public void prikaziVozila(){
		int t = 12;
		mVozilaPrik sk = new mVozilaPrik(t);
		sk.setVisible(true);
   }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
//		mPrintMag jjj = new mPrintMag();
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
//uzima sledeci broj stavke 
   public void novaStavka(){ 
		Statement statement = null;
      try {
         statement = connection.createStatement();
               		String query = "SELECT MAX(rbr) FROM zamenaulja";
			try {
	         ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					prvisledeci = rs.getInt(1);
					prvisledeci = prvisledeci + 1;
				}
				else{
					prvisledeci = 1;
				}
					t[0].setText(String.valueOf(prvisledeci));
					t[0].setSelectionStart(0);
					t[0].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
					prvisledeci = 1;
					t[0].setSelectionStart(0);
					t[0].setText(String.valueOf(prvisledeci));
					t[0].requestFocus();
		      }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju sledece stavke:"+sqlex);
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
	public void AddRecord() {
	  Statement statement = null;
	  int koji = list.getSelectedIndex();

      try {
         statement = connection.createStatement();
		 if ( t[0].getText().trim().length() != 0 && 
            t[1].getText().trim().length() != 0 ) {
            String query = "INSERT INTO zamenaulja (rbr,brsasije,regbroj,datum,kolicina," +
				"stanjekm) VALUES(" +
				Integer.parseInt(t[0].getText().trim()) + ",'" + 
				t[1].getText().trim() + "','" +
				mkdisplej.getText() + "','" +
				konvertujDatum(t[2].getText().trim()) + "'," + 
				Double.parseDouble(t[3].getText().trim()) + "," +
				Double.parseDouble(t[4].getText().trim()) + ")";			
			
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
			//JOptionPane.showMessageDialog(this, "Slog je unet");

				String upit = "SELECT rbr,brsasije,regbroj,datum,ROUND(kolicina,2),stanjekm" +
					" FROM zamenaulja" +
					" ORDER BY rbr DESC";
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
  	  int koji = list.getSelectedIndex();
		String query = " ";
	  Statement statement = null;
      try {
         statement = connection.createStatement();

		 if ( !t[0].getText().equals( "" ) && !t[1].getText().equals( "" )) {
               query = "UPDATE zamenaulja SET " +
				"brsasije='" + t[1].getText().trim() + "'," + 
				"regbroj='" + mkdisplej.getText().trim() + "'," + 
				"datum='" + konvertujDatum(t[2].getText().trim()) + "'," + 
				"kolicina=" + Double.parseDouble(t[3].getText().trim()) + "," +
				"stanjekm=" + Double.parseDouble(t[4].getText().trim()) + 
				" WHERE rbr=" + Integer.parseInt(t[0].getText().trim());

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je izmenjen");
					String upit = "SELECT rbr,brsasije,regbroj,datum,ROUND(kolicina,2),stanjekm" +
					" FROM zamenaulja" +
					" ORDER BY rbr DESC";
					popuniTabelu(upit);
					NoviPressed();
				}     
				else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					NoviPressed();
				}
         }
         else {JOptionPane.showMessageDialog(this, "Unesi prvo podatke");}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u izmeni:"+sqlex);
			JOptionPane.showMessageDialog(this, "Upit:"+query);
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
         if ( !t[0].getText().equals( "" ) && !t[1].getText().equals( "" ) ) {
               	String query = "DELETE FROM zamenaulja WHERE  rbr=" + 
						Integer.parseInt(t[0].getText().trim());

               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
				//JOptionPane.showMessageDialog(this, "Slog je izbrisan");
					String upit = "SELECT rbr,brsasije,regbroj,datum,ROUND(kolicina,2),stanjekm" +
					" FROM zamenaulja" +
					" ORDER BY rbr DESC";;
					popuniTabelu(upit);
					NoviPressed();
				}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne moze izbrisati");
               		NoviPressed();
            	}
         }
         else{JOptionPane.showMessageDialog(this, "Unesi prvo podatke");}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u brisanju");
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

		 if ( !t[0].getText().equals( "" )) {
			String query = "SELECT * FROM zamenaulja WHERE rbr=" + koji;

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					t[0].setText(rs.getString("rbr"));
					t[1].setText(rs.getString("brsasije"));
		         	t[2].setText(konvertujDatumIzPodatakaQTB(rs.getString("datum")));
					t[3].setText(rs.getString("kolicina"));
					t[4].setText(rs.getString("stanjekm"));
					mkdisplej.setText(rs.getString("regbroj"));
					izmena = true;
					rs.close();
				}
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
		 }     
         else {JOptionPane.showMessageDialog(this, "Redni broj nije unet");}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju:"+sqlex);
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
    public boolean proveriVozilo(){ 
		String queryy;
		boolean imaga=false;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[1].getText().equals( "" )) {
				queryy = "SELECT * FROM vozila WHERE brsasije='" +
		            t[1].getText().trim() + "'";
				try {
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		mkdisplej.setText(String.valueOf(rs.getString("regbroj")));
						imaga = true;
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji vozilo morate ga otvoriti");
						t[1].setText("");
						t[1].requestFocus();
					}
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Greska u trazenju vozila:"+sqlex);
					t[1].setText("");
					t[1].requestFocus();
				}
			}     
            else {
				JOptionPane.showMessageDialog(this, "Vozilo nije uneto");
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
		return imaga;
   }
//------------------------------------------------------------------------------------------------------------------
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva magacina");
		String upit = "SELECT * FROM magacini WHERE nazivm LIKE '" + String.valueOf(result) + "%'" + 
						" And pre=" + Integer.parseInt(pPre);
		popuniTabelu(upit);
		jtbl.requestFocus();
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
		ptbl.setBorder( new TitledBorder("Podaci") );
		Color c = new Color(117,175,163);
		//ptbl.setBackground(c);

	   	qtbl = new mQTMUplate(connection);
		String sql;
		sql = "SELECT rbr,brsasije,regbroj,datum,ROUND(kolicina,2),stanjekm " +
			"FROM zamenaulja ORDER BY rbr DESC";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = Integer.parseInt(String.valueOf(podaci.get(kojirec)));
		brsasije = String.valueOf(spodaci.get(kojirec));
		FindRecord();
	}
//--------------------------------------------------------------------------
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
	public void idiNaDugme(){
			if (izmena)
			{
				izmeni.requestFocus();
			}
			else{
					unesi.requestFocus();
			}
  }
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					t[1].setSelectionStart(0);
					t[1].requestFocus();
				}
				else if (source == t[1]){
					if (proveriVozilo())
					{
						if (izmena == false){
							t[2].setText(date);
						}
						t[2].setSelectionStart(0);
						t[2].requestFocus();
					}else{
						t[1].setSelectionStart(0);
						t[1].requestFocus();
					}
				}
				else if (source == t[2]){
					if (proveriDatum(t[2].getText().trim()))
					{
						t[3].setSelectionStart(0);
						t[3].requestFocus();
					}else{
						t[2].setSelectionStart(0);
						t[2].requestFocus();
					}
				}
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0)
					{
						t[3].setText("0");
					}
						t[4].setSelectionStart(0);
						t[4].requestFocus();
				}
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0)
					{
						t[3].setText("0");
					}
						t[4].setSelectionStart(0);
						t[4].requestFocus();
				}
				else if (source == t[4]){
					if (t[4].getText().trim().length() == 0)
					{
						t[4].setText("0");
					}
					if (izmena){
						izmeni.requestFocus();
					}else{
						unesi.requestFocus();
					}
				}
				/*
				else if (source == t[5]){
					if (t[5].getText().trim().length() == 0)
					{
						t[5].setText("0");
					}
						t[6].setSelectionStart(0);
						t[6].requestFocus();
				}
				else if (source == t[6]){
					if (izmena){
						izmeni.requestFocus();
					}else{
						unesi.requestFocus();
					}
				}
				*/
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				if (t[0].getText().trim().length() > 0)
				{
					koji = Integer.parseInt(t[0].getText().trim());
					FindRecord();
				}
			}
	}
//------------------------------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
	}
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
			prikaziIzTabele();
		}
	}
}//end of class ML

//===========================================================================
 class mQTMUplate extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Rbr","Br.\u0161asije","Reg.br.","Datum","Koli\u010dina","Stanje km"};

//------------------------------------------------------------------------------------------------------------------
   public mQTMUplate(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 6; }
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

               Object[] record = new Object[6];
               record[0] = rs.getString("rbr");
               record[1] = rs.getString("brsasije");
               record[2] = rs.getString("regbroj");
               record[3] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[4] = rs.getString("ROUND(kolicina,2)");
               record[5] = rs.getBigDecimal("stanjekm");
               podaci.addElement(record[0]);
               spodaci.addElement(record[1]);
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
class MCLzamenaulja implements LayoutManager {

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

}// end of class MCLzamenaulja

