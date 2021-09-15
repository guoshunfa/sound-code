package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.ws.message.stream.StreamHeader12;
import javax.xml.stream.XMLStreamReader;

final class FastInfosetStreamSOAP12Codec extends FastInfosetStreamSOAPCodec {
   FastInfosetStreamSOAP12Codec(StreamSOAPCodec soapCodec, boolean retainState) {
      super(soapCodec, SOAPVersion.SOAP_12, retainState, retainState ? "application/vnd.sun.stateful.soap+fastinfoset" : "application/soap+fastinfoset");
   }

   private FastInfosetStreamSOAP12Codec(FastInfosetStreamSOAPCodec that) {
      super(that);
   }

   public Codec copy() {
      return new FastInfosetStreamSOAP12Codec(this);
   }

   protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
      return new StreamHeader12(reader, mark);
   }

   protected ContentType getContentType(String soapAction) {
      return (ContentType)(soapAction == null ? this._defaultContentType : new ContentTypeImpl(this._defaultContentType.getContentType() + ";action=\"" + soapAction + "\""));
   }
}
