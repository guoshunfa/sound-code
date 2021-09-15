package java.awt.image;

import java.util.Hashtable;

public class ReplicateScaleFilter extends ImageFilter {
   protected int srcWidth;
   protected int srcHeight;
   protected int destWidth;
   protected int destHeight;
   protected int[] srcrows;
   protected int[] srccols;
   protected Object outpixbuf;

   public ReplicateScaleFilter(int var1, int var2) {
      if (var1 != 0 && var2 != 0) {
         this.destWidth = var1;
         this.destHeight = var2;
      } else {
         throw new IllegalArgumentException("Width (" + var1 + ") and height (" + var2 + ") must be non-zero");
      }
   }

   public void setProperties(Hashtable<?, ?> var1) {
      Hashtable var2 = (Hashtable)var1.clone();
      String var3 = "rescale";
      String var4 = this.destWidth + "x" + this.destHeight;
      Object var5 = var2.get(var3);
      if (var5 != null && var5 instanceof String) {
         var4 = (String)var5 + ", " + var4;
      }

      var2.put(var3, var4);
      super.setProperties(var2);
   }

   public void setDimensions(int var1, int var2) {
      this.srcWidth = var1;
      this.srcHeight = var2;
      if (this.destWidth < 0) {
         if (this.destHeight < 0) {
            this.destWidth = this.srcWidth;
            this.destHeight = this.srcHeight;
         } else {
            this.destWidth = this.srcWidth * this.destHeight / this.srcHeight;
         }
      } else if (this.destHeight < 0) {
         this.destHeight = this.srcHeight * this.destWidth / this.srcWidth;
      }

      this.consumer.setDimensions(this.destWidth, this.destHeight);
   }

   private void calculateMaps() {
      this.srcrows = new int[this.destHeight + 1];

      int var1;
      for(var1 = 0; var1 <= this.destHeight; ++var1) {
         this.srcrows[var1] = (2 * var1 * this.srcHeight + this.srcHeight) / (2 * this.destHeight);
      }

      this.srccols = new int[this.destWidth + 1];

      for(var1 = 0; var1 <= this.destWidth; ++var1) {
         this.srccols[var1] = (2 * var1 * this.srcWidth + this.srcWidth) / (2 * this.destWidth);
      }

   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      if (this.srcrows == null || this.srccols == null) {
         this.calculateMaps();
      }

      int var11 = (2 * var1 * this.destWidth + this.srcWidth - 1) / (2 * this.srcWidth);
      int var12 = (2 * var2 * this.destHeight + this.srcHeight - 1) / (2 * this.srcHeight);
      byte[] var13;
      if (this.outpixbuf != null && this.outpixbuf instanceof byte[]) {
         var13 = (byte[])((byte[])this.outpixbuf);
      } else {
         var13 = new byte[this.destWidth];
         this.outpixbuf = var13;
      }

      int var10;
      for(int var14 = var12; (var10 = this.srcrows[var14]) < var2 + var4; ++var14) {
         int var15 = var7 + var8 * (var10 - var2);

         int var9;
         int var16;
         for(var16 = var11; (var9 = this.srccols[var16]) < var1 + var3; ++var16) {
            var13[var16] = var6[var15 + var9 - var1];
         }

         if (var16 > var11) {
            this.consumer.setPixels(var11, var14, var16 - var11, 1, var5, (byte[])var13, var11, this.destWidth);
         }
      }

   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      if (this.srcrows == null || this.srccols == null) {
         this.calculateMaps();
      }

      int var11 = (2 * var1 * this.destWidth + this.srcWidth - 1) / (2 * this.srcWidth);
      int var12 = (2 * var2 * this.destHeight + this.srcHeight - 1) / (2 * this.srcHeight);
      int[] var13;
      if (this.outpixbuf != null && this.outpixbuf instanceof int[]) {
         var13 = (int[])((int[])this.outpixbuf);
      } else {
         var13 = new int[this.destWidth];
         this.outpixbuf = var13;
      }

      int var10;
      for(int var14 = var12; (var10 = this.srcrows[var14]) < var2 + var4; ++var14) {
         int var15 = var7 + var8 * (var10 - var2);

         int var9;
         int var16;
         for(var16 = var11; (var9 = this.srccols[var16]) < var1 + var3; ++var16) {
            var13[var16] = var6[var15 + var9 - var1];
         }

         if (var16 > var11) {
            this.consumer.setPixels(var11, var14, var16 - var11, 1, var5, (int[])var13, var11, this.destWidth);
         }
      }

   }
}
