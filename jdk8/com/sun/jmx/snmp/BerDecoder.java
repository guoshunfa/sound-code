package com.sun.jmx.snmp;

public class BerDecoder {
   public static final int BooleanTag = 1;
   public static final int IntegerTag = 2;
   public static final int OctetStringTag = 4;
   public static final int NullTag = 5;
   public static final int OidTag = 6;
   public static final int SequenceTag = 48;
   private final byte[] bytes;
   private int next = 0;
   private final int[] stackBuf = new int[200];
   private int stackTop = 0;

   public BerDecoder(byte[] var1) {
      this.bytes = var1;
      this.reset();
   }

   public void reset() {
      this.next = 0;
      this.stackTop = 0;
   }

   public int fetchInteger() throws BerException {
      return this.fetchInteger(2);
   }

   public int fetchInteger(int var1) throws BerException {
      boolean var2 = false;
      int var3 = this.next;

      try {
         if (this.fetchTag() != var1) {
            throw new BerException();
         } else {
            int var6 = this.fetchIntegerValue();
            return var6;
         }
      } catch (BerException var5) {
         this.next = var3;
         throw var5;
      }
   }

   public long fetchIntegerAsLong() throws BerException {
      return this.fetchIntegerAsLong(2);
   }

   public long fetchIntegerAsLong(int var1) throws BerException {
      long var2 = 0L;
      int var4 = this.next;

      try {
         if (this.fetchTag() != var1) {
            throw new BerException();
         } else {
            var2 = this.fetchIntegerValueAsLong();
            return var2;
         }
      } catch (BerException var6) {
         this.next = var4;
         throw var6;
      }
   }

   public byte[] fetchOctetString() throws BerException {
      return this.fetchOctetString(4);
   }

   public byte[] fetchOctetString(int var1) throws BerException {
      Object var2 = null;
      int var3 = this.next;

      try {
         if (this.fetchTag() != var1) {
            throw new BerException();
         } else {
            byte[] var6 = this.fetchStringValue();
            return var6;
         }
      } catch (BerException var5) {
         this.next = var3;
         throw var5;
      }
   }

   public long[] fetchOid() throws BerException {
      return this.fetchOid(6);
   }

   public long[] fetchOid(int var1) throws BerException {
      Object var2 = null;
      int var3 = this.next;

      try {
         if (this.fetchTag() != var1) {
            throw new BerException();
         } else {
            long[] var6 = this.fetchOidValue();
            return var6;
         }
      } catch (BerException var5) {
         this.next = var3;
         throw var5;
      }
   }

   public void fetchNull() throws BerException {
      this.fetchNull(5);
   }

   public void fetchNull(int var1) throws BerException {
      int var2 = this.next;

      try {
         if (this.fetchTag() != var1) {
            throw new BerException();
         } else {
            int var3 = this.fetchLength();
            if (var3 != 0) {
               throw new BerException();
            }
         }
      } catch (BerException var4) {
         this.next = var2;
         throw var4;
      }
   }

   public byte[] fetchAny() throws BerException {
      Object var1 = null;
      int var2 = this.next;

      try {
         int var3 = this.fetchTag();
         int var4 = this.fetchLength();
         if (var4 < 0) {
            throw new BerException();
         } else {
            int var5 = this.next + var4 - var2;
            if (var4 > this.bytes.length - this.next) {
               throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
            } else {
               byte[] var6 = new byte[var5];
               System.arraycopy(this.bytes, var2, var6, 0, var5);
               this.next += var4;
               return var6;
            }
         }
      } catch (IndexOutOfBoundsException var7) {
         this.next = var2;
         throw new BerException();
      }
   }

   public byte[] fetchAny(int var1) throws BerException {
      if (this.getTag() != var1) {
         throw new BerException();
      } else {
         return this.fetchAny();
      }
   }

   public void openSequence() throws BerException {
      this.openSequence(48);
   }

   public void openSequence(int var1) throws BerException {
      int var2 = this.next;

      try {
         if (this.fetchTag() != var1) {
            throw new BerException();
         } else {
            int var3 = this.fetchLength();
            if (var3 < 0) {
               throw new BerException();
            } else if (var3 > this.bytes.length - this.next) {
               throw new BerException();
            } else {
               this.stackBuf[this.stackTop++] = this.next + var3;
            }
         }
      } catch (BerException var4) {
         this.next = var2;
         throw var4;
      }
   }

   public void closeSequence() throws BerException {
      if (this.stackBuf[this.stackTop - 1] == this.next) {
         --this.stackTop;
      } else {
         throw new BerException();
      }
   }

   public boolean cannotCloseSequence() {
      return this.next < this.stackBuf[this.stackTop - 1];
   }

   public int getTag() throws BerException {
      boolean var1 = false;
      int var2 = this.next;

      int var6;
      try {
         var6 = this.fetchTag();
      } finally {
         this.next = var2;
      }

      return var6;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(this.bytes.length * 2);

      for(int var2 = 0; var2 < this.bytes.length; ++var2) {
         int var3 = this.bytes[var2] > 0 ? this.bytes[var2] : this.bytes[var2] + 256;
         if (var2 == this.next) {
            var1.append("(");
         }

         var1.append(Character.forDigit(var3 / 16, 16));
         var1.append(Character.forDigit(var3 % 16, 16));
         if (var2 == this.next) {
            var1.append(")");
         }
      }

      if (this.bytes.length == this.next) {
         var1.append("()");
      }

      return new String(var1);
   }

   private final int fetchTag() throws BerException {
      boolean var1 = false;
      int var2 = this.next;

      try {
         byte var3 = this.bytes[this.next++];
         int var5 = var3 >= 0 ? var3 : var3 + 256;
         if ((var5 & 31) == 31) {
            while((this.bytes[this.next] & 128) != 0) {
               var5 <<= 7;
               var5 |= this.bytes[this.next++] & 127;
            }
         }

         return var5;
      } catch (IndexOutOfBoundsException var4) {
         this.next = var2;
         throw new BerException();
      }
   }

   private final int fetchLength() throws BerException {
      int var1 = 0;
      int var2 = this.next;

      try {
         byte var3 = this.bytes[this.next++];
         if (var3 >= 0) {
            var1 = var3;
         } else {
            for(int var4 = 128 + var3; var4 > 0; --var4) {
               byte var5 = this.bytes[this.next++];
               var1 <<= 8;
               var1 |= var5 >= 0 ? var5 : var5 + 256;
            }
         }

         return var1;
      } catch (IndexOutOfBoundsException var6) {
         this.next = var2;
         throw new BerException();
      }
   }

   private int fetchIntegerValue() throws BerException {
      boolean var1 = false;
      int var2 = this.next;

      try {
         int var3 = this.fetchLength();
         if (var3 <= 0) {
            throw new BerException();
         } else if (var3 > this.bytes.length - this.next) {
            throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
         } else {
            int var4 = this.next + var3;
            int var9 = this.bytes[this.next++];

            while(this.next < var4) {
               byte var5 = this.bytes[this.next++];
               if (var5 < 0) {
                  var9 = var9 << 8 | 256 + var5;
               } else {
                  var9 = var9 << 8 | var5;
               }
            }

            return var9;
         }
      } catch (BerException var6) {
         this.next = var2;
         throw var6;
      } catch (IndexOutOfBoundsException var7) {
         this.next = var2;
         throw new BerException();
      } catch (ArithmeticException var8) {
         this.next = var2;
         throw new BerException();
      }
   }

   private final long fetchIntegerValueAsLong() throws BerException {
      long var1 = 0L;
      int var3 = this.next;

      try {
         int var4 = this.fetchLength();
         if (var4 <= 0) {
            throw new BerException();
         } else if (var4 > this.bytes.length - this.next) {
            throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
         } else {
            int var5 = this.next + var4;
            var1 = (long)this.bytes[this.next++];

            while(this.next < var5) {
               byte var6 = this.bytes[this.next++];
               if (var6 < 0) {
                  var1 = var1 << 8 | (long)(256 + var6);
               } else {
                  var1 = var1 << 8 | (long)var6;
               }
            }

            return var1;
         }
      } catch (BerException var7) {
         this.next = var3;
         throw var7;
      } catch (IndexOutOfBoundsException var8) {
         this.next = var3;
         throw new BerException();
      } catch (ArithmeticException var9) {
         this.next = var3;
         throw new BerException();
      }
   }

   private byte[] fetchStringValue() throws BerException {
      Object var1 = null;
      int var2 = this.next;

      try {
         int var3 = this.fetchLength();
         if (var3 < 0) {
            throw new BerException();
         } else if (var3 > this.bytes.length - this.next) {
            throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
         } else {
            byte[] var4 = new byte[var3];
            System.arraycopy(this.bytes, this.next, var4, 0, var3);
            this.next += var3;
            return var4;
         }
      } catch (BerException var5) {
         this.next = var2;
         throw var5;
      } catch (IndexOutOfBoundsException var6) {
         this.next = var2;
         throw new BerException();
      } catch (ArithmeticException var7) {
         this.next = var2;
         throw new BerException();
      }
   }

   private final long[] fetchOidValue() throws BerException {
      Object var1 = null;
      int var2 = this.next;

      try {
         int var3 = this.fetchLength();
         if (var3 <= 0) {
            throw new BerException();
         } else if (var3 > this.bytes.length - this.next) {
            throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
         } else {
            int var4 = 2;

            int var5;
            for(var5 = 1; var5 < var3; ++var5) {
               if ((this.bytes[this.next + var5] & 128) == 0) {
                  ++var4;
               }
            }

            var5 = var4;
            long[] var6 = new long[var4];
            byte var7 = this.bytes[this.next++];
            if (var7 < 0) {
               throw new BerException();
            } else {
               long var8 = (long)(var7 / 40);
               if (var8 > 2L) {
                  throw new BerException();
               } else {
                  long var10 = (long)(var7 % 40);
                  var6[0] = var8;
                  var6[1] = var10;

                  long var13;
                  for(int var12 = 2; var12 < var5; var6[var12++] = var13) {
                     var13 = 0L;

                     byte var15;
                     for(var15 = this.bytes[this.next++]; (var15 & 128) != 0; var15 = this.bytes[this.next++]) {
                        var13 = var13 << 7 | (long)(var15 & 127);
                        if (var13 < 0L) {
                           throw new BerException();
                        }
                     }

                     var13 = var13 << 7 | (long)var15;
                     if (var13 < 0L) {
                        throw new BerException();
                     }
                  }

                  return var6;
               }
            }
         }
      } catch (BerException var16) {
         this.next = var2;
         throw var16;
      } catch (IndexOutOfBoundsException var17) {
         this.next = var2;
         throw new BerException();
      }
   }
}
