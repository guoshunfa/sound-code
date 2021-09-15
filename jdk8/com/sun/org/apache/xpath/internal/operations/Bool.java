package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Bool extends UnaryOperation {
   static final long serialVersionUID = 44705375321914635L;

   public XObject operate(XObject right) throws TransformerException {
      if (1 == right.getType()) {
         return right;
      } else {
         return right.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      }
   }

   public boolean bool(XPathContext xctxt) throws TransformerException {
      return this.m_right.bool(xctxt);
   }
}
