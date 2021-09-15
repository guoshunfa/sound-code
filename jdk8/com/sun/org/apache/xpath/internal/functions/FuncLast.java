package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncLast extends Function {
   static final long serialVersionUID = 9205812403085432943L;
   private boolean m_isTopLevel;

   public void postCompileStep(Compiler compiler) {
      this.m_isTopLevel = compiler.getLocationPathDepth() == -1;
   }

   public int getCountOfContextNodeList(XPathContext xctxt) throws TransformerException {
      SubContextList iter = this.m_isTopLevel ? null : xctxt.getSubContextList();
      if (null != iter) {
         return iter.getLastPos(xctxt);
      } else {
         DTMIterator cnl = xctxt.getContextNodeList();
         int count;
         if (null != cnl) {
            count = cnl.getLength();
         } else {
            count = 0;
         }

         return count;
      }
   }

   public XObject execute(XPathContext xctxt) throws TransformerException {
      XNumber xnum = new XNumber((double)this.getCountOfContextNodeList(xctxt));
      return xnum;
   }

   public void fixupVariables(Vector vars, int globalsSize) {
   }
}
