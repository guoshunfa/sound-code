package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public interface Receiver {
   void receive(UnmarshallingContext.State var1, Object var2) throws SAXException;
}
