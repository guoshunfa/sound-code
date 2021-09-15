package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class UnknownUserExceptionHolder implements Streamable {
   public UnknownUserException value = null;

   public UnknownUserExceptionHolder() {
   }

   public UnknownUserExceptionHolder(UnknownUserException var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = UnknownUserExceptionHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      UnknownUserExceptionHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return UnknownUserExceptionHelper.type();
   }
}
