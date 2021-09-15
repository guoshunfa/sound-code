package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.impl.ior.IORTemplateImpl;
import com.sun.corba.se.impl.ior.IORTemplateListImpl;
import com.sun.corba.se.impl.ior.ObjectIdImpl;
import com.sun.corba.se.impl.ior.ObjectKeyFactoryImpl;
import com.sun.corba.se.impl.ior.ObjectKeyImpl;
import com.sun.corba.se.impl.ior.ObjectReferenceFactoryImpl;
import com.sun.corba.se.impl.ior.ObjectReferenceProducerBase;
import com.sun.corba.se.impl.ior.ObjectReferenceTemplateImpl;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public class IORFactories {
   private IORFactories() {
   }

   public static ObjectId makeObjectId(byte[] var0) {
      return new ObjectIdImpl(var0);
   }

   public static ObjectKey makeObjectKey(ObjectKeyTemplate var0, ObjectId var1) {
      return new ObjectKeyImpl(var0, var1);
   }

   public static IOR makeIOR(ORB var0, String var1) {
      return new IORImpl(var0, var1);
   }

   public static IOR makeIOR(ORB var0) {
      return new IORImpl(var0);
   }

   public static IOR makeIOR(InputStream var0) {
      return new IORImpl(var0);
   }

   public static IORTemplate makeIORTemplate(ObjectKeyTemplate var0) {
      return new IORTemplateImpl(var0);
   }

   public static IORTemplate makeIORTemplate(InputStream var0) {
      return new IORTemplateImpl(var0);
   }

   public static IORTemplateList makeIORTemplateList() {
      return new IORTemplateListImpl();
   }

   public static IORTemplateList makeIORTemplateList(InputStream var0) {
      return new IORTemplateListImpl(var0);
   }

   public static IORFactory getIORFactory(ObjectReferenceTemplate var0) {
      if (var0 instanceof ObjectReferenceTemplateImpl) {
         ObjectReferenceTemplateImpl var1 = (ObjectReferenceTemplateImpl)var0;
         return var1.getIORFactory();
      } else {
         throw new BAD_PARAM();
      }
   }

   public static IORTemplateList getIORTemplateList(ObjectReferenceFactory var0) {
      if (var0 instanceof ObjectReferenceProducerBase) {
         ObjectReferenceProducerBase var1 = (ObjectReferenceProducerBase)var0;
         return var1.getIORTemplateList();
      } else {
         throw new BAD_PARAM();
      }
   }

   public static ObjectReferenceTemplate makeObjectReferenceTemplate(ORB var0, IORTemplate var1) {
      return new ObjectReferenceTemplateImpl(var0, var1);
   }

   public static ObjectReferenceFactory makeObjectReferenceFactory(ORB var0, IORTemplateList var1) {
      return new ObjectReferenceFactoryImpl(var0, var1);
   }

   public static ObjectKeyFactory makeObjectKeyFactory(ORB var0) {
      return new ObjectKeyFactoryImpl(var0);
   }

   public static IOR getIOR(Object var0) {
      return ORBUtility.getIOR(var0);
   }

   public static Object makeObjectReference(IOR var0) {
      return ORBUtility.makeObjectReference(var0);
   }

   public static void registerValueFactories(ORB var0) {
      ValueFactory var1 = new ValueFactory() {
         public Serializable read_value(InputStream var1) {
            return new ObjectReferenceTemplateImpl(var1);
         }
      };
      var0.register_value_factory("IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0", var1);
      var1 = new ValueFactory() {
         public Serializable read_value(InputStream var1) {
            return new ObjectReferenceFactoryImpl(var1);
         }
      };
      var0.register_value_factory("IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0", var1);
   }
}
