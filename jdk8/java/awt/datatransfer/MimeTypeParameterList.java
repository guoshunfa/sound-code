package java.awt.datatransfer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class MimeTypeParameterList implements Cloneable {
   private Hashtable<String, String> parameters = new Hashtable();
   private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";

   public MimeTypeParameterList() {
   }

   public MimeTypeParameterList(String var1) throws MimeTypeParseException {
      this.parse(var1);
   }

   public int hashCode() {
      int var1 = 47721858;
      String var2 = null;

      for(Enumeration var3 = this.getNames(); var3.hasMoreElements(); var1 += this.get(var2).hashCode()) {
         var2 = (String)var3.nextElement();
         var1 += var2.hashCode();
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MimeTypeParameterList)) {
         return false;
      } else {
         MimeTypeParameterList var2 = (MimeTypeParameterList)var1;
         if (this.size() != var2.size()) {
            return false;
         } else {
            String var3 = null;
            String var4 = null;
            String var5 = null;
            Set var6 = this.parameters.entrySet();
            Iterator var7 = var6.iterator();
            Map.Entry var8 = null;

            label31:
            do {
               do {
                  if (!var7.hasNext()) {
                     return true;
                  }

                  var8 = (Map.Entry)var7.next();
                  var3 = (String)var8.getKey();
                  var4 = (String)var8.getValue();
                  var5 = (String)var2.parameters.get(var3);
                  if (var4 != null && var5 != null) {
                     continue label31;
                  }
               } while(var4 == var5);

               return false;
            } while(var4.equals(var5));

            return false;
         }
      }
   }

   protected void parse(String var1) throws MimeTypeParseException {
      int var2 = var1.length();
      if (var2 > 0) {
         int var3 = skipWhiteSpace(var1, 0);
         boolean var4 = false;
         if (var3 < var2) {
            char var5 = var1.charAt(var3);

            while(true) {
               if (var3 >= var2 || var5 != ';') {
                  if (var3 < var2) {
                     throw new MimeTypeParseException("More characters encountered in input than expected.");
                  }
                  break;
               }

               ++var3;
               var3 = skipWhiteSpace(var1, var3);
               if (var3 >= var2) {
                  throw new MimeTypeParseException("Couldn't find parameter name");
               }

               int var9 = var3;

               for(var5 = var1.charAt(var3); var3 < var2 && isTokenChar(var5); var5 = var1.charAt(var3)) {
                  ++var3;
               }

               String var6 = var1.substring(var9, var3).toLowerCase();
               var3 = skipWhiteSpace(var1, var3);
               if (var3 >= var2 || var1.charAt(var3) != '=') {
                  throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
               }

               ++var3;
               var3 = skipWhiteSpace(var1, var3);
               if (var3 >= var2) {
                  throw new MimeTypeParseException("Couldn't find a value for parameter named " + var6);
               }

               var5 = var1.charAt(var3);
               String var7;
               boolean var8;
               if (var5 == '"') {
                  ++var3;
                  var9 = var3;
                  if (var3 >= var2) {
                     throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                  }

                  var8 = false;

                  while(var3 < var2 && !var8) {
                     var5 = var1.charAt(var3);
                     if (var5 == '\\') {
                        var3 += 2;
                     } else if (var5 == '"') {
                        var8 = true;
                     } else {
                        ++var3;
                     }
                  }

                  if (var5 != '"') {
                     throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                  }

                  var7 = unquote(var1.substring(var9, var3));
                  ++var3;
               } else {
                  if (!isTokenChar(var5)) {
                     throw new MimeTypeParseException("Unexpected character encountered at index " + var3);
                  }

                  var9 = var3;
                  var8 = false;

                  while(var3 < var2 && !var8) {
                     var5 = var1.charAt(var3);
                     if (isTokenChar(var5)) {
                        ++var3;
                     } else {
                        var8 = true;
                     }
                  }

                  var7 = var1.substring(var9, var3);
               }

               this.parameters.put(var6, var7);
               var3 = skipWhiteSpace(var1, var3);
               if (var3 < var2) {
                  var5 = var1.charAt(var3);
               }
            }
         }
      }

   }

   public int size() {
      return this.parameters.size();
   }

   public boolean isEmpty() {
      return this.parameters.isEmpty();
   }

   public String get(String var1) {
      return (String)this.parameters.get(var1.trim().toLowerCase());
   }

   public void set(String var1, String var2) {
      this.parameters.put(var1.trim().toLowerCase(), var2);
   }

   public void remove(String var1) {
      this.parameters.remove(var1.trim().toLowerCase());
   }

   public Enumeration<String> getNames() {
      return this.parameters.keys();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.parameters.size() * 16);
      Enumeration var2 = this.parameters.keys();

      while(var2.hasMoreElements()) {
         var1.append("; ");
         String var3 = (String)var2.nextElement();
         var1.append(var3);
         var1.append('=');
         var1.append(quote((String)this.parameters.get(var3)));
      }

      return var1.toString();
   }

   public Object clone() {
      MimeTypeParameterList var1 = null;

      try {
         var1 = (MimeTypeParameterList)super.clone();
      } catch (CloneNotSupportedException var3) {
      }

      var1.parameters = (Hashtable)this.parameters.clone();
      return var1;
   }

   private static boolean isTokenChar(char var0) {
      return var0 > ' ' && var0 < 127 && "()<>@,;:\\\"/[]?=".indexOf(var0) < 0;
   }

   private static int skipWhiteSpace(String var0, int var1) {
      int var2 = var0.length();
      if (var1 < var2) {
         for(char var3 = var0.charAt(var1); var1 < var2 && Character.isWhitespace(var3); var3 = var0.charAt(var1)) {
            ++var1;
         }
      }

      return var1;
   }

   private static String quote(String var0) {
      boolean var1 = false;
      int var2 = var0.length();

      for(int var3 = 0; var3 < var2 && !var1; ++var3) {
         var1 = !isTokenChar(var0.charAt(var3));
      }

      if (!var1) {
         return var0;
      } else {
         StringBuilder var6 = new StringBuilder((int)((double)var2 * 1.5D));
         var6.append('"');

         for(int var4 = 0; var4 < var2; ++var4) {
            char var5 = var0.charAt(var4);
            if (var5 == '\\' || var5 == '"') {
               var6.append('\\');
            }

            var6.append(var5);
         }

         var6.append('"');
         return var6.toString();
      }
   }

   private static String unquote(String var0) {
      int var1 = var0.length();
      StringBuilder var2 = new StringBuilder(var1);
      boolean var3 = false;

      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var0.charAt(var4);
         if (!var3 && var5 != '\\') {
            var2.append(var5);
         } else if (var3) {
            var2.append(var5);
            var3 = false;
         } else {
            var3 = true;
         }
      }

      return var2.toString();
   }
}
