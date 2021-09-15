package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ValueBaseHolder implements Streamable {
   public Serializable value;

   public ValueBaseHolder() {
   }

   public ValueBaseHolder(Serializable var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ((org.omg.CORBA_2_3.portable.InputStream)var1).read_value();
   }

   public void _write(OutputStream var1) {
      ((org.omg.CORBA_2_3.portable.OutputStream)var1).write_value(this.value);
   }

   public TypeCode _type() {
      return ORB.init().get_primitive_tc(TCKind.tk_value);
   }
}
