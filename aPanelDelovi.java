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


public class aPanelDelovi extends JPanel{

	public static mQTMK12 qtbl;
   	public static JTable jtbl;
	private JList list;
	public static Connection connection = null;
	private String nazivp,pPre,koji="0",obradjeno="",kojimag;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	public static JTextField naziv;
	private int kojaForma,kontomal,prodavnica,vecizdat = 0;
	private int nalog=0,kojiprogram=1;
	private String magacin="2",vrstaracuna="";
    public static JFormattedTextField txtRbr,txtSifra,txtNaziv,txtKolicina;
	public static String publicsql="",brracuna="",kojidatum="";
	private JButton ok,brisi;
	public static String jedinicamere="kom";

   public aPanelDelovi(Connection _connection)	{
		connection = _connection;
		this.setLayout(new BorderLayout());
		JPanel buton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
		JLabel lblVrsta = new JLabel("Rbr.:");
		buton.add(lblVrsta);
		
		String fmm;
		fmm = "***";
		txtRbr = new JFormattedTextField(createFormatter(fmm,1));
		txtRbr.setColumns(2);
        txtRbr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtRbr.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtRbr);}});
		buton.add(txtRbr);

		JLabel lblSifra = new JLabel("\u0160ifra:");
		buton.add(lblSifra);

		fmm = "*****";
		txtSifra = new JFormattedTextField(createFormatter(fmm,4));
		txtSifra.setColumns(3);
        txtSifra.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtSifra.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtSifra);}});
        txtSifra.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        txtSifra.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziDelove();}});
		buton.add(txtSifra);

		JLabel lblNaziv = new JLabel("Naziv:");
		buton.add(lblNaziv);

		fmm = "************************************************************";
		txtNaziv = new JFormattedTextField(createFormatter(fmm,3));
		txtNaziv.setColumns(17);
        txtNaziv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtNaziv.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtNaziv);}});
		buton.add(txtNaziv);

		JLabel lblKolicina = new JLabel("Kol:");
		buton.add(lblKolicina);

		fmm = "************************************************************";
		txtKolicina = new JFormattedTextField(createFormatter(fmm,4));
		txtKolicina.setColumns(2);
        txtKolicina.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtKolicina.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtKolicina);}});
		buton.add(txtKolicina);


		ok = new JButton("U");
		//ok.setMnemonic('O');
        ok.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        ok.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {popuniTabeluSaPodacima();}});
		ok.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   popuniTabeluSaPodacima(); }});
		buton.add(ok);
		brisi = new JButton("B");
		//ok.setMnemonic('O');
        brisi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        brisi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {obrisiPodatke();}});
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   obrisiPodatke(); }});
		buton.add(brisi);

		pPre = new String("1");


		this.add(buton,BorderLayout.NORTH);
		this.add(buildTable(),BorderLayout.CENTER);

		this.setSize(250,560);

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
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
        this.setVisible(false);
    } 
//--------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		Color ccc = new Color(80,80,80);
		Color boja = new Color(81,206,128);

	   	qtbl = new mQTMK12(connection);
		String sql;

		sql = "SELECT * FROM kvarovidelovi WHERE brojkvara=0 AND rbr=0";

	   	qtbl.query(sql);

		jtbl = new JTable( qtbl );

		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.setFont(new Font("Tahoma",Font.BOLD,11));
		jtbl.addMouseListener(new ML());

		jtbl.setRowSelectionAllowed(true);
		jtbl.setSelectionBackground(Color.red);
 
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        jtbl.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Posalji();}});
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check2");
        jtbl.getActionMap().put("check2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {uzmiFokus();}});
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(200);

	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//--------------------------------------------------------------------------------------
    public static void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(40);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(40);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(200);
	}
//--------------------------------------------------------------------------------------
    public void obrisiPodatke() {
		String sql="";
		if (txtRbr.getText().trim().length()>0 && aUnosKvarova.t[0].getText().trim().length()>0)
		{
			sql = "DELETE FROM kvarovidelovi WHERE brojkvara=" + aUnosKvarova.t[0].getText().trim() +
				" AND rbr=" + txtRbr.getText().trim();
			izvrsi(sql);
			preazurirajRbr();
			
			sql = "SELECT * FROM kvarovidelovi WHERE brojkvara=" + aUnosKvarova.t[0].getText().trim();
			popuniTabelu(sql);
			nulirajPolja();
			nadjiSledeciRbr();
		}
	}
//-----------------------------------------------------------------------------------------------------
    public void preazurirajRbr() {
		int i=1,rbr=0;
		String sql="";
		String query = "SELECT * FROM kvarovidelovi WHERE " +
				"brojkvara=" + aUnosKvarova.t[0].getText().trim() + " ORDER BY rbr";
		Statement statement=null;
        try {
			statement = connection.createStatement();
		        ResultSet rs = statement.executeQuery( query );
		        while(rs.next()){
					rbr = rs.getInt("rbr");
					sql = "UPDATE kvarovidelovi SET rbr=" + i + 
						"  WHERE brojkvara=" + aUnosKvarova.t[0].getText().trim()+ " AND rbr=" + rbr;
					izvrsi(sql);
					i = i +1;
				}
				rs.close();
	    }catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "greska preazurirajRbr:"+sqlex);
        }finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch(Exception e){}
			}
	    }
  }
//--------------------------------------------------------------------------------------
    public static void popuniTabeluSaPodacima() {
		String vrr="",sql="";
		int intvrr = 0;
		if (txtRbr.getText().trim().length() > 0 && txtSifra.getText().trim().length() >0 &&
			 aUnosKvarova.t[0].getText().trim().length()>0){
			sql = "INSERT INTO kvarovidelovi(rbr,brojkvara,sifm,nazivm,jmere,kolicina) VALUES(" +
				txtRbr.getText().trim() + "," +
				aUnosKvarova.t[0].getText().trim() + "," +
				txtSifra.getText().trim() + ",'" + 
				txtNaziv.getText().trim() + "','" + 
				jedinicamere + "'," +
				txtKolicina.getText().trim() + ")";
			izvrsi(sql);
			sql = "SELECT * FROM kvarovidelovi WHERE brojkvara=" + aUnosKvarova.t[0].getText().trim();
			popuniTabelu(sql);
			nulirajPolja();
			nadjiSledeciRbr();
		
		}else{
			JOptionPane.showMessageDialog(null, "Popunite prvo podatke ");
		}
	}
//--------------------------------------------------------------------------
   public static void nulirajPolja(){
		txtRbr.setText("");
		txtRbr.setValue(null);
		txtSifra.setText("");
		txtSifra.setValue(null);
		txtNaziv.setText("");
		txtNaziv.setValue(null);
		txtKolicina.setText("");
		txtKolicina.setValue(null);
   }
//--------------------------------------------------------------------------
   public static String konvertujDatum(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(6,10);
		datum = datum + "-" + pom.substring(3,5);
		datum = datum + "-" + pom.substring(0,2);
		return datum;
   }
//------------------------------------------------------------------------------------------------------------------
	public static void izvrsi(String sql) {
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
//--------------------------------------------------------------------------------------
  private void Posalji(){
  }
//--------------------------------------------------------------------------------------
  public void uzmiFokus(){

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
//--------------------------------------------------------------------------------------
  public void proveriPoNazivu(){
    }
//--------------------------------------------------------------------------------------
  public void prikaziNalog(String _koji){
		String queryy,barkod = "0";
		Statement statement = null;
      try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM kvarovidelovi WHERE rbr=" + _koji +
					" AND brojkvara=" + aUnosKvarova.t[0].getText().trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						txtRbr.setText(rs.getString("rbr"));
						txtSifra.setText(rs.getString("sifm"));
						txtNaziv.setText(rs.getString("nazivm"));
						txtKolicina.setText(rs.getString("kolicina"));
						jedinicamere = rs.getString("jmere");
						rs.close();
					}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska prikaziNalog:" + sqlex);
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(null, "U:prikaziNalog: Nije uspeo da zatvori statement");}}
		}//********************************************************************************************
		
  }
//--------------------------------------------------------------------------------------
    public boolean proveriSifm(){ 
		boolean provera = false;
		String queryy,barkod = "0";
		Statement statement = null;
      try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM sifarnikrobe WHERE sifm=" + txtSifra.getText().trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						txtNaziv.setText(rs.getString("nazivm"));
						jedinicamere = rs.getString("jmere");
						provera = true;
						rs.close();
					}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska uproveriSifm:" + sqlex);
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(null, "U:proveriSifm: Nije uspeo da zatvori statement");}}
		}//********************************************************************************************
		return provera;
}
//--------------------------------------------------------------------------------------
    public static void nadjiSledeciRbr(){ 
		int rbr = 1;
		String queryy,barkod = "0";
		Statement statement = null;
      try {
			statement = connection.createStatement();
				queryy = "SELECT MAX(rbr) FROM kvarovidelovi WHERE brojkvara=" + aUnosKvarova.t[0].getText().trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						rbr = rs.getInt("MAX(rbr)");
						rbr = rbr + 1;
						rs.close();
					}
					txtRbr.setText(String.valueOf(rbr));
					txtRbr.requestFocus();
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska u nadjiSledeciRbr:" + sqlex);
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(null, "U:nadjiSledeciRbr: Nije uspeo da zatvori statement");}}
		}//********************************************************************************************
}
//------------------------------------------------------------------------------------------------------------------
   public void prikaziDelove(){
		int t = 1;
		mRobaPrik vrd = new mRobaPrik(connection,t);
		vrd.setModal(true);
		vrd.setVisible(true);
   }
//--------------------------------------------------------------------------------------
  public void Akcija(){
		jtbl.requestFocus();
		if (jtbl.getRowCount() > 0  )
		{
			jtbl.addRowSelectionInterval(0,0);
		}
    }
//------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == txtRbr){
					if (txtRbr.getText().trim().length()>0)
					{
						txtSifra.requestFocus();
					}else{
						txtRbr.requestFocus();
					}
				}
				else if (source == txtSifra){
					if (txtSifra.getText().trim().length()>0)
					{
						if (proveriSifm())
						{
							txtKolicina.requestFocus();
						}
					}else{
						txtSifra.requestFocus();
					}
				}
				else if (source == txtNaziv){
					if (txtNaziv.getText().trim().length() == 0)
					{
						txtNaziv.setText(" ");
					}
					txtKolicina.requestFocus();
				}
				else if (source == txtKolicina){
					if (txtKolicina.getText().trim().length() == 0)
					{
						txtKolicina.setText("1");
					}
					ok.requestFocus();
				}
	}
//===========================================================================
class ML extends MouseAdapter{
	public void mousePressed(MouseEvent e) {
		Object source = e.getSource();
		if (source == jtbl){
			int kojirec = jtbl.getSelectedRow();
			koji = jtbl.getValueAt(kojirec,0).toString();
			prikaziNalog(koji);
		}
	}
}//end of class ML	
//=======================================================================================
 class mQTMK12 extends AbstractTableModel {
   Connection dbconn;
   String[] colheads = {"Rbr","\u0160ifra","Naziv","J.mere","Koli\u010dina"};

   public mQTMK12(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//--------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 5; }
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
//--------------------------------------------------------------------------------------
   public String konvertujDatumIzPodatakaQTB(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
//--------------------------------------------------------------------------------------
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
               Object[] record = new Object[5];
				record[0] = rs.getString("rbr");
				record[1] = rs.getString("sifm");
				record[2] = rs.getString("nazivm");
				record[3] = rs.getString("jmere");
				record[4] = rs.getInt("kolicina");
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
 }//end of class QueryTableModel
}//end of class KupciPri

