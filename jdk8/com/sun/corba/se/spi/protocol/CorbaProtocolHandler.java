package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.pept.protocol.ProtocolHandler;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.UnknownException;

public interface CorbaProtocolHandler extends ProtocolHandler {
   void handleRequest(RequestMessage var1, CorbaMessageMediator var2);

   void handleRequest(LocateRequestMessage var1, CorbaMessageMediator var2);

   CorbaMessageMediator createResponse(CorbaMessageMediator var1, ServiceContexts var2);

   CorbaMessageMediator createUserExceptionResponse(CorbaMessageMediator var1, ServiceContexts var2);

   CorbaMessageMediator createUnknownExceptionResponse(CorbaMessageMediator var1, UnknownException var2);

   CorbaMessageMediator createSystemExceptionResponse(CorbaMessageMediator var1, SystemException var2, ServiceContexts var3);

   CorbaMessageMediator createLocationForward(CorbaMessageMediator var1, IOR var2, ServiceContexts var3);

   void handleThrowableDuringServerDispatch(CorbaMessageMediator var1, Throwable var2, CompletionStatus var3);
}
