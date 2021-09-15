package sun.util.locale.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.MissingResourceException;
import sun.text.CompactByteArray;
import sun.text.SupplementaryCharacterData;

class BreakDictionary {
   private static int supportedVersion = 1;
   private CompactByteArray columnMap = null;
   private SupplementaryCharacterData supplementaryCharColumnMap = null;
   private int numCols;
   private int numColGroups;
   private short[] table = null;
   private short[] rowIndex = null;
   private int[] rowIndexFlags = null;
   private short[] rowIndexFlagsIndex = null;
   private byte[] rowIndexShifts = null;

   BreakDictionary(String var1) throws IOException, MissingResourceException {
      this.readDictionaryFile(var1);
   }

   private void readDictionaryFile(final String var1) throws IOException, MissingResourceException {
      BufferedInputStream var2;
      try {
         var2 = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<BufferedInputStream>() {
            public BufferedInputStream run() throws Exception {
               return new BufferedInputStream(this.getClass().getResourceAsStream("/sun/text/resources/" + var1));
            }
         });
      } catch (PrivilegedActionException var12) {
         throw new InternalError(var12.toString(), var12);
      }

      byte[] var3 = new byte[8];
      if (var2.read(var3) != 8) {
         throw new MissingResourceException("Wrong data length", var1, "");
      } else {
         int var4 = RuleBasedBreakIterator.getInt(var3, 0);
         if (var4 != supportedVersion) {
            throw new MissingResourceException("Dictionary version(" + var4 + ") is unsupported", var1, "");
         } else {
            int var5 = RuleBasedBreakIterator.getInt(var3, 4);
            var3 = new byte[var5];
            if (var2.read(var3) != var5) {
               throw new MissingResourceException("Wrong data length", var1, "");
            } else {
               var2.close();
               byte var7 = 0;
               int var6 = RuleBasedBreakIterator.getInt(var3, var7);
               int var13 = var7 + 4;
               short[] var8 = new short[var6];

               for(int var9 = 0; var9 < var6; var13 += 2) {
                  var8[var9] = RuleBasedBreakIterator.getShort(var3, var13);
                  ++var9;
               }

               var6 = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               byte[] var14 = new byte[var6];

               int var10;
               for(var10 = 0; var10 < var6; ++var13) {
                  var14[var10] = var3[var13];
                  ++var10;
               }

               this.columnMap = new CompactByteArray(var8, var14);
               this.numCols = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               this.numColGroups = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               var6 = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               this.rowIndex = new short[var6];

               for(var10 = 0; var10 < var6; var13 += 2) {
                  this.rowIndex[var10] = RuleBasedBreakIterator.getShort(var3, var13);
                  ++var10;
               }

               var6 = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               this.rowIndexFlagsIndex = new short[var6];

               for(var10 = 0; var10 < var6; var13 += 2) {
                  this.rowIndexFlagsIndex[var10] = RuleBasedBreakIterator.getShort(var3, var13);
                  ++var10;
               }

               var6 = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               this.rowIndexFlags = new int[var6];

               for(var10 = 0; var10 < var6; var13 += 4) {
                  this.rowIndexFlags[var10] = RuleBasedBreakIterator.getInt(var3, var13);
                  ++var10;
               }

               var6 = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               this.rowIndexShifts = new byte[var6];

               for(var10 = 0; var10 < var6; ++var13) {
                  this.rowIndexShifts[var10] = var3[var13];
                  ++var10;
               }

               var6 = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               this.table = new short[var6];

               for(var10 = 0; var10 < var6; var13 += 2) {
                  this.table[var10] = RuleBasedBreakIterator.getShort(var3, var13);
                  ++var10;
               }

               var6 = RuleBasedBreakIterator.getInt(var3, var13);
               var13 += 4;
               int[] var15 = new int[var6];

               for(int var11 = 0; var11 < var6; var13 += 4) {
                  var15[var11] = RuleBasedBreakIterator.getInt(var3, var13);
                  ++var11;
               }

               this.supplementaryCharColumnMap = new SupplementaryCharacterData(var15);
            }
         }
      }
   }

   public final short getNextStateFromCharacter(int var1, int var2) {
      int var3;
      if (var2 < 65536) {
         var3 = this.columnMap.elementAt((char)var2);
      } else {
         var3 = this.supplementaryCharColumnMap.getValue(var2);
      }

      return this.getNextState(var1, var3);
   }

   public final short getNextState(int var1, int var2) {
      return this.cellIsPopulated(var1, var2) ? this.internalAt(this.rowIndex[var1], var2 + this.rowIndexShifts[var1]) : 0;
   }

   private boolean cellIsPopulated(int var1, int var2) {
      if (this.rowIndexFlagsIndex[var1] < 0) {
         return var2 == -this.rowIndexFlagsIndex[var1];
      } else {
         int var3 = this.rowIndexFlags[this.rowIndexFlagsIndex[var1] + (var2 >> 5)];
         return (var3 & 1 << (var2 & 31)) != 0;
      }
   }

   private short internalAt(int var1, int var2) {
      return this.table[var1 * this.numCols + var2];
   }
}
