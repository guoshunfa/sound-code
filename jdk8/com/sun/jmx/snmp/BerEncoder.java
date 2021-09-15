package com.sun.jmx.snmp;

public class BerEncoder {
   public static final int BooleanTag = 1;
   public static final int IntegerTag = 2;
   public static final int OctetStringTag = 4;
   public static final int NullTag = 5;
   public static final int OidTag = 6;
   public static final int SequenceTag = 48;
   protected final byte[] bytes;
   protected int start = -1;
   protected final int[] stackBuf = new int[200];
   protected int stackTop = 0;

   public BerEncoder(byte[] var1) {
      this.bytes = var1;
      this.start = var1.length;
      this.stackTop = 0;
   }

   public int trim() {
      int var1 = this.bytes.length - this.start;
      if (var1 > 0) {
         System.arraycopy(this.bytes, this.start, this.bytes, 0, var1);
      }

      this.start = this.bytes.length;
      this.stackTop = 0;
      return var1;
   }

   public void putInteger(int var1) {
      this.putInteger(var1, 2);
   }

   public void putInteger(int var1, int var2) {
      this.putIntegerValue(var1);
      this.putTag(var2);
   }

   public void putInteger(long var1) {
      this.putInteger(var1, 2);
   }

   public void putInteger(long var1, int var3) {
      this.putIntegerValue(var1);
      this.putTag(var3);
   }

   public void putOctetString(byte[] var1) {
      this.putOctetString(var1, 4);
   }

   public void putOctetString(byte[] var1, int var2) {
      this.putStringValue(var1);
      this.putTag(var2);
   }

   public void putOid(long[] var1) {
      this.putOid(var1, 6);
   }

   public void putOid(long[] var1, int var2) {
      this.putOidValue(var1);
      this.putTag(var2);
   }

   public void putNull() {
      this.putNull(5);
   }

   public void putNull(int var1) {
      this.putLength(0);
      this.putTag(var1);
   }

   public void putAny(byte[] var1) {
      this.putAny(var1, var1.length);
   }

   public void putAny(byte[] var1, int var2) {
      System.arraycopy(var1, 0, this.bytes, this.start - var2, var2);
      this.start -= var2;
   }

   public void openSequence() {
      this.stackBuf[this.stackTop++] = this.start;
   }

   public void closeSequence() {
      this.closeSequence(48);
   }

   public void closeSequence(int var1) {
      int var2 = this.stackBuf[--this.stackTop];
      this.putLength(var2 - this.start);
      this.putTag(var1);
   }

   protected final void putTag(int var1) {
      if (var1 < 256) {
         this.bytes[--this.start] = (byte)var1;
      } else {
         while(var1 != 0) {
            this.bytes[--this.start] = (byte)(var1 & 127);
            var1 <<= 7;
         }
      }

   }

   protected final void putLength(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1 < 128) {
            this.bytes[--this.start] = (byte)var1;
         } else if (var1 < 256) {
            this.bytes[--this.start] = (byte)var1;
            this.bytes[--this.start] = -127;
         } else if (var1 < 65536) {
            this.bytes[--this.start] = (byte)var1;
            this.bytes[--this.start] = (byte)(var1 >> 8);
            this.bytes[--this.start] = -126;
         } else if (var1 < 16777126) {
            this.bytes[--this.start] = (byte)var1;
            this.bytes[--this.start] = (byte)(var1 >> 8);
            this.bytes[--this.start] = (byte)(var1 >> 16);
            this.bytes[--this.start] = -125;
         } else {
            this.bytes[--this.start] = (byte)var1;
            this.bytes[--this.start] = (byte)(var1 >> 8);
            this.bytes[--this.start] = (byte)(var1 >> 16);
            this.bytes[--this.start] = (byte)(var1 >> 24);
            this.bytes[--this.start] = -124;
         }

      }
   }

   protected final void putIntegerValue(int var1) {
      int var2 = this.start;
      int var3 = 2139095040;
      int var4 = 4;
      if (var1 < 0) {
         while((var3 & var1) == var3 && var4 > 1) {
            var3 >>= 8;
            --var4;
         }
      } else {
         while((var3 & var1) == 0 && var4 > 1) {
            var3 >>= 8;
            --var4;
         }
      }

      for(int var5 = 0; var5 < var4; ++var5) {
         this.bytes[--this.start] = (byte)var1;
         var1 >>= 8;
      }

      this.putLength(var2 - this.start);
   }

   protected final void putIntegerValue(long var1) {
      int var3 = this.start;
      long var4 = 9187343239835811840L;
      int var6 = 8;
      if (var1 < 0L) {
         while((var4 & var1) == var4 && var6 > 1) {
            var4 >>= 8;
            --var6;
         }
      } else {
         while((var4 & var1) == 0L && var6 > 1) {
            var4 >>= 8;
            --var6;
         }
      }

      for(int var7 = 0; var7 < var6; ++var7) {
         this.bytes[--this.start] = (byte)((int)var1);
         var1 >>= 8;
      }

      this.putLength(var3 - this.start);
   }

   protected final void putStringValue(byte[] var1) {
      int var2 = var1.length;
      System.arraycopy(var1, 0, this.bytes, this.start - var2, var2);
      this.start -= var2;
      this.putLength(var2);
   }

   protected final void putOidValue(long[] var1) {
      int var2 = this.start;
      int var3 = var1.length;
      if (var3 >= 2 && var1[0] <= 2L && var1[1] < 40L) {
         for(int var4 = var3 - 1; var4 >= 2; --var4) {
            long var5 = var1[var4];
            if (var5 < 0L) {
               throw new IllegalArgumentException();
            }

            if (var5 < 128L) {
               this.bytes[--this.start] = (byte)((int)var5);
            } else {
               this.bytes[--this.start] = (byte)((int)(var5 & 127L));

               for(var5 >>= 7; var5 != 0L; var5 >>= 7) {
                  this.bytes[--this.start] = (byte)((int)(var5 | 128L));
               }
            }
         }

         this.bytes[--this.start] = (byte)((int)(var1[0] * 40L + var1[1]));
         this.putLength(var2 - this.start);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
