package sun.dc.pr;

import sun.dc.DuctusRenderingEngine;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathError;
import sun.dc.path.PathException;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.pipe.AATileGenerator;

public class Rasterizer implements AATileGenerator {
   public static final int EOFILL = 1;
   public static final int NZFILL = 2;
   public static final int STROKE = 3;
   public static final int ROUND = 10;
   public static final int SQUARE = 20;
   public static final int BUTT = 30;
   public static final int BEVEL = 40;
   public static final int MITER = 50;
   public static final int TILE_SIZE;
   public static final int TILE_SIZE_L2S;
   public static final int MAX_ALPHA = 1000000;
   public static final int MAX_MITER = 10;
   public static final int MAX_WN = 63;
   public static final int TILE_IS_ALL_0 = 0;
   public static final int TILE_IS_ALL_1 = 1;
   public static final int TILE_IS_GENERAL = 2;
   private static final int BEG = 1;
   private static final int PAC_FILL = 2;
   private static final int PAC_STROKE = 3;
   private static final int PATH = 4;
   private static final int SUBPATH = 5;
   private static final int RAS = 6;
   private int state = 1;
   private PathFiller filler = new PathFiller();
   private PathStroker stroker;
   private PathDasher dasher;
   private PathConsumer curPC;

   public Rasterizer() {
      this.stroker = new PathStroker(this.filler);
      this.dasher = new PathDasher(this.stroker);
      Disposer.addRecord(this, new Rasterizer.ConsumerDisposer(this.filler, this.stroker, this.dasher));
   }

   public void setUsage(int var1) throws PRError {
      if (this.state != 1) {
         throw new PRError("setUsage: unexpected");
      } else {
         if (var1 == 1) {
            this.filler.setFillMode(1);
            this.curPC = this.filler;
            this.state = 2;
         } else if (var1 == 2) {
            this.filler.setFillMode(2);
            this.curPC = this.filler;
            this.state = 2;
         } else {
            if (var1 != 3) {
               throw new PRError("setUsage: unknown usage type");
            }

            this.curPC = this.stroker;
            this.filler.setFillMode(2);
            this.stroker.setPenDiameter(1.0F);
            this.stroker.setPenT4((float[])null);
            this.stroker.setCaps(10);
            this.stroker.setCorners(10, 0.0F);
            this.state = 3;
         }

      }
   }

   public void setPenDiameter(float var1) throws PRError {
      if (this.state != 3) {
         throw new PRError("setPenDiameter: unexpected");
      } else {
         this.stroker.setPenDiameter(var1);
      }
   }

   public void setPenT4(float[] var1) throws PRError {
      if (this.state != 3) {
         throw new PRError("setPenT4: unexpected");
      } else {
         this.stroker.setPenT4(var1);
      }
   }

   public void setPenFitting(float var1, int var2) throws PRError {
      if (this.state != 3) {
         throw new PRError("setPenFitting: unexpected");
      } else {
         this.stroker.setPenFitting(var1, var2);
      }
   }

   public void setPenDisplacement(float var1, float var2) throws PRError {
      if (this.state != 3) {
         throw new PRError("setPenDisplacement: unexpected");
      } else {
         float[] var3 = new float[]{1.0F, 0.0F, 0.0F, 1.0F, var1, var2};
         this.stroker.setOutputT6(var3);
      }
   }

   public void setCaps(int var1) throws PRError {
      if (this.state != 3) {
         throw new PRError("setCaps: unexpected");
      } else {
         this.stroker.setCaps(var1);
      }
   }

   public void setCorners(int var1, float var2) throws PRError {
      if (this.state != 3) {
         throw new PRError("setCorners: unexpected");
      } else {
         this.stroker.setCorners(var1, var2);
      }
   }

   public void setDash(float[] var1, float var2) throws PRError {
      if (this.state != 3) {
         throw new PRError("setDash: unexpected");
      } else {
         this.dasher.setDash(var1, var2);
         this.curPC = this.dasher;
      }
   }

   public void setDashT4(float[] var1) throws PRError {
      if (this.state != 3) {
         throw new PRError("setDashT4: unexpected");
      } else {
         this.dasher.setDashT4(var1);
      }
   }

   public void beginPath(float[] var1) throws PRError {
      this.beginPath();
   }

   public void beginPath() throws PRError {
      if (this.state != 2 && this.state != 3) {
         throw new PRError("beginPath: unexpected");
      } else {
         try {
            this.curPC.beginPath();
            this.state = 4;
         } catch (PathError var2) {
            throw new PRError(var2.getMessage());
         }
      }
   }

   public void beginSubpath(float var1, float var2) throws PRError {
      if (this.state != 4 && this.state != 5) {
         throw new PRError("beginSubpath: unexpected");
      } else {
         try {
            this.curPC.beginSubpath(var1, var2);
            this.state = 5;
         } catch (PathError var4) {
            throw new PRError(var4.getMessage());
         }
      }
   }

   public void appendLine(float var1, float var2) throws PRError {
      if (this.state != 5) {
         throw new PRError("appendLine: unexpected");
      } else {
         try {
            this.curPC.appendLine(var1, var2);
         } catch (PathError var4) {
            throw new PRError(var4.getMessage());
         }
      }
   }

   public void appendQuadratic(float var1, float var2, float var3, float var4) throws PRError {
      if (this.state != 5) {
         throw new PRError("appendQuadratic: unexpected");
      } else {
         try {
            this.curPC.appendQuadratic(var1, var2, var3, var4);
         } catch (PathError var6) {
            throw new PRError(var6.getMessage());
         }
      }
   }

   public void appendCubic(float var1, float var2, float var3, float var4, float var5, float var6) throws PRError {
      if (this.state != 5) {
         throw new PRError("appendCubic: unexpected");
      } else {
         try {
            this.curPC.appendCubic(var1, var2, var3, var4, var5, var6);
         } catch (PathError var8) {
            throw new PRError(var8.getMessage());
         }
      }
   }

   public void closedSubpath() throws PRError {
      if (this.state != 5) {
         throw new PRError("closedSubpath: unexpected");
      } else {
         try {
            this.curPC.closedSubpath();
         } catch (PathError var2) {
            throw new PRError(var2.getMessage());
         }
      }
   }

   public void endPath() throws PRError, PRException {
      if (this.state != 4 && this.state != 5) {
         throw new PRError("endPath: unexpected");
      } else {
         try {
            this.curPC.endPath();
            this.state = 6;
         } catch (PathError var2) {
            throw new PRError(var2.getMessage());
         } catch (PathException var3) {
            throw new PRException(var3.getMessage());
         }
      }
   }

   public void useProxy(FastPathProducer var1) throws PRError, PRException {
      if (this.state != 2 && this.state != 3) {
         throw new PRError("useProxy: unexpected");
      } else {
         try {
            this.curPC.useProxy(var1);
            this.state = 6;
         } catch (PathError var3) {
            throw new PRError(var3.getMessage());
         } catch (PathException var4) {
            throw new PRException(var4.getMessage());
         }
      }
   }

   public void getAlphaBox(int[] var1) throws PRError {
      this.filler.getAlphaBox(var1);
   }

   public void setOutputArea(float var1, float var2, int var3, int var4) throws PRError, PRException {
      this.filler.setOutputArea(var1, var2, var3, var4);
   }

   public int getTileState() throws PRError {
      return this.filler.getTileState();
   }

   public void writeAlpha(byte[] var1, int var2, int var3, int var4) throws PRError, PRException, InterruptedException {
      this.filler.writeAlpha(var1, var2, var3, var4);
   }

   public void writeAlpha(char[] var1, int var2, int var3, int var4) throws PRError, PRException, InterruptedException {
      this.filler.writeAlpha(var1, var2, var3, var4);
   }

   public void nextTile() throws PRError {
      this.filler.nextTile();
   }

   public void reset() {
      this.state = 1;
      this.filler.reset();
      this.stroker.reset();
      this.dasher.reset();
   }

   public int getTileWidth() {
      return TILE_SIZE;
   }

   public int getTileHeight() {
      return TILE_SIZE;
   }

   public int getTypicalAlpha() {
      int var1 = this.filler.getTileState();
      switch(var1) {
      case 0:
         var1 = 0;
         break;
      case 1:
         var1 = 255;
         break;
      case 2:
         var1 = 128;
      }

      return var1;
   }

   public void getAlpha(byte[] var1, int var2, int var3) {
      Class var4 = Rasterizer.class;
      synchronized(Rasterizer.class) {
         try {
            this.filler.writeAlpha((byte[])var1, 1, var3, var2);
         } catch (PRException var7) {
            throw new InternalError("Ductus AA error: " + var7.getMessage());
         } catch (InterruptedException var8) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public void dispose() {
      DuctusRenderingEngine.dropRasterizer(this);
   }

   static {
      TILE_SIZE = 1 << PathFiller.tileSizeL2S;
      TILE_SIZE_L2S = PathFiller.tileSizeL2S;
   }

   private static class ConsumerDisposer implements DisposerRecord {
      PathConsumer filler;
      PathConsumer stroker;
      PathConsumer dasher;

      public ConsumerDisposer(PathConsumer var1, PathConsumer var2, PathConsumer var3) {
         this.filler = var1;
         this.stroker = var2;
         this.dasher = var3;
      }

      public void dispose() {
         this.filler.dispose();
         this.stroker.dispose();
         this.dasher.dispose();
      }
   }
}
