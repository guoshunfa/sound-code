package javax.swing;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultBoundedRangeModel implements BoundedRangeModel, Serializable {
   protected transient ChangeEvent changeEvent = null;
   protected EventListenerList listenerList = new EventListenerList();
   private int value = 0;
   private int extent = 0;
   private int min = 0;
   private int max = 100;
   private boolean isAdjusting = false;

   public DefaultBoundedRangeModel() {
   }

   public DefaultBoundedRangeModel(int var1, int var2, int var3, int var4) {
      if (var4 >= var3 && var1 >= var3 && var1 + var2 >= var1 && var1 + var2 <= var4) {
         this.value = var1;
         this.extent = var2;
         this.min = var3;
         this.max = var4;
      } else {
         throw new IllegalArgumentException("invalid range properties");
      }
   }

   public int getValue() {
      return this.value;
   }

   public int getExtent() {
      return this.extent;
   }

   public int getMinimum() {
      return this.min;
   }

   public int getMaximum() {
      return this.max;
   }

   public void setValue(int var1) {
      var1 = Math.min(var1, Integer.MAX_VALUE - this.extent);
      int var2 = Math.max(var1, this.min);
      if (var2 + this.extent > this.max) {
         var2 = this.max - this.extent;
      }

      this.setRangeProperties(var2, this.extent, this.min, this.max, this.isAdjusting);
   }

   public void setExtent(int var1) {
      int var2 = Math.max(0, var1);
      if (this.value + var2 > this.max) {
         var2 = this.max - this.value;
      }

      this.setRangeProperties(this.value, var2, this.min, this.max, this.isAdjusting);
   }

   public void setMinimum(int var1) {
      int var2 = Math.max(var1, this.max);
      int var3 = Math.max(var1, this.value);
      int var4 = Math.min(var2 - var3, this.extent);
      this.setRangeProperties(var3, var4, var1, var2, this.isAdjusting);
   }

   public void setMaximum(int var1) {
      int var2 = Math.min(var1, this.min);
      int var3 = Math.min(var1 - var2, this.extent);
      int var4 = Math.min(var1 - var3, this.value);
      this.setRangeProperties(var4, var3, var2, var1, this.isAdjusting);
   }

   public void setValueIsAdjusting(boolean var1) {
      this.setRangeProperties(this.value, this.extent, this.min, this.max, var1);
   }

   public boolean getValueIsAdjusting() {
      return this.isAdjusting;
   }

   public void setRangeProperties(int var1, int var2, int var3, int var4, boolean var5) {
      if (var3 > var4) {
         var3 = var4;
      }

      if (var1 > var4) {
         var4 = var1;
      }

      if (var1 < var3) {
         var3 = var1;
      }

      if ((long)var2 + (long)var1 > (long)var4) {
         var2 = var4 - var1;
      }

      if (var2 < 0) {
         var2 = 0;
      }

      boolean var6 = var1 != this.value || var2 != this.extent || var3 != this.min || var4 != this.max || var5 != this.isAdjusting;
      if (var6) {
         this.value = var1;
         this.extent = var2;
         this.min = var3;
         this.max = var4;
         this.isAdjusting = var5;
         this.fireStateChanged();
      }

   }

   public void addChangeListener(ChangeListener var1) {
      this.listenerList.add(ChangeListener.class, var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.listenerList.remove(ChangeListener.class, var1);
   }

   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
   }

   protected void fireStateChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public String toString() {
      String var1 = "value=" + this.getValue() + ", extent=" + this.getExtent() + ", min=" + this.getMinimum() + ", max=" + this.getMaximum() + ", adj=" + this.getValueIsAdjusting();
      return this.getClass().getName() + "[" + var1 + "]";
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }
}
