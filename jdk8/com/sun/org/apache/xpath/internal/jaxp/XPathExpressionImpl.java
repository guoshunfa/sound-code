package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
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

public class XPathExpressionImpl implements XPathExpression {
   private XPathFunctionResolver functionResolver;
   private XPathVariableResolver variableResolver;
   private JAXPPrefixResolver prefixResolver;
   private XPath xpath;
   private boolean featureSecureProcessing;
   boolean overrideDefaultParser;
   private final JdkXmlFeatures featureManager;
   static DocumentBuilderFactory dbf = null;
   static DocumentBuilder db = null;
   static Document d = null;

   protected XPathExpressionImpl() {
      this((XPath)null, (JAXPPrefixResolver)null, (XPathFunctionResolver)null, (XPathVariableResolver)null, false, new JdkXmlFeatures(false));
   }

   protected XPathExpressionImpl(XPath xpath, JAXPPrefixResolver prefixResolver, XPathFunctionResolver functionResolver, XPathVariableResolver variableResolver) {
      this(xpath, prefixResolver, functionResolver, variableResolver, false, new JdkXmlFeatures(false));
   }

   protected XPathExpressionImpl(XPath xpath, JAXPPrefixResolver prefixResolver, XPathFunctionResolver functionResolver, XPathVariableResolver variableResolver, boolean featureSecureProcessing, JdkXmlFeatures featureManager) {
      this.featureSecureProcessing = false;
      this.xpath = xpath;
      this.prefixResolver = prefixResolver;
      this.functionResolver = functionResolver;
      this.variableResolver = variableResolver;
      this.featureSecureProcessing = featureSecureProcessing;
      this.featureManager = featureManager;
      this.overrideDefaultParser = featureManager.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
   }

   public void setXPath(XPath xpath) {
      this.xpath = xpath;
   }

   public Object eval(Object item, QName returnType) throws TransformerException {
      XObject resultObject = this.eval(item);
      return this.getResultAsType(resultObject, returnType);
   }

   private XObject eval(Object contextItem) throws TransformerException {
      XPathContext xpathSupport = null;
      JAXPExtensionsProvider xobj;
      if (this.functionResolver != null) {
         xobj = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing, this.featureManager);
         xpathSupport = new XPathContext(xobj);
      } else {
         xpathSupport = new XPathContext();
      }

      xpathSupport.setVarStack(new JAXPVariableStack(this.variableResolver));
      xobj = null;
      Node contextNode = (Node)contextItem;
      XObject xobj;
      if (contextNode == null) {
         xobj = this.xpath.execute(xpathSupport, -1, this.prefixResolver);
      } else {
         xobj = this.xpath.execute(xpathSupport, contextNode, this.prefixResolver);
      }

      return xobj;
   }

   public Object evaluate(Object item, QName returnType) throws XPathExpressionException {
      String fmsg;
      if (returnType == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[]{"returnType"});
         throw new NullPointerException(fmsg);
      } else if (!this.isSupported(returnType)) {
         fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
         throw new IllegalArgumentException(fmsg);
      } else {
         try {
            return this.eval(item, returnType);
         } catch (NullPointerException var5) {
            throw new XPathExpressionException(var5);
         } catch (TransformerException var6) {
            Throwable nestedException = var6.getException();
            if (nestedException instanceof XPathFunctionException) {
               throw (XPathFunctionException)nestedException;
            } else {
               throw new XPathExpressionException(var6);
            }
         }
      }
   }

   public String evaluate(Object item) throws XPathExpressionException {
      return (String)this.evaluate(item, XPathConstants.STRING);
   }

   public Object evaluate(InputSource source, QName returnType) throws XPathExpressionException {
      String fmsg;
      if (source != null && returnType != null) {
         if (!this.isSupported(returnType)) {
            fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[]{returnType.toString()});
            throw new IllegalArgumentException(fmsg);
         } else {
            try {
               if (dbf == null) {
                  dbf = JdkXmlUtils.getDOMFactory(this.overrideDefaultParser);
               }

               db = dbf.newDocumentBuilder();
               Document document = db.parse(source);
               return this.eval(document, returnType);
            } catch (Exception var4) {
               throw new XPathExpressionException(var4);
            }
         }
      } else {
         fmsg = XSLMessages.createXPATHMessage("ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL", (Object[])null);
         throw new NullPointerException(fmsg);
      }
   }

   public String evaluate(InputSource source) throws XPathExpressionException {
      return (String)this.evaluate(source, XPathConstants.STRING);
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
}
