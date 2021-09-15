package java.awt;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import sun.java2d.pipe.RenderingEngine;

public class BasicStroke implements Stroke {
   public static final int JOIN_MITER = 0;
   public static final int JOIN_ROUND = 1;
   public static final int JOIN_BEVEL = 2;
   public static final int CAP_BUTT = 0;
   public static final int CAP_ROUND = 1;
   public static final int CAP_SQUARE = 2;
   float width;
   int join;
   int cap;
   float miterlimit;
   float[] dash;
   float dash_phase;

   @ConstructorProperties({"lineWidth", "endCap", "lineJoin", "miterLimit", "dashArray", "dashPhase"})
   public BasicStroke(float var1, int var2, int var3, float var4, float[] var5, float var6) {
      if (var1 < 0.0F) {
         throw new IllegalArgumentException("negative width");
      } else if (var2 != 0 && var2 != 1 && var2 != 2) {
         throw new IllegalArgumentException("illegal end cap value");
      } else {
         if (var3 == 0) {
            if (var4 < 1.0F) {
               throw new IllegalArgumentException("miter limit < 1");
            }
         } else if (var3 != 1 && var3 != 2) {
            throw new IllegalArgumentException("illegal line join value");
         }

         if (var5 != null) {
            if (var6 < 0.0F) {
               throw new IllegalArgumentException("negative dash phase");
            }

            boolean var7 = true;

            for(int var8 = 0; var8 < var5.length; ++var8) {
               float var9 = var5[var8];
               if ((double)var9 > 0.0D) {
                  var7 = false;
               } else if ((double)var9 < 0.0D) {
                  throw new IllegalArgumentException("negative dash length");
               }
            }

            if (var7) {
               throw new IllegalArgumentException("dash lengths all zero");
            }
         }

         this.width = var1;
         this.cap = var2;
         this.join = var3;
         this.miterlimit = var4;
         if (var5 != null) {
            this.dash = (float[])((float[])var5.clone());
         }

         this.dash_phase = var6;
      }
   }

   public BasicStroke(float var1, int var2, int var3, float var4) {
      this(var1, var2, var3, var4, (float[])null, 0.0F);
   }

   public BasicStroke(float var1, int var2, int var3) {
      this(var1, var2, var3, 10.0F, (float[])null, 0.0F);
   }

   public BasicStroke(float var1) {
      this(var1, 2, 0, 10.0F, (float[])null, 0.0F);
   }

   public BasicStroke() {
      this(1.0F, 2, 0, 10.0F, (float[])null, 0.0F);
   }

   public Shape createStrokedShape(Shape var1) {
      RenderingEngine var2 = RenderingEngine.getInstance();
      return var2.createStrokedShape(var1, this.width, this.cap, this.join, this.miterlimit, this.dash, this.dash_phase);
   }

   public float getLineWidth() {
      return this.width;
   }

   public int getEndCap() {
      return this.cap;
   }

   public int getLineJoin() {
      return this.join;
   }

   public float getMiterLimit() {
      return this.miterlimit;
   }

   public float[] getDashArray() {
      return this.dash == null ? null : (float[])((float[])this.dash.clone());
   }

   public float getDashPhase() {
      return this.dash_phase;
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.width);
      var1 = var1 * 31 + this.join;
      var1 = var1 * 31 + this.cap;
      var1 = var1 * 31 + Float.floatToIntBits(this.miterlimit);
      if (this.dash != null) {
         var1 = var1 * 31 + Float.floatToIntBits(this.dash_phase);

         for(int var2 = 0; var2 < this.dash.length; ++var2) {
            var1 = var1 * 31 + Float.floatToIntBits(this.dash[var2]);
         }
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof BasicStroke)) {
         return false;
      } else {
         BasicStroke var2 = (BasicStroke)var1;
         if (this.width != var2.width) {
            return false;
         } else if (this.join != var2.join) {
            return false;
         } else if (this.cap != var2.cap) {
            return false;
         } else if (this.miterlimit != var2.miterlimit) {
            return false;
         } else {
            if (this.dash != null) {
               if (this.dash_phase != var2.dash_phase) {
                  return false;
               }

               if (!Arrays.equals(this.dash, var2.dash)) {
                  return false;
               }
            } else if (var2.dash != null) {
               return false;
            }

            return true;
         }
      }
   }
}
