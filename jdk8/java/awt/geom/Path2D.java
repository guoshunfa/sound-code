package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import sun.awt.geom.Curve;

public abstract class Path2D implements Shape, Cloneable {
   public static final int WIND_EVEN_ODD = 0;
   public static final int WIND_NON_ZERO = 1;
   private static final byte SEG_MOVETO = 0;
   private static final byte SEG_LINETO = 1;
   private static final byte SEG_QUADTO = 2;
   private static final byte SEG_CUBICTO = 3;
   private static final byte SEG_CLOSE = 4;
   transient byte[] pointTypes;
   transient int numTypes;
   transient int numCoords;
   transient int windingRule;
   static final int INIT_SIZE = 20;
   static final int EXPAND_MAX = 500;
   static final int EXPAND_MAX_COORDS = 1000;
   static final int EXPAND_MIN = 10;
   private static final byte SERIAL_STORAGE_FLT_ARRAY = 48;
   private static final byte SERIAL_STORAGE_DBL_ARRAY = 49;
   private static final byte SERIAL_SEG_FLT_MOVETO = 64;
   private static final byte SERIAL_SEG_FLT_LINETO = 65;
   private static final byte SERIAL_SEG_FLT_QUADTO = 66;
   private static final byte SERIAL_SEG_FLT_CUBICTO = 67;
   private static final byte SERIAL_SEG_DBL_MOVETO = 80;
   private static final byte SERIAL_SEG_DBL_LINETO = 81;
   private static final byte SERIAL_SEG_DBL_QUADTO = 82;
   private static final byte SERIAL_SEG_DBL_CUBICTO = 83;
   private static final byte SERIAL_SEG_CLOSE = 96;
   private static final byte SERIAL_PATH_END = 97;

   Path2D() {
   }

   Path2D(int var1, int var2) {
      this.setWindingRule(var1);
      this.pointTypes = new byte[var2];
   }

   abstract float[] cloneCoordsFloat(AffineTransform var1);

   abstract double[] cloneCoordsDouble(AffineTransform var1);

   abstract void append(float var1, float var2);

   abstract void append(double var1, double var3);

   abstract Point2D getPoint(int var1);

   abstract void needRoom(boolean var1, int var2);

   abstract int pointCrossings(double var1, double var3);

   abstract int rectCrossings(double var1, double var3, double var5, double var7);

   static byte[] expandPointTypes(byte[] var0, int var1) {
      int var2 = var0.length;
      int var3 = var2 + var1;
      if (var3 < var2) {
         throw new ArrayIndexOutOfBoundsException("pointTypes exceeds maximum capacity !");
      } else {
         int var4 = var2;
         if (var2 > 500) {
            var4 = Math.max(500, var2 >> 3);
         } else if (var2 < 10) {
            var4 = 10;
         }

         assert var4 > 0;

         int var5 = var2 + var4;
         if (var5 < var3) {
            var5 = Integer.MAX_VALUE;
         }

         while(true) {
            try {
               return Arrays.copyOf(var0, var5);
            } catch (OutOfMemoryError var7) {
               if (var5 == var3) {
                  throw var7;
               }

               var5 = var3 + (var5 - var3) / 2;
            }
         }
      }
   }

   public abstract void moveTo(double var1, double var3);

   public abstract void lineTo(double var1, double var3);

   public abstract void quadTo(double var1, double var3, double var5, double var7);

   public abstract void curveTo(double var1, double var3, double var5, double var7, double var9, double var11);

   public final synchronized void closePath() {
      if (this.numTypes == 0 || this.pointTypes[this.numTypes - 1] != 4) {
         this.needRoom(true, 0);
         this.pointTypes[this.numTypes++] = 4;
      }

   }

   public final void append(Shape var1, boolean var2) {
      this.append(var1.getPathIterator((AffineTransform)null), var2);
   }

   public abstract void append(PathIterator var1, boolean var2);

   public final synchronized int getWindingRule() {
      return this.windingRule;
   }

   public final void setWindingRule(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");
      } else {
         this.windingRule = var1;
      }
   }

   public final synchronized Point2D getCurrentPoint() {
      int var1 = this.numCoords;
      if (this.numTypes >= 1 && var1 >= 1) {
         if (this.pointTypes[this.numTypes - 1] == 4) {
            for(int var2 = this.numTypes - 2; var2 > 0; --var2) {
               switch(this.pointTypes[var2]) {
               case 0:
                  return this.getPoint(var1 - 2);
               case 1:
                  var1 -= 2;
                  break;
               case 2:
                  var1 -= 4;
                  break;
               case 3:
                  var1 -= 6;
               case 4:
               }
            }
         }

         return this.getPoint(var1 - 2);
      } else {
         return null;
      }
   }

   public final synchronized void reset() {
      this.numTypes = this.numCoords = 0;
   }

   public abstract void transform(AffineTransform var1);

   public final synchronized Shape createTransformedShape(AffineTransform var1) {
      Path2D var2 = (Path2D)this.clone();
      if (var1 != null) {
         var2.transform(var1);
      }

      return var2;
   }

   public final Rectangle getBounds() {
      return this.getBounds2D().getBounds();
   }

   public static boolean contains(PathIterator var0, double var1, double var3) {
      if (var1 * 0.0D + var3 * 0.0D == 0.0D) {
         int var5 = var0.getWindingRule() == 1 ? -1 : 1;
         int var6 = Curve.pointCrossingsForPath(var0, var1, var3);
         return (var6 & var5) != 0;
      } else {
         return false;
      }
   }

   public static boolean contains(PathIterator var0, Point2D var1) {
      return contains(var0, var1.getX(), var1.getY());
   }

   public final boolean contains(double var1, double var3) {
      if (var1 * 0.0D + var3 * 0.0D == 0.0D) {
         if (this.numTypes < 2) {
            return false;
         } else {
            int var5 = this.windingRule == 1 ? -1 : 1;
            return (this.pointCrossings(var1, var3) & var5) != 0;
         }
      } else {
         return false;
      }
   }

   public final boolean contains(Point2D var1) {
      return this.contains(var1.getX(), var1.getY());
   }

   public static boolean contains(PathIterator var0, double var1, double var3, double var5, double var7) {
      if (!java.lang.Double.isNaN(var1 + var5) && !java.lang.Double.isNaN(var3 + var7)) {
         if (var5 > 0.0D && var7 > 0.0D) {
            int var9 = var0.getWindingRule() == 1 ? -1 : 2;
            int var10 = Curve.rectCrossingsForPath(var0, var1, var3, var1 + var5, var3 + var7);
            return var10 != Integer.MIN_VALUE && (var10 & var9) != 0;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean contains(PathIterator var0, Rectangle2D var1) {
      return contains(var0, var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public final boolean contains(double var1, double var3, double var5, double var7) {
      if (!java.lang.Double.isNaN(var1 + var5) && !java.lang.Double.isNaN(var3 + var7)) {
         if (var5 > 0.0D && var7 > 0.0D) {
            int var9 = this.windingRule == 1 ? -1 : 2;
            int var10 = this.rectCrossings(var1, var3, var1 + var5, var3 + var7);
            return var10 != Integer.MIN_VALUE && (var10 & var9) != 0;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public final boolean contains(Rectangle2D var1) {
      return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public static boolean intersects(PathIterator var0, double var1, double var3, double var5, double var7) {
      if (!java.lang.Double.isNaN(var1 + var5) && !java.lang.Double.isNaN(var3 + var7)) {
         if (var5 > 0.0D && var7 > 0.0D) {
            int var9 = var0.getWindingRule() == 1 ? -1 : 2;
            int var10 = Curve.rectCrossingsForPath(var0, var1, var3, var1 + var5, var3 + var7);
            return var10 == Integer.MIN_VALUE || (var10 & var9) != 0;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean intersects(PathIterator var0, Rectangle2D var1) {
      return intersects(var0, var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public final boolean intersects(double var1, double var3, double var5, double var7) {
      if (!java.lang.Double.isNaN(var1 + var5) && !java.lang.Double.isNaN(var3 + var7)) {
         if (var5 > 0.0D && var7 > 0.0D) {
            int var9 = this.windingRule == 1 ? -1 : 2;
            int var10 = this.rectCrossings(var1, var3, var1 + var5, var3 + var7);
            return var10 == Integer.MIN_VALUE || (var10 & var9) != 0;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public final boolean intersects(Rectangle2D var1) {
      return this.intersects(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public final PathIterator getPathIterator(AffineTransform var1, double var2) {
      return new FlatteningPathIterator(this.getPathIterator(var1), var2);
   }

   public abstract Object clone();

   final void writeObject(ObjectOutputStream var1, boolean var2) throws IOException {
      var1.defaultWriteObject();
      float[] var3;
      double[] var4;
      if (var2) {
         var4 = ((Path2D.Double)this).doubleCoords;
         var3 = null;
      } else {
         var3 = ((Path2D.Float)this).floatCoords;
         var4 = null;
      }

      int var5 = this.numTypes;
      var1.writeByte(var2 ? 49 : 48);
      var1.writeInt(var5);
      var1.writeInt(this.numCoords);
      var1.writeByte((byte)this.windingRule);
      int var6 = 0;

      for(int var7 = 0; var7 < var5; ++var7) {
         int var8;
         int var9;
         switch(this.pointTypes[var7]) {
         case 0:
            var8 = 1;
            var9 = var2 ? 80 : 64;
            break;
         case 1:
            var8 = 1;
            var9 = var2 ? 81 : 65;
            break;
         case 2:
            var8 = 2;
            var9 = var2 ? 82 : 66;
            break;
         case 3:
            var8 = 3;
            var9 = var2 ? 83 : 67;
            break;
         case 4:
            var8 = 0;
            var9 = 96;
            break;
         default:
            throw new InternalError("unrecognized path type");
         }

         var1.writeByte(var9);

         while(true) {
            --var8;
            if (var8 < 0) {
               break;
            }

            if (var2) {
               var1.writeDouble(var4[var6++]);
               var1.writeDouble(var4[var6++]);
            } else {
               var1.writeFloat(var3[var6++]);
               var1.writeFloat(var3[var6++]);
            }
         }
      }

      var1.writeByte(97);
   }

   final void readObject(ObjectInputStream var1, boolean var2) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      var1.readByte();
      int var3 = var1.readInt();
      int var4 = var1.readInt();

      try {
         this.setWindingRule(var1.readByte());
      } catch (IllegalArgumentException var11) {
         throw new InvalidObjectException(var11.getMessage());
      }

      this.pointTypes = new byte[var3 >= 0 && var3 <= 20 ? var3 : 20];
      if (var4 < 0 || var4 > 40) {
         var4 = 40;
      }

      if (var2) {
         ((Path2D.Double)this).doubleCoords = new double[var4];
      } else {
         ((Path2D.Float)this).floatCoords = new float[var4];
      }

      label77:
      for(int var6 = 0; var3 < 0 || var6 < var3; ++var6) {
         byte var10 = var1.readByte();
         boolean var7;
         int var8;
         byte var9;
         switch(var10) {
         case 64:
            var7 = false;
            var8 = 1;
            var9 = 0;
            break;
         case 65:
            var7 = false;
            var8 = 1;
            var9 = 1;
            break;
         case 66:
            var7 = false;
            var8 = 2;
            var9 = 2;
            break;
         case 67:
            var7 = false;
            var8 = 3;
            var9 = 3;
            break;
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         default:
            throw new StreamCorruptedException("unrecognized path type");
         case 80:
            var7 = true;
            var8 = 1;
            var9 = 0;
            break;
         case 81:
            var7 = true;
            var8 = 1;
            var9 = 1;
            break;
         case 82:
            var7 = true;
            var8 = 2;
            var9 = 2;
            break;
         case 83:
            var7 = true;
            var8 = 3;
            var9 = 3;
            break;
         case 96:
            var7 = false;
            var8 = 0;
            var9 = 4;
            break;
         case 97:
            if (var3 >= 0) {
               throw new StreamCorruptedException("unexpected PATH_END");
            }
            break label77;
         }

         this.needRoom(var9 != 0, var8 * 2);
         if (var7) {
            while(true) {
               --var8;
               if (var8 < 0) {
                  break;
               }

               this.append(var1.readDouble(), var1.readDouble());
            }
         } else {
            while(true) {
               --var8;
               if (var8 < 0) {
                  break;
               }

               this.append(var1.readFloat(), var1.readFloat());
            }
         }

         this.pointTypes[this.numTypes++] = var9;
      }

      if (var3 >= 0 && var1.readByte() != 97) {
         throw new StreamCorruptedException("missing PATH_END");
      }
   }

   abstract static class Iterator implements PathIterator {
      int typeIdx;
      int pointIdx;
      Path2D path;
      static final int[] curvecoords = new int[]{2, 2, 4, 6, 0};

      Iterator(Path2D var1) {
         this.path = var1;
      }

      public int getWindingRule() {
         return this.path.getWindingRule();
      }

      public boolean isDone() {
         return this.typeIdx >= this.path.numTypes;
      }

      public void next() {
         byte var1 = this.path.pointTypes[this.typeIdx++];
         this.pointIdx += curvecoords[var1];
      }
   }

   public static class Double extends Path2D implements Serializable {
      transient double[] doubleCoords;
      private static final long serialVersionUID = 1826762518450014216L;

      public Double() {
         this(1, 20);
      }

      public Double(int var1) {
         this(var1, 20);
      }

      public Double(int var1, int var2) {
         super(var1, var2);
         this.doubleCoords = new double[var2 * 2];
      }

      public Double(Shape var1) {
         this(var1, (AffineTransform)null);
      }

      public Double(Shape var1, AffineTransform var2) {
         if (var1 instanceof Path2D) {
            Path2D var3 = (Path2D)var1;
            this.setWindingRule(var3.windingRule);
            this.numTypes = var3.numTypes;
            this.pointTypes = Arrays.copyOf(var3.pointTypes, var3.numTypes);
            this.numCoords = var3.numCoords;
            this.doubleCoords = var3.cloneCoordsDouble(var2);
         } else {
            PathIterator var4 = var1.getPathIterator(var2);
            this.setWindingRule(var4.getWindingRule());
            this.pointTypes = new byte[20];
            this.doubleCoords = new double[40];
            this.append(var4, false);
         }

      }

      float[] cloneCoordsFloat(AffineTransform var1) {
         float[] var2 = new float[this.numCoords];
         if (var1 == null) {
            for(int var3 = 0; var3 < this.numCoords; ++var3) {
               var2[var3] = (float)this.doubleCoords[var3];
            }
         } else {
            var1.transform((double[])this.doubleCoords, 0, (float[])var2, 0, this.numCoords / 2);
         }

         return var2;
      }

      double[] cloneCoordsDouble(AffineTransform var1) {
         double[] var2;
         if (var1 == null) {
            var2 = Arrays.copyOf(this.doubleCoords, this.numCoords);
         } else {
            var2 = new double[this.numCoords];
            var1.transform((double[])this.doubleCoords, 0, (double[])var2, 0, this.numCoords / 2);
         }

         return var2;
      }

      void append(float var1, float var2) {
         this.doubleCoords[this.numCoords++] = (double)var1;
         this.doubleCoords[this.numCoords++] = (double)var2;
      }

      void append(double var1, double var3) {
         this.doubleCoords[this.numCoords++] = var1;
         this.doubleCoords[this.numCoords++] = var3;
      }

      Point2D getPoint(int var1) {
         return new Point2D.Double(this.doubleCoords[var1], this.doubleCoords[var1 + 1]);
      }

      void needRoom(boolean var1, int var2) {
         if (this.numTypes == 0 && var1) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
         } else {
            if (this.numTypes >= this.pointTypes.length) {
               this.pointTypes = expandPointTypes(this.pointTypes, 1);
            }

            if (this.numCoords > this.doubleCoords.length - var2) {
               this.doubleCoords = expandCoords(this.doubleCoords, var2);
            }

         }
      }

      static double[] expandCoords(double[] var0, int var1) {
         int var2 = var0.length;
         int var3 = var2 + var1;
         if (var3 < var2) {
            throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !");
         } else {
            int var4 = var2;
            if (var2 > 1000) {
               var4 = Math.max(1000, var2 >> 3);
            } else if (var2 < 10) {
               var4 = 10;
            }

            assert var4 > var1;

            int var5 = var2 + var4;
            if (var5 < var3) {
               var5 = Integer.MAX_VALUE;
            }

            while(true) {
               try {
                  return Arrays.copyOf(var0, var5);
               } catch (OutOfMemoryError var7) {
                  if (var5 == var3) {
                     throw var7;
                  }

                  var5 = var3 + (var5 - var3) / 2;
               }
            }
         }
      }

      public final synchronized void moveTo(double var1, double var3) {
         if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
            this.doubleCoords[this.numCoords - 2] = var1;
            this.doubleCoords[this.numCoords - 1] = var3;
         } else {
            this.needRoom(false, 2);
            this.pointTypes[this.numTypes++] = 0;
            this.doubleCoords[this.numCoords++] = var1;
            this.doubleCoords[this.numCoords++] = var3;
         }

      }

      public final synchronized void lineTo(double var1, double var3) {
         this.needRoom(true, 2);
         this.pointTypes[this.numTypes++] = 1;
         this.doubleCoords[this.numCoords++] = var1;
         this.doubleCoords[this.numCoords++] = var3;
      }

      public final synchronized void quadTo(double var1, double var3, double var5, double var7) {
         this.needRoom(true, 4);
         this.pointTypes[this.numTypes++] = 2;
         this.doubleCoords[this.numCoords++] = var1;
         this.doubleCoords[this.numCoords++] = var3;
         this.doubleCoords[this.numCoords++] = var5;
         this.doubleCoords[this.numCoords++] = var7;
      }

      public final synchronized void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.needRoom(true, 6);
         this.pointTypes[this.numTypes++] = 3;
         this.doubleCoords[this.numCoords++] = var1;
         this.doubleCoords[this.numCoords++] = var3;
         this.doubleCoords[this.numCoords++] = var5;
         this.doubleCoords[this.numCoords++] = var7;
         this.doubleCoords[this.numCoords++] = var9;
         this.doubleCoords[this.numCoords++] = var11;
      }

      int pointCrossings(double var1, double var3) {
         if (this.numTypes == 0) {
            return 0;
         } else {
            double[] var17 = this.doubleCoords;
            double var5;
            double var9 = var5 = var17[0];
            double var7;
            double var11 = var7 = var17[1];
            int var18 = 0;
            int var19 = 2;

            for(int var20 = 1; var20 < this.numTypes; ++var20) {
               double var13;
               double var15;
               switch(this.pointTypes[var20]) {
               case 0:
                  if (var11 != var7) {
                     var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var5, var7);
                  }

                  var5 = var9 = var17[var19++];
                  var7 = var11 = var17[var19++];
                  break;
               case 1:
                  var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var13 = var17[var19++], var15 = var17[var19++]);
                  var9 = var13;
                  var11 = var15;
                  break;
               case 2:
                  var18 += Curve.pointCrossingsForQuad(var1, var3, var9, var11, var17[var19++], var17[var19++], var13 = var17[var19++], var15 = var17[var19++], 0);
                  var9 = var13;
                  var11 = var15;
                  break;
               case 3:
                  var18 += Curve.pointCrossingsForCubic(var1, var3, var9, var11, var17[var19++], var17[var19++], var17[var19++], var17[var19++], var13 = var17[var19++], var15 = var17[var19++], 0);
                  var9 = var13;
                  var11 = var15;
                  break;
               case 4:
                  if (var11 != var7) {
                     var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var5, var7);
                  }

                  var9 = var5;
                  var11 = var7;
               }
            }

            if (var11 != var7) {
               var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var5, var7);
            }

            return var18;
         }
      }

      int rectCrossings(double var1, double var3, double var5, double var7) {
         if (this.numTypes == 0) {
            return 0;
         } else {
            double[] var9 = this.doubleCoords;
            double var14;
            double var10 = var14 = var9[0];
            double var16;
            double var12 = var16 = var9[1];
            int var22 = 0;
            int var23 = 2;

            for(int var24 = 1; var22 != Integer.MIN_VALUE && var24 < this.numTypes; ++var24) {
               double var18;
               double var20;
               switch(this.pointTypes[var24]) {
               case 0:
                  if (var10 != var14 || var12 != var16) {
                     var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
                  }

                  var14 = var10 = var9[var23++];
                  var16 = var12 = var9[var23++];
                  break;
               case 1:
                  var18 = var9[var23++];
                  var20 = var9[var23++];
                  var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var18, var20);
                  var10 = var18;
                  var12 = var20;
                  break;
               case 2:
                  var22 = Curve.rectCrossingsForQuad(var22, var1, var3, var5, var7, var10, var12, var9[var23++], var9[var23++], var18 = var9[var23++], var20 = var9[var23++], 0);
                  var10 = var18;
                  var12 = var20;
                  break;
               case 3:
                  var22 = Curve.rectCrossingsForCubic(var22, var1, var3, var5, var7, var10, var12, var9[var23++], var9[var23++], var9[var23++], var9[var23++], var18 = var9[var23++], var20 = var9[var23++], 0);
                  var10 = var18;
                  var12 = var20;
                  break;
               case 4:
                  if (var10 != var14 || var12 != var16) {
                     var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
                  }

                  var10 = var14;
                  var12 = var16;
               }
            }

            if (var22 != Integer.MIN_VALUE && (var10 != var14 || var12 != var16)) {
               var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
            }

            return var22;
         }
      }

      public final void append(PathIterator var1, boolean var2) {
         for(double[] var3 = new double[6]; !var1.isDone(); var2 = false) {
            switch(var1.currentSegment(var3)) {
            case 0:
               if (var2 && this.numTypes >= 1 && this.numCoords >= 1) {
                  if (this.pointTypes[this.numTypes - 1] == 4 || this.doubleCoords[this.numCoords - 2] != var3[0] || this.doubleCoords[this.numCoords - 1] != var3[1]) {
                     this.lineTo(var3[0], var3[1]);
                  }
                  break;
               }

               this.moveTo(var3[0], var3[1]);
               break;
            case 1:
               this.lineTo(var3[0], var3[1]);
               break;
            case 2:
               this.quadTo(var3[0], var3[1], var3[2], var3[3]);
               break;
            case 3:
               this.curveTo(var3[0], var3[1], var3[2], var3[3], var3[4], var3[5]);
               break;
            case 4:
               this.closePath();
            }

            var1.next();
         }

      }

      public final void transform(AffineTransform var1) {
         var1.transform((double[])this.doubleCoords, 0, (double[])this.doubleCoords, 0, this.numCoords / 2);
      }

      public final synchronized Rectangle2D getBounds2D() {
         int var9 = this.numCoords;
         double var1;
         double var3;
         double var5;
         double var7;
         if (var9 > 0) {
            --var9;
            var3 = var7 = this.doubleCoords[var9];
            --var9;
            var1 = var5 = this.doubleCoords[var9];

            while(var9 > 0) {
               --var9;
               double var10 = this.doubleCoords[var9];
               --var9;
               double var12 = this.doubleCoords[var9];
               if (var12 < var1) {
                  var1 = var12;
               }

               if (var10 < var3) {
                  var3 = var10;
               }

               if (var12 > var5) {
                  var5 = var12;
               }

               if (var10 > var7) {
                  var7 = var10;
               }
            }
         } else {
            var7 = 0.0D;
            var5 = 0.0D;
            var3 = 0.0D;
            var1 = 0.0D;
         }

         return new Rectangle2D.Double(var1, var3, var5 - var1, var7 - var3);
      }

      public final PathIterator getPathIterator(AffineTransform var1) {
         return (PathIterator)(var1 == null ? new Path2D.Double.CopyIterator(this) : new Path2D.Double.TxIterator(this, var1));
      }

      public final Object clone() {
         return new Path2D.Double(this);
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         super.writeObject(var1, true);
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         super.readObject(var1, true);
      }

      static class TxIterator extends Path2D.Iterator {
         double[] doubleCoords;
         AffineTransform affine;

         TxIterator(Path2D.Double var1, AffineTransform var2) {
            super(var1);
            this.doubleCoords = var1.doubleCoords;
            this.affine = var2;
         }

         public int currentSegment(float[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               this.affine.transform((double[])this.doubleCoords, this.pointIdx, (float[])var1, 0, var3 / 2);
            }

            return var2;
         }

         public int currentSegment(double[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               this.affine.transform((double[])this.doubleCoords, this.pointIdx, (double[])var1, 0, var3 / 2);
            }

            return var2;
         }
      }

      static class CopyIterator extends Path2D.Iterator {
         double[] doubleCoords;

         CopyIterator(Path2D.Double var1) {
            super(var1);
            this.doubleCoords = var1.doubleCoords;
         }

         public int currentSegment(float[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               for(int var4 = 0; var4 < var3; ++var4) {
                  var1[var4] = (float)this.doubleCoords[this.pointIdx + var4];
               }
            }

            return var2;
         }

         public int currentSegment(double[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               System.arraycopy(this.doubleCoords, this.pointIdx, var1, 0, var3);
            }

            return var2;
         }
      }
   }

   public static class Float extends Path2D implements Serializable {
      transient float[] floatCoords;
      private static final long serialVersionUID = 6990832515060788886L;

      public Float() {
         this(1, 20);
      }

      public Float(int var1) {
         this(var1, 20);
      }

      public Float(int var1, int var2) {
         super(var1, var2);
         this.floatCoords = new float[var2 * 2];
      }

      public Float(Shape var1) {
         this(var1, (AffineTransform)null);
      }

      public Float(Shape var1, AffineTransform var2) {
         if (var1 instanceof Path2D) {
            Path2D var3 = (Path2D)var1;
            this.setWindingRule(var3.windingRule);
            this.numTypes = var3.numTypes;
            this.pointTypes = Arrays.copyOf(var3.pointTypes, var3.numTypes);
            this.numCoords = var3.numCoords;
            this.floatCoords = var3.cloneCoordsFloat(var2);
         } else {
            PathIterator var4 = var1.getPathIterator(var2);
            this.setWindingRule(var4.getWindingRule());
            this.pointTypes = new byte[20];
            this.floatCoords = new float[40];
            this.append(var4, false);
         }

      }

      float[] cloneCoordsFloat(AffineTransform var1) {
         float[] var2;
         if (var1 == null) {
            var2 = Arrays.copyOf(this.floatCoords, this.numCoords);
         } else {
            var2 = new float[this.numCoords];
            var1.transform((float[])this.floatCoords, 0, (float[])var2, 0, this.numCoords / 2);
         }

         return var2;
      }

      double[] cloneCoordsDouble(AffineTransform var1) {
         double[] var2 = new double[this.numCoords];
         if (var1 == null) {
            for(int var3 = 0; var3 < this.numCoords; ++var3) {
               var2[var3] = (double)this.floatCoords[var3];
            }
         } else {
            var1.transform((float[])this.floatCoords, 0, (double[])var2, 0, this.numCoords / 2);
         }

         return var2;
      }

      void append(float var1, float var2) {
         this.floatCoords[this.numCoords++] = var1;
         this.floatCoords[this.numCoords++] = var2;
      }

      void append(double var1, double var3) {
         this.floatCoords[this.numCoords++] = (float)var1;
         this.floatCoords[this.numCoords++] = (float)var3;
      }

      Point2D getPoint(int var1) {
         return new Point2D.Float(this.floatCoords[var1], this.floatCoords[var1 + 1]);
      }

      void needRoom(boolean var1, int var2) {
         if (this.numTypes == 0 && var1) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
         } else {
            if (this.numTypes >= this.pointTypes.length) {
               this.pointTypes = expandPointTypes(this.pointTypes, 1);
            }

            if (this.numCoords > this.floatCoords.length - var2) {
               this.floatCoords = expandCoords(this.floatCoords, var2);
            }

         }
      }

      static float[] expandCoords(float[] var0, int var1) {
         int var2 = var0.length;
         int var3 = var2 + var1;
         if (var3 < var2) {
            throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !");
         } else {
            int var4 = var2;
            if (var2 > 1000) {
               var4 = Math.max(1000, var2 >> 3);
            } else if (var2 < 10) {
               var4 = 10;
            }

            assert var4 > var1;

            int var5 = var2 + var4;
            if (var5 < var3) {
               var5 = Integer.MAX_VALUE;
            }

            while(true) {
               try {
                  return Arrays.copyOf(var0, var5);
               } catch (OutOfMemoryError var7) {
                  if (var5 == var3) {
                     throw var7;
                  }

                  var5 = var3 + (var5 - var3) / 2;
               }
            }
         }
      }

      public final synchronized void moveTo(double var1, double var3) {
         if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
            this.floatCoords[this.numCoords - 2] = (float)var1;
            this.floatCoords[this.numCoords - 1] = (float)var3;
         } else {
            this.needRoom(false, 2);
            this.pointTypes[this.numTypes++] = 0;
            this.floatCoords[this.numCoords++] = (float)var1;
            this.floatCoords[this.numCoords++] = (float)var3;
         }

      }

      public final synchronized void moveTo(float var1, float var2) {
         if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
            this.floatCoords[this.numCoords - 2] = var1;
            this.floatCoords[this.numCoords - 1] = var2;
         } else {
            this.needRoom(false, 2);
            this.pointTypes[this.numTypes++] = 0;
            this.floatCoords[this.numCoords++] = var1;
            this.floatCoords[this.numCoords++] = var2;
         }

      }

      public final synchronized void lineTo(double var1, double var3) {
         this.needRoom(true, 2);
         this.pointTypes[this.numTypes++] = 1;
         this.floatCoords[this.numCoords++] = (float)var1;
         this.floatCoords[this.numCoords++] = (float)var3;
      }

      public final synchronized void lineTo(float var1, float var2) {
         this.needRoom(true, 2);
         this.pointTypes[this.numTypes++] = 1;
         this.floatCoords[this.numCoords++] = var1;
         this.floatCoords[this.numCoords++] = var2;
      }

      public final synchronized void quadTo(double var1, double var3, double var5, double var7) {
         this.needRoom(true, 4);
         this.pointTypes[this.numTypes++] = 2;
         this.floatCoords[this.numCoords++] = (float)var1;
         this.floatCoords[this.numCoords++] = (float)var3;
         this.floatCoords[this.numCoords++] = (float)var5;
         this.floatCoords[this.numCoords++] = (float)var7;
      }

      public final synchronized void quadTo(float var1, float var2, float var3, float var4) {
         this.needRoom(true, 4);
         this.pointTypes[this.numTypes++] = 2;
         this.floatCoords[this.numCoords++] = var1;
         this.floatCoords[this.numCoords++] = var2;
         this.floatCoords[this.numCoords++] = var3;
         this.floatCoords[this.numCoords++] = var4;
      }

      public final synchronized void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.needRoom(true, 6);
         this.pointTypes[this.numTypes++] = 3;
         this.floatCoords[this.numCoords++] = (float)var1;
         this.floatCoords[this.numCoords++] = (float)var3;
         this.floatCoords[this.numCoords++] = (float)var5;
         this.floatCoords[this.numCoords++] = (float)var7;
         this.floatCoords[this.numCoords++] = (float)var9;
         this.floatCoords[this.numCoords++] = (float)var11;
      }

      public final synchronized void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.needRoom(true, 6);
         this.pointTypes[this.numTypes++] = 3;
         this.floatCoords[this.numCoords++] = var1;
         this.floatCoords[this.numCoords++] = var2;
         this.floatCoords[this.numCoords++] = var3;
         this.floatCoords[this.numCoords++] = var4;
         this.floatCoords[this.numCoords++] = var5;
         this.floatCoords[this.numCoords++] = var6;
      }

      int pointCrossings(double var1, double var3) {
         if (this.numTypes == 0) {
            return 0;
         } else {
            float[] var17 = this.floatCoords;
            double var5;
            double var9 = var5 = (double)var17[0];
            double var7;
            double var11 = var7 = (double)var17[1];
            int var18 = 0;
            int var19 = 2;

            for(int var20 = 1; var20 < this.numTypes; ++var20) {
               double var13;
               double var15;
               switch(this.pointTypes[var20]) {
               case 0:
                  if (var11 != var7) {
                     var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var5, var7);
                  }

                  var5 = var9 = (double)var17[var19++];
                  var7 = var11 = (double)var17[var19++];
                  break;
               case 1:
                  var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var13 = (double)var17[var19++], var15 = (double)var17[var19++]);
                  var9 = var13;
                  var11 = var15;
                  break;
               case 2:
                  var18 += Curve.pointCrossingsForQuad(var1, var3, var9, var11, (double)var17[var19++], (double)var17[var19++], var13 = (double)var17[var19++], var15 = (double)var17[var19++], 0);
                  var9 = var13;
                  var11 = var15;
                  break;
               case 3:
                  var18 += Curve.pointCrossingsForCubic(var1, var3, var9, var11, (double)var17[var19++], (double)var17[var19++], (double)var17[var19++], (double)var17[var19++], var13 = (double)var17[var19++], var15 = (double)var17[var19++], 0);
                  var9 = var13;
                  var11 = var15;
                  break;
               case 4:
                  if (var11 != var7) {
                     var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var5, var7);
                  }

                  var9 = var5;
                  var11 = var7;
               }
            }

            if (var11 != var7) {
               var18 += Curve.pointCrossingsForLine(var1, var3, var9, var11, var5, var7);
            }

            return var18;
         }
      }

      int rectCrossings(double var1, double var3, double var5, double var7) {
         if (this.numTypes == 0) {
            return 0;
         } else {
            float[] var9 = this.floatCoords;
            double var14;
            double var10 = var14 = (double)var9[0];
            double var16;
            double var12 = var16 = (double)var9[1];
            int var22 = 0;
            int var23 = 2;

            for(int var24 = 1; var22 != Integer.MIN_VALUE && var24 < this.numTypes; ++var24) {
               double var18;
               double var20;
               switch(this.pointTypes[var24]) {
               case 0:
                  if (var10 != var14 || var12 != var16) {
                     var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
                  }

                  var14 = var10 = (double)var9[var23++];
                  var16 = var12 = (double)var9[var23++];
                  break;
               case 1:
                  var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var18 = (double)var9[var23++], var20 = (double)var9[var23++]);
                  var10 = var18;
                  var12 = var20;
                  break;
               case 2:
                  var22 = Curve.rectCrossingsForQuad(var22, var1, var3, var5, var7, var10, var12, (double)var9[var23++], (double)var9[var23++], var18 = (double)var9[var23++], var20 = (double)var9[var23++], 0);
                  var10 = var18;
                  var12 = var20;
                  break;
               case 3:
                  var22 = Curve.rectCrossingsForCubic(var22, var1, var3, var5, var7, var10, var12, (double)var9[var23++], (double)var9[var23++], (double)var9[var23++], (double)var9[var23++], var18 = (double)var9[var23++], var20 = (double)var9[var23++], 0);
                  var10 = var18;
                  var12 = var20;
                  break;
               case 4:
                  if (var10 != var14 || var12 != var16) {
                     var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
                  }

                  var10 = var14;
                  var12 = var16;
               }
            }

            if (var22 != Integer.MIN_VALUE && (var10 != var14 || var12 != var16)) {
               var22 = Curve.rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
            }

            return var22;
         }
      }

      public final void append(PathIterator var1, boolean var2) {
         for(float[] var3 = new float[6]; !var1.isDone(); var2 = false) {
            switch(var1.currentSegment(var3)) {
            case 0:
               if (var2 && this.numTypes >= 1 && this.numCoords >= 1) {
                  if (this.pointTypes[this.numTypes - 1] == 4 || this.floatCoords[this.numCoords - 2] != var3[0] || this.floatCoords[this.numCoords - 1] != var3[1]) {
                     this.lineTo(var3[0], var3[1]);
                  }
                  break;
               }

               this.moveTo(var3[0], var3[1]);
               break;
            case 1:
               this.lineTo(var3[0], var3[1]);
               break;
            case 2:
               this.quadTo(var3[0], var3[1], var3[2], var3[3]);
               break;
            case 3:
               this.curveTo(var3[0], var3[1], var3[2], var3[3], var3[4], var3[5]);
               break;
            case 4:
               this.closePath();
            }

            var1.next();
         }

      }

      public final void transform(AffineTransform var1) {
         var1.transform((float[])this.floatCoords, 0, (float[])this.floatCoords, 0, this.numCoords / 2);
      }

      public final synchronized Rectangle2D getBounds2D() {
         int var5 = this.numCoords;
         float var1;
         float var2;
         float var3;
         float var4;
         if (var5 > 0) {
            --var5;
            var2 = var4 = this.floatCoords[var5];
            --var5;
            var1 = var3 = this.floatCoords[var5];

            while(var5 > 0) {
               --var5;
               float var6 = this.floatCoords[var5];
               --var5;
               float var7 = this.floatCoords[var5];
               if (var7 < var1) {
                  var1 = var7;
               }

               if (var6 < var2) {
                  var2 = var6;
               }

               if (var7 > var3) {
                  var3 = var7;
               }

               if (var6 > var4) {
                  var4 = var6;
               }
            }
         } else {
            var4 = 0.0F;
            var3 = 0.0F;
            var2 = 0.0F;
            var1 = 0.0F;
         }

         return new Rectangle2D.Float(var1, var2, var3 - var1, var4 - var2);
      }

      public final PathIterator getPathIterator(AffineTransform var1) {
         return (PathIterator)(var1 == null ? new Path2D.Float.CopyIterator(this) : new Path2D.Float.TxIterator(this, var1));
      }

      public final Object clone() {
         return this instanceof GeneralPath ? new GeneralPath(this) : new Path2D.Float(this);
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         super.writeObject(var1, false);
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         super.readObject(var1, false);
      }

      static class TxIterator extends Path2D.Iterator {
         float[] floatCoords;
         AffineTransform affine;

         TxIterator(Path2D.Float var1, AffineTransform var2) {
            super(var1);
            this.floatCoords = var1.floatCoords;
            this.affine = var2;
         }

         public int currentSegment(float[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               this.affine.transform((float[])this.floatCoords, this.pointIdx, (float[])var1, 0, var3 / 2);
            }

            return var2;
         }

         public int currentSegment(double[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               this.affine.transform((float[])this.floatCoords, this.pointIdx, (double[])var1, 0, var3 / 2);
            }

            return var2;
         }
      }

      static class CopyIterator extends Path2D.Iterator {
         float[] floatCoords;

         CopyIterator(Path2D.Float var1) {
            super(var1);
            this.floatCoords = var1.floatCoords;
         }

         public int currentSegment(float[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               System.arraycopy(this.floatCoords, this.pointIdx, var1, 0, var3);
            }

            return var2;
         }

         public int currentSegment(double[] var1) {
            byte var2 = this.path.pointTypes[this.typeIdx];
            int var3 = curvecoords[var2];
            if (var3 > 0) {
               for(int var4 = 0; var4 < var3; ++var4) {
                  var1[var4] = (double)this.floatCoords[this.pointIdx + var4];
               }
            }

            return var2;
         }
      }
   }
}
