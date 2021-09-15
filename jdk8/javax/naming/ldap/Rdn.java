package javax.naming.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public class Rdn implements Serializable, Comparable<Object> {
   private transient ArrayList<Rdn.RdnEntry> entries;
   private static final int DEFAULT_SIZE = 1;
   private static final long serialVersionUID = -5994465067210009656L;
   private static final String escapees = ",=+<>#;\"\\";

   public Rdn(Attributes var1) throws InvalidNameException {
      if (var1.size() == 0) {
         throw new InvalidNameException("Attributes cannot be empty");
      } else {
         this.entries = new ArrayList(var1.size());
         NamingEnumeration var2 = var1.getAll();

         try {
            for(int var3 = 0; var2.hasMore(); ++var3) {
               Rdn.RdnEntry var7 = new Rdn.RdnEntry();
               Attribute var5 = (Attribute)var2.next();
               var7.type = var5.getID();
               var7.value = var5.get();
               this.entries.add(var3, var7);
            }
         } catch (NamingException var6) {
            InvalidNameException var4 = new InvalidNameException(var6.getMessage());
            var4.initCause(var6);
            throw var4;
         }

         this.sort();
      }
   }

   public Rdn(String var1) throws InvalidNameException {
      this.entries = new ArrayList(1);
      (new Rfc2253Parser(var1)).parseRdn(this);
   }

   public Rdn(Rdn var1) {
      this.entries = new ArrayList(var1.entries.size());
      this.entries.addAll(var1.entries);
   }

   public Rdn(String var1, Object var2) throws InvalidNameException {
      if (var2 == null) {
         throw new NullPointerException("Cannot set value to null");
      } else if (!var1.equals("") && !this.isEmptyValue(var2)) {
         this.entries = new ArrayList(1);
         this.put(var1, var2);
      } else {
         throw new InvalidNameException("type or value cannot be empty, type:" + var1 + " value:" + var2);
      }
   }

   private boolean isEmptyValue(Object var1) {
      return var1 instanceof String && var1.equals("") || var1 instanceof byte[] && ((byte[])((byte[])var1)).length == 0;
   }

   Rdn() {
      this.entries = new ArrayList(1);
   }

   Rdn put(String var1, Object var2) {
      Rdn.RdnEntry var3 = new Rdn.RdnEntry();
      var3.type = var1;
      if (var2 instanceof byte[]) {
         var3.value = ((byte[])((byte[])var2)).clone();
      } else {
         var3.value = var2;
      }

      this.entries.add(var3);
      return this;
   }

   void sort() {
      if (this.entries.size() > 1) {
         Collections.sort(this.entries);
      }

   }

   public Object getValue() {
      return ((Rdn.RdnEntry)this.entries.get(0)).getValue();
   }

   public String getType() {
      return ((Rdn.RdnEntry)this.entries.get(0)).getType();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      int var2 = this.entries.size();
      if (var2 > 0) {
         var1.append(this.entries.get(0));
      }

      for(int var3 = 1; var3 < var2; ++var3) {
         var1.append('+');
         var1.append(this.entries.get(var3));
      }

      return var1.toString();
   }

   public int compareTo(Object var1) {
      if (!(var1 instanceof Rdn)) {
         throw new ClassCastException("The obj is not a Rdn");
      } else if (var1 == this) {
         return 0;
      } else {
         Rdn var2 = (Rdn)var1;
         int var3 = Math.min(this.entries.size(), var2.entries.size());

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = ((Rdn.RdnEntry)this.entries.get(var4)).compareTo((Rdn.RdnEntry)var2.entries.get(var4));
            if (var5 != 0) {
               return var5;
            }
         }

         return this.entries.size() - var2.entries.size();
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Rdn)) {
         return false;
      } else {
         Rdn var2 = (Rdn)var1;
         if (this.entries.size() != var2.size()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.entries.size(); ++var3) {
               if (!((Rdn.RdnEntry)this.entries.get(var3)).equals(var2.entries.get(var3))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.entries.size(); ++var2) {
         var1 += ((Rdn.RdnEntry)this.entries.get(var2)).hashCode();
      }

      return var1;
   }

   public Attributes toAttributes() {
      BasicAttributes var1 = new BasicAttributes(true);

      for(int var2 = 0; var2 < this.entries.size(); ++var2) {
         Rdn.RdnEntry var3 = (Rdn.RdnEntry)this.entries.get(var2);
         Attribute var4 = var1.put(var3.getType(), var3.getValue());
         if (var4 != null) {
            var4.add(var3.getValue());
            var1.put(var4);
         }
      }

      return var1;
   }

   public int size() {
      return this.entries.size();
   }

   public static String escapeValue(Object var0) {
      return var0 instanceof byte[] ? escapeBinaryValue((byte[])((byte[])var0)) : escapeStringValue((String)var0);
   }

   private static String escapeStringValue(String var0) {
      char[] var1 = var0.toCharArray();
      StringBuilder var2 = new StringBuilder(2 * var0.length());

      int var3;
      for(var3 = 0; var3 < var1.length && isWhitespace(var1[var3]); ++var3) {
      }

      int var4;
      for(var4 = var1.length - 1; var4 >= 0 && isWhitespace(var1[var4]); --var4) {
      }

      for(int var5 = 0; var5 < var1.length; ++var5) {
         char var6 = var1[var5];
         if (var5 < var3 || var5 > var4 || ",=+<>#;\"\\".indexOf(var6) >= 0) {
            var2.append('\\');
         }

         var2.append(var6);
      }

      return var2.toString();
   }

   private static String escapeBinaryValue(byte[] var0) {
      StringBuilder var1 = new StringBuilder(1 + 2 * var0.length);
      var1.append("#");

      for(int var2 = 0; var2 < var0.length; ++var2) {
         byte var3 = var0[var2];
         var1.append(Character.forDigit(15 & var3 >>> 4, 16));
         var1.append(Character.forDigit(15 & var3, 16));
      }

      return var1.toString();
   }

   public static Object unescapeValue(String var0) {
      char[] var1 = var0.toCharArray();
      int var2 = 0;

      int var3;
      for(var3 = var1.length; var2 < var3 && isWhitespace(var1[var2]); ++var2) {
      }

      while(var2 < var3 && isWhitespace(var1[var3 - 1])) {
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

         StringBuilder var4 = new StringBuilder(var3 - var2);
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
                     throw new IllegalArgumentException("Not a valid attribute string value:" + var0 + ",improper usage of backslash");
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
         if (isWhitespace(var4.charAt(var6 - 1)) && var5 != var3 - 1) {
            var4.setLength(var6 - 1);
         }

         return var4.toString();
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
         throw new IllegalArgumentException("Illegal attribute value: " + new String(var0));
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

   private static boolean isWhitespace(char var0) {
      return var0 == ' ' || var0 == '\r';
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.toString());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.entries = new ArrayList(1);
      String var2 = (String)var1.readObject();

      try {
         (new Rfc2253Parser(var2)).parseRdn(this);
      } catch (InvalidNameException var4) {
         throw new StreamCorruptedException("Invalid name: " + var2);
      }
   }

   private static class RdnEntry implements Comparable<Rdn.RdnEntry> {
      private String type;
      private Object value;
      private String comparable;

      private RdnEntry() {
         this.comparable = null;
      }

      String getType() {
         return this.type;
      }

      Object getValue() {
         return this.value;
      }

      public int compareTo(Rdn.RdnEntry var1) {
         int var2 = this.type.compareToIgnoreCase(var1.type);
         if (var2 != 0) {
            return var2;
         } else {
            return this.value.equals(var1.value) ? 0 : this.getValueComparable().compareTo(var1.getValueComparable());
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Rdn.RdnEntry)) {
            return false;
         } else {
            Rdn.RdnEntry var2 = (Rdn.RdnEntry)var1;
            return this.type.equalsIgnoreCase(var2.type) && this.getValueComparable().equals(var2.getValueComparable());
         }
      }

      public int hashCode() {
         return this.type.toUpperCase(Locale.ENGLISH).hashCode() + this.getValueComparable().hashCode();
      }

      public String toString() {
         return this.type + "=" + Rdn.escapeValue(this.value);
      }

      private String getValueComparable() {
         if (this.comparable != null) {
            return this.comparable;
         } else {
            if (this.value instanceof byte[]) {
               this.comparable = Rdn.escapeBinaryValue((byte[])((byte[])this.value));
            } else {
               this.comparable = ((String)this.value).toUpperCase(Locale.ENGLISH);
            }

            return this.comparable;
         }
      }

      // $FF: synthetic method
      RdnEntry(Object var1) {
         this();
      }
   }
}
