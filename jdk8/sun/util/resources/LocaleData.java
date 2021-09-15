package sun.util.resources;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import sun.util.locale.provider.JRELocaleProviderAdapter;
import sun.util.locale.provider.LocaleProviderAdapter;

public class LocaleData {
   private final LocaleProviderAdapter.Type type;

   public LocaleData(LocaleProviderAdapter.Type var1) {
      this.type = var1;
   }

   public ResourceBundle getCalendarData(Locale var1) {
      return getBundle(this.type.getUtilResourcesPackage() + ".CalendarData", var1);
   }

   public OpenListResourceBundle getCurrencyNames(Locale var1) {
      return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".CurrencyNames", var1);
   }

   public OpenListResourceBundle getLocaleNames(Locale var1) {
      return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".LocaleNames", var1);
   }

   public TimeZoneNamesBundle getTimeZoneNames(Locale var1) {
      return (TimeZoneNamesBundle)getBundle(this.type.getUtilResourcesPackage() + ".TimeZoneNames", var1);
   }

   public ResourceBundle getBreakIteratorInfo(Locale var1) {
      return getBundle(this.type.getTextResourcesPackage() + ".BreakIteratorInfo", var1);
   }

   public ResourceBundle getCollationData(Locale var1) {
      return getBundle(this.type.getTextResourcesPackage() + ".CollationData", var1);
   }

   public ResourceBundle getDateFormatData(Locale var1) {
      return getBundle(this.type.getTextResourcesPackage() + ".FormatData", var1);
   }

   public void setSupplementary(ParallelListResourceBundle var1) {
      if (!var1.areParallelContentsComplete()) {
         String var2 = this.type.getTextResourcesPackage() + ".JavaTimeSupplementary";
         this.setSupplementary(var2, var1);
      }

   }

   private boolean setSupplementary(String var1, ParallelListResourceBundle var2) {
      ParallelListResourceBundle var3 = (ParallelListResourceBundle)var2.getParent();
      boolean var4 = false;
      if (var3 != null) {
         var4 = this.setSupplementary(var1, var3);
      }

      OpenListResourceBundle var5 = getSupplementary(var1, var2.getLocale());
      var2.setParallelContents(var5);
      var4 |= var5 != null;
      if (var4) {
         var2.resetKeySet();
      }

      return var4;
   }

   public ResourceBundle getNumberFormatData(Locale var1) {
      return getBundle(this.type.getTextResourcesPackage() + ".FormatData", var1);
   }

   public static ResourceBundle getBundle(final String var0, final Locale var1) {
      return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
         public ResourceBundle run() {
            return ResourceBundle.getBundle(var0, var1, (ResourceBundle.Control)LocaleData.LocaleDataResourceBundleControl.INSTANCE);
         }
      });
   }

   private static OpenListResourceBundle getSupplementary(final String var0, final Locale var1) {
      return (OpenListResourceBundle)AccessController.doPrivileged(new PrivilegedAction<OpenListResourceBundle>() {
         public OpenListResourceBundle run() {
            OpenListResourceBundle var1x = null;

            try {
               var1x = (OpenListResourceBundle)ResourceBundle.getBundle(var0, var1, (ResourceBundle.Control)LocaleData.SupplementaryResourceBundleControl.INSTANCE);
            } catch (MissingResourceException var3) {
            }

            return var1x;
         }
      });
   }

   private static class SupplementaryResourceBundleControl extends LocaleData.LocaleDataResourceBundleControl {
      private static final LocaleData.SupplementaryResourceBundleControl INSTANCE = new LocaleData.SupplementaryResourceBundleControl();

      private SupplementaryResourceBundleControl() {
         super(null);
      }

      public List<Locale> getCandidateLocales(String var1, Locale var2) {
         return Arrays.asList(var2);
      }

      public long getTimeToLive(String var1, Locale var2) {
         assert var1.contains("JavaTimeSupplementary");

         return -1L;
      }
   }

   private static class LocaleDataResourceBundleControl extends ResourceBundle.Control {
      private static final LocaleData.LocaleDataResourceBundleControl INSTANCE = new LocaleData.LocaleDataResourceBundleControl();
      private static final String DOTCLDR = ".cldr";

      private LocaleDataResourceBundleControl() {
      }

      public List<Locale> getCandidateLocales(String var1, Locale var2) {
         List var3 = super.getCandidateLocales(var1, var2);
         int var4 = var1.lastIndexOf(46);
         String var5 = var4 >= 0 ? var1.substring(var4 + 1) : var1;
         LocaleProviderAdapter.Type var6 = var1.contains(".cldr") ? LocaleProviderAdapter.Type.CLDR : LocaleProviderAdapter.Type.JRE;
         LocaleProviderAdapter var7 = LocaleProviderAdapter.forType(var6);
         Set var8 = ((JRELocaleProviderAdapter)var7).getLanguageTagSet(var5);
         if (!var8.isEmpty()) {
            Iterator var9 = var3.iterator();

            while(var9.hasNext()) {
               if (!LocaleProviderAdapter.isSupportedLocale((Locale)var9.next(), var6, var8)) {
                  var9.remove();
               }
            }
         }

         if (var2.getLanguage() != "en" && var6 == LocaleProviderAdapter.Type.CLDR && var5.equals("TimeZoneNames")) {
            var3.add(var3.size() - 1, Locale.ENGLISH);
         }

         return var3;
      }

      public Locale getFallbackLocale(String var1, Locale var2) {
         if (var1 != null && var2 != null) {
            return null;
         } else {
            throw new NullPointerException();
         }
      }

      public String toBundleName(String var1, Locale var2) {
         String var3 = var1;
         String var4 = var2.getLanguage();
         if (var4.length() > 0 && (var1.startsWith(LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage()) || var1.startsWith(LocaleProviderAdapter.Type.JRE.getTextResourcesPackage()))) {
            assert LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length() == LocaleProviderAdapter.Type.JRE.getTextResourcesPackage().length();

            int var5 = LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length();
            if (var1.indexOf(".cldr", var5) > 0) {
               var5 += ".cldr".length();
            }

            var3 = var1.substring(0, var5 + 1) + var4 + var1.substring(var5);
         }

         return super.toBundleName(var3, var2);
      }

      // $FF: synthetic method
      LocaleDataResourceBundleControl(Object var1) {
         this();
      }
   }
}
