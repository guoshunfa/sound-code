package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;

public interface CorbaContactInfo extends ContactInfo {
   IOR getTargetIOR();

   IOR getEffectiveTargetIOR();

   IIOPProfile getEffectiveProfile();

   void setAddressingDisposition(short var1);

   short getAddressingDisposition();

   String getMonitoringName();
}
