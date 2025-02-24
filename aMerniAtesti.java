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


public class aMerniAtesti extends JInternalFrame implements InternalFrameListener
			{
	private mQTM4 qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private boolean izmena = false;

	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],mmoj;
   	private JLabel  l[];
   	int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public aMerniAtesti() {
		super("", true, true, true, true);
        setTitle("�ifarnik ma\u0161ina");
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

		JButton brisi = new JButton("Bri�i slog");
		brisi.setMnemonic('B');
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		buttonPanel.add( brisi );

		JButton trazi = new JButton("Tra�i");
		trazi.setMnemonic('T');
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		buttonPanel.add( trazi );

		JButton stampa = new JButton("�tampa spisak");
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
		
		pPre = "1";
		uzmiKonekciju();

		container.add(buildFilterPanel());
		glavni.add(container);
		glavni.add(buildTable());
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		setSize(820,350);
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
			try {connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
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
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( new MCLAtesti() );
		p.setBorder( new TitledBorder("Unos") );

		int i;
        n_fields = 9; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 

		
		String fmm;
		fmm = "*****";
        l[0] = new JLabel("\u0160ifra :");
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setColumns(5);
		t[0].setSelectionStart(0);
		t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});


		fmm = "**************************************************";
		l[1] = new JLabel("Naziv :");
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
		t[1].setColumns(22);
		t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		l[2] = new JLabel("Tip :");
        t[2] = new JFormattedTextField(createFormatter(fmm,3));
		t[2].setColumns(22);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		l[3] = new JLabel("Fab.broj :");
        t[3] = new JFormattedTextField(createFormatter(fmm,3));
		t[3].setColumns(22);
		t[3].addFocusListener(new FL());
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		l[4] = new JLabel("Inventarski broj :");
        t[4] = new JFormattedTextField(createFormatter(fmm,3));
		t[4].setColumns(22);
		t[4].addFocusListener(new FL());
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		l[5] = new JLabel("Proizvo\u0111a\u010d :");
        t[5] = new JFormattedTextField(createFormatter(fmm,3));
		t[5].setColumns(22);
		t[5].addFocusListener(new FL());
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		l[6] = new JLabel("Godina proizvodnje :");
        t[6] = new JFormattedTextField(createFormatter(fmm,3));
		t[6].setColumns(22);
		t[6].addFocusListener(new FL());
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		fmm = "**********";
		l[7] = new JLabel("Karton :");
        t[7] = new JFormattedTextField(createFormatter(fmm,3));
		t[7].setColumns(22);
		t[7].addFocusListener(new FL());
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

		l[8] = new JLabel("Napomena :");
        t[8] = new JFormattedTextField(createFormatter(fmm,3));
		t[8].setColumns(22);
		t[8].addFocusListener(new FL());
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});



	    for(i=0;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}

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
        n_fields = 9; 
        for(i=0;i<n_fields;i++){
            t[i].setText("");
            t[i].setValue(null);
		}
		izmena = false;
		t[0].setSelectionStart(0);
		t[0].requestFocus();
    }
//------------------------------------------------------------------------------------------------------------------
    public void UnesiPressed() {
		AddRecord();
    }
//------------------------------------------------------------------------------------------------------------------
    public void Stampa() {

		jPrintSpisakMasina jpm = new jPrintSpisakMasina(connection);
	
	}
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
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
//------------------------------------------------------------------------------------------------------------------
	public void AddRecord() {
	  Statement statement = null;
      try {
         statement = connection.createStatement();
		 if ( t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 ) {
            String query = "INSERT INTO masine (sifra,naziv,tip,fabbroj,invbroj,proizvodjac,godizrade,karton,napomena)" +
				" VALUES(" +
				Integer.parseInt(t[0].getText().trim()) + ",'" +
				t[1].getText() + "','" + 
				t[2].getText().trim() + "','" +  
				t[3].getText().trim() + "','" +  
				t[4].getText().trim() + "','" +  
				t[5].getText().trim() + "','" +  
				t[6].getText().trim() + "','" +  
				t[7].getText().trim() + "','" +  
				t[8].getText().trim() + "')";			
			
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je unet");

				String upit = "SELECT * FROM masine order by sifra";
				popuniTabelu(upit);
				NoviPressed();
			}else {
				JOptionPane.showMessageDialog(this, "Slog nije unet");
				NoviPressed();
            }
        }else {JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");
			t[0].requestFocus();
		}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u unosu:" + sqlex);
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
               String query = "UPDATE masine SET " +
				"naziv='" + t[1].getText()+ "'," +
			    "tip='" + t[2].getText().trim() + "'," + 
			    "fabbroj='" + t[3].getText().trim() + "'," + 
			    "invbroj='" + t[4].getText().trim() + "'," + 
			    "proizvodjac='" + t[5].getText().trim() + "'," + 
			    "godizrade='" + t[6].getText().trim() + "'," + 
			    "karton='" + t[7].getText().trim() + "'," + 
			    "napomena='" + t[8].getText() + "'" +
				" WHERE sifra=" + Integer.parseInt(t[0].getText().trim());

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
					String upit = "SELECT * FROM masine order by sifra";
					popuniTabelu(upit);
					NoviPressed();
				}else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					NoviPressed();
				}
        }else {
			 JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje Broj Saradnika");
		}
      }catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u izmeni:"+sqlex);
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
               	String query = "DELETE FROM masine WHERE sifra=" + 
						Integer.parseInt(t[0].getText().trim());

               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
					JOptionPane.showMessageDialog(this, "Slog je izbrisan");
					String upit = "SELECT * FROM masine Where pre=" + Integer.parseInt(pPre) + " order by sifra";
					popuniTabelu(upit);
					NoviPressed();
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
			String query = "SELECT * FROM masine WHERE sifra=" + koji;

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		         	t[0].setText(rs.getString("sifra"));
		         	t[1].setText(rs.getString("naziv"));
					t[2].setText(rs.getString("tip"));
					t[3].setText(rs.getString("fabbroj"));
					t[4].setText(rs.getString("invbroj"));
					t[5].setText(rs.getString("proizvodjac"));
					t[6].setText(rs.getString("godizrade"));
					t[7].setText(rs.getString("karton"));
					t[8].setText(rs.getString("napomena"));
					izmena = true;
					rs.close();
				}
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		}else {
			 JOptionPane.showMessageDialog(this, "\u0160ifra nije uneta");
		}
      }catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u FindRecord:" + sqlex);
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
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva masine");
		String upit = "SELECT * FROM masine WHERE naziv LIKE '" + String.valueOf(result) + "%'";
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
		sql = "SELECT * FROM masine order by sifra" ;
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
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
		koji = String.valueOf(podaci.get(kojirec));
		FindRecord();
	}
//------------------------------------------------------------------------------------------------------------------
	public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;

				if (source == t[0]){
					t[1].setSelectionStart(0);
					t[1].requestFocus();
				}
				else if (source == t[1]){
					t[2].setSelectionStart(0);
					t[2].requestFocus();}
				else if (source == t[2]){
					t[3].setSelectionStart(0);
					t[3].requestFocus();
				}
				else if (source == t[3]){
					t[4].setSelectionStart(0);
					t[4].requestFocus();
				}
				else if (source == t[4]){
					t[5].setSelectionStart(0);
					t[5].requestFocus();
				}
				else if (source == t[5]){
					t[6].setSelectionStart(0);
					t[6].requestFocus();
				}
				else if (source == t[6]){
					t[7].setSelectionStart(0);
					t[7].requestFocus();
				}
				else if (source == t[7]){
					t[8].setSelectionStart(0);
					t[8].requestFocus();
				}
				else if (source == t[8]){
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
	String[] colheads = {"\u0160ifra","Naziv masine","Tip","Fab.br","Inv.br.","Proizvodjac","God.pr.","Napomena"};

//------------------------------------------------------------------------------------------------------------------
   public mQTM4(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 8; }
   public int getRowCount() { return totalrows.size(); }
   public Object getValueAt(int row, int col) {
      return ((String[])totalrows.elementAt(row))[col];
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
			totalrows = new Vector();
            while ( rs.next() ) {
               String[] record = new String[8];
               record[0] = rs.getString("sifra");
               record[1] = rs.getString("naziv");
               record[2] = rs.getString("tip");
               record[3] = rs.getString("fabbroj");
               record[4] = rs.getString("invbroj");
               record[5] = rs.getString("proizvodjac");
               record[6] = rs.getString("godizrade");
               record[7] = rs.getString("napomena");
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
class MCLAtesti implements LayoutManager {

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

}// end of class MCLAtesti

