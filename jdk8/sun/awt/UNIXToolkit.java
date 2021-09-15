package sun.awt;

import com.sun.java.swing.plaf.gtk.GTKConstants;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import sun.java2d.opengl.OGLRenderQueue;
import sun.security.action.GetIntegerAction;

public abstract class UNIXToolkit extends SunToolkit {
   public static final Object GTK_LOCK = new Object();
   private static final int[] BAND_OFFSETS = new int[]{0, 1, 2};
   private static final int[] BAND_OFFSETS_ALPHA = new int[]{0, 1, 2, 3};
   private static final int DEFAULT_DATATRANSFER_TIMEOUT = 10000;
   private Boolean nativeGTKAvailable;
   private Boolean nativeGTKLoaded;
   private BufferedImage tmpImage = null;
   public static final String FONTCONFIGAAHINT = "fontconfig/Antialias";

   public static int getDatatransferTimeout() {
      Integer var0 = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("sun.awt.datatransfer.timeout")));
      return var0 != null && var0 > 0 ? var0 : 10000;
   }

   public boolean isNativeGTKAvailable() {
      synchronized(GTK_LOCK) {
         if (this.nativeGTKLoaded != null) {
            return this.nativeGTKLoaded;
         } else if (this.nativeGTKAvailable != null) {
            return this.nativeGTKAvailable;
         } else {
            boolean var2 = check_gtk();
            this.nativeGTKAvailable = var2;
            return var2;
         }
      }
   }

   public boolean loadGTK() {
      synchronized(GTK_LOCK) {
         if (this.nativeGTKLoaded == null) {
            this.nativeGTKLoaded = load_gtk();
         }
      }

      return this.nativeGTKLoaded;
   }

   protected Object lazilyLoadDesktopProperty(String var1) {
      return var1.startsWith("gtk.icon.") ? this.lazilyLoadGTKIcon(var1) : super.lazilyLoadDesktopProperty(var1);
   }

   protected Object lazilyLoadGTKIcon(String var1) {
      Object var2 = this.desktopProperties.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         String[] var3 = var1.split("\\.");
         if (var3.length != 5) {
            return null;
         } else {
            boolean var4 = false;

            int var8;
            try {
               var8 = Integer.parseInt(var3[3]);
            } catch (NumberFormatException var7) {
               return null;
            }

            GTKConstants.TextDirection var5 = "ltr".equals(var3[4]) ? GTKConstants.TextDirection.LTR : GTKConstants.TextDirection.RTL;
            BufferedImage var6 = this.getStockIcon(-1, var3[2], var8, var5.ordinal(), (String)null);
            if (var6 != null) {
               this.setDesktopProperty(var1, var6);
            }

            return var6;
         }
      }
   }

   public BufferedImage getGTKIcon(String var1) {
      if (!this.loadGTK()) {
         return null;
      } else {
         synchronized(GTK_LOCK) {
            if (!this.load_gtk_icon(var1)) {
               this.tmpImage = null;
            }
         }

         return this.tmpImage;
      }
   }

   public BufferedImage getStockIcon(int var1, String var2, int var3, int var4, String var5) {
      if (!this.loadGTK()) {
         return null;
      } else {
         synchronized(GTK_LOCK) {
            if (!this.load_stock_icon(var1, var2, var3, var4, var5)) {
               this.tmpImage = null;
            }
         }

         return this.tmpImage;
      }
   }

   public void loadIconCallback(byte[] var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      this.tmpImage = null;
      DataBufferByte var8 = new DataBufferByte(var1, var4 * var3);
      WritableRaster var9 = Raster.createInterleavedRaster(var8, var2, var3, var4, var6, var7 ? BAND_OFFSETS_ALPHA : BAND_OFFSETS, (Point)null);
      ComponentColorModel var10 = new ComponentColorModel(ColorSpace.getInstance(1000), var7, false, 3, 0);
      this.tmpImage = new BufferedImage(var10, var9, false, (Hashtable)null);
   }

   private static native boolean check_gtk();

   private static native boolean load_gtk();

   private static native boolean unload_gtk();

   private native boolean load_gtk_icon(String var1);

   private native boolean load_stock_icon(int var1, String var2, int var3, int var4, String var5);

   private native void nativeSync();

   public void sync() {
      this.nativeSync();
      OGLRenderQueue.sync();
   }

   protected RenderingHints getDesktopAAHints() {
      Object var1 = this.getDesktopProperty("gnome.Xft/Antialias");
      if (var1 == null) {
         var1 = this.getDesktopProperty("fontconfig/Antialias");
         return var1 != null ? new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, var1) : null;
      } else {
         boolean var2 = var1 instanceof Number && ((Number)var1).intValue() != 0;
         Object var3;
         if (var2) {
            String var4 = (String)this.getDesktopProperty("gnome.Xft/RGBA");
            if (var4 != null && !var4.equals("none")) {
               if (var4.equals("rgb")) {
                  var3 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
               } else if (var4.equals("bgr")) {
                  var3 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
               } else if (var4.equals("vrgb")) {
                  var3 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
               } else if (var4.equals("vbgr")) {
                  var3 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
               } else {
                  var3 = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
               }
            } else {
               var3 = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
            }
         } else {
            var3 = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
         }

         return new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, var3);
      }
   }

   private native boolean gtkCheckVersionImpl(int var1, int var2, int var3);

   public boolean checkGtkVersion(int var1, int var2, int var3) {
      return this.loadGTK() ? this.gtkCheckVersionImpl(var1, var2, var3) : false;
   }
}
