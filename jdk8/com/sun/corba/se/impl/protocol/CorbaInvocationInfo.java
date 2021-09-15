package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class CorbaInvocationInfo implements ClientInvocationInfo {
   private boolean isRetryInvocation;
   private int entryCount;
   private ORB orb;
   private Iterator contactInfoListIterator;
   private ClientRequestDispatcher clientRequestDispatcher;
   private MessageMediator messageMediator;

   private CorbaInvocationInfo() {
   }

   public CorbaInvocationInfo(ORB var1) {
      this.orb = var1;
      this.isRetryInvocation = false;
      this.entryCount = 0;
   }

   public Iterator getContactInfoListIterator() {
      return this.contactInfoListIterator;
   }

   public void setContactInfoListIterator(Iterator var1) {
      this.contactInfoListIterator = var1;
   }

   public boolean isRetryInvocation() {
      return this.isRetryInvocation;
   }

   public void setIsRetryInvocation(boolean var1) {
      this.isRetryInvocation = var1;
   }

   public int getEntryCount() {
      return this.entryCount;
   }

   public void incrementEntryCount() {
      ++this.entryCount;
   }

   public void decrementEntryCount() {
      --this.entryCount;
   }

   public void setClientRequestDispatcher(ClientRequestDispatcher var1) {
      this.clientRequestDispatcher = var1;
   }

   public ClientRequestDispatcher getClientRequestDispatcher() {
      return this.clientRequestDispatcher;
   }

   public void setMessageMediator(MessageMediator var1) {
      this.messageMediator = var1;
   }

   public MessageMediator getMessageMediator() {
      return this.messageMediator;
   }
}
