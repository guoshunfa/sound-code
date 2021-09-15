package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

public class XMLDocumentParser extends AbstractXMLDocumentParser {
   public XMLDocumentParser() {
      super(new XIncludeAwareParserConfiguration());
   }

   public XMLDocumentParser(XMLParserConfiguration config) {
      super(config);
   }

   public XMLDocumentParser(SymbolTable symbolTable) {
      super(new XIncludeAwareParserConfiguration());
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
   }

   public XMLDocumentParser(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      super(new XIncludeAwareParserConfiguration());
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
   }
}
