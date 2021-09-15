package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class XmlOutputAbstractImpl implements XmlOutput {
   protected int[] nsUriIndex2prefixIndex;
   protected NamespaceContextImpl nsContext;
   protected XMLSerializer serializer;

   public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
      this.nsUriIndex2prefixIndex = nsUriIndex2prefixIndex;
      this.nsContext = nsContext;
      this.serializer = serializer;
   }

   public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
      this.serializer = null;
   }

   public void beginStartTag(Name name) throws IOException, XMLStreamException {
      this.beginStartTag(this.nsUriIndex2prefixIndex[name.nsUriIndex], name.localName);
   }

   public abstract void beginStartTag(int var1, String var2) throws IOException, XMLStreamException;

   public void attribute(Name name, String value) throws IOException, XMLStreamException {
      short idx = name.nsUriIndex;
      if (idx == -1) {
         this.attribute(-1, name.localName, value);
      } else {
         this.attribute(this.nsUriIndex2prefixIndex[idx], name.localName, value);
      }

   }

   public abstract void attribute(int var1, String var2, String var3) throws IOException, XMLStreamException;

   public abstract void endStartTag() throws IOException, SAXException;

   public void endTag(Name name) throws IOException, SAXException, XMLStreamException {
      this.endTag(this.nsUriIndex2prefixIndex[name.nsUriIndex], name.localName);
   }

   public abstract void endTag(int var1, String var2) throws IOException, SAXException, XMLStreamException;
}
