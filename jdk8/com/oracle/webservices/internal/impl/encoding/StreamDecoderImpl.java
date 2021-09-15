package com.oracle.webservices.internal.impl.encoding;

import com.oracle.webservices.internal.impl.internalspi.encoding.StreamDecoder;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.StreamSOAPCodec;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

public class StreamDecoderImpl implements StreamDecoder {
   public Message decode(InputStream in, String charset, AttachmentSet att, SOAPVersion soapVersion) throws IOException {
      XMLStreamReader reader = XMLStreamReaderFactory.create((String)null, in, charset, true);
      XMLStreamReader reader = new TidyXMLStreamReader(reader, in);
      return StreamSOAPCodec.decode((SOAPVersion)soapVersion, (XMLStreamReader)reader, (AttachmentSet)att);
   }
}
