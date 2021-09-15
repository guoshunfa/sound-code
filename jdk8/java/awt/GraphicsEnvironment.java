package java.awt;

import java.awt.image.BufferedImage;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.java2d.HeadlessGraphicsEnvironment;
import sun.java2d.SunGraphicsEnvironment;
import sun.security.action.GetPropertyAction;

public abstract class GraphicsEnvironment {
   private static GraphicsEnvironment localEnv;
   private static Boolean headless;
   private static Boolean defaultHeadless;

   protected GraphicsEnvironment() {
   }

   public static synchronized GraphicsEnvironment getLocalGraphicsEnvironment() {
      if (localEnv == null) {
         localEnv = createGE();
      }

      return localEnv;
   }

   private static GraphicsEnvironment createGE() {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.awt.graphicsenv", (String)null)));

      try {
         Class var2;
         try {
            var2 = Class.forName(var1);
         } catch (ClassNotFoundException var5) {
            ClassLoader var4 = ClassLoader.getSystemClassLoader();
            var2 = Class.forName(var1, true, var4);
         }

         Object var0 = (GraphicsEnvironment)var2.newInstance();
         if (isHeadless()) {
            var0 = new HeadlessGraphicsEnvironment((GraphicsEnvironment)var0);
         }

         return (GraphicsEnvironment)var0;
      } catch (ClassNotFoundException var6) {
         throw new Error("Could not find class: " + var1);
      } catch (InstantiationException var7) {
         throw new Error("Could not instantiate Graphics Environment: " + var1);
      } catch (IllegalAccessException var8) {
         throw new Error("Could not access Graphics Environment: " + var1);
      }
   }

   public static boolean isHeadless() {
      return getHeadlessProperty();
   }

   static String getHeadlessMessage() {
      if (headless == null) {
         getHeadlessProperty();
      }

      return defaultHeadless != Boolean.TRUE ? null : "\nNo X11 DISPLAY variable was set, but this program performed an operation which requires it.";
   }

   private static boolean getHeadlessProperty() {
      if (headless == null) {
         AccessController.doPrivileged(() -> {
            String var0 = System.getProperty("java.awt.headless");
            if (var0 == null) {
               if (System.getProperty("javaplugin.version") != null) {
                  headless = defaultHeadless = Boolean.FALSE;
               } else {
                  String var1 = System.getProperty("os.name");
                  if (var1.contains("OS X") && "sun.awt.HToolkit".equals(System.getProperty("awt.toolkit"))) {
                     headless = defaultHeadless = Boolean.TRUE;
                  } else {
                     String var2 = System.getenv("DISPLAY");
                     headless = defaultHeadless = ("Linux".equals(var1) || "SunOS".equals(var1) || "FreeBSD".equals(var1) || "NetBSD".equals(var1) || "OpenBSD".equals(var1) || "AIX".equals(var1)) && (var2 == null || var2.trim().isEmpty());
                  }
               }
            } else {
               headless = Boolean.valueOf(var0);
            }

            return null;
         });
      }

      return headless;
   }

   static void checkHeadless() throws HeadlessException {
      if (isHeadless()) {
         throw new HeadlessException();
      }
   }

   public boolean isHeadlessInstance() {
      return getHeadlessProperty();
   }

   public abstract GraphicsDevice[] getScreenDevices() throws HeadlessException;

   public abstract GraphicsDevice getDefaultScreenDevice() throws HeadlessException;

   public abstract Graphics2D createGraphics(BufferedImage var1);

   public abstract Font[] getAllFonts();

   public abstract String[] getAvailableFontFamilyNames();

   public abstract String[] getAvailableFontFamilyNames(Locale var1);

   public boolean registerFont(Font var1) {
      if (var1 == null) {
         throw new NullPointerException("font cannot be null.");
      } else {
         FontManager var2 = FontManagerFactory.getInstance();
         return var2.registerFont(var1);
      }
   }

   public void preferLocaleFonts() {
      FontManager var1 = FontManagerFactory.getInstance();
      var1.preferLocaleFonts();
   }

   public void preferProportionalFonts() {
      FontManager var1 = FontManagerFactory.getInstance();
      var1.preferProportionalFonts();
   }

   public Point getCenterPoint() throws HeadlessException {
      Rectangle var1 = SunGraphicsEnvironment.getUsableBounds(this.getDefaultScreenDevice());
      return new Point(var1.width / 2 + var1.x, var1.height / 2 + var1.y);
   }

   public Rectangle getMaximumWindowBounds() throws HeadlessException {
      return SunGraphicsEnvironment.getUsableBounds(this.getDefaultScreenDevice());
   }
}
