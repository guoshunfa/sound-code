package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceFactoryHelper;

public class ObjectReferenceFactoryImpl extends ObjectReferenceProducerBase implements ObjectReferenceFactory, StreamableValue {
   private transient IORTemplateList iorTemplates;
   public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0";

   public ObjectReferenceFactoryImpl(InputStream var1) {
      super((ORB)((ORB)var1.orb()));
      this._read(var1);
   }

   public ObjectReferenceFactoryImpl(ORB var1, IORTemplateList var2) {
      super(var1);
      this.iorTemplates = var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjectReferenceFactoryImpl)) {
         return false;
      } else {
         ObjectReferenceFactoryImpl var2 = (ObjectReferenceFactoryImpl)var1;
         return this.iorTemplates != null && this.iorTemplates.equals(var2.iorTemplates);
      }
   }

   public int hashCode() {
      return this.iorTemplates.hashCode();
   }

   public String[] _truncatable_ids() {
      return new String[]{"IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0"};
   }

   public TypeCode _type() {
      return ObjectReferenceFactoryHelper.type();
   }

   public void _read(InputStream var1) {
      org.omg.CORBA_2_3.portable.InputStream var2 = (org.omg.CORBA_2_3.portable.InputStream)var1;
      this.iorTemplates = IORFactories.makeIORTemplateList(var2);
   }

   public void _write(OutputStream var1) {
      org.omg.CORBA_2_3.portable.OutputStream var2 = (org.omg.CORBA_2_3.portable.OutputStream)var1;
      this.iorTemplates.write(var2);
   }

   public IORFactory getIORFactory() {
      return this.iorTemplates;
   }

   public IORTemplateList getIORTemplateList() {
      return this.iorTemplates;
   }
}
