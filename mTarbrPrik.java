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

public class mTarbrPrik extends JDialog{
	public static String kojiKup, kojiNazKup;

	private JButton ok;					
	private JList list;
	private Connection connection = null;
	private String nazivp;
   	private QTMTarbrPrik qtbl;
   	private JTable jtbl;
	private Vector<String[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaci = new Vector<String>();
	private Vector<String> spodaci = new Vector<String>();
	private JTextField odkonta, dokonta, nazsaradnici;
	private String pPre,nazivPre="";
	private int kojaForma;

   public mTarbrPrik(Connection _connection,int kojaf)	{
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
 		JPanel listPanel = new JPanel(); 
		connection = _connection;


		pPre = "1";
		//nazivPre = IzborPre.kojiNaz;

		Border b=new TitledBorder(nazivPre);

		
		kojaForma = kojaf;
		//uzmiKonekciju();

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
		kojiKup = String.valueOf(podaci.get(koji));
		kojiNazKup = String.valueOf(spodaci.get(koji));
			if (kojiKup == null)
			{kojiKup = " ";}
		switch(kojaForma){
			case 1:
				mUnosPrijem.tt[15].setText(String.valueOf(Integer.parseInt(kojiKup)));
				mUnosPrijem.tt[16].setText(String.valueOf(Integer.parseInt(kojiNazKup)));
				break;
			case 3:
				mUnosPrijem.tt[7].setText(kojiKup);
				break;
			case 4:
				//mUnosKasa.tt[11].setText(String.valueOf(Integer.parseInt(kojiKup)));
				break;
			case 5:
				//mUnosOtpr.tt[15].setText(String.valueOf(Integer.parseInt(kojiKup)));
				break;
			case 6:
				//mUnosOtprM.tt[15].setText(String.valueOf(Integer.parseInt(kojiKup)));
				break;
			case 15:
				mRobe.t[4].setText(String.valueOf(Integer.parseInt(kojiKup)));
				break;
		}
		hide();
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
		else{
			JOptionPane.showMessageDialog(this, "Konekcija sa podacima nije moguca");
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

	   	qtbl = new QTMTarbrPrik(connection);
		String sql;
		sql = "SELECT * FROM tarifnibrojevi";
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
		jtbl.addRowSelectionInterval(0,0);
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check1");
        jtbl.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {quitForm();}});
		
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
//=======================================================================================
 class QTMTarbrPrik extends AbstractTableModel {
   Connection dbconn;
   //public Vector totalrows;
   String[] colheads = {"Tar.broj","Naziv","Vrsta","Porez"};

   public QTMTarbrPrik(Connection dbc){
	JPanel pp = new JPanel();
	dbconn = dbc;
      totalrows = new Vector<String[]>();
   }
//--------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 4; }
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
		String nn;
            while ( rs.next() ) {

				String[] record = new String[4];
				record[0] = rs.getString("tarb");
				record[1] = rs.getString("naztar");
				record[2] = rs.getString("vrtar");
				record[3] = rs.getString("por1");
				podaci.addElement(record[0]);
				spodaci.addElement(record[3]);
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

