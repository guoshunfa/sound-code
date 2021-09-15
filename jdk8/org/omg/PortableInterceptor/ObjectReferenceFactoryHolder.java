package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceFactoryHolder implements Streamable {
   public ObjectReferenceFactory value = null;

   public ObjectReferenceFactoryHolder() {
   }

   public ObjectReferenceFactoryHolder(ObjectReferenceFactory var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ObjectReferenceFactoryHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ObjectReferenceFactoryHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ObjectReferenceFactoryHelper.type();
   }
}
