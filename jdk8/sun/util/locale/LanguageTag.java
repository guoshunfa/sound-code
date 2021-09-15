package sun.util.locale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LanguageTag {
   public static final String SEP = "-";
   public static final String PRIVATEUSE = "x";
   public static final String UNDETERMINED = "und";
   public static final String PRIVUSE_VARIANT_PREFIX = "lvariant";
   private String language = "";
   private String script = "";
   private String region = "";
   private String privateuse = "";
   private List<String> extlangs = Collections.emptyList();
   private List<String> variants = Collections.emptyList();
   private List<String> extensions = Collections.emptyList();
   private static final Map<String, String[]> GRANDFATHERED = new HashMap();

   private LanguageTag() {
   }

   public static LanguageTag parse(String var0, ParseStatus var1) {
      if (var1 == null) {
         var1 = new ParseStatus();
      } else {
         var1.reset();
      }

      String[] var3 = (String[])GRANDFATHERED.get(LocaleUtils.toLowerString(var0));
      StringTokenIterator var2;
      if (var3 != null) {
         var2 = new StringTokenIterator(var3[1], "-");
      } else {
         var2 = new StringTokenIterator(var0, "-");
      }

      LanguageTag var4 = new LanguageTag();
      if (var4.parseLanguage(var2, var1)) {
         var4.parseExtlangs(var2, var1);
         var4.parseScript(var2, var1);
         var4.parseRegion(var2, var1);
         var4.parseVariants(var2, var1);
         var4.parseExtensions(var2, var1);
      }

      var4.parsePrivateuse(var2, var1);
      if (!var2.isDone() && !var1.isError()) {
         String var5 = var2.current();
         var1.errorIndex = var2.currentStart();
         if (var5.length() == 0) {
            var1.errorMsg = "Empty subtag";
         } else {
            var1.errorMsg = "Invalid subtag: " + var5;
         }
      }

      return var4;
   }

   private boolean parseLanguage(StringTokenIterator var1, ParseStatus var2) {
      if (!var1.isDone() && !var2.isError()) {
         boolean var3 = false;
         String var4 = var1.current();
         if (isLanguage(var4)) {
            var3 = true;
            this.language = var4;
            var2.parseLength = var1.currentEnd();
            var1.next();
         }

         return var3;
      } else {
         return false;
      }
   }

   private boolean parseExtlangs(StringTokenIterator var1, ParseStatus var2) {
      if (!var1.isDone() && !var2.isError()) {
         boolean var3 = false;

         while(!var1.isDone()) {
            String var4 = var1.current();
            if (!isExtlang(var4)) {
               break;
            }

            var3 = true;
            if (this.extlangs.isEmpty()) {
               this.extlangs = new ArrayList(3);
            }

            this.extlangs.add(var4);
            var2.parseLength = var1.currentEnd();
            var1.next();
            if (this.extlangs.size() == 3) {
               break;
            }
         }

         return var3;
      } else {
         return false;
      }
   }

   private boolean parseScript(StringTokenIterator var1, ParseStatus var2) {
      if (!var1.isDone() && !var2.isError()) {
         boolean var3 = false;
         String var4 = var1.current();
         if (isScript(var4)) {
            var3 = true;
            this.script = var4;
            var2.parseLength = var1.currentEnd();
            var1.next();
         }

         return var3;
      } else {
         return false;
      }
   }

   private boolean parseRegion(StringTokenIterator var1, ParseStatus var2) {
      if (!var1.isDone() && !var2.isError()) {
         boolean var3 = false;
         String var4 = var1.current();
         if (isRegion(var4)) {
            var3 = true;
            this.region = var4;
            var2.parseLength = var1.currentEnd();
            var1.next();
         }

         return var3;
      } else {
         return false;
      }
   }

   private boolean parseVariants(StringTokenIterator var1, ParseStatus var2) {
      if (!var1.isDone() && !var2.isError()) {
         boolean var3 = false;

         while(!var1.isDone()) {
            String var4 = var1.current();
            if (!isVariant(var4)) {
               break;
            }

            var3 = true;
            if (this.variants.isEmpty()) {
               this.variants = new ArrayList(3);
            }

            this.variants.add(var4);
            var2.parseLength = var1.currentEnd();
            var1.next();
         }

         return var3;
      } else {
         return false;
      }
   }

   private boolean parseExtensions(StringTokenIterator var1, ParseStatus var2) {
      if (!var1.isDone() && !var2.isError()) {
         boolean var3;
         for(var3 = false; !var1.isDone(); var3 = true) {
            String var4 = var1.current();
            if (!isExtensionSingleton(var4)) {
               break;
            }

            int var5 = var1.currentStart();
            StringBuilder var7 = new StringBuilder(var4);
            var1.next();

            while(!var1.isDone()) {
               var4 = var1.current();
               if (!isExtensionSubtag(var4)) {
                  break;
               }

               var7.append("-").append(var4);
               var2.parseLength = var1.currentEnd();
               var1.next();
            }

            if (var2.parseLength <= var5) {
               var2.errorIndex = var5;
               var2.errorMsg = "Incomplete extension '" + var4 + "'";
               break;
            }

            if (this.extensions.isEmpty()) {
               this.extensions = new ArrayList(4);
            }

            this.extensions.add(var7.toString());
         }

         return var3;
      } else {
         return false;
      }
   }

   private boolean parsePrivateuse(StringTokenIterator var1, ParseStatus var2) {
      if (!var1.isDone() && !var2.isError()) {
         boolean var3 = false;
         String var4 = var1.current();
         if (isPrivateusePrefix(var4)) {
            int var5 = var1.currentStart();
            StringBuilder var6 = new StringBuilder(var4);
            var1.next();

            while(!var1.isDone()) {
               var4 = var1.current();
               if (!isPrivateuseSubtag(var4)) {
                  break;
               }

               var6.append("-").append(var4);
               var2.parseLength = var1.currentEnd();
               var1.next();
            }

            if (var2.parseLength <= var5) {
               var2.errorIndex = var5;
               var2.errorMsg = "Incomplete privateuse";
            } else {
               this.privateuse = var6.toString();
               var3 = true;
            }
         }

         return var3;
      } else {
         return false;
      }
   }

   public static LanguageTag parseLocale(BaseLocale var0, LocaleExtensions var1) {
      LanguageTag var2 = new LanguageTag();
      String var3 = var0.getLanguage();
      String var4 = var0.getScript();
      String var5 = var0.getRegion();
      String var6 = var0.getVariant();
      boolean var7 = false;
      String var8 = null;
      if (isLanguage(var3)) {
         if (var3.equals("iw")) {
            var3 = "he";
         } else if (var3.equals("ji")) {
            var3 = "yi";
         } else if (var3.equals("in")) {
            var3 = "id";
         }

         var2.language = var3;
      }

      if (isScript(var4)) {
         var2.script = canonicalizeScript(var4);
         var7 = true;
      }

      if (isRegion(var5)) {
         var2.region = canonicalizeRegion(var5);
         var7 = true;
      }

      if (var2.language.equals("no") && var2.region.equals("NO") && var6.equals("NY")) {
         var2.language = "nn";
         var6 = "";
      }

      ArrayList var9;
      if (var6.length() > 0) {
         var9 = null;
         StringTokenIterator var10 = new StringTokenIterator(var6, "_");

         while(!var10.isDone()) {
            String var11 = var10.current();
            if (!isVariant(var11)) {
               break;
            }

            if (var9 == null) {
               var9 = new ArrayList();
            }

            var9.add(var11);
            var10.next();
         }

         if (var9 != null) {
            var2.variants = var9;
            var7 = true;
         }

         if (!var10.isDone()) {
            StringBuilder var16 = new StringBuilder();

            while(!var10.isDone()) {
               String var12 = var10.current();
               if (!isPrivateuseSubtag(var12)) {
                  break;
               }

               if (var16.length() > 0) {
                  var16.append("-");
               }

               var16.append(var12);
               var10.next();
            }

            if (var16.length() > 0) {
               var8 = var16.toString();
            }
         }
      }

      var9 = null;
      String var15 = null;
      if (var1 != null) {
         Set var17 = var1.getKeys();
         Iterator var18 = var17.iterator();

         while(var18.hasNext()) {
            Character var13 = (Character)var18.next();
            Extension var14 = var1.getExtension(var13);
            if (isPrivateusePrefixChar(var13)) {
               var15 = var14.getValue();
            } else {
               if (var9 == null) {
                  var9 = new ArrayList();
               }

               var9.add(var13.toString() + "-" + var14.getValue());
            }
         }
      }

      if (var9 != null) {
         var2.extensions = var9;
         var7 = true;
      }

      if (var8 != null) {
         if (var15 == null) {
            var15 = "lvariant-" + var8;
         } else {
            var15 = var15 + "-" + "lvariant" + "-" + var8.replace("_", "-");
         }
      }

      if (var15 != null) {
         var2.privateuse = var15;
      }

      if (var2.language.length() == 0 && (var7 || var15 == null)) {
         var2.language = "und";
      }

      return var2;
   }

   public String getLanguage() {
      return this.language;
   }

   public List<String> getExtlangs() {
      return this.extlangs.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.extlangs);
   }

   public String getScript() {
      return this.script;
   }

   public String getRegion() {
      return this.region;
   }

   public List<String> getVariants() {
      return this.variants.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.variants);
   }

   public List<String> getExtensions() {
      return this.extensions.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.extensions);
   }

   public String getPrivateuse() {
      return this.privateuse;
   }

   public static boolean isLanguage(String var0) {
      int var1 = var0.length();
      return var1 >= 2 && var1 <= 8 && LocaleUtils.isAlphaString(var0);
   }

   public static boolean isExtlang(String var0) {
      return var0.length() == 3 && LocaleUtils.isAlphaString(var0);
   }

   public static boolean isScript(String var0) {
      return var0.length() == 4 && LocaleUtils.isAlphaString(var0);
   }

   public static boolean isRegion(String var0) {
      return var0.length() == 2 && LocaleUtils.isAlphaString(var0) || var0.length() == 3 && LocaleUtils.isNumericString(var0);
   }

   public static boolean isVariant(String var0) {
      int var1 = var0.length();
      if (var1 >= 5 && var1 <= 8) {
         return LocaleUtils.isAlphaNumericString(var0);
      } else if (var1 != 4) {
         return false;
      } else {
         return LocaleUtils.isNumeric(var0.charAt(0)) && LocaleUtils.isAlphaNumeric(var0.charAt(1)) && LocaleUtils.isAlphaNumeric(var0.charAt(2)) && LocaleUtils.isAlphaNumeric(var0.charAt(3));
      }
   }

   public static boolean isExtensionSingleton(String var0) {
      return var0.length() == 1 && LocaleUtils.isAlphaString(var0) && !LocaleUtils.caseIgnoreMatch("x", var0);
   }

   public static boolean isExtensionSingletonChar(char var0) {
      return isExtensionSingleton(String.valueOf(var0));
   }

   public static boolean isExtensionSubtag(String var0) {
      int var1 = var0.length();
      return var1 >= 2 && var1 <= 8 && LocaleUtils.isAlphaNumericString(var0);
   }

   public static boolean isPrivateusePrefix(String var0) {
      return var0.length() == 1 && LocaleUtils.caseIgnoreMatch("x", var0);
   }

   public static boolean isPrivateusePrefixChar(char var0) {
      return LocaleUtils.caseIgnoreMatch("x", String.valueOf(var0));
   }

   public static boolean isPrivateuseSubtag(String var0) {
      int var1 = var0.length();
      return var1 >= 1 && var1 <= 8 && LocaleUtils.isAlphaNumericString(var0);
   }

   public static String canonicalizeLanguage(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public static String canonicalizeExtlang(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public static String canonicalizeScript(String var0) {
      return LocaleUtils.toTitleString(var0);
   }

   public static String canonicalizeRegion(String var0) {
      return LocaleUtils.toUpperString(var0);
   }

   public static String canonicalizeVariant(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public static String canonicalizeExtension(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public static String canonicalizeExtensionSingleton(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public static String canonicalizeExtensionSubtag(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public static String canonicalizePrivateuse(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public static String canonicalizePrivateuseSubtag(String var0) {
      return LocaleUtils.toLowerString(var0);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.language.length() > 0) {
         var1.append(this.language);
         Iterator var2 = this.extlangs.iterator();

         String var3;
         while(var2.hasNext()) {
            var3 = (String)var2.next();
            var1.append("-").append(var3);
         }

         if (this.script.length() > 0) {
            var1.append("-").append(this.script);
         }

         if (this.region.length() > 0) {
            var1.append("-").append(this.region);
         }

         var2 = this.variants.iterator();

         while(var2.hasNext()) {
            var3 = (String)var2.next();
            var1.append("-").append(var3);
         }

         var2 = this.extensions.iterator();

         while(var2.hasNext()) {
            var3 = (String)var2.next();
            var1.append("-").append(var3);
         }
      }

      if (this.privateuse.length() > 0) {
         if (var1.length() > 0) {
            var1.append("-");
         }

         var1.append(this.privateuse);
      }

      return var1.toString();
   }

   static {
      String[][] var0 = new String[][]{{"art-lojban", "jbo"}, {"cel-gaulish", "xtg-x-cel-gaulish"}, {"en-GB-oed", "en-GB-x-oed"}, {"i-ami", "ami"}, {"i-bnn", "bnn"}, {"i-default", "en-x-i-default"}, {"i-enochian", "und-x-i-enochian"}, {"i-hak", "hak"}, {"i-klingon", "tlh"}, {"i-lux", "lb"}, {"i-mingo", "see-x-i-mingo"}, {"i-navajo", "nv"}, {"i-pwn", "pwn"}, {"i-tao", "tao"}, {"i-tay", "tay"}, {"i-tsu", "tsu"}, {"no-bok", "nb"}, {"no-nyn", "nn"}, {"sgn-BE-FR", "sfb"}, {"sgn-BE-NL", "vgt"}, {"sgn-CH-DE", "sgg"}, {"zh-guoyu", "cmn"}, {"zh-hakka", "hak"}, {"zh-min", "nan-x-zh-min"}, {"zh-min-nan", "nan"}, {"zh-xiang", "hsn"}};
      String[][] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String[] var4 = var1[var3];
         GRANDFATHERED.put(LocaleUtils.toLowerString(var4[0]), var4);
      }

   }
}
