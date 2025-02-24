// glavni program za registraciju vozila
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.nilo.plaf.nimrod.*;

public class Valdom {
	public static Font globalMenuFont = new Font("Arial", Font.PLAIN, 14);
    public static void main( String[] args ) {
		//ConnMySQL connmy = new ConnMySQL();
       try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(new com.nilo.plaf.nimrod.NimRODLookAndFeel() );
          /*
		  for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
			*/
 		}  
		catch ( UnsupportedLookAndFeelException e ) {
			System.out.println ("Metal Look & Feel not supported on this platform. \nProgram Terminated");
			System.exit(0);
		}
		/*
		catch ( IllegalAccessException e ) {
			System.out.println ("Metal Look & Feel could not be accessed. \nProgram Terminated");
			System.exit(0);
		}
		catch ( ClassNotFoundException e ) {
			System.out.println ("Metal Look & Feel could not found. \nProgram Terminated");
			System.exit(0);
		}   
		catch ( InstantiationException e ) {
			System.out.println ("Metal Look & Feel could not be instantiated. \nProgram Terminated");
			System.exit(0);
		}
		*/
		catch ( Exception e ) {
			System.out.println ("Unexpected error. \nProgram Terminated");
			e.printStackTrace();
			System.exit(0);
		}
		try{
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run() {
				makeGUI();}});
		}catch (Exception e) {}
	}
	static void makeGUI(){
		aLogin iz = new aLogin();
		iz.setModal(true);
		iz.setVisible(true);
		//AutoFrame frame = new AutoFrame();
		//frame.setVisible(true);

	}
}
