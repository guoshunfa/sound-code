package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import org.omg.CORBA_2_3.portable.OutputStream;

public class AlternateIIOPAddressComponentImpl extends TaggedComponentBase implements AlternateIIOPAddressComponent {
   private IIOPAddress addr;

   public boolean equals(Object var1) {
      if (!(var1 instanceof AlternateIIOPAddressComponentImpl)) {
         return false;
      } else {
         AlternateIIOPAddressComponentImpl var2 = (AlternateIIOPAddressComponentImpl)var1;
         return this.addr.equals(var2.addr);
      }
   }

   public int hashCode() {
      return this.addr.hashCode();
   }

   public String toString() {
      return "AlternateIIOPAddressComponentImpl[addr=" + this.addr + "]";
   }

   public AlternateIIOPAddressComponentImpl(IIOPAddress var1) {
      this.addr = var1;
   }

   public IIOPAddress getAddress() {
      return this.addr;
   }

   public void writeContents(OutputStream var1) {
      this.addr.write(var1);
   }

   public int getId() {
      return 3;
   }
}
