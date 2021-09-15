package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import java.util.List;

public interface IOR extends List, Writeable, MakeImmutable {
   ORB getORB();

   String getTypeId();

   Iterator iteratorById(int var1);

   String stringify();

   org.omg.IOP.IOR getIOPIOR();

   boolean isNil();

   boolean isEquivalent(IOR var1);

   IORTemplateList getIORTemplates();

   IIOPProfile getProfile();
}
