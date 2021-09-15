package sun.util.locale.provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.util.Calendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class DateFormatProviderImpl extends DateFormatProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public DateFormatProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }

   public DateFormat getTimeInstance(int var1, Locale var2) {
      return this.getInstance(-1, var1, var2);
   }

   public DateFormat getDateInstance(int var1, Locale var2) {
      return this.getInstance(var1, -1, var2);
   }

   public DateFormat getDateTimeInstance(int var1, int var2, Locale var3) {
      return this.getInstance(var1, var2, var3);
   }

   private DateFormat getInstance(int var1, int var2, Locale var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         SimpleDateFormat var4 = new SimpleDateFormat("", var3);
         Calendar var5 = var4.getCalendar();

         try {
            String var6 = LocaleProviderAdapter.forType(this.type).getLocaleResources(var3).getDateTimePattern(var2, var1, var5);
            var4.applyPattern(var6);
         } catch (MissingResourceException var7) {
            var4.applyPattern("M/d/yy h:mm a");
         }

         return var4;
      }
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
