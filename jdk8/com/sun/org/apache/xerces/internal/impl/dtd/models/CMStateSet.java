package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMStateSet {
   int fBitCount;
   int fByteCount;
   int fBits1;
   int fBits2;
   byte[] fByteArray;

   public CMStateSet(int bitCount) {
      this.fBitCount = bitCount;
      if (this.fBitCount < 0) {
         throw new RuntimeException("ImplementationMessages.VAL_CMSI");
      } else {
         if (this.fBitCount > 64) {
            this.fByteCount = this.fBitCount / 8;
            if (this.fBitCount % 8 != 0) {
               ++this.fByteCount;
            }

            this.fByteArray = new byte[this.fByteCount];
         }

         this.zeroBits();
      }
   }

   public String toString() {
      StringBuffer strRet = new StringBuffer();

      try {
         strRet.append("{");

         for(int index = 0; index < this.fBitCount; ++index) {
            if (this.getBit(index)) {
               strRet.append(" " + index);
            }
         }

         strRet.append(" }");
      } catch (RuntimeException var3) {
      }

      return strRet.toString();
   }

   public final void intersection(CMStateSet setToAnd) {
      if (this.fBitCount < 65) {
         this.fBits1 &= setToAnd.fBits1;
         this.fBits2 &= setToAnd.fBits2;
      } else {
         for(int index = this.fByteCount - 1; index >= 0; --index) {
            byte[] var10000 = this.fByteArray;
            var10000[index] &= setToAnd.fByteArray[index];
         }
      }

   }

   public final boolean getBit(int bitToGet) {
      if (bitToGet >= this.fBitCount) {
         throw new RuntimeException("ImplementationMessages.VAL_CMSI");
      } else if (this.fBitCount < 65) {
         int mask = 1 << bitToGet % 32;
         if (bitToGet < 32) {
            return (this.fBits1 & mask) != 0;
         } else {
            return (this.fBits2 & mask) != 0;
         }
      } else {
         byte mask = (byte)(1 << bitToGet % 8);
         int ofs = bitToGet >> 3;
         return (this.fByteArray[ofs] & mask) != 0;
      }
   }

   public final boolean isEmpty() {
      if (this.fBitCount >= 65) {
         for(int index = this.fByteCount - 1; index >= 0; --index) {
            if (this.fByteArray[index] != 0) {
               return false;
            }
         }

         return true;
      } else {
         return this.fBits1 == 0 && this.fBits2 == 0;
      }
   }

   final boolean isSameSet(CMStateSet setToCompare) {
      if (this.fBitCount != setToCompare.fBitCount) {
         return false;
      } else if (this.fBitCount >= 65) {
         for(int index = this.fByteCount - 1; index >= 0; --index) {
            if (this.fByteArray[index] != setToCompare.fByteArray[index]) {
               return false;
            }
         }

         return true;
      } else {
         return this.fBits1 == setToCompare.fBits1 && this.fBits2 == setToCompare.fBits2;
      }
   }

   public final void union(CMStateSet setToOr) {
      if (this.fBitCount < 65) {
         this.fBits1 |= setToOr.fBits1;
         this.fBits2 |= setToOr.fBits2;
      } else {
         for(int index = this.fByteCount - 1; index >= 0; --index) {
            byte[] var10000 = this.fByteArray;
            var10000[index] |= setToOr.fByteArray[index];
         }
      }

   }

   public final void setBit(int bitToSet) {
      if (bitToSet >= this.fBitCount) {
         throw new RuntimeException("ImplementationMessages.VAL_CMSI");
      } else {
         if (this.fBitCount < 65) {
            int mask = 1 << bitToSet % 32;
            if (bitToSet < 32) {
               this.fBits1 &= ~mask;
               this.fBits1 |= mask;
            } else {
               this.fBits2 &= ~mask;
               this.fBits2 |= mask;
            }
         } else {
            byte mask = (byte)(1 << bitToSet % 8);
            int ofs = bitToSet >> 3;
            byte[] var10000 = this.fByteArray;
            var10000[ofs] = (byte)(var10000[ofs] & ~mask);
            var10000 = this.fByteArray;
            var10000[ofs] |= mask;
         }

      }
   }

   public final void setTo(CMStateSet srcSet) {
      if (this.fBitCount != srcSet.fBitCount) {
         throw new RuntimeException("ImplementationMessages.VAL_CMSI");
      } else {
         if (this.fBitCount < 65) {
            this.fBits1 = srcSet.fBits1;
            this.fBits2 = srcSet.fBits2;
         } else {
            for(int index = this.fByteCount - 1; index >= 0; --index) {
               this.fByteArray[index] = srcSet.fByteArray[index];
            }
         }

      }
   }

   public final void zeroBits() {
      if (this.fBitCount < 65) {
         this.fBits1 = 0;
         this.fBits2 = 0;
      } else {
         for(int index = this.fByteCount - 1; index >= 0; --index) {
            this.fByteArray[index] = 0;
         }
      }

   }

   public boolean equals(Object o) {
      return !(o instanceof CMStateSet) ? false : this.isSameSet((CMStateSet)o);
   }

   public int hashCode() {
      if (this.fBitCount < 65) {
         return this.fBits1 + this.fBits2 * 31;
      } else {
         int hash = 0;

         for(int index = this.fByteCount - 1; index >= 0; --index) {
            hash = this.fByteArray[index] + hash * 31;
         }

         return hash;
      }
   }
}
