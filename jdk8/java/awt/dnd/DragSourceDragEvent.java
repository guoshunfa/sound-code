package java.awt.dnd;

public class DragSourceDragEvent extends DragSourceEvent {
   private static final long serialVersionUID = 481346297933902471L;
   private static final int JDK_1_3_MODIFIERS = 63;
   private static final int JDK_1_4_MODIFIERS = 16320;
   private int targetActions = 0;
   private int dropAction = 0;
   private int gestureModifiers = 0;
   private boolean invalidModifiers;

   public DragSourceDragEvent(DragSourceContext var1, int var2, int var3, int var4) {
      super(var1);
      this.targetActions = var3;
      this.gestureModifiers = var4;
      this.dropAction = var2;
      if ((var4 & -16384) != 0) {
         this.invalidModifiers = true;
      } else if (this.getGestureModifiers() != 0 && this.getGestureModifiersEx() == 0) {
         this.setNewModifiers();
      } else if (this.getGestureModifiers() == 0 && this.getGestureModifiersEx() != 0) {
         this.setOldModifiers();
      } else {
         this.invalidModifiers = true;
      }

   }

   public DragSourceDragEvent(DragSourceContext var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var5, var6);
      this.targetActions = var3;
      this.gestureModifiers = var4;
      this.dropAction = var2;
      if ((var4 & -16384) != 0) {
         this.invalidModifiers = true;
      } else if (this.getGestureModifiers() != 0 && this.getGestureModifiersEx() == 0) {
         this.setNewModifiers();
      } else if (this.getGestureModifiers() == 0 && this.getGestureModifiersEx() != 0) {
         this.setOldModifiers();
      } else {
         this.invalidModifiers = true;
      }

   }

   public int getTargetActions() {
      return this.targetActions;
   }

   public int getGestureModifiers() {
      return this.invalidModifiers ? this.gestureModifiers : this.gestureModifiers & 63;
   }

   public int getGestureModifiersEx() {
      return this.invalidModifiers ? this.gestureModifiers : this.gestureModifiers & 16320;
   }

   public int getUserAction() {
      return this.dropAction;
   }

   public int getDropAction() {
      return this.targetActions & this.getDragSourceContext().getSourceActions();
   }

   private void setNewModifiers() {
      if ((this.gestureModifiers & 16) != 0) {
         this.gestureModifiers |= 1024;
      }

      if ((this.gestureModifiers & 8) != 0) {
         this.gestureModifiers |= 2048;
      }

      if ((this.gestureModifiers & 4) != 0) {
         this.gestureModifiers |= 4096;
      }

      if ((this.gestureModifiers & 1) != 0) {
         this.gestureModifiers |= 64;
      }

      if ((this.gestureModifiers & 2) != 0) {
         this.gestureModifiers |= 128;
      }

      if ((this.gestureModifiers & 32) != 0) {
         this.gestureModifiers |= 8192;
      }

   }

   private void setOldModifiers() {
      if ((this.gestureModifiers & 1024) != 0) {
         this.gestureModifiers |= 16;
      }

      if ((this.gestureModifiers & 2048) != 0) {
         this.gestureModifiers |= 8;
      }

      if ((this.gestureModifiers & 4096) != 0) {
         this.gestureModifiers |= 4;
      }

      if ((this.gestureModifiers & 64) != 0) {
         this.gestureModifiers |= 1;
      }

      if ((this.gestureModifiers & 128) != 0) {
         this.gestureModifiers |= 2;
      }

      if ((this.gestureModifiers & 8192) != 0) {
         this.gestureModifiers |= 32;
      }

   }
}
