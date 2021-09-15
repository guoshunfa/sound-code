package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class CommandAPDU implements Serializable {
   private static final long serialVersionUID = 398698301286670877L;
   private static final int MAX_APDU_SIZE = 65544;
   private byte[] apdu;
   private transient int nc;
   private transient int ne;
   private transient int dataOffset;

   public CommandAPDU(byte[] var1) {
      this.apdu = (byte[])var1.clone();
      this.parse();
   }

   public CommandAPDU(byte[] var1, int var2, int var3) {
      this.checkArrayBounds(var1, var2, var3);
      this.apdu = new byte[var3];
      System.arraycopy(var1, var2, this.apdu, 0, var3);
      this.parse();
   }

   private void checkArrayBounds(byte[] var1, int var2, int var3) {
      if (var2 >= 0 && var3 >= 0) {
         if (var1 == null) {
            if (var2 != 0 && var3 != 0) {
               throw new IllegalArgumentException("offset and length must be 0 if array is null");
            }
         } else if (var2 > var1.length - var3) {
            throw new IllegalArgumentException("Offset plus length exceed array size");
         }

      } else {
         throw new IllegalArgumentException("Offset and length must not be negative");
      }
   }

   public CommandAPDU(ByteBuffer var1) {
      this.apdu = new byte[var1.remaining()];
      var1.get(this.apdu);
      this.parse();
   }

   public CommandAPDU(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, (byte[])null, 0, 0, 0);
   }

   public CommandAPDU(int var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, (byte[])null, 0, 0, var5);
   }

   public CommandAPDU(int var1, int var2, int var3, int var4, byte[] var5) {
      this(var1, var2, var3, var4, var5, 0, arrayLength(var5), 0);
   }

   public CommandAPDU(int var1, int var2, int var3, int var4, byte[] var5, int var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, 0);
   }

   public CommandAPDU(int var1, int var2, int var3, int var4, byte[] var5, int var6) {
      this(var1, var2, var3, var4, var5, 0, arrayLength(var5), var6);
   }

   private static int arrayLength(byte[] var0) {
      return var0 != null ? var0.length : 0;
   }

   private void parse() {
      if (this.apdu.length < 4) {
         throw new IllegalArgumentException("apdu must be at least 4 bytes long");
      } else if (this.apdu.length != 4) {
         int var1 = this.apdu[4] & 255;
         if (this.apdu.length == 5) {
            this.ne = var1 == 0 ? 256 : var1;
         } else {
            int var2;
            if (var1 != 0) {
               if (this.apdu.length == 5 + var1) {
                  this.nc = var1;
                  this.dataOffset = 5;
               } else if (this.apdu.length == 6 + var1) {
                  this.nc = var1;
                  this.dataOffset = 5;
                  var2 = this.apdu[this.apdu.length - 1] & 255;
                  this.ne = var2 == 0 ? 256 : var2;
               } else {
                  throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + var1);
               }
            } else if (this.apdu.length < 7) {
               throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + var1);
            } else {
               var2 = (this.apdu[5] & 255) << 8 | this.apdu[6] & 255;
               if (this.apdu.length == 7) {
                  this.ne = var2 == 0 ? 65536 : var2;
               } else if (var2 == 0) {
                  throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + var1 + ", b2||b3=" + var2);
               } else if (this.apdu.length == 7 + var2) {
                  this.nc = var2;
                  this.dataOffset = 7;
               } else if (this.apdu.length == 9 + var2) {
                  this.nc = var2;
                  this.dataOffset = 7;
                  int var3 = this.apdu.length - 2;
                  int var4 = (this.apdu[var3] & 255) << 8 | this.apdu[var3 + 1] & 255;
                  this.ne = var4 == 0 ? 65536 : var4;
               } else {
                  throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + var1 + ", b2||b3=" + var2);
               }
            }
         }
      }
   }

   public CommandAPDU(int var1, int var2, int var3, int var4, byte[] var5, int var6, int var7, int var8) {
      this.checkArrayBounds(var5, var6, var7);
      if (var7 > 65535) {
         throw new IllegalArgumentException("dataLength is too large");
      } else if (var8 < 0) {
         throw new IllegalArgumentException("ne must not be negative");
      } else if (var8 > 65536) {
         throw new IllegalArgumentException("ne is too large");
      } else {
         this.ne = var8;
         this.nc = var7;
         if (var7 == 0) {
            if (var8 == 0) {
               this.apdu = new byte[4];
               this.setHeader(var1, var2, var3, var4);
            } else {
               byte var9;
               if (var8 <= 256) {
                  var9 = var8 != 256 ? (byte)var8 : 0;
                  this.apdu = new byte[5];
                  this.setHeader(var1, var2, var3, var4);
                  this.apdu[4] = var9;
               } else {
                  byte var10;
                  if (var8 == 65536) {
                     var9 = 0;
                     var10 = 0;
                  } else {
                     var9 = (byte)(var8 >> 8);
                     var10 = (byte)var8;
                  }

                  this.apdu = new byte[7];
                  this.setHeader(var1, var2, var3, var4);
                  this.apdu[5] = var9;
                  this.apdu[6] = var10;
               }
            }
         } else if (var8 == 0) {
            if (var7 <= 255) {
               this.apdu = new byte[5 + var7];
               this.setHeader(var1, var2, var3, var4);
               this.apdu[4] = (byte)var7;
               this.dataOffset = 5;
               System.arraycopy(var5, var6, this.apdu, 5, var7);
            } else {
               this.apdu = new byte[7 + var7];
               this.setHeader(var1, var2, var3, var4);
               this.apdu[4] = 0;
               this.apdu[5] = (byte)(var7 >> 8);
               this.apdu[6] = (byte)var7;
               this.dataOffset = 7;
               System.arraycopy(var5, var6, this.apdu, 7, var7);
            }
         } else if (var7 <= 255 && var8 <= 256) {
            this.apdu = new byte[6 + var7];
            this.setHeader(var1, var2, var3, var4);
            this.apdu[4] = (byte)var7;
            this.dataOffset = 5;
            System.arraycopy(var5, var6, this.apdu, 5, var7);
            this.apdu[this.apdu.length - 1] = var8 != 256 ? (byte)var8 : 0;
         } else {
            this.apdu = new byte[9 + var7];
            this.setHeader(var1, var2, var3, var4);
            this.apdu[4] = 0;
            this.apdu[5] = (byte)(var7 >> 8);
            this.apdu[6] = (byte)var7;
            this.dataOffset = 7;
            System.arraycopy(var5, var6, this.apdu, 7, var7);
            if (var8 != 65536) {
               int var11 = this.apdu.length - 2;
               this.apdu[var11] = (byte)(var8 >> 8);
               this.apdu[var11 + 1] = (byte)var8;
            }
         }

      }
   }

   private void setHeader(int var1, int var2, int var3, int var4) {
      this.apdu[0] = (byte)var1;
      this.apdu[1] = (byte)var2;
      this.apdu[2] = (byte)var3;
      this.apdu[3] = (byte)var4;
   }

   public int getCLA() {
      return this.apdu[0] & 255;
   }

   public int getINS() {
      return this.apdu[1] & 255;
   }

   public int getP1() {
      return this.apdu[2] & 255;
   }

   public int getP2() {
      return this.apdu[3] & 255;
   }

   public int getNc() {
      return this.nc;
   }

   public byte[] getData() {
      byte[] var1 = new byte[this.nc];
      System.arraycopy(this.apdu, this.dataOffset, var1, 0, this.nc);
      return var1;
   }

   public int getNe() {
      return this.ne;
   }

   public byte[] getBytes() {
      return (byte[])this.apdu.clone();
   }

   public String toString() {
      return "CommmandAPDU: " + this.apdu.length + " bytes, nc=" + this.nc + ", ne=" + this.ne;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CommandAPDU)) {
         return false;
      } else {
         CommandAPDU var2 = (CommandAPDU)var1;
         return Arrays.equals(this.apdu, var2.apdu);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.apdu);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.apdu = (byte[])((byte[])var1.readUnshared());
      this.parse();
   }
}
