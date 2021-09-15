package javax.naming.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public class LdapName implements Name {
   private transient List<Rdn> rdns;
   private transient String unparsed;
   private static final long serialVersionUID = -1595520034788997356L;

   public LdapName(String var1) throws InvalidNameException {
      this.unparsed = var1;
      this.parse();
   }

   public LdapName(List<Rdn> var1) {
      this.rdns = new ArrayList(var1.size());

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Object var3 = var1.get(var2);
         if (!(var3 instanceof Rdn)) {
            throw new IllegalArgumentException("Entry:" + var3 + "  not a valid type;list entries must be of type Rdn");
         }

         this.rdns.add((Rdn)var3);
      }

   }

   private LdapName(String var1, List<Rdn> var2, int var3, int var4) {
      this.unparsed = var1;
      List var5 = var2.subList(var3, var4);
      this.rdns = new ArrayList(var5);
   }

   public int size() {
      return this.rdns.size();
   }

   public boolean isEmpty() {
      return this.rdns.isEmpty();
   }

   public Enumeration<String> getAll() {
      final Iterator var1 = this.rdns.iterator();
      return new Enumeration<String>() {
         public boolean hasMoreElements() {
            return var1.hasNext();
         }

         public String nextElement() {
            return ((Rdn)var1.next()).toString();
         }
      };
   }

   public String get(int var1) {
      return ((Rdn)this.rdns.get(var1)).toString();
   }

   public Rdn getRdn(int var1) {
      return (Rdn)this.rdns.get(var1);
   }

   public Name getPrefix(int var1) {
      try {
         return new LdapName((String)null, this.rdns, 0, var1);
      } catch (IllegalArgumentException var3) {
         throw new IndexOutOfBoundsException("Posn: " + var1 + ", Size: " + this.rdns.size());
      }
   }

   public Name getSuffix(int var1) {
      try {
         return new LdapName((String)null, this.rdns, var1, this.rdns.size());
      } catch (IllegalArgumentException var3) {
         throw new IndexOutOfBoundsException("Posn: " + var1 + ", Size: " + this.rdns.size());
      }
   }

   public boolean startsWith(Name var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.rdns.size();
         int var3 = var1.size();
         return var2 >= var3 && this.matches(0, var3, var1);
      }
   }

   public boolean startsWith(List<Rdn> var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.rdns.size();
         int var3 = var1.size();
         return var2 >= var3 && this.doesListMatch(0, var3, var1);
      }
   }

   public boolean endsWith(Name var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.rdns.size();
         int var3 = var1.size();
         return var2 >= var3 && this.matches(var2 - var3, var2, var1);
      }
   }

   public boolean endsWith(List<Rdn> var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.rdns.size();
         int var3 = var1.size();
         return var2 >= var3 && this.doesListMatch(var2 - var3, var2, var1);
      }
   }

   private boolean doesListMatch(int var1, int var2, List<Rdn> var3) {
      for(int var4 = var1; var4 < var2; ++var4) {
         if (!((Rdn)this.rdns.get(var4)).equals(var3.get(var4 - var1))) {
            return false;
         }
      }

      return true;
   }

   private boolean matches(int var1, int var2, Name var3) {
      if (var3 instanceof LdapName) {
         LdapName var9 = (LdapName)var3;
         return this.doesListMatch(var1, var2, var9.rdns);
      } else {
         for(int var4 = var1; var4 < var2; ++var4) {
            String var6 = var3.get(var4 - var1);

            Rdn var5;
            try {
               var5 = (new Rfc2253Parser(var6)).parseRdn();
            } catch (InvalidNameException var8) {
               return false;
            }

            if (!var5.equals(this.rdns.get(var4))) {
               return false;
            }
         }

         return true;
      }
   }

   public Name addAll(Name var1) throws InvalidNameException {
      return this.addAll(this.size(), var1);
   }

   public Name addAll(List<Rdn> var1) {
      return this.addAll(this.size(), var1);
   }

   public Name addAll(int var1, Name var2) throws InvalidNameException {
      this.unparsed = null;
      if (var2 instanceof LdapName) {
         LdapName var3 = (LdapName)var2;
         this.rdns.addAll(var1, var3.rdns);
      } else {
         Enumeration var4 = var2.getAll();

         while(var4.hasMoreElements()) {
            this.rdns.add(var1++, (new Rfc2253Parser((String)var4.nextElement())).parseRdn());
         }
      }

      return this;
   }

   public Name addAll(int var1, List<Rdn> var2) {
      this.unparsed = null;

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         Object var4 = var2.get(var3);
         if (!(var4 instanceof Rdn)) {
            throw new IllegalArgumentException("Entry:" + var4 + "  not a valid type;suffix list entries must be of type Rdn");
         }

         this.rdns.add(var3 + var1, (Rdn)var4);
      }

      return this;
   }

   public Name add(String var1) throws InvalidNameException {
      return this.add(this.size(), var1);
   }

   public Name add(Rdn var1) {
      return this.add(this.size(), var1);
   }

   public Name add(int var1, String var2) throws InvalidNameException {
      Rdn var3 = (new Rfc2253Parser(var2)).parseRdn();
      this.rdns.add(var1, var3);
      this.unparsed = null;
      return this;
   }

   public Name add(int var1, Rdn var2) {
      if (var2 == null) {
         throw new NullPointerException("Cannot set comp to null");
      } else {
         this.rdns.add(var1, var2);
         this.unparsed = null;
         return this;
      }
   }

   public Object remove(int var1) throws InvalidNameException {
      this.unparsed = null;
      return ((Rdn)this.rdns.remove(var1)).toString();
   }

   public List<Rdn> getRdns() {
      return Collections.unmodifiableList(this.rdns);
   }

   public Object clone() {
      return new LdapName(this.unparsed, this.rdns, 0, this.rdns.size());
   }

   public String toString() {
      if (this.unparsed != null) {
         return this.unparsed;
      } else {
         StringBuilder var1 = new StringBuilder();
         int var2 = this.rdns.size();
         if (var2 - 1 >= 0) {
            var1.append(this.rdns.get(var2 - 1));
         }

         for(int var3 = var2 - 2; var3 >= 0; --var3) {
            var1.append(',');
            var1.append(this.rdns.get(var3));
         }

         this.unparsed = var1.toString();
         return this.unparsed;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LdapName)) {
         return false;
      } else {
         LdapName var2 = (LdapName)var1;
         if (this.rdns.size() != var2.rdns.size()) {
            return false;
         } else if (this.unparsed != null && this.unparsed.equalsIgnoreCase(var2.unparsed)) {
            return true;
         } else {
            for(int var3 = 0; var3 < this.rdns.size(); ++var3) {
               Rdn var4 = (Rdn)this.rdns.get(var3);
               Rdn var5 = (Rdn)var2.rdns.get(var3);
               if (!var4.equals(var5)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int compareTo(Object var1) {
      if (!(var1 instanceof LdapName)) {
         throw new ClassCastException("The obj is not a LdapName");
      } else if (var1 == this) {
         return 0;
      } else {
         LdapName var2 = (LdapName)var1;
         if (this.unparsed != null && this.unparsed.equalsIgnoreCase(var2.unparsed)) {
            return 0;
         } else {
            int var3 = Math.min(this.rdns.size(), var2.rdns.size());

            for(int var4 = 0; var4 < var3; ++var4) {
               Rdn var5 = (Rdn)this.rdns.get(var4);
               Rdn var6 = (Rdn)var2.rdns.get(var4);
               int var7 = var5.compareTo(var6);
               if (var7 != 0) {
                  return var7;
               }
            }

            return this.rdns.size() - var2.rdns.size();
         }
      }
   }

   public int hashCode() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.rdns.size(); ++var2) {
         Rdn var3 = (Rdn)this.rdns.get(var2);
         var1 += var3.hashCode();
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.toString());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.unparsed = (String)var1.readObject();

      try {
         this.parse();
      } catch (InvalidNameException var3) {
         throw new StreamCorruptedException("Invalid name: " + this.unparsed);
      }
   }

   private void parse() throws InvalidNameException {
      this.rdns = (new Rfc2253Parser(this.unparsed)).parseDn();
   }
}
