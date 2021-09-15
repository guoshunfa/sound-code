package javax.swing.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public abstract class ComponentUI {
   public void installUI(JComponent var1) {
   }

   public void uninstallUI(JComponent var1) {
   }

   public void paint(Graphics var1, JComponent var2) {
   }

   public void update(Graphics var1, JComponent var2) {
      if (var2.isOpaque()) {
         var1.setColor(var2.getBackground());
         var1.fillRect(0, 0, var2.getWidth(), var2.getHeight());
      }

      this.paint(var1, var2);
   }

   public Dimension getPreferredSize(JComponent var1) {
      return null;
   }

   public Dimension getMinimumSize(JComponent var1) {
      return this.getPreferredSize(var1);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return this.getPreferredSize(var1);
   }

   public boolean contains(JComponent var1, int var2, int var3) {
      return var1.inside(var2, var3);
   }

   public static ComponentUI createUI(JComponent var0) {
      throw new Error("ComponentUI.createUI not implemented.");
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("Component must be non-null");
      } else if (var2 >= 0 && var3 >= 0) {
         return -1;
      } else {
         throw new IllegalArgumentException("Width and height must be >= 0");
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      if (var1 == null) {
         throw new NullPointerException("Component must be non-null");
      } else {
         return Component.BaselineResizeBehavior.OTHER;
      }
   }

   public int getAccessibleChildrenCount(JComponent var1) {
      return SwingUtilities.getAccessibleChildrenCount(var1);
   }

   public Accessible getAccessibleChild(JComponent var1, int var2) {
      return SwingUtilities.getAccessibleChild(var1, var2);
   }
}
