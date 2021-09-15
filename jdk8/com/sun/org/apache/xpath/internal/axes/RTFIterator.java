package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xpath.internal.NodeSetDTM;

public class RTFIterator extends NodeSetDTM {
   static final long serialVersionUID = 7658117366258528996L;

   public RTFIterator(int root, DTMManager manager) {
      super(root, manager);
   }
}
