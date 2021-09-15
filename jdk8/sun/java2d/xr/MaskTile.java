package sun.java2d.xr;

public class MaskTile {
   GrowableRectArray rects = new GrowableRectArray(128);
   DirtyRegion dirtyArea = new DirtyRegion();

   public void calculateDirtyAreas() {
      for(int var1 = 0; var1 < this.rects.getSize(); ++var1) {
         int var2 = this.rects.getX(var1);
         int var3 = this.rects.getY(var1);
         this.dirtyArea.growDirtyRegion(var2, var3, var2 + this.rects.getWidth(var1), var3 + this.rects.getHeight(var1));
      }

   }

   public void reset() {
      this.rects.clear();
      this.dirtyArea.clear();
   }

   public void translate(int var1, int var2) {
      if (this.rects.getSize() > 0) {
         this.dirtyArea.translate(var1, var2);
      }

      this.rects.translateRects(var1, var2);
   }

   public GrowableRectArray getRects() {
      return this.rects;
   }

   public DirtyRegion getDirtyArea() {
      return this.dirtyArea;
   }
}
