package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLPullParserConfiguration extends XMLParserConfiguration {
   void setInputSource(XMLInputSource var1) throws XMLConfigurationException, IOException;

   boolean parse(boolean var1) throws XNIException, IOException;

   void cleanup();
}
