package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class VariableStack implements Cloneable {
   public static final int CLEARLIMITATION = 1024;
   XObject[] _stackFrames = new XObject[8192];
   int _frameTop;
   private int _currentFrameBottom;
   int[] _links = new int[4096];
   int _linksTop;
   private static XObject[] m_nulls = new XObject[1024];

   public VariableStack() {
      this.reset();
   }

   public synchronized Object clone() throws CloneNotSupportedException {
      VariableStack vs = (VariableStack)super.clone();
      vs._stackFrames = (XObject[])((XObject[])this._stackFrames.clone());
      vs._links = (int[])((int[])this._links.clone());
      return vs;
   }

   public XObject elementAt(int i) {
      return this._stackFrames[i];
   }

   public int size() {
      return this._frameTop;
   }

   public void reset() {
      this._frameTop = 0;
      this._linksTop = 0;
      this._links[this._linksTop++] = 0;
      this._stackFrames = new XObject[this._stackFrames.length];
   }

   public void setStackFrame(int sf) {
      this._currentFrameBottom = sf;
   }

   public int getStackFrame() {
      return this._currentFrameBottom;
   }

   public int link(int size) {
      this._currentFrameBottom = this._frameTop;
      this._frameTop += size;
      if (this._frameTop >= this._stackFrames.length) {
         XObject[] newsf = new XObject[this._stackFrames.length + 4096 + size];
         System.arraycopy(this._stackFrames, 0, newsf, 0, this._stackFrames.length);
         this._stackFrames = newsf;
      }

      if (this._linksTop + 1 >= this._links.length) {
         int[] newlinks = new int[this._links.length + 2048];
         System.arraycopy(this._links, 0, newlinks, 0, this._links.length);
         this._links = newlinks;
      }

      this._links[this._linksTop++] = this._currentFrameBottom;
      return this._currentFrameBottom;
   }

   public void unlink() {
      this._frameTop = this._links[--this._linksTop];
      this._currentFrameBottom = this._links[this._linksTop - 1];
   }

   public void unlink(int currentFrame) {
      this._frameTop = this._links[--this._linksTop];
      this._currentFrameBottom = currentFrame;
   }

   public void setLocalVariable(int index, XObject val) {
      this._stackFrames[index + this._currentFrameBottom] = val;
   }

   public void setLocalVariable(int index, XObject val, int stackFrame) {
      this._stackFrames[index + stackFrame] = val;
   }

   public XObject getLocalVariable(XPathContext xctxt, int index) throws TransformerException {
      index += this._currentFrameBottom;
      XObject val = this._stackFrames[index];
      if (null == val) {
         throw new TransformerException(XSLMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", (Object[])null), xctxt.getSAXLocator());
      } else {
         return val.getType() == 600 ? (this._stackFrames[index] = val.execute(xctxt)) : val;
      }
   }

   public XObject getLocalVariable(int index, int frame) throws TransformerException {
      index += frame;
      XObject val = this._stackFrames[index];
      return val;
   }

   public XObject getLocalVariable(XPathContext xctxt, int index, boolean destructiveOK) throws TransformerException {
      index += this._currentFrameBottom;
      XObject val = this._stackFrames[index];
      if (null == val) {
         throw new TransformerException(XSLMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", (Object[])null), xctxt.getSAXLocator());
      } else if (val.getType() == 600) {
         return this._stackFrames[index] = val.execute(xctxt);
      } else {
         return destructiveOK ? val : val.getFresh();
      }
   }

   public boolean isLocalSet(int index) throws TransformerException {
      return this._stackFrames[index + this._currentFrameBottom] != null;
   }

   public void clearLocalSlots(int start, int len) {
      start += this._currentFrameBottom;
      System.arraycopy(m_nulls, 0, this._stackFrames, start, len);
   }

   public void setGlobalVariable(int index, XObject val) {
      this._stackFrames[index] = val;
   }

   public XObject getGlobalVariable(XPathContext xctxt, int index) throws TransformerException {
      XObject val = this._stackFrames[index];
      return val.getType() == 600 ? (this._stackFrames[index] = val.execute(xctxt)) : val;
   }

   public XObject getGlobalVariable(XPathContext xctxt, int index, boolean destructiveOK) throws TransformerException {
      XObject val = this._stackFrames[index];
      if (val.getType() == 600) {
         return this._stackFrames[index] = val.execute(xctxt);
      } else {
         return destructiveOK ? val : val.getFresh();
      }
   }

   public XObject getVariableOrParam(XPathContext xctxt, QName qname) throws TransformerException {
      throw new TransformerException(XSLMessages.createXPATHMessage("ER_VAR_NOT_RESOLVABLE", new Object[]{qname.toString()}));
   }
}
