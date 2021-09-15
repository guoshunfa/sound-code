package sun.util.cldr;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;
import sun.util.locale.provider.JRELocaleProviderAdapter;
import sun.util.locale.provider.LocaleProviderAdapter;

public class CLDRLocaleProviderAdapter extends JRELocaleProviderAdapter {
   private static final String LOCALE_DATA_JAR_NAME = "cldrdata.jar";

   public CLDRLocaleProviderAdapter() {
      String var1 = File.separator;
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.home"))) + var1 + "lib" + var1 + "ext" + var1 + "cldrdata.jar";
      final File var3 = new File(var2);
      boolean var4 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return var3.exists();
         }
      });
      if (!var4) {
         throw new UnsupportedOperationException();
      }
   }

   public LocaleProviderAdapter.Type getAdapterType() {
      return LocaleProviderAdapter.Type.CLDR;
   }

   public BreakIteratorProvider getBreakIteratorProvider() {
      return null;
   }

   public CollatorProvider getCollatorProvider() {
      return null;
   }

   public Locale[] getAvailableLocales() {
      Set var1 = this.createLanguageTagSet("All");
      Locale[] var2 = new Locale[var1.size()];
      int var3 = 0;

      String var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = Locale.forLanguageTag(var5)) {
         var5 = (String)var4.next();
      }

      return var2;
   }

   protected Set<String> createLanguageTagSet(String var1) {
      ResourceBundle var2 = ResourceBundle.getBundle("sun.util.cldr.CLDRLocaleDataMetaInfo", Locale.ROOT);
      String var3 = var2.getString(var1);
      if (var3 == null) {
         return Collections.emptySet();
      } else {
         HashSet var4 = new HashSet();
         StringTokenizer var5 = new StringTokenizer(var3);

         while(var5.hasMoreTokens()) {
            var4.add(var5.nextToken());
         }

         return var4;
      }
   }
}
