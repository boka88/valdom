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


public class regListPravna extends JInternalFrame implements InternalFrameListener
			{
	private reglistQTM3 qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private boolean izmena = false;
	private String kojiJMBG,kojiRB,date;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj,displej,imakuku,razlog;
   	private JLabel  l[];
   	int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public regListPravna() {
		super("", true, true, true, true);
        setTitle("\u0160tampa registracionog lista Pravna lica");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( null );

		uzmiKonekciju();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		buildTable();
		main.add(buildNaslov());
		main.add(buildPanel());
		getContentPane().add(main);
		pack();
		setSize(790,550);
		t[0].requestFocus();
		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));

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
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNaslov() {
		JPanel naslov = new JPanel();
		naslov.setLayout( null );
		naslov.setBorder( new TitledBorder("") );
		naslov.setBounds(0,0,780,40);

		JLabel disp = new JLabel("Naziv:");
		disp.setBounds(5,5,70,20);

		displej = new JFormattedTextField(); 
		//displej.setColumns(30);
		displej.setBounds(90,5,150,20);
		displej.setEditable(false);
		displej.setFocusable(false);
		displej.setFont(new Font("Arial",Font.BOLD,12));
		displej.setBackground(Color.blue);
		displej.setForeground(Color.white);

        JLabel razl = new JLabel("Razlog popunjavanja: ");
			razl.setBounds(260,5,100,20);
		razl.setFont(new Font("Arial",Font.PLAIN,9));

        String fmm;
		fmm = "**";
		razlog = new JFormattedTextField(createFormatter(fmm,1));
		razlog.setFont(new Font("Arial",Font.PLAIN,9));
			razlog.setBounds(370,5,50,20);
		razlog.setSelectionStart(0);
		/*
        razlog.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        razlog.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(razlog);}});
		*/

		naslov.add(razl);
		naslov.add(razlog);
		naslov.add(disp);
		naslov.add(displej);
		return naslov;
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder("Unos") );
		p.setBounds(0,40,780,510);

		// setBounds(X,Y,sirina,visina)
		int prviLX,prviTX,drugiLX,drugiTX,treciLX,treciTX,cetvrtiLX,cetvrtiTX,visina,razmakY;
		int sirinaL,txt,aa,btnY,btnX,btnsirina,btnrazmak;
		aa = 25;						//rastojanje od gornje ivice panela
		sirinaL = 80;					//sirina labele
		visina = 20;					//visina komponente
		prviLX = 10;					//X-pozicija za prvi red labela
		prviTX = prviLX + sirinaL;		//X-pozicija za prvi red text-box
		drugiLX = 200;
		drugiTX = drugiLX + sirinaL;
		treciLX = 395;
		treciTX = treciLX + sirinaL;
		cetvrtiLX = 590;
		cetvrtiTX = cetvrtiLX + sirinaL;
		razmakY = 3;					// razmak izmedju komponenti po visini
		txt = 10;						// vrednost jedinicnog razmaka u txt - polju
		btnY = 6*(visina + razmakY) + 100;
		btnX = 50;
		btnsirina = 80;
		btnrazmak = 5;

		int i;
        n_fields = 32; 
        t = new JFormattedTextField[32]; 
        l = new JLabel[32]; 

		
		String fmm;
		fmm = "*************";
        l[0] = new JLabel("JMBG ili matbr :");
			l[0].setBounds(prviLX,aa,sirinaL,visina);
		l[0].setFont(new Font("Arial",Font.PLAIN,9));
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setFont(new Font("Arial",Font.PLAIN,9));
		//t[0].setColumns(10);
			t[0].setBounds(prviTX,aa,10*txt,visina);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        t[0].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziKorisnike();}});
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0),"check2");
        t[0].getActionMap().put("check2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziPravna();}});

		fmm = "***************";
		l[1] = new JLabel("Broj registracije :");
			l[1].setBounds(prviLX,aa + visina + razmakY,sirinaL,visina);
		l[1].setFont(new Font("Arial",Font.PLAIN,9));
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
		t[1].setFont(new Font("Arial",Font.PLAIN,9));
		//t[1].setColumns(20);
			t[1].setBounds(prviTX,aa + visina + razmakY,10*txt,visina);
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "**********";
        l[2] = new JLabel("Br. registra :");
 			l[2].setBounds(prviLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[2].setFont(new Font("Arial",Font.PLAIN,9));
		t[2] = new JFormattedTextField(createFormatter(fmm,1));
		t[2].setFont(new Font("Arial",Font.PLAIN,9));
		t[2].setColumns(20);
		t[2].addFocusListener(new FL());
			t[2].setBounds(prviTX,aa + 2*(visina + razmakY),10*txt,visina);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "*";
        l[3] = new JLabel("Vrsta (1-9):");
			l[3].setBounds(prviLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[3].setFont(new Font("Arial",Font.PLAIN,9));
        t[3] = new JFormattedTextField(createFormatter(fmm,1));
		t[3].setFont(new Font("Arial",Font.PLAIN,9));
		t[3].setColumns(20);
			t[3].setBounds(prviTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "********************";
		l[4] = new JLabel("Vrsta vozila :");
			l[4].setBounds(prviLX,aa + 4*(visina + razmakY),sirinaL,visina);
		l[4].setFont(new Font("Arial",Font.PLAIN,9));
        t[4] = new JFormattedTextField(createFormatter(fmm,3));
		t[4].setFont(new Font("Arial",Font.PLAIN,9));
		t[4].setColumns(14);
			t[4].setBounds(prviTX,aa + 4*(visina + razmakY),10*txt,visina);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		l[5] = new JLabel("Oznaka JUS :");
			l[5].setBounds(prviLX,aa + 5*(visina + razmakY),sirinaL,visina);
		l[5].setFont(new Font("Arial",Font.PLAIN,9));
        t[5] = new JFormattedTextField(createFormatter(fmm,3));
		t[5].setFont(new Font("Arial",Font.PLAIN,9));
		t[5].setColumns(14);
			t[5].setBounds(prviTX,aa + 5*(visina + razmakY),10*txt,visina);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

        l[6] = new JLabel("Marka :");
			l[6].setBounds(prviLX,aa + 6*(visina + razmakY),sirinaL,visina);
		l[6].setFont(new Font("Arial",Font.PLAIN,9));
        t[6] = new JFormattedTextField(createFormatter(fmm,3));
		t[6].setFont(new Font("Arial",Font.PLAIN,9));
		t[6].setColumns(10);
			t[6].setBounds(prviTX,aa + 6*(visina + razmakY),10*txt,visina);
		t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

        l[7] = new JLabel("Tip :");
			l[7].setBounds(prviLX,aa + 7*(visina + razmakY),sirinaL,visina);
		l[7].setFont(new Font("Arial",Font.PLAIN,9));
        t[7] = new JFormattedTextField(createFormatter(fmm,3));
		t[7].setFont(new Font("Arial",Font.PLAIN,9));
		t[7].setColumns(20);
			t[7].setBounds(prviTX,aa + 7*(visina + razmakY),10*txt,visina);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});
		//--------------------------------------------------

		fmm = "******************************";
		l[8] = new JLabel("Broj \u0161asije :");
			l[8].setBounds(drugiLX,aa,sirinaL,visina);
		l[8].setFont(new Font("Arial",Font.PLAIN,9));
        t[8] = new JFormattedTextField(createFormatter(fmm,3));
		t[8].setFont(new Font("Arial",Font.PLAIN,9));
		t[8].setColumns(20);
			t[8].setBounds(drugiTX,aa,10*txt,visina);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});
 	
		l[9] = new JLabel("Broj motora :");
			l[9].setBounds(drugiLX,aa + visina + razmakY,sirinaL,visina);
		l[9].setFont(new Font("Arial",Font.PLAIN,9));
        t[9] = new JFormattedTextField(createFormatter(fmm,3));
		t[9].setFont(new Font("Arial",Font.PLAIN,9));
		t[9].setColumns(13);
			t[9].setBounds(drugiTX,aa + visina + razmakY,10*txt,visina);
        t[9].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[9].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[9]);}});

 		fmm = "****";
		l[10] = new JLabel("God. proizvodnje :");
			l[10].setBounds(drugiLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[10].setFont(new Font("Arial",Font.PLAIN,9));
        t[10] = new JFormattedTextField(createFormatter(fmm,1));
		t[10].setFont(new Font("Arial",Font.PLAIN,9));
		t[10].setColumns(20);
			t[10].setBounds(drugiTX,aa + 2*(visina + razmakY),10*txt,visina);
        t[10].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[10].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[10]);}});

 		fmm = "*************************";
		l[11] = new JLabel("Zemlja proizvodnje :");
			l[11].setBounds(drugiLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[11].setFont(new Font("Arial",Font.PLAIN,9));
        t[11] = new JFormattedTextField(createFormatter(fmm,3));
		t[11].setFont(new Font("Arial",Font.PLAIN,9));
		t[11].setColumns(20);
			t[11].setBounds(drugiTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[11].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[11].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[11]);}});

		fmm = "******";
		l[12] = new JLabel("Snaga motora :");
			l[12].setBounds(drugiLX,aa + 4*(visina + razmakY),sirinaL,visina);
		l[12].setFont(new Font("Arial",Font.PLAIN,9));
        t[12] = new JFormattedTextField(createFormatter(fmm,2));
		t[12].setFont(new Font("Arial",Font.PLAIN,9));
		t[12].setColumns(20);
			t[12].setBounds(drugiTX,aa + 4*(visina + razmakY),10*txt,visina);
        t[12].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[12].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[12]);}});

		l[13] = new JLabel("Radna zapremina:");
			l[13].setBounds(drugiLX,aa + 5*(visina + razmakY),sirinaL,visina);
		l[13].setFont(new Font("Arial",Font.PLAIN,9));
        t[13] = new JFormattedTextField(createFormatter(fmm,2));
		t[13].setFont(new Font("Arial",Font.PLAIN,9));
		t[13].setColumns(20);
			t[13].setBounds(drugiTX,aa + 5*(visina + razmakY),10*txt,visina);
        t[13].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[13].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[13]);}});


		l[14] = new JLabel("Masa praznog :");
			l[14].setBounds(drugiLX,aa + 6*(visina + razmakY),sirinaL,visina);
		l[14].setFont(new Font("Arial",Font.PLAIN,9));
        t[14] = new JFormattedTextField(createFormatter(fmm,2));
		t[14].setFont(new Font("Arial",Font.PLAIN,9));
		t[14].setColumns(20);
			t[14].setBounds(drugiTX,aa + 6*(visina + razmakY),10*txt,visina);
        t[14].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[14].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[14]);}});

		l[15] = new JLabel("Nosivost :");
			l[15].setBounds(drugiLX,aa + 7*(visina + razmakY),sirinaL,visina);
		l[15].setFont(new Font("Arial",Font.PLAIN,9));
        t[15] = new JFormattedTextField(createFormatter(fmm,2));
		t[15].setFont(new Font("Arial",Font.PLAIN,9));
		t[15].setColumns(20);
			t[15].setBounds(drugiTX,aa + 7*(visina + razmakY),10*txt,visina);
        t[15].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[15].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[15]);}});
		//----------------------------------------------
 		fmm = "***";
		l[16] = new JLabel("Br. mesta sede\u0107ih :");
			l[16].setBounds(treciLX,aa,sirinaL,visina);
		l[16].setFont(new Font("Arial",Font.PLAIN,9));
        t[16] = new JFormattedTextField(createFormatter(fmm,1));
		t[16].setFont(new Font("Arial",Font.PLAIN,9));
		t[16].setColumns(20);
			t[16].setBounds(treciTX,aa,10*txt,visina);
        t[16].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[16].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[16]);}});

 		fmm = "***";
		l[17] = new JLabel("Br. mesta staja\u0107ih :");
			l[17].setBounds(treciLX,aa + visina + razmakY,sirinaL,visina);
		l[17].setFont(new Font("Arial",Font.PLAIN,9));
        t[17] = new JFormattedTextField(createFormatter(fmm,1));
		t[17].setFont(new Font("Arial",Font.PLAIN,9));
		t[17].setColumns(20);
			t[17].setBounds(treciTX,aa + visina + razmakY,10*txt,visina);
        t[17].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[17].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[17]);}});

 		fmm = "********************";
		l[18] = new JLabel("Oblik, namena :");
			l[18].setBounds(treciLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[18].setFont(new Font("Arial",Font.PLAIN,9));
        t[18] = new JFormattedTextField(createFormatter(fmm,3));
		t[18].setFont(new Font("Arial",Font.PLAIN,9));
		t[18].setColumns(20);
			t[18].setBounds(treciTX,aa + 2*(visina + razmakY),10*txt,visina);
        t[18].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[18].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[18]);}});

 		fmm = "********************";
		l[19] = new JLabel("Osnovna namena :");
			l[19].setBounds(treciLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[19].setFont(new Font("Arial",Font.PLAIN,9));
        t[19] = new JFormattedTextField(createFormatter(fmm,3));
		t[19].setFont(new Font("Arial",Font.PLAIN,9));
		t[19].setColumns(20);
			t[19].setBounds(treciTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[19].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[19].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[19]);}});

		l[20] = new JLabel("Boja osnovna :");
			l[20].setBounds(treciLX,aa + 4*(visina + razmakY),sirinaL,visina);
		l[20].setFont(new Font("Arial",Font.PLAIN,9));
        t[20] = new JFormattedTextField(createFormatter(fmm,3));
		t[20].setFont(new Font("Arial",Font.PLAIN,9));
		t[20].setColumns(20);
			t[20].setBounds(treciTX,aa + 4*(visina + razmakY),10*txt,visina);
        t[20].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[20].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[20]);}});

		l[21] = new JLabel("Boja dodata :");
			l[21].setBounds(treciLX,aa + 5*(visina + razmakY),sirinaL,visina);
		l[21].setFont(new Font("Arial",Font.PLAIN,9));
        t[21] = new JFormattedTextField(createFormatter(fmm,3));
		t[21].setFont(new Font("Arial",Font.PLAIN,9));
		t[21].setColumns(20);
			t[21].setBounds(treciTX,aa + 5*(visina + razmakY),10*txt,visina);
        t[21].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[21].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[21]);}});

 		fmm = "**";
		l[22] = new JLabel("Broj osovina :");
			l[22].setBounds(treciLX,aa + 6*(visina + razmakY),sirinaL,visina);
		l[22].setFont(new Font("Arial",Font.PLAIN,9));
        t[22] = new JFormattedTextField(createFormatter(fmm,1));
		t[22].setFont(new Font("Arial",Font.PLAIN,9));
		t[22].setColumns(20);
			t[22].setBounds(treciTX,aa + 6*(visina + razmakY),10*txt,visina);
        t[22].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[22].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[22]);}});

		l[23] = new JLabel("Br. pogonskih osovina :");
			l[23].setBounds(treciLX,aa + 7*(visina + razmakY),sirinaL,visina);
		l[23].setFont(new Font("Arial",Font.PLAIN,9));
        t[23] = new JFormattedTextField(createFormatter(fmm,1));
		t[23].setFont(new Font("Arial",Font.PLAIN,9));
		t[23].setColumns(20);
			t[23].setBounds(treciTX,aa + 7*(visina + razmakY),10*txt,visina);
        t[23].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[23].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[23]);}});

		fmm = "*";   
		l[24] = new JLabel("Ima ure\u0111aj(1-5):");
			l[24].setBounds(cetvrtiLX,aa,sirinaL,visina);
		l[24].setFont(new Font("Arial",Font.PLAIN,9));
        t[24] = new JFormattedTextField(createFormatter(fmm,1));
		t[24].setFont(new Font("Arial",Font.PLAIN,9));
		t[24].setColumns(20);
			t[24].setBounds(cetvrtiTX,aa,10*txt,visina);
        t[24].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[24].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[24]);}});

 		fmm = "********";
		l[25] = new JLabel("Pre\u0111eno km :");
			l[25].setBounds(cetvrtiLX,aa + visina + razmakY,sirinaL,visina);
		l[25].setFont(new Font("Arial",Font.PLAIN,9));
        t[25] = new JFormattedTextField(createFormatter(fmm,2));
		t[25].setFont(new Font("Arial",Font.PLAIN,9));
		t[25].setColumns(20);
			t[25].setBounds(cetvrtiTX,aa + visina + razmakY,10*txt,visina);
        t[25].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[25].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[25]);}});

 		fmm = "##/##/####";   
		l[26] = new JLabel("Datum registracije :");
			l[26].setBounds(cetvrtiLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[26].setFont(new Font("Arial",Font.PLAIN,9));
        t[26] = new JFormattedTextField(createFormatter(fmm,4));
		t[26].setFont(new Font("Arial",Font.PLAIN,9));
		t[26].setColumns(20);
			t[26].setBounds(cetvrtiTX,aa + 2*(visina + razmakY),10*txt,visina);
        t[26].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[26].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[26]);}});

 		fmm = "*";
		l[27] = new JLabel("Vrsta pog.gor.(1-5):");
			l[27].setBounds(cetvrtiLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[27].setFont(new Font("Arial",Font.PLAIN,9));
        t[27] = new JFormattedTextField(createFormatter(fmm,1));
		t[27].setFont(new Font("Arial",Font.PLAIN,9));
		t[27].setColumns(20);
			t[27].setBounds(cetvrtiTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[27].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[27].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[27]);}});

 		fmm = "*";
		l[28] = new JLabel("Vrsta prevoza(1-2):");
			l[28].setBounds(cetvrtiLX,aa + 4*(visina + razmakY),sirinaL,visina);
		l[28].setFont(new Font("Arial",Font.PLAIN,9));
        t[28] = new JFormattedTextField(createFormatter(fmm,1));
		t[28].setFont(new Font("Arial",Font.PLAIN,9));
		t[28].setColumns(20);
			t[28].setBounds(cetvrtiTX,aa + 4*(visina + razmakY),10*txt,visina);
        t[28].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[28].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[28]);}});

 		fmm = "**";
		l[29] = new JLabel("Broj vrata :");
			l[29].setBounds(cetvrtiLX,aa + 5*(visina + razmakY),sirinaL,visina);
		l[29].setFont(new Font("Arial",Font.PLAIN,9));
        t[29] = new JFormattedTextField(createFormatter(fmm,1));
		t[29].setFont(new Font("Arial",Font.PLAIN,9));
		t[29].setColumns(20);
			t[29].setBounds(cetvrtiTX,aa + 5*(visina + razmakY),10*txt,visina);
        t[29].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[29].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[29]);}});

 		fmm = "**";
		l[30] = new JLabel("Broj to\u010dkova :");
			l[30].setBounds(cetvrtiLX,aa + 6*(visina + razmakY),sirinaL,visina);
		l[30].setFont(new Font("Arial",Font.PLAIN,9));
        t[30] = new JFormattedTextField(createFormatter(fmm,1));
		t[30].setFont(new Font("Arial",Font.PLAIN,9));
		t[30].setColumns(20);
			t[30].setBounds(cetvrtiTX,aa + 6*(visina + razmakY),10*txt,visina);
        t[30].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[30].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[30]);}});

 		fmm = "*";
		l[31] = new JLabel("Radio (1/2):");
			l[31].setBounds(cetvrtiLX,aa + 7*(visina + razmakY),sirinaL,visina);
		l[31].setFont(new Font("Arial",Font.PLAIN,9));
        t[31] = new JFormattedTextField(createFormatter(fmm,1));
		t[31].setFont(new Font("Arial",Font.PLAIN,9));
		t[31].setColumns(20);
			t[31].setBounds(cetvrtiTX,aa + 7*(visina + razmakY),10*txt,visina);
        t[31].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[31].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[31]);}});

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
		unesi.setBounds(btnX + btnsirina + btnrazmak,btnY,btnsirina,20);
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});

		novi = new JButton("Novi slog");
		novi.setMnemonic('N');
		novi.setBounds(btnX,btnY,btnsirina,20);
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});

		izmeni = new JButton("Izmeni");
		izmeni.setMnemonic('I');
		izmeni.setBounds(btnX + 2*(btnsirina + btnrazmak),btnY,btnsirina,20);
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});

		JButton brisi = new JButton("Bri\u0161i slog");
		brisi.setMnemonic('B');
		brisi.setBounds(btnX + 3*(btnsirina + btnrazmak),btnY,btnsirina,20);
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});

		JButton trazi = new JButton("Tra\u017ei");
		trazi.setMnemonic('T');
		trazi.setBounds(btnX,btnY,btnsirina,20);
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});

		JButton stampa = new JButton("\u0160tampa");
		stampa.setMnemonic('P');
		stampa.setBounds(btnX + btnsirina + btnrazmak,btnY,btnsirina,20);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

	    for(i=0;i<32;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
		//p.add(unesi);
		//p.add(novi);
		//p.add(izmeni);
		//p.add(brisi);
		p.add(trazi);
		p.add(stampa);
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
			System.exit(-1);
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
		//int y = screenSize.height - size.height;
		//int x = screenSize.width - size.width;
		int y = 5;
		int x = 2;
		this.setLocation(x,y);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 32; 
        for(i=0;i<n_fields;i++)
            t[i].setText("");
		//t[24].setText(date);
		izmena = false;
		displej.setText("");
		t[0].requestFocus();
    }
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		String jmbg,regoznaka,razl;

		if ( t[0].getText().trim().length()!=0 && 
			t[1].getText().trim().length()!=0 && 
			razlog.getText().trim().length()!=0) {
			jmbg = t[0].getText().trim();
			regoznaka = t[1].getText().trim();
			razl = razlog.getText().trim();
			PrintRegPravna kkl = new PrintRegPravna(jmbg,regoznaka,razl);}
		else{JOptionPane.showMessageDialog(this, "Unesi prvo podatke");}
	}
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
        this.setVisible(false);
    	}
//------------------------------------------------------------------------------------------------------------------
    public void quitForm() {
		// zatvaranje programa-----------------------------
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
//------------------------------------------------------------------------------------------------------------------
	public String vodeceNule(String _forma, int koliko) {
		String forma,format;
		int i,j;
		forma = _forma.trim();
		j = forma.length();
		if (j < koliko)
		{
			for (i=j;i<koliko ;i++ )
			{
				forma = "0" + forma;
			}
		}
		return forma;
 }
 //--------------------------------------------------------------------------
   private String konvertujDatum(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(6,10);
		datum = datum + "-" + pom.substring(3,5);
		datum = datum + "-" + pom.substring(0,2);
		//JOptionPane.showMessageDialog(this, datum);
	return datum;
   }
//--------------------------------------------------------------------------
   private String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
		//JOptionPane.showMessageDialog(this, datum);
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
	public void AddRecord() {
      try {
         Statement statement = connection.createStatement();

         if ( !t[0].getText().equals( "" ) && !t[1].getText().equals( "" )) {
            String ddat,dddat;
			
			ddat = String.valueOf(t[26].getText()).trim();
			dddat = konvertujDatum(ddat);
			
			String query = "INSERT INTO vozila(JMBG,regbroj,brojregistra,vrsta,vrstavozila,oznakaJUS,marka," +
				"tip,brsasije,brmotora,godproizvodnje,zemljaproizvodnje,snagamotora,radnazapremina," +
				"masapraznog,nosivost,brojmestased,brojmestastaj,obliknamena,osnovnanamena,bojaosnovna,bojadodata," +
				"brojosovina,brojpogosovina,imakuku,kilometara,datumreg,gorivo,vrstaprevoza,brojvrata," +
				"brojtockova,radio) VALUES('" +
				t[0].getText().trim() + "','" +
				t[1].getText() + "','" +
				t[2].getText() + "'," +
				Integer.parseInt(t[3].getText().trim()) + ",'" +
				t[4].getText() + "','" +
				t[5].getText() + "','" + 
				t[6].getText() + "','" + 
				t[7].getText() + "','" + 
				t[8].getText() + "','" + 
				t[9].getText() + "'," + 
				Integer.parseInt(t[10].getText().trim()) + ",'" +
				t[11].getText() + "'," + 
				Double.parseDouble(t[12].getText().trim()) + "," + 
				Double.parseDouble(t[13].getText().trim()) + "," + 
				Double.parseDouble(t[14].getText().trim()) + "," + 
				Double.parseDouble(t[15].getText().trim()) + "," + 
				Integer.parseInt(t[16].getText().trim()) + "," +
				Integer.parseInt(t[17].getText().trim()) + ",'" +
				t[18].getText() + "','" + 
				t[19].getText() + "','" + 
				t[20].getText() + "','" + 
				t[21].getText() + "'," + 
				Integer.parseInt(t[22].getText().trim()) + "," +
				Integer.parseInt(t[23].getText().trim()) + "," +
				Integer.parseInt(t[24].getText().trim()) + "," +
				Double.parseDouble(t[25].getText().trim()) + ",'" + 
				dddat + "'," + 
				Integer.parseInt(t[27].getText().trim()) + "," +
				Integer.parseInt(t[28].getText().trim()) + "," +
				Integer.parseInt(t[29].getText().trim()) + "," +
				Integer.parseInt(t[30].getText().trim()) + "," +
				Integer.parseInt(t[31].getText().trim()) + ")";
				//JOptionPane.showMessageDialog(this, query);

			int result = statement.executeUpdate( query );

			if ( result == 1 ){
				JOptionPane.showMessageDialog(this, "Slog je unet");
				String upit = "SELECT * FROM vozila";
				popuniTabelu(upit);
				NoviPressed();
			}     
			else {
				JOptionPane.showMessageDialog(this, "Slog nije unet");
				//msgout.append( "\nInsertion failed\n" );
				NoviPressed();
            }
         }
         else 
		  {JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");}
       	statement.close();
   		statement = null;
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u unosu");
		//t[0].requestFocus();
      }
  }
//------------------------------------------------------------------------------------------------------------------
	public void UpdateRecord() {
  
      try {
         Statement statement = connection.createStatement();
         if ( !t[0].getText().equals( "" ) && !t[1].getText().equals( "" )) {
			String ddat,dddat;
			ddat = String.valueOf(t[26].getText().trim());
			dddat = konvertujDatum(ddat);
               String query = "UPDATE vozila SET " +
				"JMBG='" + t[0].getText() + "'" +
				",brojregistra=" + Integer.parseInt(t[2].getText().trim())+ 
				",vrsta=" + Integer.parseInt(t[3].getText().trim())+ 
				",vrstavozila='" + t[4].getText() + "'" +
				",oznakaJUS='" + t[5].getText() + "'" +
				",marka='" + t[6].getText() + "'" +
				",tip='" + t[7].getText() + "'" +
				",brsasije='" + t[8].getText() + "'" +
				",brmotora='" + t[9].getText() + "'" +
				",godproizvodnje=" + Integer.parseInt(t[10].getText().trim()) + 
				",zemljaproizvodnje='" + t[11].getText() + "'" +
				",snagamotora=" + Integer.parseInt(t[12].getText().trim())+ 
				",radnazapremina=" + Integer.parseInt(t[13].getText().trim())+ 
				",masapraznog=" + Double.parseDouble(t[14].getText().trim()) + 
				",nosivost=" + Double.parseDouble(t[15].getText().trim()) + 
				",brojmestased=" + Integer.parseInt(t[16].getText().trim())+ 
				",brojmestastaj=" + Integer.parseInt(t[17].getText().trim())+ 
				",obliknamena='" + t[18].getText() + "'" +
				",osnovnanamena='" + t[19].getText() + "'" +
				",bojaosnovna='" + t[20].getText() + "'" +
				",bojadodata='" + t[21].getText() + "'" +
				",brojosovina=" + Integer.parseInt(t[22].getText().trim())+ 
				",brojpogosovina=" + Integer.parseInt(t[23].getText().trim())+ 
				",imakuku=" + Integer.parseInt(t[24].getText().trim())+ 
				",kilometara=" + Double.parseDouble(t[25].getText().trim()) + 
			   ",datumreg='" + dddat + "'" +
				",gorivo=" + Integer.parseInt(t[27].getText().trim())+ 
				",vrstaprevoza=" + Integer.parseInt(t[28].getText().trim())+ 
				",brojvrata=" + Integer.parseInt(t[29].getText().trim())+ 
				",brojtockova=" + Integer.parseInt(t[30].getText().trim())+ 
				",radio=" + Integer.parseInt(t[31].getText().trim())+ 
			   " WHERE regbroj='" + t[1].getText() + "'";

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
					JOptionPane.showMessageDialog(this, "Slog je izmenjen");
					String upit = "SELECT * FROM vozila ";
					popuniTabelu(upit);
					NoviPressed();
				}     
				else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					NoviPressed();
				}
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja JMBG i regbr");
		 }
   		statement.close();
		statement = null;
		}
		catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u izmeni");
      }
  }
//------------------------------------------------------------------------------------------------------------------
	public void DeleteRecord() {
 
      try {
         Statement statement = connection.createStatement();
         if ( !t[0].getText().equals( "" ) && !t[1].getText().equals( "" )) {
               	String query = "DELETE FROM vozila WHERE JMBG='" + 
						t[0].getText().trim() + "'" +
						" AND regbroj='" + t[1].getText() + "'";
;
               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					JOptionPane.showMessageDialog(this, "Slog je izbrisan");
					String upit = "SELECT * FROM vozila ";
					popuniTabelu(upit);

					NoviPressed();
				}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne mo\u017ee izbrisati");
               		NoviPressed();
            	}
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja JMBG i reg.broj");
		 }
		statement.close();
		statement = null;
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u brisanju");
      }
  }
//------------------------------------------------------------------------------------------------------------------
	public void FindRecord() {
 
      try {
         Statement statement = connection.createStatement();

		 //if ( !t[0].getText().equals( "" ) || !t[1].getText().equals( "" )) {
			String query = "SELECT * FROM vozila WHERE JMBG='" + kojiJMBG + "'" +
							" AND regbroj='" + kojiRB + "'";

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){

		         	t[0].setText(rs.getString("JMBG"));
		         	t[1].setText(rs.getString("regbroj"));
		         	t[2].setText(rs.getString("brojregistra"));
		         	t[3].setText(rs.getString("vrsta"));
		         	t[4].setText(rs.getString("vrstavozila"));
		         	t[5].setText(rs.getString("oznakaJUS"));
		         	t[6].setText(rs.getString("marka"));
		         	t[7].setText(rs.getString("tip"));
		         	t[8].setText(rs.getString("brsasije"));
		         	t[9].setText(rs.getString("brmotora"));
		         	t[10].setText(rs.getString("godproizvodnje"));
		         	t[11].setText(rs.getString("zemljaproizvodnje"));
		         	t[12].setText(rs.getString("snagamotora"));
		         	t[13].setText(rs.getString("radnazapremina"));
		         	t[14].setText(rs.getString("masapraznog"));
		         	t[15].setText(rs.getString("nosivost"));
		         	t[16].setText(rs.getString("brojmestased"));
		         	t[17].setText(rs.getString("brojmestastaj"));
		         	t[18].setText(rs.getString("obliknamena"));
		         	t[19].setText(rs.getString("osnovnanamena"));
		         	t[20].setText(rs.getString("bojaosnovna"));
		         	t[21].setText(rs.getString("bojadodata"));
		         	t[22].setText(rs.getString("brojosovina"));
		         	t[23].setText(rs.getString("brojpogosovina"));
		         	t[24].setText(rs.getString("imakuku"));
		         	t[25].setText(rs.getString("kilometara"));
		         	t[26].setText(konvertujDatumIzPodataka(rs.getString("datumreg")));
		         	t[27].setText(rs.getString("gorivo"));
		         	t[28].setText(rs.getString("vrstaprevoza"));
		         	t[29].setText(rs.getString("brojvrata"));
		         	t[30].setText(rs.getString("brojtockova"));
		         	t[31].setText(rs.getString("radio"));
					izmena = true;
				}
					t[2].setSelectionStart(0);
					t[2].setSelectionEnd(15);
					t[2].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
		 //}     
         //else {
         //   JOptionPane.showMessageDialog(this, "Nisu uneti podaci JMBG i Reg.br");
         //   }
	  statement.close();
      statement = null;
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u trazenju podataka");
      }
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
			JOptionPane.showMessageDialog(this, "Neispravna duzina datuma");
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
//------------------------------------------------------------------------------------------------------------------
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi regist.broj ili deo reg.br.");
		String upit = "SELECT * FROM vozila WHERE regbroj LIKE '" + String.valueOf(result) + "%'" +
						" AND LENGTH(TRIM(JMBG))< 12";
		popuniTabelu(upit);
		jtbl.requestFocus();
  }
//------------------------------------------------------------------------------------------------------------------
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekora\u010dena du\u017eina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		//JOptionPane.showMessageDialog(this, "Mogu se unositi samo brojevi");
		JOptionPane.showMessageDialog(this, String.valueOf(kojiJMBG));
   }
//------------------------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(100);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(100);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(100);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(100);
	}
//------------------------------------------------------------------------------------------------------------------
    public void buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new reglistQTM3(connection);
		String sql;
		sql = "SELECT * FROM vozila WHERE LENGTH(TRIM(JMBG))<12";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(100);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(100);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(100);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(4);
	   	tcol4.setPreferredWidth(100);

	   	jspane = new JScrollPane( jtbl );
		jspane.setBounds(50,270,550,190);
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		kojiJMBG = String.valueOf(podaci.get(kojirec));
		kojiRB = String.valueOf(spodaci.get(kojirec));
		FindRecord();
		proveriPravno();
	}
//-------------------------------------------------------------------------
   public void proveriKor(){
		if (t[0].getText().trim().length() == 13)
		{
			proveriKorisnika();
		}
		else{
			proveriPravno();
		}
   }
//-------------------------------------------------------------------------
   public void prikaziKorisnike(){
	   int t,tri;
	   t = 1;
			KorisniciPrik sk = new KorisniciPrik(t);
			sk.setModal(true);
			sk.setVisible(true);
   }
//-------------------------------------------------------------------------
   public void prikaziPravna(){
	   int t;
	   t = 1;
			PravnaPrik pk = new PravnaPrik(t);
			pk.setModal(true);
			pk.setVisible(true);
   }
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					if (t[0].getText().trim().length() == 0)
					{	JOptionPane.showMessageDialog(this, "Ubaci podatak");
						t[0].setText("");
						t[0].requestFocus();}
					else{
						proveriKor();}
				}
				else if (source == t[1]){
					t[2].setSelectionStart(0);
					t[2].setSelectionEnd(25);
					t[2].requestFocus();}
				else if (source == t[2]){
					t[3].setSelectionStart(0);
					t[3].setSelectionEnd(20);
					t[3].requestFocus();}
				else if (source == t[3]){
					if (t[3].getText().trim().length() == 0){
						t[3].setText("0");}
					t[4].setSelectionStart(0);
					t[4].setSelectionEnd(20);
					t[4].requestFocus();}
				else if (source == t[4]){
					t[5].setSelectionStart(0);
					t[5].setSelectionEnd(20);
					t[5].requestFocus();}
				else if (source == t[5]){
					t[6].setSelectionStart(0);
					t[6].setSelectionEnd(15);
					t[6].requestFocus();}
				else if (source == t[6]){
					t[7].setSelectionStart(0);
					t[7].setSelectionEnd(20);
					t[7].requestFocus();}
				else if (source == t[7]){
					t[8].setSelectionStart(0);
					t[8].setSelectionEnd(15);
					t[8].requestFocus();}
				else if (source == t[8]){
					t[9].setSelectionStart(0);
					t[9].setSelectionEnd(15);
					t[9].requestFocus();}
				else if (source == t[9]){
					t[10].setSelectionStart(0);
					t[10].setSelectionEnd(15);
					t[10].requestFocus();}
				else if (source == t[10]){
					if (t[10].getText().trim().length() == 0){
						t[10].setText("0");}
					t[11].setSelectionStart(0);
					t[11].setSelectionEnd(15);
					t[11].requestFocus();}
				else if (source == t[11]){
					t[12].setSelectionStart(0);
					t[12].setSelectionEnd(20);
					t[12].requestFocus();}
				else if (source == t[12]){
					if (t[12].getText().trim().length() == 0){
						t[12].setText("0");}
					t[13].setSelectionStart(0);
					t[13].setSelectionEnd(20);
					t[13].requestFocus();}
				else if (source == t[13]){
					if (t[13].getText().trim().length() == 0){
						t[13].setText("0");}
					t[14].setSelectionStart(0);
					t[14].setSelectionEnd(20);
					t[14].requestFocus();}
				else if (source == t[14]){
					if (t[14].getText().trim().length() == 0){
						t[14].setText("0");}
					t[15].setSelectionStart(0);
					t[15].setSelectionEnd(20);
					t[15].requestFocus();}
				else if (source == t[15]){
					if (t[15].getText().trim().length() == 0){
						t[15].setText("0");}
					t[16].setSelectionStart(0);
					t[16].setSelectionEnd(20);
					t[16].requestFocus();}
				else if (source == t[16]){
					if (t[16].getText().trim().length() == 0){
						t[16].setText("0");}
					t[17].setSelectionStart(0);
					t[17].setSelectionEnd(20);
					t[17].requestFocus();}
				else if (source == t[17]){
					if (t[17].getText().trim().length() == 0){
						t[17].setText("0");}
					t[18].setSelectionStart(0);
					t[18].setSelectionEnd(20);
					t[18].requestFocus();}
				else if (source == t[18]){
					t[19].setSelectionStart(0);
					t[19].setSelectionEnd(20);
					t[19].requestFocus();}
				else if (source == t[19]){
					t[20].setSelectionStart(0);
					t[20].setSelectionEnd(20);
					t[20].requestFocus();}
				else if (source == t[20]){
					t[21].setSelectionStart(0);
					t[21].setSelectionEnd(20);
					t[21].requestFocus();}
				else if (source == t[21]){
					t[22].setSelectionStart(0);
					t[22].setSelectionEnd(20);
					t[22].requestFocus();}
				else if (source == t[22]){
					if (t[22].getText().trim().length() == 0){
						t[22].setText("0");}
					t[23].setSelectionStart(0);
					t[23].setSelectionEnd(20);
					t[23].requestFocus();}
				else if (source == t[23]){
					if (t[23].getText().trim().length() == 0){
						t[23].setText("0");}
					t[24].setSelectionStart(0);
					t[24].setSelectionEnd(20);
					t[24].requestFocus();}
				else if (source == t[24]){
					if (t[24].getText().trim().length() == 0){
						t[24].setText("0");}
					t[25].setSelectionStart(0);
					t[25].setSelectionEnd(20);
					t[25].requestFocus();}
				else if (source == t[25]){
					if (t[25].getText().trim().length() == 0){
						t[25].setText("0");}
					t[26].setSelectionStart(0);
					t[26].setSelectionEnd(20);
					t[26].requestFocus();}
				else if (source == t[26]){
					if (proveriDatum(String.valueOf(t[26].getText()).trim()) == true){
						t[27].setSelectionStart(0);
						t[27].setSelectionEnd(15);
						t[27].requestFocus();}
					else{
						t[26].setSelectionStart(0);
						t[26].setSelectionEnd(15);
						t[26].requestFocus();}
				}
				else if (source == t[27]){
					if (t[27].getText().trim().length() == 0){
						t[27].setText("0");}
					t[28].setSelectionStart(0);
					t[28].setSelectionEnd(20);
					t[28].requestFocus();}
				else if (source == t[28]){
					if (t[28].getText().trim().length() == 0){
						t[28].setText("0");}
					t[29].setSelectionStart(0);
					t[29].setSelectionEnd(20);
					t[29].requestFocus();}
				else if (source == t[29]){
					if (t[29].getText().trim().length() == 0){
						t[29].setText("0");}
					t[30].setSelectionStart(0);
					t[30].setSelectionEnd(20);
					t[30].requestFocus();}
				else if (source == t[30]){
					if (t[30].getText().trim().length() == 0){
						t[30].setText("0");}
					t[31].setSelectionStart(0);
					t[31].setSelectionEnd(20);
					t[31].setText("1");
					t[31].requestFocus();}
				else if (source == t[31]){
					if (t[31].getText().trim().length() == 0){
						t[31].setText("0");}
						if (izmena)
						{izmeni.requestFocus();}
						else{unesi.requestFocus();}
				}
}
//--------------------------------------------------------------------------
   private boolean proveriKorisnika(){
	   boolean ima=false;
      try {
         Statement statement = connection.createStatement();
               String query = "SELECT * FROM korisnici WHERE JMBG='" + 
						t[0].getText().trim() + "'"; 
			try {
		         ResultSet rs = statement.executeQuery( query );
		         rs.next();
				displej.setText(rs.getString("ime") + "  " + rs.getString("prezime"));
				ima = true;
				//t[1].setSelectionStart(0);
				//t[1].setSelectionEnd(13);
				//t[1].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Nema tog korisnika morate ga otvoriti");
					ima = false;
					//t[0].setText("");
					//t[0].requestFocus();
		      }
		statement.close();
   		statement = null;
		}     
	  catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(this, "Gre\u0161ka u tra\u017eenju korisnika");
      }
		return ima;
   }
//--------------------------------------------------------------------------
   private boolean proveriPravno(){
	   boolean ima=false;
      try {
         Statement statement = connection.createStatement();
               String query = "SELECT * FROM pravna WHERE matbr='" + 
						t[0].getText().trim() + "'"; 
			try {
		         ResultSet rs = statement.executeQuery( query );
		         rs.next();
				displej.setText(rs.getString("naziv"));
				ima = true;
		      }
		      catch ( SQLException sqlex ) {
					JOptionPane.showMessageDialog(this, "Nema tog preduzeca morate ga otvoriti");
					ima = false;
		      }
		statement.close();
   		statement = null;
		}     
	  catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(this, "Gre\u0161ka u tra\u017eenju pravnih lica");
      }
		return ima;
   }
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[2]){
				kojiJMBG = String.valueOf(t[0].getText().trim());
				kojiRB = String.valueOf(t[1].getText());
				FindRecord();
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
 class reglistQTM3 extends AbstractTableModel {
	Connection dbconn;
	//public Vector totalrows;
	String[] colheads = {"Mat.br","Reg.broj","Vrsta","Vrsta vozila","Oznaka JUS","Marka","Tip"};
//------------------------------------------------------------------------------------------------------------------
   public reglistQTM3(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 7; }
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
		spodaci.clear();
		try {
        Statement statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector();
            while ( rs.next() ) {

               //Object[] record = new Object[6];
               String[] record = new String[7];
               record[0] = rs.getString("JMBG");
               record[1] = rs.getString("regbroj");
               record[2] = rs.getString("vrsta");
               record[3] = rs.getString("vrstavozila");
               record[4] = rs.getString("oznakaJUS");
               record[5] = rs.getString("marka");
               record[6] = rs.getString("tip");
               podaci.addElement(record[0]);
			   spodaci.addElement(record[1]);
               totalrows.addElement( record );
            }
            statement.close();
         	statement = null;
         }
         catch ( SQLException sqlex ) {
         }
    }
 }//end of class reglistQTM3
}// end of class korisnici ====================================================================

