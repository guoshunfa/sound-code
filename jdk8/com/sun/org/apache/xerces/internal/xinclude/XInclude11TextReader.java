package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public class XInclude11TextReader extends XIncludeTextReader {
   public XInclude11TextReader(XMLInputSource source, XIncludeHandler handler, int bufferSize) throws IOException {
      super(source, handler, bufferSize);
   }

   protected boolean isValid(int ch) {
      return XML11Char.isXML11Valid(ch);
   }
}
