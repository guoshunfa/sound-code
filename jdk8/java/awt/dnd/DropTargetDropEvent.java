package java.awt.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class DropTargetDropEvent extends DropTargetEvent {
   private static final long serialVersionUID = -1721911170440459322L;
   private static final Point zero = new Point(0, 0);
   private Point location;
   private int actions;
   private int dropAction;
   private boolean isLocalTx;

   public DropTargetDropEvent(DropTargetContext var1, Point var2, int var3, int var4) {
      super(var1);
      this.location = zero;
      this.actions = 0;
      this.dropAction = 0;
      this.isLocalTx = false;
      if (var2 == null) {
         throw new NullPointerException("cursorLocn");
      } else if (var3 != 0 && var3 != 1 && var3 != 2 && var3 != 1073741824) {
         throw new IllegalArgumentException("dropAction = " + var3);
      } else if ((var4 & -1073741828) != 0) {
         throw new IllegalArgumentException("srcActions");
      } else {
         this.location = var2;
         this.actions = var4;
         this.dropAction = var3;
      }
   }

   public DropTargetDropEvent(DropTargetContext var1, Point var2, int var3, int var4, boolean var5) {
      this(var1, var2, var3, var4);
      this.isLocalTx = var5;
   }

   public Point getLocation() {
      return this.location;
   }

   public DataFlavor[] getCurrentDataFlavors() {
      return this.getDropTargetContext().getCurrentDataFlavors();
   }

   public List<DataFlavor> getCurrentDataFlavorsAsList() {
      return this.getDropTargetContext().getCurrentDataFlavorsAsList();
   }

   public boolean isDataFlavorSupported(DataFlavor var1) {
      return this.getDropTargetContext().isDataFlavorSupported(var1);
   }

   public int getSourceActions() {
      return this.actions;
   }

   public int getDropAction() {
      return this.dropAction;
   }

   public Transferable getTransferable() {
      return this.getDropTargetContext().getTransferable();
   }

   public void acceptDrop(int var1) {
      this.getDropTargetContext().acceptDrop(var1);
   }

   public void rejectDrop() {
      this.getDropTargetContext().rejectDrop();
   }

   public void dropComplete(boolean var1) {
      this.getDropTargetContext().dropComplete(var1);
   }

   public boolean isLocalTransfer() {
      return this.isLocalTx;
   }
}
