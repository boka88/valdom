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


public class mMagacini extends JInternalFrame implements InternalFrameListener
			{
	private mQTM4 qtbl;
   	private JTable jtbl;
	private Vector<String[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaci = new Vector<String>();
	private Vector spodaci = new Vector();
	private boolean izmena = false;

	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public FormField t[],mmoj;
   	private JLabel  l[];
   	int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public mMagacini() {
		super("", true, true, true, true);
        setTitle("Šifarnik Magacina");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( new GridLayout(1,2) );

		JPanel container = new JPanel();
		container.setLayout( new BorderLayout() );

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});
		buttonPanel.add( unesi );

		JButton novi = new JButton("Novi slog");
		novi.setMnemonic('N');
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});
		buttonPanel.add( novi );

		izmeni = new JButton("Izmeni");
		izmeni.setMnemonic('I');
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});
		buttonPanel.add( izmeni );

		JButton brisi = new JButton("Briši slog");
		brisi.setMnemonic('B');
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		buttonPanel.add( brisi );

		JButton trazi = new JButton("Traži");
		trazi.setMnemonic('T');
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		buttonPanel.add( trazi );

		JButton stampa = new JButton("Štampa");
		stampa.setMnemonic('P');
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		buttonPanel.add( stampa );

		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
		buttonPanel.add( izlaz );
		
		pPre = new String(IzborPre.kojePre);
		uzmiKonekciju();

		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		setSize(700,350);
		centerDialog();
		UIManager.addPropertyChangeListener(new UISwitchListener(container));

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
		} 
    }
	protected void Izlaz(){
		try{
				this.setClosed(true);}
		catch (Exception e){
				JOptionPane.showMessageDialog(this, e);}
    }
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( new MCL4() );
		p.setBorder( new TitledBorder("Unos") );

		int i;
        n_fields = 4; 
        t = new FormField[n_fields]; 
        l = new JLabel[n_fields]; 

		
		String fmm;
		fmm = "**";
        l[0] = new JLabel("Magacin :");
        t[0] = new FormField(createFormatter(fmm,1));
		t[0].setColumns(4);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
		t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});


		fmm = "****************************************";
		l[1] = new JLabel("Naziv magacina :");
        t[1] = new FormField(createFormatter(fmm,3));
		t[1].setColumns(18);
		t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "********************";
		l[2] = new JLabel("Adresa :");
        t[2] = new FormField(createFormatter(fmm,3));
		t[2].setColumns(18);
		t[2].addFocusListener(new FL());
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "********************";
		l[3] = new JLabel("Mesto :");
        t[3] = new FormField(createFormatter(fmm,3));
		t[3].setColumns(18);
		t[3].addFocusListener(new FL());
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});



	    for(i=0;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }

		return p;
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
			System.exit(-1);
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
		this.setLocation(0,0);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 4; 
        for(i=0;i<n_fields;i++)
            t[i].setText("");		
		izmena = false;
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
		t[0].requestFocus();
    }
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {
		String sql = "SELECT * FROM magacini WHERE pre="+pPre.trim();
		String kkk = " ";
		jPrintSifarnik kk1 = new jPrintSifarnik(connection,7,sql,kkk);
		//mPrintMag jjj = new mPrintMag(connection);
	}
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
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
	public void AddRecord() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
		 if ( t[0].getText().trim().length() != 0 && 
            t[1].getText().trim().length() != 0 ) {
            String query = "INSERT INTO magacini (mag,nazivm,adesa,mesto,pre) VALUES(" +
			Integer.parseInt(t[0].getText().trim()) + ",'" +
				t[1].getText() + "','" + t[2].getText() + "','" +  t[3].getText() + "'," +
				Integer.parseInt(pPre) + ")";			
			
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
			//JOptionPane.showMessageDialog(this, "Slog je unet");

				String upit = "SELECT * FROM magacini WHERE pre=" + Integer.parseInt(pPre) + " order by mag";
				popuniTabelu(upit);
				NoviPressed();
			}     
			else {
				JOptionPane.showMessageDialog(this, "Slog nije unet");
				NoviPressed();
            }
         }
         else {JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");
			t[0].requestFocus();}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u unosu");
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
//------------------------------------------------------------------------------------------------------------------
	public void UpdateRecord() {
  
	  Statement statement = null;
      try {
         statement = connection.createStatement();

		 if ( !t[0].getText().equals( "" )) {
               String query = "UPDATE magacini SET " +
				"nazivm='" + t[1].getText()+ "', adesa='" + t[2].getText() + "'" + ", mesto='" + t[3].getText() + "'" +
				" WHERE mag=" + Integer.parseInt(t[0].getText().trim()) +
				" And pre=" + Integer.parseInt(pPre);


			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je izmenjen");
				String upit = "SELECT * FROM magacini WHERE pre=" + Integer.parseInt(pPre) + " order by mag";
				popuniTabelu(upit);
				NoviPressed();
		}     
            else {
            JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
				NoviPressed();
            }
         }
         else {JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje Broj Saradnika");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u izmeni");
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
//------------------------------------------------------------------------------------------------------------------
	public void DeleteRecord() {
 
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         if ( !t[0].getText().equals( "" ) ) {
               	String query = "DELETE FROM magacini WHERE mag=" + 
						Integer.parseInt(t[0].getText().trim()) + " And pre=" + Integer.parseInt(pPre);

				//provera da li je aktivan========
				mBrisi mb = new mBrisi();
				if (mb.nadjiPromet(6,Integer.parseInt(t[0].getText().trim()),pPre,"0"))
				{
					JOptionPane.showMessageDialog(this, "Magacin ima promet");
				}else{


               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
				//JOptionPane.showMessageDialog(this, "Slog je izbrisan");
				String upit = "SELECT * FROM magacini WHERE pre=" + Integer.parseInt(pPre) + " order by mag";
				popuniTabelu(upit);
				NoviPressed();
			}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne moze izbrisati");
               		NoviPressed();
            	}
				}//end if(mBrisi...........................
         }
         else{JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje konto");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u brisanju");
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
//------------------------------------------------------------------------------------------------------------------
	public void FindRecord() {
 
	  Statement statement = null;
      try {
         statement = connection.createStatement();

		 if ( !t[0].getText().equals( "" )) {
			String query = "SELECT * FROM magacini WHERE mag=" + koji +
				" And pre=" + Integer.parseInt(pPre);

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		         	t[0].setText(rs.getString("mag"));
		         	t[1].setText(rs.getString("nazivm"));
					t[2].setText(rs.getString("adesa"));
					t[3].setText(rs.getString("mesto"));
					izmena = true;
					rs.close();
				}
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
		 }     
         else {JOptionPane.showMessageDialog(this, "Magacin nije unet");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u trazenju");
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
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva magacina");
		String upit = "SELECT * FROM magacini WHERE nazivm LIKE '" + String.valueOf(result) + "%'" + 
						" And pre=" + Integer.parseInt(pPre);
		popuniTabelu(upit);
		jtbl.requestFocus();
  }
//------------------------------------------------------------------------------------------------------------------
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekoracena duzina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		JOptionPane.showMessageDialog(this, String.valueOf(koji));
   }
//------------------------------------------------------------------------------------------------------------------
    public void popuniTabelu(String _sql) {
		String sqll= new String(_sql);
	   	qtbl.query(sqll);
		qtbl.fire();
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new mQTM4(connection);
		String sql;
		sql = "SELECT * FROM magacini WHERE pre=" + Integer.parseInt(pPre) + " order by mag" ;
	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); 
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); 
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
 	   	
		//jtbl = new JTable( qtbl );
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		//koji = String.valueOf(podaci.get(kojirec));
		koji = jtbl.getValueAt(kojirec,0).toString();
		FindRecord();
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(FormField e) {
		FormField source;
		source = e;

				if (source == t[0]){
					t[1].setSelectionStart(0);
					t[1].requestFocus();}
				else if (source == t[1]){
					t[2].setSelectionStart(0);
					t[2].requestFocus();}
				else if (source == t[2]){
					t[3].setSelectionStart(0);
					t[3].requestFocus();}
				else if (source == t[3]){
					if (izmena)
					{
						izmeni.requestFocus();}
					else{
						unesi.requestFocus();}
				}
}

//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				if (t[0].getText().trim().length() > 0)
				{
					koji = String.valueOf(t[0].getText());
					FindRecord();
				}
			}
	}
//------------------------------------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
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
 class mQTM4 extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"Magacin","Naziv magacina","Adresa","Mesto"};

//------------------------------------------------------------------------------------------------------------------
   public mQTM4(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector<String[]>();
   }
//------------------------------------------------------------------------------------------------------------------
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
			totalrows = new Vector<String[]>();
            while ( rs.next() ) {

               String[] record = new String[4];
               record[0] = rs.getString("mag");
               record[1] = rs.getString("nazivm");
               record[2] = rs.getString("adesa");
               record[3] = rs.getString("mesto");
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
 }//end of class mQTM4

}// end of class Konto ====================================================================
class MCL4 implements LayoutManager {

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

}// end of class MCL4

