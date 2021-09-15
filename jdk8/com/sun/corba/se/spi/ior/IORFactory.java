package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;

public interface IORFactory extends Writeable, MakeImmutable {
   IOR makeIOR(ORB var1, String var2, ObjectId var3);

   boolean isEquivalent(IORFactory var1);
}
