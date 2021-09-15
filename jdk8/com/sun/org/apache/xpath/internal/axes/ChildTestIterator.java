package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class ChildTestIterator extends BasicTestIterator {
   static final long serialVersionUID = -7936835957960705722L;
   protected transient DTMAxisTraverser m_traverser;

   ChildTestIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
      super(compiler, opPos, analysis);
   }

   public ChildTestIterator(DTMAxisTraverser traverser) {
      super((PrefixResolver)null);
      this.m_traverser = traverser;
   }

   protected int getNextNode() {
      this.m_lastFetched = -1 == this.m_lastFetched ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
      return this.m_lastFetched;
   }

   public DTMIterator cloneWithReset() throws CloneNotSupportedException {
      ChildTestIterator clone = (ChildTestIterator)super.cloneWithReset();
      clone.m_traverser = this.m_traverser;
      return clone;
   }

   public void setRoot(int context, Object environment) {
      super.setRoot(context, environment);
      this.m_traverser = this.m_cdtm.getAxisTraverser(3);
   }

   public int getAxis() {
      return 3;
   }

   public void detach() {
      if (this.m_allowDetach) {
         this.m_traverser = null;
         super.detach();
      }

   }
}
