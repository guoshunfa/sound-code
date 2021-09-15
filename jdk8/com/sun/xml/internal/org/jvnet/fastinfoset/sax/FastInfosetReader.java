package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetParser;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetReader extends XMLReader, FastInfosetParser {
   String ENCODING_ALGORITHM_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler";
   String PRIMITIVE_TYPE_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler";

   void parse(InputStream var1) throws IOException, FastInfosetException, SAXException;

   void setLexicalHandler(LexicalHandler var1);

   LexicalHandler getLexicalHandler();

   void setDeclHandler(DeclHandler var1);

   DeclHandler getDeclHandler();

   void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler var1);

   EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler();

   void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler var1);

   PrimitiveTypeContentHandler getPrimitiveTypeContentHandler();
}
