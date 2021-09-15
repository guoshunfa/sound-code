package sun.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import javax.swing.JComponent;

public interface LightweightContent {
   JComponent getComponent();

   void paintLock();

   void paintUnlock();

   default void imageBufferReset(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.imageBufferReset(var1, var2, var3, var4, var5, var6);
   }

   default void imageBufferReset(int[] var1, int var2, int var3, int var4, int var5, int var6) {
      this.imageBufferReset(var1, var2, var3, var4, var5, var6, 1);
   }

   void imageReshaped(int var1, int var2, int var3, int var4);

   void imageUpdated(int var1, int var2, int var3, int var4);

   void focusGrabbed();

   void focusUngrabbed();

   void preferredSizeChanged(int var1, int var2);

   void maximumSizeChanged(int var1, int var2);

   void minimumSizeChanged(int var1, int var2);

   default void setCursor(Cursor var1) {
   }

   default <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5) {
      return null;
   }

   default DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException {
      return null;
   }

   default void addDropTarget(DropTarget var1) {
   }

   default void removeDropTarget(DropTarget var1) {
   }
}
