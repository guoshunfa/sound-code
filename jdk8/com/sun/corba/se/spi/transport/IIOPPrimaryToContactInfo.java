package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfo;
import java.util.List;

public interface IIOPPrimaryToContactInfo {
   void reset(ContactInfo var1);

   boolean hasNext(ContactInfo var1, ContactInfo var2, List var3);

   ContactInfo next(ContactInfo var1, ContactInfo var2, List var3);
}
