package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Collections;
import java.util.List;

final class StreamSOAP11Codec extends StreamSOAPCodec {
   public static final String SOAP11_MIME_TYPE = "text/xml";
   public static final String DEFAULT_SOAP11_CONTENT_TYPE = "text/xml; charset=utf-8";
   private static final List<String> EXPECTED_CONTENT_TYPES = Collections.singletonList("text/xml");

   StreamSOAP11Codec() {
      super(SOAPVersion.SOAP_11);
   }

   StreamSOAP11Codec(WSBinding binding) {
      super(binding);
   }

   StreamSOAP11Codec(WSFeatureList features) {
      super(features);
   }

   public String getMimeType() {
      return "text/xml";
   }

   protected com.sun.xml.internal.ws.api.pipe.ContentType getContentType(Packet packet) {
      ContentTypeImpl.Builder b = this.getContenTypeBuilder(packet);
      b.soapAction = packet.soapAction;
      return b.build();
   }

   protected String getDefaultContentType() {
      return "text/xml; charset=utf-8";
   }

   protected List<String> getExpectedContentTypes() {
      return EXPECTED_CONTENT_TYPES;
   }
}
