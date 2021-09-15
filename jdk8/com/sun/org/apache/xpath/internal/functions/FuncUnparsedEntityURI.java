package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncUnparsedEntityURI extends FunctionOneArg {
   static final long serialVersionUID = 845309759097448178L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      String name = this.m_arg0.execute(xctxt).str();
      int context = xctxt.getCurrentNode();
      DTM dtm = xctxt.getDTM(context);
      int doc = dtm.getDocument();
      String uri = dtm.getUnparsedEntityURI(name);
      return new XString(uri);
   }
}
