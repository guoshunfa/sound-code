package javax.swing.event;

import java.util.EventListener;

public interface MenuListener extends EventListener {
   void menuSelected(MenuEvent var1);

   void menuDeselected(MenuEvent var1);

   void menuCanceled(MenuEvent var1);
}
