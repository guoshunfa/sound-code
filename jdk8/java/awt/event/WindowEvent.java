package java.awt.event;

import java.awt.Window;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class WindowEvent extends ComponentEvent {
   public static final int WINDOW_FIRST = 200;
   public static final int WINDOW_OPENED = 200;
   public static final int WINDOW_CLOSING = 201;
   public static final int WINDOW_CLOSED = 202;
   public static final int WINDOW_ICONIFIED = 203;
   public static final int WINDOW_DEICONIFIED = 204;
   public static final int WINDOW_ACTIVATED = 205;
   public static final int WINDOW_DEACTIVATED = 206;
   public static final int WINDOW_GAINED_FOCUS = 207;
   public static final int WINDOW_LOST_FOCUS = 208;
   public static final int WINDOW_STATE_CHANGED = 209;
   public static final int WINDOW_LAST = 209;
   transient Window opposite;
   int oldState;
   int newState;
   private static final long serialVersionUID = -1567959133147912127L;

   public WindowEvent(Window var1, int var2, Window var3, int var4, int var5) {
      super(var1, var2);
      this.opposite = var3;
      this.oldState = var4;
      this.newState = var5;
   }

   public WindowEvent(Window var1, int var2, Window var3) {
      this(var1, var2, var3, 0, 0);
   }

   public WindowEvent(Window var1, int var2, int var3, int var4) {
      this(var1, var2, (Window)null, var3, var4);
   }

   public WindowEvent(Window var1, int var2) {
      this(var1, var2, (Window)null, 0, 0);
   }

   public Window getWindow() {
      return this.source instanceof Window ? (Window)this.source : null;
   }

   public Window getOppositeWindow() {
      if (this.opposite == null) {
         return null;
      } else {
         return SunToolkit.targetToAppContext(this.opposite) == AppContext.getAppContext() ? this.opposite : null;
      }
   }

   public int getOldState() {
      return this.oldState;
   }

   public int getNewState() {
      return this.newState;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 200:
         var1 = "WINDOW_OPENED";
         break;
      case 201:
         var1 = "WINDOW_CLOSING";
         break;
      case 202:
         var1 = "WINDOW_CLOSED";
         break;
      case 203:
         var1 = "WINDOW_ICONIFIED";
         break;
      case 204:
         var1 = "WINDOW_DEICONIFIED";
         break;
      case 205:
         var1 = "WINDOW_ACTIVATED";
         break;
      case 206:
         var1 = "WINDOW_DEACTIVATED";
         break;
      case 207:
         var1 = "WINDOW_GAINED_FOCUS";
         break;
      case 208:
         var1 = "WINDOW_LOST_FOCUS";
         break;
      case 209:
         var1 = "WINDOW_STATE_CHANGED";
         break;
      default:
         var1 = "unknown type";
      }

      var1 = var1 + ",opposite=" + this.getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      return var1;
   }
}
