package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class ChildIterator extends LocPathIterator {
   static final long serialVersionUID = -6935428015142993583L;

   ChildIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
      super(compiler, opPos, analysis, false);
      this.initNodeTest(-1);
   }

   public int asNode(XPathContext xctxt) throws TransformerException {
      int current = xctxt.getCurrentNode();
      DTM dtm = xctxt.getDTM(current);
      return dtm.getFirstChild(current);
   }

   public int nextNode() {
      if (this.m_foundLast) {
         return -1;
      } else {
         int next;
         this.m_lastFetched = next = -1 == this.m_lastFetched ? this.m_cdtm.getFirstChild(this.m_context) : this.m_cdtm.getNextSibling(this.m_lastFetched);
         if (-1 != next) {
            ++this.m_pos;
            return next;
         } else {
            this.m_foundLast = true;
            return -1;
         }
      }
   }

   public int getAxis() {
      return 3;
   }
}
