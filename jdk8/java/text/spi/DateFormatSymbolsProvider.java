package java.text.spi;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DateFormatSymbolsProvider extends LocaleServiceProvider {
   protected DateFormatSymbolsProvider() {
   }

   public abstract DateFormatSymbols getInstance(Locale var1);
}
