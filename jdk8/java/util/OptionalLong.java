package java.util;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class OptionalLong {
   private static final OptionalLong EMPTY = new OptionalLong();
   private final boolean isPresent;
   private final long value;

   private OptionalLong() {
      this.isPresent = false;
      this.value = 0L;
   }

   public static OptionalLong empty() {
      return EMPTY;
   }

   private OptionalLong(long var1) {
      this.isPresent = true;
      this.value = var1;
   }

   public static OptionalLong of(long var0) {
      return new OptionalLong(var0);
   }

   public long getAsLong() {
      if (!this.isPresent) {
         throw new NoSuchElementException("No value present");
      } else {
         return this.value;
      }
   }

   public boolean isPresent() {
      return this.isPresent;
   }

   public void ifPresent(LongConsumer var1) {
      if (this.isPresent) {
         var1.accept(this.value);
      }

   }

   public long orElse(long var1) {
      return this.isPresent ? this.value : var1;
   }

   public long orElseGet(LongSupplier var1) {
      return this.isPresent ? this.value : var1.getAsLong();
   }

   public <X extends Throwable> long orElseThrow(Supplier<X> var1) throws X {
      if (this.isPresent) {
         return this.value;
      } else {
         throw (Throwable)var1.get();
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OptionalLong)) {
         return false;
      } else {
         OptionalLong var2 = (OptionalLong)var1;
         return this.isPresent && var2.isPresent ? this.value == var2.value : this.isPresent == var2.isPresent;
      }
   }

   public int hashCode() {
      return this.isPresent ? Long.hashCode(this.value) : 0;
   }

   public String toString() {
      return this.isPresent ? String.format("OptionalLong[%s]", this.value) : "OptionalLong.empty";
   }
}
