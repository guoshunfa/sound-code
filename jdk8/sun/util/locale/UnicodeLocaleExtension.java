package sun.util.locale;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class UnicodeLocaleExtension extends Extension {
   public static final char SINGLETON = 'u';
   private final Set<String> attributes;
   private final Map<String, String> keywords;
   public static final UnicodeLocaleExtension CA_JAPANESE = new UnicodeLocaleExtension("ca", "japanese");
   public static final UnicodeLocaleExtension NU_THAI = new UnicodeLocaleExtension("nu", "thai");

   private UnicodeLocaleExtension(String var1, String var2) {
      super('u', var1 + "-" + var2);
      this.attributes = Collections.emptySet();
      this.keywords = Collections.singletonMap(var1, var2);
   }

   UnicodeLocaleExtension(SortedSet<String> var1, SortedMap<String, String> var2) {
      super('u');
      if (var1 != null) {
         this.attributes = var1;
      } else {
         this.attributes = Collections.emptySet();
      }

      if (var2 != null) {
         this.keywords = var2;
      } else {
         this.keywords = Collections.emptyMap();
      }

      if (!this.attributes.isEmpty() || !this.keywords.isEmpty()) {
         StringBuilder var3 = new StringBuilder();
         Iterator var4 = this.attributes.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            var3.append("-").append(var5);
         }

         var4 = this.keywords.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var8 = (Map.Entry)var4.next();
            String var6 = (String)var8.getKey();
            String var7 = (String)var8.getValue();
            var3.append("-").append(var6);
            if (var7.length() > 0) {
               var3.append("-").append(var7);
            }
         }

         this.setValue(var3.substring(1));
      }

   }

   public Set<String> getUnicodeLocaleAttributes() {
      return this.attributes == Collections.EMPTY_SET ? this.attributes : Collections.unmodifiableSet(this.attributes);
   }

   public Set<String> getUnicodeLocaleKeys() {
      return this.keywords == Collections.EMPTY_MAP ? Collections.emptySet() : Collections.unmodifiableSet(this.keywords.keySet());
   }

   public String getUnicodeLocaleType(String var1) {
      return (String)this.keywords.get(var1);
   }

   public static boolean isSingletonChar(char var0) {
      return 'u' == LocaleUtils.toLower(var0);
   }

   public static boolean isAttribute(String var0) {
      int var1 = var0.length();
      return var1 >= 3 && var1 <= 8 && LocaleUtils.isAlphaNumericString(var0);
   }

   public static boolean isKey(String var0) {
      return var0.length() == 2 && LocaleUtils.isAlphaNumericString(var0);
   }

   public static boolean isTypeSubtag(String var0) {
      int var1 = var0.length();
      return var1 >= 3 && var1 <= 8 && LocaleUtils.isAlphaNumericString(var0);
   }
}
