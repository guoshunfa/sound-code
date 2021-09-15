package sun.text;

public final class UCompactIntArray implements Cloneable {
   private static final int PLANEMASK = 196608;
   private static final int PLANESHIFT = 16;
   private static final int PLANECOUNT = 16;
   private static final int CODEPOINTMASK = 65535;
   private static final int UNICODECOUNT = 65536;
   private static final int BLOCKSHIFT = 7;
   private static final int BLOCKCOUNT = 128;
   private static final int INDEXSHIFT = 9;
   private static final int INDEXCOUNT = 512;
   private static final int BLOCKMASK = 127;
   private int defaultValue;
   private int[][] values;
   private short[][] indices;
   private boolean isCompact;
   private boolean[][] blockTouched;
   private boolean[] planeTouched;

   public UCompactIntArray() {
      this.values = new int[16][];
      this.indices = new short[16][];
      this.blockTouched = new boolean[16][];
      this.planeTouched = new boolean[16];
   }

   public UCompactIntArray(int var1) {
      this();
      this.defaultValue = var1;
   }

   public int elementAt(int var1) {
      int var2 = (var1 & 196608) >> 16;
      if (!this.planeTouched[var2]) {
         return this.defaultValue;
      } else {
         var1 &= 65535;
         return this.values[var2][(this.indices[var2][var1 >> 7] & '\uffff') + (var1 & 127)];
      }
   }

   public void setElementAt(int var1, int var2) {
      if (this.isCompact) {
         this.expand();
      }

      int var3 = (var1 & 196608) >> 16;
      if (!this.planeTouched[var3]) {
         this.initPlane(var3);
      }

      var1 &= 65535;
      this.values[var3][var1] = var2;
      this.blockTouched[var3][var1 >> 7] = true;
   }

   public void compact() {
      if (!this.isCompact) {
         for(int var1 = 0; var1 < 16; ++var1) {
            if (this.planeTouched[var1]) {
               int var2 = 0;
               int var3 = 0;
               short var4 = -1;

               int var5;
               for(var5 = 0; var5 < this.indices[var1].length; var3 += 128) {
                  this.indices[var1][var5] = -1;
                  if (!this.blockTouched[var1][var5] && var4 != -1) {
                     this.indices[var1][var5] = var4;
                  } else {
                     int var6 = var2 * 128;
                     if (var5 > var2) {
                        System.arraycopy(this.values[var1], var3, this.values[var1], var6, 128);
                     }

                     if (!this.blockTouched[var1][var5]) {
                        var4 = (short)var6;
                     }

                     this.indices[var1][var5] = (short)var6;
                     ++var2;
                  }

                  ++var5;
               }

               var5 = var2 * 128;
               int[] var7 = new int[var5];
               System.arraycopy(this.values[var1], 0, var7, 0, var5);
               this.values[var1] = var7;
               this.blockTouched[var1] = null;
            }
         }

         this.isCompact = true;
      }
   }

   private void expand() {
      if (this.isCompact) {
         for(int var3 = 0; var3 < 16; ++var3) {
            if (this.planeTouched[var3]) {
               this.blockTouched[var3] = new boolean[512];
               int[] var2 = new int[65536];

               int var1;
               for(var1 = 0; var1 < 65536; ++var1) {
                  var2[var1] = this.values[var3][this.indices[var3][var1 >> 7] & '\uffff' + (var1 & 127)];
                  this.blockTouched[var3][var1 >> 7] = true;
               }

               for(var1 = 0; var1 < 512; ++var1) {
                  this.indices[var3][var1] = (short)(var1 << 7);
               }

               this.values[var3] = var2;
            }
         }

         this.isCompact = false;
      }

   }

   private void initPlane(int var1) {
      this.values[var1] = new int[65536];
      this.indices[var1] = new short[512];
      this.blockTouched[var1] = new boolean[512];
      this.planeTouched[var1] = true;
      int var2;
      if (this.planeTouched[0] && var1 != 0) {
         System.arraycopy(this.indices[0], 0, this.indices[var1], 0, 512);
      } else {
         for(var2 = 0; var2 < 512; ++var2) {
            this.indices[var1][var2] = (short)(var2 << 7);
         }
      }

      for(var2 = 0; var2 < 65536; ++var2) {
         this.values[var1][var2] = this.defaultValue;
      }

   }

   public int getKSize() {
      int var1 = 0;

      for(int var2 = 0; var2 < 16; ++var2) {
         if (this.planeTouched[var2]) {
            var1 += this.values[var2].length * 4 + this.indices[var2].length * 2;
         }
      }

      return var1 / 1024;
   }
}
