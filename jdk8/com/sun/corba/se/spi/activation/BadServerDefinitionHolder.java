package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BadServerDefinitionHolder implements Streamable {
   public BadServerDefinition value = null;

   public BadServerDefinitionHolder() {
   }

   public BadServerDefinitionHolder(BadServerDefinition var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = BadServerDefinitionHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      BadServerDefinitionHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return BadServerDefinitionHelper.type();
   }
}
