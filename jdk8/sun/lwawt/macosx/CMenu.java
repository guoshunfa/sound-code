package sun.lwawt.macosx;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;

public class CMenu extends CMenuItem implements MenuPeer {
   public CMenu(Menu var1) {
      super(var1);
   }

   protected final void initialize(MenuItem var1) {
      this.setLabel(var1.getLabel());
      this.setEnabled(var1.isEnabled());
   }

   public final void setEnabled(boolean var1) {
      super.setEnabled(var1);
      Menu var2 = (Menu)this.getTarget();
      int var3 = var2.getItemCount();

      for(int var4 = 0; var4 < var3; ++var4) {
         MenuItem var5 = var2.getItem(var4);
         MenuItemPeer var6 = (MenuItemPeer)LWCToolkit.targetToPeer(var5);
         if (var6 != null) {
            var6.setEnabled(var1 && var5.isEnabled());
         }
      }

   }

   long createModel() {
      CMenuComponent var1 = (CMenuComponent)LWCToolkit.targetToPeer(this.getTarget().getParent());
      if (var1 instanceof CMenu) {
         return var1.executeGet(this::nativeCreateSubMenu);
      } else if (var1 instanceof CMenuBar) {
         MenuBar var2 = (MenuBar)this.getTarget().getParent();
         boolean var3 = var2.getHelpMenu() == this.getTarget();
         int var4 = ((CMenuBar)var1).getNextInsertionIndex();
         return var1.executeGet((var3x) -> {
            return this.nativeCreateMenu(var3x, var3, var4);
         });
      } else {
         throw new InternalError("Parent must be CMenu or CMenuBar");
      }
   }

   public final void addItem(MenuItem var1) {
   }

   public final void delItem(int var1) {
      this.execute((var2) -> {
         this.nativeDeleteItem(var2, var1);
      });
   }

   public final void setLabel(String var1) {
      this.execute((var2) -> {
         this.nativeSetMenuTitle(var2, var1);
      });
      super.setLabel(var1);
   }

   public final void addSeparator() {
      this.execute(this::nativeAddSeparator);
   }

   public final long getNativeMenu() {
      return this.executeGet(this::nativeGetNSMenu);
   }

   private native long nativeCreateMenu(long var1, boolean var3, int var4);

   private native long nativeCreateSubMenu(long var1);

   private native void nativeSetMenuTitle(long var1, String var3);

   private native void nativeAddSeparator(long var1);

   private native void nativeDeleteItem(long var1, int var3);

   private native long nativeGetNSMenu(long var1);
}
