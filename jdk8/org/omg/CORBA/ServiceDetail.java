package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceDetail implements IDLEntity {
   public int service_detail_type;
   public byte[] service_detail;

   public ServiceDetail() {
   }

   public ServiceDetail(int var1, byte[] var2) {
      this.service_detail_type = var1;
      this.service_detail = var2;
   }
}
