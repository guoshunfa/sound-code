package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MessageContextFactory extends com.oracle.webservices.internal.api.message.MessageContextFactory {
   private WSFeatureList features;
   private Codec soapCodec;
   private Codec xmlCodec;
   private EnvelopeStyleFeature envelopeStyle;
   private EnvelopeStyle.Style singleSoapStyle;

   public MessageContextFactory(WebServiceFeature[] wsf) {
      this((WSFeatureList)(new WebServiceFeatureList(wsf)));
   }

   public MessageContextFactory(WSFeatureList wsf) {
      this.features = wsf;
      this.envelopeStyle = (EnvelopeStyleFeature)this.features.get(EnvelopeStyleFeature.class);
      if (this.envelopeStyle == null) {
         this.envelopeStyle = new EnvelopeStyleFeature(new EnvelopeStyle.Style[]{EnvelopeStyle.Style.SOAP11});
         this.features.mergeFeatures(new WebServiceFeature[]{this.envelopeStyle}, false);
      }

      EnvelopeStyle.Style[] var2 = this.envelopeStyle.getStyles();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnvelopeStyle.Style s = var2[var4];
         if (s.isXML()) {
            if (this.xmlCodec == null) {
               this.xmlCodec = Codecs.createXMLCodec(this.features);
            }
         } else {
            if (this.soapCodec == null) {
               this.soapCodec = Codecs.createSOAPBindingCodec(this.features);
            }

            this.singleSoapStyle = s;
         }
      }

   }

   protected com.oracle.webservices.internal.api.message.MessageContextFactory newFactory(WebServiceFeature... f) {
      return new MessageContextFactory(f);
   }

   public MessageContext createContext() {
      return this.packet((Message)null);
   }

   public MessageContext createContext(SOAPMessage soap) {
      this.throwIfIllegalMessageArgument(soap);
      return this.packet(Messages.create(soap));
   }

   public MessageContext createContext(Source m, EnvelopeStyle.Style envelopeStyle) {
      this.throwIfIllegalMessageArgument(m);
      return this.packet(Messages.create(m, SOAPVersion.from(envelopeStyle)));
   }

   public MessageContext createContext(Source m) {
      this.throwIfIllegalMessageArgument(m);
      return this.packet(Messages.create(m, SOAPVersion.from(this.singleSoapStyle)));
   }

   public MessageContext createContext(InputStream in, String contentType) throws IOException {
      this.throwIfIllegalMessageArgument(in);
      Packet p = this.packet((Message)null);
      this.soapCodec.decode(in, contentType, p);
      return p;
   }

   /** @deprecated */
   @Deprecated
   public MessageContext createContext(InputStream in, MimeHeaders headers) throws IOException {
      String contentType = getHeader(headers, "Content-Type");
      Packet packet = (Packet)this.createContext(in, contentType);
      packet.acceptableMimeTypes = getHeader(headers, "Accept");
      packet.soapAction = HttpAdapter.fixQuotesAroundSoapAction(getHeader(headers, "SOAPAction"));
      return packet;
   }

   static String getHeader(MimeHeaders headers, String name) {
      String[] values = headers.getHeader(name);
      return values != null && values.length > 0 ? values[0] : null;
   }

   static Map<String, List<String>> toMap(MimeHeaders headers) {
      HashMap<String, List<String>> map = new HashMap();

      MimeHeader mh;
      Object values;
      for(Iterator i = headers.getAllHeaders(); i.hasNext(); ((List)values).add(mh.getValue())) {
         mh = (MimeHeader)i.next();
         values = (List)map.get(mh.getName());
         if (values == null) {
            values = new ArrayList();
            map.put(mh.getName(), values);
         }
      }

      return map;
   }

   public MessageContext createContext(Message m) {
      this.throwIfIllegalMessageArgument(m);
      return this.packet(m);
   }

   private Packet packet(Message m) {
      Packet p = new Packet();
      p.codec = this.soapCodec;
      if (m != null) {
         p.setMessage(m);
      }

      MTOMFeature mf = (MTOMFeature)this.features.get(MTOMFeature.class);
      if (mf != null) {
         p.setMtomFeature(mf);
      }

      return p;
   }

   private void throwIfIllegalMessageArgument(Object message) throws IllegalArgumentException {
      if (message == null) {
         throw new IllegalArgumentException("null messages are not allowed.  Consider using MessageContextFactory.createContext()");
      }
   }

   /** @deprecated */
   @Deprecated
   public MessageContext doCreate() {
      return this.packet((Message)null);
   }

   /** @deprecated */
   @Deprecated
   public MessageContext doCreate(SOAPMessage m) {
      return this.createContext(m);
   }

   /** @deprecated */
   @Deprecated
   public MessageContext doCreate(Source x, SOAPVersion soapVersion) {
      return this.packet(Messages.create(x, soapVersion));
   }
}
