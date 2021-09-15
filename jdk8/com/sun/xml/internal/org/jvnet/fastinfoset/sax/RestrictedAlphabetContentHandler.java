package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface RestrictedAlphabetContentHandler {
   void numericCharacters(char[] var1, int var2, int var3) throws SAXException;

   void dateTimeCharacters(char[] var1, int var2, int var3) throws SAXException;

   void alphabetCharacters(String var1, char[] var2, int var3, int var4) throws SAXException;
}
