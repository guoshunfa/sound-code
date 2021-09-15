package sun.awt.datatransfer;

import java.awt.EventQueue;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import sun.awt.AppContext;
import sun.awt.EventListenerAggregate;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;

public abstract class SunClipboard extends Clipboard implements PropertyChangeListener {
   private AppContext contentsContext = null;
   private final Object CLIPBOARD_FLAVOR_LISTENER_KEY;
   private volatile int numberOfFlavorListeners = 0;
   private volatile long[] currentFormats;

   public SunClipboard(String var1) {
      super(var1);
      this.CLIPBOARD_FLAVOR_LISTENER_KEY = new StringBuffer(var1 + "_CLIPBOARD_FLAVOR_LISTENER_KEY");
   }

   public synchronized void setContents(Transferable var1, ClipboardOwner var2) {
      if (var1 == null) {
         throw new NullPointerException("contents");
      } else {
         this.initContext();
         final ClipboardOwner var3 = this.owner;
         final Transferable var4 = this.contents;

         try {
            this.owner = var2;
            this.contents = new TransferableProxy(var1, true);
            this.setContentsNative(var1);
         } finally {
            if (var3 != null && var3 != var2) {
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     var3.lostOwnership(SunClipboard.this, var4);
                  }
               });
            }

         }

      }
   }

   private synchronized void initContext() {
      AppContext var1 = AppContext.getAppContext();
      if (this.contentsContext != var1) {
         synchronized(var1) {
            if (var1.isDisposed()) {
               throw new IllegalStateException("Can't set contents from disposed AppContext");
            }

            var1.addPropertyChangeListener("disposed", this);
         }

         if (this.contentsContext != null) {
            this.contentsContext.removePropertyChangeListener("disposed", this);
         }

         this.contentsContext = var1;
      }

   }

   public synchronized Transferable getContents(Object var1) {
      return (Transferable)(this.contents != null ? this.contents : new ClipboardTransferable(this));
   }

   protected synchronized Transferable getContextContents() {
      AppContext var1 = AppContext.getAppContext();
      return var1 == this.contentsContext ? this.contents : null;
   }

   public DataFlavor[] getAvailableDataFlavors() {
      Transferable var1 = this.getContextContents();
      if (var1 != null) {
         return var1.getTransferDataFlavors();
      } else {
         long[] var2 = this.getClipboardFormatsOpenClose();
         return DataTransferer.getInstance().getFlavorsForFormatsAsArray(var2, getDefaultFlavorTable());
      }
   }

   public boolean isDataFlavorAvailable(DataFlavor var1) {
      if (var1 == null) {
         throw new NullPointerException("flavor");
      } else {
         Transferable var2 = this.getContextContents();
         if (var2 != null) {
            return var2.isDataFlavorSupported(var1);
         } else {
            long[] var3 = this.getClipboardFormatsOpenClose();
            return formatArrayAsDataFlavorSet(var3).contains(var1);
         }
      }
   }

   public Object getData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
      if (var1 == null) {
         throw new NullPointerException("flavor");
      } else {
         Transferable var2 = this.getContextContents();
         if (var2 != null) {
            return var2.getTransferData(var1);
         } else {
            long var3 = 0L;
            Object var5 = null;
            Transferable var6 = null;

            byte[] var12;
            try {
               this.openClipboard((SunClipboard)null);
               long[] var7 = this.getClipboardFormats();
               Long var8 = (Long)DataTransferer.getInstance().getFlavorsForFormats(var7, getDefaultFlavorTable()).get(var1);
               if (var8 == null) {
                  throw new UnsupportedFlavorException(var1);
               }

               var3 = var8;
               var12 = this.getClipboardData(var3);
               if (DataTransferer.getInstance().isLocaleDependentTextFormat(var3)) {
                  var6 = this.createLocaleTransferable(var7);
               }
            } finally {
               this.closeClipboard();
            }

            return DataTransferer.getInstance().translateBytes(var12, var1, var3, var6);
         }
      }
   }

   protected Transferable createLocaleTransferable(long[] var1) throws IOException {
      return null;
   }

   public void openClipboard(SunClipboard var1) {
   }

   public void closeClipboard() {
   }

   public abstract long getID();

   public void propertyChange(PropertyChangeEvent var1) {
      if ("disposed".equals(var1.getPropertyName()) && Boolean.TRUE.equals(var1.getNewValue())) {
         AppContext var2 = (AppContext)var1.getSource();
         this.lostOwnershipLater(var2);
      }

   }

   protected void lostOwnershipImpl() {
      this.lostOwnershipLater((AppContext)null);
   }

   protected void lostOwnershipLater(AppContext var1) {
      AppContext var2 = this.contentsContext;
      if (var2 != null) {
         SunToolkit.postEvent(var2, new PeerEvent(this, () -> {
            this.lostOwnershipNow(var1);
         }, 1L));
      }
   }

   protected void lostOwnershipNow(AppContext var1) {
      SunClipboard var2 = this;
      ClipboardOwner var3 = null;
      Transferable var4 = null;
      synchronized(this) {
         AppContext var6 = var2.contentsContext;
         if (var6 == null) {
            return;
         }

         if (var1 != null && var6 != var1) {
            return;
         }

         var3 = var2.owner;
         var4 = var2.contents;
         var2.contentsContext = null;
         var2.owner = null;
         var2.contents = null;
         var2.clearNativeContext();
         var6.removePropertyChangeListener("disposed", var2);
      }

      if (var3 != null) {
         var3.lostOwnership(this, var4);
      }

   }

   protected abstract void clearNativeContext();

   protected abstract void setContentsNative(Transferable var1);

   protected long[] getClipboardFormatsOpenClose() {
      long[] var1;
      try {
         this.openClipboard((SunClipboard)null);
         var1 = this.getClipboardFormats();
      } finally {
         this.closeClipboard();
      }

      return var1;
   }

   protected abstract long[] getClipboardFormats();

   protected abstract byte[] getClipboardData(long var1) throws IOException;

   private static Set formatArrayAsDataFlavorSet(long[] var0) {
      return var0 == null ? null : DataTransferer.getInstance().getFlavorsForFormatsAsSet(var0, getDefaultFlavorTable());
   }

   public synchronized void addFlavorListener(FlavorListener var1) {
      if (var1 != null) {
         AppContext var2 = AppContext.getAppContext();
         EventListenerAggregate var3 = (EventListenerAggregate)var2.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
         if (var3 == null) {
            var3 = new EventListenerAggregate(FlavorListener.class);
            var2.put(this.CLIPBOARD_FLAVOR_LISTENER_KEY, var3);
         }

         var3.add(var1);
         if (this.numberOfFlavorListeners++ == 0) {
            long[] var4 = null;

            try {
               this.openClipboard((SunClipboard)null);
               var4 = this.getClipboardFormats();
            } catch (IllegalStateException var9) {
            } finally {
               this.closeClipboard();
            }

            this.currentFormats = var4;
            this.registerClipboardViewerChecked();
         }

      }
   }

   public synchronized void removeFlavorListener(FlavorListener var1) {
      if (var1 != null) {
         AppContext var2 = AppContext.getAppContext();
         EventListenerAggregate var3 = (EventListenerAggregate)var2.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
         if (var3 != null) {
            if (var3.remove(var1) && --this.numberOfFlavorListeners == 0) {
               this.unregisterClipboardViewerChecked();
               this.currentFormats = null;
            }

         }
      }
   }

   public synchronized FlavorListener[] getFlavorListeners() {
      EventListenerAggregate var1 = (EventListenerAggregate)AppContext.getAppContext().get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
      return var1 == null ? new FlavorListener[0] : (FlavorListener[])((FlavorListener[])var1.getListenersCopy());
   }

   public boolean areFlavorListenersRegistered() {
      return this.numberOfFlavorListeners > 0;
   }

   protected abstract void registerClipboardViewerChecked();

   protected abstract void unregisterClipboardViewerChecked();

   protected final void checkChange(long[] var1) {
      if (!Arrays.equals(var1, this.currentFormats)) {
         this.currentFormats = var1;
         Iterator var2 = AppContext.getAppContexts().iterator();

         while(true) {
            AppContext var3;
            EventListenerAggregate var4;
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        return;
                     }

                     var3 = (AppContext)var2.next();
                  } while(var3 == null);
               } while(var3.isDisposed());

               var4 = (EventListenerAggregate)var3.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
            } while(var4 == null);

            FlavorListener[] var5 = (FlavorListener[])((FlavorListener[])var4.getListenersInternal());

            for(int var6 = 0; var6 < var5.length; ++var6) {
               class SunFlavorChangeNotifier implements Runnable {
                  private final FlavorListener flavorListener;

                  SunFlavorChangeNotifier(FlavorListener var2) {
                     this.flavorListener = var2;
                  }

                  public void run() {
                     if (this.flavorListener != null) {
                        this.flavorListener.flavorsChanged(new FlavorEvent(SunClipboard.this));
                     }

                  }
               }

               SunToolkit.postEvent(var3, new PeerEvent(this, new SunFlavorChangeNotifier(var5[var6]), 1L));
            }
         }
      }
   }

   public static FlavorTable getDefaultFlavorTable() {
      return (FlavorTable)SystemFlavorMap.getDefaultFlavorMap();
   }
}
