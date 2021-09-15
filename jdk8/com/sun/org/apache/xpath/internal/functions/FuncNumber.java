package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncNumber extends FunctionDef1Arg {
   static final long serialVersionUID = 7266745342264153076L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      return new XNumber(this.getArg0AsNumber(xctxt));
   }
}
