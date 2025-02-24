//prikaz kupaca za unos dokumenata
import java.io.*;
import java.sql.*;
import java.sql.ResultSet.*;
import java.awt.*;
import java.awt.event.*;
//import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.lang.*;
import java.util.*;
import javax.swing.table.*;
import java.text.*;

public class KorisniciPrik extends JDialog{
	//public static String kojiKor, kojeIme;

	private JButton ok;					
	private JList list;
	private Connection connection = null;
	private String nazivp;
   	private QueryTableModel2 qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private Vector mpodaci = new Vector();
	private JTextField odkonta, dokonta, nazkorisnici;
	private String kojiKor,kojeIme,kojePrezime;
	private int kojaForma;

   public KorisniciPrik(int kojaf)	{
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
 		JPanel listPanel = new JPanel(); 

		JPanel buton = new JPanel(new FlowLayout());
		
		JButton ok = new JButton("Svi");
		ok.setMnemonic('S');
		ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   svaKonta(); }});
		
		kojaForma = kojaf;
		uzmiKonekciju();

		JLabel lnazkorisnici = new JLabel("Prezime - tra\u017ei: ");
        nazkorisnici = new JTextField(15); 
		//------------------- programiranje provere i akcije ------------------------
        nazkorisnici.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        nazkorisnici.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {proveriPoNazivu();}});

 		buton.add(lnazkorisnici); 
 		buton.add(nazkorisnici); 
		buton.add(ok);
		cont.add(buton,BorderLayout.NORTH);
		cont.add(buildTable(),BorderLayout.CENTER);

		pack();
		setSize(500,300);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		this.setTitle("\u0160ifarnik korisnika");
		setLocation(((screenWidth / 2) - 250), ((screenHeight / 2) - 260));		 
		this.addWindowListener(new WindowAdapter() {
	                       public void windowClosing(WindowEvent e) {
				   quitForm(); }});
	}
 
//--------------------------------------------------------------------------------------
   public void Kreni() {
		zatvoriKonekciju();
		int koji = jtbl.getSelectedRow();
		kojiKor = String.valueOf(podaci.get(koji));
			if (kojiKor == null){
				kojiKor = " ";
				kojeIme = "";
				kojePrezime = "";}
			else{
				kojeIme = String.valueOf(spodaci.get(koji));
				kojePrezime = String.valueOf(mpodaci.get(koji));
			}
		switch(kojaForma){
			case 1:
				aVozila.t[0].setText(kojiKor);
				aVozila.displej.setText(kojeIme + "  " + kojePrezime);
				break;
			case 2:
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
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );

	   	qtbl = new QueryTableModel2(connection);
		String sql;
		sql = "SELECT * FROM korisnici";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		//------------------- programiranje provere i akcije ------------------------
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        jtbl.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Kreni();}});
		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(100);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(100);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(100);
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
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(100);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(100);
	}
//--------------------------------------------------------------------------------------
    public void greska(){
		JOptionPane.showMessageDialog(this, "preso try");
    }
//--------------------------------------------------------------------------------------
    public void svaKonta(){
            String sql = "SELECT * FROM korisnici";
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
	      if ( !nazkorisnici.getText().equals( "" )) { 
	            String sql = "SELECT * FROM korisnici WHERE prezime " +
				" LIKE '" + String.valueOf(nazkorisnici.getText()) + "%'";
			popuniTabelu(sql);
		}
		jtbl.requestFocus();
		jtbl.addRowSelectionInterval(0,0);
    }
//=======================================================================================
 class QueryTableModel2 extends AbstractTableModel {
   Connection dbconn;
   //public Vector totalrows;
   String[] colheads = {"JMBG","Ime","Prezime","Adresa","Mesto"};

   public QueryTableModel2(Connection dbc){
	JPanel pp = new JPanel();
	dbconn = dbc;
      totalrows = new Vector();
   }
//--------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 5; }
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
//--------------------------------------------------------------------------------------
   public String konvertujDatumIzPodatakaQTB(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
		//JOptionPane.showMessageDialog(this, datum);
	return datum;
   }
//--------------------------------------------------------------------------------------
   public void query(String _sql) {
	String sql;
	sql = _sql;
	podaci.clear();
	spodaci.clear();
      try {
         Statement statement = dbconn.createStatement();
		//greska();
               
            ResultSet rs = statement.executeQuery( sql );
		totalrows = new Vector();
		String nn;
            while ( rs.next() ) {

				String[] record = new String[5];
				record[0] = vodeceNule(rs.getString("JMBG"));
				record[1] = rs.getString("ime");
				record[2] = rs.getString("prezime");
				record[3] = rs.getString("ulica");
				record[4] = rs.getString("mesto");
				podaci.addElement(record[0]);
				spodaci.addElement(record[1]);
				mpodaci.addElement(record[2]);
				totalrows.addElement( record );

            }
            statement.close();
         	statement = null;
         }
         catch ( SQLException sqlex ) {
         }
		//return;
    }
 }//end of class QueryTableModel
}//end of class KupciPri

