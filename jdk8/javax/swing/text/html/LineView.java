package javax.swing.text.html;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

class LineView extends ParagraphView {
   int tabBase;

   public LineView(Element var1) {
      super(var1);
   }

   public boolean isVisible() {
      return true;
   }

   public float getMinimumSpan(int var1) {
      return this.getPreferredSpan(var1);
   }

   public int getResizeWeight(int var1) {
      switch(var1) {
      case 0:
         return 1;
      case 1:
         return 0;
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public float getAlignment(int var1) {
      return var1 == 0 ? 0.0F : super.getAlignment(var1);
   }

   protected void layout(int var1, int var2) {
      super.layout(2147483646, var2);
   }

   public float nextTabStop(float var1, int var2) {
      return this.getTabSet() == null && StyleConstants.getAlignment(this.getAttributes()) == 0 ? this.getPreTab(var1, var2) : super.nextTabStop(var1, var2);
   }

   protected float getPreTab(float var1, int var2) {
      Document var3 = this.getDocument();
      View var4 = this.getViewAtPosition(var2, (Rectangle)null);
      if (var3 instanceof StyledDocument && var4 != null) {
         Font var5 = ((StyledDocument)var3).getFont(var4.getAttributes());
         Container var6 = this.getContainer();
         FontMetrics var7 = var6 != null ? var6.getFontMetrics(var5) : Toolkit.getDefaultToolkit().getFontMetrics(var5);
         int var8 = this.getCharactersPerTab() * var7.charWidth('W');
         int var9 = (int)this.getTabBase();
         return (float)((((int)var1 - var9) / var8 + 1) * var8 + var9);
      } else {
         return 10.0F + var1;
      }
   }

   protected int getCharactersPerTab() {
      return 8;
   }
}
