package sun.security.provider;

import java.security.DigestException;
import java.security.MessageDigestSpi;
import java.security.ProviderException;

abstract class DigestBase extends MessageDigestSpi implements Cloneable {
   private byte[] oneByte;
   private final String algorithm;
   private final int digestLength;
   private final int blockSize;
   byte[] buffer;
   private int bufOfs;
   long bytesProcessed;
   static final byte[] padding = new byte[136];

   DigestBase(String var1, int var2, int var3) {
      this.algorithm = var1;
      this.digestLength = var2;
      this.blockSize = var3;
      this.buffer = new byte[var3];
   }

   protected final int engineGetDigestLength() {
      return this.digestLength;
   }

   protected final void engineUpdate(byte var1) {
      if (this.oneByte == null) {
         this.oneByte = new byte[1];
      }

      this.oneByte[0] = var1;
      this.engineUpdate(this.oneByte, 0, 1);
   }

   protected final void engineUpdate(byte[] var1, int var2, int var3) {
      if (var3 != 0) {
         if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
            if (this.bytesProcessed < 0L) {
               this.engineReset();
            }

            this.bytesProcessed += (long)var3;
            int var4;
            if (this.bufOfs != 0) {
               var4 = Math.min(var3, this.blockSize - this.bufOfs);
               System.arraycopy(var1, var2, this.buffer, this.bufOfs, var4);
               this.bufOfs += var4;
               var2 += var4;
               var3 -= var4;
               if (this.bufOfs >= this.blockSize) {
                  this.implCompress(this.buffer, 0);
                  this.bufOfs = 0;
               }
            }

            if (var3 >= this.blockSize) {
               var4 = var2 + var3;
               var2 = this.implCompressMultiBlock(var1, var2, var4 - this.blockSize);
               var3 = var4 - var2;
            }

            if (var3 > 0) {
               System.arraycopy(var1, var2, this.buffer, 0, var3);
               this.bufOfs = var3;
            }

         } else {
            throw new ArrayIndexOutOfBoundsException();
         }
      }
   }

   private int implCompressMultiBlock(byte[] var1, int var2, int var3) {
      while(var2 <= var3) {
         this.implCompress(var1, var2);
         var2 += this.blockSize;
      }

      return var2;
   }

   protected final void engineReset() {
      if (this.bytesProcessed != 0L) {
         this.implReset();
         this.bufOfs = 0;
         this.bytesProcessed = 0L;
      }
   }

   protected final byte[] engineDigest() {
      byte[] var1 = new byte[this.digestLength];

      try {
         this.engineDigest(var1, 0, var1.length);
         return var1;
      } catch (DigestException var3) {
         throw (ProviderException)(new ProviderException("Internal error")).initCause(var3);
      }
   }

   protected final int engineDigest(byte[] var1, int var2, int var3) throws DigestException {
      if (var3 < this.digestLength) {
         throw new DigestException("Length must be at least " + this.digestLength + " for " + this.algorithm + "digests");
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         if (this.bytesProcessed < 0L) {
            this.engineReset();
         }

         this.implDigest(var1, var2);
         this.bytesProcessed = -1L;
         return this.digestLength;
      } else {
         throw new DigestException("Buffer too short to store digest");
      }
   }

   abstract void implCompress(byte[] var1, int var2);

   abstract void implDigest(byte[] var1, int var2);

   abstract void implReset();

   public Object clone() throws CloneNotSupportedException {
      DigestBase var1 = (DigestBase)super.clone();
      var1.buffer = (byte[])var1.buffer.clone();
      return var1;
   }

   static {
      padding[0] = -128;
   }
}
