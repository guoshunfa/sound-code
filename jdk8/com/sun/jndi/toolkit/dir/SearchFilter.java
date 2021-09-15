package com.sun.jndi.toolkit.dir;

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidSearchFilterException;

public class SearchFilter implements AttrFilter {
   String filter;
   int pos;
   private SearchFilter.StringFilter rootFilter;
   protected static final boolean debug = false;
   protected static final char BEGIN_FILTER_TOKEN = '(';
   protected static final char END_FILTER_TOKEN = ')';
   protected static final char AND_TOKEN = '&';
   protected static final char OR_TOKEN = '|';
   protected static final char NOT_TOKEN = '!';
   protected static final char EQUAL_TOKEN = '=';
   protected static final char APPROX_TOKEN = '~';
   protected static final char LESS_TOKEN = '<';
   protected static final char GREATER_TOKEN = '>';
   protected static final char EXTEND_TOKEN = ':';
   protected static final char WILDCARD_TOKEN = '*';
   static final int EQUAL_MATCH = 1;
   static final int APPROX_MATCH = 2;
   static final int GREATER_MATCH = 3;
   static final int LESS_MATCH = 4;

   public SearchFilter(String var1) throws InvalidSearchFilterException {
      this.filter = var1;
      this.pos = 0;
      this.normalizeFilter();
      this.rootFilter = this.createNextFilter();
   }

   public boolean check(Attributes var1) throws NamingException {
      return var1 == null ? false : this.rootFilter.check(var1);
   }

   protected void normalizeFilter() {
      this.skipWhiteSpace();
      if (this.getCurrentChar() != '(') {
         this.filter = '(' + this.filter + ')';
      }

   }

   private void skipWhiteSpace() {
      while(Character.isWhitespace(this.getCurrentChar())) {
         this.consumeChar();
      }

   }

   protected SearchFilter.StringFilter createNextFilter() throws InvalidSearchFilterException {
      this.skipWhiteSpace();

      try {
         if (this.getCurrentChar() != '(') {
            throw new InvalidSearchFilterException("expected \"(\" at position " + this.pos);
         } else {
            this.consumeChar();
            this.skipWhiteSpace();
            Object var1;
            switch(this.getCurrentChar()) {
            case '!':
               var1 = new SearchFilter.NotFilter();
               ((SearchFilter.StringFilter)var1).parse();
               break;
            case '&':
               var1 = new SearchFilter.CompoundFilter(true);
               ((SearchFilter.StringFilter)var1).parse();
               break;
            case '|':
               var1 = new SearchFilter.CompoundFilter(false);
               ((SearchFilter.StringFilter)var1).parse();
               break;
            default:
               var1 = new SearchFilter.AtomicFilter();
               ((SearchFilter.StringFilter)var1).parse();
            }

            this.skipWhiteSpace();
            if (this.getCurrentChar() != ')') {
               throw new InvalidSearchFilterException("expected \")\" at position " + this.pos);
            } else {
               this.consumeChar();
               return (SearchFilter.StringFilter)var1;
            }
         }
      } catch (InvalidSearchFilterException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new InvalidSearchFilterException("Unable to parse character " + this.pos + " in \"" + this.filter + "\"");
      }
   }

   protected char getCurrentChar() {
      return this.filter.charAt(this.pos);
   }

   protected char relCharAt(int var1) {
      return this.filter.charAt(this.pos + var1);
   }

   protected void consumeChar() {
      ++this.pos;
   }

   protected void consumeChars(int var1) {
      this.pos += var1;
   }

   protected int relIndexOf(int var1) {
      return this.filter.indexOf(var1, this.pos) - this.pos;
   }

   protected String relSubstring(int var1, int var2) {
      return this.filter.substring(var1 + this.pos, var2 + this.pos);
   }

   public static String format(Attributes var0) throws NamingException {
      if (var0 != null && var0.size() != 0) {
         String var1 = "(& ";
         NamingEnumeration var3 = var0.getAll();

         while(true) {
            while(var3.hasMore()) {
               Attribute var2 = (Attribute)var3.next();
               if (var2.size() != 0 && (var2.size() != 1 || var2.get() != null)) {
                  NamingEnumeration var4 = var2.getAll();

                  while(var4.hasMore()) {
                     String var5 = getEncodedStringRep(var4.next());
                     if (var5 != null) {
                        var1 = var1 + "(" + var2.getID() + "=" + var5 + ")";
                     }
                  }
               } else {
                  var1 = var1 + "(" + var2.getID() + "=*)";
               }
            }

            var1 = var1 + ")";
            return var1;
         }
      } else {
         return "objectClass=*";
      }
   }

   private static void hexDigit(StringBuffer var0, byte var1) {
      char var2 = (char)(var1 >> 4 & 15);
      if (var2 > '\t') {
         var2 = (char)(var2 - 10 + 65);
      } else {
         var2 = (char)(var2 + 48);
      }

      var0.append(var2);
      var2 = (char)(var1 & 15);
      if (var2 > '\t') {
         var2 = (char)(var2 - 10 + 65);
      } else {
         var2 = (char)(var2 + 48);
      }

      var0.append(var2);
   }

   private static String getEncodedStringRep(Object var0) throws NamingException {
      if (var0 == null) {
         return null;
      } else {
         StringBuffer var3;
         if (!(var0 instanceof byte[])) {
            String var1;
            if (!(var0 instanceof String)) {
               var1 = var0.toString();
            } else {
               var1 = (String)var0;
            }

            int var6 = var1.length();
            var3 = new StringBuffer(var6);

            for(int var5 = 0; var5 < var6; ++var5) {
               char var7;
               switch(var7 = var1.charAt(var5)) {
               case '\u0000':
                  var3.append("\\00");
                  break;
               case '(':
                  var3.append("\\28");
                  break;
               case ')':
                  var3.append("\\29");
                  break;
               case '*':
                  var3.append("\\2a");
                  break;
               case '\\':
                  var3.append("\\5c");
                  break;
               default:
                  var3.append(var7);
               }
            }

            return var3.toString();
         } else {
            byte[] var2 = (byte[])((byte[])var0);
            var3 = new StringBuffer(var2.length * 3);

            for(int var4 = 0; var4 < var2.length; ++var4) {
               var3.append('\\');
               hexDigit(var3, var2[var4]);
            }

            return var3.toString();
         }
      }
   }

   public static int findUnescaped(char var0, String var1, int var2) {
      int var4;
      for(int var3 = var1.length(); var2 < var3; var2 = var4 + 1) {
         var4 = var1.indexOf(var0, var2);
         if (var4 == var2 || var4 == -1 || var1.charAt(var4 - 1) != '\\') {
            return var4;
         }
      }

      return -1;
   }

   public static String format(String var0, Object[] var1) throws NamingException {
      boolean var3 = false;
      int var4 = 0;

      StringBuffer var5;
      int var7;
      int var10;
      for(var5 = new StringBuffer(var0.length()); (var10 = findUnescaped('{', var0, var4)) >= 0; var4 = var7 + 1) {
         int var6 = var10 + 1;
         var7 = var0.indexOf(125, var6);
         if (var7 < 0) {
            throw new InvalidSearchFilterException("unbalanced {: " + var0);
         }

         int var2;
         try {
            var2 = Integer.parseInt(var0.substring(var6, var7));
         } catch (NumberFormatException var9) {
            throw new InvalidSearchFilterException("integer expected inside {}: " + var0);
         }

         if (var2 >= var1.length) {
            throw new InvalidSearchFilterException("number exceeds argument list: " + var2);
         }

         var5.append(var0.substring(var4, var10)).append(getEncodedStringRep(var1[var2]));
      }

      if (var4 < var0.length()) {
         var5.append(var0.substring(var4));
      }

      return var5.toString();
   }

   public static Attributes selectAttributes(Attributes var0, String[] var1) throws NamingException {
      if (var1 == null) {
         return var0;
      } else {
         BasicAttributes var2 = new BasicAttributes();

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Attribute var4 = var0.get(var1[var3]);
            if (var4 != null) {
               var2.put(var4);
            }
         }

         return var2;
      }
   }

   final class AtomicFilter implements SearchFilter.StringFilter {
      private String attrID;
      private String value;
      private int matchType;

      public void parse() throws InvalidSearchFilterException {
         SearchFilter.this.skipWhiteSpace();

         try {
            int var1 = SearchFilter.this.relIndexOf(41);
            int var5 = SearchFilter.this.relIndexOf(61);
            char var3 = SearchFilter.this.relCharAt(var5 - 1);
            switch(var3) {
            case ':':
               throw new OperationNotSupportedException("Extensible match not supported");
            case '<':
               this.matchType = 4;
               this.attrID = SearchFilter.this.relSubstring(0, var5 - 1);
               this.value = SearchFilter.this.relSubstring(var5 + 1, var1);
               break;
            case '>':
               this.matchType = 3;
               this.attrID = SearchFilter.this.relSubstring(0, var5 - 1);
               this.value = SearchFilter.this.relSubstring(var5 + 1, var1);
               break;
            case '~':
               this.matchType = 2;
               this.attrID = SearchFilter.this.relSubstring(0, var5 - 1);
               this.value = SearchFilter.this.relSubstring(var5 + 1, var1);
               break;
            default:
               this.matchType = 1;
               this.attrID = SearchFilter.this.relSubstring(0, var5);
               this.value = SearchFilter.this.relSubstring(var5 + 1, var1);
            }

            this.attrID = this.attrID.trim();
            this.value = this.value.trim();
            SearchFilter.this.consumeChars(var1);
         } catch (Exception var4) {
            InvalidSearchFilterException var2 = new InvalidSearchFilterException("Unable to parse character " + SearchFilter.this.pos + " in \"" + SearchFilter.this.filter + "\"");
            var2.setRootCause(var4);
            throw var2;
         }
      }

      public boolean check(Attributes var1) {
         NamingEnumeration var2;
         try {
            Attribute var3 = var1.get(this.attrID);
            if (var3 == null) {
               return false;
            }

            var2 = var3.getAll();
         } catch (NamingException var4) {
            return false;
         }

         while(var2.hasMoreElements()) {
            String var5 = var2.nextElement().toString();
            switch(this.matchType) {
            case 1:
            case 2:
               if (this.substringMatch(this.value, var5)) {
                  return true;
               }
               break;
            case 3:
               if (var5.compareTo(this.value) >= 0) {
                  return true;
               }
               break;
            case 4:
               if (var5.compareTo(this.value) <= 0) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean substringMatch(String var1, String var2) {
         if (var1.equals((new Character('*')).toString())) {
            return true;
         } else if (var1.indexOf(42) == -1) {
            return var1.equalsIgnoreCase(var2);
         } else {
            int var3 = 0;
            StringTokenizer var4 = new StringTokenizer(var1, "*", false);
            if (var1.charAt(0) != '*' && !var2.toLowerCase(Locale.ENGLISH).startsWith(var4.nextToken().toLowerCase(Locale.ENGLISH))) {
               return false;
            } else {
               while(var4.hasMoreTokens()) {
                  String var5 = var4.nextToken();
                  var3 = var2.toLowerCase(Locale.ENGLISH).indexOf(var5.toLowerCase(Locale.ENGLISH), var3);
                  if (var3 == -1) {
                     return false;
                  }

                  var3 += var5.length();
               }

               if (var1.charAt(var1.length() - 1) != '*' && var3 != var2.length()) {
                  return false;
               } else {
                  return true;
               }
            }
         }
      }
   }

   final class NotFilter implements SearchFilter.StringFilter {
      private SearchFilter.StringFilter filter;

      public void parse() throws InvalidSearchFilterException {
         SearchFilter.this.consumeChar();
         this.filter = SearchFilter.this.createNextFilter();
      }

      public boolean check(Attributes var1) throws NamingException {
         return !this.filter.check(var1);
      }
   }

   final class CompoundFilter implements SearchFilter.StringFilter {
      private Vector<SearchFilter.StringFilter> subFilters = new Vector();
      private boolean polarity;

      CompoundFilter(boolean var2) {
         this.polarity = var2;
      }

      public void parse() throws InvalidSearchFilterException {
         SearchFilter.this.consumeChar();

         while(SearchFilter.this.getCurrentChar() != ')') {
            SearchFilter.StringFilter var1 = SearchFilter.this.createNextFilter();
            this.subFilters.addElement(var1);
            SearchFilter.this.skipWhiteSpace();
         }

      }

      public boolean check(Attributes var1) throws NamingException {
         for(int var2 = 0; var2 < this.subFilters.size(); ++var2) {
            SearchFilter.StringFilter var3 = (SearchFilter.StringFilter)this.subFilters.elementAt(var2);
            if (var3.check(var1) != this.polarity) {
               return !this.polarity;
            }
         }

         return this.polarity;
      }
   }

   interface StringFilter extends AttrFilter {
      void parse() throws InvalidSearchFilterException;
   }
}
