package sun.util.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InternalLocaleBuilder {
   private static final InternalLocaleBuilder.CaseInsensitiveChar PRIVATEUSE_KEY = new InternalLocaleBuilder.CaseInsensitiveChar("x");
   private String language = "";
   private String script = "";
   private String region = "";
   private String variant = "";
   private Map<InternalLocaleBuilder.CaseInsensitiveChar, String> extensions;
   private Set<InternalLocaleBuilder.CaseInsensitiveString> uattributes;
   private Map<InternalLocaleBuilder.CaseInsensitiveString, String> ukeywords;

   public InternalLocaleBuilder setLanguage(String var1) throws LocaleSyntaxException {
      if (LocaleUtils.isEmpty(var1)) {
         this.language = "";
      } else {
         if (!LanguageTag.isLanguage(var1)) {
            throw new LocaleSyntaxException("Ill-formed language: " + var1, 0);
         }

         this.language = var1;
      }

      return this;
   }

   public InternalLocaleBuilder setScript(String var1) throws LocaleSyntaxException {
      if (LocaleUtils.isEmpty(var1)) {
         this.script = "";
      } else {
         if (!LanguageTag.isScript(var1)) {
            throw new LocaleSyntaxException("Ill-formed script: " + var1, 0);
         }

         this.script = var1;
      }

      return this;
   }

   public InternalLocaleBuilder setRegion(String var1) throws LocaleSyntaxException {
      if (LocaleUtils.isEmpty(var1)) {
         this.region = "";
      } else {
         if (!LanguageTag.isRegion(var1)) {
            throw new LocaleSyntaxException("Ill-formed region: " + var1, 0);
         }

         this.region = var1;
      }

      return this;
   }

   public InternalLocaleBuilder setVariant(String var1) throws LocaleSyntaxException {
      if (LocaleUtils.isEmpty(var1)) {
         this.variant = "";
      } else {
         String var2 = var1.replaceAll("-", "_");
         int var3 = this.checkVariants(var2, "_");
         if (var3 != -1) {
            throw new LocaleSyntaxException("Ill-formed variant: " + var1, var3);
         }

         this.variant = var2;
      }

      return this;
   }

   public InternalLocaleBuilder addUnicodeLocaleAttribute(String var1) throws LocaleSyntaxException {
      if (!UnicodeLocaleExtension.isAttribute(var1)) {
         throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + var1);
      } else {
         if (this.uattributes == null) {
            this.uattributes = new HashSet(4);
         }

         this.uattributes.add(new InternalLocaleBuilder.CaseInsensitiveString(var1));
         return this;
      }
   }

   public InternalLocaleBuilder removeUnicodeLocaleAttribute(String var1) throws LocaleSyntaxException {
      if (var1 != null && UnicodeLocaleExtension.isAttribute(var1)) {
         if (this.uattributes != null) {
            this.uattributes.remove(new InternalLocaleBuilder.CaseInsensitiveString(var1));
         }

         return this;
      } else {
         throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + var1);
      }
   }

   public InternalLocaleBuilder setUnicodeLocaleKeyword(String var1, String var2) throws LocaleSyntaxException {
      if (!UnicodeLocaleExtension.isKey(var1)) {
         throw new LocaleSyntaxException("Ill-formed Unicode locale keyword key: " + var1);
      } else {
         InternalLocaleBuilder.CaseInsensitiveString var3 = new InternalLocaleBuilder.CaseInsensitiveString(var1);
         if (var2 == null) {
            if (this.ukeywords != null) {
               this.ukeywords.remove(var3);
            }
         } else {
            if (var2.length() != 0) {
               String var4 = var2.replaceAll("_", "-");
               StringTokenIterator var5 = new StringTokenIterator(var4, "-");

               while(!var5.isDone()) {
                  String var6 = var5.current();
                  if (!UnicodeLocaleExtension.isTypeSubtag(var6)) {
                     throw new LocaleSyntaxException("Ill-formed Unicode locale keyword type: " + var2, var5.currentStart());
                  }

                  var5.next();
               }
            }

            if (this.ukeywords == null) {
               this.ukeywords = new HashMap(4);
            }

            this.ukeywords.put(var3, var2);
         }

         return this;
      }
   }

   public InternalLocaleBuilder setExtension(char var1, String var2) throws LocaleSyntaxException {
      boolean var3 = LanguageTag.isPrivateusePrefixChar(var1);
      if (!var3 && !LanguageTag.isExtensionSingletonChar(var1)) {
         throw new LocaleSyntaxException("Ill-formed extension key: " + var1);
      } else {
         boolean var4 = LocaleUtils.isEmpty(var2);
         InternalLocaleBuilder.CaseInsensitiveChar var5 = new InternalLocaleBuilder.CaseInsensitiveChar(var1);
         if (var4) {
            if (UnicodeLocaleExtension.isSingletonChar(var5.value())) {
               if (this.uattributes != null) {
                  this.uattributes.clear();
               }

               if (this.ukeywords != null) {
                  this.ukeywords.clear();
               }
            } else if (this.extensions != null && this.extensions.containsKey(var5)) {
               this.extensions.remove(var5);
            }
         } else {
            String var6 = var2.replaceAll("_", "-");
            StringTokenIterator var7 = new StringTokenIterator(var6, "-");

            while(!var7.isDone()) {
               String var8 = var7.current();
               boolean var9;
               if (var3) {
                  var9 = LanguageTag.isPrivateuseSubtag(var8);
               } else {
                  var9 = LanguageTag.isExtensionSubtag(var8);
               }

               if (!var9) {
                  throw new LocaleSyntaxException("Ill-formed extension value: " + var8, var7.currentStart());
               }

               var7.next();
            }

            if (UnicodeLocaleExtension.isSingletonChar(var5.value())) {
               this.setUnicodeLocaleExtension(var6);
            } else {
               if (this.extensions == null) {
                  this.extensions = new HashMap(4);
               }

               this.extensions.put(var5, var6);
            }
         }

         return this;
      }
   }

   public InternalLocaleBuilder setExtensions(String var1) throws LocaleSyntaxException {
      if (LocaleUtils.isEmpty(var1)) {
         this.clearExtensions();
         return this;
      } else {
         var1 = var1.replaceAll("_", "-");
         StringTokenIterator var2 = new StringTokenIterator(var1, "-");
         ArrayList var3 = null;
         String var4 = null;

         int var5;
         int var6;
         String var7;
         StringBuilder var9;
         for(var5 = 0; !var2.isDone(); var3.add(var9.toString())) {
            var7 = var2.current();
            if (!LanguageTag.isExtensionSingleton(var7)) {
               break;
            }

            var6 = var2.currentStart();
            var9 = new StringBuilder(var7);
            var2.next();

            while(!var2.isDone()) {
               var7 = var2.current();
               if (!LanguageTag.isExtensionSubtag(var7)) {
                  break;
               }

               var9.append("-").append(var7);
               var5 = var2.currentEnd();
               var2.next();
            }

            if (var5 < var6) {
               throw new LocaleSyntaxException("Incomplete extension '" + var7 + "'", var6);
            }

            if (var3 == null) {
               var3 = new ArrayList(4);
            }
         }

         if (!var2.isDone()) {
            var7 = var2.current();
            if (LanguageTag.isPrivateusePrefix(var7)) {
               var6 = var2.currentStart();
               StringBuilder var8 = new StringBuilder(var7);
               var2.next();

               while(!var2.isDone()) {
                  var7 = var2.current();
                  if (!LanguageTag.isPrivateuseSubtag(var7)) {
                     break;
                  }

                  var8.append("-").append(var7);
                  var5 = var2.currentEnd();
                  var2.next();
               }

               if (var5 <= var6) {
                  throw new LocaleSyntaxException("Incomplete privateuse:" + var1.substring(var6), var6);
               }

               var4 = var8.toString();
            }
         }

         if (!var2.isDone()) {
            throw new LocaleSyntaxException("Ill-formed extension subtags:" + var1.substring(var2.currentStart()), var2.currentStart());
         } else {
            return this.setExtensions(var3, var4);
         }
      }
   }

   private InternalLocaleBuilder setExtensions(List<String> var1, String var2) {
      this.clearExtensions();
      if (!LocaleUtils.isEmpty(var1)) {
         HashSet var3 = new HashSet(var1.size());

         InternalLocaleBuilder.CaseInsensitiveChar var6;
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3.add(var6)) {
            String var5 = (String)var4.next();
            var6 = new InternalLocaleBuilder.CaseInsensitiveChar(var5);
            if (!var3.contains(var6)) {
               if (UnicodeLocaleExtension.isSingletonChar(var6.value())) {
                  this.setUnicodeLocaleExtension(var5.substring(2));
               } else {
                  if (this.extensions == null) {
                     this.extensions = new HashMap(4);
                  }

                  this.extensions.put(var6, var5.substring(2));
               }
            }
         }
      }

      if (var2 != null && var2.length() > 0) {
         if (this.extensions == null) {
            this.extensions = new HashMap(1);
         }

         this.extensions.put(new InternalLocaleBuilder.CaseInsensitiveChar(var2), var2.substring(2));
      }

      return this;
   }

   public InternalLocaleBuilder setLanguageTag(LanguageTag var1) {
      this.clear();
      if (!var1.getExtlangs().isEmpty()) {
         this.language = (String)var1.getExtlangs().get(0);
      } else {
         String var2 = var1.getLanguage();
         if (!var2.equals("und")) {
            this.language = var2;
         }
      }

      this.script = var1.getScript();
      this.region = var1.getRegion();
      List var6 = var1.getVariants();
      if (!var6.isEmpty()) {
         StringBuilder var3 = new StringBuilder((String)var6.get(0));
         int var4 = var6.size();

         for(int var5 = 1; var5 < var4; ++var5) {
            var3.append("_").append((String)var6.get(var5));
         }

         this.variant = var3.toString();
      }

      this.setExtensions(var1.getExtensions(), var1.getPrivateuse());
      return this;
   }

   public InternalLocaleBuilder setLocale(BaseLocale var1, LocaleExtensions var2) throws LocaleSyntaxException {
      String var3 = var1.getLanguage();
      String var4 = var1.getScript();
      String var5 = var1.getRegion();
      String var6 = var1.getVariant();
      if (var3.equals("ja") && var5.equals("JP") && var6.equals("JP")) {
         assert "japanese".equals(var2.getUnicodeLocaleType("ca"));

         var6 = "";
      } else if (var3.equals("th") && var5.equals("TH") && var6.equals("TH")) {
         assert "thai".equals(var2.getUnicodeLocaleType("nu"));

         var6 = "";
      } else if (var3.equals("no") && var5.equals("NO") && var6.equals("NY")) {
         var3 = "nn";
         var6 = "";
      }

      if (var3.length() > 0 && !LanguageTag.isLanguage(var3)) {
         throw new LocaleSyntaxException("Ill-formed language: " + var3);
      } else if (var4.length() > 0 && !LanguageTag.isScript(var4)) {
         throw new LocaleSyntaxException("Ill-formed script: " + var4);
      } else if (var5.length() > 0 && !LanguageTag.isRegion(var5)) {
         throw new LocaleSyntaxException("Ill-formed region: " + var5);
      } else {
         if (var6.length() > 0) {
            int var7 = this.checkVariants(var6, "_");
            if (var7 != -1) {
               throw new LocaleSyntaxException("Ill-formed variant: " + var6, var7);
            }
         }

         this.language = var3;
         this.script = var4;
         this.region = var5;
         this.variant = var6;
         this.clearExtensions();
         Set var14 = var2 == null ? null : var2.getKeys();
         if (var14 != null) {
            Iterator var8 = var14.iterator();

            while(true) {
               while(var8.hasNext()) {
                  Character var9 = (Character)var8.next();
                  Extension var10 = var2.getExtension(var9);
                  if (var10 instanceof UnicodeLocaleExtension) {
                     UnicodeLocaleExtension var11 = (UnicodeLocaleExtension)var10;

                     Iterator var12;
                     String var13;
                     for(var12 = var11.getUnicodeLocaleAttributes().iterator(); var12.hasNext(); this.uattributes.add(new InternalLocaleBuilder.CaseInsensitiveString(var13))) {
                        var13 = (String)var12.next();
                        if (this.uattributes == null) {
                           this.uattributes = new HashSet(4);
                        }
                     }

                     for(var12 = var11.getUnicodeLocaleKeys().iterator(); var12.hasNext(); this.ukeywords.put(new InternalLocaleBuilder.CaseInsensitiveString(var13), var11.getUnicodeLocaleType(var13))) {
                        var13 = (String)var12.next();
                        if (this.ukeywords == null) {
                           this.ukeywords = new HashMap(4);
                        }
                     }
                  } else {
                     if (this.extensions == null) {
                        this.extensions = new HashMap(4);
                     }

                     this.extensions.put(new InternalLocaleBuilder.CaseInsensitiveChar(var9), var10.getValue());
                  }
               }

               return this;
            }
         } else {
            return this;
         }
      }
   }

   public InternalLocaleBuilder clear() {
      this.language = "";
      this.script = "";
      this.region = "";
      this.variant = "";
      this.clearExtensions();
      return this;
   }

   public InternalLocaleBuilder clearExtensions() {
      if (this.extensions != null) {
         this.extensions.clear();
      }

      if (this.uattributes != null) {
         this.uattributes.clear();
      }

      if (this.ukeywords != null) {
         this.ukeywords.clear();
      }

      return this;
   }

   public BaseLocale getBaseLocale() {
      String var1 = this.language;
      String var2 = this.script;
      String var3 = this.region;
      String var4 = this.variant;
      if (this.extensions != null) {
         String var5 = (String)this.extensions.get(PRIVATEUSE_KEY);
         if (var5 != null) {
            StringTokenIterator var6 = new StringTokenIterator(var5, "-");
            boolean var7 = false;

            int var8;
            for(var8 = -1; !var6.isDone(); var6.next()) {
               if (var7) {
                  var8 = var6.currentStart();
                  break;
               }

               if (LocaleUtils.caseIgnoreMatch(var6.current(), "lvariant")) {
                  var7 = true;
               }
            }

            if (var8 != -1) {
               StringBuilder var9 = new StringBuilder(var4);
               if (var9.length() != 0) {
                  var9.append("_");
               }

               var9.append(var5.substring(var8).replaceAll("-", "_"));
               var4 = var9.toString();
            }
         }
      }

      return BaseLocale.getInstance(var1, var2, var3, var4);
   }

   public LocaleExtensions getLocaleExtensions() {
      if (LocaleUtils.isEmpty(this.extensions) && LocaleUtils.isEmpty(this.uattributes) && LocaleUtils.isEmpty(this.ukeywords)) {
         return null;
      } else {
         LocaleExtensions var1 = new LocaleExtensions(this.extensions, this.uattributes, this.ukeywords);
         return var1.isEmpty() ? null : var1;
      }
   }

   static String removePrivateuseVariant(String var0) {
      StringTokenIterator var1 = new StringTokenIterator(var0, "-");
      int var2 = -1;

      boolean var3;
      for(var3 = false; !var1.isDone(); var1.next()) {
         if (var2 != -1) {
            var3 = true;
            break;
         }

         if (LocaleUtils.caseIgnoreMatch(var1.current(), "lvariant")) {
            var2 = var1.currentStart();
         }
      }

      if (!var3) {
         return var0;
      } else {
         assert var2 == 0 || var2 > 1;

         return var2 == 0 ? null : var0.substring(0, var2 - 1);
      }
   }

   private int checkVariants(String var1, String var2) {
      StringTokenIterator var3 = new StringTokenIterator(var1, var2);

      while(!var3.isDone()) {
         String var4 = var3.current();
         if (!LanguageTag.isVariant(var4)) {
            return var3.currentStart();
         }

         var3.next();
      }

      return -1;
   }

   private void setUnicodeLocaleExtension(String var1) {
      if (this.uattributes != null) {
         this.uattributes.clear();
      }

      if (this.ukeywords != null) {
         this.ukeywords.clear();
      }

      StringTokenIterator var2 = new StringTokenIterator(var1, "-");

      while(!var2.isDone() && UnicodeLocaleExtension.isAttribute(var2.current())) {
         if (this.uattributes == null) {
            this.uattributes = new HashSet(4);
         }

         this.uattributes.add(new InternalLocaleBuilder.CaseInsensitiveString(var2.current()));
         var2.next();
      }

      InternalLocaleBuilder.CaseInsensitiveString var3 = null;
      int var5 = -1;
      int var6 = -1;

      while(!var2.isDone()) {
         String var4;
         if (var3 != null) {
            if (UnicodeLocaleExtension.isKey(var2.current())) {
               assert var5 == -1 || var6 != -1;

               var4 = var5 == -1 ? "" : var1.substring(var5, var6);
               if (this.ukeywords == null) {
                  this.ukeywords = new HashMap(4);
               }

               this.ukeywords.put(var3, var4);
               InternalLocaleBuilder.CaseInsensitiveString var7 = new InternalLocaleBuilder.CaseInsensitiveString(var2.current());
               var3 = this.ukeywords.containsKey(var7) ? null : var7;
               var6 = -1;
               var5 = -1;
            } else {
               if (var5 == -1) {
                  var5 = var2.currentStart();
               }

               var6 = var2.currentEnd();
            }
         } else if (UnicodeLocaleExtension.isKey(var2.current())) {
            var3 = new InternalLocaleBuilder.CaseInsensitiveString(var2.current());
            if (this.ukeywords != null && this.ukeywords.containsKey(var3)) {
               var3 = null;
            }
         }

         if (!var2.hasNext()) {
            if (var3 != null) {
               assert var5 == -1 || var6 != -1;

               var4 = var5 == -1 ? "" : var1.substring(var5, var6);
               if (this.ukeywords == null) {
                  this.ukeywords = new HashMap(4);
               }

               this.ukeywords.put(var3, var4);
            }
            break;
         }

         var2.next();
      }

   }

   static final class CaseInsensitiveChar {
      private final char ch;
      private final char lowerCh;

      private CaseInsensitiveChar(String var1) {
         this(var1.charAt(0));
      }

      CaseInsensitiveChar(char var1) {
         this.ch = var1;
         this.lowerCh = LocaleUtils.toLower(this.ch);
      }

      public char value() {
         return this.ch;
      }

      public int hashCode() {
         return this.lowerCh;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof InternalLocaleBuilder.CaseInsensitiveChar)) {
            return false;
         } else {
            return this.lowerCh == ((InternalLocaleBuilder.CaseInsensitiveChar)var1).lowerCh;
         }
      }

      // $FF: synthetic method
      CaseInsensitiveChar(String var1, Object var2) {
         this(var1);
      }
   }

   static final class CaseInsensitiveString {
      private final String str;
      private final String lowerStr;

      CaseInsensitiveString(String var1) {
         this.str = var1;
         this.lowerStr = LocaleUtils.toLowerString(var1);
      }

      public String value() {
         return this.str;
      }

      public int hashCode() {
         return this.lowerStr.hashCode();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof InternalLocaleBuilder.CaseInsensitiveString) ? false : this.lowerStr.equals(((InternalLocaleBuilder.CaseInsensitiveString)var1).lowerStr);
         }
      }
   }
}
