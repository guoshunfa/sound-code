package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultIORToSocketInfoImpl implements IORToSocketInfo {
   public List getSocketInfo(IOR var1) {
      ArrayList var3 = new ArrayList();
      IIOPProfileTemplate var4 = (IIOPProfileTemplate)var1.getProfile().getTaggedProfileTemplate();
      IIOPAddress var5 = var4.getPrimaryAddress();
      String var6 = var5.getHost().toLowerCase();
      int var7 = var5.getPort();
      SocketInfo var2 = this.createSocketInfo(var6, var7);
      var3.add(var2);
      Iterator var8 = var4.iteratorById(3);

      while(var8.hasNext()) {
         AlternateIIOPAddressComponent var9 = (AlternateIIOPAddressComponent)var8.next();
         var6 = var9.getAddress().getHost().toLowerCase();
         var7 = var9.getAddress().getPort();
         var2 = this.createSocketInfo(var6, var7);
         var3.add(var2);
      }

      return var3;
   }

   private SocketInfo createSocketInfo(final String var1, final int var2) {
      return new SocketInfo() {
         public String getType() {
            return "IIOP_CLEAR_TEXT";
         }

         public String getHost() {
            return var1;
         }

         public int getPort() {
            return var2;
         }
      };
   }
}
