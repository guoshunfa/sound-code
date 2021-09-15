package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollPanePeer;
import java.io.Serializable;

class PeerFixer implements AdjustmentListener, Serializable {
   private static final long serialVersionUID = 7051237413532574756L;
   private ScrollPane scroller;

   PeerFixer(ScrollPane var1) {
      this.scroller = var1;
   }

   public void adjustmentValueChanged(AdjustmentEvent var1) {
      Adjustable var2 = var1.getAdjustable();
      int var3 = var1.getValue();
      ScrollPanePeer var4 = (ScrollPanePeer)this.scroller.peer;
      if (var4 != null) {
         var4.setValue(var2, var3);
      }

      Component var5 = this.scroller.getComponent(0);
      switch(var2.getOrientation()) {
      case 0:
         var5.move(-var3, var5.getLocation().y);
         break;
      case 1:
         var5.move(var5.getLocation().x, -var3);
         break;
      default:
         throw new IllegalArgumentException("Illegal adjustable orientation");
      }

   }
}
