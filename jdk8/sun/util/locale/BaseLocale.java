package sun.util.locale;

import java.lang.ref.SoftReference;

public final class BaseLocale {
   public static final String SEP = "_";
   private static final BaseLocale.Cache CACHE = new BaseLocale.Cache();
   private final String language;
   private final String script;
   private final String region;
   private final String variant;
   private volatile int hash;

   private BaseLocale(String var1, String var2) {
      this.hash = 0;
      this.language = var1;
      this.script = "";
      this.region = var2;
      this.variant = "";
   }

   private BaseLocale(String var1, String var2, String var3, String var4) {
      this.hash = 0;
      this.language = var1 != null ? LocaleUtils.toLowerString(var1).intern() : "";
      this.script = var2 != null ? LocaleUtils.toTitleString(var2).intern() : "";
      this.region = var3 != null ? LocaleUtils.toUpperString(var3).intern() : "";
      this.variant = var4 != null ? var4.intern() : "";
   }

   public static BaseLocale createInstance(String var0, String var1) {
      BaseLocale var2 = new BaseLocale(var0, var1);
      CACHE.put(new BaseLocale.Key(var0, var1), var2);
      return var2;
   }

   public static BaseLocale getInstance(String var0, String var1, String var2, String var3) {
      if (var0 != null) {
         if (LocaleUtils.caseIgnoreMatch(var0, "he")) {
            var0 = "iw";
         } else if (LocaleUtils.caseIgnoreMatch(var0, "yi")) {
            var0 = "ji";
         } else if (LocaleUtils.caseIgnoreMatch(var0, "id")) {
            var0 = "in";
         }
      }

      BaseLocale.Key var4 = new BaseLocale.Key(var0, var1, var2, var3);
      BaseLocale var5 = (BaseLocale)CACHE.get(var4);
      return var5;
   }

   public String getLanguage() {
      return this.language;
   }

   public String getScript() {
      return this.script;
   }

   public String getRegion() {
      return this.region;
   }

   public String getVariant() {
      return this.variant;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BaseLocale)) {
         return false;
      } else {
         BaseLocale var2 = (BaseLocale)var1;
         return this.language == var2.language && this.script == var2.script && this.region == var2.region && this.variant == var2.variant;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.language.length() > 0) {
         var1.append("language=");
         var1.append(this.language);
      }

      if (this.script.length() > 0) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append("script=");
         var1.append(this.script);
      }

      if (this.region.length() > 0) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append("region=");
         var1.append(this.region);
      }

      if (this.variant.length() > 0) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append("variant=");
         var1.append(this.variant);
      }

      return var1.toString();
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         var1 = this.language.hashCode();
         var1 = 31 * var1 + this.script.hashCode();
         var1 = 31 * var1 + this.region.hashCode();
         var1 = 31 * var1 + this.variant.hashCode();
         this.hash = var1;
      }

      return var1;
   }

   // $FF: synthetic method
   BaseLocale(String var1, String var2, String var3, String var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   private static class Cache extends LocaleObjectCache<BaseLocale.Key, BaseLocale> {
      public Cache() {
      }

      protected BaseLocale.Key normalizeKey(BaseLocale.Key var1) {
         assert var1.lang.get() != null && var1.scrt.get() != null && var1.regn.get() != null && var1.vart.get() != null;

         return BaseLocale.Key.normalize(var1);
      }

      protected BaseLocale createObject(BaseLocale.Key var1) {
         return new BaseLocale((String)var1.lang.get(), (String)var1.scrt.get(), (String)var1.regn.get(), (String)var1.vart.get());
      }
   }

   private static final class Key {
      private final SoftReference<String> lang;
      private final SoftReference<String> scrt;
      private final SoftReference<String> regn;
      private final SoftReference<String> vart;
      private final boolean normalized;
      private final int hash;

      private Key(String var1, String var2) {
         assert var1.intern() == var1 && var2.intern() == var2;

         this.lang = new SoftReference(var1);
         this.scrt = new SoftReference("");
         this.regn = new SoftReference(var2);
         this.vart = new SoftReference("");
         this.normalized = true;
         int var3 = var1.hashCode();
         if (var2 != "") {
            int var4 = var2.length();

            for(int var5 = 0; var5 < var4; ++var5) {
               var3 = 31 * var3 + LocaleUtils.toLower(var2.charAt(var5));
            }
         }

         this.hash = var3;
      }

      public Key(String var1, String var2, String var3, String var4) {
         this(var1, var2, var3, var4, false);
      }

      private Key(String var1, String var2, String var3, String var4, boolean var5) {
         int var6 = 0;
         int var7;
         int var8;
         if (var1 != null) {
            this.lang = new SoftReference(var1);
            var7 = var1.length();

            for(var8 = 0; var8 < var7; ++var8) {
               var6 = 31 * var6 + LocaleUtils.toLower(var1.charAt(var8));
            }
         } else {
            this.lang = new SoftReference("");
         }

         if (var2 != null) {
            this.scrt = new SoftReference(var2);
            var7 = var2.length();

            for(var8 = 0; var8 < var7; ++var8) {
               var6 = 31 * var6 + LocaleUtils.toLower(var2.charAt(var8));
            }
         } else {
            this.scrt = new SoftReference("");
         }

         if (var3 != null) {
            this.regn = new SoftReference(var3);
            var7 = var3.length();

            for(var8 = 0; var8 < var7; ++var8) {
               var6 = 31 * var6 + LocaleUtils.toLower(var3.charAt(var8));
            }
         } else {
            this.regn = new SoftReference("");
         }

         if (var4 != null) {
            this.vart = new SoftReference(var4);
            var7 = var4.length();

            for(var8 = 0; var8 < var7; ++var8) {
               var6 = 31 * var6 + var4.charAt(var8);
            }
         } else {
            this.vart = new SoftReference("");
         }

         this.hash = var6;
         this.normalized = var5;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            if (var1 instanceof BaseLocale.Key && this.hash == ((BaseLocale.Key)var1).hash) {
               String var2 = (String)this.lang.get();
               String var3 = (String)((BaseLocale.Key)var1).lang.get();
               if (var2 != null && var3 != null && LocaleUtils.caseIgnoreMatch(var3, var2)) {
                  String var4 = (String)this.scrt.get();
                  String var5 = (String)((BaseLocale.Key)var1).scrt.get();
                  if (var4 != null && var5 != null && LocaleUtils.caseIgnoreMatch(var5, var4)) {
                     String var6 = (String)this.regn.get();
                     String var7 = (String)((BaseLocale.Key)var1).regn.get();
                     if (var6 != null && var7 != null && LocaleUtils.caseIgnoreMatch(var7, var6)) {
                        String var8 = (String)this.vart.get();
                        String var9 = (String)((BaseLocale.Key)var1).vart.get();
                        return var9 != null && var9.equals(var8);
                     }
                  }
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return this.hash;
      }

      public static BaseLocale.Key normalize(BaseLocale.Key var0) {
         if (var0.normalized) {
            return var0;
         } else {
            String var1 = LocaleUtils.toLowerString((String)var0.lang.get()).intern();
            String var2 = LocaleUtils.toTitleString((String)var0.scrt.get()).intern();
            String var3 = LocaleUtils.toUpperString((String)var0.regn.get()).intern();
            String var4 = ((String)var0.vart.get()).intern();
            return new BaseLocale.Key(var1, var2, var3, var4, true);
         }
      }

      // $FF: synthetic method
      Key(String var1, String var2, Object var3) {
         this(var1, var2);
      }
   }
}
