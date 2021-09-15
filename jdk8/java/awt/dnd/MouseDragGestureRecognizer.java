package java.awt.dnd;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class MouseDragGestureRecognizer extends DragGestureRecognizer implements MouseListener, MouseMotionListener {
   private static final long serialVersionUID = 6220099344182281120L;

   protected MouseDragGestureRecognizer(DragSource var1, Component var2, int var3, DragGestureListener var4) {
      super(var1, var2, var3, var4);
   }

   protected MouseDragGestureRecognizer(DragSource var1, Component var2, int var3) {
      this(var1, var2, var3, (DragGestureListener)null);
   }

   protected MouseDragGestureRecognizer(DragSource var1, Component var2) {
      this(var1, var2, 0);
   }

   protected MouseDragGestureRecognizer(DragSource var1) {
      this(var1, (Component)null);
   }

   protected void registerListeners() {
      this.component.addMouseListener(this);
      this.component.addMouseMotionListener(this);
   }

   protected void unregisterListeners() {
      this.component.removeMouseListener(this);
      this.component.removeMouseMotionListener(this);
   }

   public void mouseClicked(MouseEvent var1) {
   }

   public void mousePressed(MouseEvent var1) {
   }

   public void mouseReleased(MouseEvent var1) {
   }

   public void mouseEntered(MouseEvent var1) {
   }

   public void mouseExited(MouseEvent var1) {
   }

   public void mouseDragged(MouseEvent var1) {
   }

   public void mouseMoved(MouseEvent var1) {
   }
}
