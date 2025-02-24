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


public class aUnosKvarova extends JInternalFrame implements InternalFrameListener
			{
	private QTMRNal qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private Vector podaciList = new Vector();
	public static boolean izmena = false;
	private String date;
	private int koji,obradjeno=0;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni,racun;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj,sifRadnika,brDokumenta,txtKartonMasine;
    public static JFormattedTextField txtKontrolisao,txtDatumrada,txtRadio,txtDrugiDokument;
	private JTextArea zaglavlje,podnozje,delovi,napomena;
   	public static JLabel  l[],lblRadnikPrik,lblMasinaPrik;
   	private int n_fields,kontorep,prvisledeci=0,sifraradnika=0;
	private boolean uminus;
	private JCheckBox check1,check2;
	private JList list;
	public static String regbroj=" ";
	private JScrollPane jspane1,jspane2,jspane3,jspane4;
	private aPanelDelovi panelpregled;


//------------------------------------------------------------------------------------------------------------------
    public aUnosKvarova() {
		super("", true, true, true, true);
        setTitle("Unos kvarova ma\u0161ina                     F9-\u0160ifarnici");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );
		pPre = new String("1");
		nazivPre = new String("");

		uzmiKonekciju();

		panelpregled = new aPanelDelovi(connection);

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		buildTable();
		main.add(buildPanel(), BorderLayout.CENTER);
		getContentPane().add(main);
		pack();
		setSize(700,800);
		centerDialog();
		novaStavka();

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
		sirinaL = 100;			//sirina labele
		visina = 25;			//visina komponente
		prviLX = 10;			//X-pozicija za prvi red labela
		prviTX = 30 + sirinaL;	//X-pozicija za prvi red text-box
		drugiLX = 360;
		drugiTX = drugiLX + sirinaL + 10;
		treciLX = 450;
		treciTX = treciLX + sirinaL + 20;
		razmakY = 3;			// razmak izmedju komponenti po visini
		txt = 10;				// vrednost jedinicnog razmaka u txt - polju
		btnY = 540;
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
		l[0].setFont(new Font("Arial",Font.PLAIN,12));
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setFont(new Font("Arial",Font.PLAIN,12));
			t[0].setBounds(prviTX,aa,10*txt,visina);
		t[0].setText(String.valueOf(prvisledeci));
		t[0].setSelectionStart(0);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		
        //*************************************************************
		fmm = "********************";
		JLabel lblBrdokumenta = new JLabel("Broj dokumenta:");
			lblBrdokumenta.setBounds(prviLX+420,aa,sirinaL,visina);
		lblBrdokumenta.setFont(new Font("Arial",Font.PLAIN,12));
        brDokumenta = new JFormattedTextField(createFormatter(fmm,3));
		brDokumenta.setFont(new Font("Arial",Font.PLAIN,12));
			brDokumenta.setBounds(prviTX+420,aa,12*txt,visina);
		brDokumenta.setSelectionStart(0);
        brDokumenta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        brDokumenta.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(brDokumenta);}});
		//**************************************************************
		fmm = "********************";
		JLabel lblDrugiDokument = new JLabel("Veza sa drugim dok.:");
			lblDrugiDokument.setBounds(prviLX+420,aa + visina + razmakY,sirinaL+30,visina);
		lblDrugiDokument.setFont(new Font("Arial",Font.PLAIN,12));
        txtDrugiDokument = new JFormattedTextField(createFormatter(fmm,3));
		txtDrugiDokument.setFont(new Font("Arial",Font.PLAIN,12));
			txtDrugiDokument.setBounds(prviTX+420,aa + visina + razmakY,12*txt,visina);
		txtDrugiDokument.setSelectionStart(0);
        txtDrugiDokument.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtDrugiDokument.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtDrugiDokument);}});

				fmm = "********************";
		JLabel lblKarticaMasine = new JLabel("Karton ma\u0161ine al.:");
			lblKarticaMasine.setBounds(prviLX+420,aa + 2*(visina + razmakY),sirinaL+30,visina);
		lblKarticaMasine.setFont(new Font("Arial",Font.PLAIN,12));
        txtKartonMasine = new JFormattedTextField(createFormatter(fmm,3));
		txtKartonMasine.setFont(new Font("Arial",Font.PLAIN,12));
			txtKartonMasine.setBounds(prviTX+420,aa + + 2*(visina + razmakY),12*txt,visina);
		txtKartonMasine.setSelectionStart(0);
        txtKartonMasine.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtKartonMasine.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtKartonMasine);}});
		//**************************************************************************
		
		
		
		fmm = "##/##/####";
		l[1] = new JLabel("Datum :");
			l[1].setBounds(prviLX,aa + visina + razmakY,sirinaL,visina);
		l[1].setFont(new Font("Arial",Font.PLAIN,12));
        t[1] = new JFormattedTextField(createFormatter(fmm,4));
		t[1].setFont(new Font("Arial",Font.PLAIN,12));
			t[1].setBounds(prviTX,aa + visina + razmakY,10*txt,visina);
		t[1].setText(date);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "*****";
        l[2] = new JLabel("\u0160ifra ma\u0161ine:");
 			l[2].setBounds(prviLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[2].setFont(new Font("Arial",Font.PLAIN,12));
		t[2] = new JFormattedTextField(createFormatter(fmm,1));
		t[2].setFont(new Font("Arial",Font.PLAIN,12));
			t[2].setBounds(prviTX,aa + 2*(visina + razmakY),10*txt,visina);
		t[2].setSelectionStart(0);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[2].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziVozila();}});

		fmm = "***************";
        
		lblRadnik = new JLabel("Radnik-operater :");
 			lblRadnik.setBounds(prviLX,aa + 3*(visina + razmakY),sirinaL,visina);
		lblRadnik.setFont(new Font("Arial",Font.PLAIN,12));
		sifRadnika = new JFormattedTextField(createFormatter(fmm,3));
		sifRadnika.setFont(new Font("Arial",Font.PLAIN,12));
			sifRadnika.setBounds(prviTX,aa + 3*(visina + razmakY),10*txt,visina);
		sifRadnika.setSelectionStart(0);
        sifRadnika.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        sifRadnika.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(sifRadnika);}});
        sifRadnika.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        sifRadnika.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziRadnike();}});


		//***********  ovo se ne koristi *****************************************
		fmm = "************";
        l[3] = new JLabel("Cena radova. :");
			l[3].setBounds(drugiLX,aa,sirinaL,visina);
		l[3].setFont(new Font("Arial",Font.PLAIN,12));
        t[3] = new JFormattedTextField(createFormatter(fmm,2));
		t[3].setFont(new Font("Arial",Font.PLAIN,12));
			t[3].setBounds(drugiTX,aa,10*txt,visina);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		l[4] = new JLabel("Cena delova :");
			l[4].setBounds(drugiLX,aa + visina + razmakY,sirinaL,visina);
		l[4].setFont(new Font("Arial",Font.PLAIN,12));
        t[4] = new JFormattedTextField(createFormatter(fmm,2));
		t[4].setFont(new Font("Arial",Font.PLAIN,12));
			t[4].setBounds(drugiTX,aa + visina + razmakY,10*txt,visina);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "*************";
		l[5] = new JLabel("Garancija :");
			l[5].setBounds(drugiLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[5].setFont(new Font("Arial",Font.PLAIN,12));
        t[5] = new JFormattedTextField(createFormatter(fmm,3));
		t[5].setFont(new Font("Arial",Font.PLAIN,12));
			t[5].setBounds(drugiTX,aa + 2*(visina + razmakY),10*txt,visina);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		fmm = "********";
		l[6] = new JLabel("Rabat (%) :");
			l[6].setBounds(drugiLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[6].setFont(new Font("Arial",Font.PLAIN,12));
        t[6] = new JFormattedTextField(createFormatter(fmm,2));
		t[6].setFont(new Font("Arial",Font.PLAIN,12));
			t[6].setBounds(drugiTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});
		//********************************************************************************

		JLabel zag = new JLabel("Opis kvara :");
		zag.setFont(new Font("Arial",Font.PLAIN,12));
		zag.setBounds(prviLX,aa + 3*(visina + razmakY) + 30,sirinaL,visina);
		zaglavlje = new JTextArea();
		zaglavlje.setFont(new Font("Arial",Font.PLAIN,12));
		zaglavlje.setRows(2);
		zaglavlje.setWrapStyleWord(true);
		zaglavlje.setLineWrap(true);
		zaglavlje.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        zaglavlje.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(zaglavlje);}});

		jspane1 = new JScrollPane( zaglavlje );
		jspane1.setBounds(prviTX,aa + 3*(visina + razmakY) + 30,550,50);


		JLabel pod = new JLabel("Opis radova :");
		pod.setFont(new Font("Arial",Font.PLAIN,12));
		pod.setBounds(prviLX,aa + 4*(visina + razmakY) + 55,sirinaL,visina);
		podnozje = new JTextArea();
		podnozje.setRows(4);
		podnozje.setWrapStyleWord(true);
		podnozje.setFont(new Font("Arial",Font.PLAIN,12));
		podnozje.setLineWrap(true);
		podnozje.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        podnozje.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(podnozje);}});
		jspane2 = new JScrollPane( podnozje );
		jspane2.setBounds(prviTX,aa + 4*(visina + razmakY) + 55,550,70);

		
		fmm = "******************************";
		JLabel lblRadio = new JLabel("Lica zad.za int.:");
		lblRadio.setFont(new Font("Arial",Font.PLAIN,12));
		lblRadio.setBounds(prviLX,aa + 6*(visina + razmakY) + 80,sirinaL + 10,visina);
        txtRadio = new JFormattedTextField(createFormatter(fmm,3));
		txtRadio.setFont(new Font("Arial",Font.PLAIN,12));
			txtRadio.setBounds(prviTX,aa + 6*(visina + razmakY) + 80,20*txt,visina);
        txtRadio.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtRadio.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtRadio);}});

		fmm = "##/##/####";
		JLabel lblDatumrada = new JLabel("Datum rada:");
		lblDatumrada.setFont(new Font("Arial",Font.PLAIN,12));
		lblDatumrada.setBounds(prviLX+420,aa + 6*(visina + razmakY) + 80,sirinaL + 10,visina);
        txtDatumrada = new JFormattedTextField(createFormatter(fmm,3));
		txtDatumrada.setFont(new Font("Arial",Font.PLAIN,12));
			txtDatumrada.setBounds(prviTX+420,aa + 6*(visina + razmakY) + 80,10*txt,visina);
        txtDatumrada.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtDatumrada.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtDatumrada);}});

		
		JLabel pod1 = new JLabel("Ugra\u0111eni delovi :");
		pod1.setFont(new Font("Arial",Font.PLAIN,12));
		pod1.setBounds(prviLX,aa + 6*(visina + razmakY) + 110,sirinaL + 10,visina);
		/*
		delovi = new JTextArea();
		delovi.setRows(4);
		delovi.setWrapStyleWord(true);
		delovi.setFont(new Font("Arial",Font.PLAIN,12));
		delovi.setLineWrap(true);
		delovi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        delovi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(delovi);}});
		jspane3 = new JScrollPane( delovi );
		jspane3.setBounds(prviTX,aa + 6*(visina + razmakY) + 120,500,65);
		*/
		
		panelpregled.setBounds(prviTX,aa + 6*(visina + razmakY) + 110,550,150);


		fmm = "******************************";
		JLabel lblKontrolisao = new JLabel("Kontrolisao :");
			lblKontrolisao.setBounds(prviLX,aa + 6*(visina + razmakY) + 270,sirinaL,visina);
		lblKontrolisao.setFont(new Font("Arial",Font.PLAIN,12));
        txtKontrolisao = new JFormattedTextField(createFormatter(fmm,3));
		txtKontrolisao.setFont(new Font("Arial",Font.PLAIN,12));
			txtKontrolisao.setBounds(prviTX,aa + 6*(visina + razmakY)+270,15*txt,visina);
        txtKontrolisao.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtKontrolisao.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtKontrolisao);}});



		JLabel pod2 = new JLabel("Preventivne mere :");
		pod2.setFont(new Font("Arial",Font.PLAIN,12));
		pod2.setBounds(prviLX,aa + 7*(visina + razmakY) + 270,sirinaL+20,visina);
		napomena = new JTextArea();
		napomena.setRows(2);
		napomena.setWrapStyleWord(true);
		napomena.setFont(new Font("Arial",Font.PLAIN,12));
		napomena.setLineWrap(true);
		napomena.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        napomena.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaT(napomena);}});
		jspane4 = new JScrollPane( napomena );
		jspane4.setBounds(prviTX,aa + 7*(visina + razmakY) + 270,550,45);

		//naziv vlasnika vozila
		l[7] = new JLabel("");
			l[7].setBounds(prviTX + 10*txt +5,aa,120,visina);
		l[7].setFont(new Font("Arial",Font.PLAIN,12));
		l[7].setForeground(Color.blue);

		//ime masine
        lblMasinaPrik = new JLabel("");
 			lblMasinaPrik.setBounds(prviTX + 10*txt +5,aa + 2*(visina + razmakY),150,visina);
		lblMasinaPrik.setFont(new Font("Arial",Font.BOLD,12));

		//ime radnika
        lblRadnikPrik = new JLabel("");
 			lblRadnikPrik.setBounds(prviTX + 10*txt +5,aa + 3*(visina + razmakY),120,visina);
		lblRadnikPrik.setFont(new Font("Arial",Font.BOLD,12));

		novi = new JButton("Novi");
		novi.setMnemonic('N');
		novi.setBounds(btnX,btnY,btnsirina,25);
        novi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        novi.getActionMap().put("check", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {NoviPressed();}});
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
		unesi.setBounds(btnX + 1*(btnsirina + btnrazmak),btnY,btnsirina,25);
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {AddRecord();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   AddRecord(); }});

		JButton brisi = new JButton("Bri\u0161i slog");
		brisi.setMnemonic('B');
		brisi.setBounds(btnX + 2*(btnsirina + btnrazmak),btnY,btnsirina,25);
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});

		JButton trazi = new JButton("Materijal");
		trazi.setMnemonic('M');
		trazi.setBounds(btnX + 5*(btnsirina + btnrazmak),btnY,btnsirina + 20,25);
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});

		JButton stampa = new JButton("\u0160tampa");
		stampa.setMnemonic('P');
		stampa.setBounds(btnX + 3*(btnsirina + btnrazmak),btnY,btnsirina,25);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

		racun = new JButton("Izmeni");
		racun.setMnemonic('R');
		racun.setBounds(btnX + 4*(btnsirina + btnrazmak),btnY,btnsirina,25);
        racun.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        racun.getActionMap().put("check", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		racun.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.setBounds(btnX + 5*(btnsirina + btnrazmak),btnY,btnsirina,25);
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});

		JLabel lblCheck1 = new JLabel("Ma\u0161ina:");
		lblCheck1.setFont(new Font("Arial",Font.PLAIN,12));
		lblCheck1.setBounds(prviLX+420,aa + 3*(visina + razmakY),50,visina);
		check1 = new JCheckBox("   ",false);
		check1.setBounds(prviLX+460,aa + 3*(visina + razmakY),30,20);
		check1.setSelected(false);
		check1.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        check1.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaC(check1);}});

		JLabel lblCheck2 = new JLabel("Alat :");
		lblCheck2.setFont(new Font("Arial",Font.PLAIN,12));
		lblCheck2.setBounds(prviLX+540,aa + 3*(visina + razmakY),30,visina);
		check2 = new JCheckBox("   ",false);
		check2.setBounds(prviLX+575,aa + 3*(visina + razmakY),30,20);
		check2.setSelected(false);
		check2.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        check2.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {AkcijaC(check2);}});

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
		p.add(lblBrdokumenta);
		p.add(brDokumenta);
		
		p.add(lblRadnik);
		p.add(sifRadnika);
		p.add(lblRadnikPrik);
		p.add(lblMasinaPrik);
		p.add(lblRadio);
		p.add(txtRadio);
		p.add(lblDatumrada);
		p.add(txtDatumrada);
		p.add(lblDrugiDokument);
		p.add(txtDrugiDokument);
		p.add(lblKarticaMasine);
		p.add(txtKartonMasine);
		p.add(lblCheck1);
		p.add(check1);
		p.add(lblCheck2);
		p.add(check2);

		p.add(zag);
		p.add(jspane1);
		p.add(pod);
		p.add(jspane2);
		p.add(pod1);
		p.add(panelpregled);
		p.add(lblKontrolisao);
		p.add(txtKontrolisao);

		p.add(pod2);
		p.add(jspane4);

		p.add(novi);
		p.add(unesi);
		p.add(brisi);
		p.add(racun);
		p.add(stampa);
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
		
		for(i=0;i<3;i++){
            t[i].setText("");
		}
		t[0].setFocusable(true);
		sifRadnika.setText("");
		lblRadnikPrik.setText("");
		lblMasinaPrik.setText("");
		brDokumenta.setText("");
        l[7].setText("");
		podnozje.setText("");
		zaglavlje.setText("");
		napomena.setText("");
		txtKontrolisao.setText("");
		txtRadio.setText("");
		txtDatumrada.setText("");
		txtDrugiDokument.setText("");
		txtKartonMasine.setText("");
		check1.setSelected(false);
		check2.setSelected(false);
		izmena = false;
		t[1].setText(date);
		panelpregled.popuniTabelu("SELECT * FROM kvarovidelovi WHERE brojkvara=0");
		panelpregled.nulirajPolja();

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
               		String query = "SELECT MAX(rbr) FROM kvarovi";
			try {
	         ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					prvisledeci = rs.getInt(1);
					prvisledeci = prvisledeci + 1;
				}
				else{
					prvisledeci = 1;
				}
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
 			String query = "DELETE FROM kvarovi" +
				" WHERE rbr=" + prvisledeci; 
			izvrsi(query);
}
//------------------------------------------------------------------------------------------------------------------
 public void zauzmiRedniBroj(int _rednibroj) {
		String oop=" ";
		String query = "INSERT INTO kvarovi(rbr,sifra,opiskvara,opisradova,delovi," +
				"napomena,datum)" +
				" VALUES(" + _rednibroj + 
				",0,'" + oop + 
				"','" + oop + "','" + oop + "','" +
				oop + "','2000-01-01')"; 
		izvrsi(query);
 }
//------------------------------------------------------------------------------------------------------------------
 public void UnesiPressed() {
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 )
	{
			AddRecord();
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].setSelectionStart(0);
		t[0].requestFocus();
	}
 }
//------------------------------------------------------------------------------------------------------------------
    public void prikaziVozila() {
		int t=1;
		aMasinePrik mvp = new aMasinePrik(t);
		mvp.setModal(true);
		mvp.setVisible(true);
    }
//------------------------------------------------------------------------------------------------------------------
    public void prikaziRadnike() {
		int t=1;
		aRadniciPrik mvp = new aRadniciPrik(t);
		mvp.setModal(true);
		mvp.setVisible(true);
    }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		int pred,jur,mag,sifra,nalog;
		double kolicina;
		String sql="";

		if (t[0].getText().trim().length() > 0 && zaglavlje.getText().trim().length() > 0)
		{
			sifra = Integer.parseInt(t[0].getText().trim());
			
			sql = "DROP TABLE IF EXISTS tmpkvar";
			izvrsi(sql);
			sql = "CREATE TEMPORARY TABLE tmpkvar like kvarovi";
			izvrsi(sql);
			sql = "INSERT INTO tmpkvar SELECT * FROM kvarovi WHERE kvarovi.rbr=" + t[0].getText().trim();
			izvrsi(sql);
			sql = "ALTER TABLE tmpkvar ADD tip varchar(50) NOT NULL DEFAULT ''";
			izvrsi(sql);
			sql = "ALTER TABLE tmpkvar ADD fabbroj varchar(50) NOT NULL DEFAULT ''";
			izvrsi(sql);
			sql = "ALTER TABLE tmpkvar ADD invbroj varchar(50) NOT NULL DEFAULT ''";
			izvrsi(sql);
			sql = "ALTER TABLE tmpkvar ADD proizvodjac varchar(50) NOT NULL DEFAULT ''";
			izvrsi(sql);
			sql = "ALTER TABLE tmpkvar ADD godizrade varchar(50) NOT NULL DEFAULT ''";
			izvrsi(sql);
			sql = "UPDATE tmpkvar,masine SET tmpkvar.tip=masine.tip,tmpkvar.fabbroj=masine.fabbroj," +
				" tmpkvar.invbroj=masine.invbroj,tmpkvar.proizvodjac=masine.proizvodjac,tmpkvar.godizrade=masine.godizrade " +
				" WHERE tmpkvar.sifra=masine.sifra";
			izvrsi(sql);
			//------------------------------------------- subreport ----------------------------
			sql = "DROP TABLE IF EXISTS tmpkvardelovi";
			izvrsi(sql);
			sql = "CREATE TEMPORARY TABLE tmpkvardelovi like kvarovidelovi";
			//sql = "CREATE TABLE tmpkvardelovi like kvarovidelovi";
			izvrsi(sql);
			sql = "INSERT INTO tmpkvardelovi SELECT * FROM kvarovidelovi WHERE kvarovidelovi.brojkvara=" + t[0].getText().trim();
			izvrsi(sql);
			
			
			
			jPrintKvar pn = new jPrintKvar(connection,t[0].getText().trim());
		}else {
			 JOptionPane.showMessageDialog(this, "Unesi prvo podatke kvara");
			 t[0].setSelectionStart(0);
			 t[0].requestFocus();
		}
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
    public void Racun() {
		int pred,jur,mag,sifra,nalog;
		double kolicina;
		String brojsasije;

	if (t[0].getText().trim().length() != 0 )
	{
		sifra = Integer.parseInt(t[0].getText().trim());
		brojsasije = t[2].getText();
		aPrintRacun pn = new aPrintRacun(sifra,sifraradnika,brojsasije);

	}else {
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
	public void AddRecord() {
		String ddat,dddat,masina=" ",alat=" ";
		Statement statement = null;
		int placeno = 0,reklamacija=0;
		if (check1.isSelected())
		{
			masina="X";
		}
		if (check2.isSelected())
		{
			alat="X";
		}

      try {
         statement = connection.createStatement();

		 if ( t[0].getText().trim().length()>0 && t[1].getText().trim().length()>0
						&& t[2].getText().trim().length()>0) {
				
			ddat =String.valueOf(t[1].getText()).trim();
			dddat = konvertujDatum(ddat);

			String query = "INSERT INTO kvarovi(rbr,datum,sifra,naziv," +
				"sifraradnika,imeprezime,opiskvara,opisradova," +
				"napomena,brojdokumenta,kontrolisao,radio,datumrada,drugidokument,alat,masina,karton)" +
				" VALUES(" +
				Integer.parseInt(t[0].getText().trim()) + ",'" +
				dddat + "'," +
				t[2].getText().trim() + ",'" +
				lblMasinaPrik.getText().trim() + "'," +
				sifRadnika.getText().trim() + ",'" +
				lblRadnikPrik.getText().trim() + "','" +
				zaglavlje.getText() + "','" + 
				podnozje.getText() + "','" + 
				napomena.getText() + "','" + 
				brDokumenta.getText() + "','" +
				txtKontrolisao.getText() + "','" + 
				txtRadio.getText() + "','" +
				konvertujDatum(txtDatumrada.getText()) + "','" +
				txtDrugiDokument.getText().trim() + "','" +
				masina + "','" +
				alat + "','" +
				txtKartonMasine.getText().trim() + "')"; 
			
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je unet");
				String upit = "SELECT * FROM kvarovi ORDER BY rbr DESC";
				popuniTabelu(upit);
				NoviPressed();
			}else {
				JOptionPane.showMessageDialog(this, "Slog nije unet");
				NoviPressed();
            }
        }else{
			 JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");
		}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u AddRecord:" + sqlex);
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
		String ddat,dddat,masina=" ",alat=" ";
	  Statement statement = null;
	  int placeno = 0,reklamacija=0;
		if (check1.isSelected())
		{
			masina="X";
		}
		if (check2.isSelected())
		{
			alat="X";
		}
         
      try {
		 
		 statement = connection.createStatement();
				ddat =String.valueOf(t[1].getText()).trim();
				dddat = konvertujDatum(ddat);
		 if ( t[0].getText().trim().length()>0 && t[1].getText().trim().length()>0
						&& t[2].getText().trim().length()>0) {
               String query = "UPDATE kvarovi SET " +
				"sifra=" + t[2].getText().trim() +
				",sifraradnika=" + sifRadnika.getText().trim() +
				",imeprezime='" + lblRadnikPrik.getText().trim() + "'" +
				",naziv='" + lblMasinaPrik.getText().trim() + "'" +
				",opiskvara='" + zaglavlje.getText() + "'" +
				",opisradova='" + podnozje.getText() + "'" +
				",napomena='" + napomena.getText() + "'" +
				",datum='" + dddat + "'" +
				",brojdokumenta='" + brDokumenta.getText().trim() + "'" +
				",kontrolisao='" + txtKontrolisao.getText().trim() + "'" +
				",radio='" + txtRadio.getText() + "'" +
				",drugidokument='" + txtDrugiDokument.getText() + "'" +
				",karton='" + txtKartonMasine.getText() + "'" +
				",radio='" + txtRadio.getText() + "'" +
				",masina='" + masina + "'" +
				",alat='" + alat + "'" +
				",datumrada='" + konvertujDatum(txtDatumrada.getText().trim()) + "'" +
				" WHERE rbr=" + Integer.parseInt(t[0].getText().trim()); 

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
					String upit = "SELECT * FROM kvarovi ORDER BY rbr DESC";
					popuniTabelu(upit);
					NoviPressed();
				}else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					napomena.setText(query);
				}
         }else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje Nalog");
		 }
		}catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Gre\u0161ka u UpdateRecord:" + sqlex);
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
               	String query = "DELETE FROM kvarovi WHERE rbr=" + 
						Integer.parseInt(t[0].getText().trim());
               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					String upit = "SELECT * FROM kvarovi ORDER BY rbr DESC";
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
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u brisanju" + sqlex);
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
			String query = "SELECT * FROM kvarovi WHERE rbr=" + _koji;

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		         	t[0].setText(rs.getString("rbr"));
		         	t[1].setText(konvertujDatumIzPodataka(rs.getString("datum")));
		         	t[2].setText(rs.getString("sifra"));
		         	sifRadnika.setText(rs.getString("sifraradnika"));
		         	zaglavlje.setText(rs.getString("opiskvara"));
		         	podnozje.setText(rs.getString("opisradova"));
					lblRadnikPrik.setText(rs.getString("imeprezime"));
					lblMasinaPrik.setText(rs.getString("naziv"));
		         	napomena.setText(rs.getString("napomena"));
		         	brDokumenta.setText(rs.getString("brojdokumenta"));
		         	txtKontrolisao.setText(rs.getString("kontrolisao"));
		         	txtRadio.setText(rs.getString("radio"));
		         	txtDrugiDokument.setText(rs.getString("drugidokument"));
		         	txtKartonMasine.setText(rs.getString("karton"));
		         	
					txtDatumrada.setText(konvertujDatumIzPodataka(rs.getString("datumrada")));
					
					if (rs.getString("masina").equals("X"))
					{
						check1.setSelected(true);
					}else{
						check1.setSelected(false);
					}
					if (rs.getString("alat").equals("X"))
					{
						check2.setSelected(true);
					}else{
						check2.setSelected(false);
					}
					panelpregled.popuniTabelu("SELECT * FROM kvarovidelovi WHERE brojkvara=" + _koji);
					//proveriRadnika();
					izmena = true;
					t[1].setSelectionStart(0);
					t[1].requestFocus();
				}else{
					t[1].setSelectionStart(0);
					t[1].requestFocus();
				}
		 }else {
            JOptionPane.showMessageDialog(this, "Broj naloga nije unet");
         }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u prikazi FindRecord:"+sqlex);
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
	}
//------------------------------------------------------------------------------------------------------------------
    public void buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new QTMRNal(connection);
		String sql;

		sql = "SELECT * FROM kvarovi ORDER BY rbr DESC";
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

	   	jspane = new JScrollPane( jtbl );
		jspane.setBounds(20,580,560,180);
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = Integer.parseInt(String.valueOf(podaci.get(kojirec)));
		FindRecord(koji);
	}
//--------------------------------------------------------------------------------------
    public boolean proveriMasinu(){ 
		String queryy;
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !t[2].getText().equals( "" )) {
				queryy = "SELECT * FROM masine WHERE sifra=" + t[2].getText().trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						provera = true;
						lblMasinaPrik.setText(rs.getString("naziv"));
						txtKartonMasine.setText(rs.getString("karton"));
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji ma\u0161ina prvo ubacite podatke");
						t[2].setText("");
						t[2].setSelectionStart(0);
						t[2].requestFocus();
					}
			}else {
				JOptionPane.showMessageDialog(this, "Ma\u0161ina nije uneta");
            }
      }catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriMasinu:" + sqlex);
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
         	if ( sifRadnika.getText().length()>0) {
				queryy = "SELECT * FROM operateri WHERE sifra=" + sifRadnika.getText().trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						lblRadnikPrik.setText(rs.getString("imeprezime"));
						provera = true;
						zaglavlje.requestFocus();
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji operater morate ga otvoriti");
						sifRadnika.setText("");
						sifRadnika.setSelectionStart(0);
						sifRadnika.requestFocus();
					}
			}     
				else {
				JOptionPane.showMessageDialog(this, "Operater-radnik nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriRadnika:" + sqlex);
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
					{	
						JOptionPane.showMessageDialog(this, "Neispravan podatak");
						t[0].setText("");
						t[0].requestFocus();
					}else{
						if (Integer.parseInt(t[0].getText().trim()) < prvisledeci)
						{
							FindRecord(Integer.parseInt(t[0].getText().trim()));
							brDokumenta.setSelectionStart(0);
							brDokumenta.requestFocus();
						}
						else if (Integer.parseInt(t[0].getText().trim()) > prvisledeci)
						{
							JOptionPane.showMessageDialog(this, "Nalog ve\u0107i od narednog");
							t[0].setText(String.valueOf(prvisledeci));
							t[0].setSelectionStart(0);
							t[0].requestFocus();
						}
						else if (Integer.parseInt(t[0].getText().trim()) == prvisledeci)
						{
							brDokumenta.setSelectionStart(0);
							brDokumenta.requestFocus();
						}
					}
				}
				else if (source == brDokumenta){
						t[1].setSelectionStart(0);
						t[1].requestFocus();
				}
				else if (source == t[1]){
					if (proveriDatum(t[1].getText().trim())){
						/*
						if (izmena == false)
						{
							AddRecord();
						}
						*/
						txtDrugiDokument.setSelectionStart(0);
						txtDrugiDokument.requestFocus();
					}else{
						t[1].setSelectionStart(0);
						t[1].setSelectionEnd(10);
						t[1].requestFocus();
					}
				}
				else if (source == txtDrugiDokument){
					if (txtDrugiDokument.getText().trim().length() == 0){
						txtDrugiDokument.setText(" ");
					}
						//t[2].setText("");
						t[2].setSelectionStart(0);
						t[2].requestFocus();
				}
				else if (source == t[2]){
					if (proveriMasinu()){
						txtKartonMasine.setSelectionStart(0);
						txtKartonMasine.requestFocus();
					}else{
						t[2].setText("");
						t[2].setSelectionStart(0);
						t[2].requestFocus();
					}
				}
				else if (source == txtKartonMasine){
					if (txtKartonMasine.getText().trim().length() == 0){
						txtKartonMasine.setText(" ");
					}
						sifRadnika.setSelectionStart(0);
						sifRadnika.requestFocus();
				}
				else if (source == sifRadnika){
					if (sifRadnika.getText().trim().length() == 0){
						sifRadnika.setText("0");
					}
					if (proveriRadnika())
					{
						check1.requestFocus();
					}
				}
				else if (source == txtKontrolisao){
					if (txtKontrolisao.getText().trim().length() == 0){
						txtKontrolisao.setText(" ");
					}
						napomena.requestFocus();
				}




				//ne koristi se *******************************
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0){
						t[3].setText("0");
					}
					t[4].setSelectionStart(0);
					t[4].requestFocus();
				}
				else if (source == t[4]){
					if (t[4].getText().trim().length() == 0){
						t[4].setText("0");
					}
					t[5].setSelectionStart(0);
					t[5].requestFocus();
				}
				else if (source == t[5]){
					if (t[5].getText().trim().length() == 0){
						t[5].setText("0");
					}
					t[6].setSelectionStart(0);
					t[6].requestFocus();
				}
				else if (source == t[6]){
					if (t[6].getText().trim().length() == 0){
						t[6].setText("0");
					}
					zaglavlje.requestFocus();
				}
				else if (source == txtRadio){
					if (txtRadio.getText().trim().length() == 0){
						txtRadio.setText(" ");
					}
					txtDatumrada.requestFocus();
				}
				else if (source == txtDatumrada){
					if (txtDatumrada.getText().trim().length() < 7){
						txtDatumrada.setText(t[1].getText());
						panelpregled.nadjiSledeciRbr();
					}else{
						if (proveriDatum(txtDatumrada.getText().trim()))
						{
							panelpregled.nadjiSledeciRbr();
						}else{
							txtDatumrada.requestFocus();
						}
					}
				}
				//*************************************************
}
//------------------------------------------------------------------------------------------------------------------
	public void AkcijaT(JTextArea e) {
		JTextArea source;
		source = e;

				if (source == zaglavlje){
					podnozje.requestFocus();
				}
				else if (source == podnozje){
					//panelpregled.nadjiSledeciRbr();
					txtRadio.requestFocus();
				}else if (source == napomena){
					if (izmena == true)
					{
						racun.requestFocus();
					}else{
						unesi.requestFocus();
					}
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
					zaglavlje.requestFocus();
				}
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				koji = Integer.parseInt(t[0].getText().trim());
				if (koji < prvisledeci)
				{
					FindRecord(koji);
				}else{
				}
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
	}
}//end of class ML
//===========================================================================
 class QTMRNal extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Rbr","Datum","\u0160ifra ma\u0161ine","Naziv"};
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
               record[0] = rs.getString("rbr");
               record[1] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[2] = rs.getString("sifra");
               record[3] = rs.getString("naziv");
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

