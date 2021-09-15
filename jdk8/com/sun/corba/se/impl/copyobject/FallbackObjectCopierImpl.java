package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.spi.copyobject.ObjectCopier;
import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;

public class FallbackObjectCopierImpl implements ObjectCopier {
   private ObjectCopier first;
   private ObjectCopier second;

   public FallbackObjectCopierImpl(ObjectCopier var1, ObjectCopier var2) {
      this.first = var1;
      this.second = var2;
   }

   public Object copy(Object var1) throws ReflectiveCopyException {
      try {
         return this.first.copy(var1);
      } catch (ReflectiveCopyException var3) {
         return this.second.copy(var1);
      }
   }
}
