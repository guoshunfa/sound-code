package sun.awt;

import java.awt.Component;
import java.awt.event.FocusEvent;

public class CausedFocusEvent extends FocusEvent {
   private final CausedFocusEvent.Cause cause;

   public CausedFocusEvent.Cause getCause() {
      return this.cause;
   }

   public String toString() {
      return "java.awt.FocusEvent[" + super.paramString() + ",cause=" + this.cause + "] on " + this.getSource();
   }

   public CausedFocusEvent(Component var1, int var2, boolean var3, Component var4, CausedFocusEvent.Cause var5) {
      super(var1, var2, var3, var4);
      if (var5 == null) {
         var5 = CausedFocusEvent.Cause.UNKNOWN;
      }

      this.cause = var5;
   }

   public static FocusEvent retarget(FocusEvent var0, Component var1) {
      return var0 == null ? null : new CausedFocusEvent(var1, var0.getID(), var0.isTemporary(), var0.getOppositeComponent(), var0 instanceof CausedFocusEvent ? ((CausedFocusEvent)var0).getCause() : CausedFocusEvent.Cause.RETARGETED);
   }

   public static enum Cause {
      UNKNOWN,
      MOUSE_EVENT,
      TRAVERSAL,
      TRAVERSAL_UP,
      TRAVERSAL_DOWN,
      TRAVERSAL_FORWARD,
      TRAVERSAL_BACKWARD,
      MANUAL_REQUEST,
      AUTOMATIC_TRAVERSE,
      ROLLBACK,
      NATIVE_SYSTEM,
      ACTIVATION,
      CLEAR_GLOBAL_FOCUS_OWNER,
      RETARGETED;
   }
}
