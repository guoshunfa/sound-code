package javax.swing.event;

import java.awt.AWTEvent;
import javax.swing.JInternalFrame;

public class InternalFrameEvent extends AWTEvent {
   public static final int INTERNAL_FRAME_FIRST = 25549;
   public static final int INTERNAL_FRAME_LAST = 25555;
   public static final int INTERNAL_FRAME_OPENED = 25549;
   public static final int INTERNAL_FRAME_CLOSING = 25550;
   public static final int INTERNAL_FRAME_CLOSED = 25551;
   public static final int INTERNAL_FRAME_ICONIFIED = 25552;
   public static final int INTERNAL_FRAME_DEICONIFIED = 25553;
   public static final int INTERNAL_FRAME_ACTIVATED = 25554;
   public static final int INTERNAL_FRAME_DEACTIVATED = 25555;

   public InternalFrameEvent(JInternalFrame var1, int var2) {
      super(var1, var2);
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 25549:
         var1 = "INTERNAL_FRAME_OPENED";
         break;
      case 25550:
         var1 = "INTERNAL_FRAME_CLOSING";
         break;
      case 25551:
         var1 = "INTERNAL_FRAME_CLOSED";
         break;
      case 25552:
         var1 = "INTERNAL_FRAME_ICONIFIED";
         break;
      case 25553:
         var1 = "INTERNAL_FRAME_DEICONIFIED";
         break;
      case 25554:
         var1 = "INTERNAL_FRAME_ACTIVATED";
         break;
      case 25555:
         var1 = "INTERNAL_FRAME_DEACTIVATED";
         break;
      default:
         var1 = "unknown type";
      }

      return var1;
   }

   public JInternalFrame getInternalFrame() {
      return this.source instanceof JInternalFrame ? (JInternalFrame)this.source : null;
   }
}
