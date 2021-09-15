package java.awt.dnd;

import java.util.EventObject;

public class DropTargetEvent extends EventObject {
   private static final long serialVersionUID = 2821229066521922993L;
   protected DropTargetContext context;

   public DropTargetEvent(DropTargetContext var1) {
      super(var1.getDropTarget());
      this.context = var1;
   }

   public DropTargetContext getDropTargetContext() {
      return this.context;
   }
}
