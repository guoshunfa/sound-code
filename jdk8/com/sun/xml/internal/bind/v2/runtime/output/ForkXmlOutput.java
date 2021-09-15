package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ForkXmlOutput extends XmlOutputAbstractImpl {
   private final XmlOutput lhs;
   private final XmlOutput rhs;

   public ForkXmlOutput(XmlOutput lhs, XmlOutput rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
   }

   public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
      this.lhs.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
      this.rhs.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
   }

   public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
      this.lhs.endDocument(fragment);
      this.rhs.endDocument(fragment);
   }

   public void beginStartTag(Name name) throws IOException, XMLStreamException {
      this.lhs.beginStartTag(name);
      this.rhs.beginStartTag(name);
   }

   public void attribute(Name name, String value) throws IOException, XMLStreamException {
      this.lhs.attribute(name, value);
      this.rhs.attribute(name, value);
   }

   public void endTag(Name name) throws IOException, SAXException, XMLStreamException {
      this.lhs.endTag(name);
      this.rhs.endTag(name);
   }

   public void beginStartTag(int prefix, String localName) throws IOException, XMLStreamException {
      this.lhs.beginStartTag(prefix, localName);
      this.rhs.beginStartTag(prefix, localName);
   }

   public void attribute(int prefix, String localName, String value) throws IOException, XMLStreamException {
      this.lhs.attribute(prefix, localName, value);
      this.rhs.attribute(prefix, localName, value);
   }

   public void endStartTag() throws IOException, SAXException {
      this.lhs.endStartTag();
      this.rhs.endStartTag();
   }

   public void endTag(int prefix, String localName) throws IOException, SAXException, XMLStreamException {
      this.lhs.endTag(prefix, localName);
      this.rhs.endTag(prefix, localName);
   }

   public void text(String value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
      this.lhs.text(value, needsSeparatingWhitespace);
      this.rhs.text(value, needsSeparatingWhitespace);
   }

   public void text(Pcdata value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
      this.lhs.text(value, needsSeparatingWhitespace);
      this.rhs.text(value, needsSeparatingWhitespace);
   }
}
