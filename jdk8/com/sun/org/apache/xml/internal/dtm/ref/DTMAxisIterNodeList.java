package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.utils.IntVector;
import org.w3c.dom.Node;

public class DTMAxisIterNodeList extends DTMNodeListBase {
   private DTM m_dtm;
   private DTMAxisIterator m_iter;
   private IntVector m_cachedNodes;
   private int m_last = -1;

   private DTMAxisIterNodeList() {
   }

   public DTMAxisIterNodeList(DTM dtm, DTMAxisIterator dtmAxisIterator) {
      if (dtmAxisIterator == null) {
         this.m_last = 0;
      } else {
         this.m_cachedNodes = new IntVector();
         this.m_dtm = dtm;
      }

      this.m_iter = dtmAxisIterator;
   }

   public DTMAxisIterator getDTMAxisIterator() {
      return this.m_iter;
   }

   public Node item(int index) {
      if (this.m_iter != null) {
         int node = 0;
         int count = this.m_cachedNodes.size();
         if (count > index) {
            node = this.m_cachedNodes.elementAt(index);
            return this.m_dtm.getNode(node);
         }

         if (this.m_last == -1) {
            while(count <= index && (node = this.m_iter.next()) != -1) {
               this.m_cachedNodes.addElement(node);
               ++count;
            }

            if (node != -1) {
               return this.m_dtm.getNode(node);
            }

            this.m_last = count;
         }
      }

      return null;
   }

   public int getLength() {
      if (this.m_last == -1) {
         while(true) {
            int node;
            if ((node = this.m_iter.next()) == -1) {
               this.m_last = this.m_cachedNodes.size();
               break;
            }

            this.m_cachedNodes.addElement(node);
         }
      }

      return this.m_last;
   }
}
