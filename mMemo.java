//program za proveru demo verzije
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Set.*;
import java.beans.PropertyVetoException.*;
import javax.swing.*;

public class mMemo {
	private Connection connection = null;
 public mMemo() {
 }
 //-----------------------------------------------------------------------------------------------------
   public boolean proveriDemo() {
		boolean provera=false;
		Statement statement=null;
		uzmiKonekciju();
      try {
         statement = connection.createStatement();
               String query = "SELECT COUNT(*) FROM mprom";

			try {
		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						int broj = rs.getInt("COUNT(*)");
						if (broj > 10000)
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
  		//zatvoriKonekciju();
		return provera;
  }
//--------------------------------------------------------------------------------------
   public void uzmiKonekciju(){
		try{
			connection = aLogin.konekcija;
		}catch (Exception f) {
			JOptionPane.showMessageDialog(null, "Ne mo\u017ee preuzeti konekciju:"+f);
		}
    }
//--------------------------------------------------------------------------------------
   private void zatvoriKonekciju(){
		/*
		if (connection != null){
			try {	connection.close(); } 
			catch (Exception f) {
				JOptionPane.showMessageDialog(null, "Ne mo\u017ee se zatvoriti konekcija");
			}
		}
		*/
   	}
//--------------------------------------------------------------------------------------
 }//end of class fPrintKarKon
