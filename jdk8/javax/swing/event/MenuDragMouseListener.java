package javax.swing.event;

import java.util.EventListener;

public interface MenuDragMouseListener extends EventListener {
   void menuDragMouseEntered(MenuDragMouseEvent var1);

   void menuDragMouseExited(MenuDragMouseEvent var1);

   void menuDragMouseDragged(MenuDragMouseEvent var1);

   void menuDragMouseReleased(MenuDragMouseEvent var1);
}
