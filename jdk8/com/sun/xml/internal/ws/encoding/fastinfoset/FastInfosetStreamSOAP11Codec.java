package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.ws.message.stream.StreamHeader11;
import javax.xml.stream.XMLStreamReader;

final class FastInfosetStreamSOAP11Codec extends FastInfosetStreamSOAPCodec {
   FastInfosetStreamSOAP11Codec(StreamSOAPCodec soapCodec, boolean retainState) {
      super(soapCodec, SOAPVersion.SOAP_11, retainState, retainState ? "application/vnd.sun.stateful.fastinfoset" : "application/fastinfoset");
   }

   private FastInfosetStreamSOAP11Codec(FastInfosetStreamSOAP11Codec that) {
      super(that);
   }

   public Codec copy() {
      return new FastInfosetStreamSOAP11Codec(this);
   }

   protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
      return new StreamHeader11(reader, mark);
   }

   protected ContentType getContentType(String soapAction) {
      return (ContentType)(soapAction != null && soapAction.length() != 0 ? new ContentTypeImpl(this._defaultContentType.getContentType(), soapAction) : this._defaultContentType);
   }
}
