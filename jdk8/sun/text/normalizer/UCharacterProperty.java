package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;

public final class UCharacterProperty {
   public CharTrie m_trie_;
   public char[] m_trieIndex_;
   public char[] m_trieData_;
   public int m_trieInitialValue_;
   public VersionInfo m_unicodeVersion_;
   public static final int SRC_PROPSVEC = 2;
   public static final int SRC_COUNT = 9;
   CharTrie m_additionalTrie_;
   int[] m_additionalVectors_;
   int m_additionalColumnsCount_;
   int m_maxBlockScriptValue_;
   int m_maxJTGValue_;
   private static UCharacterProperty INSTANCE_ = null;
   private static final String DATA_FILE_NAME_ = "/sun/text/resources/uprops.icu";
   private static final int DATA_BUFFER_SIZE_ = 25000;
   private static final int VALUE_SHIFT_ = 8;
   private static final int UNSIGNED_VALUE_MASK_AFTER_SHIFT_ = 255;
   private static final int LEAD_SURROGATE_SHIFT_ = 10;
   private static final int SURROGATE_OFFSET_ = -56613888;
   private static final int FIRST_NIBBLE_SHIFT_ = 4;
   private static final int LAST_NIBBLE_MASK_ = 15;
   private static final int AGE_SHIFT_ = 24;

   public void setIndexData(CharTrie.FriendAgent var1) {
      this.m_trieIndex_ = var1.getPrivateIndex();
      this.m_trieData_ = var1.getPrivateData();
      this.m_trieInitialValue_ = var1.getPrivateInitialValue();
   }

   public final int getProperty(int var1) {
      if (var1 < 55296 || var1 > 56319 && var1 < 65536) {
         try {
            return this.m_trieData_[(this.m_trieIndex_[var1 >> 5] << 2) + (var1 & 31)];
         } catch (ArrayIndexOutOfBoundsException var3) {
            return this.m_trieInitialValue_;
         }
      } else if (var1 <= 56319) {
         return this.m_trieData_[(this.m_trieIndex_[320 + (var1 >> 5)] << 2) + (var1 & 31)];
      } else {
         return var1 <= 1114111 ? this.m_trie_.getSurrogateValue(UTF16.getLeadSurrogate(var1), (char)(var1 & 1023)) : this.m_trieInitialValue_;
      }
   }

   public static int getUnsignedValue(int var0) {
      return var0 >> 8 & 255;
   }

   public int getAdditional(int var1, int var2) {
      if (var2 == -1) {
         return this.getProperty(var1);
      } else {
         return var2 >= 0 && var2 < this.m_additionalColumnsCount_ ? this.m_additionalVectors_[this.m_additionalTrie_.getCodePointValue(var1) + var2] : 0;
      }
   }

   public VersionInfo getAge(int var1) {
      int var2 = this.getAdditional(var1, 0) >> 24;
      return VersionInfo.getInstance(var2 >> 4 & 15, var2 & 15, 0, 0);
   }

   public static int getRawSupplementary(char var0, char var1) {
      return (var0 << 10) + var1 + -56613888;
   }

   public static UCharacterProperty getInstance() {
      if (INSTANCE_ == null) {
         try {
            INSTANCE_ = new UCharacterProperty();
         } catch (Exception var1) {
            throw new MissingResourceException(var1.getMessage(), "", "");
         }
      }

      return INSTANCE_;
   }

   public static boolean isRuleWhiteSpace(int var0) {
      return var0 >= 9 && var0 <= 8233 && (var0 <= 13 || var0 == 32 || var0 == 133 || var0 == 8206 || var0 == 8207 || var0 >= 8232);
   }

   private UCharacterProperty() throws IOException {
      InputStream var1 = ICUData.getRequiredStream("/sun/text/resources/uprops.icu");
      BufferedInputStream var2 = new BufferedInputStream(var1, 25000);
      UCharacterPropertyReader var3 = new UCharacterPropertyReader(var2);
      var3.read(this);
      var2.close();
      this.m_trie_.putIndexData(this);
   }

   public void upropsvec_addPropertyStarts(UnicodeSet var1) {
      if (this.m_additionalColumnsCount_ > 0) {
         TrieIterator var2 = new TrieIterator(this.m_additionalTrie_);
         RangeValueIterator.Element var3 = new RangeValueIterator.Element();

         while(var2.next(var3)) {
            var1.add(var3.start);
         }
      }

   }
}
