package java.awt;

import java.awt.event.KeyEvent;

@FunctionalInterface
public interface KeyEventDispatcher {
   boolean dispatchKeyEvent(KeyEvent var1);
}
