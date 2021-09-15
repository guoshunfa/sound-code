package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectHolder implements Streamable {
   public Object value;

   public ObjectHolder() {
   }

   public ObjectHolder(Object var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = var1.read_Object();
   }

   public void _write(OutputStream var1) {
      var1.write_Object(this.value);
   }

   public TypeCode _type() {
      return ORB.init().get_primitive_tc(TCKind.tk_objref);
   }
}
