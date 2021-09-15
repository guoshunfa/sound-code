package javax.swing.plaf.basic;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import javax.swing.plaf.UIResource;
import sun.swing.MenuItemLayoutHelper;

public class DefaultMenuLayout extends BoxLayout implements UIResource {
   public DefaultMenuLayout(Container var1, int var2) {
      super(var1, var2);
   }

   public Dimension preferredLayoutSize(Container var1) {
      if (var1 instanceof JPopupMenu) {
         JPopupMenu var2 = (JPopupMenu)var1;
         MenuItemLayoutHelper.clearUsedClientProperties(var2);
         if (var2.getComponentCount() == 0) {
            return new Dimension(0, 0);
         }
      }

      super.invalidateLayout(var1);
      return super.preferredLayoutSize(var1);
   }
}
