package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.image.SunWritableRaster;
import sun.util.logging.PlatformLogger;

public final class SplashScreen {
   private BufferedImage image;
   private final long splashPtr;
   private static boolean wasClosed = false;
   private URL imageURL;
   private static SplashScreen theInstance = null;
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.SplashScreen");

   SplashScreen(long var1) {
      this.splashPtr = var1;
   }

   public static SplashScreen getSplashScreen() {
      Class var0 = SplashScreen.class;
      synchronized(SplashScreen.class) {
         if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
         } else {
            if (!wasClosed && theInstance == null) {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     System.loadLibrary("splashscreen");
                     return null;
                  }
               });
               long var1 = _getInstance();
               if (var1 != 0L && _isVisible(var1)) {
                  theInstance = new SplashScreen(var1);
               }
            }

            return theInstance;
         }
      }
   }

   public void setImageURL(URL var1) throws NullPointerException, IOException, IllegalStateException {
      this.checkVisible();
      URLConnection var2 = var1.openConnection();
      var2.connect();
      int var3 = var2.getContentLength();
      InputStream var4 = var2.getInputStream();
      byte[] var5 = new byte[var3];
      int var6 = 0;

      while(true) {
         int var7 = var4.available();
         if (var7 <= 0) {
            var7 = 1;
         }

         if (var6 + var7 > var3) {
            var3 = var6 * 2;
            if (var6 + var7 > var3) {
               var3 = var7 + var6;
            }

            byte[] var8 = var5;
            var5 = new byte[var3];
            System.arraycopy(var8, 0, var5, 0, var6);
         }

         int var12 = var4.read(var5, var6, var7);
         if (var12 < 0) {
            Class var11 = SplashScreen.class;
            synchronized(SplashScreen.class) {
               this.checkVisible();
               if (!_setImageData(this.splashPtr, var5)) {
                  throw new IOException("Bad image format or i/o error when loading image");
               }

               this.imageURL = var1;
               return;
            }
         }

         var6 += var12;
      }
   }

   private void checkVisible() {
      if (!this.isVisible()) {
         throw new IllegalStateException("no splash screen available");
      }
   }

   public URL getImageURL() throws IllegalStateException {
      Class var1 = SplashScreen.class;
      synchronized(SplashScreen.class) {
         this.checkVisible();
         if (this.imageURL == null) {
            try {
               String var2 = _getImageFileName(this.splashPtr);
               String var3 = _getImageJarName(this.splashPtr);
               if (var2 != null) {
                  if (var3 != null) {
                     this.imageURL = new URL("jar:" + (new File(var3)).toURL().toString() + "!/" + var2);
                  } else {
                     this.imageURL = (new File(var2)).toURL();
                  }
               }
            } catch (MalformedURLException var5) {
               if (log.isLoggable(PlatformLogger.Level.FINE)) {
                  log.fine("MalformedURLException caught in the getImageURL() method", (Throwable)var5);
               }
            }
         }

         return this.imageURL;
      }
   }

   public Rectangle getBounds() throws IllegalStateException {
      Class var1 = SplashScreen.class;
      synchronized(SplashScreen.class) {
         this.checkVisible();
         float var2 = _getScaleFactor(this.splashPtr);
         Rectangle var3 = _getBounds(this.splashPtr);

         assert var2 > 0.0F;

         if (var2 > 0.0F && var2 != 1.0F) {
            var3.setSize((int)(var3.getWidth() / (double)var2), (int)(var3.getHeight() / (double)var2));
         }

         return var3;
      }
   }

   public Dimension getSize() throws IllegalStateException {
      return this.getBounds().getSize();
   }

   public Graphics2D createGraphics() throws IllegalStateException {
      Class var1 = SplashScreen.class;
      synchronized(SplashScreen.class) {
         this.checkVisible();
         if (this.image == null) {
            Dimension var2 = _getBounds(this.splashPtr).getSize();
            this.image = new BufferedImage(var2.width, var2.height, 2);
         }

         float var6 = _getScaleFactor(this.splashPtr);
         Graphics2D var3 = this.image.createGraphics();

         assert var6 > 0.0F;

         if (var6 <= 0.0F) {
            var6 = 1.0F;
         }

         var3.scale((double)var6, (double)var6);
         return var3;
      }
   }

   public void update() throws IllegalStateException {
      Class var2 = SplashScreen.class;
      BufferedImage var1;
      synchronized(SplashScreen.class) {
         this.checkVisible();
         var1 = this.image;
      }

      if (var1 == null) {
         throw new IllegalStateException("no overlay image available");
      } else {
         DataBuffer var12 = var1.getRaster().getDataBuffer();
         if (!(var12 instanceof DataBufferInt)) {
            throw new AssertionError("Overlay image DataBuffer is of invalid type == " + var12.getClass().getName());
         } else {
            int var3 = var12.getNumBanks();
            if (var3 != 1) {
               throw new AssertionError("Invalid number of banks ==" + var3 + " in overlay image DataBuffer");
            } else if (!(var1.getSampleModel() instanceof SinglePixelPackedSampleModel)) {
               throw new AssertionError("Overlay image has invalid sample model == " + var1.getSampleModel().getClass().getName());
            } else {
               SinglePixelPackedSampleModel var4 = (SinglePixelPackedSampleModel)var1.getSampleModel();
               int var5 = var4.getScanlineStride();
               Rectangle var6 = var1.getRaster().getBounds();
               int[] var7 = SunWritableRaster.stealData((DataBufferInt)((DataBufferInt)var12), 0);
               Class var8 = SplashScreen.class;
               synchronized(SplashScreen.class) {
                  this.checkVisible();
                  _update(this.splashPtr, var7, var6.x, var6.y, var6.width, var6.height, var5);
               }
            }
         }
      }
   }

   public void close() throws IllegalStateException {
      Class var1 = SplashScreen.class;
      synchronized(SplashScreen.class) {
         this.checkVisible();
         _close(this.splashPtr);
         this.image = null;
         markClosed();
      }
   }

   static void markClosed() {
      Class var0 = SplashScreen.class;
      synchronized(SplashScreen.class) {
         wasClosed = true;
         theInstance = null;
      }
   }

   public boolean isVisible() {
      Class var1 = SplashScreen.class;
      synchronized(SplashScreen.class) {
         return !wasClosed && _isVisible(this.splashPtr);
      }
   }

   private static native void _update(long var0, int[] var2, int var3, int var4, int var5, int var6, int var7);

   private static native boolean _isVisible(long var0);

   private static native Rectangle _getBounds(long var0);

   private static native long _getInstance();

   private static native void _close(long var0);

   private static native String _getImageFileName(long var0);

   private static native String _getImageJarName(long var0);

   private static native boolean _setImageData(long var0, byte[] var2);

   private static native float _getScaleFactor(long var0);
}
