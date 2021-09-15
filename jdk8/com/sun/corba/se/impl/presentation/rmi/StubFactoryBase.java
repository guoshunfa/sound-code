package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.Object;

public abstract class StubFactoryBase implements PresentationManager.StubFactory {
   private String[] typeIds = null;
   protected final PresentationManager.ClassData classData;

   protected StubFactoryBase(PresentationManager.ClassData var1) {
      this.classData = var1;
   }

   public synchronized String[] getTypeIds() {
      if (this.typeIds == null) {
         if (this.classData == null) {
            Object var1 = this.makeStub();
            this.typeIds = StubAdapter.getTypeIds(var1);
         } else {
            this.typeIds = this.classData.getTypeIds();
         }
      }

      return this.typeIds;
   }
}
