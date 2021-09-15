package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;

public interface XMLDocumentSource {
   void setDocumentHandler(XMLDocumentHandler var1);

   XMLDocumentHandler getDocumentHandler();
}
