package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class AlreadyBoundHolder implements Streamable {
   public AlreadyBound value = null;

   public AlreadyBoundHolder() {
   }

   public AlreadyBoundHolder(AlreadyBound var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = AlreadyBoundHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      AlreadyBoundHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return AlreadyBoundHelper.type();
   }
}
