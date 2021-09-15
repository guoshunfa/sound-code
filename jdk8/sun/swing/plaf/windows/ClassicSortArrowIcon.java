package sun.swing.plaf.windows;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class ClassicSortArrowIcon implements Icon, UIResource, Serializable {
   private static final int X_OFFSET = 9;
   private boolean ascending;

   public ClassicSortArrowIcon(boolean var1) {
      this.ascending = var1;
   }

   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      var3 += 9;
      if (this.ascending) {
         var2.setColor(UIManager.getColor("Table.sortIconHighlight"));
         this.drawSide(var2, var3 + 3, var4, -1);
         var2.setColor(UIManager.getColor("Table.sortIconLight"));
         this.drawSide(var2, var3 + 4, var4, 1);
         var2.fillRect(var3 + 1, var4 + 6, 6, 1);
      } else {
         var2.setColor(UIManager.getColor("Table.sortIconHighlight"));
         this.drawSide(var2, var3 + 3, var4 + 6, -1);
         var2.fillRect(var3 + 1, var4, 6, 1);
         var2.setColor(UIManager.getColor("Table.sortIconLight"));
         this.drawSide(var2, var3 + 4, var4 + 6, 1);
      }

   }

   private void drawSide(Graphics var1, int var2, int var3, int var4) {
      byte var5 = 2;
      if (this.ascending) {
         var1.fillRect(var2, var3, 1, 2);
         ++var3;
      } else {
         --var3;
         var1.fillRect(var2, var3, 1, 2);
         var5 = -2;
         var3 -= 2;
      }

      var2 += var4;

      for(int var6 = 0; var6 < 2; ++var6) {
         var1.fillRect(var2, var3, 1, 3);
         var2 += var4;
         var3 += var5;
      }

      if (!this.ascending) {
         ++var3;
      }

      var1.fillRect(var2, var3, 1, 2);
   }

   public int getIconWidth() {
      return 17;
   }

   public int getIconHeight() {
      return 9;
   }
}
