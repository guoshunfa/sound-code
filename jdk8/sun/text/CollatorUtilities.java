package sun.text;

import sun.text.normalizer.NormalizerBase;

public class CollatorUtilities {
   static NormalizerBase.Mode[] legacyModeMap;

   public static int toLegacyMode(NormalizerBase.Mode var0) {
      int var1 = legacyModeMap.length;

      while(var1 > 0) {
         --var1;
         if (legacyModeMap[var1] == var0) {
            break;
         }
      }

      return var1;
   }

   public static NormalizerBase.Mode toNormalizerMode(int var0) {
      NormalizerBase.Mode var1;
      try {
         var1 = legacyModeMap[var0];
      } catch (ArrayIndexOutOfBoundsException var3) {
         var1 = NormalizerBase.NONE;
      }

      return var1;
   }

   static {
      legacyModeMap = new NormalizerBase.Mode[]{NormalizerBase.NONE, NormalizerBase.NFD, NormalizerBase.NFKD};
   }
}
