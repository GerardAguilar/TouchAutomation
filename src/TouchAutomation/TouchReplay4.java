package TouchAutomation;

import java.awt.AWTException;
import java.awt.CardLayout;
//import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;
import java.sql.Timestamp;
//import java.sql.Time;
import java.util.ArrayList;
//import java.util.List;

import javax.swing.JLabel;
//import javax.swing.JComponent;
//import javax.swing.JFrame;
import javax.swing.JPanel;

//import org.json.simple.JSONObject;

//import com.sun.awt.AWTUtilities;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;
//import com.sun.jna.platform.win32.WinDef.POINT;

//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
import javafx.application.Application;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.HBox;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.input.TouchEvent;
//import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
//import javafx.util.Duration;
//import me.coley.simplejna.hook.mouse.MouseEventReceiver;
//from w w w .  j a  va  2s  .  c  o m
//import me.coley.simplejna.hook.mouse.MouseHookManager;
//import me.coley.simplejna.hook.mouse.struct.MouseButtonType;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;


/**
 * @author gaguilar
 */

public class TouchReplay4 extends Application {
  private int screenWidth = 1920;
  private int screenHeight = 1080;
//  private int clickIntervalInSeconds = 1;
  private int originalWindowProperties;
//  ArrayList<MouseEvent> mouseArray = new ArrayList<MouseEvent>();  
  ArrayList<Coordinate> coordinateArray = new ArrayList<Coordinate>();
  Window w = new Window(null);  
  Window instructions = new Window(null);
  private boolean flip = true;
  Robot testRobot;
  private boolean toggleMenu = true;
  MouseAdapter mouseAdapterInstructions;
  MouseAdapter mouseAdapterRecord;
  
  public static void main(String[] args) {
    Application.launch(args);
  }
  
  private void getOriginalWindowProperties(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
	  originalWindowProperties = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
//	  System.out.println("originalWindowProperties: " + originalWindowProperties);
  }
  
//  private static void setOriginalWindowProperties(Component w) {
//	  WinDef.HWND hwnd = getHWnd(w);
//	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, originalWindowProperties);
//  }rrrr
  private void setTransparent(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
	  int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
	  wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
  }
  private void setOpaque(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
//	  System.out.println("setOpaque: originalWindowProperties: " + originalWindowProperties);
	  int wl = originalWindowProperties;
	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
//	  System.out.println("instructions.isVisible(): " + instructions.isVisible());
  }
  
  public void click(Coordinate coordinate) throws AWTException, InterruptedException {
	  click(coordinate.getX(), coordinate.getY(), coordinate.getTimeDiff());
  }

  public void click(int xCoordinate, int yCoordinate, long timeDiff) throws AWTException, InterruptedException {
	  Robot bot = new Robot();
//	  bot.wait(timeDiff);
	  Thread.currentThread().sleep(timeDiff);
//	  wait(timeDiff);
	  move(xCoordinate, yCoordinate);
	  bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	  bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }
  public void move(Coordinate coordinate) throws AWTException, InterruptedException{
	  move(coordinate.getX(), coordinate.getY());
  }
  
  public void move(int x, int y) throws AWTException, InterruptedException {
	  Robot bot = new Robot();
	  int tries = 200;
	  Point p = new Point(x,y);
	  for (int i=0; i<tries; i++) {
		  bot.mouseMove(x, y);
		  if(p.equals(MouseInfo.getPointerInfo().getLocation()) ) {
//			  System.out.println(p.toString() + " same as " + MouseInfo.getPointerInfo().getLocation().toString());
			  break;
		  }
	  }	  
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
  
  private void cleanCoordinateArray() {
	  removeCoordinateDuplicates();
	  populateCoordinateTimingDiffs();
  }
  
  private void removeCoordinateDuplicates() {
	  for(int i=1; i<coordinateArray.size(); i++) {
		  if((coordinateArray.get(i).getTimeDifference(coordinateArray.get(i-1)))<100){
			  coordinateArray.remove(i);
		  }
	  }
  }
  
  private void populateCoordinateTimingDiffs() {
	  Coordinate previous;
	  Coordinate current;
	  long diff;
	  coordinateArray.get(0).setTimeDiff(0);
	  /**
	   * Get difference, assign to current
	   */
	  for(int i=1; i<coordinateArray.size(); i++) {
		  previous = coordinateArray.get(i-1);
		  current = coordinateArray.get(i);
		  diff = current.getTimeDifference(previous);
		  current.setTimeDiff(diff);
	  }
  }
  
  @Override
  public void start(Stage primaryStage) {
	  
	CardLayout cardLayout = new CardLayout();
	  
    JPanel recordPanel = new JPanel() {
        protected void paintComponent(Graphics g) {
        	//calling this method seems to allow the window to become transparent
        	//removing it, makes the panel opaque
        }
        public Dimension getPreferredSize() {
            return new Dimension(screenWidth, screenHeight);
        }
    }; 
    JLabel label2 = new JLabel();    
    label2.setText("test");
    label2.setFont(new Font(label2.getName(), Font.PLAIN, 80));
    recordPanel.add(label2);
    
    
    JPanel instructionPanel = new JPanel() {
        protected void paintComponent(Graphics g) {
        	//calling this method seems to allow the window to become transparent
        	//removing it, makes the panel opaque
        }
        public Dimension getPreferredSize() {
            return new Dimension(screenWidth, screenHeight);
        }
    };
    
    JLabel label = new JLabel();    
    label.setText("\"R\" to record");
    label.setFont(new Font(label.getName(), Font.PLAIN, 80));

    mouseAdapterInstructions = new MouseAdapter() {
    	@Override
    	public void mousePressed(MouseEvent e) {
    		System.out.println("click");
    		if(e.isShiftDown()) {
    			coordinateArray.clear();
    			w.dispose();
    			System.exit(0);
    		}
    	}
    };
    
    mouseAdapterRecord = new MouseAdapter() { 	    	
        @Override
        public void mousePressed(MouseEvent e) {
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        	int count = e.getClickCount();
        	//Alt + Right Click = Replay with Json
			if(e.isAltDown() && flip) {
				flip=!flip;
				ArrayList<Coordinate> coordinates = JSONSimpleWrapper.getCoordinates();
				for(int i=0; i<coordinates.size(); i=i+1) {		
					long delay = coordinates.get(i).getTimeDiff();

					//responsible for timing
					try {
						Thread.currentThread();
						Thread.sleep(delay);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					try {
						setTransparent(w);
//						sleep();
						click(coordinates.get(i));
						
						System.out.println(i);
						release();						
					} catch (AWTException | InterruptedException e1) {
						e1.printStackTrace();
					}	
				}
				setOpaque(w);
				e.consume();
			//catches duplicate
			}else if(e.isAltDown() && !flip) {
				flip=!flip;
			}
    		//Shift + Right Click = Close
    		else if(e.isShiftDown()) {
    			coordinateArray.clear();
    			w.dispose();
    			System.exit(0);
    		//Ctrl + Right Click = Save 
    		}else if(e.isControlDown()) {
    			try {
    				cleanCoordinateArray();
    				JSONSimpleWrapper.writeInteractionEvents(coordinateArray);			
				} catch (Exception e1) {
					e1.printStackTrace();
				}	
    		}	
        	else{  
        		setTransparent(w); 
    	    	try {
    	    		System.out.println("click");
    				click(e.getXOnScreen(), e.getYOnScreen(), 0);
    				Coordinate coordinate = new Coordinate(e.getXOnScreen(), e.getYOnScreen(), timestamp);    	        	
    				coordinateArray.add(coordinate);
    				for(int i=0; i<coordinateArray.size(); i++) {
//    					System.out.print(i + " ");
//    					coordinateArray.get(i).printCoordinate();
    				}
    			} catch (AWTException | InterruptedException e1) {
    				e1.printStackTrace();
    			}    	
    	    	setOpaque(w);
    	    	try {
    	    		//without this "2nd" release, mouseEvent doesn't get registered 
					release();
				} catch (AWTException e1) {
					e1.printStackTrace();
				}    	    	
    		}
    	}       	        
    };    
	GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input
	keyboardHook.addKeyListener(new GlobalKeyAdapter() {
		@Override public void keyPressed(GlobalKeyEvent event) {
//			System.out.println(event);
//			if(event.getVirtualKeyCode()==GlobalKeyEvent.VK_ESCAPE)
//				run = false;m
		}
		@Override public void keyReleased(GlobalKeyEvent event) {
			System.out.println(event); 
			int key = event.getVirtualKeyCode();
			switch(key) {
				//Maybe toggle won't be our best option
				case GlobalKeyEvent.VK_R:
					if(toggleMenu) {
						System.out.println("recordPanel");   		    
						toggleMenu = !toggleMenu;
					}else {
						System.out.println("instructionPanel");
						toggleMenu = !toggleMenu;
					}	
					break;
				//Q for Quit
				case GlobalKeyEvent.VK_Q:
	    			coordinateArray.clear();
	    			w.dispose();
	    			System.exit(0);
	    			break;		
	    		//Remove instructionPanel
				case GlobalKeyEvent.VK_A:
					instructionPanel.setVisible(false);
					w.remove(instructionPanel);
	    		    w.validate();
	    		    w.pack();
	    			break;	
	    		/**
	    		 * To re-show the panel, panel has to be setVisible, then repainted, then the window has to be validated, packed, and also repainted
	    		 */
				case GlobalKeyEvent.VK_S:		
					w.add(instructionPanel);					
					instructionPanel.addMouseListener(mouseAdapterInstructions);
					instructionPanel.setVisible(true);
					instructionPanel.repaint();
				    w.validate();
				    w.pack();
				    w.repaint();
	    			break;
	    		//TODO: there seems to be an issue with the previous panel still being present even when removed. Maybe this can be solved by using a layout as a container for the panels?
				case GlobalKeyEvent.VK_0:
					System.out.println("recordPanel");
					instructionPanel.setVisible(false);
//					w.remove(instructionPanel);
					w.removeAll();
	    		    w.validate();
	    		    w.pack();
					w.add(recordPanel);					
					recordPanel.addMouseListener(mouseAdapterRecord);
					recordPanel.setVisible(true);
					recordPanel.repaint();
				    w.validate();
				    w.pack();
				    w.repaint();		    		    
					break;
				case GlobalKeyEvent.VK_1:	
					System.out.println("instructionPanel");
					recordPanel.setVisible(false);
//					w.remove(recordPanel);
					w.removeAll();
	    		    w.validate();
	    		    w.pack();
					w.add(instructionPanel);					
					instructionPanel.addMouseListener(mouseAdapterInstructions);
					instructionPanel.setVisible(true);
					instructionPanel.repaint();
				    w.validate();
				    w.pack();
				    w.repaint();
					break;
			}			
		}
	});
    
    instructionPanel.add(label);
    instructionPanel.addMouseListener(mouseAdapterInstructions);
    recordPanel.addMouseListener(mouseAdapterRecord);
//    w.add(instructionPanel);    
    w.add(recordPanel);    
    w.validate();
    w.pack();
    w.setLocationRelativeTo(null);
    w.setVisible(true);
    w.setBackground(new Color(0.25f, 0.25f, 0.25f, 0.1f));
    w.setBounds(0,0,screenWidth, screenHeight);
//    w.setAlwaysOnTop(true);
    getOriginalWindowProperties(w);
  }


  
  
  

}
