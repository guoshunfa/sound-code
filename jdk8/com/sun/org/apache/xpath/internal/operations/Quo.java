package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

/** @deprecated */
public class Quo extends Operation {
   static final long serialVersionUID = 693765299196169905L;

   public XObject operate(XObject left, XObject right) throws TransformerException {
      return new XNumber((double)((int)(left.num() / right.num())));
   }
}
