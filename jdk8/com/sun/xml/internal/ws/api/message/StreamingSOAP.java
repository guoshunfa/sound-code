package com.sun.xml.internal.ws.api.message;

import javax.xml.stream.XMLStreamReader;

public interface StreamingSOAP {
   XMLStreamReader readEnvelope();
}
