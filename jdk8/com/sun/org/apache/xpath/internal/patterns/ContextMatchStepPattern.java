package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class ContextMatchStepPattern extends StepPattern {
   static final long serialVersionUID = -1888092779313211942L;

   public ContextMatchStepPattern(int axis, int paxis) {
      super(-1, axis, paxis);
   }

   public XObject execute(XPathContext xctxt) throws TransformerException {
      return xctxt.getIteratorRoot() == xctxt.getCurrentNode() ? this.getStaticScore() : SCORE_NONE;
   }

   public XObject executeRelativePathPattern(XPathContext xctxt, StepPattern prevStep) throws TransformerException {
      XObject score = NodeTest.SCORE_NONE;
      int context = xctxt.getCurrentNode();
      DTM dtm = xctxt.getDTM(context);
      if (null != dtm) {
         int predContext = xctxt.getCurrentNode();
         int axis = this.m_axis;
         boolean needToTraverseAttrs = WalkerFactory.isDownwardAxisOfMany(axis);
         boolean iterRootIsAttr = dtm.getNodeType(xctxt.getIteratorRoot()) == 2;
         if (11 == axis && iterRootIsAttr) {
            axis = 15;
         }

         DTMAxisTraverser traverser = dtm.getAxisTraverser(axis);

         for(int relative = traverser.first(context); -1 != relative; relative = traverser.next(context, relative)) {
            try {
               xctxt.pushCurrentNode(relative);
               score = this.execute(xctxt);
               if (score != NodeTest.SCORE_NONE) {
                  if (this.executePredicates(xctxt, dtm, context)) {
                     Object var25 = score;
                     return (XObject)var25;
                  }

                  score = NodeTest.SCORE_NONE;
               }

               if (needToTraverseAttrs && iterRootIsAttr && 1 == dtm.getNodeType(relative)) {
                  int xaxis = 2;

                  for(int i = 0; i < 2; ++i) {
                     DTMAxisTraverser atraverser = dtm.getAxisTraverser(xaxis);

                     for(int arelative = atraverser.first(relative); -1 != arelative; arelative = atraverser.next(relative, arelative)) {
                        try {
                           xctxt.pushCurrentNode(arelative);
                           score = this.execute(xctxt);
                           if (score != NodeTest.SCORE_NONE && score != NodeTest.SCORE_NONE) {
                              Object var16 = score;
                              return (XObject)var16;
                           }
                        } finally {
                           xctxt.popCurrentNode();
                        }
                     }

                     xaxis = 9;
                  }
               }
            } finally {
               xctxt.popCurrentNode();
            }
         }
      }

      return (XObject)score;
   }
}