package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.NameComponent;

public class InternalBindingKey {
   public NameComponent name;
   private int idLen;
   private int kindLen;
   private int hashVal;

   public InternalBindingKey() {
   }

   public InternalBindingKey(NameComponent var1) {
      this.idLen = 0;
      this.kindLen = 0;
      this.setup(var1);
   }

   protected void setup(NameComponent var1) {
      this.name = var1;
      if (this.name.id != null) {
         this.idLen = this.name.id.length();
      }

      if (this.name.kind != null) {
         this.kindLen = this.name.kind.length();
      }

      this.hashVal = 0;
      if (this.idLen > 0) {
         this.hashVal += this.name.id.hashCode();
      }

      if (this.kindLen > 0) {
         this.hashVal += this.name.kind.hashCode();
      }

   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 instanceof InternalBindingKey) {
         InternalBindingKey var2 = (InternalBindingKey)var1;
         if (this.idLen == var2.idLen && this.kindLen == var2.kindLen) {
            if (this.idLen > 0 && !this.name.id.equals(var2.name.id)) {
               return false;
            } else {
               return this.kindLen <= 0 || this.name.kind.equals(var2.name.kind);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.hashVal;
   }
}
