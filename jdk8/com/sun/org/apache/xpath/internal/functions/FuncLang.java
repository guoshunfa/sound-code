package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncLang extends FunctionOneArg {
   static final long serialVersionUID = -7868705139354872185L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      String lang = this.m_arg0.execute(xctxt).str();
      int parent = xctxt.getCurrentNode();
      boolean isLang = false;

      for(DTM dtm = xctxt.getDTM(parent); -1 != parent; parent = dtm.getParent(parent)) {
         if (1 == dtm.getNodeType(parent)) {
            int langAttr = dtm.getAttributeNode(parent, "http://www.w3.org/XML/1998/namespace", "lang");
            if (-1 != langAttr) {
               String langVal = dtm.getNodeValue(langAttr);
               if (langVal.toLowerCase().startsWith(lang.toLowerCase())) {
                  int valLen = lang.length();
                  if (langVal.length() == valLen || langVal.charAt(valLen) == '-') {
                     isLang = true;
                  }
               }
               break;
            }
         }
      }

      return isLang ? XBoolean.S_TRUE : XBoolean.S_FALSE;
   }
}
