import java.io.*;
import java.sql.*;
import java.sql.ResultSet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.lang.*;
import java.util.*;
import javax.swing.table.*;
import java.text.*;
import java.util.ArrayList; 
import java.util.Arrays; 
import java.util.List; 


public class Repair extends JDialog{
	private QTBL qtbl;
   	private JTable jtbl;
	private JButton ok;					
	private Vector totalrows;
	private JList list;
	private ConnMySQL dbconn;
	private Connection connection = null;
	private String pPre,upit,upitlis,kontox,redbr;
	private boolean semNemaPodataka = true,zbir,pog;
	private boolean imaNemaPreduzeca = true,imapravila = true;
	private Integer sifra;
	private JLabel rbr;
	private int pozkup,duzkup,duzkup1,red;
	private int kupac,dobavljac,koperant,uvoz,izvoz;
	private double dugux,potrx;
	private JFormattedTextField txtBrojac,txtPoruka;
	//private JList lista;
	private JList<String> lista;
	public JScrollPane jspane;
	private DefaultListModel<String> listModel = new DefaultListModel<>();


   public Repair()	{
        setTitle("Reparacija baze podataka");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt)
			{
				Kreni();}});
		
		pPre = "1";

		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());

		lista = new JList();
		lista = new JList<>(listModel);
	   	jspane = new JScrollPane( lista );
		jspane.setBounds(50,60,350,300);
		jspane.getVerticalScrollBar().setPreferredSize(
				new Dimension(20, Integer.MAX_VALUE));

		JPanel buton = new JPanel(new FlowLayout());
		
		ok = new JButton("Start Reparacije");
		ok.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Start(); }});
		uzmiKonekciju();

		JLabel rbr = new JLabel("");
		JPanel brr = new JPanel(null);

		txtBrojac = new JFormattedTextField();
		txtBrojac.setBounds(50,20,100,20);
		txtBrojac.setEditable(false);

		txtPoruka = new JFormattedTextField();
		txtPoruka.setBounds(50,20,350,40);	//(X-osa,Y-osa,sirina,visina)
		txtPoruka.setEditable(false);		// X-osa udesno, Y-osa dole (od gornjeg levog ugla ekrana)
		txtPoruka.setForeground(Color.red);
						
		buton.add(ok);
		brr.add(txtPoruka);
		
		brr.add(buildTable());
		
		//brr.add(jspane);

		cont.add(brr,BorderLayout.CENTER);
		cont.add(buton,BorderLayout.SOUTH);
		pack();
		setSize(470,450);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		setLocation(((screenWidth / 2) - 150), ((screenHeight / 2) - 260));
		
	}
//-----------------------------------------------------------------------------------
   public void Kreni() {
			if (connection != null)
			{
				try {connection.close(); } 
				catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
				}
			}
		setVisible(false);
   }
//------------------------------------------------------------------------------------------------------------------
	public void uzmiKonekciju(){
		ConnMySQL _dbconn = new ConnMySQL();
		if (_dbconn.getConnection() != null)
			{connection = _dbconn.getConnection();}
		else
			{JOptionPane.showMessageDialog(this, "Konekcija nije otvorena");}
		return;
    }
//-----------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );

	   	qtbl = new QTBL();
	   	qtbl.query("","");
 	   	jtbl = new JTable( qtbl );
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(50);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		ptbl.setBounds(50,60,350,300);

		return ptbl;
	}
//-----------------------------------------------------------------------------------
   public void Start() {
		String naziv="";
			txtPoruka.setText("RADI SE REKONSTRUKCIJA MOLIM SA\u010cEKAJTE.....");
			paintAll(getGraphics());

				
			Statement statement = null;
			try {
				 statement = connection.createStatement();

					String query = "SHOW TABLES";

					ResultSet rs = statement.executeQuery( query );
						 while(rs.next()){
							naziv = rs.getString(1);
							//listModel.addElement(naziv);
							Repariraj(naziv);
						}
			}catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(this, "Greska :"+sqlex);
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

			//=====================================================================
		txtPoruka.setText("");
		paintAll(getGraphics());

		JOptionPane.showMessageDialog(null, "REKONSTRUKCIJA BAZE JE ZAVR\u0160ENA");
		ok.setEnabled(false);
  }
  //-----------------------------------------------------------------------------------
  private void Repariraj(String _naziv){
		String upit = "",status="";
		int ii = 1;
		upit = "REPAIR TABLE " + _naziv + " USE_FRM";
			Statement statement = null;
			try {
				 statement = connection.createStatement();
					ResultSet rs = statement.executeQuery( upit );
						 while(rs.next()){
							 if (ii == 2)
							 {
								status = rs.getString("Msg_text");
								qtbl.query(_naziv,status);

								//listModel.addElement(" "+_naziv + "\t\t" +" "+status);
							 }
							ii++;
						}
						qtbl.fire();
						TableColumn tcol = jtbl.getColumnModel().getColumn(0);
						tcol.setPreferredWidth(50);
						TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
						tcol1.setPreferredWidth(50);

			}catch ( SQLException sqlex ) {
				JOptionPane.showMessageDialog(this, "Greska :"+sqlex);
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
  //-----------------------------------------------------------------------------------
   public void izvrsi(String _upit) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( _upit );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska: " + sqlex);
			JOptionPane.showMessageDialog(this, "Upit: " + _upit);
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
  //-----------------------------------------------------------------------------------
   public String Formatiraj(String _naziv) {
	  // String vrati="";
	   int j=1;
	   for (j=1;j<(50-_naziv.length()) ; j++)
	   {
		   _naziv = _naziv + "_";
	   }

		return _naziv;
  }
//======================================================================================
   class QTBL extends AbstractTableModel {
	String[] colheads = {"Tabela","Status"};

//------------------------------------------------------------------------------------------------------------------
   public QTBL(){
		JPanel pp = new JPanel();
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 2; }
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
 //------------------------------------------------------------------------------------------------------------------
   public void query(String _naziv,String _status) {
           Object[] record = new Object[2];
           record[0] = _naziv;
           record[1] = _status;
           totalrows.addElement( record );

    }
 }//end of class QTBL

//-----------------------------------------------------------------------------------
} //end of class IborPre
