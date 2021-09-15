package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class UnionChildIterator extends ChildTestIterator {
   static final long serialVersionUID = 3500298482193003495L;
   private PredicatedNodeTest[] m_nodeTests = null;

   public UnionChildIterator() {
      super((DTMAxisTraverser)null);
   }

   public void addNodeTest(PredicatedNodeTest test) {
      if (null == this.m_nodeTests) {
         this.m_nodeTests = new PredicatedNodeTest[1];
         this.m_nodeTests[0] = test;
      } else {
         PredicatedNodeTest[] tests = this.m_nodeTests;
         int len = this.m_nodeTests.length;
         this.m_nodeTests = new PredicatedNodeTest[len + 1];
         System.arraycopy(tests, 0, this.m_nodeTests, 0, len);
         this.m_nodeTests[len] = test;
      }

      test.exprSetParent(this);
   }

   public void fixupVariables(Vector vars, int globalsSize) {
      super.fixupVariables(vars, globalsSize);
      if (this.m_nodeTests != null) {
         for(int i = 0; i < this.m_nodeTests.length; ++i) {
            this.m_nodeTests[i].fixupVariables(vars, globalsSize);
         }
      }

   }

   public short acceptNode(int n) {
      XPathContext xctxt = this.getXPathContext();

      try {
         xctxt.pushCurrentNode(n);

         for(int i = 0; i < this.m_nodeTests.length; ++i) {
            PredicatedNodeTest pnt = this.m_nodeTests[i];
            XObject score = pnt.execute(xctxt, n);
            if (score != NodeTest.SCORE_NONE) {
               byte var6;
               if (pnt.getPredicateCount() <= 0) {
                  var6 = 1;
                  return var6;
               }

               if (pnt.executePredicates(n, xctxt)) {
                  var6 = 1;
                  return var6;
               }
            }
         }

         return 3;
      } catch (TransformerException var10) {
         throw new RuntimeException(var10.getMessage());
      } finally {
         xctxt.popCurrentNode();
      }
   }
}
