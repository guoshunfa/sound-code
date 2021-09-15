package com.sun.org.apache.xalan.internal.extensions;

import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public interface ExpressionContext {
   Node getContextNode();

   NodeIterator getContextNodes();

   ErrorListener getErrorListener();

   double toNumber(Node var1);

   String toString(Node var1);

   XObject getVariableOrParam(QName var1) throws TransformerException;

   XPathContext getXPathContext() throws TransformerException;
}
