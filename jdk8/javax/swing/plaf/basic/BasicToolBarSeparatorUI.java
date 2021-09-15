package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

public class BasicToolBarSeparatorUI extends BasicSeparatorUI {
   public static ComponentUI createUI(JComponent var0) {
      return new BasicToolBarSeparatorUI();
   }

   protected void installDefaults(JSeparator var1) {
      Dimension var2 = ((JToolBar.Separator)var1).getSeparatorSize();
      if (var2 == null || var2 instanceof UIResource) {
         JToolBar.Separator var3 = (JToolBar.Separator)var1;
         var2 = (Dimension)((Dimension)UIManager.get("ToolBar.separatorSize"));
         if (var2 != null) {
            if (var3.getOrientation() == 0) {
               var2 = new Dimension(var2.height, var2.width);
            }

            var3.setSeparatorSize(var2);
         }
      }

   }

   public void paint(Graphics var1, JComponent var2) {
   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = ((JToolBar.Separator)var1).getSeparatorSize();
      return var2 != null ? var2.getSize() : null;
   }
}
