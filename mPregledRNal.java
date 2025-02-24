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


public class mPregledRNal extends JInternalFrame implements InternalFrameListener
			{
	private QTMPregledRNal qtbl;
   	private JTable jtbl;
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
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj,sifRadnika;
	private JTextArea zaglavlje,podnozje,delovi,napomena;
   	public static JLabel  l[],lblRadnikPrik,lblRadnik;
   	private int n_fields,kontorep,prvisledeci=0,sifraradnika=0;
	private boolean uminus;
	private JCheckBox check1,check2;
	private JList list;

//------------------------------------------------------------------------------------------------------------------
    public mPregledRNal() {
		super("", true, true, true, true);
        setTitle("Pregled radnih naloga    F9-\u0160ifarnici");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );
		pPre = new String("1");
		nazivPre = new String("");
		//kontorep = IzborPre.kontorep;
		//uminus = IzborPre.uminus;

		uzmiKonekciju();
		//sifraradnika = aLogin.UserSifra;

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		buildTable();
		main.add(buildPanel(), BorderLayout.CENTER);
		getContentPane().add(main);
		pack();
		setSize(700,600);
		centerDialog();
		//novaStavka();

		UIManager.addPropertyChangeListener(new UISwitchListener(main));
		//t[0].setFocusable(false);
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

		// setBounds(X,Y,sirina,visina)
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
		t[1].setText(date);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "*************************";
        l[2] = new JLabel("Br.\u0161asije :");
 			l[2].setBounds(prviLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[2].setFont(new Font("Arial",Font.PLAIN,10));
		t[2] = new JFormattedTextField(createFormatter(fmm,3));
		t[2].setFont(new Font("Arial",Font.PLAIN,12));
			t[2].setBounds(prviTX,aa + 2*(visina + razmakY),15*txt,visina);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[2].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziVozila();}});

		fmm = "*****";
        
		lblRadnik = new JLabel("Serviser :");
 			lblRadnik.setBounds(prviLX,aa + 3*(visina + razmakY),sirinaL,visina);
		lblRadnik.setFont(new Font("Arial",Font.PLAIN,10));
		sifRadnika = new JFormattedTextField(createFormatter(fmm,3));
		sifRadnika.setFont(new Font("Arial",Font.PLAIN,12));
			sifRadnika.setBounds(prviTX,aa + 3*(visina + razmakY),8*txt,visina);
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
		pod1.setBounds(prviLX,aa + 5*(visina + razmakY) + 90,sirinaL,visina);
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

		//naziv proizvoda-----
		l[7] = new JLabel("");
			l[7].setBounds(prviTX + 10*txt +5,aa,120,visina);
		l[7].setFont(new Font("Arial",Font.PLAIN,10));
		l[7].setForeground(Color.blue);
		//ime servisera
        lblRadnikPrik = new JLabel("");
 			lblRadnikPrik.setBounds(prviTX + 10*txt +5,aa + 3*(visina + razmakY),120,visina);
		lblRadnikPrik.setForeground(Color.blue);


		unesi = new JButton("\u0160tampa prazan");
		unesi.setMnemonic('U');
		unesi.setBounds(btnX + btnsirina + btnrazmak,btnY,btnsirina + 30,20);
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   StampaPrazan(); }});

		novi = new JButton("Novi slog");
		novi.setMnemonic('N');
		novi.setBounds(btnX,btnY,btnsirina,20);
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});

		izmeni = new JButton("Unesi");
		izmeni.setMnemonic('U');
		izmeni.setBounds(btnX + 1*(btnsirina + btnrazmak),btnY,btnsirina,20);
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});

		JButton brisi = new JButton("Bri\u0161i slog");
		brisi.setMnemonic('B');
		brisi.setBounds(btnX + 2*(btnsirina + btnrazmak),btnY,btnsirina,20);
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});

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

		JButton racun = new JButton("Ra\u010dun");
		racun.setMnemonic('R');
		racun.setBounds(btnX + 4*(btnsirina + btnrazmak),btnY,btnsirina,20);
		racun.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Racun(); }});

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
            p.add(t[i]); }
		p.add(lblRadnik);
		p.add(sifRadnika);
		p.add(lblRadnikPrik);
		for(i=3;i<7;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
	    for(i=7;i<8;i++){ 
            p.add(l[i]);} 
		p.add(zag);
		p.add(zaglavlje);
		p.add(pod);
		p.add(podnozje);
		p.add(pod1);
		p.add(delovi);
		p.add(pod2);
		p.add(napomena);
		p.add(lblCheck);
		p.add(check1);
		p.add(lblCheck2);
		p.add(check2);
		p.add(lista);
		p.add(list);

		p.add(unesi);
		p.add(novi);
		p.add(stampa);
		p.add(racun);
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
        for(i=0;i<7;i++){
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
		//novaStavka();
		t[0].setSelectionStart(0);
		t[0].requestFocus();
    }
 //---------------------------------------------------------------------------
 public void UnesiPressed() {
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 && t[3].getText().trim().length() != 0 &&
		t[4].getText().trim().length() != 0)
	{
			//AddRecord();
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
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
		int t=2;
		fKupciPrik mvp = new fKupciPrik(connection,t);
		mvp.setModal(true);
		mvp.setVisible(true);
    }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		int pred,jur,mag,sifra,nalog;
		double kolicina;
		String brojsasije;

	if (t[0].getText().trim().length() != 0 )
	{
		sifra = Integer.parseInt(t[0].getText().trim());
		brojsasije = t[2].getText();
		aPrintNalog pn = new aPrintNalog(sifra,sifraradnika,brojsasije);

	}     
    else {
         JOptionPane.showMessageDialog(this, "Unesi prvo broj naloga");
		 t[0].requestFocus();
    }
}
//------------------------------------------------------------------------------------------------------------------
    public void StampaPrazan() {
		aPrintNalogP pn = new aPrintNalogP();
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
	public void UpdateRecord() {
		String ddat,dddat;
	  Statement statement = null;
	  int placeno = 0,reklamacija=0;
	  if (check1.isSelected() == true)
	  {
		  placeno = 1;
	  }
      try {
	  if (check2.isSelected() == true)
	  {
		  reklamacija = 1;
	  }
         statement = connection.createStatement();
				ddat =String.valueOf(t[1].getText()).trim();
				dddat = konvertujDatum(ddat);
		 if ( !t[0].getText().equals( "" )) {
               String query = "UPDATE rnal SET " +
				"brsasije='" + t[2].getText() + "'" +
				",cenarada=" + Double.parseDouble(t[3].getText().trim()) +  
				",cenadelova=" + Double.parseDouble(t[4].getText().trim()) +  
				",rabat=" + Double.parseDouble(t[6].getText().trim()) +  
				",garancija='" + t[5].getText() + "'" + 
				",opiskvara='" + zaglavlje.getText() + "'" +
				",opisradova='" + podnozje.getText() + "'" +
				",napomena='" + napomena.getText() + "'" +
				",delovi='" + delovi.getText() + "'" +
				",placeno=" + placeno + 
				",reklamacija=" + reklamacija + 
				" WHERE rbr=" + Integer.parseInt(t[0].getText().trim()) + 
				 " AND sifra=" + sifraradnika;

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
					JOptionPane.showMessageDialog(this, "Slog je izmenjen");
					String upit = "SELECT rbr,sifra,datum,brsasije,ROUND(cenarada,2),ROUND(cenadelova,2)," +
					"garancija,placeno FROM rnal ORDER BY rbr DESC";
					popuniTabelu(upit);
					NoviPressed();
				}     
				else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					//JOptionPane.showMessageDialog(this, query);
					napomena.setText(query);
					//NoviPressed();
				}
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje Nalog");
		 }
		}
		catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u izmeni");
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
	if (obradjeno == 0)
	{
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         if ( !t[0].getText().equals( "" ) ) {
               	String query = "DELETE FROM rnal WHERE rbr=" + 
						Integer.parseInt(t[0].getText().trim()) +
					" AND sifra=" + sifraradnika; 
               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					JOptionPane.showMessageDialog(this, "Slog je izbrisan");
					String upit = "SELECT rbr,sifra,datum,brsasije,ROUND(cenarada,2),ROUND(cenadelova,2)," +
					"garancija,placeno FROM rnal ORDER BY rbr DESC";
					popuniTabelu(upit);

					NoviPressed();
				}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne mo\u017ee izbrisati");
               		NoviPressed();
            	}
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje Nalog");
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
	}else{
		JOptionPane.showMessageDialog(this, "Ne moze se brisati obradjen nalog");
	}
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
		         	t[3].setText(rs.getString("cenarada"));
		         	t[4].setText(rs.getString("cenadelova"));
		         	t[5].setText(rs.getString("garancija"));
		         	t[6].setText(rs.getString("rabat"));
		         	sifRadnika.setText(rs.getString("sifra"));
		         	zaglavlje.setText(rs.getString("opiskvara"));
		         	podnozje.setText(rs.getString("opisradova"));
					delovi.setText(rs.getString("delovi"));
		         	napomena.setText(rs.getString("napomena"));
					plac = rs.getInt("placeno");
					rekl = rs.getInt("reklamacija");
					sifraradnika = rs.getInt("sifra");
					if (plac == 1)
					{
						check1.setSelected(true);
					}else{
						check1.setSelected(false);
					}
					if (rekl == 1)
					{
						check2.setSelected(true);
					}else{
						check2.setSelected(false);
					}
					izmena = true;
					proveriBrsasije();
					t[1].setSelectionStart(0);
					t[1].setSelectionEnd(15);
					t[1].requestFocus();
				}else{
					t[0].requestFocus();
				}
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
		 }     
         else {
            JOptionPane.showMessageDialog(this, "Saradnik nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u trazenju");
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
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(80);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(80);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(80);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(80);
	}
//------------------------------------------------------------------------------------------------------------------
    public void buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new QTMPregledRNal(connection);
		String sql;

		sql = "SELECT rbr,sifra,datum,brsasije,ROUND(cenarada,2),ROUND(cenadelova,2)," +
			"garancija,placeno FROM rnal ORDER BY rbr DESC";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(80);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(80);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(80);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(80);

	   	jspane = new JScrollPane( jtbl );
		jspane.setBounds(20,400,560,150);
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
						l[7].setText(rs.getString("ime"));
						provera = true;
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji vozilo prvo ubacite podatke o vozilu");
					}
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji vozilo");
				}
			}     
            else {
            JOptionPane.showMessageDialog(this, "Magacin nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju vozila");
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

				if (source == t[0]){
					if (t[0].getText().trim().length() == 0)
					{	JOptionPane.showMessageDialog(this, "Neispravan podatak");
						t[0].setText("");
						t[0].requestFocus();}
					else{
						koji = Integer.parseInt(t[0].getText().trim());
						FindRecord(koji);
					}
				}
				else if (source == t[1]){
					if (proveriDatum(String.valueOf(t[1].getText()).trim()) == true){
					if (izmena == false)
					{
						//AddRecord();
					}
					t[2].setSelectionStart(0);
					t[2].setSelectionEnd(10);
					t[2].requestFocus();}
					else{
					t[1].setSelectionStart(0);
					t[1].setSelectionEnd(10);
					t[1].requestFocus();
				}
				}
				else if (source == t[2]){
					if (proveriBrsasije()){
						sifRadnika.setSelectionStart(0);
						sifRadnika.setSelectionEnd(10);
						sifRadnika.requestFocus();
					}else{
						t[2].setText("");
						t[2].setSelectionStart(0);
						t[2].requestFocus();
					}
				}
				else if (source == sifRadnika){
					if (sifRadnika.getText().trim().length() == 0){
						sifRadnika.setText("0");
					}
					t[3].setSelectionStart(0);
					t[3].setSelectionEnd(20);
					t[3].requestFocus();
				}
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0){
						t[3].setText("0");
					}
					t[4].setSelectionStart(0);
					t[4].setSelectionEnd(20);
					t[4].requestFocus();
				}
				else if (source == t[4]){
					if (t[4].getText().trim().length() == 0){
						t[4].setText("0");
					}
					t[5].setSelectionStart(0);
					t[5].setSelectionEnd(20);
					t[5].requestFocus();
				}
				else if (source == t[5]){
					if (t[5].getText().trim().length() == 0){
						t[5].setText("0");
					}
					t[6].setSelectionStart(0);
					t[6].setSelectionEnd(20);
					t[6].requestFocus();
				}
				else if (source == t[6]){
					if (t[6].getText().trim().length() == 0){
						t[6].setText("0");
					}
					zaglavlje.requestFocus();
				}
}
//------------------------------------------------------------------------------------------------------------------
	public void AkcijaT(JTextArea e) {
		JTextArea source;
		source = e;

				if (source == zaglavlje){
					podnozje.requestFocus();
				}
				if (source == podnozje){
					delovi.requestFocus();
				}
				if (source == delovi){
					napomena.requestFocus();
				}
				else if (source == napomena){
					check1.requestFocus();
				}
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
					FindRecord(koji);
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
 class QTMPregledRNal extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Rbr","Radnik","Datum","Br.\u0161asije","Cena rada","Cena delova",
						"Garancija","Pla\u0107eno"};
//------------------------------------------------------------------------------------------------------------------
   public QTMPregledRNal(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 8; }
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

               Object[] record = new Object[8];
               record[0] = rs.getString("rbr");
               record[1] = rs.getString("sifra");
               record[2] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[3] = rs.getString("brsasije");
               record[4] = rs.getBigDecimal("ROUND(cenarada,2)");
               record[5] = rs.getBigDecimal("ROUND(cenadelova,2)");
               record[6] = rs.getString("garancija");
               record[7] = rs.getString("placeno");
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
 }//end of class QTMPregledRNal
}// end of class pravna ====================================================================

