package java.util;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class OptionalDouble {
   private static final OptionalDouble EMPTY = new OptionalDouble();
   private final boolean isPresent;
   private final double value;

   private OptionalDouble() {
      this.isPresent = false;
      this.value = Double.NaN;
   }

   public static OptionalDouble empty() {
      return EMPTY;
   }

   private OptionalDouble(double var1) {
      this.isPresent = true;
      this.value = var1;
   }

   public static OptionalDouble of(double var0) {
      return new OptionalDouble(var0);
   }

   public double getAsDouble() {
      if (!this.isPresent) {
         throw new NoSuchElementException("No value present");
      } else {
         return this.value;
      }
   }

   public boolean isPresent() {
      return this.isPresent;
   }

   public void ifPresent(DoubleConsumer var1) {
      if (this.isPresent) {
         var1.accept(this.value);
      }

   }

   public double orElse(double var1) {
      return this.isPresent ? this.value : var1;
   }

   public double orElseGet(DoubleSupplier var1) {
      return this.isPresent ? this.value : var1.getAsDouble();
   }

   public <X extends Throwable> double orElseThrow(Supplier<X> var1) throws X {
      if (this.isPresent) {
         return this.value;
      } else {
         throw (Throwable)var1.get();
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OptionalDouble)) {
         return false;
      } else {
         OptionalDouble var2 = (OptionalDouble)var1;
         return this.isPresent && var2.isPresent ? Double.compare(this.value, var2.value) == 0 : this.isPresent == var2.isPresent;
      }
   }

   public int hashCode() {
      return this.isPresent ? Double.hashCode(this.value) : 0;
   }

   public String toString() {
      return this.isPresent ? String.format("OptionalDouble[%s]", this.value) : "OptionalDouble.empty";
   }
}
