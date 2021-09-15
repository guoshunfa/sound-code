package sun.security.krb5.internal.util;

import java.io.IOException;
import java.util.Arrays;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;

public class KerberosFlags {
   BitArray bits;
   protected static final int BITS_PER_UNIT = 8;

   public KerberosFlags(int var1) throws IllegalArgumentException {
      this.bits = new BitArray(var1);
   }

   public KerberosFlags(int var1, byte[] var2) throws IllegalArgumentException {
      this.bits = new BitArray(var1, var2);
      if (var1 != 32) {
         this.bits = new BitArray(Arrays.copyOf((boolean[])this.bits.toBooleanArray(), 32));
      }

   }

   public KerberosFlags(boolean[] var1) {
      this.bits = new BitArray(var1.length == 32 ? var1 : Arrays.copyOf((boolean[])var1, 32));
   }

   public void set(int var1, boolean var2) {
      this.bits.set(var1, var2);
   }

   public boolean get(int var1) {
      return this.bits.get(var1);
   }

   public boolean[] toBooleanArray() {
      return this.bits.toBooleanArray();
   }

   public byte[] asn1Encode() throws IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putUnalignedBitString(this.bits);
      return var1.toByteArray();
   }

   public String toString() {
      return this.bits.toString();
   }
}
