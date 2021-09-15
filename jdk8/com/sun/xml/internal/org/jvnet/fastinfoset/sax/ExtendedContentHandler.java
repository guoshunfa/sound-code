package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface ExtendedContentHandler extends ContentHandler {
   void characters(char[] var1, int var2, int var3, boolean var4) throws SAXException;
}
