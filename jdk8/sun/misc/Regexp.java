package sun.misc;

public class Regexp {
   public boolean ignoreCase;
   public String exp;
   public String prefix;
   public String suffix;
   public boolean exact;
   public int prefixLen;
   public int suffixLen;
   public int totalLen;
   public String[] mids;

   public Regexp(String var1) {
      this.exp = var1;
      int var2 = var1.indexOf(42);
      int var3 = var1.lastIndexOf(42);
      if (var2 < 0) {
         this.totalLen = var1.length();
         this.exact = true;
      } else {
         this.prefixLen = var2;
         if (var2 == 0) {
            this.prefix = null;
         } else {
            this.prefix = var1.substring(0, var2);
         }

         this.suffixLen = var1.length() - var3 - 1;
         if (this.suffixLen == 0) {
            this.suffix = null;
         } else {
            this.suffix = var1.substring(var3 + 1);
         }

         int var4 = 0;

         int var5;
         for(var5 = var2; var5 < var3 && var5 >= 0; var5 = var1.indexOf(42, var5 + 1)) {
            ++var4;
         }

         this.totalLen = this.prefixLen + this.suffixLen;
         if (var4 > 0) {
            this.mids = new String[var4];
            var5 = var2;

            for(int var6 = 0; var6 < var4; ++var6) {
               ++var5;
               int var7 = var1.indexOf(42, var5);
               if (var5 < var7) {
                  this.mids[var6] = var1.substring(var5, var7);
                  this.totalLen += this.mids[var6].length();
               }

               var5 = var7;
            }
         }
      }

   }

   final boolean matches(String var1) {
      return this.matches(var1, 0, var1.length());
   }

   boolean matches(String var1, int var2, int var3) {
      if (this.exact) {
         return var3 == this.totalLen && this.exp.regionMatches(this.ignoreCase, 0, var1, var2, var3);
      } else if (var3 < this.totalLen) {
         return false;
      } else if (this.prefixLen > 0 && !this.prefix.regionMatches(this.ignoreCase, 0, var1, var2, this.prefixLen) || this.suffixLen > 0 && !this.suffix.regionMatches(this.ignoreCase, 0, var1, var2 + var3 - this.suffixLen, this.suffixLen)) {
         return false;
      } else if (this.mids == null) {
         return true;
      } else {
         int var4 = this.mids.length;
         int var5 = var2 + this.prefixLen;
         int var6 = var2 + var3 - this.suffixLen;

         for(int var7 = 0; var7 < var4; ++var7) {
            String var8 = this.mids[var7];

            int var9;
            for(var9 = var8.length(); var5 + var9 <= var6 && !var8.regionMatches(this.ignoreCase, 0, var1, var5, var9); ++var5) {
            }

            if (var5 + var9 > var6) {
               return false;
            }

            var5 += var9;
         }

         return true;
      }
   }
}
