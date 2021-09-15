package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class RequestPartitioningComponentImpl extends TaggedComponentBase implements RequestPartitioningComponent {
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("oa.ior");
   private int partitionToUse;

   public boolean equals(Object var1) {
      if (!(var1 instanceof RequestPartitioningComponentImpl)) {
         return false;
      } else {
         RequestPartitioningComponentImpl var2 = (RequestPartitioningComponentImpl)var1;
         return this.partitionToUse == var2.partitionToUse;
      }
   }

   public int hashCode() {
      return this.partitionToUse;
   }

   public String toString() {
      return "RequestPartitioningComponentImpl[partitionToUse=" + this.partitionToUse + "]";
   }

   public RequestPartitioningComponentImpl() {
      this.partitionToUse = 0;
   }

   public RequestPartitioningComponentImpl(int var1) {
      if (var1 >= 0 && var1 <= 63) {
         this.partitionToUse = var1;
      } else {
         throw wrapper.invalidRequestPartitioningComponentValue(new Integer(var1), new Integer(0), new Integer(63));
      }
   }

   public int getRequestPartitioningId() {
      return this.partitionToUse;
   }

   public void writeContents(OutputStream var1) {
      var1.write_ulong(this.partitionToUse);
   }

   public int getId() {
      return 1398099457;
   }
}
