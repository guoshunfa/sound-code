package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class MTOMXmlOutput extends XmlOutputAbstractImpl {
   private final XmlOutput next;
   private String nsUri;
   private String localName;

   public MTOMXmlOutput(XmlOutput next) {
      this.next = next;
   }

   public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
      super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
      this.next.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
   }

   public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
      this.next.endDocument(fragment);
      super.endDocument(fragment);
   }

   public void beginStartTag(Name name) throws IOException, XMLStreamException {
      this.next.beginStartTag(name);
      this.nsUri = name.nsUri;
      this.localName = name.localName;
   }

   public void beginStartTag(int prefix, String localName) throws IOException, XMLStreamException {
      this.next.beginStartTag(prefix, localName);
      this.nsUri = this.nsContext.getNamespaceURI(prefix);
      this.localName = localName;
   }

   public void attribute(Name name, String value) throws IOException, XMLStreamException {
      this.next.attribute(name, value);
   }

   public void attribute(int prefix, String localName, String value) throws IOException, XMLStreamException {
      this.next.attribute(prefix, localName, value);
   }

   public void endStartTag() throws IOException, SAXException {
      this.next.endStartTag();
   }

   public void endTag(Name name) throws IOException, SAXException, XMLStreamException {
      this.next.endTag(name);
   }

   public void endTag(int prefix, String localName) throws IOException, SAXException, XMLStreamException {
      this.next.endTag(prefix, localName);
   }

   public void text(String value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
      this.next.text(value, needsSeparatingWhitespace);
   }

   public void text(Pcdata value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
      if (value instanceof Base64Data && !this.serializer.getInlineBinaryFlag()) {
         Base64Data b64d = (Base64Data)value;
         String cid;
         if (b64d.hasData()) {
            cid = this.serializer.attachmentMarshaller.addMtomAttachment(b64d.get(), 0, b64d.getDataLen(), b64d.getMimeType(), this.nsUri, this.localName);
         } else {
            cid = this.serializer.attachmentMarshaller.addMtomAttachment(b64d.getDataHandler(), this.nsUri, this.localName);
         }

         if (cid != null) {
            this.nsContext.getCurrent().push();
            int prefix = this.nsContext.declareNsUri("http://www.w3.org/2004/08/xop/include", "xop", false);
            this.beginStartTag(prefix, "Include");
            this.attribute(-1, "href", cid);
            this.endStartTag();
            this.endTag(prefix, "Include");
            this.nsContext.getCurrent().pop();
            return;
         }
      }

      this.next.text(value, needsSeparatingWhitespace);
   }
}
