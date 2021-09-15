package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncRound extends FunctionOneArg {
   static final long serialVersionUID = -7970583902573826611L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      XObject obj = this.m_arg0.execute(xctxt);
      double val = obj.num();
      if (val >= -0.5D && val < 0.0D) {
         return new XNumber(-0.0D);
      } else {
         return val == 0.0D ? new XNumber(val) : new XNumber(Math.floor(val + 0.5D));
      }
   }
}
