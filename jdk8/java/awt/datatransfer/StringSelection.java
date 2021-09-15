package java.awt.datatransfer;

import java.io.IOException;
import java.io.StringReader;

public class StringSelection implements Transferable, ClipboardOwner {
   private static final int STRING = 0;
   private static final int PLAIN_TEXT = 1;
   private static final DataFlavor[] flavors;
   private String data;

   public StringSelection(String var1) {
      this.data = var1;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return (DataFlavor[])((DataFlavor[])flavors.clone());
   }

   public boolean isDataFlavorSupported(DataFlavor var1) {
      for(int var2 = 0; var2 < flavors.length; ++var2) {
         if (var1.equals(flavors[var2])) {
            return true;
         }
      }

      return false;
   }

   public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
      if (var1.equals(flavors[0])) {
         return this.data;
      } else if (var1.equals(flavors[1])) {
         return new StringReader(this.data == null ? "" : this.data);
      } else {
         throw new UnsupportedFlavorException(var1);
      }
   }

   public void lostOwnership(Clipboard var1, Transferable var2) {
   }

   static {
      flavors = new DataFlavor[]{DataFlavor.stringFlavor, DataFlavor.plainTextFlavor};
   }
}
