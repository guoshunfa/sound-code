package java.text;

import sun.text.normalizer.NormalizerBase;

public final class Normalizer {
   private Normalizer() {
   }

   public static String normalize(CharSequence var0, Normalizer.Form var1) {
      return NormalizerBase.normalize(var0.toString(), var1);
   }

   public static boolean isNormalized(CharSequence var0, Normalizer.Form var1) {
      return NormalizerBase.isNormalized(var0.toString(), var1);
   }

   public static enum Form {
      NFD,
      NFC,
      NFKD,
      NFKC;
   }
}
