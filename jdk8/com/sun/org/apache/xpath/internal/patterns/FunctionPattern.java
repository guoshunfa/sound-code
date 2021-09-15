package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FunctionPattern extends StepPattern {
   static final long serialVersionUID = -5426793413091209944L;
   Expression m_functionExpr;

   public FunctionPattern(Expression expr, int axis, int predaxis) {
      super(0, (String)null, (String)null, axis, predaxis);
      this.m_functionExpr = expr;
   }

   public final void calcScore() {
      this.m_score = SCORE_OTHER;
      if (null == this.m_targetString) {
         this.calcTargetString();
      }

   }

   public void fixupVariables(Vector vars, int globalsSize) {
      super.fixupVariables(vars, globalsSize);
      this.m_functionExpr.fixupVariables(vars, globalsSize);
   }

   public XObject execute(XPathContext xctxt, int context) throws TransformerException {
      DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
      XNumber score = SCORE_NONE;
      int n;
      if (null != nl) {
         while(-1 != (n = nl.nextNode())) {
            score = n == context ? SCORE_OTHER : SCORE_NONE;
            if (score == SCORE_OTHER) {
               break;
            }
         }
      }

      nl.detach();
      return score;
   }

   public XObject execute(XPathContext xctxt, int context, DTM dtm, int expType) throws TransformerException {
      DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
      XNumber score = SCORE_NONE;
      if (null != nl) {
         while(true) {
            int n;
            if (-1 != (n = nl.nextNode())) {
               score = n == context ? SCORE_OTHER : SCORE_NONE;
               if (score != SCORE_OTHER) {
                  continue;
               }
            }

            nl.detach();
            break;
         }
      }

      return score;
   }

   public XObject execute(XPathContext xctxt) throws TransformerException {
      int context = xctxt.getCurrentNode();
      DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
      XNumber score = SCORE_NONE;
      if (null != nl) {
         while(true) {
            int n;
            if (-1 != (n = nl.nextNode())) {
               score = n == context ? SCORE_OTHER : SCORE_NONE;
               if (score != SCORE_OTHER) {
                  continue;
               }
            }

            nl.detach();
            break;
         }
      }

      return score;
   }

   protected void callSubtreeVisitors(XPathVisitor visitor) {
      this.m_functionExpr.callVisitors(new FunctionPattern.FunctionOwner(), visitor);
      super.callSubtreeVisitors(visitor);
   }

   class FunctionOwner implements ExpressionOwner {
      public Expression getExpression() {
         return FunctionPattern.this.m_functionExpr;
      }

      public void setExpression(Expression exp) {
         exp.exprSetParent(FunctionPattern.this);
         FunctionPattern.this.m_functionExpr = exp;
      }
   }
}
