package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateHolder implements Streamable {
   public ObjectReferenceTemplate value = null;

   public ObjectReferenceTemplateHolder() {
   }

   public ObjectReferenceTemplateHolder(ObjectReferenceTemplate var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ObjectReferenceTemplateHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ObjectReferenceTemplateHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ObjectReferenceTemplateHelper.type();
   }
}
