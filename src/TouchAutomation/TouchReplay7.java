package TouchAutomation;

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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

import com.sun.awt.AWTUtilities;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;

import TouchAutomation.VlcScreenRecorder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
//import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import me.coley.simplejna.hook.mouse.MouseEventReceiver;
//from w w w .  j a  va  2s  .  c  o m
import me.coley.simplejna.hook.mouse.MouseHookManager;
import me.coley.simplejna.hook.mouse.struct.MouseButtonType;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author gaguilar
 * https://stackoverflow.com/questions/24596936/is-there-a-way-to-stop-the-robot-class-after-it-has-started
 */

public class TouchReplay7 extends Application {
  private int screenWidth = 1920;
  private int screenHeight = 1080;
  static int originalWindowProperties;
  ArrayList<Coordinate> coordinateArray = new ArrayList<Coordinate>();
  Window w = new Window(null);  
//  private boolean flip = true;
//  private boolean record = false;
  private boolean skipReplay = false;
  Robot testRobot;
  VlcScreenRecorder recorder;

  public static void main(String[] args) {
	  Application.launch(args);
  }
  
  private static void getOriginalWindowProperties(Component w) {
	  WinDef.HWND hwnd = getHWnd(w);
	  originalWindowProperties = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
  }
  
//  private static void setOriginalWindowProperties(Component w) {
//	  WinDef.HWND hwnd = getHWnd(w);
//	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, originalWindowProperties);
//  }
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
  
  public void click(Coordinate coordinate) throws AWTException, InterruptedException {
	  click(coordinate.getX(), coordinate.getY(), coordinate.getTimeDiff());
  }

  public void click(int xCoordinate, int yCoordinate, long timeDiff) throws AWTException, InterruptedException {
	  Robot bot = new Robot();
	  Thread.currentThread();
	  Thread.sleep(timeDiff);
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
  
  private void emptyCoordinateArray() {
	  coordinateArray.clear();
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
  private void saveInteraction(int id) {
	  if(coordinateArray.size()>0) {
		  cleanCoordinateArray();
		  try {
			JSONSimpleWrapper.writeInteractionEvents(coordinateArray, id);
		  } catch (Exception e) {
			e.printStackTrace();
		  }
		  emptyCoordinateArray();
	  }else {
		  System.out.println("Coordinate Array is empty.");
	  }
  }

  private void replayInteraction(int id) {
	  Thread replayThread = new Thread(new Runnable() {
		  @Override
		  public void run() {
			  w.setVisible(false);
			  Date resultdate = new Date(System.currentTimeMillis());
			  String videoName = "InteractionEvent"+id+"_"+ resultdate + "_" + System.currentTimeMillis();
			  File tmp = new File(videoName);
			  if(!tmp.exists()) {
				  try {
					tmp.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }
			  startRecording(videoName);
			  ArrayList<Coordinate> coordinates = JSONSimpleWrapper.getCoordinates("InteractionEvent"+id+".json");
			  System.out.println("coordinates.size(): " + coordinates.size());
			  for(int i=0; i<coordinates.size(); i=i+1) {	
				  System.out.println("Replay initializing");
				  if(skipReplay){}
				  else {
					  System.out.println("Replay started");
					  long delay = coordinates.get(i).getTimeDiff();	
					  //responsible for timing
					  //maybe this is why we can't interrupt
					  try {
						  Thread.currentThread();
						  Thread.sleep(delay);
					  } catch (InterruptedException e2) {
						  e2.printStackTrace();
					  }
					  //responsible for actual clicking
					  try {
						  setTransparent(w);
						  click(coordinates.get(i));
						  release();						
					  } catch (AWTException | InterruptedException e1) {
						  e1.printStackTrace();
					  }
				  }
			  }
			  setOpaque(w);
			  skipReplay = false;
			  stopRecording();
		 }
	  });
	  replayThread.start();
  	}
	  
	//http://www.experimentalqa.com/2017/11/record-selenium-test-video-in-mp4.html
	private void startRecording(String testName) {
		  recorder.startRecording(testName);
	}
	private void stopRecording() {
		  recorder.stopRecording();
	//		recorder.releaseRecordingResources();
	}
  
  
	@Override
	public void start(Stage primaryStage) {
		recorder = new VlcScreenRecorder();
		recorder.setVideoSubdirectory("\\test");
		  
	    JPanel jpanel = new JPanel() {
	        protected void paintComponent(Graphics g) {
	        	//calling this method seems to allow the window to become transparent
	        	//removing it, makes the panel opaque
	        }
	        public Dimension getPreferredSize() {
	            return new Dimension(screenWidth, screenHeight);
	        }
	    };   
	    
	    URL fileLocation = getClass().getResource("/1920x1080.png");
	    System.out.println("file: " + fileLocation.getFile());
	    System.out.println("path: " + fileLocation.getPath());
	    
	    Pane pane = new Pane();
	    
	    InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(fileLocation.getFile()));
		    Image touchImage = new Image(is);
		    TouchImage touchArea = new TouchImage(0,0,touchImage);
		    pane.getChildren().addAll(touchArea);

		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		
		
		SwingNode swingNode = new SwingNode();

		/**
		 * What I need to do is add a JavaFX node to w
		 * But w only accepts JComponents
		 */
	
	    MouseAdapter mouseAdapter = new MouseAdapter() { 	    	
	        @Override
	        public void mousePressed(MouseEvent e) {
	    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	//    		if(record) {
	        		setTransparent(w); 
	    	    	try {
	    	    		System.out.println("click");
	    				click(e.getXOnScreen(), e.getYOnScreen(), 0);
	    				Coordinate coordinate = new Coordinate(e.getXOnScreen(), e.getYOnScreen(), timestamp);    	        	
	    				coordinateArray.add(coordinate);
	    				for(int i=0; i<coordinateArray.size(); i++) {
	    					System.out.print(i + " ");
	    					coordinateArray.get(i).printCoordinate();
	    				}
	    			} catch (AWTException | InterruptedException e1) {
	    				e1.printStackTrace();
	    			}    	
	    	    	setOpaque(w);
	    	    	try {
						release();
					} catch (AWTException e1) {
						e1.printStackTrace();
					}    
	//    		}	    		
	    	}       	        
	    };    
	    GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			@Override public void keyPressed(GlobalKeyEvent event) {
				
			}
			@Override public void keyReleased(GlobalKeyEvent event) {
				System.out.println(event); 
				int key = event.getVirtualKeyCode();
				//LControl + Shift + 0 to quit
				if(event.isControlPressed() && event.isShiftPressed()) {
					if(key==GlobalKeyEvent.VK_0) {
		    			coordinateArray.clear();
		    			w.dispose();
		    			System.exit(0);
					}
				}
				//LControl + # to record
				else if(event.isControlPressed()) {
					switch(key) {
					case GlobalKeyEvent.VK_0:
						System.out.println("LCtrl + 0");
	    				saveInteraction(0);
						break;
					case GlobalKeyEvent.VK_1:
						saveInteraction(1);
						break;
					case GlobalKeyEvent.VK_2:
						saveInteraction(2);
						break;
					case GlobalKeyEvent.VK_3:
						saveInteraction(3);
						break;
					case GlobalKeyEvent.VK_4:
						saveInteraction(4);
						break;
					case GlobalKeyEvent.VK_5:
						saveInteraction(5);
						break;
					case GlobalKeyEvent.VK_6:
						saveInteraction(6);
						break;
					case GlobalKeyEvent.VK_7:
						saveInteraction(7);
						break;
					case GlobalKeyEvent.VK_8:
						saveInteraction(8);
						break;
					case GlobalKeyEvent.VK_9:
						saveInteraction(9);
						break;
					default:
						break;
					}
				//Shift + # to replay
				}else if(event.isShiftPressed()) {
					switch(key) {
					case GlobalKeyEvent.VK_0:
						System.out.println("LShift + 0");
						replayInteraction(0);
						break;
					case GlobalKeyEvent.VK_1:
						replayInteraction(1);
						break;
					case GlobalKeyEvent.VK_2:
						replayInteraction(2);
						break;
					case GlobalKeyEvent.VK_3:
						replayInteraction(3);
						break;
					case GlobalKeyEvent.VK_4:
						replayInteraction(4);
						break;
					case GlobalKeyEvent.VK_5:
						replayInteraction(5);
						break;
					case GlobalKeyEvent.VK_6:
						replayInteraction(6);
						break;
					case GlobalKeyEvent.VK_7:
						replayInteraction(7);
						break;
					case GlobalKeyEvent.VK_8:
						replayInteraction(8);
						break;
					case GlobalKeyEvent.VK_9:
						replayInteraction(9);
						break;
					default:
						break;
					}
				}
				//R to Toggle Recording
				else{
					switch(key) {				
					case GlobalKeyEvent.VK_R:
						w.setVisible(!w.isVisible());
						break;
					case GlobalKeyEvent.VK_ESCAPE:
		    			coordinateArray.clear();
		    			w.dispose();
		    			System.exit(0);
		    			break;
					default:		
						break;
					}		
				}
			}
		});
	    
	    w.addMouseListener(mouseAdapter);
	    w.add(jpanel);
	    w.pack();
	    w.setLocationRelativeTo(null);
	    w.setVisible(true);
	    w.setBackground(new Color(0.25f, 0.5f, 0.5f, 0.1f));
	    w.setBounds(0,0,screenWidth, screenHeight);
	    w.setAlwaysOnTop(true);    
	    getOriginalWindowProperties(w);
  }
  

}
