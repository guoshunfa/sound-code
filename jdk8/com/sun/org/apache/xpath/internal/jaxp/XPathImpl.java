package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XPathImpl implements XPath {
   private XPathVariableResolver variableResolver;
   private XPathFunctionResolver functionResolver;
   private XPathVariableResolver origVariableResolver;
   private XPathFunctionResolver origFunctionResolver;
   private NamespaceContext namespaceContext;
   private JAXPPrefixResolver prefixResolver;
   private boolean featureSecureProcessing;
   private boolean overrideDefaultParser;
   private final JdkXmlFeatures featureManager;
   private static Document d = null;

   XPathImpl(XPathVariableResolver vr, XPathFunctionResolver fr) {
      this(vr, fr, false, new JdkXmlFeatures(false));
   }

   XPathImpl(XPathVariableResolver vr, XPathFunctionResolver fr, boolean featureSecureProcessing, JdkXmlFeatures featureManager) {
      this.namespaceContext = null;
      this.featureSecureProcessing = false;
      this.overrideDefaultParser = true;
      this.origVariableResolver = this.variableResolver = vr;
      this.origFunctionResolver = this.functionResolver = fr;
      this.featureSecureProcessing = featureSecureProcessing;
      this.featureManager = featureManager;
      this.overrideDefaultParser = featureManager.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
   }

   public void setXPathVariableResolver(XPathVariableResolver resolver) {
      if (resolver == null) {
         String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPathVariableResolver"});
         throw new NullPointerException(fmsg);
      } else {
         this.variableResolver = resolver;
      }
   }

   public XPathVariableResolver getXPathVariableResolver() {
      return this.variableResolver;
   }

   public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
      if (resolver == null) {
         String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPathFunctionResolver"});
         throw new NullPointerException(fmsg);
      } else {
         this.functionResolver = resolver;
      }
   }

   public XPathFunctionResolver getXPathFunctionResolver() {
      return this.functionResolver;
   }

   public void setNamespaceContext(NamespaceContext nsContext) {
      if (nsContext == null) {
         String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"NamespaceContext"});
         throw new NullPointerException(fmsg);
      } else {
         this.namespaceContext = nsContext;
         this.prefixResolver = new JAXPPrefixResolver(nsContext);
      }
   }

   public NamespaceContext getNamespaceContext() {
      return this.namespaceContext;
   }

   private DocumentBuilder getParser() {
      try {
         DocumentBuilderFactory dbf = JdkXmlUtils.getDOMFactory(this.overrideDefaultParser);
         return dbf.newDocumentBuilder();
      } catch (ParserConfigurationException var2) {
         throw new Error(var2);
      }
   }

   private XObject eval(String expression, Object contextItem) throws TransformerException {
      com.sun.org.apache.xpath.internal.XPath xpath = new com.sun.org.apache.xpath.internal.XPath(expression, (SourceLocator)null, this.prefixResolver, 0);
      XPathContext xpathSupport = null;
      JAXPExtensionsProvider xobj;
      if (this.functionResolver != null) {
         xobj = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing, this.featureManager);
         xpathSupport = new XPathContext(xobj);
      } else {
         xpathSupport = new XPathContext();
      }

      xobj = null;
      xpathSupport.setVarStack(new JAXPVariableStack(this.variableResolver));
      XObject xobj;
      if (contextItem instanceof Node) {
         xobj = xpath.execute(xpathSupport, (Node)contextItem, this.prefixResolver);
      } else {
         xobj = xpath.execute(xpathSupport, -1, this.prefixResolver);
      }

      return xobj;
   }

   public Object evaluate(String expression, Object item, QName returnType) throws XPathExpressionException {
      String fmsg;
      if (expression == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPath expression"});
         throw new NullPointerException(fmsg);
      } else if (returnType == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"returnType"});
         throw new NullPointerException(fmsg);
      } else if (!this.isSupported(returnType)) {
         fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
         throw new IllegalArgumentException(fmsg);
      } else {
         try {
            XObject resultObject = this.eval(expression, item);
            return this.getResultAsType(resultObject, returnType);
         } catch (NullPointerException var6) {
            throw new XPathExpressionException(var6);
         } catch (TransformerException var7) {
            Throwable nestedException = var7.getException();
            if (nestedException instanceof XPathFunctionException) {
               throw (XPathFunctionException)nestedException;
            } else {
               throw new XPathExpressionException(var7);
            }
         }
      }
   }

   private boolean isSupported(QName returnType) {
      return returnType.equals(XPathConstants.STRING) || returnType.equals(XPathConstants.NUMBER) || returnType.equals(XPathConstants.BOOLEAN) || returnType.equals(XPathConstants.NODE) || returnType.equals(XPathConstants.NODESET);
   }

   private Object getResultAsType(XObject resultObject, QName returnType) throws TransformerException {
      if (returnType.equals(XPathConstants.STRING)) {
         return resultObject.str();
      } else if (returnType.equals(XPathConstants.NUMBER)) {
         return new Double(resultObject.num());
      } else if (returnType.equals(XPathConstants.BOOLEAN)) {
         return new Boolean(resultObject.bool());
      } else if (returnType.equals(XPathConstants.NODESET)) {
         return resultObject.nodelist();
      } else if (returnType.equals(XPathConstants.NODE)) {
         NodeIterator ni = resultObject.nodeset();
         return ni.nextNode();
      } else {
         String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
         throw new IllegalArgumentException(fmsg);
      }
   }

   public String evaluate(String expression, Object item) throws XPathExpressionException {
      return (String)this.evaluate(expression, item, XPathConstants.STRING);
   }

   public XPathExpression compile(String expression) throws XPathExpressionException {
      if (expression == null) {
         String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPath expression"});
         throw new NullPointerException(fmsg);
      } else {
         try {
            com.sun.org.apache.xpath.internal.XPath xpath = new com.sun.org.apache.xpath.internal.XPath(expression, (SourceLocator)null, this.prefixResolver, 0);
            XPathExpressionImpl ximpl = new XPathExpressionImpl(xpath, this.prefixResolver, this.functionResolver, this.variableResolver, this.featureSecureProcessing, this.featureManager);
            return ximpl;
         } catch (TransformerException var4) {
            throw new XPathExpressionException(var4);
         }
      }
   }

   public Object evaluate(String expression, InputSource source, QName returnType) throws XPathExpressionException {
      String fmsg;
      if (source == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"source"});
         throw new NullPointerException(fmsg);
      } else if (expression == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"XPath expression"});
         throw new NullPointerException(fmsg);
      } else if (returnType == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"returnType"});
         throw new NullPointerException(fmsg);
      } else if (!this.isSupported(returnType)) {
         fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
         throw new IllegalArgumentException(fmsg);
      } else {
         try {
            Document document = this.getParser().parse(source);
            XObject resultObject = this.eval(expression, document);
            return this.getResultAsType(resultObject, returnType);
         } catch (SAXException var6) {
            throw new XPathExpressionException(var6);
         } catch (IOException var7) {
            throw new XPathExpressionException(var7);
         } catch (TransformerException var8) {
            Throwable nestedException = var8.getException();
            if (nestedException instanceof XPathFunctionException) {
               throw (XPathFunctionException)nestedException;
            } else {
               throw new XPathExpressionException(var8);
            }
         }
      }
   }

   public String evaluate(String expression, InputSource source) throws XPathExpressionException {
      return (String)this.evaluate(expression, source, XPathConstants.STRING);
   }

   public void reset() {
      this.variableResolver = this.origVariableResolver;
      this.functionResolver = this.origFunctionResolver;
      this.namespaceContext = null;
   }
}
