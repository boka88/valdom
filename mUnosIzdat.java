//program za unos materijalog prijemnica
//---------------------------------------------------------------------------------------------
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

public class mUnosIzdat extends JInternalFrame implements InternalFrameListener {

	private JButton novi,unos,brisi;
	private int brnal;
	private int jurr,magr,kontor,sifmr,s1,s2;
	private Connection connection = null;
    public static FormField t[],tt[],ttt[],fmtDatNal;
	public static JFormattedTextField mozenemoze,mozenemoze1,
		displej,dispVrsDok,dispMT,dispKol,dispCen;
    private JLabel l[],lab[];
    int n_fields;
	private Vector<Object> podaci = new Vector<Object>();
	private Vector<Object> spodaci = new Vector<Object>();
	private Vector<Object> rbrpodaci = new Vector<Object>();
	private int rbr , preneto;
	private JScrollPane jspane;
   	private mQTMIzdat qtbl;
   	private JTable jtbl;
	private boolean imaNemaNalog;
	private boolean imaStavku=false,uminus=true;
  	private double ddug,ppot,saldo,dugFzkon,potFzkon,stariDug,stariPot,vrednost,kolicina,cenn,prcn,stanje;
	private int brStav,stariKonto,noviKonto,staraSifra,novaSifra,zauzet=0;
	private String trenutniDatum,pPre,nazivPre,date,fmm,godina,opis,oznaka,upit,imebaze,godrada,brojdokumenta;
	private String koonto="",mestotroska="",tblpromet="mprom",datumnaloga="",daatum="";
	private boolean ff9= false,prenosDaNe=false;
	private aNadji anadji = null;
	private String pattern = "#########.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);
	private classBrisanje cllBrisi = null;

//-------------------------------------------------------------------------------------------------
    public mUnosIzdat() {
		super("", true, true, true, true);
        setTitle("Izdatnice        <F9> - Sifarnici");
		setMaximizable(true);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(3,1) );

		JPanel cont = new JPanel();
		cont.setLayout( new GridLayout(3,1) );
		cont.setBackground(Color.white);
		cont.setBorder( new TitledBorder("") );

		novi = new JButton("Novi nalog");
		novi.setMnemonic('N');
        novi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        novi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {NoviPressed();}});
		novi.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
			NoviPressed(); }});
		novi.requestFocus();

		uzmiKonekciju();
		anadji = new aNadji();
		cllBrisi = new classBrisanje();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		godina = date.substring(6,10);


		pPre = new String("1");
		tblpromet=tblpromet+ pPre.trim();
		nazivPre = new String("Valdom");
		imebaze = ConnMySQL.imebaze;
	    godrada=imebaze.substring(4,8);		

		if (pPre == "")
			pPre = "01";

		cont.add(buildDugPotPanel());
		cont.add(buildDugPPanel());
		cont.add(buildDisplej());

		definisiPoljaZaStavke();


		JPanel contt = new JPanel();
		contt.setLayout( new GridLayout(1,2) );
		contt.setBorder( new TitledBorder("Stavke") );

		contt.add(buildPanel1());
		contt.add(buildPanel2());

		container.add(cont);
		container.add(contt);
		String sql;
		container.add(buildTable());

		getContentPane().add(container);
		pack();
		setSize(800,600);

		rbr = 1;
		imaNemaNalog = false;
		imaStavku = false;
		brStav = 1;
		String s;

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
			if (t[0].getText().trim().length() != 0 && 
				t[1].getText().trim().length() != 0 && 
				t[2].getText().trim().length() != 0)
			{
				upit = "update "+tblpromet+" set z = 0 WHERE jur=" +
				Integer.parseInt(t[0].getText().trim()) +
				" AND ui=2 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) +
				" AND indikator=0 AND (vrdok = 3 OR vrdok=66)";
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
		//pred.setSize(100,20);
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
		t[1] = new FormField(createFormatter(fmm,2));
		t[1].setColumns(10);
		//------------------- programiranje provere i akcije ------------------------
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[1].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziMagacin();}});


		fmm = "**********";
        l[2] = new JLabel("Br.unosa :",JLabel.RIGHT);
		t[2] = new FormField(createFormatter(fmm,2));
		t[2].setColumns(10);
		//------------------- programiranje provere i akcije ------------------------
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "##/##/####";				
        l[3] = new JLabel("Datum unosa:",JLabel.RIGHT);
		fmtDatNal = new FormField(createFormatter(fmm,4));
		fmtDatNal.setColumns(10);
		fmtDatNal.setText(date);
		fmtDatNal.setSelectionStart(0);
		//------------------- programiranje provere i akcije ------------------------
        fmtDatNal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        fmtDatNal.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(fmtDatNal);}});

		p.add(novi);
		for(i=0;i<3;i++){ 
            p.add(l[i]); 
			p.add(t[i]); }
		p.add(l[3]);
		p.add(fmtDatNal);

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

		fmm = "**************************************************";
        l[3] = new JLabel("Opis:",JLabel.RIGHT);
		ttt[3] = new FormField(createFormatter(fmm,3));
		ttt[3].setText("Izdatnice");
		ttt[3].setColumns(15);
		ttt[3].setEditable(false);

		fmm = "***************";
        l[4] = new JLabel("Broj stavki :",JLabel.RIGHT);
		ttt[4] = new FormField(createFormatter(fmm,2));
		ttt[4].setColumns(12);
		ttt[4].setEditable(false);
	      for(i=3;i<5;i++){ 
      	      pp.add(l[i]); 
            	pp.add(ttt[i]); }
	return pp;
	}
//-------------------------------------------------------------------------------------------------
    public JPanel buildDisplej() {
		JPanel pp = new JPanel();
		pp.setLayout( null );
		Color c = new Color(17,203,214);		
		pp.setBackground(c);

		JLabel naz = new JLabel("Naz.robe:");
		naz.setBounds(250,5,70,20);
		displej = new JFormattedTextField(); 
		displej.setBounds(320,5,290,20);
		displej.setEditable(false);
		displej.setFont(new Font("Arial",Font.BOLD,12));

		JLabel kon = new JLabel("Konto:");
		kon.setBounds(5,5,40,20);
		dispVrsDok = new JFormattedTextField(); 
		dispVrsDok.setBounds(45,5,150,20);
		dispVrsDok.setEditable(false);
		dispVrsDok.setFont(new Font("Arial",Font.BOLD,10));

		JLabel mt = new JLabel("MTros:");
		mt.setBounds(5,30,40,20);
		dispMT = new JFormattedTextField(); 
		dispMT.setBounds(45,30,150,20);
		dispMT.setEditable(false);
		dispMT.setFont(new Font("Arial",Font.BOLD,10));

		JLabel kol = new JLabel("Kolicina:");
		kol.setBounds(250,30,70,20);
		dispKol = new JFormattedTextField(); 
		dispKol.setBounds(320,30,100,20);
		dispKol.setEditable(false);
		dispKol.setFont(new Font("Arial",Font.BOLD,12));

		JLabel cen = new JLabel("Cena:");
		cen.setBounds(450,30,50,20);
		dispCen = new JFormattedTextField(); 
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

		JButton stampa = new JButton("Štampa");
		stampa.setMnemonic('P');
		stampa.setBounds(650,5,80,20);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		pp.add(stampa);
		return pp;
	}
//-----------------------------------------------------------------------------------------------------------
	public void definisiPoljaZaStavke(){
		lab = new JLabel[21]; 

        lab[0]= new JLabel("R.br :         ",JLabel.RIGHT);
        lab[1]= new JLabel("Sifra robe :   ",JLabel.RIGHT);
        lab[2]= new JLabel("Konto :        ",JLabel.RIGHT);
        lab[3]= new JLabel("Datum :        ",JLabel.RIGHT);
        lab[4]= new JLabel("Vrsta dok. :   ",JLabel.RIGHT);
        lab[5]= new JLabel("Broj dok. :    ",JLabel.CENTER);
        lab[11]= new JLabel("Ma\u0161ina :",JLabel.RIGHT);
        lab[12]= new JLabel("Kolicina :     ",JLabel.LEFT);
        lab[13]= new JLabel("Cena bez PDV : ",JLabel.LEFT);
        lab[14]= new JLabel("Vredn.bez PDV: ",JLabel.LEFT);

        tt = new FormField[21]; 

		fmm = "*****";											//rbr-stavke
		tt[0] = new FormField(createFormatter(fmm,1));
		tt[0].setColumns(5);
		//------------------- programiranje provere i akcije ------------------------
        tt[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[0]);}});


		fmm = "*******";											//sifra robe
		tt[1] = new FormField(createFormatter(fmm,1));
		tt[1].setColumns(7);
		tt[1].setText("0");
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
		tt[2].setText("1010");
		//------------------- programiranje provere i akcije ------------------------
        tt[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[2]);}});
        tt[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        tt[2].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziKonto();}});


		fmm = "##/##/####";										//datum
		tt[3] = new FormField(createFormatter(fmm,1));
		tt[3].setColumns(10);
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
		//tt[5].setEditable(false);
		//------------------- programiranje provere i akcije ------------------------
        tt[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[5]);}});
		
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


		fmm = "*******";									//Kolicina										
        tt[12] = new FormField(createFormatter(fmm,2));
		tt[12].setColumns(7);
		//------------------- programiranje provere i akcije ------------------------
        tt[12].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[12].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[12]);}});


		fmm = "**********";									//Cena bez PDV
        tt[13] = new FormField(createFormatter(fmm,2));
		tt[13].setColumns(10);
		//tt[13].setEditable(false);
		//------------------- programiranje provere i akcije ------------------------
        tt[13].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[13].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[13]);}});

	
		fmm = "**********";									//Vrednost bez PDV
        tt[14] = new FormField(createFormatter(fmm,2));
		tt[14].setColumns(10);
		//tt[14].setEditable(false);
		//------------------- programiranje provere i akcije ------------------------
        tt[14].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[14].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[14]);}});

	}
//--------------------------------------------------------------------------------------------------
	private JPanel buildPanel1(){
		JPanel p1 = new JPanel();
		p1.setLayout( new munosCLLIzdat() );

		mozenemoze = new JFormattedTextField(); 
		mozenemoze.setColumns(20);
		mozenemoze.setEditable(false);
		mozenemoze.setFont(new Font("Arial",Font.PLAIN,11));
		mozenemoze.setText(" ");
		mozenemoze.setVisible(false);

		JLabel moze = new JLabel("      ");
		moze.setVisible(false);

		int i;
	    for(i=0;i<6;i++){ 
            p1.add(lab[i]); 
            p1.add(tt[i]); }

		p1.add(moze);
		p1.add(mozenemoze);

		return p1;
	}
//--------------------------------------------------------------------------------------------------
	private JPanel buildPanel2(){
		JPanel p2 = new JPanel();
		p2.setLayout( new munosCLLIzdat() );

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

            p2.add(lab[5]); 
            p2.add(tt[5]); 
            p2.add(lab[11]); 
            p2.add(tt[11]); 
            p2.add(lab[12]); 
            p2.add(tt[12]); 
            p2.add(lab[13]); 
            p2.add(tt[13]); 
            p2.add(lab[14]); 
            p2.add(tt[14]); 
            p2.add(lbl1); 
            p2.add(lbl2); 
            p2.add(unos); 
            p2.add(brisi); 

		return p2;
	}
//-------------------------------------------------------------------------------------------------
    private void Upis() {
			if (proveriSvaPolja())
			{
				JOptionPane.showMessageDialog(this, "Sva polja moraju biti popunjena");
				tt[0].requestFocus();
			}else{

					if (ttt[4].getText().trim().length() == 0){ttt[4].setText("0");}
					jurr = Integer.parseInt(t[0].getText().trim());
					magr = Integer.parseInt(t[1].getText().trim());
					kontor = Integer.parseInt(tt[2].getText().trim());
					sifmr = Integer.parseInt(tt[1].getText().trim());
					s1 = Integer.parseInt(tt[0].getText().trim());
					s2 = Integer.parseInt(ttt[4].getText().trim());

					novaStaraStavka();

				  //Pozivam proceduru za prazuriranje izlaznih cena
				if (s1 <= s2 && uminus==true)
				{
					//mServis3 pred = new mServis3(jurr,magr,kontor,sifmr);
				}
				tt[0].setSelectionStart(0);
				tt[0].requestFocus();
			}
	}
//-------------------------------------------------------------------------------------------------
    private void Brisanje() {
		if (t[0].getText().trim().length()!=0 && t[1].getText().trim().length()!=0 &&
			tt[0].getText().trim().length()!=0 && tt[1].getText().trim().length()!=0)
		{
			
			//connect,pre,jur,mag,vrdok,brun,rbr)
				//proverava da li je kolicina nulirana
				if(cllBrisi.proveriStavkuRobnoKolic(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												Integer.parseInt(tt[4].getText().trim()),
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())))
				{
					cllBrisi.izbrisiStavkuRobno(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												Integer.parseInt(tt[4].getText().trim()),
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())
												);
					cllBrisi.preazurirajRbrRobno(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												Integer.parseInt(tt[4].getText().trim()),
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())
												);
					String sqll = "SELECT rbr,brun,sifm,nazivm,konto,datum,vrdok,brdok,mtros," +
					"ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
					"FROM "+tblpromet+" WHERE brun=" +
					Integer.parseInt(t[2].getText().trim()) + 
					" AND ui=2 AND indikator = 0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
					" AND (vrdok = 3 or vrdok = 66) AND rbr>0" + 
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

	   	qtbl = new mQTMIzdat(connection);
		String sql;
		sql = "SELECT rbr,brun,sifm,nazivm,konto,datum,vrdok,brdok,mtros," +
				"ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
				"FROM "+tblpromet+" WHERE konto=9999";
	   	qtbl.query(sql);
 	   	
		//jtbl = new JTable( qtbl );
		TableSorter sorter = new TableSorter(qtbl); 
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); 
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());

	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(100);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
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
				" AND ui=2 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) +
				" AND (vrdok = 3 or vrdok = 66) AND indikator=0";
				Oslobodi(upit);
			}
		int i;
		rbr = 1;
		for(i=0;i<3;i++){
            t[i].setText("");
            t[i].setEditable(true);
		}
        for(i=0;i<6;i++){
            tt[i].setText("");
            tt[i].setEditable(true);
		}
        for(i=11;i<15;i++){
            tt[i].setText("");
            tt[i].setEditable(true);
		}

		for(i=3;i<5;i++){
            ttt[i].setText("");
		}
		fmtDatNal.setText("");
		displej.setText("");
		dispMT.setText("");
		dispKol.setText("");
		dispCen.setText("");
		dispVrsDok.setText("");
		fmtDatNal.setText(date);
		imaStavku=false;
		imaNemaNalog = false;
		brojdokumenta = "";
		String sql = "SELECT rbr,brun,sifm,nazivm,konto,datum,vrdok,brdok,mtros," +
				"ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
				"FROM "+tblpromet+" WHERE konto=9999";
		popuniTabelu(sql);
		fmtDatNal.setSelectionStart(0);
		mozenemoze.setVisible(false);
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
		for(i=0;i<6;i++){
			tt[i].setText("");}
		for(i=11;i<15;i++){
			tt[i].setText("");
		}
		imaStavku = false;
		displej.setText("");
		dispMT.setText("");
		dispKol.setText("");
		dispCen.setText("");
		dispVrsDok.setText("");
    }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
        this.setVisible(false);
    } 
//-------------------------------------------------------------------------------------------------
   public void Oslobodi(String _upit) {
	  //PROCEDURA ZA OSLOBADJANJE ZAUZETOG NALOG
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
	}//end if
 } 
//-------------------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
	ConnMySQL _dbconn = new ConnMySQL();
	if (_dbconn.getConnection() != null)
		{connection = _dbconn.getConnection();}
	else
		{JOptionPane.showMessageDialog(this, "Konekcija nije otvorena");}
	return;
    }
//-------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(100);
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
		int _jur,_mag,_konto,_brun;

	if (!t[0].getText().trim().equals( "" ) && !t[1].getText().trim().equals( "" ) )
	{
		_jur = Integer.parseInt(t[0].getText().trim());
		_mag = Integer.parseInt(t[1].getText().trim());
		_konto = Integer.parseInt(String.valueOf(podaci.get(0)));
		_brun = Integer.parseInt(String.valueOf(spodaci.get(0)));

	}else{
		_jur = 0;
		_mag = 0;
		_konto = 0;
		_brun = 0;
	}
		//za konto saljem 0 zato sto moze biti izdatnica sa vise konta
		JInternalFrame pred = new mStampaDokumenata(_jur,_mag,0,_brun,3);
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
			" AND ui=2 AND indikator = 0 "   +
			" AND (vrdok = 3 or vrdok = 66) " + 
			" AND pre=" + Integer.parseInt(pPre);
			try {
		         ResultSet rs = statement.executeQuery( sql );
		         rs.next();
				
				stariKonto = rs.getInt("konto");		//uzima konto stare stavke
				staraSifra = rs.getInt("sifm");		    //uzima staru sifru
				stariDug = rs.getDouble("kolic");		//uzima kolicinu stare stavke
				stariPot = rs.getDouble("vredn");		//uzima vrednost stare stavke
				preneto = rs.getInt("preneto");		    //da li je preneto u finansijsko

				tt[0].setText(rs.getString("rbr"));				//Stavka - rbr
				tt[1].setText(rs.getString("sifm")); 			//Sifra materijala - robe
				tt[2].setText(rs.getString("konto"));			//Konto
				tt[3].setText(konvertujDatumIzPodataka(rs.getString("datum")));	//Datum
				tt[4].setText(rs.getString("vrdok"));			//vrsta dokum.
				tt[5].setText(rs.getString("brdok"));			//Broj dokumenta
				tt[11].setText(rs.getString("mtros")); 			//Mesto troska
				tt[12].setText(rs.getString("kolic")); 			//Kolicina
				tt[13].setText(rs.getString("cena"));			//Cena bez PDV
				tt[14].setText(rs.getString("vredn"));			//vrednost bez PDV
				brojdokumenta = rs.getString("brdok");
				koonto = rs.getString("konto");
				proveriSifm();
				//proveriMsta();
				proveriTrosak();
				proveriKonto();
				imaStavku = true;
						if (tt[14].getText().trim().length() != 0){
					        tt[3].requestFocus();
						}

						if (preneto == 1) {
							tt[12].requestFocus();
						}
		      }
		      catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(this, "Broj stavke nije unet"+sqlex);
		      }
			}     
            else {
				JOptionPane.showMessageDialog(this, "Broj stavke nije unet");
            }
		}catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju");
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
				String ddat,dddat;
				double cena,vrednost,kol;
				cena = Double.parseDouble(tt[13].getText().trim());
				kol = Double.parseDouble(tt[12].getText().trim());
				vrednost = Double.parseDouble(tt[14].getText().trim());
				oznaka = "-";

				if (stanje - kol + stariDug  < 0)
				{
					cena = 0;
					vrednost = 0;
					oznaka = "+";
				}

				ddat =String.valueOf(tt[3].getText()).trim();
				dddat = konvertujDatum(ddat);
				trenutniDatum = ddat;

        String query = "INSERT INTO "+tblpromet+"(pre,jur,mag,ui,indikator,brun,rbr,sifm,konto," +
				"datum,datumnaloga,valuta,vrdok,brdok,kupacbr,brrac,fak,sar,kolic,koliciz,pcena,vredn,cena," +
				"pcena1,pvredn,vrtar,tarbr,por1,zav,mar,taksa,uvoz,mesec,mtros,oznaka,nazivm)" +
				" VALUES (" + Integer.parseInt(pPre) + "," +		//Pre-1
			Integer.parseInt(t[0].getText().trim())+ "," + 			//jur-2
			Integer.parseInt(t[1].getText().trim())+ ",2,0," + 		//Magacin, ui, indikator-3-4-5
			Integer.parseInt(t[2].getText().trim())+ "," + 			//Broj unosa-6
			Integer.parseInt(tt[0].getText().trim())+ "," + 		//Stavka - rbr-7
			Integer.parseInt(tt[1].getText().trim())+ "," + 		//Sifra materijala - robe-8
			Integer.parseInt(tt[2].getText().trim())+ ",'" + 		//Konto-9
			dddat + "','" + 										//Datum-10
			konvertujDatum(fmtDatNal.getText().trim()) + "','" +	//Datum-naloga
			dddat + "'," + 											//Valuta
			Integer.parseInt(tt[4].getText().trim())+ "," + 		//vrsta dokum.-11
			Integer.parseInt(t[2].getText().trim())+ "," + 			//Broj dokumenta-12
			"0," +													//Sifra saradnika-13
			"0," +													//Broj racuna-14
			"0," +													//Fakturisano ili ne-15
			"0," +													//Indikator da li je saradnik ili kooperant-16
			kol+ "," + 												//Kolicina-17
			kol+ "," + 												//Kolicina izlaz
			cena + "," + 											//Cena bez PDV-a i zav.troskova(Ulazna)-(pcena)-18
			vrednost + "," +										//vrednost bez PDV + zavisni trosak-Magacinska(vredn)-19
			cena + "," +		    								//cena magacinska(BEZ PDV + ZAVISNI TROSAK) -(cena)20
			cena + "," +		                                    //pcena1-Cena sa PDV ALI BEZ ZAVISNIH TROSKOAVA -21
			vrednost + "," +		                                //Vrednost bez PDV i zavisnih troskova - pvredn 22
			"0," +													//vrtar vrsta poreza
			"0," +													//Tarifni broj
			"0," +													//Porez - por1
			"0," +													//Zavisni troskovi
			"0,0," +												//Marza, Taksa
			"0," +													//Uvoz ili nije uvoz
			"0," +													//Mesec PDV
			Integer.parseInt(tt[11].getText().trim()) +             //Mesto troska
			",'" + oznaka + "','" +  								//Azurirano-neazurirano
			displej.getText() + "')";								//Naziv artikla

               int result = statement.executeUpdate( query );
               if ( result == 1 ){
				rbr = rbr + 1;
               	String sqll = "SELECT rbr,brun,sifm,nazivm,konto,datum,vrdok,brdok,mtros," +
				"ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
				"FROM "+tblpromet+" WHERE brun=" +
				Integer.parseInt(t[2].getText().trim()) + 
				" AND ui=2 AND indikator = 0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
				" AND (vrdok = 3 or vrdok = 66) AND rbr>0" + 
				" AND pre=" + Integer.parseInt(pPre);
				popuniTabelu(sqll);
				imaStavku = false;
				ocistiSlog();
				novaStavka();
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
		String query = "";
		Statement statement = null;
      try {
         statement = connection.createStatement();
				String ddat,dddat,dval,ddval;
				ddat = String.valueOf(tt[3].getText().trim());
				dddat = konvertujDatum(ddat);
			
				double cena,vrednost,kol;
				cena = Double.parseDouble(tt[13].getText().trim());
				kol = Double.parseDouble(tt[12].getText().trim());
				vrednost = Double.parseDouble(tt[14].getText().trim());
				oznaka = "-";

				if (stanje - kol + stariDug  < 0)
				{
					cena = 0;
					vrednost = 0;
					oznaka = "+";
				}
			query = "UPDATE "+tblpromet+" SET " +
			"datum='" + dddat + "'," + 										//Datum
			"datumnaloga='" + konvertujDatum(fmtDatNal.getText().trim()) + "'," +//Datum naloga
			"valuta='" + dddat + "'," + 									//Valuta
			"vrdok=" + Integer.parseInt(tt[4].getText().trim())+ ", " + 	//vrsta dokum.
			"sifm=" + Integer.parseInt(tt[1].getText().trim()) + ", " + 	//sifra robe
			"konto=" + Integer.parseInt(tt[2].getText().trim()) + ", " + 	//konto
			"brdok=" + Integer.parseInt(t[2].getText().trim())+ ", " + 		//Broj dokumenta
			"kolic=" + Double.parseDouble(tt[12].getText().trim())+ ", " + 	//Kolicina
			"koliciz=" + Double.parseDouble(tt[12].getText().trim())+ ", " + //Kolicina izlaz
			"cena=" + cena + ", " +											//cena magacinska (bez pdv sa zavisnim troskom)
			"vredn=" + vrednost + ", " +	                                //vrenost magacinska(bez pdv sa zavisnim troskom)
			"pcena=" + cena  + ", " +	                                    //cena bez pdv 
			"pcena1=" + cena + ", " +	                                    //cena sa pdv bez zavinog troska 
			"pvredn=" + vrednost + ", preneto1 = 0 , " +	                //Vrednost bez PDV
			"mtros=" + Integer.parseInt(tt[11].getText().trim()) +		 	//Mesto troska
			",oznaka='" + oznaka + "'" +		 	                        //Azurirano-Neazurirano
			" WHERE ui = 2 and indikator = 0 and brun=" + Integer.parseInt(t[2].getText().trim()) + 
				" AND rbr=" + Integer.parseInt(tt[0].getText().trim()) +
				" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
				" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND vrdok = " +  Integer.parseInt(tt[4].getText().trim()) +
				" AND pre=" + Integer.parseInt(pPre);

               int result = statement.executeUpdate( query );

               if ( result == 1 ){
               	String sqll = "SELECT rbr,brun,sifm,nazivm,konto,datum,vrdok,brdok,mtros," +
				"ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
				"FROM "+tblpromet+" WHERE brun=" +
					Integer.parseInt(t[2].getText().trim()) + 
					" AND ui=2 AND indikator = 0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
					" AND (vrdok = 3 or vrdok = 66) AND rbr>0" + 
					" AND pre=" + Integer.parseInt(pPre);
				popuniTabelu(sqll);
				imaStavku = true;
				//stariNoviKonto();
				ocistiSlog();
				novaStavka();

			   }
				else {
            		JOptionPane.showMessageDialog(this, "Stavka nije izmenjena");
					NoviPressed();}
	  }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u izmeni stavke"+sqlex);
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

		Statement statement = null;
      try {
         statement = connection.createStatement();
         if ( !t[0].getText().equals( "" )) {
               		String query = "SELECT MAX(rbr) FROM "+tblpromet+" WHERE brun=" +
					Integer.parseInt(t[2].getText().trim()) +
					" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
					" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND pre=" + Integer.parseInt(pPre) + 
					" AND (vrdok = 3 or vrdok = 66) " + 
					" AND ui=2 AND indikator=0";

			try {
	         ResultSet rs = statement.executeQuery( query );
		
				rs.next();
		        brStav = rs.getInt(1);
				brStav = brStav + 1;
				tt[3].setText(date);
		      }
		      catch ( SQLException sqlex ) {
				brStav = 1;
		      }
				ocistiSlog();
				tt[0].setText(String.valueOf(brStav));
				tt[0].setSelectionStart(0);
				tt[0].requestFocus();

		}     
            else {
            JOptionPane.showMessageDialog(this, "radna jedinica nije uneta");
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
//-------------------------------------------------------------------------------------
   public void updateSum() {
	Statement statement = null;
      try {
         statement = connection.createStatement();
            String query = "SELECT @dddug:=SUM(dugu), @pppot:=SUM(potr), " + 
			"@sssal:=SUM(dugu-potr) FROM fprom" +
			" WHERE vrprom=" + Integer.parseInt(t[0].getText().trim()) +
			" AND nalog=" + Integer.parseInt(t[1].getText().trim()) +
			" AND pre=" +Integer.parseInt(pPre);

		         ResultSet rs = statement.executeQuery( query );
		         	rs.next();
				ddug = rs.getDouble(1);
				ppot = rs.getDouble(2);
				ttt[4].setText(rs.getString(1));
				ttt[5].setText(rs.getString(2));
				ttt[6].setText(rs.getString(3));
         		statement.close();
       			statement = null;
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u SUM");
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
		Statement statement = null;
      try {
         statement = connection.createStatement();
         if ( t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
			t[2].getText().trim().length() != 0) {
            String sql = "SELECT * FROM "+tblpromet+" WHERE jur=" +
			Integer.parseInt(t[0].getText().trim()) +
			" AND ui=2 AND indikator=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
			" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
			" AND pre=" +Integer.parseInt(pPre)+
			" AND (vrdok = 3 or vrdok = 66) AND rbr>0";

			try {
		         ResultSet rs = statement.executeQuery( sql );
		         if(rs.next()){
					String ddat,dddat;
					ddat = rs.getString("datumnaloga");
					dddat = konvertujDatumIzPodataka(ddat);
		         	fmtDatNal.setText(dddat);
					daatum = dddat;
               		
					rs.close();
					
					String sqll = "SELECT rbr,brun,sifm,nazivm,konto,datum,vrdok,brdok,mtros," +
					"ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
					"FROM "+tblpromet+" WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
					" AND pre=" +Integer.parseInt(pPre) + 
					" AND (vrdok = 3 or vrdok = 66) AND rbr>0" + 
					" AND ui=2 AND indikator=0";
					popuniTabelu(sqll);
					imaNemaNalog = true;
					novaStavka();
				 }else{
		         	JOptionPane.showMessageDialog(this, "Slog ne postoji ili je zauzet");
					t[2].requestFocus();
				 }
		      }
		      catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(this, "Greska prikaziNalog:"+sqlex);
		      }
		}else {
				JOptionPane.showMessageDialog(this, "Unesi prvo podatke");
				t[0].requestFocus();
        }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska prikaziNalog:"+sqlex);
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
//-----------------------------------------------------------------------------
	public void brNaloga(){ 
		boolean ima;
		ima = proverimagacin();
		if (ima)
		{
		Statement statement = null;
		try {
			statement = connection.createStatement();
			if ( !t[0].getText().equals( "" ) && !t[0].getText().equals( "" )) {
				String query = "SELECT MAX(brun) FROM "+tblpromet+" WHERE jur=" +
				Integer.parseInt(t[0].getText().trim()) + " AND mag=" +
				Integer.parseInt(t[1].getText().trim()) + " AND ui=2" +
				" AND indikator = 0 AND (vrdok = 3 OR vrdok=66)" +
				" AND pre=" + Integer.parseInt(pPre);
				try {
					ResultSet rs = statement.executeQuery( query );
					if(rs.next()){
		         		brnal = rs.getInt(1);
						brnal = brnal + 1;
					}else{
						brnal = 1;
					}
					t[2].setText(String.valueOf(brnal));
					t[2].requestFocus();
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Greska u brNaloga(): "+sqlex);
					//brnal = 1;
					//t[2].setText(String.valueOf(brnal));
					//t[2].requestFocus();
				}
			}     
				else {
					JOptionPane.showMessageDialog(this, "Unesi jur i magacin");
				}
		}
		catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju zaglavlja");
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
	else
	{	t[0].setText("");
		JOptionPane.showMessageDialog(this, "Nema te vrste dokumenta morate ga uneti u sifarnik");
		dispVrsDok.setText("");
		t[0].requestFocus();}
}
//-------------------------------------------------------------------------------------
  public void unesiNoviNalog() {
		Statement statement = null;
      try {
		 statement = connection.createStatement();
         if ( !t[0].getText().equals( "" ) && 
            !t[1].getText().equals( "" ) ) {
				String ddat,dddat;
				ddat = String.valueOf(fmtDatNal.getText()).trim();
				dddat = konvertujDatum(ddat);
				trenutniDatum = ddat;

			   //unesi nulti slog
			   String query = "INSERT INTO "+tblpromet+" (pre,jur,mag,ui,indikator,brun,datum,rbr,vrdok,z)" +
				" VALUES (" + Integer.parseInt(pPre) + "," +			//preduzece
				Integer.parseInt(t[0].getText().trim())+ "," +			//jur
				Integer.parseInt(t[1].getText().trim())+ "," +			//magacin
				"2,0," + Integer.parseInt(t[2].getText().trim()) +
				",'" + konvertujDatum(fmtDatNal.getText().trim()) + "'," +													//ui
				"0,3,1)";  

               	
				int result = statement.executeUpdate( query );
               	if ( result == 1 ){
					imaNemaNalog = true;
               		String sqll = "SELECT rbr,brun,sifm,nazivm,konto,datum,vrdok,brdok,mtros," +
					"ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
					"FROM "+tblpromet+" WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
					" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND ui=2 AND indikator=0 AND rbr>0" +
					" AND (vrdok = 3 or vrdok = 66) " + 
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
		{JOptionPane.showMessageDialog(this, "Unesi prvo podatke u Vr-k i Brnal");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u unosu novog naloga: "+sqlex);
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
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		dispVrsDok.setText(String.valueOf(rs.getString("nazivk")));
						provera = true;
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji konto morate ga otvoriti");
					}
			}else {
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
    public void proveriSifm(){ 
		String queryy;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !tt[1].getText().equals( "" )) {
				queryy = "SELECT * FROM sifarnikrobe WHERE sifm=" +
		            Integer.parseInt(tt[1].getText().trim());
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		displej.setText(String.valueOf(rs.getString("nazivm")));
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji artikal morate ga otvoriti");
						tt[1].setText("");
						tt[1].setSelectionStart(0);
						tt[1].requestFocus();
					}
			}else {
				JOptionPane.showMessageDialog(this, "Sifra robe nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriSifm:" + sqlex);
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
    public boolean proveriTrosak(){ 
		String queryy;
		boolean prov=false;
		Statement statement = null;
      try {
         statement = connection.createStatement();
         	if ( !tt[11].getText().equals( "" )) {
				queryy = "SELECT * FROM masine WHERE sifra=" +
		            tt[11].getText().trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
		         		dispMT.setText(String.valueOf(rs.getString("naziv")));
						prov = true;
						
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji ma\u0161ina morate je otvoriti");
					}
			}else {
				JOptionPane.showMessageDialog(this, "Ma\u0161ina nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u proveriTrosak:" + sqlex);
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
		return prov;
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
				try {
					ResultSet rs = statement.executeQuery( queryy );
					rs.next();
						tt[8].requestFocus();
				}
				catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Ne postoji saradnik morate ga otvoriti");
					tt[7].setText("");
					tt[7].requestFocus();}
			}     
            else {
            JOptionPane.showMessageDialog(this, "Saradnik nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju saradnika broja");
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
		if (Integer.parseInt(t[2].getText().trim()) < brnal){
			prikaziNalog();
			displej.setText("");
			tt[0].setSelectionStart(0);
			tt[0].requestFocus();}
		else{
			displej.setText("");
			brojdokumenta = t[2].getText().trim();
			fmtDatNal.setSelectionStart(0);
			fmtDatNal.requestFocus();
		}
		upit = "UPDATE "+tblpromet+" SET z=1" + 
				" WHERE jur=" + Integer.parseInt(t[0].getText().trim()) +
				" AND ui=2 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) + 
				" AND indikator=0 AND (vrdok = 3 OR vrdok=66)";
		Oslobodi(upit);
		t[0].setEditable(false);
		t[1].setEditable(false);
		t[2].setEditable(false);
		return;
	}
//---------------------------------------------------------------------------------
   public void proveriUnosStavka(){
	int aa;
	aa = Integer.parseInt(tt[0].getText().trim());
	if ( tt[0].getText().trim().length() == 0){
		JOptionPane.showMessageDialog(this, "Unesi prvo stavku");
		tt[0].requestFocus();
	}
	else
	{
		if (aa < brStav)
			proveriStavku();
		else if (aa > brStav){
			JOptionPane.showMessageDialog(this, "Ne moze se povecavati redni br. stavke");
			tt[0].setText(String.valueOf(brStav));
			tt[0].requestFocus();}
		else if ( aa == 0){
			JOptionPane.showMessageDialog(this, "Ne moze stavka sa nulom");
			tt[0].requestFocus();}
		else if (aa == brStav){
			imaStavku = false;
			tt[3].setText(trenutniDatum);
			tt[1].requestFocus();}
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
//-------------------------------------------------------------------------
   public void prikaziRobu(){
		int t = 4;
		if (imaStavku == false)
		{
			mRobaPrik sk = new mRobaPrik(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
		}
   }
//-------------------------------------------------------------------------
   public void prikaziKonto(){
		int t = 4;
		if (imaStavku == false)
		{
			mKontPlan sk = new mKontPlan(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
		}
   }
//-------------------------------------------------------------------------
   public void prikaziMasinu(){
		int t = 2;
			mMasinePrik sk = new mMasinePrik(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void prikaziMTroska(){
		int t = 2;
		if (imaStavku == false)
		{
			mTrosPrik sk = new mTrosPrik(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
		}
   }
//-------------------------------------------------------------------------
   public void prikaziSarad(){
		int t = 3;
		fKupciPrik sk = new fKupciPrik(connection,t);
		sk.setModal(true);
		sk.setVisible(true);
   }
//------------------------------------------------------------------------------------------------------------------
   public void prikaziMagacin(){
		int t = 2;
		mMagPrik sk = new mMagPrik(connection,t);
		sk.setVisible(true);
   }
//------------------------------------------------------------------------------------------------------------------
	public void obrisiPomocniSlog() {
				upit = "update "+tblpromet+" set z = 0 WHERE jur=" +
				Integer.parseInt(t[0].getText().trim()) +
				" AND ui=2 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) +
				" AND indikator=0 AND (vrdok = 3 OR vrdok=66)";

	  
	  anadji.obrisiPomocniNultiSlog(connection,Integer.parseInt(pPre),
								Integer.parseInt(t[0].getText().trim()),
								Integer.parseInt(t[1].getText().trim()),
								2,0,
								Integer.parseInt(t[2].getText().trim()),
								" AND vrdok = 3");
 
 }
//-------------------------------------------------------------------------
   public void izrCenu(){
		double cenx;
		cenx=0;
		cenx = Double.parseDouble(tt[13].getText().trim());
		tt[13].setText(String.valueOf(cenx));
		tt[13].setSelectionStart(0);
		tt[13].setSelectionEnd(10);
		tt[13].requestFocus();
   }
//-------------------------------------------------------------------------
   public void izrVrednost(){
		double cen,vred,kolicina;
		cen=0;
		kolicina=0;
		vred=0;
		cen = Double.parseDouble(tt[13].getText().trim());
		kolicina = Double.parseDouble(tt[12].getText().trim());
		vred = cen * kolicina;		
		tt[13].setText(String.valueOf(cen));
		tt[13].requestFocus();

		tt[14].setText(String.valueOf(vred));
		tt[14].setSelectionStart(0);
		tt[14].setSelectionEnd(10);
		tt[14].requestFocus();
   }
 //-------------------------------------------------------------------------
  public void ispravkaVrednosti(){
		if (Double.parseDouble(tt[14].getText().trim()) != vrednost){
		tt[13].setText(String.valueOf(Double.parseDouble(tt[14].getText().trim())/kolicina));}
		vrednost = 0;
 }
 //-------------------------------------------------------------------------
 public void ubaciMesec(){
		String mes = tt[3].getText().substring(3,5);
		tt[19].setText(mes);
		tt[19].setSelectionStart(0);
		tt[19].setSelectionEnd(5);
		tt[19].requestFocus();
 }
//----------------------------------------------------------------------------
    public void proveriCenaKol(){ 
			stanje = anadji.mNadjiKolicinu(connection,
									Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									Integer.parseInt(tt[1].getText().trim()),
									Integer.parseInt(tt[2].getText().trim()));
			dispKol.setText(String.valueOf(stanje));	
			dispCen.setText(myFormatter.format(anadji.mNadjiPRC(connection,
									Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									Integer.parseInt(tt[1].getText().trim()),
									Integer.parseInt(tt[2].getText().trim()))));
			tt[13].setText(dispCen.getText().trim());
			if (stanje<0)
			{
				oznaka="+";
			}
	}
 //-------------------------------------------------------------------------
  public boolean proveriSvaPolja(){
	  boolean prv = false;
	  int i = 0;
		for (i=0;i<6 ;i++ )
		{
			if (tt[i].getText().trim().length() == 0)
				prv = true;
		}
		//ovo zbog polja Marza
		for (i=11;i<15 ;i++ )
		{
			if (tt[i].getText().trim().length() == 0)
				prv = true;
		}
		for (i=0;i<3 ;i++ )
		{
			if (t[i].getText().trim().length() == 0)
				prv = true;
		}
		return prv;
 }
//-------------------------------------------------------------------------------------
   public void izmeniDatumNaloga() {
        String sql = "UPDATE "+tblpromet+" SET datumnaloga='" + konvertujDatum(fmtDatNal.getText().trim()) + 
			"'" + " WHERE jur=" +
				Integer.parseInt(t[0].getText().trim()) +
				" AND ui=2 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND brun=" + Integer.parseInt(t[2].getText().trim()) +
				" AND pre=" +Integer.parseInt(pPre) +
				" AND indikator=0 AND (vrdok = 3 OR vrdok=66)";
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
						//proverimagacin();
						if (proverimagacin()){
							brNaloga();
							t[2].setSelectionStart(0);
							t[2].requestFocus();
						}else{
							t[1].setSelectionStart(0);
							t[1].requestFocus();
						}	
					}
				}
				//broj dokumenta
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0){
						t[2].requestFocus();
					}else{
						imaNemaNaloga();
					}
				}
				//datum
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
						proveriSifm();
						tt[2].setText(koonto);
						tt[2].setSelectionStart(0);
						tt[2].requestFocus();
				}
				//konto
				else if (source == tt[2]){
					if (proveriKonto()){
						proveriCenaKol();
						koonto = tt[2].getText().trim();
						tt[3].setText(daatum);
						tt[3].setSelectionStart(0);
						tt[3].requestFocus();
					}else{
						tt[2].setText("");
						tt[2].setSelectionStart(0);
						tt[2].requestFocus();
					}
				}
				//datum


				else if (source == tt[3]){
					if (proveriDatum(String.valueOf(tt[3].getText()).trim()) == true){
						if (imaStavku == false)
						{
							daatum = tt[3].getText().trim();
						}
						tt[4].setText("3");
						tt[4].setSelectionStart(0);
						tt[4].requestFocus();
					}
					else {
						tt[3].setSelectionStart(0);
						tt[3].requestFocus();
					}
				}
				//vrsta dokumenta
				else if (source == tt[4]){
					if (tt[4].getText().trim().length() == 0){
						tt[4].setText("3");
					}
					if (  Integer.parseInt(tt[4].getText().trim()) != 3  &&  Integer.parseInt(tt[4].getText().trim()) != 66 ){		
						tt[4].setText("3");
					}
					if (imaStavku == false){
						tt[5].setText(brojdokumenta);
					}
					tt[5].setSelectionStart(0);
					tt[5].requestFocus();}
				//broj dokumenta
				else if (source == tt[5]){
					if (tt[5].getText().trim().length() == 0){
						tt[5].setText(t[2].getText().trim());
					}
					brojdokumenta = tt[5].getText().trim();

					if (!mestotroska.trim().equals(""))
					{tt[11].setText(mestotroska);}
					
					tt[11].setSelectionStart(0);
					tt[11].requestFocus();
				}
				//valuta
				else if (source == tt[11]){
					if (tt[11].getText().trim().length() == 0){
						JOptionPane.showMessageDialog(this, "Mora se uneti mesto troska");
						tt[11].setText("50000");
						tt[11].setSelectionStart(0);
						tt[11].requestFocus();
					}else{
						if(proveriTrosak()){
							mestotroska = tt[11].getText().trim();
							tt[12].setSelectionStart(0);
							tt[12].requestFocus();
						}else{
							tt[11].setText("");
							tt[11].setSelectionStart(0);
							tt[11].requestFocus();
						}
					}
				}					
				//opis
				else if (source == tt[12]){
					if (tt[12].getText().trim().length() == 0){
						tt[12].setText("0");
					}
						//Kontrola minusa
						double kol;
						kol = Double.parseDouble(tt[12].getText().trim());

						if (uminus == false)
						{
							if (stanje - kol + stariDug < 0)
								{
									JOptionPane.showMessageDialog(this, "Kolicina ne moze u minus !!!");
									tt[12].setSelectionStart(0);
									tt[12].requestFocus();
								}
								else{
									//izrCenu();
									tt[13].setSelectionStart(0);
									tt[13].requestFocus();
								    }
						}
						else {
							//izrCenu();
							tt[13].setSelectionStart(0);
							tt[13].requestFocus();
						}
					}
				//mesec PDV
				else if (source == tt[13]){
					if (tt[13].getText().trim().length() == 0){
						tt[13].setText("0");}
					izrVrednost();
				}
				//mesec PDV
				else if (source == tt[14]){
					prenosDaNe = true;
					if (tt[14].getText().trim().length() == 0){
						tt[14].setText("0");
					}
					//ispravkaVrednosti();
						tt[1].setEditable(true);
						tt[2].setEditable(true);
						tt[3].setEditable(true);
						tt[4].setEditable(true);
						//tt[5].setEditable(true);
						tt[11].setEditable(true);
					if (proveriSvaPolja())
					{
						JOptionPane.showMessageDialog(this, "Sva polja moraju biti popunjena");
						tt[0].requestFocus();} 
					else{

							if (ttt[4].getText().trim().length() == 0){ttt[4].setText("0");}
							jurr = Integer.parseInt(t[0].getText().trim());
							magr = Integer.parseInt(t[1].getText().trim());
							kontor = Integer.parseInt(tt[2].getText().trim());
							sifmr = Integer.parseInt(tt[1].getText().trim());
							s1 = Integer.parseInt(tt[0].getText().trim());
							s2 = Integer.parseInt(ttt[4].getText().trim());

					novaStaraStavka();

				    //Pozivam proceduru za prazuriranje izlaznih cena
					if (s1 <= s2 && uminus==true)
						{
						mServis3 pred = new mServis3(jurr,magr,kontor,sifmr);
						}
						tt[0].setSelectionStart(0);
						tt[0].requestFocus();
					}
				}
				//mesec PDV
	}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == tt[4]){
				mozenemoze.setText("(3)Izdatnice (66)Ostalo");
				mozenemoze.setVisible(true);}
	}
//------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
			if (source == tt[4]){
				mozenemoze.setVisible(false);
			}
	}
}//end of class FL=============================================================================
//===========================================================================
class ML extends MouseAdapter{
	public void mousePressed(MouseEvent e) {
		Object source = e.getSource();
		if (source == jtbl){
			int kojirec = jtbl.getSelectedRow();
			//tt[0].setText(String.valueOf(rbrpodaci.get(kojirec)));
			//tt[0].setText(jtbl.getValueAt(kojirec,0).toString());
			tt[0].setText(jtbl.getValueAt(kojirec,0).toString());
			proveriUnosStavka();
		}
	}
}//end of class ML	
//===========================================================================
class mQTMIzdat extends AbstractTableModel {
   Connection dbconn;
   Vector<Object[]> totalrows;
   String[] colheads = {"Stav","Br.un.","Sifra","Naziv","Konto","Datum","Vr.Dok","Br.Dok",
		"M.Troska","Kolic","Cena ","Vrednost"};
   public mQTMIzdat(Connection dbc){
	dbconn = dbc;
      totalrows = new Vector<Object[]>();
   }
 //---------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 12; }
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
	for (i=j;i<11 ;i++ )
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
		sql = _sql;
		Statement statement = null;
		podaci.clear();
		spodaci.clear();
		rbrpodaci.clear();
      try {
         statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
		totalrows = new Vector<Object[]>();
            while ( rs.next() ) {

               Object[] record = new Object[12];
               record[0] = rs.getString("rbr");
               record[1] = rs.getString("brun");
               record[2] = rs.getString("sifm");
               record[3] = rs.getString("nazivm");
               record[4] = rs.getString("konto");
               record[5] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[6] = Formatiraj(rs.getString("vrdok"));
			   record[7] = rs.getString("brdok");
               record[8] = Formatiraj(rs.getString("mtros"));
               record[9] = rs.getBigDecimal("ROUND(kolic,2)");
               record[10] = rs.getBigDecimal("ROUND(cena,2)");
               record[11] = rs.getBigDecimal("ROUND(vredn,2)");
               
			   rbrpodaci.addElement(record[0]);
			   podaci.addElement(record[4]);
			   spodaci.addElement(record[7]);
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
}//end of class mUnosIzdat
//==============================================================================
class munosCLLIzdat implements LayoutManager {

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