package com.sun.xml.internal.bind.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface InfosetScanner<XmlNode> {
   void scan(XmlNode var1) throws SAXException;

   void setContentHandler(ContentHandler var1);

   ContentHandler getContentHandler();

   XmlNode getCurrentElement();

   LocatorEx getLocator();
}
