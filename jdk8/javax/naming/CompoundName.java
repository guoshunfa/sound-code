package javax.naming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;
import java.util.Properties;

public class CompoundName implements Name {
   protected transient NameImpl impl;
   protected transient Properties mySyntax;
   private static final long serialVersionUID = 3513100557083972036L;

   protected CompoundName(Enumeration<String> var1, Properties var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.mySyntax = var2;
         this.impl = new NameImpl(var2, var1);
      }
   }

   public CompoundName(String var1, Properties var2) throws InvalidNameException {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.mySyntax = var2;
         this.impl = new NameImpl(var2, var1);
      }
   }

   public String toString() {
      return this.impl.toString();
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof CompoundName && this.impl.equals(((CompoundName)var1).impl);
   }

   public int hashCode() {
      return this.impl.hashCode();
   }

   public Object clone() {
      return new CompoundName(this.getAll(), this.mySyntax);
   }

   public int compareTo(Object var1) {
      if (!(var1 instanceof CompoundName)) {
         throw new ClassCastException("Not a CompoundName");
      } else {
         return this.impl.compareTo(((CompoundName)var1).impl);
      }
   }

   public int size() {
      return this.impl.size();
   }

   public boolean isEmpty() {
      return this.impl.isEmpty();
   }

   public Enumeration<String> getAll() {
      return this.impl.getAll();
   }

   public String get(int var1) {
      return this.impl.get(var1);
   }

   public Name getPrefix(int var1) {
      Enumeration var2 = this.impl.getPrefix(var1);
      return new CompoundName(var2, this.mySyntax);
   }

   public Name getSuffix(int var1) {
      Enumeration var2 = this.impl.getSuffix(var1);
      return new CompoundName(var2, this.mySyntax);
   }

   public boolean startsWith(Name var1) {
      return var1 instanceof CompoundName ? this.impl.startsWith(var1.size(), var1.getAll()) : false;
   }

   public boolean endsWith(Name var1) {
      return var1 instanceof CompoundName ? this.impl.endsWith(var1.size(), var1.getAll()) : false;
   }

   public Name addAll(Name var1) throws InvalidNameException {
      if (var1 instanceof CompoundName) {
         this.impl.addAll(var1.getAll());
         return this;
      } else {
         throw new InvalidNameException("Not a compound name: " + var1.toString());
      }
   }

   public Name addAll(int var1, Name var2) throws InvalidNameException {
      if (var2 instanceof CompoundName) {
         this.impl.addAll(var1, var2.getAll());
         return this;
      } else {
         throw new InvalidNameException("Not a compound name: " + var2.toString());
      }
   }

   public Name add(String var1) throws InvalidNameException {
      this.impl.add(var1);
      return this;
   }

   public Name add(int var1, String var2) throws InvalidNameException {
      this.impl.add(var1, var2);
      return this;
   }

   public Object remove(int var1) throws InvalidNameException {
      return this.impl.remove(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.mySyntax);
      var1.writeInt(this.size());
      Enumeration var2 = this.getAll();

      while(var2.hasMoreElements()) {
         var1.writeObject(var2.nextElement());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.mySyntax = (Properties)var1.readObject();
      this.impl = new NameImpl(this.mySyntax);
      int var2 = var1.readInt();

      try {
         while(true) {
            --var2;
            if (var2 < 0) {
               return;
            }

            this.add((String)var1.readObject());
         }
      } catch (InvalidNameException var4) {
         throw new StreamCorruptedException("Invalid name");
      }
   }
}
