package javax.swing.event;

import java.util.EventListener;

public interface MenuKeyListener extends EventListener {
   void menuKeyTyped(MenuKeyEvent var1);

   void menuKeyPressed(MenuKeyEvent var1);

   void menuKeyReleased(MenuKeyEvent var1);
}
