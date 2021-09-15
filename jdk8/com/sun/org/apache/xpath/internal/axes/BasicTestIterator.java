package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public abstract class BasicTestIterator extends LocPathIterator {
   static final long serialVersionUID = 3505378079378096623L;

   protected BasicTestIterator() {
   }

   protected BasicTestIterator(PrefixResolver nscontext) {
      super(nscontext);
   }

   protected BasicTestIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
      super(compiler, opPos, analysis, false);
      int firstStepPos = OpMap.getFirstChildPos(opPos);
      int whatToShow = compiler.getWhatToShow(firstStepPos);
      if (0 != (whatToShow & 4163) && whatToShow != -1) {
         this.initNodeTest(whatToShow, compiler.getStepNS(firstStepPos), compiler.getStepLocalName(firstStepPos));
      } else {
         this.initNodeTest(whatToShow);
      }

      this.initPredicateInfo(compiler, firstStepPos);
   }

   protected BasicTestIterator(Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers) throws TransformerException {
      super(compiler, opPos, analysis, shouldLoadWalkers);
   }

   protected abstract int getNextNode();

   public int nextNode() {
      if (this.m_foundLast) {
         this.m_lastFetched = -1;
         return -1;
      } else {
         if (-1 == this.m_lastFetched) {
            this.resetProximityPositions();
         }

         VariableStack vars;
         int savedStart;
         if (-1 != this.m_stackFrame) {
            vars = this.m_execContext.getVarStack();
            savedStart = vars.getStackFrame();
            vars.setStackFrame(this.m_stackFrame);
         } else {
            vars = null;
            savedStart = 0;
         }

         byte var4;
         try {
            int next;
            do {
               next = this.getNextNode();
            } while(-1 != next && 1 != this.acceptNode(next) && next != -1);

            if (-1 != next) {
               ++this.m_pos;
               int var8 = next;
               return var8;
            }

            this.m_foundLast = true;
            var4 = -1;
         } finally {
            if (-1 != this.m_stackFrame) {
               vars.setStackFrame(savedStart);
            }

         }

         return var4;
      }
   }

   public DTMIterator cloneWithReset() throws CloneNotSupportedException {
      ChildTestIterator clone = (ChildTestIterator)super.cloneWithReset();
      clone.resetProximityPositions();
      return clone;
   }
}
