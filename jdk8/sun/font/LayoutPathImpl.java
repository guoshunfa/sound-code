package sun.font;

import java.awt.Shape;
import java.awt.font.LayoutPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;

public abstract class LayoutPathImpl extends LayoutPath {
   private static final boolean LOGMAP = false;
   private static final Formatter LOG;

   public Point2D pointToPath(double var1, double var3) {
      Point2D.Double var5 = new Point2D.Double(var1, var3);
      this.pointToPath(var5, var5);
      return var5;
   }

   public Point2D pathToPoint(double var1, double var3, boolean var5) {
      Point2D.Double var6 = new Point2D.Double(var1, var3);
      this.pathToPoint(var6, var5, var6);
      return var6;
   }

   public void pointToPath(double var1, double var3, Point2D var5) {
      var5.setLocation(var1, var3);
      this.pointToPath(var5, var5);
   }

   public void pathToPoint(double var1, double var3, boolean var5, Point2D var6) {
      var6.setLocation(var1, var3);
      this.pathToPoint(var6, var5, var6);
   }

   public abstract double start();

   public abstract double end();

   public abstract double length();

   public abstract Shape mapShape(Shape var1);

   public static LayoutPathImpl getPath(LayoutPathImpl.EndType var0, double... var1) {
      if ((var1.length & 1) != 0) {
         throw new IllegalArgumentException("odd number of points not allowed");
      } else {
         return LayoutPathImpl.SegmentPath.get(var0, var1);
      }
   }

   static {
      LOG = new Formatter(System.out);
   }

   public static class EmptyPath extends LayoutPathImpl {
      private AffineTransform tx;

      public EmptyPath(AffineTransform var1) {
         this.tx = var1;
      }

      public void pathToPoint(Point2D var1, boolean var2, Point2D var3) {
         if (this.tx != null) {
            this.tx.transform(var1, var3);
         } else {
            var3.setLocation(var1);
         }

      }

      public boolean pointToPath(Point2D var1, Point2D var2) {
         var2.setLocation(var1);
         if (this.tx != null) {
            try {
               this.tx.inverseTransform(var1, var2);
            } catch (NoninvertibleTransformException var4) {
            }
         }

         return var2.getX() > 0.0D;
      }

      public double start() {
         return 0.0D;
      }

      public double end() {
         return 0.0D;
      }

      public double length() {
         return 0.0D;
      }

      public Shape mapShape(Shape var1) {
         return this.tx != null ? this.tx.createTransformedShape(var1) : var1;
      }
   }

   public static final class SegmentPath extends LayoutPathImpl {
      private double[] data;
      LayoutPathImpl.EndType etype;

      public static LayoutPathImpl.SegmentPath get(LayoutPathImpl.EndType var0, double... var1) {
         return (new LayoutPathImpl.SegmentPathBuilder()).build(var0, var1);
      }

      SegmentPath(double[] var1, LayoutPathImpl.EndType var2) {
         this.data = var1;
         this.etype = var2;
      }

      public void pathToPoint(Point2D var1, boolean var2, Point2D var3) {
         this.locateAndGetIndex(var1, var2, var3);
      }

      public boolean pointToPath(Point2D var1, Point2D var2) {
         double var3 = var1.getX();
         double var5 = var1.getY();
         double var7 = this.data[0];
         double var9 = this.data[1];
         double var11 = this.data[2];
         double var13 = Double.MAX_VALUE;
         double var15 = 0.0D;
         double var17 = 0.0D;
         double var19 = 0.0D;
         int var21 = 0;

         for(int var22 = 3; var22 < this.data.length; var22 += 3) {
            double var23;
            double var25;
            double var27;
            label120: {
               var23 = this.data[var22];
               var25 = this.data[var22 + 1];
               var27 = this.data[var22 + 2];
               double var29 = var23 - var7;
               double var31 = var25 - var9;
               double var33 = var27 - var11;
               double var35 = var3 - var7;
               double var37 = var5 - var9;
               double var39 = var29 * var35 + var31 * var37;
               double var41;
               double var43;
               double var45;
               int var47;
               double var48;
               double var50;
               if (var33 != 0.0D && (var39 >= 0.0D || this.etype.isExtended() && var22 == 3)) {
                  var48 = var33 * var33;
                  if (var39 <= var48 || this.etype.isExtended() && var22 == this.data.length - 3) {
                     var50 = var39 / var48;
                     var41 = var7 + var50 * var29;
                     var43 = var9 + var50 * var31;
                     var45 = var11 + var50 * var33;
                     var47 = var22;
                  } else {
                     if (var22 != this.data.length - 3) {
                        break label120;
                     }

                     var41 = var23;
                     var43 = var25;
                     var45 = var27;
                     var47 = this.data.length;
                  }
               } else {
                  var41 = var7;
                  var43 = var9;
                  var45 = var11;
                  var47 = var22;
               }

               var48 = var3 - var41;
               var50 = var5 - var43;
               double var52 = var48 * var48 + var50 * var50;
               if (var52 <= var13) {
                  var13 = var52;
                  var15 = var41;
                  var17 = var43;
                  var19 = var45;
                  var21 = var47;
               }
            }

            var7 = var23;
            var9 = var25;
            var11 = var27;
         }

         var7 = this.data[var21 - 3];
         var9 = this.data[var21 - 2];
         if (var15 == var7 && var17 == var9) {
            boolean var55 = var21 != 3 && this.data[var21 - 1] != this.data[var21 - 4];
            boolean var56 = var21 != this.data.length && this.data[var21 - 1] != this.data[var21 + 2];
            boolean var57 = this.etype.isExtended() && (var21 == 3 || var21 == this.data.length);
            if (var55 && var56) {
               Point2D.Double var58 = new Point2D.Double(var3, var5);
               this.calcoffset(var21 - 3, var57, var58);
               Point2D.Double var59 = new Point2D.Double(var3, var5);
               this.calcoffset(var21, var57, var59);
               if (Math.abs(var58.y) > Math.abs(var59.y)) {
                  var2.setLocation(var58);
                  return true;
               } else {
                  var2.setLocation(var59);
                  return false;
               }
            } else if (var55) {
               var2.setLocation(var3, var5);
               this.calcoffset(var21 - 3, var57, var2);
               return true;
            } else {
               var2.setLocation(var3, var5);
               this.calcoffset(var21, var57, var2);
               return false;
            }
         } else {
            double var54 = this.data[var21];
            double var24 = this.data[var21 + 1];
            double var26 = Math.sqrt(var13);
            if ((var3 - var15) * (var24 - var9) > (var5 - var17) * (var54 - var7)) {
               var26 = -var26;
            }

            var2.setLocation(var19, var26);
            return false;
         }
      }

      private void calcoffset(int var1, boolean var2, Point2D var3) {
         double var4 = this.data[var1 - 3];
         double var6 = this.data[var1 - 2];
         double var8 = var3.getX() - var4;
         double var10 = var3.getY() - var6;
         double var12 = this.data[var1] - var4;
         double var14 = this.data[var1 + 1] - var6;
         double var16 = this.data[var1 + 2] - this.data[var1 - 1];
         double var18 = (var8 * var12 + var10 * var14) / var16;
         double var20 = (var8 * -var14 + var10 * var12) / var16;
         if (!var2) {
            if (var18 < 0.0D) {
               var18 = 0.0D;
            } else if (var18 > var16) {
               var18 = var16;
            }
         }

         var18 += this.data[var1 - 1];
         var3.setLocation(var18, var20);
      }

      public Shape mapShape(Shape var1) {
         return (new LayoutPathImpl.SegmentPath.Mapper()).mapShape(var1);
      }

      public double start() {
         return this.data[2];
      }

      public double end() {
         return this.data[this.data.length - 1];
      }

      public double length() {
         return this.data[this.data.length - 1] - this.data[2];
      }

      private double getClosedAdvance(double var1, boolean var3) {
         if (this.etype.isClosed()) {
            var1 -= this.data[2];
            int var4 = (int)(var1 / this.length());
            var1 -= (double)var4 * this.length();
            if (var1 < 0.0D || var1 == 0.0D && var3) {
               var1 += this.length();
            }

            var1 += this.data[2];
         }

         return var1;
      }

      private int getSegmentIndexForAdvance(double var1, boolean var3) {
         var1 = this.getClosedAdvance(var1, var3);
         int var4 = 5;

         for(int var5 = this.data.length - 1; var4 < var5; var4 += 3) {
            double var6 = this.data[var4];
            if (var1 < var6 || var1 == var6 && var3) {
               break;
            }
         }

         return var4 - 2;
      }

      private void map(int var1, double var2, double var4, Point2D var6) {
         double var7 = this.data[var1] - this.data[var1 - 3];
         double var9 = this.data[var1 + 1] - this.data[var1 - 2];
         double var11 = this.data[var1 + 2] - this.data[var1 - 1];
         double var13 = var7 / var11;
         double var15 = var9 / var11;
         var2 -= this.data[var1 - 1];
         var6.setLocation(this.data[var1 - 3] + var2 * var13 - var4 * var15, this.data[var1 - 2] + var2 * var15 + var4 * var13);
      }

      private int locateAndGetIndex(Point2D var1, boolean var2, Point2D var3) {
         double var4 = var1.getX();
         double var6 = var1.getY();
         int var8 = this.getSegmentIndexForAdvance(var4, var2);
         this.map(var8, var4, var6, var3);
         return var8;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("{");
         var1.append(this.etype.toString());
         var1.append(" ");

         for(int var2 = 0; var2 < this.data.length; var2 += 3) {
            if (var2 > 0) {
               var1.append(",");
            }

            float var3 = (float)((int)(this.data[var2] * 100.0D)) / 100.0F;
            float var4 = (float)((int)(this.data[var2 + 1] * 100.0D)) / 100.0F;
            float var5 = (float)((int)(this.data[var2 + 2] * 10.0D)) / 10.0F;
            var1.append("{");
            var1.append(var3);
            var1.append(",");
            var1.append(var4);
            var1.append(",");
            var1.append(var5);
            var1.append("}");
         }

         var1.append("}");
         return var1.toString();
      }

      class Mapper {
         final LayoutPathImpl.SegmentPath.LineInfo li = SegmentPath.this.new LineInfo();
         final ArrayList<LayoutPathImpl.SegmentPath.Segment> segments = new ArrayList();
         final Point2D.Double mpt;
         final Point2D.Double cpt;
         boolean haveMT;

         Mapper() {
            for(int var2 = 3; var2 < SegmentPath.this.data.length; var2 += 3) {
               if (SegmentPath.this.data[var2 + 2] != SegmentPath.this.data[var2 - 1]) {
                  this.segments.add(SegmentPath.this.new Segment(var2));
               }
            }

            this.mpt = new Point2D.Double();
            this.cpt = new Point2D.Double();
         }

         void init() {
            this.haveMT = false;
            Iterator var1 = this.segments.iterator();

            while(var1.hasNext()) {
               LayoutPathImpl.SegmentPath.Segment var2 = (LayoutPathImpl.SegmentPath.Segment)var1.next();
               var2.init();
            }

         }

         void moveTo(double var1, double var3) {
            this.mpt.x = var1;
            this.mpt.y = var3;
            this.haveMT = true;
         }

         void lineTo(double var1, double var3) {
            if (this.haveMT) {
               this.cpt.x = this.mpt.x;
               this.cpt.y = this.mpt.y;
            }

            if (var1 != this.cpt.x || var3 != this.cpt.y) {
               Iterator var5;
               LayoutPathImpl.SegmentPath.Segment var6;
               if (this.haveMT) {
                  this.haveMT = false;
                  var5 = this.segments.iterator();

                  while(var5.hasNext()) {
                     var6 = (LayoutPathImpl.SegmentPath.Segment)var5.next();
                     var6.move();
                  }
               }

               this.li.set(this.cpt.x, this.cpt.y, var1, var3);
               var5 = this.segments.iterator();

               while(var5.hasNext()) {
                  var6 = (LayoutPathImpl.SegmentPath.Segment)var5.next();
                  var6.line(this.li);
               }

               this.cpt.x = var1;
               this.cpt.y = var3;
            }
         }

         void close() {
            this.lineTo(this.mpt.x, this.mpt.y);
            Iterator var1 = this.segments.iterator();

            while(var1.hasNext()) {
               LayoutPathImpl.SegmentPath.Segment var2 = (LayoutPathImpl.SegmentPath.Segment)var1.next();
               var2.close();
            }

         }

         public Shape mapShape(Shape var1) {
            PathIterator var2 = var1.getPathIterator((AffineTransform)null, 1.0D);
            this.init();

            for(double[] var3 = new double[2]; !var2.isDone(); var2.next()) {
               switch(var2.currentSegment(var3)) {
               case 0:
                  this.moveTo(var3[0], var3[1]);
                  break;
               case 1:
                  this.lineTo(var3[0], var3[1]);
               case 2:
               case 3:
               default:
                  break;
               case 4:
                  this.close();
               }
            }

            GeneralPath var4 = new GeneralPath();
            Iterator var5 = this.segments.iterator();

            while(var5.hasNext()) {
               LayoutPathImpl.SegmentPath.Segment var6 = (LayoutPathImpl.SegmentPath.Segment)var5.next();
               var4.append(var6.gp, false);
            }

            return var4;
         }
      }

      class Segment {
         final int ix;
         final double ux;
         final double uy;
         final LayoutPathImpl.SegmentPath.LineInfo temp;
         boolean broken;
         double cx;
         double cy;
         GeneralPath gp;

         Segment(int var2) {
            this.ix = var2;
            double var3 = SegmentPath.this.data[var2 + 2] - SegmentPath.this.data[var2 - 1];
            this.ux = (SegmentPath.this.data[var2] - SegmentPath.this.data[var2 - 3]) / var3;
            this.uy = (SegmentPath.this.data[var2 + 1] - SegmentPath.this.data[var2 - 2]) / var3;
            this.temp = SegmentPath.this.new LineInfo();
         }

         void init() {
            this.broken = true;
            this.cx = this.cy = Double.MIN_VALUE;
            this.gp = new GeneralPath();
         }

         void move() {
            this.broken = true;
         }

         void close() {
            if (!this.broken) {
               this.gp.closePath();
            }

         }

         void line(LayoutPathImpl.SegmentPath.LineInfo var1) {
            if (var1.pin(this.ix, this.temp)) {
               LayoutPathImpl.SegmentPath.LineInfo var10000 = this.temp;
               var10000.sx -= SegmentPath.this.data[this.ix - 1];
               double var2 = SegmentPath.this.data[this.ix - 3] + this.temp.sx * this.ux - this.temp.sy * this.uy;
               double var4 = SegmentPath.this.data[this.ix - 2] + this.temp.sx * this.uy + this.temp.sy * this.ux;
               var10000 = this.temp;
               var10000.lx -= SegmentPath.this.data[this.ix - 1];
               double var6 = SegmentPath.this.data[this.ix - 3] + this.temp.lx * this.ux - this.temp.ly * this.uy;
               double var8 = SegmentPath.this.data[this.ix - 2] + this.temp.lx * this.uy + this.temp.ly * this.ux;
               if (var2 != this.cx || var4 != this.cy) {
                  if (this.broken) {
                     this.gp.moveTo((float)var2, (float)var4);
                  } else {
                     this.gp.lineTo((float)var2, (float)var4);
                  }
               }

               this.gp.lineTo((float)var6, (float)var8);
               this.broken = false;
               this.cx = var6;
               this.cy = var8;
            }

         }
      }

      class LineInfo {
         double sx;
         double sy;
         double lx;
         double ly;
         double m;

         void set(double var1, double var3, double var5, double var7) {
            this.sx = var1;
            this.sy = var3;
            this.lx = var5;
            this.ly = var7;
            double var9 = var5 - var1;
            if (var9 == 0.0D) {
               this.m = 0.0D;
            } else {
               double var11 = var7 - var3;
               this.m = var11 / var9;
            }

         }

         void set(LayoutPathImpl.SegmentPath.LineInfo var1) {
            this.sx = var1.sx;
            this.sy = var1.sy;
            this.lx = var1.lx;
            this.ly = var1.ly;
            this.m = var1.m;
         }

         boolean pin(double var1, double var3, LayoutPathImpl.SegmentPath.LineInfo var5) {
            var5.set(this);
            if (this.lx >= this.sx) {
               if (this.sx < var3 && this.lx >= var1) {
                  if (this.sx < var1) {
                     if (this.m != 0.0D) {
                        var5.sy = this.sy + this.m * (var1 - this.sx);
                     }

                     var5.sx = var1;
                  }

                  if (this.lx > var3) {
                     if (this.m != 0.0D) {
                        var5.ly = this.ly + this.m * (var3 - this.lx);
                     }

                     var5.lx = var3;
                  }

                  return true;
               }
            } else if (this.lx < var3 && this.sx >= var1) {
               if (this.lx < var1) {
                  if (this.m != 0.0D) {
                     var5.ly = this.ly + this.m * (var1 - this.lx);
                  }

                  var5.lx = var1;
               }

               if (this.sx > var3) {
                  if (this.m != 0.0D) {
                     var5.sy = this.sy + this.m * (var3 - this.sx);
                  }

                  var5.sx = var3;
               }

               return true;
            }

            return false;
         }

         boolean pin(int var1, LayoutPathImpl.SegmentPath.LineInfo var2) {
            double var3 = SegmentPath.this.data[var1 - 1];
            double var5 = SegmentPath.this.data[var1 + 2];
            switch(SegmentPath.this.etype) {
            case EXTENDED:
               if (var1 == 3) {
                  var3 = Double.NEGATIVE_INFINITY;
               }

               if (var1 == SegmentPath.this.data.length - 3) {
                  var5 = Double.POSITIVE_INFINITY;
               }
            case PINNED:
            case CLOSED:
            default:
               return this.pin(var3, var5, var2);
            }
         }
      }
   }

   public static final class SegmentPathBuilder {
      private double[] data;
      private int w;
      private double px;
      private double py;
      private double a;
      private boolean pconnect;

      public void reset(int var1) {
         if (this.data != null && var1 <= this.data.length) {
            if (var1 == 0) {
               this.data = null;
            }
         } else {
            this.data = new double[var1];
         }

         this.w = 0;
         this.px = this.py = 0.0D;
         this.pconnect = false;
      }

      public LayoutPathImpl.SegmentPath build(LayoutPathImpl.EndType var1, double... var2) {
         assert var2.length % 2 == 0;

         this.reset(var2.length / 2 * 3);

         for(int var3 = 0; var3 < var2.length; var3 += 2) {
            this.nextPoint(var2[var3], var2[var3 + 1], var3 != 0);
         }

         return this.complete(var1);
      }

      public void moveTo(double var1, double var3) {
         this.nextPoint(var1, var3, false);
      }

      public void lineTo(double var1, double var3) {
         this.nextPoint(var1, var3, true);
      }

      private void nextPoint(double var1, double var3, boolean var5) {
         if (var1 != this.px || var3 != this.py) {
            if (this.w == 0) {
               if (this.data == null) {
                  this.data = new double[6];
               }

               if (var5) {
                  this.w = 3;
               }
            }

            if (this.w != 0 && !var5 && !this.pconnect) {
               this.data[this.w - 3] = this.px = var1;
               this.data[this.w - 2] = this.py = var3;
            } else {
               if (this.w == this.data.length) {
                  double[] var6 = new double[this.w * 2];
                  System.arraycopy(this.data, 0, var6, 0, this.w);
                  this.data = var6;
               }

               if (var5) {
                  double var10 = var1 - this.px;
                  double var8 = var3 - this.py;
                  this.a += Math.sqrt(var10 * var10 + var8 * var8);
               }

               this.data[this.w++] = var1;
               this.data[this.w++] = var3;
               this.data[this.w++] = this.a;
               this.px = var1;
               this.py = var3;
               this.pconnect = var5;
            }
         }
      }

      public LayoutPathImpl.SegmentPath complete() {
         return this.complete(LayoutPathImpl.EndType.EXTENDED);
      }

      public LayoutPathImpl.SegmentPath complete(LayoutPathImpl.EndType var1) {
         if (this.data != null && this.w >= 6) {
            LayoutPathImpl.SegmentPath var2;
            if (this.w == this.data.length) {
               var2 = new LayoutPathImpl.SegmentPath(this.data, var1);
               this.reset(0);
            } else {
               double[] var3 = new double[this.w];
               System.arraycopy(this.data, 0, var3, 0, this.w);
               var2 = new LayoutPathImpl.SegmentPath(var3, var1);
               this.reset(2);
            }

            return var2;
         } else {
            return null;
         }
      }
   }

   public static enum EndType {
      PINNED,
      EXTENDED,
      CLOSED;

      public boolean isPinned() {
         return this == PINNED;
      }

      public boolean isExtended() {
         return this == EXTENDED;
      }

      public boolean isClosed() {
         return this == CLOSED;
      }
   }
}
