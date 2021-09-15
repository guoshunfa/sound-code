package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.XalanConstants;
import com.sun.org.apache.xalan.internal.utils.FeaturePropertyBase;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xml.internal.utils.StopParseException;
import com.sun.org.apache.xml.internal.utils.StylesheetPIHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class TransformerFactoryImpl extends SAXTransformerFactory implements SourceLoader, ErrorListener {
   public static final String TRANSLET_NAME = "translet-name";
   public static final String DESTINATION_DIRECTORY = "destination-directory";
   public static final String PACKAGE_NAME = "package-name";
   public static final String JAR_NAME = "jar-name";
   public static final String GENERATE_TRANSLET = "generate-translet";
   public static final String AUTO_TRANSLET = "auto-translet";
   public static final String USE_CLASSPATH = "use-classpath";
   public static final String DEBUG = "debug";
   public static final String ENABLE_INLINING = "enable-inlining";
   public static final String INDENT_NUMBER = "indent-number";
   private ErrorListener _errorListener = this;
   private URIResolver _uriResolver = null;
   protected static final String DEFAULT_TRANSLET_NAME = "GregorSamsa";
   private String _transletName = "GregorSamsa";
   private String _destinationDirectory = null;
   private String _packageName = null;
   private String _jarFileName = null;
   private Map<Source, TransformerFactoryImpl.PIParamWrapper> _piParams = null;
   private boolean _debug = false;
   private boolean _enableInlining = false;
   private boolean _generateTranslet = false;
   private boolean _autoTranslet = false;
   private boolean _useClasspath = false;
   private int _indentNumber = -1;
   private boolean _isNotSecureProcessing = true;
   private boolean _isSecureMode = false;
   private boolean _overrideDefaultParser;
   private String _accessExternalStylesheet = "all";
   private String _accessExternalDTD = "all";
   private XMLSecurityPropertyManager _xmlSecurityPropertyMgr;
   private XMLSecurityManager _xmlSecurityManager;
   private final JdkXmlFeatures _xmlFeatures;
   private ClassLoader _extensionClassLoader = null;
   private Map<String, Class> _xsltcExtensionFunctions;

   public TransformerFactoryImpl() {
      if (System.getSecurityManager() != null) {
         this._isSecureMode = true;
         this._isNotSecureProcessing = false;
      }

      this._xmlFeatures = new JdkXmlFeatures(!this._isNotSecureProcessing);
      this._overrideDefaultParser = this._xmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
      this._xmlSecurityPropertyMgr = new XMLSecurityPropertyManager();
      this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
      this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
      this._xmlSecurityManager = new XMLSecurityManager(true);
      this._xsltcExtensionFunctions = null;
   }

   public Map<String, Class> getExternalExtensionsMap() {
      return this._xsltcExtensionFunctions;
   }

   public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
      if (listener == null) {
         ErrorMsg err = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "TransformerFactory");
         throw new IllegalArgumentException(err.toString());
      } else {
         this._errorListener = listener;
      }
   }

   public ErrorListener getErrorListener() {
      return this._errorListener;
   }

   public Object getAttribute(String name) throws IllegalArgumentException {
      if (name.equals("translet-name")) {
         return this._transletName;
      } else if (name.equals("generate-translet")) {
         return new Boolean(this._generateTranslet);
      } else if (name.equals("auto-translet")) {
         return new Boolean(this._autoTranslet);
      } else if (name.equals("enable-inlining")) {
         return this._enableInlining ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equals("http://apache.org/xml/properties/security-manager")) {
         return this._xmlSecurityManager;
      } else if (name.equals("jdk.xml.transform.extensionClassLoader")) {
         return this._extensionClassLoader;
      } else {
         String propertyValue = this._xmlSecurityManager != null ? this._xmlSecurityManager.getLimitAsString(name) : null;
         if (propertyValue != null) {
            return propertyValue;
         } else {
            propertyValue = this._xmlSecurityPropertyMgr != null ? this._xmlSecurityPropertyMgr.getValue(name) : null;
            if (propertyValue != null) {
               return propertyValue;
            } else {
               ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
               throw new IllegalArgumentException(err.toString());
            }
         }
      }
   }

   public void setAttribute(String name, Object value) throws IllegalArgumentException {
      if (name.equals("translet-name") && value instanceof String) {
         this._transletName = (String)value;
      } else if (name.equals("destination-directory") && value instanceof String) {
         this._destinationDirectory = (String)value;
      } else if (name.equals("package-name") && value instanceof String) {
         this._packageName = (String)value;
      } else if (name.equals("jar-name") && value instanceof String) {
         this._jarFileName = (String)value;
      } else {
         ErrorMsg err;
         if (name.equals("generate-translet")) {
            if (value instanceof Boolean) {
               this._generateTranslet = (Boolean)value;
               return;
            }

            if (value instanceof String) {
               this._generateTranslet = ((String)value).equalsIgnoreCase("true");
               return;
            }
         } else if (name.equals("auto-translet")) {
            if (value instanceof Boolean) {
               this._autoTranslet = (Boolean)value;
               return;
            }

            if (value instanceof String) {
               this._autoTranslet = ((String)value).equalsIgnoreCase("true");
               return;
            }
         } else if (name.equals("use-classpath")) {
            if (value instanceof Boolean) {
               this._useClasspath = (Boolean)value;
               return;
            }

            if (value instanceof String) {
               this._useClasspath = ((String)value).equalsIgnoreCase("true");
               return;
            }
         } else if (name.equals("debug")) {
            if (value instanceof Boolean) {
               this._debug = (Boolean)value;
               return;
            }

            if (value instanceof String) {
               this._debug = ((String)value).equalsIgnoreCase("true");
               return;
            }
         } else if (name.equals("enable-inlining")) {
            if (value instanceof Boolean) {
               this._enableInlining = (Boolean)value;
               return;
            }

            if (value instanceof String) {
               this._enableInlining = ((String)value).equalsIgnoreCase("true");
               return;
            }
         } else if (name.equals("indent-number")) {
            if (value instanceof String) {
               try {
                  this._indentNumber = Integer.parseInt((String)value);
                  return;
               } catch (NumberFormatException var4) {
               }
            } else if (value instanceof Integer) {
               this._indentNumber = (Integer)value;
               return;
            }
         } else if (name.equals("jdk.xml.transform.extensionClassLoader")) {
            if (value instanceof ClassLoader) {
               this._extensionClassLoader = (ClassLoader)value;
               return;
            }

            err = new ErrorMsg("JAXP_INVALID_ATTR_VALUE_ERR", "Extension Functions ClassLoader");
            throw new IllegalArgumentException(err.toString());
         }

         if (this._xmlSecurityManager == null || !this._xmlSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) {
            if (this._xmlSecurityPropertyMgr != null && this._xmlSecurityPropertyMgr.setValue(name, FeaturePropertyBase.State.APIPROPERTY, value)) {
               this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
               this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
            } else {
               err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
               throw new IllegalArgumentException(err.toString());
            }
         }
      }
   }

   public void setFeature(String name, boolean value) throws TransformerConfigurationException {
      ErrorMsg err;
      if (name == null) {
         err = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
         throw new NullPointerException(err.toString());
      } else if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         if (this._isSecureMode && !value) {
            err = new ErrorMsg("JAXP_SECUREPROCESSING_FEATURE");
            throw new TransformerConfigurationException(err.toString());
         } else {
            this._isNotSecureProcessing = !value;
            this._xmlSecurityManager.setSecureProcessing(value);
            if (value && XalanConstants.IS_JDK8_OR_ABOVE) {
               this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, FeaturePropertyBase.State.FSP, "");
               this._xmlSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET, FeaturePropertyBase.State.FSP, "");
               this._accessExternalDTD = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
               this._accessExternalStylesheet = this._xmlSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET);
            }

            if (value && this._xmlFeatures != null) {
               this._xmlFeatures.setFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION, JdkXmlFeatures.State.FSP, false);
            }

         }
      } else if (!name.equals("http://www.oracle.com/feature/use-service-mechanism") || !this._isSecureMode) {
         if (this._xmlFeatures != null && this._xmlFeatures.setFeature(name, JdkXmlFeatures.State.APIPROPERTY, value)) {
            if (name.equals("jdk.xml.overrideDefaultParser") || name.equals("http://www.oracle.com/feature/use-service-mechanism")) {
               this._overrideDefaultParser = this._xmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
            }

         } else {
            err = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", name);
            throw new TransformerConfigurationException(err.toString());
         }
      }
   }

   public boolean getFeature(String name) {
      String[] features = new String[]{"http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stax.StAXSource/feature", "http://javax.xml.transform.stax.StAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter", "http://www.oracle.com/feature/use-service-mechanism"};
      if (name == null) {
         ErrorMsg err = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
         throw new NullPointerException(err.toString());
      } else {
         int index;
         for(index = 0; index < features.length; ++index) {
            if (name.equals(features[index])) {
               return true;
            }
         }

         if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return !this._isNotSecureProcessing;
         } else {
            index = this._xmlFeatures.getIndex(name);
            if (index > -1) {
               return this._xmlFeatures.getFeature(index);
            } else {
               return false;
            }
         }
      }
   }

   public boolean overrideDefaultParser() {
      return this._overrideDefaultParser;
   }

   public JdkXmlFeatures getJdkXmlFeatures() {
      return this._xmlFeatures;
   }

   public URIResolver getURIResolver() {
      return this._uriResolver;
   }

   public void setURIResolver(URIResolver resolver) {
      this._uriResolver = resolver;
   }

   public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
      XMLReader reader = null;
      StylesheetPIHandler _stylesheetPIHandler = new StylesheetPIHandler((String)null, media, title, charset);

      try {
         String baseId;
         if (source instanceof DOMSource) {
            DOMSource domsrc = (DOMSource)source;
            baseId = domsrc.getSystemId();
            Node node = domsrc.getNode();
            DOM2SAX dom2sax = new DOM2SAX(node);
            _stylesheetPIHandler.setBaseId(baseId);
            dom2sax.setContentHandler(_stylesheetPIHandler);
            dom2sax.parse();
         } else {
            if (source instanceof SAXSource) {
               reader = ((SAXSource)source).getXMLReader();
            }

            InputSource isource = SAXSource.sourceToInputSource(source);
            baseId = isource.getSystemId();
            if (reader == null) {
               reader = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, !this._isNotSecureProcessing);
            }

            _stylesheetPIHandler.setBaseId(baseId);
            reader.setContentHandler(_stylesheetPIHandler);
            reader.parse(isource);
         }

         if (this._uriResolver != null) {
            _stylesheetPIHandler.setURIResolver(this._uriResolver);
         }
      } catch (StopParseException var12) {
      } catch (SAXException var13) {
         throw new TransformerConfigurationException("getAssociatedStylesheets failed", var13);
      } catch (IOException var14) {
         throw new TransformerConfigurationException("getAssociatedStylesheets failed", var14);
      }

      return _stylesheetPIHandler.getAssociatedStylesheet();
   }

   public Transformer newTransformer() throws TransformerConfigurationException {
      TransformerImpl result = new TransformerImpl(new Properties(), this._indentNumber, this);
      if (this._uriResolver != null) {
         result.setURIResolver(this._uriResolver);
      }

      if (!this._isNotSecureProcessing) {
         result.setSecureProcessing(true);
      }

      return result;
   }

   public Transformer newTransformer(Source source) throws TransformerConfigurationException {
      Templates templates = this.newTemplates(source);
      Transformer transformer = templates.newTransformer();
      if (this._uriResolver != null) {
         transformer.setURIResolver(this._uriResolver);
      }

      return transformer;
   }

   private void passWarningsToListener(ArrayList<ErrorMsg> messages) throws TransformerException {
      if (this._errorListener != null && messages != null) {
         int count = messages.size();

         for(int pos = 0; pos < count; ++pos) {
            ErrorMsg msg = (ErrorMsg)messages.get(pos);
            if (msg.isWarningError()) {
               this._errorListener.error(new TransformerConfigurationException(msg.toString()));
            } else {
               this._errorListener.warning(new TransformerConfigurationException(msg.toString()));
            }
         }

      }
   }

   private void passErrorsToListener(ArrayList<ErrorMsg> messages) {
      try {
         if (this._errorListener == null || messages == null) {
            return;
         }

         int count = messages.size();

         for(int pos = 0; pos < count; ++pos) {
            String message = ((ErrorMsg)messages.get(pos)).toString();
            this._errorListener.error(new TransformerException(message));
         }
      } catch (TransformerException var5) {
      }

   }

   public Templates newTemplates(Source source) throws TransformerConfigurationException {
      if (this._useClasspath) {
         String transletName = this.getTransletBaseName(source);
         if (this._packageName != null) {
            transletName = this._packageName + "." + transletName;
         }

         ErrorMsg err;
         try {
            Class clazz = ObjectFactory.findProviderClass(transletName, true);
            this.resetTransientAttributes();
            return new TemplatesImpl(new Class[]{clazz}, transletName, (Properties)null, this._indentNumber, this);
         } catch (ClassNotFoundException var12) {
            err = new ErrorMsg("CLASS_NOT_FOUND_ERR", transletName);
            throw new TransformerConfigurationException(err.toString());
         } catch (Exception var13) {
            err = new ErrorMsg(new ErrorMsg("RUNTIME_ERROR_KEY") + var13.getMessage());
            throw new TransformerConfigurationException(err.toString());
         }
      } else {
         if (this._autoTranslet) {
            String transletClassName = this.getTransletBaseName(source);
            if (this._packageName != null) {
               transletClassName = this._packageName + "." + transletClassName;
            }

            byte[][] bytecodes;
            if (this._jarFileName != null) {
               bytecodes = this.getBytecodesFromJar(source, transletClassName);
            } else {
               bytecodes = this.getBytecodesFromClasses(source, transletClassName);
            }

            if (bytecodes != null) {
               if (this._debug) {
                  if (this._jarFileName != null) {
                     System.err.println((Object)(new ErrorMsg("TRANSFORM_WITH_JAR_STR", transletClassName, this._jarFileName)));
                  } else {
                     System.err.println((Object)(new ErrorMsg("TRANSFORM_WITH_TRANSLET_STR", transletClassName)));
                  }
               }

               this.resetTransientAttributes();
               return new TemplatesImpl(bytecodes, transletClassName, (Properties)null, this._indentNumber, this);
            }
         }

         XSLTC xsltc = new XSLTC(this._xmlFeatures);
         if (this._debug) {
            xsltc.setDebug(true);
         }

         if (this._enableInlining) {
            xsltc.setTemplateInlining(true);
         } else {
            xsltc.setTemplateInlining(false);
         }

         if (!this._isNotSecureProcessing) {
            xsltc.setSecureProcessing(true);
         }

         xsltc.setProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet", this._accessExternalStylesheet);
         xsltc.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
         xsltc.setProperty("http://apache.org/xml/properties/security-manager", this._xmlSecurityManager);
         xsltc.setProperty("jdk.xml.transform.extensionClassLoader", this._extensionClassLoader);
         xsltc.init();
         if (!this._isNotSecureProcessing) {
            this._xsltcExtensionFunctions = xsltc.getExternalExtensionFunctions();
         }

         if (this._uriResolver != null) {
            xsltc.setSourceLoader(this);
         }

         if (this._piParams != null && this._piParams.get(source) != null) {
            TransformerFactoryImpl.PIParamWrapper p = (TransformerFactoryImpl.PIParamWrapper)this._piParams.get(source);
            if (p != null) {
               xsltc.setPIParameters(p._media, p._title, p._charset);
            }
         }

         int outputType = 2;
         String transletName;
         if (this._generateTranslet || this._autoTranslet) {
            xsltc.setClassName(this.getTransletBaseName(source));
            if (this._destinationDirectory != null) {
               xsltc.setDestDirectory(this._destinationDirectory);
            } else {
               String xslName = this.getStylesheetFileName(source);
               if (xslName != null) {
                  File xslFile = new File(xslName);
                  transletName = xslFile.getParent();
                  if (transletName != null) {
                     xsltc.setDestDirectory(transletName);
                  }
               }
            }

            if (this._packageName != null) {
               xsltc.setPackageName(this._packageName);
            }

            if (this._jarFileName != null) {
               xsltc.setJarFileName(this._jarFileName);
               outputType = 5;
            } else {
               outputType = 4;
            }
         }

         InputSource input = Util.getInputSource(xsltc, source);
         byte[][] bytecodes = xsltc.compile((String)null, input, outputType);
         transletName = xsltc.getClassName();
         if ((this._generateTranslet || this._autoTranslet) && bytecodes != null && this._jarFileName != null) {
            try {
               xsltc.outputToJar();
            } catch (IOException var16) {
            }
         }

         this.resetTransientAttributes();
         if (this._errorListener != this) {
            try {
               this.passWarningsToListener(xsltc.getWarnings());
            } catch (TransformerException var15) {
               throw new TransformerConfigurationException(var15);
            }
         } else {
            xsltc.printWarnings();
         }

         if (bytecodes == null) {
            ArrayList<ErrorMsg> errs = xsltc.getErrors();
            ErrorMsg err;
            if (errs != null) {
               err = (ErrorMsg)errs.get(errs.size() - 1);
            } else {
               err = new ErrorMsg("JAXP_COMPILE_ERR");
            }

            Throwable cause = err.getCause();
            TransformerConfigurationException exc;
            if (cause != null) {
               exc = new TransformerConfigurationException(cause.getMessage(), cause);
            } else {
               exc = new TransformerConfigurationException(err.toString());
            }

            if (this._errorListener != null) {
               this.passErrorsToListener(xsltc.getErrors());

               try {
                  this._errorListener.fatalError(exc);
               } catch (TransformerException var14) {
               }
            } else {
               xsltc.printErrors();
            }

            throw exc;
         } else {
            return new TemplatesImpl(bytecodes, transletName, xsltc.getOutputProperties(), this._indentNumber, this);
         }
      }
   }

   public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
      TemplatesHandlerImpl handler = new TemplatesHandlerImpl(this._indentNumber, this);
      if (this._uriResolver != null) {
         handler.setURIResolver(this._uriResolver);
      }

      return handler;
   }

   public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
      Transformer transformer = this.newTransformer();
      if (this._uriResolver != null) {
         transformer.setURIResolver(this._uriResolver);
      }

      return new TransformerHandlerImpl((TransformerImpl)transformer);
   }

   public TransformerHandler newTransformerHandler(Source src) throws TransformerConfigurationException {
      Transformer transformer = this.newTransformer(src);
      if (this._uriResolver != null) {
         transformer.setURIResolver(this._uriResolver);
      }

      return new TransformerHandlerImpl((TransformerImpl)transformer);
   }

   public TransformerHandler newTransformerHandler(Templates templates) throws TransformerConfigurationException {
      Transformer transformer = templates.newTransformer();
      TransformerImpl internal = (TransformerImpl)transformer;
      return new TransformerHandlerImpl(internal);
   }

   public XMLFilter newXMLFilter(Source src) throws TransformerConfigurationException {
      Templates templates = this.newTemplates(src);
      return templates == null ? null : this.newXMLFilter(templates);
   }

   public XMLFilter newXMLFilter(Templates templates) throws TransformerConfigurationException {
      try {
         return new TrAXFilter(templates);
      } catch (TransformerConfigurationException var5) {
         TransformerConfigurationException e1 = var5;
         if (this._errorListener != null) {
            try {
               this._errorListener.fatalError(e1);
               return null;
            } catch (TransformerException var4) {
               new TransformerConfigurationException(var4);
            }
         }

         throw var5;
      }
   }

   public void error(TransformerException e) throws TransformerException {
      Throwable wrapped = e.getException();
      if (wrapped != null) {
         System.err.println((Object)(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage())));
      } else {
         System.err.println((Object)(new ErrorMsg("ERROR_MSG", e.getMessageAndLocation())));
      }

      throw e;
   }

   public void fatalError(TransformerException e) throws TransformerException {
      Throwable wrapped = e.getException();
      if (wrapped != null) {
         System.err.println((Object)(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage())));
      } else {
         System.err.println((Object)(new ErrorMsg("FATAL_ERR_MSG", e.getMessageAndLocation())));
      }

      throw e;
   }

   public void warning(TransformerException e) throws TransformerException {
      Throwable wrapped = e.getException();
      if (wrapped != null) {
         System.err.println((Object)(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage())));
      } else {
         System.err.println((Object)(new ErrorMsg("WARNING_MSG", e.getMessageAndLocation())));
      }

   }

   public InputSource loadSource(String href, String context, XSLTC xsltc) {
      try {
         if (this._uriResolver != null) {
            Source source = this._uriResolver.resolve(href, context);
            if (source != null) {
               return Util.getInputSource(xsltc, source);
            }
         }
      } catch (TransformerException var6) {
         ErrorMsg msg = new ErrorMsg("INVALID_URI_ERR", href + "\n" + var6.getMessage(), this);
         xsltc.getParser().reportError(2, msg);
      }

      return null;
   }

   private void resetTransientAttributes() {
      this._transletName = "GregorSamsa";
      this._destinationDirectory = null;
      this._packageName = null;
      this._jarFileName = null;
   }

   private byte[][] getBytecodesFromClasses(Source source, String fullClassName) {
      if (fullClassName == null) {
         return (byte[][])null;
      } else {
         String xslFileName = this.getStylesheetFileName(source);
         File xslFile = null;
         if (xslFileName != null) {
            xslFile = new File(xslFileName);
         }

         int lastDotIndex = fullClassName.lastIndexOf(46);
         String transletName;
         if (lastDotIndex > 0) {
            transletName = fullClassName.substring(lastDotIndex + 1);
         } else {
            transletName = fullClassName;
         }

         String transletPath = fullClassName.replace('.', '/');
         if (this._destinationDirectory != null) {
            transletPath = this._destinationDirectory + "/" + transletPath + ".class";
         } else if (xslFile != null && xslFile.getParent() != null) {
            transletPath = xslFile.getParent() + "/" + transletPath + ".class";
         } else {
            transletPath = transletPath + ".class";
         }

         File transletFile = new File(transletPath);
         if (!transletFile.exists()) {
            return (byte[][])null;
         } else {
            if (xslFile != null && xslFile.exists()) {
               long xslTimestamp = xslFile.lastModified();
               long transletTimestamp = transletFile.lastModified();
               if (transletTimestamp < xslTimestamp) {
                  return (byte[][])null;
               }
            }

            Vector bytecodes = new Vector();
            int fileLength = (int)transletFile.length();
            if (fileLength <= 0) {
               return (byte[][])null;
            } else {
               FileInputStream input;
               try {
                  input = new FileInputStream(transletFile);
               } catch (FileNotFoundException var22) {
                  return (byte[][])null;
               }

               byte[] bytes = new byte[fileLength];

               try {
                  this.readFromInputStream(bytes, input, fileLength);
                  input.close();
               } catch (IOException var21) {
                  return (byte[][])null;
               }

               bytecodes.addElement(bytes);
               String transletParentDir = transletFile.getParent();
               if (transletParentDir == null) {
                  transletParentDir = SecuritySupport.getSystemProperty("user.dir");
               }

               File transletParentFile = new File(transletParentDir);
               final String transletAuxPrefix = transletName + "$";
               File[] auxfiles = transletParentFile.listFiles(new FilenameFilter() {
                  public boolean accept(File dir, String name) {
                     return name.endsWith(".class") && name.startsWith(transletAuxPrefix);
                  }
               });

               int count;
               int auxlength;
               for(count = 0; count < auxfiles.length; ++count) {
                  File auxfile = auxfiles[count];
                  auxlength = (int)auxfile.length();
                  if (auxlength > 0) {
                     FileInputStream auxinput = null;

                     try {
                        auxinput = new FileInputStream(auxfile);
                     } catch (FileNotFoundException var24) {
                        continue;
                     }

                     byte[] bytes = new byte[auxlength];

                     try {
                        this.readFromInputStream(bytes, auxinput, auxlength);
                        auxinput.close();
                     } catch (IOException var23) {
                        continue;
                     }

                     bytecodes.addElement(bytes);
                  }
               }

               count = bytecodes.size();
               if (count <= 0) {
                  return (byte[][])null;
               } else {
                  byte[][] result = new byte[count][1];

                  for(auxlength = 0; auxlength < count; ++auxlength) {
                     result[auxlength] = (byte[])((byte[])bytecodes.elementAt(auxlength));
                  }

                  return result;
               }
            }
         }
      }
   }

   private byte[][] getBytecodesFromJar(Source source, String fullClassName) {
      String xslFileName = this.getStylesheetFileName(source);
      File xslFile = null;
      if (xslFileName != null) {
         xslFile = new File(xslFileName);
      }

      String jarPath;
      if (this._destinationDirectory != null) {
         jarPath = this._destinationDirectory + "/" + this._jarFileName;
      } else if (xslFile != null && xslFile.getParent() != null) {
         jarPath = xslFile.getParent() + "/" + this._jarFileName;
      } else {
         jarPath = this._jarFileName;
      }

      File file = new File(jarPath);
      if (!file.exists()) {
         return (byte[][])null;
      } else {
         if (xslFile != null && xslFile.exists()) {
            long xslTimestamp = xslFile.lastModified();
            long transletTimestamp = file.lastModified();
            if (transletTimestamp < xslTimestamp) {
               return (byte[][])null;
            }
         }

         ZipFile jarFile;
         try {
            jarFile = new ZipFile(file);
         } catch (IOException var19) {
            return (byte[][])null;
         }

         String transletPath = fullClassName.replace('.', '/');
         String transletAuxPrefix = transletPath + "$";
         String transletFullName = transletPath + ".class";
         Vector bytecodes = new Vector();
         Enumeration entries = jarFile.entries();

         while(true) {
            ZipEntry entry;
            String entryName;
            do {
               do {
                  if (!entries.hasMoreElements()) {
                     int count = bytecodes.size();
                     if (count <= 0) {
                        return (byte[][])null;
                     }

                     byte[][] result = new byte[count][1];

                     for(int i = 0; i < count; ++i) {
                        result[i] = (byte[])((byte[])bytecodes.elementAt(i));
                     }

                     return result;
                  }

                  entry = (ZipEntry)entries.nextElement();
                  entryName = entry.getName();
               } while(entry.getSize() <= 0L);
            } while(!entryName.equals(transletFullName) && (!entryName.endsWith(".class") || !entryName.startsWith(transletAuxPrefix)));

            try {
               InputStream input = jarFile.getInputStream(entry);
               int size = (int)entry.getSize();
               byte[] bytes = new byte[size];
               this.readFromInputStream(bytes, input, size);
               input.close();
               bytecodes.addElement(bytes);
            } catch (IOException var18) {
               return (byte[][])null;
            }
         }
      }
   }

   private void readFromInputStream(byte[] bytes, InputStream input, int size) throws IOException {
      int n = false;
      int offset = 0;

      int n;
      for(int length = size; length > 0 && (n = input.read(bytes, offset, length)) > 0; length -= n) {
         offset += n;
      }

   }

   private String getTransletBaseName(Source source) {
      String transletBaseName = null;
      if (!this._transletName.equals("GregorSamsa")) {
         return this._transletName;
      } else {
         String systemId = source.getSystemId();
         if (systemId != null) {
            String baseName = Util.baseName(systemId);
            if (baseName != null) {
               baseName = Util.noExtName(baseName);
               transletBaseName = Util.toJavaName(baseName);
            }
         }

         return transletBaseName != null ? transletBaseName : "GregorSamsa";
      }
   }

   private String getStylesheetFileName(Source source) {
      String systemId = source.getSystemId();
      if (systemId != null) {
         File file = new File(systemId);
         if (file.exists()) {
            return systemId;
         } else {
            URL url;
            try {
               url = new URL(systemId);
            } catch (MalformedURLException var6) {
               return null;
            }

            return "file".equals(url.getProtocol()) ? url.getFile() : null;
         }
      } else {
         return null;
      }
   }

   protected final XSLTCDTMManager createNewDTMManagerInstance() {
      return XSLTCDTMManager.createNewDTMManagerInstance();
   }

   private static class PIParamWrapper {
      public String _media = null;
      public String _title = null;
      public String _charset = null;

      public PIParamWrapper(String media, String title, String charset) {
         this._media = media;
         this._title = title;
         this._charset = charset;
      }
   }
}
