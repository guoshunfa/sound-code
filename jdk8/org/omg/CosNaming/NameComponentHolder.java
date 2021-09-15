package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameComponentHolder implements Streamable {
   public NameComponent value = null;

   public NameComponentHolder() {
   }

   public NameComponentHolder(NameComponent var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NameComponentHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NameComponentHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NameComponentHelper.type();
   }
}
