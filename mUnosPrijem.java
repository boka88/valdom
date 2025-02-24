//program za unos materijalog prijemnica
//---------------------------------------------------------------------------------------------
//NOVO
//cena - MAGACINSKA CENA (cena dobavljaca bez PDV + zavisni troskovi)
//pcena1 - cena dobavljaca bez PDV
//pcena - Veleprodajna cena 
//pvredn=kolic*pcena1  - vrednost dobavljaca bez PDV
//vredn=kolic*cena -Magacinska Vrednost
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
import javax.swing.JFormattedTextField.*;
import java.lang.*;
import javax.swing.table.*;
import java.util.*;
import java.util.Set.*;
import java.text.*;
import javax.swing.KeyStroke.*;
import bnband.util.NumberUtil;

public class mUnosPrijem extends JInternalFrame implements InternalFrameListener {

	private JButton novi,unos,brisi;
	private int brnal;
	private int jurr,magr,kontor,sifmr;
	public static JFormattedTextField dispMT,dispKol,dispCen,dispSaradnik,mozenemoze,mozenemoze1;
	private Connection connection = null;
	public static JFormattedTextField displej,dispVrsDok;
    public static FormField fmtDatNal,t[],tt[],ttt[],valuta;
    private JLabel l[],lab[];
    int n_fields;
	private int rbr , preneto;
	private JScrollPane jspane;
	private Vector<Object> rbrpodaci = new Vector<Object>();
	private Vector<Object> podaci = new Vector<Object>();
	private Vector<Object> spodaci = new Vector<Object>();
	private Vector<String> vpodaci = new Vector<String>();
   	private mQTM7 qtbl;
   	private JTable jtbl;
	private boolean imaNemaNalog = false;
	private boolean imaStavku=false;
  	private double ddug,ppot,saldo,dugFzkon,potFzkon,stariDug,stariPot,vrednost,kolicina,prcn,stanje;
	private int brStav,stariKonto,noviKonto,staraSifra,novaSifra,zauzet=0,kontorob;
	private String trenutniDatum,pPre,nazivPre,date,fmm,godina,oznaka="",upit,imebaze,godrada;
	private boolean ff9= false,uminus=true,prenosDaNe=false;
	private String koonto="",brrdok="",saaradnik="",raacun="",barkod="",daatum,maasina="";
	private String pattern = "#########.00",datumnaloga="",mpopdv="802";
	private String pattern1 = "#####.0000000";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);
	private DecimalFormat myFormatter1 = new DecimalFormat(pattern1);
	private String strValuta="",tblpromet="mprom";
	private double kontrolabroja=0.0,pamcen=0.0,pamcen1=0.0;
	private aNadji anadji = null;
	private classBrisanje cllBrisi = null;

//-------------------------------------------------------------------------------------------------
    public mUnosPrijem() {
		super("", true, true, true, true);
        setTitle("Ulaz delova i opreme        <F9> - Sifarnici");
		setMaximizable(true);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new GridLayout(1,1) );
 
		JPanel container = new JPanel();
		container.setLayout( null );

		JPanel cont = new JPanel();
		cont.setLayout( new GridLayout(3,1) );
		cont.setBackground(Color.white);
		cont.setBorder( new TitledBorder("") );
		cont.setBounds(0,0,775,180);

		novi = new JButton("Novi nalog");
		novi.setMnemonic('N');
        novi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        novi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {NoviPressed();}});
		novi.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
			NoviPressed(); }});

		uzmiKonekciju();
		anadji = new aNadji();
		cllBrisi = new classBrisanje();


		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		godina =  date.substring(6,10);


		pPre = new String("1");
		tblpromet = tblpromet + pPre.trim();
		nazivPre = new String("Valdom");
	    imebaze = ConnMySQL.imebaze;
	    godrada=imebaze.substring(4,8);
		kontorob = 1010;

		if (pPre == "")
			pPre = "01";

		cont.add(buildDugPotPanel());
		cont.add(buildDugPPanel());
		cont.add(buildDisplej());

		definisiPoljaZaStavke();

		JPanel contt = new JPanel();
		contt.setLayout( new GridLayout(1,3) );
		contt.setBorder( new TitledBorder("Stavke") );
		contt.setBounds(0,190,775,250);

		contt.add(buildPanel1());
		contt.add(buildPanel2());
		contt.add(buildPanel3());

		container.add(cont);
		container.add(contt);
		String sql;
		container.add(buildTable());

		main.add(container);

		getContentPane().add(main);
		pack();

		setSize(800,650);

		rbr = 1;
		imaNemaNalog = false;
		imaStavku = false;
		brStav = 1;
		String s;

		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));
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
			if (t[0].getText().trim().length() != 0 && 
				t[1].getText().trim().length() != 0 && 
				t[2].getText().trim().length() != 0)
			{
				upit = "update "+tblpromet+" set z = 0 WHERE jur=" +
				Integer.parseInt(t[0].getText().trim()) +
				" AND ui=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) +
				" AND vrdok = 1";
				Oslobodi(upit);
				obrisiPomocniSlog();
			}
			anadji = null;
		}//end if INTERNAL_FRAME_CLOSING
    }
//-------------------------------------------------------------------------------------------------
    public JPanel buildDugPotPanel() {
		JPanel p = new JPanel();
		p.setLayout( new FlowLayout(FlowLayout.LEFT) );
		Color c = new Color(17,203,214);
		p.setBackground(c);

		int i;
        t = new FormField[5]; 
        l = new JLabel[4]; 
		JLabel pred = new JLabel(nazivPre,JLabel.LEFT);
		pred.setForeground(Color.red);

		fmm = "**";
	    l[0] = new JLabel("Rad.jed.:",JLabel.RIGHT);
		t[0] = new FormField(createFormatter(fmm,1));
		t[0].setColumns(2);
		t[0].setText("1");
		//------------------- programiranje provere i akcije ------------------------
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

	
		fmm = "**********";
        l[1] = new JLabel("Magacin:",JLabel.RIGHT);
		t[1] = new FormField(createFormatter(fmm,1));
		t[1].setColumns(3);
		//------------------- programiranje provere i akcije ------------------------
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[1].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziMagacin();}});


		fmm = "**********";
        l[2] = new JLabel("Br.unosa :",JLabel.RIGHT);
		t[2] = new FormField(createFormatter(fmm,1));
		t[2].setColumns(7);
		//------------------- programiranje provere i akcije ------------------------
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "##/##/####";				
        l[3] = new JLabel("Datum unosa:",JLabel.RIGHT);
		fmtDatNal = new FormField(createFormatter(fmm,4));
		fmtDatNal.setColumns(8);
		fmtDatNal.setText(date);
		fmtDatNal.setSelectionStart(0);
		//------------------- programiranje provere i akcije ------------------------
        fmtDatNal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        fmtDatNal.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(fmtDatNal);}});

		JButton stampa = new JButton("Štampa");
		stampa.setMnemonic('P');
		stampa.setBounds(650,5,80,20);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

		fmtDatNal.addFocusListener(new FL());
		int ii=0;
		p.add(novi);
		for(ii=0;ii<3;ii++){ 
            p.add(l[ii]); 
			p.add(t[ii]); 
		}
		p.add(l[3]);
		p.add(fmtDatNal);
		p.add(stampa);

		return p;
  }
//-------------------------------------------------------------------------------------------------
    public JPanel buildDugPPanel() {
		JPanel pp = new JPanel();
		pp.setLayout( new FlowLayout() );
		pp.setBorder( new TitledBorder("") );

		Color c = new Color(17,203,214);
		pp.setBackground(c);

		int i;
        ttt = new FormField[7]; 
        l = new JLabel[7]; 

		fmm = "*****";
        l[3] = new JLabel("Opis:",JLabel.RIGHT);
		ttt[3] = new FormField(createFormatter(fmm,3));
		ttt[3].setColumns(10);
		ttt[3].setEditable(false);
		ttt[3].setText("Ulaz");

		fmm = "***************";
        l[4] = new JLabel("Broj stavki :",JLabel.RIGHT);
		ttt[4] = new FormField(createFormatter(fmm,2));
		ttt[4].setColumns(10);
		ttt[4].setEditable(false);

		JLabel sar = new JLabel("Saradnik:");
		dispSaradnik = new JFormattedTextField(); 
		dispSaradnik.setEditable(false);
		dispSaradnik.setColumns(15);
		dispSaradnik.setFont(new Font("Arial",Font.BOLD,10));

		  
		for(i=3;i<5;i++){ 
      	    pp.add(l[i]); 
            pp.add(ttt[i]); 
		}

		pp.add(sar);
		pp.add(dispSaradnik);
	return pp;
	}
//-------------------------------------------------------------------------------------------------
    public JPanel buildDisplej() {
		JPanel pp = new JPanel();
		pp.setLayout( null );
		Color c = new Color(17,203,214);		
		pp.setBackground(c);
		String fmm="********************";

		JLabel naz = new JLabel("Naz.robe:");
		naz.setBounds(250,5,70,20);
		displej = new JFormattedTextField(createFormatter(fmm,3)); 
		displej.setBounds(320,5,290,20);
		displej.setEditable(false);
		displej.setFont(new Font("Arial",Font.BOLD,12));

		JLabel kon = new JLabel("Konto:");
		kon.setBounds(5,5,40,20);
		dispVrsDok = new JFormattedTextField(createFormatter(fmm,3)); 
		dispVrsDok.setBounds(45,5,150,20);
		dispVrsDok.setEditable(false);
		dispVrsDok.setFont(new Font("Arial",Font.BOLD,10));

		JLabel mt = new JLabel("MTro\u0161:");
		mt.setBounds(5,30,40,20);
		dispMT = new JFormattedTextField(createFormatter(fmm,3)); 
		dispMT.setBounds(45,30,150,20);
		dispMT.setEditable(false);
		dispMT.setFont(new Font("Arial",Font.BOLD,10));

		JLabel kol = new JLabel("Koli\u010dina:");
		kol.setBounds(250,30,70,20);
		dispKol = new JFormattedTextField(createFormatter(fmm,3)); 
		dispKol.setBounds(320,30,100,20);
		dispKol.setEditable(false);
		dispKol.setFont(new Font("Arial",Font.BOLD,12));


		//formatiranje tekst boksa za prikaz decimala----------------------------
		DecimalFormat decimalFormat = new DecimalFormat("0.00000");	// 2 decimale
		NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
		textFormatter.setOverwriteMode(true);
		textFormatter.setAllowsInvalid(false);
		
		fmm = "************";
		JLabel cen = new JLabel("Cena:");
		cen.setBounds(450,30,50,20);
		dispCen = new JFormattedTextField(textFormatter); //formatiranje decimala (textFormatter)
		dispCen.setBounds(510,30,100,20);
		dispCen.setEditable(false);
		dispCen.setFont(new Font("Arial",Font.BOLD,12));

		pp.add(kon);
		pp.add(dispVrsDok);
		pp.add(naz);
		pp.add(displej);
		pp.add(mt);
		pp.add(dispMT);
		pp.add(kol);
		pp.add(dispKol);
		pp.add(cen);
		pp.add(dispCen);

		return pp;
	}
//-----------------------------------------------------------------------------------------------------------
	public void definisiPoljaZaStavke(){
		lab = new JLabel[22]; 

        lab[0]= new JLabel("R.br :         ",JLabel.RIGHT);
        lab[1]= new JLabel("\u0160ifra dela :   ",JLabel.RIGHT);
        lab[2]= new JLabel("Konto :        ",JLabel.RIGHT);
        lab[3]= new JLabel("Datum :        ",JLabel.RIGHT);
        lab[4]= new JLabel("Vrsta dok. :   ",JLabel.RIGHT);
        lab[5]= new JLabel("Broj dok. :    ",JLabel.CENTER);
        lab[6]= new JLabel("Saradnik/Koop: ",JLabel.CENTER);
        lab[7]= new JLabel("\u0160ifra sarad.:  ",JLabel.CENTER);
        lab[8]= new JLabel("Fakturisano/ne:",JLabel.LEFT);
        lab[9]= new JLabel("Ra\u010dun :        ",JLabel.LEFT);
        lab[10]= new JLabel("Uvoz/Nije uvoz:",JLabel.RIGHT);
        lab[11]= new JLabel("Ma\u0161ina :   ",JLabel.RIGHT);
        lab[12]= new JLabel("Koli\u010dina :     ",JLabel.LEFT);
        lab[13]= new JLabel("Cena bez PDV : ",JLabel.LEFT);
        lab[14]= new JLabel("Vredn.bez PDV: ",JLabel.LEFT);
        lab[15]= new JLabel("Tarifni br. :  ",JLabel.LEFT);
        lab[16]= new JLabel("Porez %:       ",JLabel.LEFT);
        lab[17]= new JLabel("Mar\u017ea %:       ",JLabel.LEFT);
        lab[18]= new JLabel("Zavisni trosk%:",JLabel.LEFT);
        lab[19]= new JLabel("Mesec PDV :    ",JLabel.LEFT);
        lab[20]= new JLabel("Velepr. cena : ",JLabel.LEFT);

        tt = new FormField[22]; 

		fmm = "*****";											//rbr-stavke
		tt[0] = new FormField(createFormatter(fmm,1));
		tt[0].setColumns(5);
		//------------------- programiranje provere i akcije ------------------------
        tt[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[0]);}});


		fmm = "*****";											//sifra robe
		tt[1] = new FormField(createFormatter(fmm,1));
		tt[1].setColumns(5);
		tt[1].setText("0");
		tt[1].addFocusListener(new FL());
		tt[1].setSelectionStart(0);
		//------------------- programiranje provere i akcije ------------------------
        tt[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[1]);}});
        tt[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        tt[1].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziRobu();}});


		fmm = "*****";											//Konto
		tt[2] = new FormField(createFormatter(fmm,1));
		tt[2].setColumns(5);
		//------------------- programiranje provere i akcije ------------------------
        tt[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[2]);}});
        tt[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        tt[2].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziKonto();}});


		fmm = "##/##/####";										//datum
		tt[3] = new FormField(createFormatter(fmm,1));
		tt[3].setColumns(8);
		//------------------- programiranje provere i akcije ------------------------
        tt[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[3]);}});


		fmm = "**";												//Vrsta dokumenta
		tt[4] = new FormField(createFormatter(fmm,1));
		tt[4].setColumns(2);
		tt[4].addFocusListener(new FL());
		//------------------- programiranje provere i akcije ------------------------
        tt[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[4]);}});


		fmm = "*****";											//broj dokumenta
        tt[5] = new FormField(createFormatter(fmm,4));
		tt[5].setColumns(5);
		//------------------- programiranje provere i akcije ------------------------
        tt[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[5]);}});


        tt[6] = new FormField(createFormatter(fmm,4));//saradnik
		tt[6].setColumns(5);
		tt[6].addFocusListener(new FL());
		//------------------- programiranje provere i akcije ------------------------
        tt[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[6]);}});


		fmm = "*****";											//Sifra saradnika
		tt[7] = new FormField(createFormatter(fmm,1));
		tt[7].setColumns(5);
		//------------------- programiranje provere i akcije ------------------------
        tt[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[7]);}});
        tt[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        tt[7].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziSarad();}});


		fmm = "*";											//Fakturisano-ne fakturisano
		tt[8] = new FormField(createFormatter(fmm,1));
		tt[8].setColumns(1);
		tt[8].setText("0");
		tt[8].setSelectionStart(0);
		tt[8].addFocusListener(new FL());
		//------------------- programiranje provere i akcije ------------------------
        tt[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[8]);}});


		fmm = "**********";										//Racun
		tt[9] = new FormField(createFormatter(fmm,1));
		tt[9].setColumns(8);
		//------------------- programiranje provere i akcije ------------------------
        tt[9].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[9].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[9]);}});


		fmm = "*";											//Uvoz - izvoz
		tt[10] = new FormField(createFormatter(fmm,2));
		tt[10].setColumns(1);
		tt[10].addFocusListener(new FL());
		//------------------- programiranje provere i akcije ------------------------
        tt[10].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[10].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[10]);}});
		
		fmm = "*****";										//Proizvod
		tt[11] = new FormField(createFormatter(fmm,2));	
		tt[11].setColumns(5);
		//------------------- programiranje provere i akcije ------------------------
        tt[11].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[11].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[11]);}});
        tt[11].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        tt[11].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziMasinu();}});

		fmm = "************";									//Kolicina										
        tt[12] = new FormField(createFormatter(fmm,2));
		tt[12].setColumns(7);
		tt[12].addFocusListener(new FL());
		//------------------- programiranje provere i akcije ------------------------
        tt[12].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[12].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[12]);}});


		fmm = "**********";									//Cena bez PDV
        tt[13] = new FormField(createFormatter(fmm,2));
		tt[13].setColumns(8);
		tt[13].addFocusListener(new FL());
		//------------------- programiranje provere i akcije ------------------------
        tt[13].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[13].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[13]);}});

		fmm = "**************";									//Vrednost bez PDV
        tt[14] = new FormField(createFormatter(fmm,2));
		tt[14].setColumns(10);
		//------------------- programiranje provere i akcije ------------------------
        tt[14].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[14].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[14]);}});

		fmm = "****";											//tarifni broj
        tt[15] = new FormField(createFormatter(fmm,1));
		tt[15].setColumns(2);
		//------------------- programiranje provere i akcije ------------------------
        tt[15].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[15].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[15]);}});

		fmm = "**";											//Porez
        tt[16] = new FormField(createFormatter(fmm,2));
		tt[16].setColumns(2);
		tt[16].setEditable(false);
		//------------------- programiranje provere i akcije ------------------------
        tt[16].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[16].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[16]);}});

		fmm = "**********";											//Marza
        tt[17] = new FormField(createFormatter(fmm,2));
		tt[17].setColumns(8);
		//------------------- programiranje provere i akcije ------------------------
        tt[17].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[17].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[17]);}});

		fmm = "**********";									//Zavisni troskovi
        tt[18] = new FormField(createFormatter(fmm,5));
		tt[18].setColumns(8);
		//------------------- programiranje provere i akcije ------------------------
        tt[18].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[18].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[18]);}});

		fmm = "**";											//Mesec PDV
        tt[19] = new FormField(createFormatter(fmm,1));
		tt[19].setColumns(2);
		//------------------- programiranje provere i akcije ------------------------
        tt[19].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[19].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[19]);}});

		fmm = "**********";									//Magacinska cena
        tt[20] = new FormField(createFormatter(fmm,2));
		tt[20].setColumns(8);
		//------------------- programiranje provere i akcije ------------------------
        tt[20].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[20].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[20]);}});
	

		int ii=0;
	}
//--------------------------------------------------------------------------------------------------
	private JPanel buildPanel1(){
		JPanel p1 = new JPanel();
		p1.setLayout( new munosCLL() );

		mozenemoze = new JFormattedTextField(); 
		mozenemoze.setColumns(50);
		mozenemoze.setEditable(false);
		mozenemoze.setFont(new Font("Arial",Font.PLAIN,9));
		mozenemoze.setText(" ");
		mozenemoze.setVisible(false);

		JLabel moze = new JLabel("      ");
		moze.setVisible(false);

		//dodato polje valuta
		JLabel lblValuta = new JLabel("Valuta:");
		fmm = "##/##/####";										//datum
		valuta = new FormField(createFormatter(fmm,1));
		valuta.setColumns(8);
		//------------------- programiranje provere i akcije ------------------------
        valuta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        valuta.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(valuta);}});

		
		int i;
	    for(i=0;i<4;i++){ 
            p1.add(lab[i]); 
            p1.add(tt[i]); 
		}
		p1.add(lblValuta);
		p1.add(valuta);

	    /*
		for(i=4;i<7;i++){ 
            p1.add(lab[i]); 
            p1.add(tt[i]); 
		}
		*/

		p1.add(moze);
		p1.add(mozenemoze);

		return p1;
	}
//--------------------------------------------------------------------------------------------------
	private JPanel buildPanel2(){
		JPanel p2 = new JPanel();
		p2.setLayout( new munosCLL() );
		mozenemoze1 = new JFormattedTextField(); 
		mozenemoze1.setColumns(50);
		mozenemoze1.setEditable(false);
		mozenemoze1.setFont(new Font("Arial",Font.PLAIN,9));
		mozenemoze1.setText(" ");
		mozenemoze1.setVisible(false);
		JLabel moze = new JLabel("      ");
		moze.setVisible(false);

		int i;
        p2.add(lab[7]); 
        p2.add(tt[7]);
	    for(i=11;i<14;i++){ 
            p2.add(lab[i]); 
            p2.add(tt[i]); 
		}
		p2.add(moze);
		p2.add(mozenemoze1);

		return p2;
	}
//--------------------------------------------------------------------------------------------------
	private JPanel buildPanel3(){
		JPanel p3 = new JPanel();
		p3.setLayout( new munosCLL() );

		unos = new JButton("Upis/Izmena");
		unos.setMnemonic('U');
        unos.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unos.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Upis();}});
		unos.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
			Upis(); }});

		brisi = new JButton("Brisanje");
		brisi.setMnemonic('B');
		brisi.setFocusable(false);
        brisi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        brisi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Brisanje();}});
		brisi.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
			Brisanje(); }});

		JLabel lbl1 = new JLabel("");
		JLabel lbl2 = new JLabel("");

		int i;
		/*
	    for(i=14;i<21;i++){ 
            p3.add(lab[i]); 
            p3.add(tt[i]); 
		}
		*/
        p3.add(lab[14]); 
        p3.add(tt[14]); 
        p3.add(lab[20]); 
        p3.add(tt[20]); 
        p3.add(lbl1); 
        p3.add(lbl2); 
        p3.add(unos); 
        p3.add(brisi); 

		return p3;
	}

//-------------------------------------------------------------------------------------------------
    private void Upis() {
			if (proveriSvaPolja())
			{
				JOptionPane.showMessageDialog(this, "Sva polja moraju biti popunjena");
				tt[0].requestFocus();
			}else{
				jurr = Integer.parseInt(t[0].getText().trim());
				magr = Integer.parseInt(t[1].getText().trim());
				kontor = Integer.parseInt(tt[2].getText().trim());
				sifmr = Integer.parseInt(tt[1].getText().trim());
				double kol;
				kol = Double.parseDouble(tt[12].getText().trim());

				novaStaraStavka();

				novaStavka();
			}
	}
//-------------------------------------------------------------------------------------------------
    private void Brisanje() {
		if (t[0].getText().trim().length()!=0 && t[1].getText().trim().length()!=0 &&
			tt[0].getText().trim().length()!=0 && tt[1].getText().trim().length()!=0)
		{
			//proverava da li je stavka preneta u finansijsko
			//connect,pre,jur,mag,vrdok,brun,rbr)
				//proverava da li je kolicina nulirana
				if(cllBrisi.proveriStavkuRobnoKolic(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												1,
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())))
				{
					cllBrisi.izbrisiStavkuRobno(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												1,
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())
												);
					cllBrisi.preazurirajRbrRobno(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												1,
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())
												);
               		String sqll = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2)," +
					"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok FROM "+tblpromet+" WHERE brun=" +
					Integer.parseInt(t[2].getText().trim()) + 
					" AND ui=0 AND indikator = 0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
					" AND vrdok = 1 AND rbr>0" + 
					" AND pre=" + Integer.parseInt(pPre);
					popuniTabelu(sqll);
					imaStavku = false;
					novaStavka();
				}else{
					JOptionPane.showMessageDialog(this, "Stavka mora prvo biti nulirana(kolicina=0,cena=0)");
				}

		}else{
			JOptionPane.showMessageDialog(this, "Nisu uneti potrebni podaci za brisanje stavke");
			tt[0].requestFocus();
		}
	}
//--------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );

	   	qtbl = new mQTM7(connection);
		String sql;

		sql = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2), " +
			"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok " +
			" FROM "+tblpromet+" WHERE konto=9999";
	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); 
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); 
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
 	   	
		//jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());

	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(30);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		ptbl.setBounds(0,445,775,170);
		return ptbl;
	}
//-------------------------------------------------------------------------------------------------
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height - 50;
		int x = screenSize.width - size.width;
		//this.setLocation(x,y);
		this.setLocation(0,0);
    }
//-------------------------------------------------------------------------------------------------
public void NoviPressed() {
			if (t[0].getText().trim().length() != 0 && 
				t[1].getText().trim().length() != 0 && 
				t[2].getText().trim().length() != 0)
			{
				obrisiPomocniSlog();
				upit = "update "+tblpromet+" set z = 0 WHERE jur=" +
				Integer.parseInt(t[0].getText().trim()) +
				" AND ui=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) +
				" AND vrdok = 1";
				Oslobodi(upit);
			}
        int i;
		rbr = 1;
		for(i=0;i<3;i++){
            t[i].setText("");
            t[i].setEditable(true);
		}
        for(i=0;i<21;i++){
            tt[i].setText("");
           // tt[i].setEditable(true);
		}
        for(i=3;i<5;i++){
            ttt[i].setText("");
		}
		fmtDatNal.setText(date);
		fmtDatNal.setEditable(true);
		displej.setText("");
		dispVrsDok.setText("");
		dispMT.setText("");
		dispKol.setText("");
		dispCen.setText("");
		dispSaradnik.setText("");
		fmtDatNal.setText(date);
		koonto="";
		brrdok="";
		saaradnik="";
		maasina="";
		raacun="";
		imaStavku=false;
		imaNemaNalog = false;
		datumnaloga="";
		String sql = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2), " +
			"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok FROM "+tblpromet+" WHERE konto=9999";
		popuniTabelu(sql);
		fmtDatNal.setSelectionStart(0);
		mozenemoze.setVisible(false);
		mozenemoze1.setVisible(false);
		t[0].setText("1");
		t[0].setSelectionStart(0);
		t[0].requestFocus();
    }
//-----------------------------------------------------------------------------------------------
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
					formatter.setValidCharacters("-0123456789E. ");
					break;
				case 3:
					//za tekstualna polja bez ogranicenja
					break;
				case 4:
					//formater za datume
					formatter.setValidCharacters("0123456789/ ");
					break;
				case 5:
					//formater za iznose sa decimalama bez minusa
					formatter.setValidCharacters("0123456789. ");
					break;
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
	}
//-------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		//AddRecord();
    }
//-------------------------------------------------------------------------------------------------
      public void BrisiPressed() {
        this.setVisible(false);
    	}
//-------------------------------------------------------------------------------------------------
    public void ocistiSlog() {
		int i;
		for(i=0;i<21;i++){
			tt[i].setText("");
		}
		dispMT.setText("");
		dispKol.setText("");
		dispCen.setText("");
		dispSaradnik.setText("");
		oznaka="-";
		valuta.setText("");
		displej.setText("");
		imaStavku = false;
    }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
        this.setVisible(false);
    } 
//-------------------------------------------------------------------------------------------------
  public void Oslobodi(String _upit) {
	  //PROCEDURA ZA OSLOBADJANJE ZAUZETOG NALOGA

	if (zauzet ==0)
	{
		Statement statement = null;
      try {
         statement = connection.createStatement();
					if ( imaNemaNalog )
					{
						statement.executeUpdate( _upit );
					}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u upitu Oslobodi");
		JOptionPane.showMessageDialog(this, upit);
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
  } 
//------------------------------------------------------------------------------------------------------------------
	public void obrisiPomocniSlog() {
	  anadji.obrisiPomocniNultiSlog(connection,Integer.parseInt(pPre),
								Integer.parseInt(t[0].getText().trim()),
								Integer.parseInt(t[1].getText().trim()),
								0,0,
								Integer.parseInt(t[2].getText().trim()),
								" AND vrdok=1");
 }
//-------------------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		connection = aLogin.konekcija;
    }
//-------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(30);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	}
//------------------------------------------------------------------------------------------------
    public String izmeniDatum(String _dat) {
		String dat= new String(_dat);
		String datu;
			datu = dat.charAt(8)+dat.charAt(9)+ "/" + dat.charAt(5)+dat.charAt(6)+ "/" +
			dat.charAt(0)+dat.charAt(1)+dat.charAt(2)+dat.charAt(3);
		return datu;
	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		int _jur,_mag,_konto,_brun,_kojidok=0;

	if (!t[0].getText().trim().equals( "" ) && !t[1].getText().trim().equals( "" )){
		_jur = Integer.parseInt(t[0].getText().trim());
		_mag = Integer.parseInt(t[1].getText().trim());
		if (tt[5].getText().trim().length() == 0)
		{
			_brun = Integer.parseInt(String.valueOf(spodaci.get(0)));
		}else{
			_brun = Integer.parseInt(tt[5].getText().trim());
		}
		if (tt[2].getText().trim().length() == 0)
		{
			_konto = Integer.parseInt(String.valueOf(podaci.get(0)));
		}else{
			_konto = Integer.parseInt(tt[2].getText().trim());
		}

		_kojidok = Integer.parseInt(String.valueOf(vpodaci.get(0)));

	}else{
		_jur = 0;
		_mag = 0;
		_konto = 0;
		_brun = 0;
	}

			//mPrintPrijemnica pp = new mPrintPrijemnica(connection,jurm,magm,brdokm,_vrdokm);
		JInternalFrame pred = new mStampaDokumenata(_jur,_mag,_konto,_brun,_kojidok);
			AutoFrame.desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//---------------------------------------------stavke----------------------------------------------
   public void proveriStavku() {
		Statement statement = null;
      try {
         statement = connection.createStatement();
         if ( !tt[0].getText().equals( "" )) {
               String sql = "SELECT * FROM "+tblpromet+" WHERE" +
			" brun=" + Integer.parseInt(t[2].getText().trim()) + 
			" AND rbr=" + Integer.parseInt(tt[0].getText().trim()) +
			" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
			" AND mag=" + Integer.parseInt(t[1].getText().trim()) +	
			" AND ui=0 AND indikator = 0 "   +
			" AND vrdok = 1" + 
			" AND pre=" + Integer.parseInt(pPre);

		    ResultSet rs = statement.executeQuery( sql );
		    if( rs.next()){
				
				stariKonto = rs.getInt("konto");		//uzima konto stare stavke
				staraSifra = rs.getInt("sifm");		    //uzima staru sifru
				stariDug = rs.getDouble("kolic");		//uzima kolicinu stare stavke
				stariPot = rs.getDouble("vredn");		//uzima vrednost stare stavke
				preneto = rs.getInt("preneto");		    //da li je preneto u finansijsko

				tt[0].setText(rs.getString("rbr"));				//Stavka - rbr
				tt[1].setText(rs.getString("sifm")); 			//Sifra materijala - robe
				tt[2].setText(rs.getString("konto"));			//Konto
				tt[3].setText(konvertujDatumIzPodataka(rs.getString("datum")));	//Datum
				valuta.setText(konvertujDatumIzPodataka(rs.getString("valuta")));//Valuta
				tt[4].setText(rs.getString("vrdok"));			//vrsta dokum.
				tt[5].setText(rs.getString("brdok"));			//Broj dokumenta
				tt[6].setText(rs.getString("sar"));				//Indikator da li je saradnik ili kooperant
				tt[7].setText(rs.getString("kupacbr")); 		//Sifra saradnika
				tt[8].setText(rs.getString("fak"));				//Fakturisano ili ne
				tt[9].setText(rs.getString("brrac"));			//Broj racuna
				tt[10].setText(rs.getString("uvoz")); 			//Uvoz ili nije uvoz
				tt[11].setText(rs.getString("mtros")); 			//Mesto troska
				tt[12].setText(rs.getString("kolic")); 			//Kolicina
				tt[13].setText(rs.getString("pcena1"));			//Cena DOBAVLJACA bez PDV
				tt[14].setText(rs.getString("pvredn"));			//vrednost DOBAVLJACA bez PDV
				tt[15].setText(rs.getString("tarbr")); 			//Tarifni broj
				tt[16].setText(rs.getString("por1"));			//Porez - por1
				tt[17].setText(rs.getString("mar"));			//Marza, 
				tt[18].setText(rs.getString("zav"));			//Zavisni troskovi
				tt[19].setText(rs.getString("mesec"));			//Mesec PDV
				tt[20].setText(rs.getString("pcena"));			//cena VELEPRODAJNA
				vrednost = Double.parseDouble(rs.getString("pvredn"));
				
				if (NumberUtil.strToDoubleDef(rs.getString("mar"),0.0)<0.01)
				{
					tt[17].setText("0");
				}

				koonto = rs.getString("konto");
				brrdok = rs.getString("brdok");
				saaradnik = rs.getString("kupacbr");
				raacun = rs.getString("brrac");
				maasina = rs.getString("mtros");
				
				imaStavku = true;

				proveriSifm();
				//proveriMsta();
				proveriMasinu();
				proveriKonto();
				if (Integer.parseInt(tt[4].getText().trim()) == 1)
				{
					proveriSaradnika();
				}

				tt[1].setSelectionStart(0);
				tt[1].requestFocus();
			}else{
				JOptionPane.showMessageDialog(this, "Ne postoji stavka");
			}

		}else {
				JOptionPane.showMessageDialog(this, "Broj stavke nije unet");
        }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju stavke"+sqlex);
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
//-------------------------------------------------------------------------------------
   public void unesiStavku() {
		Statement statement = null;

       try {
         statement = connection.createStatement();

         if ( !t[0].getText().equals( "" ) && 
            !t[1].getText().equals( "" ) ) {
				String ddat,dddat,vvaluta;
				double cena,vrednost,kol,pdv,zav,pcena,pcena1,pvredn;
				pcena = Double.parseDouble(tt[20].getText().trim());
				kol = Double.parseDouble(tt[12].getText().trim());
				pdv = 20;
				zav = 0;				
				cena = 0;
				cena = Double.parseDouble(tt[13].getText().trim()) + (Double.parseDouble(tt[13].getText().trim()) * zav/100);
				vrednost = cena * kol;
				pcena1= Double.parseDouble(tt[13].getText().trim());
				pvredn = kol * pcena1;
				ddat =String.valueOf(tt[3].getText()).trim();
				vvaluta = konvertujDatum(String.valueOf(valuta.getText()).trim());
				dddat = konvertujDatum(ddat);
				trenutniDatum = ddat;

        String query = "INSERT INTO "+tblpromet+"(pre,jur,mag,ui,indikator,brun,rbr,sifm,konto," +
					"datum,datumnaloga,vrdok,brdok,kupacbr,brrac,fak,sar,kolic,kolicul,pcena,vredn,cena," +
					"pcena1,pvredn,vrtar,tarbr,por1,zav,mar,taksa,uvoz,mesec,mtros," +
					"nazivm,valuta,z,mpopdv)" +
				" VALUES (" + Integer.parseInt(pPre) + "," +		//Pre-1
			Integer.parseInt(t[0].getText().trim())+ "," + 			//jur-2
			Integer.parseInt(t[1].getText().trim())+ ",0,0," + 		//Magacin, ui, indikator-3-4-5
			Integer.parseInt(t[2].getText().trim())+ "," + 			//Broj unosa-6
			Integer.parseInt(tt[0].getText().trim())+ "," + 		//Stavka - rbr-7
			Integer.parseInt(tt[1].getText().trim())+ "," + 		//Sifra materijala - robe-8
			Integer.parseInt(tt[2].getText().trim())+ ",'" + 		//Konto-9
			dddat + "','" + 										//Datum-10
			konvertujDatum(fmtDatNal.getText().trim()) + "'," +		//Datum-naloga
			"1," + 													//vrsta dokum.-11
			//izmenjeno 30-11-2009 da bude isto kao broj unosa
			Integer.parseInt(t[2].getText().trim())+ "," + 			//Broj dokumenta-12
			Integer.parseInt(tt[7].getText().trim())+ "," + 		//Sifra saradnika-13
			Integer.parseInt(t[2].getText().trim())+ "," + 			//Broj racuna-14
			"0," + 													//Fakturisano ili ne-15
			"0," + 													//Indikator da li je saradnik ili kooperant-16
			Double.parseDouble(tt[12].getText().trim())+ "," + 		//Kolicina-17
			Double.parseDouble(tt[12].getText().trim())+ "," + 		//Ulazna kolicina za PRC
            pcena+ "," + 											//Cena bez PDV-a i zav.troskova(Ulazna)-(pcena)-18
			vrednost + "," +										//vrednost bez PDV + zavisni trosak-Magacinska(vredn)-19
			cena + "," +		    								//cena magacinska(BEZ PDV + ZAVISNI TROSAK) -(cena)20
			pcena1 + "," +		                                    //pcena1-Cena sa PDV ALI BEZ ZAVISNIH TROSKOAVA -21
			pvredn + "," +		                                    //Vrednost bez PDV i zavisnih troskova - pvredn 22
			"0," +													//vrtar vrsta poreza
			"1," + 													//Tarifni broj
			"20," + 												//Porez - por1
			"0," +													//Zavisni troskovi
			"0,0," +												//Marza, Taksa
			"0," + 													//Uvoz ili nije uvoz
			"1," + 													//Mesec PDV
			Integer.parseInt(tt[11].getText().trim()) + ",'" + 		//Mesto troska
			displej.getText() + "','" +								//Naziv robe
			vvaluta + "',1,'0')";									//popdv

               int result = statement.executeUpdate( query );
               if ( result == 1 ){
					rbr = rbr + 1;
               		String sqll = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2)," +
					"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok FROM "+tblpromet+" WHERE brun=" +
					Integer.parseInt(t[2].getText().trim()) + 
					" AND ui=0 AND indikator = 0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
					" AND vrdok = 1 AND rbr>0" + 
					" AND pre=" + Integer.parseInt(pPre);
					popuniTabelu(sqll);
					imaStavku = false;
					//novaStavka();
				}     
				else {
					JOptionPane.showMessageDialog(this, "Slog nije unet");
				}
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");
		}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u unosu stavke:"+sqlex);
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
//---------------------------------------------------------------------------
   public void izmeniStavku() {

		Statement statement = null;
      try {
         statement = connection.createStatement();
				String ddat,dddat,dval,ddval,vvaluta;
				ddat = String.valueOf(tt[3].getText().trim());
				dddat = konvertujDatum(ddat);
				vvaluta = konvertujDatum(String.valueOf(valuta.getText()).trim());
			
				double cena,vrednost,kol,pdv,zav,pcena,pcena1,pvredn;
				pcena = Double.parseDouble(tt[20].getText().trim());
				kol = Double.parseDouble(tt[12].getText().trim());
				pdv = 20;
				zav = 0;				
				cena = 0;
				cena = Double.parseDouble(tt[13].getText().trim()) + (Double.parseDouble(tt[13].getText().trim()) * zav/100);
				vrednost = cena * kol;
				pcena1 = Double.parseDouble(tt[13].getText().trim());
				pvredn = kol * pcena1;

				String query = "UPDATE "+tblpromet+" SET " +
				"datum='" + dddat + "'," + 										//Datum
				"datumnaloga='" + konvertujDatum(fmtDatNal.getText().trim()) + "'," +//Datum naloga
				"valuta='" + vvaluta + "'," + 									//Valuta
				"sifm=" + Integer.parseInt(tt[1].getText().trim()) + ", " + 	//sifra robe
				"konto=" + Integer.parseInt(tt[2].getText().trim()) + ", " + 	//konto
				"brdok=" + Integer.parseInt(t[2].getText().trim())+ ", " + 		//Broj dokumenta
				"kupacbr=" + Integer.parseInt(tt[7].getText().trim())+ ", " + 	//Sifra saradnika
				"brrac=" + Integer.parseInt(t[2].getText().trim())+ ", " + 		//Broj racuna
				"preneto1 = 0" + ", " + 										//Preneto u finansijsko
				"kolic=" + Double.parseDouble(tt[12].getText().trim())+ ", " + 	//Kolicina
				"kolicul=" + Double.parseDouble(tt[12].getText().trim())+ ", " + //Ulazna Kolicina
				"cena=" + cena + ", " +	                                        //cena magacinska (bez pdv sa zavisnim troskom)
				"vredn=" + vrednost + ", " +	                                //vrenost magacinska(bez pdv sa zavisnim troskom)
				"pcena=" + pcena + ", " +										//cena prodajna 
				"pcena1=" + pcena1 + ", " +	                                    //cena sa pdv bez zavinog troska 
				"pvredn=" + pvredn + ", " +	                                    //Vrednost bez PDV
				"mtros=" + Integer.parseInt(tt[11].getText().trim()) +	"," +  	//Mesto troska
				"nazivm='" + displej.getText() + "'" + 							//Naziv robe
				" WHERE ui = 0 and indikator = 0 and brun=" + Integer.parseInt(t[2].getText().trim()) + 
				" AND rbr=" + Integer.parseInt(tt[0].getText().trim()) +
				" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
				" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND pre=" + Integer.parseInt(pPre) + " AND vrdok=1";


               int result = statement.executeUpdate( query );

               if ( result == 1 ){
               	String sqll = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2)," +
					"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok FROM "+tblpromet+" WHERE brun=" +
					Integer.parseInt(t[2].getText().trim()) + 
					" AND ui=0 AND indikator = 0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
					" AND vrdok = 1 AND rbr>0" + 
					" AND pre=" + Integer.parseInt(pPre);
				popuniTabelu(sqll);
				imaStavku = false;
				//novaStavka();

			   }
				else {
            		JOptionPane.showMessageDialog(this, "Stavka nije izmenjena");
					NoviPressed();
				}
	  }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u izmeni stavke:" + sqlex);
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
//---------------------------------------------------------------------------
//uzima sledeci broj stavke 
   public void novaStavka(){ 
		vrednost = 0;
         
		 brStav = anadji.mNadjiNovuStavku(connection,Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									Integer.parseInt(t[2].getText().trim()),
									0,0,
									" AND vrdok = 1");
			ocistiSlog();
			tt[0].setText(String.valueOf(brStav));
			tt[0].setSelectionStart(0);
			tt[0].requestFocus();
}
//----------------------------------------------------------------------------
    public void proveriCenaKol(){ 
			stanje = anadji.mNadjiKolicinu(connection,
									Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									Integer.parseInt(tt[1].getText().trim()),
									Integer.parseInt(tt[2].getText().trim()));
			dispKol.setText(myFormatter.format(stanje));	
			dispCen.setText(myFormatter.format(anadji.mNadjiPRC(connection,
									Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									Integer.parseInt(tt[1].getText().trim()),
									Integer.parseInt(tt[2].getText().trim()))));
			if (stanje<0)
			{
				oznaka="+";
			}
	}
//-------------------------------------------------------------------------------------
   public boolean postojiUMprom() {
	   boolean iimaga = false;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 )
	{
               String sql = "SELECT * FROM "+tblpromet+" WHERE jur=" +
			Integer.parseInt(t[0].getText().trim()) +
			" AND ui=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
			" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
			" AND pre=" +Integer.parseInt(pPre) + 
			" AND vrdok = 1 AND z=0";

			try {
		         ResultSet rs = statement.executeQuery( sql );
		         if(rs.next()){
					iimaga = true;
					//String ddat,dddat;
					datumnaloga = konvertujDatumIzPodataka(rs.getString("datumnaloga"));
		         	fmtDatNal.setText( datumnaloga);
				 }else{
		         	//JOptionPane.showMessageDialog(this, "Nalog ne postoji ili je zauzet");
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, "Greska u proveri naloga");
			}
		}     
            else {
            JOptionPane.showMessageDialog(this, "podaci nisu uneti");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proceduri:postojiUMprom");
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
		return iimaga;
  }
//-------------------------------------------------------------------------------------
   public void izmeniDatumNaloga() {
        String sql = "UPDATE "+tblpromet+" SET datumnaloga='" + konvertujDatum(fmtDatNal.getText().trim()) + 
			"'" + " WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
					" AND pre=" +Integer.parseInt(pPre) + 
					" AND ui=0 AND indikator=0 " +
					" AND (vrdok = 1 or vrdok = 2 or vrdok = 55)"; 
		izvrsi(sql);
		daatum = fmtDatNal.getText().trim();
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
			JOptionPane.showMessageDialog(null, "Upit: " + sql);
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
//-------------------------------------------------------------------------------------
   public void prikaziNalog() {
        String sql = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2)," +
					"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok FROM "+tblpromet+" WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
					" AND pre=" +Integer.parseInt(pPre) + 
					" AND ui=0 AND indikator=0 AND rbr>0" +
					" AND vrdok = 1 AND z=0"; 
		popuniTabelu(sql);
		imaNemaNalog = true;
		daatum = fmtDatNal.getText().trim();
		novaStavka();
  }
//-----------------------------------------------------------------------------
//uzima sledeci broj naloga iz zaglavlja za vrstu knjizenja
	public void brNaloga(){ 
			if ( t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0) {
				brnal = anadji.mNadjiBrojUnosa(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												" AND vrdok = 1 ",0,0);
				
				t[2].setText(String.valueOf(brnal));
				t[2].setSelectionStart(0);
				t[2].requestFocus();
			}else {
				JOptionPane.showMessageDialog(this, "Unesi jur i magacin");
			}
}
//-------------------------------------------------------------------------------------
  public void unesiNoviNalog() {
	
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 ){

		anadji.unesiPomocniNultiSlog(connection,Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									0,0,
									Integer.parseInt(t[2].getText().trim()),
									konvertujDatum(fmtDatNal.getText().trim()),
									1);
		imaNemaNalog = true;
        String sqll = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2)," +
					"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok FROM "+tblpromet+" WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
					" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND ui=0 AND indikator=0 AND rbr>0" +
			        " AND vrdok = 1 " + 
					" AND pre=" + Integer.parseInt(pPre);
		popuniTabelu(sqll);
		novaStavka();
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
	}
  }
//-------------------------------------------------------------------------------------
  public void izbrisiZauzetNalog() {
		Statement statement = null;
      try {
		 statement = connection.createStatement();

         if ( !t[0].getText().equals( "" ) && 
            !t[1].getText().equals( "" ) ) {
				String ddat,dddat;
				ddat = String.valueOf(fmtDatNal.getText()).trim();
				dddat = konvertujDatum(ddat);
				trenutniDatum = ddat;
               String query = "DELETE FROM "+tblpromet+" WHERE" +
				" pre=" + Integer.parseInt(pPre) + " AND " +		
				"jur=" + Integer.parseInt(t[0].getText().trim())+ " AND " +		
				"mag=" + Integer.parseInt(t[1].getText().trim())+ " AND " +		
				"ui=0  0,1,0,0,0" +										//ui,indikator,vrdok,sifm,konto,rbr
				Integer.parseInt(t[2].getText().trim())+ ",'" +		//brun
				dddat + "',1)";										//datum,zauzeto - z

               	int result = statement.executeUpdate( query );
               	if ( result == 1 ){
					imaNemaNalog = true;
               		String sqll = "SELECT rbr,sifm,konto,nazivm,datum,brdok,brrac,kupacbr,mtros,ROUND(kolic,2)," +
					"ROUND(pcena,2),ROUND(cena,2),ROUND(vredn,2),vrdok FROM "+tblpromet+" WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
					" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND ui=0 AND indikator=0 AND rbr>0" +
			        " AND vrdok = 1 " + 
					" AND pre=" + Integer.parseInt(pPre);
				popuniTabelu(sqll);
				novaStavka();
				}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog zaglavlja naloga nije unet");
					t[1].requestFocus();
            	}
         }
         else 
		{	JOptionPane.showMessageDialog(this, "Unesi prvo podatke u Vr-k i Brnal");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u unosu mzag");
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
//--------------------------------------------------------------------------------------
    public boolean proveriKonto(){ 
		String queryy;
		boolean provera = false;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !tt[2].getText().equals( "" )) {
				queryy = "SELECT * FROM materijalnakonta WHERE konto=" +
		            Integer.parseInt(tt[2].getText().trim());
				try {
					ResultSet rs = statement.executeQuery( queryy );
					rs.next();
		         		dispVrsDok.setText(String.valueOf(rs.getString("nazivk")));
						provera = true;
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji konto morate ga otvoriti");
         		}
			}     
            else {
				JOptionPane.showMessageDialog(this, "Sifra konta nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju konta");
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
    public void proveriMasinu(){ 
	if (Integer.parseInt(tt[11].getText().trim()) != 0)
	{
		String queryy;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !tt[11].getText().equals( "" )) {
				queryy = "SELECT * FROM masine WHERE sifra=" +
		            Integer.parseInt(tt[11].getText().trim());

					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		dispMT.setText(String.valueOf(rs.getString("naziv")));
						maasina = tt[11].getText().trim();
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji masina morate je otvoriti");
						tt[11].setText("0");
						tt[11].requestFocus();
					}
			}else {
				JOptionPane.showMessageDialog(this, "Masina nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u proveriTrosak");
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
}
//--------------------------------------------------------------------------------------
    public boolean proveriSifm(){ 
		String queryy;
		boolean imanema = false;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !tt[1].getText().equals( "" )) {
				queryy = "SELECT * FROM sifarnikrobe WHERE sifm=" +
		            Integer.parseInt(tt[1].getText().trim());
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		displej.setText(String.valueOf(rs.getString("nazivm")));
						imanema = true;
						tt[2].setText(koonto);
						tt[2].requestFocus();
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji artikal morate ga otvoriti");
						tt[1].setText("");
						tt[1].requestFocus();
					}
			}else {
				JOptionPane.showMessageDialog(this, "Sifra robe nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriSifm" + sqlex);
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
		return imanema;
}
//--------------------------------------------------------------------------------------
    public boolean proveriTarifu(){ 
		boolean proveratarife=false;
		String queryy;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !tt[15].getText().equals( "" )) {
				queryy = "SELECT * FROM tarifnibrojevi WHERE tarb=" +
		            Integer.parseInt(tt[15].getText().trim());
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		proveratarife = true;
						tt[16].setText(rs.getString("por1"));
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji tarifni broj morate ga otvoriti");
					}
			}else {
				JOptionPane.showMessageDialog(this, "Tarifa nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriTarifu:" + sqlex);
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
		return proveratarife;
}
//--------------------------------------------------------------------------------------
    public void proveriSaradnika(){ 
		String queryy;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !tt[7].getText().equals( "" )) {
				queryy = "SELECT * FROM saradnici WHERE kupacbr=" +
		            Integer.parseInt(tt[7].getText().trim());
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		dispSaradnik.setText(String.valueOf(rs.getString("nazivkupca")));
						saaradnik = rs.getString("kupacbr");
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji saradnik morate ga otvoriti");
						tt[7].setText("");
						tt[7].requestFocus();
					}
			} else {
				JOptionPane.showMessageDialog(this, "Saradnik nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriSaradnika:" + sqlex);
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
//--------------------------------------------------------------------------------------
   public void imaNemaNaloga(){
	   if (postojiUMprom() == true)
	   {
			prikaziNalog();
			displej.setText("");
			tt[0].setSelectionStart(0);
			tt[0].requestFocus();
	   }else{
			displej.setText("");
			fmtDatNal.setSelectionStart(0);
			fmtDatNal.requestFocus();
		}

		upit = "update "+tblpromet+" set z = 1 WHERE jur=" +
				Integer.parseInt(t[0].getText().trim()) +
				" AND ui=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) +
				" AND vrdok = 1";
		Oslobodi(upit);
		t[0].setEditable(false);
		t[1].setEditable(false);
		t[2].setEditable(false);

		return;
	}
//---------------------------------------------------------------------------------
   public void proveriUnosStavka(){
	int aa=0;
	if ( tt[0].getText().trim().length() == 0){
		JOptionPane.showMessageDialog(this, "Unesi prvo stavku");
		tt[0].requestFocus();
	}
	else
	{
		aa = Integer.parseInt(tt[0].getText().trim());
		if (aa < brStav)
			proveriStavku();
		else if (aa > brStav){
			JOptionPane.showMessageDialog(this, "Ne moze se povecavati redni br. stavke");
			tt[0].setText(String.valueOf(brStav));
			tt[0].setSelectionStart(0);
			tt[0].requestFocus();}
		else if ( aa == 0){
			JOptionPane.showMessageDialog(this, "Ne moze stavka sa nulom");
			tt[0].setText(String.valueOf(brStav));
			tt[0].setSelectionStart(0);
			tt[0].requestFocus();}
		else if (aa == brStav){
			imaStavku = false;
			tt[3].setText(trenutniDatum);
			tt[1].setSelectionStart(0);
			tt[1].requestFocus();
		}
	}
	return;
   }
//--------------------------------------------------------------------------
   private boolean proveriNazivRadneJedinice(){
	   boolean ima=false;
		Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM radnejedinice WHERE jur=" + 
		            Integer.parseInt(t[0].getText().trim()) +
			   " AND pre=" + Integer.parseInt(pPre);
			try {
		         ResultSet rs = statement.executeQuery( query );
		         rs.next();
				dispVrsDok.setText(rs.getString("nazjur"));
				ima = true;
				t[1].setText("1");
				t[1].setSelectionStart(0);
				t[1].requestFocus();

			  }
		      catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Nema te radne jedinice");
					ima = false;
					t[0].setText("");
					t[0].requestFocus();
			  }
		}     
      catch ( SQLException sqlex ) {
			System.out.println("Greska u trazenju radne jedinice");
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
		return ima;
   }
//--------------------------------------------------------------------------
   private boolean proverimagacin(){
	   boolean ima=false;
		Statement statement = null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM magacini WHERE mag=" + 
		            Integer.parseInt(t[1].getText().trim())+
			   		" AND pre=" + Integer.parseInt(pPre);

			try {
		         ResultSet rs = statement.executeQuery( query );
		         rs.next();
					dispVrsDok.setText(rs.getString("nazivm"));
					ima = true;
					t[2].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Nema tog magacina");
					ima = false;
					t[1].setText("");
					t[1].requestFocus();
		      }
		}     
      catch ( SQLException sqlex ) {
		System.out.println("Greska u trazenju magacina");
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
		return ima;
   }
//--------------------------------------------------------------------------
   public void novaStaraStavka(){
	   
		if (imaStavku == true){
			izmeniStavku();
		}else{
			unesiStavku();
		}
   }
//--------------------------------------------------------------------------
   public void obavestiGresku(Object _koji){
		JOptionPane.showMessageDialog(this, "Mogu se unositi samo brojevi");
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
//--------------------------------------------------------------------------
   public boolean proveriValutu(String _datum){
		boolean ispravan = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date testDate = null;
		java.util.Date testValuta = null;
		java.util.Calendar datummm = null;
		try
		{
			testDate = sdf.parse(tt[3].getText().trim());
			testValuta = sdf.parse(_datum);
		}
		//provera da li je odgovarajuci format datuma
		catch (ParseException e)
		{
			JOptionPane.showMessageDialog(this, "Neispravan format datuma valute"+e);
			return false;
		}
		//provera da li je datum ispravan
		if (!sdf.format(testValuta).equals(_datum))
		{
			JOptionPane.showMessageDialog(this, "Neispravan datum valute");
			return false;
		}else{
			if (Integer.parseInt(_datum.substring(6,10)+
								_datum.substring(3,5)+
								_datum.substring(0,2)) < 
				Integer.parseInt(tt[3].getText().trim().substring(6,10)+
								tt[3].getText().trim().substring(3,5)+
								tt[3].getText().trim().substring(0,2)))
			{
				JOptionPane.showMessageDialog(this, "Valuta ne mo\u017ee biti manja od datuma");
				return false;
			}
		}
		return true;
   }
//-------------------------------------------------------------------------
   public void prikaziRobu(){
		int t = 3;
		if (imaStavku == false)
		{
			mRobaPrik sk = new mRobaPrik(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
		}
   }
//-------------------------------------------------------------------------
   public void prikaziMasinu(){
		int t = 1;
		mMasinePrik sk = new mMasinePrik(connection,t);
		sk.setModal(true);
		sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void prikaziSarad(){
		int t = 3;
		fKupciPrik sk = new fKupciPrik(connection,t);
		sk.setModal(true);
		sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void prikaziKonto(){
		int t = 1;
		if (imaStavku == false)
		{
			mKontPlan sk = new mKontPlan(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
		}
   }
//-------------------------------------------------------------------------
   public void prikaziMagacin(){
		int t = 12;
		mMagPrik sk = new mMagPrik(connection,t);
		sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void izrVrednost(){
		double cen,vred;
		cen=0;
		kolicina=0;
		vred=0;
		cen = Double.parseDouble(tt[13].getText().trim());
		kolicina = Double.parseDouble(tt[12].getText().trim());
		vred = cen * kolicina;
		tt[14].setText(myFormatter.format(vred));
		tt[14].setSelectionStart(0);
		tt[14].setSelectionEnd(10);
		tt[14].requestFocus();
   }
 //-------------------------------------------------------------------------
   public void izrMagCenu(){
	if (tt[12].getText().trim().length() != 0 && tt[13].getText().trim().length() != 0 && 
		tt[15].getText().trim().length() != 0 && tt[16].getText().trim().length() != 0 &&
		tt[17].getText().trim().length() != 0 && tt[18].getText().trim().length() != 0 )
	{
	    //Racunam Veleprodajnu cenu
		double cenax,zavx,zavizn,marx,marizn,porx,prstopa,magcena,koli,cenay,ppu1,cenaz,razmar;
		long prcni;
		int tarbr;
		cenax=0;
		zavx=0;
		magcena=0;
		razmar=0;
		cenaz=0;
		tarbr = Integer.parseInt(tt[15].getText().trim());
		koli = Double.parseDouble(tt[12].getText().trim());
		cenay =  Double.parseDouble(tt[13].getText().trim()) * koli;
		cenax = Double.parseDouble(tt[13].getText().trim()) * koli;
		zavx = Double.parseDouble(tt[18].getText().trim());
		marx = Double.parseDouble(tt[17].getText().trim());
		porx = Double.parseDouble(tt[16].getText().trim());		
		
		cenax = (cenax + 0.005) * 100;
        prcni = (long) cenax;
        cenax = ((double) prcni) / 100 ; 
		cenay = (cenay + 0.005) * 100;
        prcni = (long) cenay;
        cenay = ((double) prcni) / 100 ; 
		
		//Preracunata stopa
		prstopa = ((porx * 100) / (porx + 100)) / 100;

		if (tarbr != 6 && tarbr != 7 )
		{		
			//Dobavljac je u PDV SISTEMU ako tarifni brojevi
			//nisu ni 6 ni 7
		
			//**************************************************
			//ovo je slucaj kad se unosi nabavna cena sa PDV-om
			//pa on izbija PDV a ako je bez PDV-a ovo ne ide
			//cenax = cenax * (1 - prstopa);
			//**************************************************
		}

		cenax = (cenax + 0.005) * 100;
        prcni = (long) cenax;
        cenax = ((double) prcni) / 100 ; 

		//Zavisni troskovi
		zavizn = cenax * (zavx/100);
		zavizn = (zavizn + 0.005) * 100;
        prcni = (long) zavizn;
        zavizn = ((double) prcni) / 100 ; 

		//Marza
		marizn = ((cenax + zavizn) * (marx/100));
		marizn = (marizn + 0.005) * 100;
        prcni = (long) marizn;
        marizn = ((double) prcni) / 100 ; 
		
		//Veleprodajna cena
		cenaz = cenax + marizn + zavizn;
		magcena = cenaz * (porx / 100) + cenaz;
		magcena = (magcena + 0.005) * 100;
        prcni = (long) magcena;
        magcena = ((double) prcni) / 100 ; 
		

		//Razlika marze zbog zaokruzena
		if (tarbr != 6 && tarbr != 7 )
				{
				//Dobavljac je u PDV SISTEMU
				ppu1 = (cenax + marizn + zavizn) * (porx / 100);
				ppu1 = (ppu1 + 0.005) * 100;
				prcni = (long) ppu1;
		        ppu1 = ((double) prcni) / 100 ; 
				razmar = magcena  - (cenax + ppu1 + marizn + zavizn);
				}
			else
				{
				//Dobavljac NIJE u PDV SISTEMU
				ppu1 = (cenay + marizn + zavizn) * (porx / 100);
				ppu1 = (ppu1 + 0.005) * 100;
				prcni = (long) ppu1;
		        ppu1 = ((double) prcni) / 100 ; 
				razmar = magcena  - (cenay + ppu1 + marizn + zavizn);
				}

		if (koli == 0)
		{magcena = 0;
		}
		else {
		magcena = magcena / koli;}

		magcena = (magcena + 0.005) * 100;
        prcni = (long) magcena;
        magcena = ((double) prcni) / 100 ; 
		tt[20].setText(String.valueOf(magcena));
		pamcen1 = Double.parseDouble(tt[20].getText().trim());
		tt[20].setSelectionStart(0);
		tt[20].setSelectionEnd(10);
		tt[20].requestFocus();
		tt[20].requestFocus();
 	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		tt[12].requestFocus();
	}
 }
//-------------------------------------------------------------------------
  public void ispravkaVrednosti(){
		double vvrednost=0.0,cccena=0.0;
			if (kolicina == 0 )
			{
				vrednost = 0;
				tt[12].setText(String.valueOf(0));
				tt[13].setText(String.valueOf(0));
				tt[14].setText(String.valueOf(0));
		    }
			else {
				vvrednost = Double.parseDouble(tt[14].getText().trim());
				cccena = vvrednost/kolicina;
				tt[13].setText(myFormatter1.format(cccena));
			}
	}
//-------------------------------------------------------------------------
 public void ubaciMesec(){
		String mes = tt[3].getText().substring(3,5);
		tt[19].setText(mes);
		tt[19].setSelectionStart(0);
		tt[19].setSelectionEnd(5);
		tt[19].requestFocus();
 }
 //-------------------------------------------------------------------------
  public boolean proveriSvaPolja(){
	  boolean prv = false;
	  int i = 0;
		for (i=0;i<4 ;i++ )
		{
			if (tt[i].getText().trim().length() == 0)
				prv = true;
		}
		for (i=11;i<14 ;i++ )
		{
			if (tt[i].getText().trim().length() == 0)
				prv = true;
		}
		return prv;
 }
//-------------------------------------------------------------------------------
	public void izrMagCenuObrnuto(){
	    //Racunam maloprodajnu cenu
		double cenax,zavx,zavizn,marx,marizn,porx,pory,prstopa,magcena,razmar;
		int tarbr;

		razmar=0;
		cenax=0;
		zavx=0;
		magcena=0;
		tarbr = Integer.parseInt(tt[15].getText().trim());
		cenax = Double.parseDouble(tt[13].getText().trim());
		pamcen = Double.parseDouble(tt[20].getText().trim());
		zavx = Double.parseDouble(tt[18].getText().trim());
		marx = Double.parseDouble(tt[17].getText().trim());
		pory = Double.parseDouble(tt[16].getText().trim());
		//Preracunata stopa
		prstopa = ((pory * 100) / (pory + 100)) / 100;
		porx = pory / 100;

		
		if (tarbr != 6 && tarbr != 7 )
	   {
			//ovo kad je cena nabavna sa PDV-om
			//cenax = cenax * (1 - prstopa);
		}

		//Zavisni troskovi
		zavizn = cenax * (zavx/100);
		
		//Nova marza
		marizn = (pamcen - cenax - zavizn - cenax * porx - zavizn * porx) / (1 + porx);
		marizn = (marizn * 100) / (cenax + zavizn);		
        //JOptionPane.showMessageDialog(this, String.valueOf(marizn));
		tt[17].setText(String.valueOf(marizn));

   }
//=========================================================================
	public void Akcija(FormField e) {
		FormField source;
		source = e;
				//jur
				if (source == t[0]){
					if (t[0].getText().trim().length() == 0){
						t[0].requestFocus();
					}else{
						proveriNazivRadneJedinice();
					}
				}
				//magacin
				else if (source == t[1]){
					if (t[1].getText().trim().length() == 0){
						t[1].requestFocus();
					}else{
						if (proverimagacin())
						{
							brNaloga();
							t[2].requestFocus();
						}
					}
				}
				//broj dokumenta
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0){
						t[2].requestFocus();
					}else{
						brrdok = t[2].getText().trim();
						imaNemaNaloga();
					}
				}
				//datum naloga
				else if (source == fmtDatNal){
					if (imaNemaNalog){
						if (!datumnaloga.equals(fmtDatNal.getText().trim()))
						{
							if (proveriDatum(String.valueOf(fmtDatNal.getText()).trim()) == true){
								izmeniDatumNaloga();
								novaStavka();

							}else{
								fmtDatNal.setSelectionStart(0);
								fmtDatNal.requestFocus();
							}
						}
					}else{
						if (proveriDatum(String.valueOf(fmtDatNal.getText()).trim()) == true){
							unesiNoviNalog();
							daatum = fmtDatNal.getText().trim();
							tt[0].setSelectionStart(0);
							tt[0].requestFocus();
						}else{
							fmtDatNal.setSelectionStart(0);
							fmtDatNal.requestFocus();
						}
					}
				}
				//redni br. stavke
				else if (source == tt[0]){
					proveriUnosStavka();
				}
				//sifra robe
				else if (source == tt[1]){
					if (tt[1].getText().trim().length() == 0){
						prikaziRobu();
					}else{
						if(proveriSifm()){
							tt[2].setText(koonto);
							tt[2].setSelectionStart(0);
							tt[2].requestFocus();
						}else{
							tt[1].setText("");
							tt[1].setSelectionStart(0);
							tt[1].requestFocus();
						}
					}
				}
				//konto
				else if (source == tt[2]){
					if (tt[2].getText().trim().length() == 0){
						tt[2].requestFocus();
					}else{
							proveriCenaKol();
							koonto = tt[2].getText().trim();
							if (imaStavku == false)
							{
								tt[3].setText(daatum);
							}
							tt[3].setSelectionStart(0);
							tt[3].requestFocus();
					}
				}
				//datum
				else if (source == tt[3]){
					if (proveriDatum(String.valueOf(tt[3].getText()).trim()) == true){
						if (strValuta.length()>2)
						{
							valuta.setText(strValuta);
						}else{
							valuta.setText(tt[3].getText().trim());
						}
						valuta.setSelectionStart(0);
						valuta.requestFocus();
						if (imaStavku == false)
						{
							daatum = tt[3].getText().trim();
						}
					}
					else {
						tt[3].setSelectionStart(0);
						tt[3].requestFocus();
					}
				}
				//valuta
				else if (source == valuta){
					if (proveriValutu(String.valueOf(valuta.getText()).trim()) == true){
						strValuta = valuta.getText().trim();
						if (imaStavku == false)
						{
							tt[7].setText(saaradnik);
						}
						tt[7].setSelectionStart(0);
						tt[7].requestFocus();}
					else {
						valuta.setSelectionStart(0);
						valuta.requestFocus();
					}
				}
				//vrsta dokumenta
				else if (source == tt[4]){
					if (tt[4].getText().trim().length() == 0){
						tt[4].setText("1");}
					if (  Integer.parseInt(tt[4].getText().trim()) != 1  &&  Integer.parseInt(tt[4].getText().trim()) != 2 &&  Integer.parseInt(tt[4].getText().trim()) != 55 ){		
						tt[4].setText("1");
					}
					if (imaStavku == false)
					{
						tt[5].setText(brrdok);
					}
					tt[5].setSelectionStart(0);
					tt[5].requestFocus();}
				//-------------
				else if (source == tt[5]){
					if (tt[5].getText().trim().length() == 0){
						tt[5].setText("0");
					}
					brrdok = tt[5].getText().trim();
					tt[6].setSelectionStart(0);
					tt[6].requestFocus();}
				//
				else if (source == tt[6]){
					if (tt[6].getText().trim().length() == 0){
						tt[6].setText("0");}
					if (  Integer.parseInt(tt[6].getText().trim()) != 0  &&  Integer.parseInt(tt[6].getText().trim()) != 1 ){		
						tt[6].setText("0");
					}
					if (imaStavku == false)
					{
						tt[7].setText(saaradnik);
					}
					tt[7].setSelectionStart(0);
					tt[7].requestFocus();
				}
				//-----------------
				else if (source == tt[7]){
					if (tt[7].getText().trim().length() == 0){
						tt[7].setText("0");
						tt[11].setSelectionStart(0);
						tt[11].requestFocus();
					}else{
						proveriSaradnika();
						if (imaStavku == false)
						{
							tt[11].setText(maasina);
						}
						tt[11].setSelectionStart(0);
						tt[11].requestFocus();
					}
				}
				//-------------------------------
				else if (source == tt[8]){
					if (tt[8].getText().trim().length() == 0){
						tt[8].setText("0");}
					if (  Integer.parseInt(tt[8].getText().trim()) != 0  &&  Integer.parseInt(tt[8].getText().trim()) != 1 ){		
						tt[8].setText("0");
					}
					if (imaStavku == false)
					{
						tt[9].setText(raacun);
					}
					tt[9].setSelectionStart(0);
					tt[9].requestFocus();}
				//kolicina
				else if (source == tt[9]){
					if (tt[9].getText().trim().length() == 0){
						tt[9].setText("0");
					}
					raacun = tt[9].getText().trim();
					tt[10].setSelectionStart(0);
					tt[10].requestFocus();}
				//--------------
				else if (source == tt[10]){
					if (tt[10].getText().trim().length() == 0){
						tt[10].setText("0");}
					if (  Integer.parseInt(tt[10].getText().trim()) != 0  &&  Integer.parseInt(tt[10].getText().trim()) != 1 ){		
						tt[10].setText("0");}
					tt[11].setSelectionStart(0);
					tt[11].requestFocus();}


				//----------------------------
				else if (source == tt[11]){
					if ((tt[11].getText().trim().length() == 0) || Integer.parseInt(tt[11].getText().trim()) == 0) {
						tt[11].setText("0");
						tt[12].setSelectionStart(0);
						tt[12].requestFocus();}
					else{
						proveriMasinu();
						tt[12].setSelectionStart(0);
						tt[12].requestFocus();}
				}					
				//kolicina
				else if (source == tt[12]){
					if ( (tt[12].getText().trim().length() == 0) ){
						tt[12].setText("0");
					}else {
							tt[13].setSelectionStart(0);
							tt[13].requestFocus();
					}
				}
				//cena
				else if (source == tt[13]){
					if (tt[13].getText().trim().length() == 0){
						tt[13].setText("0");
					}
					kontrolabroja = 0.0;
					try{
						kontrolabroja = Double.parseDouble(tt[13].getText().trim());
						izrVrednost();
					}catch(Exception eee){
						JOptionPane.showMessageDialog(this, "Broj nije u redu");
						tt[13].setSelectionStart(0);
						tt[13].requestFocus();
					}
				}
				//vrednost
				else if (source == tt[14]){
					if (tt[14].getText().trim().length() == 0){
						tt[14].setText("0");
					}
					kontrolabroja = 0.0;
					try{
						kontrolabroja = Double.parseDouble(tt[13].getText().trim());
						//ispravkaVrednosti();
						tt[20].setSelectionStart(0);
						tt[20].requestFocus();
					}catch(Exception eee){
						JOptionPane.showMessageDialog(this, "Broj nije u redu");
						tt[14].setText("");
						tt[13].setSelectionStart(0);
						tt[13].requestFocus();
					}
				}
				//tarifni broj
				else if (source == tt[15]){
					if (tt[15].getText().trim().length() == 0){
						tt[15].setText("0");
					}
					if (proveriTarifu())
					{
						tt[16].setSelectionStart(0);
						tt[16].requestFocus();
					}else{
						tt[15].setSelectionStart(0);
						tt[15].requestFocus();
					}
				}
				//mesec PDV
				else if (source == tt[16]){
					if (tt[16].getText().trim().length() == 0){
						tt[16].setText("0");
					}
					tt[17].setSelectionStart(0);
					tt[17].requestFocus();
				}
				//mesec PDV
				else if (source == tt[17]){
					if (tt[17].getText().trim().length() == 0){
						tt[17].setText("0");
					}
					tt[18].setSelectionStart(0);
					tt[18].requestFocus();
				}
				else if (source == tt[18]){
					if (tt[18].getText().trim().length() == 0){
						tt[18].setText("0");
					}		
					if (tt[19].getText().trim().length() == 0){
						ubaciMesec();
					}
					tt[19].setSelectionStart(0);
					tt[19].requestFocus();
				}
				//mesec PDV
				else if (source == tt[19]){
					if (tt[19].getText().trim().length() == 0){
						tt[19].setText("0");
					}
					if (Integer.parseInt(tt[19].getText().trim()) < 1 || Integer.parseInt(tt[19].getText().trim()) > 12) {
						JOptionPane.showMessageDialog(this, "Mesec mora biti od 1 do 12");
					 		ubaciMesec();
					}

					if (!tt[17].getText().trim().equals("0"))
					{
						//izrMagCenu();
					}else{
						pamcen1 = 0.0;
					}
					if (Integer.parseInt(tt[4].getText().trim()) != 2)
					{
						if (imaStavku == false)
						{
							izrMagCenu();
						}
					}else{
						if (imaStavku == false)
						{
							tt[20].setText(tt[13].getText().trim());
						}
					}
					tt[20].setSelectionStart(0);
					tt[20].requestFocus();
				}
				//mesec PDV
				else if (source == tt[20]){
					if (tt[20].getText().trim().length() == 0){
						tt[20].setText("0");
					}	
					unos.requestFocus();
				}
	}
//===========================================================================
	class ML extends MouseAdapter{
		public void mousePressed(MouseEvent e) {
			Object source = e.getSource();
			if (source == jtbl){
				int kojirec = jtbl.getSelectedRow();
				//tt[0].setText(String.valueOf(rbrpodaci.get(kojirec)));
				tt[0].setText(jtbl.getValueAt(kojirec,0).toString());
				proveriUnosStavka();
			}
		}
	}//end of class ML	
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == tt[4]){
				mozenemoze.setText("(1)Prijemn. (2)Zapisnik (55)Ostalo");
				mozenemoze.setVisible(true);}
			else if (source == tt[6]){
				mozenemoze.setText("(0)Saradnik (1)Kooperant");
				mozenemoze.setVisible(true);}
			else if (source == tt[8]){
				mozenemoze1.setText("(0)Fakturis. (1)Nefakturisano");
				mozenemoze1.setVisible(true);}
			else if (source == tt[10]){
				mozenemoze1.setText("(0)Nije Uvoz (1)Uvoz");
				mozenemoze1.setVisible(true);
			}
	}
//------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
			if (source == tt[4] || source == tt[6] || source == tt[8] || source == tt[10]){
				mozenemoze.setVisible(false);
				mozenemoze1.setVisible(false);
			}
	}
}//end of class FL=============================================================================
class mQTM7 extends AbstractTableModel {
   Connection dbconn;
   Vector<Object[]> totalrows;
   String[] colheads = {"Rb","\u0160ifra","Konto","Naziv","Datum","Br.dok",
		"Ra\u010dun","Saradnik","Ma\u0161ina","Koli\u010d","Cena bez PDV","V.cena","Nab.vr."};
   public mQTM7(Connection dbc){
	dbconn = dbc;
      totalrows = new Vector<Object[]>();
  
   }
 //---------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 13; }
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
   //-------------------------------------------------------------------------
      public String Formatiraj(String _forma) {
	String forma,format;
	int i,j;
	forma = _forma.trim();
	j = forma.length();
	for (i=j;i<12 ;i++ )
	{
		forma = "  " + forma;
	}
	return forma;
 }
 //-------------------------------------------------------------------------
   public String konvertujDatumIzPodatakaQTB(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
   
 //-------------------------------------------------------------------------
   public void query(String _sql) {
		String sql;
		rbrpodaci.clear();
		podaci.clear();
		spodaci.clear();
		vpodaci.clear();
		sql = _sql;
		Statement statement = null;
      try {
         statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector<Object[]>();
            while ( rs.next() ) {

               Object[] record = new Object[13];
               record[0] = rs.getString("rbr");
               record[1] = rs.getString("sifm");
               record[2] = rs.getString("konto");
               record[3] = rs.getString("nazivm");
               record[4] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[5] = rs.getString("brdok");
               record[6] = rs.getString("brrac");
               record[7] = rs.getString("kupacbr");
               record[8] = rs.getString("mtros");
               record[9] = rs.getBigDecimal("ROUND(kolic,2)");
               record[10] = rs.getBigDecimal("ROUND(cena,2)");
               record[11] = rs.getBigDecimal("ROUND(pcena,2)");
               record[12] = rs.getBigDecimal("ROUND(vredn,2)");
			   
			   rbrpodaci.addElement(record[0]);
			   podaci.addElement(record[2]);
			   spodaci.addElement(record[5]);
			   vpodaci.addElement(rs.getString("vrdok"));
              
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
  }// end of class Table
}//end of class mUnosPrijem
//==============================================================================
class munosCLL implements LayoutManager {

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
	//pozicija za JLabele
	  children[i].setSize(compSize.width, compSize.height);
	  children[i].setLocation( xInset + insets.left, height + 1);
	//pozicija za txt
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
/*
String s = Integer.toString(sum);
*/