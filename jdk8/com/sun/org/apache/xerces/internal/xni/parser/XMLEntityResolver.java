package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLEntityResolver {
   XMLInputSource resolveEntity(XMLResourceIdentifier var1) throws XNIException, IOException;
}
