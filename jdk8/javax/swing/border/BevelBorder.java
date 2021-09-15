package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class BevelBorder extends AbstractBorder {
   public static final int RAISED = 0;
   public static final int LOWERED = 1;
   protected int bevelType;
   protected Color highlightOuter;
   protected Color highlightInner;
   protected Color shadowInner;
   protected Color shadowOuter;

   public BevelBorder(int var1) {
      this.bevelType = var1;
   }

   public BevelBorder(int var1, Color var2, Color var3) {
      this(var1, var2.brighter(), var2, var3, var3.brighter());
   }

   @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
   public BevelBorder(int var1, Color var2, Color var3, Color var4, Color var5) {
      this(var1);
      this.highlightOuter = var2;
      this.highlightInner = var3;
      this.shadowOuter = var4;
      this.shadowInner = var5;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (this.bevelType == 0) {
         this.paintRaisedBevel(var1, var2, var3, var4, var5, var6);
      } else if (this.bevelType == 1) {
         this.paintLoweredBevel(var1, var2, var3, var4, var5, var6);
      }

   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.set(2, 2, 2, 2);
      return var2;
   }

   public Color getHighlightOuterColor(Component var1) {
      Color var2 = this.getHighlightOuterColor();
      return var2 != null ? var2 : var1.getBackground().brighter().brighter();
   }

   public Color getHighlightInnerColor(Component var1) {
      Color var2 = this.getHighlightInnerColor();
      return var2 != null ? var2 : var1.getBackground().brighter();
   }

   public Color getShadowInnerColor(Component var1) {
      Color var2 = this.getShadowInnerColor();
      return var2 != null ? var2 : var1.getBackground().darker();
   }

   public Color getShadowOuterColor(Component var1) {
      Color var2 = this.getShadowOuterColor();
      return var2 != null ? var2 : var1.getBackground().darker().darker();
   }

   public Color getHighlightOuterColor() {
      return this.highlightOuter;
   }

   public Color getHighlightInnerColor() {
      return this.highlightInner;
   }

   public Color getShadowInnerColor() {
      return this.shadowInner;
   }

   public Color getShadowOuterColor() {
      return this.shadowOuter;
   }

   public int getBevelType() {
      return this.bevelType;
   }

   public boolean isBorderOpaque() {
      return true;
   }

   protected void paintRaisedBevel(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Color var7 = var2.getColor();
      var2.translate(var3, var4);
      var2.setColor(this.getHighlightOuterColor(var1));
      var2.drawLine(0, 0, 0, var6 - 2);
      var2.drawLine(1, 0, var5 - 2, 0);
      var2.setColor(this.getHighlightInnerColor(var1));
      var2.drawLine(1, 1, 1, var6 - 3);
      var2.drawLine(2, 1, var5 - 3, 1);
      var2.setColor(this.getShadowOuterColor(var1));
      var2.drawLine(0, var6 - 1, var5 - 1, var6 - 1);
      var2.drawLine(var5 - 1, 0, var5 - 1, var6 - 2);
      var2.setColor(this.getShadowInnerColor(var1));
      var2.drawLine(1, var6 - 2, var5 - 2, var6 - 2);
      var2.drawLine(var5 - 2, 1, var5 - 2, var6 - 3);
      var2.translate(-var3, -var4);
      var2.setColor(var7);
   }

   protected void paintLoweredBevel(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Color var7 = var2.getColor();
      var2.translate(var3, var4);
      var2.setColor(this.getShadowInnerColor(var1));
      var2.drawLine(0, 0, 0, var6 - 1);
      var2.drawLine(1, 0, var5 - 1, 0);
      var2.setColor(this.getShadowOuterColor(var1));
      var2.drawLine(1, 1, 1, var6 - 2);
      var2.drawLine(2, 1, var5 - 2, 1);
      var2.setColor(this.getHighlightOuterColor(var1));
      var2.drawLine(1, var6 - 1, var5 - 1, var6 - 1);
      var2.drawLine(var5 - 1, 1, var5 - 1, var6 - 2);
      var2.setColor(this.getHighlightInnerColor(var1));
      var2.drawLine(2, var6 - 2, var5 - 2, var6 - 2);
      var2.drawLine(var5 - 2, 2, var5 - 2, var6 - 3);
      var2.translate(-var3, -var4);
      var2.setColor(var7);
   }
}
