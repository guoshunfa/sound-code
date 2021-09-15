package java.text;

import java.lang.ref.SoftReference;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class BreakIterator implements Cloneable {
   public static final int DONE = -1;
   private static final int CHARACTER_INDEX = 0;
   private static final int WORD_INDEX = 1;
   private static final int LINE_INDEX = 2;
   private static final int SENTENCE_INDEX = 3;
   private static final SoftReference<BreakIterator.BreakIteratorCache>[] iterCache = (SoftReference[])(new SoftReference[4]);

   protected BreakIterator() {
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public abstract int first();

   public abstract int last();

   public abstract int next(int var1);

   public abstract int next();

   public abstract int previous();

   public abstract int following(int var1);

   public int preceding(int var1) {
      int var2;
      for(var2 = this.following(var1); var2 >= var1 && var2 != -1; var2 = this.previous()) {
      }

      return var2;
   }

   public boolean isBoundary(int var1) {
      if (var1 == 0) {
         return true;
      } else {
         int var2 = this.following(var1 - 1);
         if (var2 == -1) {
            throw new IllegalArgumentException();
         } else {
            return var2 == var1;
         }
      }
   }

   public abstract int current();

   public abstract CharacterIterator getText();

   public void setText(String var1) {
      this.setText((CharacterIterator)(new StringCharacterIterator(var1)));
   }

   public abstract void setText(CharacterIterator var1);

   public static BreakIterator getWordInstance() {
      return getWordInstance(Locale.getDefault());
   }

   public static BreakIterator getWordInstance(Locale var0) {
      return getBreakInstance(var0, 1);
   }

   public static BreakIterator getLineInstance() {
      return getLineInstance(Locale.getDefault());
   }

   public static BreakIterator getLineInstance(Locale var0) {
      return getBreakInstance(var0, 2);
   }

   public static BreakIterator getCharacterInstance() {
      return getCharacterInstance(Locale.getDefault());
   }

   public static BreakIterator getCharacterInstance(Locale var0) {
      return getBreakInstance(var0, 0);
   }

   public static BreakIterator getSentenceInstance() {
      return getSentenceInstance(Locale.getDefault());
   }

   public static BreakIterator getSentenceInstance(Locale var0) {
      return getBreakInstance(var0, 3);
   }

   private static BreakIterator getBreakInstance(Locale var0, int var1) {
      if (iterCache[var1] != null) {
         BreakIterator.BreakIteratorCache var2 = (BreakIterator.BreakIteratorCache)iterCache[var1].get();
         if (var2 != null && var2.getLocale().equals(var0)) {
            return var2.createBreakInstance();
         }
      }

      BreakIterator var4 = createBreakInstance(var0, var1);
      BreakIterator.BreakIteratorCache var3 = new BreakIterator.BreakIteratorCache(var0, var4);
      iterCache[var1] = new SoftReference(var3);
      return var4;
   }

   private static BreakIterator createBreakInstance(Locale var0, int var1) {
      LocaleProviderAdapter var2 = LocaleProviderAdapter.getAdapter(BreakIteratorProvider.class, var0);
      BreakIterator var3 = createBreakInstance(var2, var0, var1);
      if (var3 == null) {
         var3 = createBreakInstance(LocaleProviderAdapter.forJRE(), var0, var1);
      }

      return var3;
   }

   private static BreakIterator createBreakInstance(LocaleProviderAdapter var0, Locale var1, int var2) {
      BreakIteratorProvider var3 = var0.getBreakIteratorProvider();
      BreakIterator var4 = null;
      switch(var2) {
      case 0:
         var4 = var3.getCharacterInstance(var1);
         break;
      case 1:
         var4 = var3.getWordInstance(var1);
         break;
      case 2:
         var4 = var3.getLineInstance(var1);
         break;
      case 3:
         var4 = var3.getSentenceInstance(var1);
      }

      return var4;
   }

   public static synchronized Locale[] getAvailableLocales() {
      LocaleServiceProviderPool var0 = LocaleServiceProviderPool.getPool(BreakIteratorProvider.class);
      return var0.getAvailableLocales();
   }

   private static final class BreakIteratorCache {
      private BreakIterator iter;
      private Locale locale;

      BreakIteratorCache(Locale var1, BreakIterator var2) {
         this.locale = var1;
         this.iter = (BreakIterator)var2.clone();
      }

      Locale getLocale() {
         return this.locale;
      }

      BreakIterator createBreakInstance() {
         return (BreakIterator)this.iter.clone();
      }
   }
}
