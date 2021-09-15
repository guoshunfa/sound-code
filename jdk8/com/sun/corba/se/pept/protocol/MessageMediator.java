package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;

public interface MessageMediator {
   Broker getBroker();

   ContactInfo getContactInfo();

   Connection getConnection();

   void initializeMessage();

   void finishSendingRequest();

   /** @deprecated */
   @Deprecated
   InputObject waitForResponse();

   void setOutputObject(OutputObject var1);

   OutputObject getOutputObject();

   void setInputObject(InputObject var1);

   InputObject getInputObject();
}
