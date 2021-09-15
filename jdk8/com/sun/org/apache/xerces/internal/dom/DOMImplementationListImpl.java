package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMImplementationListImpl implements DOMImplementationList {
   private Vector fImplementations;

   public DOMImplementationListImpl() {
      this.fImplementations = new Vector();
   }

   public DOMImplementationListImpl(Vector params) {
      this.fImplementations = params;
   }

   public DOMImplementation item(int index) {
      try {
         return (DOMImplementation)this.fImplementations.elementAt(index);
      } catch (ArrayIndexOutOfBoundsException var3) {
         return null;
      }
   }

   public int getLength() {
      return this.fImplementations.size();
   }
}
