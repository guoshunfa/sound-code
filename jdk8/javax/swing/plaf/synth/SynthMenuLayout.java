package javax.swing.plaf.synth;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JPopupMenu;
import javax.swing.plaf.basic.DefaultMenuLayout;

class SynthMenuLayout extends DefaultMenuLayout {
   public SynthMenuLayout(Container var1, int var2) {
      super(var1, var2);
   }

   public Dimension preferredLayoutSize(Container var1) {
      if (var1 instanceof JPopupMenu) {
         JPopupMenu var2 = (JPopupMenu)var1;
         var2.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, (Object)null);
      }

      return super.preferredLayoutSize(var1);
   }
}
