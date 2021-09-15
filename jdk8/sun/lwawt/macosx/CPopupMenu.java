package sun.lwawt.macosx;

import java.awt.Component;
import java.awt.Event;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.peer.PopupMenuPeer;

final class CPopupMenu extends CMenu implements PopupMenuPeer {
   CPopupMenu(PopupMenu var1) {
      super(var1);
   }

   long createModel() {
      return this.nativeCreatePopupMenu();
   }

   private native long nativeCreatePopupMenu();

   private native long nativeShowPopupMenu(long var1, int var3, int var4);

   public void show(Event var1) {
      Component var2 = (Component)var1.target;
      if (var2 != null) {
         Point var3 = var2.getLocationOnScreen();
         var1.x += var3.x;
         var1.y += var3.y;
         this.execute((var2x) -> {
            this.nativeShowPopupMenu(var2x, var1.x, var1.y);
         });
      }

   }
}
