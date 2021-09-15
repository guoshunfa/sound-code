package java.text;

import java.util.Vector;
import sun.text.CollatorUtilities;
import sun.text.normalizer.NormalizerBase;

public final class CollationElementIterator {
   public static final int NULLORDER = -1;
   static final int UNMAPPEDCHARVALUE = 2147418112;
   private NormalizerBase text = null;
   private int[] buffer = null;
   private int expIndex = 0;
   private StringBuffer key = new StringBuffer(5);
   private int swapOrder = 0;
   private RBCollationTables ordering;
   private RuleBasedCollator owner;

   CollationElementIterator(String var1, RuleBasedCollator var2) {
      this.owner = var2;
      this.ordering = var2.getTables();
      if (var1.length() != 0) {
         NormalizerBase.Mode var3 = CollatorUtilities.toNormalizerMode(var2.getDecomposition());
         this.text = new NormalizerBase(var1, var3);
      }

   }

   CollationElementIterator(CharacterIterator var1, RuleBasedCollator var2) {
      this.owner = var2;
      this.ordering = var2.getTables();
      NormalizerBase.Mode var3 = CollatorUtilities.toNormalizerMode(var2.getDecomposition());
      this.text = new NormalizerBase(var1, var3);
   }

   public void reset() {
      if (this.text != null) {
         this.text.reset();
         NormalizerBase.Mode var1 = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
         this.text.setMode(var1);
      }

      this.buffer = null;
      this.expIndex = 0;
      this.swapOrder = 0;
   }

   public int next() {
      if (this.text == null) {
         return -1;
      } else {
         NormalizerBase.Mode var1 = this.text.getMode();
         NormalizerBase.Mode var2 = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
         if (var1 != var2) {
            this.text.setMode(var2);
         }

         int var3;
         if (this.buffer != null) {
            if (this.expIndex < this.buffer.length) {
               return this.strengthOrder(this.buffer[this.expIndex++]);
            }

            this.buffer = null;
            this.expIndex = 0;
         } else if (this.swapOrder != 0) {
            if (Character.isSupplementaryCodePoint(this.swapOrder)) {
               char[] var6 = Character.toChars(this.swapOrder);
               this.swapOrder = var6[1];
               return var6[0] << 16;
            }

            var3 = this.swapOrder << 16;
            this.swapOrder = 0;
            return var3;
         }

         var3 = this.text.next();
         if (var3 == -1) {
            return -1;
         } else {
            int var4 = this.ordering.getUnicodeOrder(var3);
            if (var4 == -1) {
               this.swapOrder = var3;
               return 2147418112;
            } else {
               if (var4 >= 2130706432) {
                  var4 = this.nextContractChar(var3);
               }

               if (var4 >= 2113929216) {
                  this.buffer = this.ordering.getExpandValueList(var4);
                  this.expIndex = 0;
                  var4 = this.buffer[this.expIndex++];
               }

               if (this.ordering.isSEAsianSwapping()) {
                  int var5;
                  if (isThaiPreVowel(var3)) {
                     var5 = this.text.next();
                     if (isThaiBaseConsonant(var5)) {
                        this.buffer = this.makeReorderedBuffer(var5, var4, this.buffer, true);
                        var4 = this.buffer[0];
                        this.expIndex = 1;
                     } else if (var5 != -1) {
                        this.text.previous();
                     }
                  }

                  if (isLaoPreVowel(var3)) {
                     var5 = this.text.next();
                     if (isLaoBaseConsonant(var5)) {
                        this.buffer = this.makeReorderedBuffer(var5, var4, this.buffer, true);
                        var4 = this.buffer[0];
                        this.expIndex = 1;
                     } else if (var5 != -1) {
                        this.text.previous();
                     }
                  }
               }

               return this.strengthOrder(var4);
            }
         }
      }
   }

   public int previous() {
      if (this.text == null) {
         return -1;
      } else {
         NormalizerBase.Mode var1 = this.text.getMode();
         NormalizerBase.Mode var2 = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
         if (var1 != var2) {
            this.text.setMode(var2);
         }

         int var3;
         if (this.buffer != null) {
            if (this.expIndex > 0) {
               return this.strengthOrder(this.buffer[--this.expIndex]);
            }

            this.buffer = null;
            this.expIndex = 0;
         } else if (this.swapOrder != 0) {
            if (Character.isSupplementaryCodePoint(this.swapOrder)) {
               char[] var6 = Character.toChars(this.swapOrder);
               this.swapOrder = var6[1];
               return var6[0] << 16;
            }

            var3 = this.swapOrder << 16;
            this.swapOrder = 0;
            return var3;
         }

         var3 = this.text.previous();
         if (var3 == -1) {
            return -1;
         } else {
            int var4 = this.ordering.getUnicodeOrder(var3);
            if (var4 == -1) {
               this.swapOrder = 2147418112;
               return var3;
            } else {
               if (var4 >= 2130706432) {
                  var4 = this.prevContractChar(var3);
               }

               if (var4 >= 2113929216) {
                  this.buffer = this.ordering.getExpandValueList(var4);
                  this.expIndex = this.buffer.length;
                  var4 = this.buffer[--this.expIndex];
               }

               if (this.ordering.isSEAsianSwapping()) {
                  int var5;
                  if (isThaiBaseConsonant(var3)) {
                     var5 = this.text.previous();
                     if (isThaiPreVowel(var5)) {
                        this.buffer = this.makeReorderedBuffer(var5, var4, this.buffer, false);
                        this.expIndex = this.buffer.length - 1;
                        var4 = this.buffer[this.expIndex];
                     } else {
                        this.text.next();
                     }
                  }

                  if (isLaoBaseConsonant(var3)) {
                     var5 = this.text.previous();
                     if (isLaoPreVowel(var5)) {
                        this.buffer = this.makeReorderedBuffer(var5, var4, this.buffer, false);
                        this.expIndex = this.buffer.length - 1;
                        var4 = this.buffer[this.expIndex];
                     } else {
                        this.text.next();
                     }
                  }
               }

               return this.strengthOrder(var4);
            }
         }
      }
   }

   public static final int primaryOrder(int var0) {
      var0 &= -65536;
      return var0 >>> 16;
   }

   public static final short secondaryOrder(int var0) {
      var0 &= 65280;
      return (short)(var0 >> 8);
   }

   public static final short tertiaryOrder(int var0) {
      return (short)(var0 &= 255);
   }

   final int strengthOrder(int var1) {
      int var2 = this.owner.getStrength();
      if (var2 == 0) {
         var1 &= -65536;
      } else if (var2 == 1) {
         var1 &= -256;
      }

      return var1;
   }

   public void setOffset(int var1) {
      if (this.text != null) {
         if (var1 >= this.text.getBeginIndex() && var1 < this.text.getEndIndex()) {
            int var2 = this.text.setIndex(var1);
            if (this.ordering.usedInContractSeq(var2)) {
               while(this.ordering.usedInContractSeq(var2)) {
                  var2 = this.text.previous();
               }

               int var3 = this.text.getIndex();

               while(this.text.getIndex() <= var1) {
                  var3 = this.text.getIndex();
                  this.next();
               }

               this.text.setIndexOnly(var3);
            }
         } else {
            this.text.setIndexOnly(var1);
         }
      }

      this.buffer = null;
      this.expIndex = 0;
      this.swapOrder = 0;
   }

   public int getOffset() {
      return this.text != null ? this.text.getIndex() : 0;
   }

   public int getMaxExpansion(int var1) {
      return this.ordering.getMaxExpansion(var1);
   }

   public void setText(String var1) {
      this.buffer = null;
      this.swapOrder = 0;
      this.expIndex = 0;
      NormalizerBase.Mode var2 = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
      if (this.text == null) {
         this.text = new NormalizerBase(var1, var2);
      } else {
         this.text.setMode(var2);
         this.text.setText(var1);
      }

   }

   public void setText(CharacterIterator var1) {
      this.buffer = null;
      this.swapOrder = 0;
      this.expIndex = 0;
      NormalizerBase.Mode var2 = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
      if (this.text == null) {
         this.text = new NormalizerBase(var1, var2);
      } else {
         this.text.setMode(var2);
         this.text.setText(var1);
      }

   }

   private static final boolean isThaiPreVowel(int var0) {
      return var0 >= 3648 && var0 <= 3652;
   }

   private static final boolean isThaiBaseConsonant(int var0) {
      return var0 >= 3585 && var0 <= 3630;
   }

   private static final boolean isLaoPreVowel(int var0) {
      return var0 >= 3776 && var0 <= 3780;
   }

   private static final boolean isLaoBaseConsonant(int var0) {
      return var0 >= 3713 && var0 <= 3758;
   }

   private int[] makeReorderedBuffer(int var1, int var2, int[] var3, boolean var4) {
      int var6 = this.ordering.getUnicodeOrder(var1);
      if (var6 >= 2130706432) {
         var6 = var4 ? this.nextContractChar(var1) : this.prevContractChar(var1);
      }

      int[] var7 = null;
      if (var6 >= 2113929216) {
         var7 = this.ordering.getExpandValueList(var6);
      }

      int var8;
      if (!var4) {
         var8 = var6;
         var6 = var2;
         var2 = var8;
         int[] var9 = var7;
         var7 = var3;
         var3 = var9;
      }

      int[] var5;
      if (var7 == null && var3 == null) {
         var5 = new int[]{var6, var2};
      } else {
         var8 = var7 == null ? 1 : var7.length;
         int var10 = var3 == null ? 1 : var3.length;
         var5 = new int[var8 + var10];
         if (var7 == null) {
            var5[0] = var6;
         } else {
            System.arraycopy(var7, 0, var5, 0, var8);
         }

         if (var3 == null) {
            var5[var8] = var2;
         } else {
            System.arraycopy(var3, 0, var5, var8, var10);
         }
      }

      return var5;
   }

   static final boolean isIgnorable(int var0) {
      return primaryOrder(var0) == 0;
   }

   private int nextContractChar(int var1) {
      Vector var2 = this.ordering.getContractValues(var1);
      EntryPair var3 = (EntryPair)var2.firstElement();
      int var4 = var3.value;
      var3 = (EntryPair)var2.lastElement();
      int var5 = var3.entryName.length();
      NormalizerBase var6 = (NormalizerBase)this.text.clone();
      var6.previous();
      this.key.setLength(0);

      int var7;
      for(var7 = var6.next(); var5 > 0 && var7 != -1; var7 = var6.next()) {
         if (Character.isSupplementaryCodePoint(var7)) {
            this.key.append(Character.toChars(var7));
            var5 -= 2;
         } else {
            this.key.append((char)var7);
            --var5;
         }
      }

      String var8 = this.key.toString();
      var5 = 1;

      for(int var9 = var2.size() - 1; var9 > 0; --var9) {
         var3 = (EntryPair)var2.elementAt(var9);
         if (var3.fwd && var8.startsWith(var3.entryName) && var3.entryName.length() > var5) {
            var5 = var3.entryName.length();
            var4 = var3.value;
         }
      }

      while(var5 > 1) {
         var7 = this.text.next();
         var5 -= Character.charCount(var7);
      }

      return var4;
   }

   private int prevContractChar(int var1) {
      Vector var2 = this.ordering.getContractValues(var1);
      EntryPair var3 = (EntryPair)var2.firstElement();
      int var4 = var3.value;
      var3 = (EntryPair)var2.lastElement();
      int var5 = var3.entryName.length();
      NormalizerBase var6 = (NormalizerBase)this.text.clone();
      var6.next();
      this.key.setLength(0);

      int var7;
      for(var7 = var6.previous(); var5 > 0 && var7 != -1; var7 = var6.previous()) {
         if (Character.isSupplementaryCodePoint(var7)) {
            this.key.append(Character.toChars(var7));
            var5 -= 2;
         } else {
            this.key.append((char)var7);
            --var5;
         }
      }

      String var8 = this.key.toString();
      var5 = 1;

      for(int var9 = var2.size() - 1; var9 > 0; --var9) {
         var3 = (EntryPair)var2.elementAt(var9);
         if (!var3.fwd && var8.startsWith(var3.entryName) && var3.entryName.length() > var5) {
            var5 = var3.entryName.length();
            var4 = var3.value;
         }
      }

      while(var5 > 1) {
         var7 = this.text.previous();
         var5 -= Character.charCount(var7);
      }

      return var4;
   }
}
