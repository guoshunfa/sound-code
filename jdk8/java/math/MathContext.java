package java.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public final class MathContext implements Serializable {
   private static final int DEFAULT_DIGITS = 9;
   private static final RoundingMode DEFAULT_ROUNDINGMODE;
   private static final int MIN_DIGITS = 0;
   private static final long serialVersionUID = 5579720004786848255L;
   public static final MathContext UNLIMITED;
   public static final MathContext DECIMAL32;
   public static final MathContext DECIMAL64;
   public static final MathContext DECIMAL128;
   final int precision;
   final RoundingMode roundingMode;

   public MathContext(int var1) {
      this(var1, DEFAULT_ROUNDINGMODE);
   }

   public MathContext(int var1, RoundingMode var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Digits < 0");
      } else if (var2 == null) {
         throw new NullPointerException("null RoundingMode");
      } else {
         this.precision = var1;
         this.roundingMode = var2;
      }
   }

   public MathContext(String var1) {
      boolean var2 = false;
      if (var1 == null) {
         throw new NullPointerException("null String");
      } else {
         int var3;
         try {
            if (!var1.startsWith("precision=")) {
               throw new RuntimeException();
            }

            int var4 = var1.indexOf(32);
            boolean var5 = true;
            var3 = Integer.parseInt(var1.substring(10, var4));
            if (!var1.startsWith("roundingMode=", var4 + 1)) {
               throw new RuntimeException();
            }

            int var8 = var4 + 1 + 13;
            String var6 = var1.substring(var8, var1.length());
            this.roundingMode = RoundingMode.valueOf(var6);
         } catch (RuntimeException var7) {
            throw new IllegalArgumentException("bad string format");
         }

         if (var3 < 0) {
            throw new IllegalArgumentException("Digits < 0");
         } else {
            this.precision = var3;
         }
      }
   }

   public int getPrecision() {
      return this.precision;
   }

   public RoundingMode getRoundingMode() {
      return this.roundingMode;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MathContext)) {
         return false;
      } else {
         MathContext var2 = (MathContext)var1;
         return var2.precision == this.precision && var2.roundingMode == this.roundingMode;
      }
   }

   public int hashCode() {
      return this.precision + this.roundingMode.hashCode() * 59;
   }

   public String toString() {
      return "precision=" + this.precision + " roundingMode=" + this.roundingMode.toString();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      String var2;
      if (this.precision < 0) {
         var2 = "MathContext: invalid digits in stream";
         throw new StreamCorruptedException(var2);
      } else if (this.roundingMode == null) {
         var2 = "MathContext: null roundingMode in stream";
         throw new StreamCorruptedException(var2);
      }
   }

   static {
      DEFAULT_ROUNDINGMODE = RoundingMode.HALF_UP;
      UNLIMITED = new MathContext(0, RoundingMode.HALF_UP);
      DECIMAL32 = new MathContext(7, RoundingMode.HALF_EVEN);
      DECIMAL64 = new MathContext(16, RoundingMode.HALF_EVEN);
      DECIMAL128 = new MathContext(34, RoundingMode.HALF_EVEN);
   }
}
