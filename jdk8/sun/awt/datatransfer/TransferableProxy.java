package sun.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransferableProxy implements Transferable {
   protected final Transferable transferable;
   protected final boolean isLocal;

   public TransferableProxy(Transferable var1, boolean var2) {
      this.transferable = var1;
      this.isLocal = var2;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return this.transferable.getTransferDataFlavors();
   }

   public boolean isDataFlavorSupported(DataFlavor var1) {
      return this.transferable.isDataFlavorSupported(var1);
   }

   public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
      Object var2 = this.transferable.getTransferData(var1);
      if (var2 != null && this.isLocal && var1.isFlavorSerializedObjectType()) {
         ByteArrayOutputStream var3 = new ByteArrayOutputStream();
         ClassLoaderObjectOutputStream var4 = new ClassLoaderObjectOutputStream(var3);
         var4.writeObject(var2);
         ByteArrayInputStream var5 = new ByteArrayInputStream(var3.toByteArray());

         try {
            ClassLoaderObjectInputStream var6 = new ClassLoaderObjectInputStream(var5, var4.getClassLoaderMap());
            var2 = var6.readObject();
         } catch (ClassNotFoundException var7) {
            throw (IOException)(new IOException()).initCause(var7);
         }
      }

      return var2;
   }
}
