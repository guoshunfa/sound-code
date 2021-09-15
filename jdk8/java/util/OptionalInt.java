package java.util;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class OptionalInt {
   private static final OptionalInt EMPTY = new OptionalInt();
   private final boolean isPresent;
   private final int value;

   private OptionalInt() {
      this.isPresent = false;
      this.value = 0;
   }

   public static OptionalInt empty() {
      return EMPTY;
   }

   private OptionalInt(int var1) {
      this.isPresent = true;
      this.value = var1;
   }

   public static OptionalInt of(int var0) {
      return new OptionalInt(var0);
   }

   public int getAsInt() {
      if (!this.isPresent) {
         throw new NoSuchElementException("No value present");
      } else {
         return this.value;
      }
   }

   public boolean isPresent() {
      return this.isPresent;
   }

   public void ifPresent(IntConsumer var1) {
      if (this.isPresent) {
         var1.accept(this.value);
      }

   }

   public int orElse(int var1) {
      return this.isPresent ? this.value : var1;
   }

   public int orElseGet(IntSupplier var1) {
      return this.isPresent ? this.value : var1.getAsInt();
   }

   public <X extends Throwable> int orElseThrow(Supplier<X> var1) throws X {
      if (this.isPresent) {
         return this.value;
      } else {
         throw (Throwable)var1.get();
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OptionalInt)) {
         return false;
      } else {
         OptionalInt var2 = (OptionalInt)var1;
         return this.isPresent && var2.isPresent ? this.value == var2.value : this.isPresent == var2.isPresent;
      }
   }

   public int hashCode() {
      return this.isPresent ? Integer.hashCode(this.value) : 0;
   }

   public String toString() {
      return this.isPresent ? String.format("OptionalInt[%s]", this.value) : "OptionalInt.empty";
   }
}
