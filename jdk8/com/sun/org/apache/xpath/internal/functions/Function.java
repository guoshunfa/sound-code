package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public abstract class Function extends Expression {
   static final long serialVersionUID = 6927661240854599768L;

   public void setArg(Expression arg, int argNum) throws WrongNumberArgsException {
      this.reportWrongNumberArgs();
   }

   public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
      if (argNum != 0) {
         this.reportWrongNumberArgs();
      }

   }

   protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("zero", (Object[])null));
   }

   public XObject execute(XPathContext xctxt) throws TransformerException {
      System.out.println("Error! Function.execute should not be called!");
      return null;
   }

   public void callArgVisitors(XPathVisitor visitor) {
   }

   public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
      if (visitor.visitFunction(owner, this)) {
         this.callArgVisitors(visitor);
      }

   }

   public boolean deepEquals(Expression expr) {
      return this.isSameClass(expr);
   }

   public void postCompileStep(Compiler compiler) {
   }
}
