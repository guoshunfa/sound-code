package java.awt.dnd;

public class DragSourceDropEvent extends DragSourceEvent {
   private static final long serialVersionUID = -5571321229470821891L;
   private boolean dropSuccess;
   private int dropAction = 0;

   public DragSourceDropEvent(DragSourceContext var1, int var2, boolean var3) {
      super(var1);
      this.dropSuccess = var3;
      this.dropAction = var2;
   }

   public DragSourceDropEvent(DragSourceContext var1, int var2, boolean var3, int var4, int var5) {
      super(var1, var4, var5);
      this.dropSuccess = var3;
      this.dropAction = var2;
   }

   public DragSourceDropEvent(DragSourceContext var1) {
      super(var1);
      this.dropSuccess = false;
   }

   public boolean getDropSuccess() {
      return this.dropSuccess;
   }

   public int getDropAction() {
      return this.dropAction;
   }
}
