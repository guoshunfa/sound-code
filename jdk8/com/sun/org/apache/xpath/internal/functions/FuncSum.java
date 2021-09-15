package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncSum extends FunctionOneArg {
   static final long serialVersionUID = -2719049259574677519L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      DTMIterator nodes = this.m_arg0.asIterator(xctxt, xctxt.getCurrentNode());
      double sum = 0.0D;

      int pos;
      while(-1 != (pos = nodes.nextNode())) {
         DTM dtm = nodes.getDTM(pos);
         XMLString s = dtm.getStringValue(pos);
         if (null != s) {
            sum += s.toDouble();
         }
      }

      nodes.detach();
      return new XNumber(sum);
   }
}
