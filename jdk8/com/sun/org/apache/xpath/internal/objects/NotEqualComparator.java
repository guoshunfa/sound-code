package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

class NotEqualComparator extends Comparator {
   boolean compareStrings(XMLString s1, XMLString s2) {
      return !s1.equals(s2);
   }

   boolean compareNumbers(double n1, double n2) {
      return n1 != n2;
   }
}
