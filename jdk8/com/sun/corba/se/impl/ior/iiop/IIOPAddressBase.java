package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import org.omg.CORBA_2_3.portable.OutputStream;

abstract class IIOPAddressBase implements IIOPAddress {
   protected short intToShort(int var1) {
      return var1 > 32767 ? (short)(var1 - 65536) : (short)var1;
   }

   protected int shortToInt(short var1) {
      return var1 < 0 ? var1 + 65536 : var1;
   }

   public void write(OutputStream var1) {
      var1.write_string(this.getHost());
      int var2 = this.getPort();
      var1.write_short(this.intToShort(var2));
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof IIOPAddress)) {
         return false;
      } else {
         IIOPAddress var2 = (IIOPAddress)var1;
         return this.getHost().equals(var2.getHost()) && this.getPort() == var2.getPort();
      }
   }

   public int hashCode() {
      return this.getHost().hashCode() ^ this.getPort();
   }

   public String toString() {
      return "IIOPAddress[" + this.getHost() + "," + this.getPort() + "]";
   }
}
