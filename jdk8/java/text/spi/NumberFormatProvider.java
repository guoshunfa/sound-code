package java.text.spi;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class NumberFormatProvider extends LocaleServiceProvider {
   protected NumberFormatProvider() {
   }

   public abstract NumberFormat getCurrencyInstance(Locale var1);

   public abstract NumberFormat getIntegerInstance(Locale var1);

   public abstract NumberFormat getNumberInstance(Locale var1);

   public abstract NumberFormat getPercentInstance(Locale var1);
}
