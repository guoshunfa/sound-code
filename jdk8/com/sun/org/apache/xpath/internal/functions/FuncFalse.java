package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncFalse extends Function {
   static final long serialVersionUID = 6150918062759769887L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      return XBoolean.S_FALSE;
   }

   public void fixupVariables(Vector vars, int globalsSize) {
   }
}
