//Lager liste - robno
//=======================================
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
import javax.swing.SwingUtilities;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.*;
import javafx.print.PrinterJob;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class pProba extends JInternalFrame implements InternalFrameListener
			{
	private float dug,pot,saldo;
	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,nazivk;
	private JButton novi,unesi,minus,vrednost;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj,naziv,Ukupno,UkupnoNap;
   	public static JLabel  l[],lnaziv,mkdisplej,lblUkupno,lblUkupnoNap;
	private JList list;
   	private int n_fields,kkk,kontomal,datval,doktor=0,pacijent=0;
	private double koeficient=0;
	private String pattern = "##########.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);
	private String html="";

    private Stage stage;  
    private WebView browser;  
    private JFXPanel jfxPanel;  
    private JButton swingButton;  
    private WebEngine webEngine;  

//------------------------------------------------------------------------------------------------------------------
    public pProba(int _pacijent) {
		super("", true, true, true, true);
		setTitle("JavaFX HTML prikaz");
		setMaximizable(true);
		setResizable(true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);
		pacijent = _pacijent;

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( null);

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,720,500);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Prikaz ");
		unesi.setMnemonic('R');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});

		minus = new JButton("\u0160tampa");
		minus.setMnemonic('P');
        minus.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        minus.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Stampa();}});
		minus.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
        t = new JFormattedTextField[1]; 
        l = new JLabel[1]; 
		
        lblUkupno = new JLabel("Ukupno:");
        Ukupno = new JFormattedTextField();
		Ukupno.setColumns(8);
		Ukupno.setEditable(false);
		Ukupno.setHorizontalAlignment(JTextField.TRAILING);
		Ukupno.setFont(new Font("Arial",Font.BOLD,12));

        lblUkupnoNap = new JLabel("");
        UkupnoNap = new JFormattedTextField();
		UkupnoNap.setColumns(8);
		UkupnoNap.setEditable(false);
		UkupnoNap.setHorizontalAlignment(JTextField.TRAILING);
		UkupnoNap.setFont(new Font("Arial",Font.BOLD,12));


		buttonPanel.add( unesi );
		buttonPanel.add( minus );
		buttonPanel.add( izlaz );
		buttonPanel.add( lblUkupno );
		buttonPanel.add( Ukupno );
		nazivk = " ";
		pPre = new String("1");
		kontomal = 1340;

		uzmiKonekciju();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();


		//kreiranje panela u skrol panelu
		buildFilterPanel();

		//container.add(scrol);

		glavni.add(container);
		//glavni.add(buildTable());
		main.add(buildNazivPanel(), BorderLayout.NORTH);
		main.add(jfxPanel, BorderLayout.CENTER);
		//main.add(swingButton, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		
		setSize(850,700);

		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));
		proveriMasinu();

    }
//------------------------------------------------------------------------------------------------------------------
    public void internalFrameClosing(InternalFrameEvent e) {}
    public void internalFrameClosed(InternalFrameEvent e) {}
    public void internalFrameOpened(InternalFrameEvent e) {}
    public void internalFrameIconified(InternalFrameEvent e) {}
    public void internalFrameDeiconified(InternalFrameEvent e) {}
    public void internalFrameActivated(InternalFrameEvent e) {}
    public void internalFrameDeactivated(InternalFrameEvent e) {}
	protected void fireInternalFrameEvent(int id){
		if (id == InternalFrameEvent.INTERNAL_FRAME_CLOSING ) {
				if (connection != null){
					try {connection.close(); } 
					catch (Exception f) {
						JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
					}
				}
		} 
    }
	protected void Izlaz(){
		try{
				this.setClosed(true);}
		catch (Exception e){
				JOptionPane.showMessageDialog(this, e);}
    }
//------------------------------------------------------------------------------------------------------------------
    public void buildFilterPanel() {
 		JPanel p = new JPanel();
        p.setLayout(new BorderLayout());  

		jfxPanel = new JFXPanel();  
        createScene(); 
        
        swingButton = new JButton();
		
        swingButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        webEngine.reload();
                    }
                });
            }
        });
        swingButton.setText("Reload");  
    }
//------------------------------------------------------------------------------------------------------------------
    private void createScene() {  
        PlatformImpl.startup(new Runnable() {  
            @Override
            public void run() {  
                 
                stage = new Stage();  
                 
                stage.setTitle("Hello Java FX");  
                stage.setResizable(true);  
   
                Group root = new Group();  
                Scene scene = new Scene(root,80,20);  
                stage.setScene(scene);  
                 
                // Set up the embedded browser:
                browser = new WebView();
                webEngine = browser.getEngine();

				//ucitavanje stranice sa interneta
                //webEngine.load("http://www.google.rs");

				//ucitavanje stranice sa diska
				//String url = pProba.class.getResource("/web/aaa/index.html").toExternalForm();
				//webEngine.load(url);
				html = "<html><body>";
				html = html + "<p><b><u>MA\u0160INA: " + mkdisplej.getText().trim() + "</u></b></p>";
				
				ListajPreglede();
				
				html = html + "</body></html>";
				webEngine.loadContent(html);

                
                ObservableList<Node> children = root.getChildren();
                children.add(browser);                     
                 
                jfxPanel.setScene(scene);  
            }  
        });  
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNazivPanel() {
		JPanel p1 = new JPanel();
		p1.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		p1.setBorder( new TitledBorder("") );

		JLabel vrd = new JLabel("Ma\u0161ina: ");
		mkdisplej = new JLabel("                       "); 
		mkdisplej.setFont(new Font("Arial",Font.BOLD,14));
		//mkdisplej.setForeground(Color.red);
		minus = new JButton("\u0160tampa");
		minus.setMnemonic('P');
        minus.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        minus.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Stampa();}});
		minus.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});

		p1.add(vrd);
		p1.add(mkdisplej);
		p1.add(minus);
		return p1;
    }
//------------------------------------------------------------------------------------------------------------------
    public void ListajPreglede() {
		String simptomi="",dijagnoza="",lecenje="",napomena="",datum="",imedoktora;
		Statement statement = null;
		double uku=0;
	  try {
         statement = connection.createStatement();
                String query = "SELECT * FROM kvarovi WHERE sifra=" + pacijent +
					" ORDER BY datum";
		        ResultSet rs = statement.executeQuery( query );
		        while(rs.next()){
					simptomi = rs.getString("opiskvara");
					simptomi = simptomi.trim();
					dijagnoza = rs.getString("opisradova");
					dijagnoza = dijagnoza.trim();
					lecenje = rs.getString("delovi");
					lecenje = lecenje.trim();
					napomena = rs.getString("napomena");
					napomena = napomena.trim();
					datum = konvertujDatumIzPodataka(rs.getString("datum"));
					imedoktora = rs.getString("imeprezime");
					
					html = html + "<p>Datum pregleda: " + datum + "</p>";
					html = html + "<p><b>Opis kvara: </b></p>";
					if (simptomi.length()>0)
					{
						html = html + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + simptomi + "</p>";
					}
					html = html + "<br><p><b>Opis radova: </b></p>";
					if (dijagnoza.length()>0)
					{
						html = html + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + dijagnoza + "</p>";
					}
					html = html + "<br><p><b>Ugradjeni delovi: </b></p>";
					if (lecenje.length()>0)
					{
						html = html + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + lecenje + "</p>";
					}
					html = html + "<br><p><b>Napomena: </b></p>";
					if (napomena.length()>0)
					{
						html = html + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + napomena + "</p>";
					}
					html = html + "<br><p style='color:green;'><b>Radnik: " + imedoktora + "</b></p>";
					html = html + "<br><hr>";
				}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u ListajPreglede:" + sqlex);
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
//--------------------------------------------------------------------------------------
    public boolean proveriMasinu(){ 
		String queryy,ime="",roditelj="";
		boolean provera = false;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
				queryy = "SELECT * FROM masine WHERE sifra=" + pacijent;
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						mkdisplej.setText(rs.getString("naziv"));
						provera = true;
					}else{
						JOptionPane.showMessageDialog(this, "Ne postoji masina u podacima");
					}
		}catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u trazenju masine:"+sqlex);
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
		return provera;
}
//--------------------------------------------------------------------------
   public String konvertujDatumIzPodataka(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
	protected MaskFormatter createFormatter(String s, int koji) {
		MaskFormatter formatter = null;

		try {
			formatter = new MaskFormatter(s);
				switch (koji)	{
				case 1:
					//formater za cele brojeve
					formatter.setValidCharacters("0123456789 ");
					break;
				case 2:
					//formater za iznose sa decimalama
					formatter.setValidCharacters("0123456789. ");
					break;
				case 3:
					//za tekstualna polja bez ogranicenja
					break;
				case 4:
					//formater za datume
					formatter.setValidCharacters("0123456789/ ");
					break;
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
	}
//------------------------------------------------------------------------------------------------------------------
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(x,0);
    }
//--------------------------------------------------------------------------------------
    public boolean proveriDoktora(){ 
		String queryy;
		boolean provera = false;
		return provera;
}
//------------------------------------------------------------------------------------------------------------------
 public void UnesiPressed() {
		int kupac;
		String oddat,dodat,strsql,sql;
 }
//------------------------------------------------------------------------------------------------------------------
  public void Stampa() {
		PrinterJob job = PrinterJob.createPrinterJob();
		if (job != null) {
			webEngine.print(job);
			job.endJob();
		} 
  }
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska: " + sqlex);
			JOptionPane.showMessageDialog(null, "Upit: " + sql);
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
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
        this.setVisible(false);
    	}
//------------------------------------------------------------------------------------------------------------------
    public void quitForm() {
        	this.setVisible(false);
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
 //--------------------------------------------------------------------------
   private String konvertujDatum(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(6,10);
		datum = datum + "-" + pom.substring(3,5);
		datum = datum + "-" + pom.substring(0,2);
	return datum;
   }
//--------------------------------------------------------------------------
   public boolean proveriDatum(String _datum){
		boolean ispravan = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date testDate = null;
		try
		{
			testDate = sdf.parse(_datum);
		}
		//provera da li je odgovarajuci format datuma
		catch (ParseException e)
		{
			JOptionPane.showMessageDialog(this, "Neispravan format datuma");
			return false;
		}
		//provera da li je datum ispravan
		if (!sdf.format(testDate).equals(_datum))
		{
			JOptionPane.showMessageDialog(this, "Neispravan datum");
			return false;
		}
		return true;
   }
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					if (t[0].getText().trim().length() == 0){
						t[0].requestFocus();
					}
					if (proveriDoktora())
					{
						//t[1].setText("01/01/" + date.substring(6,10));
						t[1].setSelectionStart(0);
						t[1].requestFocus();
					}else{
						t[0].setSelectionStart(0);
						t[0].requestFocus();
					}
				}
				else if (source == t[1]){
					if (t[1].getText().trim().length() == 0)
					{
						t[1].requestFocus();
					}else{
						if (proveriDatum(t[1].getText().trim()))
						{
							t[2].setSelectionStart(0);
							t[2].requestFocus();
						}
					}
				}
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0)
					{
						t[2].requestFocus();
					}else{
						if (proveriDatum(t[2].getText().trim()))
						{
							unesi.requestFocus();
						}
					}
					
					
				}
}
//===========================================================================
class ML extends MouseAdapter{
	public void mousePressed(MouseEvent e) {
		Object source = e.getSource();
	}
}//end of class ML
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
	}
//------------------------------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
	}
}

}// end of class Konto ====================================================================
class CLProba implements LayoutManager {

  int xInset = 5;
  int yInset = 10;
  int yGap = 8;

//------------------------------------------------------------------------------------------------------------------
  public void addLayoutComponent(String s, Component c) {}

//------------------------------------------------------------------------------------------------------------------
  public void layoutContainer(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      Dimension compSize1 = null;
      for (int i = 0; i < children.length; i = i + 2) {
	  compSize = children[i].getPreferredSize();
	  compSize1 = children[i + 1].getPreferredSize();
	//pozicija za labele
	  children[i].setSize(compSize.width, compSize.height);
	  children[i].setLocation( xInset + insets.left, height + 5);
	//pozicija za txt
	  children[i + 1].setSize(compSize1.width, compSize1.height);
	  children[i + 1].setLocation( xInset + insets.left + 120, height);
	  height += compSize.height + yGap;
      }
  }

//------------------------------------------------------------------------------------------------------------------
  public Dimension minimumLayoutSize(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      int width = 0 + insets.left + insets.right;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      for (int i = 0; i < children.length; i++) {
	  compSize = children[i].getPreferredSize();
	  height += compSize.height + yGap;
	  width = Math.max(width, compSize.width + insets.left + insets.right + xInset*2 + 10);
      }
      height += insets.bottom - 740;


      return new Dimension( width, height);
  }
  
//------------------------------------------------------------------------------------------------------------------
  public Dimension preferredLayoutSize(Container c) {
      return minimumLayoutSize(c);
  	}
//------------------------------------------------------------------------------------------------------------------
  public void removeLayoutComponent(Component c) {} 

}// end of class CLProba
