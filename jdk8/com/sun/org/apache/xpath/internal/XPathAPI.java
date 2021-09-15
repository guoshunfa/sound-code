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

public class XPathAPI {
   public static Node selectSingleNode(Node contextNode, String str) throws TransformerException {
      return selectSingleNode(contextNode, str, contextNode);
   }

   public static Node selectSingleNode(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      NodeIterator nl = selectNodeIterator(contextNode, str, namespaceNode);
      return nl.nextNode();
   }

   public static NodeIterator selectNodeIterator(Node contextNode, String str) throws TransformerException {
      return selectNodeIterator(contextNode, str, contextNode);
   }

   public static NodeIterator selectNodeIterator(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      XObject list = eval(contextNode, str, namespaceNode);
      return list.nodeset();
   }

   public static NodeList selectNodeList(Node contextNode, String str) throws TransformerException {
      return selectNodeList(contextNode, str, contextNode);
   }

   public static NodeList selectNodeList(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      XObject list = eval(contextNode, str, namespaceNode);
      return list.nodelist();
   }

   public static XObject eval(Node contextNode, String str) throws TransformerException {
      return eval(contextNode, str, contextNode);
   }

   public static XObject eval(Node contextNode, String str, Node namespaceNode) throws TransformerException {
      XPathContext xpathSupport = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
      PrefixResolverDefault prefixResolver = new PrefixResolverDefault((Node)(namespaceNode.getNodeType() == 9 ? ((Document)namespaceNode).getDocumentElement() : namespaceNode));
      XPath xpath = new XPath(str, (SourceLocator)null, prefixResolver, 0, (ErrorListener)null);
      int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
      return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
   }

   public static XObject eval(Node contextNode, String str, PrefixResolver prefixResolver) throws TransformerException {
      XPath xpath = new XPath(str, (SourceLocator)null, prefixResolver, 0, (ErrorListener)null);
      XPathContext xpathSupport = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
      int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
      return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
   }
}
