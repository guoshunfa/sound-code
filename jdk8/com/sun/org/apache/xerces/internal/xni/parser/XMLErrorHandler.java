package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;

public interface XMLErrorHandler {
   void warning(String var1, String var2, XMLParseException var3) throws XNIException;

   void error(String var1, String var2, XMLParseException var3) throws XNIException;

   void fatalError(String var1, String var2, XMLParseException var3) throws XNIException;
}
