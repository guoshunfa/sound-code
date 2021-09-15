package java.awt.image;

public class PixelInterleavedSampleModel extends ComponentSampleModel {
   public PixelInterleavedSampleModel(int var1, int var2, int var3, int var4, int var5, int[] var6) {
      super(var1, var2, var3, var4, var5, var6);
      int var7 = this.bandOffsets[0];
      int var8 = this.bandOffsets[0];

      for(int var9 = 1; var9 < this.bandOffsets.length; ++var9) {
         var7 = Math.min(var7, this.bandOffsets[var9]);
         var8 = Math.max(var8, this.bandOffsets[var9]);
      }

      var8 -= var7;
      if (var8 > var5) {
         throw new IllegalArgumentException("Offsets between bands must be less than the scanline  stride");
      } else if (var4 * var2 > var5) {
         throw new IllegalArgumentException("Pixel stride times width must be less than or equal to the scanline stride");
      } else if (var4 < var8) {
         throw new IllegalArgumentException("Pixel stride must be greater than or equal to the offsets between bands");
      }
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      int var3 = this.bandOffsets[0];
      int var4 = this.bandOffsets.length;

      for(int var5 = 1; var5 < var4; ++var5) {
         if (this.bandOffsets[var5] < var3) {
            var3 = this.bandOffsets[var5];
         }
      }

      int[] var7;
      if (var3 > 0) {
         var7 = new int[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            var7[var6] = this.bandOffsets[var6] - var3;
         }
      } else {
         var7 = this.bandOffsets;
      }

      return new PixelInterleavedSampleModel(this.dataType, var1, var2, this.pixelStride, this.pixelStride * var1, var7);
   }

   public SampleModel createSubsetSampleModel(int[] var1) {
      int[] var2 = new int[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = this.bandOffsets[var1[var3]];
      }

      return new PixelInterleavedSampleModel(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, var2);
   }

   public int hashCode() {
      return super.hashCode() ^ 1;
   }
}
