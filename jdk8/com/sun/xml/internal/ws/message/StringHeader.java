package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class StringHeader extends AbstractHeaderImpl {
   protected final QName name;
   protected final String value;
   protected boolean mustUnderstand = false;
   protected SOAPVersion soapVersion;
   protected static final String MUST_UNDERSTAND = "mustUnderstand";
   protected static final String S12_MUST_UNDERSTAND_TRUE = "true";
   protected static final String S11_MUST_UNDERSTAND_TRUE = "1";

   public StringHeader(@NotNull QName name, @NotNull String value) {
      assert name != null;

      assert value != null;

      this.name = name;
      this.value = value;
   }

   public StringHeader(@NotNull QName name, @NotNull String value, @NotNull SOAPVersion soapVersion, boolean mustUnderstand) {
      this.name = name;
      this.value = value;
      this.soapVersion = soapVersion;
      this.mustUnderstand = mustUnderstand;
   }

   @NotNull
   public String getNamespaceURI() {
      return this.name.getNamespaceURI();
   }

   @NotNull
   public String getLocalPart() {
      return this.name.getLocalPart();
   }

   @Nullable
   public String getAttribute(@NotNull String nsUri, @NotNull String localName) {
      return this.mustUnderstand && this.soapVersion.nsUri.equals(nsUri) && "mustUnderstand".equals(localName) ? getMustUnderstandLiteral(this.soapVersion) : null;
   }

   public XMLStreamReader readHeader() throws XMLStreamException {
      MutableXMLStreamBuffer buf = new MutableXMLStreamBuffer();
      XMLStreamWriter w = buf.createFromXMLStreamWriter();
      this.writeTo(w);
      return buf.readAsXMLStreamReader();
   }

   public void writeTo(XMLStreamWriter w) throws XMLStreamException {
      w.writeStartElement("", this.name.getLocalPart(), this.name.getNamespaceURI());
      w.writeDefaultNamespace(this.name.getNamespaceURI());
      if (this.mustUnderstand) {
         w.writeNamespace("S", this.soapVersion.nsUri);
         w.writeAttribute("S", this.soapVersion.nsUri, "mustUnderstand", getMustUnderstandLiteral(this.soapVersion));
      }

      w.writeCharacters(this.value);
      w.writeEndElement();
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      SOAPHeader header = saaj.getSOAPHeader();
      if (header == null) {
         header = saaj.getSOAPPart().getEnvelope().addHeader();
      }

      SOAPHeaderElement she = header.addHeaderElement(this.name);
      if (this.mustUnderstand) {
         she.setMustUnderstand(true);
      }

      she.addTextNode(this.value);
   }

   public void writeTo(ContentHandler h, ErrorHandler errorHandler) throws SAXException {
      String nsUri = this.name.getNamespaceURI();
      String ln = this.name.getLocalPart();
      h.startPrefixMapping("", nsUri);
      if (this.mustUnderstand) {
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute(this.soapVersion.nsUri, "mustUnderstand", "S:mustUnderstand", "CDATA", getMustUnderstandLiteral(this.soapVersion));
         h.startElement(nsUri, ln, ln, attributes);
      } else {
         h.startElement(nsUri, ln, ln, EMPTY_ATTS);
      }

      h.characters(this.value.toCharArray(), 0, this.value.length());
      h.endElement(nsUri, ln, ln);
   }

   private static String getMustUnderstandLiteral(SOAPVersion sv) {
      return sv == SOAPVersion.SOAP_12 ? "true" : "1";
   }
}
