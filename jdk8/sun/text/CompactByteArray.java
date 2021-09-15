package sun.text;

public final class CompactByteArray implements Cloneable {
   public static final int UNICODECOUNT = 65536;
   private static final int BLOCKSHIFT = 7;
   private static final int BLOCKCOUNT = 128;
   private static final int INDEXSHIFT = 9;
   private static final int INDEXCOUNT = 512;
   private static final int BLOCKMASK = 127;
   private byte[] values;
   private short[] indices;
   private boolean isCompact;
   private int[] hashes;

   public CompactByteArray(byte var1) {
      this.values = new byte[65536];
      this.indices = new short[512];
      this.hashes = new int[512];

      int var2;
      for(var2 = 0; var2 < 65536; ++var2) {
         this.values[var2] = var1;
      }

      for(var2 = 0; var2 < 512; ++var2) {
         this.indices[var2] = (short)(var2 << 7);
         this.hashes[var2] = 0;
      }

      this.isCompact = false;
   }

   public CompactByteArray(short[] var1, byte[] var2) {
      if (var1.length != 512) {
         throw new IllegalArgumentException("Index out of bounds!");
      } else {
         for(int var3 = 0; var3 < 512; ++var3) {
            short var4 = var1[var3];
            if (var4 < 0 || var4 >= var2.length + 128) {
               throw new IllegalArgumentException("Index out of bounds!");
            }
         }

         this.indices = var1;
         this.values = var2;
         this.isCompact = true;
      }
   }

   public byte elementAt(char var1) {
      return this.values[(this.indices[var1 >> 7] & '\uffff') + (var1 & 127)];
   }

   public void setElementAt(char var1, byte var2) {
      if (this.isCompact) {
         this.expand();
      }

      this.values[var1] = var2;
      this.touchBlock(var1 >> 7, var2);
   }

   public void setElementAt(char var1, char var2, byte var3) {
      if (this.isCompact) {
         this.expand();
      }

      for(int var4 = var1; var4 <= var2; ++var4) {
         this.values[var4] = var3;
         this.touchBlock(var4 >> 7, var3);
      }

   }

   public void compact() {
      if (!this.isCompact) {
         int var1 = 0;
         int var2 = 0;
         short var3 = -1;

         int var4;
         for(var4 = 0; var4 < this.indices.length; var2 += 128) {
            this.indices[var4] = -1;
            boolean var5 = this.blockTouched(var4);
            if (!var5 && var3 != -1) {
               this.indices[var4] = var3;
            } else {
               int var6 = 0;
               boolean var7 = false;

               int var9;
               for(var9 = 0; var9 < var1; var6 += 128) {
                  if (this.hashes[var4] == this.hashes[var9] && arrayRegionMatches(this.values, var2, this.values, var6, 128)) {
                     this.indices[var4] = (short)var6;
                     break;
                  }

                  ++var9;
               }

               if (this.indices[var4] == -1) {
                  System.arraycopy(this.values, var2, this.values, var6, 128);
                  this.indices[var4] = (short)var6;
                  this.hashes[var9] = this.hashes[var4];
                  ++var1;
                  if (!var5) {
                     var3 = (short)var6;
                  }
               }
            }

            ++var4;
         }

         var4 = var1 * 128;
         byte[] var8 = new byte[var4];
         System.arraycopy(this.values, 0, var8, 0, var4);
         this.values = var8;
         this.isCompact = true;
         this.hashes = null;
      }

   }

   static final boolean arrayRegionMatches(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      int var5 = var1 + var4;
      int var6 = var3 - var1;

      for(int var7 = var1; var7 < var5; ++var7) {
         if (var0[var7] != var2[var7 + var6]) {
            return false;
         }
      }

      return true;
   }

   private final void touchBlock(int var1, int var2) {
      this.hashes[var1] = this.hashes[var1] + (var2 << 1) | 1;
   }

   private final boolean blockTouched(int var1) {
      return this.hashes[var1] != 0;
   }

   public short[] getIndexArray() {
      return this.indices;
   }

   public byte[] getStringArray() {
      return this.values;
   }

   public Object clone() {
      try {
         CompactByteArray var1 = (CompactByteArray)super.clone();
         var1.values = (byte[])this.values.clone();
         var1.indices = (short[])this.indices.clone();
         if (this.hashes != null) {
            var1.hashes = (int[])this.hashes.clone();
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         CompactByteArray var2 = (CompactByteArray)var1;

         for(int var3 = 0; var3 < 65536; ++var3) {
            if (this.elementAt((char)var3) != var2.elementAt((char)var3)) {
               return false;
            }
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = Math.min(3, this.values.length / 16);

      for(int var3 = 0; var3 < this.values.length; var3 += var2) {
         var1 = var1 * 37 + this.values[var3];
      }

      return var1;
   }

   private void expand() {
      if (this.isCompact) {
         this.hashes = new int[512];
         byte[] var2 = new byte[65536];

         int var1;
         for(var1 = 0; var1 < 65536; ++var1) {
            byte var3 = this.elementAt((char)var1);
            var2[var1] = var3;
            this.touchBlock(var1 >> 7, var3);
         }

         for(var1 = 0; var1 < 512; ++var1) {
            this.indices[var1] = (short)(var1 << 7);
         }

         this.values = null;
         this.values = var2;
         this.isCompact = false;
      }

   }

   private byte[] getArray() {
      return this.values;
   }
}
