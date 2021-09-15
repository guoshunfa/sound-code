package com.sun.corba.se.pept.transport;

import java.util.Iterator;

public interface ContactInfoListIterator extends Iterator {
   ContactInfoList getContactInfoList();

   void reportSuccess(ContactInfo var1);

   boolean reportException(ContactInfo var1, RuntimeException var2);

   RuntimeException getFailureException();
}
