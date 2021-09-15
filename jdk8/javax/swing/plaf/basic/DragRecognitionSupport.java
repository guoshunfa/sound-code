package javax.swing.plaf.basic;

import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import sun.awt.AppContext;
import sun.awt.dnd.SunDragSourceContextPeer;

class DragRecognitionSupport {
   private int motionThreshold;
   private MouseEvent dndArmedEvent;
   private JComponent component;

   private static DragRecognitionSupport getDragRecognitionSupport() {
      DragRecognitionSupport var0 = (DragRecognitionSupport)AppContext.getAppContext().get(DragRecognitionSupport.class);
      if (var0 == null) {
         var0 = new DragRecognitionSupport();
         AppContext.getAppContext().put(DragRecognitionSupport.class, var0);
      }

      return var0;
   }

   public static boolean mousePressed(MouseEvent var0) {
      return getDragRecognitionSupport().mousePressedImpl(var0);
   }

   public static MouseEvent mouseReleased(MouseEvent var0) {
      return getDragRecognitionSupport().mouseReleasedImpl(var0);
   }

   public static boolean mouseDragged(MouseEvent var0, DragRecognitionSupport.BeforeDrag var1) {
      return getDragRecognitionSupport().mouseDraggedImpl(var0, var1);
   }

   private void clearState() {
      this.dndArmedEvent = null;
      this.component = null;
   }

   private int mapDragOperationFromModifiers(MouseEvent var1, TransferHandler var2) {
      return var2 != null && SwingUtilities.isLeftMouseButton(var1) ? SunDragSourceContextPeer.convertModifiersToDropAction(var1.getModifiersEx(), var2.getSourceActions(this.component)) : 0;
   }

   private boolean mousePressedImpl(MouseEvent var1) {
      this.component = (JComponent)var1.getSource();
      if (this.mapDragOperationFromModifiers(var1, this.component.getTransferHandler()) != 0) {
         this.motionThreshold = DragSource.getDragThreshold();
         this.dndArmedEvent = var1;
         return true;
      } else {
         this.clearState();
         return false;
      }
   }

   private MouseEvent mouseReleasedImpl(MouseEvent var1) {
      if (this.dndArmedEvent == null) {
         return null;
      } else {
         MouseEvent var2 = null;
         if (var1.getSource() == this.component) {
            var2 = this.dndArmedEvent;
         }

         this.clearState();
         return var2;
      }
   }

   private boolean mouseDraggedImpl(MouseEvent var1, DragRecognitionSupport.BeforeDrag var2) {
      if (this.dndArmedEvent == null) {
         return false;
      } else if (var1.getSource() != this.component) {
         this.clearState();
         return false;
      } else {
         int var3 = Math.abs(var1.getX() - this.dndArmedEvent.getX());
         int var4 = Math.abs(var1.getY() - this.dndArmedEvent.getY());
         if (var3 > this.motionThreshold || var4 > this.motionThreshold) {
            TransferHandler var5 = this.component.getTransferHandler();
            int var6 = this.mapDragOperationFromModifiers(var1, var5);
            if (var6 != 0) {
               if (var2 != null) {
                  var2.dragStarting(this.dndArmedEvent);
               }

               var5.exportAsDrag(this.component, this.dndArmedEvent, var6);
               this.clearState();
            }
         }

         return true;
      }
   }

   public interface BeforeDrag {
      void dragStarting(MouseEvent var1);
   }
}
