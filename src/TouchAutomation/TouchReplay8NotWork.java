package TouchAutomation;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.awt.AWTException;
import java.awt.BorderLayout;
//import java.awt.Color;
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
//import java.awt.event.MouseEvent;
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
import org.w3c.dom.events.EventTarget;

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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
//import javafx.event.EventTarget;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
//import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import me.coley.simplejna.hook.mouse.MouseEventReceiver;
import me.coley.simplejna.hook.mouse.MouseHookManager;
import me.coley.simplejna.hook.mouse.struct.MouseButtonType;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author gaguilar
 * Instead of relying on java.awt.Window for transparency and click-throughs I can make all of it JavaFX
 */

public class TouchReplay8NotWork extends Application {
  private int screenWidth = 1920;
  private int screenHeight = 1080;
  static int originalWindowProperties;
  ArrayList<Coordinate> coordinateArray = new ArrayList<Coordinate>();
//  Window w = new Window(null);  
//  private boolean flip = true;
//  private boolean record = false;
  private boolean skipReplay = false;
  Robot testRobot;
  VlcScreenRecorder recorder;
  boolean flip;

  public static void main(String[] args) {
	  Application.launch(args);
  }
  
//  private static void getOriginalWindowProperties(Component w) {
//	  WinDef.HWND hwnd = getHWnd(w);
//	  originalWindowProperties = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
//  }
//  
//  private static void setTransparent(Component w) {
//	  WinDef.HWND hwnd = getHWnd(w);
//	  int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
//	  wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
//	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
//  }
//  private static void setOpaque(Component w) {
//	  WinDef.HWND hwnd = getHWnd(w);
//	  int wl = originalWindowProperties;
//	  User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
//  }
  
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

  private void replayInteraction(int id, Stage primaryStage) {
	  Thread replayThread = new Thread(new Runnable() {
		  @Override
		  public void run() {
//			  w.setVisible(false);
			  primaryStage.hide();
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
//						  setTransparent(w);
						  primaryStage.hide();
						  click(coordinates.get(i));
						  release();						
					  } catch (AWTException | InterruptedException e1) {
						  e1.printStackTrace();
					  }
				  }
			  }
//			  setOpaque(w);
			  primaryStage.show();
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
		
		Pane root = new Pane();
		
		
		//Creating a line object 
		Line line = new Line(); 
         
		//Setting the properties to a line 
		line.setStartX(100.0); 
		line.setStartY(150.0); 
		line.setEndX(500.0); 
		line.setEndY(150.0); 
		
		Rectangle rect = new Rectangle();
		rect.setHeight(1080);
		rect.setWidth(1920);
		rect.setStroke(Color.TRANSPARENT);
		rect.setFill(Color.TRANSPARENT);
//		rect.setOnTouchPressed(new EventHandler<TouchEvent>() {
//			@Override public void handle(TouchEvent event) {
//				System.out.println("Touch Event: " + event.toString());
//			}
//		});
		
//		try {
//			Robot robot2 = new Robot();
//			
//		} catch (AWTException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		/**
		 * Robot doesn't have touch support
		 * Is there another way to echo our touch input?
		 */
		
//		root.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
//		    mouseEvent.consume();
//		    Event touchEvent = new TouchEvent(null, null, null, screenHeight, false, false, false, false);
//		    root.fireEvent(touchEvent);
//		});
		
//		root.setOnTouchPressed(new EventHandler<TouchEvent>() {
//			@Override public void handle(TouchEvent event) {
//				System.out.println("Touch Event: " + event.toString());
//				
//				if(!flip) {
//					flip = !flip;
//					primaryStage.hide();
//					root.fireEvent(event);
//					System.out.println("Touch");
//					primaryStage.show();					
//				}else {//absorbs the duplicate
//					flip = !flip;
//				}			
//			}
//		});
		
		/**
		 * Since I don't have a touchscreen, I can try artificially creating a TouchEvent using the MouseEvent
		 * The problem is that this technique would be of specialized use, and I'm trying to create a generalized way to interact with touchscreens
		 */
		
//		Scene scene = new Scene(root, 1920, 1080);
		
		flip = false;
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent event) {
				System.out.println("Mouse Event: " + event.toString());
//				if(!flip) {
					flip = !flip;
					primaryStage.hide();
					TouchPoint touchPoint = new TouchPoint(0, TouchPoint.State.PRESSED, event.getX(), event.getY(), screenWidth, screenHeight, rect, null);
					ArrayList<TouchPoint> touchList = new ArrayList<TouchPoint>();
					touchList.add(touchPoint);					
				    Event touchEvent = new TouchEvent(TouchEvent.TOUCH_PRESSED, touchPoint, touchList, 0, false, false, false, false);
				    System.out.println("Touch Event: " + touchEvent.toString());
					root.fireEvent(touchEvent);
					TouchPoint releasePoint = new TouchPoint(0, TouchPoint.State.RELEASED, event.getX(), event.getY(), screenWidth, screenHeight, rect, null);	
					ArrayList<TouchPoint> releaseList = new ArrayList<TouchPoint>();					
					releaseList.add(releasePoint);
					Event releaseEvent = new TouchEvent(TouchEvent.TOUCH_RELEASED, releasePoint, releaseList, 0, false, false, false, false);
					System.out.println("Release Event: " + releaseEvent.toString());
					root.fireEvent(releaseEvent);
					
					primaryStage.show();
//				}else {//absorbs the duplicate
//					flip = !flip;
//				}	
			}
		});	
		
		root.setOnTouchPressed(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				System.out.println("Touch Pressed");
			}
		});
		
		root.setOnTouchReleased(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				System.out.println("Touch Released");
			}
		});
		
//		flip = false;
//		root.setOnTouchPressed(new EventHandler<TouchEvent>() {
//			@Override
//			public void handle(TouchEvent event) {
//				if(!flip) {
//					flip = !flip;
//					primaryStage.hide();
//					TouchPoint touchPoint = new TouchPoint(0, TouchPoint.State.PRESSED, event.getTouchPoint().getX(), event.getTouchPoint().getY(), screenWidth, screenHeight, rect, null);		
//					TouchPoint releasePoint = new TouchPoint(0, TouchPoint.State.RELEASED, event.getTouchPoint().getX(), event.getTouchPoint().getY(), screenWidth, screenHeight, rect, null);		 
//					ArrayList<TouchPoint> touchList = new ArrayList<TouchPoint>();
//					touchList.add(touchPoint);
//					touchList.add(releasePoint);
//				    Event touchEvent = new TouchEvent(TouchEvent.TOUCH_PRESSED, touchPoint, touchList, 0, false, false, false, true);
//				    System.out.println("Touch Event: " + touchEvent.toString());
//					root.fireEvent(touchEvent);
//					primaryStage.show();
//				}else {//absorbs the duplicate
//					flip = !flip;
//				}
//			}			
//		});
		
		//TODO: The window was placed on focus, but then the succeeding presses don't register.
		
		ObservableList<Node> list = root.getChildren();
		list.add(rect);	
	
		Scene scene = new Scene(root, 1920, 1080);
		System.out.println("Scene: " + scene.toString());
//		scene.setFill(Color.TRANSPARENT);
		
		Color color = new Color(0,0,0,0.1f);
		scene.setFill(color);
		primaryStage.setScene(scene);
		
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
		primaryStage.show();		
	    
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
		    			Platform.exit();
		    			System.exit(0);
					}
				}
				//LControl + # to record
				else if(event.isControlPressed()) {
					switch(key) {
					case GlobalKeyEvent.VK_0:
						System.out.println("LCtrl + 0");
						Platform.runLater(() -> saveInteraction(0));
						break;
					case GlobalKeyEvent.VK_1:
						Platform.runLater(() -> saveInteraction(1));
						break;
					case GlobalKeyEvent.VK_2:
						Platform.runLater(() -> saveInteraction(2));
						break;
					case GlobalKeyEvent.VK_3:
						Platform.runLater(() -> saveInteraction(3));
						break;
					case GlobalKeyEvent.VK_4:
						Platform.runLater(() -> saveInteraction(4));
						break;
					case GlobalKeyEvent.VK_5:
						Platform.runLater(() -> saveInteraction(5));
						break;
					case GlobalKeyEvent.VK_6:
						Platform.runLater(() -> saveInteraction(6));
						break;
					case GlobalKeyEvent.VK_7:
						Platform.runLater(() -> saveInteraction(7));
						break;
					case GlobalKeyEvent.VK_8:
						Platform.runLater(() -> saveInteraction(8));
						break;
					case GlobalKeyEvent.VK_9:
						Platform.runLater(() -> saveInteraction(9));
						break;
					default:
						break;
					}
				//Shift + # to replay
				}else if(event.isShiftPressed()) {
					switch(key) {
					case GlobalKeyEvent.VK_0:
						System.out.println("LShift + 0");
						Platform.runLater(() -> replayInteraction(0, primaryStage));
						break;
					case GlobalKeyEvent.VK_1:
						Platform.runLater(() -> replayInteraction(1, primaryStage));
						break;
					case GlobalKeyEvent.VK_2:
						Platform.runLater(() -> replayInteraction(2, primaryStage));
						break;
					case GlobalKeyEvent.VK_3:
						Platform.runLater(() -> replayInteraction(3, primaryStage));
						break;
					case GlobalKeyEvent.VK_4:
						Platform.runLater(() -> replayInteraction(4, primaryStage));
						break;
					case GlobalKeyEvent.VK_5:
						Platform.runLater(() -> replayInteraction(5, primaryStage));
						break;
					case GlobalKeyEvent.VK_6:
						Platform.runLater(() -> replayInteraction(6, primaryStage));
						break;
					case GlobalKeyEvent.VK_7:
						Platform.runLater(() -> replayInteraction(7, primaryStage));
						break;
					case GlobalKeyEvent.VK_8:
						Platform.runLater(() -> replayInteraction(8, primaryStage));
						break;
					case GlobalKeyEvent.VK_9:
						Platform.runLater(() -> replayInteraction(9, primaryStage));
						break;
					default:
						break;
					}
				}
				//R to Toggle Recording
				else{
					switch(key) {				
					case GlobalKeyEvent.VK_R:
//						w.setVisible(!w.isVisible());
						if(primaryStage.isShowing()) {				
							System.out.println("Turning primaryStage off");
							Platform.runLater(() -> primaryStage.hide());							
						}else if(!primaryStage.isShowing()) {//Switching the primary stage back on is still not working
							Platform.runLater(() -> primaryStage.setScene(scene));					
							Platform.runLater(() -> primaryStage.initStyle(StageStyle.TRANSPARENT));
							Platform.runLater(() -> primaryStage.show());
							System.out.println("Turning primaryStage back on");
							if(primaryStage.isShowing()) {
								System.out.println("Primary Stage should now be showing");
							}
							System.out.println(primaryStage.getScene().toString());
//							Platform.runLater(() -> System.out.println(primaryStage.getScene().toString()));
						}
						break;
					case GlobalKeyEvent.VK_ESCAPE:
		    			coordinateArray.clear();
//		    			w.dispose();
		    			Platform.exit();
		    			System.exit(0);
		    			break;
					default:		
						break;
					}		
				}
			}
		});
	   
//	    getOriginalWindowProperties(w);
		primaryStage.setAlwaysOnTop(true);
  }
  

}
