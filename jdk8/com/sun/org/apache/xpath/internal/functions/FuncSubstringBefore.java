package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstringBefore extends Function2Args {
   static final long serialVersionUID = 4110547161672431775L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      String s1 = this.m_arg0.execute(xctxt).str();
      String s2 = this.m_arg1.execute(xctxt).str();
      int index = s1.indexOf(s2);
      return -1 == index ? XString.EMPTYSTRING : new XString(s1.substring(0, index));
   }
}
