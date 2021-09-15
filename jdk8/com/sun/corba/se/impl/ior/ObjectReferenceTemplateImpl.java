package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ObjectReferenceTemplateHelper;

public class ObjectReferenceTemplateImpl extends ObjectReferenceProducerBase implements ObjectReferenceTemplate, StreamableValue {
   private transient IORTemplate iorTemplate;
   public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0";

   public ObjectReferenceTemplateImpl(InputStream var1) {
      super((ORB)((ORB)var1.orb()));
      this._read(var1);
   }

   public ObjectReferenceTemplateImpl(ORB var1, IORTemplate var2) {
      super(var1);
      this.iorTemplate = var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjectReferenceTemplateImpl)) {
         return false;
      } else {
         ObjectReferenceTemplateImpl var2 = (ObjectReferenceTemplateImpl)var1;
         return this.iorTemplate != null && this.iorTemplate.equals(var2.iorTemplate);
      }
   }

   public int hashCode() {
      return this.iorTemplate.hashCode();
   }

   public String[] _truncatable_ids() {
      return new String[]{"IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0"};
   }

   public TypeCode _type() {
      return ObjectReferenceTemplateHelper.type();
   }

   public void _read(InputStream var1) {
      org.omg.CORBA_2_3.portable.InputStream var2 = (org.omg.CORBA_2_3.portable.InputStream)var1;
      this.iorTemplate = IORFactories.makeIORTemplate(var2);
      this.orb = (ORB)((ORB)var2.orb());
   }

   public void _write(OutputStream var1) {
      org.omg.CORBA_2_3.portable.OutputStream var2 = (org.omg.CORBA_2_3.portable.OutputStream)var1;
      this.iorTemplate.write(var2);
   }

   public String server_id() {
      int var1 = this.iorTemplate.getObjectKeyTemplate().getServerId();
      return Integer.toString(var1);
   }

   public String orb_id() {
      return this.iorTemplate.getObjectKeyTemplate().getORBId();
   }

   public String[] adapter_name() {
      ObjectAdapterId var1 = this.iorTemplate.getObjectKeyTemplate().getObjectAdapterId();
      return var1.getAdapterName();
   }

   public IORFactory getIORFactory() {
      return this.iorTemplate;
   }

   public IORTemplateList getIORTemplateList() {
      IORTemplateList var1 = IORFactories.makeIORTemplateList();
      var1.add(this.iorTemplate);
      var1.makeImmutable();
      return var1;
   }
}
