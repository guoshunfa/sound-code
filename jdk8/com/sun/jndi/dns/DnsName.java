package com.sun.jndi.dns;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public final class DnsName implements Name {
   private String domain = "";
   private ArrayList<String> labels = new ArrayList();
   private short octets = 1;
   private static final long serialVersionUID = 7040187611324710271L;

   public DnsName() {
   }

   public DnsName(String var1) throws InvalidNameException {
      this.parse(var1);
   }

   private DnsName(DnsName var1, int var2, int var3) {
      int var4 = var1.size() - var3;
      int var5 = var1.size() - var2;
      this.labels.addAll(var1.labels.subList(var4, var5));
      if (this.size() == var1.size()) {
         this.domain = var1.domain;
         this.octets = var1.octets;
      } else {
         Iterator var6 = this.labels.iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            if (var7.length() > 0) {
               this.octets += (short)(var7.length() + 1);
            }
         }
      }

   }

   public String toString() {
      if (this.domain == null) {
         StringBuilder var1 = new StringBuilder();

         String var3;
         for(Iterator var2 = this.labels.iterator(); var2.hasNext(); escape(var1, var3)) {
            var3 = (String)var2.next();
            if (var1.length() > 0 || var3.length() == 0) {
               var1.append('.');
            }
         }

         this.domain = var1.toString();
      }

      return this.domain;
   }

   public boolean isHostName() {
      Iterator var1 = this.labels.iterator();

      String var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (String)var1.next();
      } while(isHostNameLabel(var2));

      return false;
   }

   public short getOctets() {
      return this.octets;
   }

   public int size() {
      return this.labels.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public int hashCode() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.size(); ++var2) {
         var1 = 31 * var1 + this.getKey(var2).hashCode();
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Name && !(var1 instanceof CompositeName)) {
         Name var2 = (Name)var1;
         return this.size() == var2.size() && this.compareTo(var1) == 0;
      } else {
         return false;
      }
   }

   public int compareTo(Object var1) {
      Name var2 = (Name)var1;
      return this.compareRange(0, this.size(), var2);
   }

   public boolean startsWith(Name var1) {
      return this.size() >= var1.size() && this.compareRange(0, var1.size(), var1) == 0;
   }

   public boolean endsWith(Name var1) {
      return this.size() >= var1.size() && this.compareRange(this.size() - var1.size(), this.size(), var1) == 0;
   }

   public String get(int var1) {
      if (var1 >= 0 && var1 < this.size()) {
         int var2 = this.size() - var1 - 1;
         return (String)this.labels.get(var2);
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public Enumeration<String> getAll() {
      return new Enumeration<String>() {
         int pos = 0;

         public boolean hasMoreElements() {
            return this.pos < DnsName.this.size();
         }

         public String nextElement() {
            if (this.pos < DnsName.this.size()) {
               return DnsName.this.get(this.pos++);
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   public Name getPrefix(int var1) {
      return new DnsName(this, 0, var1);
   }

   public Name getSuffix(int var1) {
      return new DnsName(this, var1, this.size());
   }

   public Object clone() {
      return new DnsName(this, 0, this.size());
   }

   public Object remove(int var1) {
      if (var1 >= 0 && var1 < this.size()) {
         int var2 = this.size() - var1 - 1;
         String var3 = (String)this.labels.remove(var2);
         int var4 = var3.length();
         if (var4 > 0) {
            this.octets -= (short)(var4 + 1);
         }

         this.domain = null;
         return var3;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public Name add(String var1) throws InvalidNameException {
      return this.add(this.size(), var1);
   }

   public Name add(int var1, String var2) throws InvalidNameException {
      if (var1 >= 0 && var1 <= this.size()) {
         int var3 = var2.length();
         if (var1 > 0 && var3 == 0 || var1 == 0 && this.hasRootLabel()) {
            throw new InvalidNameException("Empty label must be the last label in a domain name");
         } else {
            if (var3 > 0) {
               if (this.octets + var3 + 1 >= 256) {
                  throw new InvalidNameException("Name too long");
               }

               this.octets += (short)(var3 + 1);
            }

            int var4 = this.size() - var1;
            verifyLabel(var2);
            this.labels.add(var4, var2);
            this.domain = null;
            return this;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public Name addAll(Name var1) throws InvalidNameException {
      return this.addAll(this.size(), var1);
   }

   public Name addAll(int var1, Name var2) throws InvalidNameException {
      if (var2 instanceof DnsName) {
         DnsName var7 = (DnsName)var2;
         if (var7.isEmpty()) {
            return this;
         }

         if (var1 > 0 && var7.hasRootLabel() || var1 == 0 && this.hasRootLabel()) {
            throw new InvalidNameException("Empty label must be the last label in a domain name");
         }

         short var4 = (short)(this.octets + var7.octets - 1);
         if (var4 > 255) {
            throw new InvalidNameException("Name too long");
         }

         this.octets = var4;
         int var5 = this.size() - var1;
         this.labels.addAll(var5, var7.labels);
         if (this.isEmpty()) {
            this.domain = var7.domain;
         } else if (this.domain != null && var7.domain != null) {
            if (var1 == 0) {
               this.domain = this.domain + (var7.domain.equals(".") ? "" : ".") + var7.domain;
            } else if (var1 == this.size()) {
               this.domain = var7.domain + (this.domain.equals(".") ? "" : ".") + this.domain;
            } else {
               this.domain = null;
            }
         } else {
            this.domain = null;
         }
      } else if (var2 instanceof CompositeName) {
         DnsName var6 = (DnsName)var2;
      } else {
         for(int var3 = var2.size() - 1; var3 >= 0; --var3) {
            this.add(var1, var2.get(var3));
         }
      }

      return this;
   }

   boolean hasRootLabel() {
      return !this.isEmpty() && this.get(0).equals("");
   }

   private int compareRange(int var1, int var2, Name var3) {
      if (var3 instanceof CompositeName) {
         var3 = (DnsName)var3;
      }

      int var4 = Math.min(var2 - var1, ((Name)var3).size());

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = this.get(var5 + var1);
         String var7 = ((Name)var3).get(var5);
         int var8 = this.size() - (var5 + var1) - 1;
         int var9 = compareLabels(var6, var7);
         if (var9 != 0) {
            return var9;
         }
      }

      return var2 - var1 - ((Name)var3).size();
   }

   String getKey(int var1) {
      return keyForLabel(this.get(var1));
   }

   private void parse(String var1) throws InvalidNameException {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var3 < var1.length(); ++var3) {
         char var4 = var1.charAt(var3);
         if (var4 == '\\') {
            var4 = getEscapedOctet(var1, var3++);
            if (isDigit(var1.charAt(var3))) {
               var3 += 2;
            }

            var2.append(var4);
         } else if (var4 != '.') {
            var2.append(var4);
         } else {
            this.add(0, var2.toString());
            var2.delete(0, var3);
         }
      }

      if (!var1.equals("") && !var1.equals(".")) {
         this.add(0, var2.toString());
      }

      this.domain = var1;
   }

   private static char getEscapedOctet(String var0, int var1) throws InvalidNameException {
      try {
         ++var1;
         char var2 = var0.charAt(var1);
         if (isDigit(var2)) {
            ++var1;
            char var3 = var0.charAt(var1);
            ++var1;
            char var4 = var0.charAt(var1);
            if (isDigit(var3) && isDigit(var4)) {
               return (char)((var2 - 48) * 100 + (var3 - 48) * 10 + (var4 - 48));
            } else {
               throw new InvalidNameException("Invalid escape sequence in " + var0);
            }
         } else {
            return var2;
         }
      } catch (IndexOutOfBoundsException var5) {
         throw new InvalidNameException("Invalid escape sequence in " + var0);
      }
   }

   private static void verifyLabel(String var0) throws InvalidNameException {
      if (var0.length() > 63) {
         throw new InvalidNameException("Label exceeds 63 octets: " + var0);
      } else {
         for(int var1 = 0; var1 < var0.length(); ++var1) {
            char var2 = var0.charAt(var1);
            if ((var2 & '\uff00') != 0) {
               throw new InvalidNameException("Label has two-byte char: " + var0);
            }
         }

      }
   }

   private static boolean isHostNameLabel(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         char var2 = var0.charAt(var1);
         if (!isHostNameChar(var2)) {
            return false;
         }
      }

      return !var0.startsWith("-") && !var0.endsWith("-");
   }

   private static boolean isHostNameChar(char var0) {
      return var0 == '-' || var0 >= 'a' && var0 <= 'z' || var0 >= 'A' && var0 <= 'Z' || var0 >= '0' && var0 <= '9';
   }

   private static boolean isDigit(char var0) {
      return var0 >= '0' && var0 <= '9';
   }

   private static void escape(StringBuilder var0, String var1) {
      for(int var2 = 0; var2 < var1.length(); ++var2) {
         char var3 = var1.charAt(var2);
         if (var3 == '.' || var3 == '\\') {
            var0.append('\\');
         }

         var0.append(var3);
      }

   }

   private static int compareLabels(String var0, String var1) {
      int var2 = Math.min(var0.length(), var1.length());

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var0.charAt(var3);
         char var5 = var1.charAt(var3);
         if (var4 >= 'A' && var4 <= 'Z') {
            var4 = (char)(var4 + 32);
         }

         if (var5 >= 'A' && var5 <= 'Z') {
            var5 = (char)(var5 + 32);
         }

         if (var4 != var5) {
            return var4 - var5;
         }
      }

      return var0.length() - var1.length();
   }

   private static String keyForLabel(String var0) {
      StringBuffer var1 = new StringBuffer(var0.length());

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 >= 'A' && var3 <= 'Z') {
            var3 = (char)(var3 + 32);
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.toString());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      try {
         this.parse((String)var1.readObject());
      } catch (InvalidNameException var3) {
         throw new StreamCorruptedException("Invalid name: " + this.domain);
      }
   }
}
