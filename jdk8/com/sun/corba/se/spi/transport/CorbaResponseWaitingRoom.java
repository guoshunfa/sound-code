package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import org.omg.CORBA.SystemException;

public interface CorbaResponseWaitingRoom extends ResponseWaitingRoom {
   void signalExceptionToAllWaiters(SystemException var1);

   MessageMediator getMessageMediator(int var1);
}
