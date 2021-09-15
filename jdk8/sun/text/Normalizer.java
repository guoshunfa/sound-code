package sun.text;

import sun.text.normalizer.NormalizerBase;
import sun.text.normalizer.NormalizerImpl;

public final class Normalizer {
   public static final int UNICODE_3_2 = 262432;

   private Normalizer() {
   }

   public static String normalize(CharSequence var0, java.text.Normalizer.Form var1, int var2) {
      return NormalizerBase.normalize(var0.toString(), var1, var2);
   }

   public static boolean isNormalized(CharSequence var0, java.text.Normalizer.Form var1, int var2) {
      return NormalizerBase.isNormalized(var0.toString(), var1, var2);
   }

   public static final int getCombiningClass(int var0) {
      return NormalizerImpl.getCombiningClass(var0);
   }
}
