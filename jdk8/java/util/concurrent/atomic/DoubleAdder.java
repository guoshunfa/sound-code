package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

public class DoubleAdder extends Striped64 implements Serializable {
   private static final long serialVersionUID = 7249069246863182397L;

   public void add(double var1) {
      Striped64.Cell[] var3;
      long var4;
      if ((var3 = this.cells) != null || !this.casBase(var4 = this.base, Double.doubleToRawLongBits(Double.longBitsToDouble(var4) + var1))) {
         boolean var10 = true;
         long var6;
         int var8;
         Striped64.Cell var9;
         if (var3 == null || (var8 = var3.length - 1) < 0 || (var9 = var3[getProbe() & var8]) == null || !(var10 = var9.cas(var6 = var9.value, Double.doubleToRawLongBits(Double.longBitsToDouble(var6) + var1)))) {
            this.doubleAccumulate(var1, (DoubleBinaryOperator)null, var10);
         }
      }

   }

   public double sum() {
      Striped64.Cell[] var1 = this.cells;
      double var3 = Double.longBitsToDouble(this.base);
      if (var1 != null) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            Striped64.Cell var2;
            if ((var2 = var1[var5]) != null) {
               var3 += Double.longBitsToDouble(var2.value);
            }
         }
      }

      return var3;
   }

   public void reset() {
      Striped64.Cell[] var1 = this.cells;
      this.base = 0L;
      if (var1 != null) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            Striped64.Cell var2;
            if ((var2 = var1[var3]) != null) {
               var2.value = 0L;
            }
         }
      }

   }

   public double sumThenReset() {
      Striped64.Cell[] var1 = this.cells;
      double var3 = Double.longBitsToDouble(this.base);
      this.base = 0L;
      if (var1 != null) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            Striped64.Cell var2;
            if ((var2 = var1[var5]) != null) {
               long var6 = var2.value;
               var2.value = 0L;
               var3 += Double.longBitsToDouble(var6);
            }
         }
      }

      return var3;
   }

   public String toString() {
      return Double.toString(this.sum());
   }

   public double doubleValue() {
      return this.sum();
   }

   public long longValue() {
      return (long)this.sum();
   }

   public int intValue() {
      return (int)this.sum();
   }

   public float floatValue() {
      return (float)this.sum();
   }

   private Object writeReplace() {
      return new DoubleAdder.SerializationProxy(this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 7249069246863182397L;
      private final double value;

      SerializationProxy(DoubleAdder var1) {
         this.value = var1.sum();
      }

      private Object readResolve() {
         DoubleAdder var1 = new DoubleAdder();
         var1.base = Double.doubleToRawLongBits(this.value);
         return var1;
      }
   }
}
