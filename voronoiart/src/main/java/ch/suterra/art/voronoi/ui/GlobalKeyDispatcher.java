package ch.suterra.art.voronoi.ui;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by yannick on 09.02.17.
 */
public class GlobalKeyDispatcher implements KeyEventDispatcher {
	public static void initialize() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher( new GlobalKeyDispatcher() );
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(e.getID() == KeyEvent.KEY_RELEASED) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
		}

		//Allow the event to be redispatched
		return false;
	}
}
