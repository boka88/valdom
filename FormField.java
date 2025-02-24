import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

public class FormField extends JFormattedTextField  implements java.io.Serializable{
	private String strColor="";
	//public Color color = new Color.yellow;
	public FormField(MaskFormatter mf){
		super(mf);
		//strColor = ConnMySQL.kolor;
		//color = konvertujBoju(strColor);
		addFocusListener(new FL());
		setFormatter(mf);
		//isEditable(true);
	}

		public Color konvertujBoju(String boja) {
			Color col= Color.white;
			int i = 0,brojac=1;
			String strR="",strG="",strB="",strBoja="";
			int R=0,G=0,B=0;
		  try{
			for (i=0;i<boja.length() ;i++ )
			{
				strBoja = boja.substring(i,i+1);
				if (strBoja.equals(","))
				{
					brojac = brojac + 1;
				}else{
					if (brojac == 1)
					{
						strR = strR + strBoja;
					}else if (brojac == 2)
					{
						strG = strG + strBoja;
					}else if (brojac == 3)
					{
						strB = strB + strBoja;
					}
				}
			}
			col = new Color(Integer.parseInt(strR),
							Integer.parseInt(strG),
							Integer.parseInt(strB));
		  }catch(Exception e){
			  JOptionPane.showMessageDialog(null, "Greska u FormField "+e);
		  }
			return col;
		}
	class FL implements FocusListener {
		public void focusGained(FocusEvent e) {
			setBackground(Color.yellow);
			repaint();
		}
		public void focusLost(FocusEvent e) {
			setBackground(Color.white);
			repaint();
		}
	}

}
