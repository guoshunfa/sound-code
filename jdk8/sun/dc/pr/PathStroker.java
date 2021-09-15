package sun.dc.pr;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.geom.PathConsumer2D;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathError;
import sun.dc.path.PathException;

public class PathStroker implements PathConsumer {
   public static final int ROUND = 10;
   public static final int SQUARE = 20;
   public static final int BUTT = 30;
   public static final int BEVEL = 40;
   public static final int MITER = 50;
   private PathConsumer dest;
   private PathConsumer2D dest2D;
   private long cData;

   public PathStroker(PathConsumer var1) {
      if (var1 == null) {
         throw new InternalError("null dest for path");
      } else {
         this.dest = var1;
         this.cInitialize(var1);
         this.reset();
      }
   }

   public PathStroker(PathConsumer2D var1) {
      if (var1 == null) {
         throw new InternalError("null dest for path");
      } else {
         this.dest2D = var1;
         this.cInitialize2D(var1);
         this.reset();
      }
   }

   public native void dispose();

   protected static void classFinalize() throws Throwable {
      cClassFinalize();
   }

   public PathConsumer getConsumer() {
      return this.dest;
   }

   public native void setPenDiameter(float var1) throws PRError;

   public native void setPenT4(float[] var1) throws PRError;

   public native void setPenFitting(float var1, int var2) throws PRError;

   public native void setCaps(int var1) throws PRError;

   public native void setCorners(int var1, float var2) throws PRError;

   public native void setOutputT6(float[] var1) throws PRError;

   public native void setOutputConsumer(PathConsumer var1) throws PRError;

   public native void reset();

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

   private static native void cClassInitialize();

   private static native void cClassFinalize();

   private native void cInitialize(PathConsumer var1);

   private native void cInitialize2D(PathConsumer2D var1);

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
