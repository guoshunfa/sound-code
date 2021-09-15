package sun.awt.image;

import java.awt.image.DataBuffer;
import sun.java2d.SurfaceData;

public class DataBufferNative extends DataBuffer {
   protected SurfaceData surfaceData;
   protected int width;

   public DataBufferNative(SurfaceData var1, int var2, int var3, int var4) {
      super(var2, var3 * var4);
      this.width = var3;
      this.surfaceData = var1;
   }

   protected native int getElem(int var1, int var2, SurfaceData var3);

   public int getElem(int var1, int var2) {
      return this.getElem(var2 % this.width, var2 / this.width, this.surfaceData);
   }

   protected native void setElem(int var1, int var2, int var3, SurfaceData var4);

   public void setElem(int var1, int var2, int var3) {
      this.setElem(var2 % this.width, var2 / this.width, var3, this.surfaceData);
   }
}
