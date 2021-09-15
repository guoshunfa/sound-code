package sun.java2d.pipe;

public class RegionIterator {
   Region region;
   int curIndex;
   int numXbands;

   RegionIterator(Region var1) {
      this.region = var1;
   }

   public RegionIterator createCopy() {
      RegionIterator var1 = new RegionIterator(this.region);
      var1.curIndex = this.curIndex;
      var1.numXbands = this.numXbands;
      return var1;
   }

   public void copyStateFrom(RegionIterator var1) {
      if (this.region != var1.region) {
         throw new InternalError("region mismatch");
      } else {
         this.curIndex = var1.curIndex;
         this.numXbands = var1.numXbands;
      }
   }

   public boolean nextYRange(int[] var1) {
      this.curIndex += this.numXbands * 2;
      this.numXbands = 0;
      if (this.curIndex >= this.region.endIndex) {
         return false;
      } else {
         var1[1] = this.region.bands[this.curIndex++];
         var1[3] = this.region.bands[this.curIndex++];
         this.numXbands = this.region.bands[this.curIndex++];
         return true;
      }
   }

   public boolean nextXBand(int[] var1) {
      if (this.numXbands <= 0) {
         return false;
      } else {
         --this.numXbands;
         var1[0] = this.region.bands[this.curIndex++];
         var1[2] = this.region.bands[this.curIndex++];
         return true;
      }
   }
}
