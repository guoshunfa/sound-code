package java.awt.geom;

import java.awt.Shape;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AffineTransform implements Cloneable, Serializable {
   private static final int TYPE_UNKNOWN = -1;
   public static final int TYPE_IDENTITY = 0;
   public static final int TYPE_TRANSLATION = 1;
   public static final int TYPE_UNIFORM_SCALE = 2;
   public static final int TYPE_GENERAL_SCALE = 4;
   public static final int TYPE_MASK_SCALE = 6;
   public static final int TYPE_FLIP = 64;
   public static final int TYPE_QUADRANT_ROTATION = 8;
   public static final int TYPE_GENERAL_ROTATION = 16;
   public static final int TYPE_MASK_ROTATION = 24;
   public static final int TYPE_GENERAL_TRANSFORM = 32;
   static final int APPLY_IDENTITY = 0;
   static final int APPLY_TRANSLATE = 1;
   static final int APPLY_SCALE = 2;
   static final int APPLY_SHEAR = 4;
   private static final int HI_SHIFT = 3;
   private static final int HI_IDENTITY = 0;
   private static final int HI_TRANSLATE = 8;
   private static final int HI_SCALE = 16;
   private static final int HI_SHEAR = 32;
   double m00;
   double m10;
   double m01;
   double m11;
   double m02;
   double m12;
   transient int state;
   private transient int type;
   private static final int[] rot90conversion = new int[]{4, 5, 4, 5, 2, 3, 6, 7};
   private static final long serialVersionUID = 1330973210523860834L;

   private AffineTransform(double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
      this.m00 = var1;
      this.m10 = var3;
      this.m01 = var5;
      this.m11 = var7;
      this.m02 = var9;
      this.m12 = var11;
      this.state = var13;
      this.type = -1;
   }

   public AffineTransform() {
      this.m00 = this.m11 = 1.0D;
   }

   public AffineTransform(AffineTransform var1) {
      this.m00 = var1.m00;
      this.m10 = var1.m10;
      this.m01 = var1.m01;
      this.m11 = var1.m11;
      this.m02 = var1.m02;
      this.m12 = var1.m12;
      this.state = var1.state;
      this.type = var1.type;
   }

   @ConstructorProperties({"scaleX", "shearY", "shearX", "scaleY", "translateX", "translateY"})
   public AffineTransform(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.m00 = (double)var1;
      this.m10 = (double)var2;
      this.m01 = (double)var3;
      this.m11 = (double)var4;
      this.m02 = (double)var5;
      this.m12 = (double)var6;
      this.updateState();
   }

   public AffineTransform(float[] var1) {
      this.m00 = (double)var1[0];
      this.m10 = (double)var1[1];
      this.m01 = (double)var1[2];
      this.m11 = (double)var1[3];
      if (var1.length > 5) {
         this.m02 = (double)var1[4];
         this.m12 = (double)var1[5];
      }

      this.updateState();
   }

   public AffineTransform(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.m00 = var1;
      this.m10 = var3;
      this.m01 = var5;
      this.m11 = var7;
      this.m02 = var9;
      this.m12 = var11;
      this.updateState();
   }

   public AffineTransform(double[] var1) {
      this.m00 = var1[0];
      this.m10 = var1[1];
      this.m01 = var1[2];
      this.m11 = var1[3];
      if (var1.length > 5) {
         this.m02 = var1[4];
         this.m12 = var1[5];
      }

      this.updateState();
   }

   public static AffineTransform getTranslateInstance(double var0, double var2) {
      AffineTransform var4 = new AffineTransform();
      var4.setToTranslation(var0, var2);
      return var4;
   }

   public static AffineTransform getRotateInstance(double var0) {
      AffineTransform var2 = new AffineTransform();
      var2.setToRotation(var0);
      return var2;
   }

   public static AffineTransform getRotateInstance(double var0, double var2, double var4) {
      AffineTransform var6 = new AffineTransform();
      var6.setToRotation(var0, var2, var4);
      return var6;
   }

   public static AffineTransform getRotateInstance(double var0, double var2) {
      AffineTransform var4 = new AffineTransform();
      var4.setToRotation(var0, var2);
      return var4;
   }

   public static AffineTransform getRotateInstance(double var0, double var2, double var4, double var6) {
      AffineTransform var8 = new AffineTransform();
      var8.setToRotation(var0, var2, var4, var6);
      return var8;
   }

   public static AffineTransform getQuadrantRotateInstance(int var0) {
      AffineTransform var1 = new AffineTransform();
      var1.setToQuadrantRotation(var0);
      return var1;
   }

   public static AffineTransform getQuadrantRotateInstance(int var0, double var1, double var3) {
      AffineTransform var5 = new AffineTransform();
      var5.setToQuadrantRotation(var0, var1, var3);
      return var5;
   }

   public static AffineTransform getScaleInstance(double var0, double var2) {
      AffineTransform var4 = new AffineTransform();
      var4.setToScale(var0, var2);
      return var4;
   }

   public static AffineTransform getShearInstance(double var0, double var2) {
      AffineTransform var4 = new AffineTransform();
      var4.setToShear(var0, var2);
      return var4;
   }

   public int getType() {
      if (this.type == -1) {
         this.calculateType();
      }

      return this.type;
   }

   private void calculateType() {
      int var1 = 0;
      this.updateState();
      boolean var2;
      boolean var3;
      double var4;
      double var6;
      switch(this.state) {
      case 0:
         break;
      case 1:
         var1 = 1;
         break;
      case 3:
         var1 = 1;
      case 2:
         var2 = (var4 = this.m00) >= 0.0D;
         var3 = (var6 = this.m11) >= 0.0D;
         if (var2 == var3) {
            if (var2) {
               if (var4 == var6) {
                  var1 |= 2;
               } else {
                  var1 |= 4;
               }
            } else if (var4 != var6) {
               var1 |= 12;
            } else if (var4 != -1.0D) {
               var1 |= 10;
            } else {
               var1 |= 8;
            }
         } else if (var4 == -var6) {
            if (var4 != 1.0D && var4 != -1.0D) {
               var1 |= 66;
            } else {
               var1 |= 64;
            }
         } else {
            var1 |= 68;
         }
         break;
      case 5:
         var1 = 1;
      case 4:
         var2 = (var4 = this.m01) >= 0.0D;
         var3 = (var6 = this.m10) >= 0.0D;
         if (var2 != var3) {
            if (var4 != -var6) {
               var1 |= 12;
            } else if (var4 != 1.0D && var4 != -1.0D) {
               var1 |= 10;
            } else {
               var1 |= 8;
            }
         } else if (var4 == var6) {
            var1 |= 74;
         } else {
            var1 |= 76;
         }
         break;
      default:
         this.stateError();
      case 7:
         var1 = 1;
      case 6:
         double var8;
         double var10;
         if ((var4 = this.m00) * (var8 = this.m01) + (var10 = this.m10) * (var6 = this.m11) != 0.0D) {
            this.type = 32;
            return;
         }

         var2 = var4 >= 0.0D;
         var3 = var6 >= 0.0D;
         if (var2 == var3) {
            if (var4 == var6 && var8 == -var10) {
               if (var4 * var6 - var8 * var10 != 1.0D) {
                  var1 |= 18;
               } else {
                  var1 |= 16;
               }
            } else {
               var1 |= 20;
            }
         } else if (var4 == -var6 && var8 == var10) {
            if (var4 * var6 - var8 * var10 != 1.0D) {
               var1 |= 82;
            } else {
               var1 |= 80;
            }
         } else {
            var1 |= 84;
         }
      }

      this.type = var1;
   }

   public double getDeterminant() {
      switch(this.state) {
      case 0:
      case 1:
         return 1.0D;
      case 2:
      case 3:
         return this.m00 * this.m11;
      case 4:
      case 5:
         return -(this.m01 * this.m10);
      default:
         this.stateError();
      case 6:
      case 7:
         return this.m00 * this.m11 - this.m01 * this.m10;
      }
   }

   void updateState() {
      if (this.m01 == 0.0D && this.m10 == 0.0D) {
         if (this.m00 == 1.0D && this.m11 == 1.0D) {
            if (this.m02 == 0.0D && this.m12 == 0.0D) {
               this.state = 0;
               this.type = 0;
            } else {
               this.state = 1;
               this.type = 1;
            }
         } else if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 2;
            this.type = -1;
         } else {
            this.state = 3;
            this.type = -1;
         }
      } else if (this.m00 == 0.0D && this.m11 == 0.0D) {
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 4;
            this.type = -1;
         } else {
            this.state = 5;
            this.type = -1;
         }
      } else if (this.m02 == 0.0D && this.m12 == 0.0D) {
         this.state = 6;
         this.type = -1;
      } else {
         this.state = 7;
         this.type = -1;
      }

   }

   private void stateError() {
      throw new InternalError("missing case in transform state switch");
   }

   public void getMatrix(double[] var1) {
      var1[0] = this.m00;
      var1[1] = this.m10;
      var1[2] = this.m01;
      var1[3] = this.m11;
      if (var1.length > 5) {
         var1[4] = this.m02;
         var1[5] = this.m12;
      }

   }

   public double getScaleX() {
      return this.m00;
   }

   public double getScaleY() {
      return this.m11;
   }

   public double getShearX() {
      return this.m01;
   }

   public double getShearY() {
      return this.m10;
   }

   public double getTranslateX() {
      return this.m02;
   }

   public double getTranslateY() {
      return this.m12;
   }

   public void translate(double var1, double var3) {
      switch(this.state) {
      case 0:
         this.m02 = var1;
         this.m12 = var3;
         if (var1 != 0.0D || var3 != 0.0D) {
            this.state = 1;
            this.type = 1;
         }

         return;
      case 1:
         this.m02 += var1;
         this.m12 += var3;
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 0;
            this.type = 0;
         }

         return;
      case 2:
         this.m02 = var1 * this.m00;
         this.m12 = var3 * this.m11;
         if (this.m02 != 0.0D || this.m12 != 0.0D) {
            this.state = 3;
            this.type |= 1;
         }

         return;
      case 3:
         this.m02 += var1 * this.m00;
         this.m12 += var3 * this.m11;
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 2;
            if (this.type != -1) {
               --this.type;
            }
         }

         return;
      case 4:
         this.m02 = var3 * this.m01;
         this.m12 = var1 * this.m10;
         if (this.m02 != 0.0D || this.m12 != 0.0D) {
            this.state = 5;
            this.type |= 1;
         }

         return;
      case 5:
         this.m02 += var3 * this.m01;
         this.m12 += var1 * this.m10;
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 4;
            if (this.type != -1) {
               --this.type;
            }
         }

         return;
      case 6:
         this.m02 = var1 * this.m00 + var3 * this.m01;
         this.m12 = var1 * this.m10 + var3 * this.m11;
         if (this.m02 != 0.0D || this.m12 != 0.0D) {
            this.state = 7;
            this.type |= 1;
         }

         return;
      case 7:
         this.m02 += var1 * this.m00 + var3 * this.m01;
         this.m12 += var1 * this.m10 + var3 * this.m11;
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 6;
            if (this.type != -1) {
               --this.type;
            }
         }

         return;
      default:
         this.stateError();
      }
   }

   private final void rotate90() {
      double var1 = this.m00;
      this.m00 = this.m01;
      this.m01 = -var1;
      var1 = this.m10;
      this.m10 = this.m11;
      this.m11 = -var1;
      int var3 = rot90conversion[this.state];
      if ((var3 & 6) == 2 && this.m00 == 1.0D && this.m11 == 1.0D) {
         var3 -= 2;
      }

      this.state = var3;
      this.type = -1;
   }

   private final void rotate180() {
      this.m00 = -this.m00;
      this.m11 = -this.m11;
      int var1 = this.state;
      if ((var1 & 4) != 0) {
         this.m01 = -this.m01;
         this.m10 = -this.m10;
      } else if (this.m00 == 1.0D && this.m11 == 1.0D) {
         this.state = var1 & -3;
      } else {
         this.state = var1 | 2;
      }

      this.type = -1;
   }

   private final void rotate270() {
      double var1 = this.m00;
      this.m00 = -this.m01;
      this.m01 = var1;
      var1 = this.m10;
      this.m10 = -this.m11;
      this.m11 = var1;
      int var3 = rot90conversion[this.state];
      if ((var3 & 6) == 2 && this.m00 == 1.0D && this.m11 == 1.0D) {
         var3 -= 2;
      }

      this.state = var3;
      this.type = -1;
   }

   public void rotate(double var1) {
      double var3 = Math.sin(var1);
      if (var3 == 1.0D) {
         this.rotate90();
      } else if (var3 == -1.0D) {
         this.rotate270();
      } else {
         double var5 = Math.cos(var1);
         if (var5 == -1.0D) {
            this.rotate180();
         } else if (var5 != 1.0D) {
            double var7 = this.m00;
            double var9 = this.m01;
            this.m00 = var5 * var7 + var3 * var9;
            this.m01 = -var3 * var7 + var5 * var9;
            var7 = this.m10;
            var9 = this.m11;
            this.m10 = var5 * var7 + var3 * var9;
            this.m11 = -var3 * var7 + var5 * var9;
            this.updateState();
         }
      }

   }

   public void rotate(double var1, double var3, double var5) {
      this.translate(var3, var5);
      this.rotate(var1);
      this.translate(-var3, -var5);
   }

   public void rotate(double var1, double var3) {
      if (var3 == 0.0D) {
         if (var1 < 0.0D) {
            this.rotate180();
         }
      } else if (var1 == 0.0D) {
         if (var3 > 0.0D) {
            this.rotate90();
         } else {
            this.rotate270();
         }
      } else {
         double var5 = Math.sqrt(var1 * var1 + var3 * var3);
         double var7 = var3 / var5;
         double var9 = var1 / var5;
         double var11 = this.m00;
         double var13 = this.m01;
         this.m00 = var9 * var11 + var7 * var13;
         this.m01 = -var7 * var11 + var9 * var13;
         var11 = this.m10;
         var13 = this.m11;
         this.m10 = var9 * var11 + var7 * var13;
         this.m11 = -var7 * var11 + var9 * var13;
         this.updateState();
      }

   }

   public void rotate(double var1, double var3, double var5, double var7) {
      this.translate(var5, var7);
      this.rotate(var1, var3);
      this.translate(-var5, -var7);
   }

   public void quadrantRotate(int var1) {
      switch(var1 & 3) {
      case 0:
      default:
         break;
      case 1:
         this.rotate90();
         break;
      case 2:
         this.rotate180();
         break;
      case 3:
         this.rotate270();
      }

   }

   public void quadrantRotate(int var1, double var2, double var4) {
      switch(var1 & 3) {
      case 0:
         return;
      case 1:
         this.m02 += var2 * (this.m00 - this.m01) + var4 * (this.m01 + this.m00);
         this.m12 += var2 * (this.m10 - this.m11) + var4 * (this.m11 + this.m10);
         this.rotate90();
         break;
      case 2:
         this.m02 += var2 * (this.m00 + this.m00) + var4 * (this.m01 + this.m01);
         this.m12 += var2 * (this.m10 + this.m10) + var4 * (this.m11 + this.m11);
         this.rotate180();
         break;
      case 3:
         this.m02 += var2 * (this.m00 + this.m01) + var4 * (this.m01 - this.m00);
         this.m12 += var2 * (this.m10 + this.m11) + var4 * (this.m11 - this.m10);
         this.rotate270();
      }

      if (this.m02 == 0.0D && this.m12 == 0.0D) {
         this.state &= -2;
      } else {
         this.state |= 1;
      }

   }

   public void scale(double var1, double var3) {
      int var5 = this.state;
      switch(var5) {
      case 0:
      case 1:
         this.m00 = var1;
         this.m11 = var3;
         if (var1 != 1.0D || var3 != 1.0D) {
            this.state = var5 | 2;
            this.type = -1;
         }

         return;
      case 2:
      case 3:
         this.m00 *= var1;
         this.m11 *= var3;
         if (this.m00 == 1.0D && this.m11 == 1.0D) {
            this.state = var5 &= 1;
            this.type = var5 == 0 ? 0 : 1;
         } else {
            this.type = -1;
         }

         return;
      case 4:
      case 5:
         break;
      default:
         this.stateError();
      case 6:
      case 7:
         this.m00 *= var1;
         this.m11 *= var3;
      }

      this.m01 *= var3;
      this.m10 *= var1;
      if (this.m01 == 0.0D && this.m10 == 0.0D) {
         var5 &= 1;
         if (this.m00 == 1.0D && this.m11 == 1.0D) {
            this.type = var5 == 0 ? 0 : 1;
         } else {
            var5 |= 2;
            this.type = -1;
         }

         this.state = var5;
      }

   }

   public void shear(double var1, double var3) {
      int var5 = this.state;
      switch(var5) {
      case 0:
      case 1:
         this.m01 = var1;
         this.m10 = var3;
         if (this.m01 != 0.0D || this.m10 != 0.0D) {
            this.state = var5 | 2 | 4;
            this.type = -1;
         }

         return;
      case 2:
      case 3:
         this.m01 = this.m00 * var1;
         this.m10 = this.m11 * var3;
         if (this.m01 != 0.0D || this.m10 != 0.0D) {
            this.state = var5 | 4;
         }

         this.type = -1;
         return;
      case 4:
      case 5:
         this.m00 = this.m01 * var3;
         this.m11 = this.m10 * var1;
         if (this.m00 != 0.0D || this.m11 != 0.0D) {
            this.state = var5 | 2;
         }

         this.type = -1;
         return;
      case 6:
      case 7:
         double var6 = this.m00;
         double var8 = this.m01;
         this.m00 = var6 + var8 * var3;
         this.m01 = var6 * var1 + var8;
         var6 = this.m10;
         var8 = this.m11;
         this.m10 = var6 + var8 * var3;
         this.m11 = var6 * var1 + var8;
         this.updateState();
         return;
      default:
         this.stateError();
      }
   }

   public void setToIdentity() {
      this.m00 = this.m11 = 1.0D;
      this.m10 = this.m01 = this.m02 = this.m12 = 0.0D;
      this.state = 0;
      this.type = 0;
   }

   public void setToTranslation(double var1, double var3) {
      this.m00 = 1.0D;
      this.m10 = 0.0D;
      this.m01 = 0.0D;
      this.m11 = 1.0D;
      this.m02 = var1;
      this.m12 = var3;
      if (var1 == 0.0D && var3 == 0.0D) {
         this.state = 0;
         this.type = 0;
      } else {
         this.state = 1;
         this.type = 1;
      }

   }

   public void setToRotation(double var1) {
      double var3 = Math.sin(var1);
      double var5;
      if (var3 != 1.0D && var3 != -1.0D) {
         var5 = Math.cos(var1);
         if (var5 == -1.0D) {
            var3 = 0.0D;
            this.state = 2;
            this.type = 8;
         } else if (var5 == 1.0D) {
            var3 = 0.0D;
            this.state = 0;
            this.type = 0;
         } else {
            this.state = 6;
            this.type = 16;
         }
      } else {
         var5 = 0.0D;
         this.state = 4;
         this.type = 8;
      }

      this.m00 = var5;
      this.m10 = var3;
      this.m01 = -var3;
      this.m11 = var5;
      this.m02 = 0.0D;
      this.m12 = 0.0D;
   }

   public void setToRotation(double var1, double var3, double var5) {
      this.setToRotation(var1);
      double var7 = this.m10;
      double var9 = 1.0D - this.m00;
      this.m02 = var3 * var9 + var5 * var7;
      this.m12 = var5 * var9 - var3 * var7;
      if (this.m02 != 0.0D || this.m12 != 0.0D) {
         this.state |= 1;
         this.type |= 1;
      }

   }

   public void setToRotation(double var1, double var3) {
      double var5;
      double var7;
      if (var3 == 0.0D) {
         var5 = 0.0D;
         if (var1 < 0.0D) {
            var7 = -1.0D;
            this.state = 2;
            this.type = 8;
         } else {
            var7 = 1.0D;
            this.state = 0;
            this.type = 0;
         }
      } else if (var1 == 0.0D) {
         var7 = 0.0D;
         var5 = var3 > 0.0D ? 1.0D : -1.0D;
         this.state = 4;
         this.type = 8;
      } else {
         double var9 = Math.sqrt(var1 * var1 + var3 * var3);
         var7 = var1 / var9;
         var5 = var3 / var9;
         this.state = 6;
         this.type = 16;
      }

      this.m00 = var7;
      this.m10 = var5;
      this.m01 = -var5;
      this.m11 = var7;
      this.m02 = 0.0D;
      this.m12 = 0.0D;
   }

   public void setToRotation(double var1, double var3, double var5, double var7) {
      this.setToRotation(var1, var3);
      double var9 = this.m10;
      double var11 = 1.0D - this.m00;
      this.m02 = var5 * var11 + var7 * var9;
      this.m12 = var7 * var11 - var5 * var9;
      if (this.m02 != 0.0D || this.m12 != 0.0D) {
         this.state |= 1;
         this.type |= 1;
      }

   }

   public void setToQuadrantRotation(int var1) {
      switch(var1 & 3) {
      case 0:
         this.m00 = 1.0D;
         this.m10 = 0.0D;
         this.m01 = 0.0D;
         this.m11 = 1.0D;
         this.m02 = 0.0D;
         this.m12 = 0.0D;
         this.state = 0;
         this.type = 0;
         break;
      case 1:
         this.m00 = 0.0D;
         this.m10 = 1.0D;
         this.m01 = -1.0D;
         this.m11 = 0.0D;
         this.m02 = 0.0D;
         this.m12 = 0.0D;
         this.state = 4;
         this.type = 8;
         break;
      case 2:
         this.m00 = -1.0D;
         this.m10 = 0.0D;
         this.m01 = 0.0D;
         this.m11 = -1.0D;
         this.m02 = 0.0D;
         this.m12 = 0.0D;
         this.state = 2;
         this.type = 8;
         break;
      case 3:
         this.m00 = 0.0D;
         this.m10 = -1.0D;
         this.m01 = 1.0D;
         this.m11 = 0.0D;
         this.m02 = 0.0D;
         this.m12 = 0.0D;
         this.state = 4;
         this.type = 8;
      }

   }

   public void setToQuadrantRotation(int var1, double var2, double var4) {
      switch(var1 & 3) {
      case 0:
         this.m00 = 1.0D;
         this.m10 = 0.0D;
         this.m01 = 0.0D;
         this.m11 = 1.0D;
         this.m02 = 0.0D;
         this.m12 = 0.0D;
         this.state = 0;
         this.type = 0;
         break;
      case 1:
         this.m00 = 0.0D;
         this.m10 = 1.0D;
         this.m01 = -1.0D;
         this.m11 = 0.0D;
         this.m02 = var2 + var4;
         this.m12 = var4 - var2;
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 4;
            this.type = 8;
         } else {
            this.state = 5;
            this.type = 9;
         }
         break;
      case 2:
         this.m00 = -1.0D;
         this.m10 = 0.0D;
         this.m01 = 0.0D;
         this.m11 = -1.0D;
         this.m02 = var2 + var2;
         this.m12 = var4 + var4;
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 2;
            this.type = 8;
         } else {
            this.state = 3;
            this.type = 9;
         }
         break;
      case 3:
         this.m00 = 0.0D;
         this.m10 = -1.0D;
         this.m01 = 1.0D;
         this.m11 = 0.0D;
         this.m02 = var2 - var4;
         this.m12 = var4 + var2;
         if (this.m02 == 0.0D && this.m12 == 0.0D) {
            this.state = 4;
            this.type = 8;
         } else {
            this.state = 5;
            this.type = 9;
         }
      }

   }

   public void setToScale(double var1, double var3) {
      this.m00 = var1;
      this.m10 = 0.0D;
      this.m01 = 0.0D;
      this.m11 = var3;
      this.m02 = 0.0D;
      this.m12 = 0.0D;
      if (var1 == 1.0D && var3 == 1.0D) {
         this.state = 0;
         this.type = 0;
      } else {
         this.state = 2;
         this.type = -1;
      }

   }

   public void setToShear(double var1, double var3) {
      this.m00 = 1.0D;
      this.m01 = var1;
      this.m10 = var3;
      this.m11 = 1.0D;
      this.m02 = 0.0D;
      this.m12 = 0.0D;
      if (var1 == 0.0D && var3 == 0.0D) {
         this.state = 0;
         this.type = 0;
      } else {
         this.state = 6;
         this.type = -1;
      }

   }

   public void setTransform(AffineTransform var1) {
      this.m00 = var1.m00;
      this.m10 = var1.m10;
      this.m01 = var1.m01;
      this.m11 = var1.m11;
      this.m02 = var1.m02;
      this.m12 = var1.m12;
      this.state = var1.state;
      this.type = var1.type;
   }

   public void setTransform(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.m00 = var1;
      this.m10 = var3;
      this.m01 = var5;
      this.m11 = var7;
      this.m02 = var9;
      this.m12 = var11;
      this.updateState();
   }

   public void concatenate(AffineTransform var1) {
      int var18 = this.state;
      int var19 = var1.state;
      double var2;
      double var8;
      double var10;
      switch(var19 << 3 | var18) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
         return;
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
         this.translate(var1.m02, var1.m12);
         return;
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
         this.scale(var1.m00, var1.m11);
         return;
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      default:
         double var6 = var1.m00;
         var8 = var1.m01;
         double var14 = var1.m02;
         var10 = var1.m10;
         double var12 = var1.m11;
         double var16 = var1.m12;
         switch(var18) {
         case 1:
            this.m00 = var6;
            this.m01 = var8;
            this.m02 += var14;
            this.m10 = var10;
            this.m11 = var12;
            this.m12 += var16;
            this.state = var19 | 1;
            this.type = -1;
            return;
         case 2:
         case 3:
            var2 = this.m00;
            this.m00 = var6 * var2;
            this.m01 = var8 * var2;
            this.m02 += var14 * var2;
            var2 = this.m11;
            this.m10 = var10 * var2;
            this.m11 = var12 * var2;
            this.m12 += var16 * var2;
            break;
         case 4:
         case 5:
            var2 = this.m01;
            this.m00 = var10 * var2;
            this.m01 = var12 * var2;
            this.m02 += var16 * var2;
            var2 = this.m10;
            this.m10 = var6 * var2;
            this.m11 = var8 * var2;
            this.m12 += var14 * var2;
            break;
         default:
            this.stateError();
         case 6:
            this.state = var18 | var19;
         case 7:
            var2 = this.m00;
            double var4 = this.m01;
            this.m00 = var6 * var2 + var10 * var4;
            this.m01 = var8 * var2 + var12 * var4;
            this.m02 += var14 * var2 + var16 * var4;
            var2 = this.m10;
            var4 = this.m11;
            this.m10 = var6 * var2 + var10 * var4;
            this.m11 = var8 * var2 + var12 * var4;
            this.m12 += var14 * var2 + var16 * var4;
            this.type = -1;
            return;
         }

         this.updateState();
         return;
      case 33:
         this.m00 = 0.0D;
         this.m01 = var1.m01;
         this.m10 = var1.m10;
         this.m11 = 0.0D;
         this.state = 5;
         this.type = -1;
         return;
      case 34:
      case 35:
         this.m01 = this.m00 * var1.m01;
         this.m00 = 0.0D;
         this.m10 = this.m11 * var1.m10;
         this.m11 = 0.0D;
         this.state = var18 ^ 6;
         this.type = -1;
         return;
      case 36:
      case 37:
         this.m00 = this.m01 * var1.m10;
         this.m01 = 0.0D;
         this.m11 = this.m10 * var1.m01;
         this.m10 = 0.0D;
         this.state = var18 ^ 6;
         this.type = -1;
         return;
      case 38:
      case 39:
         var8 = var1.m01;
         var10 = var1.m10;
         var2 = this.m00;
         this.m00 = this.m01 * var10;
         this.m01 = var2 * var8;
         var2 = this.m10;
         this.m10 = this.m11 * var10;
         this.m11 = var2 * var8;
         this.type = -1;
         return;
      case 40:
         this.m02 = var1.m02;
         this.m12 = var1.m12;
      case 32:
         this.m01 = var1.m01;
         this.m10 = var1.m10;
         this.m00 = this.m11 = 0.0D;
         this.state = var19;
         this.type = var1.type;
         return;
      case 48:
         this.m01 = var1.m01;
         this.m10 = var1.m10;
      case 16:
         this.m00 = var1.m00;
         this.m11 = var1.m11;
         this.state = var19;
         this.type = var1.type;
         return;
      case 56:
         this.m01 = var1.m01;
         this.m10 = var1.m10;
      case 24:
         this.m00 = var1.m00;
         this.m11 = var1.m11;
      case 8:
         this.m02 = var1.m02;
         this.m12 = var1.m12;
         this.state = var19;
         this.type = var1.type;
      }
   }

   public void preConcatenate(AffineTransform var1) {
      int var18 = this.state;
      int var19 = var1.state;
      double var2;
      double var6;
      double var8;
      double var10;
      double var12;
      switch(var19 << 3 | var18) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
         return;
      case 8:
      case 10:
      case 12:
      case 14:
         this.m02 = var1.m02;
         this.m12 = var1.m12;
         this.state = var18 | 1;
         this.type |= 1;
         return;
      case 9:
      case 11:
      case 13:
      case 15:
         this.m02 += var1.m02;
         this.m12 += var1.m12;
         return;
      case 16:
      case 17:
         this.state = var18 | 2;
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
         var6 = var1.m00;
         var12 = var1.m11;
         if ((var18 & 4) != 0) {
            this.m01 *= var6;
            this.m10 *= var12;
            if ((var18 & 2) != 0) {
               this.m00 *= var6;
               this.m11 *= var12;
            }
         } else {
            this.m00 *= var6;
            this.m11 *= var12;
         }

         if ((var18 & 1) != 0) {
            this.m02 *= var6;
            this.m12 *= var12;
         }

         this.type = -1;
         return;
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      default:
         var6 = var1.m00;
         var8 = var1.m01;
         double var14 = var1.m02;
         var10 = var1.m10;
         var12 = var1.m11;
         double var16 = var1.m12;
         double var4;
         switch(var18) {
         case 1:
            var2 = this.m02;
            var4 = this.m12;
            var14 += var2 * var6 + var4 * var8;
            var16 += var2 * var10 + var4 * var12;
         case 0:
            this.m02 = var14;
            this.m12 = var16;
            this.m00 = var6;
            this.m10 = var10;
            this.m01 = var8;
            this.m11 = var12;
            this.state = var18 | var19;
            this.type = -1;
            return;
         case 3:
            var2 = this.m02;
            var4 = this.m12;
            var14 += var2 * var6 + var4 * var8;
            var16 += var2 * var10 + var4 * var12;
         case 2:
            this.m02 = var14;
            this.m12 = var16;
            var2 = this.m00;
            this.m00 = var2 * var6;
            this.m10 = var2 * var10;
            var2 = this.m11;
            this.m01 = var2 * var8;
            this.m11 = var2 * var12;
            break;
         case 5:
            var2 = this.m02;
            var4 = this.m12;
            var14 += var2 * var6 + var4 * var8;
            var16 += var2 * var10 + var4 * var12;
         case 4:
            this.m02 = var14;
            this.m12 = var16;
            var2 = this.m10;
            this.m00 = var2 * var8;
            this.m10 = var2 * var12;
            var2 = this.m01;
            this.m01 = var2 * var6;
            this.m11 = var2 * var10;
            break;
         default:
            this.stateError();
         case 7:
            var2 = this.m02;
            var4 = this.m12;
            var14 += var2 * var6 + var4 * var8;
            var16 += var2 * var10 + var4 * var12;
         case 6:
            this.m02 = var14;
            this.m12 = var16;
            var2 = this.m00;
            var4 = this.m10;
            this.m00 = var2 * var6 + var4 * var8;
            this.m10 = var2 * var10 + var4 * var12;
            var2 = this.m01;
            var4 = this.m11;
            this.m01 = var2 * var6 + var4 * var8;
            this.m11 = var2 * var10 + var4 * var12;
         }

         this.updateState();
         return;
      case 36:
      case 37:
         var18 |= 2;
      case 32:
      case 33:
      case 34:
      case 35:
         this.state = var18 ^ 4;
      case 38:
      case 39:
         var8 = var1.m01;
         var10 = var1.m10;
         var2 = this.m00;
         this.m00 = this.m10 * var8;
         this.m10 = var2 * var10;
         var2 = this.m01;
         this.m01 = this.m11 * var8;
         this.m11 = var2 * var10;
         var2 = this.m02;
         this.m02 = this.m12 * var8;
         this.m12 = var2 * var10;
         this.type = -1;
      }
   }

   public AffineTransform createInverse() throws NoninvertibleTransformException {
      double var1;
      switch(this.state) {
      case 0:
         return new AffineTransform();
      case 1:
         return new AffineTransform(1.0D, 0.0D, 0.0D, 1.0D, -this.m02, -this.m12, 1);
      case 2:
         if (this.m00 != 0.0D && this.m11 != 0.0D) {
            return new AffineTransform(1.0D / this.m00, 0.0D, 0.0D, 1.0D / this.m11, 0.0D, 0.0D, 2);
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 3:
         if (this.m00 != 0.0D && this.m11 != 0.0D) {
            return new AffineTransform(1.0D / this.m00, 0.0D, 0.0D, 1.0D / this.m11, -this.m02 / this.m00, -this.m12 / this.m11, 3);
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 4:
         if (this.m01 != 0.0D && this.m10 != 0.0D) {
            return new AffineTransform(0.0D, 1.0D / this.m01, 1.0D / this.m10, 0.0D, 0.0D, 0.0D, 4);
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 5:
         if (this.m01 != 0.0D && this.m10 != 0.0D) {
            return new AffineTransform(0.0D, 1.0D / this.m01, 1.0D / this.m10, 0.0D, -this.m12 / this.m10, -this.m02 / this.m01, 5);
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 6:
         var1 = this.m00 * this.m11 - this.m01 * this.m10;
         if (Math.abs(var1) <= Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + var1);
         }

         return new AffineTransform(this.m11 / var1, -this.m10 / var1, -this.m01 / var1, this.m00 / var1, 0.0D, 0.0D, 6);
      case 7:
         var1 = this.m00 * this.m11 - this.m01 * this.m10;
         if (Math.abs(var1) <= Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + var1);
         }

         return new AffineTransform(this.m11 / var1, -this.m10 / var1, -this.m01 / var1, this.m00 / var1, (this.m01 * this.m12 - this.m11 * this.m02) / var1, (this.m10 * this.m02 - this.m00 * this.m12) / var1, 7);
      default:
         this.stateError();
         return null;
      }
   }

   public void invert() throws NoninvertibleTransformException {
      double var1;
      double var3;
      double var5;
      double var7;
      double var9;
      double var11;
      double var13;
      switch(this.state) {
      case 0:
         break;
      case 1:
         this.m02 = -this.m02;
         this.m12 = -this.m12;
         break;
      case 2:
         var1 = this.m00;
         var9 = this.m11;
         if (var1 == 0.0D || var9 == 0.0D) {
            throw new NoninvertibleTransformException("Determinant is 0");
         }

         this.m00 = 1.0D / var1;
         this.m11 = 1.0D / var9;
         break;
      case 3:
         var1 = this.m00;
         var5 = this.m02;
         var9 = this.m11;
         var11 = this.m12;
         if (var1 == 0.0D || var9 == 0.0D) {
            throw new NoninvertibleTransformException("Determinant is 0");
         }

         this.m00 = 1.0D / var1;
         this.m11 = 1.0D / var9;
         this.m02 = -var5 / var1;
         this.m12 = -var11 / var9;
         break;
      case 4:
         var3 = this.m01;
         var7 = this.m10;
         if (var3 != 0.0D && var7 != 0.0D) {
            this.m10 = 1.0D / var3;
            this.m01 = 1.0D / var7;
            break;
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 5:
         var3 = this.m01;
         var5 = this.m02;
         var7 = this.m10;
         var11 = this.m12;
         if (var3 != 0.0D && var7 != 0.0D) {
            this.m10 = 1.0D / var3;
            this.m01 = 1.0D / var7;
            this.m02 = -var11 / var7;
            this.m12 = -var5 / var3;
            break;
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 6:
         var1 = this.m00;
         var3 = this.m01;
         var7 = this.m10;
         var9 = this.m11;
         var13 = var1 * var9 - var3 * var7;
         if (Math.abs(var13) <= Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + var13);
         }

         this.m00 = var9 / var13;
         this.m10 = -var7 / var13;
         this.m01 = -var3 / var13;
         this.m11 = var1 / var13;
         break;
      case 7:
         var1 = this.m00;
         var3 = this.m01;
         var5 = this.m02;
         var7 = this.m10;
         var9 = this.m11;
         var11 = this.m12;
         var13 = var1 * var9 - var3 * var7;
         if (Math.abs(var13) <= Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + var13);
         }

         this.m00 = var9 / var13;
         this.m10 = -var7 / var13;
         this.m01 = -var3 / var13;
         this.m11 = var1 / var13;
         this.m02 = (var3 * var11 - var9 * var5) / var13;
         this.m12 = (var7 * var5 - var1 * var11) / var13;
         break;
      default:
         this.stateError();
         return;
      }

   }

   public Point2D transform(Point2D var1, Point2D var2) {
      if (var2 == null) {
         if (var1 instanceof Point2D.Double) {
            var2 = new Point2D.Double();
         } else {
            var2 = new Point2D.Float();
         }
      }

      double var3 = var1.getX();
      double var5 = var1.getY();
      switch(this.state) {
      case 0:
         ((Point2D)var2).setLocation(var3, var5);
         return (Point2D)var2;
      case 1:
         ((Point2D)var2).setLocation(var3 + this.m02, var5 + this.m12);
         return (Point2D)var2;
      case 2:
         ((Point2D)var2).setLocation(var3 * this.m00, var5 * this.m11);
         return (Point2D)var2;
      case 3:
         ((Point2D)var2).setLocation(var3 * this.m00 + this.m02, var5 * this.m11 + this.m12);
         return (Point2D)var2;
      case 4:
         ((Point2D)var2).setLocation(var5 * this.m01, var3 * this.m10);
         return (Point2D)var2;
      case 5:
         ((Point2D)var2).setLocation(var5 * this.m01 + this.m02, var3 * this.m10 + this.m12);
         return (Point2D)var2;
      case 6:
         ((Point2D)var2).setLocation(var3 * this.m00 + var5 * this.m01, var3 * this.m10 + var5 * this.m11);
         return (Point2D)var2;
      case 7:
         ((Point2D)var2).setLocation(var3 * this.m00 + var5 * this.m01 + this.m02, var3 * this.m10 + var5 * this.m11 + this.m12);
         return (Point2D)var2;
      default:
         this.stateError();
         return null;
      }
   }

   public void transform(Point2D[] var1, int var2, Point2D[] var3, int var4, int var5) {
      int var6 = this.state;

      while(true) {
         --var5;
         if (var5 < 0) {
            return;
         }

         Point2D var7 = var1[var2++];
         double var8 = var7.getX();
         double var10 = var7.getY();
         Object var12 = var3[var4++];
         if (var12 == null) {
            if (var7 instanceof Point2D.Double) {
               var12 = new Point2D.Double();
            } else {
               var12 = new Point2D.Float();
            }

            var3[var4 - 1] = (Point2D)var12;
         }

         switch(var6) {
         case 0:
            ((Point2D)var12).setLocation(var8, var10);
            break;
         case 1:
            ((Point2D)var12).setLocation(var8 + this.m02, var10 + this.m12);
            break;
         case 2:
            ((Point2D)var12).setLocation(var8 * this.m00, var10 * this.m11);
            break;
         case 3:
            ((Point2D)var12).setLocation(var8 * this.m00 + this.m02, var10 * this.m11 + this.m12);
            break;
         case 4:
            ((Point2D)var12).setLocation(var10 * this.m01, var8 * this.m10);
            break;
         case 5:
            ((Point2D)var12).setLocation(var10 * this.m01 + this.m02, var8 * this.m10 + this.m12);
            break;
         case 6:
            ((Point2D)var12).setLocation(var8 * this.m00 + var10 * this.m01, var8 * this.m10 + var10 * this.m11);
            break;
         case 7:
            ((Point2D)var12).setLocation(var8 * this.m00 + var10 * this.m01 + this.m02, var8 * this.m10 + var10 * this.m11 + this.m12);
            break;
         default:
            this.stateError();
            return;
         }
      }
   }

   public void transform(float[] var1, int var2, float[] var3, int var4, int var5) {
      if (var3 == var1 && var4 > var2 && var4 < var2 + var5 * 2) {
         System.arraycopy(var1, var2, var3, var4, var5 * 2);
         var2 = var4;
      }

      double var6;
      double var8;
      double var10;
      double var12;
      double var14;
      double var16;
      double var18;
      double var20;
      switch(this.state) {
      case 0:
         if (var1 != var3 || var2 != var4) {
            System.arraycopy(var1, var2, var3, var4, var5 * 2);
         }

         return;
      case 1:
         var10 = this.m02;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (float)((double)var1[var2++] + var10);
            var3[var4++] = (float)((double)var1[var2++] + var16);
         }
      case 2:
         var6 = this.m00;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (float)(var6 * (double)var1[var2++]);
            var3[var4++] = (float)(var14 * (double)var1[var2++]);
         }
      case 3:
         var6 = this.m00;
         var10 = this.m02;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (float)(var6 * (double)var1[var2++] + var10);
            var3[var4++] = (float)(var14 * (double)var1[var2++] + var16);
         }
      case 4:
         var8 = this.m01;
         var12 = this.m10;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var3[var4++] = (float)(var8 * (double)var1[var2++]);
            var3[var4++] = (float)(var12 * var18);
         }
      case 5:
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var3[var4++] = (float)(var8 * (double)var1[var2++] + var10);
            var3[var4++] = (float)(var12 * var18 + var16);
         }
      case 6:
         var6 = this.m00;
         var8 = this.m01;
         var12 = this.m10;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var20 = (double)var1[var2++];
            var3[var4++] = (float)(var6 * var18 + var8 * var20);
            var3[var4++] = (float)(var12 * var18 + var14 * var20);
         }
      case 7:
         var6 = this.m00;
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var20 = (double)var1[var2++];
            var3[var4++] = (float)(var6 * var18 + var8 * var20 + var10);
            var3[var4++] = (float)(var12 * var18 + var14 * var20 + var16);
         }
      default:
         this.stateError();
      }
   }

   public void transform(double[] var1, int var2, double[] var3, int var4, int var5) {
      if (var3 == var1 && var4 > var2 && var4 < var2 + var5 * 2) {
         System.arraycopy(var1, var2, var3, var4, var5 * 2);
         var2 = var4;
      }

      double var6;
      double var8;
      double var10;
      double var12;
      double var14;
      double var16;
      double var18;
      double var20;
      switch(this.state) {
      case 0:
         if (var1 != var3 || var2 != var4) {
            System.arraycopy(var1, var2, var3, var4, var5 * 2);
         }

         return;
      case 1:
         var10 = this.m02;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = var1[var2++] + var10;
            var3[var4++] = var1[var2++] + var16;
         }
      case 2:
         var6 = this.m00;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = var6 * var1[var2++];
            var3[var4++] = var14 * var1[var2++];
         }
      case 3:
         var6 = this.m00;
         var10 = this.m02;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = var6 * var1[var2++] + var10;
            var3[var4++] = var14 * var1[var2++] + var16;
         }
      case 4:
         var8 = this.m01;
         var12 = this.m10;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var3[var4++] = var8 * var1[var2++];
            var3[var4++] = var12 * var18;
         }
      case 5:
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var3[var4++] = var8 * var1[var2++] + var10;
            var3[var4++] = var12 * var18 + var16;
         }
      case 6:
         var6 = this.m00;
         var8 = this.m01;
         var12 = this.m10;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var20 = var1[var2++];
            var3[var4++] = var6 * var18 + var8 * var20;
            var3[var4++] = var12 * var18 + var14 * var20;
         }
      case 7:
         var6 = this.m00;
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var20 = var1[var2++];
            var3[var4++] = var6 * var18 + var8 * var20 + var10;
            var3[var4++] = var12 * var18 + var14 * var20 + var16;
         }
      default:
         this.stateError();
      }
   }

   public void transform(float[] var1, int var2, double[] var3, int var4, int var5) {
      double var6;
      double var8;
      double var10;
      double var12;
      double var14;
      double var16;
      double var18;
      double var20;
      switch(this.state) {
      case 0:
         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (double)var1[var2++];
            var3[var4++] = (double)var1[var2++];
         }
      case 1:
         var10 = this.m02;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (double)var1[var2++] + var10;
            var3[var4++] = (double)var1[var2++] + var16;
         }
      case 2:
         var6 = this.m00;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = var6 * (double)var1[var2++];
            var3[var4++] = var14 * (double)var1[var2++];
         }
      case 3:
         var6 = this.m00;
         var10 = this.m02;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = var6 * (double)var1[var2++] + var10;
            var3[var4++] = var14 * (double)var1[var2++] + var16;
         }
      case 4:
         var8 = this.m01;
         var12 = this.m10;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var3[var4++] = var8 * (double)var1[var2++];
            var3[var4++] = var12 * var18;
         }
      case 5:
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var3[var4++] = var8 * (double)var1[var2++] + var10;
            var3[var4++] = var12 * var18 + var16;
         }
      case 6:
         var6 = this.m00;
         var8 = this.m01;
         var12 = this.m10;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var20 = (double)var1[var2++];
            var3[var4++] = var6 * var18 + var8 * var20;
            var3[var4++] = var12 * var18 + var14 * var20;
         }
      case 7:
         var6 = this.m00;
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = (double)var1[var2++];
            var20 = (double)var1[var2++];
            var3[var4++] = var6 * var18 + var8 * var20 + var10;
            var3[var4++] = var12 * var18 + var14 * var20 + var16;
         }
      default:
         this.stateError();
      }
   }

   public void transform(double[] var1, int var2, float[] var3, int var4, int var5) {
      double var6;
      double var8;
      double var10;
      double var12;
      double var14;
      double var16;
      double var18;
      double var20;
      switch(this.state) {
      case 0:
         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (float)var1[var2++];
            var3[var4++] = (float)var1[var2++];
         }
      case 1:
         var10 = this.m02;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (float)(var1[var2++] + var10);
            var3[var4++] = (float)(var1[var2++] + var16);
         }
      case 2:
         var6 = this.m00;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (float)(var6 * var1[var2++]);
            var3[var4++] = (float)(var14 * var1[var2++]);
         }
      case 3:
         var6 = this.m00;
         var10 = this.m02;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = (float)(var6 * var1[var2++] + var10);
            var3[var4++] = (float)(var14 * var1[var2++] + var16);
         }
      case 4:
         var8 = this.m01;
         var12 = this.m10;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var3[var4++] = (float)(var8 * var1[var2++]);
            var3[var4++] = (float)(var12 * var18);
         }
      case 5:
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var3[var4++] = (float)(var8 * var1[var2++] + var10);
            var3[var4++] = (float)(var12 * var18 + var16);
         }
      case 6:
         var6 = this.m00;
         var8 = this.m01;
         var12 = this.m10;
         var14 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var20 = var1[var2++];
            var3[var4++] = (float)(var6 * var18 + var8 * var20);
            var3[var4++] = (float)(var12 * var18 + var14 * var20);
         }
      case 7:
         var6 = this.m00;
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var14 = this.m11;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var18 = var1[var2++];
            var20 = var1[var2++];
            var3[var4++] = (float)(var6 * var18 + var8 * var20 + var10);
            var3[var4++] = (float)(var12 * var18 + var14 * var20 + var16);
         }
      default:
         this.stateError();
      }
   }

   public Point2D inverseTransform(Point2D var1, Point2D var2) throws NoninvertibleTransformException {
      if (var2 == null) {
         if (var1 instanceof Point2D.Double) {
            var2 = new Point2D.Double();
         } else {
            var2 = new Point2D.Float();
         }
      }

      double var3 = var1.getX();
      double var5 = var1.getY();
      switch(this.state) {
      case 0:
         ((Point2D)var2).setLocation(var3, var5);
         return (Point2D)var2;
      case 1:
         ((Point2D)var2).setLocation(var3 - this.m02, var5 - this.m12);
         return (Point2D)var2;
      case 3:
         var3 -= this.m02;
         var5 -= this.m12;
      case 2:
         if (this.m00 != 0.0D && this.m11 != 0.0D) {
            ((Point2D)var2).setLocation(var3 / this.m00, var5 / this.m11);
            return (Point2D)var2;
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 5:
         var3 -= this.m02;
         var5 -= this.m12;
      case 4:
         if (this.m01 != 0.0D && this.m10 != 0.0D) {
            ((Point2D)var2).setLocation(var5 / this.m10, var3 / this.m01);
            return (Point2D)var2;
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      default:
         this.stateError();
      case 7:
         var3 -= this.m02;
         var5 -= this.m12;
      case 6:
         double var7 = this.m00 * this.m11 - this.m01 * this.m10;
         if (Math.abs(var7) <= Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + var7);
         } else {
            ((Point2D)var2).setLocation((var3 * this.m11 - var5 * this.m01) / var7, (var5 * this.m00 - var3 * this.m10) / var7);
            return (Point2D)var2;
         }
      }
   }

   public void inverseTransform(double[] var1, int var2, double[] var3, int var4, int var5) throws NoninvertibleTransformException {
      if (var3 == var1 && var4 > var2 && var4 < var2 + var5 * 2) {
         System.arraycopy(var1, var2, var3, var4, var5 * 2);
         var2 = var4;
      }

      double var6;
      double var8;
      double var10;
      double var12;
      double var14;
      double var16;
      double var18;
      double var20;
      double var22;
      switch(this.state) {
      case 0:
         if (var1 != var3 || var2 != var4) {
            System.arraycopy(var1, var2, var3, var4, var5 * 2);
         }

         return;
      case 1:
         var10 = this.m02;
         var16 = this.m12;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = var1[var2++] - var10;
            var3[var4++] = var1[var2++] - var16;
         }
      case 2:
         var6 = this.m00;
         var14 = this.m11;
         if (var6 != 0.0D && var14 != 0.0D) {
            while(true) {
               --var5;
               if (var5 < 0) {
                  return;
               }

               var3[var4++] = var1[var2++] / var6;
               var3[var4++] = var1[var2++] / var14;
            }
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 3:
         var6 = this.m00;
         var10 = this.m02;
         var14 = this.m11;
         var16 = this.m12;
         if (var6 != 0.0D && var14 != 0.0D) {
            while(true) {
               --var5;
               if (var5 < 0) {
                  return;
               }

               var3[var4++] = (var1[var2++] - var10) / var6;
               var3[var4++] = (var1[var2++] - var16) / var14;
            }
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 4:
         var8 = this.m01;
         var12 = this.m10;
         if (var8 != 0.0D && var12 != 0.0D) {
            while(true) {
               --var5;
               if (var5 < 0) {
                  return;
               }

               var20 = var1[var2++];
               var3[var4++] = var1[var2++] / var12;
               var3[var4++] = var20 / var8;
            }
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 5:
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var16 = this.m12;
         if (var8 != 0.0D && var12 != 0.0D) {
            while(true) {
               --var5;
               if (var5 < 0) {
                  return;
               }

               var20 = var1[var2++] - var10;
               var3[var4++] = (var1[var2++] - var16) / var12;
               var3[var4++] = var20 / var8;
            }
         }

         throw new NoninvertibleTransformException("Determinant is 0");
      case 6:
         var6 = this.m00;
         var8 = this.m01;
         var12 = this.m10;
         var14 = this.m11;
         var18 = var6 * var14 - var8 * var12;
         if (Math.abs(var18) <= Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + var18);
         } else {
            while(true) {
               --var5;
               if (var5 < 0) {
                  return;
               }

               var20 = var1[var2++];
               var22 = var1[var2++];
               var3[var4++] = (var20 * var14 - var22 * var8) / var18;
               var3[var4++] = (var22 * var6 - var20 * var12) / var18;
            }
         }
      case 7:
         var6 = this.m00;
         var8 = this.m01;
         var10 = this.m02;
         var12 = this.m10;
         var14 = this.m11;
         var16 = this.m12;
         var18 = var6 * var14 - var8 * var12;
         if (Math.abs(var18) <= Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + var18);
         } else {
            while(true) {
               --var5;
               if (var5 < 0) {
                  return;
               }

               var20 = var1[var2++] - var10;
               var22 = var1[var2++] - var16;
               var3[var4++] = (var20 * var14 - var22 * var8) / var18;
               var3[var4++] = (var22 * var6 - var20 * var12) / var18;
            }
         }
      default:
         this.stateError();
      }
   }

   public Point2D deltaTransform(Point2D var1, Point2D var2) {
      if (var2 == null) {
         if (var1 instanceof Point2D.Double) {
            var2 = new Point2D.Double();
         } else {
            var2 = new Point2D.Float();
         }
      }

      double var3 = var1.getX();
      double var5 = var1.getY();
      switch(this.state) {
      case 0:
      case 1:
         ((Point2D)var2).setLocation(var3, var5);
         return (Point2D)var2;
      case 2:
      case 3:
         ((Point2D)var2).setLocation(var3 * this.m00, var5 * this.m11);
         return (Point2D)var2;
      case 4:
      case 5:
         ((Point2D)var2).setLocation(var5 * this.m01, var3 * this.m10);
         return (Point2D)var2;
      case 6:
      case 7:
         ((Point2D)var2).setLocation(var3 * this.m00 + var5 * this.m01, var3 * this.m10 + var5 * this.m11);
         return (Point2D)var2;
      default:
         this.stateError();
         return null;
      }
   }

   public void deltaTransform(double[] var1, int var2, double[] var3, int var4, int var5) {
      if (var3 == var1 && var4 > var2 && var4 < var2 + var5 * 2) {
         System.arraycopy(var1, var2, var3, var4, var5 * 2);
         var2 = var4;
      }

      double var6;
      double var8;
      double var10;
      double var12;
      double var14;
      switch(this.state) {
      case 0:
      case 1:
         if (var1 != var3 || var2 != var4) {
            System.arraycopy(var1, var2, var3, var4, var5 * 2);
         }

         return;
      case 2:
      case 3:
         var6 = this.m00;
         var12 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var3[var4++] = var1[var2++] * var6;
            var3[var4++] = var1[var2++] * var12;
         }
      case 4:
      case 5:
         var8 = this.m01;
         var10 = this.m10;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var14 = var1[var2++];
            var3[var4++] = var1[var2++] * var8;
            var3[var4++] = var14 * var10;
         }
      case 6:
      case 7:
         var6 = this.m00;
         var8 = this.m01;
         var10 = this.m10;
         var12 = this.m11;

         while(true) {
            --var5;
            if (var5 < 0) {
               return;
            }

            var14 = var1[var2++];
            double var16 = var1[var2++];
            var3[var4++] = var14 * var6 + var16 * var8;
            var3[var4++] = var14 * var10 + var16 * var12;
         }
      default:
         this.stateError();
      }
   }

   public Shape createTransformedShape(Shape var1) {
      return var1 == null ? null : new Path2D.Double(var1, this);
   }

   private static double _matround(double var0) {
      return Math.rint(var0 * 1.0E15D) / 1.0E15D;
   }

   public String toString() {
      return "AffineTransform[[" + _matround(this.m00) + ", " + _matround(this.m01) + ", " + _matround(this.m02) + "], [" + _matround(this.m10) + ", " + _matround(this.m11) + ", " + _matround(this.m12) + "]]";
   }

   public boolean isIdentity() {
      return this.state == 0 || this.getType() == 0;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.m00);
      var1 = var1 * 31L + Double.doubleToLongBits(this.m01);
      var1 = var1 * 31L + Double.doubleToLongBits(this.m02);
      var1 = var1 * 31L + Double.doubleToLongBits(this.m10);
      var1 = var1 * 31L + Double.doubleToLongBits(this.m11);
      var1 = var1 * 31L + Double.doubleToLongBits(this.m12);
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof AffineTransform)) {
         return false;
      } else {
         AffineTransform var2 = (AffineTransform)var1;
         return this.m00 == var2.m00 && this.m01 == var2.m01 && this.m02 == var2.m02 && this.m10 == var2.m10 && this.m11 == var2.m11 && this.m12 == var2.m12;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.updateState();
   }
}
