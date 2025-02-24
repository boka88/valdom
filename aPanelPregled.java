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

public class aPanelPregled extends JPanel{

	private JButton ok,stampa,stampafiz,izlaz,izmena;					
	private JList list;
	private Connection connect = null;
	private String nazivp;

	private JTextField odkonta, dokonta, nazsapregledi;
	private String pPre,nazivPre,pacijent="0",brpregleda="1",doktor="0",ordinacija="0";
	private int kojaForma,n_fields;
	private JLabel lblPacijent;
    public static FormField t[],mmoj;
	public JTextArea simptomi,dijagnoza,lecenje,napomena;
   	public static JLabel  l[],lblDoktorPrik,lblPacijentPrik,lblOrdinacijaPrik;
	private String pattern = "#######.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);
	private JScrollPane jspane1,jspane2,jspane3,jspane4;

//------------------------------------------------------------------------------------------------------------------
   public aPanelPregled(Connection _connect)	{
		this.setLayout(null);
		connect = _connect;
	

		this.add(buildFilterPanel());
		this.add(buildPanel());
		

		this.setSize(560,580);
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
				case 5:
					//formater za datume
					formatter.setValidCharacters("0123456789.- ");
					break;
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder(""));
		p.setBounds(5,5,580,40);

		
		JLabel lblDok = new JLabel(" ");
		lblDok.setBounds(5,5,70,20);
		lblDoktorPrik = new JLabel("");
		lblDoktorPrik.setBounds(80,5,150,20);
		lblDoktorPrik.setFont(new Font("Tahoma",Font.BOLD,12));

		JLabel lblOrd = new JLabel(" ");
		lblOrd.setBounds(250,5,80,20);
		lblOrdinacijaPrik = new JLabel("");
		lblOrdinacijaPrik.setBounds(330,5,150,20);
		lblOrdinacijaPrik.setFont(new Font("Tahoma",Font.BOLD,12));

        p.add(lblDok); 
        p.add(lblDoktorPrik); 
        //p.add(lblOrd); 
        //p.add(lblOrdinacijaPrik); 

		return p;
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildPanel() {
		JPanel p = new JPanel();
		p.setLayout( null );
		p.setBorder( new TitledBorder("") );
		p.setBounds(5,80,580,580);

		int prviLX,prviTX,drugiLX,drugiTX,treciLX,treciTX,visina,razmakY;
		int sirinaL,txt,aa,btnY,btnX,btnsirina,btnrazmak;
		aa = 15;					//rastojanje od gornje ivice panela
		sirinaL = 80;			//sirina labele
		visina = 25;			//visina komponente
		prviLX = 10;			//X-pozicija za prvi red labela
		prviTX = 30 + sirinaL;	//X-pozicija za prvi red text-box
		drugiLX = 360;
		drugiTX = drugiLX + sirinaL + 10;
		treciLX = 450;
		treciTX = treciLX + sirinaL + 20;
		razmakY = 3;			// razmak izmedju komponenti po visini
		txt = 10;				// vrednost jedinicnog razmaka u txt - polju
		btnY = 500;
		btnX = 50;
		btnsirina = 80;
		btnrazmak = 5;

		int visinaarea=80,rastojanjearea=0;

	    Border border = BorderFactory.createLineBorder(Color.BLACK);
		int i;
        n_fields = 10; 
        t = new FormField[10]; 
        l = new JLabel[11]; 
		JLabel lblRadnik;
		
		String fmm;
		fmm = "******";
        l[0] = new JLabel("Broj kvara :");
			l[0].setBounds(prviLX,aa,sirinaL,visina);
		l[0].setFont(new Font("Arial",Font.PLAIN,12));
        t[0] = new FormField(createFormatter(fmm,1));
		t[0].setEditable(false);
		t[0].setFont(new Font("Arial",Font.PLAIN,12));
			t[0].setBounds(prviTX,aa,10*txt,visina);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check1");
        t[0].getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {quitForm();}});

		fmm = "##/##/####";
		l[1] = new JLabel("Datum :");
			l[1].setBounds(prviLX+250,aa,70,visina);
		l[1].setFont(new Font("Arial",Font.PLAIN,12));
        t[1] = new FormField(createFormatter(fmm,4));
		t[1].setEditable(false);
		t[1].setFont(new Font("Arial",Font.PLAIN,12));
			t[1].setBounds(prviLX+250+70,aa,10*txt,visina);

		//ne koristi se ****************************************************
		fmm = "**********";
        l[2] = new JLabel("Br.kvara :");
 			l[2].setBounds(prviLX,aa + visina + razmakY,sirinaL,visina);
		l[2].setFont(new Font("Arial",Font.PLAIN,12));
		t[2] = new FormField(createFormatter(fmm,1));
		t[2].setEditable(false);
		t[2].setFont(new Font("Arial",Font.PLAIN,12));
			t[2].setBounds(prviTX,aa + visina + razmakY,8*txt,visina);
		//********************************************************************

		fmm = "*****";
        
		l[3] = new JLabel("Ma\u0161ina :");
 			l[3].setBounds(prviLX,aa + 2*(visina + razmakY),sirinaL,visina);
		l[3].setFont(new Font("Arial",Font.PLAIN,12));
		t[3] = new FormField(createFormatter(fmm,1));
		t[3].setEditable(false);
		t[3].setFont(new Font("Arial",Font.PLAIN,12));
			t[3].setBounds(prviTX,aa + 2*(visina + razmakY),8*txt,visina);
		

		// ne koristi se ================================================================
		fmm = "***";
		l[5] = new JLabel("Ordinacija :");
			l[5].setBounds(prviLX+250,aa + 1*(visina + razmakY),70,visina);
		l[5].setFont(new Font("Arial",Font.PLAIN,12));
        t[5] = new FormField(createFormatter(fmm,1));
		t[5].setEditable(false);
		t[5].setFont(new Font("Arial",Font.PLAIN,12));
			t[5].setBounds(prviLX+250+70,aa + 1*(visina + razmakY),4*txt,visina);

		fmm = "********";
		l[6] = new JLabel("Rabat (%) :");
			l[6].setBounds(drugiLX,aa + 3*(visina + razmakY),sirinaL,visina);
		l[6].setFont(new Font("Arial",Font.PLAIN,12));
        t[6] = new FormField(createFormatter(fmm,2));
		t[6].setEditable(false);
		t[6].setFont(new Font("Arial",Font.PLAIN,12));
			t[6].setBounds(drugiTX,aa + 3*(visina + razmakY),10*txt,visina);
		//================================================================================

		JLabel zag = new JLabel("Opis kvara:");
		zag.setFont(new Font("Arial",Font.PLAIN,12));
		zag.setBounds(prviLX,aa + 3*(visina + razmakY) + 30,sirinaL,visina);
		simptomi = new JTextArea();
		simptomi.setLineWrap(true);
		DefaultContextMenu contextMenu = new DefaultContextMenu();
		contextMenu.add(simptomi);		
		simptomi.setRows(4);
		simptomi.setWrapStyleWord(true);
		simptomi.setFont(new Font("Arial",Font.BOLD,12));
		simptomi.setLineWrap(true);
	   	jspane1 = new JScrollPane( simptomi );
		jspane1.setBounds(prviTX,aa + 3*(visina + razmakY) + 30,440,visinaarea);

		rastojanjearea = aa + 3*(visina + razmakY) + 30 + visinaarea + 5;

		JLabel pod = new JLabel("Opis radova :");
		pod.setFont(new Font("Arial",Font.PLAIN,12));
		pod.setBounds(prviLX,rastojanjearea,sirinaL,visina);
		dijagnoza = new JTextArea();
		dijagnoza.setFont(new Font("Arial",Font.BOLD,12));
		dijagnoza.setColumns(70);
		dijagnoza.setLineWrap(true);
		contextMenu.add(dijagnoza);		
		dijagnoza.setRows(3);
		dijagnoza.setWrapStyleWord(true);
	   	jspane2 = new JScrollPane( dijagnoza );
		jspane2.setBounds(prviTX,rastojanjearea,440,visinaarea);

		//rastojanjearea = rastojanjearea + visinaarea + 5;
		// NE KORISTI SE **********************************************
		JLabel pod1 = new JLabel("Lecenje :");
		pod1.setFont(new Font("Arial",Font.PLAIN,12));
		pod1.setBounds(prviLX,rastojanjearea,sirinaL + 20,visina);
		lecenje = new JTextArea();
		lecenje.setFont(new Font("Arial",Font.BOLD,12));
		lecenje.setColumns(70);
		lecenje.setLineWrap(true);
		contextMenu.add(lecenje);		
		lecenje.setRows(3);
		lecenje.setWrapStyleWord(true);
	   	jspane3 = new JScrollPane( lecenje );
		jspane3.setBounds(prviTX,rastojanjearea,440,visinaarea);
		//**************************************************************

		rastojanjearea =rastojanjearea + visinaarea + 5;

		JLabel pod2 = new JLabel("Preventivne mere :");
		pod2.setFont(new Font("Arial",Font.PLAIN,12));
		pod2.setBounds(prviLX,rastojanjearea,sirinaL + 30,visina);
		napomena = new JTextArea();
		napomena.setColumns(70);
		napomena.setLineWrap(true);
		contextMenu.add(napomena);		
		napomena.setRows(3);
		napomena.setWrapStyleWord(true);
		napomena.setLineWrap(true);
	   	jspane4 = new JScrollPane( napomena );
		jspane4.setBounds(prviTX,rastojanjearea,440,visinaarea);

		rastojanjearea =rastojanjearea + visinaarea + 5;

		fmm = "************";
        l[4] = new JLabel("Cena  :");
			l[4].setBounds(prviLX,rastojanjearea,sirinaL,visina);
		l[4].setFont(new Font("Arial",Font.PLAIN,12));
        t[4] = new FormField(createFormatter(fmm,2));
		t[4].setFont(new Font("Arial",Font.PLAIN,12));
		t[4].setEditable(false);
			t[4].setBounds(prviTX,rastojanjearea,10*txt,visina);

		stampa = new JButton("\u0160tampa");
		stampa.setMnemonic('P');
		stampa.setBounds(prviLX+450,rastojanjearea,100,30);
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); 
				   //StampaFiz(); 
				   }});

		stampafiz = new JButton("\u0160tampa FIZ");
		stampafiz.setMnemonic('F');
		stampafiz.setBounds(prviLX+250,rastojanjearea,100,30);
		stampafiz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   StampaFiz(); 
				   }});


	
		/*
		for(i=0;i<3;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}
		*/
		p.add(l[0]);
		p.add(t[0]);
		p.add(l[1]);
		p.add(t[1]);
		p.add(l[3]);
		p.add(t[3]);

		
		p.add(zag);
		p.add(jspane1);
		p.add(pod);
		p.add(jspane2);
		//p.add(pod1);
		//p.add(jspane3);
		p.add(pod2);
		p.add(jspane4);
        //p.add(izmena); 
        p.add(stampa); 
		/*
		if (ConnMySQL.fizio == 1)
		{
			p.add(stampafiz); 
		}
		*/
		
		return p;
    }
//------------------------------------------------------------------------------------------------------------------
   public String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
   public void Stampa(){
	   String sql = "";
	   String imepacijenta="",imedoktora="",rbr="",ordinacija="",doktor="",brmasine="";

		if (t[0].getText().trim().length() != 0 )
		{
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

			
			jPrintKvar pn = new jPrintKvar(connect,t[0].getText().trim());
		}else {
			 JOptionPane.showMessageDialog(this, "Unesi prvo podatke");
		}
   }
//------------------------------------------------------------------------------------------------------------------
   public void StampaFiz(){
	   /*
	   String masina="",rbr="",sql="";

		if (t[0].getText().trim().length() != 0 && t[3].getText().trim().length()!= 0)
		{
			rbr = t[0].getText().trim();
			masina = t[3].getText().trim();

			sql = "DROP TABLE IF EXISTS tmpkvar";
			izvrsi(sql);
			sql = "CREATE TEMPORARY TABLE tmpkvar like kvarovi";
			izvrsi(sql);
			sql = "INSERT INTO tmpkvar SELECT * FROM kvarovi WHERE kvarovi.rbr=" + rbr;
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

			
			jPrintKvarPDF pn = new jPrintKvarPDF(connect,t[0].getText().trim());

		
		}else {
			 JOptionPane.showMessageDialog(this, "Unesi prvo podatke");
		}
		*/
   }
//------------------------------------------------------------------------------------------------------------------
	public void FindRecord(String _pacijent,String _brpregleda) {
	  String doktor="",ordinacija="",query="";
	  Statement statement = null;
	  int plac,rekl;
      try {
         statement = connect.createStatement();
		 if ( !t[0].getText().equals( "" )) {
			query = "SELECT * FROM kvarovi WHERE rbr=" + _brpregleda.trim() +
				" AND datum>'2000-01-01' AND sifra=" + _pacijent.trim();

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		         	t[0].setText(rs.getString("rbr"));
		         	t[1].setText(konvertujDatumIzPodataka(rs.getString("datum")));
		         	t[2].setText(rs.getString("sifra"));
		         	simptomi.setText(rs.getString("opiskvara"));
		         	dijagnoza.setText(rs.getString("opisradova"));
		         	napomena.setText(rs.getString("napomena"));
					t[1].setSelectionStart(0);
					t[1].setSelectionEnd(15);
					t[1].requestFocus();
				}else{
		         	JOptionPane.showMessageDialog(this, "Ne postoji nalog");
				}
		 }     
         else {
            JOptionPane.showMessageDialog(this, "Broj naloga nije unet");
            }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u prikazi nalog:"+sqlex);
		JOptionPane.showMessageDialog(this, "pit:"+query);
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
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
        	this.setVisible(false);
    } 
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connect.createStatement();
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

//--------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		ConnMySQL _dbconn = new ConnMySQL();
		if (_dbconn.getConnection() != null)
			{connect = _dbconn.getConnection();}
		else
			{JOptionPane.showMessageDialog(this, "Konekcija nije otvorena");}
		return;
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
   	}
//===========================================================================
class ML extends MouseAdapter{
	public void mousePressed(MouseEvent e) {
		Object source = e.getSource();
	}
}//end of class ML
//=======================================================================================
}//end of class KupciPri

