package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncDoclocation extends FunctionDef1Arg {
   static final long serialVersionUID = 7469213946343568769L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      int whereNode = this.getArg0AsNode(xctxt);
      String fileLocation = null;
      if (-1 != whereNode) {
         DTM dtm = xctxt.getDTM(whereNode);
         if (11 == dtm.getNodeType(whereNode)) {
            whereNode = dtm.getFirstChild(whereNode);
         }

         if (-1 != whereNode) {
            fileLocation = dtm.getDocumentBaseURI();
         }
      }

      return new XString(null != fileLocation ? fileLocation : "");
   }
}
