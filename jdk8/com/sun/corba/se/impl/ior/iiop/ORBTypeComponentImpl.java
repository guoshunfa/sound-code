package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.ORBTypeComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBTypeComponentImpl extends TaggedComponentBase implements ORBTypeComponent {
   private int ORBType;

   public boolean equals(Object var1) {
      if (!(var1 instanceof ORBTypeComponentImpl)) {
         return false;
      } else {
         ORBTypeComponentImpl var2 = (ORBTypeComponentImpl)var1;
         return this.ORBType == var2.ORBType;
      }
   }

   public int hashCode() {
      return this.ORBType;
   }

   public String toString() {
      return "ORBTypeComponentImpl[ORBType=" + this.ORBType + "]";
   }

   public ORBTypeComponentImpl(int var1) {
      this.ORBType = var1;
   }

   public int getId() {
      return 0;
   }

   public int getORBType() {
      return this.ORBType;
   }

   public void writeContents(OutputStream var1) {
      var1.write_ulong(this.ORBType);
   }
}
