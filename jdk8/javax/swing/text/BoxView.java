package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;

public class BoxView extends CompositeView {
   int majorAxis;
   int majorSpan;
   int minorSpan;
   boolean majorReqValid;
   boolean minorReqValid;
   SizeRequirements majorRequest;
   SizeRequirements minorRequest;
   boolean majorAllocValid;
   int[] majorOffsets;
   int[] majorSpans;
   boolean minorAllocValid;
   int[] minorOffsets;
   int[] minorSpans;
   Rectangle tempRect = new Rectangle();

   public BoxView(Element var1, int var2) {
      super(var1);
      this.majorAxis = var2;
      this.majorOffsets = new int[0];
      this.majorSpans = new int[0];
      this.majorReqValid = false;
      this.majorAllocValid = false;
      this.minorOffsets = new int[0];
      this.minorSpans = new int[0];
      this.minorReqValid = false;
      this.minorAllocValid = false;
   }

   public int getAxis() {
      return this.majorAxis;
   }

   public void setAxis(int var1) {
      boolean var2 = var1 != this.majorAxis;
      this.majorAxis = var1;
      if (var2) {
         this.preferenceChanged((View)null, true, true);
      }

   }

   public void layoutChanged(int var1) {
      if (var1 == this.majorAxis) {
         this.majorAllocValid = false;
      } else {
         this.minorAllocValid = false;
      }

   }

   protected boolean isLayoutValid(int var1) {
      return var1 == this.majorAxis ? this.majorAllocValid : this.minorAllocValid;
   }

   protected void paintChild(Graphics var1, Rectangle var2, int var3) {
      View var4 = this.getView(var3);
      var4.paint(var1, var2);
   }

   public void replace(int var1, int var2, View[] var3) {
      super.replace(var1, var2, var3);
      int var4 = var3 != null ? var3.length : 0;
      this.majorOffsets = this.updateLayoutArray(this.majorOffsets, var1, var4);
      this.majorSpans = this.updateLayoutArray(this.majorSpans, var1, var4);
      this.majorReqValid = false;
      this.majorAllocValid = false;
      this.minorOffsets = this.updateLayoutArray(this.minorOffsets, var1, var4);
      this.minorSpans = this.updateLayoutArray(this.minorSpans, var1, var4);
      this.minorReqValid = false;
      this.minorAllocValid = false;
   }

   int[] updateLayoutArray(int[] var1, int var2, int var3) {
      int var4 = this.getViewCount();
      int[] var5 = new int[var4];
      System.arraycopy(var1, 0, var5, 0, var2);
      System.arraycopy(var1, var2, var5, var2 + var3, var4 - var3 - var2);
      return var5;
   }

   protected void forwardUpdate(DocumentEvent.ElementChange var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
      boolean var5 = this.isLayoutValid(this.majorAxis);
      super.forwardUpdate(var1, var2, var3, var4);
      if (var5 && !this.isLayoutValid(this.majorAxis)) {
         Container var6 = this.getContainer();
         if (var3 != null && var6 != null) {
            int var7 = var2.getOffset();
            int var8 = this.getViewIndexAtPosition(var7);
            Rectangle var9 = this.getInsideAllocation(var3);
            if (this.majorAxis == 0) {
               var9.x += this.majorOffsets[var8];
               var9.width -= this.majorOffsets[var8];
            } else {
               var9.y += this.minorOffsets[var8];
               var9.height -= this.minorOffsets[var8];
            }

            var6.repaint(var9.x, var9.y, var9.width, var9.height);
         }
      }

   }

   public void preferenceChanged(View var1, boolean var2, boolean var3) {
      boolean var4 = this.majorAxis == 0 ? var2 : var3;
      boolean var5 = this.majorAxis == 0 ? var3 : var2;
      if (var4) {
         this.majorReqValid = false;
         this.majorAllocValid = false;
      }

      if (var5) {
         this.minorReqValid = false;
         this.minorAllocValid = false;
      }

      super.preferenceChanged(var1, var2, var3);
   }

   public int getResizeWeight(int var1) {
      this.checkRequests(var1);
      if (var1 == this.majorAxis) {
         if (this.majorRequest.preferred != this.majorRequest.minimum || this.majorRequest.preferred != this.majorRequest.maximum) {
            return 1;
         }
      } else if (this.minorRequest.preferred != this.minorRequest.minimum || this.minorRequest.preferred != this.minorRequest.maximum) {
         return 1;
      }

      return 0;
   }

   void setSpanOnAxis(int var1, float var2) {
      if (var1 == this.majorAxis) {
         if (this.majorSpan != (int)var2) {
            this.majorAllocValid = false;
         }

         if (!this.majorAllocValid) {
            this.majorSpan = (int)var2;
            this.checkRequests(this.majorAxis);
            this.layoutMajorAxis(this.majorSpan, var1, this.majorOffsets, this.majorSpans);
            this.majorAllocValid = true;
            this.updateChildSizes();
         }
      } else {
         if ((int)var2 != this.minorSpan) {
            this.minorAllocValid = false;
         }

         if (!this.minorAllocValid) {
            this.minorSpan = (int)var2;
            this.checkRequests(var1);
            this.layoutMinorAxis(this.minorSpan, var1, this.minorOffsets, this.minorSpans);
            this.minorAllocValid = true;
            this.updateChildSizes();
         }
      }

   }

   void updateChildSizes() {
      int var1 = this.getViewCount();
      int var2;
      View var3;
      if (this.majorAxis == 0) {
         for(var2 = 0; var2 < var1; ++var2) {
            var3 = this.getView(var2);
            var3.setSize((float)this.majorSpans[var2], (float)this.minorSpans[var2]);
         }
      } else {
         for(var2 = 0; var2 < var1; ++var2) {
            var3 = this.getView(var2);
            var3.setSize((float)this.minorSpans[var2], (float)this.majorSpans[var2]);
         }
      }

   }

   float getSpanOnAxis(int var1) {
      return var1 == this.majorAxis ? (float)this.majorSpan : (float)this.minorSpan;
   }

   public void setSize(float var1, float var2) {
      this.layout(Math.max(0, (int)(var1 - (float)this.getLeftInset() - (float)this.getRightInset())), Math.max(0, (int)(var2 - (float)this.getTopInset() - (float)this.getBottomInset())));
   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
      int var4 = this.getViewCount();
      int var5 = var3.x + this.getLeftInset();
      int var6 = var3.y + this.getTopInset();
      Rectangle var7 = var1.getClipBounds();

      for(int var8 = 0; var8 < var4; ++var8) {
         this.tempRect.x = var5 + this.getOffset(0, var8);
         this.tempRect.y = var6 + this.getOffset(1, var8);
         this.tempRect.width = this.getSpan(0, var8);
         this.tempRect.height = this.getSpan(1, var8);
         int var9 = this.tempRect.x;
         int var10 = var9 + this.tempRect.width;
         int var11 = this.tempRect.y;
         int var12 = var11 + this.tempRect.height;
         int var13 = var7.x;
         int var14 = var13 + var7.width;
         int var15 = var7.y;
         int var16 = var15 + var7.height;
         if (var10 >= var13 && var12 >= var15 && var14 >= var9 && var16 >= var11) {
            this.paintChild(var1, this.tempRect, var8);
         }
      }

   }

   public Shape getChildAllocation(int var1, Shape var2) {
      if (var2 != null) {
         Shape var3 = super.getChildAllocation(var1, var2);
         if (var3 != null && !this.isAllocationValid()) {
            Rectangle var4 = var3 instanceof Rectangle ? (Rectangle)var3 : var3.getBounds();
            if (var4.width == 0 && var4.height == 0) {
               return null;
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      if (!this.isAllocationValid()) {
         Rectangle var4 = var2.getBounds();
         this.setSize((float)var4.width, (float)var4.height);
      }

      return super.modelToView(var1, var2, var3);
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      if (!this.isAllocationValid()) {
         Rectangle var5 = var3.getBounds();
         this.setSize((float)var5.width, (float)var5.height);
      }

      return super.viewToModel(var1, var2, var3, var4);
   }

   public float getAlignment(int var1) {
      this.checkRequests(var1);
      return var1 == this.majorAxis ? this.majorRequest.alignment : this.minorRequest.alignment;
   }

   public float getPreferredSpan(int var1) {
      this.checkRequests(var1);
      float var2 = var1 == 0 ? (float)(this.getLeftInset() + this.getRightInset()) : (float)(this.getTopInset() + this.getBottomInset());
      return var1 == this.majorAxis ? (float)this.majorRequest.preferred + var2 : (float)this.minorRequest.preferred + var2;
   }

   public float getMinimumSpan(int var1) {
      this.checkRequests(var1);
      float var2 = var1 == 0 ? (float)(this.getLeftInset() + this.getRightInset()) : (float)(this.getTopInset() + this.getBottomInset());
      return var1 == this.majorAxis ? (float)this.majorRequest.minimum + var2 : (float)this.minorRequest.minimum + var2;
   }

   public float getMaximumSpan(int var1) {
      this.checkRequests(var1);
      float var2 = var1 == 0 ? (float)(this.getLeftInset() + this.getRightInset()) : (float)(this.getTopInset() + this.getBottomInset());
      return var1 == this.majorAxis ? (float)this.majorRequest.maximum + var2 : (float)this.minorRequest.maximum + var2;
   }

   protected boolean isAllocationValid() {
      return this.majorAllocValid && this.minorAllocValid;
   }

   protected boolean isBefore(int var1, int var2, Rectangle var3) {
      if (this.majorAxis == 0) {
         return var1 < var3.x;
      } else {
         return var2 < var3.y;
      }
   }

   protected boolean isAfter(int var1, int var2, Rectangle var3) {
      if (this.majorAxis == 0) {
         return var1 > var3.width + var3.x;
      } else {
         return var2 > var3.height + var3.y;
      }
   }

   protected View getViewAtPoint(int var1, int var2, Rectangle var3) {
      int var4 = this.getViewCount();
      int var5;
      if (this.majorAxis == 0) {
         if (var1 < var3.x + this.majorOffsets[0]) {
            this.childAllocation(0, var3);
            return this.getView(0);
         } else {
            for(var5 = 0; var5 < var4; ++var5) {
               if (var1 < var3.x + this.majorOffsets[var5]) {
                  this.childAllocation(var5 - 1, var3);
                  return this.getView(var5 - 1);
               }
            }

            this.childAllocation(var4 - 1, var3);
            return this.getView(var4 - 1);
         }
      } else if (var2 < var3.y + this.majorOffsets[0]) {
         this.childAllocation(0, var3);
         return this.getView(0);
      } else {
         for(var5 = 0; var5 < var4; ++var5) {
            if (var2 < var3.y + this.majorOffsets[var5]) {
               this.childAllocation(var5 - 1, var3);
               return this.getView(var5 - 1);
            }
         }

         this.childAllocation(var4 - 1, var3);
         return this.getView(var4 - 1);
      }
   }

   protected void childAllocation(int var1, Rectangle var2) {
      var2.x += this.getOffset(0, var1);
      var2.y += this.getOffset(1, var1);
      var2.width = this.getSpan(0, var1);
      var2.height = this.getSpan(1, var1);
   }

   protected void layout(int var1, int var2) {
      this.setSpanOnAxis(0, (float)var1);
      this.setSpanOnAxis(1, (float)var2);
   }

   public int getWidth() {
      int var1;
      if (this.majorAxis == 0) {
         var1 = this.majorSpan;
      } else {
         var1 = this.minorSpan;
      }

      var1 += this.getLeftInset() - this.getRightInset();
      return var1;
   }

   public int getHeight() {
      int var1;
      if (this.majorAxis == 1) {
         var1 = this.majorSpan;
      } else {
         var1 = this.minorSpan;
      }

      var1 += this.getTopInset() - this.getBottomInset();
      return var1;
   }

   protected void layoutMajorAxis(int var1, int var2, int[] var3, int[] var4) {
      long var5 = 0L;
      int var7 = this.getViewCount();

      for(int var8 = 0; var8 < var7; ++var8) {
         View var9 = this.getView(var8);
         var4[var8] = (int)var9.getPreferredSpan(var2);
         var5 += (long)var4[var8];
      }

      long var17 = (long)var1 - var5;
      float var10 = 0.0F;
      int[] var11 = null;
      float var19;
      if (var17 != 0L) {
         long var12 = 0L;
         var11 = new int[var7];

         for(int var14 = 0; var14 < var7; ++var14) {
            View var15 = this.getView(var14);
            int var16;
            if (var17 < 0L) {
               var16 = (int)var15.getMinimumSpan(var2);
               var11[var14] = var4[var14] - var16;
            } else {
               var16 = (int)var15.getMaximumSpan(var2);
               var11[var14] = var16 - var4[var14];
            }

            var12 += (long)var16;
         }

         var19 = (float)Math.abs(var12 - var5);
         var10 = (float)var17 / var19;
         var10 = Math.min(var10, 1.0F);
         var10 = Math.max(var10, -1.0F);
      }

      int var18 = 0;

      for(int var13 = 0; var13 < var7; ++var13) {
         var3[var13] = var18;
         if (var17 != 0L) {
            var19 = var10 * (float)var11[var13];
            var4[var13] += Math.round(var19);
         }

         var18 = (int)Math.min((long)var18 + (long)var4[var13], 2147483647L);
      }

   }

   protected void layoutMinorAxis(int var1, int var2, int[] var3, int[] var4) {
      int var5 = this.getViewCount();

      for(int var6 = 0; var6 < var5; ++var6) {
         View var7 = this.getView(var6);
         int var8 = (int)var7.getMaximumSpan(var2);
         if (var8 < var1) {
            float var9 = var7.getAlignment(var2);
            var3[var6] = (int)((float)(var1 - var8) * var9);
            var4[var6] = var8;
         } else {
            int var10 = (int)var7.getMinimumSpan(var2);
            var3[var6] = 0;
            var4[var6] = Math.max(var10, var1);
         }
      }

   }

   protected SizeRequirements calculateMajorAxisRequirements(int var1, SizeRequirements var2) {
      float var3 = 0.0F;
      float var4 = 0.0F;
      float var5 = 0.0F;
      int var6 = this.getViewCount();

      for(int var7 = 0; var7 < var6; ++var7) {
         View var8 = this.getView(var7);
         var3 += var8.getMinimumSpan(var1);
         var4 += var8.getPreferredSpan(var1);
         var5 += var8.getMaximumSpan(var1);
      }

      if (var2 == null) {
         var2 = new SizeRequirements();
      }

      var2.alignment = 0.5F;
      var2.minimum = (int)var3;
      var2.preferred = (int)var4;
      var2.maximum = (int)var5;
      return var2;
   }

   protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
      int var3 = 0;
      long var4 = 0L;
      int var6 = Integer.MAX_VALUE;
      int var7 = this.getViewCount();

      for(int var8 = 0; var8 < var7; ++var8) {
         View var9 = this.getView(var8);
         var3 = Math.max((int)var9.getMinimumSpan(var1), var3);
         var4 = Math.max((long)((int)var9.getPreferredSpan(var1)), var4);
         var6 = Math.max((int)var9.getMaximumSpan(var1), var6);
      }

      if (var2 == null) {
         var2 = new SizeRequirements();
         var2.alignment = 0.5F;
      }

      var2.preferred = (int)var4;
      var2.minimum = var3;
      var2.maximum = var6;
      return var2;
   }

   void checkRequests(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("Invalid axis: " + var1);
      } else {
         if (var1 == this.majorAxis) {
            if (!this.majorReqValid) {
               this.majorRequest = this.calculateMajorAxisRequirements(var1, this.majorRequest);
               this.majorReqValid = true;
            }
         } else if (!this.minorReqValid) {
            this.minorRequest = this.calculateMinorAxisRequirements(var1, this.minorRequest);
            this.minorReqValid = true;
         }

      }
   }

   protected void baselineLayout(int var1, int var2, int[] var3, int[] var4) {
      int var5 = (int)((float)var1 * this.getAlignment(var2));
      int var6 = var1 - var5;
      int var7 = this.getViewCount();

      for(int var8 = 0; var8 < var7; ++var8) {
         View var9 = this.getView(var8);
         float var10 = var9.getAlignment(var2);
         float var11;
         if (var9.getResizeWeight(var2) > 0) {
            float var12 = var9.getMinimumSpan(var2);
            float var13 = var9.getMaximumSpan(var2);
            if (var10 == 0.0F) {
               var11 = Math.max(Math.min(var13, (float)var6), var12);
            } else if (var10 == 1.0F) {
               var11 = Math.max(Math.min(var13, (float)var5), var12);
            } else {
               float var14 = Math.min((float)var5 / var10, (float)var6 / (1.0F - var10));
               var11 = Math.max(Math.min(var13, var14), var12);
            }
         } else {
            var11 = var9.getPreferredSpan(var2);
         }

         var3[var8] = var5 - (int)(var11 * var10);
         var4[var8] = (int)var11;
      }

   }

   protected SizeRequirements baselineRequirements(int var1, SizeRequirements var2) {
      SizeRequirements var3 = new SizeRequirements();
      SizeRequirements var4 = new SizeRequirements();
      if (var2 == null) {
         var2 = new SizeRequirements();
      }

      var2.alignment = 0.5F;
      int var5 = this.getViewCount();

      for(int var6 = 0; var6 < var5; ++var6) {
         View var7 = this.getView(var6);
         float var8 = var7.getAlignment(var1);
         float var9 = var7.getPreferredSpan(var1);
         int var10 = (int)(var8 * var9);
         int var11 = (int)(var9 - (float)var10);
         var3.preferred = Math.max(var10, var3.preferred);
         var4.preferred = Math.max(var11, var4.preferred);
         if (var7.getResizeWeight(var1) > 0) {
            var9 = var7.getMinimumSpan(var1);
            var10 = (int)(var8 * var9);
            var11 = (int)(var9 - (float)var10);
            var3.minimum = Math.max(var10, var3.minimum);
            var4.minimum = Math.max(var11, var4.minimum);
            var9 = var7.getMaximumSpan(var1);
            var10 = (int)(var8 * var9);
            var11 = (int)(var9 - (float)var10);
            var3.maximum = Math.max(var10, var3.maximum);
            var4.maximum = Math.max(var11, var4.maximum);
         } else {
            var3.minimum = Math.max(var10, var3.minimum);
            var4.minimum = Math.max(var11, var4.minimum);
            var3.maximum = Math.max(var10, var3.maximum);
            var4.maximum = Math.max(var11, var4.maximum);
         }
      }

      var2.preferred = (int)Math.min((long)var3.preferred + (long)var4.preferred, 2147483647L);
      if (var2.preferred > 0) {
         var2.alignment = (float)var3.preferred / (float)var2.preferred;
      }

      if (var2.alignment == 0.0F) {
         var2.minimum = var4.minimum;
         var2.maximum = var4.maximum;
      } else if (var2.alignment == 1.0F) {
         var2.minimum = var3.minimum;
         var2.maximum = var3.maximum;
      } else {
         var2.minimum = Math.round(Math.max((float)var3.minimum / var2.alignment, (float)var4.minimum / (1.0F - var2.alignment)));
         var2.maximum = Math.round(Math.min((float)var3.maximum / var2.alignment, (float)var4.maximum / (1.0F - var2.alignment)));
      }

      return var2;
   }

   protected int getOffset(int var1, int var2) {
      int[] var3 = var1 == this.majorAxis ? this.majorOffsets : this.minorOffsets;
      return var3[var2];
   }

   protected int getSpan(int var1, int var2) {
      int[] var3 = var1 == this.majorAxis ? this.majorSpans : this.minorSpans;
      return var3[var2];
   }

   protected boolean flipEastAndWestAtEnds(int var1, Position.Bias var2) {
      if (this.majorAxis == 1) {
         int var3 = var2 == Position.Bias.Backward ? Math.max(0, var1 - 1) : var1;
         int var4 = this.getViewIndexAtPosition(var3);
         if (var4 != -1) {
            View var5 = this.getView(var4);
            if (var5 != null && var5 instanceof CompositeView) {
               return ((CompositeView)var5).flipEastAndWestAtEnds(var1, var2);
            }
         }
      }

      return false;
   }
}
