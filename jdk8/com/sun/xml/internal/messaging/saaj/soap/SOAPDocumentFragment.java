package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentFragmentImpl;

public class SOAPDocumentFragment extends DocumentFragmentImpl {
   public SOAPDocumentFragment(CoreDocumentImpl ownerDoc) {
      super(ownerDoc);
   }

   public SOAPDocumentFragment() {
   }
}
