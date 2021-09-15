package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Gte extends Operation {
   static final long serialVersionUID = 9142945909906680220L;

   public XObject operate(XObject left, XObject right) throws TransformerException {
      return left.greaterThanOrEqual(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
   }
}
