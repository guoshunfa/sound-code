package com.sun.xml.internal.org.jvnet.staxex;

import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface XMLStreamWriterEx extends XMLStreamWriter {
   void writeBinary(byte[] var1, int var2, int var3, String var4) throws XMLStreamException;

   void writeBinary(DataHandler var1) throws XMLStreamException;

   OutputStream writeBinary(String var1) throws XMLStreamException;

   void writePCDATA(CharSequence var1) throws XMLStreamException;

   NamespaceContextEx getNamespaceContext();
}
