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
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xerces.parsers.SAXParser;


public class mNuliranjeBazeA extends JDialog{
	private JButton ok;					
	private JList list;
	private ConnMySQL dbconn;
	private Connection connection = null;
	private String pPre,nazivPre,upit,upitlis,datumx,imebaze,godrada;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private boolean semNemaPodataka = true,zbir,pog;
	private boolean imaNemaPreduzeca = true,imapravila = true;
	private Integer sifra,postoji=0,postojip=0,vrpromx,jurr,magr;
	private Integer jurx,magx,kontox,sifmx,red;

	private JLabel rbr;
	private JFormattedTextField txtPoruka;
	private JPasswordField txtSifra;
   
   public mNuliranjeBazeA()	{
        setTitle("Nuliranje baze");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt)
			{
				Kreni();}});

		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
 		JPanel listPanel = new JPanel(); 
 		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); 

		JPanel buton = new JPanel(new FlowLayout());
		
		ok = new JButton("Start nuliranja");
		ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   Start(); }});
		uzmiKonekciju();

		JPanel sifra = new JPanel(new FlowLayout());
 
		JLabel rbr = new JLabel("Ukucaj \u0161ifru:");
		txtSifra = new JPasswordField("");
		txtSifra.setColumns(12);
        txtSifra.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        txtSifra.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(txtSifra);}});

		
		sifra.add(rbr);
		sifra.add(txtSifra);
		JPanel brr	= new JPanel(null);
					
		txtPoruka = new JFormattedTextField();
		txtPoruka.setBounds(50,50,400,40); //(X-osa,Y-osa,sirina,visina)
		txtPoruka.setEditable(false);		// X-osa udesno,  Y-osa dole (od gornjeg levog ugla ekrana)
		txtPoruka.setForeground(Color.red);

		buton.add(ok);
		brr.add(txtPoruka);

		cont.add(sifra,BorderLayout.NORTH);
		cont.add(brr,BorderLayout.CENTER);
		cont.add(buton,BorderLayout.SOUTH);
		pack();
		setSize(500,250);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		setLocation(50, 50);	
		txtSifra.requestFocus();
	}
//-----------------------------------------------------------------------------------
   public void Kreni() {
		zatvoriKonekciju();
		hide();
   }
//-----------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		ConnMySQL _dbconn = new ConnMySQL();
		if (_dbconn.getConnection() != null){
			connection = _dbconn.getConnection();
		}
		else
			{JOptionPane.showMessageDialog(this, "Konekcija sa podacima nije moguca program se prekida");
			System.exit(0);
		}
		return;
    }
//-----------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
			}
		}
   	}
//-----------------------------------------------------------------------------------
   public void Start() {
	   if (txtSifra.getText().trim().length() > 0)
	   {
		   if (txtSifra.getText().trim().equals("31101956") )
		   {
				String sql="";

				sql = "DELETE FROM rnal";
				izvrsi(sql);
				sql = "DELETE FROM uplate";
				izvrsi(sql);
				sql = "DELETE FROM zamenaulja";
				izvrsi(sql);
				sql = "DELETE FROM mprom1";
				izvrsi(sql);
				
				JOptionPane.showMessageDialog(this, "Gotovo");
		   }else{
				JOptionPane.showMessageDialog(this, "Neispravna \u0161ifra");
				txtSifra.setText("");
				txtSifra.requestFocus();
		   }
	   }else{
			JOptionPane.showMessageDialog(this, "Ukucaj prvo \u0161ifru");
			txtSifra.requestFocus();
	   }
   }
//=========================================================================
	public void Akcija(JPasswordField e) {
		JPasswordField source;
		source = e;
				if (source == txtSifra){
						ok.requestFocus();
				}
	}
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String _sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( _sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska: " + sqlex);
			JOptionPane.showMessageDialog(null, "Upit: " + _sql);
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
} //end of class 