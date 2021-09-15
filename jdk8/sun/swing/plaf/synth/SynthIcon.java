package sun.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.plaf.synth.SynthContext;

public abstract class SynthIcon implements Icon {
   public static int getIconWidth(Icon var0, SynthContext var1) {
      if (var0 == null) {
         return 0;
      } else {
         return var0 instanceof SynthIcon ? ((SynthIcon)var0).getIconWidth(var1) : var0.getIconWidth();
      }
   }

   public static int getIconHeight(Icon var0, SynthContext var1) {
      if (var0 == null) {
         return 0;
      } else {
         return var0 instanceof SynthIcon ? ((SynthIcon)var0).getIconHeight(var1) : var0.getIconHeight();
      }
   }

   public static void paintIcon(Icon var0, SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var0 instanceof SynthIcon) {
         ((SynthIcon)var0).paintIcon(var1, var2, var3, var4, var5, var6);
      } else if (var0 != null) {
         var0.paintIcon(var1.getComponent(), var2, var3, var4);
      }

   }

   public abstract void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6);

   public abstract int getIconWidth(SynthContext var1);

   public abstract int getIconHeight(SynthContext var1);

   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      this.paintIcon((SynthContext)null, var2, var3, var4, 0, 0);
   }

   public int getIconWidth() {
      return this.getIconWidth((SynthContext)null);
   }

   public int getIconHeight() {
      return this.getIconHeight((SynthContext)null);
   }
}
