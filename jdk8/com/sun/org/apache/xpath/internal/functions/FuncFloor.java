package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncFloor extends FunctionOneArg {
   static final long serialVersionUID = 2326752233236309265L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      return new XNumber(Math.floor(this.m_arg0.execute(xctxt).num()));
   }
}
