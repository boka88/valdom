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


public class aPanelIzd extends JPanel{

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
    public static JFormattedTextField txtVrsta,txtDatum;
	public static String publicsql="",brracuna="",kojidatum="";
	private JButton ok;

   public aPanelIzd()	{
		//magacin = String.valueOf(_magacin);
		this.setLayout(new BorderLayout());
		JPanel buton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
		JLabel lblVrsta = new JLabel("Vrsta knj.:");
		buton.add(lblVrsta);
		
		String fmm;
		fmm = "**";
		txtVrsta = new JFormattedTextField(createFormatter(fmm,1));
		txtVrsta.setColumns(3);
        txtVrsta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtVrsta.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtVrsta);}});
        txtVrsta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),"check1");
        txtVrsta.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziVrsDok();}});
		buton.add(txtVrsta);

		JLabel lblDatum = new JLabel("Od datuma:");
		buton.add(lblDatum);
		buton.add(lblDatum);

		fmm = "##/##/####";
		txtDatum = new JFormattedTextField(createFormatter(fmm,4));
		txtDatum.setColumns(8);
        txtDatum.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtDatum.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtDatum);}});
		buton.add(txtDatum);

		ok = new JButton("OK");
		ok.setMnemonic('O');
        ok.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        ok.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {popuniTabeluSaPodacima();}});
		ok.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   popuniTabeluSaPodacima(); }});
		buton.add(ok);

		uzmiKonekciju();

		napraviTabelu();

		pPre = new String(IzborPre.kojePre);

		JLabel lab = new JLabel("Upi\u0161i naziv ili deo : ");
		naziv = new JTextField(15);
        naziv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        naziv.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija();}});
        naziv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check2");
        naziv.getActionMap().put("check2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {uzmiFokus();}});
		naziv.addCaretListener(new CaretListener() {
	               public void caretUpdate(CaretEvent e) {
				   proveriPoNazivu(); }});

		this.add(buton,BorderLayout.NORTH);
		this.add(buildTable(),BorderLayout.CENTER);

		this.setSize(380,550);

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
   public static void idiNaPolje() {
		naziv.setText("");
		naziv.setSelectionStart(0);
		naziv.requestFocus();
   }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
        this.setVisible(false);
    } 
//--------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		try{
			connection = aLogin.konekcija;
		}catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee preuzeti konekciju:"+f);
		}
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
   	}
//--------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		Color ccc = new Color(80,80,80);
		Color boja = new Color(81,206,128);

	   	qtbl = new mQTMK12(connection);
		String sql;

		sql = "SELECT * FROM tmpracuni";

	   	qtbl.query(sql);

		jtbl = new JTable( qtbl )
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);

				//  Color row based on a cell value
				if (!isRowSelected(row))
				{
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);
					//uzima vrednost zadnje kolone "Prenet"
					int type = (Integer)getModel().getValueAt(modelRow, 3); //efakture
					
					if (type>0 ) c.setBackground(boja);
				}
				return c;
			}
		};

 	   	//jtbl = new JTable( qtbl );
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
	   	tcol.setPreferredWidth(130);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(60);

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
	   	tcol.setPreferredWidth(130);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(60);
	}
//--------------------------------------------------------------------------------------
    public static void popuniTabeluSaPodacima() {
		String vrr="",_oddatuma="";
		int intvrr = 0;
		if (txtVrsta.getText().trim().length() != 0 && txtDatum.getText().trim().length() !=0){
			vrr = txtVrsta.getText().trim();
			intvrr = Integer.parseInt(vrr);
			if (intvrr == 4)
			{
				JOptionPane.showMessageDialog(null, "Mogu samo ulazni ra\u010duni");
				txtVrsta.setText("");
				txtVrsta.requestFocus();
			}else{				
				
				_oddatuma = konvertujDatum(txtDatum.getText().trim());
				
					String strsql = "DELETE FROM tmpracuni";
					izvrsi(strsql);
					
					strsql = "INSERT INTO tmpracuni(brrac,datum) SELECT DISTINCT brrac,datum FROM fprom WHERE fprom.vrprom=" + vrr +
						" AND fprom.datum>='" + _oddatuma + "' AND SUBSTRING(konto,1,4) = '4350'";
					izvrsi(strsql);
					
					strsql = "UPDATE tmpracuni,fprom SET tmpracuni.vrstaracuna=fprom.vrstaracuna,tmpracuni.efakture=fprom.efakture WHERE " +
						" tmpracuni.brrac=fprom.brrac AND fprom.vrprom=" + vrr +
						" AND fprom.datum>='" + _oddatuma + "'";
					izvrsi(strsql);
					
					strsql = "SELECT * FROM tmpracuni ORDER BY datum DESC,brrac";
					
					qtbl.query(strsql);
					qtbl.fire();
					TableColumn tcol = jtbl.getColumnModel().getColumn(0);
					tcol.setPreferredWidth(130);
					TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
					tcol1.setPreferredWidth(100);
					TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
					tcol2.setPreferredWidth(60);
			}
		}else{
			JOptionPane.showMessageDialog(null, "Popunite prvo podatke ");
		}
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
//--------------------------------------------------------------------------------------
  public void napraviTabelu(){
		String strsql="";
		strsql = "drop table if exists tmpracuni";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE tmpracuni(" +
		  " brrac  double default '0'," +
		  " datum  datetime default '0000-00-00'," +
		  " vrstaracuna  int(3) default '0'," +
		  " efakture  int(1) default '0'" +
		") DEFAULT CHARSET=utf8";
		izvrsi(strsql);
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
  public void prikaziNalog(){
		String sql = "SELECT rbr,konto,opis,format(dugu,2),format(potr,2) FROM fprom WHERE " +
			" brrac=" + koji + " AND vrprom=" + txtVrsta.getText().trim() + " AND datum='" + 
			kojidatum + "'";
		
		publicsql = " WHERE brrac=" + koji + " AND vrprom=" + txtVrsta.getText().trim() + " AND datum='" + 
			kojidatum + "'";
		brracuna = koji;
		aPrikazRacuna.popuniTabelu(sql);
		aPrikazRacuna.tt[0].setText(vrstaracuna);
		if (vecizdat > 0)
		{
			JOptionPane.showMessageDialog(this, "Ra\u010dun je ve\u0107 poslat");
			vecizdat = 0;
		}
		
  }
//--------------------------------------------------------------------------------------
    public String proveriSifm(String _koja){ 
		String queryy,barkod = "0";
		Statement statement = null;
      try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM sifarnikrobe WHERE sifm=" + _koja.trim() + " AND pre=1";
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						barkod = rs.getString("barkod");
						rs.close();
					}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju artikla:" + sqlex);
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(this, "U:proveriSifm: Nije uspeo da zatvori statement");}}
		}//********************************************************************************************
		return barkod;
}
//--------------------------------------------------------------------------------------
    public boolean proveriVrstuKnjizenja(String _koja){ 
		boolean ima = false;
		String queryy,barkod = "0";
		Statement statement = null;
      try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM dokumentifinansijsko WHERE sifdok=" + _koja.trim();
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						ima = true;
						rs.close();
					}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u proveriVrstu:" + sqlex);
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(this, "U:proveriSifm: Nije uspeo da zatvori statement");}}
		}//********************************************************************************************
		return ima;
}
//------------------------------------------------------------------------------------------------------------------
   public void prikaziVrsDok(){
		int t = 5;
		fVrsDok vrd = new fVrsDok(connection,t);
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

				if (source == txtVrsta){
					if (txtVrsta.getText().trim().length()>0)
					{
						if (proveriVrstuKnjizenja(txtVrsta.getText().trim()))
						{
							txtDatum.requestFocus();
						}
					}else{
						txtVrsta.requestFocus();
					}

				}
				//br. radnog naloga
				else if (source == txtDatum){
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
			kojidatum = konvertujDatum(jtbl.getValueAt(kojirec,1).toString());
			vrstaracuna = jtbl.getValueAt(kojirec,2).toString();
			vecizdat = Integer.parseInt(jtbl.getValueAt(kojirec,3).toString());
			prikaziNalog();
		}
	}
}//end of class ML	
//=======================================================================================
 class mQTMK12 extends AbstractTableModel {
   Connection dbconn;
   String[] colheads = {"Br.rac.","Datum","Vrsta rac.","Prenet"};

   public mQTMK12(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//--------------------------------------------------------------------------------------
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
               Object[] record = new Object[4];
				record[0] = rs.getString("brrac");
				record[1] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
				record[2] = rs.getString("vrstaracuna");
				record[3] = rs.getInt("efakture");
				podaci.addElement(record[0]);
				spodaci.addElement(rs.getString("datum"));
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

