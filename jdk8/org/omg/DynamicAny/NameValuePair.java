package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.portable.IDLEntity;

public final class NameValuePair implements IDLEntity {
   public String id = null;
   public Any value = null;

   public NameValuePair() {
   }

   public NameValuePair(String var1, Any var2) {
      this.id = var1;
      this.value = var2;
   }
}
