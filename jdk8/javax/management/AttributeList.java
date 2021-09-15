package javax.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AttributeList extends ArrayList<Object> {
   private transient volatile boolean typeSafe;
   private transient volatile boolean tainted;
   private static final long serialVersionUID = -4077085769279709076L;

   public AttributeList() {
   }

   public AttributeList(int var1) {
      super(var1);
   }

   public AttributeList(AttributeList var1) {
      super(var1);
   }

   public AttributeList(List<Attribute> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null parameter");
      } else {
         this.adding((Collection)var1);
         super.addAll(var1);
      }
   }

   public List<Attribute> asList() {
      this.typeSafe = true;
      if (this.tainted) {
         this.adding((Collection)this);
      }

      return this;
   }

   public void add(Attribute var1) {
      super.add(var1);
   }

   public void add(int var1, Attribute var2) {
      try {
         super.add(var1, var2);
      } catch (IndexOutOfBoundsException var4) {
         throw new RuntimeOperationsException(var4, "The specified index is out of range");
      }
   }

   public void set(int var1, Attribute var2) {
      try {
         super.set(var1, var2);
      } catch (IndexOutOfBoundsException var4) {
         throw new RuntimeOperationsException(var4, "The specified index is out of range");
      }
   }

   public boolean addAll(AttributeList var1) {
      return super.addAll(var1);
   }

   public boolean addAll(int var1, AttributeList var2) {
      try {
         return super.addAll(var1, var2);
      } catch (IndexOutOfBoundsException var4) {
         throw new RuntimeOperationsException(var4, "The specified index is out of range");
      }
   }

   public boolean add(Object var1) {
      this.adding(var1);
      return super.add(var1);
   }

   public void add(int var1, Object var2) {
      this.adding(var2);
      super.add(var1, var2);
   }

   public boolean addAll(Collection<?> var1) {
      this.adding(var1);
      return super.addAll(var1);
   }

   public boolean addAll(int var1, Collection<?> var2) {
      this.adding(var2);
      return super.addAll(var1, var2);
   }

   public Object set(int var1, Object var2) {
      this.adding(var2);
      return super.set(var1, var2);
   }

   private void adding(Object var1) {
      if (var1 != null && !(var1 instanceof Attribute)) {
         if (this.typeSafe) {
            throw new IllegalArgumentException("Not an Attribute: " + var1);
         } else {
            this.tainted = true;
         }
      }
   }

   private void adding(Collection<?> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         this.adding(var3);
      }

   }
}
