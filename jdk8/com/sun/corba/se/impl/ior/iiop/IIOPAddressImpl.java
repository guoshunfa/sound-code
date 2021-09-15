package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public final class IIOPAddressImpl extends IIOPAddressBase {
   private ORB orb;
   private IORSystemException wrapper;
   private String host;
   private int port;

   public IIOPAddressImpl(ORB var1, String var2, int var3) {
      this.orb = var1;
      this.wrapper = IORSystemException.get(var1, "oa.ior");
      if (var3 >= 0 && var3 <= 65535) {
         this.host = var2;
         this.port = var3;
      } else {
         throw this.wrapper.badIiopAddressPort(new Integer(var3));
      }
   }

   public IIOPAddressImpl(InputStream var1) {
      this.host = var1.read_string();
      short var2 = var1.read_short();
      this.port = this.shortToInt(var2);
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }
}
