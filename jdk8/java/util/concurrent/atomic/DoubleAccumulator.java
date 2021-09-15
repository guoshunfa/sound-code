package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

public class DoubleAccumulator extends Striped64 implements Serializable {
   private static final long serialVersionUID = 7249069246863182397L;
   private final DoubleBinaryOperator function;
   private final long identity;

   public DoubleAccumulator(DoubleBinaryOperator var1, double var2) {
      this.function = var1;
      this.base = this.identity = Double.doubleToRawLongBits(var2);
   }

   public void accumulate(double var1) {
      Striped64.Cell[] var3;
      long var4;
      long var8;
      if ((var3 = this.cells) != null || (var8 = Double.doubleToRawLongBits(this.function.applyAsDouble(Double.longBitsToDouble(var4 = this.base), var1))) != var4 && !this.casBase(var4, var8)) {
         boolean var12 = true;
         long var6;
         int var10;
         Striped64.Cell var11;
         if (var3 == null || (var10 = var3.length - 1) < 0 || (var11 = var3[getProbe() & var10]) == null || !(var12 = (var8 = Double.doubleToRawLongBits(this.function.applyAsDouble(Double.longBitsToDouble(var6 = var11.value), var1))) == var6 || var11.cas(var6, var8))) {
            this.doubleAccumulate(var1, this.function, var12);
         }
      }

   }

   public double get() {
      Striped64.Cell[] var1 = this.cells;
      double var3 = Double.longBitsToDouble(this.base);
      if (var1 != null) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            Striped64.Cell var2;
            if ((var2 = var1[var5]) != null) {
               var3 = this.function.applyAsDouble(var3, Double.longBitsToDouble(var2.value));
            }
         }
      }

      return var3;
   }

   public void reset() {
      Striped64.Cell[] var1 = this.cells;
      this.base = this.identity;
      if (var1 != null) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            Striped64.Cell var2;
            if ((var2 = var1[var3]) != null) {
               var2.value = this.identity;
            }
         }
      }

   }

   public double getThenReset() {
      Striped64.Cell[] var1 = this.cells;
      double var3 = Double.longBitsToDouble(this.base);
      this.base = this.identity;
      if (var1 != null) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            Striped64.Cell var2;
            if ((var2 = var1[var5]) != null) {
               double var6 = Double.longBitsToDouble(var2.value);
               var2.value = this.identity;
               var3 = this.function.applyAsDouble(var3, var6);
            }
         }
      }

      return var3;
   }

   public String toString() {
      return Double.toString(this.get());
   }

   public double doubleValue() {
      return this.get();
   }

   public long longValue() {
      return (long)this.get();
   }

   public int intValue() {
      return (int)this.get();
   }

   public float floatValue() {
      return (float)this.get();
   }

   private Object writeReplace() {
      return new DoubleAccumulator.SerializationProxy(this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 7249069246863182397L;
      private final double value;
      private final DoubleBinaryOperator function;
      private final long identity;

      SerializationProxy(DoubleAccumulator var1) {
         this.function = var1.function;
         this.identity = var1.identity;
         this.value = var1.get();
      }

      private Object readResolve() {
         double var1 = Double.longBitsToDouble(this.identity);
         DoubleAccumulator var3 = new DoubleAccumulator(this.function, var1);
         var3.base = Double.doubleToRawLongBits(this.value);
         return var3;
      }
   }
}
