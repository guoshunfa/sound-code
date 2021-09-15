package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLDocumentScanner extends XMLDocumentSource {
   void setInputSource(XMLInputSource var1) throws IOException;

   boolean scanDocument(boolean var1) throws IOException, XNIException;

   int next() throws XNIException, IOException;
}
