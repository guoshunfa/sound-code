package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class StAXExConnector extends StAXStreamConnector {
   private final XMLStreamReaderEx in;

   public StAXExConnector(XMLStreamReaderEx in, XmlVisitor visitor) {
      super(in, visitor);
      this.in = in;
   }

   protected void handleCharacters() throws XMLStreamException, SAXException {
      if (this.predictor.expectText()) {
         CharSequence pcdata = this.in.getPCDATA();
         if (pcdata instanceof com.sun.xml.internal.org.jvnet.staxex.Base64Data) {
            com.sun.xml.internal.org.jvnet.staxex.Base64Data bd = (com.sun.xml.internal.org.jvnet.staxex.Base64Data)pcdata;
            Base64Data binary = new Base64Data();
            if (!bd.hasData()) {
               binary.set(bd.getDataHandler());
            } else {
               binary.set(bd.get(), bd.getDataLen(), bd.getMimeType());
            }

            this.visitor.text(binary);
            this.textReported = true;
         } else {
            this.buffer.append(pcdata);
         }
      }

   }
}
