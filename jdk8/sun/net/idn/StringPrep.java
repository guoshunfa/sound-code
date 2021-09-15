package sun.net.idn;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import sun.text.Normalizer;
import sun.text.normalizer.CharTrie;
import sun.text.normalizer.NormalizerImpl;
import sun.text.normalizer.Trie;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UCharacterIterator;
import sun.text.normalizer.UTF16;
import sun.text.normalizer.VersionInfo;

public final class StringPrep {
   public static final int DEFAULT = 0;
   public static final int ALLOW_UNASSIGNED = 1;
   private static final int UNASSIGNED = 0;
   private static final int MAP = 1;
   private static final int PROHIBITED = 2;
   private static final int DELETE = 3;
   private static final int TYPE_LIMIT = 4;
   private static final int NORMALIZATION_ON = 1;
   private static final int CHECK_BIDI_ON = 2;
   private static final int TYPE_THRESHOLD = 65520;
   private static final int MAX_INDEX_VALUE = 16319;
   private static final int MAX_INDEX_TOP_LENGTH = 3;
   private static final int INDEX_TRIE_SIZE = 0;
   private static final int INDEX_MAPPING_DATA_SIZE = 1;
   private static final int NORM_CORRECTNS_LAST_UNI_VERSION = 2;
   private static final int ONE_UCHAR_MAPPING_INDEX_START = 3;
   private static final int TWO_UCHARS_MAPPING_INDEX_START = 4;
   private static final int THREE_UCHARS_MAPPING_INDEX_START = 5;
   private static final int FOUR_UCHARS_MAPPING_INDEX_START = 6;
   private static final int OPTIONS = 7;
   private static final int INDEX_TOP = 16;
   private static final int DATA_BUFFER_SIZE = 25000;
   private StringPrep.StringPrepTrieImpl sprepTrieImpl;
   private int[] indexes;
   private char[] mappingData;
   private byte[] formatVersion;
   private VersionInfo sprepUniVer;
   private VersionInfo normCorrVer;
   private boolean doNFKC;
   private boolean checkBiDi;

   private char getCodePointValue(int var1) {
      return this.sprepTrieImpl.sprepTrie.getCodePointValue(var1);
   }

   private static VersionInfo getVersionInfo(int var0) {
      int var1 = var0 & 255;
      int var2 = var0 >> 8 & 255;
      int var3 = var0 >> 16 & 255;
      int var4 = var0 >> 24 & 255;
      return VersionInfo.getInstance(var4, var3, var2, var1);
   }

   private static VersionInfo getVersionInfo(byte[] var0) {
      return var0.length != 4 ? null : VersionInfo.getInstance(var0[0], var0[1], var0[2], var0[3]);
   }

   public StringPrep(InputStream var1) throws IOException {
      BufferedInputStream var2 = new BufferedInputStream(var1, 25000);
      StringPrepDataReader var3 = new StringPrepDataReader(var2);
      this.indexes = var3.readIndexes(16);
      byte[] var4 = new byte[this.indexes[0]];
      this.mappingData = new char[this.indexes[1] / 2];
      var3.read(var4, this.mappingData);
      this.sprepTrieImpl = new StringPrep.StringPrepTrieImpl();
      this.sprepTrieImpl.sprepTrie = new CharTrie(new ByteArrayInputStream(var4), this.sprepTrieImpl);
      this.formatVersion = var3.getDataFormatVersion();
      this.doNFKC = (this.indexes[7] & 1) > 0;
      this.checkBiDi = (this.indexes[7] & 2) > 0;
      this.sprepUniVer = getVersionInfo(var3.getUnicodeVersion());
      this.normCorrVer = getVersionInfo(this.indexes[2]);
      VersionInfo var5 = NormalizerImpl.getUnicodeVersion();
      if (var5.compareTo(this.sprepUniVer) < 0 && var5.compareTo(this.normCorrVer) < 0 && (this.indexes[7] & 1) > 0) {
         throw new IOException("Normalization Correction version not supported");
      } else {
         var2.close();
      }
   }

   private static final void getValues(char var0, StringPrep.Values var1) {
      var1.reset();
      if (var0 == 0) {
         var1.type = 4;
      } else if (var0 >= '\ufff0') {
         var1.type = var0 - '\ufff0';
      } else {
         var1.type = 1;
         if ((var0 & 2) > 0) {
            var1.isIndex = true;
            var1.value = var0 >> 2;
         } else {
            var1.isIndex = false;
            var1.value = var0 << 16 >> 16;
            var1.value >>= 2;
         }

         if (var0 >> 2 == 16319) {
            var1.type = 3;
            var1.isIndex = false;
            var1.value = 0;
         }
      }

   }

   private StringBuffer map(UCharacterIterator var1, int var2) throws ParseException {
      StringPrep.Values var3 = new StringPrep.Values();
      boolean var4 = false;
      boolean var5 = true;
      StringBuffer var6 = new StringBuffer();
      boolean var7 = (var2 & 1) > 0;

      while(true) {
         int var11;
         while((var11 = var1.nextCodePoint()) != -1) {
            char var10 = this.getCodePointValue(var11);
            getValues(var10, var3);
            if (var3.type == 0 && !var7) {
               throw new ParseException("An unassigned code point was found in the input " + var1.getText(), var1.getIndex());
            }

            if (var3.type == 1) {
               if (var3.isIndex) {
                  int var8 = var3.value;
                  char var9;
                  if (var8 >= this.indexes[3] && var8 < this.indexes[4]) {
                     var9 = 1;
                  } else if (var8 >= this.indexes[4] && var8 < this.indexes[5]) {
                     var9 = 2;
                  } else if (var8 >= this.indexes[5] && var8 < this.indexes[6]) {
                     var9 = 3;
                  } else {
                     var9 = this.mappingData[var8++];
                  }

                  var6.append((char[])this.mappingData, var8, var9);
                  continue;
               }

               var11 -= var3.value;
            } else if (var3.type == 3) {
               continue;
            }

            UTF16.append(var6, var11);
         }

         return var6;
      }
   }

   private StringBuffer normalize(StringBuffer var1) {
      return new StringBuffer(Normalizer.normalize(var1.toString(), java.text.Normalizer.Form.NFKC, 262432));
   }

   public StringBuffer prepare(UCharacterIterator var1, int var2) throws ParseException {
      StringBuffer var3 = this.map(var1, var2);
      StringBuffer var4 = var3;
      if (this.doNFKC) {
         var4 = this.normalize(var3);
      }

      UCharacterIterator var7 = UCharacterIterator.getInstance(var4);
      StringPrep.Values var8 = new StringPrep.Values();
      int var9 = 19;
      int var10 = 19;
      int var11 = -1;
      int var12 = -1;
      boolean var13 = false;
      boolean var14 = false;

      int var5;
      while((var5 = var7.nextCodePoint()) != -1) {
         char var6 = this.getCodePointValue(var5);
         getValues(var6, var8);
         if (var8.type == 2) {
            throw new ParseException("A prohibited code point was found in the input" + var7.getText(), var8.value);
         }

         var9 = UCharacter.getDirection(var5);
         if (var10 == 19) {
            var10 = var9;
         }

         if (var9 == 0) {
            var14 = true;
            var12 = var7.getIndex() - 1;
         }

         if (var9 == 1 || var9 == 13) {
            var13 = true;
            var11 = var7.getIndex() - 1;
         }
      }

      if (this.checkBiDi) {
         if (var14 && var13) {
            throw new ParseException("The input does not conform to the rules for BiDi code points." + var7.getText(), var11 > var12 ? var11 : var12);
         }

         if (var13 && (var10 != 1 && var10 != 13 || var9 != 1 && var9 != 13)) {
            throw new ParseException("The input does not conform to the rules for BiDi code points." + var7.getText(), var11 > var12 ? var11 : var12);
         }
      }

      return var4;
   }

   private static final class Values {
      boolean isIndex;
      int value;
      int type;

      private Values() {
      }

      public void reset() {
         this.isIndex = false;
         this.value = 0;
         this.type = -1;
      }

      // $FF: synthetic method
      Values(Object var1) {
         this();
      }
   }

   private static final class StringPrepTrieImpl implements Trie.DataManipulate {
      private CharTrie sprepTrie;

      private StringPrepTrieImpl() {
         this.sprepTrie = null;
      }

      public int getFoldingOffset(int var1) {
         return var1;
      }

      // $FF: synthetic method
      StringPrepTrieImpl(Object var1) {
         this();
      }
   }
}
