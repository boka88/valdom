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

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Node;

public class uGraph extends JInternalFrame implements InternalFrameListener
			{
	private QTMPreglNalRad qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane,scrol;
	private Vector podaciList = new Vector();
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private float dug,pot,saldo;
	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre,date,nazivk,godina="";
	private JButton novi,unesi,minus,vrednost;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public static JFormattedTextField t[],mmoj,naziv,Ukupno,UkupnoNap;
   	public static JLabel  l[],lnaziv,mkdisplej,lblUkupno,lblUkupnoNap;
	private JList list;
   	private int n_fields,kkk,kontomal,datval,doktor=0;
	private int intuplate[],intisplate[];
	private double koeficient=0;
	private String pattern = "##########.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);

    private Stage stage;  
    private WebView browser;  
    private JFXPanel jfxPanel;  
    private JButton swingButton;  
    private WebEngine webEngine;  
	private NumberAxis xAxis;
    private NumberAxis yAxis;

//------------------------------------------------------------------------------------------------------------------
    public uGraph() {
		super("", true, true, true, true);
		setTitle("FX grafikon");
		setMaximizable(true);
		setResizable(true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

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

		//uzmiKonekciju();

		JFormattedTextField tft = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		tft.setValue(new java.util.Date());
		date=tft.getText();
		godina = date.substring(6,10);

		//kreiranje panela u skrol panelu
		buildFilterPanel();

		//container.add(scrol);

		glavni.add(container);
		//glavni.add(buildTable());
		//main.add(buildNazivPanel(), BorderLayout.NORTH);
		main.add(jfxPanel, BorderLayout.CENTER);
		//main.add(swingButton, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		
		setSize(850,700);

		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(main));

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
			String strsql = "drop table if exists tmpuplate";
			izvrsi(strsql);
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
        //p.setLayout(null);  

		jfxPanel = new JFXPanel();
		
		nadjiIznose();
        createScene(); 
    }
//------------------------------------------------------------------------------------------------------------------
    private void createScene() {  
        PlatformImpl.startup(new Runnable() {  
            @Override
            public void run() {  
                 
           stage = new Stage();  
                 
           stage.setTitle("");  
           stage.setResizable(true);  
			
			final CategoryAxis xAxis = new CategoryAxis();
			//final NumberAxis yAxis = new NumberAxis();
			
			yAxis = new NumberAxis("Iznosi u 1000", 0.0d, 150.0d, 5.0d);
			xAxis.setLabel("Meseci");

		final LineChart<String, Number> lineChart = new LineChart<String, Number>(
        xAxis, yAxis);

		lineChart.setTitle("");
		
		/*
		lineChart.setStyle(
			".chart-vertical-grid-lines {-fx-stroke: #3278fa;}" +
			".chart-horizontal-grid-lines {-fx-stroke: #3278fa;}"+
			 ".chart-alternative-row-fill {-fx-fill: transparent;-fx-stroke: transparent; -fx-stroke-width: 0;}");
		*/

		XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
		series1.setName("Uplate");

		series1.getData().add(new XYChart.Data<String, Number>("Jan", intuplate[1]));
		series1.getData().add(new XYChart.Data<String, Number>("Feb", intuplate[2]));
		series1.getData().add(new XYChart.Data<String, Number>("Mar", intuplate[3]));
		series1.getData().add(new XYChart.Data<String, Number>("Apr", intuplate[4]));
		series1.getData().add(new XYChart.Data<String, Number>("Maj", intuplate[5]));
		series1.getData().add(new XYChart.Data<String, Number>("Jun", intuplate[6]));
		series1.getData().add(new XYChart.Data<String, Number>("Jul", intuplate[7]));
		series1.getData().add(new XYChart.Data<String, Number>("Aug", intuplate[8]));
		series1.getData().add(new XYChart.Data<String, Number>("Sep", intuplate[9]));
		series1.getData().add(new XYChart.Data<String, Number>("Okt", intuplate[10]));
		series1.getData().add(new XYChart.Data<String, Number>("Nov", intuplate[11]));
		series1.getData().add(new XYChart.Data<String, Number>("Dec", intuplate[12]));

		XYChart.Series<String, Number> series2 = new XYChart.Series<String, Number>();
		series2.setName("Isplate");

		series2.getData().add(new XYChart.Data<String, Number>("Jan", intisplate[1]));
		series2.getData().add(new XYChart.Data<String, Number>("Feb", intisplate[2]));
		series2.getData().add(new XYChart.Data<String, Number>("Mar", intisplate[3]));
		series2.getData().add(new XYChart.Data<String, Number>("Apr", intisplate[4]));
		series2.getData().add(new XYChart.Data<String, Number>("Maj", intisplate[5]));
		series2.getData().add(new XYChart.Data<String, Number>("Jun", intisplate[6]));
		series2.getData().add(new XYChart.Data<String, Number>("Jul", intisplate[7]));
		series2.getData().add(new XYChart.Data<String, Number>("Aug", intisplate[8]));
		series2.getData().add(new XYChart.Data<String, Number>("Sep", intisplate[9]));
		series2.getData().add(new XYChart.Data<String, Number>("Okt", intisplate[10]));
		series2.getData().add(new XYChart.Data<String, Number>("Nov", intisplate[11]));
		series2.getData().add(new XYChart.Data<String, Number>("Dec", intisplate[12]));

		Scene scene = new Scene(lineChart, 800, 720);
		
		//dodavanje linija u chart
		lineChart.getData().addAll(series1, series2);
		String chartCss = uGraph.class.getResource("/css/chart.css").toExternalForm();
        lineChart.getStylesheets().add(chartCss);				
               
				
		//Scene scene = new Scene(root,400,400);  
        stage.setScene(scene);  
		/*
		Node node = lineChart.lookup(".chart-vertical-grid-lines");
         // Set the first series fill to translucent pale green
         node.setStyle("{-fx-stroke: #3278fa;");
         // Set the first series fill to translucent pale green
		*/
                 
                jfxPanel.setScene(scene);  
            }  
        });  
    }
//------------------------------------------------------------------------------------------------------------------
public void nadjiIznose() {
	int i=0;
	intuplate = new int[13];
	intisplate = new int[13];

	//nuliranje vrednosti nizova
	for (i=1;i<13 ;i++ )
	{
		intuplate[i] = 10*i;
		intisplate[i] = 5*i;
	}


	//uzimanje vrednosti nizova
	/*
	for (i=1;i<13 ;i++ )
	{
		intuplate[i] = uzmiUplate(i);
		intisplate[i] = uzmiIsplate(i);
	}
	*/
}
//------------------------------------------------------------------------------------------------------------------
public void printFormData(Object form) {
}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildNazivPanel() {
		JPanel p1 = new JPanel();
		p1.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		p1.setBorder( new TitledBorder("") );

		JLabel vrd = new JLabel("Grafikon: ");
		mkdisplej = new JLabel("                       "); 
		mkdisplej.setFont(new Font("Arial",Font.BOLD,14));
		//mkdisplej.setForeground(Color.red);

		p1.add(vrd);
		p1.add(mkdisplej);
		return p1;
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
		this.setLocation(50,0);
    }
//------------------------------------------------------------------------------------------------------------------
	public void idiNaDugme(){

		unesi.requestFocus();
}
//-------------------------------------------------------------------------
   public void prikaziSarad(){
		int t = 12;
   }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
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
		int kupac;
		String oddat,dodat,naziv,nazdoktora;
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
		try{
			connection = aLogin.konekcija;
		}catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee preuzeti konekciju:"+f);
		}
    }
//------------------------------------------------------------------------------------------------------------------
    public int uzmiUplate(int _mesec) {
		int uplata=0;
		Statement statement = null;
		double uku=0;
	  try {
         statement = connection.createStatement();
                String query = "SELECT ROUND(SUM(uplata),0) AS uku" +
					" FROM uplate WHERE ABS(substring(datum,6,2))=" + _mesec +
					" AND ABS(substring(datum,1,4))=" + godina;
		        ResultSet rs = statement.executeQuery( query );
		      
				if(rs.next()){
					uplata = rs.getInt("uku");
					if (uplata>0)
					{
						uplata=uplata/50000;
					}

				}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u Ukupno" + sqlex);
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
		return uplata;
 }
//------------------------------------------------------------------------------------------------------------------
    public int uzmiIsplate(int _mesec) {
		int isplata=0;
		Statement statement = null;
		double uku=0;
	  try {
         statement = connection.createStatement();
                String query = "SELECT ROUND(SUM(isplata),0) AS uku" +
					" FROM isplate WHERE ABS(substring(datum,6,2))=" + _mesec +
					" AND ABS(substring(datum,1,4))=" + godina;
		        ResultSet rs = statement.executeQuery( query );
				
				if(rs.next()){
					isplata = rs.getInt("uku");
					if (isplata>0)
					{
						isplata=isplata/50000;
					}
				}
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska u Ukupno" + sqlex);
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
		return isplata;
 }
//------------------------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(200);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(70);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(70);

	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );
		ptbl.setBounds(260,5,450,320);
	   	qtbl = new QTMPreglNalRad(connection);
		String sql;
		sql = "SELECT ime,ROUND(uplata,2),ROUND(isplata,2) FROM tmpuplate" +
			" ORDER BY datum";

	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 		
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(200);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(70);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(70);

		jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;

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
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		t[5].setText(String.valueOf(podaci.get(kojirec)));
		t[5].requestFocus();
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
		if (source == jtbl){
			prikaziIzTabele();
		}
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
//===========================================================================
 class QTMPreglNalRad extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Doktor","Uplata","Isplata"};

//------------------------------------------------------------------------------------------------------------------
   public QTMPreglNalRad(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 3; }
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
   public String konvertujDatumIzPodatakaQTB(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(8,10);
		datum = datum + "/" + pom.substring(5,7);
		datum = datum + "/" + pom.substring(0,4);
	return datum;
   }
//------------------------------------------------------------------------------------------------------------------
   public void query(String _sql) {
		String sql;
		sql = _sql;
		podaci.clear();
		spodaci.clear();
		Statement statement = null;
		try {
			statement = dbconn.createStatement();
               
            ResultSet rs = statement.executeQuery( sql );
			totalrows = new Vector();

            while ( rs.next() ) {
               Object[] record = new Object[3];
               record[0] = rs.getString("ime");
               record[1] = rs.getBigDecimal("ROUND(uplata,2)");
               record[2] = rs.getBigDecimal("ROUND(isplata,2)");

				podaci.addElement(record[0]);
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
 }//end of class QTMPreglNalRad

}// end of class Konto ====================================================================
class CLGraph implements LayoutManager {

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

}// end of class CLGraph
