package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

/** @deprecated */
@Deprecated
public final class PrincipalHolder implements Streamable {
   public Principal value;

   public PrincipalHolder() {
   }

   public PrincipalHolder(Principal var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = var1.read_Principal();
   }

   public void _write(OutputStream var1) {
      var1.write_Principal(this.value);
   }

   public TypeCode _type() {
      return ORB.init().get_primitive_tc(TCKind.tk_Principal);
   }
}
