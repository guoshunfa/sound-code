package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import java.util.Locale;

public interface XMLParserConfiguration extends XMLComponentManager {
   void parse(XMLInputSource var1) throws XNIException, IOException;

   void addRecognizedFeatures(String[] var1);

   void setFeature(String var1, boolean var2) throws XMLConfigurationException;

   boolean getFeature(String var1) throws XMLConfigurationException;

   void addRecognizedProperties(String[] var1);

   void setProperty(String var1, Object var2) throws XMLConfigurationException;

   Object getProperty(String var1) throws XMLConfigurationException;

   void setErrorHandler(XMLErrorHandler var1);

   XMLErrorHandler getErrorHandler();

   void setDocumentHandler(XMLDocumentHandler var1);

   XMLDocumentHandler getDocumentHandler();

   void setDTDHandler(XMLDTDHandler var1);

   XMLDTDHandler getDTDHandler();

   void setDTDContentModelHandler(XMLDTDContentModelHandler var1);

   XMLDTDContentModelHandler getDTDContentModelHandler();

   void setEntityResolver(XMLEntityResolver var1);

   XMLEntityResolver getEntityResolver();

   void setLocale(Locale var1) throws XNIException;

   Locale getLocale();
}
