package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncGenerateId extends FunctionDef1Arg {
   static final long serialVersionUID = 973544842091724273L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      int which = this.getArg0AsNode(xctxt);
      return -1 != which ? new XString("N" + Integer.toHexString(which).toUpperCase()) : XString.EMPTYSTRING;
   }
}
