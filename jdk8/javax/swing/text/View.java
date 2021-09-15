package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;

public abstract class View implements SwingConstants {
   public static final int BadBreakWeight = 0;
   public static final int GoodBreakWeight = 1000;
   public static final int ExcellentBreakWeight = 2000;
   public static final int ForcedBreakWeight = 3000;
   public static final int X_AXIS = 0;
   public static final int Y_AXIS = 1;
   static final Position.Bias[] sharedBiasReturn = new Position.Bias[1];
   private View parent;
   private Element elem;
   int firstUpdateIndex;
   int lastUpdateIndex;

   public View(Element var1) {
      this.elem = var1;
   }

   public View getParent() {
      return this.parent;
   }

   public boolean isVisible() {
      return true;
   }

   public abstract float getPreferredSpan(int var1);

   public float getMinimumSpan(int var1) {
      int var2 = this.getResizeWeight(var1);
      return var2 == 0 ? this.getPreferredSpan(var1) : 0.0F;
   }

   public float getMaximumSpan(int var1) {
      int var2 = this.getResizeWeight(var1);
      return var2 == 0 ? this.getPreferredSpan(var1) : 2.14748365E9F;
   }

   public void preferenceChanged(View var1, boolean var2, boolean var3) {
      View var4 = this.getParent();
      if (var4 != null) {
         var4.preferenceChanged(this, var2, var3);
      }

   }

   public float getAlignment(int var1) {
      return 0.5F;
   }

   public abstract void paint(Graphics var1, Shape var2);

   public void setParent(View var1) {
      if (var1 == null) {
         for(int var2 = 0; var2 < this.getViewCount(); ++var2) {
            if (this.getView(var2).getParent() == this) {
               this.getView(var2).setParent((View)null);
            }
         }
      }

      this.parent = var1;
   }

   public int getViewCount() {
      return 0;
   }

   public View getView(int var1) {
      return null;
   }

   public void removeAll() {
      this.replace(0, this.getViewCount(), (View[])null);
   }

   public void remove(int var1) {
      this.replace(var1, 1, (View[])null);
   }

   public void insert(int var1, View var2) {
      View[] var3 = new View[]{var2};
      this.replace(var1, 0, var3);
   }

   public void append(View var1) {
      View[] var2 = new View[]{var1};
      this.replace(this.getViewCount(), 0, var2);
   }

   public void replace(int var1, int var2, View[] var3) {
   }

   public int getViewIndex(int var1, Position.Bias var2) {
      return -1;
   }

   public Shape getChildAllocation(int var1, Shape var2) {
      return null;
   }

   public int getNextVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      if (var1 < -1) {
         throw new BadLocationException("Invalid position", var1);
      } else {
         var5[0] = Position.Bias.Forward;
         switch(var4) {
         case 1:
         case 5:
            if (var1 == -1) {
               var1 = var4 == 1 ? Math.max(0, this.getEndOffset() - 1) : this.getStartOffset();
            } else {
               JTextComponent var6 = (JTextComponent)this.getContainer();
               Caret var7 = var6 != null ? var6.getCaret() : null;
               Point var8;
               if (var7 != null) {
                  var8 = var7.getMagicCaretPosition();
               } else {
                  var8 = null;
               }

               int var9;
               if (var8 == null) {
                  Rectangle var10 = var6.modelToView(var1);
                  var9 = var10 == null ? 0 : var10.x;
               } else {
                  var9 = var8.x;
               }

               if (var4 == 1) {
                  var1 = Utilities.getPositionAbove(var6, var1, var9);
               } else {
                  var1 = Utilities.getPositionBelow(var6, var1, var9);
               }
            }
            break;
         case 2:
         case 4:
         case 6:
         default:
            throw new IllegalArgumentException("Bad direction: " + var4);
         case 3:
            if (var1 == -1) {
               var1 = this.getStartOffset();
            } else {
               var1 = Math.min(var1 + 1, this.getDocument().getLength());
            }
            break;
         case 7:
            if (var1 == -1) {
               var1 = Math.max(0, this.getEndOffset() - 1);
            } else {
               var1 = Math.max(0, var1 - 1);
            }
         }

         return var1;
      }
   }

   public abstract Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException;

   public Shape modelToView(int var1, Position.Bias var2, int var3, Position.Bias var4, Shape var5) throws BadLocationException {
      Shape var6 = this.modelToView(var1, var5, var2);
      Object var7;
      Rectangle var8;
      if (var3 == this.getEndOffset()) {
         try {
            var7 = this.modelToView(var3, var5, var4);
         } catch (BadLocationException var11) {
            var7 = null;
         }

         if (var7 == null) {
            var8 = var5 instanceof Rectangle ? (Rectangle)var5 : var5.getBounds();
            var7 = new Rectangle(var8.x + var8.width - 1, var8.y, 1, var8.height);
         }
      } else {
         var7 = this.modelToView(var3, var5, var4);
      }

      var8 = var6.getBounds();
      Rectangle var9 = var7 instanceof Rectangle ? (Rectangle)var7 : ((Shape)var7).getBounds();
      if (var8.y != var9.y) {
         Rectangle var10 = var5 instanceof Rectangle ? (Rectangle)var5 : var5.getBounds();
         var8.x = var10.x;
         var8.width = var10.width;
      }

      var8.add(var9);
      return var8;
   }

   public abstract int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4);

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      if (this.getViewCount() > 0) {
         Element var4 = this.getElement();
         DocumentEvent.ElementChange var5 = var1.getChange(var4);
         if (var5 != null && !this.updateChildren(var5, var1, var3)) {
            var5 = null;
         }

         this.forwardUpdate(var5, var1, var2, var3);
         this.updateLayout(var5, var1, var2);
      }

   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      if (this.getViewCount() > 0) {
         Element var4 = this.getElement();
         DocumentEvent.ElementChange var5 = var1.getChange(var4);
         if (var5 != null && !this.updateChildren(var5, var1, var3)) {
            var5 = null;
         }

         this.forwardUpdate(var5, var1, var2, var3);
         this.updateLayout(var5, var1, var2);
      }

   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      if (this.getViewCount() > 0) {
         Element var4 = this.getElement();
         DocumentEvent.ElementChange var5 = var1.getChange(var4);
         if (var5 != null && !this.updateChildren(var5, var1, var3)) {
            var5 = null;
         }

         this.forwardUpdate(var5, var1, var2, var3);
         this.updateLayout(var5, var1, var2);
      }

   }

   public Document getDocument() {
      return this.elem.getDocument();
   }

   public int getStartOffset() {
      return this.elem.getStartOffset();
   }

   public int getEndOffset() {
      return this.elem.getEndOffset();
   }

   public Element getElement() {
      return this.elem;
   }

   public Graphics getGraphics() {
      Container var1 = this.getContainer();
      return var1.getGraphics();
   }

   public AttributeSet getAttributes() {
      return this.elem.getAttributes();
   }

   public View breakView(int var1, int var2, float var3, float var4) {
      return this;
   }

   public View createFragment(int var1, int var2) {
      return this;
   }

   public int getBreakWeight(int var1, float var2, float var3) {
      return var3 > this.getPreferredSpan(var1) ? 1000 : 0;
   }

   public int getResizeWeight(int var1) {
      return 0;
   }

   public void setSize(float var1, float var2) {
   }

   public Container getContainer() {
      View var1 = this.getParent();
      return var1 != null ? var1.getContainer() : null;
   }

   public ViewFactory getViewFactory() {
      View var1 = this.getParent();
      return var1 != null ? var1.getViewFactory() : null;
   }

   public String getToolTipText(float var1, float var2, Shape var3) {
      int var4 = this.getViewIndex(var1, var2, var3);
      if (var4 >= 0) {
         var3 = this.getChildAllocation(var4, var3);
         Rectangle var5 = var3 instanceof Rectangle ? (Rectangle)var3 : var3.getBounds();
         if (var5.contains((double)var1, (double)var2)) {
            return this.getView(var4).getToolTipText(var1, var2, var3);
         }
      }

      return null;
   }

   public int getViewIndex(float var1, float var2, Shape var3) {
      for(int var4 = this.getViewCount() - 1; var4 >= 0; --var4) {
         Shape var5 = this.getChildAllocation(var4, var3);
         if (var5 != null) {
            Rectangle var6 = var5 instanceof Rectangle ? (Rectangle)var5 : var5.getBounds();
            if (var6.contains((double)var1, (double)var2)) {
               return var4;
            }
         }
      }

      return -1;
   }

   protected boolean updateChildren(DocumentEvent.ElementChange var1, DocumentEvent var2, ViewFactory var3) {
      Element[] var4 = var1.getChildrenRemoved();
      Element[] var5 = var1.getChildrenAdded();
      View[] var6 = null;
      int var7;
      if (var5 != null) {
         var6 = new View[var5.length];

         for(var7 = 0; var7 < var5.length; ++var7) {
            var6[var7] = var3.create(var5[var7]);
         }
      }

      var7 = 0;
      int var8 = var1.getIndex();
      if (var4 != null) {
         var7 = var4.length;
      }

      this.replace(var8, var7, var6);
      return true;
   }

   protected void forwardUpdate(DocumentEvent.ElementChange var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
      this.calculateUpdateIndexes(var2);
      int var5 = this.lastUpdateIndex + 1;
      int var6 = var5;
      Element[] var7 = var1 != null ? var1.getChildrenAdded() : null;
      if (var7 != null && var7.length > 0) {
         var5 = var1.getIndex();
         var6 = var5 + var7.length - 1;
      }

      for(int var8 = this.firstUpdateIndex; var8 <= this.lastUpdateIndex; ++var8) {
         if (var8 < var5 || var8 > var6) {
            View var9 = this.getView(var8);
            if (var9 != null) {
               Shape var10 = this.getChildAllocation(var8, var3);
               this.forwardUpdateToView(var9, var2, var10, var4);
            }
         }
      }

   }

   void calculateUpdateIndexes(DocumentEvent var1) {
      int var2 = var1.getOffset();
      this.firstUpdateIndex = this.getViewIndex(var2, Position.Bias.Forward);
      if (this.firstUpdateIndex == -1 && var1.getType() == DocumentEvent.EventType.REMOVE && var2 >= this.getEndOffset()) {
         this.firstUpdateIndex = this.getViewCount() - 1;
      }

      this.lastUpdateIndex = this.firstUpdateIndex;
      View var3 = this.firstUpdateIndex >= 0 ? this.getView(this.firstUpdateIndex) : null;
      if (var3 != null && var3.getStartOffset() == var2 && var2 > 0) {
         this.firstUpdateIndex = Math.max(this.firstUpdateIndex - 1, 0);
      }

      if (var1.getType() != DocumentEvent.EventType.REMOVE) {
         this.lastUpdateIndex = this.getViewIndex(var2 + var1.getLength(), Position.Bias.Forward);
         if (this.lastUpdateIndex < 0) {
            this.lastUpdateIndex = this.getViewCount() - 1;
         }
      }

      this.firstUpdateIndex = Math.max(this.firstUpdateIndex, 0);
   }

   void updateAfterChange() {
   }

   protected void forwardUpdateToView(View var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
      DocumentEvent.EventType var5 = var2.getType();
      if (var5 == DocumentEvent.EventType.INSERT) {
         var1.insertUpdate(var2, var3, var4);
      } else if (var5 == DocumentEvent.EventType.REMOVE) {
         var1.removeUpdate(var2, var3, var4);
      } else {
         var1.changedUpdate(var2, var3, var4);
      }

   }

   protected void updateLayout(DocumentEvent.ElementChange var1, DocumentEvent var2, Shape var3) {
      if (var1 != null && var3 != null) {
         this.preferenceChanged((View)null, true, true);
         Container var4 = this.getContainer();
         if (var4 != null) {
            var4.repaint();
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public Shape modelToView(int var1, Shape var2) throws BadLocationException {
      return this.modelToView(var1, var2, Position.Bias.Forward);
   }

   /** @deprecated */
   @Deprecated
   public int viewToModel(float var1, float var2, Shape var3) {
      sharedBiasReturn[0] = Position.Bias.Forward;
      return this.viewToModel(var1, var2, var3, sharedBiasReturn);
   }
}
