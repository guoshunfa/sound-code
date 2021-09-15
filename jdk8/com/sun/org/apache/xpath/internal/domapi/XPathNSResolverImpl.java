package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathNSResolver;

class XPathNSResolverImpl extends PrefixResolverDefault implements XPathNSResolver {
   public XPathNSResolverImpl(Node xpathExpressionContext) {
      super(xpathExpressionContext);
   }

   public String lookupNamespaceURI(String prefix) {
      return super.getNamespaceForPrefix(prefix);
   }
}
