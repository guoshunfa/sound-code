package java.awt.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class DropTargetDragEvent extends DropTargetEvent {
   private static final long serialVersionUID = -8422265619058953682L;
   private Point location;
   private int actions;
   private int dropAction;

   public DropTargetDragEvent(DropTargetContext var1, Point var2, int var3, int var4) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("cursorLocn");
      } else if (var3 != 0 && var3 != 1 && var3 != 2 && var3 != 1073741824) {
         throw new IllegalArgumentException("dropAction" + var3);
      } else if ((var4 & -1073741828) != 0) {
         throw new IllegalArgumentException("srcActions");
      } else {
         this.location = var2;
         this.actions = var4;
         this.dropAction = var3;
      }
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

   public void acceptDrag(int var1) {
      this.getDropTargetContext().acceptDrag(var1);
   }

   public void rejectDrag() {
      this.getDropTargetContext().rejectDrag();
   }
}
