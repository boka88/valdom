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


public class mPanelRnal extends JPanel{

	private JList list;
	private Connection connection = null;
	private String nazivp,koji1,koji,sql="";
   	private mQTMK12 qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector dodatni = new Vector();
	private Vector sdodatni = new Vector();
	private Vector bdodatni = new Vector();
	public static JTextField naziv;
	private int kojaForma,kontomal,prodavnica,koji2;

	public static String brsasije="";

   public mPanelRnal()	{

		this.setLayout(new BorderLayout());

		JPanel buton = new JPanel(new FlowLayout());

		uzmiKonekciju();

		JLabel lab = new JLabel("Upi\u0161i registraciju ili deo: ");
		naziv = new JTextField(15);
        naziv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        naziv.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija();}});
        naziv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check2");
        naziv.getActionMap().put("check2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {aOstalaOprema.t[4].requestFocus();}});
		naziv.addCaretListener(new CaretListener() {
	               public void caretUpdate(CaretEvent e) {
				   proveriPoNazivu(); }});

 		buton.add(lab);
 		buton.add(naziv);
		this.add(buton,BorderLayout.NORTH);
		this.add(buildTable(),BorderLayout.CENTER);

		this.setSize(270,500);

	}
//------------------------------------------------------------------------------------------------------------------
   public static void idiNaPolje() {
		naziv.setText("");
		naziv.setSelectionStart(0);
		naziv.requestFocus();
   }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
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
		{JOptionPane.showMessageDialog(this, "Konekcija sa nosiocima nije moguca");
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
		Color ccc = new Color(80,80,80);
	   	qtbl = new mQTMK12(connection);
		String sql;

		sql = "SELECT rbr,brsasije,regbroj,datum,opiskvara FROM rnal ORDER BY datum DESC,rbr";

	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.setFont(new Font("Tahoma",Font.BOLD,10));
		jtbl.setForeground(ccc);
		jtbl.setRowSelectionAllowed(true);
		jtbl.setSelectionBackground(Color.red);
 
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        jtbl.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {prikaziIzTabele();}});
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check2");
        jtbl.getActionMap().put("check2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {aOstalaOprema.t[0].requestFocus();}});

		jtbl.addMouseListener(new ML());

	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(100);

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
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(100);
	}
//--------------------------------------------------------------------------------------
  public void greska(){
		JOptionPane.showMessageDialog(this, "preso try");
    }
//--------------------------------------------------------------------------------------
  public void proveriPoNazivu(){
		String deo="";
		//if (naziv.getText().trim().length() > 0){

			sql = "SELECT rbr,brsasije,regbroj,datum,opiskvara FROM rnal WHERE regbroj" +
				" LIKE '%" + String.valueOf(naziv.getText()) + "%' ORDER BY datum DESC";
			popuniTabelu(sql);
		//}
    }
//--------------------------------------------------------------------------------------
  public void proveriDeoNaziva(){
		String sql,naz="";
		int duzz=0;
		duzz = naziv.getText().length();
		if (duzz > 1)
		{
			naz = String.valueOf(naziv.getText().substring(1,duzz));
			sql = "SELECT rbr,brsasije,regbroj,datum,opiskvara FROM rnal WHERE regbroj" +
				" LIKE '%" + String.valueOf(naziv.getText()) + "%' ORDER BY datum DESC";
			popuniTabelu(sql);
		}
    }
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = String.valueOf(dodatni.get(kojirec));
		String queryy;
		Statement statement = null;
      try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM rnal WHERE rbr=" + koji;
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						aPregledNaloga.t[0].setText(rs.getString("rbr"));
						aPregledNaloga.t[1].setText(konvertujDatumIzPodataka(rs.getString("datum")));
						aPregledNaloga.t[2].setText(rs.getString("brsasije").trim());
						aPregledNaloga.sifRadnika.setText(rs.getString("jmbg").trim());
						aPregledNaloga.zaglavlje.setText(rs.getString("opiskvara"));
						aPregledNaloga.podnozje.setText(rs.getString("opisradova"));
						aPregledNaloga.delovi.setText(rs.getString("delovi"));
						aPregledNaloga.napomena.setText(rs.getString("napomena"));
						
						String sql = "SELECT * FROM mprom1 WHERE rnal=" + 
							rs.getString("rbr").trim() + " ORDER BY sifm";
						aPregledNaloga.popuniTabelu(sql);
						rs.close();
						//aPregledNaloga.t[4].requestFocus();
					}else{
						JOptionPane.showMessageDialog(this, "Nema tog naloga");
					
					}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju naloga:"+sqlex);
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				JOptionPane.showMessageDialog(this, "U:prikaziIzTabele: Nije uspeo da zatvori statement");}}
		}//********************************************************************************************
	}
//--------------------------------------------------------------------------------------
   public String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
//--------------------------------------------------------------------------------------
  public void Akcija(){
		jtbl.requestFocus();
		if (jtbl.getRowCount() > 0  )
		{
			jtbl.addRowSelectionInterval(0,0);
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
//=======================================================================================
 class mQTMK12 extends AbstractTableModel {
   Connection dbconn;
   //public Vector totalrows;
   String[] colheads = {"R.nal.","Br.\u0161asije","Registracija","Datum","Opis kvara"};

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
	dodatni.clear();
	sdodatni.clear();
	bdodatni.clear();
	  Statement statement = null;
      try {
         statement = dbconn.createStatement();

            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector();
            while ( rs.next() ) {
               Object[] record = new Object[5];
               record[0] = rs.getString("rbr");
               record[1] = rs.getString("brsasije");
               record[2] = rs.getString("regbroj");
               record[3] = konvertujDatumIzPodatakaQTB(rs.getString("datum"));
               record[4] = rs.getString("opiskvara");
				
				totalrows.addElement( record );

				dodatni.addElement(record[0]);
				sdodatni.addElement(record[1]);
				bdodatni.addElement(record[4]);
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

