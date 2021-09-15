package javax.swing.event;

import java.util.EventListener;

public interface PopupMenuListener extends EventListener {
   void popupMenuWillBecomeVisible(PopupMenuEvent var1);

   void popupMenuWillBecomeInvisible(PopupMenuEvent var1);

   void popupMenuCanceled(PopupMenuEvent var1);
}
