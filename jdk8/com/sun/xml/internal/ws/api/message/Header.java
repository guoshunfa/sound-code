package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public interface Header {
   boolean isIgnorable(@NotNull SOAPVersion var1, @NotNull Set<String> var2);

   @NotNull
   String getRole(@NotNull SOAPVersion var1);

   boolean isRelay();

   @NotNull
   String getNamespaceURI();

   @NotNull
   String getLocalPart();

   @Nullable
   String getAttribute(@NotNull String var1, @NotNull String var2);

   @Nullable
   String getAttribute(@NotNull QName var1);

   XMLStreamReader readHeader() throws XMLStreamException;

   <T> T readAsJAXB(Unmarshaller var1) throws JAXBException;

   /** @deprecated */
   <T> T readAsJAXB(Bridge<T> var1) throws JAXBException;

   <T> T readAsJAXB(XMLBridge<T> var1) throws JAXBException;

   @NotNull
   WSEndpointReference readAsEPR(AddressingVersion var1) throws XMLStreamException;

   void writeTo(XMLStreamWriter var1) throws XMLStreamException;

   void writeTo(SOAPMessage var1) throws SOAPException;

   void writeTo(ContentHandler var1, ErrorHandler var2) throws SAXException;

   @NotNull
   String getStringContent();
}
