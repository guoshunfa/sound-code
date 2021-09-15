package javax.swing;

import java.awt.Component;
import java.awt.Graphics;

public interface Icon {
   void paintIcon(Component var1, Graphics var2, int var3, int var4);

   int getIconWidth();

   int getIconHeight();
}
