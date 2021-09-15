package sun.lwawt.macosx;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import sun.awt.AppContext;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.SunClipboard;

final class CClipboard extends SunClipboard {
   public CClipboard(String var1) {
      super(var1);
   }

   public long getID() {
      return 0L;
   }

   protected void clearNativeContext() {
   }

   public synchronized Transferable getContents(Object var1) {
      this.checkPasteboardAndNotify();
      return super.getContents(var1);
   }

   protected synchronized Transferable getContextContents() {
      this.checkPasteboardAndNotify();
      return super.getContextContents();
   }

   protected void setContentsNative(Transferable var1) {
      FlavorTable var2 = getDefaultFlavorTable();
      DataTransferer var3 = DataTransferer.getInstance();
      long[] var4 = var3.getFormatsForTransferableAsArray(var1, var2);
      this.declareTypes(var4, this);
      SortedMap var5 = var3.getFormatsForTransferable(var1, var2);
      Iterator var6 = var5.entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         long var8 = (Long)var7.getKey();
         DataFlavor var10 = (DataFlavor)var7.getValue();

         try {
            byte[] var11 = DataTransferer.getInstance().translateTransferable(var1, var10, var8);
            this.setData(var11, var8);
         } catch (IOException var12) {
            if (!var10.isMimeTypeEqual("application/x-java-jvm-local-objectref") || !(var12 instanceof NotSerializableException)) {
               var12.printStackTrace();
            }
         }
      }

      notifyChanged();
   }

   protected native long[] getClipboardFormats();

   protected native byte[] getClipboardData(long var1) throws IOException;

   protected void unregisterClipboardViewerChecked() {
   }

   protected void registerClipboardViewerChecked() {
   }

   private native void declareTypes(long[] var1, SunClipboard var2);

   private native void setData(byte[] var1, long var2);

   void checkPasteboardAndNotify() {
      if (this.checkPasteboardWithoutNotification()) {
         notifyChanged();
         this.lostOwnershipNow((AppContext)null);
      }

   }

   native boolean checkPasteboardWithoutNotification();

   private void notifyLostOwnership() {
      this.lostOwnershipImpl();
   }

   private static void notifyChanged() {
      CClipboard var0 = (CClipboard)Toolkit.getDefaultToolkit().getSystemClipboard();
      if (var0.areFlavorListenersRegistered()) {
         var0.checkChange(var0.getClipboardFormats());
      }
   }
}
