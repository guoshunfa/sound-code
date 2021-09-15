package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public final class LdapName implements Name {
   private transient String unparsed;
   private transient Vector<LdapName.Rdn> rdns;
   private transient boolean valuesCaseSensitive = false;
   static final long serialVersionUID = -1595520034788997356L;

   public LdapName(String var1) throws InvalidNameException {
      this.unparsed = var1;
      this.parse();
   }

   private LdapName(String var1, Vector<LdapName.Rdn> var2) {
      this.unparsed = var1;
      this.rdns = (Vector)var2.clone();
   }

   private LdapName(String var1, Vector<LdapName.Rdn> var2, int var3, int var4) {
      this.unparsed = var1;
      this.rdns = new Vector();

      for(int var5 = var3; var5 < var4; ++var5) {
         this.rdns.addElement(var2.elementAt(var5));
      }

   }

   public Object clone() {
      return new LdapName(this.unparsed, this.rdns);
   }

   public String toString() {
      if (this.unparsed != null) {
         return this.unparsed;
      } else {
         StringBuffer var1 = new StringBuffer();

         for(int var2 = this.rdns.size() - 1; var2 >= 0; --var2) {
            if (var2 < this.rdns.size() - 1) {
               var1.append(',');
            }

            LdapName.Rdn var3 = (LdapName.Rdn)this.rdns.elementAt(var2);
            var1.append((Object)var3);
         }

         this.unparsed = new String(var1);
         return this.unparsed;
      }
   }

   public boolean equals(Object var1) {
      return var1 instanceof LdapName && this.compareTo(var1) == 0;
   }

   public int compareTo(Object var1) {
      LdapName var2 = (LdapName)var1;
      if (var1 == this || this.unparsed != null && this.unparsed.equals(var2.unparsed)) {
         return 0;
      } else {
         int var3 = Math.min(this.rdns.size(), var2.rdns.size());

         for(int var4 = 0; var4 < var3; ++var4) {
            LdapName.Rdn var5 = (LdapName.Rdn)this.rdns.elementAt(var4);
            LdapName.Rdn var6 = (LdapName.Rdn)var2.rdns.elementAt(var4);
            int var7 = var5.compareTo(var6);
            if (var7 != 0) {
               return var7;
            }
         }

         return this.rdns.size() - var2.rdns.size();
      }
   }

   public int hashCode() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.rdns.size(); ++var2) {
         LdapName.Rdn var3 = (LdapName.Rdn)this.rdns.elementAt(var2);
         var1 += var3.hashCode();
      }

      return var1;
   }

   public int size() {
      return this.rdns.size();
   }

   public boolean isEmpty() {
      return this.rdns.isEmpty();
   }

   public Enumeration<String> getAll() {
      final Enumeration var1 = this.rdns.elements();
      return new Enumeration<String>() {
         public boolean hasMoreElements() {
            return var1.hasMoreElements();
         }

         public String nextElement() {
            return ((LdapName.Rdn)var1.nextElement()).toString();
         }
      };
   }

   public String get(int var1) {
      return ((LdapName.Rdn)this.rdns.elementAt(var1)).toString();
   }

   public Name getPrefix(int var1) {
      return new LdapName((String)null, this.rdns, 0, var1);
   }

   public Name getSuffix(int var1) {
      return new LdapName((String)null, this.rdns, var1, this.rdns.size());
   }

   public boolean startsWith(Name var1) {
      int var2 = this.rdns.size();
      int var3 = var1.size();
      return var2 >= var3 && this.matches(0, var3, var1);
   }

   public boolean endsWith(Name var1) {
      int var2 = this.rdns.size();
      int var3 = var1.size();
      return var2 >= var3 && this.matches(var2 - var3, var2, var1);
   }

   public void setValuesCaseSensitive(boolean var1) {
      this.toString();
      this.rdns = null;

      try {
         this.parse();
      } catch (InvalidNameException var3) {
         throw new IllegalStateException("Cannot parse name: " + this.unparsed);
      }

      this.valuesCaseSensitive = var1;
   }

   private boolean matches(int var1, int var2, Name var3) {
      for(int var4 = var1; var4 < var2; ++var4) {
         LdapName.Rdn var5;
         if (var3 instanceof LdapName) {
            LdapName var6 = (LdapName)var3;
            var5 = (LdapName.Rdn)var6.rdns.elementAt(var4 - var1);
         } else {
            String var9 = var3.get(var4 - var1);

            try {
               var5 = (new LdapName.DnParser(var9, this.valuesCaseSensitive)).getRdn();
            } catch (InvalidNameException var8) {
               return false;
            }
         }

         if (!var5.equals(this.rdns.elementAt(var4))) {
            return false;
         }
      }

      return true;
   }

   public Name addAll(Name var1) throws InvalidNameException {
      return this.addAll(this.size(), var1);
   }

   public Name addAll(int var1, Name var2) throws InvalidNameException {
      if (var2 instanceof LdapName) {
         LdapName var3 = (LdapName)var2;

         for(int var4 = 0; var4 < var3.rdns.size(); ++var4) {
            this.rdns.insertElementAt(var3.rdns.elementAt(var4), var1++);
         }
      } else {
         Enumeration var5 = var2.getAll();

         while(var5.hasMoreElements()) {
            LdapName.DnParser var6 = new LdapName.DnParser((String)var5.nextElement(), this.valuesCaseSensitive);
            this.rdns.insertElementAt(var6.getRdn(), var1++);
         }
      }

      this.unparsed = null;
      return this;
   }

   public Name add(String var1) throws InvalidNameException {
      return this.add(this.size(), var1);
   }

   public Name add(int var1, String var2) throws InvalidNameException {
      LdapName.Rdn var3 = (new LdapName.DnParser(var2, this.valuesCaseSensitive)).getRdn();
      this.rdns.insertElementAt(var3, var1);
      this.unparsed = null;
      return this;
   }

   public Object remove(int var1) throws InvalidNameException {
      String var2 = this.get(var1);
      this.rdns.removeElementAt(var1);
      this.unparsed = null;
      return var2;
   }

   private void parse() throws InvalidNameException {
      this.rdns = (new LdapName.DnParser(this.unparsed, this.valuesCaseSensitive)).getDn();
   }

   private static boolean isWhitespace(char var0) {
      return var0 == ' ' || var0 == '\r';
   }

   public static String escapeAttributeValue(Object var0) {
      return LdapName.TypeAndValue.escapeValue(var0);
   }

   public static Object unescapeAttributeValue(String var0) {
      return LdapName.TypeAndValue.unescapeValue(var0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.toString());
      var1.writeBoolean(this.valuesCaseSensitive);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.unparsed = (String)var1.readObject();
      this.valuesCaseSensitive = var1.readBoolean();

      try {
         this.parse();
      } catch (InvalidNameException var3) {
         throw new StreamCorruptedException("Invalid name: " + this.unparsed);
      }
   }

   static class TypeAndValue {
      private final String type;
      private final String value;
      private final boolean binary;
      private final boolean valueCaseSensitive;
      private String comparable = null;

      TypeAndValue(String var1, String var2, boolean var3) {
         this.type = var1;
         this.value = var2;
         this.binary = var2.startsWith("#");
         this.valueCaseSensitive = var3;
      }

      public String toString() {
         return this.type + "=" + this.value;
      }

      public int compareTo(Object var1) {
         LdapName.TypeAndValue var2 = (LdapName.TypeAndValue)var1;
         int var3 = this.type.compareToIgnoreCase(var2.type);
         if (var3 != 0) {
            return var3;
         } else {
            return this.value.equals(var2.value) ? 0 : this.getValueComparable().compareTo(var2.getValueComparable());
         }
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof LdapName.TypeAndValue)) {
            return false;
         } else {
            LdapName.TypeAndValue var2 = (LdapName.TypeAndValue)var1;
            return this.type.equalsIgnoreCase(var2.type) && (this.value.equals(var2.value) || this.getValueComparable().equals(var2.getValueComparable()));
         }
      }

      public int hashCode() {
         return this.type.toUpperCase(Locale.ENGLISH).hashCode() + this.getValueComparable().hashCode();
      }

      String getType() {
         return this.type;
      }

      Object getUnescapedValue() {
         return unescapeValue(this.value);
      }

      private String getValueComparable() {
         if (this.comparable != null) {
            return this.comparable;
         } else {
            if (this.binary) {
               this.comparable = this.value.toUpperCase(Locale.ENGLISH);
            } else {
               this.comparable = (String)unescapeValue(this.value);
               if (!this.valueCaseSensitive) {
                  this.comparable = this.comparable.toUpperCase(Locale.ENGLISH);
               }
            }

            return this.comparable;
         }
      }

      static String escapeValue(Object var0) {
         return var0 instanceof byte[] ? escapeBinaryValue((byte[])((byte[])var0)) : escapeStringValue((String)var0);
      }

      private static String escapeStringValue(String var0) {
         char[] var2 = var0.toCharArray();
         StringBuffer var3 = new StringBuffer(2 * var0.length());

         int var4;
         for(var4 = 0; var4 < var2.length && LdapName.isWhitespace(var2[var4]); ++var4) {
         }

         int var5;
         for(var5 = var2.length - 1; var5 >= 0 && LdapName.isWhitespace(var2[var5]); --var5) {
         }

         for(int var6 = 0; var6 < var2.length; ++var6) {
            char var7 = var2[var6];
            if (var6 < var4 || var6 > var5 || ",=+<>#;\"\\".indexOf(var7) >= 0) {
               var3.append('\\');
            }

            var3.append(var7);
         }

         return new String(var3);
      }

      private static String escapeBinaryValue(byte[] var0) {
         StringBuffer var1 = new StringBuffer(1 + 2 * var0.length);
         var1.append("#");

         for(int var2 = 0; var2 < var0.length; ++var2) {
            byte var3 = var0[var2];
            var1.append(Character.forDigit(15 & var3 >>> 4, 16));
            var1.append(Character.forDigit(15 & var3, 16));
         }

         return (new String(var1)).toUpperCase(Locale.ENGLISH);
      }

      static Object unescapeValue(String var0) {
         char[] var1 = var0.toCharArray();
         int var2 = 0;

         int var3;
         for(var3 = var1.length; var2 < var3 && LdapName.isWhitespace(var1[var2]); ++var2) {
         }

         while(var2 < var3 && LdapName.isWhitespace(var1[var3 - 1])) {
            --var3;
         }

         if (var3 != var1.length && var2 < var3 && var1[var3 - 1] == '\\') {
            ++var3;
         }

         if (var2 >= var3) {
            return "";
         } else if (var1[var2] == '#') {
            ++var2;
            return decodeHexPairs(var1, var2, var3);
         } else {
            if (var1[var2] == '"' && var1[var3 - 1] == '"') {
               ++var2;
               --var3;
            }

            StringBuffer var4 = new StringBuffer(var3 - var2);
            int var5 = -1;

            int var6;
            for(var6 = var2; var6 < var3; ++var6) {
               if (var1[var6] == '\\' && var6 + 1 < var3) {
                  if (!Character.isLetterOrDigit(var1[var6 + 1])) {
                     ++var6;
                     var4.append(var1[var6]);
                     var5 = var6;
                  } else {
                     byte[] var7 = getUtf8Octets(var1, var6, var3);
                     if (var7.length <= 0) {
                        throw new IllegalArgumentException("Not a valid attribute string value:" + var0 + ", improper usage of backslash");
                     }

                     try {
                        var4.append(new String(var7, "UTF8"));
                     } catch (UnsupportedEncodingException var9) {
                     }

                     var6 += var7.length * 3 - 1;
                  }
               } else {
                  var4.append(var1[var6]);
               }
            }

            var6 = var4.length();
            if (LdapName.isWhitespace(var4.charAt(var6 - 1)) && var5 != var3 - 1) {
               var4.setLength(var6 - 1);
            }

            return new String(var4);
         }
      }

      private static byte[] decodeHexPairs(char[] var0, int var1, int var2) {
         byte[] var3 = new byte[(var2 - var1) / 2];

         for(int var4 = 0; var1 + 1 < var2; ++var4) {
            int var5 = Character.digit((char)var0[var1], 16);
            int var6 = Character.digit((char)var0[var1 + 1], 16);
            if (var5 < 0 || var6 < 0) {
               break;
            }

            var3[var4] = (byte)((var5 << 4) + var6);
            var1 += 2;
         }

         if (var1 != var2) {
            throw new IllegalArgumentException("Illegal attribute value: #" + new String(var0));
         } else {
            return var3;
         }
      }

      private static byte[] getUtf8Octets(char[] var0, int var1, int var2) {
         byte[] var3 = new byte[(var2 - var1) / 3];

         int var4;
         int var5;
         int var6;
         for(var4 = 0; var1 + 2 < var2 && var0[var1++] == '\\'; var3[var4++] = (byte)((var5 << 4) + var6)) {
            var5 = Character.digit((char)var0[var1++], 16);
            var6 = Character.digit((char)var0[var1++], 16);
            if (var5 < 0 || var6 < 0) {
               break;
            }
         }

         if (var4 == var3.length) {
            return var3;
         } else {
            byte[] var7 = new byte[var4];
            System.arraycopy(var3, 0, var7, 0, var4);
            return var7;
         }
      }
   }

   static class Rdn {
      private final Vector<LdapName.TypeAndValue> tvs = new Vector();

      void add(LdapName.TypeAndValue var1) {
         int var2;
         for(var2 = 0; var2 < this.tvs.size(); ++var2) {
            int var3 = var1.compareTo(this.tvs.elementAt(var2));
            if (var3 == 0) {
               return;
            }

            if (var3 < 0) {
               break;
            }
         }

         this.tvs.insertElementAt(var1, var2);
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();

         for(int var2 = 0; var2 < this.tvs.size(); ++var2) {
            if (var2 > 0) {
               var1.append('+');
            }

            var1.append(this.tvs.elementAt(var2));
         }

         return new String(var1);
      }

      public boolean equals(Object var1) {
         return var1 instanceof LdapName.Rdn && this.compareTo(var1) == 0;
      }

      public int compareTo(Object var1) {
         LdapName.Rdn var2 = (LdapName.Rdn)var1;
         int var3 = Math.min(this.tvs.size(), var2.tvs.size());

         for(int var4 = 0; var4 < var3; ++var4) {
            LdapName.TypeAndValue var5 = (LdapName.TypeAndValue)this.tvs.elementAt(var4);
            int var6 = var5.compareTo(var2.tvs.elementAt(var4));
            if (var6 != 0) {
               return var6;
            }
         }

         return this.tvs.size() - var2.tvs.size();
      }

      public int hashCode() {
         int var1 = 0;

         for(int var2 = 0; var2 < this.tvs.size(); ++var2) {
            var1 += ((LdapName.TypeAndValue)this.tvs.elementAt(var2)).hashCode();
         }

         return var1;
      }

      Attributes toAttributes() {
         BasicAttributes var1 = new BasicAttributes(true);

         for(int var4 = 0; var4 < this.tvs.size(); ++var4) {
            LdapName.TypeAndValue var2 = (LdapName.TypeAndValue)this.tvs.elementAt(var4);
            Attribute var3;
            if ((var3 = var1.get(var2.getType())) == null) {
               var1.put(var2.getType(), var2.getUnescapedValue());
            } else {
               var3.add(var2.getUnescapedValue());
            }
         }

         return var1;
      }
   }

   static class DnParser {
      private final String name;
      private final char[] chars;
      private final int len;
      private int cur = 0;
      private boolean valuesCaseSensitive;

      DnParser(String var1, boolean var2) throws InvalidNameException {
         this.name = var1;
         this.len = var1.length();
         this.chars = var1.toCharArray();
         this.valuesCaseSensitive = var2;
      }

      Vector<LdapName.Rdn> getDn() throws InvalidNameException {
         this.cur = 0;
         Vector var1 = new Vector(this.len / 3 + 10);
         if (this.len == 0) {
            return var1;
         } else {
            var1.addElement(this.parseRdn());

            while(this.cur < this.len) {
               if (this.chars[this.cur] != ',' && this.chars[this.cur] != ';') {
                  throw new InvalidNameException("Invalid name: " + this.name);
               }

               ++this.cur;
               var1.insertElementAt(this.parseRdn(), 0);
            }

            return var1;
         }
      }

      LdapName.Rdn getRdn() throws InvalidNameException {
         LdapName.Rdn var1 = this.parseRdn();
         if (this.cur < this.len) {
            throw new InvalidNameException("Invalid RDN: " + this.name);
         } else {
            return var1;
         }
      }

      private LdapName.Rdn parseRdn() throws InvalidNameException {
         LdapName.Rdn var1 = new LdapName.Rdn();

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
               var1.add(new LdapName.TypeAndValue(var2, var3, this.valuesCaseSensitive));
               if (this.cur < this.len && this.chars[this.cur] == '+') {
                  ++this.cur;
                  continue;
               }
            }

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
            for(var3 = this.cur; var3 > var1 && LdapName.isWhitespace(this.chars[var3 - 1]) && var2 != var3 - 1; --var3) {
            }

            return new String(this.chars, var1, var3 - var1);
         }
      }

      private void consumeWhitespace() {
         while(this.cur < this.len && LdapName.isWhitespace(this.chars[this.cur])) {
            ++this.cur;
         }

      }

      private boolean atTerminator() {
         return this.cur < this.len && (this.chars[this.cur] == ',' || this.chars[this.cur] == ';' || this.chars[this.cur] == '+');
      }
   }
}
