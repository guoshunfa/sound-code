package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Number extends UnaryOperation {
   static final long serialVersionUID = 7196954482871619765L;

   public XObject operate(XObject right) throws TransformerException {
      return (XObject)(2 == right.getType() ? right : new XNumber(right.num()));
   }

   public double num(XPathContext xctxt) throws TransformerException {
      return this.m_right.num(xctxt);
   }
}
