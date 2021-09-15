package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.util.Locale;

public interface XMLGrammarLoader {
   String[] getRecognizedFeatures();

   boolean getFeature(String var1) throws XMLConfigurationException;

   void setFeature(String var1, boolean var2) throws XMLConfigurationException;

   String[] getRecognizedProperties();

   Object getProperty(String var1) throws XMLConfigurationException;

   void setProperty(String var1, Object var2) throws XMLConfigurationException;

   void setLocale(Locale var1);

   Locale getLocale();

   void setErrorHandler(XMLErrorHandler var1);

   XMLErrorHandler getErrorHandler();

   void setEntityResolver(XMLEntityResolver var1);

   XMLEntityResolver getEntityResolver();

   Grammar loadGrammar(XMLInputSource var1) throws IOException, XNIException;
}
