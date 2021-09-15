package javax.swing.event;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class MenuKeyEvent extends KeyEvent {
   private MenuElement[] path;
   private MenuSelectionManager manager;

   public MenuKeyEvent(Component var1, int var2, long var3, int var5, int var6, char var7, MenuElement[] var8, MenuSelectionManager var9) {
      super(var1, var2, var3, var5, var6, var7);
      this.path = var8;
      this.manager = var9;
   }

   public MenuElement[] getPath() {
      return this.path;
   }

   public MenuSelectionManager getMenuSelectionManager() {
      return this.manager;
   }
}
