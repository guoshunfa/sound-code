package javax.naming.ldap;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InvalidNameException;

final class Rfc2253Parser {
   private final String name;
   private final char[] chars;
   private final int len;
   private int cur = 0;

   Rfc2253Parser(String var1) {
      this.name = var1;
      this.len = var1.length();
      this.chars = var1.toCharArray();
   }

   List<Rdn> parseDn() throws InvalidNameException {
      this.cur = 0;
      ArrayList var1 = new ArrayList(this.len / 3 + 10);
      if (this.len == 0) {
         return var1;
      } else {
         var1.add(this.doParse(new Rdn()));

         while(this.cur < this.len) {
            if (this.chars[this.cur] != ',' && this.chars[this.cur] != ';') {
               throw new InvalidNameException("Invalid name: " + this.name);
            }

            ++this.cur;
            var1.add(0, this.doParse(new Rdn()));
         }

         return var1;
      }
   }

   Rdn parseRdn() throws InvalidNameException {
      return this.parseRdn(new Rdn());
   }

   Rdn parseRdn(Rdn var1) throws InvalidNameException {
      var1 = this.doParse(var1);
      if (this.cur < this.len) {
         throw new InvalidNameException("Invalid RDN: " + this.name);
      } else {
         return var1;
      }
   }

   private Rdn doParse(Rdn var1) throws InvalidNameException {
      while(true) {
         if (this.cur < this.len) {
            this.consumeWhitespace();
            String var2 = this.parseAttrType();
            this.consumeWhitespace();
            if (this.cur >= this.len || this.chars[this.cur] != '=') {
               throw new InvalidNameException("Invalid name: " + this.name);
            }

            ++this.cur;
            this.consumeWhitespace();
            String var3 = this.parseAttrValue();
            this.consumeWhitespace();
            var1.put(var2, Rdn.unescapeValue(var3));
            if (this.cur < this.len && this.chars[this.cur] == '+') {
               ++this.cur;
               continue;
            }
         }

         var1.sort();
         return var1;
      }
   }

   private String parseAttrType() throws InvalidNameException {
      int var1;
      for(var1 = this.cur; this.cur < this.len; ++this.cur) {
         char var2 = this.chars[this.cur];
         if (!Character.isLetterOrDigit(var2) && var2 != '.' && var2 != '-' && var2 != ' ') {
            break;
         }
      }

      while(this.cur > var1 && this.chars[this.cur - 1] == ' ') {
         --this.cur;
      }

      if (var1 == this.cur) {
         throw new InvalidNameException("Invalid name: " + this.name);
      } else {
         return new String(this.chars, var1, this.cur - var1);
      }
   }

   private String parseAttrValue() throws InvalidNameException {
      if (this.cur < this.len && this.chars[this.cur] == '#') {
         return this.parseBinaryAttrValue();
      } else {
         return this.cur < this.len && this.chars[this.cur] == '"' ? this.parseQuotedAttrValue() : this.parseStringAttrValue();
      }
   }

   private String parseBinaryAttrValue() throws InvalidNameException {
      int var1;
      for(var1 = this.cur++; this.cur < this.len && Character.isLetterOrDigit(this.chars[this.cur]); ++this.cur) {
      }

      return new String(this.chars, var1, this.cur - var1);
   }

   private String parseQuotedAttrValue() throws InvalidNameException {
      int var1;
      for(var1 = this.cur++; this.cur < this.len && this.chars[this.cur] != '"'; ++this.cur) {
         if (this.chars[this.cur] == '\\') {
            ++this.cur;
         }
      }

      if (this.cur >= this.len) {
         throw new InvalidNameException("Invalid name: " + this.name);
      } else {
         ++this.cur;
         return new String(this.chars, var1, this.cur - var1);
      }
   }

   private String parseStringAttrValue() throws InvalidNameException {
      int var1 = this.cur;

      int var2;
      for(var2 = -1; this.cur < this.len && !this.atTerminator(); ++this.cur) {
         if (this.chars[this.cur] == '\\') {
            ++this.cur;
            var2 = this.cur;
         }
      }

      if (this.cur > this.len) {
         throw new InvalidNameException("Invalid name: " + this.name);
      } else {
         int var3;
         for(var3 = this.cur; var3 > var1 && isWhitespace(this.chars[var3 - 1]) && var2 != var3 - 1; --var3) {
         }

         return new String(this.chars, var1, var3 - var1);
      }
   }

   private void consumeWhitespace() {
      while(this.cur < this.len && isWhitespace(this.chars[this.cur])) {
         ++this.cur;
      }

   }

   private boolean atTerminator() {
      return this.cur < this.len && (this.chars[this.cur] == ',' || this.chars[this.cur] == ';' || this.chars[this.cur] == '+');
   }

   private static boolean isWhitespace(char var0) {
      return var0 == ' ' || var0 == '\r';
   }
}
