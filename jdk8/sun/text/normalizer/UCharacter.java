package sun.text.normalizer;

import java.io.IOException;
import java.util.MissingResourceException;

public final class UCharacter {
   public static final int MIN_VALUE = 0;
   public static final int MAX_VALUE = 1114111;
   public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
   private static final UCharacterProperty PROPERTY_;
   private static final char[] PROPERTY_TRIE_INDEX_;
   private static final char[] PROPERTY_TRIE_DATA_;
   private static final int PROPERTY_INITIAL_VALUE_;
   private static final UBiDiProps gBdp;
   private static final int NUMERIC_TYPE_SHIFT_ = 5;
   private static final int NUMERIC_TYPE_MASK_ = 224;

   public static int digit(int var0, int var1) {
      int var2 = getProperty(var0);
      int var3;
      if (getNumericType(var2) == 1) {
         var3 = UCharacterProperty.getUnsignedValue(var2);
      } else {
         var3 = getEuropeanDigit(var0);
      }

      return 0 <= var3 && var3 < var1 ? var3 : -1;
   }

   public static int getDirection(int var0) {
      return gBdp.getClass(var0);
   }

   public static int getCodePoint(char var0, char var1) {
      if (UTF16.isLeadSurrogate(var0) && UTF16.isTrailSurrogate(var1)) {
         return UCharacterProperty.getRawSupplementary(var0, var1);
      } else {
         throw new IllegalArgumentException("Illegal surrogate characters");
      }
   }

   public static VersionInfo getAge(int var0) {
      if (var0 >= 0 && var0 <= 1114111) {
         return PROPERTY_.getAge(var0);
      } else {
         throw new IllegalArgumentException("Codepoint out of bounds");
      }
   }

   private static int getEuropeanDigit(int var0) {
      if ((var0 <= 122 || var0 >= 65313) && var0 >= 65 && (var0 <= 90 || var0 >= 97) && var0 <= 65370 && (var0 <= 65338 || var0 >= 65345)) {
         if (var0 <= 122) {
            return var0 + 10 - (var0 <= 90 ? 65 : 97);
         } else {
            return var0 <= 65338 ? var0 + 10 - 'Ａ' : var0 + 10 - 'ａ';
         }
      } else {
         return -1;
      }
   }

   private static int getNumericType(int var0) {
      return (var0 & 224) >> 5;
   }

   private static final int getProperty(int var0) {
      if (var0 < 55296 || var0 > 56319 && var0 < 65536) {
         try {
            return PROPERTY_TRIE_DATA_[(PROPERTY_TRIE_INDEX_[var0 >> 5] << 2) + (var0 & 31)];
         } catch (ArrayIndexOutOfBoundsException var2) {
            return PROPERTY_INITIAL_VALUE_;
         }
      } else if (var0 <= 56319) {
         return PROPERTY_TRIE_DATA_[(PROPERTY_TRIE_INDEX_[320 + (var0 >> 5)] << 2) + (var0 & 31)];
      } else {
         return var0 <= 1114111 ? PROPERTY_.m_trie_.getSurrogateValue(UTF16.getLeadSurrogate(var0), (char)(var0 & 1023)) : PROPERTY_INITIAL_VALUE_;
      }
   }

   static {
      try {
         PROPERTY_ = UCharacterProperty.getInstance();
         PROPERTY_TRIE_INDEX_ = PROPERTY_.m_trieIndex_;
         PROPERTY_TRIE_DATA_ = PROPERTY_.m_trieData_;
         PROPERTY_INITIAL_VALUE_ = PROPERTY_.m_trieInitialValue_;
      } catch (Exception var3) {
         throw new MissingResourceException(var3.getMessage(), "", "");
      }

      UBiDiProps var0;
      try {
         var0 = UBiDiProps.getSingleton();
      } catch (IOException var2) {
         var0 = UBiDiProps.getDummy();
      }

      gBdp = var0;
   }

   public interface NumericType {
      int DECIMAL = 1;
   }
}
