package java.text.spi;

import java.text.Collator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class CollatorProvider extends LocaleServiceProvider {
   protected CollatorProvider() {
   }

   public abstract Collator getInstance(Locale var1);
}
