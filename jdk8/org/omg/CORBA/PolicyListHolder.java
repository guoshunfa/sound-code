package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyListHolder implements Streamable {
   public Policy[] value = null;

   public PolicyListHolder() {
   }

   public PolicyListHolder(Policy[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = PolicyListHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      PolicyListHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return PolicyListHelper.type();
   }
}
