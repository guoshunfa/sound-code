package javax.naming.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

public class BasicAttribute implements Attribute {
   protected String attrID;
   protected transient Vector<Object> values;
   protected boolean ordered;
   private static final long serialVersionUID = 6743528196119291326L;

   public Object clone() {
      BasicAttribute var1;
      try {
         var1 = (BasicAttribute)super.clone();
      } catch (CloneNotSupportedException var3) {
         var1 = new BasicAttribute(this.attrID, this.ordered);
      }

      var1.values = (Vector)this.values.clone();
      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Attribute) {
         Attribute var2 = (Attribute)var1;
         if (this.isOrdered() != var2.isOrdered()) {
            return false;
         }

         int var3;
         if (this.attrID.equals(var2.getID()) && (var3 = this.size()) == var2.size()) {
            try {
               if (this.isOrdered()) {
                  for(int var4 = 0; var4 < var3; ++var4) {
                     if (!valueEquals(this.get(var4), var2.get(var4))) {
                        return false;
                     }
                  }
               } else {
                  NamingEnumeration var6 = var2.getAll();

                  while(var6.hasMoreElements()) {
                     if (this.find(var6.nextElement()) < 0) {
                        return false;
                     }
                  }
               }

               return true;
            } catch (NamingException var5) {
               return false;
            }
         }
      }

      return false;
   }

   public int hashCode() {
      int var1 = this.attrID.hashCode();
      int var2 = this.values.size();

      for(int var4 = 0; var4 < var2; ++var4) {
         Object var3 = this.values.elementAt(var4);
         if (var3 != null) {
            if (var3.getClass().isArray()) {
               int var6 = Array.getLength(var3);

               for(int var7 = 0; var7 < var6; ++var7) {
                  Object var5 = Array.get(var3, var7);
                  if (var5 != null) {
                     var1 += var5.hashCode();
                  }
               }
            } else {
               var1 += var3.hashCode();
            }
         }
      }

      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(this.attrID + ": ");
      if (this.values.size() == 0) {
         var1.append("No values");
      } else {
         boolean var2 = true;

         for(Enumeration var3 = this.values.elements(); var3.hasMoreElements(); var2 = false) {
            if (!var2) {
               var1.append(", ");
            }

            var1.append(var3.nextElement());
         }
      }

      return var1.toString();
   }

   public BasicAttribute(String var1) {
      this(var1, false);
   }

   public BasicAttribute(String var1, Object var2) {
      this(var1, var2, false);
   }

   public BasicAttribute(String var1, boolean var2) {
      this.ordered = false;
      this.attrID = var1;
      this.values = new Vector();
      this.ordered = var2;
   }

   public BasicAttribute(String var1, Object var2, boolean var3) {
      this(var1, var3);
      this.values.addElement(var2);
   }

   public NamingEnumeration<?> getAll() throws NamingException {
      return new BasicAttribute.ValuesEnumImpl();
   }

   public Object get() throws NamingException {
      if (this.values.size() == 0) {
         throw new NoSuchElementException("Attribute " + this.getID() + " has no value");
      } else {
         return this.values.elementAt(0);
      }
   }

   public int size() {
      return this.values.size();
   }

   public String getID() {
      return this.attrID;
   }

   public boolean contains(Object var1) {
      return this.find(var1) >= 0;
   }

   private int find(Object var1) {
      int var3;
      if (var1 == null) {
         var3 = this.values.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            if (this.values.elementAt(var4) == null) {
               return var4;
            }
         }
      } else {
         Class var2;
         if (!(var2 = var1.getClass()).isArray()) {
            return this.values.indexOf(var1, 0);
         }

         var3 = this.values.size();

         for(int var5 = 0; var5 < var3; ++var5) {
            Object var6 = this.values.elementAt(var5);
            if (var6 != null && var2 == var6.getClass() && arrayEquals(var1, var6)) {
               return var5;
            }
         }
      }

      return -1;
   }

   private static boolean valueEquals(Object var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 == null) {
         return false;
      } else {
         return var0.getClass().isArray() && var1.getClass().isArray() ? arrayEquals(var0, var1) : var0.equals(var1);
      }
   }

   private static boolean arrayEquals(Object var0, Object var1) {
      int var2;
      if ((var2 = Array.getLength(var0)) != Array.getLength(var1)) {
         return false;
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            Object var4 = Array.get(var0, var3);
            Object var5 = Array.get(var1, var3);
            if (var4 != null && var5 != null) {
               if (!var4.equals(var5)) {
                  return false;
               }
            } else if (var4 != var5) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean add(Object var1) {
      if (!this.isOrdered() && this.find(var1) >= 0) {
         return false;
      } else {
         this.values.addElement(var1);
         return true;
      }
   }

   public boolean remove(Object var1) {
      int var2 = this.find(var1);
      if (var2 >= 0) {
         this.values.removeElementAt(var2);
         return true;
      } else {
         return false;
      }
   }

   public void clear() {
      this.values.setSize(0);
   }

   public boolean isOrdered() {
      return this.ordered;
   }

   public Object get(int var1) throws NamingException {
      return this.values.elementAt(var1);
   }

   public Object remove(int var1) {
      Object var2 = this.values.elementAt(var1);
      this.values.removeElementAt(var1);
      return var2;
   }

   public void add(int var1, Object var2) {
      if (!this.isOrdered() && this.contains(var2)) {
         throw new IllegalStateException("Cannot add duplicate to unordered attribute");
      } else {
         this.values.insertElementAt(var2, var1);
      }
   }

   public Object set(int var1, Object var2) {
      if (!this.isOrdered() && this.contains(var2)) {
         throw new IllegalStateException("Cannot add duplicate to unordered attribute");
      } else {
         Object var3 = this.values.elementAt(var1);
         this.values.setElementAt(var2, var1);
         return var3;
      }
   }

   public DirContext getAttributeSyntaxDefinition() throws NamingException {
      throw new OperationNotSupportedException("attribute syntax");
   }

   public DirContext getAttributeDefinition() throws NamingException {
      throw new OperationNotSupportedException("attribute definition");
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.values.size());

      for(int var2 = 0; var2 < this.values.size(); ++var2) {
         var1.writeObject(this.values.elementAt(var2));
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      this.values = new Vector(Math.min(1024, var2));

      while(true) {
         --var2;
         if (var2 < 0) {
            return;
         }

         this.values.addElement(var1.readObject());
      }
   }

   class ValuesEnumImpl implements NamingEnumeration<Object> {
      Enumeration<Object> list;

      ValuesEnumImpl() {
         this.list = BasicAttribute.this.values.elements();
      }

      public boolean hasMoreElements() {
         return this.list.hasMoreElements();
      }

      public Object nextElement() {
         return this.list.nextElement();
      }

      public Object next() throws NamingException {
         return this.list.nextElement();
      }

      public boolean hasMore() throws NamingException {
         return this.list.hasMoreElements();
      }

      public void close() throws NamingException {
         this.list = null;
      }
   }
}
