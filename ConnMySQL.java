import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;
import java.lang.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xerces.parsers.SAXParser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.io.File;
 
public class ConnMySQL
{
	public static String akrobat;
	private Connection connection;
	private String host;
	private String port;
	private String user;
	private String dbPort = "3306";
	private FileReader parametri;
	public static String serverName;
	public static String dbName;
	public static String uName;
	public static String pName;
	public static String urlll;
	public static String imebaze;
	public static String drugaBaza,nazivprodavnice="";
	public static String putanjabekap="";
	public static int bekapdane=0;
	private static Properties prop = null;
	public JPanel demo;
	
	public ConnMySQL(){
		procitajParametre();

			imebaze = dbName.substring(0,4) + aLogin.godina;		
         	String url = "jdbc:mysql://" + serverName + "/" + imebaze;
		try 
		{
        	//ucitavanje drajvera za mysql
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			try 
			{
         			connection = DriverManager.getConnection( url,uName,pName );
			}
	        catch (SQLException ex)
			{
					JOptionPane.showMessageDialog(demo,"Konekcija se ne moze otvoriti");
	        }
        }
        catch (Exception exception)
		{
			exception.printStackTrace();
        }
	}
//----------------------------------------------------------
	private void procitajParametre(){
		InputStream input = null;
 		File ff = new File("valdom.properties");
		try {
 
			input = new FileInputStream(ff);
 
			// load a properties file
		    prop = new Properties();
			prop.load(input);
			input.close();
			loadParamsStatus();
		} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Ne postoji fajl valdom.properties program se zatvara");
				System.exit(0);
		} catch (IOException e2) {
				JOptionPane.showMessageDialog(null, "Ne moze da ucita valdom.properties program se zatvara");		
				System.exit(0);
		}
	}
//----------------------------------------------------------
	private void loadParamsStatus(){

		serverName = prop.getProperty("server").trim();
		dbName = prop.getProperty("baza").trim();
		dbPort = prop.getProperty("port").trim();
		uName = prop.getProperty("user").trim();
		pName = prop.getProperty("password").trim();

		try{
			bekapdane = Integer.parseInt(prop.getProperty("bekapdane").trim());
		}catch(Exception ee){
			bekapdane = 0;
		}
		try{
			putanjabekap = prop.getProperty("putanjabekap").trim();
		}catch(Exception ee){
			putanjabekap = "";
		}
	
	}
//-----------------------------------------------------------	
	public void close()
	{
		try 
		{
			connection.close();
		}
	        catch (SQLException exception) 
		{
		}
	}
//-----------------------------------------------------------	
	public Connection getConnection()
	{
		return connection;
	}

	public String getHost()
	{
		return host;
	}
	public String getPort()
	{
		return port;
	}

	public String getUser()
	{
		return user;
	}
	
//-----------------------------------------------------------	
	public boolean reloadServer()
	{
		boolean resultat;
		
		try 
		{
                	Statement reloadServerStatement = connection.createStatement();
			ResultSet reloadServerResultSet = reloadServerStatement.executeQuery("FLUSH HOSTS, LOGS, PRIVILEGES, TABLES, STATUS");
			resultat = true;
           	}
           	catch (SQLException exception) 
		{
			switch(exception.getErrorCode())
			{
				case 1045:
					JOptionPane.showMessageDialog(demo,"Neuspeo rekonekt");
					break;
					
				default:
					break;
			}
			
			resultat = false;
		}
		
		return resultat;
	}
}//end of class ConnMySQL=================================
