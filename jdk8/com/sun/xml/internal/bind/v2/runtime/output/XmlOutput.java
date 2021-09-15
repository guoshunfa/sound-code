package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface XmlOutput {
   void startDocument(XMLSerializer var1, boolean var2, int[] var3, NamespaceContextImpl var4) throws IOException, SAXException, XMLStreamException;

   void endDocument(boolean var1) throws IOException, SAXException, XMLStreamException;

   void beginStartTag(Name var1) throws IOException, XMLStreamException;

   void beginStartTag(int var1, String var2) throws IOException, XMLStreamException;

   void attribute(Name var1, String var2) throws IOException, XMLStreamException;

   void attribute(int var1, String var2, String var3) throws IOException, XMLStreamException;

   void endStartTag() throws IOException, SAXException;

   void endTag(Name var1) throws IOException, SAXException, XMLStreamException;

   void endTag(int var1, String var2) throws IOException, SAXException, XMLStreamException;

   void text(String var1, boolean var2) throws IOException, SAXException, XMLStreamException;

   void text(Pcdata var1, boolean var2) throws IOException, SAXException, XMLStreamException;
}
