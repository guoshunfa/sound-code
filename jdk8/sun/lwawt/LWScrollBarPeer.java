package sun.lwawt;

import java.awt.Adjustable;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollbarPeer;
import javax.swing.JScrollBar;

final class LWScrollBarPeer extends LWComponentPeer<Scrollbar, JScrollBar> implements ScrollbarPeer, AdjustmentListener {
   private int currentValue;

   LWScrollBarPeer(Scrollbar var1, PlatformComponent var2) {
      super(var1, var2);
   }

   JScrollBar createDelegate() {
      return new JScrollBar();
   }

   void initializeImpl() {
      super.initializeImpl();
      Scrollbar var1 = (Scrollbar)this.getTarget();
      this.setLineIncrement(var1.getUnitIncrement());
      this.setPageIncrement(var1.getBlockIncrement());
      this.setValues(var1.getValue(), var1.getVisibleAmount(), var1.getMinimum(), var1.getMaximum());
      int var2 = var1.getOrientation();
      JScrollBar var3 = (JScrollBar)this.getDelegate();
      synchronized(this.getDelegateLock()) {
         var3.setOrientation(var2 == 0 ? 0 : 1);
         var3.addAdjustmentListener(this);
      }
   }

   public void setValues(int var1, int var2, int var3, int var4) {
      synchronized(this.getDelegateLock()) {
         this.currentValue = var1;
         ((JScrollBar)this.getDelegate()).setValues(var1, var2, var3, var4);
      }
   }

   public void setLineIncrement(int var1) {
      synchronized(this.getDelegateLock()) {
         ((JScrollBar)this.getDelegate()).setUnitIncrement(var1);
      }
   }

   public void setPageIncrement(int var1) {
      synchronized(this.getDelegateLock()) {
         ((JScrollBar)this.getDelegate()).setBlockIncrement(var1);
      }
   }

   public void adjustmentValueChanged(AdjustmentEvent var1) {
      int var2 = var1.getValue();
      synchronized(this.getDelegateLock()) {
         if (this.currentValue == var2) {
            return;
         }

         this.currentValue = var2;
      }

      ((Scrollbar)this.getTarget()).setValueIsAdjusting(var1.getValueIsAdjusting());
      ((Scrollbar)this.getTarget()).setValue(var2);
      this.postEvent(new AdjustmentEvent((Adjustable)this.getTarget(), var1.getID(), var1.getAdjustmentType(), var2, var1.getValueIsAdjusting()));
   }
}
