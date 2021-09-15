package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.Set;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class StreamHeader extends AbstractHeaderImpl {
   protected final XMLStreamBuffer _mark;
   protected boolean _isMustUnderstand;
   @NotNull
   protected String _role;
   protected boolean _isRelay;
   protected String _localName;
   protected String _namespaceURI;
   private final FinalArrayList<StreamHeader.Attribute> attributes;

   protected StreamHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
      assert reader != null && mark != null;

      this._mark = mark;
      this._localName = reader.getLocalName();
      this._namespaceURI = reader.getNamespaceURI();
      this.attributes = this.processHeaderAttributes(reader);
   }

   protected StreamHeader(XMLStreamReader reader) throws XMLStreamException {
      this._localName = reader.getLocalName();
      this._namespaceURI = reader.getNamespaceURI();
      this.attributes = this.processHeaderAttributes(reader);
      this._mark = XMLStreamBuffer.createNewBufferFromXMLStreamReader(reader);
   }

   public final boolean isIgnorable(@NotNull SOAPVersion soapVersion, @NotNull Set<String> roles) {
      if (!this._isMustUnderstand) {
         return true;
      } else if (roles == null) {
         return true;
      } else {
         return !roles.contains(this._role);
      }
   }

   @NotNull
   public String getRole(@NotNull SOAPVersion soapVersion) {
      assert this._role != null;

      return this._role;
   }

   public boolean isRelay() {
      return this._isRelay;
   }

   @NotNull
   public String getNamespaceURI() {
      return this._namespaceURI;
   }

   @NotNull
   public String getLocalPart() {
      return this._localName;
   }

   public String getAttribute(String nsUri, String localName) {
      if (this.attributes != null) {
         for(int i = this.attributes.size() - 1; i >= 0; --i) {
            StreamHeader.Attribute a = (StreamHeader.Attribute)this.attributes.get(i);
            if (a.localName.equals(localName) && a.nsUri.equals(nsUri)) {
               return a.value;
            }
         }
      }

      return null;
   }

   public XMLStreamReader readHeader() throws XMLStreamException {
      return this._mark.readAsXMLStreamReader();
   }

   public void writeTo(XMLStreamWriter w) throws XMLStreamException {
      if (this._mark.getInscopeNamespaces().size() > 0) {
         this._mark.writeToXMLStreamWriter(w, true);
      } else {
         this._mark.writeToXMLStreamWriter(w);
      }

   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      try {
         TransformerFactory tf = XmlUtil.newTransformerFactory();
         Transformer t = tf.newTransformer();
         XMLStreamBufferSource source = new XMLStreamBufferSource(this._mark);
         DOMResult result = new DOMResult();
         t.transform(source, result);
         Node d = result.getNode();
         if (d.getNodeType() == 9) {
            d = d.getFirstChild();
         }

         SOAPHeader header = saaj.getSOAPHeader();
         if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
         }

         Node node = header.getOwnerDocument().importNode(d, true);
         header.appendChild(node);
      } catch (Exception var9) {
         throw new SOAPException(var9);
      }
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      this._mark.writeTo(contentHandler);
   }

   @NotNull
   public WSEndpointReference readAsEPR(AddressingVersion expected) throws XMLStreamException {
      return new WSEndpointReference(this._mark, expected);
   }

   protected abstract FinalArrayList<StreamHeader.Attribute> processHeaderAttributes(XMLStreamReader var1);

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   protected static final class Attribute {
      final String nsUri;
      final String localName;
      final String value;

      public Attribute(String nsUri, String localName, String value) {
         this.nsUri = StreamHeader.fixNull(nsUri);
         this.localName = localName;
         this.value = value;
      }
   }
}
