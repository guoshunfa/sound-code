package java.awt.geom;

import java.awt.Shape;

public final class GeneralPath extends Path2D.Float {
   private static final long serialVersionUID = -8327096662768731142L;

   public GeneralPath() {
      super(1, 20);
   }

   public GeneralPath(int var1) {
      super(var1, 20);
   }

   public GeneralPath(int var1, int var2) {
      super(var1, var2);
   }

   public GeneralPath(Shape var1) {
      super(var1, (AffineTransform)null);
   }

   GeneralPath(int var1, byte[] var2, int var3, float[] var4, int var5) {
      this.windingRule = var1;
      this.pointTypes = var2;
      this.numTypes = var3;
      this.floatCoords = var4;
      this.numCoords = var5;
   }
}
