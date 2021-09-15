package java.util;

public final class StringJoiner {
   private final String prefix;
   private final String delimiter;
   private final String suffix;
   private StringBuilder value;
   private String emptyValue;

   public StringJoiner(CharSequence var1) {
      this(var1, "", "");
   }

   public StringJoiner(CharSequence var1, CharSequence var2, CharSequence var3) {
      Objects.requireNonNull(var2, (String)"The prefix must not be null");
      Objects.requireNonNull(var1, (String)"The delimiter must not be null");
      Objects.requireNonNull(var3, (String)"The suffix must not be null");
      this.prefix = var2.toString();
      this.delimiter = var1.toString();
      this.suffix = var3.toString();
      this.emptyValue = this.prefix + this.suffix;
   }

   public StringJoiner setEmptyValue(CharSequence var1) {
      this.emptyValue = ((CharSequence)Objects.requireNonNull(var1, (String)"The empty value must not be null")).toString();
      return this;
   }

   public String toString() {
      if (this.value == null) {
         return this.emptyValue;
      } else if (this.suffix.equals("")) {
         return this.value.toString();
      } else {
         int var1 = this.value.length();
         String var2 = this.value.append(this.suffix).toString();
         this.value.setLength(var1);
         return var2;
      }
   }

   public StringJoiner add(CharSequence var1) {
      this.prepareBuilder().append(var1);
      return this;
   }

   public StringJoiner merge(StringJoiner var1) {
      Objects.requireNonNull(var1);
      if (var1.value != null) {
         int var2 = var1.value.length();
         StringBuilder var3 = this.prepareBuilder();
         var3.append((CharSequence)var1.value, var1.prefix.length(), var2);
      }

      return this;
   }

   private StringBuilder prepareBuilder() {
      if (this.value != null) {
         this.value.append(this.delimiter);
      } else {
         this.value = (new StringBuilder()).append(this.prefix);
      }

      return this.value;
   }

   public int length() {
      return this.value != null ? this.value.length() + this.suffix.length() : this.emptyValue.length();
   }
}
