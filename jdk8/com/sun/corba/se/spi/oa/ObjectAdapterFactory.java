package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;

public interface ObjectAdapterFactory {
   void init(ORB var1);

   void shutdown(boolean var1);

   ObjectAdapter find(ObjectAdapterId var1);

   ORB getORB();
}
