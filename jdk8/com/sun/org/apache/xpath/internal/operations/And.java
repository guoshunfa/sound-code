package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class And extends Operation {
   static final long serialVersionUID = 392330077126534022L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      XObject expr1 = this.m_left.execute(xctxt);
      if (expr1.bool()) {
         XObject expr2 = this.m_right.execute(xctxt);
         return expr2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      } else {
         return XBoolean.S_FALSE;
      }
   }

   public boolean bool(XPathContext xctxt) throws TransformerException {
      return this.m_left.bool(xctxt) && this.m_right.bool(xctxt);
   }
}
