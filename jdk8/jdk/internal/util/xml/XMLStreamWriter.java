package jdk.internal.util.xml;

public interface XMLStreamWriter {
   String DEFAULT_XML_VERSION = "1.0";
   String DEFAULT_ENCODING = "UTF-8";

   void writeStartElement(String var1) throws XMLStreamException;

   void writeEmptyElement(String var1) throws XMLStreamException;

   void writeEndElement() throws XMLStreamException;

   void writeEndDocument() throws XMLStreamException;

   void close() throws XMLStreamException;

   void flush() throws XMLStreamException;

   void writeAttribute(String var1, String var2) throws XMLStreamException;

   void writeCData(String var1) throws XMLStreamException;

   void writeDTD(String var1) throws XMLStreamException;

   void writeStartDocument() throws XMLStreamException;

   void writeStartDocument(String var1) throws XMLStreamException;

   void writeStartDocument(String var1, String var2) throws XMLStreamException;

   void writeCharacters(String var1) throws XMLStreamException;

   void writeCharacters(char[] var1, int var2, int var3) throws XMLStreamException;
}
