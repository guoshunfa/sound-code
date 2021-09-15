package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.io.StringWriter;
import org.omg.CORBA.DATA_CONVERSION;

class Utility {
   private static NamingSystemException wrapper = NamingSystemException.get("naming");

   static String cleanEscapes(String var0) {
      StringWriter var1 = new StringWriter();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 != '%') {
            var1.write(var3);
         } else {
            ++var2;
            int var4 = hexOf(var0.charAt(var2));
            ++var2;
            int var5 = hexOf(var0.charAt(var2));
            int var6 = var4 * 16 + var5;
            var1.write((char)var6);
         }
      }

      return var1.toString();
   }

   static int hexOf(char var0) {
      int var1 = var0 - 48;
      if (var1 >= 0 && var1 <= 9) {
         return var1;
      } else {
         var1 = var0 - 97 + 10;
         if (var1 >= 10 && var1 <= 15) {
            return var1;
         } else {
            var1 = var0 - 65 + 10;
            if (var1 >= 10 && var1 <= 15) {
               return var1;
            } else {
               throw new DATA_CONVERSION();
            }
         }
      }
   }

   static void validateGIOPVersion(IIOPEndpointInfo var0) {
      if (var0.getMajor() > 1 || var0.getMinor() > 2) {
         throw wrapper.insBadAddress();
      }
   }
}
