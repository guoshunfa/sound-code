package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface Acceptor {
   boolean initialize();

   boolean initialized();

   String getConnectionCacheType();

   void setConnectionCache(InboundConnectionCache var1);

   InboundConnectionCache getConnectionCache();

   boolean shouldRegisterAcceptEvent();

   void accept();

   void close();

   EventHandler getEventHandler();

   MessageMediator createMessageMediator(Broker var1, Connection var2);

   MessageMediator finishCreatingMessageMediator(Broker var1, Connection var2, MessageMediator var3);

   InputObject createInputObject(Broker var1, MessageMediator var2);

   OutputObject createOutputObject(Broker var1, MessageMediator var2);
}
