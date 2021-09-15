package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.axes.PathComponent;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class Variable extends Expression implements PathComponent {
   static final long serialVersionUID = -4334975375609297049L;
   private boolean m_fixUpWasCalled = false;
   protected QName m_qname;
   protected int m_index;
   protected boolean m_isGlobal = false;
   static final java.lang.String PSUEDOVARNAMESPACE = "http://xml.apache.org/xalan/psuedovar";

   public void setIndex(int index) {
      this.m_index = index;
   }

   public int getIndex() {
      return this.m_index;
   }

   public void setIsGlobal(boolean isGlobal) {
      this.m_isGlobal = isGlobal;
   }

   public boolean getGlobal() {
      return this.m_isGlobal;
   }

   public void fixupVariables(Vector vars, int globalsSize) {
      this.m_fixUpWasCalled = true;
      int sz = vars.size();

      for(int i = vars.size() - 1; i >= 0; --i) {
         QName qn = (QName)vars.elementAt(i);
         if (qn.equals(this.m_qname)) {
            if (i < globalsSize) {
               this.m_isGlobal = true;
               this.m_index = i;
            } else {
               this.m_index = i - globalsSize;
            }

            return;
         }
      }

      java.lang.String msg = XSLMessages.createXPATHMessage("ER_COULD_NOT_FIND_VAR", new Object[]{this.m_qname.toString()});
      TransformerException te = new TransformerException(msg, this);
      throw new WrappedRuntimeException(te);
   }

   public void setQName(QName qname) {
      this.m_qname = qname;
   }

   public QName getQName() {
      return this.m_qname;
   }

   public XObject execute(XPathContext xctxt) throws TransformerException {
      return this.execute(xctxt, false);
   }

   public XObject execute(XPathContext xctxt, boolean destructiveOK) throws TransformerException {
      PrefixResolver xprefixResolver = xctxt.getNamespaceContext();
      Object result;
      if (this.m_fixUpWasCalled) {
         if (this.m_isGlobal) {
            result = xctxt.getVarStack().getGlobalVariable(xctxt, this.m_index, destructiveOK);
         } else {
            result = xctxt.getVarStack().getLocalVariable(xctxt, this.m_index, destructiveOK);
         }
      } else {
         result = xctxt.getVarStack().getVariableOrParam(xctxt, this.m_qname);
      }

      if (null == result) {
         this.warn(xctxt, "WG_ILLEGAL_VARIABLE_REFERENCE", new Object[]{this.m_qname.getLocalPart()});
         result = new XNodeSet(xctxt.getDTMManager());
      }

      return (XObject)result;
   }

   public boolean isStableNumber() {
      return true;
   }

   public int getAnalysisBits() {
      return 67108864;
   }

   public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
      visitor.visitVariableRef(owner, this);
   }

   public boolean deepEquals(Expression expr) {
      if (!this.isSameClass(expr)) {
         return false;
      } else {
         return this.m_qname.equals(((Variable)expr).m_qname);
      }
   }

   public boolean isPsuedoVarRef() {
      java.lang.String ns = this.m_qname.getNamespaceURI();
      return null != ns && ns.equals("http://xml.apache.org/xalan/psuedovar") && this.m_qname.getLocalName().startsWith("#");
   }
}
