package java.awt.event;

import java.awt.Component;
import java.awt.Rectangle;

public class PaintEvent extends ComponentEvent {
   public static final int PAINT_FIRST = 800;
   public static final int PAINT_LAST = 801;
   public static final int PAINT = 800;
   public static final int UPDATE = 801;
   Rectangle updateRect;
   private static final long serialVersionUID = 1267492026433337593L;

   public PaintEvent(Component var1, int var2, Rectangle var3) {
      super(var1, var2);
      this.updateRect = var3;
   }

   public Rectangle getUpdateRect() {
      return this.updateRect;
   }

   public void setUpdateRect(Rectangle var1) {
      this.updateRect = var1;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 800:
         var1 = "PAINT";
         break;
      case 801:
         var1 = "UPDATE";
         break;
      default:
         var1 = "unknown type";
      }

      return var1 + ",updateRect=" + (this.updateRect != null ? this.updateRect.toString() : "null");
   }
}
