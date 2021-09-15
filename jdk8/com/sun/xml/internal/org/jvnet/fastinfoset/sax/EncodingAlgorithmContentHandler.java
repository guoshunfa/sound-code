package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface EncodingAlgorithmContentHandler {
   void octets(String var1, int var2, byte[] var3, int var4, int var5) throws SAXException;

   void object(String var1, int var2, Object var3) throws SAXException;
}
