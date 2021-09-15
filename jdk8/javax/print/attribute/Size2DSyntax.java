package javax.print.attribute;

import java.io.Serializable;

public abstract class Size2DSyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = 5584439964938660530L;
   private int x;
   private int y;
   public static final int INCH = 25400;
   public static final int MM = 1000;

   protected Size2DSyntax(float var1, float var2, int var3) {
      if (var1 < 0.0F) {
         throw new IllegalArgumentException("x < 0");
      } else if (var2 < 0.0F) {
         throw new IllegalArgumentException("y < 0");
      } else if (var3 < 1) {
         throw new IllegalArgumentException("units < 1");
      } else {
         this.x = (int)(var1 * (float)var3 + 0.5F);
         this.y = (int)(var2 * (float)var3 + 0.5F);
      }
   }

   protected Size2DSyntax(int var1, int var2, int var3) {
      if (var1 < 0) {
         throw new IllegalArgumentException("x < 0");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("y < 0");
      } else if (var3 < 1) {
         throw new IllegalArgumentException("units < 1");
      } else {
         this.x = var1 * var3;
         this.y = var2 * var3;
      }
   }

   private static float convertFromMicrometers(int var0, int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("units is < 1");
      } else {
         return (float)var0 / (float)var1;
      }
   }

   public float[] getSize(int var1) {
      return new float[]{this.getX(var1), this.getY(var1)};
   }

   public float getX(int var1) {
      return convertFromMicrometers(this.x, var1);
   }

   public float getY(int var1) {
      return convertFromMicrometers(this.y, var1);
   }

   public String toString(int var1, String var2) {
      StringBuffer var3 = new StringBuffer();
      var3.append(this.getX(var1));
      var3.append('x');
      var3.append(this.getY(var1));
      if (var2 != null) {
         var3.append(' ');
         var3.append(var2);
      }

      return var3.toString();
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof Size2DSyntax && this.x == ((Size2DSyntax)var1).x && this.y == ((Size2DSyntax)var1).y;
   }

   public int hashCode() {
      return this.x & '\uffff' | (this.y & '\uffff') << 16;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.x);
      var1.append('x');
      var1.append(this.y);
      var1.append(" um");
      return var1.toString();
   }

   protected int getXMicrometers() {
      return this.x;
   }

   protected int getYMicrometers() {
      return this.y;
   }
}
