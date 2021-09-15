package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;

public class StAXResult extends SAXResult {
   public StAXResult(XMLStreamWriter writer) {
      if (writer == null) {
         throw new IllegalArgumentException();
      } else {
         super.setHandler(new ContentHandlerToXMLStreamWriter(writer));
      }
   }
}
