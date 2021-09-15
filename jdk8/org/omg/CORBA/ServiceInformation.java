package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceInformation implements IDLEntity {
   public int[] service_options;
   public ServiceDetail[] service_details;

   public ServiceInformation() {
   }

   public ServiceInformation(int[] var1, ServiceDetail[] var2) {
      this.service_options = var1;
      this.service_details = var2;
   }
}
