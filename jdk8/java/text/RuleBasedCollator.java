package java.text;

public class RuleBasedCollator extends Collator {
   static final int CHARINDEX = 1879048192;
   static final int EXPANDCHARINDEX = 2113929216;
   static final int CONTRACTCHARINDEX = 2130706432;
   static final int UNMAPPED = -1;
   private static final int COLLATIONKEYOFFSET = 1;
   private RBCollationTables tables;
   private StringBuffer primResult;
   private StringBuffer secResult;
   private StringBuffer terResult;
   private CollationElementIterator sourceCursor;
   private CollationElementIterator targetCursor;

   public RuleBasedCollator(String var1) throws ParseException {
      this(var1, 1);
   }

   RuleBasedCollator(String var1, int var2) throws ParseException {
      this.tables = null;
      this.primResult = null;
      this.secResult = null;
      this.terResult = null;
      this.sourceCursor = null;
      this.targetCursor = null;
      this.setStrength(2);
      this.setDecomposition(var2);
      this.tables = new RBCollationTables(var1, var2);
   }

   private RuleBasedCollator(RuleBasedCollator var1) {
      this.tables = null;
      this.primResult = null;
      this.secResult = null;
      this.terResult = null;
      this.sourceCursor = null;
      this.targetCursor = null;
      this.setStrength(var1.getStrength());
      this.setDecomposition(var1.getDecomposition());
      this.tables = var1.tables;
   }

   public String getRules() {
      return this.tables.getRules();
   }

   public CollationElementIterator getCollationElementIterator(String var1) {
      return new CollationElementIterator(var1, this);
   }

   public CollationElementIterator getCollationElementIterator(CharacterIterator var1) {
      return new CollationElementIterator(var1, this);
   }

   public synchronized int compare(String var1, String var2) {
      if (var1 != null && var2 != null) {
         int var3 = 0;
         if (this.sourceCursor == null) {
            this.sourceCursor = this.getCollationElementIterator(var1);
         } else {
            this.sourceCursor.setText(var1);
         }

         if (this.targetCursor == null) {
            this.targetCursor = this.getCollationElementIterator(var2);
         } else {
            this.targetCursor.setText(var2);
         }

         int var4 = 0;
         int var5 = 0;
         boolean var6 = this.getStrength() >= 1;
         boolean var7 = var6;
         boolean var8 = this.getStrength() >= 2;
         boolean var9 = true;
         boolean var10 = true;

         while(true) {
            if (var9) {
               var4 = this.sourceCursor.next();
            } else {
               var9 = true;
            }

            if (var10) {
               var5 = this.targetCursor.next();
            } else {
               var10 = true;
            }

            int var11;
            if (var4 == -1 || var5 == -1) {
               if (var4 != -1) {
                  do {
                     if (CollationElementIterator.primaryOrder(var4) != 0) {
                        return 1;
                     }

                     if (CollationElementIterator.secondaryOrder(var4) != 0 && var7) {
                        var3 = 1;
                        var7 = false;
                     }
                  } while((var4 = this.sourceCursor.next()) != -1);
               } else if (var5 != -1) {
                  do {
                     if (CollationElementIterator.primaryOrder(var5) != 0) {
                        return -1;
                     }

                     if (CollationElementIterator.secondaryOrder(var5) != 0 && var7) {
                        var3 = -1;
                        var7 = false;
                     }
                  } while((var5 = this.targetCursor.next()) != -1);
               }

               if (var3 == 0 && this.getStrength() == 3) {
                  var11 = this.getDecomposition();
                  Normalizer.Form var17;
                  if (var11 == 1) {
                     var17 = Normalizer.Form.NFD;
                  } else {
                     if (var11 != 2) {
                        return var1.compareTo(var2);
                     }

                     var17 = Normalizer.Form.NFKD;
                  }

                  String var18 = Normalizer.normalize(var1, var17);
                  String var19 = Normalizer.normalize(var2, var17);
                  return var18.compareTo(var19);
               }

               return var3;
            }

            var11 = CollationElementIterator.primaryOrder(var4);
            int var12 = CollationElementIterator.primaryOrder(var5);
            if (var4 == var5) {
               if (this.tables.isFrenchSec() && var11 != 0 && !var7) {
                  var7 = var6;
                  var8 = false;
               }
            } else if (var11 != var12) {
               if (var4 == 0) {
                  var10 = false;
               } else if (var5 == 0) {
                  var9 = false;
               } else if (var11 == 0) {
                  if (var7) {
                     var3 = 1;
                     var7 = false;
                  }

                  var10 = false;
               } else {
                  if (var12 != 0) {
                     if (var11 < var12) {
                        return -1;
                     }

                     return 1;
                  }

                  if (var7) {
                     var3 = -1;
                     var7 = false;
                  }

                  var9 = false;
               }
            } else if (var7) {
               short var13 = CollationElementIterator.secondaryOrder(var4);
               short var14 = CollationElementIterator.secondaryOrder(var5);
               if (var13 != var14) {
                  var3 = var13 < var14 ? -1 : 1;
                  var7 = false;
               } else if (var8) {
                  short var15 = CollationElementIterator.tertiaryOrder(var4);
                  short var16 = CollationElementIterator.tertiaryOrder(var5);
                  if (var15 != var16) {
                     var3 = var15 < var16 ? -1 : 1;
                     var8 = false;
                  }
               }
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public synchronized CollationKey getCollationKey(String var1) {
      if (var1 == null) {
         return null;
      } else {
         if (this.primResult == null) {
            this.primResult = new StringBuffer();
            this.secResult = new StringBuffer();
            this.terResult = new StringBuffer();
         } else {
            this.primResult.setLength(0);
            this.secResult.setLength(0);
            this.terResult.setLength(0);
         }

         boolean var2 = false;
         boolean var3 = this.getStrength() >= 1;
         boolean var4 = this.getStrength() >= 2;
         boolean var5 = true;
         boolean var6 = true;
         int var7 = 0;
         if (this.sourceCursor == null) {
            this.sourceCursor = this.getCollationElementIterator(var1);
         } else {
            this.sourceCursor.setText(var1);
         }

         int var9;
         while((var9 = this.sourceCursor.next()) != -1) {
            short var10 = CollationElementIterator.secondaryOrder(var9);
            short var11 = CollationElementIterator.tertiaryOrder(var9);
            if (!CollationElementIterator.isIgnorable(var9)) {
               this.primResult.append((char)(CollationElementIterator.primaryOrder(var9) + 1));
               if (var3) {
                  if (this.tables.isFrenchSec() && var7 < this.secResult.length()) {
                     RBCollationTables.reverse(this.secResult, var7, this.secResult.length());
                  }

                  this.secResult.append((char)(var10 + 1));
                  var7 = this.secResult.length();
               }

               if (var4) {
                  this.terResult.append((char)(var11 + 1));
               }
            } else {
               if (var3 && var10 != 0) {
                  this.secResult.append((char)(var10 + this.tables.getMaxSecOrder() + 1));
               }

               if (var4 && var11 != 0) {
                  this.terResult.append((char)(var11 + this.tables.getMaxTerOrder() + 1));
               }
            }
         }

         if (this.tables.isFrenchSec()) {
            if (var7 < this.secResult.length()) {
               RBCollationTables.reverse(this.secResult, var7, this.secResult.length());
            }

            RBCollationTables.reverse(this.secResult, 0, this.secResult.length());
         }

         this.primResult.append('\u0000');
         this.secResult.append('\u0000');
         this.secResult.append(this.terResult.toString());
         this.primResult.append(this.secResult.toString());
         if (this.getStrength() == 3) {
            this.primResult.append('\u0000');
            int var8 = this.getDecomposition();
            if (var8 == 1) {
               this.primResult.append(Normalizer.normalize(var1, Normalizer.Form.NFD));
            } else if (var8 == 2) {
               this.primResult.append(Normalizer.normalize(var1, Normalizer.Form.NFKD));
            } else {
               this.primResult.append(var1);
            }
         }

         return new RuleBasedCollationKey(var1, this.primResult.toString());
      }
   }

   public Object clone() {
      if (this.getClass() == RuleBasedCollator.class) {
         return new RuleBasedCollator(this);
      } else {
         RuleBasedCollator var1 = (RuleBasedCollator)super.clone();
         var1.primResult = null;
         var1.secResult = null;
         var1.terResult = null;
         var1.sourceCursor = null;
         var1.targetCursor = null;
         return var1;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         RuleBasedCollator var2 = (RuleBasedCollator)var1;
         return this.getRules().equals(var2.getRules());
      }
   }

   public int hashCode() {
      return this.getRules().hashCode();
   }

   RBCollationTables getTables() {
      return this.tables;
   }
}
