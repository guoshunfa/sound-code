package javax.management.relation;

import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RoleUnresolvedList extends ArrayList<Object> {
   private transient boolean typeSafe;
   private transient boolean tainted;
   private static final long serialVersionUID = 4054902803091433324L;

   public RoleUnresolvedList() {
   }

   public RoleUnresolvedList(int var1) {
      super(var1);
   }

   public RoleUnresolvedList(List<RoleUnresolved> var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null parameter");
      } else {
         checkTypeSafe((Collection)var1);
         super.addAll(var1);
      }
   }

   public List<RoleUnresolved> asList() {
      if (!this.typeSafe) {
         if (this.tainted) {
            checkTypeSafe((Collection)this);
         }

         this.typeSafe = true;
      }

      return (List)Util.cast(this);
   }

   public void add(RoleUnresolved var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter";
         throw new IllegalArgumentException(var2);
      } else {
         super.add(var1);
      }
   }

   public void add(int var1, RoleUnresolved var2) throws IllegalArgumentException, IndexOutOfBoundsException {
      if (var2 == null) {
         String var3 = "Invalid parameter";
         throw new IllegalArgumentException(var3);
      } else {
         super.add(var1, var2);
      }
   }

   public void set(int var1, RoleUnresolved var2) throws IllegalArgumentException, IndexOutOfBoundsException {
      if (var2 == null) {
         String var3 = "Invalid parameter";
         throw new IllegalArgumentException(var3);
      } else {
         super.set(var1, var2);
      }
   }

   public boolean addAll(RoleUnresolvedList var1) throws IndexOutOfBoundsException {
      return var1 == null ? true : super.addAll(var1);
   }

   public boolean addAll(int var1, RoleUnresolvedList var2) throws IllegalArgumentException, IndexOutOfBoundsException {
      if (var2 == null) {
         String var3 = "Invalid parameter";
         throw new IllegalArgumentException(var3);
      } else {
         return super.addAll(var1, var2);
      }
   }

   public boolean add(Object var1) {
      if (!this.tainted) {
         this.tainted = isTainted(var1);
      }

      if (this.typeSafe) {
         checkTypeSafe(var1);
      }

      return super.add(var1);
   }

   public void add(int var1, Object var2) {
      if (!this.tainted) {
         this.tainted = isTainted(var2);
      }

      if (this.typeSafe) {
         checkTypeSafe(var2);
      }

      super.add(var1, var2);
   }

   public boolean addAll(Collection<?> var1) {
      if (!this.tainted) {
         this.tainted = isTainted(var1);
      }

      if (this.typeSafe) {
         checkTypeSafe(var1);
      }

      return super.addAll(var1);
   }

   public boolean addAll(int var1, Collection<?> var2) {
      if (!this.tainted) {
         this.tainted = isTainted(var2);
      }

      if (this.typeSafe) {
         checkTypeSafe(var2);
      }

      return super.addAll(var1, var2);
   }

   public Object set(int var1, Object var2) {
      if (!this.tainted) {
         this.tainted = isTainted(var2);
      }

      if (this.typeSafe) {
         checkTypeSafe(var2);
      }

      return super.set(var1, var2);
   }

   private static void checkTypeSafe(Object var0) {
      try {
         RoleUnresolved var3 = (RoleUnresolved)var0;
      } catch (ClassCastException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   private static void checkTypeSafe(Collection<?> var0) {
      try {
         RoleUnresolved var1;
         Object var3;
         for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 = (RoleUnresolved)var3) {
            var3 = var2.next();
         }

      } catch (ClassCastException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   private static boolean isTainted(Object var0) {
      try {
         checkTypeSafe(var0);
         return false;
      } catch (IllegalArgumentException var2) {
         return true;
      }
   }

   private static boolean isTainted(Collection<?> var0) {
      try {
         checkTypeSafe(var0);
         return false;
      } catch (IllegalArgumentException var2) {
         return true;
      }
   }
}
