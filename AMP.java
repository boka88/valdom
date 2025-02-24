
import javax.swing.*;
import javax.swing.border.*;
import javax.accessibility.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.awt.font.*;
import java.awt.geom.*;

public class AMP extends JPanel implements Runnable {

  private Graphics og = null;
  private Image offscreen = null;
  private int height = 230;
  private int width = 575;
  private int x = 0;
  private int y = 0;
  private Thread animator = null;
  private Graphics g = null;
  //private Graphics2D g2 = null;
  private boolean started = false; 
  private String[] messageQue = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" }; // size is 5
  private Font font = new Font("Arial", Font.PLAIN, 11);
  private boolean currentlyScrolling = false;
  private void setCurrentlyScrolling(boolean currentlyScrolling) { this.currentlyScrolling = currentlyScrolling; }
  private boolean isCurrentlyScrolling() { return currentlyScrolling; }
  private String[] messageWaitingQue = { "", "", "", "", "", "", "", "", "", "", "", "", "", "" }; // also size 5, its for if a message arrives while we are currently scrolling
    
  private static AMP instance = new AMP();

  public static AMP getInstance() {
    if (instance == null) {
       instance = new AMP();
    }
    return instance;
  } 

  public AMP() {
  //init();
    setWidthAndHeight();
  }

  public void init() {
    offscreen = createImage(width, height);
    og = offscreen.getGraphics();
    //g2 = (Graphics2D)og;
  }

  public void setWidthAndHeight() {
    setPreferredSize(new Dimension(width, height));
    setMinimumSize(new Dimension(width, height));
    setMaximumSize(new Dimension(width, height));
  }
  
  public void setWidthAndHeight(int width, int height) {
     this.width = width;
     this.height = height;
     setWidthAndHeight();
  }

  
  public void update(Graphics g) {
    if((offscreen != null) && (og != null))
      paint(g);
  }

  public void paint(Graphics g) {
    if((offscreen != null) && (og != null)) {
      g.drawImage(offscreen, 0, 0, null);
    }
  }
  
  public void run() {
    boolean setup = true;
    while(setup) {
      try { animator.sleep(1000); } catch(Exception e) { }
      if((offscreen != null) && (og != null)) {
        paintBG();
        printMessages();
        repaint();
        setup = false;
      }
      else {
        offscreen = createImage(width, height);
        if(offscreen != null) {
          og = offscreen.getGraphics();
          System.out.println("\n\nget og object: " + og);
          printMessages();
        }
      }
      //while(true) {
        //just loop to check for resize of component
      //  try { animator.sleep(2000); } catch(Exception e) { }
      //  if(!currentlyScrolling) {
      //    repaint();  
      //  }
      //}
      
    }
  }
  
  private void paintBG() {
    og.setColor(Color.white);
    og.fillRect(0, 0, getWidth(), getHeight());
    //og.setColor(Color.black);
  }
  
  public void start() {
    if(started) return;
    animator = new Thread(this);
    try { animator.sleep(1500); } catch(Exception e) { }
    animator.start();
    started = true;
  }
  
  public void alertNewMessage(String message) {
    addToMessageQue(message);
  }
  
  public void printMessages() {
    
      try {
          Runnable runner = new Runnable () {
            public void run () {
          
              AMP.getInstance().setCurrentlyScrolling(true);
   Graphics2D g2 = (Graphics2D)og;
              
   int linecount = 1;
   StringTokenizer st1 = new StringTokenizer(messageQue[0]);
     String text1 = "";
     String testtext1 = "";
     String prodtext1 = "";
     while(st1.hasMoreTokens()) {
       text1 = st1.nextToken();
       testtext1 += text1 + " ";
       FontRenderContext frc1 = g2.getFontRenderContext();                 
       TextLayout t11 = new TextLayout(testtext1, font, frc1);
       int sw1 = (int) t11.getBounds().getWidth();
       if(sw1 > (getWidth() - 40)) {
        linecount++;
        testtext1 = "";
        prodtext1 = text1;
       }
       else prodtext1 += text1 + " ";
     }
     
              
            
  for (int k = -(15)*(linecount-1); k <= 15; k++) {
    paintBG();
      int y = k;
      og.setColor(Color.black);
      for(int j = 0; j < messageQue.length; j++) {
        if(messageQue[j].length() != 0) {
        StringTokenizer st = new StringTokenizer(messageQue[j]);
        String text = "";
        String testtext = "";
        String prodtext = "";
        while(st.hasMoreTokens()) {
          text = st.nextToken();
          testtext += text + " ";
            FontRenderContext frc = g2.getFontRenderContext();                 
            TextLayout t1 = new TextLayout(testtext, font, frc);
            int sw = (int) t1.getBounds().getWidth();
            if(sw > (getWidth() - 40)) {
            og.drawString(prodtext, 10, y);
            y += 12;
            testtext = "";
            prodtext = text;
            }
            else prodtext += text + " ";
          }
        og.drawString(prodtext, 10, y);
          y += 18;
          if(y > getHeight()) break;
        }
      }
      repaint();
      try { Thread.sleep(50); } catch(Exception de) { }
  }
   AMP.getInstance().setCurrentlyScrolling(false);
   AMP.getInstance().checkForMessagesWaiting();
    
            }
        };
        new Thread (runner, "printMessage.run").start ();
      }
      catch ( Exception e) { }
  }
  
  private void addToMessageQue(String message) {
  if(isCurrentlyScrolling()) putMessageInWaitingQue(message);
  else {
      //first move all messages down one then add then new message to the top
      for(int i = (messageQue.length - 2); i >= 0 ; i--)  
        messageQue[i+1] = messageQue[i]; 
      messageQue[0] = message; 
      printMessages();
  }
  }
  
  private void putMessageInWaitingQue(String message) {
  for(int i = 0; i < messageWaitingQue.length; i++) {
    if(messageWaitingQue[i].length() == 0) { //nothing there, so it's open
      messageWaitingQue[i] = message;
        break;
    }
  }
  }
  
  private boolean messageQueEmpty() {
    return  (messageWaitingQue[0].length() == 0);
  }
  
  private String getNextMessageInWaitingQue() {
  String returnStr = messageWaitingQue[0];
  adjustMessageWaitingQue();
  return returnStr;
  }
  
  private void adjustMessageWaitingQue() {
  for(int i = 0; i < (messageWaitingQue.length - 1); i++) 
    messageWaitingQue[i] = messageWaitingQue[i + 1];
    messageWaitingQue[(messageWaitingQue.length - 1)] = "";
  }
  
  private void checkForMessagesWaiting() {
    if(!messageQueEmpty()) {
    addToMessageQue(getNextMessageInWaitingQue());
  }
  }

  public void clearMessageQues() {
    for(int i = 0; i < messageQue.length; i++) messageQue[i] = ""; 
    for(int j = 0; j < messageWaitingQue.length; j++) messageWaitingQue[j] = "";
    printMessages();
  }

}
