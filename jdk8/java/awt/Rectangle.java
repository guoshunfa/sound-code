package java.awt;

import java.awt.geom.Rectangle2D;
import java.beans.Transient;
import java.io.Serializable;

public class Rectangle extends Rectangle2D implements Shape, Serializable {
   public int x;
   public int y;
   public int width;
   public int height;
   private static final long serialVersionUID = -4345857070255674764L;

   private static native void initIDs();

   public Rectangle() {
      this(0, 0, 0, 0);
   }

   public Rectangle(Rectangle var1) {
      this(var1.x, var1.y, var1.width, var1.height);
   }

   public Rectangle(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public Rectangle(int var1, int var2) {
      this(0, 0, var1, var2);
   }

   public Rectangle(Point var1, Dimension var2) {
      this(var1.x, var1.y, var2.width, var2.height);
   }

   public Rectangle(Point var1) {
      this(var1.x, var1.y, 0, 0);
   }

   public Rectangle(Dimension var1) {
      this(0, 0, var1.width, var1.height);
   }

   public double getX() {
      return (double)this.x;
   }

   public double getY() {
      return (double)this.y;
   }

   public double getWidth() {
      return (double)this.width;
   }

   public double getHeight() {
      return (double)this.height;
   }

   @Transient
   public Rectangle getBounds() {
      return new Rectangle(this.x, this.y, this.width, this.height);
   }

   public Rectangle2D getBounds2D() {
      return new Rectangle(this.x, this.y, this.width, this.height);
   }

   public void setBounds(Rectangle var1) {
      this.setBounds(var1.x, var1.y, var1.width, var1.height);
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      this.reshape(var1, var2, var3, var4);
   }

   public void setRect(double var1, double var3, double var5, double var7) {
      int var9;
      int var11;
      if (var1 > 4.294967294E9D) {
         var9 = Integer.MAX_VALUE;
         var11 = -1;
      } else {
         var9 = clip(var1, false);
         if (var5 >= 0.0D) {
            var5 += var1 - (double)var9;
         }

         var11 = clip(var5, var5 >= 0.0D);
      }

      int var10;
      int var12;
      if (var3 > 4.294967294E9D) {
         var10 = Integer.MAX_VALUE;
         var12 = -1;
      } else {
         var10 = clip(var3, false);
         if (var7 >= 0.0D) {
            var7 += var3 - (double)var10;
         }

         var12 = clip(var7, var7 >= 0.0D);
      }

      this.reshape(var9, var10, var11, var12);
   }

   private static int clip(double var0, boolean var2) {
      if (var0 <= -2.147483648E9D) {
         return Integer.MIN_VALUE;
      } else {
         return var0 >= 2.147483647E9D ? Integer.MAX_VALUE : (int)(var2 ? Math.ceil(var0) : Math.floor(var0));
      }
   }

   /** @deprecated */
   @Deprecated
   public void reshape(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public Point getLocation() {
      return new Point(this.x, this.y);
   }

   public void setLocation(Point var1) {
      this.setLocation(var1.x, var1.y);
   }

   public void setLocation(int var1, int var2) {
      this.move(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public void move(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public void translate(int var1, int var2) {
      int var3 = this.x;
      int var4 = var3 + var1;
      if (var1 < 0) {
         if (var4 > var3) {
            if (this.width >= 0) {
               this.width += var4 - Integer.MIN_VALUE;
            }

            var4 = Integer.MIN_VALUE;
         }
      } else if (var4 < var3) {
         if (this.width >= 0) {
            this.width += var4 - Integer.MAX_VALUE;
            if (this.width < 0) {
               this.width = Integer.MAX_VALUE;
            }
         }

         var4 = Integer.MAX_VALUE;
      }

      this.x = var4;
      var3 = this.y;
      var4 = var3 + var2;
      if (var2 < 0) {
         if (var4 > var3) {
            if (this.height >= 0) {
               this.height += var4 - Integer.MIN_VALUE;
            }

            var4 = Integer.MIN_VALUE;
         }
      } else if (var4 < var3) {
         if (this.height >= 0) {
            this.height += var4 - Integer.MAX_VALUE;
            if (this.height < 0) {
               this.height = Integer.MAX_VALUE;
            }
         }

         var4 = Integer.MAX_VALUE;
      }

      this.y = var4;
   }

   public Dimension getSize() {
      return new Dimension(this.width, this.height);
   }

   public void setSize(Dimension var1) {
      this.setSize(var1.width, var1.height);
   }

   public void setSize(int var1, int var2) {
      this.resize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public void resize(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public boolean contains(Point var1) {
      return this.contains(var1.x, var1.y);
   }

   public boolean contains(int var1, int var2) {
      return this.inside(var1, var2);
   }

   public boolean contains(Rectangle var1) {
      return this.contains(var1.x, var1.y, var1.width, var1.height);
   }

   public boolean contains(int var1, int var2, int var3, int var4) {
      int var5 = this.width;
      int var6 = this.height;
      if ((var5 | var6 | var3 | var4) < 0) {
         return false;
      } else {
         int var7 = this.x;
         int var8 = this.y;
         if (var1 >= var7 && var2 >= var8) {
            var5 += var7;
            var3 += var1;
            if (var3 <= var1) {
               if (var5 >= var7 || var3 > var5) {
                  return false;
               }
            } else if (var5 >= var7 && var3 > var5) {
               return false;
            }

            var6 += var8;
            var4 += var2;
            if (var4 <= var2) {
               if (var6 >= var8 || var4 > var6) {
                  return false;
               }
            } else if (var6 >= var8 && var4 > var6) {
               return false;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean inside(int var1, int var2) {
      int var3 = this.width;
      int var4 = this.height;
      if ((var3 | var4) < 0) {
         return false;
      } else {
         int var5 = this.x;
         int var6 = this.y;
         if (var1 >= var5 && var2 >= var6) {
            var3 += var5;
            var4 += var6;
            return (var3 < var5 || var3 > var1) && (var4 < var6 || var4 > var2);
         } else {
            return false;
         }
      }
   }

   public boolean intersects(Rectangle var1) {
      int var2 = this.width;
      int var3 = this.height;
      int var4 = var1.width;
      int var5 = var1.height;
      if (var4 > 0 && var5 > 0 && var2 > 0 && var3 > 0) {
         int var6 = this.x;
         int var7 = this.y;
         int var8 = var1.x;
         int var9 = var1.y;
         var4 += var8;
         var5 += var9;
         var2 += var6;
         var3 += var7;
         return (var4 < var8 || var4 > var6) && (var5 < var9 || var5 > var7) && (var2 < var6 || var2 > var8) && (var3 < var7 || var3 > var9);
      } else {
         return false;
      }
   }

   public Rectangle intersection(Rectangle var1) {
      int var2 = this.x;
      int var3 = this.y;
      int var4 = var1.x;
      int var5 = var1.y;
      long var6 = (long)var2;
      var6 += (long)this.width;
      long var8 = (long)var3;
      var8 += (long)this.height;
      long var10 = (long)var4;
      var10 += (long)var1.width;
      long var12 = (long)var5;
      var12 += (long)var1.height;
      if (var2 < var4) {
         var2 = var4;
      }

      if (var3 < var5) {
         var3 = var5;
      }

      if (var6 > var10) {
         var6 = var10;
      }

      if (var8 > var12) {
         var8 = var12;
      }

      var6 -= (long)var2;
      var8 -= (long)var3;
      if (var6 < -2147483648L) {
         var6 = -2147483648L;
      }

      if (var8 < -2147483648L) {
         var8 = -2147483648L;
      }

      return new Rectangle(var2, var3, (int)var6, (int)var8);
   }

   public Rectangle union(Rectangle var1) {
      long var2 = (long)this.width;
      long var4 = (long)this.height;
      if ((var2 | var4) < 0L) {
         return new Rectangle(var1);
      } else {
         long var6 = (long)var1.width;
         long var8 = (long)var1.height;
         if ((var6 | var8) < 0L) {
            return new Rectangle(this);
         } else {
            int var10 = this.x;
            int var11 = this.y;
            var2 += (long)var10;
            var4 += (long)var11;
            int var12 = var1.x;
            int var13 = var1.y;
            var6 += (long)var12;
            var8 += (long)var13;
            if (var10 > var12) {
               var10 = var12;
            }

            if (var11 > var13) {
               var11 = var13;
            }

            if (var2 < var6) {
               var2 = var6;
            }

            if (var4 < var8) {
               var4 = var8;
            }

            var2 -= (long)var10;
            var4 -= (long)var11;
            if (var2 > 2147483647L) {
               var2 = 2147483647L;
            }

            if (var4 > 2147483647L) {
               var4 = 2147483647L;
            }

            return new Rectangle(var10, var11, (int)var2, (int)var4);
         }
      }
   }

   public void add(int var1, int var2) {
      if ((this.width | this.height) < 0) {
         this.x = var1;
         this.y = var2;
         this.width = this.height = 0;
      } else {
         int var3 = this.x;
         int var4 = this.y;
         long var5 = (long)this.width;
         long var7 = (long)this.height;
         var5 += (long)var3;
         var7 += (long)var4;
         if (var3 > var1) {
            var3 = var1;
         }

         if (var4 > var2) {
            var4 = var2;
         }

         if (var5 < (long)var1) {
            var5 = (long)var1;
         }

         if (var7 < (long)var2) {
            var7 = (long)var2;
         }

         var5 -= (long)var3;
         var7 -= (long)var4;
         if (var5 > 2147483647L) {
            var5 = 2147483647L;
         }

         if (var7 > 2147483647L) {
            var7 = 2147483647L;
         }

         this.reshape(var3, var4, (int)var5, (int)var7);
      }
   }

   public void add(Point var1) {
      this.add(var1.x, var1.y);
   }

   public void add(Rectangle var1) {
      long var2 = (long)this.width;
      long var4 = (long)this.height;
      if ((var2 | var4) < 0L) {
         this.reshape(var1.x, var1.y, var1.width, var1.height);
      }

      long var6 = (long)var1.width;
      long var8 = (long)var1.height;
      if ((var6 | var8) >= 0L) {
         int var10 = this.x;
         int var11 = this.y;
         var2 += (long)var10;
         var4 += (long)var11;
         int var12 = var1.x;
         int var13 = var1.y;
         var6 += (long)var12;
         var8 += (long)var13;
         if (var10 > var12) {
            var10 = var12;
         }

         if (var11 > var13) {
            var11 = var13;
         }

         if (var2 < var6) {
            var2 = var6;
         }

         if (var4 < var8) {
            var4 = var8;
         }

         var2 -= (long)var10;
         var4 -= (long)var11;
         if (var2 > 2147483647L) {
            var2 = 2147483647L;
         }

         if (var4 > 2147483647L) {
            var4 = 2147483647L;
         }

         this.reshape(var10, var11, (int)var2, (int)var4);
      }
   }

   public void grow(int var1, int var2) {
      long var3 = (long)this.x;
      long var5 = (long)this.y;
      long var7 = (long)this.width;
      long var9 = (long)this.height;
      var7 += var3;
      var9 += var5;
      var3 -= (long)var1;
      var5 -= (long)var2;
      var7 += (long)var1;
      var9 += (long)var2;
      if (var7 < var3) {
         var7 -= var3;
         if (var7 < -2147483648L) {
            var7 = -2147483648L;
         }

         if (var3 < -2147483648L) {
            var3 = -2147483648L;
         } else if (var3 > 2147483647L) {
            var3 = 2147483647L;
         }
      } else {
         if (var3 < -2147483648L) {
            var3 = -2147483648L;
         } else if (var3 > 2147483647L) {
            var3 = 2147483647L;
         }

         var7 -= var3;
         if (var7 < -2147483648L) {
            var7 = -2147483648L;
         } else if (var7 > 2147483647L) {
            var7 = 2147483647L;
         }
      }

      if (var9 < var5) {
         var9 -= var5;
         if (var9 < -2147483648L) {
            var9 = -2147483648L;
         }

         if (var5 < -2147483648L) {
            var5 = -2147483648L;
         } else if (var5 > 2147483647L) {
            var5 = 2147483647L;
         }
      } else {
         if (var5 < -2147483648L) {
            var5 = -2147483648L;
         } else if (var5 > 2147483647L) {
            var5 = 2147483647L;
         }

         var9 -= var5;
         if (var9 < -2147483648L) {
            var9 = -2147483648L;
         } else if (var9 > 2147483647L) {
            var9 = 2147483647L;
         }
      }

      this.reshape((int)var3, (int)var5, (int)var7, (int)var9);
   }

   public boolean isEmpty() {
      return this.width <= 0 || this.height <= 0;
   }

   public int outcode(double var1, double var3) {
      int var5 = 0;
      if (this.width <= 0) {
         var5 |= 5;
      } else if (var1 < (double)this.x) {
         var5 |= 1;
      } else if (var1 > (double)this.x + (double)this.width) {
         var5 |= 4;
      }

      if (this.height <= 0) {
         var5 |= 10;
      } else if (var3 < (double)this.y) {
         var5 |= 2;
      } else if (var3 > (double)this.y + (double)this.height) {
         var5 |= 8;
      }

      return var5;
   }

   public Rectangle2D createIntersection(Rectangle2D var1) {
      if (var1 instanceof Rectangle) {
         return this.intersection((Rectangle)var1);
      } else {
         Rectangle2D.Double var2 = new Rectangle2D.Double();
         Rectangle2D.intersect(this, var1, var2);
         return var2;
      }
   }

   public Rectangle2D createUnion(Rectangle2D var1) {
      if (var1 instanceof Rectangle) {
         return this.union((Rectangle)var1);
      } else {
         Rectangle2D.Double var2 = new Rectangle2D.Double();
         Rectangle2D.union(this, var1, var2);
         return var2;
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Rectangle)) {
         return super.equals(var1);
      } else {
         Rectangle var2 = (Rectangle)var1;
         return this.x == var2.x && this.y == var2.y && this.width == var2.width && this.height == var2.height;
      }
   }

   public String toString() {
      return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",width=" + this.width + ",height=" + this.height + "]";
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }
}
