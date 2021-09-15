package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;

public class Detail1_1Impl extends DetailImpl {
   public Detail1_1Impl(SOAPDocumentImpl ownerDoc, String prefix) {
      super(ownerDoc, NameImpl.createDetail1_1Name(prefix));
   }

   public Detail1_1Impl(SOAPDocumentImpl ownerDoc) {
      super(ownerDoc, NameImpl.createDetail1_1Name());
   }

   protected DetailEntry createDetailEntry(Name name) {
      return new DetailEntry1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), name);
   }

   protected DetailEntry createDetailEntry(QName name) {
      return new DetailEntry1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), name);
   }
}
