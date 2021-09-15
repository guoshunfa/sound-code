package javax.swing.plaf;

import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public abstract class PopupMenuUI extends ComponentUI {
   public boolean isPopupTrigger(MouseEvent var1) {
      return var1.isPopupTrigger();
   }

   public Popup getPopup(JPopupMenu var1, int var2, int var3) {
      PopupFactory var4 = PopupFactory.getSharedInstance();
      return var4.getPopup(var1.getInvoker(), var1, var2, var3);
   }
}
