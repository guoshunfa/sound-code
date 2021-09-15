package java.awt.dnd;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class DropTargetContext implements Serializable {
   private static final long serialVersionUID = -634158968993743371L;
   private DropTarget dropTarget;
   private transient DropTargetContextPeer dropTargetContextPeer;
   private transient Transferable transferable;

   DropTargetContext(DropTarget var1) {
      this.dropTarget = var1;
   }

   public DropTarget getDropTarget() {
      return this.dropTarget;
   }

   public Component getComponent() {
      return this.dropTarget.getComponent();
   }

   public void addNotify(DropTargetContextPeer var1) {
      this.dropTargetContextPeer = var1;
   }

   public void removeNotify() {
      this.dropTargetContextPeer = null;
      this.transferable = null;
   }

   protected void setTargetActions(int var1) {
      DropTargetContextPeer var2 = this.getDropTargetContextPeer();
      if (var2 != null) {
         synchronized(var2) {
            var2.setTargetActions(var1);
            this.getDropTarget().doSetDefaultActions(var1);
         }
      } else {
         this.getDropTarget().doSetDefaultActions(var1);
      }

   }

   protected int getTargetActions() {
      DropTargetContextPeer var1 = this.getDropTargetContextPeer();
      return var1 != null ? var1.getTargetActions() : this.dropTarget.getDefaultActions();
   }

   public void dropComplete(boolean var1) throws InvalidDnDOperationException {
      DropTargetContextPeer var2 = this.getDropTargetContextPeer();
      if (var2 != null) {
         var2.dropComplete(var1);
      }

   }

   protected void acceptDrag(int var1) {
      DropTargetContextPeer var2 = this.getDropTargetContextPeer();
      if (var2 != null) {
         var2.acceptDrag(var1);
      }

   }

   protected void rejectDrag() {
      DropTargetContextPeer var1 = this.getDropTargetContextPeer();
      if (var1 != null) {
         var1.rejectDrag();
      }

   }

   protected void acceptDrop(int var1) {
      DropTargetContextPeer var2 = this.getDropTargetContextPeer();
      if (var2 != null) {
         var2.acceptDrop(var1);
      }

   }

   protected void rejectDrop() {
      DropTargetContextPeer var1 = this.getDropTargetContextPeer();
      if (var1 != null) {
         var1.rejectDrop();
      }

   }

   protected DataFlavor[] getCurrentDataFlavors() {
      DropTargetContextPeer var1 = this.getDropTargetContextPeer();
      return var1 != null ? var1.getTransferDataFlavors() : new DataFlavor[0];
   }

   protected List<DataFlavor> getCurrentDataFlavorsAsList() {
      return Arrays.asList(this.getCurrentDataFlavors());
   }

   protected boolean isDataFlavorSupported(DataFlavor var1) {
      return this.getCurrentDataFlavorsAsList().contains(var1);
   }

   protected Transferable getTransferable() throws InvalidDnDOperationException {
      DropTargetContextPeer var1 = this.getDropTargetContextPeer();
      if (var1 == null) {
         throw new InvalidDnDOperationException();
      } else {
         if (this.transferable == null) {
            Transferable var2 = var1.getTransferable();
            boolean var3 = var1.isTransferableJVMLocal();
            synchronized(this) {
               if (this.transferable == null) {
                  this.transferable = this.createTransferableProxy(var2, var3);
               }
            }
         }

         return this.transferable;
      }
   }

   DropTargetContextPeer getDropTargetContextPeer() {
      return this.dropTargetContextPeer;
   }

   protected Transferable createTransferableProxy(Transferable var1, boolean var2) {
      return new DropTargetContext.TransferableProxy(var1, var2);
   }

   protected class TransferableProxy implements Transferable {
      protected Transferable transferable;
      protected boolean isLocal;
      private sun.awt.datatransfer.TransferableProxy proxy;

      TransferableProxy(Transferable var2, boolean var3) {
         this.proxy = new sun.awt.datatransfer.TransferableProxy(var2, var3);
         this.transferable = var2;
         this.isLocal = var3;
      }

      public DataFlavor[] getTransferDataFlavors() {
         return this.proxy.getTransferDataFlavors();
      }

      public boolean isDataFlavorSupported(DataFlavor var1) {
         return this.proxy.isDataFlavorSupported(var1);
      }

      public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
         return this.proxy.getTransferData(var1);
      }
   }
}
