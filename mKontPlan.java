//prikaz i trazenje kontnog plana kod unosa naloga
//==========================================================================
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


public class mKontPlan extends JDialog{
	public static String kojiKonto, kojiNazKonta;

	private JButton ok,stampa;
	private JList list;
	private Connection connection = null;
	private String nazivp;
   	private mQTMK qtbl;
   	private JTable jtbl;
	private Vector<String[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaci = new Vector<String>();
	private Vector<String> spodaci = new Vector<String>();
	private JTextField naziv;
	private int kojaForma;

   public mKontPlan(Connection _connection,int kojaf)	{

		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
		setTitle("Kontni plan");
 		JPanel listPanel = new JPanel();

		kojaForma = kojaf;
		JPanel buton = new JPanel(new FlowLayout());

		JButton ok2 = new JButton("Tra\u017ei po nazivu");
		ok2.setMnemonic('N');
		ok2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   proveriPoNazivu(); }});

		connection = _connection;
		//uzmiKonekciju();

		JLabel lab = new JLabel("Upi\u0161i konto ili naziv: ");
		naziv = new JTextField(15);
        naziv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        naziv.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija();}});
        naziv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check1");
        naziv.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {quitForm();}});

		naziv.addCaretListener(new CaretListener() {
	               public void caretUpdate(CaretEvent e) {
				   proveriPoNazivu(); }});


 		buton.add(lab);
 		buton.add(naziv);
		buton.add(ok2);
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
		//zatvoriKonekciju();
		int koji = jtbl.getSelectedRow();
		kojiKonto = String.valueOf(podaci.get(koji));
		kojiNazKonta = String.valueOf(spodaci.get(koji));
		switch(kojaForma){
			case 1:
				mUnosPrijem.tt[2].setText(kojiKonto);
				mUnosPrijem.dispVrsDok.setText(kojiNazKonta);
				break;
			case 2:
				//mKartice.t[4].setText(kojiKonto);
				//mKartice.mkdisplej.setText(kojiNazKonta);
				break;
			case 3:
				mUnosPoc.tt[2].setText(kojiKonto);
				mUnosPoc.dispVrsDok.setText(kojiNazKonta);
				break;
			case 4:
				mUnosIzdat.tt[2].setText(kojiKonto);
				mUnosIzdat.dispVrsDok.setText(kojiNazKonta);
				break;
			case 5:
				//mUnosIzdat.tt[2].setText(kojiKonto);
				//mUnosIzdat.dispVrsDok.setText(kojiNazKonta);
				break;
		}
		this.setVisible(false);
   }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
    	/*
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
			}
		}
		*/
        this.setVisible(false);
    } 
//--------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
   	}
//--------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );

	   	qtbl = new mQTMK(connection);
		String sql;
		sql = "SELECT * FROM materijalnakonta";
	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); 
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); 
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
 	   	
		//jtbl = new JTable( qtbl );
		//------------------- programiranje provere i akcije ------------------------
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        jtbl.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Kreni();}});

	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(10);

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
	   	tcol.setPreferredWidth(15);
	}
//--------------------------------------------------------------------------------------
  public void greska(){
		JOptionPane.showMessageDialog(this, "preso try");
    }
//--------------------------------------------------------------------------------------
  public void proveriPoKontu(){
	      if ( !naziv.getText().equals( "" )) {
	            String sql = "SELECT * FROM materijalnakonta WHERE konto " +
				" LIKE '" + String.valueOf(naziv.getText()) + "%'";
			popuniTabelu(sql);
		}
		jtbl.requestFocus();
		jtbl.addRowSelectionInterval(0,0);
    }
	//--------------------------------------------------------------------------------------
  public void proveriPoNazivu(){
	      if ( !naziv.getText().equals( "" )) {
	            String sql = "SELECT * FROM materijalnakonta WHERE nazivk " +
				" LIKE '" + String.valueOf(naziv.getText()) + "%'";
			popuniTabelu(sql);
		}
    }
	//--------------------------------------------------------------------------------------
  public void Akcija(){
		jtbl.requestFocus();
		jtbl.addRowSelectionInterval(0,0);
    }
//=======================================================================================
 class mQTMK extends AbstractTableModel {
   Connection dbconn;
   String[] colheads = {"konto","naziv mat. konta"};

   public mQTMK(Connection dbc){
	JPanel pp = new JPanel();
	dbconn = dbc;
      totalrows = new Vector<String[]>();
   }
//--------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 2; }
   public int getRowCount() { return totalrows.size(); }
   public Object getValueAt(int row, int col) {
      return totalrows.elementAt(row)[col];
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
			totalrows = new Vector<String[]>();
            while ( rs.next() ) {

				String[] record = new String[2];
				record[0] = rs.getString("konto");
				record[1] = rs.getString("nazivk");
				podaci.addElement(record[0]);
				spodaci.addElement(record[1]);
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

