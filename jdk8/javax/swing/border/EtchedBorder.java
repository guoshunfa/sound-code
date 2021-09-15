package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class EtchedBorder extends AbstractBorder {
   public static final int RAISED = 0;
   public static final int LOWERED = 1;
   protected int etchType;
   protected Color highlight;
   protected Color shadow;

   public EtchedBorder() {
      this(1);
   }

   public EtchedBorder(int var1) {
      this(var1, (Color)null, (Color)null);
   }

   public EtchedBorder(Color var1, Color var2) {
      this(1, var1, var2);
   }

   @ConstructorProperties({"etchType", "highlightColor", "shadowColor"})
   public EtchedBorder(int var1, Color var2, Color var3) {
      this.etchType = var1;
      this.highlight = var2;
      this.shadow = var3;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var2.translate(var3, var4);
      var2.setColor(this.etchType == 1 ? this.getShadowColor(var1) : this.getHighlightColor(var1));
      var2.drawRect(0, 0, var5 - 2, var6 - 2);
      var2.setColor(this.etchType == 1 ? this.getHighlightColor(var1) : this.getShadowColor(var1));
      var2.drawLine(1, var6 - 3, 1, 1);
      var2.drawLine(1, 1, var5 - 3, 1);
      var2.drawLine(0, var6 - 1, var5 - 1, var6 - 1);
      var2.drawLine(var5 - 1, var6 - 1, var5 - 1, 0);
      var2.translate(-var3, -var4);
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.set(2, 2, 2, 2);
      return var2;
   }

   public boolean isBorderOpaque() {
      return true;
   }

   public int getEtchType() {
      return this.etchType;
   }

   public Color getHighlightColor(Component var1) {
      return this.highlight != null ? this.highlight : var1.getBackground().brighter();
   }

   public Color getHighlightColor() {
      return this.highlight;
   }

   public Color getShadowColor(Component var1) {
      return this.shadow != null ? this.shadow : var1.getBackground().darker();
   }

   public Color getShadowColor() {
      return this.shadow;
   }
}
