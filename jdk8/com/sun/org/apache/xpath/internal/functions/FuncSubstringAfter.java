package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstringAfter extends Function2Args {
   static final long serialVersionUID = -8119731889862512194L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      XMLString s1 = this.m_arg0.execute(xctxt).xstr();
      XMLString s2 = this.m_arg1.execute(xctxt).xstr();
      int index = s1.indexOf(s2);
      return -1 == index ? XString.EMPTYSTRING : (XString)s1.substring(index + s2.length());
   }
}
