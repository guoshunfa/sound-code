package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.helpers.AttributesImpl;

public abstract class AbstractHeaderImpl implements Header {
   protected static final AttributesImpl EMPTY_ATTS = new AttributesImpl();

   protected AbstractHeaderImpl() {
   }

   /** @deprecated */
   public final <T> T readAsJAXB(Bridge<T> bridge, BridgeContext context) throws JAXBException {
      return this.readAsJAXB(bridge);
   }

   public <T> T readAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      try {
         return unmarshaller.unmarshal(this.readHeader());
      } catch (Exception var3) {
         throw new JAXBException(var3);
      }
   }

   /** @deprecated */
   public <T> T readAsJAXB(Bridge<T> bridge) throws JAXBException {
      try {
         return bridge.unmarshal(this.readHeader());
      } catch (XMLStreamException var3) {
         throw new JAXBException(var3);
      }
   }

   public <T> T readAsJAXB(XMLBridge<T> bridge) throws JAXBException {
      try {
         return bridge.unmarshal((XMLStreamReader)this.readHeader(), (AttachmentUnmarshaller)null);
      } catch (XMLStreamException var3) {
         throw new JAXBException(var3);
      }
   }

   public WSEndpointReference readAsEPR(AddressingVersion expected) throws XMLStreamException {
      XMLStreamReader xsr = this.readHeader();
      WSEndpointReference epr = new WSEndpointReference(xsr, expected);
      XMLStreamReaderFactory.recycle(xsr);
      return epr;
   }

   public boolean isIgnorable(@NotNull SOAPVersion soapVersion, @NotNull Set<String> roles) {
      String v = this.getAttribute(soapVersion.nsUri, "mustUnderstand");
      if (v != null && this.parseBool(v)) {
         if (roles == null) {
            return true;
         } else {
            return !roles.contains(this.getRole(soapVersion));
         }
      } else {
         return true;
      }
   }

   @NotNull
   public String getRole(@NotNull SOAPVersion soapVersion) {
      String v = this.getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
      if (v == null) {
         v = soapVersion.implicitRole;
      }

      return v;
   }

   public boolean isRelay() {
      String v = this.getAttribute(SOAPVersion.SOAP_12.nsUri, "relay");
      return v == null ? false : this.parseBool(v);
   }

   public String getAttribute(QName name) {
      return this.getAttribute(name.getNamespaceURI(), name.getLocalPart());
   }

   protected final boolean parseBool(String value) {
      if (value.length() == 0) {
         return false;
      } else {
         char ch = value.charAt(0);
         return ch == 't' || ch == '1';
      }
   }

   public String getStringContent() {
      try {
         XMLStreamReader xsr = this.readHeader();
         xsr.nextTag();
         return xsr.getElementText();
      } catch (XMLStreamException var2) {
         return null;
      }
   }
}
