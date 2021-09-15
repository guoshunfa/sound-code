package sun.java2d.pipe.hw;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AccelDeviceEventNotifier {
   private static AccelDeviceEventNotifier theInstance;
   public static final int DEVICE_RESET = 0;
   public static final int DEVICE_DISPOSED = 1;
   private final Map<AccelDeviceEventListener, Integer> listeners = Collections.synchronizedMap(new HashMap(1));

   private AccelDeviceEventNotifier() {
   }

   private static synchronized AccelDeviceEventNotifier getInstance(boolean var0) {
      if (theInstance == null && var0) {
         theInstance = new AccelDeviceEventNotifier();
      }

      return theInstance;
   }

   public static final void eventOccured(int var0, int var1) {
      AccelDeviceEventNotifier var2 = getInstance(false);
      if (var2 != null) {
         var2.notifyListeners(var1, var0);
      }

   }

   public static final void addListener(AccelDeviceEventListener var0, int var1) {
      getInstance(true).add(var0, var1);
   }

   public static final void removeListener(AccelDeviceEventListener var0) {
      getInstance(true).remove(var0);
   }

   private final void add(AccelDeviceEventListener var1, int var2) {
      this.listeners.put(var1, var2);
   }

   private final void remove(AccelDeviceEventListener var1) {
      this.listeners.remove(var1);
   }

   private final void notifyListeners(int var1, int var2) {
      HashMap var3;
      synchronized(this.listeners) {
         var3 = new HashMap(this.listeners);
      }

      Set var4 = var3.keySet();
      Iterator var5 = var4.iterator();

      while(true) {
         AccelDeviceEventListener var6;
         Integer var7;
         do {
            if (!var5.hasNext()) {
               return;
            }

            var6 = (AccelDeviceEventListener)var5.next();
            var7 = (Integer)var3.get(var6);
         } while(var7 != null && var7 != var2);

         if (var1 == 0) {
            var6.onDeviceReset();
         } else if (var1 == 1) {
            var6.onDeviceDispose();
         }
      }
   }
}
