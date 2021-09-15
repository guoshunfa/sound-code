package com.sun.xml.internal.org.jvnet.staxex;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public interface XMLStreamReaderEx extends XMLStreamReader {
   CharSequence getPCDATA() throws XMLStreamException;

   NamespaceContextEx getNamespaceContext();

   String getElementTextTrim() throws XMLStreamException;
}
