package com.sun.xml.internal.ws.api.server;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface SDDocumentFilter {
   XMLStreamWriter filter(SDDocument var1, XMLStreamWriter var2) throws XMLStreamException, IOException;
}
