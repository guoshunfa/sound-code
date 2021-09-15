package java.awt.dnd;

import java.util.EventListener;

public interface DragSourceListener extends EventListener {
   void dragEnter(DragSourceDragEvent var1);

   void dragOver(DragSourceDragEvent var1);

   void dropActionChanged(DragSourceDragEvent var1);

   void dragExit(DragSourceEvent var1);

   void dragDropEnd(DragSourceDropEvent var1);
}
