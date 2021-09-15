package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.spi.LocaleNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.locale.BaseLocale;
import sun.util.locale.InternalLocaleBuilder;
import sun.util.locale.LanguageTag;
import sun.util.locale.LocaleExtensions;
import sun.util.locale.LocaleMatcher;
import sun.util.locale.LocaleObjectCache;
import sun.util.locale.LocaleSyntaxException;
import sun.util.locale.LocaleUtils;
import sun.util.locale.ParseStatus;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;
import sun.util.locale.provider.LocaleServiceProviderPool;

public final class Locale implements Cloneable, Serializable {
   private static final Locale.Cache LOCALECACHE = new Locale.Cache();
   public static final Locale ENGLISH = createConstant("en", "");
   public static final Locale FRENCH = createConstant("fr", "");
   public static final Locale GERMAN = createConstant("de", "");
   public static final Locale ITALIAN = createConstant("it", "");
   public static final Locale JAPANESE = createConstant("ja", "");
   public static final Locale KOREAN = createConstant("ko", "");
   public static final Locale CHINESE = createConstant("zh", "");
   public static final Locale SIMPLIFIED_CHINESE = createConstant("zh", "CN");
   public static final Locale TRADITIONAL_CHINESE = createConstant("zh", "TW");
   public static final Locale FRANCE = createConstant("fr", "FR");
   public static final Locale GERMANY = createConstant("de", "DE");
   public static final Locale ITALY = createConstant("it", "IT");
   public static final Locale JAPAN = createConstant("ja", "JP");
   public static final Locale KOREA = createConstant("ko", "KR");
   public static final Locale CHINA;
   public static final Locale PRC;
   public static final Locale TAIWAN;
   public static final Locale UK;
   public static final Locale US;
   public static final Locale CANADA;
   public static final Locale CANADA_FRENCH;
   public static final Locale ROOT;
   public static final char PRIVATE_USE_EXTENSION = 'x';
   public static final char UNICODE_LOCALE_EXTENSION = 'u';
   static final long serialVersionUID = 9149081749638150636L;
   private static final int DISPLAY_LANGUAGE = 0;
   private static final int DISPLAY_COUNTRY = 1;
   private static final int DISPLAY_VARIANT = 2;
   private static final int DISPLAY_SCRIPT = 3;
   private transient BaseLocale baseLocale;
   private transient LocaleExtensions localeExtensions;
   private transient volatile int hashCodeValue;
   private static volatile Locale defaultLocale;
   private static volatile Locale defaultDisplayLocale;
   private static volatile Locale defaultFormatLocale;
   private transient volatile String languageTag;
   private static final ObjectStreamField[] serialPersistentFields;
   private static volatile String[] isoLanguages;
   private static volatile String[] isoCountries;

   private Locale(BaseLocale var1, LocaleExtensions var2) {
      this.hashCodeValue = 0;
      this.baseLocale = var1;
      this.localeExtensions = var2;
   }

   public Locale(String var1, String var2, String var3) {
      this.hashCodeValue = 0;
      if (var1 != null && var2 != null && var3 != null) {
         this.baseLocale = BaseLocale.getInstance(convertOldISOCodes(var1), "", var2, var3);
         this.localeExtensions = getCompatibilityExtensions(var1, "", var2, var3);
      } else {
         throw new NullPointerException();
      }
   }

   public Locale(String var1, String var2) {
      this(var1, var2, "");
   }

   public Locale(String var1) {
      this(var1, "", "");
   }

   private static Locale createConstant(String var0, String var1) {
      BaseLocale var2 = BaseLocale.createInstance(var0, var1);
      return getInstance(var2, (LocaleExtensions)null);
   }

   static Locale getInstance(String var0, String var1, String var2) {
      return getInstance(var0, "", var1, var2, (LocaleExtensions)null);
   }

   static Locale getInstance(String var0, String var1, String var2, String var3, LocaleExtensions var4) {
      if (var0 != null && var1 != null && var2 != null && var3 != null) {
         if (var4 == null) {
            var4 = getCompatibilityExtensions(var0, var1, var2, var3);
         }

         BaseLocale var5 = BaseLocale.getInstance(var0, var1, var2, var3);
         return getInstance(var5, var4);
      } else {
         throw new NullPointerException();
      }
   }

   static Locale getInstance(BaseLocale var0, LocaleExtensions var1) {
      Locale.LocaleKey var2 = new Locale.LocaleKey(var0, var1);
      return (Locale)LOCALECACHE.get(var2);
   }

   public static Locale getDefault() {
      return defaultLocale;
   }

   public static Locale getDefault(Locale.Category var0) {
      Class var1;
      switch(var0) {
      case DISPLAY:
         if (defaultDisplayLocale == null) {
            var1 = Locale.class;
            synchronized(Locale.class) {
               if (defaultDisplayLocale == null) {
                  defaultDisplayLocale = initDefault(var0);
               }
            }
         }

         return defaultDisplayLocale;
      case FORMAT:
         if (defaultFormatLocale == null) {
            var1 = Locale.class;
            synchronized(Locale.class) {
               if (defaultFormatLocale == null) {
                  defaultFormatLocale = initDefault(var0);
               }
            }
         }

         return defaultFormatLocale;
      default:
         assert false : "Unknown Category";

         return getDefault();
      }
   }

   private static Locale initDefault() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.language", "en")));
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.region")));
      String var2;
      String var3;
      String var4;
      if (var1 != null) {
         int var5 = var1.indexOf(95);
         if (var5 >= 0) {
            var3 = var1.substring(0, var5);
            var4 = var1.substring(var5 + 1);
         } else {
            var3 = var1;
            var4 = "";
         }

         var2 = "";
      } else {
         var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.script", "")));
         var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.country", "")));
         var4 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.variant", "")));
      }

      return getInstance(var0, var2, var3, var4, (LocaleExtensions)null);
   }

   private static Locale initDefault(Locale.Category var0) {
      return getInstance((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0.languageKey, defaultLocale.getLanguage()))), (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0.scriptKey, defaultLocale.getScript()))), (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0.countryKey, defaultLocale.getCountry()))), (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0.variantKey, defaultLocale.getVariant()))), (LocaleExtensions)null);
   }

   public static synchronized void setDefault(Locale var0) {
      setDefault(Locale.Category.DISPLAY, var0);
      setDefault(Locale.Category.FORMAT, var0);
      defaultLocale = var0;
   }

   public static synchronized void setDefault(Locale.Category var0, Locale var1) {
      if (var0 == null) {
         throw new NullPointerException("Category cannot be NULL");
      } else if (var1 == null) {
         throw new NullPointerException("Can't set default locale to NULL");
      } else {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(new PropertyPermission("user.language", "write"));
         }

         switch(var0) {
         case DISPLAY:
            defaultDisplayLocale = var1;
            break;
         case FORMAT:
            defaultFormatLocale = var1;
            break;
         default:
            assert false : "Unknown Category";
         }

      }
   }

   public static Locale[] getAvailableLocales() {
      return LocaleServiceProviderPool.getAllAvailableLocales();
   }

   public static String[] getISOCountries() {
      if (isoCountries == null) {
         isoCountries = getISO2Table("ADANDAEAREAFAFGAGATGAIAIAALALBAMARMANANTAOAGOAQATAARARGASASMATAUTAUAUSAWABWAXALAAZAZEBABIHBBBRBBDBGDBEBELBFBFABGBGRBHBHRBIBDIBJBENBLBLMBMBMUBNBRNBOBOLBQBESBRBRABSBHSBTBTNBVBVTBWBWABYBLRBZBLZCACANCCCCKCDCODCFCAFCGCOGCHCHECICIVCKCOKCLCHLCMCMRCNCHNCOCOLCRCRICUCUBCVCPVCWCUWCXCXRCYCYPCZCZEDEDEUDJDJIDKDNKDMDMADODOMDZDZAECECUEEESTEGEGYEHESHERERIESESPETETHFIFINFJFJIFKFLKFMFSMFOFROFRFRAGAGABGBGBRGDGRDGEGEOGFGUFGGGGYGHGHAGIGIBGLGRLGMGMBGNGINGPGLPGQGNQGRGRCGSSGSGTGTMGUGUMGWGNBGYGUYHKHKGHMHMDHNHNDHRHRVHTHTIHUHUNIDIDNIEIRLILISRIMIMNININDIOIOTIQIRQIRIRNISISLITITAJEJEYJMJAMJOJORJPJPNKEKENKGKGZKHKHMKIKIRKMCOMKNKNAKPPRKKRKORKWKWTKYCYMKZKAZLALAOLBLBNLCLCALILIELKLKALRLBRLSLSOLTLTULULUXLVLVALYLBYMAMARMCMCOMDMDAMEMNEMFMAFMGMDGMHMHLMKMKDMLMLIMMMMRMNMNGMOMACMPMNPMQMTQMRMRTMSMSRMTMLTMUMUSMVMDVMWMWIMXMEXMYMYSMZMOZNANAMNCNCLNENERNFNFKNGNGANINICNLNLDNONORNPNPLNRNRUNUNIUNZNZLOMOMNPAPANPEPERPFPYFPGPNGPHPHLPKPAKPLPOLPMSPMPNPCNPRPRIPSPSEPTPRTPWPLWPYPRYQAQATREREUROROURSSRBRURUSRWRWASASAUSBSLBSCSYCSDSDNSESWESGSGPSHSHNSISVNSJSJMSKSVKSLSLESMSMRSNSENSOSOMSRSURSSSSDSTSTPSVSLVSXSXMSYSYRSZSWZTCTCATDTCDTFATFTGTGOTHTHATJTJKTKTKLTLTLSTMTKMTNTUNTOTONTRTURTTTTOTVTUVTWTWNTZTZAUAUKRUGUGAUMUMIUSUSAUYURYUZUZBVAVATVCVCTVEVENVGVGBVIVIRVNVNMVUVUTWFWLFWSWSMYEYEMYTMYTZAZAFZMZMBZWZWE");
      }

      String[] var0 = new String[isoCountries.length];
      System.arraycopy(isoCountries, 0, var0, 0, isoCountries.length);
      return var0;
   }

   public static String[] getISOLanguages() {
      if (isoLanguages == null) {
         isoLanguages = getISO2Table("aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbobodbrbrebsboscacatcechechchacocoscrcrecscescuchucvchvcycymdadandedeudvdivdzdzoeeeweelellenengeoepoesspaetesteueusfafasfffulfifinfjfijfofaofrfrafyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyhyehzheriainaidindieileigiboiiiiiikipkinindioidoisislititaiuikuiwhebjajpnjiyidjvjavkakatkgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimrimkmkdmlmalmnmonmomolmrmarmsmsamtmltmymyananaunbnobndndenenepngndonlnldnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunroronrurusrwkinsasanscsrdsdsndsesmesgsagsisinskslkslslvsmsmosnsnasosomsqsqisrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhzhozuzul");
      }

      String[] var0 = new String[isoLanguages.length];
      System.arraycopy(isoLanguages, 0, var0, 0, isoLanguages.length);
      return var0;
   }

   private static String[] getISO2Table(String var0) {
      int var1 = var0.length() / 5;
      String[] var2 = new String[var1];
      int var3 = 0;

      for(int var4 = 0; var3 < var1; var4 += 5) {
         var2[var3] = var0.substring(var4, var4 + 2);
         ++var3;
      }

      return var2;
   }

   public String getLanguage() {
      return this.baseLocale.getLanguage();
   }

   public String getScript() {
      return this.baseLocale.getScript();
   }

   public String getCountry() {
      return this.baseLocale.getRegion();
   }

   public String getVariant() {
      return this.baseLocale.getVariant();
   }

   public boolean hasExtensions() {
      return this.localeExtensions != null;
   }

   public Locale stripExtensions() {
      return this.hasExtensions() ? getInstance(this.baseLocale, (LocaleExtensions)null) : this;
   }

   public String getExtension(char var1) {
      if (!LocaleExtensions.isValidKey(var1)) {
         throw new IllegalArgumentException("Ill-formed extension key: " + var1);
      } else {
         return this.hasExtensions() ? this.localeExtensions.getExtensionValue(var1) : null;
      }
   }

   public Set<Character> getExtensionKeys() {
      return !this.hasExtensions() ? Collections.emptySet() : this.localeExtensions.getKeys();
   }

   public Set<String> getUnicodeLocaleAttributes() {
      return !this.hasExtensions() ? Collections.emptySet() : this.localeExtensions.getUnicodeLocaleAttributes();
   }

   public String getUnicodeLocaleType(String var1) {
      if (!isUnicodeExtensionKey(var1)) {
         throw new IllegalArgumentException("Ill-formed Unicode locale key: " + var1);
      } else {
         return this.hasExtensions() ? this.localeExtensions.getUnicodeLocaleType(var1) : null;
      }
   }

   public Set<String> getUnicodeLocaleKeys() {
      return this.localeExtensions == null ? Collections.emptySet() : this.localeExtensions.getUnicodeLocaleKeys();
   }

   BaseLocale getBaseLocale() {
      return this.baseLocale;
   }

   LocaleExtensions getLocaleExtensions() {
      return this.localeExtensions;
   }

   public final String toString() {
      boolean var1 = this.baseLocale.getLanguage().length() != 0;
      boolean var2 = this.baseLocale.getScript().length() != 0;
      boolean var3 = this.baseLocale.getRegion().length() != 0;
      boolean var4 = this.baseLocale.getVariant().length() != 0;
      boolean var5 = this.localeExtensions != null && this.localeExtensions.getID().length() != 0;
      StringBuilder var6 = new StringBuilder(this.baseLocale.getLanguage());
      if (var3 || var1 && (var4 || var2 || var5)) {
         var6.append('_').append(this.baseLocale.getRegion());
      }

      if (var4 && (var1 || var3)) {
         var6.append('_').append(this.baseLocale.getVariant());
      }

      if (var2 && (var1 || var3)) {
         var6.append("_#").append(this.baseLocale.getScript());
      }

      if (var5 && (var1 || var3)) {
         var6.append('_');
         if (!var2) {
            var6.append('#');
         }

         var6.append(this.localeExtensions.getID());
      }

      return var6.toString();
   }

   public String toLanguageTag() {
      if (this.languageTag != null) {
         return this.languageTag;
      } else {
         LanguageTag var1 = LanguageTag.parseLocale(this.baseLocale, this.localeExtensions);
         StringBuilder var2 = new StringBuilder();
         String var3 = var1.getLanguage();
         if (var3.length() > 0) {
            var2.append(LanguageTag.canonicalizeLanguage(var3));
         }

         var3 = var1.getScript();
         if (var3.length() > 0) {
            var2.append("-");
            var2.append(LanguageTag.canonicalizeScript(var3));
         }

         var3 = var1.getRegion();
         if (var3.length() > 0) {
            var2.append("-");
            var2.append(LanguageTag.canonicalizeRegion(var3));
         }

         List var4 = var1.getVariants();
         Iterator var5 = var4.iterator();

         String var6;
         while(var5.hasNext()) {
            var6 = (String)var5.next();
            var2.append("-");
            var2.append(var6);
         }

         var4 = var1.getExtensions();
         var5 = var4.iterator();

         while(var5.hasNext()) {
            var6 = (String)var5.next();
            var2.append("-");
            var2.append(LanguageTag.canonicalizeExtension(var6));
         }

         var3 = var1.getPrivateuse();
         if (var3.length() > 0) {
            if (var2.length() > 0) {
               var2.append("-");
            }

            var2.append("x").append("-");
            var2.append(var3);
         }

         String var9 = var2.toString();
         synchronized(this) {
            if (this.languageTag == null) {
               this.languageTag = var9;
            }
         }

         return this.languageTag;
      }
   }

   public static Locale forLanguageTag(String var0) {
      LanguageTag var1 = LanguageTag.parse(var0, (ParseStatus)null);
      InternalLocaleBuilder var2 = new InternalLocaleBuilder();
      var2.setLanguageTag(var1);
      BaseLocale var3 = var2.getBaseLocale();
      LocaleExtensions var4 = var2.getLocaleExtensions();
      if (var4 == null && var3.getVariant().length() > 0) {
         var4 = getCompatibilityExtensions(var3.getLanguage(), var3.getScript(), var3.getRegion(), var3.getVariant());
      }

      return getInstance(var3, var4);
   }

   public String getISO3Language() throws MissingResourceException {
      String var1 = this.baseLocale.getLanguage();
      if (var1.length() == 3) {
         return var1;
      } else {
         String var2 = getISO3Code(var1, "aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbobodbrbrebsboscacatcechechchacocoscrcrecscescuchucvchvcycymdadandedeudvdivdzdzoeeeweelellenengeoepoesspaetesteueusfafasfffulfifinfjfijfofaofrfrafyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyhyehzheriainaidindieileigiboiiiiiikipkinindioidoisislititaiuikuiwhebjajpnjiyidjvjavkakatkgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimrimkmkdmlmalmnmonmomolmrmarmsmsamtmltmymyananaunbnobndndenenepngndonlnldnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunroronrurusrwkinsasanscsrdsdsndsesmesgsagsisinskslkslslvsmsmosnsnasosomsqsqisrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhzhozuzul");
         if (var2 == null) {
            throw new MissingResourceException("Couldn't find 3-letter language code for " + var1, "FormatData_" + this.toString(), "ShortLanguage");
         } else {
            return var2;
         }
      }
   }

   public String getISO3Country() throws MissingResourceException {
      String var1 = getISO3Code(this.baseLocale.getRegion(), "ADANDAEAREAFAFGAGATGAIAIAALALBAMARMANANTAOAGOAQATAARARGASASMATAUTAUAUSAWABWAXALAAZAZEBABIHBBBRBBDBGDBEBELBFBFABGBGRBHBHRBIBDIBJBENBLBLMBMBMUBNBRNBOBOLBQBESBRBRABSBHSBTBTNBVBVTBWBWABYBLRBZBLZCACANCCCCKCDCODCFCAFCGCOGCHCHECICIVCKCOKCLCHLCMCMRCNCHNCOCOLCRCRICUCUBCVCPVCWCUWCXCXRCYCYPCZCZEDEDEUDJDJIDKDNKDMDMADODOMDZDZAECECUEEESTEGEGYEHESHERERIESESPETETHFIFINFJFJIFKFLKFMFSMFOFROFRFRAGAGABGBGBRGDGRDGEGEOGFGUFGGGGYGHGHAGIGIBGLGRLGMGMBGNGINGPGLPGQGNQGRGRCGSSGSGTGTMGUGUMGWGNBGYGUYHKHKGHMHMDHNHNDHRHRVHTHTIHUHUNIDIDNIEIRLILISRIMIMNININDIOIOTIQIRQIRIRNISISLITITAJEJEYJMJAMJOJORJPJPNKEKENKGKGZKHKHMKIKIRKMCOMKNKNAKPPRKKRKORKWKWTKYCYMKZKAZLALAOLBLBNLCLCALILIELKLKALRLBRLSLSOLTLTULULUXLVLVALYLBYMAMARMCMCOMDMDAMEMNEMFMAFMGMDGMHMHLMKMKDMLMLIMMMMRMNMNGMOMACMPMNPMQMTQMRMRTMSMSRMTMLTMUMUSMVMDVMWMWIMXMEXMYMYSMZMOZNANAMNCNCLNENERNFNFKNGNGANINICNLNLDNONORNPNPLNRNRUNUNIUNZNZLOMOMNPAPANPEPERPFPYFPGPNGPHPHLPKPAKPLPOLPMSPMPNPCNPRPRIPSPSEPTPRTPWPLWPYPRYQAQATREREUROROURSSRBRURUSRWRWASASAUSBSLBSCSYCSDSDNSESWESGSGPSHSHNSISVNSJSJMSKSVKSLSLESMSMRSNSENSOSOMSRSURSSSSDSTSTPSVSLVSXSXMSYSYRSZSWZTCTCATDTCDTFATFTGTGOTHTHATJTJKTKTKLTLTLSTMTKMTNTUNTOTONTRTURTTTTOTVTUVTWTWNTZTZAUAUKRUGUGAUMUMIUSUSAUYURYUZUZBVAVATVCVCTVEVENVGVGBVIVIRVNVNMVUVUTWFWLFWSWSMYEYEMYTMYTZAZAFZMZMBZWZWE");
      if (var1 == null) {
         throw new MissingResourceException("Couldn't find 3-letter country code for " + this.baseLocale.getRegion(), "FormatData_" + this.toString(), "ShortCountry");
      } else {
         return var1;
      }
   }

   private static String getISO3Code(String var0, String var1) {
      int var2 = var0.length();
      if (var2 == 0) {
         return "";
      } else {
         int var3 = var1.length();
         int var4 = var3;
         if (var2 == 2) {
            char var5 = var0.charAt(0);
            char var6 = var0.charAt(1);

            for(var4 = 0; var4 < var3 && (var1.charAt(var4) != var5 || var1.charAt(var4 + 1) != var6); var4 += 5) {
            }
         }

         return var4 < var3 ? var1.substring(var4 + 2, var4 + 5) : null;
      }
   }

   public final String getDisplayLanguage() {
      return this.getDisplayLanguage(getDefault(Locale.Category.DISPLAY));
   }

   public String getDisplayLanguage(Locale var1) {
      return this.getDisplayString(this.baseLocale.getLanguage(), var1, 0);
   }

   public String getDisplayScript() {
      return this.getDisplayScript(getDefault(Locale.Category.DISPLAY));
   }

   public String getDisplayScript(Locale var1) {
      return this.getDisplayString(this.baseLocale.getScript(), var1, 3);
   }

   public final String getDisplayCountry() {
      return this.getDisplayCountry(getDefault(Locale.Category.DISPLAY));
   }

   public String getDisplayCountry(Locale var1) {
      return this.getDisplayString(this.baseLocale.getRegion(), var1, 1);
   }

   private String getDisplayString(String var1, Locale var2, int var3) {
      if (var1.length() == 0) {
         return "";
      } else if (var2 == null) {
         throw new NullPointerException();
      } else {
         LocaleServiceProviderPool var4 = LocaleServiceProviderPool.getPool(LocaleNameProvider.class);
         String var5 = var3 == 2 ? "%%" + var1 : var1;
         String var6 = (String)var4.getLocalizedObject(Locale.LocaleNameGetter.INSTANCE, var2, var5, var3, var1);
         return var6 != null ? var6 : var1;
      }
   }

   public final String getDisplayVariant() {
      return this.getDisplayVariant(getDefault(Locale.Category.DISPLAY));
   }

   public String getDisplayVariant(Locale var1) {
      if (this.baseLocale.getVariant().length() == 0) {
         return "";
      } else {
         LocaleResources var2 = LocaleProviderAdapter.forJRE().getLocaleResources(var1);
         String[] var3 = this.getDisplayVariantArray(var1);
         return formatList(var3, var2.getLocaleName("ListPattern"), var2.getLocaleName("ListCompositionPattern"));
      }
   }

   public final String getDisplayName() {
      return this.getDisplayName(getDefault(Locale.Category.DISPLAY));
   }

   public String getDisplayName(Locale var1) {
      LocaleResources var2 = LocaleProviderAdapter.forJRE().getLocaleResources(var1);
      String var3 = this.getDisplayLanguage(var1);
      String var4 = this.getDisplayScript(var1);
      String var5 = this.getDisplayCountry(var1);
      String[] var6 = this.getDisplayVariantArray(var1);
      String var7 = var2.getLocaleName("DisplayNamePattern");
      String var8 = var2.getLocaleName("ListPattern");
      String var9 = var2.getLocaleName("ListCompositionPattern");
      String var10 = null;
      String[] var11 = null;
      if (var3.length() == 0 && var4.length() == 0 && var5.length() == 0) {
         return var6.length == 0 ? "" : formatList(var6, var8, var9);
      } else {
         ArrayList var12 = new ArrayList(4);
         if (var3.length() != 0) {
            var12.add(var3);
         }

         if (var4.length() != 0) {
            var12.add(var4);
         }

         if (var5.length() != 0) {
            var12.add(var5);
         }

         if (var6.length != 0) {
            var12.addAll(Arrays.asList(var6));
         }

         var10 = (String)var12.get(0);
         int var13 = var12.size();
         var11 = var13 > 1 ? (String[])var12.subList(1, var13).toArray(new String[var13 - 1]) : new String[0];
         Object[] var14 = new Object[]{new Integer(var11.length != 0 ? 2 : 1), var10, var11.length != 0 ? formatList(var11, var8, var9) : null};
         if (var7 != null) {
            return (new MessageFormat(var7)).format(var14);
         } else {
            StringBuilder var15 = new StringBuilder();
            var15.append((String)var14[1]);
            if (var14.length > 2) {
               var15.append(" (");
               var15.append((String)var14[2]);
               var15.append(')');
            }

            return var15.toString();
         }
      }
   }

   public Object clone() {
      try {
         Locale var1 = (Locale)super.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public int hashCode() {
      int var1 = this.hashCodeValue;
      if (var1 == 0) {
         var1 = this.baseLocale.hashCode();
         if (this.localeExtensions != null) {
            var1 ^= this.localeExtensions.hashCode();
         }

         this.hashCodeValue = var1;
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Locale)) {
         return false;
      } else {
         BaseLocale var2 = ((Locale)var1).baseLocale;
         if (!this.baseLocale.equals(var2)) {
            return false;
         } else if (this.localeExtensions == null) {
            return ((Locale)var1).localeExtensions == null;
         } else {
            return this.localeExtensions.equals(((Locale)var1).localeExtensions);
         }
      }
   }

   private String[] getDisplayVariantArray(Locale var1) {
      StringTokenizer var2 = new StringTokenizer(this.baseLocale.getVariant(), "_");
      String[] var3 = new String[var2.countTokens()];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = this.getDisplayString(var2.nextToken(), var1, 2);
      }

      return var3;
   }

   private static String formatList(String[] var0, String var1, String var2) {
      if (var1 != null && var2 != null) {
         if (var0.length > 3) {
            MessageFormat var5 = new MessageFormat(var2);
            var0 = composeList(var5, var0);
         }

         Object[] var6 = new Object[var0.length + 1];
         System.arraycopy(var0, 0, var6, 1, var0.length);
         var6[0] = new Integer(var0.length);
         MessageFormat var7 = new MessageFormat(var1);
         return var7.format(var6);
      } else {
         StringBuilder var3 = new StringBuilder();

         for(int var4 = 0; var4 < var0.length; ++var4) {
            if (var4 > 0) {
               var3.append(',');
            }

            var3.append(var0[var4]);
         }

         return var3.toString();
      }
   }

   private static String[] composeList(MessageFormat var0, String[] var1) {
      if (var1.length <= 3) {
         return var1;
      } else {
         String[] var2 = new String[]{var1[0], var1[1]};
         String var3 = var0.format(var2);
         String[] var4 = new String[var1.length - 1];
         System.arraycopy(var1, 2, var4, 1, var4.length - 1);
         var4[0] = var3;
         return composeList(var0, var4);
      }
   }

   private static boolean isUnicodeExtensionKey(String var0) {
      return var0.length() == 2 && LocaleUtils.isAlphaNumericString(var0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("language", this.baseLocale.getLanguage());
      var2.put("script", this.baseLocale.getScript());
      var2.put("country", this.baseLocale.getRegion());
      var2.put("variant", this.baseLocale.getVariant());
      var2.put("extensions", this.localeExtensions == null ? "" : this.localeExtensions.getID());
      var2.put("hashcode", (int)-1);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String var3 = (String)var2.get("language", "");
      String var4 = (String)var2.get("script", "");
      String var5 = (String)var2.get("country", "");
      String var6 = (String)var2.get("variant", "");
      String var7 = (String)var2.get("extensions", "");
      this.baseLocale = BaseLocale.getInstance(convertOldISOCodes(var3), var4, var5, var6);
      if (var7.length() > 0) {
         try {
            InternalLocaleBuilder var8 = new InternalLocaleBuilder();
            var8.setExtensions(var7);
            this.localeExtensions = var8.getLocaleExtensions();
         } catch (LocaleSyntaxException var9) {
            throw new IllformedLocaleException(var9.getMessage());
         }
      } else {
         this.localeExtensions = null;
      }

   }

   private Object readResolve() throws ObjectStreamException {
      return getInstance(this.baseLocale.getLanguage(), this.baseLocale.getScript(), this.baseLocale.getRegion(), this.baseLocale.getVariant(), this.localeExtensions);
   }

   private static String convertOldISOCodes(String var0) {
      var0 = LocaleUtils.toLowerString(var0).intern();
      if (var0 == "he") {
         return "iw";
      } else if (var0 == "yi") {
         return "ji";
      } else {
         return var0 == "id" ? "in" : var0;
      }
   }

   private static LocaleExtensions getCompatibilityExtensions(String var0, String var1, String var2, String var3) {
      LocaleExtensions var4 = null;
      if (LocaleUtils.caseIgnoreMatch(var0, "ja") && var1.length() == 0 && LocaleUtils.caseIgnoreMatch(var2, "jp") && "JP".equals(var3)) {
         var4 = LocaleExtensions.CALENDAR_JAPANESE;
      } else if (LocaleUtils.caseIgnoreMatch(var0, "th") && var1.length() == 0 && LocaleUtils.caseIgnoreMatch(var2, "th") && "TH".equals(var3)) {
         var4 = LocaleExtensions.NUMBER_THAI;
      }

      return var4;
   }

   public static List<Locale> filter(List<Locale.LanguageRange> var0, Collection<Locale> var1, Locale.FilteringMode var2) {
      return LocaleMatcher.filter(var0, var1, var2);
   }

   public static List<Locale> filter(List<Locale.LanguageRange> var0, Collection<Locale> var1) {
      return filter(var0, var1, Locale.FilteringMode.AUTOSELECT_FILTERING);
   }

   public static List<String> filterTags(List<Locale.LanguageRange> var0, Collection<String> var1, Locale.FilteringMode var2) {
      return LocaleMatcher.filterTags(var0, var1, var2);
   }

   public static List<String> filterTags(List<Locale.LanguageRange> var0, Collection<String> var1) {
      return filterTags(var0, var1, Locale.FilteringMode.AUTOSELECT_FILTERING);
   }

   public static Locale lookup(List<Locale.LanguageRange> var0, Collection<Locale> var1) {
      return LocaleMatcher.lookup(var0, var1);
   }

   public static String lookupTag(List<Locale.LanguageRange> var0, Collection<String> var1) {
      return LocaleMatcher.lookupTag(var0, var1);
   }

   // $FF: synthetic method
   Locale(BaseLocale var1, LocaleExtensions var2, Object var3) {
      this(var1, var2);
   }

   static {
      CHINA = SIMPLIFIED_CHINESE;
      PRC = SIMPLIFIED_CHINESE;
      TAIWAN = TRADITIONAL_CHINESE;
      UK = createConstant("en", "GB");
      US = createConstant("en", "US");
      CANADA = createConstant("en", "CA");
      CANADA_FRENCH = createConstant("fr", "CA");
      ROOT = createConstant("", "");
      defaultLocale = initDefault();
      defaultDisplayLocale = null;
      defaultFormatLocale = null;
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("language", String.class), new ObjectStreamField("country", String.class), new ObjectStreamField("variant", String.class), new ObjectStreamField("hashcode", Integer.TYPE), new ObjectStreamField("script", String.class), new ObjectStreamField("extensions", String.class)};
      isoLanguages = null;
      isoCountries = null;
   }

   public static final class LanguageRange {
      public static final double MAX_WEIGHT = 1.0D;
      public static final double MIN_WEIGHT = 0.0D;
      private final String range;
      private final double weight;
      private volatile int hash;

      public LanguageRange(String var1) {
         this(var1, 1.0D);
      }

      public LanguageRange(String var1, double var2) {
         this.hash = 0;
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var2 >= 0.0D && var2 <= 1.0D) {
            var1 = var1.toLowerCase();
            boolean var4 = false;
            String[] var5 = var1.split("-");
            if (!isSubtagIllFormed(var5[0], true) && !var1.endsWith("-")) {
               for(int var6 = 1; var6 < var5.length; ++var6) {
                  if (isSubtagIllFormed(var5[var6], false)) {
                     var4 = true;
                     break;
                  }
               }
            } else {
               var4 = true;
            }

            if (var4) {
               throw new IllegalArgumentException("range=" + var1);
            } else {
               this.range = var1;
               this.weight = var2;
            }
         } else {
            throw new IllegalArgumentException("weight=" + var2);
         }
      }

      private static boolean isSubtagIllFormed(String var0, boolean var1) {
         if (!var0.equals("") && var0.length() <= 8) {
            if (var0.equals("*")) {
               return false;
            } else {
               char[] var2 = var0.toCharArray();
               char[] var3;
               int var4;
               int var5;
               char var6;
               if (!var1) {
                  var3 = var2;
                  var4 = var2.length;

                  for(var5 = 0; var5 < var4; ++var5) {
                     var6 = var3[var5];
                     if (var6 < '0' || var6 > '9' && var6 < 'a' || var6 > 'z') {
                        return true;
                     }
                  }
               } else {
                  var3 = var2;
                  var4 = var2.length;

                  for(var5 = 0; var5 < var4; ++var5) {
                     var6 = var3[var5];
                     if (var6 < 'a' || var6 > 'z') {
                        return true;
                     }
                  }
               }

               return false;
            }
         } else {
            return true;
         }
      }

      public String getRange() {
         return this.range;
      }

      public double getWeight() {
         return this.weight;
      }

      public static List<Locale.LanguageRange> parse(String var0) {
         return LocaleMatcher.parse(var0);
      }

      public static List<Locale.LanguageRange> parse(String var0, Map<String, List<String>> var1) {
         return mapEquivalents(parse(var0), var1);
      }

      public static List<Locale.LanguageRange> mapEquivalents(List<Locale.LanguageRange> var0, Map<String, List<String>> var1) {
         return LocaleMatcher.mapEquivalents(var0, var1);
      }

      public int hashCode() {
         if (this.hash == 0) {
            byte var1 = 17;
            int var4 = 37 * var1 + this.range.hashCode();
            long var2 = Double.doubleToLongBits(this.weight);
            var4 = 37 * var4 + (int)(var2 ^ var2 >>> 32);
            this.hash = var4;
         }

         return this.hash;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Locale.LanguageRange)) {
            return false;
         } else {
            Locale.LanguageRange var2 = (Locale.LanguageRange)var1;
            return this.hash == var2.hash && this.range.equals(var2.range) && this.weight == var2.weight;
         }
      }
   }

   public static enum FilteringMode {
      AUTOSELECT_FILTERING,
      EXTENDED_FILTERING,
      IGNORE_EXTENDED_RANGES,
      MAP_EXTENDED_RANGES,
      REJECT_EXTENDED_RANGES;
   }

   public static final class Builder {
      private final InternalLocaleBuilder localeBuilder = new InternalLocaleBuilder();

      public Locale.Builder setLocale(Locale var1) {
         try {
            this.localeBuilder.setLocale(var1.baseLocale, var1.localeExtensions);
            return this;
         } catch (LocaleSyntaxException var3) {
            throw new IllformedLocaleException(var3.getMessage(), var3.getErrorIndex());
         }
      }

      public Locale.Builder setLanguageTag(String var1) {
         ParseStatus var2 = new ParseStatus();
         LanguageTag var3 = LanguageTag.parse(var1, var2);
         if (var2.isError()) {
            throw new IllformedLocaleException(var2.getErrorMessage(), var2.getErrorIndex());
         } else {
            this.localeBuilder.setLanguageTag(var3);
            return this;
         }
      }

      public Locale.Builder setLanguage(String var1) {
         try {
            this.localeBuilder.setLanguage(var1);
            return this;
         } catch (LocaleSyntaxException var3) {
            throw new IllformedLocaleException(var3.getMessage(), var3.getErrorIndex());
         }
      }

      public Locale.Builder setScript(String var1) {
         try {
            this.localeBuilder.setScript(var1);
            return this;
         } catch (LocaleSyntaxException var3) {
            throw new IllformedLocaleException(var3.getMessage(), var3.getErrorIndex());
         }
      }

      public Locale.Builder setRegion(String var1) {
         try {
            this.localeBuilder.setRegion(var1);
            return this;
         } catch (LocaleSyntaxException var3) {
            throw new IllformedLocaleException(var3.getMessage(), var3.getErrorIndex());
         }
      }

      public Locale.Builder setVariant(String var1) {
         try {
            this.localeBuilder.setVariant(var1);
            return this;
         } catch (LocaleSyntaxException var3) {
            throw new IllformedLocaleException(var3.getMessage(), var3.getErrorIndex());
         }
      }

      public Locale.Builder setExtension(char var1, String var2) {
         try {
            this.localeBuilder.setExtension(var1, var2);
            return this;
         } catch (LocaleSyntaxException var4) {
            throw new IllformedLocaleException(var4.getMessage(), var4.getErrorIndex());
         }
      }

      public Locale.Builder setUnicodeLocaleKeyword(String var1, String var2) {
         try {
            this.localeBuilder.setUnicodeLocaleKeyword(var1, var2);
            return this;
         } catch (LocaleSyntaxException var4) {
            throw new IllformedLocaleException(var4.getMessage(), var4.getErrorIndex());
         }
      }

      public Locale.Builder addUnicodeLocaleAttribute(String var1) {
         try {
            this.localeBuilder.addUnicodeLocaleAttribute(var1);
            return this;
         } catch (LocaleSyntaxException var3) {
            throw new IllformedLocaleException(var3.getMessage(), var3.getErrorIndex());
         }
      }

      public Locale.Builder removeUnicodeLocaleAttribute(String var1) {
         try {
            this.localeBuilder.removeUnicodeLocaleAttribute(var1);
            return this;
         } catch (LocaleSyntaxException var3) {
            throw new IllformedLocaleException(var3.getMessage(), var3.getErrorIndex());
         }
      }

      public Locale.Builder clear() {
         this.localeBuilder.clear();
         return this;
      }

      public Locale.Builder clearExtensions() {
         this.localeBuilder.clearExtensions();
         return this;
      }

      public Locale build() {
         BaseLocale var1 = this.localeBuilder.getBaseLocale();
         LocaleExtensions var2 = this.localeBuilder.getLocaleExtensions();
         if (var2 == null && var1.getVariant().length() > 0) {
            var2 = Locale.getCompatibilityExtensions(var1.getLanguage(), var1.getScript(), var1.getRegion(), var1.getVariant());
         }

         return Locale.getInstance(var1, var2);
      }
   }

   public static enum Category {
      DISPLAY("user.language.display", "user.script.display", "user.country.display", "user.variant.display"),
      FORMAT("user.language.format", "user.script.format", "user.country.format", "user.variant.format");

      final String languageKey;
      final String scriptKey;
      final String countryKey;
      final String variantKey;

      private Category(String var3, String var4, String var5, String var6) {
         this.languageKey = var3;
         this.scriptKey = var4;
         this.countryKey = var5;
         this.variantKey = var6;
      }
   }

   private static class LocaleNameGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<LocaleNameProvider, String> {
      private static final Locale.LocaleNameGetter INSTANCE = new Locale.LocaleNameGetter();

      public String getObject(LocaleNameProvider var1, Locale var2, String var3, Object... var4) {
         assert var4.length == 2;

         int var5 = (Integer)var4[0];
         String var6 = (String)var4[1];
         switch(var5) {
         case 0:
            return var1.getDisplayLanguage(var6, var2);
         case 1:
            return var1.getDisplayCountry(var6, var2);
         case 2:
            return var1.getDisplayVariant(var6, var2);
         case 3:
            return var1.getDisplayScript(var6, var2);
         default:
            assert false;

            return null;
         }
      }
   }

   private static final class LocaleKey {
      private final BaseLocale base;
      private final LocaleExtensions exts;
      private final int hash;

      private LocaleKey(BaseLocale var1, LocaleExtensions var2) {
         this.base = var1;
         this.exts = var2;
         int var3 = this.base.hashCode();
         if (this.exts != null) {
            var3 ^= this.exts.hashCode();
         }

         this.hash = var3;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Locale.LocaleKey)) {
            return false;
         } else {
            Locale.LocaleKey var2 = (Locale.LocaleKey)var1;
            if (this.hash == var2.hash && this.base.equals(var2.base)) {
               if (this.exts == null) {
                  return var2.exts == null;
               } else {
                  return this.exts.equals(var2.exts);
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.hash;
      }

      // $FF: synthetic method
      LocaleKey(BaseLocale var1, LocaleExtensions var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class Cache extends LocaleObjectCache<Locale.LocaleKey, Locale> {
      private Cache() {
      }

      protected Locale createObject(Locale.LocaleKey var1) {
         return new Locale(var1.base, var1.exts);
      }

      // $FF: synthetic method
      Cache(Object var1) {
         this();
      }
   }
}
