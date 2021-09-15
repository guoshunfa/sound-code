package javax.swing;

import java.beans.Transient;
import java.io.Serializable;
import java.util.BitSet;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DefaultListSelectionModel implements ListSelectionModel, Cloneable, Serializable {
   private static final int MIN = -1;
   private static final int MAX = Integer.MAX_VALUE;
   private int selectionMode = 2;
   private int minIndex = Integer.MAX_VALUE;
   private int maxIndex = -1;
   private int anchorIndex = -1;
   private int leadIndex = -1;
   private int firstAdjustedIndex = Integer.MAX_VALUE;
   private int lastAdjustedIndex = -1;
   private boolean isAdjusting = false;
   private int firstChangedIndex = Integer.MAX_VALUE;
   private int lastChangedIndex = -1;
   private BitSet value = new BitSet(32);
   protected EventListenerList listenerList = new EventListenerList();
   protected boolean leadAnchorNotificationEnabled = true;

   public int getMinSelectionIndex() {
      return this.isSelectionEmpty() ? -1 : this.minIndex;
   }

   public int getMaxSelectionIndex() {
      return this.maxIndex;
   }

   public boolean getValueIsAdjusting() {
      return this.isAdjusting;
   }

   public int getSelectionMode() {
      return this.selectionMode;
   }

   public void setSelectionMode(int var1) {
      switch(var1) {
      case 0:
      case 1:
      case 2:
         this.selectionMode = var1;
         return;
      default:
         throw new IllegalArgumentException("invalid selectionMode");
      }
   }

   public boolean isSelectedIndex(int var1) {
      return var1 >= this.minIndex && var1 <= this.maxIndex ? this.value.get(var1) : false;
   }

   public boolean isSelectionEmpty() {
      return this.minIndex > this.maxIndex;
   }

   public void addListSelectionListener(ListSelectionListener var1) {
      this.listenerList.add(ListSelectionListener.class, var1);
   }

   public void removeListSelectionListener(ListSelectionListener var1) {
      this.listenerList.remove(ListSelectionListener.class, var1);
   }

   public ListSelectionListener[] getListSelectionListeners() {
      return (ListSelectionListener[])this.listenerList.getListeners(ListSelectionListener.class);
   }

   protected void fireValueChanged(boolean var1) {
      if (this.lastChangedIndex != -1) {
         int var2 = this.firstChangedIndex;
         int var3 = this.lastChangedIndex;
         this.firstChangedIndex = Integer.MAX_VALUE;
         this.lastChangedIndex = -1;
         this.fireValueChanged(var2, var3, var1);
      }
   }

   protected void fireValueChanged(int var1, int var2) {
      this.fireValueChanged(var1, var2, this.getValueIsAdjusting());
   }

   protected void fireValueChanged(int var1, int var2, boolean var3) {
      Object[] var4 = this.listenerList.getListenerList();
      ListSelectionEvent var5 = null;

      for(int var6 = var4.length - 2; var6 >= 0; var6 -= 2) {
         if (var4[var6] == ListSelectionListener.class) {
            if (var5 == null) {
               var5 = new ListSelectionEvent(this, var1, var2, var3);
            }

            ((ListSelectionListener)var4[var6 + 1]).valueChanged(var5);
         }
      }

   }

   private void fireValueChanged() {
      if (this.lastAdjustedIndex != -1) {
         if (this.getValueIsAdjusting()) {
            this.firstChangedIndex = Math.min(this.firstChangedIndex, this.firstAdjustedIndex);
            this.lastChangedIndex = Math.max(this.lastChangedIndex, this.lastAdjustedIndex);
         }

         int var1 = this.firstAdjustedIndex;
         int var2 = this.lastAdjustedIndex;
         this.firstAdjustedIndex = Integer.MAX_VALUE;
         this.lastAdjustedIndex = -1;
         this.fireValueChanged(var1, var2);
      }
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   private void markAsDirty(int var1) {
      if (var1 != -1) {
         this.firstAdjustedIndex = Math.min(this.firstAdjustedIndex, var1);
         this.lastAdjustedIndex = Math.max(this.lastAdjustedIndex, var1);
      }
   }

   private void set(int var1) {
      if (!this.value.get(var1)) {
         this.value.set(var1);
         this.markAsDirty(var1);
         this.minIndex = Math.min(this.minIndex, var1);
         this.maxIndex = Math.max(this.maxIndex, var1);
      }
   }

   private void clear(int var1) {
      if (this.value.get(var1)) {
         this.value.clear(var1);
         this.markAsDirty(var1);
         if (var1 == this.minIndex) {
            ++this.minIndex;

            while(this.minIndex <= this.maxIndex && !this.value.get(this.minIndex)) {
               ++this.minIndex;
            }
         }

         if (var1 == this.maxIndex) {
            --this.maxIndex;

            while(this.minIndex <= this.maxIndex && !this.value.get(this.maxIndex)) {
               --this.maxIndex;
            }
         }

         if (this.isSelectionEmpty()) {
            this.minIndex = Integer.MAX_VALUE;
            this.maxIndex = -1;
         }

      }
   }

   public void setLeadAnchorNotificationEnabled(boolean var1) {
      this.leadAnchorNotificationEnabled = var1;
   }

   public boolean isLeadAnchorNotificationEnabled() {
      return this.leadAnchorNotificationEnabled;
   }

   private void updateLeadAnchorIndices(int var1, int var2) {
      if (this.leadAnchorNotificationEnabled) {
         if (this.anchorIndex != var1) {
            this.markAsDirty(this.anchorIndex);
            this.markAsDirty(var1);
         }

         if (this.leadIndex != var2) {
            this.markAsDirty(this.leadIndex);
            this.markAsDirty(var2);
         }
      }

      this.anchorIndex = var1;
      this.leadIndex = var2;
   }

   private boolean contains(int var1, int var2, int var3) {
      return var3 >= var1 && var3 <= var2;
   }

   private void changeSelection(int var1, int var2, int var3, int var4, boolean var5) {
      for(int var6 = Math.min(var3, var1); var6 <= Math.max(var4, var2); ++var6) {
         boolean var7 = this.contains(var1, var2, var6);
         boolean var8 = this.contains(var3, var4, var6);
         if (var8 && var7) {
            if (var5) {
               var7 = false;
            } else {
               var8 = false;
            }
         }

         if (var8) {
            this.set(var6);
         }

         if (var7) {
            this.clear(var6);
         }
      }

      this.fireValueChanged();
   }

   private void changeSelection(int var1, int var2, int var3, int var4) {
      this.changeSelection(var1, var2, var3, var4, true);
   }

   public void clearSelection() {
      this.removeSelectionIntervalImpl(this.minIndex, this.maxIndex, false);
   }

   public void setSelectionInterval(int var1, int var2) {
      if (var1 != -1 && var2 != -1) {
         if (this.getSelectionMode() == 0) {
            var1 = var2;
         }

         this.updateLeadAnchorIndices(var1, var2);
         int var3 = this.minIndex;
         int var4 = this.maxIndex;
         int var5 = Math.min(var1, var2);
         int var6 = Math.max(var1, var2);
         this.changeSelection(var3, var4, var5, var6);
      }
   }

   public void addSelectionInterval(int var1, int var2) {
      if (var1 != -1 && var2 != -1) {
         if (this.getSelectionMode() == 0) {
            this.setSelectionInterval(var1, var2);
         } else {
            this.updateLeadAnchorIndices(var1, var2);
            int var3 = Integer.MAX_VALUE;
            byte var4 = -1;
            int var5 = Math.min(var1, var2);
            int var6 = Math.max(var1, var2);
            if (this.getSelectionMode() != 1 || var6 >= this.minIndex - 1 && var5 <= this.maxIndex + 1) {
               this.changeSelection(var3, var4, var5, var6);
            } else {
               this.setSelectionInterval(var1, var2);
            }
         }
      }
   }

   public void removeSelectionInterval(int var1, int var2) {
      this.removeSelectionIntervalImpl(var1, var2, true);
   }

   private void removeSelectionIntervalImpl(int var1, int var2, boolean var3) {
      if (var1 != -1 && var2 != -1) {
         if (var3) {
            this.updateLeadAnchorIndices(var1, var2);
         }

         int var4 = Math.min(var1, var2);
         int var5 = Math.max(var1, var2);
         int var6 = Integer.MAX_VALUE;
         byte var7 = -1;
         if (this.getSelectionMode() != 2 && var4 > this.minIndex && var5 < this.maxIndex) {
            var5 = this.maxIndex;
         }

         this.changeSelection(var4, var5, var6, var7);
      }
   }

   private void setState(int var1, boolean var2) {
      if (var2) {
         this.set(var1);
      } else {
         this.clear(var1);
      }

   }

   public void insertIndexInterval(int var1, int var2, boolean var3) {
      int var4 = var3 ? var1 : var1 + 1;
      int var5 = var4 + var2 - 1;

      for(int var6 = this.maxIndex; var6 >= var4; --var6) {
         this.setState(var6 + var2, this.value.get(var6));
      }

      boolean var9 = this.getSelectionMode() == 0 ? false : this.value.get(var1);

      int var7;
      for(var7 = var4; var7 <= var5; ++var7) {
         this.setState(var7, var9);
      }

      var7 = this.leadIndex;
      if (var7 > var1 || var3 && var7 == var1) {
         var7 = this.leadIndex + var2;
      }

      int var8 = this.anchorIndex;
      if (var8 > var1 || var3 && var8 == var1) {
         var8 = this.anchorIndex + var2;
      }

      if (var7 != this.leadIndex || var8 != this.anchorIndex) {
         this.updateLeadAnchorIndices(var8, var7);
      }

      this.fireValueChanged();
   }

   public void removeIndexInterval(int var1, int var2) {
      int var3 = Math.min(var1, var2);
      int var4 = Math.max(var1, var2);
      int var5 = var4 - var3 + 1;

      int var6;
      for(var6 = var3; var6 <= this.maxIndex; ++var6) {
         this.setState(var6, this.value.get(var6 + var5));
      }

      var6 = this.leadIndex;
      if (var6 != 0 || var3 != 0) {
         if (var6 > var4) {
            var6 = this.leadIndex - var5;
         } else if (var6 >= var3) {
            var6 = var3 - 1;
         }
      }

      int var7 = this.anchorIndex;
      if (var7 != 0 || var3 != 0) {
         if (var7 > var4) {
            var7 = this.anchorIndex - var5;
         } else if (var7 >= var3) {
            var7 = var3 - 1;
         }
      }

      if (var6 != this.leadIndex || var7 != this.anchorIndex) {
         this.updateLeadAnchorIndices(var7, var6);
      }

      this.fireValueChanged();
   }

   public void setValueIsAdjusting(boolean var1) {
      if (var1 != this.isAdjusting) {
         this.isAdjusting = var1;
         this.fireValueChanged(var1);
      }

   }

   public String toString() {
      String var1 = (this.getValueIsAdjusting() ? "~" : "=") + this.value.toString();
      return this.getClass().getName() + " " + Integer.toString(this.hashCode()) + " " + var1;
   }

   public Object clone() throws CloneNotSupportedException {
      DefaultListSelectionModel var1 = (DefaultListSelectionModel)super.clone();
      var1.value = (BitSet)this.value.clone();
      var1.listenerList = new EventListenerList();
      return var1;
   }

   @Transient
   public int getAnchorSelectionIndex() {
      return this.anchorIndex;
   }

   @Transient
   public int getLeadSelectionIndex() {
      return this.leadIndex;
   }

   public void setAnchorSelectionIndex(int var1) {
      this.updateLeadAnchorIndices(var1, this.leadIndex);
      this.fireValueChanged();
   }

   public void moveLeadSelectionIndex(int var1) {
      if (var1 != -1 || this.anchorIndex == -1) {
         this.updateLeadAnchorIndices(this.anchorIndex, var1);
         this.fireValueChanged();
      }
   }

   public void setLeadSelectionIndex(int var1) {
      int var2 = this.anchorIndex;
      if (var1 == -1) {
         if (var2 == -1) {
            this.updateLeadAnchorIndices(var2, var1);
            this.fireValueChanged();
         }

      } else if (var2 != -1) {
         if (this.leadIndex == -1) {
            this.leadIndex = var1;
         }

         boolean var3 = this.value.get(this.anchorIndex);
         if (this.getSelectionMode() == 0) {
            var2 = var1;
            var3 = true;
         }

         int var4 = Math.min(this.anchorIndex, this.leadIndex);
         int var5 = Math.max(this.anchorIndex, this.leadIndex);
         int var6 = Math.min(var2, var1);
         int var7 = Math.max(var2, var1);
         this.updateLeadAnchorIndices(var2, var1);
         if (var3) {
            this.changeSelection(var4, var5, var6, var7);
         } else {
            this.changeSelection(var6, var7, var4, var5, false);
         }

      }
   }
}
