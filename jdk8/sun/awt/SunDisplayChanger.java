package sun.awt;

import java.awt.IllegalComponentStateException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import sun.util.logging.PlatformLogger;

public class SunDisplayChanger {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.multiscreen.SunDisplayChanger");
   private Map<DisplayChangedListener, Void> listeners = Collections.synchronizedMap(new WeakHashMap(1));

   public void add(DisplayChangedListener var1) {
      if (log.isLoggable(PlatformLogger.Level.FINE) && var1 == null) {
         log.fine("Assertion (theListener != null) failed");
      }

      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("Adding listener: " + var1);
      }

      this.listeners.put(var1, (Object)null);
   }

   public void remove(DisplayChangedListener var1) {
      if (log.isLoggable(PlatformLogger.Level.FINE) && var1 == null) {
         log.fine("Assertion (theListener != null) failed");
      }

      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("Removing listener: " + var1);
      }

      this.listeners.remove(var1);
   }

   public void notifyListeners() {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
         log.finest("notifyListeners");
      }

      HashSet var1;
      synchronized(this.listeners) {
         var1 = new HashSet(this.listeners.keySet());
      }

      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         DisplayChangedListener var3 = (DisplayChangedListener)var2.next();

         try {
            if (log.isLoggable(PlatformLogger.Level.FINEST)) {
               log.finest("displayChanged for listener: " + var3);
            }

            var3.displayChanged();
         } catch (IllegalComponentStateException var5) {
            this.listeners.remove(var3);
         }
      }

   }

   public void notifyPaletteChanged() {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
         log.finest("notifyPaletteChanged");
      }

      HashSet var1;
      synchronized(this.listeners) {
         var1 = new HashSet(this.listeners.keySet());
      }

      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         DisplayChangedListener var3 = (DisplayChangedListener)var2.next();

         try {
            if (log.isLoggable(PlatformLogger.Level.FINEST)) {
               log.finest("paletteChanged for listener: " + var3);
            }

            var3.paletteChanged();
         } catch (IllegalComponentStateException var5) {
            this.listeners.remove(var3);
         }
      }

   }
}
