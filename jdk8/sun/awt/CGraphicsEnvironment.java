package sun.awt;

import java.awt.AWTError;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import sun.java2d.MacosxSurfaceManagerFactory;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;

public final class CGraphicsEnvironment extends SunGraphicsEnvironment {
   private final Map<Integer, CGraphicsDevice> devices = new HashMap(5);
   private final long displayReconfigContext;
   static String[] sLogicalFonts;

   private static native void initCocoa();

   private static native int[] getDisplayIDs();

   private static native int getMainDisplayID();

   public static void init() {
   }

   private native long registerDisplayReconfiguration();

   private native void deregisterDisplayReconfiguration(long var1);

   public CGraphicsEnvironment() {
      if (isHeadless()) {
         this.displayReconfigContext = 0L;
      } else {
         this.initDevices();
         this.displayReconfigContext = this.registerDisplayReconfiguration();
         if (this.displayReconfigContext == 0L) {
            throw new RuntimeException("Could not register CoreGraphics display reconfiguration callback");
         }
      }
   }

   void _displayReconfiguration(int var1, boolean var2) {
      synchronized(this) {
         if (var2 && this.devices.containsKey(var1)) {
            CGraphicsDevice var4 = (CGraphicsDevice)this.devices.remove(var1);
            var4.invalidate(getMainDisplayID());
            var4.displayChanged();
         }
      }

      this.initDevices();
   }

   protected void finalize() throws Throwable {
      try {
         super.finalize();
      } finally {
         this.deregisterDisplayReconfiguration(this.displayReconfigContext);
      }

   }

   private void initDevices() {
      synchronized(this) {
         HashMap var2 = new HashMap(this.devices);
         this.devices.clear();
         int var3 = getMainDisplayID();
         if (!var2.containsKey(var3)) {
            var2.put(var3, new CGraphicsDevice(var3));
         }

         int[] var4 = getDisplayIDs();
         int var5 = var4.length;
         int var6 = 0;

         while(true) {
            if (var6 >= var5) {
               break;
            }

            int var7 = var4[var6];
            this.devices.put(var7, var2.containsKey(var7) ? (CGraphicsDevice)var2.get(var7) : new CGraphicsDevice(var7));
            ++var6;
         }
      }

      this.displayChanged();
   }

   public synchronized GraphicsDevice getDefaultScreenDevice() throws HeadlessException {
      int var1 = getMainDisplayID();
      CGraphicsDevice var2 = (CGraphicsDevice)this.devices.get(var1);
      if (var2 == null) {
         this.initDevices();
         var2 = (CGraphicsDevice)this.devices.get(var1);
         if (var2 == null) {
            throw new AWTError("no screen devices");
         }
      }

      return var2;
   }

   public synchronized GraphicsDevice[] getScreenDevices() throws HeadlessException {
      return (GraphicsDevice[])this.devices.values().toArray(new CGraphicsDevice[this.devices.values().size()]);
   }

   public synchronized GraphicsDevice getScreenDevice(int var1) {
      return (GraphicsDevice)this.devices.get(var1);
   }

   protected synchronized int getNumScreens() {
      return this.devices.size();
   }

   protected GraphicsDevice makeScreenDevice(int var1) {
      throw new UnsupportedOperationException("This method is unused and should not be called in this implementation");
   }

   public boolean isDisplayLocal() {
      return true;
   }

   public Font[] getAllFonts() {
      Font[] var2 = super.getAllFonts();
      int var3 = sLogicalFonts.length;
      int var4 = var2.length;
      Font[] var1 = new Font[var4 + var3];
      System.arraycopy(var2, 0, var1, var3, var4);

      for(int var5 = 0; var5 < var3; ++var5) {
         var1[var5] = new Font(sLogicalFonts[var5], 0, 1);
      }

      return var1;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            if (GraphicsEnvironment.isHeadless()) {
               return null;
            } else {
               CGraphicsEnvironment.initCocoa();
               return null;
            }
         }
      });
      SurfaceManagerFactory.setInstance(new MacosxSurfaceManagerFactory());
      sLogicalFonts = new String[]{"Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput"};
   }
}
