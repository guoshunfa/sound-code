package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Rectangle;

public class ComponentEvent extends AWTEvent {
   public static final int COMPONENT_FIRST = 100;
   public static final int COMPONENT_LAST = 103;
   public static final int COMPONENT_MOVED = 100;
   public static final int COMPONENT_RESIZED = 101;
   public static final int COMPONENT_SHOWN = 102;
   public static final int COMPONENT_HIDDEN = 103;
   private static final long serialVersionUID = 8101406823902992965L;

   public ComponentEvent(Component var1, int var2) {
      super(var1, var2);
   }

   public Component getComponent() {
      return this.source instanceof Component ? (Component)this.source : null;
   }

   public String paramString() {
      Rectangle var2 = this.source != null ? ((Component)this.source).getBounds() : null;
      String var1;
      switch(this.id) {
      case 100:
         var1 = "COMPONENT_MOVED (" + var2.x + "," + var2.y + " " + var2.width + "x" + var2.height + ")";
         break;
      case 101:
         var1 = "COMPONENT_RESIZED (" + var2.x + "," + var2.y + " " + var2.width + "x" + var2.height + ")";
         break;
      case 102:
         var1 = "COMPONENT_SHOWN";
         break;
      case 103:
         var1 = "COMPONENT_HIDDEN";
         break;
      default:
         var1 = "unknown type";
      }

      return var1;
   }
}
