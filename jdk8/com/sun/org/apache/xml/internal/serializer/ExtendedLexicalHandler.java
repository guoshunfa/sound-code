package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

interface ExtendedLexicalHandler extends LexicalHandler {
   void comment(String var1) throws SAXException;
}
