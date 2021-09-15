package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundReasonHolder implements Streamable {
   public NotFoundReason value = null;

   public NotFoundReasonHolder() {
   }

   public NotFoundReasonHolder(NotFoundReason var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NotFoundReasonHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NotFoundReasonHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NotFoundReasonHelper.type();
   }
}
