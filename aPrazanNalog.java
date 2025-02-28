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
import javax.swing.JInternalFrame;


public class aPrazanNalog extends JDialog{
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


   public aPrazanNalog() {
        setTitle("Prazan list kvarova");
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
		
		ok = new JButton("Print");
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
		//brr.add(txtPoruka);
		
		//brr.add(buildTable());
		
		//brr.add(jspane);

		cont.add(brr,BorderLayout.CENTER);
		cont.add(buton,BorderLayout.SOUTH);
		pack();
		setSize(470,250);
		
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
   public void Start() {

		jPrintPrazanNalog npr = new jPrintPrazanNalog(connection);
  
  }
//======================================================================================

//-----------------------------------------------------------------------------------
} //end of class IborPre
