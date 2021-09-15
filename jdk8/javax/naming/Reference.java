package javax.naming;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class Reference implements Cloneable, Serializable {
   protected String className;
   protected Vector<RefAddr> addrs;
   protected String classFactory;
   protected String classFactoryLocation;
   private static final long serialVersionUID = -1673475790065791735L;

   public Reference(String var1) {
      this.addrs = null;
      this.classFactory = null;
      this.classFactoryLocation = null;
      this.className = var1;
      this.addrs = new Vector();
   }

   public Reference(String var1, RefAddr var2) {
      this.addrs = null;
      this.classFactory = null;
      this.classFactoryLocation = null;
      this.className = var1;
      this.addrs = new Vector();
      this.addrs.addElement(var2);
   }

   public Reference(String var1, String var2, String var3) {
      this(var1);
      this.classFactory = var2;
      this.classFactoryLocation = var3;
   }

   public Reference(String var1, RefAddr var2, String var3, String var4) {
      this(var1, var2);
      this.classFactory = var3;
      this.classFactoryLocation = var4;
   }

   public String getClassName() {
      return this.className;
   }

   public String getFactoryClassName() {
      return this.classFactory;
   }

   public String getFactoryClassLocation() {
      return this.classFactoryLocation;
   }

   public RefAddr get(String var1) {
      int var2 = this.addrs.size();

      for(int var4 = 0; var4 < var2; ++var4) {
         RefAddr var3 = (RefAddr)this.addrs.elementAt(var4);
         if (var3.getType().compareTo(var1) == 0) {
            return var3;
         }
      }

      return null;
   }

   public RefAddr get(int var1) {
      return (RefAddr)this.addrs.elementAt(var1);
   }

   public Enumeration<RefAddr> getAll() {
      return this.addrs.elements();
   }

   public int size() {
      return this.addrs.size();
   }

   public void add(RefAddr var1) {
      this.addrs.addElement(var1);
   }

   public void add(int var1, RefAddr var2) {
      this.addrs.insertElementAt(var2, var1);
   }

   public Object remove(int var1) {
      Object var2 = this.addrs.elementAt(var1);
      this.addrs.removeElementAt(var1);
      return var2;
   }

   public void clear() {
      this.addrs.setSize(0);
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Reference) {
         Reference var2 = (Reference)var1;
         if (var2.className.equals(this.className) && var2.size() == this.size()) {
            Enumeration var3 = this.getAll();
            Enumeration var4 = var2.getAll();

            do {
               if (!var3.hasMoreElements()) {
                  return true;
               }
            } while(((RefAddr)var3.nextElement()).equals(var4.nextElement()));

            return false;
         }
      }

      return false;
   }

   public int hashCode() {
      int var1 = this.className.hashCode();

      for(Enumeration var2 = this.getAll(); var2.hasMoreElements(); var1 += ((RefAddr)var2.nextElement()).hashCode()) {
      }

      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("Reference Class Name: " + this.className + "\n");
      int var2 = this.addrs.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.append(this.get(var3).toString());
      }

      return var1.toString();
   }

   public Object clone() {
      Reference var1 = new Reference(this.className, this.classFactory, this.classFactoryLocation);
      Enumeration var2 = this.getAll();
      var1.addrs = new Vector();

      while(var2.hasMoreElements()) {
         var1.addrs.addElement(var2.nextElement());
      }

      return var1;
   }
}
