package com.sun.corba.se.spi.copyobject;

import com.sun.corba.se.impl.copyobject.FallbackObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.JavaStreamObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.ORBStreamObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.ReferenceObjectCopierImpl;
import com.sun.corba.se.spi.orb.ORB;

public abstract class CopyobjectDefaults {
   private static final ObjectCopier referenceObjectCopier = new ReferenceObjectCopierImpl();
   private static ObjectCopierFactory referenceObjectCopierFactory = new ObjectCopierFactory() {
      public ObjectCopier make() {
         return CopyobjectDefaults.referenceObjectCopier;
      }
   };

   private CopyobjectDefaults() {
   }

   public static ObjectCopierFactory makeORBStreamObjectCopierFactory(final ORB var0) {
      return new ObjectCopierFactory() {
         public ObjectCopier make() {
            return new ORBStreamObjectCopierImpl(var0);
         }
      };
   }

   public static ObjectCopierFactory makeJavaStreamObjectCopierFactory(final ORB var0) {
      return new ObjectCopierFactory() {
         public ObjectCopier make() {
            return new JavaStreamObjectCopierImpl(var0);
         }
      };
   }

   public static ObjectCopierFactory getReferenceObjectCopierFactory() {
      return referenceObjectCopierFactory;
   }

   public static ObjectCopierFactory makeFallbackObjectCopierFactory(final ObjectCopierFactory var0, final ObjectCopierFactory var1) {
      return new ObjectCopierFactory() {
         public ObjectCopier make() {
            ObjectCopier var1x = var0.make();
            ObjectCopier var2 = var1.make();
            return new FallbackObjectCopierImpl(var1x, var2);
         }
      };
   }
}
