package sun.lwawt.macosx;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.peer.MenuBarPeer;
import sun.awt.AWTAccessor;

public final class CMenuBar extends CMenuComponent implements MenuBarPeer {
   private int nextInsertionIndex = -1;

   public CMenuBar(MenuBar var1) {
      super(var1);
   }

   long createModel() {
      return this.nativeCreateMenuBar();
   }

   public void addHelpMenu(Menu var1) {
      CMenu var2 = (CMenu)AWTAccessor.getMenuComponentAccessor().getPeer(var1);
      this.execute((var2x) -> {
         var2.execute((var3) -> {
            this.nativeSetHelpMenu(var2x, var3);
         });
      });
   }

   public int getNextInsertionIndex() {
      return this.nextInsertionIndex;
   }

   public void setNextInsertionIndex(int var1) {
      this.nextInsertionIndex = var1;
   }

   public void addMenu(Menu var1) {
   }

   public void delMenu(int var1) {
      this.execute((var2) -> {
         this.nativeDelMenu(var2, var1);
      });
   }

   private native long nativeCreateMenuBar();

   private native void nativeSetHelpMenu(long var1, long var3);

   private native void nativeDelMenu(long var1, int var3);
}
