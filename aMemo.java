import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Set.*;
import java.beans.PropertyVetoException.*;
import javax.swing.*;

public class aMemo {
	private Connection connection = null;
 public aMemo() {
 }
 //-----------------------------------------------------------------------------------------------------
   public boolean proveriDemo() {
		boolean provera=false;
		Statement statement=null;
		uzmiKonekciju();
      try {
         statement = connection.createStatement();
               String query = "SELECT COUNT(*) FROM rnal";

			try {
		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						int broj = rs.getInt("COUNT(*)");
						if (broj > 20)
						{
							provera = true;
						}
					}
			}
		     catch ( SQLException sqlex ) {
		         	JOptionPane.showMessageDialog(null, "Nema podataka o mprom");
		     }
      }
      catch ( SQLException sqlex ) {
			JOptionPane.showMessageDialog(null, "Greska u podacima - mprom");
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
  		zatvoriKonekciju();
		return provera;
  }
//--------------------------------------------------------------------------------------
   private void uzmiKonekciju(){
		ConnMySQL _dbconn = new ConnMySQL();
		if (_dbconn.getConnection() != null){
			connection = _dbconn.getConnection();
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Konekcija sa podacima nije mogu\u0107a");
		}
		return;
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(null, "Ne mo\u017ee se zatvoriti konekcija");
			}
		}
   	}
//--------------------------------------------------------------------------------------
 }//end of class fPrintKarKon
