package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CosNaming.NameComponent;

public class InternalBindingKey implements Serializable {
   private static final long serialVersionUID = -5410796631793704055L;
   public String id;
   public String kind;

   public InternalBindingKey() {
   }

   public InternalBindingKey(NameComponent var1) {
      this.setup(var1);
   }

   protected void setup(NameComponent var1) {
      this.id = var1.id;
      this.kind = var1.kind;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof InternalBindingKey)) {
         return false;
      } else {
         InternalBindingKey var2 = (InternalBindingKey)var1;
         if (this.id != null && var2.id != null) {
            if (this.id.length() != var2.id.length()) {
               return false;
            }

            if (this.id.length() > 0 && !this.id.equals(var2.id)) {
               return false;
            }
         } else if (this.id == null && var2.id != null || this.id != null && var2.id == null) {
            return false;
         }

         if (this.kind != null && var2.kind != null) {
            if (this.kind.length() != var2.kind.length()) {
               return false;
            }

            if (this.kind.length() > 0 && !this.kind.equals(var2.kind)) {
               return false;
            }
         } else if (this.kind == null && var2.kind != null || this.kind != null && var2.kind == null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = 0;
      if (this.id.length() > 0) {
         var1 += this.id.hashCode();
      }

      if (this.kind.length() > 0) {
         var1 += this.kind.hashCode();
      }

      return var1;
   }
}
