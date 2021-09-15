package sun.net;

public class URLCanonicalizer {
   public String canonicalize(String var1) {
      String var2 = var1;
      if (var1.startsWith("ftp.")) {
         var2 = "ftp://" + var1;
      } else if (var1.startsWith("gopher.")) {
         var2 = "gopher://" + var1;
      } else if (var1.startsWith("/")) {
         var2 = "file:" + var1;
      } else if (!this.hasProtocolName(var1)) {
         if (this.isSimpleHostName(var1)) {
            var1 = "www." + var1 + ".com";
         }

         var2 = "http://" + var1;
      }

      return var2;
   }

   public boolean hasProtocolName(String var1) {
      int var2 = var1.indexOf(58);
      if (var2 <= 0) {
         return false;
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var1.charAt(var3);
            if ((var4 < 'A' || var4 > 'Z') && (var4 < 'a' || var4 > 'z') && var4 != '-') {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean isSimpleHostName(String var1) {
      for(int var2 = 0; var2 < var1.length(); ++var2) {
         char var3 = var1.charAt(var2);
         if ((var3 < 'A' || var3 > 'Z') && (var3 < 'a' || var3 > 'z') && (var3 < '0' || var3 > '9') && var3 != '-') {
            return false;
         }
      }

      return true;
   }
}
