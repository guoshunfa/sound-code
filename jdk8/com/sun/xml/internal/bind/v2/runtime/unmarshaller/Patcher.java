package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public interface Patcher {
   void run() throws SAXException;
}
