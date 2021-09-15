package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.StringReader;

public class RFC2253Parser {
   public static String rfc2253toXMLdsig(String var0) {
      String var1 = normalize(var0, true);
      return rfctoXML(var1);
   }

   public static String xmldsigtoRFC2253(String var0) {
      String var1 = normalize(var0, false);
      return xmltoRFC(var1);
   }

   public static String normalize(String var0) {
      return normalize(var0, true);
   }

   public static String normalize(String var0, boolean var1) {
      if (var0 != null && !var0.equals("")) {
         try {
            String var2 = semicolonToComma(var0);
            StringBuilder var3 = new StringBuilder();
            int var4 = 0;
            int var5 = 0;

            int var6;
            for(int var7 = 0; (var6 = var2.indexOf(44, var7)) >= 0; var7 = var6 + 1) {
               var5 += countQuotes(var2, var7, var6);
               if (var6 > 0 && var2.charAt(var6 - 1) != '\\' && var5 % 2 == 0) {
                  var3.append(parseRDN(var2.substring(var4, var6).trim(), var1) + ",");
                  var4 = var6 + 1;
                  var5 = 0;
               }
            }

            var3.append(parseRDN(trim(var2.substring(var4)), var1));
            return var3.toString();
         } catch (IOException var8) {
            return var0;
         }
      } else {
         return "";
      }
   }

   static String parseRDN(String var0, boolean var1) throws IOException {
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;
      int var4 = 0;

      int var5;
      for(int var6 = 0; (var5 = var0.indexOf(43, var6)) >= 0; var6 = var5 + 1) {
         var4 += countQuotes(var0, var6, var5);
         if (var5 > 0 && var0.charAt(var5 - 1) != '\\' && var4 % 2 == 0) {
            var2.append(parseATAV(trim(var0.substring(var3, var5)), var1) + "+");
            var3 = var5 + 1;
            var4 = 0;
         }
      }

      var2.append(parseATAV(trim(var0.substring(var3)), var1));
      return var2.toString();
   }

   static String parseATAV(String var0, boolean var1) throws IOException {
      int var2 = var0.indexOf(61);
      if (var2 == -1 || var2 > 0 && var0.charAt(var2 - 1) == '\\') {
         return var0;
      } else {
         String var3 = normalizeAT(var0.substring(0, var2));
         String var4 = null;
         if (var3.charAt(0) >= '0' && var3.charAt(0) <= '9') {
            var4 = var0.substring(var2 + 1);
         } else {
            var4 = normalizeV(var0.substring(var2 + 1), var1);
         }

         return var3 + "=" + var4;
      }
   }

   static String normalizeAT(String var0) {
      String var1 = var0.toUpperCase().trim();
      if (var1.startsWith("OID")) {
         var1 = var1.substring(3);
      }

      return var1;
   }

   static String normalizeV(String var0, boolean var1) throws IOException {
      String var2 = trim(var0);
      if (var2.startsWith("\"")) {
         StringBuilder var3 = new StringBuilder();
         StringReader var4 = new StringReader(var2.substring(1, var2.length() - 1));

         char var6;
         int var7;
         for(boolean var5 = false; (var7 = var4.read()) > -1; var3.append(var6)) {
            var6 = (char)var7;
            if (var6 == ',' || var6 == '=' || var6 == '+' || var6 == '<' || var6 == '>' || var6 == '#' || var6 == ';') {
               var3.append('\\');
            }
         }

         var2 = trim(var3.toString());
      }

      if (var1) {
         if (var2.startsWith("#")) {
            var2 = '\\' + var2;
         }
      } else if (var2.startsWith("\\#")) {
         var2 = var2.substring(1);
      }

      return var2;
   }

   static String rfctoXML(String var0) {
      try {
         String var1 = changeLess32toXML(var0);
         return changeWStoXML(var1);
      } catch (Exception var2) {
         return var0;
      }
   }

   static String xmltoRFC(String var0) {
      try {
         String var1 = changeLess32toRFC(var0);
         return changeWStoRFC(var1);
      } catch (Exception var2) {
         return var0;
      }
   }

   static String changeLess32toRFC(String var0) throws IOException {
      StringBuilder var1 = new StringBuilder();
      StringReader var2 = new StringReader(var0);
      boolean var3 = false;

      while(true) {
         while(true) {
            int var8;
            while((var8 = var2.read()) > -1) {
               char var4 = (char)var8;
               if (var4 == '\\') {
                  var1.append(var4);
                  char var5 = (char)var2.read();
                  char var6 = (char)var2.read();
                  if ((var5 >= '0' && var5 <= '9' || var5 >= 'A' && var5 <= 'F' || var5 >= 'a' && var5 <= 'f') && (var6 >= '0' && var6 <= '9' || var6 >= 'A' && var6 <= 'F' || var6 >= 'a' && var6 <= 'f')) {
                     char var7 = (char)Byte.parseByte("" + var5 + var6, 16);
                     var1.append(var7);
                  } else {
                     var1.append(var5);
                     var1.append(var6);
                  }
               } else {
                  var1.append(var4);
               }
            }

            return var1.toString();
         }
      }
   }

   static String changeLess32toXML(String var0) throws IOException {
      StringBuilder var1 = new StringBuilder();
      StringReader var2 = new StringReader(var0);
      boolean var3 = false;

      int var4;
      while((var4 = var2.read()) > -1) {
         if (var4 < 32) {
            var1.append('\\');
            var1.append(Integer.toHexString(var4));
         } else {
            var1.append((char)var4);
         }
      }

      return var1.toString();
   }

   static String changeWStoXML(String var0) throws IOException {
      StringBuilder var1 = new StringBuilder();
      StringReader var2 = new StringReader(var0);
      boolean var3 = false;

      int var7;
      while((var7 = var2.read()) > -1) {
         char var4 = (char)var7;
         if (var4 == '\\') {
            char var5 = (char)var2.read();
            if (var5 == ' ') {
               var1.append('\\');
               String var6 = "20";
               var1.append(var6);
            } else {
               var1.append('\\');
               var1.append(var5);
            }
         } else {
            var1.append(var4);
         }
      }

      return var1.toString();
   }

   static String changeWStoRFC(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;

      int var3;
      for(int var4 = 0; (var3 = var0.indexOf("\\20", var4)) >= 0; var4 = var3 + 3) {
         var1.append(trim(var0.substring(var2, var3)) + "\\ ");
         var2 = var3 + 3;
      }

      var1.append(var0.substring(var2));
      return var1.toString();
   }

   static String semicolonToComma(String var0) {
      return removeWSandReplace(var0, ";", ",");
   }

   static String removeWhiteSpace(String var0, String var1) {
      return removeWSandReplace(var0, var1, var1);
   }

   static String removeWSandReplace(String var0, String var1, String var2) {
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;
      int var5 = 0;

      int var6;
      for(int var7 = 0; (var6 = var0.indexOf(var1, var7)) >= 0; var7 = var6 + 1) {
         var5 += countQuotes(var0, var7, var6);
         if (var6 > 0 && var0.charAt(var6 - 1) != '\\' && var5 % 2 == 0) {
            var3.append(trim(var0.substring(var4, var6)) + var2);
            var4 = var6 + 1;
            var5 = 0;
         }
      }

      var3.append(trim(var0.substring(var4)));
      return var3.toString();
   }

   private static int countQuotes(String var0, int var1, int var2) {
      int var3 = 0;

      for(int var4 = var1; var4 < var2; ++var4) {
         if (var0.charAt(var4) == '"') {
            ++var3;
         }
      }

      return var3;
   }

   static String trim(String var0) {
      String var1 = var0.trim();
      int var2 = var0.indexOf(var1) + var1.length();
      if (var0.length() > var2 && var1.endsWith("\\") && !var1.endsWith("\\\\") && var0.charAt(var2) == ' ') {
         var1 = var1 + " ";
      }

      return var1;
   }
}
