package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import java.util.HashMap;
import java.util.Map;

public class DOMWSFilter implements DTMWSFilter {
   private AbstractTranslet m_translet;
   private StripFilter m_filter;
   private Map<DTM, short[]> m_mappings;
   private DTM m_currentDTM;
   private short[] m_currentMapping;

   public DOMWSFilter(AbstractTranslet translet) {
      this.m_translet = translet;
      this.m_mappings = new HashMap();
      if (translet instanceof StripFilter) {
         this.m_filter = (StripFilter)translet;
      }

   }

   public short getShouldStripSpace(int node, DTM dtm) {
      if (this.m_filter != null && dtm instanceof DOM) {
         DOM dom = (DOM)dtm;
         int type = false;
         if (dtm instanceof DOMEnhancedForDTM) {
            DOMEnhancedForDTM mappableDOM = (DOMEnhancedForDTM)dtm;
            short[] mapping;
            if (dtm == this.m_currentDTM) {
               mapping = this.m_currentMapping;
            } else {
               mapping = (short[])this.m_mappings.get(dtm);
               if (mapping == null) {
                  mapping = mappableDOM.getMapping(this.m_translet.getNamesArray(), this.m_translet.getUrisArray(), this.m_translet.getTypesArray());
                  this.m_mappings.put(dtm, mapping);
                  this.m_currentDTM = dtm;
                  this.m_currentMapping = mapping;
               }
            }

            int expType = mappableDOM.getExpandedTypeID(node);
            short type;
            if (expType >= 0 && expType < mapping.length) {
               type = mapping[expType];
            } else {
               type = -1;
            }

            return (short)(this.m_filter.stripSpace(dom, node, type) ? 2 : 1);
         } else {
            return 3;
         }
      } else {
         return 1;
      }
   }
}
