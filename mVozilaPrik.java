//prikaz i izbor robe-materijala
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


public class mVozilaPrik extends JDialog{
	public static String kojaRoba, kojiNazRobe;

	private JButton ok;					
	private JList list;
	private Connection connection = null;
	private String nazivp;
   	private QTMKRobaPrik qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private Vector bpodaci = new Vector();
	private JTextField odkonta, dokonta, nazsaradnici,ime;
	private String pPre,nazivPre;
	private int kojaForma;

   public mVozilaPrik(int kojaf)	{
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
 		JPanel listPanel = new JPanel(); 
		setTitle("\u0160ifarnik vozila");

		pPre = "1";
		nazivPre = "";

		Border b=new TitledBorder(nazivPre);

		JPanel buton = new JPanel(new FlowLayout());
		buton.setBorder(b);
		
		JButton ok = new JButton("Svi");
		ok.setMnemonic('S');
		ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   svaKonta(); }});
		
		kojaForma = kojaf;
		uzmiKonekciju();

		JLabel lnazsaradnici = new JLabel("Br.\u0161asije ");
        nazsaradnici = new JTextField(15); 
		//------------------- programiranje provere i akcije ------------------------
        nazsaradnici.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        nazsaradnici.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {predjiUIme();}});
		nazsaradnici.addCaretListener(new CaretListener() {
	               public void caretUpdate(CaretEvent e) {
				   proveriPoNazivu(); }});

		JLabel lblime = new JLabel("Reg.br: ");
        ime = new JTextField(15); 
		//------------------- programiranje provere i akcije ------------------------
        ime.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        ime.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {predjiUTabelu();}});
		ime.addCaretListener(new CaretListener() {
	               public void caretUpdate(CaretEvent e) {
				   proveriPoImenu(); }});

 		buton.add(lnazsaradnici); 
 		buton.add(nazsaradnici); 
 		buton.add(lblime); 
 		buton.add(ime); 

		buton.add(ok);
		cont.add(buton,BorderLayout.NORTH);
		cont.add(buildTable(),BorderLayout.CENTER);

		pack();
		setSize(600,400);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		setLocation(((screenWidth / 2) - 250), ((screenHeight / 2) - 260));		 
		this.addWindowListener(new WindowAdapter() {
	                       public void windowClosing(WindowEvent e) {
				   quitForm(); }});
	}
 
//--------------------------------------------------------------------------------------
   public void Kreni() {
		zatvoriKonekciju();
		int koji = jtbl.getSelectedRow();
		kojaRoba = String.valueOf(podaci.get(koji));
			if (kojaRoba == null)
			{kojaRoba = " ";}
		kojiNazRobe = String.valueOf(spodaci.get(koji));
		switch(kojaForma){
			case 1:
				mRNal.t[2].setText(String.valueOf(podaci.get(koji)));
				mRNal.regbroj=kojiNazRobe;
				break;
			case 2:
				aTrazenjeRadNal.t[0].setText(String.valueOf(podaci.get(koji)));
				aTrazenjeRadNal.mkdisplej.setText(kojiNazRobe);
				break;
			case 3:
				mUplate.t[1].setText(String.valueOf(podaci.get(koji)));
				mUplate.mkdisplej.setText(kojiNazRobe);
				break;
			case 4:
				mFinanKupci.t[0].setText(String.valueOf(podaci.get(koji)));
				mFinanKupci.mkdisplej.setText(kojiNazRobe);
				break;
			case 5:
				mFinanKupciRn.t[0].setText(String.valueOf(podaci.get(koji)));
				mFinanKupciRn.mkdisplej.setText(kojiNazRobe);
				break;
			case 6:
				mUnosIzdat.tt[11].setText(String.valueOf(podaci.get(koji)));
				mUnosIzdat.dispMT.setText(kojiNazRobe);
				break;
			case 7:
				mKarticeV.t[5].setText(String.valueOf(podaci.get(koji)));
				mKarticeV.mkdisplej.setText(kojiNazRobe);
				break;
			case 8:
				aGorivo.t[1].setText(String.valueOf(podaci.get(koji)));
				aGorivo.mkdisplej.setText(kojiNazRobe);
				break;
			case 9:
				mKarticeGorivo.t[0].setText(String.valueOf(podaci.get(koji)));
				mKarticeGorivo.mkdisplej.setText(kojiNazRobe);
				break;
			case 10:
				mPregledNalogaR.t[0].setText(String.valueOf(podaci.get(koji)));
				mPregledNalogaR.mkdisplej.setText(kojiNazRobe);
				break;
			case 11:
				aOstalaOprema.t[8].setText(String.valueOf(podaci.get(koji)));
				break;
			case 12:
				aZamenaUlja.t[1].setText(String.valueOf(podaci.get(koji)));
				aZamenaUlja.mkdisplej.setText(kojiNazRobe);
				break;
		}
		hide();
   }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
	// zatvaranje programa-----------------------------
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
			}
		}
        	this.setVisible(false);
    } 
//--------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
	ConnMySQL _dbconn = new ConnMySQL();
	if (_dbconn.getConnection() != null){
		connection = _dbconn.getConnection();
	}
	else
		{JOptionPane.showMessageDialog(this, "Konekcija sa podacima nije moguca");
		}
	return;
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
			}
		}
   	}
//--------------------------------------------------------------------------------------
    public JPanel buildTable() {
		Color ccc = new Color(100,100,100);
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );

	   	qtbl = new QTMKRobaPrik(connection);
		String sql;

		sql = "SELECT * FROM vozila ";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		//------------------- programiranje provere i akcije ------------------------
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        jtbl.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Kreni();}});

		jtbl.setFont(new Font("Tahoma",Font.PLAIN,12));
		jtbl.setForeground(ccc);

	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(100);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(120);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//--------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(100);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(80);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(120);
	}
//--------------------------------------------------------------------------------------
    public void greska(){
		JOptionPane.showMessageDialog(this, "preso try");

    }
//--------------------------------------------------------------------------------------
    public void svaKonta(){
            String sql = "SELECT * FROM vozila";
		popuniTabelu(sql);
    }
//--------------------------------------------------------------------------------------
    public String vodeceNule(String _nule){
		String saNulama,nule;
		saNulama = new String("");
		int i,j;
		saNulama = _nule.trim();
		j = saNulama.length();
		for (	i=j;i<4;i++)
			{ saNulama = "0" + saNulama;  }	
		return saNulama;
    }
//--------------------------------------------------------------------------------------
    public void proveriPoNazivu(){
	    String sql = "SELECT * " +
			"FROM vozila WHERE brsasije " +
			" LIKE '" + String.valueOf(nazsaradnici.getText()) + "%'";
			popuniTabelu(sql);
    }
//--------------------------------------------------------------------------------------
    public void proveriPoImenu(){
	    String sql = "SELECT * " +
			"FROM vozila WHERE regbroj " +
			" LIKE '%" + String.valueOf(ime.getText()) + "%'";
			popuniTabelu(sql);
    }
//--------------------------------------------------------------------------------------
    public void predjiUTabelu(){
		jtbl.requestFocus();
		jtbl.addRowSelectionInterval(0,0);
    }
//--------------------------------------------------------------------------------------
    public void predjiUIme(){
		if (nazsaradnici.getText().trim().length() > 0)
		{
			predjiUTabelu();
		}else{
			ime.requestFocus();
		}
    }
//=======================================================================================
 class QTMKRobaPrik extends AbstractTableModel {
   Connection dbconn;
   String[] colheads = {"Br.\u0161asije","Reg.br.","Br.motora","Vrsta","Marka","Tip"};
   public QTMKRobaPrik(Connection dbc){
	JPanel pp = new JPanel();
	dbconn = dbc;
      totalrows = new Vector();
   }
//--------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 6; }
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
		String nn;
            while ( rs.next() ) {

               Object[] record = new Object[6];
               record[0] = rs.getString("brsasije");
               record[1] = rs.getString("regbroj");
               record[2] = rs.getString("brmotora");
               record[3] = rs.getString("vrstavozila");
               record[4] = rs.getString("marka");
               record[5] = rs.getString("tip");
				podaci.addElement(record[0]);
				spodaci.addElement(record[1]);
				//bpodaci.addElement(record[6]);
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

