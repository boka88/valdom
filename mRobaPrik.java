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

public class mRobaPrik extends JDialog{
	public static String kojaRoba, kojiNazRobe;

	private JButton ok;					
	private JList list;
	private Connection connection = null;
	private String nazivp;
   	private QTMKRobaPrik qtbl;
   	private JTable jtbl;
	private Vector<String[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaci = new Vector<String>();
	private Vector<String> spodaci = new Vector<String>();
	private JTextField odkonta, dokonta, nazsaradnici;
	private String pPre,nazivPre;
	private int kojaForma;

   public mRobaPrik(Connection _connection,int kojaf)	{
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
 		JPanel listPanel = new JPanel(); 
		setTitle("Sifarnik delova");

		pPre = "1";
		nazivPre = " ";
		connection = _connection;

		Border b=new TitledBorder(nazivPre);

		JPanel buton = new JPanel(new FlowLayout());
		buton.setBorder(b);
		
		JButton ok = new JButton("Svi");
		ok.setMnemonic('S');
		ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   svaKonta(); }});
		
		kojaForma = kojaf;

		JLabel lnazsaradnici = new JLabel("Naziv - trazi: ");
        nazsaradnici = new JTextField(15); 
		//------------------- programiranje provere i akcije ------------------------
        nazsaradnici.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        nazsaradnici.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {predjiUTabelu();}});
        nazsaradnici.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check1");
        nazsaradnici.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {quitForm();}});

		nazsaradnici.addCaretListener(new CaretListener() {
	               public void caretUpdate(CaretEvent e) {
				   proveriPoNazivu(); }});

 		buton.add(lnazsaradnici); 
 		buton.add(nazsaradnici); 
		buton.add(ok);
		cont.add(buton,BorderLayout.NORTH);
		cont.add(buildTable(),BorderLayout.CENTER);

		pack();
		setSize(620,400);
		
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
		int koji = jtbl.getSelectedRow();
		kojaRoba = String.valueOf(podaci.get(koji));
			if (kojaRoba == null)
			{kojaRoba = " ";}
		kojiNazRobe = String.valueOf(spodaci.get(koji));
		switch(kojaForma){
			case 1:
				aPanelDelovi.txtSifra.setText(kojaRoba);
				aPanelDelovi.txtNaziv.setText(kojiNazRobe);
				break;
			case 2:
				mUnosPoc.tt[1].setText(kojaRoba);
				mUnosPoc.displej.setText(kojiNazRobe);
				break;
			case 3:
				mUnosPrijem.tt[1].setText(kojaRoba);
				mUnosPrijem.displej.setText(kojiNazRobe);
				break;
			case 4:
				mUnosIzdat.tt[1].setText(kojaRoba);
				mUnosIzdat.displej.setText(kojiNazRobe);
				break;
		}
		//hide();
		this.setVisible(false);
   }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
        	this.setVisible(false);
    } 
//--------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );

	   	qtbl = new QTMKRobaPrik(connection);
		String sql;
		sql = "SELECT * FROM sifarnikrobe";
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
        jtbl.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"check1");
        jtbl.getActionMap().put("check1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {quitForm();}});
		jtbl.setFont(new Font("Tahoma",Font.BOLD,12));
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(30);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(150);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(40);
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
	   	tcol.setPreferredWidth(30);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(150);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(40);
	}
//--------------------------------------------------------------------------------------
    public void greska(){
		JOptionPane.showMessageDialog(this, "preso try");

    }
//--------------------------------------------------------------------------------------
    public void svaKonta(){
            String sql = "SELECT * FROM sifarnikrobe";
		popuniTabelu(sql);
    }
//--------------------------------------------------------------------------------------
    public void proveriPoNazivu(){
	            String sql = "SELECT * FROM sifarnikrobe WHERE nazivm " +
				" LIKE '%" + String.valueOf(nazsaradnici.getText()) + "%'";
			popuniTabelu(sql);
    }
//--------------------------------------------------------------------------------------
    public void predjiUTabelu(){
		jtbl.requestFocus();
		jtbl.addRowSelectionInterval(0,0);
    }
//=======================================================================================
 class QTMKRobaPrik extends AbstractTableModel {
   Connection dbconn;
   String[] colheads = {"Sifra","Naziv","JMere","Kat.br"};

   public QTMKRobaPrik(Connection dbc){
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
				record[0] = rs.getString("sifm");
				record[1] = rs.getString("nazivm");
				record[2] = rs.getString("jmere");
				record[3] = rs.getString("katbr");
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

