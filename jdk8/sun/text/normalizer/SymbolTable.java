package sun.text.normalizer;

import java.text.ParsePosition;

/** @deprecated */
@Deprecated
public interface SymbolTable {
   /** @deprecated */
   @Deprecated
   char SYMBOL_REF = '$';

   /** @deprecated */
   @Deprecated
   char[] lookup(String var1);

   /** @deprecated */
   @Deprecated
   UnicodeMatcher lookupMatcher(int var1);

   /** @deprecated */
   @Deprecated
   String parseReference(String var1, ParsePosition var2, int var3);
}
