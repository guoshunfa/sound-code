package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public interface CorbaContactInfoListFactory {
   void setORB(ORB var1);

   CorbaContactInfoList create(IOR var1);
}
