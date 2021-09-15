package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncStringLength extends FunctionDef1Arg {
   static final long serialVersionUID = -159616417996519839L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      return new XNumber((double)this.getArg0AsString(xctxt).length());
   }
}
