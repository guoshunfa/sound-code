package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.StringReader;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSParserFilter;
import org.w3c.dom.ls.LSResourceResolver;

public class DOMParserImpl extends AbstractDOMParser implements LSParser, DOMConfiguration {
   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   protected static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
   protected static final String XMLSCHEMA = "http://apache.org/xml/features/validation/schema";
   protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
   protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
   protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String PSVI_AUGMENT = "http://apache.org/xml/features/validation/schema/augment-psvi";
   protected boolean fNamespaceDeclarations;
   protected String fSchemaType;
   protected boolean fBusy;
   private boolean abortNow;
   private Thread currentThread;
   protected static final boolean DEBUG = false;
   private Vector fSchemaLocations;
   private String fSchemaLocation;
   private DOMStringList fRecognizedParameters;
   private DOMParserImpl.AbortHandler abortHandler;

   public DOMParserImpl(XMLParserConfiguration config, String schemaType) {
      this(config);
      if (schemaType != null) {
         if (schemaType.equals(Constants.NS_DTD)) {
            this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
            this.fSchemaType = Constants.NS_DTD;
         } else if (schemaType.equals(Constants.NS_XMLSCHEMA)) {
            this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
         }
      }

   }

   public DOMParserImpl(XMLParserConfiguration config) {
      super(config);
      this.fNamespaceDeclarations = true;
      this.fSchemaType = null;
      this.fBusy = false;
      this.abortNow = false;
      this.fSchemaLocations = new Vector();
      this.fSchemaLocation = null;
      this.abortHandler = null;
      String[] domRecognizedFeatures = new String[]{"canonical-form", "cdata-sections", "charset-overrides-xml-encoding", "infoset", "namespace-declarations", "split-cdata-sections", "supported-media-types-only", "certified", "well-formed", "ignore-unknown-character-denormalizations"};
      this.fConfiguration.addRecognizedFeatures(domRecognizedFeatures);
      this.fConfiguration.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
      this.fConfiguration.setFeature("namespace-declarations", true);
      this.fConfiguration.setFeature("well-formed", true);
      this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
      this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
      this.fConfiguration.setFeature("http://xml.org/sax/features/namespaces", true);
      this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", false);
      this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
      this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", false);
      this.fConfiguration.setFeature("canonical-form", false);
      this.fConfiguration.setFeature("charset-overrides-xml-encoding", true);
      this.fConfiguration.setFeature("split-cdata-sections", true);
      this.fConfiguration.setFeature("supported-media-types-only", false);
      this.fConfiguration.setFeature("ignore-unknown-character-denormalizations", true);
      this.fConfiguration.setFeature("certified", true);

      try {
         this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
      } catch (XMLConfigurationException var4) {
      }

   }

   public DOMParserImpl(SymbolTable symbolTable) {
      this((XMLParserConfiguration)(new XIncludeAwareParserConfiguration()));
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
   }

   public DOMParserImpl(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this((XMLParserConfiguration)(new XIncludeAwareParserConfiguration()));
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
   }

   public void reset() {
      super.reset();
      this.fNamespaceDeclarations = this.fConfiguration.getFeature("namespace-declarations");
      if (this.fSkippedElemStack != null) {
         this.fSkippedElemStack.removeAllElements();
      }

      this.fSchemaLocations.clear();
      this.fRejectedElementDepth = 0;
      this.fFilterReject = false;
      this.fSchemaType = null;
   }

   public DOMConfiguration getDomConfig() {
      return this;
   }

   public LSParserFilter getFilter() {
      return this.fDOMFilter;
   }

   public void setFilter(LSParserFilter filter) {
      this.fDOMFilter = filter;
      if (this.fSkippedElemStack == null) {
         this.fSkippedElemStack = new Stack();
      }

   }

   public void setParameter(String name, Object value) throws DOMException {
      if (value instanceof Boolean) {
         boolean state = (Boolean)value;

         try {
            if (name.equalsIgnoreCase("comments")) {
               this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", state);
            } else if (name.equalsIgnoreCase("datatype-normalization")) {
               this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", state);
            } else if (name.equalsIgnoreCase("entities")) {
               this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", state);
            } else if (name.equalsIgnoreCase("disallow-doctype")) {
               this.fConfiguration.setFeature("http://apache.org/xml/features/disallow-doctype-decl", state);
            } else {
               String normalizedName;
               if (!name.equalsIgnoreCase("supported-media-types-only") && !name.equalsIgnoreCase("normalize-characters") && !name.equalsIgnoreCase("check-character-normalization") && !name.equalsIgnoreCase("canonical-form")) {
                  if (name.equalsIgnoreCase("namespaces")) {
                     this.fConfiguration.setFeature("http://xml.org/sax/features/namespaces", state);
                  } else if (name.equalsIgnoreCase("infoset")) {
                     if (state) {
                        this.fConfiguration.setFeature("http://xml.org/sax/features/namespaces", true);
                        this.fConfiguration.setFeature("namespace-declarations", true);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", false);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", false);
                     }
                  } else if (name.equalsIgnoreCase("cdata-sections")) {
                     this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", state);
                  } else if (name.equalsIgnoreCase("namespace-declarations")) {
                     this.fConfiguration.setFeature("namespace-declarations", state);
                  } else if (!name.equalsIgnoreCase("well-formed") && !name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                     if (name.equalsIgnoreCase("validate")) {
                        this.fConfiguration.setFeature("http://xml.org/sax/features/validation", state);
                        if (this.fSchemaType != Constants.NS_DTD) {
                           this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", state);
                           this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", state);
                        }

                        if (state) {
                           this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", false);
                        }
                     } else if (name.equalsIgnoreCase("validate-if-schema")) {
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", state);
                        if (state) {
                           this.fConfiguration.setFeature("http://xml.org/sax/features/validation", false);
                        }
                     } else if (name.equalsIgnoreCase("element-content-whitespace")) {
                        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", state);
                     } else if (name.equalsIgnoreCase("psvi")) {
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
                        this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl");
                     } else {
                        if (name.equals("http://apache.org/xml/features/namespace-growth")) {
                           normalizedName = "http://apache.org/xml/features/namespace-growth";
                        } else if (name.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                           normalizedName = "http://apache.org/xml/features/internal/tolerate-duplicates";
                        } else {
                           normalizedName = name.toLowerCase(Locale.ENGLISH);
                        }

                        this.fConfiguration.setFeature(normalizedName, state);
                     }
                  } else if (!state) {
                     normalizedName = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
                     throw new DOMException((short)9, normalizedName);
                  }
               } else if (state) {
                  normalizedName = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
                  throw new DOMException((short)9, normalizedName);
               }
            }
         } catch (XMLConfigurationException var11) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{name});
            throw new DOMException((short)8, msg);
         }
      } else {
         String normalizedName;
         if (name.equalsIgnoreCase("error-handler")) {
            if (!(value instanceof DOMErrorHandler) && value != null) {
               normalizedName = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, normalizedName);
            }

            try {
               this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)value);
               this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fErrorHandler);
            } catch (XMLConfigurationException var9) {
            }
         } else if (name.equalsIgnoreCase("resource-resolver")) {
            if (!(value instanceof LSResourceResolver) && value != null) {
               normalizedName = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, normalizedName);
            }

            try {
               this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper((LSResourceResolver)value));
            } catch (XMLConfigurationException var8) {
            }
         } else if (name.equalsIgnoreCase("schema-location")) {
            if (!(value instanceof String) && value != null) {
               normalizedName = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, normalizedName);
            }

            try {
               if (value == null) {
                  this.fSchemaLocation = null;
                  this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", (Object)null);
               } else {
                  this.fSchemaLocation = (String)value;
                  StringTokenizer t = new StringTokenizer(this.fSchemaLocation, " \n\t\r");
                  if (t.hasMoreTokens()) {
                     this.fSchemaLocations.clear();
                     this.fSchemaLocations.add(t.nextToken());

                     while(t.hasMoreTokens()) {
                        this.fSchemaLocations.add(t.nextToken());
                     }

                     this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", this.fSchemaLocations.toArray());
                  } else {
                     this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", value);
                  }
               }
            } catch (XMLConfigurationException var12) {
            }
         } else if (name.equalsIgnoreCase("schema-type")) {
            if (!(value instanceof String) && value != null) {
               normalizedName = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, normalizedName);
            }

            try {
               if (value == null) {
                  this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", false);
                  this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
                  this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", (Object)null);
                  this.fSchemaType = null;
               } else if (value.equals(Constants.NS_XMLSCHEMA)) {
                  this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", true);
                  this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
                  this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
                  this.fSchemaType = Constants.NS_XMLSCHEMA;
               } else if (value.equals(Constants.NS_DTD)) {
                  this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", false);
                  this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
                  this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                  this.fSchemaType = Constants.NS_DTD;
               }
            } catch (XMLConfigurationException var7) {
            }
         } else {
            if (!name.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
               normalizedName = name.toLowerCase(Locale.ENGLISH);

               try {
                  this.fConfiguration.setProperty(normalizedName, value);
                  return;
               } catch (XMLConfigurationException var10) {
                  try {
                     if (name.equals("http://apache.org/xml/features/namespace-growth")) {
                        normalizedName = "http://apache.org/xml/features/namespace-growth";
                     } else if (name.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                        normalizedName = "http://apache.org/xml/features/internal/tolerate-duplicates";
                     }

                     this.fConfiguration.getFeature(normalizedName);
                     throw newTypeMismatchError(name);
                  } catch (XMLConfigurationException var6) {
                     throw newFeatureNotFoundError(name);
                  }
               }
            }

            this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", value);
         }
      }

   }

   public Object getParameter(String name) throws DOMException {
      if (name.equalsIgnoreCase("comments")) {
         return this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("datatype-normalization")) {
         return this.fConfiguration.getFeature("http://apache.org/xml/features/validation/schema/normalized-value") ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("entities")) {
         return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("namespaces")) {
         return this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces") ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("validate")) {
         return this.fConfiguration.getFeature("http://xml.org/sax/features/validation") ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("validate-if-schema")) {
         return this.fConfiguration.getFeature("http://apache.org/xml/features/validation/dynamic") ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("element-content-whitespace")) {
         return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("disallow-doctype")) {
         return this.fConfiguration.getFeature("http://apache.org/xml/features/disallow-doctype-decl") ? Boolean.TRUE : Boolean.FALSE;
      } else if (!name.equalsIgnoreCase("infoset")) {
         if (name.equalsIgnoreCase("cdata-sections")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes") ? Boolean.TRUE : Boolean.FALSE;
         } else if (!name.equalsIgnoreCase("check-character-normalization") && !name.equalsIgnoreCase("normalize-characters")) {
            if (!name.equalsIgnoreCase("namespace-declarations") && !name.equalsIgnoreCase("well-formed") && !name.equalsIgnoreCase("ignore-unknown-character-denormalizations") && !name.equalsIgnoreCase("canonical-form") && !name.equalsIgnoreCase("supported-media-types-only") && !name.equalsIgnoreCase("split-cdata-sections") && !name.equalsIgnoreCase("charset-overrides-xml-encoding")) {
               if (name.equalsIgnoreCase("error-handler")) {
                  return this.fErrorHandler != null ? this.fErrorHandler.getErrorHandler() : null;
               } else if (name.equalsIgnoreCase("resource-resolver")) {
                  try {
                     XMLEntityResolver entityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
                     return entityResolver != null && entityResolver instanceof DOMEntityResolverWrapper ? ((DOMEntityResolverWrapper)entityResolver).getEntityResolver() : null;
                  } catch (XMLConfigurationException var5) {
                     return null;
                  }
               } else if (name.equalsIgnoreCase("schema-type")) {
                  return this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
               } else if (name.equalsIgnoreCase("schema-location")) {
                  return this.fSchemaLocation;
               } else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
                  return this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/symbol-table");
               } else if (name.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
                  return this.fConfiguration.getProperty("http://apache.org/xml/properties/dom/document-class-name");
               } else {
                  String normalizedName;
                  if (name.equals("http://apache.org/xml/features/namespace-growth")) {
                     normalizedName = "http://apache.org/xml/features/namespace-growth";
                  } else if (name.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                     normalizedName = "http://apache.org/xml/features/internal/tolerate-duplicates";
                  } else {
                     normalizedName = name.toLowerCase(Locale.ENGLISH);
                  }

                  try {
                     return this.fConfiguration.getFeature(normalizedName) ? Boolean.TRUE : Boolean.FALSE;
                  } catch (XMLConfigurationException var6) {
                     try {
                        return this.fConfiguration.getProperty(normalizedName);
                     } catch (XMLConfigurationException var4) {
                        throw newFeatureNotFoundError(name);
                     }
                  }
               }
            } else {
               return this.fConfiguration.getFeature(name.toLowerCase(Locale.ENGLISH)) ? Boolean.TRUE : Boolean.FALSE;
            }
         } else {
            return Boolean.FALSE;
         }
      } else {
         boolean infoset = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces") && this.fConfiguration.getFeature("namespace-declarations") && this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") && this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") && !this.fConfiguration.getFeature("http://apache.org/xml/features/validation/dynamic") && !this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") && !this.fConfiguration.getFeature("http://apache.org/xml/features/validation/schema/normalized-value") && !this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes");
         return infoset ? Boolean.TRUE : Boolean.FALSE;
      }
   }

   public boolean canSetParameter(String name, Object value) {
      if (value == null) {
         return true;
      } else if (value instanceof Boolean) {
         boolean state = (Boolean)value;
         if (!name.equalsIgnoreCase("supported-media-types-only") && !name.equalsIgnoreCase("normalize-characters") && !name.equalsIgnoreCase("check-character-normalization") && !name.equalsIgnoreCase("canonical-form")) {
            if (!name.equalsIgnoreCase("well-formed") && !name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
               if (!name.equalsIgnoreCase("cdata-sections") && !name.equalsIgnoreCase("charset-overrides-xml-encoding") && !name.equalsIgnoreCase("comments") && !name.equalsIgnoreCase("datatype-normalization") && !name.equalsIgnoreCase("disallow-doctype") && !name.equalsIgnoreCase("entities") && !name.equalsIgnoreCase("infoset") && !name.equalsIgnoreCase("namespaces") && !name.equalsIgnoreCase("namespace-declarations") && !name.equalsIgnoreCase("validate") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("element-content-whitespace") && !name.equalsIgnoreCase("xml-declaration")) {
                  try {
                     String normalizedName;
                     if (name.equalsIgnoreCase("http://apache.org/xml/features/namespace-growth")) {
                        normalizedName = "http://apache.org/xml/features/namespace-growth";
                     } else if (name.equalsIgnoreCase("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                        normalizedName = "http://apache.org/xml/features/internal/tolerate-duplicates";
                     } else {
                        normalizedName = name.toLowerCase(Locale.ENGLISH);
                     }

                     this.fConfiguration.getFeature(normalizedName);
                     return true;
                  } catch (XMLConfigurationException var5) {
                     return false;
                  }
               } else {
                  return true;
               }
            } else {
               return state;
            }
         } else {
            return !state;
         }
      } else if (name.equalsIgnoreCase("error-handler")) {
         return value instanceof DOMErrorHandler || value == null;
      } else if (name.equalsIgnoreCase("resource-resolver")) {
         return value instanceof LSResourceResolver || value == null;
      } else if (!name.equalsIgnoreCase("schema-type")) {
         if (name.equalsIgnoreCase("schema-location")) {
            return value instanceof String || value == null;
         } else {
            return name.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name");
         }
      } else {
         return value instanceof String && (value.equals(Constants.NS_XMLSCHEMA) || value.equals(Constants.NS_DTD)) || value == null;
      }
   }

   public DOMStringList getParameterNames() {
      if (this.fRecognizedParameters == null) {
         Vector parameters = new Vector();
         parameters.add("namespaces");
         parameters.add("cdata-sections");
         parameters.add("canonical-form");
         parameters.add("namespace-declarations");
         parameters.add("split-cdata-sections");
         parameters.add("entities");
         parameters.add("validate-if-schema");
         parameters.add("validate");
         parameters.add("datatype-normalization");
         parameters.add("charset-overrides-xml-encoding");
         parameters.add("check-character-normalization");
         parameters.add("supported-media-types-only");
         parameters.add("ignore-unknown-character-denormalizations");
         parameters.add("normalize-characters");
         parameters.add("well-formed");
         parameters.add("infoset");
         parameters.add("disallow-doctype");
         parameters.add("element-content-whitespace");
         parameters.add("comments");
         parameters.add("error-handler");
         parameters.add("resource-resolver");
         parameters.add("schema-location");
         parameters.add("schema-type");
         this.fRecognizedParameters = new DOMStringListImpl(parameters);
      }

      return this.fRecognizedParameters;
   }

   public Document parseURI(String uri) throws LSException {
      if (this.fBusy) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null);
         throw new DOMException((short)11, msg);
      } else {
         XMLInputSource source = new XMLInputSource((String)null, uri, (String)null);

         try {
            this.currentThread = Thread.currentThread();
            this.fBusy = true;
            this.parse(source);
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
               this.abortNow = false;
               Thread.interrupted();
            }
         } catch (Exception var5) {
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
               Thread.interrupted();
            }

            if (this.abortNow) {
               this.abortNow = false;
               this.restoreHandlers();
               return null;
            }

            if (var5 != AbstractDOMParser.Abort.INSTANCE) {
               if (!(var5 instanceof XMLParseException) && this.fErrorHandler != null) {
                  DOMErrorImpl error = new DOMErrorImpl();
                  error.fException = var5;
                  error.fMessage = var5.getMessage();
                  error.fSeverity = 3;
                  this.fErrorHandler.getErrorHandler().handleError(error);
               }

               throw (LSException)DOMUtil.createLSException((short)81, var5).fillInStackTrace();
            }
         }

         Document doc = this.getDocument();
         this.dropDocumentReferences();
         return doc;
      }
   }

   public Document parse(LSInput is) throws LSException {
      XMLInputSource xmlInputSource = this.dom2xmlInputSource(is);
      if (this.fBusy) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null);
         throw new DOMException((short)11, msg);
      } else {
         try {
            this.currentThread = Thread.currentThread();
            this.fBusy = true;
            this.parse(xmlInputSource);
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
               this.abortNow = false;
               Thread.interrupted();
            }
         } catch (Exception var5) {
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
               Thread.interrupted();
            }

            if (this.abortNow) {
               this.abortNow = false;
               this.restoreHandlers();
               return null;
            }

            if (var5 != AbstractDOMParser.Abort.INSTANCE) {
               if (!(var5 instanceof XMLParseException) && this.fErrorHandler != null) {
                  DOMErrorImpl error = new DOMErrorImpl();
                  error.fException = var5;
                  error.fMessage = var5.getMessage();
                  error.fSeverity = 3;
                  this.fErrorHandler.getErrorHandler().handleError(error);
               }

               throw (LSException)DOMUtil.createLSException((short)81, var5).fillInStackTrace();
            }
         }

         Document doc = this.getDocument();
         this.dropDocumentReferences();
         return doc;
      }
   }

   private void restoreHandlers() {
      this.fConfiguration.setDocumentHandler(this);
      this.fConfiguration.setDTDHandler(this);
      this.fConfiguration.setDTDContentModelHandler(this);
   }

   public Node parseWithContext(LSInput is, Node cnode, short action) throws DOMException, LSException {
      throw new DOMException((short)9, "Not supported");
   }

   XMLInputSource dom2xmlInputSource(LSInput is) {
      XMLInputSource xis = null;
      if (is.getCharacterStream() != null) {
         xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getCharacterStream(), "UTF-16");
      } else if (is.getByteStream() != null) {
         xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getByteStream(), is.getEncoding());
      } else if (is.getStringData() != null && is.getStringData().length() > 0) {
         xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), new StringReader(is.getStringData()), "UTF-16");
      } else {
         if ((is.getSystemId() == null || is.getSystemId().length() <= 0) && (is.getPublicId() == null || is.getPublicId().length() <= 0)) {
            if (this.fErrorHandler != null) {
               DOMErrorImpl error = new DOMErrorImpl();
               error.fType = "no-input-specified";
               error.fMessage = "no-input-specified";
               error.fSeverity = 3;
               this.fErrorHandler.getErrorHandler().handleError(error);
            }

            throw new LSException((short)81, "no-input-specified");
         }

         xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI());
      }

      return xis;
   }

   public boolean getAsync() {
      return false;
   }

   public boolean getBusy() {
      return this.fBusy;
   }

   public void abort() {
      if (this.fBusy) {
         this.fBusy = false;
         if (this.currentThread != null) {
            this.abortNow = true;
            if (this.abortHandler == null) {
               this.abortHandler = new DOMParserImpl.AbortHandler();
            }

            this.fConfiguration.setDocumentHandler(this.abortHandler);
            this.fConfiguration.setDTDHandler(this.abortHandler);
            this.fConfiguration.setDTDContentModelHandler(this.abortHandler);
            if (this.currentThread == Thread.currentThread()) {
               throw AbstractDOMParser.Abort.INSTANCE;
            }

            this.currentThread.interrupt();
         }
      }

   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) {
      if (!this.fNamespaceDeclarations && this.fNamespaceAware) {
         int len = attributes.getLength();

         for(int i = len - 1; i >= 0; --i) {
            if (XMLSymbols.PREFIX_XMLNS == attributes.getPrefix(i) || XMLSymbols.PREFIX_XMLNS == attributes.getQName(i)) {
               attributes.removeAttributeAt(i);
            }
         }
      }

      super.startElement(element, attributes, augs);
   }

   private static DOMException newFeatureNotFoundError(String name) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{name});
      return new DOMException((short)8, msg);
   }

   private static DOMException newTypeMismatchError(String name) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
      return new DOMException((short)17, msg);
   }

   private class AbortHandler implements XMLDocumentHandler, XMLDTDHandler, XMLDTDContentModelHandler {
      private XMLDocumentSource documentSource;
      private XMLDTDContentModelSource dtdContentSource;
      private XMLDTDSource dtdSource;

      private AbortHandler() {
      }

      public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void comment(XMLString text, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void characters(XMLString text, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endElement(QName element, Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startCDATA(Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endCDATA(Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endDocument(Augmentations augs) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void setDocumentSource(XMLDocumentSource source) {
         this.documentSource = source;
      }

      public XMLDocumentSource getDocumentSource() {
         return this.documentSource;
      }

      public void startDTD(XMLLocator locator, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startParameterEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endParameterEntity(String name, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startExternalSubset(XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endExternalSubset(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void elementDecl(String name, String contentModel, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startAttlist(String elementName, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endAttlist(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void internalEntityDecl(String name, XMLString text, XMLString nonNormalizedText, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void externalEntityDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier, String notation, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startConditional(short type, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void ignoredCharacters(XMLString text, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endConditional(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endDTD(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void setDTDSource(XMLDTDSource source) {
         this.dtdSource = source;
      }

      public XMLDTDSource getDTDSource() {
         return this.dtdSource;
      }

      public void startContentModel(String elementName, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void any(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void empty(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void startGroup(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void pcdata(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void element(String elementName, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void separator(short separator, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void occurrence(short occurrence, Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endGroup(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void endContentModel(Augmentations augmentations) throws XNIException {
         throw AbstractDOMParser.Abort.INSTANCE;
      }

      public void setDTDContentModelSource(XMLDTDContentModelSource source) {
         this.dtdContentSource = source;
      }

      public XMLDTDContentModelSource getDTDContentModelSource() {
         return this.dtdContentSource;
      }

      // $FF: synthetic method
      AbortHandler(Object x1) {
         this();
      }
   }
}
