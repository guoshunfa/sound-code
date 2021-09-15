package com.sun.corba.se.pept.protocol;

import java.util.Iterator;

public interface ClientInvocationInfo {
   Iterator getContactInfoListIterator();

   void setContactInfoListIterator(Iterator var1);

   boolean isRetryInvocation();

   void setIsRetryInvocation(boolean var1);

   int getEntryCount();

   void incrementEntryCount();

   void decrementEntryCount();

   void setClientRequestDispatcher(ClientRequestDispatcher var1);

   ClientRequestDispatcher getClientRequestDispatcher();

   void setMessageMediator(MessageMediator var1);

   MessageMediator getMessageMediator();
}
