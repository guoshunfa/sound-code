package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public interface XMLGrammarDescription extends XMLResourceIdentifier {
   String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   String XML_DTD = "http://www.w3.org/TR/REC-xml";

   String getGrammarType();
}
