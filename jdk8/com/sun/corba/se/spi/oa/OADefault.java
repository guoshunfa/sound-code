package com.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.spi.orb.ORB;

public class OADefault {
   public static ObjectAdapterFactory makePOAFactory(ORB var0) {
      POAFactory var1 = new POAFactory();
      var1.init(var0);
      return var1;
   }

   public static ObjectAdapterFactory makeTOAFactory(ORB var0) {
      TOAFactory var1 = new TOAFactory();
      var1.init(var0);
      return var1;
   }
}
