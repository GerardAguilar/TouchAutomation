import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.awt.AWTUtilities;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
//import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import me.coley.simplejna.hook.mouse.MouseEventReceiver;
//from w w w .  j a  va  2s  .  c  o m
import me.coley.simplejna.hook.mouse.MouseHookManager;
import me.coley.simplejna.hook.mouse.struct.MouseButtonType;

/**
 * @author gaguilar
 * Very basic click recorder (1 second between clicks)
 ** Click anywhere
 ** Press Alt + Left Click to replay
 ** Press Shift + Left Click to finish
 ** Press Ctrl + Left Click to 
 * 
 */

public class TouchReplay extends Application {
  private int screenWidth = 1920;
  private int screenHeight = 1080;
  private int clickIntervalInSeconds = 1;
  static int originalWindowProperties;
  ArrayList<MouseEvent> mouseArray = new ArrayList<MouseEvent>();  
  ArrayList<Coordinate> coordinateArray = new ArrayList<Coordinate>();
  Window w = new Window(null);  
  
  public static void main(String[] args) {
    Application.launch(args);
  }
  
  private static void getOriginalWindowProperties(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
	  originalWindowProperties = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
  }
  
  private static void setOriginalWindowProperties(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, originalWindowProperties);
  }
  private static void setTransparent(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
	  int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
	  wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
  }
  private static void setOpaque(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
	  int wl = originalWindowProperties;
	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
  }
  
  public void move(MouseEvent e) throws AWTException{
	  Robot bot = new Robot();
	  int tries = 200;
	  Point p = e.getPoint();
	  for (int i=0; i<tries; i++) {
		  bot.mouseMove(e.getXOnScreen(), e.getYOnScreen());
		  if(p.equals(MouseInfo.getPointerInfo().getLocation()) ) {
//			  System.out.println(p.toString() + " same as " + MouseInfo.getPointerInfo().getLocation().toString());
			  break;
		  }
	  }	  
  }
  
  public void click(int xCoordinate, int yCoordinate) throws AWTException {
	  Robot bot = new Robot();
	  bot.mouseMove(xCoordinate, yCoordinate);
	  bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	  bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }
  
  public void click(MouseEvent e) throws AWTException {
	  Window w = (Window) e.getSource();	 
	  System.out.println(w.getBounds().toString() + " | " + w.getX() + ":" +  w.getY());
	  Robot bot = new Robot();
	  bot.mouseMove(e.getXOnScreen(), e.getYOnScreen());	  
	  try {
		  Thread.sleep(clickIntervalInSeconds * 1000);
	  }catch (InterruptedException e1) {
		// TODO Auto-generated catch block
		  e1.printStackTrace();
	  }
	  bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	  bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }
  
  public void release() throws AWTException{
	  Robot bot = new Robot();
	  bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }
	
  /**
   * Get the window handle from the OS
   */
  private static HWND getHWnd(Component w) {
	  HWND hwnd = new HWND();
	  hwnd.setPointer(Native.getComponentPointer(w));
	  return hwnd;
  }

@Override
  public void start(Stage primaryStage) {
    JPanel jpanel = new JPanel() {
        protected void paintComponent(Graphics g) {
        	//calling this method seems to allow the window to become transparent
        	//removing it, makes the panel opaque
        }
        public Dimension getPreferredSize() {
            return new Dimension(screenWidth, screenHeight);
        }
    };   
    
    MouseAdapter mouseAdapter = new MouseAdapter() { 	    	
        @Override
        public void mousePressed(MouseEvent e) {
        	Coordinate coordinate = new Coordinate(e.getXOnScreen(), e.getYOnScreen());
        	coordinateArray.add(coordinate);
        	//Alt + Right Click = Replay
        	if(e.isAltDown()) {				
    			for(int i=0; i<mouseArray.size(); i=i+2) {
    				try {
    					setTransparent(w);
    					move(mouseArray.get(i));
    					click(mouseArray.get(i));
    					setOpaque(w);
    				} catch (AWTException e1) {
						e1.printStackTrace();
					}
    			}	
    		//Shift + Right Click = Close
    		}else if(e.isShiftDown()) {
    			System.out.println("Coordinates");
    			for (int i=0; i<coordinateArray.size(); i++) {
    				coordinateArray.get(i).printCoordinate();
    			}
    			coordinateArray.clear();
    			w.dispose();
    			System.exit(0);
    		//Ctrl + Right Click = Clear memory 
    		}else if(e.isControlDown()) {
    			mouseArray.clear();		
    			e.consume();
    		}		
        	else {     
        		setTransparent(w); 
    	    	try {
    				click(e.getXOnScreen(), e.getYOnScreen());
    				Window w = (Window) e.getSource();	 
//    				System.out.println("Mouse pressed: " + e.getXOnScreen() + ", " + e.getYOnScreen());//these need to be stored and then replayed
    				mouseArray.add(e);
    			} catch (AWTException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}    	
    	    	setOpaque(w);
    	    	try {
					release();
				} catch (AWTException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
        }
    };    
    w.addMouseListener(mouseAdapter);
    w.add(jpanel);
    w.pack();
    w.setLocationRelativeTo(null);
    w.setVisible(true);
    w.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.1f));
    w.setBounds(0,0,screenWidth, screenHeight);
    w.setAlwaysOnTop(true);    
    getOriginalWindowProperties(w);    
  }

}

class Coordinate{
	int x;
	int y;
	public Coordinate(int newX, int newY) {
		setX(newX);
		setY(newY);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int newX) {
		x = newX;
	}
	public int getY() {
		return y;
	}
	public void setY(int newY) {
		y = newY; 
	}
	public void printCoordinate() {
		System.out.println("Coordinate: " + getX() + ", " + getY());
	}
}