package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class NormalizerImpl {
   static final NormalizerImpl IMPL;
   static final int UNSIGNED_BYTE_MASK = 255;
   static final long UNSIGNED_INT_MASK = 4294967295L;
   private static final String DATA_FILE_NAME = "/sun/text/resources/unorm.icu";
   public static final int QC_NFC = 17;
   public static final int QC_NFKC = 34;
   public static final int QC_NFD = 4;
   public static final int QC_NFKD = 8;
   public static final int QC_ANY_NO = 15;
   public static final int QC_MAYBE = 16;
   public static final int QC_ANY_MAYBE = 48;
   public static final int QC_MASK = 63;
   private static final int COMBINES_FWD = 64;
   private static final int COMBINES_BACK = 128;
   public static final int COMBINES_ANY = 192;
   private static final int CC_SHIFT = 8;
   public static final int CC_MASK = 65280;
   private static final int EXTRA_SHIFT = 16;
   private static final long MIN_SPECIAL = 4227858432L;
   private static final long SURROGATES_TOP = 4293918720L;
   private static final long MIN_HANGUL = 4293918720L;
   private static final long JAMO_V_TOP = 4294115328L;
   static final int INDEX_TRIE_SIZE = 0;
   static final int INDEX_CHAR_COUNT = 1;
   static final int INDEX_COMBINE_DATA_COUNT = 2;
   public static final int INDEX_MIN_NFC_NO_MAYBE = 6;
   public static final int INDEX_MIN_NFKC_NO_MAYBE = 7;
   public static final int INDEX_MIN_NFD_NO_MAYBE = 8;
   public static final int INDEX_MIN_NFKD_NO_MAYBE = 9;
   static final int INDEX_FCD_TRIE_SIZE = 10;
   static final int INDEX_AUX_TRIE_SIZE = 11;
   static final int INDEX_TOP = 32;
   private static final int AUX_UNSAFE_SHIFT = 11;
   private static final int AUX_COMP_EX_SHIFT = 10;
   private static final int AUX_NFC_SKIPPABLE_F_SHIFT = 12;
   private static final int AUX_MAX_FNC = 1024;
   private static final int AUX_UNSAFE_MASK = 2048;
   private static final int AUX_FNC_MASK = 1023;
   private static final int AUX_COMP_EX_MASK = 1024;
   private static final long AUX_NFC_SKIP_F_MASK = 4096L;
   private static final int MAX_BUFFER_SIZE = 20;
   private static NormalizerImpl.FCDTrieImpl fcdTrieImpl;
   private static NormalizerImpl.NormTrieImpl normTrieImpl;
   private static NormalizerImpl.AuxTrieImpl auxTrieImpl;
   private static int[] indexes;
   private static char[] combiningTable;
   private static char[] extraData;
   private static boolean isDataLoaded;
   private static boolean isFormatVersion_2_1;
   private static boolean isFormatVersion_2_2;
   private static byte[] unicodeVersion;
   private static final int DATA_BUFFER_SIZE = 25000;
   public static final int MIN_WITH_LEAD_CC = 768;
   private static final int DECOMP_FLAG_LENGTH_HAS_CC = 128;
   private static final int DECOMP_LENGTH_MASK = 127;
   private static final int BMP_INDEX_LENGTH = 2048;
   private static final int SURROGATE_BLOCK_BITS = 5;
   public static final int JAMO_L_BASE = 4352;
   public static final int JAMO_V_BASE = 4449;
   public static final int JAMO_T_BASE = 4519;
   public static final int HANGUL_BASE = 44032;
   public static final int JAMO_L_COUNT = 19;
   public static final int JAMO_V_COUNT = 21;
   public static final int JAMO_T_COUNT = 28;
   public static final int HANGUL_COUNT = 11172;
   private static final int OPTIONS_NX_MASK = 31;
   private static final int OPTIONS_UNICODE_MASK = 224;
   public static final int OPTIONS_SETS_MASK = 255;
   private static final UnicodeSet[] nxCache;
   private static final int NX_HANGUL = 1;
   private static final int NX_CJK_COMPAT = 2;
   public static final int BEFORE_PRI_29 = 256;
   public static final int OPTIONS_COMPAT = 4096;
   public static final int OPTIONS_COMPOSE_CONTIGUOUS = 8192;
   public static final int WITHOUT_CORRIGENDUM4_CORRECTIONS = 262144;
   private static final char[][] corrigendum4MappingTable;

   public static int getFromIndexesArr(int var0) {
      return indexes[var0];
   }

   private NormalizerImpl() throws IOException {
      if (!isDataLoaded) {
         InputStream var1 = ICUData.getRequiredStream("/sun/text/resources/unorm.icu");
         BufferedInputStream var2 = new BufferedInputStream(var1, 25000);
         NormalizerDataReader var3 = new NormalizerDataReader(var2);
         indexes = var3.readIndexes(32);
         byte[] var4 = new byte[indexes[0]];
         int var5 = indexes[2];
         combiningTable = new char[var5];
         int var6 = indexes[1];
         extraData = new char[var6];
         byte[] var7 = new byte[indexes[10]];
         byte[] var8 = new byte[indexes[11]];
         fcdTrieImpl = new NormalizerImpl.FCDTrieImpl();
         normTrieImpl = new NormalizerImpl.NormTrieImpl();
         auxTrieImpl = new NormalizerImpl.AuxTrieImpl();
         var3.read(var4, var7, var8, extraData, combiningTable);
         NormalizerImpl.NormTrieImpl.normTrie = new IntTrie(new ByteArrayInputStream(var4), normTrieImpl);
         NormalizerImpl.FCDTrieImpl.fcdTrie = new CharTrie(new ByteArrayInputStream(var7), fcdTrieImpl);
         NormalizerImpl.AuxTrieImpl.auxTrie = new CharTrie(new ByteArrayInputStream(var8), auxTrieImpl);
         isDataLoaded = true;
         byte[] var9 = var3.getDataFormatVersion();
         isFormatVersion_2_1 = var9[0] > 2 || var9[0] == 2 && var9[1] >= 1;
         isFormatVersion_2_2 = var9[0] > 2 || var9[0] == 2 && var9[1] >= 2;
         unicodeVersion = var3.getUnicodeVersion();
         var2.close();
      }

   }

   private static boolean isHangulWithoutJamoT(char var0) {
      var0 -= '가';
      return var0 < 11172 && var0 % 28 == 0;
   }

   private static boolean isNorm32Regular(long var0) {
      return var0 < 4227858432L;
   }

   private static boolean isNorm32LeadSurrogate(long var0) {
      return 4227858432L <= var0 && var0 < 4293918720L;
   }

   private static boolean isNorm32HangulOrJamo(long var0) {
      return var0 >= 4293918720L;
   }

   private static boolean isJamoVTNorm32JamoV(long var0) {
      return var0 < 4294115328L;
   }

   public static long getNorm32(char var0) {
      return 4294967295L & (long)NormalizerImpl.NormTrieImpl.normTrie.getLeadValue(var0);
   }

   public static long getNorm32FromSurrogatePair(long var0, char var2) {
      return 4294967295L & (long)NormalizerImpl.NormTrieImpl.normTrie.getTrailValue((int)var0, var2);
   }

   private static long getNorm32(int var0) {
      return 4294967295L & (long)NormalizerImpl.NormTrieImpl.normTrie.getCodePointValue(var0);
   }

   private static long getNorm32(char[] var0, int var1, int var2) {
      long var3 = getNorm32(var0[var1]);
      if ((var3 & (long)var2) > 0L && isNorm32LeadSurrogate(var3)) {
         var3 = getNorm32FromSurrogatePair(var3, var0[var1 + 1]);
      }

      return var3;
   }

   public static VersionInfo getUnicodeVersion() {
      return VersionInfo.getInstance(unicodeVersion[0], unicodeVersion[1], unicodeVersion[2], unicodeVersion[3]);
   }

   public static char getFCD16(char var0) {
      return NormalizerImpl.FCDTrieImpl.fcdTrie.getLeadValue(var0);
   }

   public static char getFCD16FromSurrogatePair(char var0, char var1) {
      return NormalizerImpl.FCDTrieImpl.fcdTrie.getTrailValue(var0, var1);
   }

   public static int getFCD16(int var0) {
      return NormalizerImpl.FCDTrieImpl.fcdTrie.getCodePointValue(var0);
   }

   private static int getExtraDataIndex(long var0) {
      return (int)(var0 >> 16);
   }

   private static int decompose(long var0, int var2, NormalizerImpl.DecomposeArgs var3) {
      int var4 = getExtraDataIndex(var0);
      var3.length = extraData[var4++];
      if ((var0 & (long)var2 & 8L) != 0L && var3.length >= 256) {
         var4 += (var3.length >> 7 & 1) + (var3.length & 127);
         var3.length >>= 8;
      }

      if ((var3.length & 128) > 0) {
         char var5 = extraData[var4++];
         var3.cc = 255 & var5 >> 8;
         var3.trailCC = 255 & var5;
      } else {
         var3.cc = var3.trailCC = 0;
      }

      var3.length &= 127;
      return var4;
   }

   private static int decompose(long var0, NormalizerImpl.DecomposeArgs var2) {
      int var3 = getExtraDataIndex(var0);
      var2.length = extraData[var3++];
      if ((var2.length & 128) > 0) {
         char var4 = extraData[var3++];
         var2.cc = 255 & var4 >> 8;
         var2.trailCC = 255 & var4;
      } else {
         var2.cc = var2.trailCC = 0;
      }

      var2.length &= 127;
      return var3;
   }

   private static int getNextCC(NormalizerImpl.NextCCArgs var0) {
      var0.c = var0.source[var0.next++];
      long var1 = getNorm32(var0.c);
      if ((var1 & 65280L) == 0L) {
         var0.c2 = 0;
         return 0;
      } else {
         if (!isNorm32LeadSurrogate(var1)) {
            var0.c2 = 0;
         } else {
            if (var0.next == var0.limit || !UTF16.isTrailSurrogate(var0.c2 = var0.source[var0.next])) {
               var0.c2 = 0;
               return 0;
            }

            ++var0.next;
            var1 = getNorm32FromSurrogatePair(var1, var0.c2);
         }

         return (int)(255L & var1 >> 8);
      }
   }

   private static long getPrevNorm32(NormalizerImpl.PrevArgs var0, int var1, int var2) {
      var0.c = var0.src[--var0.current];
      var0.c2 = 0;
      if (var0.c < var1) {
         return 0L;
      } else if (!UTF16.isSurrogate(var0.c)) {
         return getNorm32(var0.c);
      } else if (UTF16.isLeadSurrogate(var0.c)) {
         return 0L;
      } else if (var0.current != var0.start && UTF16.isLeadSurrogate(var0.c2 = var0.src[var0.current - 1])) {
         --var0.current;
         long var3 = getNorm32(var0.c2);
         return (var3 & (long)var2) == 0L ? 0L : getNorm32FromSurrogatePair(var3, var0.c);
      } else {
         var0.c2 = 0;
         return 0L;
      }
   }

   private static int getPrevCC(NormalizerImpl.PrevArgs var0) {
      return (int)(255L & getPrevNorm32(var0, 768, 65280) >> 8);
   }

   public static boolean isNFDSafe(long var0, int var2, int var3) {
      if ((var0 & (long)var2) == 0L) {
         return true;
      } else if (isNorm32Regular(var0) && (var0 & (long)var3) != 0L) {
         NormalizerImpl.DecomposeArgs var4 = new NormalizerImpl.DecomposeArgs();
         decompose(var0, var3, var4);
         return var4.cc == 0;
      } else {
         return (var0 & 65280L) == 0L;
      }
   }

   public static boolean isTrueStarter(long var0, int var2, int var3) {
      if ((var0 & (long)var2) == 0L) {
         return true;
      } else {
         if ((var0 & (long)var3) != 0L) {
            NormalizerImpl.DecomposeArgs var5 = new NormalizerImpl.DecomposeArgs();
            int var4 = decompose(var0, var3, var5);
            if (var5.cc == 0) {
               int var6 = var2 & 63;
               if ((getNorm32(extraData, var4, var6) & (long)var6) == 0L) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static int insertOrdered(char[] var0, int var1, int var2, int var3, char var4, char var5, int var6) {
      int var11 = var6;
      if (var1 < var2 && var6 != 0) {
         NormalizerImpl.PrevArgs var12 = new NormalizerImpl.PrevArgs();
         var12.current = var2;
         var12.start = var1;
         var12.src = var0;
         int var10 = getPrevCC(var12);
         int var8 = var12.current;
         if (var6 < var10) {
            var11 = var10;

            int var7;
            for(var7 = var8; var1 < var8; var7 = var8) {
               var10 = getPrevCC(var12);
               var8 = var12.current;
               if (var6 >= var10) {
                  break;
               }
            }

            int var9 = var3;

            do {
               --var9;
               --var2;
               var0[var9] = var0[var2];
            } while(var7 != var2);
         }
      }

      var0[var2] = var4;
      if (var5 != 0) {
         var0[var2 + 1] = var5;
      }

      return var11;
   }

   private static int mergeOrdered(char[] var0, int var1, int var2, char[] var3, int var4, int var5, boolean var6) {
      int var9 = 0;
      boolean var10 = var2 == var4;
      NormalizerImpl.NextCCArgs var11 = new NormalizerImpl.NextCCArgs();
      var11.source = var3;
      var11.next = var4;
      var11.limit = var5;
      if (var1 != var2 || !var6) {
         while(var11.next < var11.limit) {
            int var8 = getNextCC(var11);
            if (var8 == 0) {
               var9 = 0;
               if (var10) {
                  var2 = var11.next;
               } else {
                  var3[var2++] = var11.c;
                  if (var11.c2 != 0) {
                     var3[var2++] = var11.c2;
                  }
               }

               if (var6) {
                  break;
               }

               var1 = var2;
            } else {
               int var7 = var2 + (var11.c2 == 0 ? 1 : 2);
               var9 = insertOrdered(var0, var1, var2, var7, var11.c, var11.c2, var8);
               var2 = var7;
            }
         }
      }

      if (var11.next == var11.limit) {
         return var9;
      } else {
         if (!var10) {
            do {
               var0[var2++] = var3[var11.next++];
            } while(var11.next != var11.limit);

            var11.limit = var2;
         }

         NormalizerImpl.PrevArgs var12 = new NormalizerImpl.PrevArgs();
         var12.src = var3;
         var12.start = var1;
         var12.current = var11.limit;
         return getPrevCC(var12);
      }
   }

   private static int mergeOrdered(char[] var0, int var1, int var2, char[] var3, int var4, int var5) {
      return mergeOrdered(var0, var1, var2, var3, var4, var5, true);
   }

   public static NormalizerBase.QuickCheckResult quickCheck(char[] var0, int var1, int var2, int var3, int var4, int var5, boolean var6, UnicodeSet var7) {
      NormalizerImpl.ComposePartArgs var18 = new NormalizerImpl.ComposePartArgs();
      int var20 = var1;
      if (!isDataLoaded) {
         return NormalizerBase.MAYBE;
      } else {
         int var8 = '\uff00' | var4;
         NormalizerBase.QuickCheckResult var17 = NormalizerBase.YES;
         char var14 = 0;

         while(true) {
            while(var1 != var2) {
               long var9;
               char var11;
               if ((var11 = var0[var1++]) >= var3 && ((var9 = getNorm32(var11)) & (long)var8) != 0L) {
                  char var12;
                  if (isNorm32LeadSurrogate(var9)) {
                     if (var1 != var2 && UTF16.isTrailSurrogate(var12 = var0[var1])) {
                        ++var1;
                        var9 = getNorm32FromSurrogatePair(var9, var12);
                     } else {
                        var9 = 0L;
                        var12 = 0;
                     }
                  } else {
                     var12 = 0;
                  }

                  if (nx_contains(var7, var11, var12)) {
                     var9 = 0L;
                  }

                  char var13 = (char)((int)(var9 >> 8 & 255L));
                  if (var13 != 0 && var13 < var14) {
                     return NormalizerBase.NO;
                  }

                  var14 = var13;
                  long var15 = var9 & (long)var4;
                  if ((var15 & 15L) >= 1L) {
                     var17 = NormalizerBase.NO;
                     return var17;
                  }

                  if (var15 != 0L) {
                     if (var6) {
                        var17 = NormalizerBase.MAYBE;
                     } else {
                        int var22 = var4 << 2 & 15;
                        int var21 = var1 - 1;
                        if (UTF16.isTrailSurrogate(var0[var21])) {
                           --var21;
                        }

                        var21 = findPreviousStarter(var0, var20, var21, var8, var22, (char)var3);
                        var1 = findNextStarter(var0, var1, var2, var4, var22, (char)var3);
                        var18.prevCC = var13;
                        char[] var19 = composePart(var18, var21, var0, var1, var2, var5, var7);
                        if (0 != strCompare(var19, 0, var18.length, var0, var21, var1, false)) {
                           var17 = NormalizerBase.NO;
                           return var17;
                        }
                     }
                  }
               } else {
                  var14 = 0;
               }
            }

            return var17;
         }
      }
   }

   public static int decompose(char[] var0, int var1, int var2, char[] var3, int var4, int var5, boolean var6, int[] var7, UnicodeSet var8) {
      char[] var9 = new char[3];
      int var25 = var4;
      int var26 = var1;
      byte var14;
      char var19;
      if (!var6) {
         var19 = (char)indexes[8];
         var14 = 4;
      } else {
         var19 = (char)indexes[9];
         var14 = 8;
      }

      int var13 = '\uff00' | var14;
      int var15 = 0;
      int var21 = 0;
      long var11 = 0L;
      char var17 = 0;
      int var24 = 0;
      int var22 = -1;
      int var20 = -1;

      while(true) {
         int var10;
         for(var10 = var26; var26 != var2 && ((var17 = var0[var26]) < var19 || ((var11 = getNorm32(var17)) & (long)var13) == 0L); ++var26) {
            var21 = 0;
         }

         int var16;
         if (var26 != var10) {
            var16 = var26 - var10;
            if (var25 + var16 <= var5) {
               System.arraycopy(var0, var10, var3, var25, var16);
            }

            var25 += var16;
            var15 = var25;
         }

         if (var26 == var2) {
            var7[0] = var21;
            return var25 - var4;
         }

         ++var26;
         char var18;
         char[] var23;
         if (isNorm32HangulOrJamo(var11)) {
            if (nx_contains(var8, var17)) {
               var18 = 0;
               var23 = null;
               var16 = 1;
            } else {
               var23 = var9;
               var24 = 0;
               var22 = 0;
               var20 = 0;
               var17 -= '가';
               var18 = (char)(var17 % 28);
               var17 = (char)(var17 / 28);
               if (var18 > 0) {
                  var9[2] = (char)(4519 + var18);
                  var16 = 3;
               } else {
                  var16 = 2;
               }

               var9[1] = (char)(4449 + var17 % 21);
               var9[0] = (char)(4352 + var17 / 21);
            }
         } else {
            if (isNorm32Regular(var11)) {
               var18 = 0;
               var16 = 1;
            } else if (var26 != var2 && UTF16.isTrailSurrogate(var18 = var0[var26])) {
               ++var26;
               var16 = 2;
               var11 = getNorm32FromSurrogatePair(var11, var18);
            } else {
               var18 = 0;
               var16 = 1;
               var11 = 0L;
            }

            if (nx_contains(var8, var17, var18)) {
               var22 = 0;
               var20 = 0;
               var23 = null;
            } else if ((var11 & (long)var14) == 0L) {
               var20 = var22 = (int)(255L & var11 >> 8);
               var23 = null;
               var24 = -1;
            } else {
               NormalizerImpl.DecomposeArgs var27 = new NormalizerImpl.DecomposeArgs();
               var24 = decompose(var11, var14, var27);
               var23 = extraData;
               var16 = var27.length;
               var20 = var27.cc;
               var22 = var27.trailCC;
               if (var16 == 1) {
                  var17 = var23[var24];
                  var18 = 0;
                  var23 = null;
                  var24 = -1;
               }
            }
         }

         if (var25 + var16 <= var5) {
            int var28 = var25;
            if (var23 == null) {
               if (var20 != 0 && var20 < var21) {
                  var25 += var16;
                  var22 = insertOrdered(var3, var15, var28, var25, var17, var18, var20);
               } else {
                  var3[var25++] = var17;
                  if (var18 != 0) {
                     var3[var25++] = var18;
                  }
               }
            } else if (var20 != 0 && var20 < var21) {
               var25 += var16;
               var22 = mergeOrdered(var3, var15, var28, var23, var24, var24 + var16);
            } else {
               do {
                  var3[var25++] = var23[var24++];
                  --var16;
               } while(var16 > 0);
            }
         } else {
            var25 += var16;
         }

         var21 = var22;
         if (var22 == 0) {
            var15 = var25;
         }
      }
   }

   private static int getNextCombining(NormalizerImpl.NextCombiningArgs var0, int var1, UnicodeSet var2) {
      var0.c = var0.source[var0.start++];
      long var3 = getNorm32(var0.c);
      var0.c2 = 0;
      var0.combiningIndex = 0;
      var0.cc = 0;
      if ((var3 & 65472L) == 0L) {
         return 0;
      } else {
         if (!isNorm32Regular(var3)) {
            if (isNorm32HangulOrJamo(var3)) {
               var0.combiningIndex = (int)(4294967295L & (65520L | var3 >> 16));
               return (int)(var3 & 192L);
            }

            if (var0.start == var1 || !UTF16.isTrailSurrogate(var0.c2 = var0.source[var0.start])) {
               var0.c2 = 0;
               return 0;
            }

            ++var0.start;
            var3 = getNorm32FromSurrogatePair(var3, var0.c2);
         }

         if (nx_contains(var2, var0.c, var0.c2)) {
            return 0;
         } else {
            var0.cc = (char)((int)(var3 >> 8 & 255L));
            int var5 = (int)(var3 & 192L);
            if (var5 != 0) {
               int var6 = getExtraDataIndex(var3);
               var0.combiningIndex = var6 > 0 ? extraData[var6 - 1] : 0;
            }

            return var5;
         }
      }
   }

   private static int getCombiningIndexFromStarter(char var0, char var1) {
      long var2 = getNorm32(var0);
      if (var1 != 0) {
         var2 = getNorm32FromSurrogatePair(var2, var1);
      }

      return extraData[getExtraDataIndex(var2) - 1];
   }

   private static int combine(char[] var0, int var1, int var2, int[] var3) {
      if (var3.length < 2) {
         throw new IllegalArgumentException();
      } else {
         while(true) {
            char var4 = var0[var1++];
            if (var4 >= var2) {
               if ((var4 & 32767) == var2) {
                  char var5 = var0[var1];
                  int var7 = (int)(4294967295L & (long)((var5 & 8192) + 1));
                  char var6;
                  int var8;
                  if ((var5 & '耀') != 0) {
                     if ((var5 & 16384) != 0) {
                        var8 = (int)(4294967295L & (long)(var5 & 1023 | '\ud800'));
                        var6 = var0[var1 + 1];
                     } else {
                        var8 = var0[var1 + 1];
                        var6 = 0;
                     }
                  } else {
                     var8 = var5 & 8191;
                     var6 = 0;
                  }

                  var3[0] = var8;
                  var3[1] = var6;
                  return var7;
               }

               return 0;
            }

            var1 += (var0[var1] & '耀') != 0 ? 2 : 1;
         }
      }
   }

   private static char recompose(NormalizerImpl.RecomposeArgs var0, int var1, UnicodeSet var2) {
      int var10 = 0;
      int var11 = 0;
      int[] var15 = new int[2];
      int var14 = -1;
      int var7 = 0;
      boolean var13 = false;
      char var12 = 0;
      NormalizerImpl.NextCombiningArgs var16 = new NormalizerImpl.NextCombiningArgs();
      var16.source = var0.source;
      var16.cc = 0;
      var16.c2 = 0;

      while(true) {
         while(true) {
            var16.start = var0.start;
            int var6 = getNextCombining(var16, var0.limit, var2);
            int var8 = var16.combiningIndex;
            var0.start = var16.start;
            if ((var6 & 128) != 0 && var14 != -1) {
               int var3;
               int var4;
               int var5;
               if ((var8 & '耀') != 0) {
                  if ((var1 & 256) != 0 || var12 == 0) {
                     var3 = -1;
                     var6 = 0;
                     var16.c2 = var0.source[var14];
                     if (var8 == 65522) {
                        var16.c2 = (char)(var16.c2 - 4352);
                        if (var16.c2 < 19) {
                           var3 = var0.start - 1;
                           var16.c = (char)('가' + (var16.c2 * 21 + (var16.c - 4449)) * 28);
                           if (var0.start != var0.limit && (var16.c2 = (char)(var0.source[var0.start] - 4519)) < 28) {
                              ++var0.start;
                              var16.c += var16.c2;
                           } else {
                              var6 = 64;
                           }

                           if (!nx_contains(var2, var16.c)) {
                              var0.source[var14] = var16.c;
                           } else {
                              if (!isHangulWithoutJamoT(var16.c)) {
                                 --var0.start;
                              }

                              var3 = var0.start;
                           }
                        }
                     } else if (isHangulWithoutJamoT(var16.c2)) {
                        var16.c2 = (char)(var16.c2 + (var16.c - 4519));
                        if (!nx_contains(var2, var16.c2)) {
                           var3 = var0.start - 1;
                           var0.source[var14] = var16.c2;
                        }
                     }

                     if (var3 != -1) {
                        var4 = var3;

                        for(var5 = var0.start; var5 < var0.limit; var0.source[var4++] = var0.source[var5++]) {
                        }

                        var0.start = var3;
                        var0.limit = var4;
                     }

                     var16.c2 = 0;
                     if (var6 != 0) {
                        if (var0.start == var0.limit) {
                           return (char)var12;
                        }

                        var7 = 65520;
                        continue;
                     }
                  }
               } else if ((var7 & '耀') == 0) {
                  label143: {
                     if ((var1 & 256) != 0) {
                        if (var12 == var16.cc && var12 != 0) {
                           break label143;
                        }
                     } else if (var12 >= var16.cc && var12 != 0) {
                        break label143;
                     }

                     int var9;
                     if (0 != (var9 = combine(combiningTable, var7, var8, var15)) && !nx_contains(var2, (char)var10, (char)var11)) {
                        var10 = var15[0];
                        var11 = var15[1];
                        var3 = var16.c2 == 0 ? var0.start - 1 : var0.start - 2;
                        var0.source[var14] = (char)var10;
                        if (var13) {
                           if (var11 != 0) {
                              var0.source[var14 + 1] = (char)var11;
                           } else {
                              var13 = false;
                              var4 = var14 + 1;

                              for(var5 = var4 + 1; var5 < var3; var0.source[var4++] = var0.source[var5++]) {
                              }

                              --var3;
                           }
                        } else if (var11 != 0) {
                           var13 = true;
                           var0.source[var14 + 1] = (char)var11;
                        }

                        if (var3 < var0.start) {
                           var4 = var3;

                           for(var5 = var0.start; var5 < var0.limit; var0.source[var4++] = var0.source[var5++]) {
                           }

                           var0.start = var3;
                           var0.limit = var4;
                        }

                        if (var0.start == var0.limit) {
                           return (char)var12;
                        }

                        if (var9 > 1) {
                           var7 = getCombiningIndexFromStarter((char)var10, (char)var11);
                        } else {
                           var14 = -1;
                        }
                        continue;
                     }
                  }
               }
            }

            var12 = var16.cc;
            if (var0.start == var0.limit) {
               return (char)var12;
            }

            if (var16.cc == 0) {
               if ((var6 & 64) != 0) {
                  if (var16.c2 == 0) {
                     var13 = false;
                     var14 = var0.start - 1;
                  } else {
                     var13 = false;
                     var14 = var0.start - 2;
                  }

                  var7 = var8;
               } else {
                  var14 = -1;
               }
            } else if ((var1 & 8192) != 0) {
               var14 = -1;
            }
         }
      }
   }

   private static int findPreviousStarter(char[] var0, int var1, int var2, int var3, int var4, char var5) {
      NormalizerImpl.PrevArgs var8 = new NormalizerImpl.PrevArgs();
      var8.src = var0;
      var8.start = var1;
      var8.current = var2;

      while(var8.start < var8.current) {
         long var6 = getPrevNorm32(var8, var5, var3 | var4);
         if (isTrueStarter(var6, var3, var4)) {
            break;
         }
      }

      return var8.current;
   }

   private static int findNextStarter(char[] var0, int var1, int var2, int var3, int var4, char var5) {
      int var9 = '\uff00' | var3;

      char var11;
      for(NormalizerImpl.DecomposeArgs var12 = new NormalizerImpl.DecomposeArgs(); var1 != var2; var1 += var11 == 0 ? 1 : 2) {
         char var10 = var0[var1];
         if (var10 < var5) {
            break;
         }

         long var7 = getNorm32(var10);
         if ((var7 & (long)var9) == 0L) {
            break;
         }

         if (isNorm32LeadSurrogate(var7)) {
            if (var1 + 1 == var2 || !UTF16.isTrailSurrogate(var11 = var0[var1 + 1])) {
               break;
            }

            var7 = getNorm32FromSurrogatePair(var7, var11);
            if ((var7 & (long)var9) == 0L) {
               break;
            }
         } else {
            var11 = 0;
         }

         if ((var7 & (long)var4) != 0L) {
            int var6 = decompose(var7, var4, var12);
            if (var12.cc == 0 && (getNorm32(extraData, var6, var3) & (long)var3) == 0L) {
               break;
            }
         }
      }

      return var1;
   }

   private static char[] composePart(NormalizerImpl.ComposePartArgs var0, int var1, char[] var2, int var3, int var4, int var5, UnicodeSet var6) {
      boolean var8 = (var5 & 4096) != 0;
      int[] var9 = new int[1];
      char[] var10 = new char[(var4 - var1) * 20];

      while(true) {
         var0.length = decompose(var2, var1, var3, var10, 0, var10.length, var8, var9, var6);
         if (var0.length <= var10.length) {
            int var7 = var0.length;
            if (var0.length >= 2) {
               NormalizerImpl.RecomposeArgs var11 = new NormalizerImpl.RecomposeArgs();
               var11.source = var10;
               var11.start = 0;
               var11.limit = var7;
               var0.prevCC = recompose(var11, var5, var6);
               var7 = var11.limit;
            }

            var0.length = var7;
            return var10;
         }

         var10 = new char[var0.length];
      }
   }

   private static boolean composeHangul(char var0, char var1, long var2, char[] var4, int[] var5, int var6, boolean var7, char[] var8, int var9, UnicodeSet var10) {
      int var11 = var5[0];
      if (isJamoVTNorm32JamoV(var2)) {
         var0 = (char)(var0 - 4352);
         if (var0 < 19) {
            var1 = (char)('가' + (var0 * 21 + (var1 - 4449)) * 28);
            if (var11 != var6) {
               char var12 = var4[var11];
               char var13;
               if ((var13 = (char)(var12 - 4519)) < 28) {
                  ++var11;
                  var1 += var13;
               } else if (var7) {
                  var2 = getNorm32(var12);
                  if (isNorm32Regular(var2) && (var2 & 8L) != 0L) {
                     NormalizerImpl.DecomposeArgs var15 = new NormalizerImpl.DecomposeArgs();
                     int var14 = decompose(var2, 8, var15);
                     if (var15.length == 1 && (var13 = (char)(extraData[var14] - 4519)) < 28) {
                        ++var11;
                        var1 += var13;
                     }
                  }
               }
            }

            if (nx_contains(var10, var1)) {
               if (!isHangulWithoutJamoT(var1)) {
                  --var11;
               }

               return false;
            }

            var8[var9] = var1;
            var5[0] = var11;
            return true;
         }
      } else if (isHangulWithoutJamoT(var0)) {
         var1 = (char)(var0 + (var1 - 4519));
         if (nx_contains(var10, var1)) {
            return false;
         }

         var8[var9] = var1;
         var5[0] = var11;
         return true;
      }

      return false;
   }

   public static int compose(char[] var0, int var1, int var2, char[] var3, int var4, int var5, int var6, UnicodeSet var7) {
      int[] var21 = new int[1];
      int var22 = var4;
      int var23 = var1;
      byte var13;
      char var18;
      if ((var6 & 4096) != 0) {
         var18 = (char)indexes[7];
         var13 = 34;
      } else {
         var18 = (char)indexes[6];
         var13 = 17;
      }

      int var9 = var1;
      int var12 = '\uff00' | var13;
      int var14 = 0;
      int var20 = 0;
      long var10 = 0L;
      char var16 = 0;

      while(true) {
         int var8;
         for(var8 = var23; var23 != var2 && ((var16 = var0[var23]) < var18 || ((var10 = getNorm32(var16)) & (long)var12) == 0L); ++var23) {
            var20 = 0;
         }

         int var15;
         if (var23 != var8) {
            var15 = var23 - var8;
            if (var22 + var15 <= var5) {
               System.arraycopy(var0, var8, var3, var22, var15);
            }

            var22 += var15;
            var14 = var22;
            var9 = var23 - 1;
            if (UTF16.isTrailSurrogate(var0[var9]) && var8 < var9 && UTF16.isLeadSurrogate(var0[var9 - 1])) {
               --var9;
            }

            var8 = var23;
         }

         if (var23 == var2) {
            break;
         }

         ++var23;
         char var17;
         int var19;
         byte var28;
         if (isNorm32HangulOrJamo(var10)) {
            var19 = 0;
            var20 = 0;
            var14 = var22;
            var21[0] = var23;
            if (var22 > 0 && composeHangul(var0[var8 - 1], var16, var10, var0, var21, var2, (var6 & 4096) != 0, var3, var22 <= var5 ? var22 - 1 : 0, var7)) {
               var23 = var21[0];
               var9 = var23;
               continue;
            }

            var23 = var21[0];
            var17 = 0;
            var28 = 1;
            var9 = var8;
         } else {
            if (isNorm32Regular(var10)) {
               var17 = 0;
               var28 = 1;
            } else if (var23 != var2 && UTF16.isTrailSurrogate(var17 = var0[var23])) {
               ++var23;
               var28 = 2;
               var10 = getNorm32FromSurrogatePair(var10, var17);
            } else {
               var17 = 0;
               var28 = 1;
               var10 = 0L;
            }

            NormalizerImpl.ComposePartArgs var24 = new NormalizerImpl.ComposePartArgs();
            if (nx_contains(var7, var16, var17)) {
               var19 = 0;
            } else {
               if ((var10 & (long)var13) != 0L) {
                  int var26 = var13 << 2 & 15;
                  if (isTrueStarter(var10, '\uff00' | var13, var26)) {
                     var9 = var8;
                  } else {
                     var22 -= var8 - var9;
                  }

                  var23 = findNextStarter(var0, var23, var2, var13, var26, var18);
                  var24.prevCC = var20;
                  var24.length = var28;
                  char[] var25 = composePart(var24, var9, var0, var23, var2, var6, var7);
                  if (var25 == null) {
                     break;
                  }

                  var20 = var24.prevCC;
                  var15 = var24.length;
                  if (var22 + var24.length <= var5) {
                     for(int var27 = 0; var27 < var24.length; --var15) {
                        var3[var22++] = var25[var27++];
                     }
                  } else {
                     var22 += var15;
                  }

                  var9 = var23;
                  continue;
               }

               var19 = (int)(255L & var10 >> 8);
            }
         }

         if (var22 + var28 <= var5) {
            if (var19 != 0 && var19 < var20) {
               int var29 = var22;
               var22 += var28;
               var20 = insertOrdered(var3, var14, var29, var22, var16, var17, var19);
            } else {
               var3[var22++] = var16;
               if (var17 != 0) {
                  var3[var22++] = var17;
               }

               var20 = var19;
            }
         } else {
            var22 += var28;
            var20 = var19;
         }
      }

      return var22 - var4;
   }

   public static int getCombiningClass(int var0) {
      long var1 = getNorm32(var0);
      return (int)(var1 >> 8 & 255L);
   }

   public static boolean isFullCompositionExclusion(int var0) {
      if (isFormatVersion_2_1) {
         char var1 = NormalizerImpl.AuxTrieImpl.auxTrie.getCodePointValue(var0);
         return (var1 & 1024) != 0;
      } else {
         return false;
      }
   }

   public static boolean isCanonSafeStart(int var0) {
      if (isFormatVersion_2_1) {
         char var1 = NormalizerImpl.AuxTrieImpl.auxTrie.getCodePointValue(var0);
         return (var1 & 2048) == 0;
      } else {
         return false;
      }
   }

   public static boolean isNFSkippable(int var0, NormalizerBase.Mode var1, long var2) {
      var2 &= 4294967295L;
      long var4 = getNorm32(var0);
      if ((var4 & var2) != 0L) {
         return false;
      } else if (var1 != NormalizerBase.NFD && var1 != NormalizerBase.NFKD && var1 != NormalizerBase.NONE) {
         if ((var4 & 4L) == 0L) {
            return true;
         } else if (isNorm32HangulOrJamo(var4)) {
            return !isHangulWithoutJamoT((char)var0);
         } else if (!isFormatVersion_2_2) {
            return false;
         } else {
            char var6 = NormalizerImpl.AuxTrieImpl.auxTrie.getCodePointValue(var0);
            return ((long)var6 & 4096L) == 0L;
         }
      } else {
         return true;
      }
   }

   public static UnicodeSet addPropertyStarts(UnicodeSet var0) {
      TrieIterator var2 = new TrieIterator(NormalizerImpl.NormTrieImpl.normTrie);
      RangeValueIterator.Element var3 = new RangeValueIterator.Element();

      while(var2.next(var3)) {
         var0.add(var3.start);
      }

      TrieIterator var4 = new TrieIterator(NormalizerImpl.FCDTrieImpl.fcdTrie);
      RangeValueIterator.Element var5 = new RangeValueIterator.Element();

      while(var4.next(var5)) {
         var0.add(var5.start);
      }

      if (isFormatVersion_2_1) {
         TrieIterator var6 = new TrieIterator(NormalizerImpl.AuxTrieImpl.auxTrie);
         RangeValueIterator.Element var7 = new RangeValueIterator.Element();

         while(var6.next(var7)) {
            var0.add(var7.start);
         }
      }

      for(int var1 = 44032; var1 < 55204; var1 += 28) {
         var0.add(var1);
         var0.add(var1 + 1);
      }

      var0.add(55204);
      return var0;
   }

   public static final int quickCheck(int var0, int var1) {
      int[] var2 = new int[]{0, 0, 4, 8, 17, 34};
      int var3 = (int)getNorm32(var0) & var2[var1];
      if (var3 == 0) {
         return 1;
      } else {
         return (var3 & 15) != 0 ? 0 : 2;
      }
   }

   private static int strCompare(char[] var0, int var1, int var2, char[] var3, int var4, int var5, boolean var6) {
      int var7 = var1;
      int var8 = var4;
      int var13 = var2 - var1;
      int var14 = var5 - var4;
      int var9;
      byte var15;
      if (var13 < var14) {
         var15 = -1;
         var9 = var1 + var13;
      } else if (var13 == var14) {
         var15 = 0;
         var9 = var1 + var13;
      } else {
         var15 = 1;
         var9 = var1 + var14;
      }

      if (var0 == var3) {
         return var15;
      } else {
         while(var1 != var9) {
            char var11 = var0[var1];
            char var12 = var3[var4];
            if (var11 != var12) {
               var9 = var7 + var13;
               int var10 = var8 + var14;
               if (var11 >= '\ud800' && var12 >= '\ud800' && var6) {
                  if ((var11 > '\udbff' || var1 + 1 == var9 || !UTF16.isTrailSurrogate(var0[var1 + 1])) && (!UTF16.isTrailSurrogate(var11) || var7 == var1 || !UTF16.isLeadSurrogate(var0[var1 - 1]))) {
                     var11 = (char)(var11 - 10240);
                  }

                  if ((var12 > '\udbff' || var4 + 1 == var10 || !UTF16.isTrailSurrogate(var3[var4 + 1])) && (!UTF16.isTrailSurrogate(var12) || var8 == var4 || !UTF16.isLeadSurrogate(var3[var4 - 1]))) {
                     var12 = (char)(var12 - 10240);
                  }
               }

               return var11 - var12;
            }

            ++var1;
            ++var4;
         }

         return var15;
      }
   }

   private static final synchronized UnicodeSet internalGetNXHangul() {
      if (nxCache[1] == null) {
         nxCache[1] = new UnicodeSet(44032, 55203);
      }

      return nxCache[1];
   }

   private static final synchronized UnicodeSet internalGetNXCJKCompat() {
      if (nxCache[2] == null) {
         UnicodeSet var0 = new UnicodeSet("[:Ideographic:]");
         UnicodeSet var1 = new UnicodeSet();
         UnicodeSetIterator var2 = new UnicodeSetIterator(var0);

         while(var2.nextRange() && var2.codepoint != UnicodeSetIterator.IS_STRING) {
            int var3 = var2.codepoint;

            for(int var4 = var2.codepointEnd; var3 <= var4; ++var3) {
               long var5 = getNorm32(var3);
               if ((var5 & 4L) > 0L) {
                  var1.add(var3);
               }
            }
         }

         nxCache[2] = var1;
      }

      return nxCache[2];
   }

   private static final synchronized UnicodeSet internalGetNXUnicode(int var0) {
      var0 &= 224;
      if (var0 == 0) {
         return null;
      } else {
         if (nxCache[var0] == null) {
            UnicodeSet var1 = new UnicodeSet();
            switch(var0) {
            case 32:
               var1.applyPattern("[:^Age=3.2:]");
               nxCache[var0] = var1;
               break;
            default:
               return null;
            }
         }

         return nxCache[var0];
      }
   }

   private static final synchronized UnicodeSet internalGetNX(int var0) {
      var0 &= 255;
      if (nxCache[var0] == null) {
         if (var0 == 1) {
            return internalGetNXHangul();
         }

         if (var0 == 2) {
            return internalGetNXCJKCompat();
         }

         if ((var0 & 224) != 0 && (var0 & 31) == 0) {
            return internalGetNXUnicode(var0);
         }

         UnicodeSet var1 = new UnicodeSet();
         UnicodeSet var2;
         if ((var0 & 1) != 0 && null != (var2 = internalGetNXHangul())) {
            var1.addAll(var2);
         }

         if ((var0 & 2) != 0 && null != (var2 = internalGetNXCJKCompat())) {
            var1.addAll(var2);
         }

         if ((var0 & 224) != 0 && null != (var2 = internalGetNXUnicode(var0))) {
            var1.addAll(var2);
         }

         nxCache[var0] = var1;
      }

      return nxCache[var0];
   }

   public static final UnicodeSet getNX(int var0) {
      return (var0 &= 255) == 0 ? null : internalGetNX(var0);
   }

   private static final boolean nx_contains(UnicodeSet var0, int var1) {
      return var0 != null && var0.contains(var1);
   }

   private static final boolean nx_contains(UnicodeSet var0, char var1, char var2) {
      return var0 != null && var0.contains(var2 == 0 ? var1 : UCharacterProperty.getRawSupplementary(var1, var2));
   }

   public static int getDecompose(int[] var0, String[] var1) {
      NormalizerImpl.DecomposeArgs var2 = new NormalizerImpl.DecomposeArgs();
      boolean var3 = false;
      long var4 = 0L;
      int var6 = -1;
      boolean var7 = false;
      int var8 = 0;

      while(true) {
         ++var6;
         if (var6 >= 195102) {
            return var8;
         }

         if (var6 == 12543) {
            var6 = 63744;
         } else if (var6 == 65536) {
            var6 = 119134;
         } else if (var6 == 119233) {
            var6 = 194560;
         }

         var4 = getNorm32(var6);
         if ((var4 & 4L) != 0L && var8 < var0.length) {
            var0[var8] = var6;
            int var9 = decompose(var4, var2);
            var1[var8++] = new String(extraData, var9, var2.length);
         }
      }
   }

   private static boolean needSingleQuotation(char var0) {
      return var0 >= '\t' && var0 <= '\r' || var0 >= ' ' && var0 <= '/' || var0 >= ':' && var0 <= '@' || var0 >= '[' && var0 <= '`' || var0 >= '{' && var0 <= '~';
   }

   public static String canonicalDecomposeWithSingleQuotation(String var0) {
      char[] var1 = var0.toCharArray();
      int var2 = 0;
      int var3 = var1.length;
      char[] var4 = new char[var1.length * 3];
      int var5 = 0;
      int var6 = var4.length;
      char[] var7 = new char[3];
      byte var12 = 4;
      char var17 = (char)indexes[8];
      int var11 = '\uff00' | var12;
      int var13 = 0;
      int var19 = 0;
      long var9 = 0L;
      char var15 = 0;
      boolean var22 = false;
      boolean var20 = true;
      boolean var18 = true;

      while(true) {
         int var8;
         for(var8 = var2; var2 != var3 && ((var15 = var1[var2]) < var17 || ((var9 = getNorm32(var15)) & (long)var11) == 0L || var15 >= '가' && var15 <= '힣'); ++var2) {
            var19 = 0;
         }

         int var14;
         if (var2 != var8) {
            var14 = var2 - var8;
            if (var5 + var14 <= var6) {
               System.arraycopy(var1, var8, var4, var5, var14);
            }

            var5 += var14;
            var13 = var5;
         }

         if (var2 == var3) {
            return new String(var4, 0, var5);
         }

         ++var2;
         char var16;
         if (isNorm32Regular(var9)) {
            var16 = 0;
            var14 = 1;
         } else if (var2 != var3 && Character.isLowSurrogate(var16 = var1[var2])) {
            ++var2;
            var14 = 2;
            var9 = getNorm32FromSurrogatePair(var9, var16);
         } else {
            var16 = 0;
            var14 = 1;
            var9 = 0L;
         }

         char[] var21;
         int var24;
         int var25;
         int var26;
         if ((var9 & (long)var12) == 0L) {
            var24 = var25 = (int)(255L & var9 >> 8);
            var21 = null;
            var26 = -1;
         } else {
            NormalizerImpl.DecomposeArgs var23 = new NormalizerImpl.DecomposeArgs();
            var26 = decompose(var9, var12, var23);
            var21 = extraData;
            var14 = var23.length;
            var24 = var23.cc;
            var25 = var23.trailCC;
            if (var14 == 1) {
               var15 = var21[var26];
               var16 = 0;
               var21 = null;
               var26 = -1;
            }
         }

         if (var5 + var14 * 3 >= var6) {
            char[] var27 = new char[var6 * 2];
            System.arraycopy(var4, 0, var27, 0, var5);
            var4 = var27;
            var6 = var27.length;
         }

         int var28 = var5;
         if (var21 == null) {
            if (needSingleQuotation(var15)) {
               var4[var5++] = '\'';
               var4[var5++] = var15;
               var4[var5++] = '\'';
               var25 = 0;
            } else if (var24 != 0 && var24 < var19) {
               var5 += var14;
               var25 = insertOrdered(var4, var13, var28, var5, var15, var16, var24);
            } else {
               var4[var5++] = var15;
               if (var16 != 0) {
                  var4[var5++] = var16;
               }
            }
         } else if (needSingleQuotation(var21[var26])) {
            var4[var5++] = '\'';
            var4[var5++] = var21[var26++];
            var4[var5++] = '\'';
            --var14;

            do {
               var4[var5++] = var21[var26++];
               --var14;
            } while(var14 > 0);
         } else if (var24 != 0 && var24 < var19) {
            var5 += var14;
            var25 = mergeOrdered(var4, var13, var28, var21, var26, var26 + var14);
         } else {
            do {
               var4[var5++] = var21[var26++];
               --var14;
            } while(var14 > 0);
         }

         var19 = var25;
         if (var25 == 0) {
            var13 = var5;
         }
      }
   }

   public static String convert(String var0) {
      if (var0 == null) {
         return null;
      } else {
         boolean var1 = true;
         StringBuffer var2 = new StringBuffer();
         UCharacterIterator var3 = UCharacterIterator.getInstance(var0);

         int var4;
         while((var4 = var3.nextCodePoint()) != -1) {
            switch(var4) {
            case 194664:
               var2.append(corrigendum4MappingTable[0]);
               break;
            case 194676:
               var2.append(corrigendum4MappingTable[1]);
               break;
            case 194847:
               var2.append(corrigendum4MappingTable[2]);
               break;
            case 194911:
               var2.append(corrigendum4MappingTable[3]);
               break;
            case 195007:
               var2.append(corrigendum4MappingTable[4]);
               break;
            default:
               UTF16.append(var2, var4);
            }
         }

         return var2.toString();
      }
   }

   static {
      try {
         IMPL = new NormalizerImpl();
      } catch (Exception var1) {
         throw new RuntimeException(var1.getMessage());
      }

      nxCache = new UnicodeSet[256];
      corrigendum4MappingTable = new char[][]{{'\ud844', '\udf6a'}, {'弳'}, {'䎫'}, {'窮'}, {'䵗'}};
   }

   private static final class ComposePartArgs {
      int prevCC;
      int length;

      private ComposePartArgs() {
      }

      // $FF: synthetic method
      ComposePartArgs(Object var1) {
         this();
      }
   }

   private static final class RecomposeArgs {
      char[] source;
      int start;
      int limit;

      private RecomposeArgs() {
      }

      // $FF: synthetic method
      RecomposeArgs(Object var1) {
         this();
      }
   }

   private static final class NextCombiningArgs {
      char[] source;
      int start;
      char c;
      char c2;
      int combiningIndex;
      char cc;

      private NextCombiningArgs() {
      }

      // $FF: synthetic method
      NextCombiningArgs(Object var1) {
         this();
      }
   }

   private static final class PrevArgs {
      char[] src;
      int start;
      int current;
      char c;
      char c2;

      private PrevArgs() {
      }

      // $FF: synthetic method
      PrevArgs(Object var1) {
         this();
      }
   }

   private static final class NextCCArgs {
      char[] source;
      int next;
      int limit;
      char c;
      char c2;

      private NextCCArgs() {
      }

      // $FF: synthetic method
      NextCCArgs(Object var1) {
         this();
      }
   }

   private static final class DecomposeArgs {
      int cc;
      int trailCC;
      int length;

      private DecomposeArgs() {
      }

      // $FF: synthetic method
      DecomposeArgs(Object var1) {
         this();
      }
   }

   static final class AuxTrieImpl implements Trie.DataManipulate {
      static CharTrie auxTrie = null;

      public int getFoldingOffset(int var1) {
         return (var1 & 1023) << 5;
      }
   }

   static final class FCDTrieImpl implements Trie.DataManipulate {
      static CharTrie fcdTrie = null;

      public int getFoldingOffset(int var1) {
         return var1;
      }
   }

   static final class NormTrieImpl implements Trie.DataManipulate {
      static IntTrie normTrie = null;

      public int getFoldingOffset(int var1) {
         return 2048 + (var1 >> 11 & 32736);
      }
   }
}
