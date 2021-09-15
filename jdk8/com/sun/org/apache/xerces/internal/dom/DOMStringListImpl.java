package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMStringList;

public class DOMStringListImpl implements DOMStringList {
   private Vector fStrings;

   public DOMStringListImpl() {
      this.fStrings = new Vector();
   }

   public DOMStringListImpl(Vector params) {
      this.fStrings = params;
   }

   public String item(int index) {
      try {
         return (String)this.fStrings.elementAt(index);
      } catch (ArrayIndexOutOfBoundsException var3) {
         return null;
      }
   }

   public int getLength() {
      return this.fStrings.size();
   }

   public boolean contains(String param) {
      return this.fStrings.contains(param);
   }

   public void add(String param) {
      this.fStrings.add(param);
   }
}
