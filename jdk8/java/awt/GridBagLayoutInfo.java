package java.awt;

import java.io.Serializable;

public class GridBagLayoutInfo implements Serializable {
   private static final long serialVersionUID = -4899416460737170217L;
   int width;
   int height;
   int startx;
   int starty;
   int[] minWidth;
   int[] minHeight;
   double[] weightX;
   double[] weightY;
   boolean hasBaseline;
   short[] baselineType;
   int[] maxAscent;
   int[] maxDescent;

   GridBagLayoutInfo(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   boolean hasConstantDescent(int var1) {
      return (this.baselineType[var1] & 1 << Component.BaselineResizeBehavior.CONSTANT_DESCENT.ordinal()) != 0;
   }

   boolean hasBaseline(int var1) {
      return this.hasBaseline && this.baselineType[var1] != 0;
   }
}
