package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;

public interface ReplyMessage extends Message, LocateReplyOrReplyMessage {
   int NO_EXCEPTION = 0;
   int USER_EXCEPTION = 1;
   int SYSTEM_EXCEPTION = 2;
   int LOCATION_FORWARD = 3;
   int LOCATION_FORWARD_PERM = 4;
   int NEEDS_ADDRESSING_MODE = 5;

   ServiceContexts getServiceContexts();

   void setServiceContexts(ServiceContexts var1);

   void setIOR(IOR var1);
}
