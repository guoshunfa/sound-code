package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.DOMMessage;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.ProblemActionHeader;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.message.source.ProtocolSourceMessage;
import com.sun.xml.internal.ws.message.stream.PayloadStreamReaderMessage;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Messages {
   private Messages() {
   }

   /** @deprecated */
   public static Message create(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
      return JAXBMessage.create(context, jaxbObject, soapVersion);
   }

   /** @deprecated */
   public static Message createRaw(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
      return JAXBMessage.createRaw(context, jaxbObject, soapVersion);
   }

   /** @deprecated */
   public static Message create(Marshaller marshaller, Object jaxbObject, SOAPVersion soapVersion) {
      return create(BindingContextFactory.getBindingContext(marshaller).getJAXBContext(), jaxbObject, soapVersion);
   }

   public static Message create(SOAPMessage saaj) {
      return SAAJFactory.create(saaj);
   }

   public static Message createUsingPayload(Source payload, SOAPVersion ver) {
      if (payload instanceof DOMSource) {
         if (((DOMSource)payload).getNode() == null) {
            return new EmptyMessageImpl(ver);
         }
      } else if (payload instanceof StreamSource) {
         StreamSource ss = (StreamSource)payload;
         if (ss.getInputStream() == null && ss.getReader() == null && ss.getSystemId() == null) {
            return new EmptyMessageImpl(ver);
         }
      } else if (payload instanceof SAXSource) {
         SAXSource ss = (SAXSource)payload;
         if (ss.getInputSource() == null && ss.getXMLReader() == null) {
            return new EmptyMessageImpl(ver);
         }
      }

      return new PayloadSourceMessage(payload, ver);
   }

   public static Message createUsingPayload(XMLStreamReader payload, SOAPVersion ver) {
      return new PayloadStreamReaderMessage(payload, ver);
   }

   public static Message createUsingPayload(Element payload, SOAPVersion ver) {
      return new DOMMessage(ver, payload);
   }

   public static Message create(Element soapEnvelope) {
      SOAPVersion ver = SOAPVersion.fromNsUri(soapEnvelope.getNamespaceURI());
      Element header = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Header");
      HeaderList headers = null;
      if (header != null) {
         for(Node n = header.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
               if (headers == null) {
                  headers = new HeaderList(ver);
               }

               headers.add(Headers.create((Element)n));
            }
         }
      }

      Element body = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Body");
      if (body == null) {
         throw new WebServiceException("Message doesn't have <S:Body> " + soapEnvelope);
      } else {
         Element payload = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Body");
         return (Message)(payload == null ? new EmptyMessageImpl(headers, new AttachmentSetImpl(), ver) : new DOMMessage(ver, headers, payload));
      }
   }

   public static Message create(Source envelope, SOAPVersion soapVersion) {
      return new ProtocolSourceMessage(envelope, soapVersion);
   }

   public static Message createEmpty(SOAPVersion soapVersion) {
      return new EmptyMessageImpl(soapVersion);
   }

   @NotNull
   public static Message create(@NotNull XMLStreamReader reader) {
      if (reader.getEventType() != 1) {
         XMLStreamReaderUtil.nextElementContent(reader);
      }

      assert reader.getEventType() == 1 : reader.getEventType();

      SOAPVersion ver = SOAPVersion.fromNsUri(reader.getNamespaceURI());
      return Codecs.createSOAPEnvelopeXmlCodec(ver).decode(reader);
   }

   @NotNull
   public static Message create(@NotNull XMLStreamBuffer xsb) {
      try {
         return create((XMLStreamReader)xsb.readAsXMLStreamReader());
      } catch (XMLStreamException var2) {
         throw new XMLStreamReaderException(var2);
      }
   }

   public static Message create(Throwable t, SOAPVersion soapVersion) {
      return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, (CheckedExceptionImpl)null, (Throwable)t);
   }

   public static Message create(SOAPFault fault) {
      SOAPVersion ver = SOAPVersion.fromNsUri(fault.getNamespaceURI());
      return new DOMMessage(ver, fault);
   }

   /** @deprecated */
   public static Message createAddressingFaultMessage(WSBinding binding, QName missingHeader) {
      return createAddressingFaultMessage(binding, (Packet)null, missingHeader);
   }

   public static Message createAddressingFaultMessage(WSBinding binding, Packet p, QName missingHeader) {
      AddressingVersion av = binding.getAddressingVersion();
      if (av == null) {
         throw new WebServiceException(AddressingMessages.ADDRESSING_SHOULD_BE_ENABLED());
      } else {
         WsaTubeHelper helper = av.getWsaHelper((WSDLPort)null, (SEIModel)null, binding);
         return create(helper.newMapRequiredFault(new MissingAddressingHeaderException(missingHeader, p)));
      }
   }

   public static Message create(@NotNull String unsupportedAction, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      QName subcode = av.actionNotSupportedTag;
      String faultstring = String.format(av.actionNotSupportedText, unsupportedAction);

      try {
         SOAPFault fault;
         if (sv == SOAPVersion.SOAP_12) {
            fault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
            fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
            fault.appendFaultSubcode(subcode);
            Detail detail = fault.addDetail();
            SOAPElement se = detail.addChildElement(av.problemActionTag);
            se = se.addChildElement(av.actionTag);
            se.addTextNode(unsupportedAction);
         } else {
            fault = SOAPVersion.SOAP_11.getSOAPFactory().createFault();
            fault.setFaultCode(subcode);
         }

         fault.setFaultString(faultstring);
         Message faultMessage = SOAPFaultBuilder.createSOAPFaultMessage(sv, fault);
         if (sv == SOAPVersion.SOAP_11) {
            faultMessage.getHeaders().add(new ProblemActionHeader(unsupportedAction, av));
         }

         return faultMessage;
      } catch (SOAPException var9) {
         throw new WebServiceException(var9);
      }
   }

   @NotNull
   public static Message create(@NotNull SOAPVersion soapVersion, @NotNull ProtocolException pex, @Nullable QName faultcode) {
      return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, pex, faultcode);
   }
}
