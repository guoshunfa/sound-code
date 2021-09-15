package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

public final class ResponseAPDU implements Serializable {
   private static final long serialVersionUID = 6962744978375594225L;
   private byte[] apdu;

   public ResponseAPDU(byte[] var1) {
      var1 = (byte[])var1.clone();
      check(var1);
      this.apdu = var1;
   }

   private static void check(byte[] var0) {
      if (var0.length < 2) {
         throw new IllegalArgumentException("apdu must be at least 2 bytes long");
      }
   }

   public int getNr() {
      return this.apdu.length - 2;
   }

   public byte[] getData() {
      byte[] var1 = new byte[this.apdu.length - 2];
      System.arraycopy(this.apdu, 0, var1, 0, var1.length);
      return var1;
   }

   public int getSW1() {
      return this.apdu[this.apdu.length - 2] & 255;
   }

   public int getSW2() {
      return this.apdu[this.apdu.length - 1] & 255;
   }

   public int getSW() {
      return this.getSW1() << 8 | this.getSW2();
   }

   public byte[] getBytes() {
      return (byte[])this.apdu.clone();
   }

   public String toString() {
      return "ResponseAPDU: " + this.apdu.length + " bytes, SW=" + Integer.toHexString(this.getSW());
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ResponseAPDU)) {
         return false;
      } else {
         ResponseAPDU var2 = (ResponseAPDU)var1;
         return Arrays.equals(this.apdu, var2.apdu);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.apdu);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.apdu = (byte[])((byte[])var1.readUnshared());
      check(this.apdu);
   }
}
