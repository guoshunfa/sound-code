package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstring extends Function3Args {
   static final long serialVersionUID = -5996676095024715502L;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      XMLString s1 = this.m_arg0.execute(xctxt).xstr();
      double start = this.m_arg1.execute(xctxt).num();
      int lenOfS1 = s1.length();
      if (lenOfS1 <= 0) {
         return XString.EMPTYSTRING;
      } else {
         int startIndex;
         if (Double.isNaN(start)) {
            start = -1000000.0D;
            startIndex = 0;
         } else {
            start = (double)Math.round(start);
            startIndex = start > 0.0D ? (int)start - 1 : 0;
         }

         XMLString substr;
         if (null != this.m_arg2) {
            double len = this.m_arg2.num(xctxt);
            int end = (int)((double)Math.round(len) + start) - 1;
            if (end < 0) {
               end = 0;
            } else if (end > lenOfS1) {
               end = lenOfS1;
            }

            if (startIndex > lenOfS1) {
               startIndex = lenOfS1;
            }

            substr = s1.substring(startIndex, end);
         } else {
            if (startIndex > lenOfS1) {
               startIndex = lenOfS1;
            }

            substr = s1.substring(startIndex);
         }

         return (XString)substr;
      }
   }

   public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
      if (argNum < 2) {
         this.reportWrongNumberArgs();
      }

   }

   protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("ER_TWO_OR_THREE", (Object[])null));
   }
}
