package sun.util.locale.provider;

import java.io.IOException;
import java.text.BreakIterator;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class BreakIteratorProviderImpl extends BreakIteratorProvider implements AvailableLanguageTags {
   private static final int CHARACTER_INDEX = 0;
   private static final int WORD_INDEX = 1;
   private static final int LINE_INDEX = 2;
   private static final int SENTENCE_INDEX = 3;
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public BreakIteratorProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public BreakIterator getWordInstance(Locale var1) {
      return this.getBreakInstance(var1, 1, "WordData", "WordDictionary");
   }

   public BreakIterator getLineInstance(Locale var1) {
      return this.getBreakInstance(var1, 2, "LineData", "LineDictionary");
   }

   public BreakIterator getCharacterInstance(Locale var1) {
      return this.getBreakInstance(var1, 0, "CharacterData", "CharacterDictionary");
   }

   public BreakIterator getSentenceInstance(Locale var1) {
      return this.getBreakInstance(var1, 3, "SentenceData", "SentenceDictionary");
   }

   private BreakIterator getBreakInstance(Locale var1, int var2, String var3, String var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LocaleResources var5 = LocaleProviderAdapter.forJRE().getLocaleResources(var1);
         String[] var6 = (String[])((String[])var5.getBreakIteratorInfo("BreakIteratorClasses"));
         String var7 = (String)var5.getBreakIteratorInfo(var3);

         try {
            String var8 = var6[var2];
            byte var9 = -1;
            switch(var8.hashCode()) {
            case 1249121520:
               if (var8.equals("DictionaryBasedBreakIterator")) {
                  var9 = 1;
               }
               break;
            case 1909671382:
               if (var8.equals("RuleBasedBreakIterator")) {
                  var9 = 0;
               }
            }

            switch(var9) {
            case 0:
               return new RuleBasedBreakIterator(var7);
            case 1:
               String var10 = (String)var5.getBreakIteratorInfo(var4);
               return new DictionaryBasedBreakIterator(var7, var10);
            default:
               throw new IllegalArgumentException("Invalid break iterator class \"" + var6[var2] + "\"");
            }
         } catch (MissingResourceException | IllegalArgumentException | IOException var11) {
            throw new InternalError(var11.toString(), var11);
         }
      }
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }
}
