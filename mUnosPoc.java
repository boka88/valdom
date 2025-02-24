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

public class mUnosPoc extends JInternalFrame implements InternalFrameListener{
	private JButton novi,unos,brisi;
	private int brnal;
	private int jurr,magr,kontor,sifmr;
	public static JFormattedTextField displej,dispVrsDok,
		mozenemoze,mozenemoze1,dispMT,dispKol,dispCen;
	private Connection connection = null;
    public static FormField t[],tt[],ttt[],fmtDatNal;
    private JLabel l[],lab[];
    int n_fields;
	private int rbr;
	private JScrollPane jspane;
   	private mQTM7Poc qtbl;
   	private JTable jtbl;
	private Vector<Object> rbrpodaci = new Vector<Object>();
	private Vector<Object> datpodaci = new Vector<Object>();
	private boolean imaNemaNalog=false;
	private boolean imaStavku=false;
  	private double ddug,ppot,saldo,dugFzkon,potFzkon,stariDug=0.0,stariPot=0.0,vrednost=0.0,kolicina=0.0,prcn,stanje=0.0;
	private int brStav,stariKonto,noviKonto,staraSifra,novaSifra,zauzet=0;
	private String trenutniDatum,pPre,nazivPre,date,fmm,godina,oznaka=""
			,upit,imebaze,godrada;
	private boolean ff9= false,uminus;
	private String koonto="",tblpromet="mprom";
	private aNadji anadji = null;
	private String pattern = "#########.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);
	private int tarifnibroj=1;
	private double procenatporeza=0;
	private classBrisanje cllBrisi = null;


//-------------------------------------------------------------------------------------------------
    public mUnosPoc() {
		super("", true, true, true, true);
        setTitle("Robno Materijalno-Po\u010detno stanje         <F9> - \u0160ifarnici");
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

		uzmiKonekciju();

		anadji = new aNadji();
		cllBrisi = new classBrisanje();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		godina = date.substring(6,10);

		date = "01/01/" + godina;

		//uzima koje je preduzece iz forme za izbor preduzeca
		pPre = new String("1");
		tblpromet = tblpromet + pPre.trim();

		uminus = true;
		nazivPre = new String("Valdom");
	    imebaze = ConnMySQL.imebaze;
	    //imebaze = "baza2006";
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

				if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 )
				{
					upit = "update "+tblpromet+" set z = 0 WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND ui=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND brun=0 AND indikator=0" + 
					" AND pre=" +Integer.parseInt(pPre) +
					" AND vrdok = 0 ";
					Oslobodi(upit);
					obrisiPomocniSlog();
				}
			anadji = null;
		} 
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

	
		fmm = "******";
        l[1] = new JLabel("Magacin:",JLabel.RIGHT);
		t[1] = new FormField(createFormatter(fmm,1));
		t[1].setColumns(4);
		//------------------- programiranje provere i akcije ------------------------
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[1].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziMagacin();}});

		fmm = "*";
        l[2] = new JLabel("Br.unosa :",JLabel.RIGHT);
		t[2] = new FormField(createFormatter(fmm,1));
		t[2].setColumns(10);
		t[2].setText("0");
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
        //ttt = new FormField[7]; 
        ttt = new FormField[7]; 
        l = new JLabel[7]; 

		fmm = "***************";
        l[3] = new JLabel("Opis:",JLabel.RIGHT);
		ttt[3] = new FormField(createFormatter(fmm,3));
		ttt[3].setColumns(15);
		ttt[3].setEditable(false);
		ttt[3].setText("Po\u010detno stanje");

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
		dispVrsDok.setFont(new Font("Arial",Font.BOLD,12));

		JLabel kol = new JLabel("Koli\u010dina:");
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
        lab[1]= new JLabel("\u0160ifra robe :   ",JLabel.RIGHT);
        lab[2]= new JLabel("Konto :        ",JLabel.RIGHT);
        lab[3]= new JLabel("Datum :        ",JLabel.RIGHT);
        lab[12]= new JLabel("Koli\u010dina :     ",JLabel.LEFT);
        lab[13]= new JLabel("Cena bez PDV : ",JLabel.LEFT);
        lab[14]= new JLabel("Vredn.bez PDV: ",JLabel.LEFT);
        tt = new FormField[15]; 

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
		tt[3].setColumns(10);
		//------------------- programiranje provere i akcije ------------------------
        tt[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[3]);}});
		fmm = "*******";									//Kolicina										
        tt[12] = new FormField(createFormatter(fmm,5));
		tt[12].setColumns(7);
		//------------------- programiranje provere i akcije ------------------------
        tt[12].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[12].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[12]);}});

		fmm = "**********";									//Cena bez PDV
        tt[13] = new FormField(createFormatter(fmm,5));
		tt[13].setColumns(10);
		//------------------- programiranje provere i akcije ------------------------
        tt[13].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[13].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[13]);}});

		fmm = "**********";									//Vrednost bez PDV
        tt[14] = new FormField(createFormatter(fmm,5));
		tt[14].setColumns(10);
		//------------------- programiranje provere i akcije ------------------------
        tt[14].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        tt[14].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(tt[14]);}});

	}
//--------------------------------------------------------------------------------------------------
	private JPanel buildPanel1(){
		JPanel p1 = new JPanel();
		p1.setLayout( new munosCLLPoc() );

		mozenemoze = new JFormattedTextField(); 
		mozenemoze.setColumns(20);
		mozenemoze.setEditable(false);
		mozenemoze.setFont(new Font("Arial",Font.PLAIN,9));
		mozenemoze.setText(" ");
		mozenemoze.setVisible(false);

		JLabel moze = new JLabel("      ");
		moze.setVisible(false);

		int i;
	    for(i=0;i<4;i++){ 
            p1.add(lab[i]); 
            p1.add(tt[i]); }

		p1.add(moze);
		p1.add(mozenemoze);

		return p1;
	}
//--------------------------------------------------------------------------------------------------
	private JPanel buildPanel2(){
		JPanel p2 = new JPanel();
		p2.setLayout( new munosCLLPoc() );
		mozenemoze1 = new JFormattedTextField(); 
		mozenemoze1.setColumns(20);
		mozenemoze1.setEditable(false);
		mozenemoze1.setFont(new Font("Arial",Font.PLAIN,9));
		mozenemoze1.setText(" ");
		mozenemoze1.setVisible(false);
		JLabel moze = new JLabel("      ");
		moze.setVisible(false);

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
	    for(i=12;i<15;i++){ 
            p2.add(lab[i]); 
            p2.add(tt[i]); }
		p2.add(moze);
		p2.add(mozenemoze1);
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
						tt[0].setSelectionStart(0);
						tt[0].requestFocus();} 
					else{
					ispravkaVrednosti();

							jurr = Integer.parseInt(t[0].getText().trim());
							magr = Integer.parseInt(t[1].getText().trim());
							kontor = Integer.parseInt(tt[2].getText().trim());
							sifmr = Integer.parseInt(tt[1].getText().trim());
							double kol=0.0;
							kol = Double.parseDouble(tt[12].getText().trim());

					novaStaraStavka();

				    //Pozivam proceduru za prazuriranje izlaznih cena
					if (oznaka.equals("+") || (stanje + kol - stariDug) < 0)
						{
							mServis3 pred = new mServis3(jurr,magr,kontor,sifmr);
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
			//connect,pre,jur,mag,vrdok,brun,rbr
			//proverava da li je stavka preneta u finansijsko
			
				//proverava da li je kolicina nulirana
				if(cllBrisi.proveriStavkuRobnoKolic(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												0,
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())))
				{

					cllBrisi.izbrisiStavkuRobno(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												0,
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())
												);
					cllBrisi.preazurirajRbrRobno(connection,Integer.parseInt(pPre),
												Integer.parseInt(t[0].getText().trim()),
												Integer.parseInt(t[1].getText().trim()),
												0,
												Integer.parseInt(t[2].getText().trim()),
												Integer.parseInt(tt[0].getText().trim())
												);
					String sqll = "SELECT rbr,sifm,konto,nazivm,datum,ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2)" +
						" FROM "+tblpromet+" WHERE brun=0 AND vrdok=0" +
						" AND ui=0 AND mag= " + Integer.parseInt(t[1].getText().trim()) +
						" AND jur= " + Integer.parseInt(t[0].getText().trim()) +
						" AND pre= " + Integer.parseInt(pPre);

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

	   	qtbl = new mQTM7Poc(connection);
		String sql;
		sql = "SELECT rbr,sifm,konto,nazivm,datum,ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " + 
			"FROM "+tblpromet+" WHERE konto=9999";
	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); 
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); 
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
 	   	
		//jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());

	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(50);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(180);

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
        int i;
		rbr = 1;
		for(i=0;i<3;i++){
            t[i].setText("");
		}
        for(i=0;i<4;i++){
            tt[i].setText("");
		}
        for(i=12;i<15;i++){
            tt[i].setText("");
		}
        for(i=3;i<5;i++){
            ttt[i].setText("");
		}
		fmtDatNal.setText("");
		displej.setText("");
		dispVrsDok.setText("");
		dispKol.setText("");
		dispCen.setText("");
		fmtDatNal.setText(date);
		koonto="";
		String sql = "SELECT rbr,sifm,konto,nazivm,datum,ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2) " +
			"FROM "+tblpromet+" WHERE konto=9999";
		popuniTabelu(sql);
		fmtDatNal.setSelectionStart(0);
		mozenemoze.setVisible(false);
		mozenemoze1.setVisible(false);
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
        for(i=0;i<4;i++){
            tt[i].setText("");
		}
        for(i=12;i<15;i++){
            tt[i].setText("");
		}
		tt[3].setText(trenutniDatum);
    }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
       	this.setVisible(false);
    } 
//------------------------------------------------------------------------------------------------------------------
	public void obrisiPomocniSlog() {
	  
	  anadji.obrisiPomocniNultiSlog(connection,Integer.parseInt(pPre),
								Integer.parseInt(t[0].getText().trim()),
								Integer.parseInt(t[1].getText().trim()),
								0,0,
								Integer.parseInt(t[2].getText().trim()),
								" AND vrdok=0");
 }
//-------------------------------------------------------------------------------------------------
   public void Oslobodi(String _upit) {
	  //PROCEDURA ZA OSLOBADJANJE ZAUZETOG NALOGA

	if (zauzet == 0)
	{
      try {
         Statement statement = connection.createStatement();
				if ( imaNemaNalog )
				{
					statement.executeUpdate( _upit );
				}
         		statement.close();
       			statement = null;
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u upitu Oslobodi");
		JOptionPane.showMessageDialog(this, upit);
      }
	}
  } 
//-------------------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		try{
			connection = aLogin.konekcija;
		}catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee preuzeti konekciju:"+f);
		}
    }
//-------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(50);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(180);
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
	}else{
		_jur = 0;
		_mag = 0;
	}
		_konto = 0;
		_brun = 0;
		String ddatum =	String.valueOf(datpodaci.get(0));

		jPrintPocStanje mpc = new jPrintPocStanje(connection,_jur,_mag);
	}
//---------------------------------------------stavke----------------------------------------------
   public void proveriStavku() {
 
		Statement statement = null;
      try {
         statement = connection.createStatement();
         if ( !tt[0].getText().equals( "" )) {
               String sql = "SELECT * FROM "+tblpromet+" WHERE" +
			" brun=0" +  
			" AND rbr=" + Integer.parseInt(tt[0].getText().trim()) +
			" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
			" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
			" AND pre=" + Integer.parseInt(pPre);

		         ResultSet rs = statement.executeQuery( sql );
		         if(rs.next()){
					stariDug = rs.getDouble("kolic");		//uzima kolicinu stare stavke
					
					tt[0].setText(rs.getString("rbr"));				//Stavka - rbr
					tt[1].setText(rs.getString("sifm")); 			//Sifra materijala - robe
					tt[2].setText(rs.getString("konto"));			//Konto
					tt[3].setText(konvertujDatumIzPodataka(rs.getString("datum")));	//Datum
					tt[12].setText(rs.getString("kolic")); 			//Kolicina
					tt[13].setText(rs.getString("cena"));			//jur
					tt[14].setText(rs.getString("vredn"));			//vrednost bez PDV
		
					koonto = rs.getString("konto");
					if (tt[14].getText().trim().length() != 0){
						tt[3].setSelectionStart(0);
						tt[3].requestFocus();
					}

					imaStavku = true;
					proveriKonto();
					proveriSifm();

					tt[1].setSelectionStart(0);
					tt[1].requestFocus();
				 }
		}else{
				JOptionPane.showMessageDialog(this, "Broj stavke nije unet");
        }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u proveriStavku:" + sqlex);
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
				double cena,vrednost,kol,pdv,zav,pcena,pcena1,pvredn;
				cena = Double.parseDouble(tt[13].getText().trim());
				kol = Double.parseDouble(tt[12].getText().trim());
				vrednost = kol * cena;
				ddat =String.valueOf(tt[3].getText()).trim();
				dddat = konvertujDatum(ddat);
				trenutniDatum = ddat;

        String query = "INSERT INTO "+tblpromet+"(pre,jur,mag,ui,indikator,brun,rbr,sifm,konto," +
					"datum,datumnaloga,valuta,vrdok,brdok,kupacbr,brrac,fak,sar,kolic,kolicul,pcena,vredn,cena," +
					"pcena1,pvredn,vrtar,tarbr,por1,zav,mar,taksa,uvoz,mesec,mtros,nazivm,z)" +
				" VALUES (" + Integer.parseInt(pPre) + "," +		//Pre-1
			Integer.parseInt(t[0].getText().trim())+ "," + 			//jur-2
			Integer.parseInt(t[1].getText().trim())+ ",0,0," + 		//Magacin, ui, indikator-3-4-5
			Integer.parseInt(t[2].getText().trim())+ "," + 			//Broj unosa-6
			Integer.parseInt(tt[0].getText().trim())+ "," + 		//Stavka - rbr-7
			Integer.parseInt(tt[1].getText().trim())+ "," + 		//Sifra materijala - robe-8
			Integer.parseInt(tt[2].getText().trim())+ ",'" + 		//Konto-9
			dddat + "','" + 										//Datum-10
			konvertujDatum(fmtDatNal.getText().trim()) + "','" +	//Datum-naloga
			dddat + "'," + 											//Valuta
			"0," + 													//vrsta dokum.-11
			"0," + 													//Broj dokumenta-12
			"0," + 													//Sifra saradnika-13
			"0," + 													//Broj racuna-14
			"0," + 													//Fakturisano ili ne-15
			"0," + 													//Indikator da li je saradnik ili kooperant-16
			kol + "," + 											//Kolicina-17
			kol + "," + 											//Kolicina ulaz
			cena + "," +											//Cena bez PDV-a i zav.troskova(Ulazna)-(pcena)-18
			vrednost + "," +										//vrednost bez PDV + zavisni trosak-Magacinska(vredn)-19
			cena + "," +		    								//cena magacinska(BEZ PDV + ZAVISNI TROSAK) -(cena)20
			cena + "," +		                                    //pcena1-Cena sa PDV ALI BEZ ZAVISNIH TROSKOAVA -21
			vrednost + "," +										//Vrednost bez PDV i zavisnih troskova - pvredn 22
			"0," +													//vrtar vrsta poreza
			tarifnibroj + "," +										//Tarifni broj
			"0," +													//Porez - por1
			"0," +													//Zavisni troskovi
			"0,0," +												//Marza, Taksa
			"0," +													//Uvoz ili nije uvoz
			"0," +													//Mesec PDV
			"0,'" + 												//Mesto troska
			displej.getText() + "',1)"; 							//Naziv robe ,zauzeto

               int result = statement.executeUpdate( query );
               if ( result == 1 ){
				rbr = rbr + 1;
	         		statement.close();
         			statement = null;

               	String sqll = "SELECT rbr,sifm,konto,nazivm,datum,ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2)" +
					" FROM "+tblpromet+" WHERE brun=0 AND vrdok=0" +
					" AND ui=0 AND mag= " + Integer.parseInt(t[1].getText().trim()) +
					" AND jur= " + Integer.parseInt(t[0].getText().trim()) +
					" AND pre= " + Integer.parseInt(pPre);

				popuniTabelu(sqll);
				imaStavku = false;
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
			JOptionPane.showMessageDialog(this, "Greska u unosu stavke");
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
				String ddat,dddat,dval,ddval;
				ddat = String.valueOf(tt[3].getText().trim());
				dddat = konvertujDatum(ddat);
			
			String query = "UPDATE "+tblpromet+" SET " +
			"datum='" + dddat + "'," + 										//Datum
			"datumnaloga='" + konvertujDatum(fmtDatNal.getText().trim()) + "'," +//Datum naloga
			"valuta='" + dddat + "'," + 									//Valuta
			"sifm=" + Integer.parseInt(tt[1].getText().trim()) + ", " + 	//sifra robe
			"konto=" + Integer.parseInt(tt[2].getText().trim()) + ", " + 	//konto
			"kolic=" + Double.parseDouble(tt[12].getText().trim())+ ", " + 	//Kolicina
			"kolicul=" + Double.parseDouble(tt[12].getText().trim())+ ", " + //Kolicina ulaz
			"cena=" + Double.parseDouble(tt[13].getText().trim())+ ", " +	//pcena1
			"vredn=" + Double.parseDouble(tt[14].getText().trim())+ ", " +	//pcena1
			"pcena=" + Double.parseDouble(tt[13].getText().trim())+ ", " +	//pcena1
			"pcena1=" + Double.parseDouble(tt[13].getText().trim())+ ", " +	//pcena1
			"pvredn=" + Double.parseDouble(tt[14].getText().trim())+		//Vrednost bez PDV
			" WHERE brun=0 " + 
				" AND rbr=" + Integer.parseInt(tt[0].getText().trim()) +
				" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
				" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
				" AND pre= " + Integer.parseInt(pPre) + " AND vrdok=0";

               int result = statement.executeUpdate( query );

               if ( result == 1 ){
               	String sqll = "SELECT rbr,sifm,konto,nazivm,datum,ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2)" +
					" FROM "+tblpromet+" WHERE brun=0" +
					" AND ui=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND jur=" + Integer.parseInt(t[0].getText().trim()) +
					" AND pre=" + Integer.parseInt(pPre);
				popuniTabelu(sqll);
				imaStavku = true;
				ocistiSlog();
				novaStavka();
			   }
				else {
            		JOptionPane.showMessageDialog(this, "Stavka nije izmenjena");
					NoviPressed();
				}
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

					displej.setText("");
					tt[3].setText(fmtDatNal.getText());
			brStav = anadji.mNadjiNovuStavku(connection,Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									0,0,0," AND vrdok = 0");
			ocistiSlog();
			tt[0].setText(String.valueOf(brStav));
			tt[0].setSelectionStart(0);
			tt[0].requestFocus();
}
//-------------------------------------------------------------------------------------
   public void prikaziNalog() {
		Statement statement = null;
      try {
         statement = connection.createStatement();
         if ( !t[0].getText().equals( "" ) && !t[1].getText().equals( "" )
			 && !t[2].getText().equals( "" )) {
               		String sql = "SELECT rbr,sifm,konto,nazivm,datum,ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2)" +
						" FROM "+tblpromet+" WHERE jur=" +
						Integer.parseInt(t[0].getText().trim()) +
						" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
						" AND brun=0 AND vrdok=0" + 
						" AND pre=" +Integer.parseInt(pPre) + 
						" AND ui=0 AND indikator=0";
			try {
		         ResultSet rs = statement.executeQuery( sql );
		         if(rs.next()){
					
					popuniTabelu(sql);
					imaNemaNalog = true;
					novaStavka();
				 }else{
					zauzet = 1;
					novaStavka();
				 }
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, "Slog ne postoji ili je zauzet:"+sqlex);
					zauzet=1;
				try{
					this.setClosed(true);}
					catch (Exception e){
						JOptionPane.showMessageDialog(this, e);}
		      }
		}     
            else {
				JOptionPane.showMessageDialog(this, "Vrsta knjizenja nije uneta");
            }
      }
      catch ( SQLException sqlex ) {
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
//-----------------------------------------------------------------------------
  public void unesiNoviNalog() {

	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 ){

		anadji.unesiPomocniNultiSlog(connection,Integer.parseInt(pPre),
									Integer.parseInt(t[0].getText().trim()),
									Integer.parseInt(t[1].getText().trim()),
									0,0,0,
									konvertujDatum(fmtDatNal.getText().trim()),
									0);
		imaNemaNalog = true;
        String sqll = "SELECT rbr,sifm,konto,nazivm,datum,ROUND(kolic,2),ROUND(cena,2),ROUND(vredn,2)" +
						" FROM "+tblpromet+" WHERE jur=" +
						Integer.parseInt(t[0].getText().trim()) +
						" AND brun=0 AND vrdok=0" + 
						" AND mag=" + Integer.parseInt(t[1].getText().trim()) +
						" AND ui=0 AND indikator=0" +
						" AND pre=" + Integer.parseInt(pPre);
		popuniTabelu(sqll);
		novaStavka();
	}else{
		JOptionPane.showMessageDialog(this, "Prvo popunite sva polja");
		t[0].requestFocus();
	}
  
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
						tt[2].setText(koonto);
						tt[2].setSelectionStart(0);
						tt[2].requestFocus();
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
		         		displej.setText(String.valueOf(rs.getString("nazivkupca")));
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
		if (brnal == 0){
			prikaziNalog();
			displej.setText("");
			tt[0].setSelectionStart(0);
			tt[0].requestFocus();}
		else{
			displej.setText("");
			fmtDatNal.setSelectionStart(0);
			fmtDatNal.requestFocus();
		}
					upit = "update "+tblpromet+" set z = 1 WHERE jur=" +
					Integer.parseInt(t[0].getText().trim()) +
					" AND ui=0 AND mag=" + Integer.parseInt(t[1].getText().trim()) +
					" AND brun=0 AND indikator=0" + 
					" AND pre=" +Integer.parseInt(pPre) +
					" AND vrdok = 0 ";
					Oslobodi(upit);
					t[0].setEditable(false);
					t[1].setEditable(false);
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
		            Integer.parseInt(t[0].getText().trim())+
			   		" AND pre=" + Integer.parseInt(pPre);

			try {
		         ResultSet rs = statement.executeQuery( query );
		         rs.next();
					dispVrsDok.setText(rs.getString("nazjur"));
					ima = true;
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
//-------------------------------------------------------------------------
   public void prikaziRobu(){
		int t = 2;
		if (imaStavku == false)
		{
			mRobaPrik sk = new mRobaPrik(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
		}
   }
//-------------------------------------------------------------------------
   public void prikaziKonto(){
		int t = 3;
		if (imaStavku == false)
		{
			mKontPlan sk = new mKontPlan(connection,t);
			sk.setModal(true);
			sk.setVisible(true);
		}
   }
//------------------------------------------------------------------------------------------------------------------
   public void prikaziMagacin(){
		int t = 11;
		mMagPrik sk = new mMagPrik(connection,t);
		sk.setVisible(true);
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
			if (stanje<0)
			{
				oznaka="+";
			}
	}
//-------------------------------------------------------------------------
   public void izrVrednost(){
		double cena,cena1;
		cena=0;
		kolicina=0;
		vrednost=0;
		cena = Double.parseDouble(tt[13].getText().trim());
		kolicina = Double.parseDouble(tt[12].getText().trim());
		vrednost = cena * kolicina;
		tt[14].setText(String.valueOf(vrednost));
		tt[14].setSelectionStart(0);
		tt[14].requestFocus();
   }
//-------------------------------------------------------------------------
  public void ispravkaVrednosti(){
			if (kolicina == 0 )
			{
				vrednost = 0;
				tt[12].setText(String.valueOf(0));
				tt[13].setText(String.valueOf(0));
				tt[14].setText(String.valueOf(0));
		    }
			else {
				tt[13].setText(String.valueOf(Double.parseDouble(tt[14].getText().trim())/kolicina));
			}
			
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
		//ovo zbog polja Marza
		for (i=12;i<15 ;i++ )
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
//=========================================================================
	public void Akcija(FormField e) {
		FormField source;
		source = e;
				//jur
				if (source == t[0]){
					if (t[0].getText().trim().length() == 0){
						t[0].setText("1");
					}
					t[1].requestFocus();
				}
				//magacin
				else if (source == t[1]){
					//proverimagacin();
					if (proverimagacin())
					{
						t[2].setText("0");
						t[2].requestFocus();
					}
					
				}
				//broj dokumenta
				else if (source == t[2]){
					t[2].setText("0");
					imaNemaNaloga();
				}
				//datum
				else if (source == fmtDatNal){
					if (proveriDatum(String.valueOf(fmtDatNal.getText()).trim()) == true){
						fmtDatNal.setEditable(false);
						if (imaNemaNalog)
						{
							//izmeniDatumNaloga();
							tt[0].setSelectionStart(0);
							tt[0].requestFocus();
						}else{
							unesiNoviNalog();
						}
					}
					else{
						fmtDatNal.setSelectionStart(0);
						fmtDatNal.requestFocus();
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
						proveriSifm();
						tt[2].setText(koonto);
						tt[2].setSelectionStart(0);
						tt[2].requestFocus();
					}
				}
				//konto
				else if (source == tt[2]){
					if (tt[2].getText().trim().length() == 0){
						tt[2].requestFocus();
					}else{
						proveriCenaKol();
						koonto = tt[2].getText().trim();
						tt[3].setText(fmtDatNal.getText().trim());
						tt[3].setSelectionStart(0);
						tt[3].requestFocus();
					}
				}
				//datum
				else if (source == tt[3]){
					if (proveriDatum(String.valueOf(tt[3].getText()).trim()) == true){
						tt[12].setSelectionStart(0);
						tt[12].requestFocus();}
					else {
						tt[3].setSelectionStart(0);
						tt[3].requestFocus();}
				}

				//opis
				else if (source == tt[12]){
					tt[12].setSelectionStart(0);
					if (tt[12].getText().trim().length() == 0){
						tt[12].setText("0");
					}

						//Kontrola minusa
						double kol;
						kol = Double.parseDouble(tt[12].getText().trim());

						if (uminus == false)
						{

							if (stanje + kol - stariDug < 0)
								{
									JOptionPane.showMessageDialog(this, "Kolicina ne moze u minus !!!");
									tt[12].setSelectionStart(0);
									tt[12].requestFocus();
								}
								else{
									tt[13].setSelectionStart(0);
									tt[13].requestFocus();

								    }
						}
						else {
							tt[13].setSelectionStart(0);
							tt[13].requestFocus();
						}
				}
				//mesec PDV
				else if (source == tt[13]){
					if (tt[13].getText().trim().length() == 0){
						tt[13].setText("0");}
					izrVrednost();
					//tt[14].requestFocus();
					}
				//mesec PDV
				else if (source == tt[14]){
					if (tt[14].getText().trim().length() == 0){
						tt[14].setText("0");
					}

							tt[1].setEditable(true);
							tt[2].setEditable(true);
							tt[3].setEditable(true);
					unos.requestFocus();
				}
	}
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
				mozenemoze1.setVisible(true);}
	}
//------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
			if (source == tt[4] || source == tt[6] || source == tt[8] || source == tt[10]){
				mozenemoze.setVisible(false);
				mozenemoze1.setVisible(false);}
	}
}//end of class FL=============================================================================
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
class mQTM7Poc extends AbstractTableModel {
   Connection dbconn;
   Vector<Object[]> totalrows;
   String[] colheads = {"Broj Stavke","\u0160ifra","Konto","Naziv","Datum","Koli\u010dina","Cena bez PDV","Vrednost bez PDV"};
   public mQTM7Poc(Connection dbc){
	dbconn = dbc;
      totalrows = new Vector<Object[]>();
   }
 //---------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 8; }
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
	for (i=j;i<7 ;i++ )
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
		rbrpodaci.clear();
		datpodaci.clear();
		Statement statement = null;
      try {
         statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
		totalrows = new Vector<Object[]>();
            while ( rs.next() ) {

               Object[] record = new Object[8];
               record[0] = rs.getString("rbr");
               record[1] = rs.getString("sifm");
               record[2] = rs.getString("konto");
               record[3] = rs.getString("nazivm");
               record[4] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[5] = rs.getBigDecimal("ROUND(kolic,2)");
               record[6] = rs.getBigDecimal("ROUND(cena,2)");
               record[7] = rs.getBigDecimal("ROUND(vredn,2)");
               
			   rbrpodaci.addElement(record[0]);
			   datpodaci.addElement(record[4]);
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
class munosCLLPoc implements LayoutManager {

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
