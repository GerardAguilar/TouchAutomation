package TouchAutomation;

import java.util.Map.Entry;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;


//from https://github.com/kristian/system-hook
public class KbExample {
	private static boolean run = true;
	public static void main(String[] args) {
		// might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails 
		GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input

		System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");
		for(Entry<Long,String> keyboard:GlobalKeyboardHook.listKeyboards().entrySet())
			System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());
		
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			@Override public void keyPressed(GlobalKeyEvent event) {
				System.out.println(event);
				if(event.getVirtualKeyCode()==GlobalKeyEvent.VK_ESCAPE)
					run = false;
			}
			@Override public void keyReleased(GlobalKeyEvent event) {
				System.out.println(event); }
		});
		
		try {
			while(run) Thread.sleep(128);
		} catch(InterruptedException e) { /* nothing to do here */ }
		  finally { keyboardHook.shutdownHook(); }
	}
}
