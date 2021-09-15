package sun.java2d.loops;

class SolidPixelWriter extends PixelWriter {
   protected Object srcData;

   SolidPixelWriter(Object var1) {
      this.srcData = var1;
   }

   public void writePixel(int var1, int var2) {
      this.dstRast.setDataElements(var1, var2, this.srcData);
   }
}
