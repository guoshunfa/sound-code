package java.security.spec;

import java.math.BigInteger;

public class EllipticCurve {
   private final ECField field;
   private final BigInteger a;
   private final BigInteger b;
   private final byte[] seed;

   private static void checkValidity(ECField var0, BigInteger var1, String var2) {
      if (var0 instanceof ECFieldFp) {
         BigInteger var3 = ((ECFieldFp)var0).getP();
         if (var3.compareTo(var1) != 1) {
            throw new IllegalArgumentException(var2 + " is too large");
         }

         if (var1.signum() < 0) {
            throw new IllegalArgumentException(var2 + " is negative");
         }
      } else if (var0 instanceof ECFieldF2m) {
         int var4 = ((ECFieldF2m)var0).getM();
         if (var1.bitLength() > var4) {
            throw new IllegalArgumentException(var2 + " is too large");
         }
      }

   }

   public EllipticCurve(ECField var1, BigInteger var2, BigInteger var3) {
      this(var1, var2, var3, (byte[])null);
   }

   public EllipticCurve(ECField var1, BigInteger var2, BigInteger var3, byte[] var4) {
      if (var1 == null) {
         throw new NullPointerException("field is null");
      } else if (var2 == null) {
         throw new NullPointerException("first coefficient is null");
      } else if (var3 == null) {
         throw new NullPointerException("second coefficient is null");
      } else {
         checkValidity(var1, var2, "first coefficient");
         checkValidity(var1, var3, "second coefficient");
         this.field = var1;
         this.a = var2;
         this.b = var3;
         if (var4 != null) {
            this.seed = (byte[])var4.clone();
         } else {
            this.seed = null;
         }

      }
   }

   public ECField getField() {
      return this.field;
   }

   public BigInteger getA() {
      return this.a;
   }

   public BigInteger getB() {
      return this.b;
   }

   public byte[] getSeed() {
      return this.seed == null ? null : (byte[])this.seed.clone();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof EllipticCurve) {
            EllipticCurve var2 = (EllipticCurve)var1;
            if (this.field.equals(var2.field) && this.a.equals(var2.a) && this.b.equals(var2.b)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.field.hashCode() << 6 + (this.a.hashCode() << 4) + (this.b.hashCode() << 2);
   }
}
