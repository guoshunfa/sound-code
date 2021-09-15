package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class NameValuePair implements IDLEntity {
   public String id;
   public Any value;

   public NameValuePair() {
   }

   public NameValuePair(String var1, Any var2) {
      this.id = var1;
      this.value = var2;
   }
}
