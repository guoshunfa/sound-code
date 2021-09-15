package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;

public abstract class FlowView extends BoxView {
   protected int layoutSpan = Integer.MAX_VALUE;
   protected View layoutPool;
   protected FlowView.FlowStrategy strategy = new FlowView.FlowStrategy();

   public FlowView(Element var1, int var2) {
      super(var1, var2);
   }

   public int getFlowAxis() {
      return this.getAxis() == 1 ? 0 : 1;
   }

   public int getFlowSpan(int var1) {
      return this.layoutSpan;
   }

   public int getFlowStart(int var1) {
      return 0;
   }

   protected abstract View createRow();

   protected void loadChildren(ViewFactory var1) {
      if (this.layoutPool == null) {
         this.layoutPool = new FlowView.LogicalView(this.getElement());
      }

      this.layoutPool.setParent(this);
      this.strategy.insertUpdate(this, (DocumentEvent)null, (Rectangle)null);
   }

   protected int getViewIndexAtPosition(int var1) {
      if (var1 >= this.getStartOffset() && var1 < this.getEndOffset()) {
         for(int var2 = 0; var2 < this.getViewCount(); ++var2) {
            View var3 = this.getView(var2);
            if (var1 >= var3.getStartOffset() && var1 < var3.getEndOffset()) {
               return var2;
            }
         }
      }

      return -1;
   }

   protected void layout(int var1, int var2) {
      int var3 = this.getFlowAxis();
      int var4;
      if (var3 == 0) {
         var4 = var1;
      } else {
         var4 = var2;
      }

      if (this.layoutSpan != var4) {
         this.layoutChanged(var3);
         this.layoutChanged(this.getAxis());
         this.layoutSpan = var4;
      }

      if (!this.isLayoutValid(var3)) {
         int var5 = this.getAxis();
         int var6 = var5 == 0 ? this.getWidth() : this.getHeight();
         this.strategy.layout(this);
         int var7 = (int)this.getPreferredSpan(var5);
         if (var6 != var7) {
            View var8 = this.getParent();
            if (var8 != null) {
               var8.preferenceChanged(this, var5 == 0, var5 == 1);
            }

            Container var9 = this.getContainer();
            if (var9 != null) {
               var9.repaint();
            }
         }
      }

      super.layout(var1, var2);
   }

   protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
      if (var2 == null) {
         var2 = new SizeRequirements();
      }

      float var3 = this.layoutPool.getPreferredSpan(var1);
      float var4 = this.layoutPool.getMinimumSpan(var1);
      var2.minimum = (int)var4;
      var2.preferred = Math.max(var2.minimum, (int)var3);
      var2.maximum = Integer.MAX_VALUE;
      var2.alignment = 0.5F;
      return var2;
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.layoutPool.insertUpdate(var1, var2, var3);
      this.strategy.insertUpdate(this, var1, this.getInsideAllocation(var2));
   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.layoutPool.removeUpdate(var1, var2, var3);
      this.strategy.removeUpdate(this, var1, this.getInsideAllocation(var2));
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.layoutPool.changedUpdate(var1, var2, var3);
      this.strategy.changedUpdate(this, var1, this.getInsideAllocation(var2));
   }

   public void setParent(View var1) {
      super.setParent(var1);
      if (var1 == null && this.layoutPool != null) {
         this.layoutPool.setParent((View)null);
      }

   }

   static class LogicalView extends CompositeView {
      LogicalView(Element var1) {
         super(var1);
      }

      protected int getViewIndexAtPosition(int var1) {
         Element var2 = this.getElement();
         return var2.isLeaf() ? 0 : super.getViewIndexAtPosition(var1);
      }

      protected void loadChildren(ViewFactory var1) {
         Element var2 = this.getElement();
         if (var2.isLeaf()) {
            LabelView var3 = new LabelView(var2);
            this.append(var3);
         } else {
            super.loadChildren(var1);
         }

      }

      public AttributeSet getAttributes() {
         View var1 = this.getParent();
         return var1 != null ? var1.getAttributes() : null;
      }

      public float getPreferredSpan(int var1) {
         float var2 = 0.0F;
         float var3 = 0.0F;
         int var4 = this.getViewCount();

         for(int var5 = 0; var5 < var4; ++var5) {
            View var6 = this.getView(var5);
            var3 += var6.getPreferredSpan(var1);
            if (var6.getBreakWeight(var1, 0.0F, 2.14748365E9F) >= 3000) {
               var2 = Math.max(var2, var3);
               var3 = 0.0F;
            }
         }

         var2 = Math.max(var2, var3);
         return var2;
      }

      public float getMinimumSpan(int var1) {
         float var2 = 0.0F;
         float var3 = 0.0F;
         boolean var4 = false;
         int var5 = this.getViewCount();

         for(int var6 = 0; var6 < var5; ++var6) {
            View var7 = this.getView(var6);
            if (var7.getBreakWeight(var1, 0.0F, 2.14748365E9F) == 0) {
               var3 += var7.getPreferredSpan(var1);
               var4 = true;
            } else if (var4) {
               var2 = Math.max(var3, var2);
               var4 = false;
               var3 = 0.0F;
            }

            if (var7 instanceof ComponentView) {
               var2 = Math.max(var2, var7.getMinimumSpan(var1));
            }
         }

         var2 = Math.max(var2, var3);
         return var2;
      }

      protected void forwardUpdateToView(View var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
         View var5 = var1.getParent();
         var1.setParent(this);
         super.forwardUpdateToView(var1, var2, var3, var4);
         var1.setParent(var5);
      }

      protected void forwardUpdate(DocumentEvent.ElementChange var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
         super.forwardUpdate(var1, var2, var3, var4);
         DocumentEvent.EventType var5 = var2.getType();
         if (var5 == DocumentEvent.EventType.INSERT || var5 == DocumentEvent.EventType.REMOVE) {
            this.firstUpdateIndex = Math.min(this.lastUpdateIndex + 1, this.getViewCount() - 1);
            this.lastUpdateIndex = Math.max(this.getViewCount() - 1, 0);

            for(int var6 = this.firstUpdateIndex; var6 <= this.lastUpdateIndex; ++var6) {
               View var7 = this.getView(var6);
               if (var7 != null) {
                  var7.updateAfterChange();
               }
            }
         }

      }

      public void paint(Graphics var1, Shape var2) {
      }

      protected boolean isBefore(int var1, int var2, Rectangle var3) {
         return false;
      }

      protected boolean isAfter(int var1, int var2, Rectangle var3) {
         return false;
      }

      protected View getViewAtPoint(int var1, int var2, Rectangle var3) {
         return null;
      }

      protected void childAllocation(int var1, Rectangle var2) {
      }
   }

   public static class FlowStrategy {
      Position damageStart = null;
      Vector<View> viewBuffer;

      void addDamage(FlowView var1, int var2) {
         if (var2 >= var1.getStartOffset() && var2 < var1.getEndOffset() && (this.damageStart == null || var2 < this.damageStart.getOffset())) {
            try {
               this.damageStart = var1.getDocument().createPosition(var2);
            } catch (BadLocationException var4) {
               assert false;
            }
         }

      }

      void unsetDamage() {
         this.damageStart = null;
      }

      public void insertUpdate(FlowView var1, DocumentEvent var2, Rectangle var3) {
         if (var2 != null) {
            this.addDamage(var1, var2.getOffset());
         }

         if (var3 != null) {
            Container var4 = var1.getContainer();
            if (var4 != null) {
               var4.repaint(var3.x, var3.y, var3.width, var3.height);
            }
         } else {
            var1.preferenceChanged((View)null, true, true);
         }

      }

      public void removeUpdate(FlowView var1, DocumentEvent var2, Rectangle var3) {
         this.addDamage(var1, var2.getOffset());
         if (var3 != null) {
            Container var4 = var1.getContainer();
            if (var4 != null) {
               var4.repaint(var3.x, var3.y, var3.width, var3.height);
            }
         } else {
            var1.preferenceChanged((View)null, true, true);
         }

      }

      public void changedUpdate(FlowView var1, DocumentEvent var2, Rectangle var3) {
         this.addDamage(var1, var2.getOffset());
         if (var3 != null) {
            Container var4 = var1.getContainer();
            if (var4 != null) {
               var4.repaint(var3.x, var3.y, var3.width, var3.height);
            }
         } else {
            var1.preferenceChanged((View)null, true, true);
         }

      }

      protected View getLogicalView(FlowView var1) {
         return var1.layoutPool;
      }

      public void layout(FlowView var1) {
         View var2 = this.getLogicalView(var1);
         int var5 = var1.getEndOffset();
         int var3;
         int var4;
         int var6;
         if (var1.majorAllocValid) {
            if (this.damageStart == null) {
               return;
            }

            for(var6 = this.damageStart.getOffset(); (var3 = var1.getViewIndexAtPosition(var6)) < 0; --var6) {
            }

            if (var3 > 0) {
               --var3;
            }

            var4 = var1.getView(var3).getStartOffset();
         } else {
            var3 = 0;
            var4 = var1.getStartOffset();
         }

         this.reparentViews(var2, var4);
         this.viewBuffer = new Vector(10, 10);

         for(var6 = var1.getViewCount(); var4 < var5; ++var3) {
            if (var3 >= var6) {
               View var7 = var1.createRow();
               var1.append(var7);
            } else {
               var1.getView(var3);
            }

            var4 = this.layoutRow(var1, var3, var4);
         }

         this.viewBuffer = null;
         if (var3 < var6) {
            var1.replace(var3, var6 - var3, (View[])null);
         }

         this.unsetDamage();
      }

      protected int layoutRow(FlowView var1, int var2, int var3) {
         View var4 = var1.getView(var2);
         float var5 = (float)var1.getFlowStart(var2);
         float var6 = (float)var1.getFlowSpan(var2);
         int var7 = var1.getEndOffset();
         TabExpander var8 = var1 instanceof TabExpander ? (TabExpander)var1 : null;
         int var9 = var1.getFlowAxis();
         int var10 = 0;
         float var11 = 0.0F;
         float var12 = 0.0F;
         int var13 = -1;
         int var14 = 0;
         this.viewBuffer.clear();

         while(var3 < var7 && var6 >= 0.0F) {
            View var15 = this.createView(var1, var3, (int)var6, var2);
            if (var15 == null) {
               break;
            }

            int var16 = var15.getBreakWeight(var9, var5, var6);
            if (var16 >= 3000) {
               View var20 = var15.breakView(var9, var3, var5, var6);
               if (var20 != null) {
                  this.viewBuffer.add(var20);
               } else if (var14 == 0) {
                  this.viewBuffer.add(var15);
               }
               break;
            }

            if (var16 >= var10 && var16 > 0) {
               var10 = var16;
               var11 = var5;
               var12 = var6;
               var13 = var14;
            }

            float var17;
            if (var9 == 0 && var15 instanceof TabableView) {
               var17 = ((TabableView)var15).getTabbedSpan(var5, var8);
            } else {
               var17 = var15.getPreferredSpan(var9);
            }

            if (var17 > var6 && var13 >= 0) {
               if (var13 < var14) {
                  var15 = (View)this.viewBuffer.get(var13);
               }

               for(int var18 = var14 - 1; var18 >= var13; --var18) {
                  this.viewBuffer.remove(var18);
               }

               var15 = var15.breakView(var9, var15.getStartOffset(), var11, var12);
            }

            var6 -= var17;
            var5 += var17;
            this.viewBuffer.add(var15);
            var3 = var15.getEndOffset();
            ++var14;
         }

         View[] var19 = new View[this.viewBuffer.size()];
         this.viewBuffer.toArray(var19);
         var4.replace(0, var4.getViewCount(), var19);
         return var19.length > 0 ? var4.getEndOffset() : var3;
      }

      protected void adjustRow(FlowView var1, int var2, int var3, int var4) {
         int var5 = var1.getFlowAxis();
         View var6 = var1.getView(var2);
         int var7 = var6.getViewCount();
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         int var11 = -1;

         View var12;
         int var13;
         for(var13 = 0; var13 < var7; ++var13) {
            var12 = var6.getView(var13);
            int var14 = var3 - var8;
            int var15 = var12.getBreakWeight(var5, (float)(var4 + var8), (float)var14);
            if (var15 >= var9 && var15 > 0) {
               var9 = var15;
               var11 = var13;
               var10 = var8;
               if (var15 >= 3000) {
                  break;
               }
            }

            var8 = (int)((float)var8 + var12.getPreferredSpan(var5));
         }

         if (var11 >= 0) {
            var13 = var3 - var10;
            var12 = var6.getView(var11);
            var12 = var12.breakView(var5, var12.getStartOffset(), (float)(var4 + var10), (float)var13);
            View[] var20 = new View[]{var12};
            View var21 = this.getLogicalView(var1);
            int var16 = var6.getView(var11).getStartOffset();
            int var17 = var6.getEndOffset();

            for(int var18 = 0; var18 < var21.getViewCount(); ++var18) {
               View var19 = var21.getView(var18);
               if (var19.getEndOffset() > var17) {
                  break;
               }

               if (var19.getStartOffset() >= var16) {
                  var19.setParent(var21);
               }
            }

            var6.replace(var11, var7 - var11, var20);
         }
      }

      void reparentViews(View var1, int var2) {
         int var3 = var1.getViewIndex(var2, Position.Bias.Forward);
         if (var3 >= 0) {
            for(int var4 = var3; var4 < var1.getViewCount(); ++var4) {
               var1.getView(var4).setParent(var1);
            }
         }

      }

      protected View createView(FlowView var1, int var2, int var3, int var4) {
         View var5 = this.getLogicalView(var1);
         int var6 = var5.getViewIndex(var2, Position.Bias.Forward);
         View var7 = var5.getView(var6);
         if (var2 == var7.getStartOffset()) {
            return var7;
         } else {
            var7 = var7.createFragment(var2, var7.getEndOffset());
            return var7;
         }
      }
   }
}
