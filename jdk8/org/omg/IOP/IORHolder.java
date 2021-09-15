package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class IORHolder implements Streamable {
   public IOR value = null;

   public IORHolder() {
   }

   public IORHolder(IOR var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = IORHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      IORHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return IORHelper.type();
   }
}
