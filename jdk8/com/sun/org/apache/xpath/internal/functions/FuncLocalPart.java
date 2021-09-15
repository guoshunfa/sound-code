package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncLocalPart extends FunctionDef1Arg {
   static final long serialVersionUID = 7591798770325814746L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      int context = this.getArg0AsNode(xctxt);
      if (-1 == context) {
         return XString.EMPTYSTRING;
      } else {
         DTM dtm = xctxt.getDTM(context);
         String s = context != -1 ? dtm.getLocalName(context) : "";
         return !s.startsWith("#") && !s.equals("xmlns") ? new XString(s) : XString.EMPTYSTRING;
      }
   }
}
