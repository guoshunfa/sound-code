package java.text;

import java.util.Vector;
import sun.text.IntHashtable;
import sun.text.UCompactIntArray;

final class RBCollationTables {
   static final int EXPANDCHARINDEX = 2113929216;
   static final int CONTRACTCHARINDEX = 2130706432;
   static final int UNMAPPED = -1;
   static final int PRIMARYORDERMASK = -65536;
   static final int SECONDARYORDERMASK = 65280;
   static final int TERTIARYORDERMASK = 255;
   static final int PRIMARYDIFFERENCEONLY = -65536;
   static final int SECONDARYDIFFERENCEONLY = -256;
   static final int PRIMARYORDERSHIFT = 16;
   static final int SECONDARYORDERSHIFT = 8;
   private String rules = null;
   private boolean frenchSec = false;
   private boolean seAsianSwapping = false;
   private UCompactIntArray mapping = null;
   private Vector<Vector<EntryPair>> contractTable = null;
   private Vector<int[]> expandTable = null;
   private IntHashtable contractFlags = null;
   private short maxSecOrder = 0;
   private short maxTerOrder = 0;

   public RBCollationTables(String var1, int var2) throws ParseException {
      this.rules = var1;
      RBTableBuilder var3 = new RBTableBuilder(new RBCollationTables.BuildAPI());
      var3.build(var1, var2);
   }

   public String getRules() {
      return this.rules;
   }

   public boolean isFrenchSec() {
      return this.frenchSec;
   }

   public boolean isSEAsianSwapping() {
      return this.seAsianSwapping;
   }

   Vector<EntryPair> getContractValues(int var1) {
      int var2 = this.mapping.elementAt(var1);
      return this.getContractValuesImpl(var2 - 2130706432);
   }

   private Vector<EntryPair> getContractValuesImpl(int var1) {
      return var1 >= 0 ? (Vector)this.contractTable.elementAt(var1) : null;
   }

   boolean usedInContractSeq(int var1) {
      return this.contractFlags.get(var1) == 1;
   }

   int getMaxExpansion(int var1) {
      int var2 = 1;
      if (this.expandTable != null) {
         for(int var3 = 0; var3 < this.expandTable.size(); ++var3) {
            int[] var4 = (int[])this.expandTable.elementAt(var3);
            int var5 = var4.length;
            if (var5 > var2 && var4[var5 - 1] == var1) {
               var2 = var5;
            }
         }
      }

      return var2;
   }

   final int[] getExpandValueList(int var1) {
      return (int[])this.expandTable.elementAt(var1 - 2113929216);
   }

   int getUnicodeOrder(int var1) {
      return this.mapping.elementAt(var1);
   }

   short getMaxSecOrder() {
      return this.maxSecOrder;
   }

   short getMaxTerOrder() {
      return this.maxTerOrder;
   }

   static void reverse(StringBuffer var0, int var1, int var2) {
      int var3 = var1;

      for(int var5 = var2 - 1; var3 < var5; --var5) {
         char var4 = var0.charAt(var3);
         var0.setCharAt(var3, var0.charAt(var5));
         var0.setCharAt(var5, var4);
         ++var3;
      }

   }

   static final int getEntry(Vector<EntryPair> var0, String var1, boolean var2) {
      for(int var3 = 0; var3 < var0.size(); ++var3) {
         EntryPair var4 = (EntryPair)var0.elementAt(var3);
         if (var4.fwd == var2 && var4.entryName.equals(var1)) {
            return var3;
         }
      }

      return -1;
   }

   final class BuildAPI {
      private BuildAPI() {
      }

      void fillInTables(boolean var1, boolean var2, UCompactIntArray var3, Vector<Vector<EntryPair>> var4, Vector<int[]> var5, IntHashtable var6, short var7, short var8) {
         RBCollationTables.this.frenchSec = var1;
         RBCollationTables.this.seAsianSwapping = var2;
         RBCollationTables.this.mapping = var3;
         RBCollationTables.this.contractTable = var4;
         RBCollationTables.this.expandTable = var5;
         RBCollationTables.this.contractFlags = var6;
         RBCollationTables.this.maxSecOrder = var7;
         RBCollationTables.this.maxTerOrder = var8;
      }

      // $FF: synthetic method
      BuildAPI(Object var2) {
         this();
      }
   }
}
