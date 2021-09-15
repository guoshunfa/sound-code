package sun.awt;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;

public class ModalityEvent extends AWTEvent implements ActiveEvent {
   public static final int MODALITY_PUSHED = 1300;
   public static final int MODALITY_POPPED = 1301;
   private ModalityListener listener;

   public ModalityEvent(Object var1, ModalityListener var2, int var3) {
      super(var1, var3);
      this.listener = var2;
   }

   public void dispatch() {
      switch(this.getID()) {
      case 1300:
         this.listener.modalityPushed(this);
         break;
      case 1301:
         this.listener.modalityPopped(this);
         break;
      default:
         throw new Error("Invalid event id.");
      }

   }
}
