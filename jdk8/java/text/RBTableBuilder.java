package java.text;

import java.util.Vector;
import sun.text.ComposedCharIter;
import sun.text.IntHashtable;
import sun.text.UCompactIntArray;
import sun.text.normalizer.NormalizerImpl;

final class RBTableBuilder {
   static final int CHARINDEX = 1879048192;
   private static final int IGNORABLEMASK = 65535;
   private static final int PRIMARYORDERINCREMENT = 65536;
   private static final int SECONDARYORDERINCREMENT = 256;
   private static final int TERTIARYORDERINCREMENT = 1;
   private static final int INITIALTABLESIZE = 20;
   private static final int MAXKEYSIZE = 5;
   private RBCollationTables.BuildAPI tables = null;
   private MergeCollation mPattern = null;
   private boolean isOverIgnore = false;
   private char[] keyBuf = new char[5];
   private IntHashtable contractFlags = new IntHashtable(100);
   private boolean frenchSec = false;
   private boolean seAsianSwapping = false;
   private UCompactIntArray mapping = null;
   private Vector<Vector<EntryPair>> contractTable = null;
   private Vector<int[]> expandTable = null;
   private short maxSecOrder = 0;
   private short maxTerOrder = 0;

   public RBTableBuilder(RBCollationTables.BuildAPI var1) {
      this.tables = var1;
   }

   public void build(String var1, int var2) throws ParseException {
      boolean var3 = true;
      boolean var4 = false;
      if (var1.length() == 0) {
         throw new ParseException("Build rules empty.", 0);
      } else {
         this.mapping = new UCompactIntArray(-1);
         var1 = NormalizerImpl.canonicalDecomposeWithSingleQuotation(var1);
         this.mPattern = new MergeCollation(var1);
         int var7 = 0;

         for(int var10 = 0; var10 < this.mPattern.getCount(); ++var10) {
            PatternEntry var8 = this.mPattern.getItemAt(var10);
            if (var8 != null) {
               String var6 = var8.getChars();
               if (var6.length() > 1) {
                  switch(var6.charAt(var6.length() - 1)) {
                  case '!':
                     this.seAsianSwapping = true;
                     var6 = var6.substring(0, var6.length() - 1);
                     break;
                  case '@':
                     this.frenchSec = true;
                     var6 = var6.substring(0, var6.length() - 1);
                  }
               }

               var7 = this.increment(var8.getStrength(), var7);
               String var5 = var8.getExtension();
               if (var5.length() != 0) {
                  this.addExpandOrder(var6, var5, var7);
               } else {
                  char var9;
                  if (var6.length() > 1) {
                     var9 = var6.charAt(0);
                     if (Character.isHighSurrogate(var9) && var6.length() == 2) {
                        this.addOrder(Character.toCodePoint(var9, var6.charAt(1)), var7);
                     } else {
                        this.addContractOrder(var6, var7);
                     }
                  } else {
                     var9 = var6.charAt(0);
                     this.addOrder(var9, var7);
                  }
               }
            }
         }

         this.addComposedChars();
         this.commit();
         this.mapping.compact();
         this.tables.fillInTables(this.frenchSec, this.seAsianSwapping, this.mapping, this.contractTable, this.expandTable, this.contractFlags, this.maxSecOrder, this.maxTerOrder);
      }
   }

   private void addComposedChars() throws ParseException {
      ComposedCharIter var1 = new ComposedCharIter();

      while(true) {
         while(true) {
            int var2;
            do {
               if ((var2 = var1.next()) == -1) {
                  return;
               }
            } while(this.getCharOrder(var2) != -1);

            String var3 = var1.decomposition();
            int var7;
            if (var3.length() == 1) {
               var7 = this.getCharOrder(var3.charAt(0));
               if (var7 != -1) {
                  this.addOrder(var2, var7);
               }
            } else {
               if (var3.length() == 2) {
                  char var4 = var3.charAt(0);
                  if (Character.isHighSurrogate(var4)) {
                     int var8 = this.getCharOrder(var3.codePointAt(0));
                     if (var8 != -1) {
                        this.addOrder(var2, var8);
                     }
                     continue;
                  }
               }

               var7 = this.getContractOrder(var3);
               if (var7 != -1) {
                  this.addOrder(var2, var7);
               } else {
                  boolean var5 = true;

                  for(int var6 = 0; var6 < var3.length(); ++var6) {
                     if (this.getCharOrder(var3.charAt(var6)) == -1) {
                        var5 = false;
                        break;
                     }
                  }

                  if (var5) {
                     this.addExpandOrder(var2, var3, -1);
                  }
               }
            }
         }
      }
   }

   private final void commit() {
      if (this.expandTable != null) {
         for(int var1 = 0; var1 < this.expandTable.size(); ++var1) {
            int[] var2 = (int[])this.expandTable.elementAt(var1);

            for(int var3 = 0; var3 < var2.length; ++var3) {
               int var4 = var2[var3];
               if (var4 < 2113929216 && var4 > 1879048192) {
                  int var5 = var4 - 1879048192;
                  int var6 = this.getCharOrder(var5);
                  if (var6 == -1) {
                     var2[var3] = '\uffff' & var5;
                  } else {
                     var2[var3] = var6;
                  }
               }
            }
         }
      }

   }

   private final int increment(int var1, int var2) {
      switch(var1) {
      case 0:
         var2 += 65536;
         var2 &= -65536;
         this.isOverIgnore = true;
         break;
      case 1:
         var2 += 256;
         var2 &= -256;
         if (!this.isOverIgnore) {
            ++this.maxSecOrder;
         }
         break;
      case 2:
         ++var2;
         if (!this.isOverIgnore) {
            ++this.maxTerOrder;
         }
      }

      return var2;
   }

   private final void addOrder(int var1, int var2) {
      int var3 = this.mapping.elementAt(var1);
      if (var3 >= 2130706432) {
         int var4 = 1;
         if (Character.isSupplementaryCodePoint(var1)) {
            var4 = Character.toChars(var1, this.keyBuf, 0);
         } else {
            this.keyBuf[0] = (char)var1;
         }

         this.addContractOrder(new String(this.keyBuf, 0, var4), var2);
      } else {
         this.mapping.setElementAt(var1, var2);
      }

   }

   private final void addContractOrder(String var1, int var2) {
      this.addContractOrder(var1, var2, true);
   }

   private final void addContractOrder(String var1, int var2, boolean var3) {
      if (this.contractTable == null) {
         this.contractTable = new Vector(20);
      }

      int var4 = var1.codePointAt(0);
      int var5 = this.mapping.elementAt(var4);
      Vector var6 = this.getContractValuesImpl(var5 - 2130706432);
      int var7;
      if (var6 == null) {
         var7 = 2130706432 + this.contractTable.size();
         var6 = new Vector(20);
         this.contractTable.addElement(var6);
         var6.addElement(new EntryPair(var1.substring(0, Character.charCount(var4)), var5));
         this.mapping.setElementAt(var4, var7);
      }

      var7 = RBCollationTables.getEntry(var6, var1, var3);
      EntryPair var8;
      if (var7 != -1) {
         var8 = (EntryPair)var6.elementAt(var7);
         var8.value = var2;
      } else {
         var8 = (EntryPair)var6.lastElement();
         if (var1.length() > var8.entryName.length()) {
            var6.addElement(new EntryPair(var1, var2, var3));
         } else {
            var6.insertElementAt(new EntryPair(var1, var2, var3), var6.size() - 1);
         }
      }

      if (var3 && var1.length() > 1) {
         this.addContractFlags(var1);
         this.addContractOrder((new StringBuffer(var1)).reverse().toString(), var2, false);
      }

   }

   private int getContractOrder(String var1) {
      int var2 = -1;
      if (this.contractTable != null) {
         int var3 = var1.codePointAt(0);
         Vector var4 = this.getContractValues(var3);
         if (var4 != null) {
            int var5 = RBCollationTables.getEntry(var4, var1, true);
            if (var5 != -1) {
               EntryPair var6 = (EntryPair)var4.elementAt(var5);
               var2 = var6.value;
            }
         }
      }

      return var2;
   }

   private final int getCharOrder(int var1) {
      int var2 = this.mapping.elementAt(var1);
      if (var2 >= 2130706432) {
         Vector var3 = this.getContractValuesImpl(var2 - 2130706432);
         EntryPair var4 = (EntryPair)var3.firstElement();
         var2 = var4.value;
      }

      return var2;
   }

   private Vector<EntryPair> getContractValues(int var1) {
      int var2 = this.mapping.elementAt(var1);
      return this.getContractValuesImpl(var2 - 2130706432);
   }

   private Vector<EntryPair> getContractValuesImpl(int var1) {
      return var1 >= 0 ? (Vector)this.contractTable.elementAt(var1) : null;
   }

   private final void addExpandOrder(String var1, String var2, int var3) throws ParseException {
      int var4 = this.addExpansion(var3, var2);
      if (var1.length() > 1) {
         char var5 = var1.charAt(0);
         if (Character.isHighSurrogate(var5) && var1.length() == 2) {
            char var6 = var1.charAt(1);
            if (Character.isLowSurrogate(var6)) {
               this.addOrder(Character.toCodePoint(var5, var6), var4);
            }
         } else {
            this.addContractOrder(var1, var4);
         }
      } else {
         this.addOrder(var1.charAt(0), var4);
      }

   }

   private final void addExpandOrder(int var1, String var2, int var3) throws ParseException {
      int var4 = this.addExpansion(var3, var2);
      this.addOrder(var1, var4);
   }

   private int addExpansion(int var1, String var2) {
      if (this.expandTable == null) {
         this.expandTable = new Vector(20);
      }

      int var3 = var1 == -1 ? 0 : 1;
      int[] var4 = new int[var2.length() + var3];
      if (var3 == 1) {
         var4[0] = var1;
      }

      int var5 = var3;

      int var6;
      for(var6 = 0; var6 < var2.length(); ++var6) {
         char var7 = var2.charAt(var6);
         int var9;
         if (Character.isHighSurrogate(var7)) {
            ++var6;
            char var8;
            if (var6 == var2.length() || !Character.isLowSurrogate(var8 = var2.charAt(var6))) {
               break;
            }

            var9 = Character.toCodePoint(var7, var8);
         } else {
            var9 = var7;
         }

         int var10 = this.getCharOrder(var9);
         if (var10 != -1) {
            var4[var5++] = var10;
         } else {
            var4[var5++] = 1879048192 + var9;
         }
      }

      if (var5 < var4.length) {
         int[] var11 = new int[var5];

         while(true) {
            --var5;
            if (var5 < 0) {
               var4 = var11;
               break;
            }

            var11[var5] = var4[var5];
         }
      }

      var6 = 2113929216 + this.expandTable.size();
      this.expandTable.addElement(var4);
      return var6;
   }

   private void addContractFlags(String var1) {
      int var4 = var1.length();

      for(int var5 = 0; var5 < var4; ++var5) {
         char var2 = var1.charAt(var5);
         int var10000;
         if (Character.isHighSurrogate(var2)) {
            ++var5;
            var10000 = Character.toCodePoint(var2, var1.charAt(var5));
         } else {
            var10000 = var2;
         }

         int var3 = var10000;
         this.contractFlags.put(var3, 1);
      }

   }
}
