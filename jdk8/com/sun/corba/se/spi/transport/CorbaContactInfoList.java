package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;

public interface CorbaContactInfoList extends ContactInfoList {
   void setTargetIOR(IOR var1);

   IOR getTargetIOR();

   void setEffectiveTargetIOR(IOR var1);

   IOR getEffectiveTargetIOR();

   LocalClientRequestDispatcher getLocalClientRequestDispatcher();

   int hashCode();
}
