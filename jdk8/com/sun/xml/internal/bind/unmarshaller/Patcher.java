package com.sun.xml.internal.bind.unmarshaller;

import org.xml.sax.SAXException;

public interface Patcher {
   void run() throws SAXException;
}
