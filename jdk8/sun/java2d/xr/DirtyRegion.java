package sun.java2d.xr;

public class DirtyRegion implements Cloneable {
   int x;
   int y;
   int x2;
   int y2;

   public DirtyRegion() {
      this.clear();
   }

   public void clear() {
      this.x = Integer.MAX_VALUE;
      this.y = Integer.MAX_VALUE;
      this.x2 = Integer.MIN_VALUE;
      this.y2 = Integer.MIN_VALUE;
   }

   public void growDirtyRegion(int var1, int var2, int var3, int var4) {
      this.x = Math.min(var1, this.x);
      this.y = Math.min(var2, this.y);
      this.x2 = Math.max(var3, this.x2);
      this.y2 = Math.max(var4, this.y2);
   }

   public int getWidth() {
      return this.x2 - this.x;
   }

   public int getHeight() {
      return this.y2 - this.y;
   }

   public void growDirtyRegionTileLimit(int var1, int var2, int var3, int var4) {
      if (var1 < this.x) {
         this.x = Math.max(var1, 0);
      }

      if (var2 < this.y) {
         this.y = Math.max(var2, 0);
      }

      if (var3 > this.x2) {
         this.x2 = Math.min(var3, 256);
      }

      if (var4 > this.y2) {
         this.y2 = Math.min(var4, 256);
      }

   }

   public static DirtyRegion combineRegion(DirtyRegion var0, DirtyRegion var1) {
      DirtyRegion var2 = new DirtyRegion();
      var2.x = Math.min(var0.x, var1.x);
      var2.y = Math.min(var0.y, var1.y);
      var2.x2 = Math.max(var0.x2, var1.x2);
      var2.y2 = Math.max(var0.y2, var1.y2);
      return var2;
   }

   public void setDirtyLineRegion(int var1, int var2, int var3, int var4) {
      if (var1 < var3) {
         this.x = var1;
         this.x2 = var3;
      } else {
         this.x = var3;
         this.x2 = var1;
      }

      if (var2 < var4) {
         this.y = var2;
         this.y2 = var4;
      } else {
         this.y = var4;
         this.y2 = var2;
      }

   }

   public void translate(int var1, int var2) {
      if (this.x != Integer.MAX_VALUE) {
         this.x += var1;
         this.x2 += var1;
         this.y += var2;
         this.y2 += var2;
      }

   }

   public String toString() {
      return this.getClass().getName() + "(x: " + this.x + ", y:" + this.y + ", x2:" + this.x2 + ", y2:" + this.y2 + ")";
   }

   public DirtyRegion cloneRegion() {
      try {
         return (DirtyRegion)this.clone();
      } catch (CloneNotSupportedException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
