package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.Bridge;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class InternalBridge<T> extends Bridge<T> {
   protected InternalBridge(JAXBContextImpl context) {
      super(context);
   }

   public JAXBContextImpl getContext() {
      return this.context;
   }

   abstract void marshal(T var1, XMLSerializer var2) throws IOException, SAXException, XMLStreamException;
}
