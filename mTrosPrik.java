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


public class mTrosPrik extends JDialog{
	public static String kojiKonto, kojiNazKonta;

	private JButton ok,stampa;
	private JList list;
	private Connection connection = null;
	private String nazivp;
   	private mQTMTrosPrik qtbl;
	private String pPre,nazivPre;
   	private JTable jtbl;
	private Vector<String[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaci = new Vector<String>();
	private Vector<String> spodaci = new Vector<String>();
	private JTextField naziv;
	private int kojaForma;

   public mTrosPrik(Connection _connection,int kojaf)	{

		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
		setTitle("Mesta troska");
 		JPanel listPanel = new JPanel();
		connection = _connection;

		kojaForma = kojaf;
		JPanel buton = new JPanel(new FlowLayout());

		JButton ok2 = new JButton("Trazi po nazivu");
		ok2.setMnemonic('N');
		ok2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   proveriPoNazivu(); }});
		//uzmiKonekciju();
		pPre = new String("1");

		JLabel lab = new JLabel("Upisi naziv: ");
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
				mUnosPrijem.tt[11].setText(kojiKonto);
				mUnosPrijem.displej.setText(kojiNazKonta);
				break;
			case 2:
				mUnosIzdat.tt[11].setText(kojiKonto);
				mUnosIzdat.displej.setText(kojiNazKonta);
				break;
			/*
			case 3:
				mUnosFakt.tt[7].setText(kojiKonto);
				mUnosFakt.displej.setText(kojiNazKonta);
				break;
			case 4:
				mUnosPrijemM.tt[11].setText(kojiKonto);
				mUnosPrijemM.displej.setText(kojiNazKonta);
				break;
			case 5:
				mUnosOtprM.tt[8].setText(kojiKonto);
				mUnosOtprM.dispMT.setText(kojiNazKonta);
				break;
			case 6:
				mUnosFaktM.tt[7].setText(kojiKonto);
				mUnosFaktM.displej.setText(kojiNazKonta);
				break;
			case 7:
				fUnosn.tt[8].setText(kojiKonto);
				break;
			case 20:
				mRNal.t[6].setText(kojiKonto);
				mRNal.l[10].setText(kojiNazKonta);
				break;
			case 21:
				fUnosRn.tt[6].setText(kojiKonto);
				break;
			case 22:
				mUnosOtpr.tt[8].setText(kojiKonto);
				mUnosOtpr.dispMT.setText(kojiNazKonta);
				break;
			case 23:
				fUnospoc.tt[8].setText(kojiKonto);
				break;
			*/
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
	   /*
		ConnMySQL _dbconn = new ConnMySQL();
		if (_dbconn.getConnection() != null){
			connection = _dbconn.getConnection();
		}
		else
			{JOptionPane.showMessageDialog(this, "Konekcija sa podacima nije moguca");
			}
		return;
		*/
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
	   /*
		if (connection != null){
			try {	connection.close(); }
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
			}
		}
		*/
   	}
//--------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );

	   	qtbl = new mQTMTrosPrik(connection);
		String sql;
		sql = "SELECT * FROM mestatroska Where pre=" + Integer.parseInt(pPre) + 
								" order by mtros";
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
  public void proveriPoNazivu(){
			if ( !naziv.getText().equals( "" )) {
	            String sql = "SELECT * FROM mestatroska WHERE naztros" +
				" LIKE '" + String.valueOf(naziv.getText()) + "%' AND pre=" +
					Integer.parseInt(pPre);
			popuniTabelu(sql);
			}
    }
	//--------------------------------------------------------------------------------------
  public void Akcija(){
		jtbl.requestFocus();
		jtbl.addRowSelectionInterval(0,0);
    }
//=======================================================================================
 class mQTMTrosPrik extends AbstractTableModel {
   Connection dbconn;
	String[] colheads = {"M.Troska","Naziv mesta troska","J.Mere"};

   public mQTMTrosPrik(Connection dbc){
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
               String[] record = new String[3];
				record[0] = rs.getString("mtros");
				record[1] = rs.getString("naztros");
				record[2] = rs.getString("nazjm");
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

