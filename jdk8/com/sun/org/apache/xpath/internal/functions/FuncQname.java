package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncQname extends FunctionDef1Arg {
   static final long serialVersionUID = -1532307875532617380L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      int context = this.getArg0AsNode(xctxt);
      XString val;
      if (-1 != context) {
         DTM dtm = xctxt.getDTM(context);
         String qname = dtm.getNodeNameX(context);
         val = null == qname ? XString.EMPTYSTRING : new XString(qname);
      } else {
         val = XString.EMPTYSTRING;
      }

      return val;
   }
}
