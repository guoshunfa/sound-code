package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xpath.internal.axes.LocPathIterator;
import com.sun.org.apache.xpath.internal.axes.UnionPathIterator;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.operations.Operation;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.patterns.UnionPattern;

public class XPathVisitor {
   public boolean visitLocationPath(ExpressionOwner owner, LocPathIterator path) {
      return true;
   }

   public boolean visitUnionPath(ExpressionOwner owner, UnionPathIterator path) {
      return true;
   }

   public boolean visitStep(ExpressionOwner owner, NodeTest step) {
      return true;
   }

   public boolean visitPredicate(ExpressionOwner owner, Expression pred) {
      return true;
   }

   public boolean visitBinaryOperation(ExpressionOwner owner, Operation op) {
      return true;
   }

   public boolean visitUnaryOperation(ExpressionOwner owner, UnaryOperation op) {
      return true;
   }

   public boolean visitVariableRef(ExpressionOwner owner, Variable var) {
      return true;
   }

   public boolean visitFunction(ExpressionOwner owner, Function func) {
      return true;
   }

   public boolean visitMatchPattern(ExpressionOwner owner, StepPattern pattern) {
      return true;
   }

   public boolean visitUnionPattern(ExpressionOwner owner, UnionPattern pattern) {
      return true;
   }

   public boolean visitStringLiteral(ExpressionOwner owner, XString str) {
      return true;
   }

   public boolean visitNumberLiteral(ExpressionOwner owner, XNumber num) {
      return true;
   }
}
