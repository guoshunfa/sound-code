package org.omg.PortableServer.ServantLocatorPackage;

import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CookieHolder implements Streamable {
   public Object value;

   public CookieHolder() {
   }

   public CookieHolder(Object var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      throw new NO_IMPLEMENT();
   }

   public void _write(OutputStream var1) {
      throw new NO_IMPLEMENT();
   }

   public TypeCode _type() {
      throw new NO_IMPLEMENT();
   }
}
