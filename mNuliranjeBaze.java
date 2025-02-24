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


public class mNuliranjeBaze extends JDialog{
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
   
   public mNuliranjeBaze()	{
        setTitle("Nuliranje baze");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt)
			{
				Kreni();}});

		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
 		JPanel listPanel = new JPanel(); 
 		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); 

		JPanel buton	= new JPanel(new FlowLayout());
		
		JButton ok = new JButton("Start brisanja");
		ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PocStart(); }});
		uzmiKonekciju();
 
		JLabel rbr = new JLabel("");
		JPanel brr	= new JPanel(null);
					
		txtPoruka = new JFormattedTextField();
		txtPoruka.setBounds(50,50,400,40); //(X-osa,Y-osa,sirina,visina)
		txtPoruka.setEditable(false);		// X-osa udesno,  Y-osa dole (od gornjeg levog ugla ekrana)
		txtPoruka.setForeground(Color.red);

		JLabel poruk = new JLabel("PA\u017dNJA - Bri\u0161u se svi podaci iz baze");
		poruk.setForeground(Color.red);
		poruk.setFont(new Font("Arial",Font.BOLD,14));
		
		buton.add(ok);
		
		JPanel poruka	= new JPanel(new FlowLayout());

		poruka.add(poruk);
		
		cont.add(poruka,BorderLayout.NORTH);
		cont.add(poruka,BorderLayout.CENTER);
		cont.add(buton,BorderLayout.SOUTH);
		pack();
		setSize(500,250);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		setLocation(((screenWidth / 2) - 150), 0);		 
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
// -----------------------------------------------------------------------------------
   private void PocStart(){
	   int aaa=0;
	   aaa = JOptionPane.showConfirmDialog(this,"Da li stvarno bri\u0161ete sve podatke","Da li stvarno bri\u0161ete sve podatke",0);
	   if (aaa == 0){
		   Start();
	   }

   	}
//-----------------------------------------------------------------------------------
   public void Start() {
		String sql="";
		sql = "DELETE FROM rnal";
		izvrsi(sql);
		sql = "DELETE FROM uplate";
		izvrsi(sql);
		sql = "DELETE FROM zamenaulja";
		izvrsi(sql);
		sql = "DELETE FROM mprom1";
		izvrsi(sql);
		JOptionPane.showMessageDialog(this, "Podaci obrisani");
   
   }
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska: " + sql);
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