package java.awt.dnd;

import java.awt.Point;
import java.util.EventObject;

public class DragSourceEvent extends EventObject {
   private static final long serialVersionUID = -763287114604032641L;
   private final boolean locationSpecified;
   private final int x;
   private final int y;

   public DragSourceEvent(DragSourceContext var1) {
      super(var1);
      this.locationSpecified = false;
      this.x = 0;
      this.y = 0;
   }

   public DragSourceEvent(DragSourceContext var1, int var2, int var3) {
      super(var1);
      this.locationSpecified = true;
      this.x = var2;
      this.y = var3;
   }

   public DragSourceContext getDragSourceContext() {
      return (DragSourceContext)this.getSource();
   }

   public Point getLocation() {
      return this.locationSpecified ? new Point(this.x, this.y) : null;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }
}
