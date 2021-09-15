package sun.net.www;

import java.util.Iterator;

public class HeaderParser {
   String raw;
   String[][] tab;
   int nkeys;
   int asize = 10;

   public HeaderParser(String var1) {
      this.raw = var1;
      this.tab = new String[this.asize][2];
      this.parse();
   }

   private HeaderParser() {
   }

   public HeaderParser subsequence(int var1, int var2) {
      if (var1 == 0 && var2 == this.nkeys) {
         return this;
      } else if (var1 >= 0 && var1 < var2 && var2 <= this.nkeys) {
         HeaderParser var3 = new HeaderParser();
         var3.tab = new String[this.asize][2];
         var3.asize = this.asize;
         System.arraycopy(this.tab, var1, var3.tab, 0, var2 - var1);
         var3.nkeys = var2 - var1;
         return var3;
      } else {
         throw new IllegalArgumentException("invalid start or end");
      }
   }

   private void parse() {
      if (this.raw != null) {
         this.raw = this.raw.trim();
         char[] var1 = this.raw.toCharArray();
         int var2 = 0;
         int var3 = 0;
         int var4 = 0;
         boolean var5 = true;
         boolean var6 = false;
         int var7 = var1.length;

         while(true) {
            while(var3 < var7) {
               char var8 = var1[var3];
               if (var8 == '=' && !var6) {
                  this.tab[var4][0] = (new String(var1, var2, var3 - var2)).toLowerCase();
                  var5 = false;
                  ++var3;
                  var2 = var3;
               } else if (var8 == '"') {
                  if (var6) {
                     this.tab[var4++][1] = new String(var1, var2, var3 - var2);
                     var6 = false;

                     do {
                        ++var3;
                     } while(var3 < var7 && (var1[var3] == ' ' || var1[var3] == ','));

                     var5 = true;
                     var2 = var3;
                  } else {
                     var6 = true;
                     ++var3;
                     var2 = var3;
                  }
               } else if (var8 != ' ' && var8 != ',') {
                  ++var3;
               } else {
                  if (var6) {
                     ++var3;
                     continue;
                  }

                  if (var5) {
                     this.tab[var4++][0] = (new String(var1, var2, var3 - var2)).toLowerCase();
                  } else {
                     this.tab[var4++][1] = new String(var1, var2, var3 - var2);
                  }

                  while(var3 < var7 && (var1[var3] == ' ' || var1[var3] == ',')) {
                     ++var3;
                  }

                  var5 = true;
                  var2 = var3;
               }

               if (var4 == this.asize) {
                  this.asize *= 2;
                  String[][] var9 = new String[this.asize][2];
                  System.arraycopy(this.tab, 0, var9, 0, this.tab.length);
                  this.tab = var9;
               }
            }

            --var3;
            if (var3 > var2) {
               if (!var5) {
                  if (var1[var3] == '"') {
                     this.tab[var4++][1] = new String(var1, var2, var3 - var2);
                  } else {
                     this.tab[var4++][1] = new String(var1, var2, var3 - var2 + 1);
                  }
               } else {
                  this.tab[var4++][0] = (new String(var1, var2, var3 - var2 + 1)).toLowerCase();
               }
            } else if (var3 == var2) {
               if (!var5) {
                  if (var1[var3] == '"') {
                     this.tab[var4++][1] = String.valueOf(var1[var3 - 1]);
                  } else {
                     this.tab[var4++][1] = String.valueOf(var1[var3]);
                  }
               } else {
                  this.tab[var4++][0] = String.valueOf(var1[var3]).toLowerCase();
               }
            }

            this.nkeys = var4;
            break;
         }
      }

   }

   public String findKey(int var1) {
      return var1 >= 0 && var1 <= this.asize ? this.tab[var1][0] : null;
   }

   public String findValue(int var1) {
      return var1 >= 0 && var1 <= this.asize ? this.tab[var1][1] : null;
   }

   public String findValue(String var1) {
      return this.findValue(var1, (String)null);
   }

   public String findValue(String var1, String var2) {
      if (var1 == null) {
         return var2;
      } else {
         var1 = var1.toLowerCase();

         for(int var3 = 0; var3 < this.asize; ++var3) {
            if (this.tab[var3][0] == null) {
               return var2;
            }

            if (var1.equals(this.tab[var3][0])) {
               return this.tab[var3][1];
            }
         }

         return var2;
      }
   }

   public Iterator<String> keys() {
      return new HeaderParser.ParserIterator(false);
   }

   public Iterator<String> values() {
      return new HeaderParser.ParserIterator(true);
   }

   public String toString() {
      Iterator var1 = this.keys();
      StringBuffer var2 = new StringBuffer();
      var2.append("{size=" + this.asize + " nkeys=" + this.nkeys + " ");

      for(int var3 = 0; var1.hasNext(); ++var3) {
         String var4 = (String)var1.next();
         String var5 = this.findValue(var3);
         if (var5 != null && "".equals(var5)) {
            var5 = null;
         }

         var2.append(" {" + var4 + (var5 == null ? "" : "," + var5) + "}");
         if (var1.hasNext()) {
            var2.append(",");
         }
      }

      var2.append(" }");
      return new String(var2);
   }

   public int findInt(String var1, int var2) {
      try {
         return Integer.parseInt(this.findValue(var1, String.valueOf(var2)));
      } catch (Throwable var4) {
         return var2;
      }
   }

   class ParserIterator implements Iterator<String> {
      int index;
      boolean returnsValue;

      ParserIterator(boolean var2) {
         this.returnsValue = var2;
      }

      public boolean hasNext() {
         return this.index < HeaderParser.this.nkeys;
      }

      public String next() {
         return HeaderParser.this.tab[this.index++][this.returnsValue ? 1 : 0];
      }

      public void remove() {
         throw new UnsupportedOperationException("remove not supported");
      }
   }
}
