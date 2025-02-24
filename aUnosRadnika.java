//Sifarnik valutnih kurseva---------------
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


public class aUnosRadnika extends JInternalFrame implements InternalFrameListener
			{
	private QTMRadnici qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector podaciList = new Vector();
	private Vector spodaci = new Vector();
	private boolean izmena = false;

	private String koji,date,kojidatum;
	private MaskFormatter fmt = null;
	private String pPre="1",nazivPre;
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],mmoj;
	private JPasswordField pass1,pass2;
   	private JLabel  l[];
	private JList list;
   	private int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public aUnosRadnika() {
		super("", true, true, true, true);
        setTitle("\u0160ifarnik radnika ");
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
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});
		buttonPanel.add( izmeni );

		JButton brisi = new JButton("Bri\u0161i slog");
		brisi.setMnemonic('B');
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		buttonPanel.add( brisi );

		JButton trazi = new JButton("Tra\u017ei");
		trazi.setMnemonic('T');
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		buttonPanel.add( trazi );

		JButton stampa = new JButton("\u0160tampa svi");
		stampa.setMnemonic('P');
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		buttonPanel.add( stampa );

		JButton stampapoj = new JButton("\u0160tampa 1");
		stampapoj.setMnemonic('1');
		stampapoj.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   StampaPoj(); }});
		buttonPanel.add( stampapoj );

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
		buttonPanel.add( izlaz );

		uzmiKonekciju();

		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		setSize(700,380);
		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(container));
		novi.requestFocus();

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
		p.setLayout( null );
		p.setBorder( new TitledBorder("Unos") );

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		int i;
        t = new JFormattedTextField[9]; 
        l = new JLabel[9]; 
		
		String fmm;
		fmm = "*************";
        l[0] = new JLabel("JMBG :");
			l[0].setBounds(10,30,100,20);
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
			t[0].setBounds(140,30,150,20);
		t[0].setText("");
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "***********************************";
		l[1] = new JLabel("Ime i prezime :");
			l[1].setBounds(10,60,100,20);
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
			t[1].setBounds(140,60,150,20);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "***************************";
        l[2] = new JLabel("Radno mesto :");
			l[2].setBounds(10,90,100,20);
        t[2] = new JFormattedTextField(createFormatter(fmm,3));
			t[2].setBounds(140,90,150,20);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		l[3] = new JLabel("Broj voza\u010dke dozvole :");
			l[3].setBounds(10,120,100,20);
        t[3] = new JFormattedTextField(createFormatter(fmm,3));
			t[3].setBounds(140,120,150,20);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "##/##/####";
		l[4] = new JLabel("Datum rodjenja :");
			l[4].setBounds(10,150,100,20);
        t[4] = new JFormattedTextField(createFormatter(fmm,4));
			t[4].setBounds(140,150,150,20);
		t[4].addFocusListener(new FL());
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "***************************";
		l[5] = new JLabel("Broj LK:");
			l[5].setBounds(10,180,100,20);
        t[5] = new JFormattedTextField(createFormatter(fmm,3));
			t[5].setBounds(140,180,150,20);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		fmm = "##/##/####";
		l[6] = new JLabel("Zaposlen u firmi od:");
			l[6].setBounds(10,210,100,20);
        t[6] = new JFormattedTextField(createFormatter(fmm,4));
			t[6].setBounds(140,210,150,20);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		l[7] = new JLabel("Datum zadnjeg lekarskog:");
			l[7].setBounds(10,240,100,20);
        t[7] = new JFormattedTextField(createFormatter(fmm,4));
			t[7].setBounds(140,240,150,20);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

		fmm = "**";
		l[8] = new JLabel("Lekarski (meseci):");
			l[8].setBounds(10,270,100,20);
        t[8] = new JFormattedTextField(createFormatter(fmm,1));
			t[8].setBounds(140,270,150,20);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});


		JLabel lista = new JLabel("Izaberi status :");
		lista.setBounds(10,210,120,20);
		String a1 = new String("Radnik");
		String a2 = new String("Administrator");
		podaciList.addElement( a1 );
		podaciList.addElement( a2 );
		list = new JList(podaciList); 
 		list.setVisibleRowCount(2);
		list.setSelectedIndex(0);
		list.setBounds(140,210,90,40);
		list.addMouseListener(new ML());
        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        list.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {idiNaDugme();}});


        for (i=0;i<9 ;i++ )
        {
			p.add(l[i]);
			p.add(t[i]);
        }
		//p.add(lista);
		//p.add(list);

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
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(10,0);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 9 ; 
        for(i=0;i<n_fields;i++){
            t[i].setText("");
		}
		izmena = false;
		t[0].requestFocus();
    }
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		jPrintRadnici pv = new jPrintRadnici(connection);
	}
//------------------------------------------------------------------------------------------------------------------
    public void StampaPoj() {
		if (t[0].getText().trim().length() != 0 ){
			String radnik = t[0].getText().trim();
			jPrintRadnik pv = new jPrintRadnik(connection,radnik);

		}else{
			JOptionPane.showMessageDialog(this, "Prvo uzmite podatke");
			t[0].requestFocus();
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
		}else{
		}
		return true;
   }
//------------------------------------------------------------------------------------------------------------------
	public void AddRecord() {
		int statuss;
		String ss,query=" ";
		Statement statement = null;		
      try {
         statement = connection.createStatement();

		 if ( t[0].getText().trim().length() != 0 &&
			 t[1].getText().trim().length() != 0 )
		 {
			 statuss = list.getSelectedIndex();
             query = "INSERT INTO radnici (jmbg,imeprezime,radnomesto,brvozdozvole," +
				 "datrodjenja,brlk,zaposlenod,datumzadlek,lekarskimes) VALUES('" +
				t[0].getText().trim() + "','" + t[1].getText() + 
				"','" + t[2].getText() + "','" + t[3].getText() + "','" +
				 konvertujDatum(t[4].getText().trim()) + "','" + t[5].getText() + 
				 "','" + konvertujDatum(t[6].getText().trim()) + 
				 "','" + konvertujDatum(t[7].getText().trim()) + 
				 "'," + Integer.parseInt(t[8].getText().trim()) + ")";	
		
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je unet");
				String upit = "SELECT * FROM radnici";
				popuniTabelu(upit);
				NoviPressed();

			}     
			else {
				JOptionPane.showMessageDialog(this, "Slog nije unet");
				NoviPressed();
            }
         }
         else {JOptionPane.showMessageDialog(this, "Unesi sva polja");
			t[0].requestFocus();}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Gre\u0161ka u unosu:"+sqlex);
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
	public void UpdateRecord() {
		Statement statement = null;  
		String query = " ";
      try {
         statement = connection.createStatement();

		 if ( !t[0].getText().equals( "" )) {
               query = "UPDATE radnici SET " +
				"imeprezime='" + t[1].getText() + "'," +
				"radnomesto='" + t[2].getText() + "'," +
				"brvozdozvole='" + t[3].getText() + "'," +
				"datrodjenja='" + konvertujDatum(t[4].getText().trim()) + "'," +
				"zaposlenod='" + konvertujDatum(t[6].getText().trim()) + "'," +
				"datumzadlek='" + konvertujDatum(t[7].getText().trim()) + "'," +
				"brlk='" + t[5].getText() + "'," +
				"lekarskimes=" + Integer.parseInt(t[8].getText().trim()) + 
				" WHERE jmbg='" + t[0].getText().trim() + "'";

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
					//JOptionPane.showMessageDialog(this, "Slog je izmenjen");
					String upit = "SELECT * FROM radnici ";
					popuniTabelu(upit);
					NoviPressed();
				}     
				else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					NoviPressed();
				}
         }
         else{
			 JOptionPane.showMessageDialog(this, "Neispravni podaci");
		 }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Gre\u0161ka u izmeni:"+sqlex);
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
         if ( !t[0].getText().equals( "" ) ) {
               	String query = "DELETE FROM radnici WHERE jmbg='" + 
						t[0].getText().trim() + "'" ;
						

               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					//JOptionPane.showMessageDialog(this, "Slog je izbrisan");
				String upit = "SELECT * FROM radnici";
				popuniTabelu(upit);
				NoviPressed();
			}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne mo\u017ee izbrisati");
               		NoviPressed();
            	}
         }
         else{JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje sifra");}
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
		 if ( !t[0].getText().equals( "" )) {
			String query = "SELECT * FROM radnici WHERE jmbg='" + koji + "'"; 

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					t[0].setText(rs.getString("jmbg"));
		         	t[1].setText(rs.getString("imeprezime"));
		         	t[2].setText(rs.getString("radnomesto"));
		         	t[3].setText(rs.getString("brvozdozvole"));
					t[4].setText(konvertujDatumIzPodataka(rs.getString("datrodjenja")));
					t[5].setText(rs.getString("brlk"));
					t[6].setText(konvertujDatumIzPodataka(rs.getString("zaposlenod")));
					t[7].setText(konvertujDatumIzPodataka(rs.getString("datumzadlek")));
					t[8].setText(rs.getString("lekarskimes"));
					izmena = true;				
				}
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
		 }     
         else {
            JOptionPane.showMessageDialog(this, "Radnik nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u tra\u017eenju");
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
   public String konvertujDatum(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(6,10);
		datum = datum + "-" + pom.substring(3,5);
		datum = datum + "-" + pom.substring(0,2);
		return datum;
   }
//--------------------------------------------------------------------------
   public String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
		return datum;
   }

//------------------------------------------------------------------------------------------------------------------
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi ime prezime ili deo");
		String upit = "SELECT * FROM radnici WHERE imeprezime LIKE '%" + String.valueOf(result) + "%' ORDER BY imeprezime";
		popuniTabelu(upit);
		jtbl.requestFocus();
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
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(2);
	   	tcol3.setPreferredWidth(80);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new QTMRadnici(connection);
		String sql;
		sql = "SELECT * FROM radnici";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(2);
	   	tcol3.setPreferredWidth(80);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = String.valueOf(podaci.get(kojirec));
		FindRecord();
	}
//------------------------------------------------------------------------------------------------------------------
    public void idiNaDugme() {
		/*
		int koji = list.getSelectedIndex();
		if (koji == 0)
		{
			t[0].setText("Radnik");
		}else{
			t[0].setText("Administrator");
		}
		*/
		if (izmena)
		{
			izmeni.requestFocus();
		}else{
			unesi.requestFocus();
		}
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
						t[1].requestFocus();
					}
				}
				else if (source == t[1]){
					if (t[1].getText().trim().length() == 0)
					{
						t[1].setText(" ");
					}
					t[2].setSelectionStart(0);
					t[2].requestFocus();
				}
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0)
					{
						t[2].setText(" ");
					}
					t[3].setSelectionStart(0);
					t[3].requestFocus();
				}
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0)
					{
						t[3].setText(" ");
					}
					t[4].setSelectionStart(0);
					t[4].requestFocus();
				}
				else if (source == t[4]){
					if (t[4].getText().trim().length() == 0)
					{
						t[4].requestFocus();
					}else{
						if (proveriDatum(t[4].getText().trim()))
						{
							t[5].setSelectionStart(0);
							t[5].requestFocus();
						}else{
							t[4].setSelectionStart(0);
							t[4].requestFocus();
						}
					}
				}
				else if (source == t[5]){
					t[6].setSelectionStart(0);
					t[6].requestFocus();
				}
				else if (source == t[6]){
					if (t[6].getText().trim().length() == 0)
					{
						t[6].requestFocus();
					}else{
						if (proveriDatum(t[6].getText().trim()))
						{
							t[7].setSelectionStart(0);
							t[7].requestFocus();
						}else{
							t[6].setSelectionStart(0);
							t[6].requestFocus();
						}
					}
				}
				else if (source == t[7]){
					if (t[7].getText().trim().length() == 0)
					{
						t[7].requestFocus();
					}else{
						if (proveriDatum(t[7].getText().trim()))
						{
							t[8].setSelectionStart(0);
							t[8].requestFocus();
						}else{
							t[7].setSelectionStart(0);
							t[7].requestFocus();
						}
					}
				}
				else if (source == t[8]){
					if (t[8].getText().trim().length() == 0)
					{
						t[8].setText("0");
					}
					if (izmena)
					{
						izmeni.requestFocus();
					}else{
						unesi.requestFocus();
					}
				}
	}
//==========================================================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[2]){
				if (t[0].getText().trim().length() > 0)
				{
					koji = t[0].getText();
					FindRecord();
				}
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
		if (source == list){
			idiNaDugme();
		}
		else if (source == jtbl){
			prikaziIzTabele();
		}
	}
}//end of class ML

//===========================================================================
 class QTMRadnici extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"JMBG","Ime Prezime","Radno mesto","Br.vozacke dozvole","Dat.rodjenja"};

//------------------------------------------------------------------------------------------------------------------
   public QTMRadnici(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 5; }
   public int getRowCount() { return totalrows.size(); }
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
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
   public void query(String _sql) {
		String sql;
		sql = _sql;
		podaci.clear();
		Statement statement = null;
		try {
        statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector();
            while ( rs.next() ) {

               String[] record = new String[5];
               record[0] = rs.getString("jmbg");
               record[1] = rs.getString("imeprezime");
               record[2] = rs.getString("radnomesto");
               record[3] = rs.getString("brvozdozvole");
               record[4] = konvertujDatumIzPodatakaQTB(rs.getString("datrodjenja"));
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
 }//end of class QTMRadnici

}// end of class Konto ====================================================================
class CL6 implements LayoutManager {

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

}// end of class CL6
/*
INSERT INTO radnici (sifra,ime,passwd,stat) VALUES(8,'aaa   ', 
MD5('12345678'),0)*/
