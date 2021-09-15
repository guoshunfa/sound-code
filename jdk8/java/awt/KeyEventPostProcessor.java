package java.awt;

import java.awt.event.KeyEvent;

@FunctionalInterface
public interface KeyEventPostProcessor {
   boolean postProcessKeyEvent(KeyEvent var1);
}
