package com.sun.corba.se.impl.naming.cosnaming;

import java.io.StringWriter;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

public class InterOperableNamingImpl {
   public String convertToString(NameComponent[] var1) {
      String var2 = this.convertNameComponentToString(var1[0]);

      for(int var4 = 1; var4 < var1.length; ++var4) {
         String var3 = this.convertNameComponentToString(var1[var4]);
         if (var3 != null) {
            var2 = var2 + "/" + this.convertNameComponentToString(var1[var4]);
         }
      }

      return var2;
   }

   private String convertNameComponentToString(NameComponent var1) {
      if (var1.id != null && var1.id.length() != 0 || var1.kind != null && var1.kind.length() != 0) {
         String var2;
         if (var1.id != null && var1.id.length() != 0) {
            if (var1.kind != null && var1.kind.length() != 0) {
               var2 = this.addEscape(var1.id);
               String var3 = this.addEscape(var1.kind);
               return var2 + "." + var3;
            } else {
               var2 = this.addEscape(var1.id);
               return var2;
            }
         } else {
            var2 = this.addEscape(var1.kind);
            return "." + var2;
         }
      } else {
         return ".";
      }
   }

   private String addEscape(String var1) {
      if (var1 == null || var1.indexOf(46) == -1 && var1.indexOf(47) == -1) {
         return var1;
      } else {
         StringBuffer var2 = new StringBuffer();

         for(int var4 = 0; var4 < var1.length(); ++var4) {
            char var3 = var1.charAt(var4);
            if (var3 != '.' && var3 != '/') {
               var2.append(var3);
            } else {
               var2.append('\\');
               var2.append(var3);
            }
         }

         return new String(var2);
      }
   }

   public NameComponent[] convertToNameComponent(String var1) throws InvalidName {
      String[] var2 = this.breakStringToNameComponents(var1);
      if (var2 != null && var2.length != 0) {
         NameComponent[] var3 = new NameComponent[var2.length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = this.createNameComponentFromString(var2[var4]);
         }

         return var3;
      } else {
         return null;
      }
   }

   private String[] breakStringToNameComponents(String var1) {
      int[] var2 = new int[100];
      int var3 = 0;
      int var4 = 0;

      while(true) {
         while(var4 <= var1.length()) {
            var2[var3] = var1.indexOf(47, var4);
            if (var2[var3] == -1) {
               var4 = var1.length() + 1;
            } else if (var2[var3] > 0 && var1.charAt(var2[var3] - 1) == '\\') {
               var4 = var2[var3] + 1;
               var2[var3] = -1;
            } else {
               var4 = var2[var3] + 1;
               ++var3;
            }
         }

         if (var3 == 0) {
            String[] var5 = new String[]{var1};
            return var5;
         }

         if (var3 != 0) {
            ++var3;
         }

         return this.StringComponentsFromIndices(var2, var3, var1);
      }
   }

   private String[] StringComponentsFromIndices(int[] var1, int var2, String var3) {
      String[] var4 = new String[var2];
      int var5 = 0;
      int var6 = var1[0];

      for(int var7 = 0; var7 < var2; ++var7) {
         var4[var7] = var3.substring(var5, var6);
         if (var1[var7] < var3.length() - 1 && var1[var7] != -1) {
            var5 = var1[var7] + 1;
         } else {
            var5 = 0;
            var7 = var2;
         }

         if (var7 + 1 < var1.length && var1[var7 + 1] < var3.length() - 1 && var1[var7 + 1] != -1) {
            var6 = var1[var7 + 1];
         } else {
            var7 = var2;
         }

         if (var5 != 0 && var7 == var2) {
            var4[var2 - 1] = var3.substring(var5);
         }
      }

      return var4;
   }

   private NameComponent createNameComponentFromString(String var1) throws InvalidName {
      String var2 = null;
      String var3 = null;
      if (var1 != null && var1.length() != 0 && !var1.endsWith(".")) {
         int var4 = var1.indexOf(46, 0);
         if (var4 == -1) {
            var2 = var1;
         } else if (var4 == 0) {
            if (var1.length() != 1) {
               var3 = var1.substring(1);
            }
         } else if (var1.charAt(var4 - 1) != '\\') {
            var2 = var1.substring(0, var4);
            var3 = var1.substring(var4 + 1);
         } else {
            boolean var5 = false;

            while(var4 < var1.length() && !var5) {
               var4 = var1.indexOf(46, var4 + 1);
               if (var4 > 0) {
                  if (var1.charAt(var4 - 1) != '\\') {
                     var5 = true;
                  }
               } else {
                  var4 = var1.length();
               }
            }

            if (var5) {
               var2 = var1.substring(0, var4);
               var3 = var1.substring(var4 + 1);
            } else {
               var2 = var1;
            }
         }

         var2 = this.cleanEscapeCharacter(var2);
         var3 = this.cleanEscapeCharacter(var3);
         if (var2 == null) {
            var2 = "";
         }

         if (var3 == null) {
            var3 = "";
         }

         return new NameComponent(var2, var3);
      } else {
         throw new InvalidName();
      }
   }

   private String cleanEscapeCharacter(String var1) {
      if (var1 != null && var1.length() != 0) {
         int var2 = var1.indexOf(92);
         if (var2 == 0) {
            return var1;
         } else {
            StringBuffer var3 = new StringBuffer(var1);
            StringBuffer var4 = new StringBuffer();

            for(int var6 = 0; var6 < var1.length(); ++var6) {
               char var5 = var3.charAt(var6);
               if (var5 != '\\') {
                  var4.append(var5);
               } else if (var6 + 1 < var1.length()) {
                  char var7 = var3.charAt(var6 + 1);
                  if (Character.isLetterOrDigit(var7)) {
                     var4.append(var5);
                  }
               }
            }

            return new String(var4);
         }
      } else {
         return var1;
      }
   }

   public String createURLBasedAddress(String var1, String var2) throws InvalidAddress {
      String var3 = null;
      if (var1 != null && var1.length() != 0) {
         var3 = "corbaname:" + var1 + "#" + this.encode(var2);
         return var3;
      } else {
         throw new InvalidAddress();
      }
   }

   private String encode(String var1) {
      StringWriter var2 = new StringWriter();
      boolean var3 = false;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         char var5 = var1.charAt(var4);
         if (Character.isLetterOrDigit(var5)) {
            var2.write(var5);
         } else if (var5 != ';' && var5 != '/' && var5 != '?' && var5 != ':' && var5 != '@' && var5 != '&' && var5 != '=' && var5 != '+' && var5 != '$' && var5 != ';' && var5 != '-' && var5 != '_' && var5 != '.' && var5 != '!' && var5 != '~' && var5 != '*' && var5 != ' ' && var5 != '(' && var5 != ')') {
            var2.write(37);
            String var6 = Integer.toHexString(var5);
            var2.write(var6);
         } else {
            var2.write(var5);
         }
      }

      return var2.toString();
   }
}
