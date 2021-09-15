package javax.naming.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class BasicAttributes implements Attributes {
   private boolean ignoreCase;
   transient Hashtable<String, Attribute> attrs;
   private static final long serialVersionUID = 4980164073184639448L;

   public BasicAttributes() {
      this.ignoreCase = false;
      this.attrs = new Hashtable(11);
   }

   public BasicAttributes(boolean var1) {
      this.ignoreCase = false;
      this.attrs = new Hashtable(11);
      this.ignoreCase = var1;
   }

   public BasicAttributes(String var1, Object var2) {
      this();
      this.put(new BasicAttribute(var1, var2));
   }

   public BasicAttributes(String var1, Object var2, boolean var3) {
      this(var3);
      this.put(new BasicAttribute(var1, var2));
   }

   public Object clone() {
      BasicAttributes var1;
      try {
         var1 = (BasicAttributes)super.clone();
      } catch (CloneNotSupportedException var3) {
         var1 = new BasicAttributes(this.ignoreCase);
      }

      var1.attrs = (Hashtable)this.attrs.clone();
      return var1;
   }

   public boolean isCaseIgnored() {
      return this.ignoreCase;
   }

   public int size() {
      return this.attrs.size();
   }

   public Attribute get(String var1) {
      Attribute var2 = (Attribute)this.attrs.get(this.ignoreCase ? var1.toLowerCase(Locale.ENGLISH) : var1);
      return var2;
   }

   public NamingEnumeration<Attribute> getAll() {
      return new BasicAttributes.AttrEnumImpl();
   }

   public NamingEnumeration<String> getIDs() {
      return new BasicAttributes.IDEnumImpl();
   }

   public Attribute put(String var1, Object var2) {
      return this.put(new BasicAttribute(var1, var2));
   }

   public Attribute put(Attribute var1) {
      String var2 = var1.getID();
      if (this.ignoreCase) {
         var2 = var2.toLowerCase(Locale.ENGLISH);
      }

      return (Attribute)this.attrs.put(var2, var1);
   }

   public Attribute remove(String var1) {
      String var2 = this.ignoreCase ? var1.toLowerCase(Locale.ENGLISH) : var1;
      return (Attribute)this.attrs.remove(var2);
   }

   public String toString() {
      return this.attrs.size() == 0 ? "No attributes" : this.attrs.toString();
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Attributes) {
         Attributes var2 = (Attributes)var1;
         if (this.ignoreCase != var2.isCaseIgnored()) {
            return false;
         }

         if (this.size() == var2.size()) {
            try {
               NamingEnumeration var5 = var2.getAll();

               Attribute var3;
               Attribute var4;
               do {
                  if (!var5.hasMore()) {
                     return true;
                  }

                  var3 = (Attribute)var5.next();
                  var4 = this.get(var3.getID());
               } while(var3.equals(var4));

               return false;
            } catch (NamingException var6) {
               return false;
            }
         }
      }

      return false;
   }

   public int hashCode() {
      int var1 = this.ignoreCase ? 1 : 0;

      try {
         for(NamingEnumeration var2 = this.getAll(); var2.hasMore(); var1 += var2.next().hashCode()) {
         }
      } catch (NamingException var3) {
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.attrs.size());
      Enumeration var2 = this.attrs.elements();

      while(var2.hasMoreElements()) {
         var1.writeObject(var2.nextElement());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      this.attrs = var2 >= 1 ? new Hashtable(1 + (int)((float)Math.min(768, var2) / 0.75F)) : new Hashtable(2);

      while(true) {
         --var2;
         if (var2 < 0) {
            return;
         }

         this.put((Attribute)var1.readObject());
      }
   }

   class IDEnumImpl implements NamingEnumeration<String> {
      Enumeration<Attribute> elements;

      public IDEnumImpl() {
         this.elements = BasicAttributes.this.attrs.elements();
      }

      public boolean hasMoreElements() {
         return this.elements.hasMoreElements();
      }

      public String nextElement() {
         Attribute var1 = (Attribute)this.elements.nextElement();
         return var1.getID();
      }

      public boolean hasMore() throws NamingException {
         return this.hasMoreElements();
      }

      public String next() throws NamingException {
         return this.nextElement();
      }

      public void close() throws NamingException {
         this.elements = null;
      }
   }

   class AttrEnumImpl implements NamingEnumeration<Attribute> {
      Enumeration<Attribute> elements;

      public AttrEnumImpl() {
         this.elements = BasicAttributes.this.attrs.elements();
      }

      public boolean hasMoreElements() {
         return this.elements.hasMoreElements();
      }

      public Attribute nextElement() {
         return (Attribute)this.elements.nextElement();
      }

      public boolean hasMore() throws NamingException {
         return this.hasMoreElements();
      }

      public Attribute next() throws NamingException {
         return this.nextElement();
      }

      public void close() throws NamingException {
         this.elements = null;
      }
   }
}
