package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class SelfIteratorNoPredicate extends LocPathIterator {
   static final long serialVersionUID = -4226887905279814201L;

   SelfIteratorNoPredicate(Compiler compiler, int opPos, int analysis) throws TransformerException {
      super(compiler, opPos, analysis, false);
   }

   public SelfIteratorNoPredicate() throws TransformerException {
      super((PrefixResolver)null);
   }

   public int nextNode() {
      if (this.m_foundLast) {
         return -1;
      } else {
         DTM dtm = this.m_cdtm;
         int var10001 = -1 == this.m_lastFetched ? this.m_context : -1;
         int next = var10001;
         this.m_lastFetched = var10001;
         if (-1 != next) {
            ++this.m_pos;
            return next;
         } else {
            this.m_foundLast = true;
            return -1;
         }
      }
   }

   public int asNode(XPathContext xctxt) throws TransformerException {
      return xctxt.getCurrentNode();
   }

   public int getLastPos(XPathContext xctxt) {
      return 1;
   }
}
