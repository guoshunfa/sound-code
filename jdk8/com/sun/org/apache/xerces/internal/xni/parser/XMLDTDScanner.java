package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLDTDScanner extends XMLDTDSource, XMLDTDContentModelSource {
   void setInputSource(XMLInputSource var1) throws IOException;

   boolean scanDTDInternalSubset(boolean var1, boolean var2, boolean var3) throws IOException, XNIException;

   boolean scanDTDExternalSubset(boolean var1) throws IOException, XNIException;

   boolean skipDTD(boolean var1) throws IOException;

   void setLimitAnalyzer(XMLLimitAnalyzer var1);
}
