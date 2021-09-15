package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public class JPEGImageDecoder extends ImageDecoder {
   private static ColorModel RGBcolormodel;
   private static ColorModel ARGBcolormodel;
   private static ColorModel Graycolormodel;
   private static final Class InputStreamClass = InputStream.class;
   private ColorModel colormodel;
   Hashtable props = new Hashtable();
   private static final int hintflags = 22;

   private static native void initIDs(Class var0);

   private native void readImage(InputStream var1, byte[] var2) throws ImageFormatException, IOException;

   public JPEGImageDecoder(InputStreamImageSource var1, InputStream var2) {
      super(var1, var2);
   }

   private static void error(String var0) throws ImageFormatException {
      throw new ImageFormatException(var0);
   }

   public boolean sendHeaderInfo(int var1, int var2, boolean var3, boolean var4, boolean var5) {
      this.setDimensions(var1, var2);
      this.setProperties(this.props);
      if (var3) {
         this.colormodel = Graycolormodel;
      } else if (var4) {
         this.colormodel = ARGBcolormodel;
      } else {
         this.colormodel = RGBcolormodel;
      }

      this.setColorModel(this.colormodel);
      byte var6 = 22;
      if (!var5) {
         int var7 = var6 | 8;
      }

      this.setHints(22);
      this.headerComplete();
      return true;
   }

   public boolean sendPixels(int[] var1, int var2) {
      int var3 = this.setPixels(0, var2, var1.length, 1, this.colormodel, var1, 0, var1.length);
      if (var3 <= 0) {
         this.aborted = true;
      }

      return !this.aborted;
   }

   public boolean sendPixels(byte[] var1, int var2) {
      int var3 = this.setPixels(0, var2, var1.length, 1, this.colormodel, var1, 0, var1.length);
      if (var3 <= 0) {
         this.aborted = true;
      }

      return !this.aborted;
   }

   public void produceImage() throws IOException, ImageFormatException {
      try {
         this.readImage(this.input, new byte[1024]);
         if (!this.aborted) {
            this.imageComplete(3, true);
         }
      } catch (IOException var5) {
         if (!this.aborted) {
            throw var5;
         }
      } finally {
         this.close();
      }

   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("jpeg");
            return null;
         }
      });
      initIDs(InputStreamClass);
      RGBcolormodel = new DirectColorModel(24, 16711680, 65280, 255);
      ARGBcolormodel = ColorModel.getRGBdefault();
      byte[] var0 = new byte[256];

      for(int var1 = 0; var1 < 256; ++var1) {
         var0[var1] = (byte)var1;
      }

      Graycolormodel = new IndexColorModel(8, 256, var0, var0, var0);
   }
}
