package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

public interface Border {
   void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6);

   Insets getBorderInsets(Component var1);

   boolean isBorderOpaque();
}
