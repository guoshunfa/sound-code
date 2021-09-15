package java.text.spi;

import java.text.DateFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DateFormatProvider extends LocaleServiceProvider {
   protected DateFormatProvider() {
   }

   public abstract DateFormat getTimeInstance(int var1, Locale var2);

   public abstract DateFormat getDateInstance(int var1, Locale var2);

   public abstract DateFormat getDateTimeInstance(int var1, int var2, Locale var3);
}
