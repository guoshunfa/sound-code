package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;

public final class DTMXRTreeFrag {
   private DTM m_dtm;
   private int m_dtmIdentity = -1;
   private XPathContext m_xctxt;

   public DTMXRTreeFrag(int dtmIdentity, XPathContext xctxt) {
      this.m_xctxt = xctxt;
      this.m_dtmIdentity = dtmIdentity;
      this.m_dtm = xctxt.getDTM(dtmIdentity);
   }

   public final void destruct() {
      this.m_dtm = null;
      this.m_xctxt = null;
   }

   final DTM getDTM() {
      return this.m_dtm;
   }

   public final int getDTMIdentity() {
      return this.m_dtmIdentity;
   }

   final XPathContext getXPathContext() {
      return this.m_xctxt;
   }

   public final int hashCode() {
      return this.m_dtmIdentity;
   }

   public final boolean equals(Object obj) {
      if (obj instanceof DTMXRTreeFrag) {
         return this.m_dtmIdentity == ((DTMXRTreeFrag)obj).getDTMIdentity();
      } else {
         return false;
      }
   }
}
