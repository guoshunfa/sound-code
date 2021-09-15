package java.awt.dnd;

import java.util.EventListener;

public interface DropTargetListener extends EventListener {
   void dragEnter(DropTargetDragEvent var1);

   void dragOver(DropTargetDragEvent var1);

   void dropActionChanged(DropTargetDragEvent var1);

   void dragExit(DropTargetEvent var1);

   void drop(DropTargetDropEvent var1);
}
