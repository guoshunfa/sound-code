package javax.swing.text;

import java.awt.Rectangle;
import java.awt.Shape;

public abstract class CompositeView extends View {
   private static View[] ZERO = new View[0];
   private View[] children = new View[1];
   private int nchildren = 0;
   private short left;
   private short right;
   private short top;
   private short bottom;
   private Rectangle childAlloc = new Rectangle();

   public CompositeView(Element var1) {
      super(var1);
   }

   protected void loadChildren(ViewFactory var1) {
      if (var1 != null) {
         Element var2 = this.getElement();
         int var3 = var2.getElementCount();
         if (var3 > 0) {
            View[] var4 = new View[var3];

            for(int var5 = 0; var5 < var3; ++var5) {
               var4[var5] = var1.create(var2.getElement(var5));
            }

            this.replace(0, 0, var4);
         }

      }
   }

   public void setParent(View var1) {
      super.setParent(var1);
      if (var1 != null && this.nchildren == 0) {
         ViewFactory var2 = this.getViewFactory();
         this.loadChildren(var2);
      }

   }

   public int getViewCount() {
      return this.nchildren;
   }

   public View getView(int var1) {
      return this.children[var1];
   }

   public void replace(int var1, int var2, View[] var3) {
      if (var3 == null) {
         var3 = ZERO;
      }

      int var4;
      for(var4 = var1; var4 < var1 + var2; ++var4) {
         if (this.children[var4].getParent() == this) {
            this.children[var4].setParent((View)null);
         }

         this.children[var4] = null;
      }

      var4 = var3.length - var2;
      int var5 = var1 + var2;
      int var6 = this.nchildren - var5;
      int var7 = var5 + var4;
      int var8;
      if (this.nchildren + var4 >= this.children.length) {
         var8 = Math.max(2 * this.children.length, this.nchildren + var4);
         View[] var9 = new View[var8];
         System.arraycopy(this.children, 0, var9, 0, var1);
         System.arraycopy(var3, 0, var9, var1, var3.length);
         System.arraycopy(this.children, var5, var9, var7, var6);
         this.children = var9;
      } else {
         System.arraycopy(this.children, var5, this.children, var7, var6);
         System.arraycopy(var3, 0, this.children, var1, var3.length);
      }

      this.nchildren += var4;

      for(var8 = 0; var8 < var3.length; ++var8) {
         var3[var8].setParent(this);
      }

   }

   public Shape getChildAllocation(int var1, Shape var2) {
      Rectangle var3 = this.getInsideAllocation(var2);
      this.childAllocation(var1, var3);
      return var3;
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      boolean var4 = var3 == Position.Bias.Backward;
      int var5 = var4 ? Math.max(0, var1 - 1) : var1;
      if (var4 && var5 < this.getStartOffset()) {
         return null;
      } else {
         int var6 = this.getViewIndexAtPosition(var5);
         if (var6 != -1 && var6 < this.getViewCount()) {
            View var7 = this.getView(var6);
            if (var7 != null && var5 >= var7.getStartOffset() && var5 < var7.getEndOffset()) {
               Shape var8 = this.getChildAllocation(var6, var2);
               if (var8 == null) {
                  return null;
               }

               Shape var9 = var7.modelToView(var1, var8, var3);
               if (var9 == null && var7.getEndOffset() == var1) {
                  ++var6;
                  if (var6 < this.getViewCount()) {
                     var7 = this.getView(var6);
                     var9 = var7.modelToView(var1, this.getChildAllocation(var6, var2), var3);
                  }
               }

               return var9;
            }
         }

         throw new BadLocationException("Position not represented by view", var1);
      }
   }

   public Shape modelToView(int var1, Position.Bias var2, int var3, Position.Bias var4, Shape var5) throws BadLocationException {
      if (var1 == this.getStartOffset() && var3 == this.getEndOffset()) {
         return var5;
      } else {
         Rectangle var6 = this.getInsideAllocation(var5);
         Rectangle var7 = new Rectangle(var6);
         View var8 = this.getViewAtPosition(var2 == Position.Bias.Backward ? Math.max(0, var1 - 1) : var1, var7);
         Rectangle var9 = new Rectangle(var6);
         View var10 = this.getViewAtPosition(var4 == Position.Bias.Backward ? Math.max(0, var3 - 1) : var3, var9);
         if (var8 == var10) {
            return var8 == null ? var5 : var8.modelToView(var1, var2, var3, var4, var7);
         } else {
            int var11 = this.getViewCount();

            for(int var12 = 0; var12 < var11; ++var12) {
               View var13;
               if ((var13 = this.getView(var12)) == var8 || var13 == var10) {
                  Rectangle var16 = new Rectangle();
                  View var14;
                  Rectangle var15;
                  if (var13 == var8) {
                     var15 = var8.modelToView(var1, var2, var8.getEndOffset(), Position.Bias.Backward, var7).getBounds();
                     var14 = var10;
                  } else {
                     var15 = var10.modelToView(var10.getStartOffset(), Position.Bias.Forward, var3, var4, var9).getBounds();
                     var14 = var8;
                  }

                  while(true) {
                     ++var12;
                     if (var12 >= var11 || this.getView(var12) == var14) {
                        if (var14 != null) {
                           Shape var17;
                           if (var14 == var10) {
                              var17 = var10.modelToView(var10.getStartOffset(), Position.Bias.Forward, var3, var4, var9);
                           } else {
                              var17 = var8.modelToView(var1, var2, var8.getEndOffset(), Position.Bias.Backward, var7);
                           }

                           if (var17 instanceof Rectangle) {
                              var15.add((Rectangle)var17);
                           } else {
                              var15.add(var17.getBounds());
                           }
                        }

                        return var15;
                     }

                     var16.setBounds(var6);
                     this.childAllocation(var12, var16);
                     var15.add(var16);
                  }
               }
            }

            throw new BadLocationException("Position not represented by view", var1);
         }
      }
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      Rectangle var5 = this.getInsideAllocation(var3);
      int var12;
      if (this.isBefore((int)var1, (int)var2, var5)) {
         var12 = -1;

         try {
            var12 = this.getNextVisualPositionFrom(-1, Position.Bias.Forward, var3, 3, var4);
         } catch (BadLocationException var8) {
         } catch (IllegalArgumentException var9) {
         }

         if (var12 == -1) {
            var12 = this.getStartOffset();
            var4[0] = Position.Bias.Forward;
         }

         return var12;
      } else if (this.isAfter((int)var1, (int)var2, var5)) {
         var12 = -1;

         try {
            var12 = this.getNextVisualPositionFrom(-1, Position.Bias.Forward, var3, 7, var4);
         } catch (BadLocationException var10) {
         } catch (IllegalArgumentException var11) {
         }

         if (var12 == -1) {
            var12 = this.getEndOffset() - 1;
            var4[0] = Position.Bias.Forward;
         }

         return var12;
      } else {
         View var6 = this.getViewAtPoint((int)var1, (int)var2, var5);
         return var6 != null ? var6.viewToModel(var1, var2, var5, var4) : -1;
      }
   }

   public int getNextVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      if (var1 < -1) {
         throw new BadLocationException("invalid position", var1);
      } else {
         this.getInsideAllocation(var3);
         switch(var4) {
         case 1:
            return this.getNextNorthSouthVisualPositionFrom(var1, var2, var3, var4, var5);
         case 2:
         case 4:
         case 6:
         default:
            throw new IllegalArgumentException("Bad direction: " + var4);
         case 3:
            return this.getNextEastWestVisualPositionFrom(var1, var2, var3, var4, var5);
         case 5:
            return this.getNextNorthSouthVisualPositionFrom(var1, var2, var3, var4, var5);
         case 7:
            return this.getNextEastWestVisualPositionFrom(var1, var2, var3, var4, var5);
         }
      }
   }

   public int getViewIndex(int var1, Position.Bias var2) {
      if (var2 == Position.Bias.Backward) {
         --var1;
      }

      return var1 >= this.getStartOffset() && var1 < this.getEndOffset() ? this.getViewIndexAtPosition(var1) : -1;
   }

   protected abstract boolean isBefore(int var1, int var2, Rectangle var3);

   protected abstract boolean isAfter(int var1, int var2, Rectangle var3);

   protected abstract View getViewAtPoint(int var1, int var2, Rectangle var3);

   protected abstract void childAllocation(int var1, Rectangle var2);

   protected View getViewAtPosition(int var1, Rectangle var2) {
      int var3 = this.getViewIndexAtPosition(var1);
      if (var3 >= 0 && var3 < this.getViewCount()) {
         View var4 = this.getView(var3);
         if (var2 != null) {
            this.childAllocation(var3, var2);
         }

         return var4;
      } else {
         return null;
      }
   }

   protected int getViewIndexAtPosition(int var1) {
      Element var2 = this.getElement();
      return var2.getElementIndex(var1);
   }

   protected Rectangle getInsideAllocation(Shape var1) {
      if (var1 != null) {
         Rectangle var2;
         if (var1 instanceof Rectangle) {
            var2 = (Rectangle)var1;
         } else {
            var2 = var1.getBounds();
         }

         this.childAlloc.setBounds(var2);
         Rectangle var10000 = this.childAlloc;
         var10000.x += this.getLeftInset();
         var10000 = this.childAlloc;
         var10000.y += this.getTopInset();
         var10000 = this.childAlloc;
         var10000.width -= this.getLeftInset() + this.getRightInset();
         var10000 = this.childAlloc;
         var10000.height -= this.getTopInset() + this.getBottomInset();
         return this.childAlloc;
      } else {
         return null;
      }
   }

   protected void setParagraphInsets(AttributeSet var1) {
      this.top = (short)((int)StyleConstants.getSpaceAbove(var1));
      this.left = (short)((int)StyleConstants.getLeftIndent(var1));
      this.bottom = (short)((int)StyleConstants.getSpaceBelow(var1));
      this.right = (short)((int)StyleConstants.getRightIndent(var1));
   }

   protected void setInsets(short var1, short var2, short var3, short var4) {
      this.top = var1;
      this.left = var2;
      this.right = var4;
      this.bottom = var3;
   }

   protected short getLeftInset() {
      return this.left;
   }

   protected short getRightInset() {
      return this.right;
   }

   protected short getTopInset() {
      return this.top;
   }

   protected short getBottomInset() {
      return this.bottom;
   }

   protected int getNextNorthSouthVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      return Utilities.getNextVisualPositionFrom(this, var1, var2, var3, var4, var5);
   }

   protected int getNextEastWestVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      return Utilities.getNextVisualPositionFrom(this, var1, var2, var3, var4, var5);
   }

   protected boolean flipEastAndWestAtEnds(int var1, Position.Bias var2) {
      return false;
   }
}
