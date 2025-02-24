//Sifrnik robe-materijala
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


public class mRobe extends JInternalFrame implements InternalFrameListener
			{
	private mQTM1 qtbl;
   	private JTable jtbl;
	private Vector<Object[]> totalrows;
	private JScrollPane jspane;
	private Vector<Object> podaci = new Vector<Object>();
	private Vector spodaci = new Vector();
	private JFormattedTextField displej;
	private String koji;
	private boolean izmena = false;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,selekcija="";
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static FormField t[],mmoj;
   	private JLabel  l[];
   	int n_fields;
	private JCheckBox check1;
//------------------------------------------------------------------------------------------------------------------
    public mRobe() {
		super("", true, true, true, true);
        setTitle("\u0160ifarnik delova-materijala");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );
	
		JPanel glavni = new JPanel();
		glavni.setLayout( null );

		JPanel container = new JPanel();
		container.setLayout( null );
		container.setBounds(5,5,370,400);

		JPanel dugmad = new JPanel();
		dugmad.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		dugmad.setBounds(5,370,400,40);

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
		dugmad.add( unesi );

		JButton novi = new JButton("Novi slog");
		novi.setMnemonic('N');
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});
		dugmad.add( novi );

		izmeni = new JButton("Izmeni");
		izmeni.setMnemonic('I');
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});
		dugmad.add( izmeni );

		JButton brisi = new JButton("Bri\u0161i slog");
		brisi.setMnemonic('B');
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		dugmad.add( brisi );

		JButton trazi = new JButton("Tra\u017ei");
		trazi.setMnemonic('T');
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		buttonPanel.add( trazi );

		JButton stampasve = new JButton("\u0160tampa sve");
		stampasve.setMnemonic('P');
		stampasve.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(1); }});
		buttonPanel.add( stampasve );

		JButton stampa = new JButton("\u0160tampa cenovnik");
		stampa.setMnemonic('A');
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   StampaCenovnik(); }});
		//buttonPanel.add( stampa );

		JButton stampasel = new JButton("\u0160tampa cenovnik selekcije");
		stampasel.setMnemonic('A');
		stampasel.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   StampaCenovnikSel(); }});
		//buttonPanel.add( stampasel );

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
		buttonPanel.add( izlaz );

		pPre = "1";
		uzmiKonekciju();

		container.add(buildFilterPanel());
		container.add(dugmad);
		glavni.add(container);
		glavni.add(buildTable());
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		setSize(790,470);
		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(container));

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
				if (connection != null){
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
				JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");}}
	  }
  }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( new mCL1() );
		p.setBorder( new TitledBorder("Unos") );
		p.setBounds(5,5,390,330);

		int i;
        n_fields = 12; 
        t = new FormField[n_fields]; 
        l = new JLabel[n_fields]; 

		
		String fmm;
		fmm = "*****";
        l[0] = new JLabel("\u0160ifra :");
        t[0] = new FormField(createFormatter(fmm,1));
		t[0].setColumns(5);
		t[0].setSelectionStart(0);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "******************************";
		l[1] = new JLabel("Naziv dela :");
        t[1] = new FormField(createFormatter(fmm,3));
		t[1].setColumns(18);
		t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "******";
        l[2] = new JLabel("Jedinica mere :");
        t[2] = new FormField(createFormatter(fmm,3));
		t[2].setColumns(8);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "********************";
        l[3] = new JLabel("Katalo\u0161ki broj :");
        t[3] = new FormField(createFormatter(fmm,3));
		t[3].setColumns(18);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "******";
        l[4] = new JLabel("Tarifni broj :");
        t[4] = new FormField(createFormatter(fmm,1));
		t[4].setColumns(8);
		t[4].setSelectionStart(5);
		t[4].setSelectionEnd(6);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[4].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziTarifu();}});

		fmm = "*";
        l[5] = new JLabel("Ambala\u017ea: (1-ima, 0-nema)");
        t[5] = new FormField(createFormatter(fmm,1));
		t[5].setColumns(2);
		t[5].setText("0");
		t[5].setSelectionStart(0);
		t[5].setSelectionEnd(1);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		fmm = "***************";
        l[6] = new JLabel("Mal. cena :");
        t[6] = new FormField(createFormatter(fmm,2));
		t[6].setColumns(10);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		fmm = "***************";
        l[7] = new JLabel("Min. koli\u010dina :");
        t[7] = new FormField(createFormatter(fmm,2));
		t[7].setColumns(10);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

		fmm = "***************";
        l[8] = new JLabel("Dobavlja\u010d :");
        t[8] = new FormField(createFormatter(fmm,1));
		t[8].setColumns(10);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[8].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziDobavljaca();}});


		fmm = "***************";
        l[9] = new JLabel("Bar - kod :");
        t[9] = new FormField(createFormatter(fmm,3));
		t[9].setColumns(18);
        t[9].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[9].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[9]);}});

		fmm = "*******************************";
        l[10] = new JLabel("\u0160if. kod dob. :");
        t[10] = new FormField(createFormatter(fmm,3));
		t[10].setColumns(10);
        t[10].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[10].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[10]);}});

		/*
		fmm = "*********";
        l[11] = new JLabel("Te\u017eina :");
        t[11] = new FormField(createFormatter(fmm,2));
		t[11].setColumns(10);
        t[11].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[11].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[11]);}});

		JLabel lblCheck = new JLabel("Ulazi u cenovnik:");
		//lblCheck.setFont(new Font("Arial",Font.PLAIN,12));
		check1 = new JCheckBox("   ",false);
		check1.setSelected(false);
		check1.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        check1.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaC(check1);}});
		*/

		for(i=0;i<4;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
		for(i=8;i<11;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
        //p.add(lblCheck); 
        //p.add(check1); 
		return p;
    }
//--------------------------------------------------------------------------------------------
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
		this.setLocation(0,0);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
		for(i=0;i<4;i++){ 
            t[i].setText("");
            t[i].setValue(null);
		}
		for(i=8;i<11;i++){ 
            t[i].setText("");
            t[i].setValue(null);
		}

		izmena = false;
		t[4].setSelectionStart(5);
		t[4].setSelectionEnd(5);
		t[5].setText("0");
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
		t[0].requestFocus();
		//check1.setSelected(false);
    }
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    	}
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
        this.setVisible(false);
    	}
//-------------------------------------------------------------------------
   public void prikaziTarifu(){
		int t = 15;
		mTarbrPrik sk = new mTarbrPrik(connection,t);
		sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void prikaziDobavljaca(){
		int t = 4;
		fSaradPrik sk = new fSaradPrik(connection,t);
		sk.setVisible(true);
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
	public void AddRecord() {
	  Statement statement = null;
	  String query="";
	
      try {
         statement = connection.createStatement();
		 if ( !t[0].getText().equals( "" ) && !t[1].getText().equals( "" ) ) {
            query = "INSERT INTO sifarnikrobe (sifm,nazivm,jmere,katbr," +
				"kupacbr,barkod,sifrakoddob) VALUES(" +
				Integer.parseInt(t[0].getText().trim()) + ",'" +
				t[1].getText() + "','" +
				t[2].getText() + "','" +
				t[3].getText() + "'," +
				Integer.parseInt(t[8].getText().trim()) + ",'" +
				t[9].getText().trim() + "','" +
				t[10].getText().trim() + "')"; 
				
			    int result = statement.executeUpdate( query );

				if ( result == 1 ){
					//JOptionPane.showMessageDialog(this, "Slog je unet");
					String upit = "SELECT * FROM sifarnikrobe ORDER BY sifm";
					popuniTabelu(upit);
					NoviPressed();
				}else {
					JOptionPane.showMessageDialog(this, "Slog nije unet");
					NoviPressed();
				}
         } else {
			 JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");
		 }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u unosu;"+sqlex);
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
      try {
         statement = connection.createStatement();

		 if ( !t[0].getText().equals( "" )) {
               String query = "UPDATE sifarnikrobe SET " +
				"nazivm='" + t[1].getText()+ "'" +
				",jmere='" + t[2].getText() + "'" +
				",katbr='" + t[3].getText() + "'" +
				",kupacbr=" + Integer.parseInt(t[8].getText().trim()) +
				",barkod='" + t[9].getText().trim() + "'" +
				",sifrakoddob='" + t[10].getText().trim() + "'" +
				" WHERE sifm=" + Integer.parseInt(t[0].getText().trim());

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je izmenjen");
				String upit = "SELECT * FROM sifarnikrobe ORDER BY sifm";
				popuniTabelu(upit);
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
		JOptionPane.showMessageDialog(this, "Greska u izmeni");
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

               	String query = "DELETE FROM sifarnikrobe WHERE sifm=" + 
						Integer.parseInt(t[0].getText().trim());
						
               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					String upit = "SELECT * FROM sifarnikrobe ORDER BY sifm";
					popuniTabelu(upit);
					NoviPressed();
				}else {
            		JOptionPane.showMessageDialog(this, "Slog se ne moze izbrisati");
               		NoviPressed();
            	}

         }
			else {JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje konto");
		 }
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
			String query = "SELECT * FROM sifarnikrobe WHERE sifm=" + 
				Integer.parseInt(koji.trim());

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){

		         	t[0].setText(rs.getString("sifm"));
		         	t[1].setText(rs.getString("nazivm"));
		         	t[2].setText(rs.getString("jmere"));
		         	t[3].setText(rs.getString("katbr"));
					t[8].setText(rs.getString("kupacbr"));
					t[9].setText(rs.getString("barkod"));
					t[10].setText(rs.getString("sifrakoddob"));
					izmena = true;
					rs.close();		
				}
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		 }else {
              JOptionPane.showMessageDialog(this, "Deo nije unet");
          }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u FindRecord:" + sqlex);
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
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva");
		String upit = "SELECT * FROM sifarnikrobe WHERE nazivm LIKE '%" + 
			String.valueOf(result) + "%'";
		selekcija = upit;
		popuniTabelu(upit);
		jtbl.requestFocus();
  }
//------------------------------------------------------------------------------------------------------------------
	public void StampaCenovnik(){
		jPrintRobe pre = new jPrintRobe(connection,2,"SELECT * FROM sifarnikrobe WHERE pre=" + 
			pPre.trim() + " AND cenovnik=1 ORDER BY sifm");
		//mPrintCenovnik jjj = new mPrintCenovnik(connection);
   }
//------------------------------------------------------------------------------------------------------------------
	public void StampaCenovnikSel(){
		jPrintRobe pre = new jPrintRobe(connection,3,selekcija + " AND cenovnik=1 ORDER BY nazivm");
		//mPrintCenovnikSel jjj = new mPrintCenovnikSel(connection,selekcija);
   }
//------------------------------------------------------------------------------------------------------------------
	public void Stampa(int _koji){
		jPrintRobe pre = new jPrintRobe(connection,1,"SELECT * FROM sifarnikrobe ORDER BY sifm");

		//mPrintRobe jjj = new mPrintRobe(connection,_koji);
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
	   	tcol1.setPreferredWidth(150);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(80);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );
		ptbl.setBounds(400,5,380,350);

	   	qtbl = new mQTM1(connection);
		String sql;
		sql = "SELECT * FROM sifarnikrobe ORDER BY sifm";
	   	qtbl.query(sql);
        
		TableSorter sorter = new TableSorter(qtbl); //ADDED THIS
		jtbl = new JTable( sorter );
		jtbl.addMouseListener(new ML());

        sorter.addMouseListenerToHeaderInTable(jtbl); //ADDED THIS
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));

		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 

		TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(150);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(80);

	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		//koji = String.valueOf(podaci.get(kojirec));
		koji = jtbl.getValueAt(kojirec,0).toString();
		FindRecord();
	}
//------------------------------------------------------------------------------------------------------------------
	public void AkcijaC(JCheckBox e) {
		JCheckBox source;
		source = e;
		if (source == check1){
			if (izmena)
			{
				izmeni.requestFocus();
			}
			else{
				unesi.requestFocus();
			}
		}
}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(FormField e) {
		FormField source;
		source = e;

				if (source == t[0]){
					t[1].setSelectionStart(0);
					t[1].requestFocus();}
				else if (source == t[1]){
					t[2].setSelectionStart(0);
					t[2].requestFocus();}
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0){
						t[2].setText(" ");}
					t[3].setSelectionStart(0);
					t[3].requestFocus();}
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0){
						t[3].setText(" ");
					}
					t[8].setSelectionStart(0);
					t[8].requestFocus();
				}
				/*
				else if (source == t[4]){
					if (t[4].getText().trim().length() == 0){
						t[4].setText("1");}
					t[5].setSelectionStart(0);
					t[5].requestFocus();}
				else if (source == t[5]){
					if (t[5].getText().trim().length() == 0){
						t[5].setText("0");}
					t[6].setSelectionStart(0);
					t[6].requestFocus();}
				else if (source == t[6]){
					if (t[6].getText().trim().length() == 0){
						t[6].setText("0");}
					t[7].setSelectionStart(0);
					t[7].requestFocus();}
				else if (source == t[7]){
					if (t[7].getText().trim().length() == 0){
						t[7].setText("0");}
					t[8].setSelectionStart(0);
					t[8].requestFocus();}
					*/
				else if (source == t[8]){
					if (t[8].getText().trim().length() == 0){
						t[8].setText("0");}
					t[9].setSelectionStart(0);
					t[9].requestFocus();
				}
				else if (source == t[9]){
					if (t[9].getText().trim().length() == 0){
						t[9].setText(t[0].getText().trim());
					}
					t[10].setSelectionStart(0);
					t[10].requestFocus();
				}
				else if (source == t[10]){
					if (t[10].getText().trim().length() == 0){
						t[10].setText("0");
					}
					if (izmena)
					{
						izmeni.requestFocus();
					}
					else{
						unesi.requestFocus();
					}
				}
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				if (t[0].getText().trim().length() > 0)
				{
					koji = t[0].getText().trim();
					FindRecord();
				}
			}
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
 class mQTM1 extends AbstractTableModel {
	Connection dbconn;
	//public Vector totalrows;
	String[] colheads = {"\u0160ifra","Naziv","JM","Kat.br.","Dobavlja\u010d","\u0160ifra kod dob."};

//------------------------------------------------------------------------------------------------------------------
   public mQTM1(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector<Object[]>();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 6; }
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
					Object[] record = new Object[6];
					record[0] = rs.getString("sifm");
					record[1] = rs.getString("nazivm");
					record[2] = rs.getString("jmere");
					record[3] = rs.getString("katbr");
					record[4] = rs.getString("kupacbr");
					record[5] = rs.getString("sifrakoddob");
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
 }//end of class mQTM1

}// end of class Konto ====================================================================
class mCL1 implements LayoutManager {

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
	  children[i].setLocation( xInset + insets.left, height);
	//pozicija za txt
	  children[i + 1].setSize(compSize1.width, compSize1.height);
	  children[i + 1].setLocation( xInset + insets.left + 140, height);
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
	  width = Math.max(width, compSize.width + insets.left + insets.right + xInset*2 + 5);
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

}// end of class mCL1

