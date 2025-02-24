//klasa za razdvajanje PDV - a
import java.io.*;
import java.sql.*;
import java.sql.ResultSet.*;
import java.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.util.Set.*;
import javax.swing.*;
import java.io.*;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.File;
import java.lang.*;
import javax.swing.table.*;
import java.text.*;



public class classBekap {
	private static java.io.FileWriter output;
	private String []cmds={"cmd.exe", "/c" ,"start","bekap.bat" };
	private	String strdate="",dan="";
	private	String baza = "";	   
	private	String korisnik = "";	   
	private	String sifra = "";	   
	private	String putanja = "";
	private	String server = "";
	private Connection connect=null;
	private String pattern = "#########0.00";
	private DecimalFormat myFormatter = new DecimalFormat(pattern);
	private int proc=10;
	private Runtime rt;
	private Process pp;

 public classBekap(Connection _connect) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		Date date = calendar.getTime();

		int day = calendar.get(Calendar.DATE);

		//tekuci mesec mora se dodati 1
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		strdate = date.toString();
		dan = strdate.substring(0,3);
		baza = ConnMySQL.dbName;	   
		korisnik = ConnMySQL.uName;	   
		sifra = ConnMySQL.pName;	   
		putanja = ConnMySQL.putanjabekap;
		server = ConnMySQL.serverName;
		connect = _connect;

 }
//-----------------------------------------------------------------------------------------------------
 public boolean uradiBekap() {
		boolean uspelo = true;
		proc=10;

	//provera da li putanja postoji
	File ff = new File(putanja);
	if (ff.exists() && ff.isDirectory())
	{
		
		//================================================================================
		int selection = JOptionPane.showOptionDialog(null,
			"Da li radite bekap podataka","Confirm",JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,null,
			new String[]{"Yes","No"},"Yes");
		if (selection == JOptionPane.YES_OPTION)
		{
				//kreiranje .bat fajla za izvrsenje bekapa baze
				String batFilePath = "./bekap.bat";
				try (FileWriter output = new FileWriter(batFilePath)) {
					// Kreiranje .bat fajla
					output.write("mysqldump -h"+server+" -u"+korisnik+" -p"+sifra+" "+baza+">"+
					putanja+dan+"_"+ baza+".sql \n");
					output.write("exit\n");
					output.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Greška u kreiranju .bat fajla: " + e.getMessage());
					return false;
				}			
				
				//izvrsavanje .bat fajla za bekap baze
				try{
					rt = Runtime.getRuntime();
					pp = rt.exec(cmds);
					pp.waitFor();
					proveriBekapDump(putanja+dan+"_"+ baza+".sql");		

				}catch (InterruptedException ee){
					JOptionPane.showMessageDialog(null, "Gre\u0161ka InterruptedException"+ee);
					uspelo = false;
				}catch (IOException e){
					JOptionPane.showMessageDialog(null, "Greska u izvrsenju bekapa"+e);
					uspelo = false;
				}
				// Brisanje privremenog .bat fajla
				File batFile = new File(batFilePath);
				if (batFile.exists() && !batFile.delete()) {
					JOptionPane.showMessageDialog(null, "Nije moguce obrisati privremeni .bat fajl.");
				}
		}

	}else{
		JOptionPane.showMessageDialog(null, "Ne postoji putanja za bekap proverite agen.properties");	
	}
		
	return uspelo;
}
//-----------------------------------------------------------------------------------------------------
 private void proveriBekapDump(String filePath) {
    File dumpFile = new File(filePath);
	JOptionPane.showMessageDialog(null,"Bekap je na putanji:"+ filePath);
    // Proverite da li fajl postoji
    if (!dumpFile.exists()) {
        JOptionPane.showMessageDialog(null, "Dump fajl nije pronaden! Proverite da li je bekap uspešan.");
        return;
    }

    // Proverite velicinu fajla (nakon male pauze ako je potrebno)
    try {
        Thread.sleep(2000); // Opcionalna pauza (2 sekundi)
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    long fileSize = dumpFile.length(); // Velicina fajla u bajtovima
    if (fileSize == 0) {
        JOptionPane.showMessageDialog(null, "Dump fajl je prazan. Bekap nije uspešan.");
    } else {
        JOptionPane.showMessageDialog(null, "Bekap je uspešno završen. Velicina bekap fajla: " + fileSize + " bajtova.");
    }
}
//-----------------------------------------------------------------------------------------------------
  public void napraviSQL(Connection connect) {
	
	Statement statement = null;
	String izlaz = new String("");
	String s="",sos="";
	StringBuilder sb = new StringBuilder();
	BufferedWriter buff = null;
	FileWriter fw = null;
	File file = null;
	Object obj = null;

  //provera da li putanja za bekap postoji
  File ff = new File(putanja);
  if (ff.exists() && ff.isDirectory())
  {
	
	//================================================================================
	int selection = JOptionPane.showOptionDialog(null,
		"Da li radite bekap podataka","Confirm",JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,null,
		new String[]{"Yes","No"},"Yes");
	String outfile = putanja+dan+"_txt_"+ baza+".sql";

	if (selection == JOptionPane.YES_OPTION)
	{
		try {
			OutputStreamWriter ous = new OutputStreamWriter(new FileOutputStream(outfile),"UTF-8");
	
			buff = new BufferedWriter(ous);
		}catch(IOException eee){
			JOptionPane.showMessageDialog(null, "Problem sa kreiranjem fajla:"+eee);	
		}

		try{
		
		ResultSet rs = query("SHOW FULL TABLES WHERE Table_type != 'VIEW'");

            while (rs.next()) {
                String tbl = rs.getString(1);
 
                sb.append("\n");
                sb.append("-- ----------------------------\n")
                        .append("-- Table structure for  ").append(tbl)
                        .append(" \n-- ----------------------------\n");
                sb.append("DROP TABLE IF EXISTS  ").append(tbl).append(" ;\n");

                ResultSet rs2 = query("SHOW CREATE TABLE  " + tbl + " ");
                rs2.next();
                String crt = rs2.getString(2) + ";";
                sb.append(crt).append("\n");
                sb.append("\n");
                sb.append("-- ----------------------------\n").append("-- Records for  ").append(tbl)
					.append(" \n-- ----------------------------\n");
 
                ResultSet rss = query("SELECT * FROM " + tbl);
                while (rss.next()) {
                    int colCount = rss.getMetaData().getColumnCount();
                    if (colCount > 0) {
                        sb.append("INSERT INTO ").append(tbl).append(" VALUES(");
 
                        for (int i = 0; i < colCount; i++) {                             if (i > 0) {
                                sb.append(",");
                            }
                                s = "";
								sos = "";
								obj = null;
                            try {
								obj = rss.getObject(i + 1);
								if (obj instanceof Integer)
								{
									s = rss.getString(i + 1);
								}else if (obj instanceof Double)
								{
									s = myFormatter.format(rss.getDouble(i + 1));
								}else{
									byte[] bytes = rss.getBytes(i + 1);
									s = new String(bytes,"UTF-8");
									s = s.replace("\""," ");
									s = s.replace("'","\\'"); 
									s = s.replace("&"," ");

									s = "'" + s.trim() + "'";
								}
                            } catch (Exception e) {
                                s = "NULL";
								sos = "NULL";
                            }
                            sb.append(s);
                        }
                        sb.append(");\n");
                        buff.append(sb.toString());
                        sb = new StringBuilder();
                    }
                }
            }

			ResultSet rs4 = query("SHOW FULL TABLES WHERE Table_type = 'VIEW'");
            while (rs4.next()) {
                String tbl = rs4.getString(1);
 
                sb.append("\n");
                sb.append("-- ----------------------------\n")
                        .append("-- View structure for  ").append(tbl)
                        .append(" \n-- ----------------------------\n");
                sb.append("DROP VIEW IF EXISTS  ").append(tbl).append(" ;\n");

                ResultSet rs3 = query("SHOW CREATE VIEW  " + tbl + " ");
                rs3.next();
                String crt = rs3.getString(2) + ";";
                sb.append(crt).append("\n");
            }
 
            buff.flush();
            buff.close();
				
			//ispitivanje duzine fajla
			File fff = new File(putanja+dan+"_txt_"+ baza+".sql");
			long duzinaubajtovima = fff.length();
			long duzinaukb = duzinaubajtovima/1024;
			//ako je duzina manja od 100k verovatno baza ima neku neispravnu tabelu
			if (duzinaukb<100)
			{
				JOptionPane.showMessageDialog(null, "Proverite bekap izgleda da nije u redu");	
			}


        } catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Greska u query:"+e);
        }
	}//end if selection
   }else{
		JOptionPane.showMessageDialog(null, "Ne postoji putanja za bekap proverite agen.properties");	
   }//end if provera putanje

}
//-----------------------------------------------------------------------------------------------------
  public ResultSet query(String sql) {
		ResultSet rsss = null;
		Statement statement = null;
		try {
			statement = connect.createStatement();
			rsss = statement.executeQuery(sql);
		}
		catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska u query:"+sqlex);
		}
		return rsss;
  }
//--------------------------------------------------------------------------------------
 }//end of class
