package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface ContactInfo {
   Broker getBroker();

   ContactInfoList getContactInfoList();

   ClientRequestDispatcher getClientRequestDispatcher();

   boolean isConnectionBased();

   boolean shouldCacheConnection();

   String getConnectionCacheType();

   void setConnectionCache(OutboundConnectionCache var1);

   OutboundConnectionCache getConnectionCache();

   Connection createConnection();

   MessageMediator createMessageMediator(Broker var1, ContactInfo var2, Connection var3, String var4, boolean var5);

   MessageMediator createMessageMediator(Broker var1, Connection var2);

   MessageMediator finishCreatingMessageMediator(Broker var1, Connection var2, MessageMediator var3);

   InputObject createInputObject(Broker var1, MessageMediator var2);

   OutputObject createOutputObject(MessageMediator var1);

   int hashCode();
}
