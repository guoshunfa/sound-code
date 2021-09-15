package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;

public class AsyncBoxView extends View {
   int axis;
   List<AsyncBoxView.ChildState> stats = new ArrayList();
   float majorSpan;
   boolean estimatedMajorSpan;
   float minorSpan;
   protected AsyncBoxView.ChildLocator locator;
   float topInset;
   float bottomInset;
   float leftInset;
   float rightInset;
   AsyncBoxView.ChildState minRequest;
   AsyncBoxView.ChildState prefRequest;
   boolean majorChanged;
   boolean minorChanged;
   Runnable flushTask;
   AsyncBoxView.ChildState changing;

   public AsyncBoxView(Element var1, int var2) {
      super(var1);
      this.axis = var2;
      this.locator = new AsyncBoxView.ChildLocator();
      this.flushTask = new AsyncBoxView.FlushTask();
      this.minorSpan = 32767.0F;
      this.estimatedMajorSpan = false;
   }

   public int getMajorAxis() {
      return this.axis;
   }

   public int getMinorAxis() {
      return this.axis == 0 ? 1 : 0;
   }

   public float getTopInset() {
      return this.topInset;
   }

   public void setTopInset(float var1) {
      this.topInset = var1;
   }

   public float getBottomInset() {
      return this.bottomInset;
   }

   public void setBottomInset(float var1) {
      this.bottomInset = var1;
   }

   public float getLeftInset() {
      return this.leftInset;
   }

   public void setLeftInset(float var1) {
      this.leftInset = var1;
   }

   public float getRightInset() {
      return this.rightInset;
   }

   public void setRightInset(float var1) {
      this.rightInset = var1;
   }

   protected float getInsetSpan(int var1) {
      float var2 = var1 == 0 ? this.getLeftInset() + this.getRightInset() : this.getTopInset() + this.getBottomInset();
      return var2;
   }

   protected void setEstimatedMajorSpan(boolean var1) {
      this.estimatedMajorSpan = var1;
   }

   protected boolean getEstimatedMajorSpan() {
      return this.estimatedMajorSpan;
   }

   protected AsyncBoxView.ChildState getChildState(int var1) {
      synchronized(this.stats) {
         return var1 >= 0 && var1 < this.stats.size() ? (AsyncBoxView.ChildState)this.stats.get(var1) : null;
      }
   }

   protected LayoutQueue getLayoutQueue() {
      return LayoutQueue.getDefaultQueue();
   }

   protected AsyncBoxView.ChildState createChildState(View var1) {
      return new AsyncBoxView.ChildState(var1);
   }

   protected synchronized void majorRequirementChange(AsyncBoxView.ChildState var1, float var2) {
      if (!this.estimatedMajorSpan) {
         this.majorSpan += var2;
      }

      this.majorChanged = true;
   }

   protected synchronized void minorRequirementChange(AsyncBoxView.ChildState var1) {
      this.minorChanged = true;
   }

   protected void flushRequirementChanges() {
      AbstractDocument var1 = (AbstractDocument)this.getDocument();

      try {
         var1.readLock();
         View var2 = null;
         boolean var3 = false;
         boolean var4 = false;
         synchronized(this) {
            synchronized(this.stats) {
               int var7 = this.getViewCount();
               if (var7 > 0 && (this.minorChanged || this.estimatedMajorSpan)) {
                  LayoutQueue var8 = this.getLayoutQueue();
                  AsyncBoxView.ChildState var9 = this.getChildState(0);
                  AsyncBoxView.ChildState var10 = this.getChildState(0);
                  float var11 = 0.0F;

                  for(int var12 = 1; var12 < var7; ++var12) {
                     AsyncBoxView.ChildState var13 = this.getChildState(var12);
                     if (this.minorChanged) {
                        if (var13.min > var9.min) {
                           var9 = var13;
                        }

                        if (var13.pref > var10.pref) {
                           var10 = var13;
                        }
                     }

                     if (this.estimatedMajorSpan) {
                        var11 += var13.getMajorSpan();
                     }
                  }

                  if (this.minorChanged) {
                     this.minRequest = var9;
                     this.prefRequest = var10;
                  }

                  if (this.estimatedMajorSpan) {
                     this.majorSpan = var11;
                     this.estimatedMajorSpan = false;
                     this.majorChanged = true;
                  }
               }
            }

            if (this.majorChanged || this.minorChanged) {
               var2 = this.getParent();
               if (var2 != null) {
                  if (this.axis == 0) {
                     var3 = this.majorChanged;
                     var4 = this.minorChanged;
                  } else {
                     var4 = this.majorChanged;
                     var3 = this.minorChanged;
                  }
               }

               this.majorChanged = false;
               this.minorChanged = false;
            }
         }

         if (var2 != null) {
            var2.preferenceChanged(this, var3, var4);
            Container var5 = this.getContainer();
            if (var5 != null) {
               var5.repaint();
            }
         }
      } finally {
         var1.readUnlock();
      }

   }

   public void replace(int var1, int var2, View[] var3) {
      synchronized(this.stats) {
         for(int var5 = 0; var5 < var2; ++var5) {
            AsyncBoxView.ChildState var6 = (AsyncBoxView.ChildState)this.stats.remove(var1);
            float var7 = var6.getMajorSpan();
            var6.getChildView().setParent((View)null);
            if (var7 != 0.0F) {
               this.majorRequirementChange(var6, -var7);
            }
         }

         LayoutQueue var10 = this.getLayoutQueue();
         if (var3 != null) {
            for(int var11 = 0; var11 < var3.length; ++var11) {
               AsyncBoxView.ChildState var12 = this.createChildState(var3[var11]);
               this.stats.add(var1 + var11, var12);
               var10.addTask(var12);
            }
         }

         var10.addTask(this.flushTask);
      }
   }

   protected void loadChildren(ViewFactory var1) {
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

   protected synchronized int getViewIndexAtPosition(int var1, Position.Bias var2) {
      boolean var3 = var2 == Position.Bias.Backward;
      var1 = var3 ? Math.max(0, var1 - 1) : var1;
      Element var4 = this.getElement();
      return var4.getElementIndex(var1);
   }

   protected void updateLayout(DocumentEvent.ElementChange var1, DocumentEvent var2, Shape var3) {
      if (var1 != null) {
         int var4 = Math.max(var1.getIndex() - 1, 0);
         AsyncBoxView.ChildState var5 = this.getChildState(var4);
         this.locator.childChanged(var5);
      }

   }

   public void setParent(View var1) {
      super.setParent(var1);
      if (var1 != null && this.getViewCount() == 0) {
         ViewFactory var2 = this.getViewFactory();
         this.loadChildren(var2);
      }

   }

   public synchronized void preferenceChanged(View var1, boolean var2, boolean var3) {
      if (var1 == null) {
         this.getParent().preferenceChanged(this, var2, var3);
      } else {
         if (this.changing != null) {
            View var4 = this.changing.getChildView();
            if (var4 == var1) {
               this.changing.preferenceChanged(var2, var3);
               return;
            }
         }

         int var7 = this.getViewIndex(var1.getStartOffset(), Position.Bias.Forward);
         AsyncBoxView.ChildState var5 = this.getChildState(var7);
         var5.preferenceChanged(var2, var3);
         LayoutQueue var6 = this.getLayoutQueue();
         var6.addTask(var5);
         var6.addTask(this.flushTask);
      }

   }

   public void setSize(float var1, float var2) {
      this.setSpanOnAxis(0, var1);
      this.setSpanOnAxis(1, var2);
   }

   float getSpanOnAxis(int var1) {
      return var1 == this.getMajorAxis() ? this.majorSpan : this.minorSpan;
   }

   void setSpanOnAxis(int var1, float var2) {
      float var3 = this.getInsetSpan(var1);
      if (var1 == this.getMinorAxis()) {
         float var4 = var2 - var3;
         if (var4 != this.minorSpan) {
            this.minorSpan = var4;
            int var5 = this.getViewCount();
            if (var5 != 0) {
               LayoutQueue var6 = this.getLayoutQueue();

               for(int var7 = 0; var7 < var5; ++var7) {
                  AsyncBoxView.ChildState var8 = this.getChildState(var7);
                  var8.childSizeValid = false;
                  var6.addTask(var8);
               }

               var6.addTask(this.flushTask);
            }
         }
      } else if (this.estimatedMajorSpan) {
         this.majorSpan = var2 - var3;
      }

   }

   public void paint(Graphics var1, Shape var2) {
      synchronized(this.locator) {
         this.locator.setAllocation(var2);
         this.locator.paintChildren(var1);
      }
   }

   public float getPreferredSpan(int var1) {
      float var2 = this.getInsetSpan(var1);
      if (var1 == this.axis) {
         return this.majorSpan + var2;
      } else if (this.prefRequest != null) {
         View var3 = this.prefRequest.getChildView();
         return var3.getPreferredSpan(var1) + var2;
      } else {
         return var2 + 30.0F;
      }
   }

   public float getMinimumSpan(int var1) {
      if (var1 == this.axis) {
         return this.getPreferredSpan(var1);
      } else if (this.minRequest != null) {
         View var2 = this.minRequest.getChildView();
         return var2.getMinimumSpan(var1);
      } else {
         return var1 == 0 ? this.getLeftInset() + this.getRightInset() + 5.0F : this.getTopInset() + this.getBottomInset() + 5.0F;
      }
   }

   public float getMaximumSpan(int var1) {
      return var1 == this.axis ? this.getPreferredSpan(var1) : 2.14748365E9F;
   }

   public int getViewCount() {
      synchronized(this.stats) {
         return this.stats.size();
      }
   }

   public View getView(int var1) {
      AsyncBoxView.ChildState var2 = this.getChildState(var1);
      return var2 != null ? var2.getChildView() : null;
   }

   public Shape getChildAllocation(int var1, Shape var2) {
      Shape var3 = this.locator.getChildAllocation(var1, var2);
      return var3;
   }

   public int getViewIndex(int var1, Position.Bias var2) {
      return this.getViewIndexAtPosition(var1, var2);
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      int var4 = this.getViewIndex(var1, var3);
      Shape var5 = this.locator.getChildAllocation(var4, var2);
      AsyncBoxView.ChildState var6 = this.getChildState(var4);
      synchronized(var6) {
         View var8 = var6.getChildView();
         Shape var9 = var8.modelToView(var1, var5, var3);
         return var9;
      }
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      int var6;
      Shape var7;
      synchronized(this.locator) {
         var6 = this.locator.getViewIndexAtPoint(var1, var2, var3);
         var7 = this.locator.getChildAllocation(var6, var3);
      }

      AsyncBoxView.ChildState var8 = this.getChildState(var6);
      synchronized(var8) {
         View var10 = var8.getChildView();
         int var5 = var10.viewToModel(var1, var2, var7, var4);
         return var5;
      }
   }

   public int getNextVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      if (var1 < -1) {
         throw new BadLocationException("invalid position", var1);
      } else {
         return Utilities.getNextVisualPositionFrom(this, var1, var2, var3, var4, var5);
      }
   }

   class FlushTask implements Runnable {
      public void run() {
         AsyncBoxView.this.flushRequirementChanges();
      }
   }

   public class ChildState implements Runnable {
      private float min;
      private float pref;
      private float max;
      private boolean minorValid;
      private float span;
      private float offset;
      private boolean majorValid;
      private View child;
      private boolean childSizeValid;

      public ChildState(View var2) {
         this.child = var2;
         this.minorValid = false;
         this.majorValid = false;
         this.childSizeValid = false;
         this.child.setParent(AsyncBoxView.this);
      }

      public View getChildView() {
         return this.child;
      }

      public void run() {
         AbstractDocument var1 = (AbstractDocument)AsyncBoxView.this.getDocument();

         try {
            var1.readLock();
            if (!this.minorValid || !this.majorValid || !this.childSizeValid) {
               if (this.child.getParent() != AsyncBoxView.this) {
                  return;
               }

               synchronized(AsyncBoxView.this) {
                  AsyncBoxView.this.changing = this;
               }

               this.updateChild();
               synchronized(AsyncBoxView.this) {
                  AsyncBoxView.this.changing = null;
               }

               this.updateChild();
               return;
            }
         } finally {
            var1.readUnlock();
         }

      }

      void updateChild() {
         boolean var1 = false;
         synchronized(this) {
            if (!this.minorValid) {
               int var3 = AsyncBoxView.this.getMinorAxis();
               this.min = this.child.getMinimumSpan(var3);
               this.pref = this.child.getPreferredSpan(var3);
               this.max = this.child.getMaximumSpan(var3);
               this.minorValid = true;
               var1 = true;
            }
         }

         if (var1) {
            AsyncBoxView.this.minorRequirementChange(this);
         }

         boolean var2 = false;
         float var11 = 0.0F;
         float var5;
         synchronized(this) {
            if (!this.majorValid) {
               var5 = this.span;
               this.span = this.child.getPreferredSpan(AsyncBoxView.this.axis);
               var11 = this.span - var5;
               this.majorValid = true;
               var2 = true;
            }
         }

         if (var2) {
            AsyncBoxView.this.majorRequirementChange(this, var11);
            AsyncBoxView.this.locator.childChanged(this);
         }

         synchronized(this) {
            if (!this.childSizeValid) {
               float var6;
               if (AsyncBoxView.this.axis == 0) {
                  var5 = this.span;
                  var6 = this.getMinorSpan();
               } else {
                  var5 = this.getMinorSpan();
                  var6 = this.span;
               }

               this.childSizeValid = true;
               this.child.setSize(var5, var6);
            }

         }
      }

      public float getMinorSpan() {
         return this.max < AsyncBoxView.this.minorSpan ? this.max : Math.max(this.min, AsyncBoxView.this.minorSpan);
      }

      public float getMinorOffset() {
         if (this.max < AsyncBoxView.this.minorSpan) {
            float var1 = this.child.getAlignment(AsyncBoxView.this.getMinorAxis());
            return (AsyncBoxView.this.minorSpan - this.max) * var1;
         } else {
            return 0.0F;
         }
      }

      public float getMajorSpan() {
         return this.span;
      }

      public float getMajorOffset() {
         return this.offset;
      }

      public void setMajorOffset(float var1) {
         this.offset = var1;
      }

      public void preferenceChanged(boolean var1, boolean var2) {
         if (AsyncBoxView.this.axis == 0) {
            if (var1) {
               this.majorValid = false;
            }

            if (var2) {
               this.minorValid = false;
            }
         } else {
            if (var1) {
               this.minorValid = false;
            }

            if (var2) {
               this.majorValid = false;
            }
         }

         this.childSizeValid = false;
      }

      public boolean isLayoutValid() {
         return this.minorValid && this.majorValid && this.childSizeValid;
      }
   }

   public class ChildLocator {
      protected AsyncBoxView.ChildState lastValidOffset;
      protected Rectangle lastAlloc = new Rectangle();
      protected Rectangle childAlloc = new Rectangle();

      public synchronized void childChanged(AsyncBoxView.ChildState var1) {
         if (this.lastValidOffset == null) {
            this.lastValidOffset = var1;
         } else if (var1.getChildView().getStartOffset() < this.lastValidOffset.getChildView().getStartOffset()) {
            this.lastValidOffset = var1;
         }

      }

      public synchronized void paintChildren(Graphics var1) {
         Rectangle var2 = var1.getClipBounds();
         float var3 = AsyncBoxView.this.axis == 0 ? (float)(var2.x - this.lastAlloc.x) : (float)(var2.y - this.lastAlloc.y);
         int var4 = this.getViewIndexAtVisualOffset(var3);
         int var5 = AsyncBoxView.this.getViewCount();
         float var6 = AsyncBoxView.this.getChildState(var4).getMajorOffset();

         for(int var7 = var4; var7 < var5; ++var7) {
            AsyncBoxView.ChildState var8 = AsyncBoxView.this.getChildState(var7);
            var8.setMajorOffset(var6);
            Shape var9 = this.getChildAllocation(var7);
            if (!this.intersectsClip(var9, var2)) {
               break;
            }

            synchronized(var8) {
               View var11 = var8.getChildView();
               var11.paint(var1, var9);
            }

            var6 += var8.getMajorSpan();
         }

      }

      public synchronized Shape getChildAllocation(int var1, Shape var2) {
         if (var2 == null) {
            return null;
         } else {
            this.setAllocation(var2);
            AsyncBoxView.ChildState var3 = AsyncBoxView.this.getChildState(var1);
            if (this.lastValidOffset == null) {
               this.lastValidOffset = AsyncBoxView.this.getChildState(0);
            }

            if (var3.getChildView().getStartOffset() > this.lastValidOffset.getChildView().getStartOffset()) {
               this.updateChildOffsetsToIndex(var1);
            }

            Shape var4 = this.getChildAllocation(var1);
            return var4;
         }
      }

      public int getViewIndexAtPoint(float var1, float var2, Shape var3) {
         this.setAllocation(var3);
         float var4 = AsyncBoxView.this.axis == 0 ? var1 - (float)this.lastAlloc.x : var2 - (float)this.lastAlloc.y;
         int var5 = this.getViewIndexAtVisualOffset(var4);
         return var5;
      }

      protected Shape getChildAllocation(int var1) {
         AsyncBoxView.ChildState var2 = AsyncBoxView.this.getChildState(var1);
         if (!var2.isLayoutValid()) {
            var2.run();
         }

         if (AsyncBoxView.this.axis == 0) {
            this.childAlloc.x = this.lastAlloc.x + (int)var2.getMajorOffset();
            this.childAlloc.y = this.lastAlloc.y + (int)var2.getMinorOffset();
            this.childAlloc.width = (int)var2.getMajorSpan();
            this.childAlloc.height = (int)var2.getMinorSpan();
         } else {
            this.childAlloc.y = this.lastAlloc.y + (int)var2.getMajorOffset();
            this.childAlloc.x = this.lastAlloc.x + (int)var2.getMinorOffset();
            this.childAlloc.height = (int)var2.getMajorSpan();
            this.childAlloc.width = (int)var2.getMinorSpan();
         }

         Rectangle var10000 = this.childAlloc;
         var10000.x += (int)AsyncBoxView.this.getLeftInset();
         var10000 = this.childAlloc;
         var10000.y += (int)AsyncBoxView.this.getRightInset();
         return this.childAlloc;
      }

      protected void setAllocation(Shape var1) {
         if (var1 instanceof Rectangle) {
            this.lastAlloc.setBounds((Rectangle)var1);
         } else {
            this.lastAlloc.setBounds(var1.getBounds());
         }

         AsyncBoxView.this.setSize((float)this.lastAlloc.width, (float)this.lastAlloc.height);
      }

      protected int getViewIndexAtVisualOffset(float var1) {
         int var2 = AsyncBoxView.this.getViewCount();
         if (var2 > 0) {
            boolean var3 = this.lastValidOffset != null;
            if (this.lastValidOffset == null) {
               this.lastValidOffset = AsyncBoxView.this.getChildState(0);
            }

            int var5;
            if (var1 > AsyncBoxView.this.majorSpan) {
               if (!var3) {
                  return 0;
               }

               int var8 = this.lastValidOffset.getChildView().getStartOffset();
               var5 = AsyncBoxView.this.getViewIndex(var8, Position.Bias.Forward);
               return var5;
            }

            if (var1 > this.lastValidOffset.getMajorOffset()) {
               return this.updateChildOffsets(var1);
            }

            float var4 = 0.0F;

            for(var5 = 0; var5 < var2; ++var5) {
               AsyncBoxView.ChildState var6 = AsyncBoxView.this.getChildState(var5);
               float var7 = var4 + var6.getMajorSpan();
               if (var1 < var7) {
                  return var5;
               }

               var4 = var7;
            }
         }

         return var2 - 1;
      }

      int updateChildOffsets(float var1) {
         int var2 = AsyncBoxView.this.getViewCount();
         int var3 = var2 - 1;
         int var4 = this.lastValidOffset.getChildView().getStartOffset();
         int var5 = AsyncBoxView.this.getViewIndex(var4, Position.Bias.Forward);
         float var6 = this.lastValidOffset.getMajorOffset();
         float var7 = var6;

         for(int var8 = var5; var8 < var2; ++var8) {
            AsyncBoxView.ChildState var9 = AsyncBoxView.this.getChildState(var8);
            var9.setMajorOffset(var7);
            var7 += var9.getMajorSpan();
            if (var1 < var7) {
               var3 = var8;
               this.lastValidOffset = var9;
               break;
            }
         }

         return var3;
      }

      void updateChildOffsetsToIndex(int var1) {
         int var2 = this.lastValidOffset.getChildView().getStartOffset();
         int var3 = AsyncBoxView.this.getViewIndex(var2, Position.Bias.Forward);
         float var4 = this.lastValidOffset.getMajorOffset();

         for(int var5 = var3; var5 <= var1; ++var5) {
            AsyncBoxView.ChildState var6 = AsyncBoxView.this.getChildState(var5);
            var6.setMajorOffset(var4);
            var4 += var6.getMajorSpan();
         }

      }

      boolean intersectsClip(Shape var1, Rectangle var2) {
         Rectangle var3 = var1 instanceof Rectangle ? (Rectangle)var1 : var1.getBounds();
         return var3.intersects(var2) ? this.lastAlloc.intersects(var3) : false;
      }
   }
}
