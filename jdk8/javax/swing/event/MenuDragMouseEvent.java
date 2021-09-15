package javax.swing.event;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class MenuDragMouseEvent extends MouseEvent {
   private MenuElement[] path;
   private MenuSelectionManager manager;

   public MenuDragMouseEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, boolean var9, MenuElement[] var10, MenuSelectionManager var11) {
      super(var1, var2, var3, var5, var6, var7, var8, var9);
      this.path = var10;
      this.manager = var11;
   }

   public MenuDragMouseEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, MenuElement[] var12, MenuSelectionManager var13) {
      super(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11, 0);
      this.path = var12;
      this.manager = var13;
   }

   public MenuElement[] getPath() {
      return this.path;
   }

   public MenuSelectionManager getMenuSelectionManager() {
      return this.manager;
   }
}
