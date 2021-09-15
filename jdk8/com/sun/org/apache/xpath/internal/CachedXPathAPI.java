package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class CachedXPathAPI {
   protected XPathContext xpathSupport;

   public CachedXPathAPI() {
      this.xpathSupport = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
   }

   public CachedXPathAPI(CachedXPathAPI priorXPathAPI) {
      this.xpathSupport = priorXPathAPI.xpathSupport;
   }

   public XPathContext getXPathContext() {
      return this.xpathSupport;
   }

   public Node selectSingleNode(Node contextNode, String str) throws TransformerException {
      return this.selectSingleNode(contextNode, str, contextNode);
   }

   public Node selectSingleNode(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      NodeIterator nl = this.selectNodeIterator(contextNode, str, namespaceNode);
      return nl.nextNode();
   }

   public NodeIterator selectNodeIterator(Node contextNode, String str) throws TransformerException {
      return this.selectNodeIterator(contextNode, str, contextNode);
   }

   public NodeIterator selectNodeIterator(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      XObject list = this.eval(contextNode, str, namespaceNode);
      return list.nodeset();
   }

   public NodeList selectNodeList(Node contextNode, String str) throws TransformerException {
      return this.selectNodeList(contextNode, str, contextNode);
   }

   public NodeList selectNodeList(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      XObject list = this.eval(contextNode, str, namespaceNode);
      return list.nodelist();
   }

   public XObject eval(Node contextNode, String str) throws TransformerException {
      return this.eval(contextNode, str, contextNode);
   }

   public XObject eval(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      PrefixResolverDefault prefixResolver = new PrefixResolverDefault((Node)(namespaceNode.getNodeType() == 9 ? ((Document)namespaceNode).getDocumentElement() : namespaceNode));
      XPath xpath = new XPath(str, (SourceLocator)null, prefixResolver, 0, (ErrorListener)null);
      int ctxtNode = this.xpathSupport.getDTMHandleFromNode(contextNode);
      return xpath.execute(this.xpathSupport, ctxtNode, prefixResolver);
   }

   public XObject eval(Node contextNode, String str, PrefixResolver prefixResolver) throws TransformerException {
      XPath xpath = new XPath(str, (SourceLocator)null, prefixResolver, 0, (ErrorListener)null);
      XPathContext xpathSupport = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
      int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
      return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
   }
}
