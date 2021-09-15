package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.LongBinaryOperator;

public class LongAccumulator extends Striped64 implements Serializable {
   private static final long serialVersionUID = 7249069246863182397L;
   private final LongBinaryOperator function;
   private final long identity;

   public LongAccumulator(LongBinaryOperator var1, long var2) {
      this.function = var1;
      this.base = this.identity = var2;
   }

   public void accumulate(long var1) {
      Striped64.Cell[] var3;
      long var4;
      long var8;
      if ((var3 = this.cells) != null || (var8 = this.function.applyAsLong(var4 = this.base, var1)) != var4 && !this.casBase(var4, var8)) {
         boolean var12 = true;
         long var6;
         int var10;
         Striped64.Cell var11;
         if (var3 == null || (var10 = var3.length - 1) < 0 || (var11 = var3[getProbe() & var10]) == null || !(var12 = (var8 = this.function.applyAsLong(var6 = var11.value, var1)) == var6 || var11.cas(var6, var8))) {
            this.longAccumulate(var1, this.function, var12);
         }
      }

   }

   public long get() {
      Striped64.Cell[] var1 = this.cells;
      long var3 = this.base;
      if (var1 != null) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            Striped64.Cell var2;
            if ((var2 = var1[var5]) != null) {
               var3 = this.function.applyAsLong(var3, var2.value);
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

   public long getThenReset() {
      Striped64.Cell[] var1 = this.cells;
      long var3 = this.base;
      this.base = this.identity;
      if (var1 != null) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            Striped64.Cell var2;
            if ((var2 = var1[var5]) != null) {
               long var6 = var2.value;
               var2.value = this.identity;
               var3 = this.function.applyAsLong(var3, var6);
            }
         }
      }

      return var3;
   }

   public String toString() {
      return Long.toString(this.get());
   }

   public long longValue() {
      return this.get();
   }

   public int intValue() {
      return (int)this.get();
   }

   public float floatValue() {
      return (float)this.get();
   }

   public double doubleValue() {
      return (double)this.get();
   }

   private Object writeReplace() {
      return new LongAccumulator.SerializationProxy(this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 7249069246863182397L;
      private final long value;
      private final LongBinaryOperator function;
      private final long identity;

      SerializationProxy(LongAccumulator var1) {
         this.function = var1.function;
         this.identity = var1.identity;
         this.value = var1.get();
      }

      private Object readResolve() {
         LongAccumulator var1 = new LongAccumulator(this.function, this.identity);
         var1.base = this.value;
         return var1;
      }
   }
}
