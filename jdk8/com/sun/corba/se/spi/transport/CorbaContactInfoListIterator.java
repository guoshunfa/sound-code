package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfoListIterator;
import com.sun.corba.se.spi.ior.IOR;

public interface CorbaContactInfoListIterator extends ContactInfoListIterator {
   void reportAddrDispositionRetry(CorbaContactInfo var1, short var2);

   void reportRedirect(CorbaContactInfo var1, IOR var2);
}
