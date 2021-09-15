package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyErrorHolder implements Streamable {
   public PolicyError value = null;

   public PolicyErrorHolder() {
   }

   public PolicyErrorHolder(PolicyError var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = PolicyErrorHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      PolicyErrorHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return PolicyErrorHelper.type();
   }
}
