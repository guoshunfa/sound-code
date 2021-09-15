package java.time.temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;

public final class ValueRange implements Serializable {
   private static final long serialVersionUID = -7317881728594519368L;
   private final long minSmallest;
   private final long minLargest;
   private final long maxSmallest;
   private final long maxLargest;

   public static ValueRange of(long var0, long var2) {
      if (var0 > var2) {
         throw new IllegalArgumentException("Minimum value must be less than maximum value");
      } else {
         return new ValueRange(var0, var0, var2, var2);
      }
   }

   public static ValueRange of(long var0, long var2, long var4) {
      return of(var0, var0, var2, var4);
   }

   public static ValueRange of(long var0, long var2, long var4, long var6) {
      if (var0 > var2) {
         throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value");
      } else if (var4 > var6) {
         throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
      } else if (var2 > var6) {
         throw new IllegalArgumentException("Minimum value must be less than maximum value");
      } else {
         return new ValueRange(var0, var2, var4, var6);
      }
   }

   private ValueRange(long var1, long var3, long var5, long var7) {
      this.minSmallest = var1;
      this.minLargest = var3;
      this.maxSmallest = var5;
      this.maxLargest = var7;
   }

   public boolean isFixed() {
      return this.minSmallest == this.minLargest && this.maxSmallest == this.maxLargest;
   }

   public long getMinimum() {
      return this.minSmallest;
   }

   public long getLargestMinimum() {
      return this.minLargest;
   }

   public long getSmallestMaximum() {
      return this.maxSmallest;
   }

   public long getMaximum() {
      return this.maxLargest;
   }

   public boolean isIntValue() {
      return this.getMinimum() >= -2147483648L && this.getMaximum() <= 2147483647L;
   }

   public boolean isValidValue(long var1) {
      return var1 >= this.getMinimum() && var1 <= this.getMaximum();
   }

   public boolean isValidIntValue(long var1) {
      return this.isIntValue() && this.isValidValue(var1);
   }

   public long checkValidValue(long var1, TemporalField var3) {
      if (!this.isValidValue(var1)) {
         throw new DateTimeException(this.genInvalidFieldMessage(var3, var1));
      } else {
         return var1;
      }
   }

   public int checkValidIntValue(long var1, TemporalField var3) {
      if (!this.isValidIntValue(var1)) {
         throw new DateTimeException(this.genInvalidFieldMessage(var3, var1));
      } else {
         return (int)var1;
      }
   }

   private String genInvalidFieldMessage(TemporalField var1, long var2) {
      return var1 != null ? "Invalid value for " + var1 + " (valid values " + this + "): " + var2 : "Invalid value (valid values " + this + "): " + var2;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException, InvalidObjectException {
      var1.defaultReadObject();
      if (this.minSmallest > this.minLargest) {
         throw new InvalidObjectException("Smallest minimum value must be less than largest minimum value");
      } else if (this.maxSmallest > this.maxLargest) {
         throw new InvalidObjectException("Smallest maximum value must be less than largest maximum value");
      } else if (this.minLargest > this.maxLargest) {
         throw new InvalidObjectException("Minimum value must be less than maximum value");
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ValueRange)) {
         return false;
      } else {
         ValueRange var2 = (ValueRange)var1;
         return this.minSmallest == var2.minSmallest && this.minLargest == var2.minLargest && this.maxSmallest == var2.maxSmallest && this.maxLargest == var2.maxLargest;
      }
   }

   public int hashCode() {
      long var1 = this.minSmallest + (this.minLargest << 16) + (this.minLargest >> 48) + (this.maxSmallest << 32) + (this.maxSmallest >> 32) + (this.maxLargest << 48) + (this.maxLargest >> 16);
      return (int)(var1 ^ var1 >>> 32);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.minSmallest);
      if (this.minSmallest != this.minLargest) {
         var1.append('/').append(this.minLargest);
      }

      var1.append(" - ").append(this.maxSmallest);
      if (this.maxSmallest != this.maxLargest) {
         var1.append('/').append(this.maxLargest);
      }

      return var1.toString();
   }
}
