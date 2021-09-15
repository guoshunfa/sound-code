package sun.swing.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class SortArrowIcon implements Icon, UIResource, Serializable {
   private static final int ARROW_HEIGHT = 5;
   private static final int X_PADDING = 7;
   private boolean ascending;
   private Color color;
   private String colorKey;

   public SortArrowIcon(boolean var1, Color var2) {
      this.ascending = var1;
      this.color = var2;
      if (var2 == null) {
         throw new IllegalArgumentException();
      }
   }

   public SortArrowIcon(boolean var1, String var2) {
      this.ascending = var1;
      this.colorKey = var2;
      if (var2 == null) {
         throw new IllegalArgumentException();
      }
   }

   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      var2.setColor(this.getColor());
      int var5 = 7 + var3 + 2;
      int var6;
      int var7;
      if (this.ascending) {
         var6 = var4;
         var2.fillRect(var5, var4, 1, 1);

         for(var7 = 1; var7 < 5; ++var7) {
            var2.fillRect(var5 - var7, var6 + var7, var7 + var7 + 1, 1);
         }
      } else {
         var6 = var4 + 5 - 1;
         var2.fillRect(var5, var6, 1, 1);

         for(var7 = 1; var7 < 5; ++var7) {
            var2.fillRect(var5 - var7, var6 - var7, var7 + var7 + 1, 1);
         }
      }

   }

   public int getIconWidth() {
      return 17;
   }

   public int getIconHeight() {
      return 7;
   }

   private Color getColor() {
      return this.color != null ? this.color : UIManager.getColor(this.colorKey);
   }
}
