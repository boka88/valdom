//program za unos sifarnika polaznika
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


public class aPregledNaloga extends JInternalFrame implements InternalFrameListener
			{
	public static QTMRNal qtbl;
   	public static JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private Vector podaciList = new Vector();
	private boolean izmena = false;
	private String date;
	private int koji,obradjeno=0;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni,racun;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj,sifRadnika;
	public static JTextArea zaglavlje,podnozje,delovi,napomena;
   	public static JLabel  l[],lblRadnikPrik;
   	private int n_fields,kontorep,prvisledeci=0,sifraradnika=0;
	private boolean uminus;
	private JCheckBox check1,check2;
	private JList list;
	public static String regbroj=" ";
	private mPanelRnal panelrnal;
	private JPanel butonpanel;

//------------------------------------------------------------------------------------------------------------------
    public aPregledNaloga() {
		super("", true, true, true, true);
        setTitle("Pregled radnih naloga    ");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel glavni = new JPanel();
		glavni.setLayout( null);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );
		main.setBounds(0,0,500,350);

		butonpanel = new JPanel();
		butonpanel.setLayout( new FlowLayout());
		butonpanel.setBounds(0,355,500,50);

		glavni.add(butonpanel);

		pPre = new String("1");
		nazivPre = new String("");

		panelrnal = new mPanelRnal();
		panelrnal.setBounds(505,0,350,600);

		uzmiKonekciju();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		//buildTable();
		glavni.add(buildTable());
		glavni.add(panelrnal);
		main.add(buildPanel());
		//main.add(buildPanel(), BorderLayout.CENTER);
		glavni.add(main);
		
		
		getContentPane().add(glavni);
		pack();
		setSize(900,660);
		centerDialog();

		UIManager.addPropertyChangeListener(new UISwitchListener(main));
		t[0].setSelectionStart(0);
		t[0].requestFocus();
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
			//izbrisiZauzetNalog();			
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
    public JPanel buildPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder("") );

		int prviLX,prviTX,drugiLX,drugiTX,treciLX,treciTX,visina,razmakY;
		int sirinaL,txt,aa,btnY,btnX,btnsirina,btnrazmak;
		aa = 15;					//rastojanje od gornje ivice panela
		sirinaL = 80;			//sirina labele
		visina = 20;			//visina komponente
		prviLX = 10;			//X-pozicija za prvi red labela
		prviTX = 30 + sirinaL;	//X-pozicija za prvi red text-box
		drugiLX = 360;
		drugiTX = drugiLX + sirinaL + 10;
		treciLX = 450;
		treciTX = treciLX + sirinaL + 20;
		razmakY = 3;			// razmak izmedju komponenti po visini
		txt = 10;				// vrednost jedinicnog razmaka u txt - polju
		btnY = 380;
		btnX = 50;
		btnsirina = 80;
		btnrazmak = 5;

		int i;
        n_fields = 10; 
        t = new JFormattedTextField[10]; 
        l = new JLabel[11]; 
		JLabel lblRadnik;
		
		String fmm;
		fmm = "******";
        l[0] = new JLabel("Nalog br. :");
			l[0].setBounds(prviLX,aa,sirinaL,visina);
		l[0].setFont(new Font("Arial",Font.PLAIN,10));
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setFont(new Font("Arial",Font.PLAIN,12));
			t[0].setBounds(prviTX,aa,10*txt,visina);
		t[0].setText(String.valueOf(prvisledeci));
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "##/##/####";
		l[1] = new JLabel("Datum :");
			l[1].setBounds(prviLX,aa + visina + razmakY,sirinaL,visina);
		l[1].setFont(new Font("Arial",Font.PLAIN,10));
        t[1] = new JFormattedTextField(createFormatter(fmm,4));
		t[1].setFont(new Font("Arial",Font.PLAIN,12));
			t[1].setBounds(prviTX,aa + visina + razmakY,10*txt,visina);
		//t[1].setText(date);
		//t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "*************************";
        l[2] = new JLabel("Br.\u0161asije :");
 			l[2].setBounds(prviLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[2].setFont(new Font("Arial",Font.PLAIN,10));
		t[2] = new JFormattedTextField(createFormatter(fmm,3));
		t[2].setFont(new Font("Arial",Font.PLAIN,12));
			t[2].setBounds(prviTX,aa + 2*(visina + razmakY),17*txt,visina);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[2].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziVozila();}});

		fmm = "***************";
        
		lblRadnik = new JLabel("Serviser :");
 			lblRadnik.setBounds(prviLX,aa + 3*(visina + razmakY),sirinaL,visina);
		lblRadnik.setFont(new Font("Arial",Font.PLAIN,10));
		sifRadnika = new JFormattedTextField(createFormatter(fmm,3));
		sifRadnika.setFont(new Font("Arial",Font.PLAIN,12));
			sifRadnika.setBounds(prviTX,aa + 3*(visina + razmakY),10*txt,visina);
		sifRadnika.setSelectionStart(0);
		sifRadnika.setSelectionEnd(1);
        sifRadnika.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        sifRadnika.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(sifRadnika);}});
        sifRadnika.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        sifRadnika.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziRadnike();}});



		fmm = "************";
        l[3] = new JLabel("Cena radova. :");
			l[3].setBounds(drugiLX,aa,sirinaL,visina);
		l[3].setFont(new Font("Arial",Font.PLAIN,10));
        t[3] = new JFormattedTextField(createFormatter(fmm,2));
		t[3].setFont(new Font("Arial",Font.PLAIN,12));
			t[3].setBounds(drugiTX,aa,10*txt,visina);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		l[4] = new JLabel("Cena delova :");
			l[4].setBounds(drugiLX,aa + visina + razmakY,sirinaL,visina);
		l[4].setFont(new Font("Arial",Font.PLAIN,10));
        t[4] = new JFormattedTextField(createFormatter(fmm,2));
		t[4].setFont(new Font("Arial",Font.PLAIN,12));
			t[4].setBounds(drugiTX,aa + visina + razmakY,10*txt,visina);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "*************";
		l[5] = new JLabel("Garancija :");
			l[5].setBounds(drugiLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[5].setFont(new Font("Arial",Font.PLAIN,10));
        t[5] = new JFormattedTextField(createFormatter(fmm,3));
		t[5].setFont(new Font("Arial",Font.PLAIN,12));
			t[5].setBounds(drugiTX,aa + 2*(visina + razmakY),10*txt,visina);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		fmm = "********";
		l[6] = new JLabel("Rabat (%) :");
			l[6].setBounds(drugiLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[6].setFont(new Font("Arial",Font.PLAIN,10));
        t[6] = new JFormattedTextField(createFormatter(fmm,2));
		t[6].setFont(new Font("Arial",Font.PLAIN,12));
			t[6].setBounds(drugiTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		JLabel zag = new JLabel("Opis kvara :");
		zag.setFont(new Font("Arial",Font.PLAIN,10));
		zag.setBounds(prviLX,aa + 3*(visina + razmakY) + 30,sirinaL,visina);
		zaglavlje = new JTextArea();
		zaglavlje.setFont(new Font("Arial",Font.PLAIN,12));
		zaglavlje.setLineWrap(true);
		zaglavlje.setBounds(prviTX,aa + 3*(visina + razmakY) + 30,500,45);
		zaglavlje.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        zaglavlje.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(zaglavlje);}});

		JLabel pod = new JLabel("Opis radova :");
		pod.setFont(new Font("Arial",Font.PLAIN,10));
		pod.setBounds(prviLX,aa + 4*(visina + razmakY) + 50,sirinaL,visina);
		podnozje = new JTextArea();
		podnozje.setFont(new Font("Arial",Font.PLAIN,12));
		podnozje.setLineWrap(true);
		podnozje.setBounds(prviTX,aa + 4*(visina + razmakY) + 60,500,45);
		podnozje.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        podnozje.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(podnozje);}});

		JLabel pod1 = new JLabel("Ugra\u0111eni delovi :");
		pod1.setFont(new Font("Arial",Font.PLAIN,10));
		pod1.setBounds(prviLX,aa + 5*(visina + razmakY) + 90,sirinaL + 10,visina);
		delovi = new JTextArea();
		delovi.setFont(new Font("Arial",Font.PLAIN,12));
		delovi.setLineWrap(true);
		delovi.setBounds(prviTX,aa + 5*(visina + razmakY) + 90,500,40);
		delovi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        delovi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(delovi);}});

		JLabel pod2 = new JLabel("Napomena :");
		pod2.setFont(new Font("Arial",Font.PLAIN,10));
		pod2.setBounds(prviLX,aa + 6*(visina + razmakY) + 120,sirinaL,visina);
		napomena = new JTextArea();
		napomena.setFont(new Font("Arial",Font.PLAIN,12));
		napomena.setLineWrap(true);
		napomena.setBounds(prviTX,aa + 6*(visina + razmakY) + 120,500,45);
		napomena.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        napomena.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(napomena);}});

		//naziv vlasnika vozila
		l[7] = new JLabel("");
			l[7].setBounds(prviTX + 10*txt +5,aa,120,visina);
		l[7].setFont(new Font("Arial",Font.PLAIN,10));
		l[7].setForeground(Color.blue);

		//ime servisera
        lblRadnikPrik = new JLabel("");
 			lblRadnikPrik.setBounds(prviTX + 10*txt +5,aa + 3*(visina + razmakY),120,visina);
		lblRadnikPrik.setForeground(Color.blue);

		novi = new JButton("Novi nalog");
		novi.setMnemonic('N');
		novi.setBounds(btnX,btnY,btnsirina,20);
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});

		JButton trazi = new JButton("Materijal");
		trazi.setMnemonic('M');
		trazi.setBounds(btnX + 5*(btnsirina + btnrazmak),btnY,btnsirina + 20,20);
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});

		JButton stampa = new JButton("\u0160tampa");
		stampa.setMnemonic('P');
		stampa.setBounds(btnX + 3*(btnsirina + btnrazmak),btnY,btnsirina,20);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});


		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.setBounds(btnX + 5*(btnsirina + btnrazmak),btnY,btnsirina,20);
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});

		JLabel lblCheck = new JLabel("Pla\u0107eno:");
		lblCheck.setFont(new Font("Arial",Font.PLAIN,10));
		lblCheck.setBounds(prviLX,aa + 10*(visina + razmakY) + 90,sirinaL,visina);
		check1 = new JCheckBox("   ",false);
		check1.setBounds(prviTX,aa + 10*(visina + razmakY) + 90,50,20);
		check1.setSelected(false);
		check1.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        check1.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaC(check1);}});
		//check1.addMouseListener(new ML());

		JLabel lblCheck2 = new JLabel("Reklamacija :");
		lblCheck2.setFont(new Font("Arial",Font.PLAIN,10));
		lblCheck2.setBounds(prviTX + 50,aa + 10*(visina + razmakY) + 90,sirinaL,visina);
		check2 = new JCheckBox("   ",false);
		check2.setBounds(prviTX + 120,aa + 10*(visina + razmakY) + 90,50,20);
		check2.setSelected(false);
		check2.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        check2.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaC(check2);}});
		//check2.addMouseListener(new ML());

		JLabel lista = new JLabel("Izaberi :");
		lista.setBounds(prviTX + 220,aa + 10*(visina + razmakY) + 90,sirinaL,visina);
		String a1 = new String("Gotovina");
		String a2 = new String("\u017diralno");
		podaciList.addElement( a1 );
		podaciList.addElement( a2 );
		list = new JList(podaciList); 
 		list.setVisibleRowCount(2);
		list.setSelectedIndex(0);
		list.setBounds(prviTX + 280,aa + 10*(visina + razmakY) + 80,sirinaL,visina + 20);
        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        list.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {izmeni.requestFocus();}});
	
		for(i=0;i<3;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
		p.add(lblRadnik);
		p.add(sifRadnika);
		p.add(lblRadnikPrik);

		/* for(i=3;i<7;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		} 
		*/
	    /*
		for(i=7;i<8;i++){ 
            p.add(l[i]);} 
		*/
		p.add(zag);
		p.add(zaglavlje);
		p.add(pod);
		p.add(podnozje);
		p.add(pod1);
		p.add(delovi);
		p.add(pod2);
		p.add(napomena);
		
		//butonpanel.add(novi);
		//p.add(izmeni);
		//butonpanel.add(unesi);
		//butonpanel.add(brisi);
		//p.add(racun);
		//p.add(trazi);
		butonpanel.add(stampa);
		butonpanel.add(izlaz);
		//p.add(jspane);
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
					formatter.setValidCharacters("0123456789/- ");
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
		int y = 0;
		int x = 20;
		this.setLocation(x,y);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
		//izbrisiZauzetNalog();			

		
		for(i=0;i<3;i++){
            t[i].setText("");
		}
		t[0].setFocusable(true);
		sifRadnika.setText("");
		lblRadnikPrik.setText("");
        l[7].setText("");
		podnozje.setText("");
		zaglavlje.setText("");
		delovi.setText("");
		napomena.setText("");
		izmena = false;
		t[1].setText(date);
		novaStavka();
		t[0].setSelectionStart(0);
		t[0].requestFocus();
    }
 //---------------------------------------------------------------------------
//uzima sledeci broj stavke 
   public void novaStavka(){ 
		Statement statement = null;
      try {
         statement = connection.createStatement();
               		String query = "SELECT MAX(rbr) FROM rnal";
			try {
	         ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					prvisledeci = rs.getInt(1);
					prvisledeci = prvisledeci + 1;
				}
				else{
					prvisledeci = 1;
				}
					//zauzmiRedniBroj(prvisledeci);
					izmena = false;
					t[0].setText(String.valueOf(prvisledeci));
					t[0].setSelectionStart(0);
					t[0].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
					izmena = false;
					prvisledeci = 1;
					t[0].setText(String.valueOf(prvisledeci));
					t[0].setSelectionStart(0);
					t[0].requestFocus();
		      }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju prve sledece stavke");
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
 public void izbrisiZauzetNalog() {
 			String query = "DELETE FROM rnal" +
				" WHERE rbr=" + prvisledeci; 
			izvrsi(query);
}
//------------------------------------------------------------------------------------------------------------------
 public void zauzmiRedniBroj(int _rednibroj) {
		String oop=" ";
		String query = "INSERT INTO rnal(rbr,jmbg,opiskvara,opisradova,delovi," +
				"napomena,datum)" +
				" VALUES(" + _rednibroj + 
				",' ','" + oop + 
				"','" + oop + "','" + oop + "','" +
				oop + "','2000-01-01')"; 
		izvrsi(query);
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
			JOptionPane.showMessageDialog(null, "Greska: " + sql);
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
 public void UnesiPressed() {
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 )
	{
			//AddRecord();
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].setSelectionStart(0);
		t[0].requestFocus();
	}
 }
//------------------------------------------------------------------------------------------------------------------
    public void prikaziVozila() {
		int t=1;
		mVozilaPrik mvp = new mVozilaPrik(t);
		mvp.setModal(true);
		mvp.setVisible(true);
    }
//------------------------------------------------------------------------------------------------------------------
    public void prikaziRadnike() {
		int t=1;
		fKupciPrik mvp = new fKupciPrik(connection,t);
		mvp.setModal(true);
		mvp.setVisible(true);
    }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		int pred,jur,mag,sifra,nalog;
		double kolicina;
		String brojsasije,sifrad;

	if (t[0].getText().trim().length() != 0 )
	{
		sifra = Integer.parseInt(t[0].getText().trim());
		brojsasije = t[2].getText();
		sifrad = sifRadnika.getText();
		jPrintRadniNalog pn = new jPrintRadniNalog(connection,sifra);

	}     
    else {
         JOptionPane.showMessageDialog(this, "Unesi prvo broj naloga");
		 t[0].setSelectionStart(0);
		 t[0].requestFocus();
    }
}
//------------------------------------------------------------------------------------------------------------------
    public void Racun() {
		int pred,jur,mag,sifra,nalog;
		double kolicina;
		String brojsasije;

	if (t[0].getText().trim().length() != 0 )
	{
		sifra = Integer.parseInt(t[0].getText().trim());
		brojsasije = t[2].getText();
		aPrintRacun pn = new aPrintRacun(sifra,sifraradnika,brojsasije);

	}     
    else {
         JOptionPane.showMessageDialog(this, "Unesi prvo broj naloga");
		 t[0].setSelectionStart(0);
		 t[0].requestFocus();
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
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
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
//--------------------------------------------------------------------------
   private String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
	public void FindRecord(int _koji) {
	  Statement statement = null;
	  int plac,rekl;
      try {
         statement = connection.createStatement();
		 if ( !t[0].getText().equals( "" )) {
			String query = "SELECT * FROM rnal WHERE rbr=" + _koji;

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		         	t[0].setText(rs.getString("rbr"));
		         	t[1].setText(konvertujDatumIzPodataka(rs.getString("datum")));
		         	t[2].setText(rs.getString("brsasije"));
		         	sifRadnika.setText(rs.getString("jmbg"));
					if (sifRadnika.getText().trim().length() == 0)
					{
						sifRadnika.setText("0");
					}
		         	zaglavlje.setText(rs.getString("opiskvara"));
		         	podnozje.setText(rs.getString("opisradova"));
					delovi.setText(rs.getString("delovi"));
					regbroj = rs.getString("regbroj");
		         	napomena.setText(rs.getString("napomena"));
					izmena = true;
					//sifraradnika = Integer.parseInt(sifRadnika.getText().trim());
					t[1].setSelectionStart(0);
					t[1].requestFocus();
				}else{
					//zauzmiRedniBroj(Integer.parseInt(t[0].getText().trim()));
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		         	//JOptionPane.showMessageDialog(this, "Ne postoji nalog");
					//novi.requestFocus();
				}
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, "Greska u prikazi nalog:"+sqlex);
		      }
		 }     
         else {
            JOptionPane.showMessageDialog(this, "Broj naloga nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u prikazi nalog:"+sqlex);
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
   private boolean proveriDatum(String _datum){

		String datum;
		int god,mes,dan;
		datum = _datum;
		boolean ispravan = false;
		dan = Integer.parseInt(datum.substring(0,2));
		mes = Integer.parseInt(datum.substring(3,5));
		god = Integer.parseInt(datum.substring(6,10));

		if (datum.length() != 10)
			JOptionPane.showMessageDialog(this, "Neispravna du\u017eina datuma");
		else if (god < 1900 || god > 2100)
			JOptionPane.showMessageDialog(this, "Neispravna godina");
		else if (mes < 1 || mes > 12)
			JOptionPane.showMessageDialog(this, "Neispravan mesec");
		else if (dan < 1 || dan > 31)
			JOptionPane.showMessageDialog(this, "Neispravan dan");
		else{
			ispravan = true;
			}
	return ispravan;
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
	private void TraziRecord(){
		int kojinalog;
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
    public static void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(150);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(80);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(80);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Ugradjeni delovi") );

	   	qtbl = new QTMRNal(connection);
		String sql;

		sql = "SELECT * FROM mprom1 WHERE rnal=9999999 ORDER BY sifm";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(150);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(80);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(80);

	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		ptbl.setBounds(0,410,400,200);
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = Integer.parseInt(String.valueOf(podaci.get(kojirec)));
		FindRecord(koji);
	}
//--------------------------------------------------------------------------------------
    public boolean proveriBrsasije(){ 
		String queryy;
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[2].getText().equals( "" )) {
				queryy = "SELECT * FROM vozila WHERE brsasije='" +
		            t[2].getText() + "'";
				try {
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						provera = true;
						regbroj = rs.getString("regbroj");
					    zaglavlje.requestFocus();
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji vozilo prvo ubacite podatke o vozilu");
						t[2].setText("");
						t[2].setSelectionStart(0);
						t[2].requestFocus();
					}
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji vozilo:" + sqlex);
					t[2].setText("");
					t[2].setSelectionStart(0);
					t[2].requestFocus();
				}
			}     
            else {
            JOptionPane.showMessageDialog(this, "Magacin nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju vozila" + sqlex);
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
    public boolean proveriRadnika(){ 
		String queryy;
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !sifRadnika.getText().equals( "" )) {
				queryy = "SELECT * FROM radnici WHERE jmbg='" +
		            sifRadnika.getText().trim() + "'";
				try {
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						lblRadnikPrik.setText(rs.getString("imeprezime"));
						provera = true;
						zaglavlje.requestFocus();
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji serviser morate ga otvoriti");
						sifRadnika.setText("");
						sifRadnika.setSelectionStart(0);
						sifRadnika.requestFocus();
					}
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji serviser");
					sifRadnika.setText("");
					sifRadnika.setSelectionStart(0);
					sifRadnika.requestFocus();
				}
			}     
            else {
            JOptionPane.showMessageDialog(this, "Serviser nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju servisera" + sqlex);
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
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

}
//------------------------------------------------------------------------------------------------------------------
	public void AkcijaT(JTextArea e) {
		JTextArea source;
		source = e;

}
//------------------------------------------------------------------------------------------------------------------
	public void AkcijaC(JCheckBox e) {
		JCheckBox source;
		source = e;
				if (source == check1){
					check2.requestFocus();
				}
				else if (source == check2){
					list.requestFocus();
				}
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				koji = Integer.parseInt(t[0].getText().trim());
			}
	}
//----------------------------------------------------------------------------
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
		/*
		else if (source == check2){
				if (izmena)
				{
					izmeni.requestFocus();
				}
				else{
					unesi.requestFocus();
				}
		}
		*/
	}
}//end of class ML
//===========================================================================
 class QTMRNal extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"\u0160ifra","Naziv dela","Koli\u010dina","Cena"};
//------------------------------------------------------------------------------------------------------------------
   public QTMRNal(Connection dbc){
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
               record[0] = rs.getString("sifm");
               record[1] = rs.getString("nazivm");
               record[2] = rs.getString("kolic");
               record[3] = rs.getString("cena");
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
 }//end of class QTMRNal
}// end of class pravna ====================================================================

