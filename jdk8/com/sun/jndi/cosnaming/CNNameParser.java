package com.sun.jndi.cosnaming;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import org.omg.CosNaming.NameComponent;

public final class CNNameParser implements NameParser {
   private static final Properties mySyntax = new Properties();
   private static final char kindSeparator = '.';
   private static final char compSeparator = '/';
   private static final char escapeChar = '\\';

   public Name parse(String var1) throws NamingException {
      Vector var2 = insStringToStringifiedComps(var1);
      return new CNNameParser.CNCompoundName(var2.elements());
   }

   static NameComponent[] nameToCosName(Name var0) throws InvalidNameException {
      int var1 = var0.size();
      if (var1 == 0) {
         return new NameComponent[0];
      } else {
         NameComponent[] var2 = new NameComponent[var1];

         for(int var3 = 0; var3 < var1; ++var3) {
            var2[var3] = parseComponent(var0.get(var3));
         }

         return var2;
      }
   }

   static String cosNameToInsString(NameComponent[] var0) {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var2 > 0) {
            var1.append('/');
         }

         var1.append(stringifyComponent(var0[var2]));
      }

      return var1.toString();
   }

   static Name cosNameToName(NameComponent[] var0) {
      CompositeName var1 = new CompositeName();

      for(int var2 = 0; var0 != null && var2 < var0.length; ++var2) {
         try {
            var1.add(stringifyComponent(var0[var2]));
         } catch (InvalidNameException var4) {
         }
      }

      return var1;
   }

   private static Vector<String> insStringToStringifiedComps(String var0) throws InvalidNameException {
      int var1 = var0.length();
      Vector var2 = new Vector(10);
      char[] var3 = new char[var1];
      char[] var4 = new char[var1];
      int var8 = 0;

      while(var8 < var1) {
         int var6 = 0;
         int var5 = 0;
         boolean var7 = true;

         while(true) {
            while(var8 < var1 && var0.charAt(var8) != '/') {
               if (var0.charAt(var8) == '\\') {
                  if (var8 + 1 >= var1) {
                     throw new InvalidNameException(var0 + ": unescaped \\ at end of component");
                  }

                  if (!isMeta(var0.charAt(var8 + 1))) {
                     throw new InvalidNameException(var0 + ": invalid character being escaped");
                  }

                  ++var8;
                  if (var7) {
                     var3[var5++] = var0.charAt(var8++);
                  } else {
                     var4[var6++] = var0.charAt(var8++);
                  }
               } else if (var7 && var0.charAt(var8) == '.') {
                  ++var8;
                  var7 = false;
               } else if (var7) {
                  var3[var5++] = var0.charAt(var8++);
               } else {
                  var4[var6++] = var0.charAt(var8++);
               }
            }

            var2.addElement(stringifyComponent(new NameComponent(new String(var3, 0, var5), new String(var4, 0, var6))));
            if (var8 < var1) {
               ++var8;
            }
            break;
         }
      }

      return var2;
   }

   private static NameComponent parseComponent(String var0) throws InvalidNameException {
      NameComponent var1 = new NameComponent();
      int var2 = -1;
      int var3 = var0.length();
      int var4 = 0;
      char[] var5 = new char[var3];
      boolean var6 = false;

      int var7;
      for(var7 = 0; var7 < var3 && var2 < 0; ++var7) {
         if (var6) {
            var5[var4++] = var0.charAt(var7);
            var6 = false;
         } else if (var0.charAt(var7) == '\\') {
            if (var7 + 1 >= var3) {
               throw new InvalidNameException(var0 + ": unescaped \\ at end of component");
            }

            if (!isMeta(var0.charAt(var7 + 1))) {
               throw new InvalidNameException(var0 + ": invalid character being escaped");
            }

            var6 = true;
         } else if (var0.charAt(var7) == '.') {
            var2 = var7;
         } else {
            var5[var4++] = var0.charAt(var7);
         }
      }

      var1.id = new String(var5, 0, var4);
      if (var2 < 0) {
         var1.kind = "";
      } else {
         var4 = 0;
         var6 = false;

         for(var7 = var2 + 1; var7 < var3; ++var7) {
            if (var6) {
               var5[var4++] = var0.charAt(var7);
               var6 = false;
            } else if (var0.charAt(var7) == '\\') {
               if (var7 + 1 >= var3) {
                  throw new InvalidNameException(var0 + ": unescaped \\ at end of component");
               }

               if (!isMeta(var0.charAt(var7 + 1))) {
                  throw new InvalidNameException(var0 + ": invalid character being escaped");
               }

               var6 = true;
            } else {
               var5[var4++] = var0.charAt(var7);
            }
         }

         var1.kind = new String(var5, 0, var4);
      }

      return var1;
   }

   private static String stringifyComponent(NameComponent var0) {
      StringBuffer var1 = new StringBuffer(escape(var0.id));
      if (var0.kind != null && !var0.kind.equals("")) {
         var1.append('.' + escape(var0.kind));
      }

      return var1.length() == 0 ? "." : var1.toString();
   }

   private static String escape(String var0) {
      if (var0.indexOf(46) < 0 && var0.indexOf(47) < 0 && var0.indexOf(92) < 0) {
         return var0;
      } else {
         int var1 = var0.length();
         int var2 = 0;
         char[] var3 = new char[var1 + var1];

         for(int var4 = 0; var4 < var1; ++var4) {
            if (isMeta(var0.charAt(var4))) {
               var3[var2++] = '\\';
            }

            var3[var2++] = var0.charAt(var4);
         }

         return new String(var3, 0, var2);
      }
   }

   private static boolean isMeta(char var0) {
      switch(var0) {
      case '.':
      case '/':
      case '\\':
         return true;
      default:
         return false;
      }
   }

   static {
      mySyntax.put("jndi.syntax.direction", "left_to_right");
      mySyntax.put("jndi.syntax.separator", "/");
      mySyntax.put("jndi.syntax.escape", "\\");
   }

   static final class CNCompoundName extends CompoundName {
      private static final long serialVersionUID = -6599252802678482317L;

      CNCompoundName(Enumeration<String> var1) {
         super(var1, CNNameParser.mySyntax);
      }

      public Object clone() {
         return new CNNameParser.CNCompoundName(this.getAll());
      }

      public Name getPrefix(int var1) {
         Enumeration var2 = super.getPrefix(var1).getAll();
         return new CNNameParser.CNCompoundName(var2);
      }

      public Name getSuffix(int var1) {
         Enumeration var2 = super.getSuffix(var1).getAll();
         return new CNNameParser.CNCompoundName(var2);
      }

      public String toString() {
         try {
            return CNNameParser.cosNameToInsString(CNNameParser.nameToCosName(this));
         } catch (InvalidNameException var2) {
            return super.toString();
         }
      }
   }
}
