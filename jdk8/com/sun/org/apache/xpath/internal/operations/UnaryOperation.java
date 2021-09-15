package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public abstract class UnaryOperation extends Expression implements ExpressionOwner {
   static final long serialVersionUID = 6536083808424286166L;
   protected Expression m_right;

   public void fixupVariables(Vector vars, int globalsSize) {
      this.m_right.fixupVariables(vars, globalsSize);
   }

   public boolean canTraverseOutsideSubtree() {
      return null != this.m_right && this.m_right.canTraverseOutsideSubtree();
   }

   public void setRight(Expression r) {
      this.m_right = r;
      r.exprSetParent(this);
   }

   public XObject execute(XPathContext xctxt) throws TransformerException {
      return this.operate(this.m_right.execute(xctxt));
   }

   public abstract XObject operate(XObject var1) throws TransformerException;

   public Expression getOperand() {
      return this.m_right;
   }

   public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
      if (visitor.visitUnaryOperation(owner, this)) {
         this.m_right.callVisitors(this, visitor);
      }

   }

   public Expression getExpression() {
      return this.m_right;
   }

   public void setExpression(Expression exp) {
      exp.exprSetParent(this);
      this.m_right = exp;
   }

   public boolean deepEquals(Expression expr) {
      if (!this.isSameClass(expr)) {
         return false;
      } else {
         return this.m_right.deepEquals(((UnaryOperation)expr).m_right);
      }
   }
}
