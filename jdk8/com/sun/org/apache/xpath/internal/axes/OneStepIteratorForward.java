package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public class OneStepIteratorForward extends ChildTestIterator {
   static final long serialVersionUID = -1576936606178190566L;
   protected int m_axis = -1;

   OneStepIteratorForward(Compiler compiler, int opPos, int analysis) throws TransformerException {
      super(compiler, opPos, analysis);
      int firstStepPos = OpMap.getFirstChildPos(opPos);
      this.m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);
   }

   public OneStepIteratorForward(int axis) {
      super((DTMAxisTraverser)null);
      this.m_axis = axis;
      int whatToShow = -1;
      this.initNodeTest(whatToShow);
   }

   public void setRoot(int context, Object environment) {
      super.setRoot(context, environment);
      this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_axis);
   }

   protected int getNextNode() {
      this.m_lastFetched = -1 == this.m_lastFetched ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
      return this.m_lastFetched;
   }

   public int getAxis() {
      return this.m_axis;
   }

   public boolean deepEquals(Expression expr) {
      if (!super.deepEquals(expr)) {
         return false;
      } else {
         return this.m_axis == ((OneStepIteratorForward)expr).m_axis;
      }
   }
}
