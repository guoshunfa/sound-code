package java.text;

import java.util.ArrayList;

final class MergeCollation {
   ArrayList<PatternEntry> patterns = new ArrayList();
   private transient PatternEntry saveEntry = null;
   private transient PatternEntry lastEntry = null;
   private transient StringBuffer excess = new StringBuffer();
   private transient byte[] statusArray = new byte[8192];
   private final byte BITARRAYMASK = 1;
   private final int BYTEPOWER = 3;
   private final int BYTEMASK = 7;

   public MergeCollation(String var1) throws ParseException {
      for(int var2 = 0; var2 < this.statusArray.length; ++var2) {
         this.statusArray[var2] = 0;
      }

      this.setPattern(var1);
   }

   public String getPattern() {
      return this.getPattern(true);
   }

   public String getPattern(boolean var1) {
      StringBuffer var2 = new StringBuffer();
      PatternEntry var3 = null;
      ArrayList var4 = null;

      int var5;
      PatternEntry var6;
      for(var5 = 0; var5 < this.patterns.size(); ++var5) {
         var6 = (PatternEntry)this.patterns.get(var5);
         if (var6.extension.length() != 0) {
            if (var4 == null) {
               var4 = new ArrayList();
            }

            var4.add(var6);
         } else {
            if (var4 != null) {
               PatternEntry var7 = this.findLastWithNoExtension(var5 - 1);

               for(int var8 = var4.size() - 1; var8 >= 0; --var8) {
                  var3 = (PatternEntry)var4.get(var8);
                  var3.addToBuffer(var2, false, var1, var7);
               }

               var4 = null;
            }

            var6.addToBuffer(var2, false, var1, (PatternEntry)null);
         }
      }

      if (var4 != null) {
         var6 = this.findLastWithNoExtension(var5 - 1);

         for(int var9 = var4.size() - 1; var9 >= 0; --var9) {
            var3 = (PatternEntry)var4.get(var9);
            var3.addToBuffer(var2, false, var1, var6);
         }

         var4 = null;
      }

      return var2.toString();
   }

   private final PatternEntry findLastWithNoExtension(int var1) {
      --var1;

      while(var1 >= 0) {
         PatternEntry var2 = (PatternEntry)this.patterns.get(var1);
         if (var2.extension.length() == 0) {
            return var2;
         }

         --var1;
      }

      return null;
   }

   public String emitPattern() {
      return this.emitPattern(true);
   }

   public String emitPattern(boolean var1) {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var3 < this.patterns.size(); ++var3) {
         PatternEntry var4 = (PatternEntry)this.patterns.get(var3);
         if (var4 != null) {
            var4.addToBuffer(var2, true, var1, (PatternEntry)null);
         }
      }

      return var2.toString();
   }

   public void setPattern(String var1) throws ParseException {
      this.patterns.clear();
      this.addPattern(var1);
   }

   public void addPattern(String var1) throws ParseException {
      if (var1 != null) {
         PatternEntry.Parser var2 = new PatternEntry.Parser(var1);

         for(PatternEntry var3 = var2.next(); var3 != null; var3 = var2.next()) {
            this.fixEntry(var3);
         }

      }
   }

   public int getCount() {
      return this.patterns.size();
   }

   public PatternEntry getItemAt(int var1) {
      return (PatternEntry)this.patterns.get(var1);
   }

   private final void fixEntry(PatternEntry var1) throws ParseException {
      if (this.lastEntry != null && var1.chars.equals(this.lastEntry.chars) && var1.extension.equals(this.lastEntry.extension)) {
         if (var1.strength != 3 && var1.strength != -2) {
            throw new ParseException("The entries " + this.lastEntry + " and " + var1 + " are adjacent in the rules, but have conflicting strengths: A character can't be unequal to itself.", -1);
         }
      } else {
         boolean var2 = true;
         if (var1.strength != -2) {
            int var3 = -1;
            if (var1.chars.length() == 1) {
               char var4 = var1.chars.charAt(0);
               int var5 = var4 >> 3;
               byte var6 = this.statusArray[var5];
               byte var7 = (byte)(1 << (var4 & 7));
               if (var6 != 0 && (var6 & var7) != 0) {
                  var3 = this.patterns.lastIndexOf(var1);
               } else {
                  this.statusArray[var5] = (byte)(var6 | var7);
               }
            } else {
               var3 = this.patterns.lastIndexOf(var1);
            }

            if (var3 != -1) {
               this.patterns.remove(var3);
            }

            this.excess.setLength(0);
            int var8 = this.findLastEntry(this.lastEntry, this.excess);
            if (this.excess.length() != 0) {
               var1.extension = this.excess + var1.extension;
               if (var8 != this.patterns.size()) {
                  this.lastEntry = this.saveEntry;
                  var2 = false;
               }
            }

            if (var8 == this.patterns.size()) {
               this.patterns.add(var1);
               this.saveEntry = var1;
            } else {
               this.patterns.add(var8, var1);
            }
         }

         if (var2) {
            this.lastEntry = var1;
         }

      }
   }

   private final int findLastEntry(PatternEntry var1, StringBuffer var2) throws ParseException {
      if (var1 == null) {
         return 0;
      } else {
         int var3;
         if (var1.strength != -2) {
            var3 = -1;
            if (var1.chars.length() == 1) {
               int var5 = var1.chars.charAt(0) >> 3;
               if ((this.statusArray[var5] & 1 << (var1.chars.charAt(0) & 7)) != 0) {
                  var3 = this.patterns.lastIndexOf(var1);
               }
            } else {
               var3 = this.patterns.lastIndexOf(var1);
            }

            if (var3 == -1) {
               throw new ParseException("couldn't find last entry: " + var1, var3);
            } else {
               return var3 + 1;
            }
         } else {
            for(var3 = this.patterns.size() - 1; var3 >= 0; --var3) {
               PatternEntry var4 = (PatternEntry)this.patterns.get(var3);
               if (var4.chars.regionMatches(0, var1.chars, 0, var4.chars.length())) {
                  var2.append(var1.chars.substring(var4.chars.length(), var1.chars.length()));
                  break;
               }
            }

            if (var3 == -1) {
               throw new ParseException("couldn't find: " + var1, var3);
            } else {
               return var3 + 1;
            }
         }
      }
   }
}
