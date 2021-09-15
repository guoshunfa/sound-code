package java.awt.datatransfer;

import java.io.IOException;

public interface Transferable {
   DataFlavor[] getTransferDataFlavors();

   boolean isDataFlavorSupported(DataFlavor var1);

   Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException;
}
