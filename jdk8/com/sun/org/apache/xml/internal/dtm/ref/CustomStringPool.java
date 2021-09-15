package com.sun.org.apache.xml.internal.dtm.ref;

import java.util.HashMap;
import java.util.Map;

public class CustomStringPool extends DTMStringPool {
   final Map<String, Integer> m_stringToInt = new HashMap();
   public static final int NULL = -1;

   public void removeAllElements() {
      this.m_intToString.removeAllElements();
      if (this.m_stringToInt != null) {
         this.m_stringToInt.clear();
      }

   }

   public String indexToString(int i) throws ArrayIndexOutOfBoundsException {
      return (String)this.m_intToString.elementAt(i);
   }

   public int stringToIndex(String s) {
      if (s == null) {
         return -1;
      } else {
         Integer iobj = (Integer)this.m_stringToInt.get(s);
         if (iobj == null) {
            this.m_intToString.addElement(s);
            iobj = this.m_intToString.size();
            this.m_stringToInt.put(s, iobj);
         }

         return iobj;
      }
   }
}
