package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateSeqHolder implements Streamable {
   public ObjectReferenceTemplate[] value = null;

   public ObjectReferenceTemplateSeqHolder() {
   }

   public ObjectReferenceTemplateSeqHolder(ObjectReferenceTemplate[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ObjectReferenceTemplateSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ObjectReferenceTemplateSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ObjectReferenceTemplateSeqHelper.type();
   }
}
