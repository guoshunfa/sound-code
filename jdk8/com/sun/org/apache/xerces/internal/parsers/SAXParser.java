package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SAXParser extends AbstractSAXParser {
   protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
   protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
   private static final String[] RECOGNIZED_FEATURES = new String[]{"http://apache.org/xml/features/scanner/notify-builtin-refs", "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace"};
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool"};

   public SAXParser(XMLParserConfiguration config) {
      super(config);
   }

   public SAXParser() {
      this((SymbolTable)null, (XMLGrammarPool)null);
   }

   public SAXParser(SymbolTable symbolTable) {
      this(symbolTable, (XMLGrammarPool)null);
   }

   public SAXParser(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      super(new XIncludeAwareParserConfiguration());
      this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
      this.fConfiguration.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
      this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
      if (symbolTable != null) {
         this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
      }

      if (grammarPool != null) {
         this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
      }

   }

   public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://apache.org/xml/properties/security-manager")) {
         this.securityManager = XMLSecurityManager.convert(value, this.securityManager);
         super.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager);
      } else if (name.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
         if (value == null) {
            this.securityPropertyManager = new XMLSecurityPropertyManager();
         } else {
            this.securityPropertyManager = (XMLSecurityPropertyManager)value;
         }

         super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
      } else {
         if (this.securityManager == null) {
            this.securityManager = new XMLSecurityManager(true);
            super.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager);
         }

         if (this.securityPropertyManager == null) {
            this.securityPropertyManager = new XMLSecurityPropertyManager();
            super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
         }

         int index = this.securityPropertyManager.getIndex(name);
         if (index > -1) {
            this.securityPropertyManager.setValue(index, XMLSecurityPropertyManager.State.APIPROPERTY, (String)value);
         } else if (!this.securityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) {
            super.setProperty(name, value);
         }

      }
   }
}
