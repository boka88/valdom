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



public class fMerila extends JInternalFrame implements InternalFrameListener
			{
	private QTM2 qtbl;
   	private JTable jtbl;
	private Vector<String[]> totalrows;
	private JScrollPane jspane;
	private Vector<String> podaci = new Vector<String>();
	private Vector spodaci = new Vector();
	private boolean izmena = false;
	private boolean bris=false,bris1=false;

	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni,brisi;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public FormField t[],mmoj;
   	private JLabel  l[];
   	int n_fields,koperant,kupac,dobavljac,duzkup,duzkup1,pozkup,uvoz,izvoz,avans1,avans2;;
//------------------------------------------------------------------------------------------------------------------
    public fMerila() {
		super("", true, true, true, true);
        setTitle("\u0160ifarnik merno kontrolnih sredstava.");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( null );

		JPanel container = new JPanel();
		container.setLayout( null );
		container.setBounds(5,5,360,450);

		JPanel dugmad = new JPanel();
		dugmad.setLayout ( new FlowLayout(FlowLayout.LEFT) );
		dugmad.setBounds(5,410,360,40);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
		unesi.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.butonfont));
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});
		dugmad.add( unesi );

		novi = new JButton("Novi slog");
		novi.setMnemonic('N');
		novi.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.butonfont));
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   NoviPressed(); }});
		dugmad.add( novi );

		izmeni = new JButton("Izmeni");
		izmeni.setMnemonic('I');
		izmeni.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.butonfont));
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UpdateRecord();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UpdateRecord(); }});
		dugmad.add( izmeni );

		brisi = new JButton("Bri\u0161i slog");
		brisi.setMnemonic('B');
		brisi.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.butonfont));
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		dugmad.add( brisi );

		JButton trazi = new JButton("Tra\u017ei po nazivu");
		trazi.setMnemonic('T');
		trazi.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.butonfont));
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		buttonPanel.add( trazi );

		/*
		JButton trazipomestu = new JButton("Tra\u017ei po nazivu");
		trazipomestu.setMnemonic('M');
		trazipomestu.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecordPoMestu(); }});
		buttonPanel.add( trazipomestu );
		*/

		JButton stampasve = new JButton("\u0160tampa spisak");
		stampasve.setMnemonic('P');
		stampasve.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.butonfont));
		stampasve.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		buttonPanel.add( stampasve );


		JButton izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.butonfont));
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
		buttonPanel.add( izlaz );

		pPre = new String("1");
		uzmiKonekciju();

		container.add(buildFilterPanel());
		container.add(dugmad);
		glavni.add(container);
		glavni.add(buildTable());
		main.add(glavni, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
		setSize(790,550);
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
				if (connection != null){
					try {	connection.close(); } 
					catch (Exception f) {
					JOptionPane.showMessageDialog(this, "Ne moze se zatvoriti konekcija");
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
    public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( new CL22() );
		p.setBorder( new TitledBorder("Unos") );
		p.setBounds(5,5,370,380);

		int i;
        n_fields = 13; 
        t = new FormField[n_fields]; 
        l = new JLabel[n_fields]; 
		
		String fmm;
		fmm = "*****";
        l[0] = new JLabel("\u0160ifra :");
        t[0] = new FormField(createFormatter(fmm,1));
		t[0].setColumns(5);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "**************************************************";
		l[1] = new JLabel("Naziv merila :");
        t[1] = new FormField(createFormatter(fmm,3));
		t[1].setColumns(18);
		t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "******************";
        l[2] = new JLabel("Oznaka :");
        t[2] = new FormField(createFormatter(fmm,3));
		t[2].setColumns(18);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "******************";
        l[3] = new JLabel("Grupa merila :");
        t[3] = new FormField(createFormatter(fmm,3));
		t[3].setColumns(18);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "******************************";
        l[4] = new JLabel("Merni opseg :");
        t[4] = new FormField(createFormatter(fmm,3));
		t[4].setColumns(18);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "******************";
		l[5] = new JLabel("Klasa ta\u010dnosti :");
        t[5] = new FormField(createFormatter(fmm,3));
		t[5].setColumns(18);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		fmm = "******************";
        l[6] = new JLabel("Proizvodja\u010d");
        t[6] = new FormField(createFormatter(fmm,3));
		t[6].setColumns(18);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		fmm = "*********";
        l[7] = new JLabel("Tip :");
        t[7] = new FormField(createFormatter(fmm,3));
		t[7].setColumns(18);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

		fmm = "******************************";
        l[8] = new JLabel("Serija :");
        t[8] = new FormField(createFormatter(fmm,3));
		t[8].setColumns(18);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});

		fmm = "****************************************";
        l[9] = new JLabel("Period atestiranja :");
        t[9] = new FormField(createFormatter(fmm,3));
		t[9].setColumns(18);
        t[9] = new FormField(createFormatter(fmm,4));
		t[9].setColumns(18);
        t[9].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[9].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[9]);}});

		fmm = "****";
        l[10] = new JLabel( "God. nabavke:");
        t[10] = new FormField(createFormatter(fmm,1));
		t[10].setColumns(5);
        t[10].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[10].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[10]);}});

		fmm = "##/##/####";
        l[11] = new JLabel("Dat.prvog preg. :");
        t[11] = new FormField(createFormatter(fmm,4));
		t[11].setColumns(10);
        t[11].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[11].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[11]);}});
		
		fmm = "**************************************************";
        l[12] = new JLabel("Napomena :");
        t[12] = new FormField(createFormatter(fmm,3));
		t[12].setColumns(18);
        t[12].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[12].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[12]);}});


	    for(i=0;i<13;i++){ 
			l[i].setFont(new Font("Arial",Font.PLAIN,ConnMySQL.lblfont));
			t[i].setFont(new Font("Arial",Font.PLAIN,ConnMySQL.txtfont));
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
					formatter.setValidCharacters("0123456789/- ");
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
        for(i=0;i<13;i++){
            t[i].setText("");
            t[i].setValue(null);
		}
		izmena = false;
		t[2].setText("");
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
		t[0].setText("");
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
			jPrintSpisakMKS kk1 = new jPrintSpisakMKS(connection);
	}
//------------------------------------------------------------------------------------------------------------------
    public void Stampakupdob(int _koji) {
		String sql = " ";
		String kkk = "";
		if (_koji == 1)
		{
			kkk = "KUPCI";
			sql = "SELECT * FROM saradnici WHERE kupdob='K'";
		}else{
			kkk = "DOBAVLJACI";
			sql = "SELECT * FROM saradnici WHERE kupdob='D'";
		}
		//jPrintSifarnik kk1 = new jPrintSifarnik(connection,2,sql,kkk);
		//fPrintKupDob kkl = new fPrintKupDob(connection,_koji);
	}
//------------------------------------------------------------------------------------------------------------------
    public void BrisiPressed() {
        this.setVisible(false);
    }
//------------------------------------------------------------------------------------------------------------------
    public void quitForm() {
		try {	connection.close(); } 
		catch (Exception f) {
			JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
		}
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

		 if ( t[0].getText().trim().length() != 0 && 
            t[1].getText().trim().length() != 0 ) {
            String query = "INSERT INTO mernasredstva (rbr,nazmerila,oznaka,grupmer,merops," +
				"klastac,proiz,tip,serija,peratest,datumnabavke,prvipregled,napomena) VALUES(" +
				Integer.parseInt(t[0].getText().trim()) + ",'" +
				t[1].getText() + "','" +
				t[2].getText() + "','" +
				t[3].getText() + "','" +
				t[4].getText() + "','" + 
				t[5].getText() + "','" + 
				t[6].getText() + "','" + 
				t[7].getText().trim() + "','" + 
				t[8].getText().trim() + "','" + 
				t[9].getText() + "','" +
				t[10].getText() + "','" +
				konvertujDatum(t[11].getText()) + "','" +
				t[12].getText() + "')"; 
			
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
				String upit = "SELECT * FROM mernasredstva order by rbr";
				popuniTabelu(upit);
				NoviPressed();
			}     
			else {
				JOptionPane.showMessageDialog(this, "Slog nije unet");
				NoviPressed();
            }
         }
         else{
				JOptionPane.showMessageDialog(this, "Unesi prvo podatke u polja");
				t[0].requestFocus();
		  }
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u unosu" + sqlex);
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
               String query = "UPDATE mernasredstva SET " +
				"nazmerila='" + t[1].getText()+ "'" +
				",oznaka='" + t[2].getText().trim() + "'" +
				//",valuta=" + Integer.parseInt(t[11].getText().trim()) +
				",grupmer='" + t[3].getText() + "'" +
				",merops='" + t[4].getText() + "'" +
				",klastac='" + t[5].getText() + "'" +
				",proiz='" + t[6].getText() + "'" +
				",tip='" + t[7].getText() + "'" +
				",serija='" + t[8].getText() + "'" +
				",peratest='" + t[9].getText() + "'" +
				",datumnabavke='" + t[10].getText() + "'" +
				",prvipregled='" + konvertujDatum(t[11].getText()) + "'" +
				",napomena='" + t[12].getText().toUpperCase() + "'" +
				" WHERE rbr=" + Integer.parseInt(t[0].getText().trim());

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
				//JOptionPane.showMessageDialog(this, "Slog je izmenjen");
				String upit = "SELECT * FROM mernasredstva order by rbr";
				popuniTabelu(upit);
				NoviPressed();
		}     
            else {
            JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
				NoviPressed();
            }
         }
         else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje Broj Saradnika");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u izmeni" + sqlex);
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
					String query = "DELETE FROM mernasredstva WHERE rbr=" + 
						Integer.parseInt(t[0].getText().trim());

               		int rs = statement.executeUpdate( query );
               		if ( rs != 0 ){
						String upit = "SELECT * FROM mernasredstva order by rbr";
						popuniTabelu(upit);
						NoviPressed();
					}     
            		else {
            			JOptionPane.showMessageDialog(this, "Slog se ne moze izbrisati");
               			NoviPressed();
            		}
        }else {
			JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje \u0160ifra");
		}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u DeleteRecord:" + sqlex);
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
			String query = "SELECT * FROM mernasredstva WHERE rbr=" + 
				Integer.parseInt(koji);

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){

		         	t[0].setText(String.valueOf(rs.getInt("rbr")));
		         	t[1].setText(rs.getString("nazmerila").trim());
		         	t[2].setText(rs.getString("oznaka").trim());
		         	t[3].setText(rs.getString("grupmer").trim());
		         	t[4].setText(rs.getString("merops").trim());
					t[5].setText(rs.getString("klastac").trim());
					t[6].setText(rs.getString("proiz").trim());
					t[7].setText(rs.getString("tip").trim());
					t[8].setText(rs.getString("serija").trim());
					t[9].setText(rs.getString("peratest").trim());
					t[10].setText(rs.getString("datumnabavke").trim());
					t[11].setText(konvertujDatumIzPodataka(rs.getString("prvipregled")));
					t[12].setText(rs.getString("napomena").trim());
	         		rs.close();
					izmena = true;				
				}
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
		 }     
         else {
            JOptionPane.showMessageDialog(this, "Saradnik nije unet");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u tra\u017eenju");
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
	private void TraziRecord(){
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva");
		String upit = "SELECT * FROM mernasredstva WHERE nazmerila LIKE '%" + String.valueOf(result) + "%'";
		popuniTabelu(upit);
		jtbl.requestFocus();
  }
//------------------------------------------------------------------------------------------------------------------
	private void TraziRecordPoMestu(){
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva saradnika");
		String upit = "SELECT * FROM mernasrestva WHERE mesto LIKE '%" + String.valueOf(result) + "%'";
		popuniTabelu(upit);
		jtbl.requestFocus();
  }
//------------------------------------------------------------------------------------------------------------------
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekora\u010dena du\u017eina podatka");
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
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );
		ptbl.setBounds(380,5,380,360);

	   	qtbl = new QTM2(connection);
		String sql;
		sql = "SELECT * FROM mernasredstva order by rbr";
	   	qtbl.query(sql);
		TableSorter sorter = new TableSorter(qtbl); 
		jtbl = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable(jtbl); 
        jtbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
 	   	
		//jtbl = new JTable( qtbl );
		jtbl.setAlignmentX(JTable.RIGHT_ALIGNMENT); 
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		jtbl.setFont(new Font("Arial",Font.PLAIN,ConnMySQL.tablefont));
		JTableHeader header = jtbl.getTableHeader();
        header.setFont(new Font("Arial", Font.PLAIN,ConnMySQL.tablefont));

		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(100);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
	}
//------------------------------------------------------------------------------------------------------------------
    public void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = jtbl.getValueAt(kojirec,0).toString();
		FindRecord();
	}
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(this, "Greska: " + sql);
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
//--------------------------------------------------------------------------
   public String konvertujDatum(String _datum){
		String datum,pom;
		pom = _datum;
		datum = pom.substring(6,10);
		datum = datum + "-" + pom.substring(3,5);
		datum = datum + "-" + pom.substring(0,2);
		return datum;
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
	public void Akcija(FormField e) {
		FormField source;
		source = e;

				if (source == t[0]){
					t[1].setSelectionStart(0);
					t[1].requestFocus();}
				else if (source == t[1]){
					if (t[1].getText().trim().length() == 0){
						t[1].setText("0");}
					t[2].setSelectionStart(0);
					t[2].requestFocus();}
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0){
						t[2].setText("0");}
					t[3].setSelectionStart(0);
					t[3].requestFocus();}
				else if (source == t[3]){
					t[4].setSelectionStart(0);
					t[4].requestFocus();}
				else if (source == t[4]){
					t[5].setSelectionStart(0);
					t[5].requestFocus();}
				else if (source == t[5]){
					if (t[5].getText().trim().length() == 0){
						t[5].setText("0");}
					t[6].setSelectionStart(0);
					t[6].requestFocus();}
				else if (source == t[6]){
					if (t[6].getText().trim().length() == 0){
						t[6].setText("0");}
					t[7].setSelectionStart(0);
					t[7].requestFocus();}
				else if (source == t[7]){
					if (t[7].getText().trim().length() == 0){
						t[7].setText("0");}
					t[8].setSelectionStart(0);
					t[8].requestFocus();}
				else if (source == t[8]){
					if (t[8].getText().trim().length() == 0){
						t[8].setText(" ");
					}
					t[9].setSelectionStart(0);
					t[9].requestFocus();}
				else if (source == t[9]){
					if (t[9].getText().trim().length() == 0){
						t[9].setText(" ");
					}
					t[10].setSelectionStart(0);
					t[10].requestFocus();
				}
				else if (source == t[10]){
					if (t[10].getText().trim().length() == 0){
						t[10].setText(" ");
					}
					t[11].setSelectionStart(0);
					t[11].requestFocus();
				}
				else if (source == t[11]){
					if (t[11].getText().trim().length() == 0){
						t[11].requestFocus();
					}else{
						if (proveriDatum(t[11].getText().trim()))
						{
							t[12].setSelectionStart(0);
							t[12].requestFocus();
						}
					}
				}
				else if (source == t[12]){
					if (t[12].getText().trim().length() == 0){
						t[12].setText(" ");
					}
					if (izmena)
					{
						izmeni.requestFocus();
					}
					else{
						unesi.requestFocus();
					}
				}
}
//===========================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
			if (source == t[1]){
				if (t[0].getText().trim().length() > 0)
				{
					koji = t[0].getText().trim();
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
 class QTM2 extends AbstractTableModel {
	Connection dbconn;
	String[] colheads = {"\u0160ifra","Naziv merila","Oznaka","Grupa merila","Merni opseg","Klasa tac"};

//------------------------------------------------------------------------------------------------------------------
   public QTM2(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector<String[]>();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 6; }
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

               String[] record = new String[6];
               record[0] = rs.getString("rbr");
               record[1] = rs.getString("nazmerila");
               record[2] = rs.getString("oznaka");
               record[3] = rs.getString("grupmer");
               record[4] = rs.getString("merops");
               record[5] = rs.getString("klastac");
               podaci.addElement(record[0]);
               totalrows.addElement( record );
            }
				rs.close();
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
 }//end of class QTM2

}// end of class Konto ====================================================================
class CL22 implements LayoutManager {

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
}