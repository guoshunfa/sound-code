package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.StringTokenizer;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXNotSupportedException;

public class Extensions {
   private Extensions() {
   }

   public static NodeSet nodeset(ExpressionContext myProcessor, Object rtf) {
      if (rtf instanceof NodeIterator) {
         return new NodeSet((NodeIterator)rtf);
      } else {
         String textNodeValue;
         if (rtf instanceof String) {
            textNodeValue = (String)rtf;
         } else if (rtf instanceof Boolean) {
            textNodeValue = (new XBoolean((Boolean)rtf)).str();
         } else if (rtf instanceof Double) {
            textNodeValue = (new XNumber((Double)rtf)).str();
         } else {
            textNodeValue = rtf.toString();
         }

         Document myDoc = JdkXmlUtils.getDOMDocument();
         Text textNode = myDoc.createTextNode(textNodeValue);
         DocumentFragment docFrag = myDoc.createDocumentFragment();
         docFrag.appendChild(textNode);
         return new NodeSet(docFrag);
      }
   }

   public static NodeList intersection(NodeList nl1, NodeList nl2) {
      return ExsltSets.intersection(nl1, nl2);
   }

   public static NodeList difference(NodeList nl1, NodeList nl2) {
      return ExsltSets.difference(nl1, nl2);
   }

   public static NodeList distinct(NodeList nl) {
      return ExsltSets.distinct(nl);
   }

   public static boolean hasSameNodes(NodeList nl1, NodeList nl2) {
      NodeSet ns1 = new NodeSet(nl1);
      NodeSet ns2 = new NodeSet(nl2);
      if (ns1.getLength() != ns2.getLength()) {
         return false;
      } else {
         for(int i = 0; i < ns1.getLength(); ++i) {
            Node n = ns1.elementAt(i);
            if (!ns2.contains(n)) {
               return false;
            }
         }

         return true;
      }
   }

   public static XObject evaluate(ExpressionContext myContext, String xpathExpr) throws SAXNotSupportedException {
      return ExsltDynamic.evaluate(myContext, xpathExpr);
   }

   public static NodeList tokenize(String toTokenize, String delims) {
      Document doc = JdkXmlUtils.getDOMDocument();
      StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
      NodeSet resultSet = new NodeSet();
      synchronized(doc) {
         while(lTokenizer.hasMoreTokens()) {
            resultSet.addNode(doc.createTextNode(lTokenizer.nextToken()));
         }

         return resultSet;
      }
   }

   public static NodeList tokenize(String toTokenize) {
      return tokenize(toTokenize, " \t\n\r");
   }
}
