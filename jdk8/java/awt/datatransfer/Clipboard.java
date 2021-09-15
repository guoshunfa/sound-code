package java.awt.datatransfer;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import sun.awt.EventListenerAggregate;

public class Clipboard {
   String name;
   protected ClipboardOwner owner;
   protected Transferable contents;
   private EventListenerAggregate flavorListeners;
   private Set<DataFlavor> currentDataFlavors;

   public Clipboard(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public synchronized void setContents(Transferable var1, ClipboardOwner var2) {
      final ClipboardOwner var3 = this.owner;
      final Transferable var4 = this.contents;
      this.owner = var2;
      this.contents = var1;
      if (var3 != null && var3 != var2) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               var3.lostOwnership(Clipboard.this, var4);
            }
         });
      }

      this.fireFlavorsChanged();
   }

   public synchronized Transferable getContents(Object var1) {
      return this.contents;
   }

   public DataFlavor[] getAvailableDataFlavors() {
      Transferable var1 = this.getContents((Object)null);
      return var1 == null ? new DataFlavor[0] : var1.getTransferDataFlavors();
   }

   public boolean isDataFlavorAvailable(DataFlavor var1) {
      if (var1 == null) {
         throw new NullPointerException("flavor");
      } else {
         Transferable var2 = this.getContents((Object)null);
         return var2 == null ? false : var2.isDataFlavorSupported(var1);
      }
   }

   public Object getData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
      if (var1 == null) {
         throw new NullPointerException("flavor");
      } else {
         Transferable var2 = this.getContents((Object)null);
         if (var2 == null) {
            throw new UnsupportedFlavorException(var1);
         } else {
            return var2.getTransferData(var1);
         }
      }
   }

   public synchronized void addFlavorListener(FlavorListener var1) {
      if (var1 != null) {
         if (this.flavorListeners == null) {
            this.currentDataFlavors = this.getAvailableDataFlavorSet();
            this.flavorListeners = new EventListenerAggregate(FlavorListener.class);
         }

         this.flavorListeners.add(var1);
      }
   }

   public synchronized void removeFlavorListener(FlavorListener var1) {
      if (var1 != null && this.flavorListeners != null) {
         this.flavorListeners.remove(var1);
      }
   }

   public synchronized FlavorListener[] getFlavorListeners() {
      return this.flavorListeners == null ? new FlavorListener[0] : (FlavorListener[])((FlavorListener[])this.flavorListeners.getListenersCopy());
   }

   private void fireFlavorsChanged() {
      if (this.flavorListeners != null) {
         Set var1 = this.currentDataFlavors;
         this.currentDataFlavors = this.getAvailableDataFlavorSet();
         if (!var1.equals(this.currentDataFlavors)) {
            FlavorListener[] var2 = (FlavorListener[])((FlavorListener[])this.flavorListeners.getListenersInternal());

            for(int var3 = 0; var3 < var2.length; ++var3) {
               final FlavorListener var4 = var2[var3];
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     var4.flavorsChanged(new FlavorEvent(Clipboard.this));
                  }
               });
            }

         }
      }
   }

   private Set<DataFlavor> getAvailableDataFlavorSet() {
      HashSet var1 = new HashSet();
      Transferable var2 = this.getContents((Object)null);
      if (var2 != null) {
         DataFlavor[] var3 = var2.getTransferDataFlavors();
         if (var3 != null) {
            var1.addAll(Arrays.asList(var3));
         }
      }

      return var1;
   }
}
