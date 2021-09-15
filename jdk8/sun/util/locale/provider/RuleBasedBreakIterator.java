package sun.util.locale.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.MissingResourceException;
import sun.text.CompactByteArray;
import sun.text.SupplementaryCharacterData;

class RuleBasedBreakIterator extends BreakIterator {
   protected static final byte IGNORE = -1;
   private static final short START_STATE = 1;
   private static final short STOP_STATE = 0;
   static final byte[] LABEL = new byte[]{66, 73, 100, 97, 116, 97, 0};
   static final int LABEL_LENGTH;
   static final byte supportedVersion = 1;
   private static final int HEADER_LENGTH = 36;
   private static final int BMP_INDICES_LENGTH = 512;
   private CompactByteArray charCategoryTable = null;
   private SupplementaryCharacterData supplementaryCharCategoryTable = null;
   private short[] stateTable = null;
   private short[] backwardsStateTable = null;
   private boolean[] endStates = null;
   private boolean[] lookaheadStates = null;
   private byte[] additionalData = null;
   private int numCategories;
   private CharacterIterator text = null;
   private long checksum;
   private int cachedLastKnownBreak = -1;

   RuleBasedBreakIterator(String var1) throws IOException, MissingResourceException {
      this.readTables(var1);
   }

   protected final void readTables(String var1) throws IOException, MissingResourceException {
      byte[] var2 = this.readFile(var1);
      int var3 = getInt(var2, 0);
      int var4 = getInt(var2, 4);
      int var5 = getInt(var2, 8);
      int var6 = getInt(var2, 12);
      int var7 = getInt(var2, 16);
      int var8 = getInt(var2, 20);
      int var9 = getInt(var2, 24);
      this.checksum = getLong(var2, 28);
      this.stateTable = new short[var3];
      int var10 = 36;

      int var11;
      for(var11 = 0; var11 < var3; var10 += 2) {
         this.stateTable[var11] = getShort(var2, var10);
         ++var11;
      }

      this.backwardsStateTable = new short[var4];

      for(var11 = 0; var11 < var4; var10 += 2) {
         this.backwardsStateTable[var11] = getShort(var2, var10);
         ++var11;
      }

      this.endStates = new boolean[var5];

      for(var11 = 0; var11 < var5; ++var10) {
         this.endStates[var11] = var2[var10] == 1;
         ++var11;
      }

      this.lookaheadStates = new boolean[var6];

      for(var11 = 0; var11 < var6; ++var10) {
         this.lookaheadStates[var11] = var2[var10] == 1;
         ++var11;
      }

      short[] var16 = new short[512];

      for(int var12 = 0; var12 < 512; var10 += 2) {
         var16[var12] = getShort(var2, var10);
         ++var12;
      }

      byte[] var15 = new byte[var7];
      System.arraycopy(var2, var10, var15, 0, var7);
      var10 += var7;
      this.charCategoryTable = new CompactByteArray(var16, var15);
      int[] var13 = new int[var8];

      for(int var14 = 0; var14 < var8; var10 += 4) {
         var13[var14] = getInt(var2, var10);
         ++var14;
      }

      this.supplementaryCharCategoryTable = new SupplementaryCharacterData(var13);
      if (var9 > 0) {
         this.additionalData = new byte[var9];
         System.arraycopy(var2, var10, this.additionalData, 0, var9);
      }

      this.numCategories = this.stateTable.length / this.endStates.length;
   }

   protected byte[] readFile(final String var1) throws IOException, MissingResourceException {
      BufferedInputStream var2;
      try {
         var2 = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<BufferedInputStream>() {
            public BufferedInputStream run() throws Exception {
               return new BufferedInputStream(this.getClass().getResourceAsStream("/sun/text/resources/" + var1));
            }
         });
      } catch (PrivilegedActionException var7) {
         throw new InternalError(var7.toString(), var7);
      }

      int var3 = 0;
      int var4 = LABEL_LENGTH + 5;
      byte[] var5 = new byte[var4];
      if (var2.read(var5) != var4) {
         throw new MissingResourceException("Wrong header length", var1, "");
      } else {
         for(int var6 = 0; var6 < LABEL_LENGTH; ++var3) {
            if (var5[var3] != LABEL[var3]) {
               throw new MissingResourceException("Wrong magic number", var1, "");
            }

            ++var6;
         }

         if (var5[var3] != 1) {
            throw new MissingResourceException("Unsupported version(" + var5[var3] + ")", var1, "");
         } else {
            ++var3;
            var4 = getInt(var5, var3);
            var5 = new byte[var4];
            if (var2.read(var5) != var4) {
               throw new MissingResourceException("Wrong data length", var1, "");
            } else {
               var2.close();
               return var5;
            }
         }
      }
   }

   byte[] getAdditionalData() {
      return this.additionalData;
   }

   void setAdditionalData(byte[] var1) {
      this.additionalData = var1;
   }

   public Object clone() {
      RuleBasedBreakIterator var1 = (RuleBasedBreakIterator)super.clone();
      if (this.text != null) {
         var1.text = (CharacterIterator)this.text.clone();
      }

      return var1;
   }

   public boolean equals(Object var1) {
      try {
         if (var1 == null) {
            return false;
         } else {
            RuleBasedBreakIterator var2 = (RuleBasedBreakIterator)var1;
            if (this.checksum != var2.checksum) {
               return false;
            } else if (this.text == null) {
               return var2.text == null;
            } else {
               return this.text.equals(var2.text);
            }
         }
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('[');
      var1.append("checksum=0x");
      var1.append(Long.toHexString(this.checksum));
      var1.append(']');
      return var1.toString();
   }

   public int hashCode() {
      return (int)this.checksum;
   }

   public int first() {
      CharacterIterator var1 = this.getText();
      var1.first();
      return var1.getIndex();
   }

   public int last() {
      CharacterIterator var1 = this.getText();
      var1.setIndex(var1.getEndIndex());
      return var1.getIndex();
   }

   public int next(int var1) {
      int var2;
      for(var2 = this.current(); var1 > 0; --var1) {
         var2 = this.handleNext();
      }

      while(var1 < 0) {
         var2 = this.previous();
         ++var1;
      }

      return var2;
   }

   public int next() {
      return this.handleNext();
   }

   public int previous() {
      CharacterIterator var1 = this.getText();
      if (this.current() == var1.getBeginIndex()) {
         return -1;
      } else {
         int var2 = this.current();
         int var3 = this.cachedLastKnownBreak;
         if (var3 < var2 && var3 > -1) {
            var1.setIndex(var3);
         } else {
            this.getPrevious();
            var3 = this.handlePrevious();
         }

         for(int var4 = var3; var4 != -1 && var4 < var2; var4 = this.handleNext()) {
            var3 = var4;
         }

         var1.setIndex(var3);
         this.cachedLastKnownBreak = var3;
         return var3;
      }
   }

   private int getPrevious() {
      char var1 = this.text.previous();
      if (Character.isLowSurrogate(var1) && this.text.getIndex() > this.text.getBeginIndex()) {
         char var2 = this.text.previous();
         if (Character.isHighSurrogate(var2)) {
            return Character.toCodePoint(var2, var1);
         }

         this.text.next();
      }

      return var1;
   }

   int getCurrent() {
      char var1 = this.text.current();
      if (Character.isHighSurrogate(var1) && this.text.getIndex() < this.text.getEndIndex()) {
         char var2 = this.text.next();
         this.text.previous();
         if (Character.isLowSurrogate(var2)) {
            return Character.toCodePoint(var1, var2);
         }
      }

      return var1;
   }

   private int getCurrentCodePointCount() {
      char var1 = this.text.current();
      if (Character.isHighSurrogate(var1) && this.text.getIndex() < this.text.getEndIndex()) {
         char var2 = this.text.next();
         this.text.previous();
         if (Character.isLowSurrogate(var2)) {
            return 2;
         }
      }

      return 1;
   }

   int getNext() {
      int var1 = this.text.getIndex();
      int var2 = this.text.getEndIndex();
      if (var1 != var2 && (var1 += this.getCurrentCodePointCount()) < var2) {
         this.text.setIndex(var1);
         return this.getCurrent();
      } else {
         return 65535;
      }
   }

   private int getNextIndex() {
      int var1 = this.text.getIndex() + this.getCurrentCodePointCount();
      int var2 = this.text.getEndIndex();
      return var1 > var2 ? var2 : var1;
   }

   protected static final void checkOffset(int var0, CharacterIterator var1) {
      if (var0 < var1.getBeginIndex() || var0 > var1.getEndIndex()) {
         throw new IllegalArgumentException("offset out of bounds");
      }
   }

   public int following(int var1) {
      CharacterIterator var2 = this.getText();
      checkOffset(var1, var2);
      var2.setIndex(var1);
      if (var1 == var2.getBeginIndex()) {
         this.cachedLastKnownBreak = this.handleNext();
         return this.cachedLastKnownBreak;
      } else {
         int var3 = this.cachedLastKnownBreak;
         if (var3 < var1 && var3 > -1) {
            var2.setIndex(var3);
         } else {
            var3 = this.handlePrevious();
         }

         while(var3 != -1 && var3 <= var1) {
            var3 = this.handleNext();
         }

         this.cachedLastKnownBreak = var3;
         return var3;
      }
   }

   public int preceding(int var1) {
      CharacterIterator var2 = this.getText();
      checkOffset(var1, var2);
      var2.setIndex(var1);
      return this.previous();
   }

   public boolean isBoundary(int var1) {
      CharacterIterator var2 = this.getText();
      checkOffset(var1, var2);
      if (var1 == var2.getBeginIndex()) {
         return true;
      } else {
         return this.following(var1 - 1) == var1;
      }
   }

   public int current() {
      return this.getText().getIndex();
   }

   public CharacterIterator getText() {
      if (this.text == null) {
         this.text = new StringCharacterIterator("");
      }

      return this.text;
   }

   public void setText(CharacterIterator var1) {
      int var2 = var1.getEndIndex();

      boolean var3;
      try {
         var1.setIndex(var2);
         var3 = var1.getIndex() == var2;
      } catch (IllegalArgumentException var5) {
         var3 = false;
      }

      if (var3) {
         this.text = var1;
      } else {
         this.text = new RuleBasedBreakIterator.SafeCharIterator(var1);
      }

      this.text.first();
      this.cachedLastKnownBreak = -1;
   }

   protected int handleNext() {
      CharacterIterator var1 = this.getText();
      if (var1.getIndex() == var1.getEndIndex()) {
         return -1;
      } else {
         int var2 = this.getNextIndex();
         int var3 = 0;
         int var4 = 1;

         int var6;
         for(var6 = this.getCurrent(); var6 != 65535 && var4 != 0; var6 = this.getNext()) {
            int var5 = this.lookupCategory(var6);
            if (var5 != -1) {
               var4 = this.lookupState(var4, var5);
            }

            if (this.lookaheadStates[var4]) {
               if (this.endStates[var4]) {
                  var2 = var3;
               } else {
                  var3 = this.getNextIndex();
               }
            } else if (this.endStates[var4]) {
               var2 = this.getNextIndex();
            }
         }

         if (var6 == 65535 && var3 == var1.getEndIndex()) {
            var2 = var3;
         }

         var1.setIndex(var2);
         return var2;
      }
   }

   protected int handlePrevious() {
      CharacterIterator var1 = this.getText();
      int var2 = 1;
      int var3 = 0;
      int var4 = 0;

      int var5;
      for(var5 = this.getCurrent(); var5 != 65535 && var2 != 0; var5 = this.getPrevious()) {
         var4 = var3;
         var3 = this.lookupCategory(var5);
         if (var3 != -1) {
            var2 = this.lookupBackwardState(var2, var3);
         }
      }

      if (var5 != 65535) {
         if (var4 != -1) {
            this.getNext();
            this.getNext();
         } else {
            this.getNext();
         }
      }

      return var1.getIndex();
   }

   protected int lookupCategory(int var1) {
      return var1 < 65536 ? this.charCategoryTable.elementAt((char)var1) : this.supplementaryCharCategoryTable.getValue(var1);
   }

   protected int lookupState(int var1, int var2) {
      return this.stateTable[var1 * this.numCategories + var2];
   }

   protected int lookupBackwardState(int var1, int var2) {
      return this.backwardsStateTable[var1 * this.numCategories + var2];
   }

   static long getLong(byte[] var0, int var1) {
      long var2 = (long)(var0[var1] & 255);

      for(int var4 = 1; var4 < 8; ++var4) {
         var2 = var2 << 8 | (long)(var0[var1 + var4] & 255);
      }

      return var2;
   }

   static int getInt(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;

      for(int var3 = 1; var3 < 4; ++var3) {
         var2 = var2 << 8 | var0[var1 + var3] & 255;
      }

      return var2;
   }

   static short getShort(byte[] var0, int var1) {
      short var2 = (short)(var0[var1] & 255);
      var2 = (short)(var2 << 8 | var0[var1 + 1] & 255);
      return var2;
   }

   static {
      LABEL_LENGTH = LABEL.length;
   }

   private static final class SafeCharIterator implements CharacterIterator, Cloneable {
      private CharacterIterator base;
      private int rangeStart;
      private int rangeLimit;
      private int currentIndex;

      SafeCharIterator(CharacterIterator var1) {
         this.base = var1;
         this.rangeStart = var1.getBeginIndex();
         this.rangeLimit = var1.getEndIndex();
         this.currentIndex = var1.getIndex();
      }

      public char first() {
         return this.setIndex(this.rangeStart);
      }

      public char last() {
         return this.setIndex(this.rangeLimit - 1);
      }

      public char current() {
         return this.currentIndex >= this.rangeStart && this.currentIndex < this.rangeLimit ? this.base.setIndex(this.currentIndex) : '\uffff';
      }

      public char next() {
         ++this.currentIndex;
         if (this.currentIndex >= this.rangeLimit) {
            this.currentIndex = this.rangeLimit;
            return '\uffff';
         } else {
            return this.base.setIndex(this.currentIndex);
         }
      }

      public char previous() {
         --this.currentIndex;
         if (this.currentIndex < this.rangeStart) {
            this.currentIndex = this.rangeStart;
            return '\uffff';
         } else {
            return this.base.setIndex(this.currentIndex);
         }
      }

      public char setIndex(int var1) {
         if (var1 >= this.rangeStart && var1 <= this.rangeLimit) {
            this.currentIndex = var1;
            return this.current();
         } else {
            throw new IllegalArgumentException("Invalid position");
         }
      }

      public int getBeginIndex() {
         return this.rangeStart;
      }

      public int getEndIndex() {
         return this.rangeLimit;
      }

      public int getIndex() {
         return this.currentIndex;
      }

      public Object clone() {
         RuleBasedBreakIterator.SafeCharIterator var1 = null;

         try {
            var1 = (RuleBasedBreakIterator.SafeCharIterator)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new Error("Clone not supported: " + var3);
         }

         CharacterIterator var2 = (CharacterIterator)this.base.clone();
         var1.base = var2;
         return var1;
      }
   }
}
