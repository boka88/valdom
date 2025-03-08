//glavni ekran programa za knjigovodstvo
//================================================================
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.io.File;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.geom.GeneralPath;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import javax.swing.plaf.metal.*;
import java.util.*;
import java.applet.*;
import java.beans.PropertyVetoException.*;


/**
  * This is the main container frame for the Fin app
  *
  * @version 1.0
  * @author Boris Ivanisevic
  */
public class AutoFrame extends JFrame {

    JMenuBar menuBar;
    public static JDesktopPane desktop;
	public String userName="";
	public int userSifra,userStatus;
    JInternalFrame toolPalette;
    JCheckBoxMenuItem showToolPaletteMenuItem;
    //public ConnMySQL connection;     
	public static Font veciFont = new Font("Arial", Font.PLAIN, 18); // Primer sa vecim fontom
    static final Integer DOCLAYER = new Integer(5);
    static final Integer TOOLLAYER = new Integer(6);
    static final Integer HELPLAYER = new Integer(7);

    static final String ABOUTMSG = "Auto \n \nOva aplikacija je pisana u Javi. \n \nBIN - Soft team \n  Boris Ivanisevi\u0107";
    static final String PORUKA = "Ne mo\u017ee se ostvariti konekcija"; 
    static final String PRINTT = "\u0160tampa u Javi"; 
	private classBekap classbekap = null;


//--------------------------------------------------------------------------------------------
	public AutoFrame() {
        super("Valdom - poslovna godina "+aLogin.godina);
        final int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width,screenSize.height);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		UIManager.put("MenuItem.font", veciFont);
		UIManager.put("Menu.font", veciFont); // Opcionalno i za JMenu
		// A�uriranje UI nakon promene
		SwingUtilities.updateComponentTreeUI(this);
		
		classbekap = new classBekap(aLogin.konekcija);
		
		buildContent();
		buildMenus();
	
		this.addWindowListener(new WindowAdapter() {
	                       public void windowClosing(WindowEvent e) {
				   quit();}});
		UIManager.addPropertyChangeListener(new UISwitchListener((JComponent)getRootPane()));

    }
//--------------------------------------------------------------------------------------------
    protected void buildMenus() {
		menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		JMenu file = buildFileMenu();
		file.setMnemonic('F');
		JMenu nalozi = buildNalMenu();
		nalozi.setMnemonic('I');
		JMenu edit = buildEditMenu();
		edit.setMnemonic('A');
		JMenu views = buildViewsMenu();
		views.setMnemonic('P');
		JMenu speed = buildSpeedMenu();
		speed.setMnemonic('U');
		JMenu help = buildHelpMenu();
		help.setMnemonic('H');

		menuBar.add(file);
		menuBar.add(views);
		menuBar.add(nalozi);
		//menuBar.add(speed);
		menuBar.add(edit);
		menuBar.add(help);
		setJMenuBar(menuBar);	
    }
//--------------------------------------------------------------------------------------------
    protected JMenu buildFileMenu() {
	JMenu file = new JMenu("\u0160ifarnici");
        //file.setFont(veciFont);

		JMenuItem nom2 = new JMenuItem("\u0160ifarnik ma\u0161ina");
		JMenuItem radnici = new JMenuItem("\u0160ifarnik radnika-operatera");
		JMenuItem nomm = new JMenuItem("Dobavlja\u010di");
		JMenuItem mas = new JMenuItem("\u0160ifarnik merno kontrolnih sredstava");
		
		JMenuItem nom22 = new JMenuItem("Ostala oprema u vozilu");
		JMenuItem nom3 = new JMenuItem("Delovi i materijal");
		JMenuItem nom33 = new JMenuItem("Pregled radnih naloga");
		JMenuItem nom333 = new JMenuItem("Pregled r.n. po imenu");
		JMenuItem nom4 = new JMenuItem("Vozila");
		JMenuItem nom5 = new JMenuItem("\u0160ifarnik svih radnika");
		JMenuItem nom6 = new JMenuItem("Preduze\u0107e");
		JMenuItem nom7 = new JMenuItem("Unos uplata");

        /*
		nom2.setFont(veciFont);
        nom22.setFont(veciFont);
        nom3.setFont(veciFont);
        nomm.setFont(veciFont);
        nom33.setFont(veciFont);
        nom4.setFont(veciFont);
        nom5.setFont(veciFont);
        nom6.setFont(veciFont);
        nom7.setFont(veciFont);
		*/


		/*
		if (userStatus == 0)
		{
			nom5.setEnabled(false);
			nom33.setEnabled(false);
		}
		*/
		file.add(nom2);
		file.add(radnici);
		file.add(nomm);
		file.add(mas);
		file.addSeparator();
		//file.add(nom22);
		file.add(nom3);
		//file.add(nom33);
		//file.add(nom333);
		file.addSeparator();
		//file.add(nom4);
		//file.add(nom5);
		file.add(nom6);
		//file.add(nom7);
		
		nom2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosMasina();}});
		nom22.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosOstalaOprema();}});
		nom3.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   DeloviOprema();}});
		nom33.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledRadnihNaloga();}});
		nom333.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledRadnihNalogaIme();}});
		nomm.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   SifSarad();}});
		mas.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   SifMerila();}});
		radnici.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   Radnici();}});

		nom4.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosMasina();}});
		nom5.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   SifarnikRadnika();}});
		nom6.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   SifarnikPreduzeca();}});
		nom7.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosUplata();}});
		return file;
    }
//--------------------------------------------------------------------------------------------
    protected JMenu buildViewsMenu() {
		JMenu views = new JMenu("Podaci");

		JMenuItem kvarovi = new JMenuItem("Unos kvarova ma\u0161ina");
		JMenuItem atesti = new JMenuItem("Unos atesta merno kontrolnih sredstava");

		
		JMenuItem knji1 = new JMenuItem("Po\u010detno Stanje");
		JMenuItem knji2 = new JMenuItem("Ulaz delova i opreme");
		JMenuItem knji3 = new JMenuItem("Izdatnice");
		
		
		JMenuItem box1 = new JMenuItem("Ulaz delova i opreme");
		JMenuItem box2 = new JMenuItem("Izdavanje delova i opreme");
		JMenuItem box3 = new JMenuItem("Radni nalozi");
		JMenuItem box4 = new JMenuItem("Gorivo");
		JMenuItem box5 = new JMenuItem("Zamena ulja");

	
		box1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosDelova();}});
		box2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   IzlazDelova();}});
		box3.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosRadnihNaloga();}});
		box4.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   Gorivo();}});
		box5.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   ZamenaUlja();}});
		kvarovi.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosKvarova();}});
		atesti.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   UnosAtesta();}});

		knji1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   mUnosPoc();}});
		knji2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   mUnosPoNabavnojCeni();}});
		knji3.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   mUnosIzdatnica();}});

		views.add(kvarovi);
		views.add(atesti);
		views.addSeparator();
		views.add(knji1);
		views.add(knji2);
		views.add(knji3);
		//views.add(box5);
	
		return views;
    }
//--------------------------------------------------------------------------------------------
    protected JMenu buildNalMenu() {
		JMenu nom = new JMenu("Izve\u0161taji");
			JMenuItem kartmas = new JMenuItem("Kartice ma\u0161ina");
			JMenuItem kartdel = new JMenuItem("Kartice delova i opreme");
			JMenuItem unos2 = new JMenuItem("Lager lista delova i opreme");
			
			//----  ne koristi se ---------------------------------------------
			JMenuItem aaa1 = new JMenuItem("Lager lista delova i opreme");
			JMenuItem aaa2 = new JMenuItem("Kartice delova i opreme");
			JMenuItem aaa3 = new JMenuItem("Kartice delova - vozila");
			JMenuItem aaa4 = new JMenuItem("Kartice goriva - vozila");
			JMenuItem aaa5 = new JMenuItem("Pregled radnih naloga");
			JMenuItem nom1 = new JMenuItem("Spisak radnih naloga po vozilima");
			//------------------------------------------------------------------
			
			JMenuItem prprijem = new JMenuItem("Pregled prijemnica");
			JMenuItem prizdat = new JMenuItem("Pregled izdatnica");
			JMenuItem prutrdel = new JMenuItem("Pregled utro\u0161enih delova");
			JMenuItem prazNal = new JMenuItem("Prazan nalog");



			//----  ne koristi se ---------------------------------------------
			JMenuItem nom4 = new JMenuItem("Kartice korisnika - dugovanja");
			JMenuItem nom41 = new JMenuItem("Kartice korisnika - radni nalozi");
			JMenuItem nom5 = new JMenuItem("Lista du\u017enika");
			JMenuItem nom6 = new JMenuItem("Izve\u0161taj po marki vozila");
			//------------------------------------------------------------------

		aaa1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   LagerLista();}});
		aaa2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   Kartice();}});
		aaa3.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   KarticeVozila();}});
		aaa4.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   KarticeGoriva();}});
		aaa5.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledNaloga();}});

		nom1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   izvestajFizicka();}});

		prprijem.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledPrijemnica();}});
		prizdat.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledIzdatnica();}});

		prutrdel.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledUtrosenihDelova();}});
		prazNal.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PrazanNalog();}});



		nom4.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   KarticeKupaca();}});
		nom41.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   KarticeKupacaRn();}});
		nom5.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   ListaDuznika();}});
		nom6.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   IzvestajMarka();}});
		kartmas.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   KarticeMasina();}});
		kartdel.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   KarticeDelova();}});
		unos2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   LagerListe(1);}});

	nom.add(kartmas);
	nom.addSeparator();
	nom.add(kartdel);
	nom.add(unos2);
	nom.addSeparator();
	nom.add(prprijem);
	nom.add(prizdat);
	nom.add(prutrdel);
	nom.add(prazNal);
	
	/*
	nom.add(aaa1);
	nom.add(aaa2);
	nom.add(aaa3);
	nom.addSeparator();
	nom.add(aaa4);
	nom.add(aaa5);
	*/

	//nom.add(nom1);
	/*
    nom.add(nom2);
	nom.add(rekl);
    nom.add(nom3);
    nom.add(nom4);
    nom.add(nom41);
    nom.add(nom5);
	nom.add(nom6);
	*/
	return nom;
    }
//--------------------------------------------------------------------------------------------
    protected JMenu buildEditMenu() {
	JMenu malo = new JMenu("Administracija");
		JMenuItem knji1 = new JMenuItem("Brisanje baze - nova godina");
		
		knji1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   NuliranjeBaze();}});

	//malo.add(knji1);
	return malo;
    }
//--------------------------------------------------------------------------------------------
     protected JMenu buildSpeedMenu() {
        JMenu speed = new JMenu("Pregledi");

	JMenuItem stavka = new JMenuItem("Registracije");
	JMenuItem stavka1 = new JMenuItem("Tehni\u010dki pregledi");
	JMenuItem stavka2 = new JMenuItem("Lekarska uverenja");
	JMenuItem stavka3 = new JMenuItem("Zamena ulja");


		stavka.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledRegistracija();}});

		stavka1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   TehnickiPregledi();}});
		stavka2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   PregledLekarski();}});
		stavka3.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   ZamenaUlja();}});

	speed.add(stavka);
	speed.add(stavka1);
	speed.add(stavka2);
	speed.addSeparator();
	speed.add(stavka3);
	return speed;
     }

//--------------------------------------------------------------------------------------------
    protected JMenu buildHelpMenu() {
	JMenu help = new JMenu("Help");
    JMenuItem about = new JMenuItem("O programu");
	JMenuItem openPrint = new JMenuItem("Nuliranje baze");
	JMenuItem openHelp = new JMenuItem("Prozor za pomo\u0107");
	JMenuItem stavka3 = new JMenuItem("Zamena ulja");
	
	JMenuItem repar = new JMenuItem("Reparacija baze");

		about.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        showAboutBox();
			}});

		openHelp.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   openHelpWindow();}});
		openPrint.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   openJR();}});
		repar.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   ReparacijaBaze();}});


		help.add(openHelp);
		help.add(about);
		help.add(openPrint);
		help.addSeparator();
		help.add(repar);
		return help;
    }
//--------------------------------------------------------------------------------------------
    protected void buildContent() {
        desktop = new JDesktopPane();
		desktop.setBackground(Color.black);
        getContentPane().add(desktop);
    }
//--------------------------------------------------------------------------------------------
    public void quit() {
			if (ConnMySQL.bekapdane == 1)
			{
				classbekap.uradiBekap();
				//classbekap.napraviSQL(aLogin.konekcija);
			}
			// zatvaranje konekcije sa mysql serverom
		    zatvoriKonekciju();		
	        System.exit(0);
    }
//------------------------------------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
		if (aLogin.konekcija != null){
			try {aLogin.konekcija.close(); 
					aLogin.konekcija = null;
			} 
			catch (Exception f) {
				System.out.println("Ne moze se zatvoriti konekcija");
   			}	
		}
	}
//--------------------------------------------------------------------------------------------
    public void UnosMasina() {
		JInternalFrame pred = new aMasine();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void LagerLista() {
		JInternalFrame pred = new mLager(1);
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void Kartice() {
		JInternalFrame pred = new mKartice();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void KarticeVozila() {
		JInternalFrame pred = new mKarticeV();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void UnosDelova() {
		JInternalFrame pred = new mUnosPrijem();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void IzlazDelova() {
		JInternalFrame pred = new mUnosIzdat();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void DeloviOprema() {
		JInternalFrame pred = new mRobe();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void SifSarad() {
		JInternalFrame pred = new fSarad();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void SifMerila() {
		JInternalFrame pred = new fMerila();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void UnosOstalaOprema() {
		JInternalFrame pred = new aOstalaOprema();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void UnosRadnihNaloga() {
		JInternalFrame pred = new mRNal();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void PregledRadnihNaloga() {
		JInternalFrame pred = new mPregledRNal();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void PregledRadnihNalogaIme() {
		/*
		JInternalFrame pred = new mPregledRNIme();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
		*/
    }

//--------------------------------------------------------------------------------------------
    public void izvestajPravna() {
		JInternalFrame pred = new mPregledNalogaSvi();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void izvestajReklamacije() {
		JInternalFrame pred = new mPregledNalogaRekl();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void regListFiz() {
		JInternalFrame pred = new regListFizicka();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void regListPrav() {
		JInternalFrame pred = new regListPravna();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void izvestajFizicka() {
		JInternalFrame pred = new mPregledNalogaR();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }

//--------------------------------------------------------------------------------------------
    public void SifarnikRadnika() {
		JInternalFrame pred = new aUnosRadnika();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void SifarnikPreduzeca() {
		JInternalFrame pred = new fPreduz();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void UnosUplata() {
		JInternalFrame pred = new mUplate();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void openHelpWindow() {
		JInternalFrame help = new FinHelp();
			desktop.add(help, HELPLAYER);
		try { 
			help.setVisible(true);
			help.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void TrazenjeRadNal() {
		JInternalFrame pred = new aTrazenjeRadNal();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void KarticeKupaca() {
		JInternalFrame pred = new mFinanKupci();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void KarticeKupacaRn() {
		JInternalFrame pred = new mFinanKupciRn();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void ListaDuznika() {
		/*
		JInternalFrame pred = new mListaDuznika();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
		*/
	}
//--------------------------------------------------------------------------------------------
    public void IzvestajMarka() {
		JInternalFrame pred = new aIzvestajMarka();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void showAboutBox() {
        JOptionPane.showMessageDialog(this, ABOUTMSG);
    }
//--------------------------------------------------------------------------------------------
    public void openPrefsWindow() {
        FinPrefs dialog = new FinPrefs(this);
		//dialog.show();
		dialog.setVisible(true);
    }
//--------------------------------------------------------------------------------------------
    public void NuliranjeBaze() {
		mNuliranjeBaze jjj = new mNuliranjeBaze();
		jjj.setModal(true);
		jjj.setVisible(true);
	}
//--------------------------------------------------------------------------------------------
    public void Gorivo() {
		JInternalFrame pred = new aGorivo();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}

//--------------------------------------------------------------------------------------------
    public void KarticeGoriva() {
		JInternalFrame pred = new mKarticeGorivo();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void PregledNaloga() {
		JInternalFrame pred = new aPregledNaloga();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void PregledRegistracija() {
		JInternalFrame pred = new izvPravna();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void PregledLekarski() {
		JInternalFrame pred = new izvLekarski();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void TehnickiPregledi() {
		JInternalFrame pred = new izvTehnicki();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void ZamenaUlja() {
		JInternalFrame pred = new izvZamenaUlja();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void openJR() {
		mNuliranjeBazeA jjj = new mNuliranjeBazeA();
		jjj.setVisible(true);
	}
//------------------------------------------------------------------------------------------------------------------
    public void ReparacijaBaze() {
		Repair rrr = new Repair();
		rrr.setVisible(true);
	}
//--------------------------------------------------------------------------------------------
    public void UnosKvarova() {
		JInternalFrame pred = new aUnosKvarova();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void UnosAtesta() {
		JInternalFrame pred = new aMerniAtesti();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void Radnici() {
		JInternalFrame pred = new aRadnici();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void KarticeMasina() {
		JInternalFrame pred = new aKarticaMasine();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void mUnosPoNabavnojCeni() {
		JInternalFrame pred = new mUnosPrijem();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void mUnosPoc() {
		JInternalFrame pred = new mUnosPoc();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void mUnosIzdatnica() {
		JInternalFrame pred = new mUnosIzdat();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
    }
//--------------------------------------------------------------------------------------------
    public void KarticeDelova() {
		JInternalFrame pred = new mKartice();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//------------------------------------------------------------------------------------------------------------------
    public void LagerListe(int _kk) {
		JInternalFrame pred = new mLager(_kk);
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void PregledPrijemnica() {
		JInternalFrame pred = new aStampaPrijemnice();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void PregledIzdatnica() {
		JInternalFrame pred = new aStampaIzdatnice();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void PregledUtrosenihDelova() {
		JInternalFrame pred = new aStampaUtrdelova();
			desktop.add(pred, "");
		try { 
			pred.setVisible(true);
			pred.setSelected(true);
		} catch (java.beans.PropertyVetoException e2) {}
	}
//--------------------------------------------------------------------------------------------
    public void PrazanNalog() {
		aPrazanNalog aa = new aPrazanNalog();
		aa.setVisible(true);
			/*desktop.add(aa, "");
		try { 
			aa.setVisible(true);
			//pred.setSelected(true); 
		} catch (java.beans.PropertyVetoException e2) {}*/
	}

}// end of FinFrame class
