package com.sun.xml.internal.org.jvnet.fastinfoset.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public interface LowLevelFastInfosetStreamWriter {
   void initiateLowLevelWriting() throws XMLStreamException;

   int getNextElementIndex();

   int getNextAttributeIndex();

   int getLocalNameIndex();

   int getNextLocalNameIndex();

   void writeLowLevelTerminationAndMark() throws IOException;

   void writeLowLevelStartElementIndexed(int var1, int var2) throws IOException;

   boolean writeLowLevelStartElement(int var1, String var2, String var3, String var4) throws IOException;

   void writeLowLevelStartNamespaces() throws IOException;

   void writeLowLevelNamespace(String var1, String var2) throws IOException;

   void writeLowLevelEndNamespaces() throws IOException;

   void writeLowLevelStartAttributes() throws IOException;

   void writeLowLevelAttributeIndexed(int var1) throws IOException;

   boolean writeLowLevelAttribute(String var1, String var2, String var3) throws IOException;

   void writeLowLevelAttributeValue(String var1) throws IOException;

   void writeLowLevelStartNameLiteral(int var1, String var2, byte[] var3, String var4) throws IOException;

   void writeLowLevelStartNameLiteral(int var1, String var2, int var3, String var4) throws IOException;

   void writeLowLevelEndStartElement() throws IOException;

   void writeLowLevelEndElement() throws IOException;

   void writeLowLevelText(char[] var1, int var2) throws IOException;

   void writeLowLevelText(String var1) throws IOException;

   void writeLowLevelOctets(byte[] var1, int var2) throws IOException;
}
