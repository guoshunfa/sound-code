package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Equals extends Operation {
   static final long serialVersionUID = -2658315633903426134L;

   public XObject operate(XObject left, XObject right) throws TransformerException {
      return left.equals(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
   }

   public boolean bool(XPathContext xctxt) throws TransformerException {
      XObject left = this.m_left.execute(xctxt, true);
      XObject right = this.m_right.execute(xctxt, true);
      boolean result = left.equals(right);
      left.detach();
      right.detach();
      return result;
   }
}
