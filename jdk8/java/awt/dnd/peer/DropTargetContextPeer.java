package java.awt.dnd.peer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;

public interface DropTargetContextPeer {
   void setTargetActions(int var1);

   int getTargetActions();

   DropTarget getDropTarget();

   DataFlavor[] getTransferDataFlavors();

   Transferable getTransferable() throws InvalidDnDOperationException;

   boolean isTransferableJVMLocal();

   void acceptDrag(int var1);

   void rejectDrag();

   void acceptDrop(int var1);

   void rejectDrop();

   void dropComplete(boolean var1);
}
