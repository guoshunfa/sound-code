package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.spi.NumberFormatProvider;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class NumberFormat extends Format {
   public static final int INTEGER_FIELD = 0;
   public static final int FRACTION_FIELD = 1;
   private static final int NUMBERSTYLE = 0;
   private static final int CURRENCYSTYLE = 1;
   private static final int PERCENTSTYLE = 2;
   private static final int SCIENTIFICSTYLE = 3;
   private static final int INTEGERSTYLE = 4;
   private boolean groupingUsed = true;
   private byte maxIntegerDigits = 40;
   private byte minIntegerDigits = 1;
   private byte maxFractionDigits = 3;
   private byte minFractionDigits = 0;
   private boolean parseIntegerOnly = false;
   private int maximumIntegerDigits = 40;
   private int minimumIntegerDigits = 1;
   private int maximumFractionDigits = 3;
   private int minimumFractionDigits = 0;
   static final int currentSerialVersion = 1;
   private int serialVersionOnStream = 1;
   static final long serialVersionUID = -2308460125733713944L;

   protected NumberFormat() {
   }

   public StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3) {
      if (var1 instanceof Long || var1 instanceof Integer || var1 instanceof Short || var1 instanceof Byte || var1 instanceof AtomicInteger || var1 instanceof AtomicLong || var1 instanceof BigInteger && ((BigInteger)var1).bitLength() < 64) {
         return this.format(((Number)var1).longValue(), var2, var3);
      } else if (var1 instanceof Number) {
         return this.format(((Number)var1).doubleValue(), var2, var3);
      } else {
         throw new IllegalArgumentException("Cannot format given Object as a Number");
      }
   }

   public final Object parseObject(String var1, ParsePosition var2) {
      return this.parse(var1, var2);
   }

   public final String format(double var1) {
      String var3 = this.fastFormat(var1);
      return var3 != null ? var3 : this.format(var1, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString();
   }

   String fastFormat(double var1) {
      return null;
   }

   public final String format(long var1) {
      return this.format(var1, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString();
   }

   public abstract StringBuffer format(double var1, StringBuffer var3, FieldPosition var4);

   public abstract StringBuffer format(long var1, StringBuffer var3, FieldPosition var4);

   public abstract Number parse(String var1, ParsePosition var2);

   public Number parse(String var1) throws ParseException {
      ParsePosition var2 = new ParsePosition(0);
      Number var3 = this.parse(var1, var2);
      if (var2.index == 0) {
         throw new ParseException("Unparseable number: \"" + var1 + "\"", var2.errorIndex);
      } else {
         return var3;
      }
   }

   public boolean isParseIntegerOnly() {
      return this.parseIntegerOnly;
   }

   public void setParseIntegerOnly(boolean var1) {
      this.parseIntegerOnly = var1;
   }

   public static final NumberFormat getInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT), 0);
   }

   public static NumberFormat getInstance(Locale var0) {
      return getInstance(var0, 0);
   }

   public static final NumberFormat getNumberInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT), 0);
   }

   public static NumberFormat getNumberInstance(Locale var0) {
      return getInstance(var0, 0);
   }

   public static final NumberFormat getIntegerInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT), 4);
   }

   public static NumberFormat getIntegerInstance(Locale var0) {
      return getInstance(var0, 4);
   }

   public static final NumberFormat getCurrencyInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT), 1);
   }

   public static NumberFormat getCurrencyInstance(Locale var0) {
      return getInstance(var0, 1);
   }

   public static final NumberFormat getPercentInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT), 2);
   }

   public static NumberFormat getPercentInstance(Locale var0) {
      return getInstance(var0, 2);
   }

   static final NumberFormat getScientificInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT), 3);
   }

   static NumberFormat getScientificInstance(Locale var0) {
      return getInstance(var0, 3);
   }

   public static Locale[] getAvailableLocales() {
      LocaleServiceProviderPool var0 = LocaleServiceProviderPool.getPool(NumberFormatProvider.class);
      return var0.getAvailableLocales();
   }

   public int hashCode() {
      return this.maximumIntegerDigits * 37 + this.maxFractionDigits;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         NumberFormat var2 = (NumberFormat)var1;
         return this.maximumIntegerDigits == var2.maximumIntegerDigits && this.minimumIntegerDigits == var2.minimumIntegerDigits && this.maximumFractionDigits == var2.maximumFractionDigits && this.minimumFractionDigits == var2.minimumFractionDigits && this.groupingUsed == var2.groupingUsed && this.parseIntegerOnly == var2.parseIntegerOnly;
      }
   }

   public Object clone() {
      NumberFormat var1 = (NumberFormat)super.clone();
      return var1;
   }

   public boolean isGroupingUsed() {
      return this.groupingUsed;
   }

   public void setGroupingUsed(boolean var1) {
      this.groupingUsed = var1;
   }

   public int getMaximumIntegerDigits() {
      return this.maximumIntegerDigits;
   }

   public void setMaximumIntegerDigits(int var1) {
      this.maximumIntegerDigits = Math.max(0, var1);
      if (this.minimumIntegerDigits > this.maximumIntegerDigits) {
         this.minimumIntegerDigits = this.maximumIntegerDigits;
      }

   }

   public int getMinimumIntegerDigits() {
      return this.minimumIntegerDigits;
   }

   public void setMinimumIntegerDigits(int var1) {
      this.minimumIntegerDigits = Math.max(0, var1);
      if (this.minimumIntegerDigits > this.maximumIntegerDigits) {
         this.maximumIntegerDigits = this.minimumIntegerDigits;
      }

   }

   public int getMaximumFractionDigits() {
      return this.maximumFractionDigits;
   }

   public void setMaximumFractionDigits(int var1) {
      this.maximumFractionDigits = Math.max(0, var1);
      if (this.maximumFractionDigits < this.minimumFractionDigits) {
         this.minimumFractionDigits = this.maximumFractionDigits;
      }

   }

   public int getMinimumFractionDigits() {
      return this.minimumFractionDigits;
   }

   public void setMinimumFractionDigits(int var1) {
      this.minimumFractionDigits = Math.max(0, var1);
      if (this.maximumFractionDigits < this.minimumFractionDigits) {
         this.maximumFractionDigits = this.minimumFractionDigits;
      }

   }

   public Currency getCurrency() {
      throw new UnsupportedOperationException();
   }

   public void setCurrency(Currency var1) {
      throw new UnsupportedOperationException();
   }

   public RoundingMode getRoundingMode() {
      throw new UnsupportedOperationException();
   }

   public void setRoundingMode(RoundingMode var1) {
      throw new UnsupportedOperationException();
   }

   private static NumberFormat getInstance(Locale var0, int var1) {
      LocaleProviderAdapter var2 = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, var0);
      NumberFormat var3 = getInstance(var2, var0, var1);
      if (var3 == null) {
         var3 = getInstance(LocaleProviderAdapter.forJRE(), var0, var1);
      }

      return var3;
   }

   private static NumberFormat getInstance(LocaleProviderAdapter var0, Locale var1, int var2) {
      NumberFormatProvider var3 = var0.getNumberFormatProvider();
      NumberFormat var4 = null;
      switch(var2) {
      case 0:
         var4 = var3.getNumberInstance(var1);
         break;
      case 1:
         var4 = var3.getCurrencyInstance(var1);
         break;
      case 2:
         var4 = var3.getPercentInstance(var1);
      case 3:
      default:
         break;
      case 4:
         var4 = var3.getIntegerInstance(var1);
      }

      return var4;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.serialVersionOnStream < 1) {
         this.maximumIntegerDigits = this.maxIntegerDigits;
         this.minimumIntegerDigits = this.minIntegerDigits;
         this.maximumFractionDigits = this.maxFractionDigits;
         this.minimumFractionDigits = this.minFractionDigits;
      }

      if (this.minimumIntegerDigits <= this.maximumIntegerDigits && this.minimumFractionDigits <= this.maximumFractionDigits && this.minimumIntegerDigits >= 0 && this.minimumFractionDigits >= 0) {
         this.serialVersionOnStream = 1;
      } else {
         throw new InvalidObjectException("Digit count range invalid");
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.maxIntegerDigits = this.maximumIntegerDigits > 127 ? 127 : (byte)this.maximumIntegerDigits;
      this.minIntegerDigits = this.minimumIntegerDigits > 127 ? 127 : (byte)this.minimumIntegerDigits;
      this.maxFractionDigits = this.maximumFractionDigits > 127 ? 127 : (byte)this.maximumFractionDigits;
      this.minFractionDigits = this.minimumFractionDigits > 127 ? 127 : (byte)this.minimumFractionDigits;
      var1.defaultWriteObject();
   }

   public static class Field extends Format.Field {
      private static final long serialVersionUID = 7494728892700160890L;
      private static final Map<String, NumberFormat.Field> instanceMap = new HashMap(11);
      public static final NumberFormat.Field INTEGER = new NumberFormat.Field("integer");
      public static final NumberFormat.Field FRACTION = new NumberFormat.Field("fraction");
      public static final NumberFormat.Field EXPONENT = new NumberFormat.Field("exponent");
      public static final NumberFormat.Field DECIMAL_SEPARATOR = new NumberFormat.Field("decimal separator");
      public static final NumberFormat.Field SIGN = new NumberFormat.Field("sign");
      public static final NumberFormat.Field GROUPING_SEPARATOR = new NumberFormat.Field("grouping separator");
      public static final NumberFormat.Field EXPONENT_SYMBOL = new NumberFormat.Field("exponent symbol");
      public static final NumberFormat.Field PERCENT = new NumberFormat.Field("percent");
      public static final NumberFormat.Field PERMILLE = new NumberFormat.Field("per mille");
      public static final NumberFormat.Field CURRENCY = new NumberFormat.Field("currency");
      public static final NumberFormat.Field EXPONENT_SIGN = new NumberFormat.Field("exponent sign");

      protected Field(String var1) {
         super(var1);
         if (this.getClass() == NumberFormat.Field.class) {
            instanceMap.put(var1, this);
         }

      }

      protected Object readResolve() throws InvalidObjectException {
         if (this.getClass() != NumberFormat.Field.class) {
            throw new InvalidObjectException("subclass didn't correctly implement readResolve");
         } else {
            Object var1 = instanceMap.get(this.getName());
            if (var1 != null) {
               return var1;
            } else {
               throw new InvalidObjectException("unknown attribute name");
            }
         }
      }
   }
}
