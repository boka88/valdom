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


public class Pravna extends JInternalFrame implements InternalFrameListener
			{
	private QTM3 qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private boolean izmena = false;
	private String koji,date;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],mmoj;
   	private JLabel  l[];
   	int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public Pravna() {
		super("", true, true, true, true);
        setTitle("\u0160ifarnik pravnih lica");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		uzmiKonekciju();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();

		buildTable();
		main.add(buildPanel(), BorderLayout.CENTER);
		getContentPane().add(main);
		pack();
		setSize(620,480);
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
    public JPanel buildPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder("Unos") );

		// setBounds(X,Y,sirina,visina)
		int prviLX,prviTX,drugiLX,drugiTX,treciLX,treciTX,visina,razmakY;
		int sirinaL,txt,aa,btnY,btnX,btnsirina,btnrazmak;
		aa = 25;					//rastojanje od gornje ivice panela
		sirinaL = 80;			//sirina labele
		visina = 20;			//visina komponente
		prviLX = 10;			//X-pozicija za prvi red labela
		prviTX = 10 + sirinaL;	//X-pozicija za prvi red text-box
		drugiLX = 210;
		drugiTX = drugiLX + sirinaL + 20;
		treciLX = 450;
		treciTX = treciLX + sirinaL + 20;
		razmakY = 3;			// razmak izmedju komponenti po visini
		txt = 10;				// vrednost jedinicnog razmaka u txt - polju
		btnY = 6*(visina + razmakY) + 50;
		btnX = 50;
		btnsirina = 80;
		btnrazmak = 5;

		int i;
        n_fields = 10; 
        t = new JFormattedTextField[10]; 
        l = new JLabel[10]; 

		
		String fmm;
		fmm = "************";
        l[0] = new JLabel("Maticni br. :");
			l[0].setBounds(prviLX,aa,sirinaL,visina);
		l[0].setFont(new Font("Arial",Font.PLAIN,9));
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setFont(new Font("Arial",Font.PLAIN,9));
		t[0].setColumns(10);
			t[0].setBounds(prviTX,aa,8*txt,visina);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "*************************";
		l[1] = new JLabel("Naziv :");
			l[1].setBounds(prviLX,aa + visina + razmakY,sirinaL,visina);
		l[1].setFont(new Font("Arial",Font.PLAIN,9));
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
		t[1].setFont(new Font("Arial",Font.PLAIN,9));
		t[1].setColumns(20);
			t[1].setBounds(prviTX,aa + visina + razmakY,10*txt,visina);
		t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "************";
        l[2] = new JLabel("Delatnost :");
 			l[2].setBounds(prviLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[2].setFont(new Font("Arial",Font.PLAIN,9));
		t[2] = new JFormattedTextField(createFormatter(fmm,1));
		t[2].setFont(new Font("Arial",Font.PLAIN,9));
		t[2].setColumns(20);
			t[2].setBounds(prviTX,aa + 2*(visina + razmakY),10*txt,visina);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "**********************";
        l[3] = new JLabel("Ulica :");
			l[3].setBounds(prviLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[3].setFont(new Font("Arial",Font.PLAIN,9));
        t[3] = new JFormattedTextField(createFormatter(fmm,3));
		t[3].setFont(new Font("Arial",Font.PLAIN,9));
		t[3].setColumns(20);
			t[3].setBounds(prviTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "******";
		l[4] = new JLabel("Broj :");
			l[4].setBounds(prviLX,aa + 4*(visina + razmakY),sirinaL,visina);
		l[4].setFont(new Font("Arial",Font.PLAIN,9));
        t[4] = new JFormattedTextField(createFormatter(fmm,1));
		t[4].setFont(new Font("Arial",Font.PLAIN,9));
		t[4].setColumns(14);
			t[4].setBounds(prviTX,aa + 4*(visina + razmakY),10*txt,visina);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "********************";
		l[5] = new JLabel("Mesto");
			l[5].setBounds(drugiLX,aa,sirinaL,visina);
		l[5].setFont(new Font("Arial",Font.PLAIN,9));
        t[5] = new JFormattedTextField(createFormatter(fmm,3));
		t[5].setFont(new Font("Arial",Font.PLAIN,9));
		t[5].setColumns(14);
			t[5].setBounds(drugiTX,aa,10*txt,visina);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

        l[6] = new JLabel("Op\u0161tina :");
			l[6].setBounds(drugiLX,aa + visina + razmakY,sirinaL,visina);
		l[6].setFont(new Font("Arial",Font.PLAIN,9));
        t[6] = new JFormattedTextField(createFormatter(fmm,3));
		t[6].setFont(new Font("Arial",Font.PLAIN,9));
		t[6].setColumns(10);
			t[6].setBounds(drugiTX,aa + visina + razmakY,10*txt,visina);
		t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		fmm = "***************";
        l[7] = new JLabel("Telefon :");
			l[7].setBounds(drugiLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[7].setFont(new Font("Arial",Font.PLAIN,9));
        t[7] = new JFormattedTextField(createFormatter(fmm,4));
		t[7].setFont(new Font("Arial",Font.PLAIN,9));
		t[7].setColumns(20);
			t[7].setBounds(drugiTX,aa + 2*(visina + razmakY),10*txt,visina);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

        l[8] = new JLabel("Mobilni :");
			l[8].setBounds(drugiLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[8].setFont(new Font("Arial",Font.PLAIN,9));
        t[8] = new JFormattedTextField(createFormatter(fmm,4));
		t[8].setFont(new Font("Arial",Font.PLAIN,9));
		t[8].setColumns(20);
			t[8].setBounds(drugiTX,aa + 3*(visina + razmakY),10*txt,visina);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});

		fmm = "*************************";
		l[9] = new JLabel("Lice :");
			l[9].setBounds(drugiLX,aa + 4*(visina + razmakY),sirinaL,visina);
		l[9].setFont(new Font("Arial",Font.PLAIN,9));
        t[9] = new JFormattedTextField(createFormatter(fmm,3));
		t[9].setFont(new Font("Arial",Font.PLAIN,9));
		t[9].setColumns(13);
			t[9].setBounds(drugiTX,aa + 4*(visina + razmakY),10*txt,visina);
        t[9].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[9].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[9]);}});


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
		trazi.setBounds(btnX + 4*(btnsirina + btnrazmak),btnY,btnsirina,20);
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});

		JButton stampa = new JButton("\u0160tampa");
		stampa.setMnemonic('P');
		stampa.setBounds(btnX + 5*(btnsirina + btnrazmak),btnY,btnsirina,20);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

	    for(i=0;i<10;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }
		p.add(unesi);
		p.add(novi);
		p.add(izmeni);
		p.add(brisi);
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
		int x = 5;
		this.setLocation(x,y);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 10; 
        for(i=0;i<n_fields;i++)
            t[i].setText("");
		izmena = false;
		t[0].requestFocus();
    }
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		PrintPolazn kkl = new PrintPolazn();
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

		 if ( !t[0].getText().equals( "" ) ) {

			String query = "INSERT INTO pravna(matbr,naziv,delatnost,ulica," +
				"broj,mesto,opstina,telefon,mobilni,lice) VALUES('" +
				t[0].getText().trim() + "','" +
				t[1].getText() + "','" +
				t[2].getText().trim() + "','" +
				t[3].getText() + "','" +
				t[4].getText().trim() + "','" + 
				t[5].getText() + "','" + 
				t[6].getText() + "','" + 
				t[7].getText() + "','" + 
				t[8].getText() + "','" + 
				t[9].getText() + "')"; 
			
			int result = statement.executeUpdate( query );
				//JOptionPane.showMessageDialog(this, "doso posle execute");

			if ( result == 1 ){
				JOptionPane.showMessageDialog(this, "Slog je unet");
				String upit = "SELECT * FROM pravna";
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
		 if ( !t[0].getText().equals( "" )) {
               String query = "UPDATE pravna SET " +
				"naziv='" + t[1].getText()+ "'" +
				",delatnost='" + t[2].getText() + "'" +
				",ulica='" + t[3].getText() + "'" +
				",broj='" + t[4].getText() + "'" +
				",mesto='" + t[5].getText() + "'" +
				",opstina='" + t[6].getText() + "'" +
				",telefon='" + t[7].getText() + "'" +
				",mobilni='" + t[8].getText() + "'" +
				",lice='" + t[9].getText() + "'" +
				" WHERE matbr='" + t[0].getText().trim() + "'";

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
					JOptionPane.showMessageDialog(this, "Slog je izmenjen");
					String upit = "SELECT * FROM pravna ";
					popuniTabelu(upit);
					NoviPressed();
				}     
				else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					NoviPressed();
				}
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje matbr");
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
         if ( !t[0].getText().equals( "" ) ) {
               	String query = "DELETE FROM pravna WHERE matbr='" + 
						t[0].getText().trim() + "'";
               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					JOptionPane.showMessageDialog(this, "Slog je izbrisan");
					String upit = "SELECT * FROM pravna ";
					popuniTabelu(upit);

					NoviPressed();
				}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne mo\u017ee izbrisati");
               		NoviPressed();
            	}
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje matbr");
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

		 if ( !t[0].getText().equals( "" )) {
			String query = "SELECT * FROM pravna WHERE matbr='" + koji + "'";

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){

		         	t[0].setText(rs.getString("matbr"));
		         	t[1].setText(rs.getString("naziv"));
		         	t[2].setText(rs.getString("delatnost"));
		         	t[3].setText(rs.getString("ulica"));
		         	t[4].setText(rs.getString("broj"));
		         	t[5].setText(rs.getString("mesto"));
		         	t[6].setText(rs.getString("opstina"));
		         	t[7].setText(rs.getString("telefon"));
		         	t[8].setText(rs.getString("mobilni"));
		         	t[9].setText(rs.getString("lice"));
					izmena = true;
				}
	         		statement.close();
         			statement = null;
					t[1].setSelectionStart(0);
					t[1].setSelectionEnd(15);
					t[1].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	//JOptionPane.showMessageDialog(this, "slog ne postoji");
		         	JOptionPane.showMessageDialog(this, sqlex);
		         	//JOptionPane.showMessageDialog(this, pPre);
		      }
		 }     
         else {
            JOptionPane.showMessageDialog(this, "Saradnik nije unet");
            }
         	//statement.close();
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u trazenju");
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
//------------------------------------------------------------------------------------------------------------------
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva");
		String upit = "SELECT * FROM pravna WHERE naziv LIKE '" + String.valueOf(result) + "%'";
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
		JOptionPane.showMessageDialog(this, String.valueOf(koji));
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

	   	qtbl = new QTM3(connection);
		String sql;
		sql = "SELECT * FROM pravna ";
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
		jspane.setBounds(50,220,500,200);
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = String.valueOf(podaci.get(kojirec));
		FindRecord();
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
					t[1].setSelectionStart(0);
					t[1].setSelectionEnd(13);
					t[1].requestFocus();}
				}
				else if (source == t[1]){
					t[2].setSelectionStart(0);
					t[2].setSelectionEnd(25);
					t[2].requestFocus();}
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0){
						t[2].setText("0");}
					t[3].setSelectionStart(0);
					t[3].setSelectionEnd(20);
					t[3].requestFocus();}
				else if (source == t[3]){
					t[4].setSelectionStart(0);
					t[4].setSelectionEnd(20);
					t[4].requestFocus();}
				else if (source == t[4]){
					if (t[4].getText().trim().length() == 0){
						t[4].setText("0");}
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
					if (izmena)
					{
						izmeni.requestFocus();}
					else{
						unesi.requestFocus();}
				}
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				koji = String.valueOf(t[0].getText());
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
 class QTM3 extends AbstractTableModel {
	Connection dbconn;
	//public Vector totalrows;
	String[] colheads = {"Mat.br","Naziv","Delatnost","Ulica","Mesto","Opstina"};
//------------------------------------------------------------------------------------------------------------------
   public QTM3(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 6; }
   public int getRowCount() { return totalrows.size(); }
	/*
   public Object getValueAt(int row, int col) {
      return ((Object[])totalrows.elementAt(row))[col];
   }
   public Class getColumnClass(int c) {
	return getValueAt(0 , c).getClass();
   }
   public boolean isCellEditable(int row, int col) {
      return false;
   }
   public void fire() {
      fireTableChanged(null);
   }
   */
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
		//JOptionPane.showMessageDialog(this, datum);
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
               String[] record = new String[6];
               record[0] = rs.getString("matbr");
               record[1] = rs.getString("naziv");
               record[2] = rs.getString("delatnost");
               record[3] = rs.getString("ulica");
               record[4] = rs.getString("mesto");
               record[5] = rs.getString("opstina");
               podaci.addElement(record[0]);
			   //spodaci.addElement(record[1]);
               totalrows.addElement( record );
            }
            statement.close();
         	statement = null;
         }
         catch ( SQLException sqlex ) {
         }
    }
 }//end of class QTM3
}// end of class pravna ====================================================================

