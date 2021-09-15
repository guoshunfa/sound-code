package sun.lwawt.macosx;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.MouseEvent;
import sun.awt.dnd.SunDragSourceContextPeer;

class CMouseDragGestureRecognizer extends MouseDragGestureRecognizer {
   private static final int fMotionThreshold = getMotionThreshold();
   private static final int kDefaultMotionThreshold = 3;
   protected static final int ButtonMask = 7168;

   private static int getMotionThreshold() {
      try {
         return (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold");
      } catch (Exception var1) {
         return 3;
      }
   }

   protected CMouseDragGestureRecognizer(DragSource var1, Component var2, int var3, DragGestureListener var4) {
      super(var1, var2, var3, var4);
   }

   protected CMouseDragGestureRecognizer(DragSource var1, Component var2, int var3) {
      this(var1, var2, var3, (DragGestureListener)null);
   }

   protected CMouseDragGestureRecognizer(DragSource var1, Component var2) {
      this(var1, var2, 0);
   }

   protected CMouseDragGestureRecognizer(DragSource var1) {
      this(var1, (Component)null);
   }

   protected int mapDragOperationFromModifiers(MouseEvent var1) {
      int var2 = var1.getModifiersEx();
      int var3 = var2 & 7168;
      return SunDragSourceContextPeer.convertModifiersToDropAction(var2, this.getSourceActions());
   }

   public void mouseClicked(MouseEvent var1) {
   }

   public void mousePressed(MouseEvent var1) {
      this.events.clear();
      if (this.mapDragOperationFromModifiers(var1) != 0) {
         this.appendEvent(var1);
      }

   }

   public void mouseReleased(MouseEvent var1) {
      this.events.clear();
   }

   public void mouseEntered(MouseEvent var1) {
      this.events.clear();
   }

   public void mouseExited(MouseEvent var1) {
      if (!this.events.isEmpty()) {
         int var2 = this.mapDragOperationFromModifiers(var1);
         if (var2 == 0) {
            this.events.clear();
         }
      }

   }

   public void mouseDragged(MouseEvent var1) {
      if (!this.events.isEmpty()) {
         int var2 = this.mapDragOperationFromModifiers(var1);
         if (var2 == 0) {
            return;
         }

         MouseEvent var3 = (MouseEvent)this.events.get(0);
         Point var4 = var3.getPoint();
         Point var5 = var1.getPoint();
         int var6 = Math.abs(var4.x - var5.x);
         int var7 = Math.abs(var4.y - var5.y);
         if (var6 < fMotionThreshold && var7 < fMotionThreshold) {
            this.appendEvent(var1);
         } else {
            this.fireDragGestureRecognized(var2, ((MouseEvent)this.getTriggerEvent()).getPoint());
         }
      }

   }

   public void mouseMoved(MouseEvent var1) {
   }
}
