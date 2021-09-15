package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollPanePeer;
import java.io.Serializable;
import sun.awt.AWTAccessor;

public class ScrollPaneAdjustable implements Adjustable, Serializable {
   private ScrollPane sp;
   private int orientation;
   private int value;
   private int minimum;
   private int maximum;
   private int visibleAmount;
   private transient boolean isAdjusting;
   private int unitIncrement = 1;
   private int blockIncrement = 1;
   private AdjustmentListener adjustmentListener;
   private static final String SCROLLPANE_ONLY = "Can be set by scrollpane only";
   private static final long serialVersionUID = -3359745691033257079L;

   private static native void initIDs();

   ScrollPaneAdjustable(ScrollPane var1, AdjustmentListener var2, int var3) {
      this.sp = var1;
      this.orientation = var3;
      this.addAdjustmentListener(var2);
   }

   void setSpan(int var1, int var2, int var3) {
      this.minimum = var1;
      this.maximum = Math.max(var2, this.minimum + 1);
      this.visibleAmount = Math.min(var3, this.maximum - this.minimum);
      this.visibleAmount = Math.max(this.visibleAmount, 1);
      this.blockIncrement = Math.max((int)((double)var3 * 0.9D), 1);
      this.setValue(this.value);
   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setMinimum(int var1) {
      throw new AWTError("Can be set by scrollpane only");
   }

   public int getMinimum() {
      return 0;
   }

   public void setMaximum(int var1) {
      throw new AWTError("Can be set by scrollpane only");
   }

   public int getMaximum() {
      return this.maximum;
   }

   public synchronized void setUnitIncrement(int var1) {
      if (var1 != this.unitIncrement) {
         this.unitIncrement = var1;
         if (this.sp.peer != null) {
            ScrollPanePeer var2 = (ScrollPanePeer)this.sp.peer;
            var2.setUnitIncrement(this, var1);
         }
      }

   }

   public int getUnitIncrement() {
      return this.unitIncrement;
   }

   public synchronized void setBlockIncrement(int var1) {
      this.blockIncrement = var1;
   }

   public int getBlockIncrement() {
      return this.blockIncrement;
   }

   public void setVisibleAmount(int var1) {
      throw new AWTError("Can be set by scrollpane only");
   }

   public int getVisibleAmount() {
      return this.visibleAmount;
   }

   public void setValueIsAdjusting(boolean var1) {
      if (this.isAdjusting != var1) {
         this.isAdjusting = var1;
         AdjustmentEvent var2 = new AdjustmentEvent(this, 601, 5, this.value, var1);
         this.adjustmentListener.adjustmentValueChanged(var2);
      }

   }

   public boolean getValueIsAdjusting() {
      return this.isAdjusting;
   }

   public void setValue(int var1) {
      this.setTypedValue(var1, 5);
   }

   private void setTypedValue(int var1, int var2) {
      var1 = Math.max(var1, this.minimum);
      var1 = Math.min(var1, this.maximum - this.visibleAmount);
      if (var1 != this.value) {
         this.value = var1;
         AdjustmentEvent var3 = new AdjustmentEvent(this, 601, var2, this.value, this.isAdjusting);
         this.adjustmentListener.adjustmentValueChanged(var3);
      }

   }

   public int getValue() {
      return this.value;
   }

   public synchronized void addAdjustmentListener(AdjustmentListener var1) {
      if (var1 != null) {
         this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, var1);
      }
   }

   public synchronized void removeAdjustmentListener(AdjustmentListener var1) {
      if (var1 != null) {
         this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, var1);
      }
   }

   public synchronized AdjustmentListener[] getAdjustmentListeners() {
      return (AdjustmentListener[])((AdjustmentListener[])AWTEventMulticaster.getListeners(this.adjustmentListener, AdjustmentListener.class));
   }

   public String toString() {
      return this.getClass().getName() + "[" + this.paramString() + "]";
   }

   public String paramString() {
      return (this.orientation == 1 ? "vertical," : "horizontal,") + "[0.." + this.maximum + "],val=" + this.value + ",vis=" + this.visibleAmount + ",unit=" + this.unitIncrement + ",block=" + this.blockIncrement + ",isAdjusting=" + this.isAdjusting;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setScrollPaneAdjustableAccessor(new AWTAccessor.ScrollPaneAdjustableAccessor() {
         public void setTypedValue(ScrollPaneAdjustable var1, int var2, int var3) {
            var1.setTypedValue(var2, var3);
         }
      });
   }
}
