package com.sun.xml.internal.org.jvnet.fastinfoset.stax;

import javax.xml.stream.XMLStreamException;

public interface FastInfosetStreamReader {
   int peekNext() throws XMLStreamException;

   int accessNamespaceCount();

   String accessLocalName();

   String accessNamespaceURI();

   String accessPrefix();

   char[] accessTextCharacters();

   int accessTextStart();

   int accessTextLength();
}
