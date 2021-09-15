package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyHolder implements Streamable {
   public Policy value = null;

   public PolicyHolder() {
   }

   public PolicyHolder(Policy var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = PolicyHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      PolicyHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return PolicyHelper.type();
   }
}
