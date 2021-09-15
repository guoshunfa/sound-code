package javax.swing;

import java.awt.Container;
import sun.awt.AppContext;

public abstract class LayoutStyle {
   public static void setInstance(LayoutStyle var0) {
      Class var1 = LayoutStyle.class;
      synchronized(LayoutStyle.class) {
         if (var0 == null) {
            AppContext.getAppContext().remove(LayoutStyle.class);
         } else {
            AppContext.getAppContext().put(LayoutStyle.class, var0);
         }

      }
   }

   public static LayoutStyle getInstance() {
      Class var1 = LayoutStyle.class;
      LayoutStyle var0;
      synchronized(LayoutStyle.class) {
         var0 = (LayoutStyle)AppContext.getAppContext().get(LayoutStyle.class);
      }

      return var0 == null ? UIManager.getLookAndFeel().getLayoutStyle() : var0;
   }

   public abstract int getPreferredGap(JComponent var1, JComponent var2, LayoutStyle.ComponentPlacement var3, int var4, Container var5);

   public abstract int getContainerGap(JComponent var1, int var2, Container var3);

   public static enum ComponentPlacement {
      RELATED,
      UNRELATED,
      INDENT;
   }
}
