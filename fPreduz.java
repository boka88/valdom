//Sifarnik preduzeca-----------------------
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

public class fPreduz extends JInternalFrame implements InternalFrameListener
			{
	private QTM1 qtbl;
   	private JTable jtbl;
	private Vector totalrows;
	private JScrollPane jspane;
	private Vector podaci = new Vector();
	private Vector spodaci = new Vector();
	private boolean izmena = false;
	private boolean bris=false,bris1=false;

	private String koji;
	private MaskFormatter fmt = null;
	private String pPre,nazivPre;
	private JButton novi,unesi,izmeni;
	private ConnMySQL dbconn;
	private Connection connection = null;
    public JFormattedTextField t[],mmoj;
   	private JLabel  l[];
	private boolean unosIzmena;
   	int n_fields;
//------------------------------------------------------------------------------------------------------------------
    public fPreduz() {
		super("", true, true, true, true);
        setTitle("\u0160ifarnik preduze\u0107a");
		setMaximizable(false);
		setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    addInternalFrameListener(this);

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout() );

		JPanel glavni = new JPanel();
		glavni.setLayout( null );

		JPanel container = new JPanel();
		container.setLayout( new GridLayout(1,1) );
		container.setBounds(5,5,340,380);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   UnesiPressed(); }});
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {UnesiPressed();}});

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

		JButton brisi = new JButton("Bri\u0161i slog");
		brisi.setMnemonic('B');
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   DeleteRecord(); }});
		buttonPanel.add( brisi );

		JButton trazi = new JButton("Tra\u017ei");
		trazi.setMnemonic('T');
		trazi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   TraziRecord(); }});
		buttonPanel.add( trazi );

		JButton stampa = new JButton("\u0160tampa");
		stampa.setMnemonic('P');
		stampa.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Stampa(); }});
		//buttonPanel.add( stampa );

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
		setSize(760,450);
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
				JOptionPane.showMessageDialog(this, "Ne mo\u017ee se zatvoriti konekcija");
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
		p.setLayout( new ColumnLayout2() );
		p.setBorder( new TitledBorder("Unos") );



		int i;
        n_fields = 14; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 

		
		String fmm;
		fmm = "**";
        l[0] = new JLabel("\u0160ifra :");
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setColumns(2);
		t[0].setSelectionStart(0);
		t[0].setSelectionEnd(1);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});


		fmm = "******************************";
		l[1] = new JLabel("Naziv preduze\u0107a :");
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
		t[1].setColumns(15);
		t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "****";
        l[2] = new JLabel("Po\u0161tanski broj :");
        t[2] = new JFormattedTextField(createFormatter(fmm,1));
		t[2].setColumns(5);
		t[2].setSelectionStart(0);
		t[2].setSelectionEnd(1);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "********************";
        l[3] = new JLabel("Mesto :");
        t[3] = new JFormattedTextField(createFormatter(fmm,3));
		t[3].setColumns(15);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

		fmm = "*************************";
		l[4] = new JLabel("Adresa :");
        t[4] = new JFormattedTextField(createFormatter(fmm,3));
		t[4].setColumns(15);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

		fmm = "***************";
        l[5] = new JLabel("Telefon");
        t[5] = new JFormattedTextField(createFormatter(fmm,4));
		t[5].setColumns(15);
        t[5].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[5].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[5]);}});

		fmm = "***************";
        l[6] = new JLabel("Fax :");
        t[6] = new JFormattedTextField(createFormatter(fmm,4));
		t[6].setColumns(15);
        t[6].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[6].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[6]);}});

		fmm = "******************************";
        l[7] = new JLabel("\u017diro racun1 :");
        t[7] = new JFormattedTextField(createFormatter(fmm,4));
		t[7].setColumns(15);
        t[7].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[7].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[7]);}});

        l[8] = new JLabel("\u017diro racun2 :");
        t[8] = new JFormattedTextField(createFormatter(fmm,4));
		t[8].setColumns(15);
        t[8].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[8].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[8]);}});

        l[9] = new JLabel("\u017diro racun3 :");
        t[9] = new JFormattedTextField(createFormatter(fmm,4));
		t[9].setColumns(15);
        t[9].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[9].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[9]);}});

		fmm = "*************";
        l[10] = new JLabel("Mati\u010dni broj :");
        t[10] = new JFormattedTextField(createFormatter(fmm,1));
		t[10].setColumns(13);
        t[10].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[10].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[10]);}});

		fmm = "***************";
        l[11] = new JLabel("\u0160ifra delatnosti :");
        t[11] = new JFormattedTextField(createFormatter(fmm,1));
		t[11].setColumns(15);
        t[11].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[11].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[11]);}});

		fmm = "*********";
        l[12] = new JLabel("PIB :");
        t[12] = new JFormattedTextField(createFormatter(fmm,1));
		t[12].setColumns(9);
        t[12].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[12].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[12]);}});

		fmm = "********************";
        l[13] = new JLabel("PEPDV :");
        t[13] = new JFormattedTextField(createFormatter(fmm,1));
		t[13].setColumns(15);
		//------------------- programiranje provere i akcije ------------------------
        t[13].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[13].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[13]);}});


	    for(i=0;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); }

		return p;
    }
//-----------------------------------------------------------------------------------------------
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
					//formater za datum, ziro i telefon
					formatter.setValidCharacters("0123456789/- ");
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
		this.setLocation(50,0);
    }
//------------------------------------------------------------------------------------------------------------------
    public void NoviPressed() {
        int i;
        n_fields = 14; 
        for(i=0;i<n_fields;i++)
            t[i].setText("");
		izmena = false;
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
    public void BrisiPressed() {
        this.setVisible(false);
    	}
	
//------------------------------------------------------------------------------------------------------------------
    public void quitForm() {
		// zatvaranje programa-----------------------------
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
	public String vodeceNule(String _forma, int koliko) {
		String forma,format;
		int i,j;
		forma = _forma.trim();
		j = forma.length();
		if (j < koliko)
		{
			for (i=j;i<koliko ;i++ )
			{
				forma = "0" + forma;
			}
		}
		return forma;
 }
//------------------------------------------------------------------------------------------------------------------
	public void AddRecord() {
		Statement statement = null;
      try {
         statement = connection.createStatement();

		 if ( t[0].getText().trim().length() != 0 && 
            t[1].getText().trim().length() != 0 ) {
            String query = "INSERT INTO preduzeca (Pre,Naziv1,pbroj,Mesto,Adresa," +
				"Telefon,Fax,Ziror,Ziror1,Ziror2,matbr,sido,pib,pepdv) VALUES(" +
				Integer.parseInt(t[0].getText().trim()) + ",'" +
				t[1].getText() + "'," +
				Integer.parseInt(t[2].getText().trim()) + ",'" +
				t[3].getText() + "','" +
				t[4].getText() + "','" + 
				t[5].getText() + "','" + 
				t[6].getText() + "','" + 
				t[7].getText() + "','" + 
				t[8].getText() + "','" + 
				t[9].getText() + "','" + 
				t[10].getText()+ "','" +			
				t[11].getText()+ "','" + 
				t[12].getText()+ "','" + 
				t[13].getText()+ "')"; 
			
			int result = statement.executeUpdate( query );

			if ( result == 1 ){
			JOptionPane.showMessageDialog(this, "Slog je unet");
				String upit = "SELECT * FROM preduzeca";
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
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u unosu");}
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
		Statement statement=null;
      try {
         statement = connection.createStatement();
		 if ( !t[0].getText().equals( "" )) {
               String query = "UPDATE preduzeca SET " +
				"Naziv1='" + t[1].getText()+ "'" +
				",pbroj=" + Integer.parseInt(t[2].getText().trim()) +
				",Mesto='" + t[3].getText() + "'" +
				",Adresa='" + t[4].getText() + "'" +
				",Telefon='" + t[5].getText() + "'" +
				",Fax='" + t[6].getText() + "'" +
				",Ziror='" + t[7].getText() + "'" +
				",Ziror1='" + t[8].getText() + "'" +
				",Ziror2='" + t[9].getText() + "'" +
				",matbr='" + t[10].getText() + "'" +
				",sido='" + t[11].getText() + "'" +
				",pib='" + t[12].getText() + "'" +
				",pepdv='" + t[13].getText() + "'" +
				" WHERE Pre=" + Integer.parseInt(t[0].getText().trim());

			   int result = statement.executeUpdate( query );
               if ( result == 1 ){
					JOptionPane.showMessageDialog(this, "Slog je izmenjen");
					String upit = "SELECT * FROM preduzeca ";
					popuniTabelu(upit);
					NoviPressed();
				}     
				else {
					JOptionPane.showMessageDialog(this, "Slog nije izmenjen");
					NoviPressed();}
         }
         else {
		JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje \u0160ifra");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Greska u izmeni");}
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
		Statement statement=null;
      try {
         statement = connection.createStatement();
         if ( !t[0].getText().equals( "" ) ) {
               	String query = "DELETE FROM preduzeca WHERE Pre=" + 
						Integer.parseInt(t[0].getText().trim());
						



               	int rs = statement.executeUpdate( query );
               	if ( rs != 0 ){
				JOptionPane.showMessageDialog(this, "Slog je izbrisan");
				String upit = "SELECT * FROM preduzeca ";
				popuniTabelu(upit);
				NoviPressed();
			}     
            	else {
            		JOptionPane.showMessageDialog(this, "Slog se ne moze izbrisati");
               		NoviPressed();
            	}

         }
         else {JOptionPane.showMessageDialog(this, "Unesi prvo podatak u polje \u0160ifra");}
      }
      catch ( SQLException sqlex ) {
		JOptionPane.showMessageDialog(this, "Gre\u0161ka u brisanju");}
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
		Statement statement=null;
      try {
         statement = connection.createStatement();

		 if ( !t[0].getText().equals( "" )) {
			String query = "SELECT * FROM preduzeca WHERE Pre=" + koji;

			try {
		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){

		         	t[0].setText(String.valueOf(rs.getInt("Pre")));
		         	t[1].setText(String.valueOf(rs.getString("Naziv1")));
		         	t[2].setText(String.valueOf(rs.getInt("pbroj")));
		         	t[3].setText(String.valueOf(rs.getString("Mesto")));
		         	t[4].setText(String.valueOf(rs.getString("Adresa")));
					t[5].setText(String.valueOf(rs.getString("Telefon")));
					t[6].setText(String.valueOf(rs.getString("Fax")));
					t[7].setText(String.valueOf(rs.getString("Ziror")));
					t[8].setText(String.valueOf(rs.getString("Ziror1")));
					t[9].setText(String.valueOf(rs.getString("Ziror2")));
					t[10].setText(String.valueOf(rs.getString("matbr")));
					t[11].setText(String.valueOf(rs.getString("sido")));
					t[12].setText(String.valueOf(rs.getString("pib")));
					t[13].setText(String.valueOf(rs.getString("pepdv")));
					izmena = true;				
				}
					rs.close();
					t[1].setSelectionStart(0);
					t[1].requestFocus();
		      }
		      catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(this, sqlex);
		      }
		 }     
         else {JOptionPane.showMessageDialog(this, "Saradnik nije unet");}
         	//statement.close();
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
        String result = JOptionPane.showInputDialog(this, "Unesi naziv ili deo naziva preduze\u0107a");
		String upit = "SELECT * FROM preduzeca WHERE Naziv1 LIKE '" + String.valueOf(result) + "%'";
		popuniTabelu(upit);
		jtbl.requestFocus();
  }
//------------------------------------------------------------------------------------------------------------------
	public void Stampa(){
		//fPrintPreduz pre = new fPrintPreduz();
   }
//------------------------------------------------------------------------------------------------------------------
	public void greskaDuzina(){
		JOptionPane.showMessageDialog(this, "Prekora\u010dena du\u017eina podatka");
   }
//------------------------------------------------------------------------------------------------------------------
	public void obavestiGresku(){
		//JOptionPane.showMessageDialog(this, "Mogu se unositi samo brojevi");
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
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(2);
	   	tcol3.setPreferredWidth(100);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(3);
	   	tcol4.setPreferredWidth(100);
	}
//------------------------------------------------------------------------------------------------------------------
    public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new QTM1(connection);
		String sql;
		sql = "SELECT * FROM preduzeca ";
	   	qtbl.query(sql);
 	   	jtbl = new JTable( qtbl );
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
		
	   	TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(60);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(2);
	   	tcol3.setPreferredWidth(100);
	   	TableColumn tcol4 = jtbl.getColumnModel().getColumn(3);
	   	tcol4.setPreferredWidth(100);
	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		ptbl.setBounds(350,5,400,380);
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
					t[1].requestFocus();}
				else if (source == t[1]){
					t[2].setSelectionStart(0);
					t[2].requestFocus();}
				else if (source == t[2]){
					if (t[2].getText().trim().length() == 0){
						t[2].setText("0");}
					t[3].setSelectionStart(0);
					t[3].requestFocus();
				}
				else if (source == t[3]){
					t[4].setSelectionStart(0);
					t[4].requestFocus();}
				else if (source == t[4]){
					t[5].setSelectionStart(0);
					t[5].requestFocus();}
				else if (source == t[5]){
					t[6].setSelectionStart(0);
					t[6].requestFocus();}
				else if (source == t[6]){
					t[7].setSelectionStart(0);
					t[7].requestFocus();}
				else if (source == t[7]){
					t[8].setSelectionStart(0);
					t[8].requestFocus();}
				else if (source == t[8]){
					t[9].setSelectionStart(0);
					t[9].requestFocus();}
				else if (source == t[9]){
					t[10].setSelectionStart(0);
					t[10].requestFocus();}
				else if (source == t[10]){
					t[11].setSelectionStart(0);
					t[11].requestFocus();}
				else if (source == t[11]){
					t[12].setSelectionStart(0);
					t[12].requestFocus();}
				else if (source == t[12]){
					t[13].setSelectionStart(0);
					t[13].requestFocus();}
				else if (source == t[13]){
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
 class QTM1 extends AbstractTableModel {
	Connection dbconn;
	//public Vector totalrows;
	String[] colheads = {"\u0160ifra","Naziv","Mesto","Adresa"};

//------------------------------------------------------------------------------------------------------------------
   public QTM1(Connection dbc){
		JPanel pp = new JPanel();
		dbconn = dbc;
		totalrows = new Vector();
   }
//------------------------------------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 4; }
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
		//JOptionPane.showMessageDialog(this, datum);
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

               String[] record = new String[4];
               record[0] = rs.getString("Pre");
               record[1] = rs.getString("Naziv1");
               record[2] = rs.getString("Mesto");
               record[3] = rs.getString("Adresa");
               podaci.addElement(record[0]);
			   //spodaci.addElement(record[1]);
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
 }//end of class QTM1
}// end of class Konto ====================================================================
class ColumnLayout2 implements LayoutManager {

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
	  children[i].setLocation( xInset + insets.left, height + 1);
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

}// end of class ColumnLayout2

