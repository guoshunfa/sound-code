package javax.swing.text;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import sun.swing.SwingUtilities2;

public class FieldView extends PlainView {
   public FieldView(Element var1) {
      super(var1);
   }

   protected FontMetrics getFontMetrics() {
      Container var1 = this.getContainer();
      return var1.getFontMetrics(var1.getFont());
   }

   protected Shape adjustAllocation(Shape var1) {
      if (var1 != null) {
         Rectangle var2 = var1.getBounds();
         int var3 = (int)this.getPreferredSpan(1);
         int var4 = (int)this.getPreferredSpan(0);
         if (var2.height != var3) {
            int var5 = var2.height - var3;
            var2.y += var5 / 2;
            var2.height -= var5;
         }

         Container var13 = this.getContainer();
         if (var13 instanceof JTextField) {
            JTextField var6 = (JTextField)var13;
            BoundedRangeModel var7 = var6.getHorizontalVisibility();
            int var8 = Math.max(var4, var2.width);
            int var9 = var7.getValue();
            int var10 = Math.min(var8, var2.width - 1);
            if (var9 + var10 > var8) {
               var9 = var8 - var10;
            }

            var7.setRangeProperties(var9, var10, var7.getMinimum(), var8, false);
            if (var4 < var2.width) {
               int var11 = var2.width - 1 - var4;
               int var12 = ((JTextField)var13).getHorizontalAlignment();
               if (Utilities.isLeftToRight(var13)) {
                  if (var12 == 10) {
                     var12 = 2;
                  } else if (var12 == 11) {
                     var12 = 4;
                  }
               } else if (var12 == 10) {
                  var12 = 4;
               } else if (var12 == 11) {
                  var12 = 2;
               }

               switch(var12) {
               case 0:
                  var2.x += var11 / 2;
                  var2.width -= var11;
                  break;
               case 4:
                  var2.x += var11;
                  var2.width -= var11;
               }
            } else {
               var2.width = var4;
               var2.x -= var7.getValue();
            }
         }

         return var2;
      } else {
         return null;
      }
   }

   void updateVisibilityModel() {
      Container var1 = this.getContainer();
      if (var1 instanceof JTextField) {
         JTextField var2 = (JTextField)var1;
         BoundedRangeModel var3 = var2.getHorizontalVisibility();
         int var4 = (int)this.getPreferredSpan(0);
         int var5 = var3.getExtent();
         int var6 = Math.max(var4, var5);
         var5 = var5 == 0 ? var6 : var5;
         int var7 = var6 - var5;
         int var8 = var3.getValue();
         if (var8 + var5 > var6) {
            var8 = var6 - var5;
         }

         var7 = Math.max(0, Math.min(var7, var8));
         var3.setRangeProperties(var7, var5, 0, var6, false);
      }

   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = (Rectangle)var2;
      var1.clipRect(var3.x, var3.y, var3.width, var3.height);
      super.paint(var1, var2);
   }

   Shape adjustPaintRegion(Shape var1) {
      return this.adjustAllocation(var1);
   }

   public float getPreferredSpan(int var1) {
      switch(var1) {
      case 0:
         Segment var2 = SegmentCache.getSharedSegment();
         Document var3 = this.getDocument();

         int var4;
         try {
            FontMetrics var5 = this.getFontMetrics();
            var3.getText(0, var3.getLength(), var2);
            var4 = Utilities.getTabbedTextWidth(var2, var5, 0, this, 0);
            if (var2.count > 0) {
               Container var6 = this.getContainer();
               this.firstLineOffset = SwingUtilities2.getLeftSideBearing(var6 instanceof JComponent ? (JComponent)var6 : null, var5, var2.array[var2.offset]);
               this.firstLineOffset = Math.max(0, -this.firstLineOffset);
            } else {
               this.firstLineOffset = 0;
            }
         } catch (BadLocationException var7) {
            var4 = 0;
         }

         SegmentCache.releaseSharedSegment(var2);
         return (float)(var4 + this.firstLineOffset);
      default:
         return super.getPreferredSpan(var1);
      }
   }

   public int getResizeWeight(int var1) {
      return var1 == 0 ? 1 : 0;
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      return super.modelToView(var1, this.adjustAllocation(var2), var3);
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      return super.viewToModel(var1, var2, this.adjustAllocation(var3), var4);
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.insertUpdate(var1, this.adjustAllocation(var2), var3);
      this.updateVisibilityModel();
   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.removeUpdate(var1, this.adjustAllocation(var2), var3);
      this.updateVisibilityModel();
   }
}
