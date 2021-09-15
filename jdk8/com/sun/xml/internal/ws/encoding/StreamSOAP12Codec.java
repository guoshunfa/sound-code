package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

final class StreamSOAP12Codec extends StreamSOAPCodec {
   public static final String SOAP12_MIME_TYPE = "application/soap+xml";
   public static final String DEFAULT_SOAP12_CONTENT_TYPE = "application/soap+xml; charset=utf-8";
   private static final List<String> EXPECTED_CONTENT_TYPES = Collections.singletonList("application/soap+xml");

   StreamSOAP12Codec() {
      super(SOAPVersion.SOAP_12);
   }

   StreamSOAP12Codec(WSBinding binding) {
      super(binding);
   }

   StreamSOAP12Codec(WSFeatureList features) {
      super(features);
   }

   public String getMimeType() {
      return "application/soap+xml";
   }

   protected com.sun.xml.internal.ws.api.pipe.ContentType getContentType(Packet packet) {
      ContentTypeImpl.Builder b = this.getContenTypeBuilder(packet);
      if (packet.soapAction == null) {
         return b.build();
      } else {
         b.contentType = b.contentType + ";action=" + this.fixQuotesAroundSoapAction(packet.soapAction);
         return b.build();
      }
   }

   public void decode(InputStream in, String contentType, Packet packet, AttachmentSet att) throws IOException {
      ContentType ct = new ContentType(contentType);
      packet.soapAction = this.fixQuotesAroundSoapAction(ct.getParameter("action"));
      super.decode(in, contentType, packet, att);
   }

   private String fixQuotesAroundSoapAction(String soapAction) {
      if (soapAction == null || soapAction.startsWith("\"") && soapAction.endsWith("\"")) {
         return soapAction;
      } else {
         String fixedSoapAction = soapAction;
         if (!soapAction.startsWith("\"")) {
            fixedSoapAction = "\"" + soapAction;
         }

         if (!soapAction.endsWith("\"")) {
            fixedSoapAction = fixedSoapAction + "\"";
         }

         return fixedSoapAction;
      }
   }

   protected List<String> getExpectedContentTypes() {
      return EXPECTED_CONTENT_TYPES;
   }

   protected String getDefaultContentType() {
      return "application/soap+xml; charset=utf-8";
   }
}
