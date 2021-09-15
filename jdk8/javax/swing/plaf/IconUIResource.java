package javax.swing.plaf;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;

public class IconUIResource implements Icon, UIResource, Serializable {
   private Icon delegate;

   public IconUIResource(Icon var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null delegate icon argument");
      } else {
         this.delegate = var1;
      }
   }

   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      this.delegate.paintIcon(var1, var2, var3, var4);
   }

   public int getIconWidth() {
      return this.delegate.getIconWidth();
   }

   public int getIconHeight() {
      return this.delegate.getIconHeight();
   }
}
