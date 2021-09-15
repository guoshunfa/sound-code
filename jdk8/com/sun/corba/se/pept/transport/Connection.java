package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface Connection {
   boolean shouldRegisterReadEvent();

   boolean shouldRegisterServerReadEvent();

   boolean read();

   void close();

   Acceptor getAcceptor();

   ContactInfo getContactInfo();

   EventHandler getEventHandler();

   boolean isServer();

   boolean isBusy();

   long getTimeStamp();

   void setTimeStamp(long var1);

   void setState(String var1);

   void writeLock();

   void writeUnlock();

   void sendWithoutLock(OutputObject var1);

   void registerWaiter(MessageMediator var1);

   InputObject waitForResponse(MessageMediator var1);

   void unregisterWaiter(MessageMediator var1);

   void setConnectionCache(ConnectionCache var1);

   ConnectionCache getConnectionCache();
}
