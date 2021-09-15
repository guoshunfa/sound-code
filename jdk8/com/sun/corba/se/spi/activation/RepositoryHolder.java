package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class RepositoryHolder implements Streamable {
   public Repository value = null;

   public RepositoryHolder() {
   }

   public RepositoryHolder(Repository var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = RepositoryHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      RepositoryHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return RepositoryHelper.type();
   }
}
