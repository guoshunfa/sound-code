package sun.dc.pr;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathError;
import sun.dc.path.PathException;

public class PathFiller implements PathConsumer {
   public static final int EOFILL = 1;
   public static final int NZFILL = 2;
   public static final int MAX_PATH = 1000000;
   public static final int TILE_IS_ALL_0 = 0;
   public static final int TILE_IS_ALL_1 = 1;
   public static final int TILE_IS_GENERAL = 2;
   static int tileSizeL2S;
   private static int tileSize;
   private static float tileSizeF;
   public static final float maxPathF = 1000000.0F;
   private long cData;

   public static final boolean validLoCoord(float var0) {
      return var0 >= -1000000.0F;
   }

   public static final boolean validHiCoord(float var0) {
      return var0 <= 1000000.0F;
   }

   public PathFiller() {
      this.cInitialize();
      this.reset();
   }

   public native void dispose();

   protected static void classFinalize() throws Throwable {
      cClassFinalize();
   }

   public PathConsumer getConsumer() {
      return null;
   }

   public native void setFillMode(int var1) throws PRError;

   public native void beginPath() throws PathError;

   public native void beginSubpath(float var1, float var2) throws PathError;

   public native void appendLine(float var1, float var2) throws PathError;

   public native void appendQuadratic(float var1, float var2, float var3, float var4) throws PathError;

   public native void appendCubic(float var1, float var2, float var3, float var4, float var5, float var6) throws PathError;

   public native void closedSubpath() throws PathError;

   public native void endPath() throws PathError, PathException;

   public void useProxy(FastPathProducer var1) throws PathError, PathException {
      var1.sendTo(this);
   }

   public native long getCPathConsumer();

   public native void getAlphaBox(int[] var1) throws PRError;

   public native void setOutputArea(float var1, float var2, int var3, int var4) throws PRError, PRException;

   public native int getTileState() throws PRError;

   public void writeAlpha(byte[] var1, int var2, int var3, int var4) throws PRError, PRException, InterruptedException {
      this.writeAlpha8(var1, var2, var3, var4);
   }

   public void writeAlpha(char[] var1, int var2, int var3, int var4) throws PRError, PRException, InterruptedException {
      this.writeAlpha16(var1, var2, var3, var4);
   }

   private native void writeAlpha8(byte[] var1, int var2, int var3, int var4) throws PRError, PRException;

   private native void writeAlpha16(char[] var1, int var2, int var3, int var4) throws PRError, PRException;

   public native void nextTile() throws PRError;

   public native void reset();

   private static native void cClassInitialize();

   private static native void cClassFinalize();

   private native void cInitialize();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("dcpr");
            return null;
         }
      });
      cClassInitialize();
   }
}
