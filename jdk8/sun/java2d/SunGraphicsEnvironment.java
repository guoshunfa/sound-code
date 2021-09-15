package sun.java2d;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeMap;
import sun.awt.DisplayChangedListener;
import sun.awt.SunDisplayChanger;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.FontManagerForSGE;

public abstract class SunGraphicsEnvironment extends GraphicsEnvironment implements DisplayChangedListener {
   public static boolean isOpenSolaris;
   private static Font defaultFont;
   protected GraphicsDevice[] screens;
   protected SunDisplayChanger displayChanger = new SunDisplayChanger();

   public SunGraphicsEnvironment() {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String var1 = System.getProperty("os.version", "0.0");

            try {
               float var2 = Float.parseFloat(var1);
               if (var2 > 5.1F) {
                  File var3 = new File("/etc/release");
                  FileInputStream var4 = new FileInputStream(var3);
                  InputStreamReader var5 = new InputStreamReader(var4, "ISO-8859-1");
                  BufferedReader var6 = new BufferedReader(var5);
                  String var7 = var6.readLine();
                  if (var7.indexOf("OpenSolaris") >= 0) {
                     SunGraphicsEnvironment.isOpenSolaris = true;
                  } else {
                     String var8 = "/usr/openwin/lib/X11/fonts/TrueType/CourierNew.ttf";
                     File var9 = new File(var8);
                     SunGraphicsEnvironment.isOpenSolaris = !var9.exists();
                  }

                  var4.close();
               }
            } catch (Exception var10) {
            }

            SunGraphicsEnvironment.defaultFont = new Font("Dialog", 0, 12);
            return null;
         }
      });
   }

   public synchronized GraphicsDevice[] getScreenDevices() {
      GraphicsDevice[] var1 = this.screens;
      if (var1 == null) {
         int var2 = this.getNumScreens();
         var1 = new GraphicsDevice[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            var1[var3] = this.makeScreenDevice(var3);
         }

         this.screens = var1;
      }

      return var1;
   }

   protected abstract int getNumScreens();

   protected abstract GraphicsDevice makeScreenDevice(int var1);

   public GraphicsDevice getDefaultScreenDevice() {
      GraphicsDevice[] var1 = this.getScreenDevices();
      if (var1.length == 0) {
         throw new AWTError("no screen devices");
      } else {
         return var1[0];
      }
   }

   public Graphics2D createGraphics(BufferedImage var1) {
      if (var1 == null) {
         throw new NullPointerException("BufferedImage cannot be null");
      } else {
         SurfaceData var2 = SurfaceData.getPrimarySurfaceData(var1);
         return new SunGraphics2D(var2, Color.white, Color.black, defaultFont);
      }
   }

   public static FontManagerForSGE getFontManagerForSGE() {
      FontManager var0 = FontManagerFactory.getInstance();
      return (FontManagerForSGE)var0;
   }

   public static void useAlternateFontforJALocales() {
      getFontManagerForSGE().useAlternateFontforJALocales();
   }

   public Font[] getAllFonts() {
      FontManagerForSGE var1 = getFontManagerForSGE();
      Font[] var2 = var1.getAllInstalledFonts();
      Font[] var3 = var1.getCreatedFonts();
      if (var3 != null && var3.length != 0) {
         int var4 = var2.length + var3.length;
         Font[] var5 = (Font[])Arrays.copyOf((Object[])var2, var4);
         System.arraycopy(var3, 0, var5, var2.length, var3.length);
         return var5;
      } else {
         return var2;
      }
   }

   public String[] getAvailableFontFamilyNames(Locale var1) {
      FontManagerForSGE var2 = getFontManagerForSGE();
      String[] var3 = var2.getInstalledFontFamilyNames(var1);
      TreeMap var4 = var2.getCreatedFontFamilyNames();
      if (var4 != null && var4.size() != 0) {
         for(int var5 = 0; var5 < var3.length; ++var5) {
            var4.put(var3[var5].toLowerCase(var1), var3[var5]);
         }

         String[] var8 = new String[var4.size()];
         Object[] var6 = var4.keySet().toArray();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            var8[var7] = (String)var4.get(var6[var7]);
         }

         return var8;
      } else {
         return var3;
      }
   }

   public String[] getAvailableFontFamilyNames() {
      return this.getAvailableFontFamilyNames(Locale.getDefault());
   }

   public static Rectangle getUsableBounds(GraphicsDevice var0) {
      GraphicsConfiguration var1 = var0.getDefaultConfiguration();
      Insets var2 = Toolkit.getDefaultToolkit().getScreenInsets(var1);
      Rectangle var3 = var1.getBounds();
      var3.x += var2.left;
      var3.y += var2.top;
      var3.width -= var2.left + var2.right;
      var3.height -= var2.top + var2.bottom;
      return var3;
   }

   public void displayChanged() {
      GraphicsDevice[] var1 = this.getScreenDevices();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GraphicsDevice var4 = var1[var3];
         if (var4 instanceof DisplayChangedListener) {
            ((DisplayChangedListener)var4).displayChanged();
         }
      }

      this.displayChanger.notifyListeners();
   }

   public void paletteChanged() {
      this.displayChanger.notifyPaletteChanged();
   }

   public abstract boolean isDisplayLocal();

   public void addDisplayChangedListener(DisplayChangedListener var1) {
      this.displayChanger.add(var1);
   }

   public void removeDisplayChangedListener(DisplayChangedListener var1) {
      this.displayChanger.remove(var1);
   }

   public boolean isFlipStrategyPreferred(ComponentPeer var1) {
      return false;
   }
}
