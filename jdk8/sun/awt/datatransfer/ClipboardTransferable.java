package sun.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClipboardTransferable implements Transferable {
   private final HashMap flavorsToData = new HashMap();
   private DataFlavor[] flavors = new DataFlavor[0];

   public ClipboardTransferable(SunClipboard var1) {
      var1.openClipboard((SunClipboard)null);

      try {
         long[] var2 = var1.getClipboardFormats();
         if (var2 != null && var2.length > 0) {
            HashMap var3 = new HashMap(var2.length, 1.0F);
            Map var4 = DataTransferer.getInstance().getFlavorsForFormats(var2, SunClipboard.getDefaultFlavorTable());
            Iterator var5 = var4.keySet().iterator();

            while(var5.hasNext()) {
               DataFlavor var6 = (DataFlavor)var5.next();
               Long var7 = (Long)var4.get(var6);
               this.fetchOneFlavor(var1, var6, var7, var3);
            }

            DataTransferer.getInstance();
            this.flavors = DataTransferer.setToSortedDataFlavorArray(this.flavorsToData.keySet());
         }
      } finally {
         var1.closeClipboard();
      }

   }

   private boolean fetchOneFlavor(SunClipboard var1, DataFlavor var2, Long var3, HashMap var4) {
      if (!this.flavorsToData.containsKey(var2)) {
         long var5 = var3;
         Object var7 = null;
         if (!var4.containsKey(var3)) {
            try {
               var7 = var1.getClipboardData(var5);
            } catch (IOException var9) {
               var7 = var9;
            } catch (Throwable var10) {
               var10.printStackTrace();
            }

            var4.put(var3, var7);
         } else {
            var7 = var4.get(var3);
         }

         if (var7 instanceof IOException) {
            this.flavorsToData.put(var2, var7);
            return false;
         }

         if (var7 != null) {
            this.flavorsToData.put(var2, new ClipboardTransferable.DataFactory(var5, (byte[])((byte[])var7)));
            return true;
         }
      }

      return false;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return (DataFlavor[])((DataFlavor[])this.flavors.clone());
   }

   public boolean isDataFlavorSupported(DataFlavor var1) {
      return this.flavorsToData.containsKey(var1);
   }

   public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
      if (!this.isDataFlavorSupported(var1)) {
         throw new UnsupportedFlavorException(var1);
      } else {
         Object var2 = this.flavorsToData.get(var1);
         if (var2 instanceof IOException) {
            throw (IOException)var2;
         } else {
            if (var2 instanceof ClipboardTransferable.DataFactory) {
               ClipboardTransferable.DataFactory var3 = (ClipboardTransferable.DataFactory)var2;
               var2 = var3.getTransferData(var1);
            }

            return var2;
         }
      }
   }

   private final class DataFactory {
      final long format;
      final byte[] data;

      DataFactory(long var2, byte[] var4) {
         this.format = var2;
         this.data = var4;
      }

      public Object getTransferData(DataFlavor var1) throws IOException {
         return DataTransferer.getInstance().translateBytes(this.data, var1, this.format, ClipboardTransferable.this);
      }
   }
}
