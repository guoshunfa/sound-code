package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.ior.iiop.AlternateIIOPAddressComponentImpl;
import com.sun.corba.se.impl.ior.iiop.CodeSetsComponentImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPAddressImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPProfileImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl;
import com.sun.corba.se.impl.ior.iiop.JavaCodebaseComponentImpl;
import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
import com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl;
import com.sun.corba.se.impl.ior.iiop.ORBTypeComponentImpl;
import com.sun.corba.se.impl.ior.iiop.RequestPartitioningComponentImpl;
import com.sun.corba.se.spi.ior.EncapsulationFactoryBase;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedProfile;

public abstract class IIOPFactories {
   private IIOPFactories() {
   }

   public static IdentifiableFactory makeRequestPartitioningComponentFactory() {
      return new EncapsulationFactoryBase(1398099457) {
         public Identifiable readContents(InputStream var1) {
            int var2 = var1.read_ulong();
            RequestPartitioningComponentImpl var3 = new RequestPartitioningComponentImpl(var2);
            return var3;
         }
      };
   }

   public static RequestPartitioningComponent makeRequestPartitioningComponent(int var0) {
      return new RequestPartitioningComponentImpl(var0);
   }

   public static IdentifiableFactory makeAlternateIIOPAddressComponentFactory() {
      return new EncapsulationFactoryBase(3) {
         public Identifiable readContents(InputStream var1) {
            IIOPAddressImpl var2 = new IIOPAddressImpl(var1);
            AlternateIIOPAddressComponentImpl var3 = new AlternateIIOPAddressComponentImpl(var2);
            return var3;
         }
      };
   }

   public static AlternateIIOPAddressComponent makeAlternateIIOPAddressComponent(IIOPAddress var0) {
      return new AlternateIIOPAddressComponentImpl(var0);
   }

   public static IdentifiableFactory makeCodeSetsComponentFactory() {
      return new EncapsulationFactoryBase(1) {
         public Identifiable readContents(InputStream var1) {
            return new CodeSetsComponentImpl(var1);
         }
      };
   }

   public static CodeSetsComponent makeCodeSetsComponent(ORB var0) {
      return new CodeSetsComponentImpl(var0);
   }

   public static IdentifiableFactory makeJavaCodebaseComponentFactory() {
      return new EncapsulationFactoryBase(25) {
         public Identifiable readContents(InputStream var1) {
            String var2 = var1.read_string();
            JavaCodebaseComponentImpl var3 = new JavaCodebaseComponentImpl(var2);
            return var3;
         }
      };
   }

   public static JavaCodebaseComponent makeJavaCodebaseComponent(String var0) {
      return new JavaCodebaseComponentImpl(var0);
   }

   public static IdentifiableFactory makeORBTypeComponentFactory() {
      return new EncapsulationFactoryBase(0) {
         public Identifiable readContents(InputStream var1) {
            int var2 = var1.read_ulong();
            ORBTypeComponentImpl var3 = new ORBTypeComponentImpl(var2);
            return var3;
         }
      };
   }

   public static ORBTypeComponent makeORBTypeComponent(int var0) {
      return new ORBTypeComponentImpl(var0);
   }

   public static IdentifiableFactory makeMaxStreamFormatVersionComponentFactory() {
      return new EncapsulationFactoryBase(38) {
         public Identifiable readContents(InputStream var1) {
            byte var2 = var1.read_octet();
            MaxStreamFormatVersionComponentImpl var3 = new MaxStreamFormatVersionComponentImpl(var2);
            return var3;
         }
      };
   }

   public static MaxStreamFormatVersionComponent makeMaxStreamFormatVersionComponent() {
      return new MaxStreamFormatVersionComponentImpl();
   }

   public static IdentifiableFactory makeJavaSerializationComponentFactory() {
      return new EncapsulationFactoryBase(1398099458) {
         public Identifiable readContents(InputStream var1) {
            byte var2 = var1.read_octet();
            JavaSerializationComponent var3 = new JavaSerializationComponent(var2);
            return var3;
         }
      };
   }

   public static JavaSerializationComponent makeJavaSerializationComponent() {
      return JavaSerializationComponent.singleton();
   }

   public static IdentifiableFactory makeIIOPProfileFactory() {
      return new EncapsulationFactoryBase(0) {
         public Identifiable readContents(InputStream var1) {
            IIOPProfileImpl var2 = new IIOPProfileImpl(var1);
            return var2;
         }
      };
   }

   public static IIOPProfile makeIIOPProfile(ORB var0, ObjectKeyTemplate var1, ObjectId var2, IIOPProfileTemplate var3) {
      return new IIOPProfileImpl(var0, var1, var2, var3);
   }

   public static IIOPProfile makeIIOPProfile(ORB var0, TaggedProfile var1) {
      return new IIOPProfileImpl(var0, var1);
   }

   public static IdentifiableFactory makeIIOPProfileTemplateFactory() {
      return new EncapsulationFactoryBase(0) {
         public Identifiable readContents(InputStream var1) {
            IIOPProfileTemplateImpl var2 = new IIOPProfileTemplateImpl(var1);
            return var2;
         }
      };
   }

   public static IIOPProfileTemplate makeIIOPProfileTemplate(ORB var0, GIOPVersion var1, IIOPAddress var2) {
      return new IIOPProfileTemplateImpl(var0, var1, var2);
   }

   public static IIOPAddress makeIIOPAddress(ORB var0, String var1, int var2) {
      return new IIOPAddressImpl(var0, var1, var2);
   }

   public static IIOPAddress makeIIOPAddress(InputStream var0) {
      return new IIOPAddressImpl(var0);
   }
}
