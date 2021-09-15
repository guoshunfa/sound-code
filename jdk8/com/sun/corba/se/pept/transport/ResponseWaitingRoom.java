package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface ResponseWaitingRoom {
   void registerWaiter(MessageMediator var1);

   InputObject waitForResponse(MessageMediator var1);

   void responseReceived(InputObject var1);

   void unregisterWaiter(MessageMediator var1);

   int numberRegistered();
}
