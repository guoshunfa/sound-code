package java.text.spi;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class BreakIteratorProvider extends LocaleServiceProvider {
   protected BreakIteratorProvider() {
   }

   public abstract BreakIterator getWordInstance(Locale var1);

   public abstract BreakIterator getLineInstance(Locale var1);

   public abstract BreakIterator getCharacterInstance(Locale var1);

   public abstract BreakIterator getSentenceInstance(Locale var1);
}
