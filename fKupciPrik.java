//prikaz kupaca za unos dokumenata
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

public class fKupciPrik extends JDialog{
	public static String kojiKup, kojiNazKup;

	private JButton ok;					
	private JList list;
	private Connection connection = null;
	private String nazivp;
   	private QueryTableModel2 qtbl;
   	private JTable jtbl;
	private Vector<String[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaci = new Vector<String>();
	private Vector<String> spodaci = new Vector<String>();
	private JTextField odkonta, dokonta, nazsaradnici;
	private String pPre,nazivPre;
	private int kojaForma;

   public fKupciPrik(Connection _connection,int kojaf)	{
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
 		JPanel listPanel = new JPanel(); 
		setTitle("Saradnici");

		pPre = "1";
		nazivPre = "Valdom";
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
            public void actionPerformed(ActionEvent e) {		
				jtbl.requestFocus();
				jtbl.addRowSelectionInterval(0,0);}});
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
		int koji = jtbl.getSelectedRow();
		kojiKup = String.valueOf(podaci.get(koji));
			if (kojiKup == null)
			{kojiKup = " ";}
		kojiNazKup = String.valueOf(spodaci.get(koji));

		switch(kojaForma){
			case 1:
			case 2:
			case 3:
				mUnosPrijem.tt[7].setText(kojiKup);
				mUnosPrijem.dispVrsDok.setText(kojiNazKup);
				break;
			case 4:
				//mUnosFakt.t[2].setText(kojiKup);
				//mUnosFakt.displej.setText(kojiNazKup);
				break;
			case 5:
				//mUnosOtpr.tt[7].setText(kojiKup);
				//mUnosOtpr.dispSarad.setText(kojiNazKup);
				break;

		}
	    this.setVisible(false);
   }
//-------------------------------------------------------------------------------------------------
    public void quitForm() {
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

	   	qtbl = new QueryTableModel2(connection);
		String sql;
		sql = "SELECT * FROM saradnici";
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
    public void svaKonta(){
            String sql = "SELECT * FROM saradnici";
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
	      if ( !nazsaradnici.getText().equals( "" )) { 
	            String sql = "SELECT * FROM saradnici WHERE nazivkupca " +
				" LIKE '%" + String.valueOf(nazsaradnici.getText()) + "%'";
			popuniTabelu(sql);
		}
    }
//=======================================================================================
 class QueryTableModel2 extends AbstractTableModel {
   Connection dbconn;
   //public Vector totalrows;
   String[] colheads = {"Br.saradnika","Naziv","Adresa","Mesto"};

   public QueryTableModel2(Connection dbc){
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
		//JOptionPane.showMessageDialog(this, datum);
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
				record[0] = rs.getString("kupacbr");
				record[1] = rs.getString("nazivkupca");
				record[2] = rs.getString("adresa");
				record[3] = rs.getString("mesto");
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

