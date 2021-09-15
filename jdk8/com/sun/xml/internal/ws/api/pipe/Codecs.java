package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;

public abstract class Codecs {
   @NotNull
   public static SOAPBindingCodec createSOAPBindingCodec(WSFeatureList feature) {
      return new com.sun.xml.internal.ws.encoding.SOAPBindingCodec(feature);
   }

   @NotNull
   public static Codec createXMLCodec(WSFeatureList feature) {
      return new XMLHTTPBindingCodec(feature);
   }

   @NotNull
   public static SOAPBindingCodec createSOAPBindingCodec(WSBinding binding, StreamSOAPCodec xmlEnvelopeCodec) {
      return new com.sun.xml.internal.ws.encoding.SOAPBindingCodec(binding.getFeatures(), xmlEnvelopeCodec);
   }

   @NotNull
   public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull SOAPVersion version) {
      return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(version);
   }

   /** @deprecated */
   @NotNull
   public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSBinding binding) {
      return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(binding);
   }

   @NotNull
   public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSFeatureList features) {
      return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(features);
   }
}
