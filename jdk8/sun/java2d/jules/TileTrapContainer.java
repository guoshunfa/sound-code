package sun.java2d.jules;

import sun.java2d.xr.GrowableIntArray;

class TileTrapContainer {
   int tileAlpha;
   GrowableIntArray traps;

   public TileTrapContainer(GrowableIntArray var1) {
      this.traps = var1;
   }

   public void setTileAlpha(int var1) {
      this.tileAlpha = var1;
   }

   public int getTileAlpha() {
      return this.tileAlpha;
   }

   public GrowableIntArray getTraps() {
      return this.traps;
   }
}
