package com.sun.org.apache.xerces.internal.xs;

public interface XSImplementation {
   StringList getRecognizedVersions();

   XSLoader createXSLoader(StringList var1) throws XSException;
}
