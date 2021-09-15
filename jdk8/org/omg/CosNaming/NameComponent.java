package org.omg.CosNaming;

import org.omg.CORBA.portable.IDLEntity;

public final class NameComponent implements IDLEntity {
   public String id = null;
   public String kind = null;

   public NameComponent() {
   }

   public NameComponent(String var1, String var2) {
      this.id = var1;
      this.kind = var2;
   }
}
