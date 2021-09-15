package java.awt.event;

import java.awt.Component;
import java.awt.Container;

public class ContainerEvent extends ComponentEvent {
   public static final int CONTAINER_FIRST = 300;
   public static final int CONTAINER_LAST = 301;
   public static final int COMPONENT_ADDED = 300;
   public static final int COMPONENT_REMOVED = 301;
   Component child;
   private static final long serialVersionUID = -4114942250539772041L;

   public ContainerEvent(Component var1, int var2, Component var3) {
      super(var1, var2);
      this.child = var3;
   }

   public Container getContainer() {
      return this.source instanceof Container ? (Container)this.source : null;
   }

   public Component getChild() {
      return this.child;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 300:
         var1 = "COMPONENT_ADDED";
         break;
      case 301:
         var1 = "COMPONENT_REMOVED";
         break;
      default:
         var1 = "unknown type";
      }

      return var1 + ",child=" + this.child.getName();
   }
}
